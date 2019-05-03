package rainbow.db.deploy;

import static rainbow.core.util.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.DaoUtils;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.SimpleDriverDataSource;
import rainbow.db.model.Field;
import rainbow.db.model.Model;
import rainbow.db.model.Unit;

public class Worker {

	private static Map<String, Class<? extends Driver>> driverClassMap = ImmutableMap
			.<String, Class<? extends Driver>>builder().put("mysql", com.mysql.cj.jdbc.Driver.class) //
			.put("h2", org.h2.Driver.class) //
			.build();

	private TransformerFactory tf = TransformerFactory.newInstance();

	private Model model;

	private Map<String, Entity> entityMap;

	private Path output;

	private Path preset;

	public void setOutputDir(String outputDir) {
		this.output = Paths.get(outputDir);
		checkArgument(Files.exists(this.output) && Files.isDirectory(this.output), "bad output dir {}", output);
	}

	public void setPresetDir(String presetDir) {
		this.preset = Paths.get(presetDir);
		checkArgument(Files.exists(this.preset) && Files.isDirectory(this.preset), "bad preset dir {}", preset);
	}

	public void setModelFile(String fileName) throws IOException {
		Path modelFile = Paths.get(fileName);
		if (Files.exists(modelFile) && Files.isRegularFile(modelFile)) {
			try (InputStream is = Files.newInputStream(modelFile)) {
				model = JSON.parseObject(is, StandardCharsets.UTF_8, Model.class);
				entityMap = DaoUtils.loadModel(model);
			}
		} else
			throw new AppException("{} is not a valid model file", fileName);
		// 现在是从Excel中加载数据，所以暂时把Map用code来做主键
		Map<String, Entity> tempMap = new HashMap<String, Entity>();
		entityMap.values().forEach(e -> tempMap.put(e.getCode(), e));
		entityMap = tempMap;
	}

	public void outputRdm() throws IOException {
		Path rdm = output.resolve(model.getName() + ".rdm");
		try (Writer writer = Files.newBufferedWriter(rdm)) {
			generateRDM(writer);
		}
	}

	private void generateRDM(Writer writer) throws IOException {
		try (PrintWriter pw = new PrintWriter(writer)) {
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			pw.println("<model xmlns=\"http://rainbow/db/model\">");
			pw.append("\t<name>").append(model.getName()).println("</name>");
			pw.println("\t<entities>");
			writeUnit(pw, model);
			pw.println("\t</entities>");
			pw.println("</model>");
		}
	}

	public void doWork(Work work)
			throws IOException, TransformerException, InstantiationException, IllegalAccessException {
		String server = Utils.substringBetween(work.getUrl(), "jdbc:", ":").toLowerCase();
		checkArgument(driverClassMap.containsKey(server), "jdbcUrl ({}) not support", work.getUrl());
		generateDDL(server);
		Class<? extends Driver> driverClass = driverClassMap.get(server);
		Driver driver = driverClass.newInstance();
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, work.getUrl(), work.getUser(),
				work.getPassword());
		DaoImpl dao = new DaoImpl(dataSource, entityMap);
		generateDatabase(dao, server);
		if (preset != null) {
			Files.list(preset).filter(f -> f.getFileName().toString().endsWith(".xlsx")).forEach(f -> {
				try {
					loadPresetDataFromExcel(dao, f);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	private void writeUnit(PrintWriter writer, Unit unit) {
		if (!Utils.isNullOrEmpty(unit.getTables())) {
			unit.getTables().forEach(table -> {
				writer.println("\t\t<entity>");
				writer.append("\t\t\t<name>").append(table.getName()).println("</name>");
				writer.append("\t\t\t<dbName>").append(table.getCode()).println("</dbName>");
				writer.append("\t\t\t<cnName>").append(table.getLabel()).println("</cnName>");
				writer.println("\t\t\t<columns>");
				table.getFields().forEach(field -> {
					writer.println("\t\t\t\t<column>");
					writer.append("\t\t\t\t\t<name>").append(field.getName()).println("</name>");
					writer.append("\t\t\t\t\t<dbName>").append(field.getCode()).println("</dbName>");
					writer.append("\t\t\t\t\t<cnName>").append(field.getLabel()).println("</cnName>");
					writer.append("\t\t\t\t\t<type>").append(field.getType().toString()).println("</type>");
					writer.append("\t\t\t\t\t<length>").append(Integer.toString(field.getLength()))
							.println("</length>");
					writer.append("\t\t\t\t\t<precision>").append(Integer.toString(field.getPrecision()))
							.println("</precision>");
					writer.append("\t\t\t\t\t<key>").append(field.isKey() ? "true" : "false").println("</key>");
					writer.append("\t\t\t\t\t<mandatory>").append(field.isMandatory() ? "true" : "false")
							.println("</mandatory>");
					writer.println("\t\t\t\t</column>");
				});
				writer.println("\t\t\t</columns>");
				if (!Utils.isNullOrEmpty(table.getIndexes())) {
					writer.println("\t\t\t<indexes>");
					table.getIndexes().forEach(index -> {
						writer.println("\t\t\t\t<index>");
						writer.append("\t\t\t\t\t<name>").append(index.getCode()).println("</name>");
						writer.append("\t\t\t\t\t<unique>").append(index.isUnique() ? "true" : "false")
								.println("</unique>");
						writer.println("\t\t\t\t\t<inxColumns>");
						index.getFields().forEach(inxColumn -> {
							writer.println("\t\t\t\t\t\t<inxColumn>");
							Field field = table.getFields().parallelStream()
									.filter(f -> Objects.equals(inxColumn, f.getName())).findAny().get();
							writer.append("\t\t\t\t\t\t\t<name>").append(field.getCode()).println("</name>");
							writer.println("\t\t\t\t\t\t\t<asc>true</asc>");
							writer.println("\t\t\t\t\t\t</inxColumn>");
						});
						writer.println("\t\t\t\t\t</inxColumns>");
						writer.println("\t\t\t\t</index>");
					});
					writer.println("\t\t\t</indexes>");
				}
				writer.println("\t\t</entity>");
			});
		}
		if (!Utils.isNullOrEmpty(unit.getUnits())) {
			unit.getUnits().forEach(child -> writeUnit(writer, child));
		}
	}

	private void generateDDL(String server) throws IOException, TransformerException {
		Path ddl = output.resolve(server + ".sql");
		Path rdm = output.resolve(model.getName() + ".rdm");
		try (InputStream is = Worker.class.getResourceAsStream(server + ".xsl")) {
			Transformer transformer = tf.newTransformer(new StreamSource(is));
			transformer.transform(new StreamSource(Files.newBufferedReader(rdm)),
					new StreamResult(Files.newOutputStream(ddl)));
		}
	}

	private void generateDatabase(Dao dao, String server) throws IOException {
		Path ddlFile = output.resolve(server + ".sql");
		String ddl = String.join("", Files.readAllLines(ddlFile));
		dao.execSql(ddl);
		System.out.println("Done!");
	}

	private void loadPresetDataFromExcel(Dao dao, Path file) throws IOException {
		System.out.append("processing file: ").println(file.getFileName().toString());
		try (InputStream inStream = Files.newInputStream(file, StandardOpenOption.READ)) {
			Workbook wb = WorkbookFactory.create(inStream);
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				String table = Utils.substringBefore(sheet.getSheetName(), "(");
				if (table.startsWith("//"))
					continue;
				Entity entity = dao.getEntity(table);
				System.out.println(Utils.format("loading table: {}", sheet.getSheetName()));

				// 读取文件中的列
				Map<String, Column> columnMap = entity.getColumns().stream()
						.collect(Collectors.toMap(Column::getCode, Function.identity()));
				List<Column> columns = new ArrayList<Column>();
				Row row = sheet.getRow(1);
				for (int col = 0; col < row.getLastCellNum(); col++) {
					Cell cell = row.getCell(col);
					String v = cell.getStringCellValue();
					if (columnMap.containsKey(v))
						columns.add(columnMap.get(v));
					else
						throw new AppException("field[{}] not defined at col[{}]", v, col);
				}
				// 读数据
				List<NeoBean> list = new ArrayList<NeoBean>(sheet.getLastRowNum() - 2);
				for (int rowInx = 2; rowInx <= sheet.getLastRowNum(); rowInx++) {
					NeoBean neo = dao.newNeoBean(table);
					row = sheet.getRow(rowInx);
					for (int col = 0; col < columns.size(); col++) {
						Cell cell = row.getCell(col);
						Column column = columns.get(col);
						Object value = getValue(column, cell);
						neo.setValue(column, value);
					}
				}
				dao.insert(list, 500, false);
			}
		}
	}

	private Object getValue(Column column, Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case NUMERIC:
			switch (column.getType()) {
			case DATE:
				return cell.getDateCellValue();
			case TIME:
				return cell.getDateCellValue();
			case TIMESTAMP:
				return cell.getDateCellValue();
			case INT:
			case SMALLINT:
				Double d = Double.valueOf(cell.getNumericCellValue());
				return d.intValue();
			case LONG:
				d = Double.valueOf(cell.getNumericCellValue());
				return d.longValue();
			case DOUBLE:
			case NUMERIC:
				return cell.getNumericCellValue();
			case CHAR:
			case VARCHAR:
			case CLOB:
				String v = Double.toString(cell.getNumericCellValue());
				if (v.contains(".")) {
					if (v.contains(".")) {
						v = v.replaceAll("0+$", "");// 去掉多余的0
						v = v.replaceAll("[.]$", "");// 如最后一位是.则去掉
					}
				}
				return v;
			default:
				return null;
			}
		case BLANK:
			return null;
		case STRING:
			return cell.getStringCellValue();
		default:
			return null;
		}
	}

}
