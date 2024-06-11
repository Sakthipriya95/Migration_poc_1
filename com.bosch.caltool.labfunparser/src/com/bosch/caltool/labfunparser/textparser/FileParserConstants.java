/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.labfunparser.textparser;


/**
 * @author adn1cob
 */
public class FileParserConstants {

  // iCDM-711

  /**
   * Defines extension for .FUN
   */
  public static final String FUN_EXTN_UPPER = ".FUN";

  /**
   * For Sonarcube
   */
  private FileParserConstants() {}

  /**
   * Defines extension for .fun
   */
  public static final String FUN_EXTN_LOWER = ".fun";

  /**
   * Defines extension for .LAB
   */
  public static final String LAB_EXTN_UPPER = ".LAB";

  /**
   * Defines extension for .lab
   */
  public static final String LAB_EXTN_LOWER = ".lab";

  /**
   * Defines start of a BLOCK - start char
   */
  public static final String START_BLOCK_OPEN = "[";

  /**
   * Defines start of a BLOCK - end char
   */
  public static final String START_BLOCK_CLOSE = "]";

  /**
   * Defines Empty line
   */
  public static final String EMPTY_LINE = "\n";

  /**
   * Defines block [Label]
   */
  public static final String LABEL_BLOCK = "[Label]";

  /**
   * Defines block [Function]
   */
  public static final String FUNCTION_BLOCK = "[Function]";
  
  /**
   * ALM-442313
   * Defines block [GROUP]
   */
  public static final String GROUP_BLOCK = "[GROUP]";

  /**
   * Constant for ; (Comment)
   */
  public static final String COMMENT_KEYWORD = ";";
  
  /**
   * Defines file types
   */
  public enum INPUT_FILE_TYPE {
                                      /**
                                       * Label file
                                       */
                                      LAB("LAB"),
                                      /**
                                       * Function file
                                       */
                                      FUN("FUN");

    final String fileType;

    INPUT_FILE_TYPE(final String fileType) {
      this.fileType = fileType;
    }

    /**
     * @return FileType
     */
    public final String getFileType() {
      return this.fileType;
    }
  }

}
