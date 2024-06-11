/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * This class represents the BO for a row in TQuestionDepenAttrValue
 *
 * @author jvi6cob
 */
public class QuestionDepenAttrValue extends AbstractCdrObject implements Comparable<QuestionDepenAttrValue> {

  /**
   * @param cdrDataProvider CDRDataProvider
   * @param qDepenAttrValID Long
   */
  protected QuestionDepenAttrValue(final CDRDataProvider cdrDataProvider, final Long qDepenAttrValID) {
    super(cdrDataProvider, qDepenAttrValID);
    getDataCache().getQuestionDepenAttrValueMap().put(qDepenAttrValID, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQuestionDepenAttrValue(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQuestionDepenAttrValue(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionDepenAttrValue(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionDepenAttrValue(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QUESTION_DEPEN_ATTR_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAttributeValue().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getAttributeValue().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionDepenAttrValue qDepenAttrValToCmp) {
    int compResult = ApicUtil.compare(getName(), qDepenAttrValToCmp.getName());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), qDepenAttrValToCmp.getID());
    }
    return compResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return Attribute
   */
  public AttributeValue getAttributeValue() {
    final TabvAttrValue tabvAttributeValue =
        getEntityProvider().getDbQuestionDepenAttrValue(getID()).getTabvAttrValue();
    return getApicDataProvider().getAttrValue(tabvAttributeValue.getValueId());
  }

  /**
   * get the QuestionDepenAttr to which it is mapped to
   *
   * @return QuestionDepenAttr
   */
  public QuestionDepenAttr getQuesDepnAttr() {
    TQuestionDepenAttribute tQuestionDepenAttribute =
        getEntityProvider().getDbQuestionDepenAttrValue(getID()).getTQuestionDepenAttribute();
    return getDataProvider().getQuestionDepenAttribute(tQuestionDepenAttribute.getQattrDepenId());
  }

}
