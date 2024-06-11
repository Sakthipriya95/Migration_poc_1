/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;

/**
 * @author rgo7cob
 * @param <D> extends IParameterAttribute
 * @param <P> extends IParameter
 */
public interface IParamRuleResponse<D extends IParameterAttribute, P extends IParameter> {


  /**
   * @return the paramMap
   */
  public abstract Map<String, P> getParamMap();


  /**
   * @return the paramMap
   */
  public abstract Map<String, List<ReviewRule>> getReviewRuleMap();

  /**
   * @return the attrMap
   */
  public abstract Map<String, List<D>> getAttrMap();


  /**
   * @return the attr val model map
   */
  public abstract Map<Long, Attribute> getAttrObjMap();

  /**
   * @return key - attribute id, value - Don't care attribute value object
   */
  public Map<Long, AttributeValueDontCare> getDontCareAttrValueMap();


}
