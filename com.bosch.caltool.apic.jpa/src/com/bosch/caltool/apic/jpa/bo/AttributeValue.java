/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.bo.apic.attr.AttrValueTextResolver;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristicValue;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;

/**
 * This class represents an APIC user as stored in the database table TABV_ATTR_VALUES
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AttributeValue extends ApicObject implements Comparable<AttributeValue>, IPastableItem {


  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Value column
                            */
                           SORT_ATTR_VAL,
                           /**
                            * Unit column
                            */
                           SORT_ATTR_VAL_UNIT,
                           /**
                            * Description column
                            */
                           SORT_ATTR_VAL_DESC,
                           /**
                            * Status column
                            */
                           SORT_ATTR_CLEAR_STATUS,

                           /**
                            * Icdm-955 sort char value
                            */
                           SORT_CHAR_VALUE
  }


  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @author rgo7cob enumeration for the Clearing status
   */
  public enum CLEARING_STATUS {
                               /**
                                * cleared Value
                                */
                               CLEARED("Y", "Cleared"),
                               /**
                                * not cleared Value
                                */
                               NOT_CLEARED("N", "Not Cleared"),
                               /**
                                * In clearing
                                */
                               IN_CLEARING("I", "In Clearing"),

                               /**
                                * Deleted Status - Icdm-1180 new Status
                                */
                               DELETED("D", "Deleted"),
                               /**
                                * Rejected Icdm-1180 new Status
                                */
                               REJECTED("R", "Rejected");

    /**
     * DB Text for clearing status
     */
    private final String dbText;

    /**
     * UI text for clearing status
     */
    private final String uiText;


    /**
     * Constructor
     *
     * @param dbText DB text
     * @param uiText UI display text
     */
    CLEARING_STATUS(final String dbText, final String uiText) {
      this.dbText = dbText;
      this.uiText = uiText;
    }

    /**
     * @return the text
     */
    public String getDBText() {
      return this.dbText;
    }

    /**
     * @return the ui text
     */
    public String getUiText() {
      return this.uiText;
    }

    /**
     * @param dbText dbType
     * @return the Enum For the Cleraing Status
     */
    public static CLEARING_STATUS getClearingStatus(final String dbText) {

      for (CLEARING_STATUS clrStatus : CLEARING_STATUS.values()) {
        if (clrStatus.getDBText().equals(dbText)) {
          return clrStatus;
        }
      }
      return null;

    }


  }

  /**
   * Object details Key for deleted flag
   */
  private static final String FLD_DEL_FLAG = "DELETED_FLAG";
  /**
   * Object details Key for numeric value
   */
  private static final String FLD_NUM_VALUE = "NUM_VALUE";
  /**
   * Object details Key for english text value
   */
  private static final String FLD_TXTVAL_ENG = "TEXTVALUE_ENG";
  /**
   * Object details Key for german text value
   */
  private static final String FLD_TXTVAL_GER = "TEXTVALUE_GER";
  /**
   * Object details Key for english description
   */
  private static final String FLD_VAL_DSC_ENG = "VALUE_DESC_ENG";
  /**
   * Object details Key for german description
   */
  private static final String FLD_VAL_DSC_GER = "VALUE_DESC_GER";

  /**
   * A flag to indicate whether the value is visible or not due to dependencies
   */
  private boolean isValidValue = true;


  /**
   * Constructor
   *
   * @param apicDataProvider data provider
   * @param valueID value ID
   */
  public AttributeValue(final ApicDataProvider apicDataProvider, final Long valueID) {
    super(apicDataProvider, valueID);
  }

  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @return the Clearing Status of the Attribute
   */
  public String getClearingStatusStr() {
    return getClearingStatus().getUiText();
  }


  /**
   * @return the Clearing Status Enum of the Attribute
   */
  public CLEARING_STATUS getClearingStatus() {
    return CLEARING_STATUS.getClearingStatus(getEntityProvider().getDbValue(getValueID()).getClearingStatus());
  }

  /**
   * @return whether the value is cleared
   */
  public boolean isCleared() {
    // Icdm-830 Data Model Changes
    return getClearingStatus() == CLEARING_STATUS.CLEARED;
  }

  /**
   * ICdm-1180 - new method to check for Deleted or Cleared Value Status
   *
   * @return whether the value is cleared
   */
  public boolean isDelOrRej() {
    // Icdm-830 Data Model Changes
    return (getClearingStatus() == CLEARING_STATUS.DELETED) || (getClearingStatus() == CLEARING_STATUS.REJECTED);
  }

  /**
   * {@inheritDoc} return true if the current user has APIC_WRITE access
   */
  @Override
  public boolean isModifiable() {

    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    return false;

  }


  /**
   * ICdm-954 Get the Char Val defined for the Attribute Value
   *
   * @return the Char Val defined for the Attribute Val
   */
  public AttributeCharacteristicValue getCharacteristicValue() {

    final TCharacteristicValue tabvCharValue = getEntityProvider().getDbValue(getID()).gettCharacteristicValue();
    if (tabvCharValue != null) {
      if (getDataCache().getAllCharValMap().get(tabvCharValue.getCharValId()) == null) {
        // If the Value is added to the Cache Already create the new Obj Will Automatically add to the cache
        new AttributeCharacteristicValue(getDataCache().getDataProvider(), tabvCharValue.getCharValId());
      }
      return getDataCache().getAllCharValMap().get(tabvCharValue.getCharValId());
    }
    return null;
  }


  /**
   * ICdm-955
   *
   * @return the Char string
   */
  public String getCharValStr() {
    String charValStr = "";
    if (!CommonUtils.isEqual(getID(), AttributeValueDummy.VALUE_ID)) {
      AttributeCharacteristicValue charVal = getCharacteristicValue();
      if (charVal != null) {
        charValStr = charVal.getName();
      }
    }
    return charValStr;
  }


  /**
   * ICdm-955
   *
   * @return true if the value has Char Value
   */
  public boolean hasCharactersiticValue() {
    return getCharacteristicValue() == null ? false : true;
  }

  /**
   * @return id of the attribute value
   */
  public Long getValueID() {
    return getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbValue(getValueID()).getVersion();
  }

  /**
   * @return id of the attribute
   */
  public Long getAttributeID() {
    return getEntityProvider().getDbValue(getID()).getTabvAttribute().getAttrId();
  }

  /**
   * @return attribute object
   */
  public Attribute getAttribute() {
    return getDataCache().getAttribute(getAttributeID());
  }

  /**
   * Get the attribute value as a String independently of the datatype. Textvalues will be returned in the language
   * defined in the ApicDataProvider setting.
   *
   * @return the attribute value as a String
   */
  public String getValue() {
    return getValue(false);
  }

  /**
   * Get the attribute value as a String independently of the datatype. Textvalues will be returned in the language
   * defined in the ApicDataProvider setting
   *
   * @param showUnit if true, unit will be added at the end for number type values
   * @return the attribute value as a String
   */
  public String getValue(final boolean showUnit) {
    String dispVal = AttrValueTextResolver.getStringValue(getEntityProvider().getDbValue(getID()),
        getDataCache().getLanguage(), false);
    if (showUnit && (getAttribute().getValueType() == AttributeValueType.NUMBER)) {
      String unit = getUnit();
      // ICDM-1248
      if (!CommonUtils.isEqual(CommonUtils.checkNull(unit).trim(), ApicConstants.ATTRVAL_EMPTY_UNIT)) {
        dispVal = CommonUtils.concatenate(dispVal, " ", unit);
      }
    }

    return dispVal;
  }

  /**
   * Get the attribute value as a String independently of the datatype. Textvalues will be returned always in English,
   * independently of the language setting. This is used e.g. for the vCDM interface because values are stored in vCDM
   * only in English.
   *
   * @return the attribute value as a String
   */
  public String getValueEng() {
    return AttrValueTextResolver.getStringValue(getEntityProvider().getDbValue(getID()), getDataCache().getLanguage(),
        true);
  }

  /**
   * @return unit of the attribute value
   */
  public String getUnit() {
    return getAttribute().getUnit();
  }

  /**
   * @return description of the attribute value
   */
  @Override
  public String getDescription() {

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getValueDescEng(), getValueDescGer(),
        ApicConstants.EMPTY_STRING);
  }

  /**
   * @return attribute value in text format
   */
  public String getTextValue() {
    if ((getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_TEXT) ||
        (getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_HYPERLINK)) {

      return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getTextValueEng(), getTextValueGer(),
          ApicConstants.EMPTY_STRING);
    }
    return null;
  }

  /**
   * @return attribute value in number format
   */
  public BigDecimal getNumberValue() {
    if (getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_NUMBER) {
      return getEntityProvider().getDbValue(getID()).getNumvalue();
    }
    return null;

  }

  /**
   * @param isNumtype isNumType
   * @return the numValue
   */
  public BigDecimal getNumberValue(final boolean isNumtype) {
    if (isNumtype) {
      return getNumberValue();
    }
    return getEntityProvider().getDbValue(getID()).getNumvalue();
  }

  /**
   * @return attribute value in date format
   */
  public Calendar getDateValue() {
    if (getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_DATE) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbValue(getID()).getDatevalue());
    }
    return null;
  }

  /**
   * @return true if the attribute value is deleted
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbValue(getID()).getDeletedFlag().equals(ApicConstants.YES);
  }

  /**
   * @return created date of the attribute value
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbValue(getID()).getCreatedDate());
  }

  /**
   * @return created user of the attribute value
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbValue(getID()).getCreatedUser();
  }

  /**
   * @return modified date of the attribute value
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbValue(getID()).getModifiedDate());
  }

  /**
   * @return modified user of the attribute value
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbValue(getID()).getModifiedUser();
  }


  /**
   * Icdm-590
   *
   * @return the English Value of the Attr value of type Text
   */
  public String getValueEngText() {
    final TabvAttrValue dbValue = getEntityProvider().getDbValue(getID());
    if (ApicConstants.ATTR_VALUE_TYPE_TEXT == (int) dbValue.getTabvAttribute().getTabvAttrValueType()
        .getValueTypeId()) {
      return dbValue.getTextvalueEng();
    }
    return null;
  }

  /**
   * Icdm-590
   *
   * @return the English Value of the Attr value of type Text
   */
  public String getValueGer() {
    final TabvAttrValue dbValue = getEntityProvider().getDbValue(getID());
    if (ApicConstants.ATTR_VALUE_TYPE_TEXT == (int) dbValue.getTabvAttribute().getTabvAttrValueType()
        .getValueTypeId()) {
      return dbValue.getTextvalueGer();
    }
    return null;
  }

  /**
   * Returns the value(only TEXT) in ENGLISH language
   *
   * @return String
   */
  public String getTextValueEng() {
    String textValue = "";
    if ((getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_TEXT) ||
        (getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_HYPERLINK)) {
      textValue = getEntityProvider().getDbValue(getID()).getTextvalueEng();
    }
    return textValue;

  }

  /**
   * Returns the value(only TEXT) only in GERMAN language
   *
   * @return String
   */
  public String getTextValueGer() {
    String textValue = "";
    if (getAttribute().getValueTypeID() == ApicConstants.ATTR_VALUE_TYPE_TEXT) {
      textValue = getEntityProvider().getDbValue(getID()).getTextvalueGer() == null ? ""
          : getEntityProvider().getDbValue(getID()).getTextvalueGer();
    }
    return textValue;
  }

  /**
   * Returns the description in ENGLISH language
   *
   * @return String
   */
  public String getValueDescEng() {
    return getEntityProvider().getDbValue(getID()).getValueDescEng();
  }

  /**
   * Returns the description only in GERMAN language
   *
   * @return String
   */
  public String getValueDescGer() {
    return getEntityProvider().getDbValue(getID()).getValueDescGer() == null ? ""
        : getEntityProvider().getDbValue(getID()).getValueDescGer();
  }

  /**
   * Get all DEPENDENCIES defined for this value, pass parameter to include deleted items
   *
   * @param includeDeleted if TRUE includes items which are even marked as deleted, FALSE oterwise
   * @return List<AttrValueDependency>
   */
  public List<AttrValueDependency> getValueDependencies(final boolean includeDeleted) {
    List<AttrValueDependency> valueDependencies = new ArrayList<AttrValueDependency>();

    synchronized (getDataCache().attrSyncLock) {
      // Null check added, since locally created values's dependency list will be null
      if (getEntityProvider().getDbValue(getID()).getTabvAttrDependencies() == null) {
        return valueDependencies;
      }

      for (TabvAttrDependency dbDependency : getEntityProvider().getDbValue(getID()).getTabvAttrDependencies()) {

        if (includeDeleted) {
          // add all dependencies which are also marked as deleted
          valueDependencies.add(getDataCache().getValueDependency(dbDependency.getDepenId()));
        }
        else {
          // check if the dependency is deleted
          if (!dbDependency.getDeletedFlag().equals(ApicConstants.YES)) {
            // add only not deleted dependencies to the results list
            valueDependencies.add(getDataCache().getValueDependency(dbDependency.getDepenId()));
          }
        }

      }
    }
    return valueDependencies;
  }

  /**
   * @param curCntxtValMap Value map in the current context
   * @return true if the value is to be set as visible by checking the dependencies and current context
   */
  public boolean isValid(final Map<Long, AttributeValue> curCntxtValMap) {

    if (getValueDependencies(false).isEmpty()) {
      // no dependencies defined for this value
      // => value is visible
      return true;
    }
    for (AttrValueDependency valueDependency : getValueDependencies(false /* includeDeleted */)) {
      Long dependencyAttrID = valueDependency.getDependencyAttribute().getAttributeID();

      if (curCntxtValMap.containsKey(dependencyAttrID)) {
        // dependency attribute defined for current context
        if (curCntxtValMap.get(dependencyAttrID).getValueID().equals(valueDependency.getDependencyValueID())) {
          // value in current context is matching the dependency value
          // => value is visible
          return true;
        }
      }
      else {
        // dependency attribute not defined for current context
        // => value not visible
        return false;
      }

    }

    // no matching value in current context and dependencies
    // => value not visible
    return false;

  }

  /**
   * ICDM-120 Check if the value is valid (visible) based on the value dependencies. This method assumes: - that an
   * value has only one dependency - that if an value depends on the used flag, only one dependency is defined
   *
   * @param refPidcAttributes the list of attributes defined in the PIDC
   * @param includeDeleted included deleted values for checking
   * @return boolean
   */
  public boolean isValidValue(final Map<Long, IPIDCAttribute> refPidcAttributes, final boolean includeDeleted) {

    // the list of all dependencies of this attribute
    List<AttrValueDependency> dependencies = getValueDependencies(includeDeleted);

    if (dependencies.isEmpty()) {
      // no dependencies
      return true;
    }
    // get the attribute on which this attribute depends on
    Long dependencyAttrID = dependencies.get(0).getDependencyAttribute().getAttributeID();

    // check, if the dependency attribute is deleted
    if (getDataCache().getAllAttributes().get(dependencyAttrID).isDeleted()) {
      // the dependency attribute is deleted
      // thus the attribute is visible
      return true;
    }
    boolean equalsProjName =
        getDataCache().getAllAttributes().get(dependencyAttrID).equals(getDataCache().getProjNameAttribute());
    // ICDM-196 place a null check if the dependency is at higher level
    if (((refPidcAttributes.get(dependencyAttrID) == null) || refPidcAttributes.get(dependencyAttrID).isVariant()) &&
        !equalsProjName) {
      return false;
    }


    // check, if attribute depends on the used flag
    if ((dependencies.get(0).getDependencyValueID() == null) && refPidcAttributes.get(dependencyAttrID).isVisible()) {
      // attribute depends on the used flag
      // check the used flag of the dependency attribute in the PIDC
      return refPidcAttributes.get(dependencyAttrID).getUsedInfo().equals(ApicConstants.YES);
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
    // check if a value is defined for the attribute
    if (referenceValue == null) {
      // no value defined for dependency attribute
      // ==> attribute invisible
      return false;
    }

    // iterate over all dependencies
    for (AttrValueDependency attrValDependency : dependencies) {
      // If the dependent attr is Project Name - ICDM-2118
      if (equalsProjName) {
        if ((attrValDependency.getDependencyValue() != null) &&
            attrValDependency.getDependencyValue().equals(referenceValue)) {
          return true;
        }
      }
      // check the value
      if ((attrValDependency.getDependencyValue() != null) &&
          attrValDependency.getDependencyValue().getValueID().equals(referenceValue.getValueID()) &&
          refPidcAttributes.get(dependencyAttrID).isVisible()) {
        return true;
      }

    }

    return false;

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
   * {@inheritDoc} compare result of the attribute values
   */

  @Override
  public int compareTo(final AttributeValue attrValue2) {
    int compareResult = 0;


    int attrValueType = (int) getAttribute().getValueTypeID();
    int attrValue2Type = (int) attrValue2.getAttribute().getValueTypeID();

    if (attrValueType == attrValue2Type) {

      switch (attrValueType) {
        case ApicConstants.ATTR_VALUE_TYPE_TEXT:
        case ApicConstants.ATTR_VALUE_TYPE_HYPERLINK: // iCDM-321
          compareResult = ApicUtil.compare(getTextValue(), attrValue2.getTextValue());
          break;

        case ApicConstants.ATTR_VALUE_TYPE_NUMBER:
          compareResult = ApicUtil.compareBigDecimal(getNumberValue(), attrValue2.getNumberValue());
          break;

        case ApicConstants.ATTR_VALUE_TYPE_DATE:
          compareResult = ApicUtil.compareCalendar(getDateValue(), attrValue2.getDateValue());
          break;

        case ApicConstants.ATTR_VALUE_TYPE_BOOLEAN:
          compareResult = ApicUtil.compare(getValue(), attrValue2.getValue());
          break;

        default:
          break;
      }

      if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
        // compare result is equal, compare the attribute names
        compareResult = getAttribute().compareTo(attrValue2.getAttribute());
      }

    }
    else {
      // different value types to be compared
      // => do a String compare
      compareResult = getValue().compareTo(attrValue2.getValue());
    }


    return compareResult;
  }

  /**
   * Compare the value against another String This is used e.g. to compare the value against a "special value" like
   * <VARIANT>
   *
   * @param stringValue2 other vlaue
   * @return int
   */
  public int compareTo(final String stringValue2) {

    return ApicUtil.compare(getValue(), stringValue2);
  }

  /**
   * Compare objects with sort column
   *
   * @param arg0 other vlaue
   * @param sortColumn sort column
   * @return int
   */
  public int compareTo(final AttributeValue arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_VAL:
        compareResult = this.compareTo(arg0);
        break;
      case SORT_ATTR_VAL_UNIT:
        compareResult = getUnit().compareTo(arg0.getUnit());
        break;
      case SORT_ATTR_VAL_DESC:
        compareResult = getDescription().compareTo(arg0.getDescription());
        break;
      // Icdm-830 Data Model Changes
      case SORT_ATTR_CLEAR_STATUS:
        compareResult = getClearingStatusStr().compareTo(arg0.getClearingStatusStr());
        break;
      case SORT_CHAR_VALUE:
        compareResult = getCharValStr().compareTo(arg0.getCharValStr());
        break;
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }

  /**
   * {@inheritDoc} returns true if the attribute is not deleted & if the user can modify the attribute values
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {

    if ((selectedObj instanceof ArrayList) && (((ArrayList<?>) selectedObj).size() == 1)) {
      List<?> selectedObjList = (ArrayList<?>) selectedObj;
      if (selectedObjList.get(0) instanceof Attribute) {
        Attribute attr = (Attribute) selectedObjList.get(0);
        if (!attr.isDeleted() && (attr.canModifyValues() || getDataCache().getCurrentUser().hasApicWriteAccess())) {
          return true;
        }
        return false;
      }

    }
    return false;
  }

  /**
   * {@inheritDoc} return object details of the attribute value
   */
  @Override
  public Map<String, String> getObjectDetails() {
    final Map<String, String> objDetails = new HashMap<String, String>();

    objDetails.put(FLD_DEL_FLAG, String.valueOf(isDeleted()));
    objDetails.put(FLD_NUM_VALUE, String.valueOf(getNumberValue()));
    objDetails.put(FLD_TXTVAL_ENG, getTextValueEng());
    objDetails.put(FLD_TXTVAL_GER, getTextValueGer());
    objDetails.put(FLD_VAL_DSC_ENG, getValueDescEng());
    objDetails.put(FLD_VAL_DSC_GER, getValueDescGer());


    return objDetails;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getValue(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.ATTRIB_VALUE;
  }


  /**
   * Icdm-891 new method to check for deleted and not cleared values
   *
   * @return if the value is invalid
   */
  public boolean isValueInvalid() {
    return isDeleted();
  }


  /**
   * ICDM-1042 Tooltip for the nodes in the pidc tree
   *
   * @return tooltip text
   */
  @Override
  public String getToolTip() {
    return CommonUtils.concatenate("Attribute: ", getAttribute().getName(), "\nDescription: ",
        getAttribute().getDescription(), "\nValue: ", getName(), "\nValue Description: ", getDescription());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return CommonUtils.concatenate("AttributeValue [getID()=", getID(), ", getValue()=", getValue(),
        ", getClearingStatus()=", getClearingStatus(), ", isModifiable()=", isModifiable(), ", getVersion()=",
        getVersion(), ", getDescription()=", getDescription(), ", isDeleted()=", isDeleted(), "]");
  }

  // ICDM-1397
  /**
   * Returns the Attribute value change comment
   *
   * @return Attribute value change comment in String
   */
  public String getChangeComment() {
    String changeComment = getEntityProvider().getDbValue(getID()).getChangeComment();
    return CommonUtils.checkNull(changeComment);
  }

  /**
   * ICDM-2296 Get the predefined attr value Combination
   *
   * @return List<PredefinedAttrValue>
   */
  public SortedSet<PredefinedAttrValue> getPreDefinedAttrValues() {
    return CommonUtils.isNotEmpty(getPreDefinedAttrValueSet()) ? new TreeSet<>(getPreDefinedAttrValueSet()) : null;
  }

  /**
   * ICDM-2296 Get the predefined attr value Combination
   *
   * @return List<PredefinedAttrValue>
   */
  public Set<PredefinedAttrValue> getPreDefinedAttrValueSet() {
    final List<TPredefinedAttrValue> listAttrValues = getEntityProvider().getDbValue(getID()).gettGroupAttrValue();
    Set<PredefinedAttrValue> predfndAttrValuesSet = new HashSet<PredefinedAttrValue>();
    if (CommonUtils.isNotEmpty(listAttrValues)) {
      for (TPredefinedAttrValue tGroupAttrValue : listAttrValues) {
        if (getDataCache().getPredAttrValMap().get(tGroupAttrValue.getPreAttrValId()) == null) {
          new PredefinedAttrValue(getDataCache().getDataProvider(), tGroupAttrValue.getPreAttrValId());
        }


        predfndAttrValuesSet.add(getDataCache().getPredAttrValMap().get(tGroupAttrValue.getPreAttrValId()));
      }
      return predfndAttrValuesSet;
    }
    return null;
  }

  /**
   * ICDM-2296 Get the predefined attr value valididty
   *
   * @return PredefinedAttrValuesValidityModel
   */
  public PredefinedAttrValuesValidityModel getValidity() {

    final List<TPredefinedValidity> listAttrValidity = getEntityProvider().getDbValue(getID()).gettGroupAttrValidity();

    Map<PredefinedAttrValuesValidity, AttributeValue> validityVals = new ConcurrentHashMap<>();
    Attribute validityAttr = null;
    if (CommonUtils.isNotEmpty(listAttrValidity)) {
      for (TPredefinedValidity tGroupAttrValidity : listAttrValidity) {
        if (getDataCache().getPredAttrValValidityMap().get(tGroupAttrValidity.getValidityId()) == null) {
          new PredefinedAttrValuesValidity(getDataCache().getDataProvider(), tGroupAttrValidity.getValidityId());
        }
        PredefinedAttrValuesValidity predefinedAttrValidity =
            getDataCache().getPredAttrValValidityMap().get(tGroupAttrValidity.getValidityId());
        validityVals.put(predefinedAttrValidity, predefinedAttrValidity.getValidityAttributeValue());
        validityAttr = predefinedAttrValidity.getValidityAttribute();
      }
      // one attribute with one/multiple values
      return new PredefinedAttrValuesValidityModel(validityAttr, validityVals);

    }
    return null;
  }

  /**
   * @return SortedSet of links
   */
  public SortedSet<Link> getLinks() {

    getLogger().debug("fetching links for the attribute value :" + getName());
    SortedSet<Link> linkSet = new TreeSet<Link>();
    final List<TLink> listDbLinks = getEntityProvider().getDbValue(getID()).gettLinks();
    if (CommonUtils.isNotEmpty(listDbLinks)) {
      for (TLink tLink : listDbLinks) {
        linkSet.add(getDataCache().getLink(tLink.getLinkId()));
      }
    }
    getLogger().debug("fetching links for the attribute value :" + getName() + "completed");
    return linkSet;
  }

  /**
   * @return the isValidValue
   */
  public boolean isValidValue() {
    return this.isValidValue;
  }


  /**
   * @param isValidValue the isValidValue to set
   */
  public void setValidValue(final boolean isValidValue) {
    this.isValidValue = isValidValue;
  }


}