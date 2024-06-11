/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * ICDM-2054 This class is a virtual structure to denote attribute value combination for a question
 * 
 * @author mkl2cob
 */
public class QuesDepnValCombination implements Cloneable {

  /**
   * trail comma size to display text
   */
  private static final int TRAIL_COMMA_SIZE = 2;

  /**
   * initial size of StringBuilder
   */
  private static final int STR_BUILDER_SIZE = 100;

  /**
   * combination id
   */
  private Long combinationId;

  /**
   * Question
   */
  private Question question;
  /**
   * key- attribute, value -attribute value
   */
  private final ConcurrentMap<QuestionDepenAttr, QuestionDepenAttrValue> quesAttrValMap =
      new ConcurrentHashMap<QuestionDepenAttr, QuestionDepenAttrValue>();

  /**
   * key- attribute, value -attribute value
   */
  private ConcurrentMap<Attribute, AttributeValue> attrValMap = new ConcurrentHashMap<Attribute, AttributeValue>();
  /**
   * This flag will be true only in case of adding a new attr dependency to the already existing set of att
   * dependencies(update)
   */
  private boolean addedToExistingOnes;

  /**
   * @param question Question
   * @param isAddedToExistingOnes
   */
  public QuesDepnValCombination(final Question question, final boolean isAddedToExistingOnes) {
    this.question = question;
    this.addedToExistingOnes = isAddedToExistingOnes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + ' ' + this.attrValMap;
  }

  /**
   * @return display string
   */
  public String getDisplayText() {
    StringBuilder displayStr = new StringBuilder(STR_BUILDER_SIZE);
    for (Attribute attr : new TreeSet<>(this.attrValMap.keySet())) {
      displayStr.append(attr.getName()).append("->").append(this.attrValMap.get(attr).getName()).append(", ");
    }
    // trailing comma is removed
    if (displayStr.length() > 0) {
      displayStr.delete(displayStr.length() - TRAIL_COMMA_SIZE, displayStr.length() - 1);
    }
    return displayStr.toString();

  }

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
   * @return the combinationId
   */
  public Long getCombinationId() {
    return this.combinationId;
  }

  /**
   * @param combinationId the combinationId to set
   */
  void setCombinationId(final Long combinationId) {
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
   * @param map
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
   */
  @Override
  public QuesDepnValCombination clone() {
    QuesDepnValCombination clone = null;
    try {
      clone = (QuesDepnValCombination) super.clone();

      /*
       * For the attr value map
       */
      clone.attrValMap = new ConcurrentHashMap<>();
      for (Attribute attr : getAttrValMap().keySet()) {
        clone.attrValMap.put(attr, getAttrValMap().get(attr));
      }

    }
    catch (CloneNotSupportedException exp) {
      CDMLogger.getInstance().error("Error occured while cloning the QuesDepnValCombination object", exp);
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
