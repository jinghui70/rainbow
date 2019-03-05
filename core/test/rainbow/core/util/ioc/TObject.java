package rainbow.core.util.ioc;

public class TObject implements InitializingBean {

	private Long timestamp;

	private Double depend;
	
	private Long number = Long.valueOf(5);

	@Override
	public void afterPropertiesSet() throws Exception {
		if (timestamp == 0)
			timestamp = System.currentTimeMillis();
	}

	public Long getTimestamp() {
		return timestamp;
	}

	@Inject
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Double getDepend() {
		return depend;
	}

	@Inject
	public void setDepend(Double depend) {
		this.depend = depend;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

}
