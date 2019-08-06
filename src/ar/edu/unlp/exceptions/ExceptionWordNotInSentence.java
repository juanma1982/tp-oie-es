package ar.edu.unlp.exceptions;

public class ExceptionWordNotInSentence extends Exception {

	public ExceptionWordNotInSentence(String message) {
		super(message);
	}
	public ExceptionWordNotInSentence(String message,Throwable cause) {
		super(message,cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

}
