/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.CPImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.FunctionImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.RulesetImportRuleManager;
import com.bosch.caltool.icdm.bo.comppkg.CompPkgRuleManager;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;

/**
 * @author rgo7cob
 */
public class RuleManagerFactory {


  private final ServiceData serviceData;

  /**
   * @param serviceData serviceData
   * @param logger logger
   */
  public RuleManagerFactory(final ServiceData serviceData) {
    super();
    this.serviceData = serviceData;
  }


  /**
   * @param reviewRulepramCol paramCollection
   * @return the rule manager
   */
  public IRuleManager createRuleManager(final ReviewRuleParamCol reviewRulepramCol) throws IcdmException {
    SSDServiceHandler serviceHandler = new SSDServiceHandler(this.serviceData);
    if (reviewRulepramCol.getParamCollection() instanceof Function) {
      return new FunctionRuleManager(serviceHandler,serviceData);
    }
    if (reviewRulepramCol.getParamCollection() instanceof RuleSet) {
      return new RuleSetRuleManager(serviceHandler, (RuleSet) reviewRulepramCol.getParamCollection(),serviceData);
    }

    if (reviewRulepramCol.getParamCollection() instanceof CompPackage) {
      return new CompPkgRuleManager(serviceHandler, (CompPackage) reviewRulepramCol.getParamCollection(),serviceData);
    }
    return null;
  }


  /**
   * @param paramCol paramCol
   * @return
   */
  public AbstractImportRuleManager createCalImportRuleManager(final ParamCollection paramCol) throws IcdmException {


    if (paramCol instanceof Function) {
      return new FunctionImportRuleManager(this.serviceData, new SSDServiceHandler(this.serviceData));
    }
    if (paramCol instanceof RuleSet) {
      return new RulesetImportRuleManager(this.serviceData, new SSDServiceHandler(this.serviceData));
    }
    if (paramCol instanceof CompPackage) {
      return new CPImportRuleManager(this.serviceData, new SSDServiceHandler(this.serviceData));
    }

    return null;
  }

}
