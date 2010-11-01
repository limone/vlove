package vlove;

public class VirtException extends Exception {

	public VirtException() {
		super();
	}

	public VirtException(String message) {
		super(message);
	}

	public VirtException(Throwable cause) {
		super(cause);
	}

	public VirtException(String message, Throwable cause) {
		super(message, cause);
	}
}