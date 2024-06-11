/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.internal.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.bosch.calcomp.tulservice.utils.MessageConstants;

/**
 * @author GDH9COB
 */
public class ToolUsageStat {

  /**
   *
   */
  // Mandatory
  private String username;
  private Long timestamp;
  // Mandatory
  private String toolCategory;
  // Mandatory
  private String tool;
  // Mandatory
  private String toolVersion;
  private String component;
  private String feature;
  private String jobID;
  // Mandatory
  private String event;
  // Mandatory
  private Long eventsCount;
  private Context context;
  private String artifactInfo;
  // Mandatory
  private String isTest;
  // Mandatory
  private String schemaVer;
  private String misc;
  private String filePath;

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the timestamp
   */
  public Long getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * @return the toolCategory
   */
  public String getToolCategory() {
    return toolCategory;
  }

  /**
   * @param toolCategory the toolCategory to set
   */
  public void setToolCategory(String toolCategory) {
    this.toolCategory = toolCategory;
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
  public void setTool(String tool) {
    this.tool = tool;
  }

  /**
   * @return the toolVersion
   */
  public String getToolVersion() {
    return toolVersion;
  }

  /**
   * @param toolVersion the toolVersion to set
   */
  public void setToolVersion(String toolVersion) {
    this.toolVersion = toolVersion;
  }

  /**
   * @return the component
   */
  public String getComponent() {
    return component;
  }

  /**
   * @param component the component to set
   */
  public void setComponent(String component) {
    this.component = component;
  }

  /**
   * @return the feature
   */
  public String getFeature() {
    return feature;
  }

  /**
   * @param feature the feature to set
   */
  public void setFeature(String feature) {
    this.feature = feature;
  }

  /**
   * @return the jobID
   */
  public String getJobID() {
    return jobID;
  }

  /**
   * @param jobID the jobID to set
   */
  public void setJobID(String jobID) {
    this.jobID = jobID;
  }

  /**
   * @return the event
   */
  public String getEvent() {
    return event;
  }

  /**
   * @param event the event to set
   */
  public void setEvent(String event) {
    this.event = event;
  }

  /**
   * @return the context
   */
  public Context getContext() {
    return context;
  }

  /**
   * @param context the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * @return the artifactInfo
   */
  public String getArtifactInfo() {
    return artifactInfo;
  }

  /**
   * @param artifactInfo the artifactInfo to set
   */
  public void setArtifactInfo(String artifactInfo) {
    this.artifactInfo = artifactInfo;
  }

  /**
   * @return the isTest
   */
  public String getIsTest() {
    return isTest;
  }

  /**
   * @param isTest the isTest to set
   */
  public void setIsTest(String isTest) {
    this.isTest = isTest;
  }

  /**
   * @return the schemaVer
   */
  public String getSchemaVer() {
    return schemaVer;
  }

  /**
   * @param schemaVer the schemaVer to set
   */
  public void setSchemaVer(String schemaVer) {
    this.schemaVer = schemaVer;
  }

  /**
   * @return the misc
   */
  public String getMisc() {
    return misc;
  }

  /**
   * @param misc the misc to set
   */
  public void setMisc(String misc) {
    this.misc = misc;
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
   * @return
   */
  private Map<String, Object> getToolUsageStatMap() {
    Map<String, Object> parameterValueMap = new HashMap<>();
    parameterValueMap.put(MessageConstants.USERNAME, this.username);
    parameterValueMap.put(MessageConstants.TIMESTAMP, this.timestamp);
    parameterValueMap.put(MessageConstants.TOOL_CATEGORY, this.toolCategory);
    parameterValueMap.put(MessageConstants.EVENT, this.event);
    parameterValueMap.put(MessageConstants.TOOL, this.tool);
    parameterValueMap.put(MessageConstants.TOOL_VERSION, this.toolVersion);
//    parameterValueMap.put(MessageConstants.CONTEXT, this.context.toString())
    if (Objects.nonNull(this.jobID)) {
      parameterValueMap.put(MessageConstants.JOB_ID, this.jobID);
    }
    parameterValueMap.put(MessageConstants.TEST, this.isTest);
    parameterValueMap.put(MessageConstants.SCHEMA_VER, this.schemaVer);

    if (Objects.nonNull(this.component)) {
      parameterValueMap.put(MessageConstants.COMPONENT, this.component);
    }
    if (Objects.nonNull(this.feature)) {
      parameterValueMap.put(MessageConstants.FEATURE, this.feature);
    }
    if (Objects.nonNull(this.artifactInfo)) {
      parameterValueMap.put(MessageConstants.ARTIFACT_INFO, this.artifactInfo);
    }
    if (Objects.nonNull(this.misc)) {
      parameterValueMap.put(MessageConstants.MISC, this.misc);
    }

    return parameterValueMap;
  }

  /**
   * @return the eventsCount
   */
  public Long getEventsCount() {
    return this.eventsCount;
  }

  /**
   * @param eventsCount the eventsCount to set
   */
  public void setEventsCount(final Long eventsCount) {
    this.eventsCount = eventsCount;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    Map<String, Object> parameterMap = getToolUsageStatMap();
    StringBuilder stringBuilder = new StringBuilder("{");
    int size = parameterMap.size();
    int count = 0;

    for (Entry<String, Object> entrySet : parameterMap.entrySet()) {
      Object value = entrySet.getValue();
      String parameter = entrySet.getKey();

      if (value != null) {
        stringBuilder.append(parameter);
        stringBuilder.append(":");

        if ((value instanceof String) && !parameter.contains(MessageConstants.CONTEXT)) {
          stringBuilder.append("\"");
          stringBuilder.append(value);
          stringBuilder.append("\"");
        }
        else {
          stringBuilder.append(value);
        }
        count++;

        if (count < size) {
          stringBuilder.append(",");
        }
      }
    }

    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
