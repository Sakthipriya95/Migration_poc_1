/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetHwComponent;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetParamResp;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetParamType;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetSysElement;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;

/**
 * @author rgo7cob
 */
public class RuleSetParameterLoader extends AbstractBusinessObject<RuleSetParameter, TRuleSetParam> {

  /**
   * @param servData Service Data
   */
  public RuleSetParameterLoader(final ServiceData servData) {
    super(servData, MODEL_TYPE.CDR_RULE_SET_PARAM, TRuleSetParam.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RuleSetParameter createDataObject(final TRuleSetParam entity) throws DataException {
    RuleSetParameter data = new RuleSetParameter();

    data.setId(entity.getRsetParamId());
    data.setRuleSetId(entity.getTRuleSet().getRsetId());
    data.setVersion(entity.getVersion());


    TParameter paramEntity = new ParameterLoader(getServiceData()).getEntityObject(entity.getTParameter().getId());

    data.setName(paramEntity.getName());
    data.setParamId(paramEntity.getId());
    data.setDescription(getLangSpecTxt(paramEntity.getLongname(), paramEntity.getLongnameGer()));
    data.setSsdClass(paramEntity.getSsdClass());
    data.setCodeWord(paramEntity.getIscodeword());
    data.setIsBitWise(paramEntity.getIsbitwise());
    data.setBlackList(yOrNToBoolean(paramEntity.getIsBlackListLabel()));
    data.setCustPrm(paramEntity.getIscustprm());
    data.setLongName(paramEntity.getLongname());
    data.setLongNameGer(paramEntity.getLongnameGer());
    data.setParamHint(paramEntity.getHint());
    data.setType(paramEntity.getPtype());
    data.setParamHint(paramEntity.getHint());
    data.setQssdFlag(yOrNToBoolean(paramEntity.getQssdFlag()));
    ParameterClass paramClass = ParameterClass.getParamClass(paramEntity.getPclass());
    data.setpClassText(paramClass == null ? "" : paramClass.getText());

    FunctionLoader functionLoader = new FunctionLoader(getServiceData());
    TFunction functionEntity = functionLoader.getEntityObject(entity.getTFunction().getId());
    data.setFuncId(functionEntity.getId());

    TRulesetParamType tRulesetParamType = entity.gettRulesetParamType();
    if (CommonUtils.isNotNull(tRulesetParamType)) {
      data.setParamType(
          new RulesetParamTypeLoader(getServiceData()).createDataObject(tRulesetParamType).getParamType());
    }

    TRulesetParamResp tRulesetParamResp = entity.gettRulesetParamResp();
    if (CommonUtils.isNotNull(tRulesetParamResp)) {
      data.setParamResp(
          new RulesetParamRespLoader(getServiceData()).createDataObject(tRulesetParamResp).getParamResp());
    }
    TRulesetSysElement tRulesetSysElement = entity.gettRulesetSysElement();
    if (CommonUtils.isNotNull(tRulesetSysElement)) {
      data.setSysElement(
          new RulesetSysElementLoader(getServiceData()).createDataObject(tRulesetSysElement).getSysElement());
    }

    TRulesetHwComponent tRulesetHwComponent = entity.gettRulesetHwComponent();
    if (CommonUtils.isNotNull(tRulesetHwComponent)) {
      data.setHwComponent(
          new RulesetHwComponentLoader(getServiceData()).createDataObject(tRulesetHwComponent).getHwComponent());
    }
    setCommonFields(data, entity);

    return data;
  }


  /**
   * @param ruleSetId rule set id
   * @return the rule set Params
   * @throws DataException DataException
   */
  public Map<String, RuleSetParameter> getAllRuleSetParams(final Long ruleSetId) throws DataException {

    getLogger().debug("Fetching rule-set params for rule-set : {}", ruleSetId);

    Map<String, RuleSetParameter> ruleSetParamMap = new HashMap<>();

    TRuleSet ruleSetEntity = getRuleSetEntity(ruleSetId);

    List<TRuleSetParam> tRuleSetParams = ruleSetEntity.getTRuleSetParams();

    for (TRuleSetParam tRuleSetParam : tRuleSetParams) {
      ruleSetParamMap.put(tRuleSetParam.getTParameter().getName(), createDataObject(tRuleSetParam));
    }

    getLogger().debug("Fetching rule set params finished. Parameter count = {}", ruleSetParamMap.size());

    return ruleSetParamMap;

  }


  /**
   * @param ruleSetId rule set id
   * @return the rule set Params
   * @throws DataException DataException
   */
  public List<RuleSetParamWithFunction> getAllRuleSetParamWithFunc(final Long ruleSetId) throws DataException {

    List<RuleSetParamWithFunction> ruleSetParamList = new ArrayList<>();

    TRuleSet ruleSetEntity = getRuleSetEntity(ruleSetId);

    List<TRuleSetParam> tRuleSetParams = ruleSetEntity.getTRuleSetParams();

    for (TRuleSetParam tRuleSetParam : tRuleSetParams) {
      RuleSetParamWithFunction ruleSetParamWithFunction = new RuleSetParamWithFunction();
      ruleSetParamWithFunction.setRuleSetParam(createDataObject(tRuleSetParam));
      ruleSetParamWithFunction
          .setFunction(new FunctionLoader(getServiceData()).getDataObjectByID(tRuleSetParam.getTFunction().getId()));
      ruleSetParamList.add(ruleSetParamWithFunction);

    }
    return ruleSetParamList;

  }


  /**
   * @param ruleSetId ruleSetId
   * @return the rule set Entuity
   */
  public TRuleSet getRuleSetEntity(final Long ruleSetId) {
    RuleSetLoader loader = new RuleSetLoader(getServiceData());
    return loader.getEntityObject(ruleSetId);
  }

}
