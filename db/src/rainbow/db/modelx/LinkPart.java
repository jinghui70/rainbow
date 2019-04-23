package rainbow.db.modelx;

import java.util.List;

public class LinkPart {

	private String table;
	
	private List<String> fields;
	
	private boolean one;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public boolean isOne() {
		return one;
	}

	public void setOne(boolean one) {
		this.one = one;
	}

}
