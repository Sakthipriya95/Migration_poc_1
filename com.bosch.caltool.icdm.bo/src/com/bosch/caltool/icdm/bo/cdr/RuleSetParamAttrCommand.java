/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParamAttr;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;


/**
 * Command class for Review Attribute Value
 *
 * @author bru2cob
 */
public class RuleSetParamAttrCommand extends AbstractCommand<RuleSetParameterAttr, RuleSetParameterAttrLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RuleSetParamAttrCommand(final ServiceData serviceData, final RuleSetParameterAttr input,
      final COMMAND_MODE commandMode) throws IcdmException {
    super(serviceData, input, new RuleSetParameterAttrLoader(serviceData), commandMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRuleSetParamAttr entity = new TRuleSetParamAttr();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());


    entity.setTabvAttribute(attrLoader.getEntityObject(getInputData().getAttrId()));

    RuleSetParameterLoader ruleSetParamLoader = new RuleSetParameterLoader(getServiceData());


    TRuleSetParam tRuleSetParam = ruleSetParamLoader.getEntityObject(getInputData().getRuleSetParamId());
    entity.setTRuleSetParam(tRuleSetParam);
    tRuleSetParam.getTRuleSetParamAttrs().add(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {


    RuleSetParameterAttrLoader loader = new RuleSetParameterAttrLoader(getServiceData());
    TRuleSetParamAttr truleSetParamAttr = loader.getEntityObject(getInputData().getId());

    RuleSetParameterLoader ruleSetParamLoader = new RuleSetParameterLoader(getServiceData());

    TRuleSetParam tRuleSetParam = ruleSetParamLoader.getEntityObject(getInputData().getRuleSetParamId());

    tRuleSetParam.getTRuleSetParamAttrs().remove(truleSetParamAttr);
    getEm().remove(truleSetParamAttr);
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
