package rainbow.core.model.object;

import rainbow.core.util.Utils;

public class IdNameObject extends IdObject implements INameObject {

	protected String name;

    public IdNameObject() {
    }

    public IdNameObject(String id, String name) {
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
