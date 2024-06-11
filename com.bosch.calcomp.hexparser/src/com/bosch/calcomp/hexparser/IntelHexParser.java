/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.hexparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.calutil.tools.CalFileReader;
import com.bosch.calcomp.calutil.tools.MessageBuffer;
import com.bosch.calcomp.hexparser.exception.HexParserException;
import com.bosch.calcomp.hexparser.model.HexMemory;
import com.bosch.calcomp.hexparser.model.handler.HexParserDataHandler;
import com.bosch.calcomp.hexparser.process.HexMemoryHandler;
import com.bosch.calcomp.hexparser.process.IntelHexRecord;
import com.bosch.calcomp.hexparser.util.HexParserConstants;
import com.bosch.calcomp.hexparser.util.HexParserUtil;


/**
 * <pre>
 * Version         Date                        Modified by                        Changes
 * ----------------------------------------------------------------------------
 * 0.1                Unknown                        Frank Henze                        First draft.<br>
 * 0.2                15-Jul-09                Deepa                                HEXP-1: Changed the existing HexParser to IntelHexParser.<br>
 *                                                                                                 Added getHexParserDataHandler().<br>
 * </pre>
 */

/**
 * Parses the hex file, with the file extension ".hex" and stores the parsed data in HexMemory. HexParserDataHandler can
 * be used to access the hex parser's data.
 * <P>
 * To invoke the parser,
 * <p>
 * HexParserFactory parserFactory = new HexParserFactory();<br>
 * IHexParser parser = parserFactory.createHexParser("c:\\temp\\testHexFile.hex");<br>
 * parser.parse();<br>
 * <br>
 * <p>
 *
 * @author Frank Henze
 */
public class IntelHexParser implements IHexParser, MessageBuffer {

  private final ILoggerAdapter logger;

  /**
   * the name of the HEX file
   */
  private String fileName;
  // ALM-280807
  /**
   * InputStream
   */
  private InputStream inputStream;

  /**
   * number of parser errors
   */
  private int noOfErrors;

  /**
   * number of lines parsed
   */
  private int lineCount;

  /**
   * parse time in milli seconds
   */
  private long analyzeTime;

  /**
   * A temporary segment is used by the parse() method. The current handled segment by the parse() is stored in this
   * temporary segment. Once the parsing is complete for a segment, the temporary segment is written into the HexMemory.
   * Also the same segment is written as a HexBlock in the HexMemory.
   */

  /**
   * Temporary Segment Size
   */
  private int tempSegmentSize;

  /**
   * Temporary Segment Start Address
   */
  private long tempSegmentStartAddress;

  /**
   * Temporary Segment Offset <-- same as start address
   */
  private int tempSegmentOffset;

  /**
   * Temporary Segment Data
   */
  private short[] tempSegmentData;

  /**
   * Holds all the messages.
   */
  private final StringBuilder messageBuffer;

  /**
   * CalFileReader instance.
   */
  private CalFileReader inFile = null;

  /**
   * HexMemory instance.
   */
  private HexMemory hexMemory;
  private HexMemoryHandler hexMemoryHandler;

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private final HexParserUtil hexParserUtil;

  /**
   * Class name used to initialize logger.
   */
  private final String className = "com.bosch.calcomp.hexparser.IntelHexParser";

  // HEXP-6
  /**
   * HashMap for warnings encountered during the parsing of A2L file
   */
  private final Map<String, List<String>> warningsMap;

  /**
   * Create a new HexParser instance
   *
   * @param logger
   * @param hexFilePath
   * @param hexParserUtil - HexParserUtil
   */
  public IntelHexParser(final String hexFilePath, final ILoggerAdapter logger, final HexParserUtil hexParserUtil) {
    this.messageBuffer = new StringBuilder();
    this.warningsMap = new HashMap<>();
    this.logger = logger;
    this.fileName = hexFilePath;
    this.hexParserUtil = hexParserUtil;
    setFileName(hexFilePath);
    initVars();
    initTempSegment();
  }


  // ALM-280807
  /**
   * Create a new HexParser instance and initialize the HEX file name
   *
   * @param fileName the HEX file name
   */
  public IntelHexParser(final InputStream inputStream, final ILoggerAdapter logger, final HexParserUtil hexParserUtil) {
    this.messageBuffer = new StringBuilder();
    this.warningsMap = new HashMap<>();
    this.logger = logger;
    this.inputStream = inputStream;
    this.hexParserUtil = hexParserUtil;
    setInputStream(inputStream);
    initVars();
    initTempSegment();
  }

  /**
   * Get the current HEX file name
   *
   * @return the HEX file name
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * Set the HEX file name. This will initialize the parser.
   *
   * @param fileName
   */
  private void setFileName(final String fileName) {
    this.fileName = fileName;
    // ALM-280807
    if (this.fileName != null) {
      try {
        this.inputStream = new FileInputStream(fileName);

      }
      catch (FileNotFoundException e) {

        throw new HexParserException("Errors found when parsing the Hex file.");
      }

      initVars();
      initTempSegment();
    }
  }

  /**
   * Appends the parameter 'newMessage' to the messageBuffer
   *
   * @param newMessage
   * @param isError
   */
  public void bufferMessage(final String newMessage, final boolean isError) {
    if (isError) {
      this.noOfErrors++;
    }
    if ((this.messageBuffer != null) && (this.inFile != null)) {
      this.messageBuffer
          .append("line " + this.inFile.getLineCount() + " : " + newMessage + HexParserConstants.NEW_LINE);
    }
  }

  /**
   * Initialize variables
   */
  private void initVars() {
    this.lineCount = -1;
    this.noOfErrors = 0;
    this.analyzeTime = -1;

    this.hexMemory = null;

    this.hexMemoryHandler = null;

    // string buffers
    this.messageBuffer.delete(0, this.messageBuffer.length());
  }

  /**
   * Create a new temporary segment
   */
  private void initTempSegment() {
    this.tempSegmentSize = 0;
    this.tempSegmentOffset = 0;
    this.tempSegmentStartAddress = -1;
    this.tempSegmentData = new short[HexParserConstants.MAXIMUM_SEGMENT_SIZE];
  }

  /**
   * Save the temporary segment
   */
  private void storeTempSegment() {
    if (this.tempSegmentSize > 0) {
      // store the last segment
      this.hexMemoryHandler.addData(this.tempSegmentData, this.tempSegmentStartAddress, this.tempSegmentSize);
      initTempSegment();
    }
  }

  /**
   * Get the next record from the HEX file.
   * <p>
   * The next line is read from the Hex file. It is converted to a Hex Record and returned.
   * <p>
   *
   * @return the next HEX record
   */
  private IntelHexRecord getNextHexRecord() {
    boolean hexRecordFound = false;
    String currentLine = "";
    IntelHexRecord newHexRecord = null;

    while (!hexRecordFound && !this.inFile.isEndOfFile()) {
      currentLine = this.inFile.getLine();

      if ((!this.inFile.isEndOfFile()) && (currentLine.charAt(0) == HexParserConstants.COLON_IDENTIFIER.charAt(0))) {
        // data line
        newHexRecord = new IntelHexRecord(currentLine, this.logger, this.hexParserUtil);
        hexRecordFound = true;
      }
    }

    if (hexRecordFound) {
      return newHexRecord;
    }
    return null;
  }

  /**
   * Parse the Intel HEX file.
   */
  @Override
  public HexMemory parse() throws HexParserException {

    // ALM-280807
    if (this.fileName != null) {
      setFileName(this.fileName);
    }

    this.logger
        .info(this.className + HexParserConstants.COLON_IDENTIFIER + " Parsing of HEX file started: " + getFileName());

    // the base address of the current segment
    long linearBaseAddress = 0l; // must be long since integer is always
                                 // signed
    long segmentBaseAddress = 0l; // must be long since integer is always
                                  // signed
    long newLinearBaseAddress = 0l; // must be long since integer is always
                                    // signed
    long newSegmentBaseAddress = 0l; // must be long since integer is
                                     // always signed
    /**
     * Start address of the current segment handled by this method.
     */
    long hexSegmentStartAddress = 0l; // must be long since integer is
                                      // always signed

    short[] binData;
    IntelHexRecord hexRecord;

    // initialize the start time
    java.util.Date startTime = new java.util.Date();

    if (this.inFile == null) {
      // ALM-280807
      this.inFile = new CalFileReader(this.inputStream);
      this.inFile.setMessageBuffer(this);
    }

    initVars();

    this.hexMemory = new HexMemory();
    this.hexMemoryHandler = new HexMemoryHandler(this.hexMemory);

    try {
      while ((hexRecord = getNextHexRecord()) != null) {
        binData = hexRecord.getBinData();

        switch (hexRecord.getRecType()) {
          case IntelHexRecord.DATA_RECORD: {
            parseDataRecordType(hexSegmentStartAddress, binData, hexRecord);
            break;
          }

          case IntelHexRecord.END_RECORD: {
            storeTempSegment();

            break;
          }

          case IntelHexRecord.EXTENDED_SEGMENT_ADDRESS_RECORD: {
            /*
             * The extended segment address specifies bit 4-19 of the destination address => get the high byte (index 0)
             * shift it by 8 add the low byte (index 1) and shift all by 4
             */
            newSegmentBaseAddress = ((binData[0] << 8) + binData[1]) << 4;

            // some HEX files have address records before each data record
            // => avoid not necessary segments
            if (newSegmentBaseAddress != segmentBaseAddress) {
              storeTempSegment();
              linearBaseAddress = 0;
              segmentBaseAddress = newSegmentBaseAddress;

              hexSegmentStartAddress = linearBaseAddress + segmentBaseAddress;
            }

            break;
          }

          // HEXP-14 --changed the error flag from true to false
          case IntelHexRecord.START_SEGMENT_ADDRESS_RECORD: {
            // specifies the execution start address
            bufferMessage("not handled record type " + hexRecord.getRecType(), false);
            break;
          }

          case IntelHexRecord.EXTENDED_LINEAR_ADDRESS_RECORD: {
            /*
             * The extended linear address specifies bit 16-31 of the destination address => get the high byte (index 0)
             * shift it by 8 add the low byte (index 1) and shift all by 16
             */
            newLinearBaseAddress = ((long) (binData[0] << 8) + binData[1]) << 16;
            // some HEX files have address records before each data record
            // => avoid not necessary segments
            if (newLinearBaseAddress != linearBaseAddress) {
              storeTempSegment(); // saves previous temp segment
              segmentBaseAddress = 0;
              linearBaseAddress = newLinearBaseAddress;

              hexSegmentStartAddress = linearBaseAddress + segmentBaseAddress;
            }

            break;
          }

          // HEXP-14 --changed the error flag from true to false
          case IntelHexRecord.START_LINEAR_ADDRESS_RECORD: {
            // specifies the excution start address
            bufferMessage("not handled record type " + hexRecord.getRecType(), false);
            break;
          }
          default: {
            // invalid record type
            bufferMessage("invalid record type " + hexRecord.getRecType(), true);
          }
        }
      }

      this.lineCount = this.inFile.getLineCount();
    }
    catch (Exception e) {
      if (e instanceof HexParserException) {
        throw (HexParserException) e;
      }
      this.logger.error("Error while parsing the Intel Hex file: " + e.getMessage());
      throw new HexParserException("Error while parsing the Intel Hex file: " + e.getMessage(),
          HexParserException.HEX_FILE_PARSE_EXCEPTION);
    }
    finally {
      this.inFile.closeFile();
    }

    this.analyzeTime = (new java.util.Date()).getTime() - startTime.getTime();
    this.logger.info(this.className + HexParserConstants.COLON_IDENTIFIER + " Parsing of HEX file is ended");
    this.logger.info(this.className + HexParserConstants.COLON_IDENTIFIER +
        " Total time taken to parse the hex file in milli seconds: " + this.analyzeTime);

    return this.hexMemory;
  }

  /**
   * Parses the Hex Record of "Data Record" type.
   *
   * @param hexSegmentStartAddress the start address of the hex segment
   * @param binData the binary data
   * @param hexRecord HexRecord
   */
  private void parseDataRecordType(final long hexSegmentStartAddress, final short[] binData,
      final IntelHexRecord hexRecord) {
    if (this.tempSegmentSize == 0) {
      // new segment
      this.tempSegmentOffset = hexRecord.getOffset();
      this.tempSegmentStartAddress = hexSegmentStartAddress + this.tempSegmentOffset;
    }

    // check if there is a gap in the hex data
    if (hexRecord.getOffset() > (this.tempSegmentSize + this.tempSegmentOffset)) {
      storeTempSegment();
      this.tempSegmentOffset = hexRecord.getOffset();
      this.tempSegmentStartAddress = hexSegmentStartAddress + this.tempSegmentOffset;
    }

    for (int i = 0; i < hexRecord.getRecordLength(); i++) {
      // if record exceeds segment, create a new segment
      if (this.tempSegmentSize == HexParserConstants.MAXIMUM_SEGMENT_SIZE) {
        long oldTempSegmentStartAddress = this.tempSegmentStartAddress;
        storeTempSegment();
        this.tempSegmentOffset = HexParserConstants.MAXIMUM_SEGMENT_SIZE;
        this.tempSegmentStartAddress = oldTempSegmentStartAddress + this.tempSegmentOffset;
      }

      // check overlapping data
      if (((hexRecord.getOffset() + i) - this.tempSegmentOffset) < this.tempSegmentSize) {
        if (this.tempSegmentData[(hexRecord.getOffset() + i) - this.tempSegmentOffset] != binData[i]) {
          this.logger.error(this.className + HexParserConstants.COLON_IDENTIFIER +
              " Overlapping data with different values in segment " + hexSegmentStartAddress + " at offset " +
              hexRecord.getOffset());
          // overlapping with different data
          throw new HexParserException("Overlapping data with different values in segment " + hexSegmentStartAddress +
              " at offset " + hexRecord.getOffset(), HexParserException.DIFFERENT_OVERLAPPING_DATA);
        }
      }
      else {
        // no overlapping
        // get data from HEX file into tempSegment
        this.tempSegmentData[(hexRecord.getOffset() + i) - this.tempSegmentOffset] = binData[i];
        this.tempSegmentSize++;
      }
    }
  }

  /**
   * Get the number of parse errors
   *
   * @return the number of errors
   */
  public int getNoOfErrors() {
    return this.noOfErrors;
  }

  /**
   * Get the error message buffer
   *
   * @return the message buffer
   */
  public StringBuilder getMessageBuffer() {
    return this.messageBuffer;
  }

  /**
   * Get the number of lines in the HEX file Returns -1, if the file has not been parsed.
   *
   * @return the number od lines
   */
  public int getLineCount() {
    return this.lineCount;
  }

  /**
   * Get the time used to parse the HEX file. Returns -1 if the has not been parsed.
   *
   * @return the number of milli seconds used to parse the file
   */
  public long getAnalyzeTime() {
    return this.analyzeTime;
  }

  /**
   * Get the contents of the HEX file Returns null if the file has not been parsed
   *
   * @return the HexMemory
   */
  public HexMemory getHexMemory() {
    return this.hexMemory;
  }


  /**
   * Gets the HexParserDataHandler to access the hex parser data. It acts like an interface to other components.
   *
   * @return HexParserDataHandler
   */

  // HEXP-1
  public HexParserDataHandler getHexParserDataHandler() {
    return new HexParserDataHandler(getHexMemory());

  }


  // HEXP-6
  /**
   * This method is responsible to return the warnings Map
   */
  @Override
  public Map<String, List<String>> getWarningsMap() {
    return this.warningsMap;
  }

  /**
   * @return the inputStream
   */
  public InputStream getInputStream() {
    return this.inputStream;
  }

  // ALM-280807
  /**
   * @param inputStream the inputStream to set
   */
  public void setInputStream(final InputStream inputStream) {
    if (inputStream != null) {

      this.inputStream = inputStream;
    }
    else {

      throw new HexParserException("Errors found when parsing the Hex file.");
    }
  }
}
