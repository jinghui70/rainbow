package rainbow.core.model.object;


/**
 * 
 * 有name属性的对象基类
 * 
 * @author lijinghui
 * 
 */
public class SimpleNameObject implements INameObject {

    protected String name;

    public SimpleNameObject() {
    }

    public SimpleNameObject(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return (name == null) ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleNameObject other = (SimpleNameObject) obj;
        if (name == null) 
            return other.name == null;
        else
            return name.equals(other.name);
    }

}
