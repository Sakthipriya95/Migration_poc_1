package com.bosch.calcomp.hexparser.exception;

import com.bosch.calcomp.exception.CalDataRuntimeException;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 0.2 08-Aug-2008 Deepa SAC-106: Added INVALID_HEXRECORD.<br>
 * 0.3 15-Jul-2009 Deepa HEXP-1: added new constants.<br>
 * 0.4 22-Jul-2009 Deepa SPARSER-2: added HEX_FILE_VALIDATION_FAILED.<br>
 */

/**
 * Defines the HexParser Exceptions.
 * 
 * @author Frank Henze
 */
public class HexParserException extends CalDataRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Illegal Character.
   */
  public static final int ILLEGAL_CHARACTER = -2010;

  /**
   * Invalid file format. It must be adhere to hex or s19 file formats.
   */
  // HEXP-1
  public static final int INVALID_FILE_FORMAT = -2011;

  /**
   * Hex file is not found.
   */
  // HEXP-1
  public static final int HEX_FILE_NOT_FOUND_EXCEPTION = -2012;

  /**
   * Parse error.
   */
  // HEXP-1
  public static final int HEX_FILE_PARSE_EXCEPTION = -2013;

  /**
   * Hex file validation failed.
   */
  // SPARSER-2
  public static final int HEX_FILE_VALIDATION_FAILED = -2014;

  /**
   * No Data in Hex
   */
  public static final int NO_DATA_IN_HEX = -2020;

  /**
   * Byte could not be read.
   */
  public static final int BYTE_COULD_NOT_BE_READ = -2100;

  /**
   * Overlapping data
   */
  public static final int DIFFERENT_OVERLAPPING_DATA = -2200;

  /**
   * Invalid HexRecord
   */
  public static final int INVALID_HEXRECORD = -2300;

  /**
   * Unexpected error.
   */
  public static final int UNEXPECTED_ERROR = -2998;
  private static final int UNKNOWN_ERROR = -2999;

  /**
   * HexParserException with error code and throwable.
   * 
   * @param errorCode error code
   * @param cause     Throwable
   */
  public HexParserException(int errorCode, Throwable cause) {
    super(errorCode, cause);
  }

  /**
   * HexParserException with error message, error code and throwable.
   * 
   * @param errorMessage error message
   * @param errorCode    error code
   * @param cause        Throwable
   */
  public HexParserException(String errorMessage, int errorCode, Throwable cause) {
    super(errorMessage, errorCode, cause);
  }

  /**
   * HexParserException with error message and error code.
   * 
   * @param errorMessgae error message
   * @param errorCode    error code
   */
  public HexParserException(String errorMessgae, int errorCode) {
    super(errorMessgae, errorCode);
  }

  /**
   * HexParserException with error message.
   * 
   * @param errorMessage error message
   */
  public HexParserException(String errorMessage) {
    super(errorMessage, UNKNOWN_ERROR);
  }
}
