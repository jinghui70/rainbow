package rainbow.db.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rainbow.core.util.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Name {

    /**
     * 对象类型
     * 
     * @return
     */
    String type();

    /**
     * 对象子类型
     * 
     * @return
     */
    String subType() default Utils.NULL_STR;

    /**
     * 源字段
     * 
     * @return
     */
    String src() default Utils.NULL_STR;

}
