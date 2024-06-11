/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents an Attributes dependency as stored in TABV_ATTR_DEPENDENCIES database table
 */
public class AttrDependency extends ApicObject implements Comparable<AttrDependency> {

  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Dependency attribute
                            */
                           SORT_ATTR_DEPEN_NAME,
                           /**
                            * Dependency value
                            */
                           SORT_ATTR_DEPEN_VAL
  }

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param dependencyID primary key
   */
  public AttrDependency(final ApicDataProvider apicDataProvider, final Long dependencyID) {
    super(apicDataProvider, dependencyID);
  }

  /**
   * {@inheritDoc} returns true if the user has APIC_WRITE access
   */
  @Override
  public boolean isModifiable() {
    return getDataCache().getCurrentUser().hasApicWriteAccess();
  }

  /**
   * Gets the dependant attribute for this dependencyID
   *
   * @return Attribute
   */
  public Attribute getDependencyAttribute() {
    return getDataCache().getAttribute(getEntityProvider().getDbDependency(getID()).getTabvAttributeD().getAttrId());
  }

  /**
   * Gets the Attribute for which the dependency is created
   *
   * @return Attribute
   */
  public Attribute getAttribute() {
    final TabvAttribute tabvAttribute = getEntityProvider().getDbDependency(getID()).getTabvAttribute();
    if (tabvAttribute != null) {
      return getDataCache().getAttribute(tabvAttribute.getAttrId());
    }
    return null;
  }

  /**
   * Gets the AttributeValue for which the dependency is created
   *
   * @return AttributeValue
   */
  public AttributeValue getAttributeValue() {
    final TabvAttrValue tabvAttrValue = getEntityProvider().getDbDependency(getID()).getTabvAttrValue();
    if (tabvAttrValue != null) {
      return getDataCache().getAttrValue(tabvAttrValue.getValueId());
    }
    return null;
  }

  /**
   * Checks if this dependency is deleted
   *
   * @return whether this dependency is deleted
   */
  public boolean isDeleted() {
    if (dependencyIdValid()) {
      return getEntityProvider().getDbDependency(getID()).getDeletedFlag().equals(ApicConstants.YES);
    }
    return false;
  }

  /**
   * Gets the dependencyID
   *
   * @return the dependencyID
   */
  public Long getDependencyID() {
    return getID();
  }

  /**
   * Get the ID of the Value on which the dependency depends on
   *
   * @return the ID, if the Value is NULL, NULL will be returned
   */
  public Long getDependencyValueID() {
    final TabvAttrValue dbDependencyValue = getEntityProvider().getDbDependency(getID()).getTabvAttrValueD();

    if (dbDependencyValue == null) {
      return null;
    }
    return dbDependencyValue.getValueId();
  }

  /**
   * @return attribute dependency value
   */
  public AttributeValue getDependencyValue() {

    if (getDependencyValueID() == null) {
      return null;
    }
    return getDataCache().getAttrValue(getDependencyValueID());
  }

  /**
   * @return attribute dependency value in text
   */
  public String getDependencyValueText() {
    if (getDependencyValueID() == null) {
      return "<USED FLAG = TRUE>";
    }
    return getDataCache().getAttrValue(getDependencyValueID()).getValue();
  }

  /**
   * Icdm-590
   *
   * @return the attr value of the dependency
   */
  public AttributeValue getAttrValue() {
    if (getDependencyValueID() == null) {
      return null;
    }
    return getDataCache().getAttrValue(getDependencyValueID());
  }

  /**
   * @return true if the dependency id is not null
   */
  private boolean dependencyIdValid() {
    return getEntityProvider().getDbDependency(getID()) != null;
  }

  /**
   * {@inheritDoc} returns the compare value of attribute dependency names
   */
  @Override
  public int compareTo(final AttrDependency arg0) {
    return ApicUtil.compare(getDependencyAttribute().getAttributeName(),
        arg0.getDependencyAttribute().getAttributeName());
  }

  /**
   * Compare with sort column
   *
   * @param arg0 other
   * @param sortColumn sortColumn
   * @return int -1/0/1
   */
  public int compareTo(final AttrDependency arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_DEPEN_VAL:
        compareResult = getDependencyValue().compareTo(arg0.getDependencyValue());
        break;
      case SORT_ATTR_DEPEN_NAME: // dependency attribute name needs not to be compared because it is the default sort
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
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
   * Gets the created date
   *
   * @return Calendar
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbDependency(getID()).getCreatedDate());
  }

  /**
   * Gets the created user
   *
   * @return String
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbDependency(getID()).getCreatedUser();
  }

  /**
   * Gets the modified date
   *
   * @return Calendar
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbDependency(getID()).getModifiedDate());
  }

  /**
   * Gets the modified user
   *
   * @return modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbDependency(getID()).getModifiedUser();
  }

  /**
   * ICDM-341 This method checks whether the attr dependency can be undeleted
   *
   * @return true/false
   */
  public boolean isUnDeleteAllowed() {
    for (AttrDependency attrDependency : getAttribute().getAttrDependencies(false)) {
      // If any other valid dependency exists with another attribute then undelete is not allowed.
      if (!attrDependency.getDependencyAttribute().getAttributeName()
          .equals(getDependencyAttribute().getAttributeName())) {
        return false;
      }
      // If any other valid dependency exists based on used flag then undelete is not allowed.
      if (attrDependency.getDependencyValue() == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getDependencyAttribute().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getDependencyAttribute().getDescription();
  }


  // ICDM-1397
  /**
   * Returns the dependency change comment
   *
   * @return dependency change comment in String
   */
  public String getChangeComment() {
    String changeComment = getEntityProvider().getDbDependency(getID()).getChangeComment();
    return CommonUtils.checkNull(changeComment);
  }
}
