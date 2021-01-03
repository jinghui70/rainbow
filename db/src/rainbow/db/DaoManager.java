package rainbow.db;

import java.util.List;

import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoConfig;

/**
 * 数据库Dao管理器
 * 
 * @author lijinghui
 * 
 */
public interface DaoManager {

	/**
	 * 返回系统配置的数据源对应的Dao对象
	 * 
	 * @param name 数据源名
	 * @return 系统配置的数据源对应的Dao对象
	 */
	Dao getDao(String name);

	/**
	 * 返回系统配置的所有Dao名字
	 * 
	 * @return
	 */
	List<String> getDaoNames();

	/**
	 * 获取制定数据库连接配置
	 * 
	 * @param name
	 * @return
	 */
	DaoConfig loadConfig(String name);
}
