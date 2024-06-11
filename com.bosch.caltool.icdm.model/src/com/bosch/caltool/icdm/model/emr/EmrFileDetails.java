/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;


/**
 * @author dja7cob
 *
 */
public class EmrFileDetails {
  
  /**
   * EMR file id
   */
  private Long emrFileId;
  
  /**
   * EMR file name
   */
  private String emrFileName;
  
  /**
   * Pidc variant Id
   */
  private Long variantId;
  
  /**
   * Pidc variant name
   */
  private String variantName;
  
  /**
   * Pidc version Id
   */
  private Long pidcVersId;
  
  /**
   * Pidc Name
   */
  private String pidcVersName;
  
  /**
   * Loaded without errors flag
   */
  private boolean loadedWithoutErrorsFlag;
  
  /**
   * Deleted flag
   */
  private boolean deletedFlag;

  
  /**
   * @return the emrFileId
   */
  public Long getEmrFileId() {
    return emrFileId;
  }

  
  /**
   * @param emrFileId the emrFileId to set
   */
  public void setEmrFileId(Long emrFileId) {
    this.emrFileId = emrFileId;
  }

  
  /**
   * @return the emrFileName
   */
  public String getEmrFileName() {
    return emrFileName;
  }

  
  /**
   * @param emrFileName the emrFileName to set
   */
  public void setEmrFileName(String emrFileName) {
    this.emrFileName = emrFileName;
  }

  
  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return variantId;
  }

  
  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(Long variantId) {
    this.variantId = variantId;
  }

  
  /**
   * @return the variantName
   */
  public String getVariantName() {
    return variantName;
  }

  
  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(String variantName) {
    this.variantName = variantName;
  }

  
  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return pidcVersId;
  }

  
  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  
  /**
   * @return the pidcName
   */
  public String getPidcVersName() {
    return pidcVersName;
  }

  
  /**
   * @param pidcVersName the pidcName to set
   */
  public void setPidcVersName(String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }

  
  /**
   * @return the loadedWithoutErrorsFlag
   */
  public boolean isLoadedWithoutErrorsFlag() {
    return loadedWithoutErrorsFlag;
  }

  
  /**
   * @param loadedWithoutErrorsFlag the loadedWithoutErrorsFlag to set
   */
  public void setLoadedWithoutErrorsFlag(boolean loadedWithoutErrorsFlag) {
    this.loadedWithoutErrorsFlag = loadedWithoutErrorsFlag;
  }

  
  /**
   * @return the deletedFlag
   */
  public boolean isDeletedFlag() {
    return deletedFlag;
  }

  
  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(boolean deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

}
