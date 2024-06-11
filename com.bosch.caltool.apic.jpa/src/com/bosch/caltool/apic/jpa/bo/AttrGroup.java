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
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents an APIC user as stored in the database table TABV_ATTR_GROUPS
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AttrGroup extends ApicObject implements Comparable<AttrGroup> {

  /**
   * Set of attributes
   */
  private SortedSet<Attribute> attributesList;

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param groupID group id
   */
  public AttrGroup(final ApicDataProvider apicDataProvider, final Long groupID) {
    super(apicDataProvider, groupID);
  }

  /**
   * @return true if the group id is not null
   */
  private boolean groupIdValid() {
    return getEntityProvider().getDbGroup(getID()) != null;
  }

  /**
   * {@inheritDoc} return true if the user has APIC_WRITE access
   */
  @Override
  public boolean isModifiable() {
    // Attr group can be modified if the current user has APIC_WRITE access
    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * @return sorted set of all attributes under the group
   */
  public SortedSet<Attribute> getAllAttributes() {
    synchronized (this) {
      if (this.attributesList == null) {
        this.attributesList = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

        for (TabvAttribute dbAttribute : getEntityProvider().getDbGroup(getID()).getTabvAttributes()) {
          this.attributesList.add(getDataCache().getAttribute(dbAttribute.getAttrId()));

        }
      }
    }
    return this.attributesList;
  }

  /**
   * @return the name of the attribute group
   */
  @Override
  public String getName() {
    if (groupIdValid()) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getGroupNameEng(), getGroupNameGer(),
          ApicConstants.EMPTY_STRING);
    }
    return "";
  }

  /**
   * @return the description of the attribute group
   */
  public String getDesc() {
    if (groupIdValid()) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getGroupDescEng(), getGroupDescGer(),
          ApicConstants.EMPTY_STRING);
    }
    return "";
  }

  /**
   * @return the super group id
   */
  public Long getSuperGroupID() {
    return getEntityProvider().getDbGroup(getID()).getTabvAttrSuperGroup().getSuperGroupId();
  }

  /**
   * @return the super group object
   */
  public AttrSuperGroup getSuperGroup() {
    return getDataCache().getSuperGroup(getSuperGroupID());
  }

  /**
   * {@inheritDoc} returns the compare value of the group names
   */
  @Override
  public int compareTo(final AttrGroup arg0) {

    return ApicUtil.compare(getName(), arg0.getName());
  }

  /**
   * @return the id of the attribute group
   */
  public Long getGroupID() {
    return getID();
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
  public Long getVersion() {
    return getEntityProvider().getDbGroup(getGroupID()).getVersion();
  }

  /**
   * @return group name in english
   */
  public String getGroupNameEng() {
    String returnValue = getEntityProvider().getDbGroup(getGroupID()).getGroupNameEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return group description in english
   */
  public String getGroupDescEng() {
    String returnValue = getEntityProvider().getDbGroup(getGroupID()).getGroupDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return group name in german
   */
  public String getGroupNameGer() {
    String returnValue = getEntityProvider().getDbGroup(getGroupID()).getGroupNameGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return group description in german
   */
  public String getGroupDescGer() {
    String returnValue = getEntityProvider().getDbGroup(getGroupID()).getGroupDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * @return modified user of the attribute group
   */
  public String getAttrGroupModifiedUser() {
    String modifiedUser = "";
    if (attrIdValid()) {
      modifiedUser = getEntityProvider().getDbGroup(getID()).getModifiedUser();
    }
    return modifiedUser;
  }

  /**
   * @return true if the group is valid
   */
  private boolean attrIdValid() {
    return getEntityProvider().getDbGroup(getID()) != null;
  }

  /**
   * @return summary of data icdm-235
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> summaryMap = new HashMap<String, String>();

    // Name, description, deleted flag etc. is not added to the map as this is available in the attribute value
    summaryMap.put("NAME_ENG", getGroupNameEng());
    summaryMap.put("DESC_ENG", getGroupDescEng());
    summaryMap.put("NAME_GERMAN", getGroupNameGer());
    summaryMap.put("DESC_GERMAN", getGroupDescEng());
    return summaryMap;
  }

  /**
   * Gets the created date
   *
   * @return Calendar
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbGroup(getID()).getCreatedDate());
  }

  /**
   * Gets the created user
   *
   * @return String
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbGroup(getID()).getCreatedUser();
  }

  /**
   * Gets the modified date
   *
   * @return Calendar
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbGroup(getID()).getModifiedDate());
  }

  /**
   * Gets the modified user
   *
   * @return modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbGroup(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.GROUP;
  }

  /**
   * ICDM-930
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
    return getDesc();
  }
}