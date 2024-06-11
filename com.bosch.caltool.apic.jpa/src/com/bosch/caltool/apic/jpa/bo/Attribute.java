/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;

/**
 * This class represents attribute as stored in the database file TABV_ATTRIBUTES
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class Attribute extends ApicObject implements Comparable<Attribute> { // NOPMD by bne4cob on 6/20/14 10:27 AM


  /**
   * Tooltip string builer intial size
   */
  private static final int SIZE = 40;
  /**
   * Deleted flag
   */
  private static final String FLD_DELETED_FLAG = "DELETED_FLAG";
  /**
   * Attribute level
   */
  private static final String FLD_ATTR_LEVEL = "ATTR_LEVEL";
  /**
   * Attr Description German
   */
  private static final String FLD_ATTR_DESC_GER = "ATTR_DESC_GER";
  /**
   * Attr Description Eng
   */
  private static final String FLD_ATTR_DESC_ENG = "ATTR_DESC_ENG";
  /**
   * Attr Name German
   */
  private static final String FLD_ATTR_NAME_GER = "ATTR_NAME_GER";
  /**
   * Attribute name English
   */
  public static final String FLD_ATTR_NAME_ENG = "ATTR_NAME_ENG";

  /**
   * Object details Key for normalized flag
   */
  private static final String FLD_NRMALIZD_FLG = "NORMALIZD_FLAG";
  /**
   * Object details Key for part number flag
   */
  private static final String FLD_PRT_NUM_FLAG = "PART_NUMBER_FLAG";
  /**
   * Object details Key for spec link flag
   */
  private static final String FLD_SPC_LNK_FLG = "SPEC_LINK_FLAG";
  /**
   * Object details Key for group
   */
  private static final String FLD_GROUP = "GROUP";
  /**
   * Object details Key for value type
   */
  private static final String FLD_VAL_TYPE = "VAL_TYPE";
  /**
   * Object details Key for attribute class
   */
  private static final String FLD_ATTR_CLASS = "CHAR_TYPE";
  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param attributeID ID
   */
  public Attribute(final ApicDataProvider apicDataProvider, final Long attributeID) {
    super(apicDataProvider, attributeID);
  }

  /**
   * Check, if the attribute ID is valid The ID is valid if the related database entity is available
   *
   * @return TRUE if the attributeID is valid
   */
  private boolean attrIdValid() {
    return getEntityProvider().getDbAttribute(getID()) != null;
  }

  /**
   * {@inheritDoc} returns true if the user has APIC_WRITE access
   */
  @Override
  public boolean isModifiable() {

    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    return false;

  }

  /**
   * Get a sorted set of the Attribute
   *
   * @return NodeAccessRight
   */
  public SortedSet<NodeAccessRight> getAccessRights() {
    return getDataLoader().getNodeAccessRights(getID());
  }


  /**
   * Check, if the current user is allowed to modify values of the Attribute. Modify includes Create, Update and mark as
   * deleted
   *
   * @return TRUE if user is allowed to modify
   */
  public boolean canModifyValues() {

    if (getDataCache().getCurrentUser().hasApicWriteAccess() || isWriteAccessEnabled()) {
      return true;
    }
    return false;

  }

  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @return Whether the Attribute has uncleared Values
   */
  public boolean hasUnclearedValues() {

    for (AttributeValue attributeValue : getAttrValues()) {
      if (!attributeValue.isCleared() && !attributeValue.isDelOrRej()) {
        return true;
      }
    }
    return false;
  }


  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @return the cleared Value List
   */
  public List<AttributeValue> getUnclearedValues() {

    final List<AttributeValue> unclearedValList = new ArrayList<AttributeValue>();
    for (AttributeValue attributeValue : getAttrValues()) {
      if (!attributeValue.isCleared()) {
        unclearedValList.add(attributeValue);
      }
    }
    return unclearedValList;
  }

  /**
   * Check, if the current user is allowed to modify dependencies of the Attribute. Modify includes Create, Update and
   * mark as deleted
   *
   * @return TRUE if user is allowed to modify
   */
  public boolean canModifyDependencies() {

    if (getDataCache().getCurrentUser().hasApicWriteAccess() || isWriteAccessEnabled()) {
      return true;
    }
    return false;

  }

  /**
   * Get the attributeID of the Attribute object
   *
   * @return The unique ID of the Attribute (from the database object)
   */
  public Long getAttributeID() {
    return getID();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getVersion();
    }
    return Long.valueOf(0);
  }

  /**
   * Get the ID of the ValueType defined for the Attribute If the Attribute is not valid (not defined in the database) 0
   * will be returned.
   *
   * @return The ValueTypeID as defined in the database
   */
  public long getValueTypeID() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getTabvAttrValueType().getValueTypeId();
    }
    return 0;
  }

  /**
   * Get the ValueType defined for the Attribute If the Attribute is not valid (not defined in the database) an empty
   * String will be returned.
   *
   * @return the value type as AttributeValueType enum value
   */
  public AttributeValueType getValueType() {
    return AttributeValueType
        .getType(getEntityProvider().getDbAttribute(getID()).getTabvAttrValueType().getValueTypeId());
  }

  /**
   * Get the creation date of the Attribute
   *
   * @return The date when the attribute has been created in the database
   */
  public Calendar getAttrCreatedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbAttribute(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * Get the ID of the user who has created the Attribute
   *
   * @return The ID of the user who has created the Attribute
   */
  public String getAttrCreatedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getCreatedUser();
    }
    return "";
  }

  /**
   * Get the date when the Attribute has been modified the last time
   *
   * @return The date when the Attribute has been modified the last time
   */
  public Calendar getAttrModifiedDate() {
    if (attrIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbAttribute(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * Get the user who has modified the Attribute the last time
   *
   * @return The user who has modified the Attribute the last time
   */
  public String getAttrModifiedUser() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getModifiedUser();
    }
    return "";
  }

  /**
   * Check, if the Attribute has been marked as deleted
   *
   * @return TRUE, if the Attribute has been marked as deleted
   */
  public boolean isDeleted() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getDeletedFlag().equals(ApicConstants.YES);
    }
    return false;
  }

  /**
   * Check, if the Attribute has been defined as normalized A normalized attribute can have only pre-defined values
   *
   * @return TRUE, if the Attribute has been marked as normalized
   */
  public boolean isNormalized() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getNormalizedFlag().equals(ApicConstants.YES);
    }
    return false;
  }

  /**
   * Check, if the Attribute has Part Number
   *
   * @return TRUE, if the Attribute has Part Number
   */
  public boolean hasPartNumber() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getPartNumberFlag().equals(ApicConstants.YES);
    }
    return false;
  }

  /**
   * Check, if the Attribute has Specification
   *
   * @return TRUE, if the Attribute has Specification
   */
  public boolean hasSpecLink() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getSpecLinkFlag().equals(ApicConstants.YES);
    }
    return false;
  }


  /**
   * Icdm-480 Check, if the Attribute is Secured Internally
   *
   * @return TRUE, if the Attribute has Specification
   */
  public boolean isAttrExternal() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getAttrSecurity().equals(ApicConstants.EXTERNAL);
    }
    return false;
  }


  /**
   * Icdm-480 Check, if the Attribute is Secured Internally
   *
   * @return TRUE, if the Attribute has Specification
   */
  public boolean isValueExternal() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getValueSecurity().equals(ApicConstants.EXTERNAL);
    }
    return false;
  }

  /**
   * Return the text representation of the attribute's normalised flag. Should be used only when the text value is to be
   * displayed
   *
   * @return Yes if attribute is normalised, else No
   */
  public String getNormalized() {
    return isNormalized() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * Return the text representation of the attribute's normalised flag. Should be used only when the text value is to be
   * displayed
   *
   * @return Yes if attribute is normalised, else No
   */
  public String getMandatory() {
    return isMandatory() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * Check, if the Attribute has been defined as Mandatory A mandatory Attribute can not be defined as "not used" in a
   * PIDC
   *
   * @return TRUE, if the Attribute is defined as mandatory
   */
  public boolean isMandatory() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getMandatory().equals(ApicConstants.YES);
    }
    return false;
  }

  /**
   * Check if the attribute is valid (visible) based on the attribute dependencies. This method assumes: - that an
   * attribute depends on only one attribute - that if an attribute depends on the used flag, only one dependency is
   * defined
   *
   * @param refPidcAttributes the list of attributes defined in the PIDC
   * @return true if the attr is valid
   */
  protected <P extends IPIDCAttribute> boolean isValid(final Map<Long, P> refPidcAttributes) {

    // the list of all dependencies of this attribute
    List<AttrDependency> dependencies = getAttrDependencies(false/* includeDeleted */);

    if (dependencies.isEmpty()) {
      // no dependencies
      return true;
    }
    // get the attribute on which this attribute depends on
    final Long dependencyAttrID = dependencies.get(0).getDependencyAttribute().getAttributeID();

    // check, if the dependency attribute is deleted
    if (getDataCache().getAllAttributes().get(dependencyAttrID).isDeleted()) {
      // the dependency attribute is deleted
      // thus the attribute is visible
      return true;
    }
    boolean equalsProjName =
        getDataCache().getAllAttributes().get(dependencyAttrID).equals(getDataCache().getProjNameAttribute());
    // ICDM-196 null check placed if the depency is at higher level
    // ICDM-963
    if (((refPidcAttributes.get(dependencyAttrID) == null) || refPidcAttributes.get(dependencyAttrID).isVariant()) &&
        !equalsProjName) {
      return true;
    }


    // check, if attribute depends on the used flag
    // ICDM-962
    if ((dependencies.get(0).getDependencyValueID() == null) && refPidcAttributes.get(dependencyAttrID).isVisible()) {
      // attribute depends on the used flag
      // check the used flag of the dependency attribute in the PIDC
      // ICDM-133
      return refPidcAttributes.get(dependencyAttrID).getIsUsed().equals(ApicConstants.USED_YES_DISPLAY);
    }
    AttributeValue referenceValue;
    // If the dependent attr is Project Name - ICDM-2118
    if (equalsProjName && !refPidcAttributes.isEmpty()) {
      referenceValue = (refPidcAttributes.values().iterator().next()).getPidcVersion().getPidc().getNameValue();
    }
    else {
      // get the value defined in the PIDC for the dependency attribute
      referenceValue = refPidcAttributes.get(dependencyAttrID).getAttributeValue();
    }
    // check if a value is defined for the attribute and not a Variant or Sub variant
    if (referenceValue == null) {
      return refPidcAttributes.get(dependencyAttrID).isVariant();
    }

    // iterate over all dependencies
    for (AttrDependency attrDependency : dependencies) {
      // If the dependent attr is Project Name - ICDM-2118
      if (equalsProjName) {
        if ((attrDependency.getDependencyValue() != null) &&
            attrDependency.getDependencyValue().equals(referenceValue)) {
          return true;
        }
      }
      // check the value
      // ICDM-962
      if ((attrDependency.getDependencyValue() != null) &&
          attrDependency.getDependencyValue().getValueID().equals(referenceValue.getValueID()) &&
          refPidcAttributes.get(dependencyAttrID).isVisible()) {
        return true;
      }

    }
    return false;

  }

  /**
   * Get the name of the Attribute Currently only the English name will be returned. Multi language support will be
   * implementd later.
   *
   * @return The name of the Attribute
   */
  public String getAttributeName() {
    if (attrIdValid()) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getAttributeNameEng(), getAttributeNameGer(),
          ApicConstants.EMPTY_STRING);
    }
    return "";
  }

  /**
   * Returns the Attribute Name in ENGLISH
   *
   * @return Attribute English Name in String
   */
  public String getAttributeNameEng() {
    String returnValue = getEntityProvider().getDbAttribute(getID()).getAttrNameEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Attribute Name in GERMAN
   *
   * @return Attribute German Name in String
   */
  public String getAttributeNameGer() {
    String returnValue = getEntityProvider().getDbAttribute(getID()).getAttrNameGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }


  /**
   * Get the description of the Attribute Currently only the English description will be returned. Multi language
   * support will be implementd later.
   *
   * @return The description of the Attribute
   */
  public String getAttributeDesc() {
    if (attrIdValid()) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getAttributeDescEng(), getAttributeDescGer(),
          ApicConstants.EMPTY_STRING);
    }
    return "";
  }

  /**
   * Returns the Attribute Description in ENGLISH
   *
   * @return Attribute English description in String
   */
  public String getAttributeDescEng() {
    String returnValue = getEntityProvider().getDbAttribute(getID()).getAttrDescEng();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Returns the Attribute Description in GERMAN
   *
   * @return Attribute German Description in String
   */
  public String getAttributeDescGer() {
    String returnValue = getEntityProvider().getDbAttribute(getID()).getAttrDescGer();
    if (returnValue == null) {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Get the ID of the Group to which the Attribute has been assigned
   *
   * @return The ID of the Group to which the Attribute has been assigned
   */
  public long getAttributeGroupID() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getTabvAttrGroup().getGroupId();
    }
    return 0;
  }

  /**
   * Get the Group to which the Attribute has been assigned
   *
   * @return The Group to which the Attribute has been assigned
   */
  public AttrGroup getAttributeGroup() {
    return getDataCache().getGroup(getAttributeGroupID());
  }

  /**
   * Get the Attribute Level which has been defined for the attribute. If no level has ben defined, 0 will be returned.
   *
   * @return the Attribute Level which has been defined for the attribute.
   */
  public int getAttrLevel() {
    if (attrIdValid()) {
      BigDecimal bdAttrLevel = getEntityProvider().getDbAttribute(getID()).getAttrLevel();

      if (bdAttrLevel != null) {
        return bdAttrLevel.intValue();
      }
      return 0;
    }
    return 0;
  }

  /**
   * iCDM-1345 Check if this aattribute is a level attribute
   *
   * @return true if level attr
   */
  public boolean isLevelAttr() {
    return getAttrLevel() != 0;
  }

  /**
   * Get the Format definition defined for the Attribute If no Format has been defined, an empty String will be returned
   *
   * @return The format definition of the Attribute
   */
  public String getFormat() {

    String format = null;

    if (attrIdValid()) {
      format = getEntityProvider().getDbAttribute(getID()).getFormat();
    }

    if (format == null) {
      return "";
    }
    return format;
  }

  /**
   * Get the Unit defined for the Attribute
   *
   * @return the Unit defined for the Attribute
   */
  public String getUnit() {
    if (attrIdValid()) {
      return getEntityProvider().getDbAttribute(getID()).getUnits();
    }
    return "";
  }


  /**
   * Icdm-954 Get the Unit defined for the Attribute
   *
   * @return the Unit defined for the Attribute
   */
  public AttributeCharacteristic getCharacteristic() {
    if (attrIdValid()) {
      final TCharacteristic tabvChar = getEntityProvider().getDbAttribute(getID()).gettCharacteristic();
      if (tabvChar != null) {
        return getDataCache().getAllCharMap().get(tabvChar.getCharId());
      }
    }
    return null;
  }


  /**
   * Icdm-956 get the Character String
   *
   * @return the Char string
   */
  public String getCharStr() {
    String charStr = "";
    final AttributeCharacteristic characteristic = getCharacteristic();
    if (characteristic != null) {
      charStr = characteristic.getName();
    }
    return charStr;
  }

  /**
   * @return true if the Attribute has characteristic
   */
  public boolean hasCharacteristic() {
    return getCharacteristic() == null ? false : true;
  }

  /**
   * @return a list of Attribute Values with Char Values
   */
  public List<AttributeValue> getValuesWithCharVal() {
    final List<AttributeValue> vallWithCharVal = new ArrayList<AttributeValue>();
    for (AttributeValue attrVal : getAttrValues(false)) {
      if (attrVal.hasCharactersiticValue()) {
        vallWithCharVal.add(attrVal);
      }
    }
    return vallWithCharVal;
  }

  /**
   * ICdm-956
   *
   * @return true if the Attribute's values have Char Value
   */
  public boolean hasCharValue() {
    final List<AttributeValue> attrValues = getAttrValues(false);
    if (attrValues != null) {
      for (AttributeValue attrValue : attrValues) {
        if (attrValue.getCharacteristicValue() != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Get the values which are defined for the attribute Return NULL if the attribute is not valid
   *
   * @param includeDeleted whether to include the deleted values
   * @return the list of attribute values
   */
  public List<AttributeValue> getAttrValues(final boolean includeDeleted) {
    // create the result object
    final List<AttributeValue> resultList = new ArrayList<AttributeValue>();

    synchronized (getDataCache().attrSyncLock) {

      // iterate over all values assigned to the attribute
      for (TabvAttrValue dbAttrValue : getEntityProvider().getDbAttribute(getID()).getTabvAttrValues()) {
        // get the AttributeValue object for the current value from the data provider
        if (includeDeleted) {
          resultList.add(getDataCache().getAttrValue(dbAttrValue.getValueId()));
        }
        else {
          // check if the value is deleted
          if (!dbAttrValue.getDeletedFlag().equals(ApicConstants.YES)) {
            // add only not deleted value to the results list
            resultList.add(getDataCache().getAttrValue(dbAttrValue.getValueId()));
          }
        }
      }
    }

    return resultList;


  }

  /**
   * Get the values which are defined for the attribute. The return includes the deleted values also.
   *
   * @return the list of values defined for this attribute. NULL if the attribute is not valid
   */
  public List<AttributeValue> getAttrValues() {
    return getAttrValues(true); // Include deleted
  }

  /**
   * Get all DEPENDENCIES defined for this attribute, pass parameter to include deleted items
   *
   * @param includeDeleted - if TRUE includes items which are even marked as deleted, FALSE otherwise
   * @return List<AttrDependency>
   */
  public List<AttrDependency> getAttrDependencies(final boolean includeDeleted) {
    // create the result object
    List<AttrDependency> attrDependencies = new ArrayList<AttrDependency>();

    synchronized (getDataCache().attrSyncLock) {
      // iterate over all dependencies defined for this attribute
      for (TabvAttrDependency dbDependency : getEntityProvider().getDbAttribute(getID()).getTabvAttrDependencies()) {

        if (includeDeleted) {
          // add all dependencies which are also marked as deleted
          attrDependencies.add(getDataCache().getAttrDependency(dbDependency.getDepenId()));
        }
        else {
          // check if the dependency is deleted
          if (!dbDependency.getDeletedFlag().equals(ApicConstants.YES)) {
            // add only not deleted dependencies to the results list
            attrDependencies.add(getDataCache().getAttrDependency(dbDependency.getDepenId()));
          }
        }

      }
    }
    return attrDependencies;
  }

  /**
   * ICDM-120
   *
   * @param refPidcAttributes Reference PIDC attributes
   * @param includeDeleted true/false
   * @return valid values
   */
  public List<AttributeValue> getValidAttrValues(final Map<Long, IPIDCAttribute> refPidcAttributes,
      final boolean includeDeleted) {

    final List<AttributeValue> valueList = getAttrValues(includeDeleted);
    final List<AttributeValue> resultList = new ArrayList<AttributeValue>();
    for (AttributeValue attributeValue : valueList) {
      attributeValue.setValidValue(attributeValue.isValidValue(refPidcAttributes, false));
      resultList.add(attributeValue);
    }
    return resultList;

  }

  /**
   * ICDM-133 Get all Attributes which are depending on this attribute, pass parameter to include deleted items
   *
   * @param includeDeleted - if TRUE includes items which are even marked as deleted, FALSE otherwise
   * @return List<AttrDependency>
   */
  public List<AttrDependency> getReferentialAttrDependencies(final boolean includeDeleted) {

    // create the result object
    List<AttrDependency> attrDependencies = new ArrayList<AttrDependency>();

    synchronized (getDataCache().attrSyncLock) {
      // iterate over all dependencies defined for this attribute
      for (TabvAttrDependency dbDependency : getEntityProvider().getDbAttribute(getID()).getTabvAttrDependenciesD()) {

        if (includeDeleted) {
          // add all dependencies which are also marked as deleted
          attrDependencies.add(getDataCache().getAttrDependency(dbDependency.getDepenId()));
        }
        else {
          // check if the dependency is deleted
          if (!dbDependency.getDeletedFlag().equals(ApicConstants.YES)) {
            // add only not deleted dependencies to the results list
            attrDependencies.add(getDataCache().getAttrDependency(dbDependency.getDepenId()));
          }
        }

      }
    }
    return attrDependencies;
  }

  /**
   * @return dependencies as string
   */
  public String getDependencies() {

    StringBuilder depStr = new StringBuilder();

    int countr;

    List<AttrDependency> attrDepList = getAttrDependencies(false);

    if (!attrDepList.isEmpty()) {

      depStr.append("[ ");

      countr = 0;
      for (AttrDependency dependency : attrDepList) {
        countr++;

        if (countr > 1) {
          depStr.append(" [OR] ");
        }

        depStr.append(dependency.getDependencyAttribute().getAttributeName()).append(" = ")
            .append(dependency.getDependencyValueText());
      }
      depStr.append(" ]");
    }

    return depStr.toString();
  }

  /**
   * iCDM-1345 Checks if this attribute is dependent on level attribute
   *
   * @return true if there is atleast one dependency on level attr
   */
  public boolean isDependentOnLevelAttr() {
    for (AttrDependency dependency : getAttrDependencies(false)) {
      if (dependency.getDependencyAttribute().isLevelAttr()) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc} returns compare result of two attributes
   */
  @Override
  public int compareTo(final Attribute arg0) {
    // Compare attribute name, (if english not available, then german name is compared)
    int compResult = ApicUtil.compare(getAttributeName(), arg0.getAttributeName());
    // iCDM-1039; If name is same, then compare by attribute id
    if (compResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ApicUtil.compare(getAttributeID(), arg0.getAttributeID());
    }
    return compResult;
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final Attribute arg0, final int sortColumn) { // NOPMD by bne4cob on 6/20/14 10:27 AM

    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
        // attribute name needs not to be compared because it is the default sort
        compareResult = 0;
        break;

      case ApicConstants.SORT_ATTRDESCR:
        compareResult = ApicUtil.compare(getAttributeDesc(), arg0.getAttributeDesc());
        break;

      case ApicConstants.SORT_SUPERGROUP:
        compareResult = compSuperGrp(arg0);
        break;

      case ApicConstants.SORT_GROUP:
        compareResult = ApicUtil.compare(getAttributeGroup().getName(), arg0.getAttributeGroup().getName());
        break;

      case ApicConstants.SORT_LEVEL:
        compareResult = ApicUtil.compareLong(getAttrLevel(), arg0.getAttrLevel());
        break;

      case ApicConstants.SORT_UNIT:
        compareResult = ApicUtil.compare(getUnit(), arg0.getUnit());
        break;

      case ApicConstants.SORT_VALUETYPE:
        compareResult = ApicUtil.compare(getValueType(), arg0.getValueType());
        break;
      // ICDM-179
      case ApicConstants.SORT_MANDATORY:
        // compare only boolean value information
        compareResult = ApicUtil.compareBoolean(isMandatory(), arg0.isMandatory());
        break;
      // ICDM-860
      case ApicConstants.SORT_NORMALIZED_FLAG:
        compareResult = ApicUtil.compareBoolean(isNormalized(), arg0.isNormalized());
        break;
      case ApicConstants.SORT_FORMAT:
        compareResult = ApicUtil.compare(getFormat(), arg0.getFormat());
        break;
      case ApicConstants.SORT_PART_NUMBER:
        compareResult = ApicUtil.compareBoolean(hasPartNumber(), arg0.hasPartNumber());
        break;
      case ApicConstants.SORT_SPEC_LINK:
        compareResult = ApicUtil.compareBoolean(hasSpecLink(), arg0.hasSpecLink());
        break;
      // ICdm-480
      case ApicConstants.SORT_ATTR_SEC:
        compareResult = ApicUtil.compareBoolean(isAttrExternal(), arg0.isAttrExternal());
        break;
      case ApicConstants.SORT_ATTR_VAL_SEC:
        compareResult = ApicUtil.compareBoolean(isValueExternal(), arg0.isValueExternal());
        break;
      case ApicConstants.SORT_CHAR:
        compareResult = ApicUtil.compare(getCharStr(), arg0.getCharStr());
        break;
      // ICDM-1560
      case ApicConstants.SORT_ATTR_EADM_NAME:
        compareResult = ApicUtil.compare(getEadmName(), arg0.getEadmName());
        break;
      case ApicConstants.SORT_ATTR_CREATED_DATE_PIDC:
        compareResult = ApicUtil.compareCalendar(getAttrCreatedDate(), arg0.getAttrCreatedDate());
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

  /**
   * @param arg0
   * @return
   */
  private int compSuperGrp(final Attribute arg0) {
    int compareResult;
    compareResult = ApicUtil.compare(getAttributeGroup().getSuperGroup().getName(),
        arg0.getAttributeGroup().getSuperGroup().getName());

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compareResult = ApicUtil.compare(getAttributeGroup().getName(), arg0.getAttributeGroup().getName());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    objDetails.put(FLD_ATTR_NAME_ENG, getAttributeNameEng());
    objDetails.put(FLD_ATTR_NAME_GER, getAttributeNameGer());
    objDetails.put(FLD_ATTR_DESC_ENG, getAttributeDescEng());
    objDetails.put(FLD_ATTR_DESC_GER, getAttributeDescEng());
    objDetails.put(FLD_ATTR_LEVEL, String.valueOf(getAttrLevel()));
    objDetails.put(FLD_VAL_TYPE, getValueType().getDisplayText());
    objDetails.put(FLD_DELETED_FLAG, String.valueOf(isDeleted()));
    objDetails.put(FLD_NRMALIZD_FLG, String.valueOf(isNormalized()));
    objDetails.put(FLD_PRT_NUM_FLAG, String.valueOf(hasPartNumber()));
    objDetails.put(FLD_SPC_LNK_FLG, String.valueOf(hasSpecLink()));
    objDetails.put(FLD_GROUP, getAttributeGroup().getName());
    objDetails.put(FLD_ATTR_CLASS, getCharStr());

    return objDetails;
  }

  /**
   * @return is attr enabled
   */
  public boolean isGrantAccessEnabled() {
    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    // Icdm-346
    NodeAccessRight accessRights = getDataCache().getNodeAccRights(getID());
    if ((accessRights != null) && (accessRights.hasGrantOption())) {
      return true;
    }
    return false;
  }


  /**
   * @return is attr enabled
   */
  public boolean isWriteAccessEnabled() {
    // Icdm-346
    NodeAccessRight accessRights = getDataCache().getNodeAccRights(getID());
    if ((accessRights != null) && (accessRights.hasWriteAccess())) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getID() == null) ? 0 : getID().hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc} return String form of attribute
   */
  @Override
  public String toString() {
    return "Attribute [attributeID=" + getID() + ", Name=" + getAttributeName() + ", ValueType=" + getValueType() +
        ", Normalized=" + isNormalized() + ", Mandatory=" + isMandatory() + "]";
  }

  // ICDM-374
  /**
   * @return true if part number is used
   */
  public String hasPartNumberAsString() {
    return hasPartNumber() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * @return true if specification link is used
   */
  public String hasSpecAsString() {
    return hasSpecLink() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * Icdm-480
   *
   * @return true if Attr is Internally Secured
   */
  public String isAttrExtAsString() {
    return isAttrExternal() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }

  /**
   * @return true if attr Value is Internally Secured
   */
  public String isAttrValExtAsString() {
    return isValueExternal() ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
  }


  /**
   * checks if user can add attribute value
   *
   * @return true if user can add value
   */
  // ICDM-367
  public boolean canAddValue() {
    // only for noarmlized attribute , attribute access is checked.
    if ((isNormalized() && canModifyValues()) || !isNormalized()) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAttributeName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.ATTRIBUTE;
  }

  /**
   * ICDM-452
   *
   * @return SortedSet of links
   */
  public SortedSet<Link> getLinks() {
    return getDataCache().getLinks(this);
  }

  /**
   * ICDM-1042 Tooltip for the nodes in the pidc tree
   *
   * @return tooltip text
   */
  @Override
  public String getToolTip() {
    StringBuilder toolTip = new StringBuilder(SIZE);
    toolTip.append("Attribute: ").append(getName()).append("\nDescription: ").append(getAttributeDesc());
    return toolTip.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getAttributeDesc();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getAttrCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getAttrModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return getAttrCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return getAttrModifiedDate();
  }

  // ICDM-1397
  /**
   * Returns the Attribute change comment
   *
   * @return Attribute change comment in String
   */
  public String getChangeComment() {
    String changeComment = getEntityProvider().getDbAttribute(getID()).getChangeComment();
    return CommonUtils.checkNull(changeComment);
  }

  // ICDM-1560
  /**
   * Returns the Attribute eadm name
   *
   * @return Attribute eadm name in String
   */
  public String getEadmName() {
    return CommonUtils.checkNull(getEntityProvider().getDbAttribute(getID()).getEadmName());
  }

  /**
   * ICDM-2430 Checks whether the attribute can be moved to from PIDC level to variant
   *
   * @return TRUE, if the Attribute can be moved to variant/subvar level
   */
  public boolean canMoveDown() {
    return !(ApicConstants.CODE_NO).equals(getEntityProvider().getDbAttribute(getID()).getMoveDownYN());
  }

  /**
   * Check, if the Attribute is grouped attribute
   *
   * @return TRUE, if the Attribute is a grouped attr
   */
  public boolean isGrouped() {
    return CommonUtils.isEqual(ApicConstants.YES, getEntityProvider().getDbAttribute(getID()).getGroupFlag());
  }
}