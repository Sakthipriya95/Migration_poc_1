/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.hexparser.exception.HexParserException;

/**
 * @author dec1kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		17-Jul-09		Deepa				SPARSER-2: First draft.<br>
 * 0.2		22-Jul-09		Deepa				SPARSER-2: added util methods for Motorola parser.<br>
 *         </pre>
 */

/**
 * Utility class for the hex parsers.
 *
 * @author dec1kor
 */
public class HexParserUtil {

  /**
   * Read one byte from the hexBuffer at the given position. The return type is short, because all JAVA types are signed
   *
   * @param hexBuffer the buffer holding the HEX data
   * @param position the offset in the hexBuffer
   * @param logger
   * @return the byte
   */
  public short getByteAt(final String hexBuffer, final int position, final ILoggerAdapter logger) {
    short byteValue;

    try {
      byteValue = Short.parseShort(hexBuffer.substring(position, position + 2), 16);
    }
    catch (NumberFormatException e) {
      logger.error("illegal character at position " + position + " in line: " + hexBuffer);
      throw new HexParserException("illegal character at position " + position + " in line: " + hexBuffer,
          HexParserException.ILLEGAL_CHARACTER);
    }

    return byteValue;
  }

  /**
   * Read two bytes (one word) from the hexBuffer at the given position. The return type is int, because all JAVA types
   * are signed
   *
   * @param hexBuffer the buffer holding the HEX data
   * @param position the offset in the hexBuffer
   * @param logger
   * @return the word
   */
  public int getWordAt(final String hexBuffer, final int position, final ILoggerAdapter logger) {
    int wordValue;
    try {
      wordValue = Integer.parseInt(hexBuffer.substring(position, position + 4), 16);
    }
    catch (NumberFormatException e) {
      logger.error("illegal character at position " + position + " in line: " + hexBuffer);
      throw new HexParserException("illegal character at position " + position + " in line: " + hexBuffer,
          HexParserException.ILLEGAL_CHARACTER);
    }
    return wordValue;
  }

  /**
   * Read three bytes from the hexBuffer at the given position.
   *
   * @param hexBuffer
   * @param position
   * @param logger
   * @return 3 bytes as long value
   */
  public long getD6WordAt(final String hexBuffer, final int position, final ILoggerAdapter logger) {
    long longValue;
    try {
      longValue = Long.parseLong(hexBuffer.substring(position, position + 6), 16);
    }
    catch (NumberFormatException e) {
      logger.error("illegal character at position " + position + " in line: " + hexBuffer);
      throw new HexParserException("illegal character at position " + position + " in line: " + hexBuffer,
          HexParserException.ILLEGAL_CHARACTER);
    }
    return longValue;
  }

  /**
   * Read four bytes from the hexBuffer at the given position.
   *
   * @param hexBuffer
   * @param position
   * @param logger
   * @return 4 bytes as long value
   */
  public long getDWordAt(final String hexBuffer, final int position, final ILoggerAdapter logger) {
    long longValue;
    try {
      longValue = Long.parseLong(hexBuffer.substring(position, position + 8), 16);
    }
    catch (NumberFormatException e) {
      logger.error("illegal character at position " + position + " in line: " + hexBuffer);
      throw new HexParserException("illegal character at position " + position + " in line: " + hexBuffer,
          HexParserException.ILLEGAL_CHARACTER);
    }
    return longValue;
  }

  /**
   * Determines the hex parser to be created from the factory.
   *
   * @param hexFilePath path of the hex file
   * @param logger
   * @throws HexParserException
   * @return Hex file type: intel hex or motorola s19.
   */
  public String determineHexParser(String hexFilePath, final ILoggerAdapter logger) throws HexParserException {
    String hexFileType = "";
    BufferedReader bRdr = null;
    try {
      if ((hexFilePath != null) && (hexFilePath.trim().length() > 0)) {
        hexFilePath = hexFilePath.trim();
        File hexFile = new File(hexFilePath);
        if (hexFile.exists()) {
          // HEXP-13 --checking hexFilePath ends with .H32
          if (hexFilePath.toLowerCase().endsWith(HexParserConstants.INTEL_HEX_FILE_EXTENSION) ||
              (hexFilePath.toLowerCase().endsWith(HexParserConstants.INTEL_H32_FILE_EXTENSION))) {
            // an Intel hex file
            hexFileType = HexParserConstants.INTEL_HEX_PARSER;
          }
          else if (hexFilePath.toLowerCase().endsWith(HexParserConstants.MOTOROLA_S19_FILE_EXTENSION)) {
            // a Motorola S19 file
            hexFileType = HexParserConstants.MOTOROLA_S19_PARSER;
          }
          else {
            // read the file. Based on the first line, determine the type of parser.
            bRdr = new BufferedReader(new FileReader(hexFile.getAbsolutePath()));
            String line = null;
            while ((line = bRdr.readLine()) != null) {
              line = line.trim();
              if (line != null) {
                if (line.startsWith(HexParserConstants.COLON_IDENTIFIER)) {
                  // an Intel hex file
                  hexFileType = HexParserConstants.INTEL_HEX_PARSER;
                }
                else if (line.startsWith(HexParserConstants.MOTOROLA_S19_FILE_RECORD_IDENTIFIER)) {
                  // a Motorola S19 file
                  hexFileType = HexParserConstants.MOTOROLA_S19_PARSER;
                }
                else {
                  // exception invalid hex file format.
                  logger.error("Invalid file. It must either be a hex file or a s19 file. " + hexFilePath);
                  throw new HexParserException(
                      "Invalid file. It must either be a hex file or a s19 file. " + hexFilePath,
                      HexParserException.INVALID_FILE_FORMAT);
                }
              }
            }
          }
        }
        else {
          // exception file do not exists.
          logger.error("File does not exists: " + hexFilePath);
          throw new HexParserException("File does not exists: " + hexFilePath,
              HexParserException.HEX_FILE_NOT_FOUND_EXCEPTION);
        }
      }
    }
    catch (Exception e) {
      logger.error("Error in determing the type of Hex parser, intel or motorola file types.");
      throw new HexParserException("Error in determing the type of Hex parser, intel or motorola file types.",
          HexParserException.INVALID_FILE_FORMAT);
    }
    finally {
      if (bRdr != null) {
        try {
          bRdr.close();
          bRdr = null;
        }
        catch (IOException e) {
          logger.error("Error in determing the type of Hex parser, intel or motorola file types.");
          throw new HexParserException("Error in determing the type of Hex parser, intel or motorola file types.",
              HexParserException.INVALID_FILE_FORMAT);
        }
      }
    }
    return hexFileType;
  }

}
