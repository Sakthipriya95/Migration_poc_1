/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;

/**
 * @author rgo7cob
 */
public class ParamAttrProvider implements IParamAttrProvider<ParameterAttribute> {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ParameterAttribute> getParamAttrs(final IParameter param, final List<Attribute> attributes,
      final Long ruleSetId) {

    List<ParameterAttribute> paramAttrList = new ArrayList<>();
    for (Attribute attribute : attributes) {
      ParameterAttribute paramAttr = new ParameterAttribute();
      paramAttr.setAttrId(attribute.getId());
      paramAttr.setParamId(param.getId());
      paramAttr.setName(attribute.getName());
      paramAttrList.add(paramAttr);
    }

    return paramAttrList;
  }

}
