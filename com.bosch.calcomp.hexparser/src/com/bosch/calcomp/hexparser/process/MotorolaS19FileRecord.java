/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser.process;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.hexparser.util.HexParserUtil;

/**
 * @author dec1kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		15-Jul-09		Deepa				SPARSER-2: First draft.<br>
 * 0.2		17-Jul-09		Deepa				SPARSER-2: added readMotorolaS19Record().<br>
 * 0.3		20-Jul-09		Jagadeesh			SPARSER-2: modified readMotorolaS19Record().<br>
 *         </pre>
 */
/**
 * Holds the S record of Motorola's S19 file.
 *
 * @author dec1kor
 */
public class MotorolaS19FileRecord {

  public static final byte S0_HEADER_RECORD = 0;
  public static final byte S1_2BYTE_ADDRESS_RECORD = 1;
  public static final byte S2_3BYTE_ADDRESS_RECORD = 2;
  public static final byte S3_4BYTE_ADDRESS_RECORD = 3;
  public static final byte S5_COUNT_RECORD = 5;
  public static final byte S7_TERMINATION_RECORD = 7; // for S3
  public static final byte S8_TERMINATION_RECORD = 8; // for S2
  public static final byte S9_TERMINATION_RECORD = 9; // for S1
  public static final int S6_COUNT_RECORD = 6;


  // then number of data byte in the record
  private short recordLength;

  // the Record Type
  private short recType;

  // the given checksum of the record
  private short checksum;

  // flag if the given checksum is valid
  private boolean checksumValid;

  // the data part of the record
  private String hexData;

  // the binary data part of the record
  private short[] binData;

  // the complete address of the record
  private long saddress;

  // count of data records in S5 record
  private int countOfDataS5Records;

  // count of data records in S6 record
  private long countOfDataS6Records;

  private String sRecord;
  private final ILoggerAdapter logger;
  
  private int lineNumber;

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private final HexParserUtil hexParserUtil;

  /**
   * Create a HEX Record instance of a given line out of a Motorola s19 file
   *
   * @param hexBuffer
   * @param logger
   * @param hexParserUtil - HexParserUtil
   * @param lineCount 
   */
  public MotorolaS19FileRecord(final String hexBuffer, final ILoggerAdapter logger, final HexParserUtil hexParserUtil, int lineCount) {
    setSRecord(hexBuffer);
    this.logger = logger;
    this.hexParserUtil = hexParserUtil;
    this.setLineNumber(lineCount);
    MotorolaS19FileRecordReader handler = new MotorolaS19FileRecordReader(this);
    handler.readMotorolaS19Record();
  }


  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }

  /**
   * Gets the number of data bytes in the record.
   *
   * @return the number of data bytes
   */
  public int getRecordLength() {
    return this.recordLength;
  }

  /**
   * Sets the number of data bytes in the record.
   *
   * @param recordLength the recordLength to set
   */
  public void setRecordLength(final short recordLength) {
    this.recordLength = recordLength;
  }


  /**
   * Get the offset address of the record
   *
   * @return the address offset of the record
   */
  public long getAddress() {
    return this.saddress;
  }

  /**
   * Get the type of the record
   *
   * @return the record type
   */
  public int getRecType() {
    return this.recType;
  }

  /**
   * Sets the record type.
   *
   * @param recType
   */
  public void setRecType(final short recType) {
    this.recType = recType;
  }

  /**
   * Gets the HEX records data part as a string.
   *
   * @return the data string
   */
  public String getHexData() {
    return this.hexData;
  }

  /**
   * Sets the HEX records data part as a string.
   *
   * @param hexData the hexData to set
   */
  public void setHexData(final String hexData) {
    this.hexData = hexData;
  }

  /**
   * Get the HEX records data part as an array
   *
   * @return the data array
   */
  public short[] getBinData() {
    return this.binData;
  }

  /**
   * Sets the HEX records data part as an array.
   *
   * @param binData the binData to set
   */
  public void setBinData(final short[] binData) {
    this.binData = binData;
  }

  /**
   * Get the checksum given in the HEX record.
   *
   * @return the checksum
   */
  public int getChecksum() {
    return this.checksum;
  }

  /**
   * Set the checksum given in the HEX record.
   *
   * @param checksum the checksum to set
   */
  public void setChecksum(final short checksum) {
    this.checksum = checksum;
  }

  /**
   * Check if the given checksum is valid.
   *
   * @return true, if the checksum is valid
   */
  public boolean isChecksumValid() {
    return this.checksumValid;
  }

  /**
   * Gets the count of data records from S5 record.
   *
   * @return countOfDataS5Records
   */
  public int getCountOfDataS5Records() {
    return this.countOfDataS5Records;
  }

  /**
   * Sets the count of data records from S5 record.
   *
   * @param countOfDataS5Records
   */
  public void setCountOfDataS5Records(final int countOfDataS5Records) {
    this.countOfDataS5Records = countOfDataS5Records;
  }

  /**
   * Sets the S record.
   *
   * @param sRecord the sRecord to set
   */
  public void setSRecord(final String sRecord) {
    this.sRecord = sRecord;
  }

  /**
   * Gets the S record.
   *
   * @return the sRecord
   */
  public String getSRecord() {
    return this.sRecord;
  }

  /**
   * Gets the S record address.
   *
   * @return the saddress
   */
  public long getSaddress() {
    return this.saddress;
  }

  /**
   * Sets the S record address.
   *
   * @param saddress the saddress to set
   */
  public void setSaddress(final long saddress) {
    this.saddress = saddress;
  }

  /**
   * @return the countOfDataS6Records
   */
  public long getCountOfDataS6Records() {
    return this.countOfDataS6Records;
  }

  /**
   * @param countOfDataS6Records the countOfDataS6Records to set
   */
  public void setCountOfDataS6Records(final long countOfDataS6Records) {
    this.countOfDataS6Records = countOfDataS6Records;
  }


  /**
   * @return the hexParserUtil
   */
  public HexParserUtil getHexParserUtil() {
    return this.hexParserUtil;
  }


  /**
   * @return the lineNumber
   */
  public int getLineNumber() {
    return lineNumber;
  }


  /**
   * @param lineNumber the lineNumber to set
   */
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

}
