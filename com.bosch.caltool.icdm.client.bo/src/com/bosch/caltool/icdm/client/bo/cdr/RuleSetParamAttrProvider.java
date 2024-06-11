/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;

/**
 * @author rgo7cob
 */
public class RuleSetParamAttrProvider implements IParamAttrProvider<RuleSetParameterAttr> {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<RuleSetParameterAttr> getParamAttrs(final IParameter param, final List<Attribute> attributes,
      final Long ruleSetId) {
    List<RuleSetParameterAttr> paramAttrList = new ArrayList<>();
    for (Attribute attribute : attributes) {
      RuleSetParameterAttr paramAttr = new RuleSetParameterAttr();
      paramAttr.setAttrId(attribute.getId());
      RuleSetParameter ruleSetParam = (RuleSetParameter) param;
      paramAttr.setParamId(ruleSetParam.getParamId());
      paramAttr.setRuleSetParamId(param.getId());
      paramAttr.setName(attribute.getName());
      paramAttr.setRuleSetId(ruleSetId);
      paramAttrList.add(paramAttr);
    }
    return paramAttrList;
  }

}
