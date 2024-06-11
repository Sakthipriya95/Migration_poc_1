/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.general;

import com.bosch.calcomp.externallink.ILink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.calcomp.externallink.process.ILinkValidator;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.MODEL_TYPE;

/**
 * @author bne4cob
 */
public enum EXTERNAL_LINK_TYPE implements ILinkType {
                                                     /**
                                                      * PIDC Version link type
                                                      */
                                                     PIDC_VERSION(
                                                                  EXTERNAL_LINK_TYPE.KEY_PIDC_VERS,
                                                                  MODEL_TYPE.PIDC_VERSION),
                                                     /**
                                                      * PIDC Link type
                                                      */
                                                     PIDC(EXTERNAL_LINK_TYPE.KEY_PIDC, MODEL_TYPE.PIDC),
                                                     /**
                                                      * PIDC Version link type
                                                      */
                                                     PIDC_VARIANT(
                                                                  EXTERNAL_LINK_TYPE.KEY_PIDC_VARIANT,
                                                                  MODEL_TYPE.VARIANT),
                                                     /**
                                                     *
                                                     */
                                                     SUB_VARIANT(
                                                                 EXTERNAL_LINK_TYPE.KEY_PIDC_SUB_VARIANT,
                                                                 MODEL_TYPE.SUB_VARIANT),
                                                     /**
                                                      * A2L File
                                                      */
                                                     A2L_FILE(
                                                              EXTERNAL_LINK_TYPE.KEY_A2L_FILE,
                                                              "A2L File",
                                                              MODEL_TYPE.PIDC_A2L),
                                                     /**
                                                      * CDR Result link type
                                                      */
                                                     CDR_RESULT(
                                                                EXTERNAL_LINK_TYPE.KEY_CDR_RESULT,
                                                                "CDR Result",
                                                                MODEL_TYPE.CDR_RESULT),
                                                     /**
                                                      * CDR Result variant
                                                      */
                                                     CDR_RESULT_VAR(
                                                                    EXTERNAL_LINK_TYPE.KEY_CDR_RESULT,
                                                                    "CDR Result",
                                                                    MODEL_TYPE.CDR_RES_VARIANTS),
                                                     /**
                                                      * Function parameter
                                                      */
                                                     CDR_FUNCTION_PARAM(
                                                                        EXTERNAL_LINK_TYPE.KEY_CDR_PARAM,
                                                                        MODEL_TYPE.CDR_FUNC_PARAM),
                                                     
                                                     /**
                                                     * Review Questionnaire response
                                                     */
                                                    QNAIRE_RESPONSE(EXTERNAL_LINK_TYPE.KEY_QNAIRE_RESPONSE,"Questionnaire Response",MODEL_TYPE.RVW_QNAIRE_RESPONSE),
                                                    
                                                    /**
                                                     * Review Questionnaire response
                                                     */
                                                    QNAIRE_RESPONSE_VAR(EXTERNAL_LINK_TYPE.KEY_QNAIRE_RESPONSE,"Questionnaire Response",MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT),
                                                     
                                                     /**
                                                      * Rule Set link type
                                                      */
                                                     RULE_SET(EXTERNAL_LINK_TYPE.KEY_RULE_SET, MODEL_TYPE.CDR_RULE_SET),
                                                     /**
                                                      * Usecase to pidc version link type
                                                      */
                                                     USE_CASE(
                                                              EXTERNAL_LINK_TYPE.KEY_USECASE_PIDC_VERS,
                                                              MODEL_TYPE.USE_CASE),
                                                     /**
                                                      * Usecase to pidc version link type
                                                      */
                                                     USE_CASE_GROUP(
                                                                    EXTERNAL_LINK_TYPE.KEY_USECASE_PIDC_VERS,
                                                                    MODEL_TYPE.USE_CASE_GROUP),
                                                     /**
                                                     *
                                                     */
                                                     USE_CASE_SECT(
                                                                   EXTERNAL_LINK_TYPE.KEY_USECASE_PIDC_VERS,
                                                                   MODEL_TYPE.USE_CASE_SECT),

                                                     /**
                                                      * Project Usecase link type
                                                      */
                                                     PROJECT_USE_CASE(
                                                                      EXTERNAL_LINK_TYPE.KEY_PROJECT_USECASE_PIDC_VERS,
                                                                      MODEL_TYPE.UC_FAV);

  /**
   * PIDC type Key
   */
  private static final String KEY_PIDC = "pidid";
  /**
   * PIDC Version type Key
   */
  private static final String KEY_PIDC_VERS = "pidvid";
  /**
   * Usecase PIDC Version type Key
   */
  private static final String KEY_USECASE_PIDC_VERS = "pidvucid";
  /**
   * Project Usecase PIDC Version type Key
   */
  private static final String KEY_PROJECT_USECASE_PIDC_VERS = "pidcprjucid";
  /**
   * PIDC variant type Key
   */
  private static final String KEY_PIDC_VARIANT = "pidvarid";
  /**
   * PIDC subvariant type Key
   */
  private static final String KEY_PIDC_SUB_VARIANT = "pidcsubvarid";

  /**
   * A2L file type Key
   */
  private static final String KEY_A2L_FILE = "a2lid";

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
   * Questionnaire Response type Key
   */
  private static final String KEY_QNAIRE_RESPONSE = "qnrrespid";

  /**
   * Key
   */
  private String key;
  private String typeName;
  /**
   * Display text of the type
   */
  private MODEL_TYPE modelType;

  /**
   * Constructor
   */
  private EXTERNAL_LINK_TYPE(final String key, final MODEL_TYPE modelType) {
    this.key = key;
    this.modelType = modelType;
  }

  /**
   * Constructor
   */
  private EXTERNAL_LINK_TYPE(final String key, final String typeName, final MODEL_TYPE modelType) {
    this.key = key;
    this.typeName = typeName;
    this.modelType = modelType;
  }

  /**
   * Get the external link type from the code
   *
   * @param typeCode type code
   * @return link type, or null, if type code is not mapped
   */
  public static EXTERNAL_LINK_TYPE getLinkType(final String typeCode) {
    EXTERNAL_LINK_TYPE ret = null;
    for (EXTERNAL_LINK_TYPE e : EXTERNAL_LINK_TYPE.values()) {
      if (e.modelType.getTypeCode().equals(typeCode)) {
        ret = e;
        break;
      }
    }
    return ret;
  }


  /**
   * Find the link type of the given model object
   *
   * @param model input
   * @return link type
   * @throws NullPointerException if input is null
   */
  public static ILinkType getLinkType(final IModel model) {
    if (model == null) {
      throw new NullPointerException("Input cannot be null");
    }

    Class<?> objClz = model.getClass();
    EXTERNAL_LINK_TYPE ret = null;
    for (EXTERNAL_LINK_TYPE e : EXTERNAL_LINK_TYPE.values()) {
      if (e.modelType.getTypeClass().equals(objClz)) {
        ret = e;
        break;
      }
    }
    return ret;
  }

  /**
   * @return key
   */
  @Override
  public String getKey() {
    return this.key;
  }

  /**
   * @return name
   */
  @Override
  public String getName() {
    return name();
  }


  /**
   * @return object type
   */
  @Override
  public Class<?> getObjectType() {
    return this.modelType.getTypeClass();
  }

  /**
   * @return Display text
   */
  @Override
  public String getTypeDisplayText() {
    return this.typeName == null ? this.modelType.getTypeName() : this.typeName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkValidator getValidator() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <O extends ILinkableObject> ILink<O> createNewLink(final O obj) {
    throw new UnsupportedOperationException("The method is not implemented");
  }


}
