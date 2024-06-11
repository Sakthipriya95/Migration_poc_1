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
public class SSDFileRulesResolver extends AbstractSimpleBusinessObject implements IReviewRulesResolver {

  /**
   * @param serviceData Service Data
   */
  public SSDFileRulesResolver(final ServiceData serviceData) {
    super(serviceData);
    // TODO Auto-generated method stub
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
