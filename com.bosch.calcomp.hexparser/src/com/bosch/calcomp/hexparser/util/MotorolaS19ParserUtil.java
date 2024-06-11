/**********************************************************
 * Copyright (c) 2007, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.util;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.process.MotorolaS19FileRecord;

/**
 * @author dec1kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		22-Jul-2009		Deepa				SPARSER-2: First draft.<br>
 *         </pre>
 */

/**
 * Utilities explicitly for Motorola S19 files.
 *
 * @author dec1kor
 */
public class MotorolaS19ParserUtil {

  /**
   * Header Data
   */
  private short[] motorolaS19HeaderData;

  /**
   * Termination Data
   */
  private short[] motorolaS19TerminationData;

  /**
   * Termination record type
   */
  private int motorolaS19TerminationRecType;

  /**
   * Data record type
   */
  private int motorolaS19DataRecType;

  /**
   * Validates the number of data records with S5 record.
   *
   * @param hexRecord - hex record
   * @param noOfDataRecords - number of record
   * @param logger
   * @throws HexParserException - exception
   */
  public void validateCountOfDataS5Records(final MotorolaS19FileRecord hexRecord, final int noOfDataRecords,
      final ILoggerAdapter logger) {
    if (hexRecord.getCountOfDataS5Records() != noOfDataRecords) {
      logger.info("Invalid S19 file. Invalid S5 record.");
    }
  }

  /**
   * Validates the number of data records with S5 record.
   *
   * @param hexRecord - hex record
   * @param noOfDataRecords - number of record
   * @param logger
   * @throws HexParserException - exception
   */
  public void validateCountOfDataS6Records(final MotorolaS19FileRecord hexRecord, final int noOfDataRecords,
      final ILoggerAdapter logger) {
    if (hexRecord.getCountOfDataS6Records() != noOfDataRecords) {
      logger.info("Invalid S19 file. Invalid S6 record.");
    }
  }

  /**
   * Validates the termination record for the corresponding data type record.
   *
   * @param isTerminationRecordPresent - isTerminationRecordPresent
   * @param logger
   * @throws HexParserException - exception
   */
  public void validateEndOfFile(final boolean isTerminationRecordPresent, final ILoggerAdapter logger) {
    if (isTerminationRecordPresent) {
      String errMsg = null;
      int dataRecType = getMotorolaS19DataRecType();
      int terminationRecType = getMotorolaS19TerminationRecType();

      switch (dataRecType) {
        case MotorolaS19FileRecord.S1_2BYTE_ADDRESS_RECORD:
          if (terminationRecType != MotorolaS19FileRecord.S9_TERMINATION_RECORD) {
            errMsg = "Invalid termination S record. It must be S9.";
          }
          break;

        case MotorolaS19FileRecord.S2_3BYTE_ADDRESS_RECORD:
          if (terminationRecType != MotorolaS19FileRecord.S8_TERMINATION_RECORD) {
            errMsg = "Invalid termination S record. It must be S8.";
          }
          break;

        case MotorolaS19FileRecord.S3_4BYTE_ADDRESS_RECORD:
          if (terminationRecType != MotorolaS19FileRecord.S7_TERMINATION_RECORD) {
            errMsg = "Invalid termination S record. It must be S7.";
          }
          break;

        default:
          errMsg = "Invalid S data record. It must be either of S1, S2 or S3.";
          break;
      }

      if ((errMsg != null) && (errMsg.length() > 0)) {
        logger.error(errMsg);
        throw new HexParserException(errMsg, HexParserException.HEX_FILE_VALIDATION_FAILED);
      }
    }

  }

  /**
   * Sets the Motorola's header record data in binary.
   *
   * @param binData motorolaS19HeaderData
   */
  public void setMotorolaS19Header(final short[] binData) {
    this.motorolaS19HeaderData = binData;
  }

  /**
   * Gets the Motorola's header record data in binary.
   *
   * @return motorolaS19HeaderData
   */
  public short[] getMotorolaS19HeaderData() {
    return this.motorolaS19HeaderData;
  }

  /**
   * Sets the Motorola's termination record type and its data in binary.
   *
   * @param recType record type
   * @param binData binary data
   */
  public void setMotorolaS19TerminationInfo(final int recType, final short[] binData) {
    this.motorolaS19TerminationData = binData;
    this.motorolaS19TerminationRecType = recType;
  }

  /**
   * Gets the Motorola's termination record data in binary.
   *
   * @return motorolaS19TerminationData
   */
  public short[] getMotorolaS19TerminationData() {
    return this.motorolaS19TerminationData;
  }

  /**
   * Gets the Motorola's termination record type.
   *
   * @return motorolaS19TerminationRecType
   */
  public int getMotorolaS19TerminationRecType() {
    return this.motorolaS19TerminationRecType;
  }

  /**
   * Sets the Motorola's data record type.
   *
   * @param recType motorolaS19DataRecType
   */
  public void setMotorolaS19DataRecType(final int recType) {
    this.motorolaS19DataRecType = recType;
  }

  /**
   * Gets the Motorola's data record type.
   *
   * @return motorolaS19DataRecType
   */
  public int getMotorolaS19DataRecType() {
    return this.motorolaS19DataRecType;
  }

}
