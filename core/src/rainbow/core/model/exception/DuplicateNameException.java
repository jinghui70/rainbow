package rainbow.core.model.exception;

public class DuplicateNameException extends AppException {

	private static final long serialVersionUID = 1L;

	public DuplicateNameException(String name, String obj) {
		super(String.format("已经有名称为[%s]的[%s]了", name, obj));
	}

}
