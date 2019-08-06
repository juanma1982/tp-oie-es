package ar.edu.unlp.exceptions;

public class ExceptionArgumentExtractor extends Exception {
	public ExceptionArgumentExtractor(String message) {
		super(message);
	}
	public ExceptionArgumentExtractor(String message,Throwable cause) {
		super(message,cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
