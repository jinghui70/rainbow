package rainbow.core.bundle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rainbow.core.util.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

	/**
	 * 扩展的名字
	 * 
	 * 如果不配置这个属性，注册时取扩展实现类的name属性，如果也没有的话取该类的类名
	 * 
	 * @return
	 */
	String name() default Utils.NULL_STR;

	/**
	 * 扩展加载顺序
	 * 
	 * @return
	 */
	int order() default 0;

	/**
	 * 扩展点定义类
	 * 
	 * 如果不配置这个属性，默认为扩展实现类的第一个实现接口
	 * 
	 * @return
	 */
	Class<?> point() default Object.class;

}
