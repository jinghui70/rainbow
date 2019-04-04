package rainbow.core.model.object;

import rainbow.core.util.Utils;

public class IdNameObject<I> extends IdObject<I> implements INameObject {

	protected String name;

    public IdNameObject() {
    }

    public IdNameObject(I id, String name) {
    	super();
    	setId(id);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Utils.format("id:{},name:{}", id, name);
    }

}
