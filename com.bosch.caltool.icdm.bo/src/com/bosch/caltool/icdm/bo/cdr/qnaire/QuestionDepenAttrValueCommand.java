/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;

/**
 * @author nip4cob
 */
public class QuestionDepenAttrValueCommand
    extends AbstractCommand<QuestionDepenAttrValue, QuestionDepenAttrValueLoader> {

  /**
   * @param serviceData
   * @param inputData
   * @throws IcdmException
   */
  public QuestionDepenAttrValueCommand(final ServiceData serviceData, final QuestionDepenAttrValue inputData)
      throws IcdmException {
    super(serviceData, inputData, new QuestionDepenAttrValueLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData
   * @param inputData
   * @param isUpdate
   * @throws IcdmException
   */
  public QuestionDepenAttrValueCommand(final ServiceData serviceData, final QuestionDepenAttrValue inputData,
      final boolean isUpdate) throws IcdmException {
    super(serviceData, inputData, new QuestionDepenAttrValueLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    final TQuestionDepenAttrValue dbQuesAttrValue = new TQuestionDepenAttrValue();
    TQuestionDepenAttribute dbQuesDepenAttr =
        new QuestionDepenAttrLoader(getServiceData()).getEntityObject(getInputData().getQAttrDepId());
    dbQuesAttrValue.setTQuestionDepenAttribute(dbQuesDepenAttr);
    dbQuesAttrValue
        .setTabvAttrValue(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId()));

    // set combination number
    setCombinationNumber(dbQuesAttrValue);

    setUserDetails(COMMAND_MODE.CREATE, dbQuesAttrValue);
    persistEntity(dbQuesAttrValue);

    // get the list from ques depn attr
    Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues = dbQuesDepenAttr.getTQuestionDepenAttrValues();
    if (null == tQuestionDepenAttrValues) {
      tQuestionDepenAttrValues = new HashSet<>();
      dbQuesDepenAttr.setTQuestionDepenAttrValues(tQuestionDepenAttrValues);
    }
    // add the created entity to the list
    dbQuesDepenAttr.getTQuestionDepenAttrValues().add(dbQuesAttrValue);
    dbQuesAttrValue.getTabvAttrValue().addTQuestionDepenAttrValues(dbQuesAttrValue);
  }


  /**
   * @param dbQuesAttrVal
   */
  private void setCombinationNumber(final TQuestionDepenAttrValue dbQuesAttrVal) {
    Set<TQuestionDepenAttrValue> quesAttrValSet = new QuestionDepenAttrLoader(getServiceData())
        .getEntityObject(getInputData().getQAttrDepId()).getTQuestionDepenAttrValues();
    dbQuesAttrVal.setQCombiNum(getMaxCombination(quesAttrValSet) + 1);
  }

  /**
   * @param quesAttrValSet question attribute value set
   * @return  maximum combination number in given value set
   */
  private Long getMaxCombination(final Set<TQuestionDepenAttrValue> quesAttrValSet) {
    Long maxCombNo = 0L;
    if ((quesAttrValSet != null) && !quesAttrValSet.isEmpty()) {
      for (TQuestionDepenAttrValue quesAttrVal : quesAttrValSet) {
        Long combNum = quesAttrVal.getQCombiNum();
        if (combNum > maxCombNo) {
          maxCombNo = combNum;
        }
      }
    }
    return maxCombNo;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    final TQuestionDepenAttrValue dbQuestionAttrVal =
        new QuestionDepenAttrValueLoader(getServiceData()).getEntityObject(getInputData().getId());
    // update the attribute value
    dbQuestionAttrVal
        .setTabvAttrValue(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId()));
    setUserDetails(COMMAND_MODE.UPDATE, dbQuestionAttrVal);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    final TQuestionDepenAttrValue dbQuestionAttrVal =
        new QuestionDepenAttrValueLoader(getServiceData()).getEntityObject(getInputData().getId());
    dbQuestionAttrVal.getTQuestionDepenAttribute().getTQuestionDepenAttrValues().remove(dbQuestionAttrVal);
    dbQuestionAttrVal.getTabvAttrValue().removeTQuestionDepenAttrValues(dbQuestionAttrVal);
    getEm().remove(dbQuestionAttrVal);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().getValueId(), getOldData().getValueId());
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
    return true;
  }


}
