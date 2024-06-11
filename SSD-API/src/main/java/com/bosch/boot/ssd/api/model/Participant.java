/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.io.Serializable;

/**
 * @author ICP1COB
 */
public class Participant implements Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = 7681810280740145358L;

  private int participantTypeId;
  private String participantUserNTID;
  
  /**
   * @return the participantTypeId
   */
  public int getParticipantTypeId() {
    return participantTypeId;
  }
  
  /**
   * @param participantTypeId the participantTypeId to set
   */
  public void setParticipantTypeId(int participantTypeId) {
    this.participantTypeId = participantTypeId;
  }
  
  /**
   * @return the participantUserNTID
   */
  public String getParticipantUserNTID() {
    return participantUserNTID;
  }
  
  /**
   * @param participantUserNTID the participantUserNTID to set
   */
  public void setParticipantUserNTID(String participantUserNTID) {
    this.participantUserNTID = participantUserNTID;
  }

  
}
