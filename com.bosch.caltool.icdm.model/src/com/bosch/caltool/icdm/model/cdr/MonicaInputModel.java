/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

/**
 * @author say8cob
 */
public class MonicaInputModel {

  private Long pidcA2lId;

  private String description;

  private String ownUserName;

  private List<String> reviewParticipants;

  private String calEngUserName;

  private String audUserName;

  private List<MonicaInputData> monicaInputDataList;


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }


  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the ownUserName
   */
  public String getOwnUserName() {
    return this.ownUserName;
  }


  /**
   * @param ownUserName the ownUserName to set
   */
  public void setOwnUserName(final String ownUserName) {
    this.ownUserName = ownUserName;
  }


  /**
   * @return the reviewParticipants
   */
  public List<String> getReviewParticipants() {
    return this.reviewParticipants;
  }


  /**
   * @param reviewParticipants the reviewParticipants to set
   */
  public void setReviewParticipants(final List<String> reviewParticipants) {
    this.reviewParticipants = reviewParticipants;
  }


  /**
   * @return the calEngUserName
   */
  public String getCalEngUserName() {
    return this.calEngUserName;
  }


  /**
   * @param calEngUserName the calEngUserName to set
   */
  public void setCalEngUserName(final String calEngUserName) {
    this.calEngUserName = calEngUserName;
  }


  /**
   * @return the audUserName
   */
  public String getAudUserName() {
    return this.audUserName;
  }


  /**
   * @param audUserName the audUserName to set
   */
  public void setAudUserName(final String audUserName) {
    this.audUserName = audUserName;
  }


  /**
   * @return the monicaInputDataList
   */
  public List<MonicaInputData> getMonicaInputDataList() {
    return this.monicaInputDataList;
  }


  /**
   * @param monicaInputDataList the monicaInputDataList to set
   */
  public void setMonicaInputDataList(final List<MonicaInputData> monicaInputDataList) {
    this.monicaInputDataList = monicaInputDataList;
  }


}
