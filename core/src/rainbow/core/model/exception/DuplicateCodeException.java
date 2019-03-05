package rainbow.core.model.exception;

public class DuplicateCodeException extends AppException {

    private static final long serialVersionUID = 1L;

    public DuplicateCodeException(String code) {
        super(String.format("代码[%s]已存在", code));
    }

    public DuplicateCodeException(String code, String obj) {
    	super(String.format("已存在代码为[%s]的[%s]", code, obj));
    }
    
}
