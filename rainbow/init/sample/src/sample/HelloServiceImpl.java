package sample;

import rainbow.core.bundle.Bean;
import rainbow.core.util.Utils;

@Bean
public class HelloServiceImpl implements HelloService {

	public String sayHello(String name) {
		return "hello " + name + "!";
	}
}
