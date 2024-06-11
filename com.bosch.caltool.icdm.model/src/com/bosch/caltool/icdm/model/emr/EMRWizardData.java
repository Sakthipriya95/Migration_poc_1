/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;


/**
 * @author mkl2cob
 */
public class EMRWizardData {

  private String fileName;

  private String filePath;

  private String descripton;

  private boolean partialPIDCScope;

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
   * @return the descripton
   */
  public String getDescripton() {
    return this.descripton;
  }


  /**
   * @param descripton the descripton to set
   */
  public void setDescripton(final String descripton) {
    this.descripton = descripton;
  }


  /**
   * @return the partialPIDCValidity
   */
  public boolean isPartialPIDCScope() {
    return this.partialPIDCScope;
  }


  /**
   * @param partialPIDCValidity the partialPIDCValidity to set
   */
  public void setPartialPIDCScope(final boolean partialPIDCValidity) {
    this.partialPIDCScope = partialPIDCValidity;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return filePath;
  }


  /**
   * @param filePath the filePath to set
   */
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }


}
