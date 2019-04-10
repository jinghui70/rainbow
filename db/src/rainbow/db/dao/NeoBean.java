package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import rainbow.core.util.converter.Converters;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.ColumnType;

public class NeoBean {

	private final static Logger logger = LoggerFactory.getLogger(NeoBean.class);

	private Entity entity;

	private Map<Column, Object> valueMap = Maps.newHashMap();

	public Entity getEntity() {
		return entity;
	}

	public NeoBean(Entity entity) {
		checkNotNull(entity);
		this.entity = entity;
	}

	public NeoBean(Entity entity, Object obj) {
		this(entity);
		init(obj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init(Object obj) {
		if (obj == null) {
			valueMap.clear();
			return;
		}
		if (obj instanceof NeoBean) {
			NeoBean other = (NeoBean) obj;
			for (Column column : entity.getColumns()) {
				Object value = other.getObject(column.getName());
				if (value != null) {
					setValue(column, value);
				}
			}

		} else if (obj instanceof Map) {
			Map map = (Map) obj;
			for (Object key : map.keySet()) {
				Column column = entity.getColumn(key.toString());
				Object value = map.get(key);
				if (value != null)
					setValue(column, value);
			}
		} else {
			init(obj, new ClassInfo(obj.getClass()));
		}
	}

	public void init(Object obj, ClassInfo<?> info) {
		if (obj == null) {
			valueMap.clear();
			return;
		}
		for (Column column : entity.getColumns()) {
			String propertyName = column.getName();
			Property p = info.getProperty(propertyName);
			if (p != null) {
				Object value = p.getValue(obj);
				if (value != null) {
					setValue(column, value);
				}
			}
		}
	}

	public Object getObject(Column column) {
		return valueMap.get(column);
	}

	public Object getObject(String property) {
		Column column = checkNotNull(entity.getColumn(property), "property {} not defined", property);
		return valueMap.get(column);
	}

	/**
	 * 向一个属性设置值，这个函数仅供内部使用
	 * 
	 * @param column
	 * @param value
	 */
	void setObject(Column column, Object value) {
		if (value == null) {
			valueMap.remove(column);
			return;
		}
		valueMap.put(column, value);
	}

	public <T> T getValue(Column column, Class<T> clazz) {
		return Converters.convert(getObject(column), clazz);
	}

	public <T> T getValue(String property, Class<T> clazz) {
		Column column = checkNotNull(entity.getColumn(property));
		return getValue(column, clazz);
	}

	public String getString(String property) {
		return getValue(property, String.class);
	}

	public Integer getInt(String property) {
		return getValue(property, Integer.class);
	}

	public int getInt(String property, int nullDef) {
		Integer v = getInt(property);
		return v == null ? nullDef : v.intValue();
	}

	public Long getLong(String property) {
		return getValue(property, Long.class);
	}

	public long getLong(String property, long nullDef) {
		Long v = getLong(property);
		return v == null ? nullDef : v.longValue();
	}

	public Double getDouble(String property) {
		return getValue(property, Double.class);
	}

	public double getDouble(String property, double nullDef) {
		Double v = getDouble(property);
		return v == null ? nullDef : v.doubleValue();
	}

	public BigDecimal getBigDecimal(String property) {
		return getValue(property, BigDecimal.class);
	}

	public Boolean getBool(String property) {
		return getValue(property, Boolean.class);
	}

	public boolean getBool(String property, boolean nullDef) {
		Boolean v = getBool(property);
		return v == null ? nullDef : v.booleanValue();
	}

	public NeoBean setValue(String property, Object value) {
		Column column = checkNotNull(entity.getColumn(property), "property {} not defined", property);
		return setValue(column, value);
	}

	public NeoBean setValue(Column column, Object value) {
		checkArgument(value != null || !column.isMandatory(), "property {} can not set null", column.getName());
		if (Dao.NOW.equals(value)) {
			checkArgument(java.util.Date.class.isAssignableFrom(column.getType().dataClass()),
					"property {} can't assign NOW", column.getName());
		} else
			value = Converters.convert(value, column.getType().dataClass());
		setObject(column, value);
		return this;
	}

	/**
	 * 具有Blob字段对象，如果存放的是比较大的数据，不适合用byteArray时，才用这个函数。
	 * 因为InputStream是单向读的，因此这个函数只是为了在insert的时候准备参数用的。
	 * 
	 * @param property
	 * @param value
	 * @return
	 */
	public NeoBean setBlob(String property, InputStream value) {
		Column column = checkNotNull(entity.getColumn(property), "property {} not defined", property);
		checkArgument(column.getType() == ColumnType.BLOB, "property {} is not a blob", property);
		setObject(column, value);
		return this;
	}

	public Set<Column> valueColumns() {
		return valueMap.keySet();
	}

	/**
	 * 变身为某一个类实例
	 * 
	 * @param clazz
	 * @param converters
	 * @return
	 */
	public <T> T bianShen(Class<T> clazz) {
		return bianShen(new ClassInfo<T>(clazz));
	}

	public <T> T bianShen(ClassInfo<T> classInfo) {
		T object = classInfo.makeInstance();
		for (Column column : entity.getColumns()) {
			String propertyName = column.getName();
			Property p = classInfo.getProperty(propertyName);
			if (p != null) {
				Object value = getValue(column, p.getType());
				if (value != null) {
					try {
						p.setValue(object, value);
					} catch (Throwable e) {
						logger.error("transfer neobean data {} to property {} failed", value, propertyName, e);
						throw e;
					}
				}

			}
		}
		return object;
	}

}
