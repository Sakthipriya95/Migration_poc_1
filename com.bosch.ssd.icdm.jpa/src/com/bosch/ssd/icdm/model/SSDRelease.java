/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * @author mrf5cob
 */
public class SSDRelease {

  /*
   * // query to get the release details based on the VILLA Sw Version Id select SV.VILLA_SWVERS_ID,LL.*,SR.* from
   * V_LDB2_SW_VERS SV INNER JOIN V_LDB2_PROJECT_REVISION LL ON SV.VERS_ID=LL.VERS_ID INNER JOIN
   * V_LDB2_PROJECT_RELEASE_EDC17 SR ON LL.PRO_REV_ID=SR.PRO_REV_ID WHERE SV.VILLA_SWVERS_ID in (109591)
   */
  private BigDecimal releaseId;

  private String release;

  private String labelListDesc;

  private String releaseDesc;

  private Date releaseDate;

  private String createdUser;

  private String externalRelease;

  private String errors;

  private String globalByPass;

  private Date buggyDate;

  private List<FeatureValueModel> dependencyList = new ArrayList<>();


  /**
   * @return the releaseId
   */
  public BigDecimal getReleaseId() {
    return this.releaseId;
  }


  /**
   * @param releaseId the releaseId to set
   */
  public void setReleaseId(final BigDecimal releaseId) {
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
  public Date getReleaseDate() {
    return (Date) this.releaseDate.clone();
  }


  /**
   * @param releaseDate the releaseDate to set
   */
  public void setReleaseDate(final Date releaseDate) {
    this.releaseDate = (Date) releaseDate.clone();
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
  public Date getBuggyDate() {
    if (Objects.nonNull(this.buggyDate)) {
      return (Date) this.buggyDate.clone();
    }
    return null;
  }


  /**
   * @param buggyDate the buggyDate to set
   */
  public void setBuggyDate(final Date buggyDate) {
    if (Objects.nonNull(buggyDate)) {
      this.buggyDate = (Date) buggyDate.clone();
    }
  }


  /**
   * @return the dependencyList
   */
  public List<FeatureValueModel> getDependencyList() {
    return new ArrayList<>(this.dependencyList);
  }


  /**
   * @param dependencyList the dependencyList to set
   */
  public void setDependencyList(final List<FeatureValueModel> dependencyList) {
    this.dependencyList = new ArrayList<>(dependencyList);
  }

}
