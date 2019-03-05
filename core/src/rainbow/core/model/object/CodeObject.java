package rainbow.core.model.object;

/**
 * 有 id code name 属性的对象基类
 * 
 * @author lijinghui
 *
 */
public class CodeObject<I> extends NameObject<I> implements ICodeObject {

	protected String code;
	
	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
