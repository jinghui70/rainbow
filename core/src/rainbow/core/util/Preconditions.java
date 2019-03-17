package rainbow.core.util;

public final class Preconditions {
	private Preconditions() {
	}

	public static void checkArgument(boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException();
		}
	}

	public static void checkArgument(boolean expression, String errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(Utils.format(errorMessageTemplate, errorMessageArgs));
		}
	}
	
	public static void checkState(boolean expression) {
		if (!expression) {
			throw new IllegalStateException();
		}
	}

	public static void checkState(boolean expression, String errorMessage) {
		if (!expression) {
			throw new IllegalStateException(errorMessage);
		}
	}


	public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalStateException(Utils.format(errorMessageTemplate, errorMessageArgs));
		}
	}
	
	
	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}


	public static <T> T checkNotNull(T reference, String errorMessage) {
		if (reference == null) {
			throw new NullPointerException(errorMessage);
		}
		return reference;
	}
	
	public static <T> T checkNotNull(T reference, String errorMessageTemplate,
			Object ... errorMessageArgs) {
		if (reference == null) {
			throw new NullPointerException(Utils.format(errorMessageTemplate, errorMessageArgs));
		}
		return reference;
	}

}
