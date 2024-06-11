/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;


/**
 * @author bne4cob
 */
public class CDRParticipant extends AbstractCdrObject {

  /**
   * @param dataProvider CDR data provider
   * @param objID primary key
   */
  protected CDRParticipant(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllCDRParticipants().put(objID, this);
  }

  /**
   * @return the participant
   */
  public ApicUser getUser() {
    final TRvwParticipant dbParticipant = getEntityProvider().getDbCDRParticipant(getID());
    return getApicDataProvider().getApicUser(dbParticipant.getTabvApicUser().getUsername());
  }

  /**
   * @return type type of participation
   */
  public CDRConstants.REVIEW_USER_TYPE getParticipationType() {
    return CDRConstants.REVIEW_USER_TYPE.getType(getEntityProvider().getDbCDRParticipant(getID()).getActivityType());
  }

  /**
   * @return the CDR result object
   */
  public CDRResult getResult() {
    final TRvwParticipant dbParticipant = getEntityProvider().getDbCDRParticipant(getID());
    return getDataCache().getCDRResult(dbParticipant.getTRvwResult().getResultId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getUser().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_PARTICIPANT;
  }

  /**
   * Not applicable
   * <p>
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
    return getEntityProvider().getDbCDRParticipant(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbCDRParticipant(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRParticipant(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCDRParticipant(getID()).getModifiedDate());
  }


}
