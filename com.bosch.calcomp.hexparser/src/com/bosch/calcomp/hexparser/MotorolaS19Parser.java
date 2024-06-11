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
import com.bosch.calcomp.hexparser.process.MotorolaS19FileRecord;
import com.bosch.calcomp.hexparser.util.HexParserConstants;
import com.bosch.calcomp.hexparser.util.HexParserUtil;
import com.bosch.calcomp.hexparser.util.MotorolaS19ParserUtil;


/**
 * @author dec1kor
 *
 *         <pre>
 * Version         Date                        Modified by                        Changes
 * ----------------------------------------------------------------------------
 * 0.1                15-Jul-09                Deepa                                SPARSER-2: First draft.<br>
 * 0.2                17-Jul-09                Deepa                                SPARSER-2: Implemented parse().<br>
 * 0.3                17-Jul-09                Jagadeesh                        SPARSER-2: Modified parse().<br>
 * 0.4                20-Jul-09                Deepa                                SPARSER-2: Modified parseDataRecords().<br>
 * 0.5                21-Jul-09                Jagadeesh                        SPARSER-2: Modified parse().<br>
 * 0.6                24-Jul-09                Jagadeesh                         SPARSER-2: Modified parse().<br>
 *         </pre>
 */

/**
 * Parses the S19 file, with the file extension ".s19" and stores the parsed data in HexMemory. HexParserDataHandler can
 * be used to access the hex parser's data.
 * <P>
 * To invoke the parser,
 * <p>
 * HexParserFactory parserFactory = new HexParserFactory();<br>
 * IHexParser parser = parserFactory.createHexParser("c:\\temp\\testS19File.s19");<br>
 * parser.parse();<br>
 * <br>
 * <p>
 *
 * @author dec1kor
 */
public class MotorolaS19Parser implements IHexParser, MessageBuffer {

  /**
   * class name
   */
  private static final String CLASSNAME = "com.bosch.calcomp.hexparser.MotorolaS19Parser";

  /**
   * CalFileReader instance.
   */
  private CalFileReader inFile = null;

  /**
   * S19 file name.
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
   * HexMemory instance.
   */
  private HexMemory hexMemory;
  private HexMemoryHandler hexMemoryHandler;

  /**
   * Holds all the messages.
   */
  private final StringBuilder messageBuffer;

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
   * Temporary Segment Data
   */
  private short[] tempSegmentData;

  // HEXP-6
  /**
   * HashMap for warnings encountered during the parsing of A2L file
   */
  private final Map<String, List<String>> warningsMap;

  private final ILoggerAdapter logger;

  /**
   * MotorolaS19ParserUtil instance to support Multi Threading
   */
  private final MotorolaS19ParserUtil motorolaS19ParserUtil = new MotorolaS19ParserUtil();

  /**
   * HexParserUtil instance to support Multi Threading
   */
  private final HexParserUtil hexParserUtil = new HexParserUtil();


  /**
   * Create a new HexParser instance and initialize the HEX file name
   *
   * @param fileName the HEX file name
   * @param logger
   */
  public MotorolaS19Parser(final String fileName, final ILoggerAdapter logger) {
    messageBuffer=new StringBuilder();
    this.logger = logger;
    this.fileName = fileName;
    this.warningsMap= new HashMap<>();
    initVars();
    initTempSegment();
    setFileName(fileName);

  }

  // ALM-280807
  /**
   * @param inputStream
   */
  public MotorolaS19Parser(final InputStream inputStream, final ILoggerAdapter logger) {
    messageBuffer=new StringBuilder();
    this.warningsMap= new HashMap<>();
    this.logger = logger;
    this.inputStream = inputStream;
    initVars();
    initTempSegment();
    setInputStream(inputStream);
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
   * Parse the Motorola S19 file.
   */
  @Override
  public HexMemory parse() {

    // ALM-280807
    if (this.fileName != null) {
      setFileName(this.fileName);
    }
    boolean isTerminationRecordPresent = false;
    int noOfDataRecords = 0;

    this.logger.info(
        CLASSNAME + HexParserConstants.COLON_IDENTIFIER + " Parsing of Motorola S19 file started: " + getFileName());

    short[] binData;
    MotorolaS19FileRecord hexRecord;

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
          case MotorolaS19FileRecord.S0_HEADER_RECORD:
            this.motorolaS19ParserUtil.setMotorolaS19Header(hexRecord.getBinData());
            break;
          case MotorolaS19FileRecord.S1_2BYTE_ADDRESS_RECORD:
          case MotorolaS19FileRecord.S2_3BYTE_ADDRESS_RECORD:
          case MotorolaS19FileRecord.S3_4BYTE_ADDRESS_RECORD:
            noOfDataRecords++;
            this.motorolaS19ParserUtil.setMotorolaS19DataRecType(hexRecord.getRecType());
            parseDataRecords(binData, hexRecord);
            break;
          case MotorolaS19FileRecord.S5_COUNT_RECORD:
            this.motorolaS19ParserUtil.validateCountOfDataS5Records(hexRecord, noOfDataRecords, this.logger);
            noOfDataRecords = 0;
            break;
          case MotorolaS19FileRecord.S6_COUNT_RECORD:
            this.motorolaS19ParserUtil.validateCountOfDataS6Records(hexRecord, noOfDataRecords, this.logger);
            noOfDataRecords = 0;
            break;
          case MotorolaS19FileRecord.S7_TERMINATION_RECORD:
          case MotorolaS19FileRecord.S8_TERMINATION_RECORD:
          case MotorolaS19FileRecord.S9_TERMINATION_RECORD:
            isTerminationRecordPresent = true;
            this.motorolaS19ParserUtil.setMotorolaS19TerminationInfo(hexRecord.getRecType(), hexRecord.getBinData());
            storeTempSegment();

            break;
          default:
            bufferMessage("invalid record type: S" + hexRecord.getRecType(), true);
            break;
        }
      }

      // validates the termination record for the data type record.
      this.motorolaS19ParserUtil.validateEndOfFile(isTerminationRecordPresent, this.logger);

      // when the termination record is not found throw an exception
      if (!isTerminationRecordPresent) {
        this.logger.error(CLASSNAME + HexParserConstants.COLON_IDENTIFIER +
            " Exception while parsing the S19 file. Termination Record not found ");
        throw new HexParserException("Exception while parsing the S19 file. Termination Record not found  ",
            HexParserException.HEX_FILE_PARSE_EXCEPTION);
      }

      this.lineCount = this.inFile.getLineCount();

    }
    catch (HexParserException hexException) {
      throw hexException;
    }
    catch (Exception e) {
      this.logger.error("Error while parsing the Motorola S19 file: " + e.getMessage());
      throw new HexParserException("Error while parsing the Motorola S19 file: " + e.getMessage(),
          HexParserException.HEX_FILE_PARSE_EXCEPTION);
    }
    finally {
      this.inFile.closeFile();
    }

    this.analyzeTime = (new java.util.Date()).getTime() - startTime.getTime();
    this.logger.info(CLASSNAME + HexParserConstants.COLON_IDENTIFIER + " Parsing of Motorola S19 file is ended");
    this.logger.info(CLASSNAME + HexParserConstants.COLON_IDENTIFIER +
        " Total time taken to parse the Motorola S19 file in milli seconds: " + this.analyzeTime);

    return this.hexMemory;
  }

  /**
   * Parses the data records of type S1, S2, S3.
   *
   * @param binData binary data
   * @param hexRecord MotorolaS19FileRecord
   */
  private void parseDataRecords(final short[] binData, final MotorolaS19FileRecord hexRecord) {
    try {
      // 435061
      if (hexRecord.getAddress() < this.tempSegmentStartAddress) {
        storeTempSegment();
        this.tempSegmentStartAddress = hexRecord.getAddress();
        this.tempSegmentSize = 0;
      }

      if (this.tempSegmentSize == 0) {
        this.tempSegmentStartAddress = hexRecord.getAddress();
      }

      // check if there is a gap in the hex data
      if (hexRecord.getAddress() > (this.tempSegmentSize + this.tempSegmentStartAddress)) {
        storeTempSegment();
        this.tempSegmentStartAddress = hexRecord.getAddress();
      }

      for (int i = 0; i < binData.length; i++) {
        // if record exceeds segment, create a new segment
        if (this.tempSegmentSize == HexParserConstants.MAXIMUM_SEGMENT_SIZE) {
          storeTempSegment();
          // current address to be updated
          this.tempSegmentStartAddress = hexRecord.getAddress() + i;
        }
        int hexRecordAddress = (int) (hexRecord.getAddress() + i);
        int size = 0;

        // check overlapping data
        size = (int) (hexRecordAddress - this.tempSegmentStartAddress);

        if (size < this.tempSegmentSize) {
          if (this.tempSegmentData[size] != binData[i]) {
            this.logger.error(CLASSNAME + HexParserConstants.COLON_IDENTIFIER +
                " Overlapping data with different values in segment " + " at address " + hexRecord.getAddress());
            // overlapping with different data
            throw new HexParserException(
                "Overlapping data with different values in segment " + " at address " + hexRecord.getAddress(),
                HexParserException.DIFFERENT_OVERLAPPING_DATA);
          }
        }
        else {
          // no overlapping
          // get data from HEX file into tempSegment
          this.tempSegmentData[size] = binData[i];
          this.tempSegmentSize++;
        }
      }
    }
    catch (Exception e) {

      this.logger.error(CLASSNAME + HexParserConstants.COLON_IDENTIFIER +
          " Exception while parsing the S19 file at address " + hexRecord.getAddress() + e.getLocalizedMessage());
      throw new HexParserException("Exception while parsing the S19 file at address " + hexRecord.getAddress(),
          HexParserException.HEX_FILE_PARSE_EXCEPTION);
    }
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
   * Create a new temporary segment
   */
  private void initTempSegment() {
    this.tempSegmentSize = 0;
    this.tempSegmentStartAddress = -1;
    this.tempSegmentData = new short[HexParserConstants.MAXIMUM_SEGMENT_SIZE];
  }

  /**
   * Get the next record from the HEX file.
   * <p>
   * The next line is read from the Hex file. It is converted to a Hex Record and returned.
   * <p>
   *
   * @return the next HEX record
   */
  private MotorolaS19FileRecord getNextHexRecord() {
    boolean hexRecordFound = false;
    String currentLine = "";
    MotorolaS19FileRecord newHexRecord = null;

    while (!hexRecordFound && (this.inFile != null) && !this.inFile.isEndOfFile()) {
      currentLine = this.inFile.getLine();
      if (currentLine != null) {
        currentLine = currentLine.trim();
      }

      if ((!this.inFile.isEndOfFile()) && ((currentLine != null) &&
          (currentLine.charAt(0) == HexParserConstants.MOTOROLA_S19_FILE_RECORD_IDENTIFIER.charAt(0)))) {
        newHexRecord = new MotorolaS19FileRecord(currentLine, this.logger, this.hexParserUtil, inFile.getLineCount());
        hexRecordFound = true;
      }
    }

    if (hexRecordFound) {
      return newHexRecord;
    }
    return null;
  }

  /**
   * Get the time used to parse the HEX file. Returns -1 if the has not been parsed.
   *
   * @return the number of milli seconds used to parse the file
   */
  @Override
  public long getAnalyzeTime() {
    return this.analyzeTime;
  }

  /**
   * Get the current S19 file name
   *
   * @return the S19 file name
   */
  @Override
  public String getFileName() {
    return this.fileName;
  }

  // ALM-280807
  /**
   * @param fileName the fileName to set
   */
  private void setFileName(final String fileName) {
    if (this.fileName != null) {
      try {
        this.inputStream = new FileInputStream(fileName);

      }
      catch (FileNotFoundException e) {
        this.logger.error(CLASSNAME + HexParserConstants.COLON_IDENTIFIER + "Errors found when parsing the Hex file." +
            e.getLocalizedMessage());
        throw new HexParserException("Errors found when parsing the Hex file.");
      }
    }
  }

  /**
   * Get the number of lines in the HEX file Returns -1, if the file has not been parsed.
   *
   * @return the number odd lines
   */
  @Override
  public int getLineCount() {
    return this.lineCount;
  }

  /**
   * Get the error message buffer
   *
   * @return the message buffer
   */
  @Override
  public StringBuilder getMessageBuffer() {
    return this.messageBuffer;
  }

  /**
   * Get the number of parse errors
   *
   * @return the number of errors
   */
  @Override
  public int getNoOfErrors() {
    return this.noOfErrors;
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
  @Override
  public HexParserDataHandler getHexParserDataHandler() {
    return new HexParserDataHandler(getHexMemory());

  }

  /**
   * Buffer the messages.
   *
   * @param newMessage message to be logged
   * @param isError indicates whether it is an error message. true, if it is an error message, false otherwise.
   */
  public void bufferMessage(final String newMessage, final boolean isError) {
    if (isError) {
      this.noOfErrors++;
    }

    if (this.messageBuffer != null) {
      this.messageBuffer
          .append("line " + this.inFile.getLineCount() + " : " + newMessage + HexParserConstants.NEW_LINE);
    }
  }

  // HEXP-6
  /**
   * This method is responsible to return the warnings Map
   */
  @Override
  public Map<String, List<String>> getWarningsMap() {
    return this.warningsMap;
  }

  // ALM-280807
  /**
   * @return the inputStream
   */
  public InputStream getInputStream() {
    return this.inputStream;
  }

  /**
   * @param inputStream the inputStream to set
   */
  public void setInputStream(final InputStream inputStream) {
    this.inputStream = inputStream;
  }

}
