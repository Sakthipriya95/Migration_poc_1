/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Command class for Review Attribute Value
 *
 * @author bru2cob
 */
public class RuleSetParameterCommand extends AbstractCommand<RuleSetParameter, RuleSetParameterLoader> {


  private Map<String, List<CDRRule>> cdrRuleMap = new HashMap<>();


  /**
   * @param serviceData
   * @param obj
   * @param create
   * @throws IcdmException
   */
  public RuleSetParameterCommand(final ServiceData serviceData, final RuleSetParameter obj,
      final COMMAND_MODE commandMode) throws IcdmException {
    super(serviceData, obj, new RuleSetParameterLoader(serviceData), commandMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRuleSetParam entity = new TRuleSetParam();
    FunctionLoader funcLoader = new FunctionLoader(getServiceData());
    TFunction tfunction = funcLoader.getEntityObject(getInputData().getFuncId());

    entity.setTFunction(tfunction);
    ParameterLoader paramLoader = new ParameterLoader(getServiceData());
    TParameter tParam = paramLoader.getEntityObject(getInputData().getParamId());

    entity.setTParameter(tParam);

    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    TRuleSet tRuleSet = ruleSetLoader.getEntityObject(getInputData().getRuleSetId());
    entity.setTRuleSet(tRuleSet);

    tRuleSet.getTRuleSetParams().add(entity);

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

    RuleSetParameterLoader ruleSetParamLoader = new RuleSetParameterLoader(getServiceData());
    TRuleSetParam deletedRuleSetParam = ruleSetParamLoader.getEntityObject(getInputData().getId());
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    TRuleSet tRuleSet = ruleSetLoader.getEntityObject(getInputData().getRuleSetId());
    tRuleSet.getTRuleSetParams().remove(deletedRuleSetParam);
    RuleSetParameter ruleSetParam = ruleSetParamLoader.getDataObjectByID(getInputData().getId());
    RuleSetParamProcessor processor = new RuleSetParamProcessor(getServiceData());


    processor.deleteRules(ruleSetParam, this.cdrRuleMap);


    // remove from rule set
    RuleSetParameterAttrLoader loader = new RuleSetParameterAttrLoader(getServiceData());

    Map<String, RuleSetParameter> ruleSetParamMap = new HashMap<>();
    ruleSetParamMap.put(ruleSetParam.getName(), ruleSetParam);

    Map<String, List<RuleSetParameterAttr>> paramAttrMap = loader.getParamAttrMap(ruleSetParamMap);

    if (paramAttrMap.get(ruleSetParam.getName()) != null) {
      for (RuleSetParameterAttr truleSetParamattr : paramAttrMap.get(ruleSetParam.getName())) {
        RuleSetParamAttrCommand command =
            new RuleSetParamAttrCommand(getServiceData(), truleSetParamattr, COMMAND_MODE.DELETE);
        executeChildCommand(command);
      }
    }
    getEm().remove(deletedRuleSetParam);
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


  /**
   * @return the cdrRuleMap
   */
  public Map<String, List<CDRRule>> getCdrRuleMap() {
    return this.cdrRuleMap;
  }


  /**
   * @param cdrRuleMap the cdrRuleMap to set
   */
  public void setCdrRuleMap(final Map<String, List<CDRRule>> cdrRuleMap) {
    this.cdrRuleMap = cdrRuleMap;
  }


}
