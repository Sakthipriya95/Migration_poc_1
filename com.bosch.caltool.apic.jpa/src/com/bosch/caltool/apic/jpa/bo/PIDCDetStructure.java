package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author dmo5cob
 */
public class PIDCDetStructure extends ApicObject implements Comparable<PIDCDetStructure> {

  /**
   * Constructor
   *
   * @param apicDataProvider the data provider
   * @param pdsId pdsID
   */
  public PIDCDetStructure(final ApicDataProvider apicDataProvider, final Long pdsId) {
    super(apicDataProvider, pdsId);
    apicDataProvider.getDataCache().getAllPidcDetStructure().put(pdsId, this);
  }

  /**
   * @return get Attribute ID
   */
  public Long getAttrID() {
    return getEntityProvider().getDbPidcDetStructure(getID()).getTabvAttribute().getAttrId();
  }

  /**
   * @return get Attribute
   */
  public Attribute getAttribute() {
    return getDataCache().getAttribute(getAttrID());
  }

  /**
   * @return get Attribute Level
   */
  public Long getPidAttrLevel() {
    return getEntityProvider().getDbPidcDetStructure(getID()).getPidAttrLevel();
  }

  /**
   * @return get PIDC ID
   */
  public Long getPidcVersionID() {
    return getEntityProvider().getDbPidcDetStructure(getID()).getTPidcVersion().getPidcVersId();
  }

  /**
   * @return get PIDC
   */
  public PIDCVersion getPidcVersion() {
    return getDataCache()
        .getPidcVersion(getEntityProvider().getDbPidcDetStructure(getID()).getTPidcVersion().getPidcVersId());
  }

  @Override
  public int compareTo(final PIDCDetStructure arg0) {
    // default sorted by level
    return arg0.getPidAttrLevel().compareTo(getPidAttrLevel());
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
    return getAttribute().getName();
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
  public String getDescription() {
    return getAttribute().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbPidcDetStructure(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPidcDetStructure(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcDetStructure(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPidcDetStructure(getID()).getModifiedDate());
  }

}
