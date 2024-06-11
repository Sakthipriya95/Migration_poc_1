/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * The Class QuestAttrAndValDepModel.
 *
 * @author nip4cob
 */
public class QuestAttrAndValDepModel {

  /** Key : AttrId , value : Attribute */
  private Map<Long, Attribute> attributeMap = new HashMap<>();

  /** Key : ValueId , value : AttributeValue */
  private Map<Long, AttributeValue> attributeValueMap = new HashMap<>();

  /** Key : QnDepAttrId , value : QuestionDepenAttrValue */
  private Map<Long, Set<QuestionDepenAttrValue>> qDepattrValueMap = new HashMap<>();

  /** Key : QuestionId + "" + AttrId , value : QuestionDepenAttr */
  private Map<String, QuestionDepenAttr> qDepAttrMap = new HashMap<>();

  /**
   * Gets the attribute map.
   *
   * @return the attributeMap
   */
  public Map<Long, Attribute> getAttributeMap() {
    return this.attributeMap;
  }

  /**
   * Sets the attribute map.
   *
   * @param attributeMap the attributeMap to set
   */
  public void setAttributeMap(final Map<Long, Attribute> attributeMap) {
    this.attributeMap = attributeMap;
  }

  /**
   * Gets the attribute value map.
   *
   * @return the attributeValueMap
   */
  public Map<Long, AttributeValue> getAttributeValueMap() {
    return this.attributeValueMap;
  }

  /**
   * Sets the attribute value map.
   *
   * @param attributeValueMap the attributeValueMap to set
   */
  public void setAttributeValueMap(final Map<Long, AttributeValue> attributeValueMap) {
    this.attributeValueMap = attributeValueMap;
  }

  /**
   * Gets the q depattr value map.
   *
   * @return the qDepattrValueMap
   */
  public Map<Long, Set<QuestionDepenAttrValue>> getqDepattrValueMap() {
    return this.qDepattrValueMap;
  }

  /**
   * Setq depattr value map.
   *
   * @param qDepattrValueMap the qDepattrValueMap to set
   */
  public void setqDepattrValueMap(final Map<Long, Set<QuestionDepenAttrValue>> qDepattrValueMap) {
    this.qDepattrValueMap = qDepattrValueMap;
  }

  /**
   * Gets the q dep attr map.
   *
   * @return the qDepAttrMap
   */
  public Map<String, QuestionDepenAttr> getqDepAttrMap() {
    return this.qDepAttrMap;
  }

  /**
   * Setq dep attr map.
   *
   * @param qDepAttrMap the qDepAttrMap to set
   */
  public void setqDepAttrMap(final Map<String, QuestionDepenAttr> qDepAttrMap) {
    this.qDepAttrMap = qDepAttrMap;
  }


}
