/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;


/**
 * Command class for Review Paticipants
 *
 * @author bru2cob
 */
public class RvwParticipantCommand extends AbstractCommand<RvwParticipant, RvwParticipantLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwParticipantCommand(final ServiceData serviceData, final RvwParticipant input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwParticipantLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwParticipant entity = new TRvwParticipant();

    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    if (null != resultEntity) {
      entity.setTRvwResult(resultEntity);
      Set<TRvwParticipant> rvwParticipants = resultEntity.getTRvwParticipants();
      if (null == rvwParticipants) {
        rvwParticipants = new HashSet<>();
      }
      rvwParticipants.add(entity);
      resultEntity.setTRvwParticipants(rvwParticipants);
    }
    entity.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()));
    entity.setActivityType(getInputData().getActivityType());
    entity.setEditFlag(ApicConstants.CODE_NO);
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RvwParticipantLoader loader = new RvwParticipantLoader(getServiceData());
    TRvwParticipant entity = loader.getEntityObject(getInputData().getId());

    entity.setTRvwResult(new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId()));
    entity.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()));
    entity.setActivityType(getInputData().getActivityType());
    entity.setEditFlag(getInputData().isEditFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwParticipantLoader loader = new RvwParticipantLoader(getServiceData());
    TRvwParticipant entity = loader.getEntityObject(getInputData().getId());
    entity.getTRvwResult().getTRvwParticipants().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }

}
