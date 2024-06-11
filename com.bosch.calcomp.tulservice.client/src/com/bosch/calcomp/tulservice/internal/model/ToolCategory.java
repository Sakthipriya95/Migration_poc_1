/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.internal.model;


/**
 * ENUM that contains list of possible tool cateogry to be passed as input to TUL server
 * 
 * @author SSN9COB
 */
public enum ToolCategory {

                          /**
                           * For Category - PROCESSING. Standard Artefacts processing Tools EG: SW Build
                           */
                          PROCESSING("processing"),

                          /**
                           * For Category - WORLFLOW. Hotline Ticket processing or approval workflows
                           */
                          WORKFLOW("workflow"),

                          /**
                           * For Category - PUBLICATION. Tools providing Read-Only Information, EG: Via Web Pages likes
                           * Process Library
                           */
                          PUBLICATION("publication");

  private final String categoryType;


  /**
   * Constructor
   */
  private ToolCategory(final String type) {
    this.categoryType = type;
  }

  /**
   * @return String - For Category as required by TUL Server
   */
  public String getToolCategory() {
    return this.categoryType;
  }
}
