/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;

/**
 * @author nip4cob
 */
public class QuestDepAttrCommand extends AbstractCommand<QuestionDepenAttr, QuestionDepenAttrLoader> {

  /**
   * @param serviceData
   * @param inputData
   * @param busObj
   * @param mode
   * @throws IcdmException
   */
  protected QuestDepAttrCommand(final ServiceData serviceData, final QuestionDepenAttr inputData) throws IcdmException {
    super(serviceData, inputData, new QuestionDepenAttrLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData
   * @param inputData
   * @param isDelete
   * @throws IcdmException
   */
  public QuestDepAttrCommand(final ServiceData serviceData, final QuestionDepenAttr inputData, final boolean isDelete)
      throws IcdmException {
    super(serviceData, inputData, new QuestionDepenAttrLoader(serviceData), COMMAND_MODE.DELETE);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    final TQuestionDepenAttribute dbQuesAttr = new TQuestionDepenAttribute();
    TQuestion dbQuestion = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQId());
    dbQuesAttr.setTQuestion(dbQuestion);
    dbQuesAttr.setTabvAttribute(new AttributeLoader(getServiceData()).getEntityObject(getInputData().getAttrId()));
    setUserDetails(COMMAND_MODE.CREATE, dbQuesAttr);
    persistEntity(dbQuesAttr);
    // get the dependency list and add the newly created entity
    Set<TQuestionDepenAttribute> tQuestionDepenAttrList = dbQuestion.getTQuestionDepenAttributes();

    // Update other entity mapping
    if (tQuestionDepenAttrList == null) {
      tQuestionDepenAttrList = new HashSet<>();
      dbQuestion.setTQuestionDepenAttributes(tQuestionDepenAttrList);
    }
    tQuestionDepenAttrList.add(dbQuesAttr);
    dbQuesAttr.getTabvAttribute().addTQuestionDepenAttributes(dbQuesAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TQuestion dbQuestion = new QuestionLoader(getServiceData()).getEntityObject(getInputData().getQId());
    final TQuestionDepenAttribute dbQuestionAttr =
        new QuestionDepenAttrLoader(getServiceData()).getEntityObject(getInputData().getId());
    dbQuestion.getTQuestionDepenAttributes().remove(dbQuestionAttr);
    getEm().remove(dbQuestionAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
