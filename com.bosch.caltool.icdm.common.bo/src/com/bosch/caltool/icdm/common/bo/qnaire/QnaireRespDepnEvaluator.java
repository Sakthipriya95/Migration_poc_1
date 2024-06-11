/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.qnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestDepenValCombination;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttr;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionDepenAttrValue;


/**
 * Attribute dependency evaluation of question response.
 *
 * @author bne4cob
 */
// ICDM-2106
public class QnaireRespDepnEvaluator {


  private final PidcVersion pidcVersion;
  /**
   * selected pidc variant
   */
  private final PidcVariant pidcVariant;

  /**
   * Constructor.
   *
   * @param pidcVersion the pidc version
   * @param pidcVariant the pidc variant
   */
  public QnaireRespDepnEvaluator(final PidcVersion pidcVersion, final PidcVariant pidcVariant) {
    this.pidcVersion = pidcVersion;
    this.pidcVariant = pidcVariant;


  }

  /**
   * validate whether question is applicable for the response by attribute dependency analysis.
   *
   * @param question question to check
   * @param quesRespDataProvider the attr val model
   * @return true, if question is applicable to the response
   */
  public boolean isQuestionApplicable(final Question question, final IQuesRespDataProvider quesRespDataProvider) {
    boolean match = false;
    Set<QuestDepenValCombination> qnCombinations =
        createCombinations(question.getId(), quesRespDataProvider.getQuesAttrDepModel());
    if (!CommonUtils.isNotEmpty(qnCombinations)) {
      // Question is applicable if there is no dependency definition against the question
      match = true;
    }
    else {
      getLogger().debug("QnDepValdatn: Validating question for dependency : {}", question.getId());

      for (QuestDepenValCombination quesAttrValComb : qnCombinations) {
        getLogger().debug("QnDepValdatn: Checking combination : {}", quesAttrValComb.getCombinationId());
        if (isDepAttrCombMatch(quesAttrValComb, quesRespDataProvider)) {
          getLogger().debug("QnDepValdatn: Match found.", question.getId());
          match = true;
          break;
        }
      }
    }
    return match;
  }


  /**
   * Gets the logger.
   *
   * @return the logger
   */
  private CDMLogger getLogger() {
    return CDMLogger.getInstance();
  }

  /**
   * Match the validity of the question agaidnst an attribute value combination.
   *
   * @param quesAttrValComb the ques attr val comb
   * @param quesRespDataProvider consolidated map of project version and variant level attributes
   * @return true, if combination is match against the project attributes
   */
  private boolean isDepAttrCombMatch(final QuestDepenValCombination quesAttrValComb,
      final IQuesRespDataProvider quesRespDataProvider) {

    boolean match = true;
    for (Entry<Attribute, AttributeValue> attrValEntry : quesAttrValComb.getAttrValMap().entrySet()) {

      Attribute depAttr = attrValEntry.getKey();
      if (depAttr.isDeleted()) {
        // Dependency definition to a deleted attribute can be skipped
        getLogger().debug("QnDepValdatn: Skipping dependency attr - deleted attribute : {}", depAttr.getId());
        continue;
      }
      IProjectAttribute projAttr = null;

      IProjectAttribute tempProjAttr = quesRespDataProvider.getUsedProjAttrMap().get(depAttr.getId());
      if (tempProjAttr != null) {
        if (tempProjAttr.isAtChildLevel()) {
          projAttr = quesRespDataProvider.getUsedVarAttrMap().get(depAttr.getId());
        }
        else {
          projAttr = quesRespDataProvider.getUsedProjAttrMap().get(depAttr.getId());
          // Attribute hidden check is to be done only at project version level attribute,
          // not required for variant/sub-variant levels
          if ((projAttr == null) || projAttr.isAttrHidden()) {
            // Project attribute is not a match if : (a) not available (b) hidden to user
            getLogger().debug("QnDepValdatn: PROJATTR no match:{}. missing/hidden", depAttr.getId());
            return false;
          }
        }
      }
      else {
        return false;
      }


      AttributeValue depValue = attrValEntry.getValue();
      if (depValue.isDeleted() || !quesRespDataProvider.isCleared(depValue)) {// isCleared boolean
        getLogger().debug("QnDepValdatn: Skippping dependency attr - deleted/not-cleared value {}", depValue.getId());
        // Skip dependency definition to deleted/not-cleared values
        continue;
      }
      if (isDepAttrValMatch(depAttr, depValue, projAttr, quesRespDataProvider)) {
        // attribute value is match Match
        continue;
      }
      match = false;
      break;
    }
    return match;
  }

  /**
   * Match a single attribute value to the corresponding project attribute.
   *
   * @param depAttr dependency attribute
   * @param qnCombiDepAttrValue dependnecy value
   * @param projAttr the proj attr
   * @param quesRespDataProvider
   * @return true, if a match is found
   */
  private boolean isDepAttrValMatch(final Attribute depAttr, final AttributeValue qnCombiDepAttrValue,
      final IProjectAttribute projAttr, final IQuesRespDataProvider quesRespDataProvider) {
    if ((projAttr == null) || !quesRespDataProvider.isVisible(projAttr)) {
      // Project attribute is not a match if : (a) not available
      // (c) not visible due to attribute dependency
      getLogger().debug("QnDepValdatn: PROJATTR no match:{}. missing/depmissing", depAttr.getId());
      return false;
    }

    if (projAttr.isAtChildLevel()) {
      // defined at sub-variant level (proj attributes at this point already have variant level attributes)
      if (projAttr instanceof PidcVariantAttribute) {
        // If attribute is defined at sub-variant level, if any one of the sub-variant has the correct attribute value,
        // then attribute value is OK.
        getLogger().debug("QnDepValdatn: PROJATTR defined at svar level {}", depAttr.getId());

        // subVariants map
        Map<Long, PidcSubVariant> subVarMap = quesRespDataProvider.getSubVarMap();
        for (PidcSubVariant subVariant : subVarMap.values()) {
          // checking attr and value only for the sub-variant belonging to the variant
          if ((null != this.pidcVariant) && subVariant.getPidcVariantId().equals(this.pidcVariant.getId())) {
            getLogger().debug("QnDepValdatn: Validating match in svar {}", subVariant.getId());

            if (isDepAttrValMatch(depAttr, qnCombiDepAttrValue,
                quesRespDataProvider.getProjSubVarAttr(depAttr, subVariant), quesRespDataProvider)) {
              return true;
            }
          }
        }
        return false;
      }
      return false;
    }

    if (!CommonUtils.isEqual(ApicConstants.CODE_YES, projAttr.getUsedFlag())) {
      getLogger().debug("QnDepValdatn: PROJATTR no match:{}. not-defined/not-used", depAttr.getId());
      // Used flag is not yes
      return false;
    }
    AttributeValue projVersAttrValue = quesRespDataProvider.getAttributeValue(projAttr);
    if (!CommonUtils.isEqual(projVersAttrValue, qnCombiDepAttrValue)) {
      getLogger().debug("QnDepValdatn: PROJATTR no match:{}. value-mismatch", depAttr.getId());
      // Value is not same
      return false;
    }
    return true;
  }

  /**
   * Creates the combinations.
   *
   * @param questionId the question id
   * @param questAttrAndValDepModel the quest attr and val dep model
   * @return the sets the
   */
  private Set<QuestDepenValCombination> createCombinations(final long questionId,
      final QuestAttrAndValDepModel questAttrAndValDepModel) {
    Map<Long, QuestDepenValCombination> retMap = new HashMap<>();
    ArrayList<QuestionDepenAttr> list = questAttrAndValDepModel.getqDepAttrMap().values().stream()
        .filter(s -> s.getQId().equals(questionId)).collect(Collectors.toCollection(ArrayList::new));
    for (QuestionDepenAttr qDepAttr : list) {
      for (QuestionDepenAttrValue qDepAttrVal : questAttrAndValDepModel.getqDepattrValueMap().get(qDepAttr.getId())) {
        QuestDepenValCombination combi;
        if (!retMap.containsKey(qDepAttrVal.getQCombiNum())) {
          // since it is used only for display purpose only the attributevalue map is inserted for a combination
          combi = new QuestDepenValCombination();
          retMap.put(qDepAttrVal.getQCombiNum(), combi);
        }
        else {
          combi = retMap.get(qDepAttrVal.getQCombiNum());
        }
        if (combi != null) {
          combi.getQuesAttrValMap().put(qDepAttr, qDepAttrVal);
          combi.getAttrValMap().put(questAttrAndValDepModel.getAttributeMap().get(qDepAttr.getAttrId()),
              questAttrAndValDepModel.getAttributeValueMap().get(qDepAttrVal.getValueId()));
          combi.setCombinationId(qDepAttrVal.getQCombiNum());
        }
      }
    }
    return retMap.values().stream().collect(Collectors.toCollection(HashSet::new));
  }

}
