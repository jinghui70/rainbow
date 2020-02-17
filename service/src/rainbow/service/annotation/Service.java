package rainbow.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务默认的id是类名，如果需要修改以避免重名，可以用这个注解指定id
 * 
 * @author lijinghui
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Service {

	String id();

}
