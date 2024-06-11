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
 * Reader class for the Motorola S19 file record.
 *
 * @author dec1kor
 */
public class MotorolaS19FileRecordReader {

  /**
   * Class instance.
   */
  private MotorolaS19FileRecord motorolaS19FileRecord = null;

  private final ILoggerAdapter logger;

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private final HexParserUtil hexParserUtil;

  /**
   * Constructor which takes MotorolaS19FileRecord as a parameter.
   *
   * @param motorolaS19FileRecord MotorolaS19FileRecord
   */
  public MotorolaS19FileRecordReader(final MotorolaS19FileRecord motorolaS19FileRecord) {
    this.motorolaS19FileRecord = motorolaS19FileRecord;
    this.logger = motorolaS19FileRecord.getLogger();
    this.hexParserUtil = motorolaS19FileRecord.getHexParserUtil();
  }

  /**
   * Parses the Motorola's S record.
   *
   * @throws HexParserException
   */
  public void readMotorolaS19Record() throws HexParserException {
    String hexBuffer = null;
    try {
      hexBuffer = this.motorolaS19FileRecord.getSRecord();
      int lineChecksum = 0; // the calculated checksum of the line
      short recordLength = 0;
      short[] binData = null;
      boolean checksumValid;

      this.motorolaS19FileRecord.setRecType(Short.parseShort(hexBuffer.substring(1, 2)));
      recordLength = this.hexParserUtil.getByteAt(hexBuffer, 2, this.logger);
      this.motorolaS19FileRecord.setRecordLength(recordLength);
      lineChecksum += recordLength;

      switch (this.motorolaS19FileRecord.getRecType()) {
        case 0: // address will always be 0's.
        case 1: // 2 byte address
          binData = new short[recordLength - 3];
          this.motorolaS19FileRecord.setSaddress(this.hexParserUtil.getWordAt(hexBuffer, 4, this.logger));
          this.motorolaS19FileRecord.setHexData(hexBuffer.substring(8, (recordLength * 2) + 2));
          lineChecksum = calculateCheckSumFor2ByteAddrRec(hexBuffer, lineChecksum, binData);
          break;
        case 2: // s2 3 byte address
        case 8: // s8 3 byte address
          binData = new short[recordLength - 4];
          this.motorolaS19FileRecord.setSaddress(this.hexParserUtil.getD6WordAt(hexBuffer, 4, this.logger));
          this.motorolaS19FileRecord.setHexData(hexBuffer.substring(10, (recordLength * 2) + 2));
          lineChecksum = calculateChecksumFor3ByteAddrRec(hexBuffer, lineChecksum, binData);
          break;
        case 3: // s3 4-byte address
        case 7: // s7 4 byte address
          binData = new short[recordLength - 5];
          this.motorolaS19FileRecord.setSaddress(this.hexParserUtil.getDWordAt(hexBuffer, 4, this.logger));
          this.motorolaS19FileRecord.setHexData(hexBuffer.substring(12, (recordLength * 2) + 2));
          lineChecksum = calculateChecksumFor4ByteAddrRec(hexBuffer, lineChecksum, binData);
          break;
        case 5: // s5 2 byte address
        
          if(hexBuffer.length()==10)
          {

          // S5, length, count in the address field, checksum
          this.motorolaS19FileRecord.setCountOfDataS5Records(this.hexParserUtil.getWordAt(hexBuffer, 4, this.logger));
          lineChecksum = calculateCheckSumFor2ByteAddrRec(hexBuffer, lineChecksum, binData);
          }else {
            this.motorolaS19FileRecord.setChecksum((short) -1);
            lineChecksum -= recordLength;
            this.logger.warn("invalid Hex Record: " + hexBuffer + " in line number : "+ this.motorolaS19FileRecord.getLineNumber()+"\n" ); 
          }
          break;
        case 6: // s6 3 byte address
          if(hexBuffer.length()==10)
          {
            
          // S6, length, count in the address field, checksum
          this.motorolaS19FileRecord.setCountOfDataS6Records(this.hexParserUtil.getD6WordAt(hexBuffer, 4, this.logger));
          lineChecksum = calculateChecksumFor3ByteAddrRec(hexBuffer, lineChecksum, binData);
          }else {
            this.motorolaS19FileRecord.setChecksum((short) -1);
            lineChecksum -= recordLength;
            this.logger.warn("invalid Hex Record: " + hexBuffer + " in line number :"+ this.motorolaS19FileRecord.getLineNumber()+"\n" ); 
          }
          break;
        case 9: // s9 2 byte address
          binData = new short[recordLength - 3];
          this.motorolaS19FileRecord.setSaddress(this.hexParserUtil.getWordAt(hexBuffer, 4, this.logger));
          lineChecksum += this.hexParserUtil.getByteAt(hexBuffer, 4, this.logger);
          lineChecksum += this.hexParserUtil.getByteAt(hexBuffer, 6, this.logger);
          this.motorolaS19FileRecord.setHexData(hexBuffer.substring(8, (recordLength * 2) + 2));
          lineChecksum = calculateCheckSumFor2ByteAddrRec(hexBuffer, lineChecksum, binData);
          break;
        default:
          break;
      }


      lineChecksum += this.motorolaS19FileRecord.getChecksum() + 1;
      checksumValid = ((lineChecksum % 256) == 0);
      if (!checksumValid) {
        this.logger.warn(" Error while Parsing Hex File : Checksum Error!!");
      }

    }
    catch (Exception e) {

      this.logger.error("invalid Hex Record: " + hexBuffer + "\n" + e.getLocalizedMessage());
      throw new HexParserException("invalid Hex Record: " + hexBuffer, HexParserException.INVALID_HEXRECORD);
    }
  }

  /**
   * Calculates the checksum for 4 byte address S record.
   *
   * @param hexBuffer
   * @param lineChecksum
   * @param binData
   * @return calculated checksum
   */
  private int calculateChecksumFor4ByteAddrRec(final String hexBuffer, final int lineChecksum, final short[] binData) {
    int checkSumLine = lineChecksum;
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 4, this.logger);
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 6, this.logger);
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 8, this.logger);
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 10, this.logger);
    int recordLength = this.motorolaS19FileRecord.getRecordLength();
    String hexData = this.motorolaS19FileRecord.getHexData();
    for (int i = 0; i < (recordLength - 5); i++) {
      binData[i] = this.hexParserUtil.getByteAt(hexData, i * 2, this.logger);
      checkSumLine += binData[i];
    }
    this.motorolaS19FileRecord.setBinData(binData);
    this.motorolaS19FileRecord
        .setChecksum(this.hexParserUtil.getByteAt(hexBuffer, (recordLength * 2) + 2, this.logger));
    return checkSumLine;
  }

  /**
   * Calculates the checksum for 3 byte address S record.
   *
   * @param hexBuffer
   * @param lineChecksum
   * @param binData
   * @return calculated checksum
   */
  private int calculateChecksumFor3ByteAddrRec(final String hexBuffer, final int lineChecksum, final short[] binData) {
    int checkSumLine = lineChecksum;
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 4, this.logger);
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 6, this.logger);
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 8, this.logger);
    int recordLength = this.motorolaS19FileRecord.getRecordLength();
    String hexData = this.motorolaS19FileRecord.getHexData();
    for (int i = 0; i < (recordLength - 4); i++) {
      binData[i] = this.hexParserUtil.getByteAt(hexData, i * 2, this.logger);
      checkSumLine += binData[i];
    }
    this.motorolaS19FileRecord.setBinData(binData);
    this.motorolaS19FileRecord
        .setChecksum(this.hexParserUtil.getByteAt(hexBuffer, (recordLength * 2) + 2, this.logger));
    return checkSumLine;
  }

  /**
   * Calculates the checksum for 2 byte address S record.
   *
   * @param hexBuffer
   * @param lineChecksum
   * @param binData
   * @return calculated checksum
   */
  private int calculateCheckSumFor2ByteAddrRec(final String hexBuffer, final int lineChecksum, final short[] binData) {
    int checkSumLine = lineChecksum;
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 4, this.logger);
    checkSumLine += this.hexParserUtil.getByteAt(hexBuffer, 6, this.logger);
    int recordLength = this.motorolaS19FileRecord.getRecordLength();
    String hexData = this.motorolaS19FileRecord.getHexData();
    for (int i = 0; i < (recordLength - 3); i++) {
      binData[i] = this.hexParserUtil.getByteAt(hexData, i * 2, this.logger);
      checkSumLine += binData[i];
    }
    this.motorolaS19FileRecord.setBinData(binData);
    this.motorolaS19FileRecord
        .setChecksum(this.hexParserUtil.getByteAt(hexBuffer, (recordLength * 2) + 2, this.logger));

    return checkSumLine;
  }

}
