/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * POJO for representing TVCDM_PST_CONT retreived from DB
 *
 * @author dmo5cob
 */
public class VCDMPSTContents extends ApicObject implements Comparable<VCDMPSTContents> {


  /**
   * @param apicDataProvider Apicdataprovider instance
   * @param pstId object id
   */
  public VCDMPSTContents(final ApicDataProvider apicDataProvider, final Long pstId) {
    super(apicDataProvider, pstId);
  }

  /**
   * Returns the pst name
   *
   * @return pst Name in String
   */
  public String getFileName() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPstCont(getID()).getFileName());
  }

  /**
   * Returns the pst revision
   *
   * @return pst revision
   */
  public Long getFileId() {
    return getEntityProvider().getDbTvcdmPstCont(getID()).getFileId().longValue();
  }

  /**
   * Returns the pst id
   *
   * @return pst id
   */
  public String getVCDMClass() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPstCont(getID()).getVcdmClass());
  }

  /**
   * Returns the a2lfile info id
   *
   * @return a2lfileinfo id
   */
  public String getVCDMName() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPstCont(getID()).getVcdmName());
  }

  /**
   * Returns the pst variant
   *
   * @return pst variant in String
   */
  public String getVCDMVariant() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPstCont(getID()).getVcdmVariant());
  }

  /**
   * Returns the pver name
   *
   * @return pver name in String
   */
  public Long getVCDMRevision() {
    return getEntityProvider().getDbTvcdmPstCont(getID()).getVcdmRevision().longValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
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
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPstCont(getID()).getFileName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (ApicConstants.HASH_CODE_PRIME * result) + getName().hashCode();
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPstCont(getID()).getFileName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VCDMPSTContents arg0) {
    int compareResult;
    compareResult = ApicUtil.compare(getVCDMClass(), arg0.getVCDMClass());

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compareResult = ApicUtil.compare(getName(), arg0.getName());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    VCDMPSTContents other = (VCDMPSTContents) obj;
    return getName().equals(other.getName());
  }
}
