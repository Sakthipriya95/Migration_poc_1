/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rgo7cob Input element for web service
 */
public class A2lGroupInput {

  private final List<String> a2lGrplist = new ArrayList<>();

  private final Map<String, Set<String>> grpCharParamMap = new ConcurrentHashMap<>();

  private Long rootGrpID;

  private Long a2lID;

  private Long pidcA2LId;

  private Map<String, String> a2lGrpMap;

  private Long mappingSource;

  private Long defaultWPRespId;

  private String currentUserId;

  private Long typeId;

  private Long groupMappingId;

  /**
   * @return the rootGrpID
   */
  public Long getRootGrpID() {
    return this.rootGrpID;
  }


  /**
   * @param rootGrpID the rootGrpID to set
   */
  public void setRootGrpID(final Long rootGrpID) {
    this.rootGrpID = rootGrpID;
  }


  /**
   * @return the grpCharParamMap
   */
  public Map<String, Set<String>> getGrpCharParamMap() {
    return this.grpCharParamMap;
  }


  /**
   * @param a2lID the a2lID to set
   */
  public void setA2lID(final Long a2lID) {
    this.a2lID = a2lID;
  }


  /**
   * @return the a2lGrplist
   */
  public List<String> getA2lGrplist() {
    return this.a2lGrplist;
  }


  /**
   * @return the a2lID
   */
  public Long getA2lID() {
    return this.a2lID;
  }


  /**
   * @return the pidcA2LId
   */
  public Long getPidcA2LId() {
    return this.pidcA2LId;
  }


  /**
   * @param pidcA2LId the pidcA2LId to set
   */
  public void setPidcA2LId(final Long pidcA2LId) {
    this.pidcA2LId = pidcA2LId;
  }


  /**
   * @return the a2lGrpMap
   */
  public Map<String, String> getA2lGrpMap() {
    return this.a2lGrpMap;
  }


  /**
   * @param a2lGrpMap the a2lGrpMap to set
   */
  public void setA2lGrpMap(final Map<String, String> a2lGrpMap) {
    this.a2lGrpMap = a2lGrpMap;
  }


  /**
   * @return the mappingSource
   */
  public Long getMappingSource() {
    return this.mappingSource;
  }


  /**
   * @param mappingSource the mappingSource to set
   */
  public void setMappingSource(final Long mappingSource) {
    this.mappingSource = mappingSource;
  }


  /**
   * @return the defaultWPRespId
   */
  public Long getDefaultWPRespId() {
    return this.defaultWPRespId;
  }


  /**
   * @param defaultWPRespId the defaultWPRespId to set
   */
  public void setDefaultWPRespId(final Long defaultWPRespId) {
    this.defaultWPRespId = defaultWPRespId;
  }


  /**
   * @return the currentUserId
   */
  public String getCurrentUserId() {
    return this.currentUserId;
  }


  /**
   * @param currentUserId the currentUserId to set
   */
  public void setCurrentUserId(final String currentUserId) {
    this.currentUserId = currentUserId;
  }


  /**
   * @return the typeId
   */
  public Long getTypeId() {
    return this.typeId;
  }


  /**
   * @param typeId the typeId to set
   */
  public void setTypeId(final Long typeId) {
    this.typeId = typeId;
  }


  /**
   * @return the groupMappingId
   */
  public Long getGroupMappingId() {
    return this.groupMappingId;
  }


  /**
   * @param groupMappingId the groupMappingId to set
   */
  public void setGroupMappingId(final Long groupMappingId) {
    this.groupMappingId = groupMappingId;
  }


}
