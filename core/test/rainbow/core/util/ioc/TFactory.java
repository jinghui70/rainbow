package rainbow.core.util.ioc;

public class TFactory implements Factory<String>, InitializingBean {

	private int seed;

	@Override
	public String create() {
		return Integer.toString(seed++);
	}

	@Override
	public Class<String> targetClass() {
		return String.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		seed = 100;
	}

}
