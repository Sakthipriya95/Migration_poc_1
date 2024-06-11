/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.wp;

import java.util.Map;
import java.util.TreeMap;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bne4cob
 */
public class WorkPkg implements IDataObject, Comparable<WorkPkg> {

  /**
   *
   */
  private static final long serialVersionUID = 4327235334633248192L;

  private Long id;

  private String name;
  private String wpNameEng;
  private String wpNameGer;

  private String description;
  private String wpDescEng;
  private String wpDescGer;

  private Long wpMasterlistId;

  private boolean deleted;

  private final Map<String, String> functionMap = new TreeMap<>();
  private Long version;

  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;

  /**
   * Columns for advanced sorting
   */
  public enum SortColumns {
                           /**
                            * fc2wp name
                            */
                           SORT_NAME,
                           /**
                            * division Name
                            */
                           SORT_DESC,
                           /**
                            * Group Name column
                            */
                           SORT_WP_GROUP_NAME,
                           /**
                            * Work Package Name
                            */
                           SORT_WP_NAME,
                           /**
                            * Work Package Number
                            */
                           SORT_WP_NUMBER

  }

  /**
   * @return the wpId
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param wpId the wpId to set
   */
  @Override
  public void setId(final Long wpId) {
    this.id = wpId;
  }


  /**
   * {@inheritDoc}
   *
   * @return the wpName
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * {@inheritDoc}
   *
   * @param name the wpName to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the wpNameEng
   */
  public String getWpNameEng() {
    return this.wpNameEng;
  }


  /**
   * @param wpNameEng the wpNameEng to set
   */
  public void setWpNameEng(final String wpNameEng) {
    this.wpNameEng = wpNameEng;
  }


  /**
   * @return the wpNameGer
   */
  public String getWpNameGer() {
    return this.wpNameGer;
  }


  /**
   * @param wpNameGer the wpNameGer to set
   */
  public void setWpNameGer(final String wpNameGer) {
    this.wpNameGer = wpNameGer;
  }


  /**
   * {@inheritDoc}
   *
   * @return the wpDesc
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * {@inheritDoc}
   *
   * @param description the wpDesc to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the wpDescEng
   */
  public String getWpDescEng() {
    return this.wpDescEng;
  }


  /**
   * @param wpDescEng the wpDescEng to set
   */
  public void setWpDescEng(final String wpDescEng) {
    this.wpDescEng = wpDescEng;
  }


  /**
   * @return the wpDescGer
   */
  public String getWpDescGer() {
    return this.wpDescGer;
  }


  /**
   * @param wpDescGer the wpDescGer to set
   */
  public void setWpDescGer(final String wpDescGer) {
    this.wpDescGer = wpDescGer;
  }


  /**
   * @return the wpMasterlistId
   */
  public Long getWpMasterlistId() {
    return this.wpMasterlistId;
  }


  /**
   * @param wpMasterlistId the wpMasterlistId to set
   */
  public void setWpMasterlistId(final Long wpMasterlistId) {
    this.wpMasterlistId = wpMasterlistId;
  }


  /**
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * Compares this wp with another wp based on column to sort
   *
   * @param wp2 the second wp
   * @param sortColumn column selected for sorting
   * @return the int value based on String.compare() method
   */
  public int compareTo(final WorkPkg wp2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        // compare the user IDs
        compareResult = ModelUtil.compare(getName(), wp2.getName());
        break;

      case SORT_DESC:
        // compare the first names
        compareResult = ModelUtil.compare(getDescription(), wp2.getDescription());
        break;


      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the last name
      compareResult = ModelUtil.compare(getDescription(), wp2.getDescription());
    }

    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WorkPkg wp) {
    return compareWpName(wp);
  }

  /**
   * @param wp WorkPackage
   * @return compare result based on sort column
   */
  public int compareWpName(final WorkPkg wp) {
    int compareResult;
    compareResult = ModelUtil.compare(getName(), wp.getName());
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getId(), wp.getId());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WorkPkg other = (WorkPkg) obj;

    return ModelUtil.isEqual(getId(), other.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WorkPkg clone() throws CloneNotSupportedException {
    return (WorkPkg) super.clone();
  }


  /**
   * @return the functionMap
   */
  public Map<String, String> getFunctionMap() {
    return this.functionMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String user) {
    this.createdUser = user;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String date) {
    this.createdDate = date;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String date) {
    this.modifiedDate = date;
  }

}
