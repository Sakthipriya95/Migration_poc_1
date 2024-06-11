/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.bo.qnaire.IQuesRespDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;

/**
 * @author mkl2cob
 */
public class QuesRespDataProviderServer implements IQuesRespDataProvider {

  /**
   * qnaireVersId
   */
  private final Long qnaireVersId;
  /**
   * ServiceData
   */
  private final ServiceData serviceData;
  /**
   * QnaireVersionModel
   */
  private QnaireVersionModel qnaireVersionWithDetails;
  /**
   * QuestAttrAndValDepModel
   */
  private QuestAttrAndValDepModel allQnDepnAttrValModel;
  /**
   * RvwQnaireResponseModel
   */
  private final RvwQnaireResponseModel qnaireRespModel;

  /**
   * PidcVersionWithDetails
   */
  private PidcVersionWithDetails pidcVersionWithDetails;

  /**
   * @param serviceData ServiceData
   * @param qnaireVersId qnaire version id
   * @param qnaireRespModel RvwQnaireResponseModel
   * @throws IcdmException Exception while loading data
   */
  public QuesRespDataProviderServer(final ServiceData serviceData, final Long qnaireVersId,
      final RvwQnaireResponseModel qnaireRespModel) throws IcdmException {
    this.serviceData = serviceData;
    this.qnaireVersId = qnaireVersId;
    this.qnaireRespModel = qnaireRespModel;
    initialisefields();
  }

  /**
   * initialise the fields that will be needed for the other methods
   *
   * @throws IcdmException
   */
  private void initialisefields() throws IcdmException {
    this.qnaireVersionWithDetails =
        new QuestionnaireVersionLoader(this.serviceData).getQnaireVersionWithDetails(this.qnaireVersId);
    this.allQnDepnAttrValModel = new QuestionLoader(this.serviceData).getAllQnDepnAttrValModel(this.qnaireVersId);
    // initialise flags
    PidcVersionWithDetailsInput flags =
        new PidcVersionWithDetailsInput(this.qnaireRespModel.getPidcVersion().getId(), true);
    this.pidcVersionWithDetails =
        new PidcVersionLoader(this.serviceData).getPidcVersionWithDetails(flags, "ALL", new HashSet<Long>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Question> getQuestionsSet() {
    return new HashSet<>(this.qnaireVersionWithDetails.getQuestionMap().values());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestAttrAndValDepModel getQuesAttrDepModel() {
    return this.allQnDepnAttrValModel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, IProjectAttribute> getUsedProjAttrMap() {
    Map<Long, IProjectAttribute> usedPIDCAttrMap = new HashMap<>();
    // Get all used PIDC variant attributes
    Map<Long, PidcVersionAttribute> varAttrmap = this.pidcVersionWithDetails.getPidcVersionAttributeMap();
    if (null != varAttrmap) {
      for (PidcVersionAttribute attribute : varAttrmap.values()) {
        if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
            !isValueInvalid(attribute)) {
          usedPIDCAttrMap.put(attribute.getAttrId(), attribute);
        }
      }
    }
    return usedPIDCAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, IProjectAttribute> getUsedVarAttrMap() {
    Map<Long, IProjectAttribute> usedPIDCAttrMap = new HashMap<>();
    // Get all used PIDC variant attributes
    Map<Long, PidcVariantAttribute> varAttrmap =
        this.pidcVersionWithDetails.getPidcVariantAttributeMap().get(this.qnaireRespModel.getPidcVariant().getId());
    if (null != varAttrmap) {
      for (PidcVariantAttribute attribute : varAttrmap.values()) {
        if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
            !isValueInvalid(attribute)) {
          usedPIDCAttrMap.put(attribute.getAttrId(), attribute);
        }
      }
    }
    return usedPIDCAttrMap;
  }

  /**
   * @param attribute PidcVariantAttribute
   * @return This method returns whether the value set is not cleared or deleted
   */
  public boolean isValueInvalid(final IProjectAttribute attribute) {
    return ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType().equals(attribute.getUsedFlag()) &&
        (this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()) != null) &&
        (this.pidcVersionWithDetails.getAttributeValueMap().get(attribute.getValueId()).isDeleted());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCleared(final AttributeValue attrVal) {
    return ApicConstants.CODE_YES.equals(attrVal.getClearingStatus());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isVisible(final IProjectAttribute projAttr) {
    boolean isVisible = false;
    if (projAttr instanceof PidcVersionAttribute) {
      isVisible = !this.pidcVersionWithDetails.getPidcVersInvisibleAttrSet().contains(projAttr.getAttrId());
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      Long variantId = ((PidcVariantAttribute) projAttr).getVariantId();
      if (this.pidcVersionWithDetails.getVariantInvisbleAttributeMap().get(variantId) != null) {
        isVisible =
            !this.pidcVersionWithDetails.getVariantInvisbleAttributeMap().get(variantId).contains(projAttr.getAttrId());
      }
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      Long subvariantId = ((PidcSubVariantAttribute) projAttr).getSubVariantId();
      if (this.pidcVersionWithDetails.getSubVariantInvisbleAttributeMap().get(subvariantId) != null) {
        return !this.pidcVersionWithDetails.getSubVariantInvisbleAttributeMap().get(subvariantId)
            .contains(projAttr.getAttrId());
      }
    }
    return isVisible;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariant> getSubVarMap() {
    return this.pidcVersionWithDetails.getPidcSubVariantMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcSubVariantAttribute getProjSubVarAttr(final Attribute depAttr, final PidcSubVariant subVariant) {
    Map<Long, PidcSubVariantAttribute> subVarMap =
        this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().get(subVariant.getId());
    return subVarMap.get(depAttr.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValue getAttributeValue(final IProjectAttribute projAttr) {
    return this.pidcVersionWithDetails.getAttributeValueMap().get(projAttr.getValueId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isQuesDepChkNeeded() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Question getQuestion(final Long questionId) {
    if ((questionId == null) || !this.qnaireVersionWithDetails.getQuestionMap().containsKey(questionId)) {
      return null;
    }
    return this.qnaireVersionWithDetails.getQuestionMap().get(questionId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isHeading(final long questionId) {
    if ((this.qnaireVersionWithDetails.getQuestionMap() != null) &&
        this.qnaireVersionWithDetails.getQuestionMap().containsKey(questionId)) {
      return this.qnaireVersionWithDetails.getQuestionMap().get(questionId).getHeadingFlag();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getQuesRespVersId() {
    // Not used
    return null;
  }

}
