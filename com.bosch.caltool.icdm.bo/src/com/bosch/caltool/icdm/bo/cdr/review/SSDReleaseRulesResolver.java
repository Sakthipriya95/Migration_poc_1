/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;

/**
 * @author bru2cob
 */
public class SSDReleaseRulesResolver extends AbstractSimpleBusinessObject implements IReviewRulesResolver {

  /**
   * @param serviceData Service Data
   */
  public SSDReleaseRulesResolver(final ServiceData serviceData) {
    super(serviceData);
    // TODO Auto-generated constructor stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile getRules() {
    // TODO Auto-generated method stub
    return null;
  }

}
