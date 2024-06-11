/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author bru2cob
 */
public class VcdmHexFileData {

  byte[] hexFileBytes;

  String fileName;


  /**
   * @return the hexFileBytes
   */
  public byte[] getHexFileBytes() {
    return this.hexFileBytes;
  }


  /**
   * @param hexFileBytes the hexFileBytes to set
   */
  public void setHexFileBytes(final byte[] hexFileBytes) {
    this.hexFileBytes = hexFileBytes;
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

}
