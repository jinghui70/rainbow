package rainbow.db.dao;

import rainbow.db.model.Column;

public interface ColumnFinder {

	Column find(String tableAlias, String fieldName);
	
}
