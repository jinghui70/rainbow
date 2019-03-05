package rainbow.db.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ColumnType")
@XmlEnum
public enum ColumnType {

    SMALLINT(Short.class),
    INT(Integer.class),
    LONG(Long.class),
    DOUBLE(Double.class),
    NUMERIC(BigDecimal.class),
    DATE(java.sql.Date.class),
    TIME(java.sql.Time.class),
    TIMESTAMP(java.sql.Timestamp.class),
    CHAR(String.class),
    VARCHAR(String.class),
    CLOB(String.class),
    BLOB(byte[].class);
	
	private Class<?> clazz;
	
	ColumnType(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> dataClass() {
		return clazz;
	}
}
