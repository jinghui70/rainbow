package rainbow.db.object;


/**
 * 字段翻译配置
 * 
 * @author lijinghui
 *
 */
public class NameRule {
    
    /**
     * 主键字段
     */
    private String keyField;
    
    /**
     * 对象类型
     */
    private String objType;
    
    /**
     * 对象子类型
     */
    private String subType;
    
    /**
     * 翻译后名字字段
     */
    private String nameField;

    public NameRule(String keyField, String objectType, String nameField) {
        setKeyField(keyField);
        setObjType(objectType);
        setNameField(nameField);
    }
    
    public NameRule(String keyField, String objType) {
        this(keyField, objType, keyField);
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        int index = objType.indexOf('|');
        if (index==-1)
            this.objType = objType;
        else {
            this.objType = objType.substring(0, index);
            this.subType = objType.substring(index + 1);
        }
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }
    
}
