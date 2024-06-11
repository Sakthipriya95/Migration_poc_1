/**********************************************************
 * Copyright (c) 2007, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.util;

/**
 * @author dec1kor
 * 
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		15-Jul-09		Deepa				HEXP-1: First draft.<br>
 *         </pre>
 */
/**
 * Constants used for the parser.
 * 
 * @author dec1kor
 */
public class HexParserConstants {

  /**
   * Constant to identify it as intel's hex parser.
   */
  public static final String INTEL_HEX_PARSER = "INTEL_HEX_PARSER";

  /**
   * Constant to identify it as motorola's s19 parser.
   */
  public static final String MOTOROLA_S19_PARSER = "MOTOROLA_S19_PARSER";

  /**
   * Intel's hex file extension.
   */
  public static final String INTEL_HEX_FILE_EXTENSION = ".hex";

  /**
   * Motorola's s19 file extension.
   */
  public static final String MOTOROLA_S19_FILE_EXTENSION = ".s19";

  /**
   * Intel's hex record identifier.
   */
  public static final String COLON_IDENTIFIER = ":";

  /**
   * Motorola's hex record identifier.
   */
  public static final String MOTOROLA_S19_FILE_RECORD_IDENTIFIER = "S";

  /**
   * Maximum size in the HexSegment.
   */
  public static final int MAXIMUM_SEGMENT_SIZE = 64 * 1024;

  /**
   * New line.
   */
  public static final String NEW_LINE = System.getProperty("line.separator");

  // HEXP-13

  /**
   * 
   */
  public static final String INTEL_H32_FILE_EXTENSION = ".h32";

  private HexParserConstants() {}

}
