package rainbow.core.util;

/**
 * 增强StringBuild，在连续append的时候处理多余的逗号这样的场景
 * 
 * @author lijinghui
 * 
 */
public class StringBuilderX implements Appendable {

	protected StringBuilder sb = new StringBuilder();

	private String tempStr = null;

	public StringBuilderX() {
	}

	public StringBuilderX(String str) {
		this();
		append(str);
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	public void setSql(String sql) {
		sb.setLength(0);
		sb.append(sql);
	}

	public StringBuilderX append(Object obj) {
		return this.append(obj.toString());
	}

	@Override
	public StringBuilderX append(char ch) {
		checkTemp();
		sb.append(ch);
		return this;
	}

	@Override
	public StringBuilderX append(CharSequence csq) {
		checkTemp();
		sb.append(csq);
		return this;
	}

	@Override
	public StringBuilderX append(CharSequence csq, int start, int end) {
		checkTemp();
		sb.append(csq, start, end);
		return this;
	}

	public StringBuilderX append(String str, int times) {
		checkTemp();
		for (int i = 0; i < times; i++)
			sb.append(str);
		return this;
	}

	public StringBuilderX append(String str, int times, char delimiter) {
		checkTemp();
		if (times == 1)
			sb.append(str);
		else
			for (int i = 1; i < times; i++) {
				sb.append(delimiter);
				sb.append(str);
			}
		return this;
	}

	/**
	 * 临时字符串用来对付 Where and 逗号这样时有时无 的字符串，当后续有append的时候，会被自动append进去
	 * 
	 * @param str
	 * @return
	 */
	public StringBuilderX appendTemp(String str) {
		checkTemp();
		this.tempStr = str;
		return this;
	}

	public StringBuilderX appendTempComma() {
		return this.appendTemp(",");
	}

	/**
	 * 取消temp中存储的字符串
	 * 
	 * @return
	 */
	public StringBuilderX clearTemp() {
		this.tempStr = null;
		return this;
	}

	private void checkTemp() {
		if (tempStr != null)
			sb.append(tempStr);
		tempStr = null;
	}

}
