/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author say8cob
 *
 */
public class WPRespStatusMsgWrapper {
  
  private Long wpRespId;
  
  private String workPackageName;
  
  private String respName;
  
  private String outputStatusMsg;

  
  /**
   * @return the wpRespId
   */
  public Long getWpRespId() {
    return wpRespId;
  }

  
  /**
   * @param wpRespId the wpRespId to set
   */
  public void setWpRespId(Long wpRespId) {
    this.wpRespId = wpRespId;
  }

  
  /**
   * @return the outputStatusMsg
   */
  public String getOutputStatusMsg() {
    return outputStatusMsg;
  }

  
  /**
   * @param outputStatusMsg the outputStatusMsg to set
   */
  public void setOutputStatusMsg(String outputStatusMsg) {
    this.outputStatusMsg = outputStatusMsg;
  }


  
  /**
   * @return the workPackageName
   */
  public String getWorkPackageName() {
    return workPackageName;
  }


  
  /**
   * @param workPackageName the workPackageName to set
   */
  public void setWorkPackageName(String workPackageName) {
    this.workPackageName = workPackageName;
  }


  
  /**
   * @return the respName
   */
  public String getRespName() {
    return respName;
  }


  
  /**
   * @param respName the respName to set
   */
  public void setRespName(String respName) {
    this.respName = respName;
  }
  
  

}
