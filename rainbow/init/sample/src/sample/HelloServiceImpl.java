package sample;

import rainbow.core.bundle.Bean;

@Bean
public class HelloServiceImpl implements HelloService {

	public String sayHello(String name) {
		return "hello " + name + "!";
	}
}
