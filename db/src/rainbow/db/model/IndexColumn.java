package rainbow.db.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "IndexColumn", propOrder = { "name", "asc" })
public class IndexColumn implements Cloneable {

	@XmlElement(required = true)
	private String name = "";

	private boolean asc = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	@Override
	public IndexColumn clone() {
		try {
			return (IndexColumn) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
