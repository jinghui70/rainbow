package rainbow.db.dev;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PresetService {

	/**
	 * 返回数据组列表
	 * 
	 * @return
	 */
	List<String> types();

	/**
	 * 从预置数据文件中获取指定实体数据
	 * 
	 * @param dataSet
	 * @param entityName
	 * @return
	 */
	List<Map<String, Object>> load(String dataSet, String entityName);

	boolean save(String presetType, String entityName, List<Map<String, Object>> data);

	/**
	 * @param dataSet
	 * @param entities
	 */
	void importFromDb(String dataSet, List<String> entities);

	void exportToDb(String dataSet, List<String> entities);

	List<String> hasPreset(String dataSet) throws IOException;

	boolean initDatabase(String dataSet);
}
