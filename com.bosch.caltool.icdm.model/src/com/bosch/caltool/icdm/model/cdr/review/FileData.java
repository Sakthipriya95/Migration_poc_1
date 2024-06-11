/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;

import java.util.Set;

/**
 * @author bru2cob
 */
public class FileData {

  /**
   * Paths of file to be reviewed
   */
  private Set<String> selFilesPath;
  /**
   * Funlab file path
   */
  private String funLabFilePath;

  /**
   * @return the selFilesPath
   */
  public Set<String> getSelFilesPath() {
    return this.selFilesPath;
  }

  /**
   * @param selFilesPath the selFilesPath to set
   */
  public void setSelFilesPath(final Set<String> selFilesPath) {
    this.selFilesPath = selFilesPath;
  }


  /**
   * @return the funLabFilePath
   */
  public String getFunLabFilePath() {
    return this.funLabFilePath;
  }

  /**
   * @param funLabFilePath the funLabFilePath to set
   */
  public void setFunLabFilePath(final String funLabFilePath) {
    this.funLabFilePath = funLabFilePath;
  }


}
