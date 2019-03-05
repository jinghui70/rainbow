package rainbow.db.object;

import rainbow.core.model.object.INameObject;

import com.google.common.base.Function;

public class NameObjectType<I, T extends INameObject> extends ObjectTypeAdapter {

    private String name;

    private Function<I, T> func;

    public NameObjectType(String name, Function<I, T> func) {
        this.name = name;
        this.func = func;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getObjectName(Object key) {
        I id = (I) key;
        T obj = func.apply(id);
        return (obj == null) ? id.toString() : obj.getName();
    }

    @Override
    public String getName() {
        return name;
    }

}
