package rainbow.db.deploy;

import static rainbow.core.util.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.ImmutableMap;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.DaoUtils;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.SimpleDriverDataSource;
import rainbow.db.model.Field;
import rainbow.db.model.Model;
import rainbow.db.model.Unit;

public class Worker {

	private static Type MAP_TYPE = new TypeReference<Map<String, Object>>() {
	}.getType();

	private static Map<String, Class<? extends Driver>> driverClassMap = ImmutableMap
			.<String, Class<? extends Driver>>builder() //
			.put("mysql", com.mysql.cj.jdbc.Driver.class) //
			.put("h2", org.h2.Driver.class) //
			.build();

	private TransformerFactory tf = TransformerFactory.newInstance();

	private Model model;

	private Map<String, Entity> entityMap;

	private Path output;

	private List<Path> preset = Collections.emptyList();

	private JdbcConfig jdbc;

	public void setOutputDir(String outputDir) {
		this.output = Paths.get(outputDir);
		checkArgument(Files.exists(this.output) && Files.isDirectory(this.output), "bad output dir {}", output);
	}

	public void setPresetDir(String presetDir) {
		String[] dirs = Utils.split(presetDir, ',');
		this.preset = Arrays.stream(dirs).map(Paths::get).collect(Collectors.toList());
	}

	public void setModelFile(String fileName) throws IOException {
		Path modelFile = Paths.get(fileName);
		if (Files.exists(modelFile) && Files.isRegularFile(modelFile)) {
			try (InputStream is = Files.newInputStream(modelFile)) {
				model = JSON.parseObject(is, StandardCharsets.UTF_8, Model.class);
				entityMap = DaoUtils.resolveModel(model);
			}
		} else
			throw new AppException("{} is not a valid model file", fileName);
	}

	public void setJdbcConfig(String jdbcStr) {
		this.jdbc = JSON.parseObject(jdbcStr, JdbcConfig.class);
	}

	public void outputRdm() throws IOException {
		Path rdm = output.resolve(model.getName() + ".rdm");
		try (Writer writer = Files.newBufferedWriter(rdm)) {
			generateRDM(writer);
		}
	}

	private void generateRDM(Writer writer) throws IOException {
		PrintWriter pw = new PrintWriter(writer);
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		pw.println("<model xmlns=\"http://rainbow/db/model\">");
		pw.append("\t<name>").append(model.getName()).println("</name>");
		pw.println("\t<entities>");
		writeUnit(pw, model);
		pw.println("\t</entities>");
		pw.println("</model>");
	}

	public void doWork() throws IOException, TransformerException, InstantiationException, IllegalAccessException {
		String server = Utils.substringBetween(jdbc.getUrl(), "jdbc:", ":").toLowerCase();
		checkArgument(driverClassMap.containsKey(server), "jdbcUrl ({}) not support", jdbc.getUrl());
		System.out.append("process database ").println(server);
		generateDDL(server);
		Class<? extends Driver> driverClass = driverClassMap.get(server);
		Driver driver = driverClass.newInstance();
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, jdbc.getUrl(), jdbc.getUser(),
				jdbc.getPassword());
		DaoImpl dao = new DaoImpl(dataSource, entityMap);
		generateDatabase(dao, server);
		for (Path p : preset) {
			System.out.println("loading preset data " + p.getFileName());
			Files.list(p).filter(f -> f.getFileName().toString().endsWith(".json")).sorted().forEach(f -> {
				try {
					loadPresetData(dao, f);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			System.out.println("Done!");
		}
	}

	private void loadPresetData(Dao dao, Path file) throws IOException {
		String entityName = Utils.substringBefore(file.getFileName().toString(), ".json");
		System.out.print("processing ");
		System.out.println(entityName);
		Entity entity = dao.getEntity(entityName);
		if (entity == null)
			System.out.println("entity not defined");
		else {
			List<String> lines = Files.readAllLines(file);
			List<NeoBean> neoList = Utils.transform(lines, line -> {
				Map<String, Object> map = JSON.parseObject(line, MAP_TYPE);
				return dao.makeNeoBean(entityName, map);
			});
			dao.insert(neoList);
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
	}

}
