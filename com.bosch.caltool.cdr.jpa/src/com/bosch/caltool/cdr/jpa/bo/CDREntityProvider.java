/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.AbstractEntityProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
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
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
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

/**
 * Class to interact with entities
 *
 * @author BNE4COB
 */
class CDREntityProvider extends AbstractEntityProvider {

  /**
   * logger
   */
  protected ILoggerAdapter logger;

  /**
   * Constructor
   *
   * @param logger logger
   * @param entityTypes entity types
   */
  public CDREntityProvider(final ILoggerAdapter logger, final IEntityType<?, ?>... entityTypes) {
    super(entityTypes);
    this.logger = logger;
  }

  /**
   * @param fileID primary key
   * @return the entity
   */
  protected synchronized TRvwFile getDbCDRFile(final Long fileID) {
    return getEm().find(TRvwFile.class, fileID);
  }

  /**
   * @param funcId primary key
   * @return the entity
   */
  protected synchronized TRvwFunction getDbCDRResFunction(final Long funcId) {
    return getEm().find(TRvwFunction.class, funcId);
  }

  /**
   * @param paramId primary key
   * @return the entity
   */
  protected synchronized TRvwParameter getDbCDRResParameter(final Long paramId) {
    return getEm().find(TRvwParameter.class, paramId);
  }

  /**
   * @param participantId primary key
   * @return the entity
   */
  protected synchronized TRvwParticipant getDbCDRParticipant(final Long participantId) {
    return getEm().find(TRvwParticipant.class, participantId);
  }

  /**
   * @param resultId primary key
   * @return the entity
   */
  protected synchronized TRvwResult getDbCDRResult(final Long resultId) {
    return getEm().find(TRvwResult.class, resultId);
  }

  /**
   * @param secResID primary key
   * @return the entity
   */
  protected synchronized TRvwResultsSecondary getDbResSecondary(final Long secResID) {
    return getEm().find(TRvwResultsSecondary.class, secResID);
  }

  /**
   * @param paramSecRvwID primary key
   * @return the entity
   */
  protected synchronized TRvwParametersSecondary getDbParamSecondary(final Long paramSecRvwID) {
    return getEm().find(TRvwParametersSecondary.class, paramSecRvwID);
  }

  protected TabvApicUser getDbApicUser(final Long userID) {
    return getEm().find(TabvApicUser.class, userID);
  }

  protected TabvProjectidcard getDbPidc(final Long pidcID) {
    return getEm().find(TabvProjectidcard.class, pidcID);
  }

  protected TPidcA2l getDbPidcA2l(final Long pidcA2lID) {
    return getEm().find(TPidcA2l.class, pidcA2lID);
  }

  /**
   * @param pidcVariantId entity ID
   * @return the PIDC Variant entity
   */
  protected synchronized TabvProjectVariant getDbPidcVariant(final long pidcVariantId) {
    return getEm().find(TabvProjectVariant.class, pidcVariantId);
  }

  /**
   * @param funID primary key
   * @return the entity
   */
  protected synchronized TFunction getDbFunction(final Long funID) {
    return getEm().find(TFunction.class, funID);
  }

  /**
   * @param paramID primary key
   * @return the entity
   */
  protected synchronized TParameter getDbParameter(final Long paramID) {
    return getEm().find(TParameter.class, paramID);
  }

  /**
   * @param fileID File ID
   * @return ICDM file entity
   */
  public TabvIcdmFile getDbIcdmFile(final Long fileID) {
    return getEm().find(TabvIcdmFile.class, fileID);
  }

  /**
   * Icdm-1214
   *
   * @param rvwValID review Attr Val id
   * @return the Rvw attr Value id
   */
  public TRvwAttrValue getDbRvwAttrVal(final Long rvwValID) {
    return getEm().find(TRvwAttrValue.class, rvwValID);
  }

  /**
   * @param rvwVarID review Attr Val id
   * @return the review varaint
   */
  public TRvwVariant getDbRvwVaraint(final Long rvwVarID) {
    // Icdm-2084
    return getEm().find(TRvwVariant.class, rvwVarID);
  }

  /**
   * Icdm-1214
   *
   * @param valueID primary key
   * @return the entity
   */
  protected TabvAttrValue getDbValue(final Long valueID) {
    synchronized (this) {
      return getEm().find(TabvAttrValue.class, valueID);
    }
  }

  /**
   * Icdm-1238
   *
   * @param attrID primary key
   * @return the entity
   */
  protected TabvAttribute getDbAttribute(final Long attrID) {
    synchronized (this) {
      return getEm().find(TabvAttribute.class, attrID);
    }
  }

  // iCDM-1366
  /**
   * @param rsetId primary key
   * @return the entity
   */
  protected synchronized TRuleSet getDbRuleSet(final Long rsetId) {
    return getEm().find(TRuleSet.class, rsetId);
  }

  /**
   * @param rsetParamId primary key
   * @return the entity
   */
  protected synchronized TRuleSetParam getDbRuleSetParam(final Long rsetParamId) {
    return getEm().find(TRuleSetParam.class, rsetParamId);
  }

  /**
   * @param rsetParamAttrId primary key
   * @return the entity
   */
  protected synchronized TRuleSetParamAttr getDbRuleSetParamAttr(final Long rsetParamAttrId) {
    return getEm().find(TRuleSetParamAttr.class, rsetParamAttrId);
  }

  /**
   * ICdm-1032 return the TParamAttr from DB
   *
   * @param paramAttrID paramAttrID
   * @return the entity
   */
  public TParamAttr getDbParamAttr(final Long paramAttrID) {
    return getEm().find(TParamAttr.class, paramAttrID);
  }

  /**
   * @param paramID primary key
   * @return the entity
   */
  /* iCDM-471 */
  protected synchronized TParameter getDbFunctionParameter(final Long paramID) {
    return getEm().find(TParameter.class, paramID);
  }

  /**
   * ICDM-2005
   *
   * @param qNaireID primary key
   * @return TQuestionnaire
   */
  protected TQuestionnaire getDbQuestionnaire(final Long qNaireID) {
    return getEm().find(TQuestionnaire.class, qNaireID);
  }

  /**
   * ICDM-2005
   *
   * @param qNaireVersID primary key
   * @return TQuestionnaireVersion
   */
  public TQuestionnaireVersion getDbQuestionnaireVersion(final Long qNaireVersID) {
    return getEm().find(TQuestionnaireVersion.class, qNaireVersID);
  }

  /**
   * ICDM-2005
   *
   * @param questionID primary key
   * @return TQuestion
   */
  public TQuestion getDbQuestion(final Long questionID) {
    return getEm().find(TQuestion.class, questionID);
  }

  /**
   * ICDM-2005
   *
   * @param qConfigID primary key
   * @return TQuestionConfig
   */
  public TQuestionConfig getDbQuestionConfig(final Long qConfigID) {
    return getEm().find(TQuestionConfig.class, qConfigID);
  }

  /**
   * ICDM-2005
   *
   * @param questionAttrID primary key
   * @return TQuestionDepenAttribute
   */
  public TQuestionDepenAttribute getDbQuestionDepenAttr(final Long questionAttrID) {
    return getEm().find(TQuestionDepenAttribute.class, questionAttrID);
  }

  /**
   * ICDM-2005
   *
   * @param questionAttrValID primary key
   * @return TQuestionDepenAttrValue
   */
  public TQuestionDepenAttrValue getDbQuestionDepenAttrValue(final Long questionAttrValID) {
    return getEm().find(TQuestionDepenAttrValue.class, questionAttrValID);
  }

  /**
   * ICDM-2039
   *
   * @param revQnaireID primary key
   * @return TRvwQuestionnaire
   */
//  public TRvwQnaireResult getDbReviewQuestionnaire(final Long revQnaireID) {
//    return getEm().find(TRvwQnaireResult.class, revQnaireID);
//  }

  /**
   * ICDM-2039
   *
   * @param revQnaireAnsID primary key
   * @return TRvwQnaireAnswer
   */
  public TRvwQnaireAnswer getDbReviewQnaireAnswer(final Long revQnaireAnsID) {
    return getEm().find(TRvwQnaireAnswer.class, revQnaireAnsID);
  }


  /**
   * ICDM-2190
   *
   * @param qnaireAnsOpenPointsID primary key
   * @return TRvwQnaireAnswer
   */
  public TRvwQnaireAnswerOpl getDbQnaireAnsOpenPoint(final Long qnaireAnsOpenPointsID) {
    return getEm().find(TRvwQnaireAnswerOpl.class, qnaireAnsOpenPointsID);
  }

  /**
   * @param pidcVersId pidc version id
   * @return TPidcVersion
   */
  public TPidcVersion getDbPIDCVersion(final Long pidcVersId) {
    return getEm().find(TPidcVersion.class, pidcVersId);
  }

  /**
   * @param wpDivId wpDivId
   * @return TWorkpackageDivision
   */
  public TWorkpackageDivision getDbWrkPkgDiv(final Long wpDivId) {
    return getEm().find(TWorkpackageDivision.class, wpDivId);
  }

  /**
   * @param wrkPkgId wrkPkgId
   * @return TWorkpackage
   */
  public TWorkpackage getDbIcdmWorkPackage(final Long wrkPkgId) {
    return getEm().find(TWorkpackage.class, wrkPkgId);
  }

  /**
   * Get the questionnaire response entity
   *
   * @param respID quesitonnaire response ID
   * @return response entity
   */
  // ICDM-2404
  public TRvwQnaireResponse getDbQnaireResponse(final Long respID) {
    return getEm().find(TRvwQnaireResponse.class, respID);
  }

  /**
   * @param secRvwParamID secRvwParamID
   * @return the TRvwParametersSecondary object
   */
  public TRvwParametersSecondary getDbResParamSecondary(final Long secRvwParamID) {
    return getEm().find(TRvwParametersSecondary.class, secRvwParamID);
  }

}
