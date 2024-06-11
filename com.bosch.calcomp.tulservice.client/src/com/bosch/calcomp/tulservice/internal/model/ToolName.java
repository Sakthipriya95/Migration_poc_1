/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.internal.model;


/**
 * Acceptable tool names for Tool Logger client
 * 
 * @author GDH9COB
 *
 */
public enum ToolName {
  
  /**
   * CheckSSD Tool Name
   */
  CHECKSSD("CheckSSD"),
  /**
   * SSD Tool Name
   */
  SSD("SSD"),
  /**
   * ICDM Tool Name
   */
  ICDM("iCDM"),
  /**
   * DataChecker Tool Name
   */
  DATACHECKER("DataChecker");
  
  private String tool;

  /**
   * @param toolName
   */
  private ToolName(String tool) {
    this.setTool(tool);
  }

  /**
   * @return the tool
   */
  public String getTool() {
    return tool;
  }

  /**
   * @param tool the tool to set
   */
  private void setTool(String tool) {
    this.tool = tool;
  }
  
  

}
