/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;

/**
 * @author svj7cob
 */
// ICDM-2469
public class VcdmDatasetsWorkpkgStat extends ApicObject implements Comparable<VcdmDatasetsWorkpkgStat> {

  /**
   * @param apicDataProvider apicDataProvider
   * @param objID objID
   */
  protected VcdmDatasetsWorkpkgStat(final AbstractDataProvider apicDataProvider, final Long objID) {
    super(apicDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VcdmDatasetsWorkpkgStat o) {
    return 0;
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return the aprjId
   */
  public Long getAprjId() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getAprjId();
  }

  /**
   * @return the aprjName
   */
  public String getAprjName() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getAprjName();
  }

  /**
   * @return the calibratedLabels
   */
  public Long getCalibratedLabels() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getCalibratedLabels();
  }


  /**
   * @return the changedLabels
   */
  public Long getChangedLabels() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getChangedLabels();
  }

  /**
   * @return the checkedLabels
   */
  public Long getCheckedLabels() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getCheckedLabels();
  }

  /**
   * @return the completedLabels
   */
  public Long getCompletedLabels() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getCompletedLabels();
  }

  /**
   * @return the nostateLabels
   */
  public Long getNostateLabels() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getNostateLabels();
  }

  /**
   * @return the prelimcalibratedLabels
   */
  public Long getPrelimcalibratedLabels() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getPrelimcalibratedLabels();
  }

  /**
   * @return the statusId
   */
  public Long getStatusId() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getStatusId();
  }

  /**
   * @return the vcdmSoftware
   */
  public String getVcdmSoftware() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getVcdmSoftware();
  }

  /**
   * @return the vcdmVariant
   */
  public String getVcdmVariant() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getVcdmVariant();
  }

  /**
   * @return the workpkgName
   */
  public String getWorkpkgName() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getWorkpkgName();
  }

  /**
   * @return the easeedstModDate
   */
  public Date getEaseedstModDate() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getEaseedstModDate();
  }

  /**
   * @return the easeeDstId
   */
  // 221731
  public Long getEaseeDstId() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getEaseeDstId();
  }

  /**
   * @return the revision Number
   */
  // 221731
  public Long getRevisionNo() {
    return getEntityProvider().getDbVcdmWorkpkgStatSet(getID()).getRevision().longValue();
  }


}
