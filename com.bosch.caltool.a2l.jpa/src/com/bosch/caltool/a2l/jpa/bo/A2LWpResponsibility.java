/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.a2l.jpa.A2LEntityType;
import com.bosch.caltool.a2l.jpa.AbstractA2LObject;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResource;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * @author rgo7cob
 */
@Deprecated
public class A2LWpResponsibility extends AbstractA2LObject implements Comparable<A2LWpResponsibility> {


  /**
   * enum to declare the sort columns
   */
  public enum SortGroupColumns {
                                /**
                                 * Group Name column
                                 */
                                SORT_GROUP_NAME,
                                /**
                                 * Work Package Name
                                 */
                                SORT_GROUP_LONG,
                                /**
                                 * Work Package Number
                                 */
                                SORT_NUM_REF,

                                /**
                                 * Work Package Number
                                 */
                                SORT_RESP

  }

  /**
   * enum to declare the sort columns
   */
  public enum SortWPColumns {
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
                             SORT_WP_NUMBER,
                             /**
                              * Resp Sort
                              */
                             SORT_RESP
  }

  /**
   * label map of the A2L group.
   */
  private final Map<String, String> labelMap = new ConcurrentHashMap<>();
  /**
   * wp map for the wp group
   */
  private final Map<String, WorkPackage> wpMap = new ConcurrentHashMap<>();

  /**
   * @param a2lDataProvider a2lDataProvider
   * @param objID objID
   */
  public A2LWpResponsibility(final A2LDataProvider a2lDataProvider, final Long objID) {
    super(a2lDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbA2lWpResp(getID()).getVersion();
  }

  /**
   * @return the A2l Responsibilty from the cache.
   */
  public A2LResponsibility getA2lResponsibility() {
    TA2lResp ta2lResp = getEntityProvider().getDbA2lWpResp(getID()).getTA2lResp();
    return getDataCache().getA2lRespMap().get(ta2lResp.getA2lRespId());
  }

  /**
   * @return the wp responbility
   */
  public WPResponsibility getResponbility() {
    TWpResp tWpResp = getEntityProvider().getDbA2lWpResp(getID()).getTWpResp();
    if (tWpResp == null) {
      return null;
    }
    return getDataCache().getWpRespMap().get(tWpResp.getRespId());
  }


  /**
   * @return the resp enum
   */
  public CDRConstants.WPResponsibilityEnum getResponbilityEnum() {
    if (getResponbility() == null) {
      return CDRConstants.WPResponsibilityEnum.RB;
    }
    return getResponbility().getWpRespEnum();
  }


  /**
   * @return the Icdm A2l Group
   */
  public ICDMA2LGroup getIcdmA2lGroup() {
    TA2lGroup ta2lGroup = getEntityProvider().getDbA2lWpResp(getID()).getTA2lGroup();
    if (ta2lGroup == null) {
      return null;
    }
    return getDataCache().getA2lGrpMap().get(ta2lGroup.getGroupId());
  }


  /**
   * @return the name English
   */
  public String getWpNameEng() {
    TWorkpackage tWorkpackage = getEntityProvider().getDbA2lWpResp(getID()).getTWorkpackage();
    if (tWorkpackage == null) {
      return null;
    }
    return tWorkpackage.getWpNameE();
  }


  /**
   * @return the name Germany
   */
  public String getWpNameGer() {
    TWorkpackage tWorkpackage = getEntityProvider().getDbA2lWpResp(getID()).getTWorkpackage();
    if (tWorkpackage == null) {
      return null;
    }
    return tWorkpackage.getWpNameG();
  }

  /**
   * @return true if a2l grp is null.
   */
  public boolean isWorkPkg() {
    return getIcdmA2lGroup() == null;
  }

  /**
   * @return true if work pkg is null.
   */
  public boolean isA2lGrp() {
    return getEntityProvider().getDbA2lWpResp(getID()).getTA2lGroup() != null;
  }


  /**
   * @return the wp name
   */
  public String getWpName() {
    return ApicUtil
        .getLangSpecTxt(getDataCache().getLanguage(), getWpNameEng(), getWpNameGer(), ApicConstants.EMPTY_STRING)
        .trim();
  }

  /**
   * @return the wp Grp name
   */
  public String getWpAndGrpName() {
    return CommonUtils.concatenate(getWpResource(), ":", getWpName());
  }

  /**
   * @return the wp group.
   */
  public String getWpResource() {
    TWorkpackage tWorkpackage = getEntityProvider().getDbA2lWpResp(getID()).getTWorkpackage();

    Long divId = getA2lResponsibility().getPidcA2l().getPidcVersion().getDivisionAttrValue().getValueID();


    TypedQuery<TWorkpackageDivision> tQuery = getDataProvider().getEntProvider().getEm()
        .createNamedQuery(TWorkpackageDivision.NQ_FIND_RES_BY_DIV_ID, TWorkpackageDivision.class);
    tQuery.setParameter("divValueId", divId);
    tQuery.setParameter("wpId", tWorkpackage.getWpId());

    if ((null != tQuery.getResultList()) && (null != tQuery.getResultList().get(0).getTWpResource())) {
      TWpResource tWpRes = tQuery.getResultList().get(0).getTWpResource();
      return tWpRes.getResourceCode().trim();
    }
    return "";
  }


  /**
   * Get the creation date of the Value
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lWpResp(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Value
   *
   * @return The ID of the user who has created the Value
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbA2lWpResp(getID()).getCreatedUser();

  }

  /**
   * Get the date when the Value has been modified the last time
   *
   * @return The date when the Value has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbA2lWpResp(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Value the last time
   *
   * @return The user who has modified the Value the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbA2lWpResp(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();
    return objDetails;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return isA2lGrp() ? getIcdmA2lGroup().getGroupName() : getWpName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return A2LEntityType.A2L_WP_RESP;
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
  public String getCreatedUserDisplayName() {
    // Not applicable
    return null;
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
  public int compareTo(final A2LWpResponsibility obj) {
    return ApicUtil.compare(getID(), obj.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUserDisplayName() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() {
    // Not applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {

    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModified(final Map<String, String> oldObjDetails) {
    return false;
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
  public String getToolTip() {
    return null;
  }


  /**
   * @param respWp workPackage
   * @param sortColumn sortColumn
   * @return the compare value
   */
  public int compareTo(final A2LWpResponsibility respWp, final SortWPColumns sortColumn) {
    // TODO Auto-generated method stub
    int compareResult;

    switch (sortColumn) {
      // Sort based on Work package group name
      case SORT_WP_GROUP_NAME:
        compareResult = ApicUtil.compare(getWpResource(), respWp.getWpResource());
        break;
      // Sort based on Work package name
      case SORT_WP_NAME:
        compareResult = ApicUtil.compare(getWpName(), respWp.getWpName());
        break;
      case SORT_RESP:
        compareResult =
            ApicUtil.compare(getResponbilityEnum().getDispName(), respWp.getResponbilityEnum().getDispName());
        break;
      // Sort based on work packege number
      case SORT_WP_NUMBER:
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(getWpResource(), respWp.getWpResource());
    }

    return compareResult;

  }

  /**
   * @param respWp workPackage
   * @param sortColumn sortColumn
   * @return the compare value
   */
  public int compareTo(final A2LWpResponsibility respWp, final SortGroupColumns sortColumn) {
    // TODO Auto-generated method stub
    int compareResult;

    switch (sortColumn) {
      // Sort based on Work package group name
      case SORT_GROUP_NAME:
        compareResult = ApicUtil.compare(getIcdmA2lGroup().getGroupName(), respWp.getIcdmA2lGroup().getGroupName());
        break;
      // Sort based on Work package name
      case SORT_GROUP_LONG:
        compareResult = ApicUtil.compare(getIcdmA2lGroup().getLongName(), respWp.getIcdmA2lGroup().getLongName());
        break;
      // Sort based on work packege number
      case SORT_NUM_REF:
        compareResult = ApicUtil.compareLong(getLabelMap().size(), respWp.getLabelMap().size());
        break;
      case SORT_RESP:
        compareResult =
            ApicUtil.compare(getResponbilityEnum().getDispName(), respWp.getResponbilityEnum().getDispName());

        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(getIcdmA2lGroup().getGroupName(), respWp.getIcdmA2lGroup().getGroupName());
    }

    return compareResult;

  }

  /**
   * @return the label map
   */
  public Map<String, String> getLabelMap() {
    return this.labelMap;
  }

  /**
   * @return the work package map
   */
  public Map<String, WorkPackage> getWorkpackageMap() {
    return this.wpMap;
  }

  /**
   * @return the work package
   */
  public String getWorkPackageNum() {
    return this.wpMap.get(getWpName()) == null ? "" : this.wpMap.get(getWpName()).getWpNumber();
  }

}
