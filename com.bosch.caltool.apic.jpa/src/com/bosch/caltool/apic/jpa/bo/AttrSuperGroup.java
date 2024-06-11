/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents an Attribute Super Group as stored in the database table TabV_ATTR_SUPER_GROUPS
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AttrSuperGroup extends ApicObject implements Comparable<AttrSuperGroup> {

  private SortedSet<Attribute> attributesList;
  private SortedSet<AttrGroup> groupList;

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param superGroupID ID
   */
  public AttrSuperGroup(final ApicDataProvider apicDataProvider, final Long superGroupID) {
    super(apicDataProvider, superGroupID);
  }

  /**
   * @return true if the super group id is not null
   */
  private boolean superGroupIdValid() {
    return getEntityProvider().getDbSuperGroup(getID()) != null;
  }

  /**
   * {@inheritDoc} return true if the current user has APIC_WRITE access
   */
  @Override
  public boolean isModifiable() {

    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * @return set of all attribute groups
   */
  public SortedSet<AttrGroup> getGroups() {

    synchronized (this) {
      if (this.groupList == null) {
        this.groupList = Collections.synchronizedSortedSet(new TreeSet<AttrGroup>());

        for (TabvAttrGroup dbGroup : getEntityProvider().getDbSuperGroup(getID()).getTabvAttrGroups()) {
          this.groupList.add(getDataCache().getGroup(dbGroup.getGroupId()));

        }
      }
    }
    return this.groupList;
  }

  /**
   * @return set of all attributes
   */
  public SortedSet<Attribute> getAllAttributes() {
    synchronized (this) {
      if (this.attributesList == null) {
        this.attributesList = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

        for (AttrGroup group : getGroups()) {
          this.attributesList.addAll(group.getAllAttributes());
        }
      }
    }
    return this.attributesList;
  }

  /**
   * @return name of the super group
   */
  @Override
  public String getName() {
    if (superGroupIdValid()) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getSuperGroupNameEng(), getSuperGroupNameGer(),
          ApicConstants.EMPTY_STRING);
    }
    return "";
  }

  /**
   * @return description of the super group
   */
  public String getDesc() {
    if (superGroupIdValid()) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getSuperGroupDescEng(), getSuperGroupDescEng(),
          ApicConstants.EMPTY_STRING);
    }
    return "";
  }

  /**
   * {@inheritDoc} return compare result of super group names
   */
  @Override
  public int compareTo(final AttrSuperGroup arg0) {

    return ApicUtil.compare(getName(), arg0.getName());
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
   * @return the id of the super group
   */
  public Long getSuperGroupID() {
    return getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbSuperGroup(getSuperGroupID()).getVersion();
  }

  /**
   * @return English name of the super group
   */
  public String getSuperGroupNameEng() {
    String returnValue = getEntityProvider().getDbSuperGroup(getSuperGroupID()).getSuperGroupNameEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return English description of the super group
   */
  public String getSuperGroupDescEng() {
    // Get super group's English description
    String returnValue = getEntityProvider().getDbSuperGroup(getSuperGroupID()).getSuperGroupDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return German name of the super group
   */
  public String getSuperGroupNameGer() {
    // Get super group's German Name
    String returnValue = getEntityProvider().getDbSuperGroup(getSuperGroupID()).getSuperGroupNameGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return German description of super group
   */
  public String getSuperGroupDescGer() {
    // Get super group's German Description
    String returnValue = getEntityProvider().getDbSuperGroup(getSuperGroupID()).getSuperGroupDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return summary of data icdm-235
   */
  @Override
  public Map<String, String> getObjectDetails() {
    Map<String, String> summaryMap = new HashMap<String, String>();

    // Name, description, deleted flag etc. is not added to the map as this is available in the attribute value
    summaryMap.put("NAME_ENG", String.valueOf(getSuperGroupNameEng()));
    summaryMap.put("DESC_ENG", String.valueOf(getSuperGroupDescEng()));
    summaryMap.put("NAME_GERMAN", String.valueOf(getSuperGroupNameGer()));
    summaryMap.put("DESC_GERMAN", String.valueOf(getSuperGroupDescGer()));
    return summaryMap;
  }

  /**
   * Gets the created date
   *
   * @return Calendar
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbSuperGroup(getID()).getCreatedDate());
  }

  /**
   * Gets the created user
   *
   * @return String
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbSuperGroup(getID()).getCreatedUser();
  }

  /**
   * Gets the modified date
   *
   * @return Calendar
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbSuperGroup(getID()).getModifiedDate());
  }

  /**
   * Gets the modified user
   *
   * @return modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbSuperGroup(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.SUPER_GROUP;
  }

  /**
   * ICDM-929
   *
   * @return SortedSet of links
   */
  public SortedSet<Link> getLinks() {
    return getDataCache().getLinks(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getSuperGroupDescEng(), getSuperGroupDescGer(),
        ApicConstants.EMPTY_STRING);
  }

}