/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author rgo7cob
 */
// Icdm-1485- Entity Object for the mview Table_datasets
public class VcdmDataSet extends ApicObject implements Comparable<VcdmDataSet> {

  /**
   * @param apicDataProvider apicDataProvider
   * @param objID objID
   */
  protected VcdmDataSet(final AbstractDataProvider apicDataProvider, final Long objID) {
    // 221731 - objID acts as easeeDstId here
    super(apicDataProvider, objID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {

    return null;
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
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbDataSet(getID()).getCreUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbDataSet(getID()).getModUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.dateToCalendar(getEntityProvider().getDbDataSet(getID()).getCreDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.dateToCalendar(getEntityProvider().getDbDataSet(getID()).getModDate());
  }


  /**
   * @return the Calibrated Labels
   */
  public Long getCalibratedLabels() {
    return getEntityProvider().getDbDataSet(getID()).getCalibratedLabels();
  }

  /**
   * @return the Calibrated Labels
   */
  public Long getParitionLabels() {
    return getEntityProvider().getDbDataSet(getID()).getPartitionLabel();
  }

  /**
   * @return the Changed Labels
   */
  public Long getChangedLabels() {
    return getEntityProvider().getDbDataSet(getID()).getChangedLabels();
  }

  /**
   * @return the Checked Labels
   */
  public Long getCheckedLabels() {
    return getEntityProvider().getDbDataSet(getID()).getCheckedLabels();
  }

  /**
   * @return the Completed Labels
   */
  public Long getCompletedLabels() {
    return getEntityProvider().getDbDataSet(getID()).getCompletedLabels();
  }

  /**
   * @return the No State Labels
   */
  public Long getNoStateLabels() {
    return getEntityProvider().getDbDataSet(getID()).getNoStateLabels();
  }


  /**
   * @return the PreLim labels
   */
  public Long getPreLimLabels() {
    return getEntityProvider().getDbDataSet(getID()).getPreLimLabels();
  }

  /**
   * @return the Total labels
   */
  public Long getTotalLabels() {
    return getEntityProvider().getDbDataSet(getID()).getTotalLabels();
  }

  /**
   * @return the Aprj Id
   */
  public Long getAprjId() {
    return getEntityProvider().getDbDataSet(getID()).getElementId();
  }

  /**
   * @return the Aprj Name
   */
  public String getAprjName() {
    return getEntityProvider().getDbDataSet(getID()).getElementName();
  }

  /**
   * @return the Status
   */
  public String getEaseedstState() {
    return getEntityProvider().getDbDataSet(getID()).getEaseedstState();
  }


  /**
   * @return the Prgram Key
   */
  public String getProgKey() {
    return getEntityProvider().getDbDataSet(getID()).getProgKey();
  }


  /**
   * @return the Prgram Key
   */
  public Date getEaseedstModDate() {
    return getEntityProvider().getDbDataSet(getID()).getEaseedstModDate();
  }


  /**
   * @return the Prgram Key
   */
  public String getProdKey() {
    return getEntityProvider().getDbDataSet(getID()).getProdKey();
  }

  /**
   * @return the Prgram Key
   */
  // 221731
  public Long getRevisionNo() {
    return getEntityProvider().getDbDataSet(getID()).getRevision().longValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VcdmDataSet arg0) {
    // TODO No Implementation for now.
    return 0;
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
    // TODO Auto-generated method stub
    return super.hashCode();
  }

  /**
   * @return the element Id
   */
  public Long getElementId() {
    return getEntityProvider().getDbDataSet(getID()).getElementId();
  }

}
