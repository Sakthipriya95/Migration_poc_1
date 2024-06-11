/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.text.MessageFormat;
import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author bru2cob
 */
public class WorkPackageDivision extends AbstractCdrObject implements Comparable<WorkPackageDivision> {

  /**
   * Name format of Workpackage division
   */
  private static final String WP_DIV_NAME_FORMAT = "{0} - {1}";

  /**
   * @param cdrDataProvider data provider
   * @param objID primary KEY
   */
  protected WorkPackageDivision(final AbstractDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
    getDataCache().getWrkPkgDivisionMap().put(getID(), this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbWrkPkgDiv(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbWrkPkgDiv(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbWrkPkgDiv(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbWrkPkgDiv(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.WORKPACKAGE_DIVISION;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return buildName(getWorkPackage().getName(), getDivisionName());
  }

  /**
   * @return English name including division name
   */
  public String getNameEng() {
    return buildName(getWPNameEng(), getDivisionName());
  }

  /**
   * @return @return German name including division name
   */
  public String getNameGer() {
    String wpNameGerman = getWPNameGerman();
    if (CommonUtils.isEmptyString(wpNameGerman)) {
      wpNameGerman = getNameEng();
    }
    return buildName(wpNameGerman, getDivisionName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getWorkPackage().getDescription();
  }

  /**
   * @return mapped workpackage id
   */
  public Long getWorkPackageId() {
    return getEntityProvider().getDbWrkPkgDiv(getID()).getTWorkpackage().getWpId();
  }

  /**
   * @return division name
   */
  public String getDivisionName() {
    return getDivision().getName();
  }

  /**
   * @return division
   */
  public AttributeValue getDivision() {
    return getDataProvider().getApicDataProvider()
        .getAttrValue(getEntityProvider().getDbWrkPkgDiv(getID()).getTabvAttrValue().getValueId());
  }

  /**
   * @return mapped workpackage
   */
  public IcdmWorkPackage getWorkPackage() {
    return getDataProvider().getIcdmWorkPackage(getWorkPackageId());
  }

  /**
   * @return workpackage name eng
   */
  public String getWPNameEng() {
    return getWorkPackage().getNameEng();
  }

  /**
   * @return workpackage name german
   */
  public String getWPNameGerman() {
    return getWorkPackage().getNameGer();
  }

  // Review 237201
  /**
   * @return mapped workpackage id
   */
  public boolean isWpDivDeleteFlag() {

    String deleteFlag = null == getEntityProvider().getDbWrkPkgDiv(getID()).getDeleteFlag() ? ""
        : getEntityProvider().getDbWrkPkgDiv(getID()).getDeleteFlag();

    if (deleteFlag.equalsIgnoreCase("Y")) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WorkPackageDivision wpDiv) {
    // compare using wp name

    int compResult = ApicUtil.compare(getWPNameEng(), wpDiv.getWPNameEng());
    // comapre using wp div name
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      int compResult1 = ApicUtil.compare(getName(), wpDiv.getName());
      // compare using wp div id
      if (compResult1 == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        return ApicUtil.compare(getID(), wpDiv.getID());
      }
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
   * Build the WP Division name.
   *
   * @param wpName name of WP
   * @param divName name of Division
   * @return formatted name
   */
  static String buildName(final String wpName, final String divName) {
    return MessageFormat.format(WP_DIV_NAME_FORMAT, wpName, divName);
  }

}
