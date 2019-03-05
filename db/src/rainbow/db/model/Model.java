package rainbow.db.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import rainbow.core.util.XmlBinder;

@XmlRootElement
@XmlType(name = "")
public class Model {

	private String name;

	@XmlElementWrapper(name = "entities", required = true)
	@XmlElement(name = "entity", required = true)
	private List<Entity> entities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public static XmlBinder<Model> getXmlBinder() {
		return new XmlBinder<Model>("rainbow.db.model", Model.class.getClassLoader());
	}
}
