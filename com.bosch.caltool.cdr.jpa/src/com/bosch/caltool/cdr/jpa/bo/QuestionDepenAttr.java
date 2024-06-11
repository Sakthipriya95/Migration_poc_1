/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.Collections;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author jvi6cob
 */
public class QuestionDepenAttr extends AbstractCdrObject implements Comparable<QuestionDepenAttr> {

  /**
   * @param cdrDataProvider CDRDataProvider
   * @param qDepenAttrID Long
   */
  public QuestionDepenAttr(final CDRDataProvider cdrDataProvider, final Long qDepenAttrID) {
    super(cdrDataProvider, qDepenAttrID);
    getDataCache().getQuestionDepenAttributeMap().put(qDepenAttrID, this);
    Collections.synchronizedSortedSet(new TreeSet<QuestionDepenAttrValue>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQuestionDepenAttr(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQuestionDepenAttr(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionDepenAttr(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQuestionDepenAttr(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QUESTION_DEPEN_ATTR;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAttribute().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getAttribute().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QuestionDepenAttr qDepenAttrToCmp) {
    int compResult = ApicUtil.compare(getName(), qDepenAttrToCmp.getName());
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getID(), qDepenAttrToCmp.getID());
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
  public Attribute getAttribute() {
    final TabvAttribute tabvAttribute = getEntityProvider().getDbQuestionDepenAttr(getID()).getTabvAttribute();
    // Since Attribute Id cannot be null. (Not null for Attr ID)
    return getApicDataProvider().getAttribute(tabvAttribute.getAttrId());
  }


  /**
   * @return Question
   */
  public Question getQuestion() {
    // Get question from data cache
    return getDataCache().getQuestion(getEntityProvider().getDbQuestionDepenAttr(getID()).getTQuestion().getQId());
  }
}
