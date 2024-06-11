package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * Icdm-470 This class represents Top Level entity
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class TopLevelEntity extends ApicObject implements Comparable<TopLevelEntity> {


  /**
   * Constructor
   *
   * @param apicDataProvider apicDataProvider
   * @param entId entId
   */
  public TopLevelEntity(final ApicDataProvider apicDataProvider, final Long entId) {
    super(apicDataProvider, entId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final TopLevelEntity other) {

    return 0;
  }


  /**
   * Gets the created date
   *
   * @return Calendar
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbTopLevelEntity(getID()).getLastModDate());
  }

  /**
   * Gets the Entity Name
   *
   * @return Entity Name
   */
  public String getEntityName() {
    return getEntityProvider().getDbTopLevelEntity(getID()).getEntityName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getEntityName();
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
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    // Not applicable
    return null;
  }


}