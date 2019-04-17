package rainbow.db;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

import rainbow.core.util.XmlBinder;
import rainbow.core.util.converter.Converters;
import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Column;
import rainbow.db.model.Model;

public final class DBTest {

	private static Logger logger = LoggerFactory.getLogger(DBTest.class);

	private DBTest() {
	}

	public static MemoryDao createMemoryDao(Object... modelSource) throws IOException, JAXBException {
		InputStream is = null;
		MemoryDao dao = new MemoryDao();
		for (int i = 0; i < modelSource.length; i++) {
			Object source = modelSource[i];
			if (source instanceof URL)
				is = ((URL) source).openStream();
			else if (source instanceof String) {
				is = new FileInputStream((String) source);
			} else if (source instanceof File) {
				is = new FileInputStream((File) source);
			} else
				throw new IllegalArgumentException("bad model param");
			try {
				Model model = new XmlBinder<Model>(Model.class).unmarshal(is);
				dao.setModel(model);
			} finally {
				Closeables.closeQuietly(is);
			}
		}
		return dao;
	}

	public static InputStream getClasspathFile(String file) {
		InputStream is = DBTest.class.getResourceAsStream(file);
		checkNotNull(is, "file {} not found", file);
		return is;
	}

	public static void loadDataFromExcel(Dao dao, String fileName) throws InvalidFormatException, IOException {
		File file = new File(fileName);
		InputStream is = new FileInputStream(file);
		try {
			loadDataFromExcel(dao, is);
		} finally {
			Closeables.close(is, true);
		}
	}

	public static void loadDataFromExcel(Dao dao, URL url) throws InvalidFormatException, IOException {
		InputStream is = url.openStream();
		try {
			loadDataFromExcel(dao, is);
		} finally {
			Closeables.close(is, true);
		}
	}

	/**
	 * 从Excel载入数据
	 * 
	 * @param dao
	 * @param file
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static void loadDataFromExcel(Dao dao, InputStream is) throws InvalidFormatException, IOException {
		Workbook wb = new XSSFWorkbook(is);
		try {
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				String entityName = sheet.getSheetName();
				logger.info("load... page {}", entityName);
				Entity entity = dao.getEntity(entityName);
				if (entity != null) {
					for (int rowInx = 1; rowInx <= sheet.getLastRowNum(); rowInx++) {
						Row row = sheet.getRow(rowInx);
						NeoBean neo = dao.newNeoBean(entityName);
						int col = 0;
						for (Column column : entity.getColumns()) {
							try {
								Cell cell = row.getCell(col++);
								if (cell != null)
									neo.setValue(column, getValue(column, cell));
							} catch (Throwable e) {
								logger.error("row {} col {} error", rowInx, column.getName());
								throw e;
							}
						}
						dao.insert(neo);
					}
				} else {
					logger.warn("entity not found");
				}
			}
		} finally {
			Closeables.close(wb, true);
		}
	}

	private static Object getValue(Column column, Cell cell) {
		switch (cell.getCellType()) {
		case NUMERIC:
			switch (column.getType()) {
			case DATE:
			case TIME:
			case TIMESTAMP:
				return cell.getDateCellValue();
			case CHAR:
			case VARCHAR:
			case CLOB:
				String v = Double.toString(cell.getNumericCellValue());
				if (v.contains(".")) {
					v = v.replaceAll("0+$", "");// 去掉多余的0
					v = v.replaceAll("[.]$", "");// 如最后一位是.则去掉
				}
				return v;
			default:
				return Converters.convert(cell.getNumericCellValue(), column.dataClass());
			}
		case BLANK:
			return null;
		case STRING:
			return Converters.convert(cell.getStringCellValue(), column.dataClass());
		default:
			return null;
		}
	}
}