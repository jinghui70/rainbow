package rainbow.db.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Index", propOrder = { "name","unique","inxColumns"})
public class Index implements Cloneable {

	@XmlElement(required = true)
	private String name;

	private boolean unique;

	/**
     * 索引属性列表
     */
    @XmlElementWrapper(name = "inxColumns")
    @XmlElement(name = "inxColumn", required = true)
    private List<IndexColumn> inxColumns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IndexColumn> getInxColumns() {
        if (inxColumns == null)
        	inxColumns = new ArrayList<IndexColumn>();
		return inxColumns;
	}

	public void setInxColumns(List<IndexColumn> inxColumns) {
		this.inxColumns = inxColumns;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	@Override
	public Index clone() {
		try {
            Index index = (Index) super.clone();
            List<IndexColumn> newInxColumns = new ArrayList<IndexColumn>(getInxColumns().size());
            for (IndexColumn inxColumn : getInxColumns()) {
            	newInxColumns.add(inxColumn.clone());
            }
            index.setInxColumns(newInxColumns);
            return index;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
