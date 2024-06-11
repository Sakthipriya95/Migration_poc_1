/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author and4cob
 */
public class MonicaFileData {

  private int index;

  /**
   * DCM File name
   */
  private String dcmFileName;

  /**
   * DCM File path
   */
  private String dcmFIlePath;

  /**
   * MoniCa File name
   */
  private String monicaFileName;

  /**
   * Monica File path
   */
  private String monicaFilePath;

  /**
   * Excel Sheet name
   */
  private String sheetName;

  /**
   * Variant name
   */
  private String variantName;
  /**
   * Variant Id
   */
  private Long varId;

  /**
   * All monica file sheets present in the excel sheet
   */
  private List<String> allMonicaSheets = new ArrayList<>();


  private boolean isDeltaReview;


  /**
   * @param dcmFilePath dcmFilePath
   * @param dcmFileName dcmFileName
   * @param monicaFilePath monicaFilePath
   * @param monicaFileName monicaFileName
   * @param sheetName sheetName
   * @param variantName variantName
   * @param allMonicaSheets allMonicaSheets
   */
  public MonicaFileData(final String dcmFilePath, final String dcmFileName, final String monicaFilePath,
      final String monicaFileName, final String sheetName, final String variantName,
      final List<String> allMonicaSheets) {
    super();
    this.dcmFIlePath = dcmFilePath;
    this.dcmFileName = dcmFileName;
    this.monicaFilePath = monicaFilePath;
    this.monicaFileName = monicaFileName;
    this.sheetName = sheetName;
    this.variantName = variantName;
    this.allMonicaSheets = allMonicaSheets;
  }

  /**
   * Copy constructor
   *
   * @param monicaFileData monica File Data
   */
  public MonicaFileData(final MonicaFileData monicaFileData) {
    super();
    this.dcmFIlePath = monicaFileData.getDcmFIlePath();
    this.dcmFileName = monicaFileData.getDcmFileName();
    this.monicaFilePath = monicaFileData.getMonicaFilePath();
    this.monicaFileName = monicaFileData.getMonicaFileName();
    this.sheetName = monicaFileData.getSheetName();
    this.variantName = monicaFileData.getVariantName();
    this.allMonicaSheets = monicaFileData.getAllMonicaSheets();
    this.varId = monicaFileData.getVarId();
  }

  /**
   *
   */
  public MonicaFileData() {
    // TODO Auto-generated constructor stub
  }


  /**
   * @return the dcmFileName
   */
  public String getDcmFileName() {
    return this.dcmFileName;
  }


  /**
   * @param dcmFileName the dcmFileName to set
   */
  public void setDcmFileName(final String dcmFileName) {
    this.dcmFileName = dcmFileName;
  }


  /**
   * @return the monicaFileName
   */
  public String getMonicaFileName() {
    return this.monicaFileName;
  }


  /**
   * @param monicaFileName the monicaFileName to set
   */
  public void setMonicaFileName(final String monicaFileName) {
    this.monicaFileName = monicaFileName;
  }

  /**
   * @return the sheetName
   */
  public String getSheetName() {
    return this.sheetName;
  }


  /**
   * @param sheetName the sheetName to set
   */
  public void setSheetName(final String sheetName) {
    this.sheetName = sheetName;
  }


  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }


  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
  }


  /**
   * @return the dcmFIlePath
   */
  public String getDcmFIlePath() {
    return this.dcmFIlePath;
  }


  /**
   * @param dcmFIlePath the dcmFIlePath to set
   */
  public void setDcmFIlePath(final String dcmFIlePath) {
    this.dcmFIlePath = dcmFIlePath;
  }


  /**
   * @return the monicaFilePath
   */
  public String getMonicaFilePath() {
    return this.monicaFilePath;
  }


  /**
   * @param monicaFilePath the monicaFilePath to set
   */
  public void setMonicaFilePath(final String monicaFilePath) {
    this.monicaFilePath = monicaFilePath;
  }


  /**
   * @return the varId
   */
  public Long getVarId() {
    return this.varId;
  }


  /**
   * @param varId the varId to set
   */
  public void setVarId(final Long varId) {
    this.varId = varId;
  }


  /**
   * @return the index
   */
  public int getIndex() {
    return this.index;
  }


  /**
   * @param index the index to set
   */
  public void setIndex(final int index) {
    this.index = index;
  }


  /**
   * @return the allMonicaSheets list
   */
  public List<String> getAllMonicaSheets() {
    return this.allMonicaSheets;
  }


  /**
   * @param allMonicaSheets the allMonicaSheets list to set
   */
  public void setAllMonicaSheets(final List<String> allMonicaSheets) {
    this.allMonicaSheets = allMonicaSheets;
  }


  /**
   * @return the isDeltaReview
   */
  public boolean isDeltaReview() {
    return this.isDeltaReview;
  }


  /**
   * @param isDeltaReview the isDeltaReview to set
   */
  public void setDeltaReview(final boolean isDeltaReview) {
    this.isDeltaReview = isDeltaReview;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.dcmFileName == null) ? 0 : this.dcmFileName.hashCode());
    result = (prime * result) + (this.isDeltaReview ? 1231 : 1237);
    result = (prime * result) + ((this.monicaFileName == null) ? 0 : this.monicaFileName.hashCode());
    result = (prime * result) + ((this.sheetName == null) ? 0 : this.sheetName.hashCode());
    result = (prime * result) + ((this.varId == null) ? 0 : this.varId.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MonicaFileData other = (MonicaFileData) obj;
    if (this.dcmFileName == null) {
      if (other.dcmFileName != null) {
        return false;
      }
    }
    else if (!this.dcmFileName.equals(other.dcmFileName)) {
      return false;
    }
    if (this.isDeltaReview != other.isDeltaReview) {
      return false;
    }
    if (this.monicaFileName == null) {
      if (other.monicaFileName != null) {
        return false;
      }
    }
    else if (!this.monicaFileName.equals(other.monicaFileName)) {
      return false;
    }
    if (this.sheetName == null) {
      if (other.sheetName != null) {
        return false;
      }
    }
    else if (!this.sheetName.equals(other.sheetName)) {
      return false;
    }
    if (this.varId == null) {
      if (other.varId != null) {
        return false;
      }
    }
    else if (!this.varId.equals(other.varId)) {
      return false;
    }
    return true;
  }

}
