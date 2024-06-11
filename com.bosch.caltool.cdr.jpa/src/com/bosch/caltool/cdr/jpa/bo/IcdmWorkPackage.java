/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author bru2cob
 */
/**
 * @author rgo7cob
 */
public class IcdmWorkPackage extends AbstractCdrObject implements Comparable<IcdmWorkPackage> {

  /**
   * Sort columns
   */
  public enum SortColumns {
                           SORT_WP_GROUP,
                           SORT_WP_NAME
  }

  /**
   * @param cdrDataProvider
   * @param objID
   */
  public IcdmWorkPackage(final AbstractDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
    getDataCache().getIcdmWorkPackageMap().put(getID(), this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbIcdmWorkPackage(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbIcdmWorkPackage(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbIcdmWorkPackage(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbIcdmWorkPackage(getID()).getModifiedDate());
  }

  /**
   * @return
   */
  public String getNameEng() {
    return getEntityProvider().getDbIcdmWorkPackage(getID()).getWpNameE();
  }

  /**
   * @return
   */
  public String getNameGer() {
    return getEntityProvider().getDbIcdmWorkPackage(getID()).getWpNameG();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.ICDM_WORKPACKAGE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getNameEng(), getNameGer(), "");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IcdmWorkPackage icdmWp) {
    // compare with name
    int compResult = ApicUtil.compare(getName(), icdmWp.getName());
    // if equal compare with id
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), icdmWp.getID());
    }
    return compResult;
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
   * @param arg0 other
   * @param sortColumn sort column
   * @param division
   * @return compare result based on sort column
   */
  public int compareTo(final IcdmWorkPackage arg0, final SortColumns sortColumn, final AttributeValue division) { // NOPMD
                                                                                                                  // by
                                                                                                                  // bne4cob
                                                                                                                  // on
                                                                                                                  // 6/20/14
                                                                                                                  // 10:27
    // AM

    int compareResult;

    switch (sortColumn) {

      // wp name sort
      case SORT_WP_NAME:
        compareResult = ApicUtil.compare(getName(), arg0.getName());
        break;
      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }


  // Review 237201
  /**
   * @return true if Work Package Delete Flag is 'Y'
   */
  public boolean isWpDeleteFlag() {

    String deleteFlag = null == getEntityProvider().getDbIcdmWorkPackage(getID()).getDeleteFlag() ? ""
        : getEntityProvider().getDbIcdmWorkPackage(getID()).getDeleteFlag();

    if (deleteFlag.equalsIgnoreCase("Y")) {
      return true;
    }
    return false;
  }


}
