/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.wp.WorkPkg;


/**
 * The Class A2LWpRespExt.
 */
public class A2LWpRespExt implements Comparable<A2LWpRespExt> {

  /**
   * Field declared to get a2l id of copied a2l wp responsibility object and this id will be used to check whether a2l
   * files are same or different while using paste responsibility action
   */
  private Long a2lId;


  /**
   * wp map for the wp group. <br>
   * key - workpackage Name, value - workpackage Number
   */
  private Map<String, String> wpNumMap = new ConcurrentHashMap<>();

  /** The name. */
  private String name;

  /** The a 2 l wp resp. */
  private A2lWpResp a2lWpResp;

  /** The icdm A 2 l group. */
  private IcdmA2lGroup icdmA2lGroup;

  /** The work pkg. */
  private WorkPkg workPackage;

  /** The wp resource. */
  private String wpResource;

  /** The is work pkg. */
  private boolean isWorkPkg;

  /** The is A 2 l grp. */
  private boolean isA2lGrp;

  /** The div attr value id. */
  private Long divAttrValueId;


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Gets the icdm A 2 l group.
   *
   * @return the Icdm A2l Group
   */
  public IcdmA2lGroup getIcdmA2lGroup() {
    return this.icdmA2lGroup;
  }

  /**
   * Checks if is work pkg.
   *
   * @return true if a2l grp is null.
   */
  public boolean isWorkPkg() {
    this.isWorkPkg = (getIcdmA2lGroup() == null);
    return this.isWorkPkg;
  }

  /**
   * Checks if is a 2 l grp.
   *
   * @return true if work pkg is null.
   */
  public boolean isA2lGrp() {
    this.isA2lGrp = (getIcdmA2lGroup() != null);
    return this.isA2lGrp;
  }


  /**
   * Gets the wp resource.
   *
   * @return the wp group.
   */
  public String getWpResource() {
    return this.wpResource;
  }

  /**
   * Gets the a 2 l wp resp.
   *
   * @return the a2lWpResp
   */
  public A2lWpResp getA2lWpResp() {
    return this.a2lWpResp;
  }


  /**
   * Sets the a 2 l wp resp.
   *
   * @param a2lWpResp the a2lWpResp to set
   */
  public void setA2lWpResp(final A2lWpResp a2lWpResp) {
    this.a2lWpResp = a2lWpResp;
  }

  /**
   * Gets the work package.
   *
   * @return the workPackage
   */
  public WorkPkg getWorkPackage() {
    return this.workPackage;
  }


  /**
   * Sets the work package.
   *
   * @param workPackage the workPackage to set
   */
  public void setWorkPackage(final WorkPkg workPackage) {
    this.workPackage = workPackage;
  }


  /**
   * Gets the wp map.
   *
   * @return the wpMap
   */
  public Map<String, String> getWpNumMap() {
    return this.wpNumMap;
  }


  /**
   * Sets the icdm A 2 l group.
   *
   * @param icdmA2lGroup the icdmA2lGroup to set
   */
  public void setIcdmA2lGroup(final IcdmA2lGroup icdmA2lGroup) {
    this.icdmA2lGroup = icdmA2lGroup;
  }


  /**
   * Sets the wp resource.
   *
   * @param wpResource the wpResource to set
   */
  public void setWpResource(final String wpResource) {
    this.wpResource = wpResource;
  }


  /**
   * Sets the work pkg.
   *
   * @param isWorkPkg the isWorkPkg to set
   */
  public void setWorkPkg(final boolean isWorkPkg) {
    this.isWorkPkg = isWorkPkg;
  }


  /**
   * Sets the a 2 l grp.
   *
   * @param isA2lGrp the isA2lGrp to set
   */
  public void setA2lGrp(final boolean isA2lGrp) {
    this.isA2lGrp = isA2lGrp;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LWpRespExt obj) {
    return this.a2lWpResp.compareTo(obj.getA2lWpResp());
  }

  /**
   * Sets the div attr value id.
   *
   * @param divAttrValueId the new div attr value id
   */
  public void setDivAttrValueId(final Long divAttrValueId) {
    this.divAttrValueId = divAttrValueId;
  }

  /**
   * Gets the div attr value id.
   *
   * @return the div attr value id
   */
  public Long getDivAttrValueId() {
    return this.divAttrValueId;
  }

  /**
   * Gets the workpackage map.
   *
   * @return the work package map
   */
  public Map<String, String> getWorkpackageMap() {
    return this.wpNumMap;
  }


  /**
   * @param wpNumMap the wpNumMap to set
   */
  public void setWpNumMap(final Map<String, String> wpNumMap) {
    this.wpNumMap = wpNumMap;
  }

  /**
   * @return the a2lId
   */
  public Long getA2lId() {
    return this.a2lId;
  }


  /**
   * @param a2lId the a2lId to set
   */
  public void setA2lId(final Long a2lId) {
    this.a2lId = a2lId;
  }
}
