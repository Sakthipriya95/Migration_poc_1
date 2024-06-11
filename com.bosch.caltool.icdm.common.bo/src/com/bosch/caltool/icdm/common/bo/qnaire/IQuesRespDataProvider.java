/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.qnaire;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;

/**
 * @author mkl2cob
 */
public interface IQuesRespDataProvider {

  /**
   * @return set of questions
   */
  Set<Question> getQuestionsSet();

  /**
   * @return Questionnaire response version id
   */
  Long getQuesRespVersId();

  /**
   * @return QuestAttrAndValDepModel
   */
  QuestAttrAndValDepModel getQuesAttrDepModel();

  /**
   * map of used project attributes
   *
   * @return Map<Long, IProjectAttribute>
   */
  Map<Long, IProjectAttribute> getUsedProjAttrMap();

  /**
   * map of used project attributes
   *
   * @return Map<Long, IProjectAttribute>
   */
  Map<Long, IProjectAttribute> getUsedVarAttrMap();

  /**
   * @param attrVal AttributeValue
   * @return true if the attribute value is cleared
   */
  boolean isCleared(AttributeValue attrVal);

  /**
   * @param projAttr IProjectAttribute
   * @return true if the project attribute is visible in the project
   */
  boolean isVisible(IProjectAttribute projAttr);

  /**
   * @return Map<Long, PidcSubVariant>
   */
  Map<Long, PidcSubVariant> getSubVarMap();

  /**
   * @param depAttr Attribute
   * @param subVariant PidcSubVariant
   * @return PidcSubVariantAttribute corresponding to the attribute
   */
  PidcSubVariantAttribute getProjSubVarAttr(final Attribute depAttr, final PidcSubVariant subVariant);

  /**
   * @param projAttr IProjectAttribute
   * @return AttributeValue
   */
  AttributeValue getAttributeValue(final IProjectAttribute projAttr);

  /**
   * @return true if ques dep check needs to be done
   */
  boolean isQuesDepChkNeeded();

  /**
   * @param questionId
   * @return Question based on id
   */
  Question getQuestion(Long questionId);

  /**
   * @param questionId
   * @return true if the given question id belong to heading
   */
  boolean isHeading(long questionId);

}
