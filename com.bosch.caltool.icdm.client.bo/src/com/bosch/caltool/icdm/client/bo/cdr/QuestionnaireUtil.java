/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestDepenValCombination;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;


/**
 * @author mkl2cob
 */
public final class QuestionnaireUtil {

  private QuestionnaireUtil() {
    // private constructor to not allow instantiation
  }

  /**
   * ICDM-2054 construct attribute value combination for questions
   *
   * @param attrValMap Map<Attribute, SortedSet<AttributeValue>>
   * @param question Question
   * @param addToExisting
   * @return
   */
  public static List<QuestDepenValCombination> constructAttrValCombiForQues(
      final Map<Attribute, SortedSet<AttributeValue>> attrValMap, final Question question,
      final boolean addToExisting) {
    List<QuestDepenValCombination> attrValCombList = new ArrayList<>();
    // using the combination calculator to form the attribute value map
    AttrValcombinationCalculator combinationCalculator = new AttrValcombinationCalculator(attrValMap);
    Map<Integer, Map<Attribute, AttributeValue>> constructAttrValCombi = combinationCalculator.constructAttrValCombi();

    for (Entry<Integer, Map<Attribute, AttributeValue>> combination : constructAttrValCombi.entrySet()) {

      // create attr value combination bo
      QuestDepenValCombination quesDepnValComb = new QuestDepenValCombination();
      quesDepnValComb.setQuestion(question);
      quesDepnValComb.setAddedToExistingOnes(addToExisting);
      quesDepnValComb.setAttrValMap(new ConcurrentHashMap<>(combination.getValue()));
      attrValCombList.add(quesDepnValComb);
    }
    return attrValCombList;
  }
}
