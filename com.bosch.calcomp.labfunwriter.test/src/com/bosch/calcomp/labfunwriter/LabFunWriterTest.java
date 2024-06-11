/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.labfunwriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;
import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.calcomp.labfunwriter.exception.LabFunWriterException;

/**
 * JUnit Test class for LabFunWriter
 *
 * @author dja7cob
 */
public class LabFunWriterTest extends JUnitTest {

  /**
   * Assert message
   */
  private static final String MSG_FILE_CRE_CHK = "Check File created";

  /**
   * Custom directory to write LAB/FUN file
   */
  private static final String OUTPUT_DIR_CUSTOM = TEMP_DIR + "JUnitLabFunWriter";

  /**
   * Invalid directory path
   */
  private static final String OUTPUT_DIR_NOT_EXIST = "D:\\InvalidJUnit";

  /**
   * Custom folder name
   */
  private static final String OUTPUT_FILE_NAME_FUN = "CreatedByJUnitTest";

  /**
   * Input labels
   */
  private static final Set<String> LABEL_SET = new HashSet<>(Arrays.asList("DDRC_DurDeb.IgnClPs_numShCirGnd2DebDef_C",
      "VFZGGRDKH", "CDFO", "StrtCtl_nEngMaxRlyCtlOff_C", "KFZWWLNMOT", "DFC_CtlMsk.DFC_KPEmax_C"));

  /**
   * Input functions
   */
  private static final Set<String> FUNCTION_SET =
      new HashSet<>(Arrays.asList("I14229_AC", "Gpta", "AC_DataAcq", "APP2MED", "AEVABZK"));

  /**
   * Input groups
   */
  private static final Set<String> GROUP_SET = new HashSet<>(Arrays.asList("InjSyG", "IKCtl", "AirDvP"));

  /**
   * Writer for tests. Uses default output directory, file name, file type
   */
  // Note : instance is created for each test
  private final LabFunWriter labFunWriter = new LabFunWriter(AUT_LOGGER);

  /**
   * Test method to write file with default output directory, file name, file type
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileLabelsFunctionsGroups() throws LabFunWriterException {
    this.labFunWriter.setLabels(LABEL_SET);
    this.labFunWriter.setFunctions(FUNCTION_SET);
    this.labFunWriter.setGroups(GROUP_SET);

    String outputFilePath = this.labFunWriter.writeToFile();

    assertTrue(MSG_FILE_CRE_CHK, new File(outputFilePath).exists());
  }

  /**
   * Test method to write file with default output directory, file name, file type
   *
   * @throws LabFunWriterException Exception in writing LAB file
   * @throws IOException during file delete
   */
  @Test
  public void testWriteToFileFreshNOverwrite() throws LabFunWriterException, IOException {
    this.labFunWriter.setLabels(LABEL_SET);

    TESTER_LOGGER.info("Run once to reset state, and get file path");
    String outputFilePath = this.labFunWriter.writeToFile();

    File opFile = new File(outputFilePath);
    assertTrue(MSG_FILE_CRE_CHK, opFile.exists());

    TESTER_LOGGER.info("Delete output file created, for a fresh run");
    Files.delete(opFile.toPath());
    assertFalse("File does not exist", opFile.exists());

    TESTER_LOGGER.info("Run first time");
    outputFilePath = this.labFunWriter.writeToFile();
    assertTrue(MSG_FILE_CRE_CHK, opFile.exists());

    TESTER_LOGGER.info("Run second time");
    outputFilePath = this.labFunWriter.writeToFile();
    assertTrue(MSG_FILE_CRE_CHK, opFile.exists());
  }

  /**
   * Test method to write file with custom output directory, file name, file type
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileCustomOutputDir() throws LabFunWriterException {
    // Create and use custom directory
    new File(OUTPUT_DIR_CUSTOM).mkdir();
    this.labFunWriter.setOutputDir(OUTPUT_DIR_CUSTOM);

    this.labFunWriter.setOutputFileName(OUTPUT_FILE_NAME_FUN);
    this.labFunWriter.setFileType(OUTPUT_FILE_TYPE.FUN);
    this.labFunWriter.setLabels(LABEL_SET);

    String outputFilePath = this.labFunWriter.writeToFile();

    File outputFile = new File(outputFilePath);
    assertTrue(MSG_FILE_CRE_CHK, outputFile.exists());
    assertEquals("Output directory same as input", OUTPUT_DIR_CUSTOM, outputFile.getParent());
  }

  /**
   * Test method to write file to invalid directory
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileInvalidDirectory() throws LabFunWriterException {
    this.thrown.expectMessage("Output Directory does not exist");

    this.labFunWriter.setOutputDir(OUTPUT_DIR_NOT_EXIST);
    this.labFunWriter.setLabels(LABEL_SET);
    this.labFunWriter.writeToFile();
  }

  /**
   * Test method to write file with diresctory path set as empty
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileDirPathMissing() throws LabFunWriterException {
    this.thrown.expectMessage("Output Directory cannot be empty");

    this.labFunWriter.setOutputDir(null);
    this.labFunWriter.setLabels(LABEL_SET);
    this.labFunWriter.writeToFile();
  }

  /**
   * Test method to write file with file name empty
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileMissingFileName() throws LabFunWriterException {
    this.thrown.expectMessage("Output File Name cannot be empty");

    this.labFunWriter.setOutputFileName(null);
    this.labFunWriter.setLabels(LABEL_SET);
    this.labFunWriter.writeToFile();
  }

  /**
   * Test method to write file with file type empty
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileMissingFileType() throws LabFunWriterException {
    this.thrown.expectMessage("File Type cannot be empty");

    this.labFunWriter.setFileType(null);
    this.labFunWriter.setLabels(LABEL_SET);
    this.labFunWriter.writeToFile();
  }

  /**
   * Test method to write file without any labbels/functions/groups
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileMissingInputLabels() throws LabFunWriterException {
    this.thrown.expectMessage("No labels/functions/groups to write in the file");

    this.labFunWriter.writeToFile();
  }

  /**
   * Test method to write only labels to LAB file(default file type)
   *
   * @throws LabFunWriterException Exception in writing LAB file
   */
  @Test
  public void testWriteToFileLabels() throws LabFunWriterException {
    this.labFunWriter.setLabels(LABEL_SET);

    String outputFilePath = this.labFunWriter.writeToFile();

    assertTrue(MSG_FILE_CRE_CHK, new File(outputFilePath).exists());
  }

  /**
   * Test method to write only functions to FUN file
   *
   * @throws LabFunWriterException Exception in writing FUN file
   */
  @Test
  public void testWriteToFileFunctions() throws LabFunWriterException {
    this.labFunWriter.setFileType(OUTPUT_FILE_TYPE.FUN);
    this.labFunWriter.setFunctions(FUNCTION_SET);

    String outputFilePath = this.labFunWriter.writeToFile();

    assertTrue(MSG_FILE_CRE_CHK, new File(outputFilePath).exists());
  }

  /**
   * Test method to write only groups
   *
   * @throws LabFunWriterException Exception in writing FUN file
   */
  @Test
  public void testWriteToFileGroups() throws LabFunWriterException {
    this.labFunWriter.setGroups(GROUP_SET);

    String outputFilePath = this.labFunWriter.writeToFile();

    assertTrue(MSG_FILE_CRE_CHK, new File(outputFilePath).exists());
  }
}
