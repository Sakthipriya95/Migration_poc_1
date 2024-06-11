/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser;

import java.util.List;
import java.util.Map;

import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.model.HexMemory;
import com.bosch.calcomp.hexparser.model.handler.HexParserDataHandler;

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
 * This class must be implemented by all the hex specific parsers like Intel's hex file, Motorola's s19 file.
 */
public interface IHexParser {

  /**
   * Parses the hex file set in the specific parser and return the Hex model.
   *
   * @return HexMemory
   * @throws HexParserException
   */
  public HexMemory parse() throws HexParserException;


  /**
   * Gets the Hex memory.
   *
   * @return HexMemory
   */
  public HexMemory getHexMemory();

  /**
   * Gets the HexParserDataHandler to access the hex parser data. It acts like an interface to other components.
   *
   * @return HexParserDataHandler
   */
  public HexParserDataHandler getHexParserDataHandler();

  /**
   * Gets the count of errors.
   *
   * @return number of errors.
   */
  public int getNoOfErrors();

  /**
   * Gets the messages from the parser, can be of many infos and warns.
   *
   * @return StringBuilder
   */
  public StringBuilder getMessageBuffer();

  /**
   * Gets the hex file name that is parsed.
   *
   * @return hex file name
   */
  public String getFileName();

  /**
   * Gets the number of lines present in the hex file.
   *
   * @return number of lines.
   */
  public int getLineCount();

  /**
   * Gets the time taken for parsing the hex file.
   *
   * @return time taken for parsing.
   */
  public long getAnalyzeTime();

  // HEXP-6
  /**
   * Collects the warning messages while parsing the hex file
   *
   * @return the warningsMap
   */
  public Map<String, List<String>> getWarningsMap();


}
