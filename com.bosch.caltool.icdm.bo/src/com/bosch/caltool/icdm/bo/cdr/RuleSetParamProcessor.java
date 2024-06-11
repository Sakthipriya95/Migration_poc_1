/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author rgo7cob
 */
public class RuleSetParamProcessor extends AbstractSimpleBusinessObject {


  /**
   * @param serviceData serviceData
   */
  public RuleSetParamProcessor(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param ruleSetParam
   * @param ruleSetParamLoader
   * @param ruleSetParam
   * @param cdrRuleMap
   * @throws UnAuthorizedAccessException
   * @throws DataException
   * @throws IcdmException
   */
  public void deleteRules(final RuleSetParameter ruleSetParam, final Map<String, List<CDRRule>> cdrRuleMap)
      throws IcdmException {
    RuleSetLoader ruleSetLoader = new RuleSetLoader(getServiceData());
    RuleSet ruleSet = ruleSetLoader.getDataObjectByID(ruleSetParam.getRuleSetId());


    RuleManagerFactory factory = new RuleManagerFactory(getServiceData());
    ReviewRuleParamCol paramcol = new ReviewRuleParamCol();
    paramcol.setParamCollection(ruleSet);
    IRuleManager createRuleManager = factory.createRuleManager(paramcol);


    if ((cdrRuleMap != null) && (cdrRuleMap.get(ruleSetParam.getName()) != null)) {
      createRuleManager.deleteMultipleRules(cdrRuleMap.get(ruleSetParam.getName()));
    }
  }


}
