/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.List;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents the attribute value dependency
 */
public class AttrValueDependency extends ApicObject implements Comparable<AttrValueDependency> {

  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Dependency attribute column
                            */
                           SORT_ATTR_VAL_DEPEN_NAME,
                           /**
                            * Dependency value column
                            */
                           SORT_ATTR_VAL_DEPEN_VAL
  }

  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param dependencyID ID
   */
  public AttrValueDependency(final ApicDataProvider apicDataProvider, final Long dependencyID) {
    super(apicDataProvider, dependencyID);
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
   * @return Dependency attribute
   */
  public Attribute getDependencyAttribute() {
    return getDataCache().getAttribute(getEntityProvider().getDbDependency(getID()).getTabvAttributeD().getAttrId());
  }

  /**
   * @return true if the dependency is deleted
   */
  public boolean isDeleted() {
    if (dependencyIdValid()) {
      return getEntityProvider().getDbDependency(getID()).getDeletedFlag().equals(ApicConstants.YES);
    }
    return false;
  }

  /**
   * @return Dependency value
   */
  public AttributeValue getDependencyValue() {

    if (getDependencyValueID() == null) {
      return null;
    }
    return getDataCache().getAttrValue(getDependencyValueID());

  }

  /**
   * @return Dependency value in text form
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
   * @return the attribute Value
   */
  public AttributeValue getAttrValue() {
    if (getDependencyValueID() == null) {
      return null;
    }
    return getDataCache().getAttrValue(getDependencyValueID());
  }

  /**
   * @return id of dependency value
   */
  public Long getDependencyValueID() {
    if (getEntityProvider().getDbDependency(getID()).getTabvAttrValueD() == null) {
      return null;
    }
    return getEntityProvider().getDbDependency(getID()).getTabvAttrValueD().getValueId();
  }

  /**
   * ICDM-341
   *
   * @return value of the attribute
   */
  public AttributeValue getValue() {

    if (getValueID() == null) {
      return null;
    }
    return getDataCache().getAttrValue(getValueID());

  }

  /**
   * @return id of the value
   */
  public Long getValueID() {
    if (getEntityProvider().getDbDependency(getID()).getTabvAttrValue() == null) {
      return null;
    }
    return getEntityProvider().getDbDependency(getID()).getTabvAttrValue().getValueId();
  }

  private boolean dependencyIdValid() {
    return getEntityProvider().getDbDependency(getID()) != null;
  }

  /**
   * @return the dependencyID
   */
  public Long getDependencyID() {
    return getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttrValueDependency arg0) {
    return ApicUtil.compare(getDependencyAttribute().getAttributeName(),
        arg0.getDependencyAttribute().getAttributeName());
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
   * Compare objects with sort column
   *
   * @param arg0 other
   * @param sortColumn sort column
   * @return int
   */
  public int compareTo(final AttrValueDependency arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_VAL_DEPEN_VAL:
        compareResult = getDependencyValue().compareTo(arg0.getDependencyValue());
        break;
      case SORT_ATTR_VAL_DEPEN_NAME: // Value dependency attribute name needs not to be compared because it is the
                                     // default sort
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
   * @return the modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbDependency(getID()).getModifiedUser();
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
   * ICDM-341 This method checks whether the value dependency can be undeleted
   *
   * @return true/false
   */
  public boolean isUnDeleteAllowed() {

    AttributeValue attrVal = getValue();
    List<AttrValueDependency> listValDepn = attrVal.getValueDependencies(false);
    for (AttrValueDependency valDependency : listValDepn) {
      // If any other valid dependency exists with another attribute then undelete is not allowed.
      if (!valDependency.getDependencyAttribute().getAttributeName()
          .equals(getDependencyAttribute().getAttributeName())) {
        return false;
      }
      // If any other valid dependency exists based on used flag then undelete is not allowed.
      if (valDependency.getDependencyValue() == null) {
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
    return getDependencyAttribute().getAttributeName();
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
