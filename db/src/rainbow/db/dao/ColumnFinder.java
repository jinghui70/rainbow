package rainbow.db.dao;

import rainbow.db.dao.model.Column;

public interface ColumnFinder {

	Column find(String tableAlias, String fieldName);
	
}
