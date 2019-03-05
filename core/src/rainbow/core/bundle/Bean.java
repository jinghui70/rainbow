package rainbow.core.bundle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rainbow.core.util.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bean {
	
	String name() default Utils.NULL_STR;
	
	boolean singleton() default true;
	
	Class<?> extension() default String.class;
}
