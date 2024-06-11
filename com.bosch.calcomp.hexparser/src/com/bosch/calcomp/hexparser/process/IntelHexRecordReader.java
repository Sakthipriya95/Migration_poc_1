/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.process;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.util.HexParserUtil;

/**
 * @author dec1kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		09-Sep-2009		Deepa				HEXP-1: First draft.<br>
 *         </pre>
 */
/**
 * Reader class for the Intel hex file record.
 *
 * @author dec1kor
 */
public class IntelHexRecordReader {

  /**
   * Class instance.
   */
  private IntelHexRecord intelHexRecord = null;

  private ILoggerAdapter logger = null;

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private HexParserUtil hexParserUtil = null;

  /**
   * Constructor with the IntelHexRecord.
   *
   * @param intelHexRecord IntelHexRecord
   */
  public IntelHexRecordReader(final IntelHexRecord intelHexRecord) {
    this.intelHexRecord = intelHexRecord;
    this.logger = this.intelHexRecord.getLogger();
    this.hexParserUtil = this.intelHexRecord.getHexParserUtil();
  }

  /**
   * Parses the Intel's hex record.
   *
   * @throws HexParserException
   */
  public void readIntelHexRecord() throws HexParserException {
    String hexBuffer = null;
    try {
      int lineChecksum = 0; // the calculated checksum of the line
      short recordLength = 0;
      short recType = 0;
      short[] binData = null;
      short checksum = 0;
      boolean checksumValid;
      hexBuffer = this.intelHexRecord.getHexRecord();

      recordLength = this.hexParserUtil.getByteAt(hexBuffer, 1, this.logger);
      this.intelHexRecord.setRecordLength(recordLength);
      lineChecksum += recordLength;

      this.intelHexRecord.setOffset(this.hexParserUtil.getWordAt(hexBuffer, 3, this.logger));
      lineChecksum += this.hexParserUtil.getByteAt(hexBuffer, 3, this.logger);
      lineChecksum += this.hexParserUtil.getByteAt(hexBuffer, 5, this.logger);

      recType = this.hexParserUtil.getByteAt(hexBuffer, 7, this.logger);
      this.intelHexRecord.setRecType(recType);
      lineChecksum += recType;

      this.intelHexRecord.setHexData(hexBuffer.substring(9, 9 + (recordLength * 2)));
      binData = new short[recordLength];

      // get the binary data bytes
      for (int i = 0; i < recordLength; i++) {
        binData[i] = this.hexParserUtil.getByteAt(this.intelHexRecord.getHexData(), i * 2, this.logger);
        lineChecksum += binData[i];
      }
      this.intelHexRecord.setBinData(binData);

      checksum = this.hexParserUtil.getByteAt(hexBuffer, 9 + (recordLength * 2), this.logger);
      this.intelHexRecord.setChecksum(checksum);
      lineChecksum += checksum;

      checksumValid = ((lineChecksum % 256) == 0);
      if (!checksumValid) {
        this.logger.warn(" Error while Parsing Hex File : Checksum Error!!");
      }
    }
    catch (Exception e) {
      this.logger.error("invalid Hex Record: " + hexBuffer);
      throw new HexParserException("invalid Hex Record: " + hexBuffer, HexParserException.INVALID_HEXRECORD);
    }
  }

}
