/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;

/**
 * @author apj4cob
 */
public class RvwQnaireAnswerOplCommand extends AbstractCommand<RvwQnaireAnswerOpl, RvwQnaireAnswerOplLoader> {

  /**
   * @param serviceData ServiceData
   * @param inputData RvwQnaireAnswerOpl
   * @param cmdMode COMMAND_MODE
   * @throws IcdmException error when initializing
   */
  public RvwQnaireAnswerOplCommand(final ServiceData serviceData, final RvwQnaireAnswerOpl inputData,
      final COMMAND_MODE cmdMode) throws IcdmException {
    super(serviceData, inputData, new RvwQnaireAnswerOplLoader(serviceData), cmdMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwQnaireAnswerOpl dbQnAnsOpenPoint = new TRvwQnaireAnswerOpl();
    dbQnAnsOpenPoint.setMeasure(getInputData().getMeasure());
    dbQnAnsOpenPoint.setResult(getInputData().getResult());
    if (null != getInputData().getCompletionDate()) {
      try {
        dbQnAnsOpenPoint.setCompletionDate(string2timestamp(getInputData().getCompletionDate()));
      }
      catch (ParseException exp) {

        throw new CommandException(exp.getMessage(), exp);
      }
    }
    dbQnAnsOpenPoint.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getResponsible()));
    dbQnAnsOpenPoint.setOpenPoints(getInputData().getOpenPoints());
    dbQnAnsOpenPoint.setTRvwQnaireAnswer(
        new RvwQnaireAnswerLoader(getServiceData()).getEntityObject(getInputData().getRvwAnswerId()));
    Set<TRvwQnaireAnswerOpl> tRvwAnsOpenPoints = new RvwQnaireAnswerLoader(getServiceData())
        .getEntityObject(getInputData().getRvwAnswerId()).getTQnaireAnsOpenPoints();
    if (tRvwAnsOpenPoints == null) {
      tRvwAnsOpenPoints = new HashSet<>();
    }
    tRvwAnsOpenPoints.add(dbQnAnsOpenPoint);

    new RvwQnaireAnswerLoader(getServiceData()).getEntityObject(getInputData().getRvwAnswerId())
        .setTQnaireAnsOpenPoints(tRvwAnsOpenPoints);
    setUserDetails(COMMAND_MODE.CREATE, dbQnAnsOpenPoint);
    persistEntity(dbQnAnsOpenPoint);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RvwQnaireAnswerOplLoader ldr = new RvwQnaireAnswerOplLoader(getServiceData());
    TRvwQnaireAnswerOpl entity = ldr.getEntityObject(getInputData().getId());
    if (isObjectChanged(getInputData().getMeasure(), getOldData().getMeasure())) {
      entity.setMeasure(getInputData().getMeasure());
    }
    if (isObjectChanged(getInputData().getResult(), getOldData().getResult())) {
      entity.setResult(getInputData().getResult());
    }
    if (isObjectChanged(getInputData().getCompletionDate(), getOldData().getCompletionDate())) {
      try {
        entity.setCompletionDate(string2timestamp(getInputData().getCompletionDate()));
      }
      catch (ParseException exp) {
        throw new CommandException(exp.getMessage(), exp);
      }
    }
    if (isObjectChanged(getInputData().getResponsible(), getOldData().getResponsible())) {
      entity.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getResponsible()));
    }
    if (isObjectChanged(getInputData().getRvwAnswerId(), getOldData().getRvwAnswerId())) {
      entity.setTRvwQnaireAnswer(
          new RvwQnaireAnswerLoader(getServiceData()).getEntityObject(getInputData().getRvwAnswerId()));
    }
    if (isObjectChanged(getInputData().getOpenPoints(), getOldData().getOpenPoints())) {
      entity.setOpenPoints(getInputData().getOpenPoints());
    }
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwQnaireAnswerOplLoader ldr = new RvwQnaireAnswerOplLoader(getServiceData());
    TRvwQnaireAnswerOpl entity = ldr.getEntityObject(getInputData().getId());
    entity.getTRvwQnaireAnswer().getTQnaireAnsOpenPoints().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return dataChangedResultMeasCompDate() || dataChangedRespRvwAnsId();
  }

  /**
   * @return
   */
  private boolean dataChangedRespRvwAnsId() {
    return isObjectChanged(getInputData().getOpenPoints(), getOldData().getOpenPoints()) ||
        isObjectChanged(getInputData().getResponsible(), getOldData().getResponsible()) ||
        isObjectChanged(getInputData().getRvwAnswerId(), getOldData().getRvwAnswerId());
  }

  /**
   * @return
   */
  private boolean dataChangedResultMeasCompDate() {
    return isObjectChanged(getInputData().getMeasure(), getOldData().getMeasure()) ||
        isObjectChanged(getInputData().getResult(), getOldData().getResult()) ||
        isObjectChanged(getInputData().getCompletionDate(), getOldData().getCompletionDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

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
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return true;
  }
}