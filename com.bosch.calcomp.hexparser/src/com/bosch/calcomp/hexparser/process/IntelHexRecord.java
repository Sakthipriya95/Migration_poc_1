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
 * 0.2		08-Aug-2008		Deepa		SAC-106: Modified constructor. Added exception handling.<br>
 *         </pre>
 */

/**
 * A HexRecord is one line out of a Intel-HEX file.
 *
 * @author DS/SAE2-Henze
 */
public class IntelHexRecord {

  public static final byte DATA_RECORD = 0;
  public static final byte END_RECORD = 1;
  public static final byte EXTENDED_SEGMENT_ADDRESS_RECORD = 2;
  public static final byte START_SEGMENT_ADDRESS_RECORD = 3;
  public static final byte EXTENDED_LINEAR_ADDRESS_RECORD = 4;
  public static final byte START_LINEAR_ADDRESS_RECORD = 5;

  // then number of data byte in the record
  private short recordLength;

  // the address offset of the record
  private int offset;

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

  // each line in hex file.
  private String hexRecord;

  private final ILoggerAdapter logger;

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private final HexParserUtil hexParserUtil;

  /**
   * Create a HEX Record instance of a given line out of an Intel HEX file
   *
   * @param hexBuffer
   * @param logger
   * @param hexParserUtil - HexParserUtil
   */
  public IntelHexRecord(final String hexBuffer, final ILoggerAdapter logger, final HexParserUtil hexParserUtil) {
    setHexRecord(hexBuffer);
    this.logger = logger;
    this.hexParserUtil = hexParserUtil;
    IntelHexRecordReader handler = new IntelHexRecordReader(this);
    handler.readIntelHexRecord();
  }

  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }

  /**
   * Sets the hex record.
   *
   * @param hexBuffer each line in hex file
   */
  private void setHexRecord(final String hexBuffer) {
    this.hexRecord = hexBuffer;
  }

  /**
   * Gets the hex record.
   *
   * @return the hexRecord
   */
  public String getHexRecord() {
    return this.hexRecord;
  }

  /**
   * Get the number of data bytes in the record.
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
   * Get the offset address of the record.
   *
   * @return the address offset of the record
   */
  public int getOffset() {
    return this.offset;
  }

  /**
   * Sets the offset address of the record.
   *
   * @param offset the offset to set
   */
  public void setOffset(final int offset) {
    this.offset = offset;
  }

  /**
   * Get the type of the record.
   *
   * @return the record type
   */
  public int getRecType() {
    return this.recType;
  }

  /**
   * Sets the type of the record.
   *
   * @param recType the recType to set
   */
  public void setRecType(final short recType) {
    this.recType = recType;
  }

  /**
   * Get the HEX records data part as a string.
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
   * Gets the HEX records data part as an array.
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
   * Gets the checksum given in the HEX record.
   *
   * @return the checksum
   */
  public int getChecksum() {
    return this.checksum;
  }

  /**
   * Sets the checksum given in the HEX record.
   *
   * @param checksum the checksum to set
   */
  public void setChecksum(final short checksum) {
    this.checksum = checksum;
  }

  /**
   * Check if the given checksum is valid
   *
   * @return true, if the checksum is valid
   */
  public boolean isChecksumValid() {
    return this.checksumValid;
  }


  /**
   * @return the hexParserUtil
   */
  public HexParserUtil getHexParserUtil() {
    return this.hexParserUtil;
  }

}