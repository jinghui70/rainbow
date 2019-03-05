package rainbow.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要检查的session标注。检查时，首先看method上是否定义，没有时检查service上是否定义，仍然没有的时候检查配置文件中全局的定义
 * 
 * @author lijinghui
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Session {

	String[] value(); 
	
}
