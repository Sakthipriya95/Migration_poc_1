/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;
import java.util.SortedSet;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;

/**
 * @author rgo7cob
 */
public class ConfigBasedRuleInput<C extends ParamCollection> {

  /**
   * attr Val Model Set
   */
  private SortedSet<AttributeValueModel> attrValueModSet;
  /**
   * label names
   */
  private List<String> labelNames;
  /**
   * Param Collection object
   */
  private ReviewRuleParamCol<C> paramCol;

  /**
   * @return the attrValueModSet
   */
  public SortedSet<AttributeValueModel> getAttrValueModSet() {
    return this.attrValueModSet;
  }

  /**
   * @param attrValueModSet the attrValueModSet to set
   */
  public void setAttrValueModSet(final SortedSet<AttributeValueModel> attrValueModSet) {
    this.attrValueModSet = attrValueModSet;
  }

  /**
   * @return the labelNames
   */
  public List<String> getLabelNames() {
    return this.labelNames;
  }

  /**
   * @param labelNames the labelNames to set
   */
  public void setLabelNames(final List<String> labelNames) {
    this.labelNames = labelNames;
  }


  /**
   * @return the paramCol
   */
  public ReviewRuleParamCol<C> getParamCol() {
    return this.paramCol;
  }


  /**
   * @param paramCol the paramCol to set
   */
  public void setParamCol(final ReviewRuleParamCol<C> paramCol) {
    this.paramCol = paramCol;
  }


}
