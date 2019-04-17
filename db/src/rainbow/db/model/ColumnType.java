package rainbow.db.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ColumnType")
@XmlEnum
public enum ColumnType {
    SMALLINT,
    INT,
    LONG,
    DOUBLE,
    NUMERIC,
    DATE,
    TIME,
    TIMESTAMP,
    CHAR,
    VARCHAR,
    CLOB,
    BLOB;
}
