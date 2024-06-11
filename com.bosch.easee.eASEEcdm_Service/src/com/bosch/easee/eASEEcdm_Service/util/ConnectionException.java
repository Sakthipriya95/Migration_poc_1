package com.bosch.easee.eASEEcdm_Service.util;

/**
 * @author vau3cob
 *
 */
public class ConnectionException extends Exception {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 2435425743287824862L;

	/**
	 * 
	 */
	public ConnectionException() {
		super();
	}

	/**
	 * @param e
	 */
	public ConnectionException(Throwable e) {
		super(e);
	}

	/**
	 * @param message
	 */
	public ConnectionException(final String message) {
		super(message);	
	}

	
}
