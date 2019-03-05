package rainbow.core.util.ioc;

public class TDepend {

	@Inject
	private Integer age;

	private String name;

	private String email;

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	@Inject
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	@Inject
	public void setEmail(String email) {
		this.email = email;
	}

}
