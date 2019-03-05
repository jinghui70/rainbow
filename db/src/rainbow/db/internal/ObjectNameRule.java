package rainbow.db.internal;

import java.lang.reflect.Method;

public class ObjectNameRule {

    private Method getKeyMethod;

    private Method setNameMethod;

    private String objType;
    
    private String subType;

    public Method getGetKeyMethod() {
        return getKeyMethod;
    }

    public void setGetKeyMethod(Method getKeyMethod) {
        this.getKeyMethod = getKeyMethod;
    }

    public Method getSetNameMethod() {
        return setNameMethod;
    }

    public void setSetNameMethod(Method setNameMethod) {
        this.setNameMethod = setNameMethod;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

}
