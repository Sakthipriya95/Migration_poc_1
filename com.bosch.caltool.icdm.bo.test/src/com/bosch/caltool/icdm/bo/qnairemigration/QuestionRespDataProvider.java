/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.qnairemigration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionLoader;
import com.bosch.caltool.icdm.common.bo.qnaire.IQuesRespDataProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;

/**
 * @author say8cob
 */
public class QuestionRespDataProvider implements IQuesRespDataProvider {


  /**
   * RvwQnaireRespVersion
   */
  private final RvwQnaireRespVersion rvwQnaireRespVers;
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

  private final PidcVersionAttributeModel pidcVersionAttributeModel;

  private Map<Long, Set<Long>> subVariantInvisbleAttributeMap = new HashMap<>();

  private Map<Long, Set<Long>> variantInvisbleAttributeMap = new HashMap<>();

  /**
   * @param serviceData ServiceData
   * @param inputRvwQnaireRespVers RvwQnaireRespVersion
   * @param qnaireRespModel RvwQnaireResponseModel
   * @param pidcVersionAttributeModel as pidcVersionAttributeModel
   * @throws IcdmException Exception while loading data
   */
  public QuestionRespDataProvider(final ServiceData serviceData, final RvwQnaireRespVersion inputRvwQnaireRespVers,
      final RvwQnaireResponseModel qnaireRespModel, final PidcVersionAttributeModel pidcVersionAttributeModel)
      throws IcdmException {
    this.serviceData = serviceData;
    this.rvwQnaireRespVers = inputRvwQnaireRespVers;
    this.qnaireRespModel = qnaireRespModel;
    this.pidcVersionAttributeModel = pidcVersionAttributeModel;
    initialisefields();
  }

  /**
   * initialise the fields that will be needed for the other methods
   *
   * @throws IcdmException
   */
  private void initialisefields() throws IcdmException {
    this.qnaireVersionWithDetails = new QuestionnaireVersionLoader(this.serviceData)
        .getQnaireVersionWithDetails(this.rvwQnaireRespVers.getQnaireVersionId());
    this.allQnDepnAttrValModel =
        new QuestionLoader(this.serviceData).getAllQnDepnAttrValModel(this.rvwQnaireRespVers.getQnaireVersionId());
    fillInvisibleAttrVarLevel();
    fillInvisibleAttrSubVarLevel();
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
  public Long getQuesRespVersId() {
    return this.rvwQnaireRespVers.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuestAttrAndValDepModel getQuesAttrDepModel() {
    return this.allQnDepnAttrValModel;
  }

  /**
   * @param pidcVersionWithDetails
   * @param model
   */
  private void fillInvisibleAttrSubVarLevel() {
    for (PidcSubVariant subVariant : this.pidcVersionAttributeModel.getSubVariantMap().values()) {
      this.subVariantInvisbleAttributeMap.put(subVariant.getId(),
          this.pidcVersionAttributeModel.getSubVariantInvisbleAttributeSet(subVariant.getId()));
    }
  }

  /**
   * @param pidcVersionWithDetails
   * @param model
   */
  private void fillInvisibleAttrVarLevel() {
    for (PidcVariant variant : this.pidcVersionAttributeModel.getVariantMap().values()) {
      getVariantInvisbleAttributeMap().put(variant.getId(),
          this.pidcVersionAttributeModel.getVariantInvisbleAttributeSet(variant.getId()));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, IProjectAttribute> getUsedProjAttrMap() {
    Map<Long, IProjectAttribute> usedPIDCAttrMap = new HashMap<>();
    // Get all used PIDC variant attributes
    Map<Long, PidcVersionAttribute> varAttrmap = this.pidcVersionAttributeModel.getPidcVersAttrMap();
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
        this.pidcVersionAttributeModel.getAllVariantAttributeMap().get(this.qnaireRespModel.getPidcVariant().getId());
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
        (this.pidcVersionAttributeModel.getRelevantAttrValueMap().get(attribute.getValueId()) != null) &&
        (this.pidcVersionAttributeModel.getRelevantAttrValueMap().get(attribute.getValueId()).isDeleted());
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
      isVisible = !this.pidcVersionAttributeModel.getPidcVersInvisibleAttrSet().contains(projAttr.getAttrId());
    }
    else if (projAttr instanceof PidcVariantAttribute) {
      Long variantId = ((PidcVariantAttribute) projAttr).getVariantId();
      if (getVariantInvisbleAttributeMap().get(variantId) != null) {
        isVisible = !getVariantInvisbleAttributeMap().get(variantId).contains(projAttr.getAttrId());
      }
    }
    else if (projAttr instanceof PidcSubVariantAttribute) {
      Long subvariantId = ((PidcSubVariantAttribute) projAttr).getSubVariantId();
      if (getSubVariantInvisbleAttributeMap().get(subvariantId) != null) {
        return !getSubVariantInvisbleAttributeMap().get(subvariantId).contains(projAttr.getAttrId());
      }
    }
    return isVisible;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Long, PidcSubVariant> getSubVarMap() {
    return this.pidcVersionAttributeModel.getSubVariantMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcSubVariantAttribute getProjSubVarAttr(final Attribute depAttr, final PidcSubVariant subVariant) {
    Map<Long, PidcSubVariantAttribute> subVarMap =
        this.pidcVersionAttributeModel.getAllSubVariantAttrMap().get(subVariant.getId());
    return subVarMap.get(depAttr.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttributeValue getAttributeValue(final IProjectAttribute projAttr) {
    return this.pidcVersionAttributeModel.getRelevantAttrValueMap().get(projAttr.getValueId());
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
   * @return the subVariantInvisbleAttributeMap
   */
  public Map<Long, Set<Long>> getSubVariantInvisbleAttributeMap() {
    return this.subVariantInvisbleAttributeMap;
  }


  /**
   * @param subVariantInvisbleAttributeMap the subVariantInvisbleAttributeMap to set
   */
  public void setSubVariantInvisbleAttributeMap(final Map<Long, Set<Long>> subVariantInvisbleAttributeMap) {
    this.subVariantInvisbleAttributeMap = subVariantInvisbleAttributeMap;
  }


  /**
   * @return the variantInvisbleAttributeMap
   */
  public Map<Long, Set<Long>> getVariantInvisbleAttributeMap() {
    return this.variantInvisbleAttributeMap;
  }


  /**
   * @param variantInvisbleAttributeMap the variantInvisbleAttributeMap to set
   */
  public void setVariantInvisbleAttributeMap(final Map<Long, Set<Long>> variantInvisbleAttributeMap) {
    this.variantInvisbleAttributeMap = variantInvisbleAttributeMap;
  }


}
