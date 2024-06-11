/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.factory;

import java.io.InputStream;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.hexparser.IHexParser;
import com.bosch.calcomp.hexparser.IntelHexParser;
import com.bosch.calcomp.hexparser.MotorolaS19Parser;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.util.HexParserConstants;
import com.bosch.calcomp.hexparser.util.HexParserUtil;

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
 * Factory class which created the hex parser for the given hex file.
 *
 * @author dec1kor
 */
public class HexParserFactory {

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private final HexParserUtil hexParserUtil = new HexParserUtil();

  /**
   * Creates the hex parser based on the file type.
   *
   * @param inputStream1
   * @param hexFilePath
   * @param logger
   * @return IHexParser
   * @throws HexParserException
   */
  public IHexParser createHexParser(final String hexFilePath, final ILoggerAdapter logger) {
    IHexParser hexParser = null;

    // Hex file type: intel hex or motorola s19.
    String hexFileType = this.hexParserUtil.determineHexParser(hexFilePath, logger);

    if (HexParserConstants.INTEL_HEX_PARSER.equals(hexFileType)) {
      hexParser = new IntelHexParser(hexFilePath, logger, this.hexParserUtil);
    }
    else if (HexParserConstants.MOTOROLA_S19_PARSER.equals(hexFileType)) {
      hexParser = new MotorolaS19Parser(hexFilePath, logger);
    }

    return hexParser;

  }

  // ALM-280807
  /**
   * @param inputStream
   * @param hexFileType
   * @return
   */
  public IHexParser createHexParser(final InputStream inputStream, String hexFileType, final ILoggerAdapter logger) {
    IHexParser hexParser = null;
    if (hexFileType != null) {
      if (!hexFileType.startsWith(".")) {
        hexFileType = ".".concat(hexFileType);
      }
      // Hex file type: intel hex or motorola s19.
      if (HexParserConstants.INTEL_HEX_FILE_EXTENSION.equals(hexFileType) ||
          (HexParserConstants.INTEL_H32_FILE_EXTENSION.equals(hexFileType))) {
        hexParser = new IntelHexParser(inputStream, logger, this.hexParserUtil);
      }
      else if (HexParserConstants.MOTOROLA_S19_FILE_EXTENSION.equals(hexFileType)) {
        hexParser = new MotorolaS19Parser(inputStream, logger);
      }
      else {
        throw new HexParserException("Unable to identify the hex type!! Please provide valid the hex type");
      }

    }
    return hexParser;

  }


}
