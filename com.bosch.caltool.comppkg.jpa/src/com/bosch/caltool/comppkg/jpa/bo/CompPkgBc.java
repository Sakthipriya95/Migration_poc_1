/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;


/**
 * ICdm-949 isIDValid method is removed Component package's BC
 */
@Deprecated
public class CompPkgBc extends AbstractCPObject implements Comparable<CompPkgBc> {


  /**
   * The data provider
   */
  private final CPDataProvider cpDataProvider;

  /**
   * Defines map of FC's for this compPkg BC
   */
  private Map<Long, CompPkgBcFc> fcMap;
  /**
   * Display text indicatiog mutiple fc's are mapped for this bc
   */
  public static final String MULTI_FC_MAPPING = "<PARTIAL>";

  /**
   * Display text indicatiog all fc's are mapped for this bc
   */
  public static final String ALL_FC_MAPPING = "<ALL>";

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
   * BC name key
   */
  private static final String BC_NAME = "BC_NAME";

  /**
   * BC Sequence number key
   */
  private static final String BC_SEQ_NO = "BC_SEQ_NO";

  /**
   * @param dataProvider dataProvider
   * @param objID primary key
   */
  protected CompPkgBc(final CPDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    this.cpDataProvider = dataProvider;
    this.cpDataProvider.getDataCache().getAllCompPkgBcs().put(objID, this);
  }

  /**
   * Get the BC name
   *
   * @return bc name
   */
  public String getBcName() {
    return getEntityProvider().getDbCompPkgBc(getID()).getBcName();
  }

  /**
   * @return created date
   */
  @Override
  public Calendar getCreatedDate() {

    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkgBc(getID()).getCreatedDate());


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final ConcurrentMap<String, String> objDetails = new ConcurrentHashMap<String, String>();

    objDetails.put(BC_NAME, CommonUtils.checkNull(getBcName()));
    objDetails.put(BC_SEQ_NO, CommonUtils.checkNull(String.valueOf(getBcSeqNo())));
    return objDetails;
  }

  /**
   * @return created user
   */
  @Override
  public String getCreatedUser() {

    return getEntityProvider().getDbCompPkgBc(getID()).getCreatedUser();

  }

  /**
   * @return modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkgBc(getID()).getModifiedDate());

  }

  /**
   * @return modified user
   */
  @Override
  public String getModifiedUser() {

    return getEntityProvider().getDbCompPkgBc(getID()).getModifiedUser();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {

    return getEntityProvider().getDbCompPkgBc(getID()).getVersion();

  }

  /**
   * @return BC sequence number
   */
  public Long getBcSeqNo() {
    return getEntityProvider().getDbCompPkgBc(getID()).getBcSeqNo();
  }

  /**
   * Get the FCs mapped for this BC
   *
   * @return set of FC details
   */
  public SortedSet<CompPkgBcFc> getCompPkgBcFcs() {
    return new TreeSet<CompPkgBcFc>(getCompPkgBcFcsMap().values());
  }

  /**
   * Get the description from SDomBC
   *
   * @return description of bc
   */
  @Override
  public String getDescription() {

    final SdomBC bcObj = this.cpDataProvider.getDataCache().getAllBcs().get(getBcName());
    if (bcObj == null) {
      return "";
    }
    return bcObj.getDescription();

  }

  /**
   * @return Map of FC's Made the method as public for use in the Outline Filter
   */
  public Map<Long, CompPkgBcFc> getCompPkgBcFcsMap() {

    if (this.fcMap == null) {
      this.fcMap = new HashMap<Long, CompPkgBcFc>();
      final List<TCompPkgBcFc> fcList = getEntityProvider().getDbCompPkgBc(getID()).getTCompPkgBcFcs();
      if (fcList != null) {
        createCompBcFc(fcList);
      }
    }
    return this.fcMap;

  }

  /**
   * Icdm-949 sonar Qube
   *
   * @param fcList
   */
  private void createCompBcFc(final List<TCompPkgBcFc> fcList) {
    // NOPMD by adn1cob on 6/30/14 10:16 AM
    for (TCompPkgBcFc tCompPkgBcFc : fcList) {
      CompPkgBcFc compPkgBcFc = getDataCache().getCompPkgBcFc(tCompPkgBcFc.getCompBcFcId());
      if (compPkgBcFc == null) {
        compPkgBcFc = new CompPkgBcFc(this.cpDataProvider, tCompPkgBcFc.getCompBcFcId()); // NOPMD by adn1cob on
                                                                                          // 6/30/14 10:16 AM
        // add it to all bcs in data cache
        getDataCache().getAllCompPkgBcFcs().put(tCompPkgBcFc.getCompBcFcId(), compPkgBcFc);
      }
      this.fcMap.put(tCompPkgBcFc.getCompBcFcId(), compPkgBcFc);
    }
  }


  /**
   * Return the display text for UI ( indicates if 'ALL' Fc's mapped for this BC or 'Multiple' Bcs mapped)
   *
   * @return String 'ALL' or 'MULTIPLE'
   */
  public String getFCMappingDisplay() {

    String dispStr = ALL_FC_MAPPING;
    if (!getCompPkgBcFcs().isEmpty()) {
      dispStr = MULTI_FC_MAPPING;
    }
    return dispStr;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgBc other) {
    // sort based on seq no
    return getBcSeqNo().compareTo(other.getBcSeqNo());
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
  public String getName() {
    return getBcName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CPEntityType.COMP_PKG_BC;
  }


  /**
   * @param param2 parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final CompPkgBc param2, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        // comparing the BC names
        compareResult = ApicUtil.compare(getName(), param2.getName());
        break;
      case SORT_LONG_NAME:
        compareResult = ApicUtil.compare(getDescription(), param2.getDescription());
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
   * Getter for parent Component package
   *
   * @return CompPkg
   */
  public CompPkg getCompPkg() {
    return this.cpDataProvider.getDataCache()
        .getCompPkg(getEntityProvider().getDbCompPkgBc(getID()).getTCompPkg().getCompPkgId());
  }

}
