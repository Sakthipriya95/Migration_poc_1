/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerResultFileModel {

  private String fileName;

  private String filePath;

  private String fileDescription;

  private Long fileSize = 0L;

  /**
   * @param fileName
   * @param filePath
   */
  public CaldataAnalyzerResultFileModel(final String fileName, final String filePath, final Long fileSize) {
    this.fileName = fileName;
    this.filePath = filePath;
    this.fileSize = fileSize;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


  /**
   * @param filePath the filePath to set
   */
  public void setFilePath(final String filePath) {
    this.filePath = filePath;
  }


  /**
   * @return the fileDescription
   */
  public String getFileDescription() {
    return this.fileDescription;
  }


  /**
   * @param fileDescription the fileDescription to set
   */
  public void setFileDescription(final String fileDescription) {
    this.fileDescription = fileDescription;
  }


  /**
   * @return the fileSize
   */
  public Long getFileSize() {
    return this.fileSize;
  }


  /**
   * @param fileSize the fileSize to set
   */
  public void setFileSize(final Long fileSize) {
    this.fileSize = fileSize;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "CaldataAnalyzerResultFileModel [fileName=" + this.fileName + ", filePath=" + this.filePath +
        ", fileDescription=" + this.fileDescription + ", fileSize=" + this.fileSize + "]";
  }


}
