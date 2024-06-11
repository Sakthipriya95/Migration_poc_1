/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.bo.ApicBOUtil;
import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.rules.bo.IParameter;
import com.bosch.caltool.apic.jpa.rules.bo.IParameterAttribute;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Business Object for Component Package Rule Attributes
 *
 * @author bne4cob
 */
@Deprecated
public class CompPkgRuleAttr extends AbstractCPObject implements Comparable<CompPkgRuleAttr>, IParameterAttribute {

  /**
   * The data provider
   */
  private final CPDataProvider cpDataProvider;

  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Attribute name
                            */
                           ATTR_NAME,
                           /**
                            * Attribute description
                            */
                           ATTR_DESC,

  }

  /**
   * Constructor
   *
   * @param dataProvider dataProvider
   * @param objID primary key
   */
  protected CompPkgRuleAttr(final CPDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    this.cpDataProvider = dataProvider;
    this.cpDataProvider.getDataCache().getAllCompPkgRuleAttrs().put(objID, this);
  }

  /**
   * @return created date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkgRuleAttr(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getObjectDetails() {
    // No specific properties in this object, that can be updated
    return new ConcurrentHashMap<String, String>();
  }

  /**
   * @return created user
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbCompPkgRuleAttr(getID()).getCreatedUser();
  }

  /**
   * @return modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbCompPkgRuleAttr(getID()).getModifiedDate());
  }

  /**
   * @return modified user
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbCompPkgRuleAttr(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbCompPkgRuleAttr(getID()).getVersion();
  }

  /**
   * Get the description from attribute
   *
   * @return description of attribute
   */
  @Override
  public String getDescription() {
    return getAttribute().getAttributeDesc();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgRuleAttr other) {
    return ApicUtil.compare(getAttribute(), other.getAttribute());
  }

  /**
   * @return Attribute
   */
  @Override
  public Attribute getAttribute() {
    return getApicDataProvider()
        .getAttribute(getEntityProvider().getDbCompPkgRuleAttr(getID()).getTabvAttribute().getAttrId());
  }

  /**
   * Returns the available values of the mapped attribute, that have feature value mapping
   *
   * @return sorted set of available attribute values
   */
  public SortedSet<AttributeValue> getAttrValues() {
    return ApicBOUtil.getFeatureMappedAttrValues(getApicDataProvider(), getAttribute());
  }

  /**
   * Returns the attribute name in the Rule attribute object.
   * <p>
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
  public IEntityType<?, ?> getEntityType() {
    return CPEntityType.COMP_PKG_RULE_ATTR;
  }


  /**
   * @param other parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final CompPkgRuleAttr other, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case ATTR_NAME:
        // comparing the BC names
        compareResult = ApicUtil.compare(getName(), other.getName());
        break;
      case ATTR_DESC:
        compareResult = ApicUtil.compare(getDescription(), other.getDescription());
        break;
      default:
        // Compare name
        compareResult = compareTo(other);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compareResult = compareTo(other);
    }

    return compareResult;
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
   * Getter for parent Component package
   *
   * @return CompPkg
   */
  public CompPkg getCompPkg() {
    return this.cpDataProvider.getDataCache()
        .getCompPkg(getEntityProvider().getDbCompPkgRuleAttr(getID()).getTCompPkg().getCompPkgId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IParameter<?> getParameter() {
    // In Component pacakge, no direct relation between parameters and attributes. Attributes are mapped to component
    // pacakge directly, and are applicable to all parameters
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<AttributeValue> getAttributeValues() {
    return ApicBOUtil.getFeatureMappedAttrValues(getApicDataProvider(), getAttribute());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasAttrValues() {
    return !getAttributeValues().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IParameterAttribute paramAttr) {
    return ApicUtil.compare(getAttribute().getAttributeName(), paramAttr.getAttribute().getAttributeName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IParameterAttribute arg0, final int sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(getAttribute().getAttributeName(), arg0.getAttribute().getAttributeName());
        break;
      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getAttribute().getAttributeDesc(), arg0.getAttribute().getAttributeDesc());
        break;
      case ApicConstants.SORT_UNIT:
        compareResult = ApicUtil.compare(getAttribute().getUnit(), arg0.getAttribute().getUnit());
        break;
      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }

}
