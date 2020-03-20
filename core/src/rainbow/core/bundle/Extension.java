package rainbow.core.bundle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rainbow.core.util.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

	String name() default Utils.NULL_STR;

	Class<?> point() default Object.class;

}
