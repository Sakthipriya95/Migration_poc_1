/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;


/**
 * Command class for Questionnaire
 *
 * @author bne4cob
 */
public class QuestionnaireCommand extends AbstractCommand<Questionnaire, QuestionnaireLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public QuestionnaireCommand(final ServiceData serviceData, final Questionnaire input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new QuestionnaireLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TQuestionnaire entity = new TQuestionnaire();

    entity.setNameEng(getInputData().getNameEng());
    entity.setNameGer(getInputData().getNameGer());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());
    entity.setDeletedFlag(booleanToYorN(getInputData().getDeleted()));
    entity.setTWorkpackageDivision(
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(getInputData().getWpDivId()));

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    QuestionnaireLoader loader = new QuestionnaireLoader(getServiceData());
    TQuestionnaire entity = loader.getEntityObject(getInputData().getId());

    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
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
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().getDescEng(), getOldData().getDescEng()) ||
        isObjectChanged(getInputData().getDescGer(), getOldData().getDescGer());
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
