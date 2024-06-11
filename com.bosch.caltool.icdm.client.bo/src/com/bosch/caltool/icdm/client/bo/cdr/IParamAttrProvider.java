/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.List;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;

/**
 * @author rgo7cob
 */
public interface IParamAttrProvider<D extends IParameterAttribute> {

  // Get list of param attributes
  public List<D> getParamAttrs(IParameter param, final List<Attribute> attribute, Long ruleSetId);


}
