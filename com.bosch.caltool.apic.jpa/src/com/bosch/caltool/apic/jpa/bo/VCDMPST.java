/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPstCont;

/**
 * POJO for representing TVCDM_PST retreived from DB
 *
 * @author dmo5cob
 */
public class VCDMPST extends ApicObject implements Comparable<VCDMPST> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @param apicDataProvider Apicdataprovider instance
   * @param pstId object id
   */
  public VCDMPST(final ApicDataProvider apicDataProvider, final Long pstId) {
    super(apicDataProvider, pstId);
  }

  /**
   * Returns the pst name
   *
   * @return pst Name in String
   */
  public String getPstName() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPst(getID()).getPstName());
  }

  /**
   * Returns the pst revision
   *
   * @return pst revision
   */
  public Long getPstRevision() {
    return getEntityProvider().getDbTvcdmPst(getID()).getPstRevision().longValue();
  }

  /**
   * Returns the pst id
   *
   * @return pst id
   */
  public Long getPstId() {
    return getEntityProvider().getDbTvcdmPst(getID()).getPstId().longValue();
  }

  /**
   * Returns the a2lfile info id
   *
   * @return a2lfileinfo id
   */
  public Long getA2lFileInfoId() {
    return getEntityProvider().getDbTvcdmPst(getID()).getA2lInfoId().longValue();
  }

  /**
   * Returns the pst variant
   *
   * @return pst variant in String
   */
  public String getPstVariant() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPst(getID()).getPstVariant());
  }

  /**
   * Returns the pver name
   *
   * @return pver name in String
   */
  public String getPverName() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPst(getID()).getPverName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbTvcdmPst(getID()).getCreUser();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbTvcdmPst(getID()).getModUser();
  }

  /**
   * @return SortedSet<vCDMPSTContents>
   */
  public SortedSet<VCDMPSTContents> getPSTContents() {
    SortedSet<VCDMPSTContents> vcdmPSTContentsSet = new TreeSet<VCDMPSTContents>();
    for (TvcdmPstCont iterable_element : getEntityProvider().getDbTvcdmPst(getID()).getTvcdmPstCont()) {
      VCDMPSTContents vcdmPSTContents =
          new VCDMPSTContents((ApicDataProvider) getDataProvider(), new Long(iterable_element.getId().longValue()));
      vcdmPSTContentsSet.add(vcdmPSTContents);
    }
    return vcdmPSTContentsSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.dateToCalendar(getEntityProvider().getDbTvcdmPst(getID()).getCreDate());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.dateToCalendar(getEntityProvider().getDbTvcdmPst(getID()).getModDate());
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
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPst(getID()).getPstName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return CommonUtils.checkNull(getEntityProvider().getDbTvcdmPst(getID()).getPstName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VCDMPST arg0) {
    return ApicUtil.compare(getName(), arg0.getName());
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
    VCDMPST other = (VCDMPST) obj;
    return getName().equals(other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getName() == null) ? 0 : getName().hashCode());
    return result;
  }

}
