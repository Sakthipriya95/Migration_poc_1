/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionConfig;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParamAttr;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParamAttr;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParametersSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Type of entity
 *
 * @author bne4cob
 */
public enum CDREntityType implements IEntityType<AbstractCdrObject, CDRDataProvider> {


                                                                                      /**
                                                                                       * CDR Review File - TRvwFile
                                                                                       */
                                                                                      CDR_RES_FILE(TRvwFile.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return null;
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbCDRFile(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_FILE;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * CDR Review Function -
                                                                                       * TRvwFunction
                                                                                       */
                                                                                      CDR_RES_FUNCTION(
                                                                                                       TRvwFunction.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCDRResFunction(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbCDRResFunction(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RES_FUN;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * CDR Parameter - TRvwParameter
                                                                                       */
                                                                                      CDR_RES_PARAMETER(
                                                                                                        TRvwParameter.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCDRResParameter(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbCDRResParameter(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RES_PRM;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * CDR Participants -
                                                                                       * TRvwParticipant
                                                                                       */
                                                                                      CDR_PARTICIPANT(
                                                                                                      TRvwParticipant.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getDataCache()
                                                                                              .getCDRParticipant(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbCDRParticipant(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_USER;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * CDR Review result - TRvwResult
                                                                                       */
                                                                                      CDR_RESULT(TRvwResult.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCDRResult(entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbCDRResult(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RESULT;
                                                                                        }

                                                                                      },

                                                                                      /**
                                                                                       * Icdm-1214 CDR Review result -
                                                                                       * TRvwResult
                                                                                       */
                                                                                      CDR_RES_ATTR_VALUE(
                                                                                                         TRvwAttrValue.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCdrRvwAttrVal(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbRvwAttrVal(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RVW_ATTR_VAL;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * TRvwVariant
                                                                                       */
                                                                                      CDR_RES_VARIANTS(
                                                                                                       TRvwVariant.class) {

                                                                                        // Icdm-2084 CDR Review varaint
                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCdrRvwVar(entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbRvwVaraint(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RVW_VAR;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * Icdm-1032-new Enumeration for
                                                                                       * Param Attr class
                                                                                       */
                                                                                      PARAMETER_ATTR(TParamAttr.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getParamAttr(entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbParamAttr(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_PARAM_ATTR;
                                                                                        }


                                                                                      },
                                                                                      /**
                                                                                       * CalData Review Function-
                                                                                       * TFunction
                                                                                       */
                                                                                      CDR_FUNCTION(TFunction.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          TFunction dbParam =
                                                                                              dataProvider
                                                                                                  .getEntityProvider()
                                                                                                  .getDbFunction(
                                                                                                      entityID);
                                                                                          return dataProvider
                                                                                              .getCDRFunction(
                                                                                                  dbParam.getId());
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbFunction(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_FUNCTION;
                                                                                        }

                                                                                        @Override
                                                                                        public String getEntityTypeString() {
                                                                                          return ApicConstants.FUNC_NODE_TYPE;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * CalData Review Function version
                                                                                       * - T_PARAMETER
                                                                                       */
                                                                                      /* iCDM-471 */
                                                                                      CDR_FUNC_PARAM(TParameter.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          TParameter dbParam =
                                                                                              dataProvider
                                                                                                  .getEntityProvider()
                                                                                                  .getDbFunctionParameter(
                                                                                                      entityID);
                                                                                          return dataProvider
                                                                                              .getFunctionParameter(
                                                                                                  dbParam.getName(),
                                                                                                  dbParam.getPtype());
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbFunctionParameter(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_FUNC_PARAM;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * Rule Set (iCDM-1366)
                                                                                       */
                                                                                      CDR_RULE_SET(TRuleSet.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          TRuleSet dbRuleSet =
                                                                                              dataProvider
                                                                                                  .getEntityProvider()
                                                                                                  .getDbRuleSet(
                                                                                                      entityID);
                                                                                          return dataProvider
                                                                                              .getRuleSet(dbRuleSet
                                                                                                  .getRsetId());
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbRuleSet(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RULE_SET;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * Rule Set (iCDM-1366)
                                                                                       */
                                                                                      CDR_RULE_SET_PARAM(
                                                                                                         TRuleSetParam.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          TRuleSetParam dbRuleSetParam =
                                                                                              dataProvider
                                                                                                  .getEntityProvider()
                                                                                                  .getDbRuleSetParam(
                                                                                                      entityID);
                                                                                          return dataProvider
                                                                                              .getRuleSetParam(
                                                                                                  dbRuleSetParam
                                                                                                      .getRsetParamId());
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbRuleSetParam(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RULE_SET_PARAM;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * Rule Set (iCDM-1366)
                                                                                       */
                                                                                      CDR_RULE_SET_PARAM_ATTR(
                                                                                                              TRuleSetParamAttr.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          TRuleSetParamAttr dbRuleSetParamAttr =
                                                                                              dataProvider
                                                                                                  .getEntityProvider()
                                                                                                  .getDbRuleSetParamAttr(
                                                                                                      entityID);
                                                                                          return dataProvider
                                                                                              .getRuleSetParamAttr(
                                                                                                  dbRuleSetParamAttr
                                                                                                      .getRsetParAttrId());
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbRuleSetParamAttr(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_RULE_SET_PARAM_ATTR;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * Workpackage division -
                                                                                       * TWorkpackageDivision
                                                                                       */
                                                                                      WORKPACKAGE_DIVISION(
                                                                                                           TWorkpackageDivision.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getWorkPackageDivision(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbWrkPkgDiv(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_WORKPACKAGE_DIVSION;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * Workpackage - TWorkpackage
                                                                                       */
                                                                                      ICDM_WORKPACKAGE(
                                                                                                       TWorkpackage.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getIcdmWorkPackage(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbIcdmWorkPackage(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_ICDM_WORKPACKAGE;
                                                                                        }

                                                                                      },

                                                                                      /**
                                                                                       * Questionnaire - TQuestionnaire
                                                                                       */
                                                                                      QUESTIONNAIRE(
                                                                                                    TQuestionnaire.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestionnaire(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQuestionnaire(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QUESTIONNAIRE;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * Questionnaire Version-
                                                                                       * TQuestionnaireVersion
                                                                                       */
                                                                                      QUESTIONNAIRE_VERSION(
                                                                                                            TQuestionnaireVersion.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestionnaireVersion(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQuestionnaireVersion(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QUESTIONNAIRE_VERSION;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * Question - TQuestion
                                                                                       */
                                                                                      QUESTION(TQuestion.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestion(entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQuestion(entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QUESTION;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * QuestionConfig - TQuestion
                                                                                       */
                                                                                      QUESTION_CONFIG(
                                                                                                      TQuestionConfig.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestionConfig(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQuestionConfig(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QUESTION_CONFIG;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * QUESTION_DEPEN_ATTR -
                                                                                       * TQuestionDepenAttribute
                                                                                       */
                                                                                      QUESTION_DEPEN_ATTR(
                                                                                                          TQuestionDepenAttribute.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestionDepenAttribute(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQuestionDepenAttr(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QUESTION_DEPEN_ATTR;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * QUESTION_DEPEN_ATTR_VALUE -
                                                                                       * TQuestionDepenAttribute
                                                                                       */
                                                                                      QUESTION_DEPEN_ATTR_VALUE(
                                                                                                                TQuestionDepenAttrValue.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestionDepenAttrValue(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQuestionDepenAttrValue(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QUS_DEPN_VAL;
                                                                                        }

                                                                                      },
                                                                                      /**
                                                                                       * Questionnaire response -
                                                                                       * TRvwQnaireResponse
                                                                                       */
                                                                                      RVW_QNAIRE_RESPONSE(
                                                                                                          TRvwQnaireResponse.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQuestionnaireResponse(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQnaireResponse(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QNAIRE_RESP;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * RVW_QUESTIONNAIRE -
                                                                                       * TRvwQuestionnaire
                                                                                       */
//                                                                                      RVW_QUESTIONNAIRE(TRvwQnaireResult.class) {
//
//                                                                                        @Override
//                                                                                        public AbstractCdrObject getDataObject(
//                                                                                            final CDRDataProvider dataProvider,
//                                                                                            final Long entityID) {
//                                                                                          return dataProvider
//                                                                                              .getReviewQuestionnaire(
//                                                                                                  entityID);
//                                                                                        }
//
//                                                                                        @Override
//                                                                                        public long getVersion(
//                                                                                            final CDRDataProvider dataProvider,
//                                                                                            final Long entityID) {
//                                                                                          return dataProvider
//                                                                                              .getEntityProvider()
//                                                                                              .getDbReviewQuestionnaire(
//                                                                                                  entityID)
//                                                                                              .getVersion();
//                                                                                        }
//
//                                                                                        @Override
//                                                                                        public int getOrder() {
//                                                                                          return ORDER_RVW_QUESTIONNAIRE;
//                                                                                        }
//                                                                                      },
                                                                                      /**
                                                                                       * RVW_QNAIRE_ANSWER -
                                                                                       * TRvwQnaireAnswer
                                                                                       */
                                                                                      RVW_QNAIRE_ANS(
                                                                                                     TRvwQnaireAnswer.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getReviewQnaireAnswer(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbReviewQnaireAnswer(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_RVW_QNAIRE_ANSWER;
                                                                                        }
                                                                                      },
                                                                                      /**
                                                                                       * RVW_QNAIRE_ANSWER -
                                                                                       * TRvwQnaireAnswer
                                                                                       */
                                                                                      QNAIRE_ANS_OPEN_POINTS(
                                                                                                             TRvwQnaireAnswerOpl.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getQnaireAnsOpenPoints(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbQnaireAnsOpenPoint(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_QS_ANS_OPEN_POINTS;
                                                                                        }
                                                                                      },
                                                                                      CDR_RESULT_SECONDARY(
                                                                                                           TRvwResultsSecondary.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCDRResSecondary(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbResSecondary(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RESULT_SEC;
                                                                                        }

                                                                                      },
                                                                                      CDR_RESULT_PARAM_SECONDARY(
                                                                                                                 TRvwParametersSecondary.class) {

                                                                                        @Override
                                                                                        public AbstractCdrObject getDataObject(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getCDRResSecondary(
                                                                                                  entityID);
                                                                                        }

                                                                                        @Override
                                                                                        public long getVersion(
                                                                                            final CDRDataProvider dataProvider,
                                                                                            final Long entityID) {
                                                                                          return dataProvider
                                                                                              .getEntityProvider()
                                                                                              .getDbResParamSecondary(
                                                                                                  entityID)
                                                                                              .getVersion();
                                                                                        }

                                                                                        @Override
                                                                                        public int getOrder() {
                                                                                          return ORDER_CDR_RVW_PARAM_SEC;
                                                                                        }

                                                                                      };


  private static final int ORDER_CDR_FUNCTION = 1;
  private static final int ORDER_CDR_FUNC_PARAM = 2;

  private static final int ORDER_CDR_RULE_SET = 3;
  private static final int ORDER_CDR_RULE_SET_PARAM = 4;

  // Icdcm-1032 loaded after CDRFuncParam
  private static final int ORDER_PARAM_ATTR = 5;
  private static final int ORDER_RULE_SET_PARAM_ATTR = 6;

  private static final int ORDER_CDR_RESULT = 7;
  private static final int ORDER_CDR_RVW_VAR = 8;
  private static final int ORDER_CDR_USER = 9;
  private static final int ORDER_CDR_RES_FUN = 10;
  private static final int ORDER_CDR_RES_PRM = 11;
  private static final int ORDER_CDR_FILE = 12;
  private static final int ORDER_CDR_RVW_ATTR_VAL = 13;
  private static final int ORDER_WORKPACKAGE_DIVSION = 14;
  private static final int ORDER_ICDM_WORKPACKAGE = 15;
  private static final int ORDER_QUESTIONNAIRE = 16;
  private static final int ORDER_QUESTIONNAIRE_VERSION = 17;
  private static final int ORDER_QUESTION = 18;
  private static final int ORDER_QUESTION_CONFIG = 19;
  private static final int ORDER_QUESTION_DEPEN_ATTR = 20;
  private static final int ORDER_QUS_DEPN_VAL = 21;
  private static final int ORDER_QNAIRE_RESP = 22;
  private static final int ORDER_CDR_RESULT_SEC = 26;
  private static final int ORDER_CDR_RVW_PARAM_SEC = 27;
  /**
   * Order of review questionnaire object
   */
  private static final int ORDER_RVW_QUESTIONNAIRE = 23;
  /**
   * Order of questionnaire answer
   */
  private static final int ORDER_RVW_QNAIRE_ANSWER = 24;
  /**
   * Order of questionnaire response - open points
   */
  private static final int ORDER_QS_ANS_OPEN_POINTS = 25;
  /**
   * Entity class for this enumeration value
   */
  private Class<?> entityClass;

  /**
   * Constructor
   *
   * @param entityClass entity class
   */
  CDREntityType(final Class<?> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * @return the entity class of this enumeration value
   */
  @Override
  public Class<?> getEntityClass() {
    return this.entityClass;
  }

  /**
   * Retrieves the entity type value for the given entity class
   *
   * @param entityClass entity class
   * @return the entity type
   */
  public static CDREntityType getEntityType(final Class<?> entityClass) {
    for (CDREntityType eType : CDREntityType.values()) {
      if (eType.getEntityClass().equals(entityClass)) {
        return eType;
      }
    }
    return null;

  }

  @Override
  public String getEntityTypeString() {
    return name();
  }

  /**
   * @return true for TOP_LEVEL_ENTITY.Modifying the data for TOP_LEVEL_ENTITY should not stop the command execution.
   */
  @Override
  public boolean stopCommandForEntityUpdate() {

    return true;
  }
}
