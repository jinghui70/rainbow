package rainbow.core.model.object;

/**
 * 
 * 有 id 和 name 属性的对象基类
 * 
 * @author lijinghui
 *
 */
public class NameObject<I> extends IdObject<I> implements INameObject {

	protected String name;

    public NameObject() {
    }

    public NameObject(I id, String name) {
    	this.id = id;
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
