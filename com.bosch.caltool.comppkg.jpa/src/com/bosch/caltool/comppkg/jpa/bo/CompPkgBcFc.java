/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * Icdm-949 isIDValidMethod is removed Component package-BC's FC
 *
 * @author adn1cob
 */
@Deprecated
public class CompPkgBcFc extends AbstractCPObject implements Comparable<CompPkgBcFc> {

  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Function Name
                            */
                           SORT_NAME,
                           /**
                            * Parameter Name
                            */
                           SORT_LONG_NAME,

  }

  /**
   * @param dataProvider data provider
   * @param objID primary key
   */
  protected CompPkgBcFc(final CPDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllCompPkgBcFcs().put(objID, this);
  }

  /**
   * Get the FC name
   *
   * @return fc name
   */
  public String getFcName() {
    return getEntityProvider().getDbCompPkgBcFc(getID()).getFcName();
  }


  /**
   * @return created date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkgBcFc(getID()).getCreatedDate());

  }

  /**
   * @return created user
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbCompPkgBcFc(getID()).getCreatedUser();

  }

  /**
   * @return modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkgBcFc(getID()).getModifiedDate());

  }

  /**
   * @return modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbCompPkgBcFc(getID()).getModifiedUser();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbCompPkgBcFc(getID()).getVersion();

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
  public int compareTo(final CompPkgBcFc other) {
    return ApicUtil.compare(getFcName(), other.getFcName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getFcName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CPEntityType.COMP_PKG_BC_FC;
  }


  /**
   * @param param2 parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final CompPkgBcFc param2, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        // TODO change to long name
      case SORT_LONG_NAME:
        // comparing the BC names
        compareResult = ApicUtil.compare(getName(), param2.getName());
        break;
      default:
        // Compare name
        compareResult = compareTo(param2);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the parameter name
      compareResult = compareTo(param2);
    }

    return compareResult;
  }

  /**
   * Getter for parent CompPkgBc
   *
   * @return CompPkgBc
   */
  public CompPkgBc getCompPkgBC() {

    CompPkgBc compPkgBc =
        getDataCache().getCompPkgBc(getEntityProvider().getDbCompPkgBcFc(getID()).getTCompPkgBc().getCompBcId());
    return compPkgBc;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getFcName();
  }
}
