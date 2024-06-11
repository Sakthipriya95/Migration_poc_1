/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.review.IReviewParamResolver;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author bru2cob
 */
public class CompliParamResolver implements IReviewParamResolver {

  ServiceData serviceData;

  /**
   * @param serviceData ServiceData
   */
  public CompliParamResolver(final ServiceData serviceData) {
    this.serviceData = serviceData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getParameters() throws IcdmException {
    ParameterLoader parameterLoader = new ParameterLoader(this.serviceData);
    Set<String> compliQSSDLabelSet = new HashSet<>();
    // add all compli labels
    compliQSSDLabelSet.addAll(parameterLoader.getCompliParamWithType().keySet());
    // add all qssd labels
    compliQSSDLabelSet.addAll(parameterLoader.getQssdParams().keySet());
    return compliQSSDLabelSet.isEmpty() ? new ArrayList<>() : new ArrayList<>(compliQSSDLabelSet);

  }

}

