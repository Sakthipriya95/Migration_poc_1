/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;


/**
 * @author gue1cob 
 * Model to hold the error details for the release generated
 *
 */
public class ReleaseErrorModel {
  /**
   * Label Name
   */
  private String label;
  /**
   * Error Number 
   */
  private BigDecimal errorNo;
  /**
   * Description of the error
   */
  private String errorDescription;
  /**
   * scope of the node
   */
  private String nodeScope;
  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }
  
  /**
   * @return the errorNo
   */
  public BigDecimal getErrorNo() {
    return errorNo;
  }
  
  /**
   * @param errorNo the errorNo to set
   */
  public void setErrorNo(BigDecimal errorNo) {
    this.errorNo = errorNo;
  }
  
  /**
   * @return the errorDescription
   */
  public String getErrorDescription() {
    return errorDescription;
  }
  
  /**
   * @param errorDescription the errorDescription to set
   */
  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }
  
  /**
   * @return the nodeScope
   */
  public String getNodeScope() {
    return nodeScope;
  }
  
  /**
   * @param nodeScope the nodeScope to set
   */
  public void setNodeScope(String nodeScope) {
    this.nodeScope = nodeScope;
  }
  
  
  

  
}
