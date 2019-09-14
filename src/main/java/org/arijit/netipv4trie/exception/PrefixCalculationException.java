package org.arijit.netipv4trie.exception;

public class PrefixCalculationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PrefixCalculationException() {
		// TODO Auto-generated constructor stub
	}
	
	public PrefixCalculationException(String message, Throwable th) {
		super(message, th);
	}
}
