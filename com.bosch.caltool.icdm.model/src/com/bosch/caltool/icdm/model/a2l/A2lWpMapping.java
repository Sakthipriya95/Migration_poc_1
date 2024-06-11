/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class A2lWpMapping.
 *
 * @author gge6cob
 */
public class A2lWpMapping {

  /** The div attr value id. */
  private Long divAttrValueId;

  /** The wp root grp attr value id. */
  private Long wpRootGrpAttrValueId;

  /** The wp type attr value id. */
  private Long wpTypeAttrValueId;

  /** The group mapping id. */
  private Long groupMappingId;

  /** Mapping source. */
  private Long mappingSourceId;

  /** Root group name. */
  private String wpRootGroupName;

  // icdm-276
  /** Key : FC2WP-WpName . Value : A2lWorkPackageGroup. */
  private Map<String, A2lWorkPackageGroup> workPackageGroupMap = new TreeMap<>();

  // iCDM-602, // icdm-276 Supporting changes
  /** Key : FC2WP-Id Value : A2lWorkPackage. */
  private Map<Long, A2lWpObj> wpMap = new TreeMap<>();

  /** The a 2 l grp map. */

  /**
   * The a2l grp map. <br>
   * key - A2lGroup Name . Value - A2LGroup
   */
  private Map<String, A2LGroup> a2lGrpMap = new HashMap<>();

  /** A2LResponsibility instance. */
  private A2lResp a2lResp;

  /**
   * The wp grp resp map. <br>
   * key - a2lWpResp Name . Value - WpRespEnum
   */
  private Map<String, WpRespType> wpGrpRespMap = new HashMap<>();

  /** The a 2 l wp resp map. */
  Map<Long, A2LWpRespExt> a2lWpRespMap = new HashMap<>();
  /**
   * The wp Resp map. <br>
   * key - WpResp Id Name . Value - WpResp
   */
  private Map<Long, WpResp> wpRespMap = new HashMap<>();

  /**
   * A2Lgroup list
   */
  private List<A2LGroup> a2lGroupList = new ArrayList<>();
  /**
   * Flag to check If the Assignment SW2CAL has a attribute value for which a valid FC2WP is not defined
   */
  private boolean isFC2WPConfigMissingError;
  /**
   * Flag to check If the workpackage related attribute(Workpackage Type,SW2CAL Root Group,Division Attribute) has been
   * set
   */
  private boolean isWPAttrMissingError;
  /**
   * Error message If a2l WorkPackage Mapping Fails
   */
  private String errorMessage;

  /**
   * label map of the A2L group mapped to A2lWpResp. <br>
   * key - a2lWpResp Id. Value - A2lGroupLabelMap
   */
  private Map<Long, Map<String, String>> a2lWpRespGrpLabelMap = new ConcurrentHashMap<>();


  /**
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * @param errorMessage the errorMessage to set
   */
  public void setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Gets the work package group map.
   *
   * @return the workPackageGroupMap
   */
  public Map<String, A2lWorkPackageGroup> getWorkPackageGroupMap() {
    return this.workPackageGroupMap;
  }


  /**
   * Gets the wp map.
   *
   * @return the wpMap
   */
  public Map<Long, A2lWpObj> getWpMap() {
    return this.wpMap;
  }


  /**
   * Gets the a 2 l grp map.
   *
   * @return the a2lGrpMap
   */
  public Map<String, A2LGroup> getA2lGrpMap() {
    return this.a2lGrpMap;
  }


  /**
   * Gets the work package list.
   *
   * @return the workPackageList
   */
  public List<A2lWpObj> getWorkPackageList() {
    return new ArrayList<>(this.wpMap.values());
  }


  /**
   * Sets the work package group map.
   *
   * @param workPackageGroupMap the workPackageGroupMap to set
   */
  public void setWorkPackageGroupMap(final Map<String, A2lWorkPackageGroup> workPackageGroupMap) {
    this.workPackageGroupMap = workPackageGroupMap;
  }


  /**
   * Sets the wp map.
   *
   * @param wpMap the wpMap to set
   */
  public void setWpMap(final Map<Long, A2lWpObj> wpMap) {
    this.wpMap = wpMap;
  }


  /**
   * Sets the A 2 l grp map.
   *
   * @param a2lGrpMap the a2lGrpMap to set
   */
  public void setA2lGrpMap(final Map<String, A2LGroup> a2lGrpMap) {
    this.a2lGrpMap = a2lGrpMap;
  }


  /**
   * Gets the mapping source id.
   *
   * @return the mappingSourceId
   */
  public Long getMappingSourceId() {
    return this.mappingSourceId;
  }


  /**
   * Sets the mapping source id.
   *
   * @param mappingSourceId the mappingSourceId to set
   */
  public void setMappingSourceId(final Long mappingSourceId) {
    this.mappingSourceId = mappingSourceId;
  }


  /**
   * Gets the div attr value id.
   *
   * @return the divAttrValueId
   */
  public Long getDivAttrValueId() {
    return this.divAttrValueId;
  }


  /**
   * Sets the div attr value id.
   *
   * @param divAttrValueId the divAttrValueId to set
   */
  public void setDivAttrValueId(final Long divAttrValueId) {
    this.divAttrValueId = divAttrValueId;
  }


  /**
   * Gets the wp type attr value id.
   *
   * @return the wpTypeAttrValueId
   */
  public Long getWpTypeAttrValueId() {
    return this.wpTypeAttrValueId;
  }


  /**
   * Sets the wp type attr value id.
   *
   * @param wpTypeAttrValueId the wpTypeAttrValueId to set
   */
  public void setWpTypeAttrValueId(final Long wpTypeAttrValueId) {
    this.wpTypeAttrValueId = wpTypeAttrValueId;
  }


  /**
   * Gets the a 2 l group list.
   *
   * @return the a2lGroupList
   */
  public List<A2LGroup> getA2lGroupList() {
    return this.a2lGroupList;
  }

  /**
   * Gets the wp root group name.
   *
   * @return the wpRootGroupName
   */
  public String getWpRootGroupName() {
    return this.wpRootGroupName;
  }


  /**
   * Sets the wp root group name.
   *
   * @param wpRootGroupName the wpRootGroupName to set
   */
  public void setWpRootGroupName(final String wpRootGroupName) {
    this.wpRootGroupName = wpRootGroupName;
  }


  /**
   * Gets the wp root grp attr value id.
   *
   * @return the wpRootGrpAttrValueId
   */
  public Long getWpRootGrpAttrValueId() {
    return this.wpRootGrpAttrValueId;
  }


  /**
   * Sets the wp root grp attr value id.
   *
   * @param wpRootGrpAttrValueId the wpRootGrpAttrValueId to set
   */
  public void setWpRootGrpAttrValueId(final Long wpRootGrpAttrValueId) {
    this.wpRootGrpAttrValueId = wpRootGrpAttrValueId;
  }


  /**
   * Gets the group mapping id.
   *
   * @return the groupMappingId
   */
  public Long getGroupMappingId() {
    return this.groupMappingId;
  }


  /**
   * Sets the group mapping id.
   *
   * @param groupMappingId the groupMappingId to set
   */
  public void setGroupMappingId(final Long groupMappingId) {
    this.groupMappingId = groupMappingId;
  }

  /**
   * Gets the a 2 l resp.
   *
   * @return the a2lResp
   */
  public A2lResp getA2lResp() {
    return this.a2lResp;
  }

  /**
   * Sets the a 2 l resp.
   *
   * @param a2lResp the a2lResp to set
   */
  public void setA2lResp(final A2lResp a2lResp) {
    this.a2lResp = a2lResp;
  }

  /**
   * Gets the wp grp resp map.
   *
   * @return the wpGrpRespMap
   */
  public Map<String, WpRespType> getWpGrpRespMap() {
    return this.wpGrpRespMap;
  }

  /**
   * Gets the a 2 l wp resp map.
   *
   * @return the a2lWpRespMap
   */
  public Map<Long, A2LWpRespExt> getA2lWpRespMap() {
    return this.a2lWpRespMap;
  }

  /**
   * Sets the A 2 l wp resp map.
   *
   * @param a2lWpRespMap the a2lWpRespMap to set
   */
  public void setA2lWpRespMap(final Map<Long, A2LWpRespExt> a2lWpRespMap) {
    this.a2lWpRespMap = a2lWpRespMap;
  }

  /**
   * @return the wsRespMap
   */
  public Map<Long, WpResp> getWpRespMap() {
    return this.wpRespMap;
  }

  /**
   * @param wpGrpRespMap the wpGrpRespMap to set
   */
  public void setWpGrpRespMap(final Map<String, WpRespType> wpGrpRespMap) {
    this.wpGrpRespMap = wpGrpRespMap;
  }

  /**
   * @param wpRespMap the wpRespMap to set
   */
  public void setWpRespMap(final Map<Long, WpResp> wpRespMap) {
    this.wpRespMap = wpRespMap;
  }


  /**
   * @param a2lGroupList the a2lGroupList to set
   */
  public void setA2lGroupList(final List<A2LGroup> a2lGroupList) {
    if (a2lGroupList != null) {
      this.a2lGroupList = new ArrayList<>(a2lGroupList);
    }
  }


  /**
   * @return the a2lWpRespGrpLabelMap
   */
  public Map<Long, Map<String, String>> getA2lWpRespGrpLabelMap() {
    return this.a2lWpRespGrpLabelMap;
  }


  /**
   * @param a2lWpRespGrpLabelMap the a2lWpRespGrpLabelMap to set
   */
  public void setA2lWpRespGrpLabelMap(final Map<Long, Map<String, String>> a2lWpRespGrpLabelMap) {
    this.a2lWpRespGrpLabelMap = a2lWpRespGrpLabelMap;
  }

  /**
   * @return the isFC2WPConfigMissingError
   */
  public boolean isFC2WPConfigMissingError() {
    return this.isFC2WPConfigMissingError;
  }


  /**
   * @param isFC2WPConfigMissingError the isFC2WPConfigMissingError to set
   */
  public void setFC2WPConfigMissingError(final boolean isFC2WPConfigMissingError) {
    this.isFC2WPConfigMissingError = isFC2WPConfigMissingError;
  }


  /**
   * @return the isWPAttrMissingError
   */
  public boolean isWPAttrMissingError() {
    return this.isWPAttrMissingError;
  }

  /**
   * @param isWPAttrMissingError the isWPAttrMissingError to set
   */
  public void setWPAttrMissingError(final boolean isWPAttrMissingError) {
    this.isWPAttrMissingError = isWPAttrMissingError;
  }
}
