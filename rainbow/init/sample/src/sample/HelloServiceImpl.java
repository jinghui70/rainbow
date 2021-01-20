package sample;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;

@Bean
public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		if (Utils.isNullOrEmpty(name))
			return "hello world!";
		return "hello " + name + "!";
	}
}
