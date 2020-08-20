package rainbow.db;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.model.Model;

public final class DBTest {

	private DBTest() {
	}

	public static MemoryDao createMemoryDao(Object source) throws IOException, SQLException {
		try (InputStream is = sourceToInputStream(source)) {
			Model model = JSONObject.parseObject(is, Model.class);
			Map<String, Entity> entityMap = DatabaseUtils.resolveModel(model);
			return new MemoryDao(entityMap);
		}
	}

	private static InputStream sourceToInputStream(Object source) throws IOException {
		if (source instanceof URL)
			return ((URL) source).openStream();
		else if (source instanceof String) {
			return new FileInputStream((String) source);
		} else if (source instanceof File) {
			return new FileInputStream((File) source);
		} else
			throw new IllegalArgumentException("bad model param");
	}

	public static InputStream getClasspathFile(String file) {
		InputStream is = DBTest.class.getResourceAsStream(file);
		checkNotNull(is, "file {} not found", file);
		return is;
	}

}