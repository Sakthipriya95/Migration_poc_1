/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author ICP1COB
 */
public class ComplianceReview implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4876012356462299387L;

  private Integer buId;
  private List<Participant> participantsList;
  private ReviewDetails commonReviewDetail;
  private List<String> checkListNames;

  
  

  
  
  /**
   * @return the buId
   */
  public Integer getBuId() {
    return buId;
  }


  
  /**
   * @param buId the buId to set
   */
  public void setBuId(Integer buId) {
    this.buId = buId;
  }


  /**
   * @return the commonReviewDetail
   */
  public ReviewDetails getCommonReviewDetail() {
    return commonReviewDetail;
  }

  
  /**
   * @param commonReviewDetail the commonReviewDetail to set
   */
  public void setCommonReviewDetail(ReviewDetails commonReviewDetail) {
    this.commonReviewDetail = commonReviewDetail;
  }

  /**
   * @return the participantsList
   */
  public List<Participant> getParticipantsList() {
    return participantsList;
  }

  /**
   * @param participantsList the participantsList to set
   */
  public void setParticipantsList(List<Participant> participantsList) {
    this.participantsList = participantsList;
  }

  /**
   * @return the checkListNames
   */
  public List<String> getCheckListNames() {
    return checkListNames;
  }

  /**
   * @param checkListNames the checkListNames to set
   */
  public void setCheckListNames(List<String> checkListNames) {
    this.checkListNames = checkListNames;
  }
}
