/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.labfunwriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.calcomp.labfunwriter.exception.LabFunWriterException;

/**
 * Creates LAB/FUN file with the given input labels, functions, groups
 *
 * @author dja7cob
 */
public class LabFunWriter {

  /**
   * Logger
   */
  private final ILoggerAdapter logger;

  /**
   * Set of labels
   */
  private SortedSet<String> labels = new TreeSet<>();

  /**
   * Set of functions
   */
  private SortedSet<String> functions = new TreeSet<>();

  /**
   * Set of Groups
   */
  private SortedSet<String> groups = new TreeSet<>();

  /**
   * Directory path where the LAb/FUN file should be written
   */
  private String outputDir = LabFunWriterConstants.DEFAULT_OUTPUT_DIR;

  /**
   * File name for the LAB/FUN file
   */
  private String outputFileName = LabFunWriterConstants.DEFAULT_FILE_NAME;

  /**
   * File Type : LAB/FUN
   */
  private OUTPUT_FILE_TYPE fileType = OUTPUT_FILE_TYPE.LAB;

  /**
   * LAB/FUN file path based on the directory path, file name and file type
   */
  private String outputFilePath;

  /**
   * @param logger ILoggerAdapter instance
   */
  public LabFunWriter(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * Writes to LAB/FUN file
   *
   * @return Output file path
   * @throws LabFunWriterException Exception in writing LAB/FUN file
   */
  public String writeToFile() throws LabFunWriterException {
    validateInputs();
    createOutputFile();
    writeOutputFile();

    this.logger.info("{} file created successfully. Labels = {}, Functions = {}, Groups = {}, File path : {}",
        this.fileType.getFileType(), this.labels.size(), this.functions.size(), this.groups.size(),
        this.outputFilePath);

    return this.outputFilePath;
  }

  /**
   * Validates the inputs (output directory, output file name, fle type, input labels/functions/groups)
   *
   * @throws LabFunWriterException
   */
  private void validateInputs() throws LabFunWriterException {
    validateOutputDir();
    validateOutputFileName();
    validateFileType();
    validateLabelsFuncsGroups();
  }

  /**
   * Validates whether input labels, functions, groups are available
   *
   * @throws LabFunWriterException
   */
  private void validateLabelsFuncsGroups() throws LabFunWriterException {
    if (!(!this.labels.isEmpty() || !this.functions.isEmpty() || !this.groups.isEmpty())) {
      this.logger.error("No data to write");
      throw new LabFunWriterException("No labels/functions/groups to write in the file");
    }
  }

  /**
   * Writes the LAB/FUN file in the given filepath
   */
  private void writeOutputFile() throws LabFunWriterException {
    try (FileWriter writer = new FileWriter(this.outputFilePath)) {
      doWriteOutputFile(writer);
    }
    catch (IOException e) {
      this.logger.error("Error while writing to output file - " + e.getMessage());
      throw new LabFunWriterException("Exception in writing output file. " + e.getMessage(), e);
    }
  }

  /**
   * Writes the labels/ functions/groups to the file
   *
   * @param writer File writer instance
   */
  private void doWriteOutputFile(final FileWriter writer) throws IOException {
    this.logger.debug("Writing data to file...");

    doWrite(writer, LabFunWriterConstants.LABEL_TAG, this.labels);
    doWrite(writer, LabFunWriterConstants.FUNCTION_TAG, this.functions);
    doWrite(writer, LabFunWriterConstants.GROUP_TAG, this.groups);

    this.logger.debug("LAB/FUN file writing completed");
  }

  /**
   * Write the labels/functions/groups with the corresponding header tags
   *
   * @param writer File writer instance
   * @throws IOException Exception in writing to the LAB/FUN file
   */
  private void doWrite(final FileWriter writer, final String headerTag, final Set<String> inpList) throws IOException {
    if (!inpList.isEmpty()) {
      writer.write(headerTag);
      for (String input : inpList) {
        writeEmptyLines(writer, 1);
        writer.write(input);
      }
      writeEmptyLines(writer, 2);
    }
  }

  /**
   * Writes empty lines in the file
   *
   * @param writer
   * @param numofLines
   * @throws IOException
   */
  private void writeEmptyLines(final FileWriter writer, final int numofLines) throws IOException {
    for (int i = 0; i < numofLines; i++) {
      writer.write(System.lineSeparator());
    }
  }

  /**
   * Creates the output file (LAB/FUN) if not available already
   *
   * @throws LabFunWriterException Exception in creating output file
   */
  private void createOutputFile() throws LabFunWriterException {
    buildOutputFilePath();

    this.logger.debug("Creating output file...");
    try {
      boolean newFile = new File(this.outputFilePath).createNewFile();

      String msg = newFile ? "Output file created" : "File already exists. Overwriting...";
      this.logger.debug(msg);
    }
    catch (IOException e) {
      this.logger.error("Error while creating output file. " + e.getMessage());
      throw new LabFunWriterException("Exception in creating output file. " + e.getMessage(), e);
    }
  }

  /**
   * Builds the output file path based on the directory path, file name and extension
   */
  private void buildOutputFilePath() {
    this.logger.debug("Building output file path...");

    StringBuilder outputPath = new StringBuilder(this.outputDir);
    if (!this.outputDir.endsWith(File.separator)) {
      outputPath.append(File.separator);
    }
    outputPath.append(this.outputFileName);
    if (!this.outputFileName.endsWith("." + this.fileType.getFileExtension())) {
      outputPath.append('.').append(this.fileType.getFileExtension());
    }
    this.outputFilePath = outputPath.toString();

    this.logger.debug("Output File path : {}", this.outputFilePath);
  }

  /**
   * Validates that file type should not be null
   *
   * @throws LabFunWriterException Exception when the file type is empty
   */
  private void validateFileType() throws LabFunWriterException {
    this.logger.debug("Validating File Type...");

    if (null == this.fileType) {
      this.logger.error("File type is null");
      throw new LabFunWriterException("File Type cannot be empty");
    }

    this.logger.debug("File Type is valid : {}", this.fileType);
  }

  /**
   * Validates that the output file name should not be null or empty
   *
   * @throws LabFunWriterException Exception when the output file name is empty
   */
  private void validateOutputFileName() throws LabFunWriterException {
    this.logger.debug("Validating Output File Name...");

    if (isEmpty(this.outputFileName)) {
      this.logger.error("Output file name is null or empty");
      throw new LabFunWriterException("Output File Name cannot be empty");
    }

    this.logger.debug("Output File Name is valid : {}", this.outputFileName);
  }

  /**
   * Checks whether a string is null or empty
   *
   * @return boolean
   */
  private boolean isEmpty(final String text) {
    return (null == text) || text.isEmpty();
  }

  /**
   * Validates and sets the output directory path. Else set the default directory
   *
   * @throws LabFunWriterException
   */
  private void validateOutputDir() throws LabFunWriterException {
    this.logger.debug("Validating Output Directory ...");

    if (isEmpty(this.outputDir)) {
      this.logger.error("Output Directory is null or empty");
      throw new LabFunWriterException("Output Directory cannot be empty");
    }
    if (!new File(this.outputDir).exists()) {
      this.logger.error("Output Directory invalid - " + this.outputDir);
      throw new LabFunWriterException("Output Directory does not exist : " + this.outputDir);
    }

    this.logger.debug("Output Directory is valid : {}", this.outputDir);
  }

  /**
   * @param labels the labels to set
   */
  public void setLabels(final Set<String> labels) {
    this.labels = new TreeSet<>(labels);
  }

  /**
   * @param functions the functions to set
   */
  public void setFunctions(final Set<String> functions) {
    this.functions = new TreeSet<>(functions);
  }

  /**
   * @param groups the groups to set
   */
  public void setGroups(final Set<String> groups) {
    this.groups = new TreeSet<>(groups);
  }

  /**
   * @param outputDir the outputDir to set
   */
  public void setOutputDir(final String outputDir) {
    this.outputDir = outputDir;
  }

  /**
   * @param outputFileName the outputFileName to set
   */
  public void setOutputFileName(final String outputFileName) {
    this.outputFileName = outputFileName;
  }

  /**
   * @param fileType the fileType to set
   */
  public void setFileType(final OUTPUT_FILE_TYPE fileType) {
    this.fileType = fileType;
  }
}
