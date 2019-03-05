package rainbow.core.model.object;

/**
 * 有Id,Code,Name的树形对象基类
 * 
 * @author lijinghui
 * 
 */
public class TreeCodeObject<I> extends TreeObject<I> implements INameObject, ICodeObject {

	protected String name;
	
	protected String code;

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
