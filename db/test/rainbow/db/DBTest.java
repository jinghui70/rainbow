package rainbow.db;

import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import rainbow.db.dao.DaoUtils;
import rainbow.db.dao.memory.MemoryDao;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Model;

public final class DBTest {

	private DBTest() {
	}

	public static MemoryDao createMemoryDao(Object source) throws IOException {
		try (InputStream is = sourceToInputStream(source)) {
			Model model = JSONObject.parseObject(is, Model.class);
			Map<String, Entity> entityMap = DaoUtils.resolveModel(model);
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