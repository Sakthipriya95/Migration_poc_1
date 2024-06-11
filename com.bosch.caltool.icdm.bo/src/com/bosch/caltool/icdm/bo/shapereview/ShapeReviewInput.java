/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.shapereview;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;


/**
 * Input data for shape analysis
 *
 * @author bne4cob
 */
public class ShapeReviewInput {

  private Long pidcVersID;

  private final Set<String> dataReviewParamsSet = new HashSet<>();

  private final ConcurrentMap<String, CalData> dataReviewInputDataMap = new ConcurrentHashMap<>();

  private Long a2lFileID;

  private final SortedSet<A2LBaseComponents> a2lBcSet;

  /**
   *
   */
  private final A2LFileInfo a2lFileInfo;

  /**
   * @param a2lBcSet
   * @param a2lFileContentsMap
   */
  public ShapeReviewInput(final SortedSet<A2LBaseComponents> a2lBcSet, final A2LFileInfo a2lFileInfo) {
    super();
    this.a2lBcSet = a2lBcSet;
    this.a2lFileInfo = a2lFileInfo;
  }

  /**
   * @return the dataReviewInputDataMap
   */
  public Map<String, CalData> getDataReviewInputDataMap() {
    return new ConcurrentHashMap<>(this.dataReviewInputDataMap);
  }


  /**
   * @param dataReviewInputDataMap the dataReviewInputDataMap to set
   */
  public void setDataReviewInputDataMap(final Map<String, CalData> dataReviewInputDataMap) {
    this.dataReviewInputDataMap.clear();
    this.dataReviewInputDataMap.putAll(dataReviewInputDataMap);
  }


  /**
   * @return the a2lFileID
   */
  public Long getA2lFileID() {
    return this.a2lFileID;
  }


  /**
   * @param a2lFileID the a2lFileID to set
   */
  public void setA2lFileID(final Long a2lFileID) {
    this.a2lFileID = a2lFileID;
  }


  /**
   * @return the dataReviewParamsSet
   */
  public Set<String> getDataReviewParamsSet() {
    return new HashSet<>(this.dataReviewParamsSet);
  }


  /**
   * @param dataReviewParamsSet the dataReviewParamsSet to set
   */
  public void setDataReviewParamsSet(final Set<String> dataReviewParamsSet) {
    this.dataReviewParamsSet.clear();
    this.dataReviewParamsSet.addAll(dataReviewParamsSet);
  }


  /**
   * @return the a2lBcSet
   */
  public SortedSet<A2LBaseComponents> getA2lBcSet() {
    return this.a2lBcSet;
  }

  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersID;
  }

  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersID = pidcVersId;
  }

  /**
   * @return the a2lFileInfo
   */
  public A2LFileInfo getA2lFileInfo() {
    return this.a2lFileInfo;
  }

}
