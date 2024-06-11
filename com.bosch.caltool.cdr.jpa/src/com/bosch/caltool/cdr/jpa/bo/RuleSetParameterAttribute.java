/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.Map;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;


/**
 * RuleSet Parameter Atribute
 *
 * @author adn1cob
 */
public class RuleSetParameterAttribute extends AbstractParameterAttribute
    implements Comparable<RuleSetParameterAttribute> {


  /**
   * @param cdrDataProvider cdrDataProvider
   * @param objID primary key
   */
  protected RuleSetParameterAttribute(final AbstractDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
    // TODO Auto-generated constructor stub
  }

  /**
   * return the version {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbRuleSetParamAttr(getID()).getVersion();
  }


  /**
   * Icdm-1032 Get the Attribute for the Param Attribute
   *
   * @return List of Char Values
   */
  @Override
  public Attribute getAttribute() {
    final TabvAttribute tabvAttribute = getEntityProvider().getDbRuleSetParamAttr(getID()).getTabvAttribute();
    // Since Attribute Id cannot be null. (Not null for Attr ID)
    return getApicDataProvider().getAttribute(tabvAttribute.getAttrId());

  }


  /**
   * Get the Rule set paramter
   *
   * @return RuleSetParameter
   * @throws SsdInterfaceException
   */
  @Override
  public RuleSetParameter getParameter() throws SsdInterfaceException {
    long rsId = getEntityProvider().getDbRuleSetParamAttr(getID()).getTRuleSetParam().getTRuleSet().getRsetId();
    String rsParamName =
        getEntityProvider().getDbRuleSetParamAttr(getID()).getTRuleSetParam().getTParameter().getName();
    // get the rule set parameter
    return getDataCache().getRuleSet(rsId).getAllParameters(false).get(rsParamName);
  }


  /**
   * Get the creation date of the Char
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRuleSetParamAttr(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Char
   *
   * @return The ID of the user who has created the Char
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbRuleSetParamAttr(getID()).getCreatedUser();
  }

  /**
   * Get the date when the Char has been modified the last time
   *
   * @return The date when the Char has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRuleSetParamAttr(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Char the last time
   *
   * @return The user who has modified the Char the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbRuleSetParamAttr(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc} return object details in Map Not implemanted as of now.
   */
  @Override
  public Map<String, String> getObjectDetails() {
    // Not implemanted as of now.
    return null;
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
   * {@inheritDoc} Entity Type Param Attr
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RULE_SET_PARAM_ATTR;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleSetParameterAttribute other) {
    return ApicUtil.compare(getAttribute().getAttributeName(), other.getAttribute().getAttributeName());
  }

}
