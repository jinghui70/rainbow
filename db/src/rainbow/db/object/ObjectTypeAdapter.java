package rainbow.db.object;

public abstract class ObjectTypeAdapter implements ObjectType {

    @Override
    public boolean hasSubType() {
        return false;
    }

    @Override
    public String getObjectName(Object key) {
        return key.toString();
    }

    @Override
    public String getObjectName(String subType, Object key) {
        return getObjectName(key);
    }

}
