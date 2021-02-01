package rainbow.db.model;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

	private Table table;

	private List<Field> fields = new ArrayList<>();

	private Field curField;

	public TableBuilder(String code) {
		table = new Table();
		table.setCode(code);
	}

	public TableBuilder setName(String name) {
		if (curField == null)
			table.setName(name);
		else
			curField.setName(name);
		return this;
	}

	public TableBuilder setLabel(String label) {
		if (curField == null)
			table.setLabel(label);
		else
			curField.setLabel(label);
		return this;
	}

	public TableBuilder addField(String code) {
		curField = new Field();
		curField.setCode(code);
		fields.add(curField);
		return this;
	}

	public TableBuilder setDataType(DataType type) {
		curField.setType(type);
		return this;
	}

	public TableBuilder setLength(int length) {
		curField.setLength(length);
		return this;
	}

	public TableBuilder setVarchar(int length) {
		curField.setType(DataType.VARCHAR);
		curField.setLength(length);
		return this;
	}

	public TableBuilder setNumeric(int length, int precision) {
		curField.setType(DataType.NUMERIC);
		curField.setLength(length);
		curField.setPrecision(precision);
		return this;
	}

	public TableBuilder setKey() {
		curField.setKey(true);
		curField.setMandatory(true);
		return this;
	}

	public TableBuilder setMandatory() {
		curField.setMandatory(true);
		return this;
	}

	public Table build() {
		table.setFields(fields);
		return table;
	}
}
