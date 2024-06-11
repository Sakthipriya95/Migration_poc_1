/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.labfunwriter;


/**
 * Constants used in LabFunWriter
 *
 * @author dja7cob
 */
public class LabFunWriterConstants {


  /**
   * Defines file types
   */
  public enum OUTPUT_FILE_TYPE {
                                /**
                                 * Label file
                                 */
                                LAB("LAB", "lab"),
                                /**
                                 * Function file
                                 */
                                FUN("FUN", "fun");

    final String fileType;

    final String fileExtension;


    OUTPUT_FILE_TYPE(final String fileType, final String fileExtension) {
      this.fileType = fileType;
      this.fileExtension = fileExtension;
    }

    /**
     * @return the fileExtension
     */
    public String getFileExtension() {
      return this.fileExtension;
    }

    /**
     * @return FileType
     */
    public final String getFileType() {
      return this.fileType;
    }
  }

  /**
   * Default output directory to write LAB/FUN file
   */
  static final String DEFAULT_OUTPUT_DIR = System.getProperty("java.io.tmpdir");

  /**
   * Default file name to write LAB/FUN file
   */
  static final String DEFAULT_FILE_NAME = "file";

  /**
   * Header tag to write Functions
   */
  static final String FUNCTION_TAG = "[Function]";

  /**
   * Header tag to write leabels
   */
  static final String LABEL_TAG = "[Label]";

  /**
   * Header tag to write groups
   */
  static final String GROUP_TAG = "[Group]";

  /**
   * Private constructor
   */
  private LabFunWriterConstants() {
    // Private Constructor
  }
}
