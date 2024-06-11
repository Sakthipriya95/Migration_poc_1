/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.model;

import com.bosch.calcomp.tulservice.internal.model.ToolCategory;
import com.bosch.calcomp.tulservice.internal.model.ToolEvents;

/**
 * @author GDH9COB
 */
public class ToolLogData {

  private String jobID;
  private String username;
  private String toolCategory;
  private String tool;
  private String toolVersion;
  private String event;
  private String component;
  private String feature;
  private String artifactInfo;
  private String misc;

  /**
   * @param username     Name
   * @param toolCategory categoryType
   * @param tool         ToolName
   * @param toolVersion  Version
   * @param test         isTest?
   */

  @Deprecated
  public ToolLogData(final String username, final String toolCategory, final String tool, final String toolVersion,
      final boolean test) {
    this(username, toolCategory, tool, toolVersion);
  }

  /**
   * ToolLogData Constructor that gets the following inputs TODO: Constructor to be deprecated once iCDM is using the
   * new constructor
   *
   * @param username     Name
   * @param toolCategory CateogryType
   * @param tool         Tool
   * @param toolVersion  Version
   */
  public ToolLogData(final String username, final String toolCategory, final String tool, final String toolVersion) {
    this.username = username;
    this.toolCategory = toolCategory;
    this.tool = tool;
    this.toolVersion = toolVersion;
    // DEFAULT EVENT
    this.event = ToolEvents.STARTED.getToolEvent();
  }
  /**
   * 
   */
  public ToolLogData() {
    // Public constructor
  }

  /**
   * ToolLogData Constructor that gets the following inputs
   *
   * @param username     Name
   * @param toolCategory CateogryType
   * @param tool         Tool
   * @param toolVersion  Version
   * @param event        event
   */
  public ToolLogData(final String username, final ToolCategory toolCategory, final String tool,
      final String toolVersion, final ToolEvents event) {
    this.username = username;
    this.toolCategory = toolCategory.getToolCategory();
    this.tool = tool;
    this.toolVersion = toolVersion;
    this.event = event.getToolEvent();
  }

  /**
   * @return the component
   */
  public String getComponent() {
    return this.component;
  }

  /**
   * @param component the component to set
   */
  public void setComponent(final String component) {
    this.component = component;
  }

  /**
   * @return the feature
   */
  public String getFeature() {
    return this.feature;
  }

  /**
   * @param feature the feature to set
   */
  public void setFeature(final String feature) {
    this.feature = feature;
  }

  /**
   * @return the event
   */
  public String getEvent() {
    return this.event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(final String event) {
    this.event = event;
  }

  /**
   * @return the artifactInfo
   */
  public String getArtifactInfo() {
    return this.artifactInfo;
  }

  /**
   * @param artifactInfo the artifactInfo to set
   */
  public void setArtifactInfo(final String artifactInfo) {
    this.artifactInfo = artifactInfo;
  }

  /**
   * @return the misc
   */
  public String getMisc() {
    return this.misc;
  }

  /**
   * @param misc the misc to set
   */
  public void setMisc(final String misc) {
    this.misc = misc;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @return the toolCategory
   */
  public String getToolCategory() {
    return this.toolCategory;
  }

  /**
   * @return the tool
   */
  public String getTool() {
    return this.tool;
  }

  /**
   * @return the toolVersion
   */
  public String getToolVersion() {
    return this.toolVersion;
  }

  /**
   * @return the jobID
   */
  public String getJobID() {
    return this.jobID;
  }

  /**
   * @param jobID the jobID to set
   */
  public void setJobID(final String jobID) {
    this.jobID = jobID;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @param tool the tool to set
   */
  public void setTool(String tool) {
    this.tool = tool;
  }

  /**
   * @param toolCategory the toolCategory to set
   */
  public void setToolCategory(String toolCategory) {
    this.toolCategory = toolCategory;
  }

  /**
   * @param toolVersion the toolVersion to set
   */
  public void setToolVersion(String toolVersion) {
    this.toolVersion = toolVersion;
  }

}
