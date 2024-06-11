/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author say8cob
 */
public class SSDReleaseIcdmModel {

  private Long releaseId;

  private String release;

  private String labelListDesc;

  private String releaseDesc;

  private String releaseDate;

  private String createdUser;

  private String externalRelease;


  /**
   * @return the releaseId
   */
  public Long getReleaseId() {
    return this.releaseId;
  }


  /**
   * @param releaseId the releaseId to set
   */
  public void setReleaseId(final Long releaseId) {
    this.releaseId = releaseId;
  }


  /**
   * @return the release
   */
  public String getRelease() {
    return this.release;
  }


  /**
   * @param release the release to set
   */
  public void setRelease(final String release) {
    this.release = release;
  }


  /**
   * @return the labelListDesc
   */
  public String getLabelListDesc() {
    return this.labelListDesc;
  }


  /**
   * @param labelListDesc the labelListDesc to set
   */
  public void setLabelListDesc(final String labelListDesc) {
    this.labelListDesc = labelListDesc;
  }


  /**
   * @return the releaseDesc
   */
  public String getReleaseDesc() {
    return this.releaseDesc;
  }


  /**
   * @param releaseDesc the releaseDesc to set
   */
  public void setReleaseDesc(final String releaseDesc) {
    this.releaseDesc = releaseDesc;
  }


  /**
   * @return the releaseDate
   */
  public String getReleaseDate() {
    return this.releaseDate;
  }


  /**
   * @param releaseDate the releaseDate to set
   */
  public void setReleaseDate(final String releaseDate) {
    this.releaseDate = releaseDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the externalRelease
   */
  public String getExternalRelease() {
    return this.externalRelease;
  }


  /**
   * @param externalRelease the externalRelease to set
   */
  public void setExternalRelease(final String externalRelease) {
    this.externalRelease = externalRelease;
  }


  /**
   * @return the errors
   */
  public String getErrors() {
    return this.errors;
  }


  /**
   * @param errors the errors to set
   */
  public void setErrors(final String errors) {
    this.errors = errors;
  }


  /**
   * @return the globalByPass
   */
  public String getGlobalByPass() {
    return this.globalByPass;
  }


  /**
   * @param globalByPass the globalByPass to set
   */
  public void setGlobalByPass(final String globalByPass) {
    this.globalByPass = globalByPass;
  }


  /**
   * @return the buggyDate
   */
  public String getBuggyDate() {
    return this.buggyDate;
  }


  /**
   * @param buggyDate the buggyDate to set
   */
  public void setBuggyDate(final String buggyDate) {
    this.buggyDate = buggyDate;
  }


  /**
   * @return the dependencyList
   */
  public List<FeatureValueICDMModel> getDependencyList() {
    return this.dependencyList;
  }


  /**
   * @param dependencyList the dependencyList to set
   */
  public void setDependencyList(final List<FeatureValueICDMModel> dependencyList) {
    if (dependencyList != null) {
      this.dependencyList = new ArrayList<>(dependencyList);
    }
  }

  private String errors;

  private String globalByPass;

  private String buggyDate;

  private List<FeatureValueICDMModel> dependencyList = new ArrayList<FeatureValueICDMModel>();
}
