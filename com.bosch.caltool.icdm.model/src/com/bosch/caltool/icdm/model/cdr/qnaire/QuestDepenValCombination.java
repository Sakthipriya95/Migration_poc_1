/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author NIP4COB
 */
public class QuestDepenValCombination implements Cloneable {


  /**
   * combination id
   */
  private Long combinationId = 0l;

  /**
   * Question
   */
  private Question question;
  /**
   * key- attribute, value -attribute value
   */
  private ConcurrentMap<QuestionDepenAttr, QuestionDepenAttrValue> quesAttrValMap = new ConcurrentHashMap<>();

  /**
   * key- attribute, value -attribute value
   */
  private ConcurrentMap<Attribute, AttributeValue> attrValMap = new ConcurrentHashMap<>();
  /**
   * This flag will be true only in case of adding a new attr dependency to the already existing set of att
   * dependencies(update)
   */
  private boolean addedToExistingOnes;

  
  /**
   * @return the attrValMap
   */
  public ConcurrentMap<Attribute, AttributeValue> getAttrValMap() {
    return this.attrValMap;
  }


  /**
   * @return the quesAttrValMap
   */
  public ConcurrentMap<QuestionDepenAttr, QuestionDepenAttrValue> getQuesAttrValMap() {
    return this.quesAttrValMap;
  }


  /**
   * @param quesAttrValMap the quesAttrValMap to set
   */
  public void setQuesAttrValMap(final ConcurrentMap<QuestionDepenAttr, QuestionDepenAttrValue> quesAttrValMap) {
    this.quesAttrValMap = quesAttrValMap;
  }


  /**
   * @return the combinationId
   */
  public Long getCombinationId() {
    return this.combinationId;
  }

  /**
   * @param combinationId the combinationId to set
   */
  public void setCombinationId(final Long combinationId) {
    this.combinationId = combinationId;
  }

  /**
   * @return the question
   */
  public Question getQuestion() {
    return this.question;
  }

  /**
   * @param question the question to set
   */
  public void setQuestion(final Question question) {
    this.question = question;
  }


  /**
   * @param map attributevaluemap
   */
  public void setAttrValMap(final ConcurrentMap<Attribute, AttributeValue> map) {
    this.attrValMap = map;
  }


  /**
   * @return the isAddedToExistingOnes
   */
  public boolean isAddedToExistingOnes() {
    return this.addedToExistingOnes;
  }

  /**
   * {@inheritDoc}
   *
   * @throws CloneNotSupportedException
   */
  @Override
  public QuestDepenValCombination clone() throws CloneNotSupportedException {
    QuestDepenValCombination clone = null;
    clone = (QuestDepenValCombination) super.clone();
    /*
     * For the attr value map
     */
    clone.attrValMap = new ConcurrentHashMap<>();
    for (Attribute attr : getAttrValMap().keySet()) {
      clone.attrValMap.put(attr, getAttrValMap().get(attr));
    }
    return clone;
  }


  /**
   * @param isAddedToExistingOnes the isAddedToExistingOnes to set
   */
  public void setAddedToExistingOnes(final boolean isAddedToExistingOnes) {
    this.addedToExistingOnes = isAddedToExistingOnes;
  }

}
