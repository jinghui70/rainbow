package rainbow.db.dao.model;

import rainbow.db.model.DataType;

/**
 * 物理数据库字段的描述，用于改变数据库结构
 * 
 * @author lijinghui
 *
 */
public class PureColumn {

	protected String code;

	protected DataType type;

	protected int length;

	protected int precision;

	protected boolean mandatory;

	protected boolean key;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public PureColumn() {
	}

	public PureColumn(String code, DataType type) {
		this(code, type, 0, 0, false);
	}

	public PureColumn(String code, DataType type, int length) {
		this(code, type, length, 0, false);
	}

	public PureColumn(String code, int length, int precision) {
		this(code, DataType.NUMERIC, length, precision, false);
	}

	public PureColumn(String code, DataType type, int length, int precision, boolean mandatory) {
		this.code = code;
		this.type = type;
		this.length = length;
		this.precision = precision;
		this.mandatory = mandatory;
	}
}
