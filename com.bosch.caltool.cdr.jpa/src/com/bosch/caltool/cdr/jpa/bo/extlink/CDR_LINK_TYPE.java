/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.extlink;

import com.bosch.calcomp.externallink.ILink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.calcomp.externallink.process.ILinkValidator;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.cdr.jpa.bo.CDRResult;
import com.bosch.caltool.cdr.jpa.bo.RuleSet;

/**
 * External Link types in CDR module
 *
 * @author bne4cob
 */
// ICDM-1649
@Deprecated
public enum CDR_LINK_TYPE implements ILinkType {

                                                /**
                                                 * CDR Result link type
                                                 */
                                                CDR_RESULT(
                                                           CDR_LINK_TYPE.KEY_CDR_RESULT,
                                                           CDRResult.class,
                                                           "CDR Result") {

                                                  /**
                                                   * {@inheritDoc}
                                                   */
                                                  @Override
                                                  public ILink createNewLink(final ILinkableObject obj) {
                                                    CDRResult cdrRes = (CDRResult) obj;
                                                    return new CdrResultExternalLink(cdrRes);
                                                  }


                                                },
                                                /**
                                                 * CDR Result link type
                                                 */
                                                CDR_FUNCTION_PARAM(
                                                                   CDR_LINK_TYPE.KEY_CDR_PARAM,
                                                                   CDRFuncParameter.class,
                                                                   "CDR Function Param") {

                                                  /**
                                                   * {@inheritDoc}
                                                   */
                                                  @Override
                                                  public ILink createNewLink(final ILinkableObject obj) {
                                                    CDRFuncParameter cdrRes = (CDRFuncParameter) obj;
                                                    return new CDRFuncParamExternalLink(cdrRes);
                                                  }


                                                },
                                                /**
                                                 * Rule Set link type
                                                 */
                                                RULE_SET(CDR_LINK_TYPE.KEY_RULE_SET, RuleSet.class, "Rule Set") {

                                                  /**
                                                   * {@inheritDoc}
                                                   */
                                                  @Override
                                                  public ILink createNewLink(final ILinkableObject obj) {
                                                    RuleSet ruleset = (RuleSet) obj;
                                                    return new RuleSetExternalLink(ruleset);
                                                  }


                                                };

  /**
   * CDR Result type Key
   */
  private static final String KEY_CDR_RESULT = "cdrid";
  /**
   * RULE SET type Key
   */
  private static final String KEY_RULE_SET = "rulesetid";

  /**
   * CDR Result type Key
   */
  private static final String KEY_CDR_PARAM = "paramname";

  /**
   * Key
   */
  private String key;

  /**
   * Display text of the type
   */
  private String typeDisplayTxt;

  /**
   * Linkable Object's type
   */
  private final Class<?> linkObjectType;


  /**
   * Constructor
   */
  private CDR_LINK_TYPE(final String key, final Class<?> linkObjectType, final String typeDisplayTxt) {
    this.key = key;
    this.linkObjectType = linkObjectType;
    this.typeDisplayTxt = typeDisplayTxt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKey() {
    return this.key;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkValidator getValidator() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getObjectType() {
    return this.linkObjectType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTypeDisplayText() {
    return this.typeDisplayTxt;
  }
}
