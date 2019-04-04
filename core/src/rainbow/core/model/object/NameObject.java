package rainbow.core.model.object;

/**
 * 
 * 有 name 属性的对象基类
 * 
 * @author lijinghui
 *
 */
public class NameObject implements INameObject {

	protected String name;

    public NameObject() {
    }

    public NameObject(String name) {
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

}
