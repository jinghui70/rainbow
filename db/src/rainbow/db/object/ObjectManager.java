package rainbow.db.object;

import java.util.List;

import rainbow.db.internal.ObjectNameRule;

/**
 * 对象翻译管理器
 * 
 * @author lijinghui
 *
 */
public interface ObjectManager {

    /**
     * 翻译一个指定主键对象的名字
     * 
     * @param typeName
     * @param key
     * @return
     */
    String translate(String typeName, Object key);

    /**
     * 根据类标注得到翻译名字的规则
     * 
     * @param clazz
     * @return
     */
    List<ObjectNameRule> getObjectNameRule(Class<?> clazz);

    /**
     * 根据规则给一组对象做翻译
     * 
     * @param source
     * @param rules
     * @return
     */
    <T> List<T> listSetName(List<T> source, List<ObjectNameRule> rules);

    /**
     * 根据规则给一个对象做翻译
     * 
     * @param obj
     * @param rules
     * @return
     */
    <T> T setName(T obj, List<ObjectNameRule> rules);
}
