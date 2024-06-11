/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

/**
 * @author say8cob
 */
public class MonicaInputData {

  private Long variantId;

  private List<MonicaReviewData> monicaObject;

  private String dcmFileName;

  private String monicaExcelFileName;

  private String dcmFilePath;

  private String monicaExcelFilePath;

  /**
   * is delta review
   */
  private boolean isDeltaReview;

  /**
   * Org Result Id
   */
  private Long orgResultId;

  /**
   * Delta Review Type
   */
  private String deltaReviewType;

  private String selMoniCaSheet;


  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }


  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }


  /**
   * @return the monicaObject
   */
  public List<MonicaReviewData> getMonicaObject() {
    return this.monicaObject;
  }


  /**
   * @param monicaObject the monicaObject to set
   */
  public void setMonicaObject(final List<MonicaReviewData> monicaObject) {
    this.monicaObject = monicaObject;
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
   * @return the monicaExcelFileName
   */
  public String getMonicaExcelFileName() {
    return this.monicaExcelFileName;
  }


  /**
   * @param monicaExcelFileName the monicaExcelFileName to set
   */
  public void setMonicaExcelFileName(final String monicaExcelFileName) {
    this.monicaExcelFileName = monicaExcelFileName;
  }


  /**
   * @return the dcmFilePath
   */
  public String getDcmFilePath() {
    return this.dcmFilePath;
  }


  /**
   * @param dcmFilePath the dcmFilePath to set
   */
  public void setDcmFilePath(final String dcmFilePath) {
    this.dcmFilePath = dcmFilePath;
  }


  /**
   * @return the monicaExcelFilePath
   */
  public String getMonicaExcelFilePath() {
    return this.monicaExcelFilePath;
  }


  /**
   * @param monicaExcelFilePath the monicaExcelFilePath to set
   */
  public void setMonicaExcelFilePath(final String monicaExcelFilePath) {
    this.monicaExcelFilePath = monicaExcelFilePath;
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
   * @return the orgResultId
   */
  public Long getOrgResultId() {
    return this.orgResultId;
  }


  /**
   * @param orgResultId the orgResultId to set
   */
  public void setOrgResultId(final Long orgResultId) {
    this.orgResultId = orgResultId;
  }


  /**
   * @return the deltaReviewType
   */
  public String getDeltaReviewType() {
    return this.deltaReviewType;
  }


  /**
   * @param deltaReviewType the deltaReviewType to set
   */
  public void setDeltaReviewType(final String deltaReviewType) {
    this.deltaReviewType = deltaReviewType;
  }


  /**
   * @return the selMoniCaSheet
   */
  public String getSelMoniCaSheet() {
    return this.selMoniCaSheet;
  }


  /**
   * @param selMoniCaSheet the selMoniCaSheet to set
   */
  public void setSelMoniCaSheet(final String selMoniCaSheet) {
    this.selMoniCaSheet = selMoniCaSheet;
  }


}
