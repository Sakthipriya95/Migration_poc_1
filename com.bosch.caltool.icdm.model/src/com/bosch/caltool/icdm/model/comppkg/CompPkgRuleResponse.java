/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author dmr1cob
 */
public class CompPkgRuleResponse implements IParamRuleResponse<CompPkgParamAttr, CompPkgParameter> {

  private Map<String, CompPkgParameter> paramMap = new HashMap<>();

  private Map<String, List<ReviewRule>> rulesMap = new HashMap<>();

  private Map<String, List<CompPkgParamAttr>> attrMap = new HashMap<>();

  private Map<Long, Attribute> attrValModelMap = new HashMap<>();

  private Map<Long, AttributeValueDontCare> dontCareAttrValueMap = new HashMap<>();


  /**
   * @return the attrValModelMap
   */
  public Map<Long, Attribute> getAttrObjMap() {
    return this.attrValModelMap;
  }


  /**
   * @param attrValModelMap the attrValModelMap to set
   */
  public void setAttrValModelMap(final Map<Long, Attribute> attrValModelMap) {
    this.attrValModelMap = attrValModelMap;
  }


  /**
   * @param paramMap the paramMap to set
   */
  public void setParamMap(final Map<String, CompPkgParameter> paramMap) {
    this.paramMap = paramMap;
  }


  /**
   * @param rulesMap the rulesMap to set
   */
  public void setRulesMap(final Map<String, List<ReviewRule>> rulesMap) {
    this.rulesMap = rulesMap;
  }


  /**
   * @return the attrMap
   */
  @Override
  public Map<String, List<CompPkgParamAttr>> getAttrMap() {
    return this.attrMap;
  }


  /**
   * @param attrMap the attrMap to set
   */
  public void setAttrMap(final Map<String, List<CompPkgParamAttr>> attrMap) {
    this.attrMap = attrMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, List<ReviewRule>> getReviewRuleMap() {
    return this.rulesMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, CompPkgParameter> getParamMap() {
    return this.paramMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, AttributeValueDontCare> getDontCareAttrValueMap() {
    return this.dontCareAttrValueMap;
  }

  /**
   * @param dontCareAttrValueMap the dontCareAttrValueMap to set
   */
  public void setDontCareAttrValueMap(final Map<Long, AttributeValueDontCare> dontCareAttrValueMap) {
    this.dontCareAttrValueMap = dontCareAttrValueMap;
  }


}
