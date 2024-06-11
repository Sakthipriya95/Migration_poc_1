package com.bosch.calcomp.pacoparser.exception;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.1 15-May-2007 Parvathy Unnikrishnan First Draft<br>
 * 0.2 31-Dec-2007 Parvathy Added comment for SAX_EXCEPTION<br>
 * 0.3 02-Sep-2008 Parvathy SAC-113, added INVALID_FILE <br>
 */
/**
 * The exception thrown by the paco parser plugin.
 *
 * @author par7kor
 */
public class PacoParserException extends Exception {

  /*
   * This exception class should define only error codes between -8000 and -8999
   */
  /**
   * Serial version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Error code for illegal characteristic type from the paco file.
   */
  public static final int ILLEGAL_CHARACTERISTIC_TYPE = 8100;

  /**
   * Error code for unexpected error.
   */
  public static final int UNEXPECTED_ERROR = 8998;

  /**
   * Error code for paco parser error.
   */
  public static final int PACOPARSER_ERROR = 8001;

  /**
   * Error code for paco parser file IO error.
   */
  public static final int PACOPARSER_IOERROR = 8002;

  /**
   * Error code for sax parser. No entry in errorcode.properties as the error message is directly captured from
   * underlying SaxParser.
   */
  // VCL-182
  public static final int SAX_EXCEPTION = 8003;

  /**
   * Error code for sax parser.
   */
  public static final int CLASS_EXCEPTION = 8004;

  /**
   * Error code for invalid paco file path or file type.
   */
  public static final int INVALID_FILE = 8005;

  /**
   * The internal error code of the exception.
   */
  private int errorCode;

  /**
   * Create a new FileParserException with the error code for an unexpected. error
   */
  public PacoParserException() {
    this.errorCode = UNEXPECTED_ERROR;
  }

  /**
   * Create a new PacoParserException with error message but without an error code. * @param message
   * @param message 
   * message
   */
  public PacoParserException(final String message) {
    super(message);
  }

  /**
   * Create a new PacoParserException with error code but without an error message.
   * 
   * @param errorCode the error code
   */
  public PacoParserException(final int errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Create a new Exception with throwable.
   * 
   * @param exception
   * exception
   */
  public PacoParserException(final Throwable exception) {
    super(exception);
  }

  /**
   * Create a new PacoParserException with error code and error message.
   * 
   * @param errorCode the error code
   * @param message the message
   */
  public PacoParserException(final int errorCode, final String message) {
    super(message);

    this.errorCode = errorCode;
  }

  /**
   * Create a new PacoParserException with error code and causing exception but without a specific error message.
   * 
   * @param errorCode
   * errorCode
   * @param cause
   * cause
   */
  public PacoParserException(final int errorCode, final Throwable cause) {
    super(cause);

    this.errorCode = errorCode;
  }

  /**
   * Create a new PacoParserException with error code, message and causing exception.
   * 
   * @param errorCode
   * errorCode
   * @param message
   * message
   * @param cause
   * cause
   */
  public PacoParserException(final int errorCode, final String message, final Throwable cause) {
    super(message, cause);

    this.errorCode = errorCode;
  }

  /**
   * Get the error code of the exception.
   * 
   * @return the error code
   */
  public final int getErrorCode() {
    return this.errorCode;
  }

}
