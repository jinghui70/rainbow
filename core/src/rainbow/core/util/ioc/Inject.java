package rainbow.core.util.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rainbow.core.util.Utils;

/**
 * 注入标记。
 * 
 * 需要注意的是，目前虽然可以标注在private的属性上，但是这样的类不能被继承，因为注入处理时没有处理父类。
 * 
 * @author lijinghui
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Inject {

	String value() default Utils.NULL_STR;
	
	/**
	 * 是否强制必须注入
	 * @return
	 */
	boolean obliged() default true;

}
