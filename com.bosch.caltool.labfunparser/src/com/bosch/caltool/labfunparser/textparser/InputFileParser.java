/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.labfunparser.textparser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.mozilla.universalchardet.UnicodeBOMInputStream;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.FileParserConstants.INPUT_FILE_TYPE;

/**
 * This is a parser for text input files like .LAB and .FUN files
 *
 * @author adn1cob
 */
// iCDM-711
public class InputFileParser {

  private static final Charset[] FILE_CHARSETS = { StandardCharsets.UTF_8, StandardCharsets.UTF_16LE };

  /**
   * Input File path
   */
  private final InputStream inputStream;

  /**
   * Holds the current line
   */
  private String currentLine;

  private final String filePath;


  /**
   * Handles list of labels in the file
   */
  private final List<String> labelList = new ArrayList<>();

  /**
   * Handles list of functions in the file
   */
  private final List<String> functionList = new ArrayList<>();

  /**
   * Handles list of Groups in the file
   */
  private final List<String> groupList = new ArrayList<>();
  
  /**
   * Defines file type
   */
  private INPUT_FILE_TYPE fileType;

  private final ILoggerAdapter logger;

  /**
   * Constructor
   *
   * @param logger Logger
   * @param inputStream as Input
   * @param fileName LAB, FUN, GROUP etc..
   */
  public InputFileParser(final ILoggerAdapter logger, final InputStream inputStream, final String fileName) {
    this.logger = logger;
    this.inputStream = inputStream;
    this.filePath = fileName;
  }

  /**
   * Constructor
   *
   * @param logger Logger
   * @param filePath path of the file to read
   */
  public InputFileParser(final ILoggerAdapter logger, final String filePath) {
    this.logger = logger;
    this.inputStream = null;
    this.filePath = filePath;
  }

  /**
   * Method reads file
   *
   * @throws ParserException
   */
  private void readFile(final BufferedReader brReader) throws ParserException {
    // read first line
    getNextLine(brReader);
    // return if this is EndOfFile
    if (isEOF()) {
      return;
    }
    // start reading blocks
    readBlocks(brReader);
  }

  /**
   * Method reads BLOCKS ( [Function] , [Label]...)
   *
   * @throws ParserException
   */
  private void readBlocks(final BufferedReader brReader) throws ParserException {
    do {
      if (isEOF()) {
        return;
      }
      /* [Function] block */
      // iCDM-1141 - case insensitive
      if (FileParserConstants.FUNCTION_BLOCK.equalsIgnoreCase(getCurrentLine().trim())) {
        readBlocks(brReader, FileParserConstants.FUNCTION_BLOCK);
      }
      /* [Label] block */
      else if (FileParserConstants.LABEL_BLOCK.equalsIgnoreCase(getCurrentLine().trim())) {
        readBlocks(brReader, FileParserConstants.LABEL_BLOCK);
      }
      /* [GROUP] block  ALM-442313*/
      else if (FileParserConstants.GROUP_BLOCK.equalsIgnoreCase(getCurrentLine().trim())) {
        readBlocks(brReader, FileParserConstants.GROUP_BLOCK);
      }
      else if (!isEOF() && !isValidBlock()) {
        getNextLine(brReader);
      }
    }
    while (!isEOF()); // until end of file
  }


  /**
   * ALM-442313
   * Method reads [Label] / [FUNCTION] / [GROUP] blocks
   *
   * @throws ParserException
   */
  private void readBlocks(final BufferedReader brReader, String blockType) throws ParserException {
    boolean endBlock = false;
    do {

      if (isEOF()) {
        return;
      }
      final String strLine = getNextLine(brReader);
      if ((strLine != null) && isBlockString()) {
        endBlock = true;
      }
      // ICDM-2573
      else if ((strLine != null) && !"".equals(strLine) && !FileParserConstants.EMPTY_LINE.equals(strLine) &&
          !isCommentedLine()) {
        switch (blockType) {
          case FileParserConstants.FUNCTION_BLOCK:
            this.functionList.add(strLine);
            break;
          case FileParserConstants.LABEL_BLOCK:
            this.labelList.add(strLine);
            break;
          case FileParserConstants.GROUP_BLOCK:
            this.groupList.add(strLine);
            break;
          default:
            break;
        }
      }
    }
    while (!endBlock);
  }

  /**
   * Checks if the current line is Block statement
   *
   * @return true if [block] statement
   */
  private boolean isBlockString() {
    return getCurrentLine().startsWith(FileParserConstants.START_BLOCK_OPEN) &&
        getCurrentLine().endsWith(FileParserConstants.START_BLOCK_CLOSE);
  }

  /**
   * Checks if the block is valid. Currently [Function] and [Label] are valid
   *
   * @return true if valid block
   */
  private boolean isValidBlock() {
    // iCDM-1141
    return FileParserConstants.FUNCTION_BLOCK.equalsIgnoreCase(getCurrentLine().trim()) ||
        FileParserConstants.LABEL_BLOCK.equalsIgnoreCase(getCurrentLine().trim());
  }


  /**
   * @return list of functions
   */
  public List<String> getFunctions() {
    return new ArrayList<>(this.functionList);
  }

  /**
   * @return list of labels
   */
  public List<String> getLabels() {
    return new ArrayList<>(this.labelList);
  }

  /**
   * @param fileName
   */
  private void findFileType() {
    this.fileType = this.filePath.toUpperCase().endsWith(".FUN") ? INPUT_FILE_TYPE.FUN : INPUT_FILE_TYPE.LAB;
    this.logger.info("Input file type is : {}", this.fileType.getFileType());
  }


  /**
   * Parse the Lab/Fun File
   *
   * @throws ParserException exception
   */
  public void parse() throws ParserException {

    this.logger.debug("Parsing input started");

    if (this.inputStream == null) {
      if ((this.filePath == null) || "".equals(this.filePath.trim())) {
        this.logger.error("Missing file path");
        throw new ParserException("File path is mandatory");
      }
    }
    else {
      if ((this.filePath == null) || "".equals(this.filePath.trim())) {
        this.logger.error("Missing file name for stream input");
        throw new ParserException("File name/path is mandatory for stream input");
      }
    }

    validateFileExtn();

    findFileType();
    try {
      if (this.inputStream == null) {
        // Allowed file extensions
        parseLabFunUsingFilePath();
      }
      else {
        parseLabFunUsingStream();
      }
    }
    catch (IOException e) {
      this.logger.error("Error reading file : " + this.filePath + ". " + e.getMessage(), e);
      throw new ParserException("Error reading file : " + this.filePath, e);
    }

    this.logger.info("Parsing input completed. Labels = {}, Functions = {}, groups = {}", this.labelList.size(),
        this.functionList.size(), this.groupList.size());
  }

  /**
   * @throws IOException
   * @throws ParserException
   */
  private void parseLabFunUsingStream() throws IOException, ParserException {
    byte[] byteArray = getByteArray(this.inputStream);
    try (InputStream inputStreamForUnicode = new ByteArrayInputStream(byteArray);) {
      Charset charSetIdetifier = new CharSetIdentifier().charSetIdetifier(byteArray, FILE_CHARSETS);
      if (charSetIdetifier != null) {
        parseFromStream(inputStreamForUnicode, charSetIdetifier);
      }
    }
  }

  /**
   * @param inputStreamForUnicode
   * @param charSetIdetifier
   * @throws IOException
   * @throws ParserException
   */
  private void parseFromStream(final InputStream inputStreamForUnicode, final Charset charSetIdetifier)
      throws IOException, ParserException {
    try (UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStreamForUnicode);
        InputStreamReader inputStreamReader = new InputStreamReader(ubis, charSetIdetifier);
        BufferedReader brReader = new BufferedReader(inputStreamReader);) {
      ubis.skipBOM();
      // read the file
      readFile(brReader);
    }
  }

  /**
   * @throws IOException
   * @throws ParserException
   * @throws FileNotFoundException
   */
  private void parseLabFunUsingFilePath() throws IOException, ParserException {
    try (FileInputStream fileStream = new FileInputStream(this.filePath);) {
      byte[] fileDataArr = getByteArray(fileStream);
      Charset charSetIdetifier = new CharSetIdentifier().charSetIdetifier(fileDataArr, FILE_CHARSETS);
      if (charSetIdetifier != null) {
        parseFromFilePath(fileDataArr, charSetIdetifier);
      }
    }
  }

  /**
   * @param fileDataArr
   * @param charSetIdetifier
   * @throws IOException
   * @throws ParserException
   */
  private void parseFromFilePath(final byte[] fileDataArr, final Charset charSetIdetifier)
      throws IOException, ParserException {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileDataArr);
        UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(byteArrayInputStream);
        InputStreamReader inputStreamReader = new InputStreamReader(ubis, charSetIdetifier);
        BufferedReader brReader = new BufferedReader(inputStreamReader);) {
      ubis.skipBOM();
      // read the file
      readFile(brReader);
    }
  }


  /**
   * Validates allowed input file extensions
   *
   * @throws ParserException
   */
  private void validateFileExtn() throws ParserException {

    String fileNameUpper = this.filePath.toUpperCase().trim();
    // Allowed file extensions
    if (!(fileNameUpper.endsWith(FileParserConstants.LAB_EXTN_UPPER) ||
        fileNameUpper.endsWith(FileParserConstants.FUN_EXTN_UPPER))) {
      this.logger.error("Input file type not supported. file : " + this.filePath);
      throw new ParserException("Input file type not supported. Please check the file extension : " + this.filePath);
    }
  }

  private byte[] getByteArray(final InputStream fileInputStream) throws IOException {
    byte[] byteArray;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
      byte[] buffer = new byte[1024];
      int read = 0;
      while ((read = fileInputStream.read(buffer, 0, buffer.length)) != -1) {
        baos.write(buffer, 0, read);
      }
      baos.flush();
      byteArray = baos.toByteArray();
    }
    return byteArray;
  }

  /**
   * Reads one line
   *
   * @param brReader for parsing
   * @return line from the file
   * @throws ParserException exception
   */
  private String getNextLine(final BufferedReader brReader) throws ParserException {
    try {
      this.currentLine = brReader.readLine();
    }
    catch (IOException e) {
      throw new ParserException("Error reading line after '" + this.currentLine + "' in file :" + this.filePath, e);
    }
    //ALM-432212
    if(this.currentLine!=null) {
      excludeCommentFromLine();
    }
    return this.currentLine;
  }

  /**
   * Gets the current line, which is read
   *
   * @return current line
   */
  private String getCurrentLine() {
    return this.currentLine;
  }

  /**
   * Checks if end of file
   *
   * @return true if end of file is reached
   */
  private boolean isEOF() {
    return (this.currentLine == null) ? true : false;
  }

  /**
   * Checks if a line
   *
   * @return true if the current line is commented
   */
  // ICDM-2573
  private boolean isCommentedLine() {
    return (this.currentLine.startsWith(FileParserConstants.COMMENT_KEYWORD)) ? true : false;
  }

  /**
   * Check if it is not a commented line and then ignore the content along with ; on a normal line
   * ALM-432212
   * @return current line
   */
  private String excludeCommentFromLine() {
    if(!isCommentedLine() && this.currentLine.contains(FileParserConstants.COMMENT_KEYWORD)) {
        //Omit the ; and its trailing comments
        String currLine = this.currentLine.substring(0, this.currentLine.indexOf(FileParserConstants.COMMENT_KEYWORD));
        this.currentLine = currLine;
    }
    return this.currentLine;
  }
  
  /**
   * @return the fileType
   */
  public INPUT_FILE_TYPE getFileType() {
    return this.fileType;
  }

  /**
   * @return the groupList
   */
  public List<String> getGroups() {
    return new ArrayList<>(this.groupList);
  }


}
