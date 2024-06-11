/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;


/**
 * @author TRL1COB
 */
public class DataAssessmentBaseline {

  /**
   * Unique Id of Data assessment Baseline
   */
  private Long id;
  /**
   * Name of the baseline
   */
  private String name;
  /**
   * Remarks on the baseline
   */
  private String remark;
  /**
   * Flag to indicate if the baseline is of series release
   */
  private boolean seriesReleaseType;
  /**
   * Flag to indicate if the baseline is of development
   */
  private boolean developmentType;
  /**
   * Open Assessment
   */
  private String openAssessment;
  /**
   * Archieved file
   */
  private String openArchievedFile;
  /**
   * Pidc Variant
   */
  private String pidcVariant;
  /**
   * Baseline file generation status
   */
  private String baselineFileGenStatus;
  /**
   * Date of baseline creation
   */
  private String createdDate;

  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the remark
   */
  public String getRemark() {
    return this.remark;
  }

  /**
   * @param remark the remark to set
   */
  public void setRemark(final String remark) {
    this.remark = remark;
  }

  /**
   * @return the seriesReleaseType
   */
  public boolean isSeriesReleaseType() {
    return this.seriesReleaseType;
  }

  /**
   * @param seriesReleaseType the seriesReleaseType to set
   */
  public void setSeriesReleaseType(final boolean seriesReleaseType) {
    this.seriesReleaseType = seriesReleaseType;
  }

  /**
   * @return the developmentType
   */
  public boolean isDevelopmentType() {
    return this.developmentType;
  }

  /**
   * @param developmentType the developmentType to set
   */
  public void setDevelopmentType(final boolean developmentType) {
    this.developmentType = developmentType;
  }

  /**
   * @return the openAssessment
   */
  public String getOpenAssessment() {
    return this.openAssessment;
  }

  /**
   * @param openAssessment the openAssessment to set
   */
  public void setOpenAssessment(final String openAssessment) {
    this.openAssessment = openAssessment;
  }

  /**
   * @return the openArchievedFile
   */
  public String getOpenArchievedFile() {
    return this.openArchievedFile;
  }

  /**
   * @param openArchievedFile the openArchievedFile to set
   */
  public void setOpenArchievedFile(final String openArchievedFile) {
    this.openArchievedFile = openArchievedFile;
  }

  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the pidcVariant
   */
  public String getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final String pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @return the baselineFileGenStatus
   */
  public String getBaselineFileGenStatus() {
    return this.baselineFileGenStatus;
  }


  /**
   * @param baselineFileGenStatus the baselineFileGenStatus to set
   */
  public void setBaselineFileGenStatus(final String baselineFileGenStatus) {
    this.baselineFileGenStatus = baselineFileGenStatus;
  }


}
