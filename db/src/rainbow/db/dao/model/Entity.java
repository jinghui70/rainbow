package rainbow.db.dao.model;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

import rainbow.core.model.object.INameObject;
import rainbow.db.dao.Field;
import rainbow.db.model.Column;

public class Entity implements INameObject, Function<String, Field> {

	private rainbow.db.model.Entity origin;
	
	private Map<String, Column> columnMap;

	private List<Column> keys;

	public rainbow.db.model.Entity getOrigin() {
		return origin;
	}
	
	@Override
	public String getName() {
		return origin.getName();
	}

	public String getDbName() {
		return "";//origin.getDbName();
	}

	public String getCnName() {
		return "";//origin.getCnName();
	}

	public List<Column> getColumns() {
		return null;//origin.getColumns();
	}

	public Column getColumn(String columnName) {
		return columnMap.get(columnName);
	}

	public Entity(rainbow.db.model.Entity src) {
		this.origin = src;
//		ImmutableList.Builder<Column> listBuilder = ImmutableList.builder();
//		ImmutableMap.Builder<String, Column> mapBuilder = ImmutableMap.builder();
//		ImmutableList.Builder<Column> keyBuilder = ImmutableList.builder();
//		checkState(!Utils.isNullOrEmpty(src.getColumns()), "Entity %s has no column", src.getName());
//
//		for (Column column : src.getColumns()) {
//			checkNotNull(column.getName(), "Entity %s has a null name column", column);
//			listBuilder.add(column);
//			mapBuilder.put(column.getName(), column);
//			if (column.isKey())
//				keyBuilder.add(column);
//		}
//		columnMap = mapBuilder.build();
//		keys = keyBuilder.build();
	}

	public List<Column> getKeys() {
		return keys;
	}

	public int getKeyCount() {
		return keys.size();
	}

	@Override
	public String toString() {
		return new StringBuilder("Entity [name=").append(getName()).append("]").toString();
	}

	@Override
	public Field apply(String input) {
		return new Field(input, this);
	}

}
