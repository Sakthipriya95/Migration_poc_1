/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.SSDMessageWrapper;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CompPackageRuleServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class CompPackageRuleProvider implements ReviewRuleDataProvider<CompPackage> {

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageWrapper createCdrRule(final ReviewRuleParamCol reviewRuleParamCol) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageWrapper updateCdrRule(final ReviewRuleParamCol reviewRuleParamCol) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSDMessageWrapper deleteRule(final ReviewRuleParamCol reviewRuleParamCol) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewRuleParamCol readRules(final ReviewRuleParamCol reviewRuleParamCol) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws ApicWebServiceException
   */
  @Override
  public Map searchRuleForDep(final ConfigBasedRuleInput configInput) throws ApicWebServiceException {
    CompPackageRuleServiceClient client = new CompPackageRuleServiceClient();


    return client.searchRuleForDep(configInput);


  }
}

