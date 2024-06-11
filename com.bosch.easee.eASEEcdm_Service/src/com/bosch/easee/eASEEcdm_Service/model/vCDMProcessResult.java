/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.easee.eASEEcdm_Service.model;

import com.bosch.easee.eASEEcdm_ComAPI.ECdmProcessResult;
import com.bosch.easee.eASEEcdm_ComAPI.ECdmValidationResult;

/**
 * @author VAU3COB
 *
 */
public class vCDMProcessResult {
  
  /**
   * eCDM Validation Result
   */
  public ECdmValidationResult validationResult;
  
  /**
   * eCDM Process Result
   */
  public ECdmProcessResult ecdmProcessResult;
  
  /**
   * Exported filepath if any
   */
  public String filePath;
  
  /**
   * Variable to pass any message if required
   */
  public String message;

  
  /**
   * @param validationResult - Validation Result of the process
   * @param ecdmProcessResult - Integration Result of the process
   * @param filePath - File path of the Exported file if any
   * @param message - Any message to be returned
   */
  public vCDMProcessResult(ECdmValidationResult validationResult, ECdmProcessResult ecdmProcessResult, String filePath,
      String message) {
    super();
    this.validationResult = validationResult;
    this.ecdmProcessResult = ecdmProcessResult;
    this.filePath = filePath;
    this.message = message;
  }


  /**
   * 
   */
  public vCDMProcessResult() {
    // TODO Auto-generated constructor stub
  }


  /**
   * @return the validationResult
   */
  public ECdmValidationResult getValidationResult() {
    return validationResult;
  }

  
  /**
   * @param validationResult the validationResult to set
   */
  public void setValidationResult(ECdmValidationResult validationResult) {
    this.validationResult = validationResult;
  }

  
  /**
   * @return the ecdmProcessResult
   */
  public ECdmProcessResult getEcdmProcessResult() {
    return ecdmProcessResult;
  }

  
  /**
   * @param ecdmProcessResult the ecdmProcessResult to set
   */
  public void setEcdmProcessResult(ECdmProcessResult ecdmProcessResult) {
    this.ecdmProcessResult = ecdmProcessResult;
  }

  
  /**
   * @return the filePath
   */
  public String getFilePath() {
    return filePath;
  }

  
  /**
   * @param filePath the filePath to set
   */
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  
  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  
  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }
}
