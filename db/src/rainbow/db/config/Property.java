package rainbow.db.config;

import javax.xml.bind.annotation.XmlAttribute;

public class Property {

	@XmlAttribute
	private String value;

	@XmlAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s=%s", name, value);
	}

}
