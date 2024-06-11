/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import com.bosch.caltool.icdm.model.apic.IPastableItem;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author rgo7cob
 */
public class CdrRuleWrapper implements IPastableItem {

  /**
   * @param rule rule
   * @param paramName paramName
   * @param paramType paramType
   */
  public CdrRuleWrapper(final ReviewRule rule, final String paramName, final String paramType) {
    super();
    this.rule = rule;
    this.paramName = paramName;
    this.paramType = paramType;
  }


  private final ReviewRule rule;


  /**
   * @return the rule
   */
  public ReviewRule getRule() {
    return this.rule;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @return the paramType
   */
  public String getParamType() {
    return this.paramType;
  }


  private final String paramName;
  private final String paramType;


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    return true;
  }

}
