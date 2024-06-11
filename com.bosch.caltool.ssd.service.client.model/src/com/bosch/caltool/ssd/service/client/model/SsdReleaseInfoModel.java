/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.ssd.service.client.model;

import java.util.Map;

/**
 * @author QRK1COB
 *
 */
public class SsdReleaseInfoModel {

private String proRelId;

private String proRevId;

private String relId;

private String relType;

private String globalChk;

private String globalSsdHeader;

private String creDate;

private String creUser;

private String errors;

private Map<Integer, String> configInfo;

private Map<String, String> featureValueInfo;


/**
 * @return the proRelId
 */
public String getProRelId() {
  return this.proRelId;
}


/**
 * @param proRelId the proRelId to set
 */
public void setProRelId(final String proRelId) {
  this.proRelId = proRelId;
}


/**
 * @return the proRevId
 */
public String getProRevId() {
  return this.proRevId;
}


/**
 * @param proRevId the proRevId to set
 */
public void setProRevId(final String proRevId) {
  this.proRevId = proRevId;
}


/**
 * @return the relId
 */
public String getRelId() {
  return this.relId;
}


/**
 * @param relId the relId to set
 */
public void setRelId(final String relId) {
  this.relId = relId;
}


/**
 * @return the relType
 */
public String getRelType() {
  return this.relType;
}


/**
 * @param relType the relType to set
 */
public void setRelType(final String relType) {
  this.relType = relType;
}


/**
 * @return the globalChk
 */
public String getGlobalChk() {
  return this.globalChk;
}


/**
 * @param globalChk the globalChk to set
 */
public void setGlobalChk(final String globalChk) {
  this.globalChk = globalChk;
}


/**
 * @return the globalSsdHeader
 */
public String getGlobalSsdHeader() {
  return this.globalSsdHeader;
}


/**
 * @param globalSsdHeader the globalSsdHeader to set
 */
public void setGlobalSsdHeader(final String globalSsdHeader) {
  this.globalSsdHeader = globalSsdHeader;
}


/**
 * @return the creDate
 */
public String getCreDate() {
  return this.creDate;
}


/**
 * @param creDate the creDate to set
 */
public void setCreDate(final String creDate) {
  this.creDate = creDate;
}


/**
 * @return the creUser
 */
public String getCreUser() {
  return this.creUser;
}


/**
 * @param creUser the creUser to set
 */
public void setCreUser(final String creUser) {
  this.creUser = creUser;
}


/**
 * @return the errors
 */
public String getErrors() {
  return this.errors;
}


/**
 * @param errors the errors to set
 */
public void setErrors(final String errors) {
  this.errors = errors;
}


/**
 * @return the configInfo
 */
public Map<Integer, String> getConfigInfo() {
  return this.configInfo;
}


/**
 * @param configInfo the configInfo to set
 */
public void setConfigInfo(final Map<Integer, String> configInfo) {
  this.configInfo = configInfo;
}


/**
 * @return the featureValueInfo
 */
public Map<String, String> getFeatureValueInfo() {
  return this.featureValueInfo;
}


/**
 * @param featureValueInfo the featureValueInfo to set
 */
public void setFeatureValueInfo(final Map<String, String> featureValueInfo) {
  this.featureValueInfo = featureValueInfo;
}


}
