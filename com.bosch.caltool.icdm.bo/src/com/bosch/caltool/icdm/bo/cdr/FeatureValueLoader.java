/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TSsdFeature;
import com.bosch.caltool.icdm.database.entity.apic.TSsdValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;

/**
 * @author dja7cob
 */
public class FeatureValueLoader extends AbstractBusinessObject<FeatureValue, TSsdValue> {


  /**
   *
   */
  private static final String ATTR_VAL_NOT_SET = "?";

  /**
   * @param serviceData Service Data
   */
  public FeatureValueLoader(final ServiceData serviceData) {
    super(serviceData, "SSD Feature Value", TSsdValue.class);
  }

  /**
   * Get the SSD Feature value for the given attribute value
   *
   * @param featureId Feature ID
   * @param attrValue Attribute value input
   * @return Feature value object
   * @throws DataException error while creating object
   */
  public FeatureValue getFeatureValue(final Long featureId, final AttributeValue attrValue) throws DataException {
    List<TSsdValue> ssdValues = new FeatureLoader(getServiceData()).getEntityObject(featureId).getTSsdValues();

    for (TSsdValue dbFeatureVal : ssdValues) {

      if (isDontCareFeatureValue(attrValue, dbFeatureVal) || isValuePresent(attrValue, dbFeatureVal) ||
          isValueNotSet(attrValue, dbFeatureVal) || isUsedFlagValue(attrValue, dbFeatureVal) ||
          isBoolValueNFeaValUsedFlag(attrValue, dbFeatureVal)) {

        return createDataObject(dbFeatureVal);
      }
    }

    return null;

  }

  /**
   * Get the feature value mainly for dummy attribute value IDs. Use {@link #getFeatureValue(Long, AttributeValue)} if
   * attribute value is avaialble Don't care attribute is not handled in this method TODO - this needs to removed.
   * Permanent fix - modify notused/used/notDef values from -1,-2, etc to different value
   *
   * @param featureId Feature ID
   * @param valueId iCDM Attribute Value ID input
   * @return Feature value object
   * @throws DataException error while creating object
   */
  public FeatureValue getFeatureValueWithoutDontCare(final Long featureId, final Long valueId) throws DataException {
    List<TSsdValue> ssdValues = new FeatureLoader(getServiceData()).getEntityObject(featureId).getTSsdValues();
    for (TSsdValue dbFeatureVal : ssdValues) {
      if (isTabvAttrValId(valueId, dbFeatureVal) || isUsedFlagVal(valueId, dbFeatureVal)) {
        return createDataObject(dbFeatureVal);
      }
    }
    return null;
  }


  /**
   * Get the feature value mainly for dummy attribute value IDs. Use {@link #getFeatureValue(Long, AttributeValue)} if
   * attribute value is avaialble
   *
   * @param featureId Feature ID
   * @param valueId iCDM Attribute Value ID input
   * @return Feature value object
   * @throws DataException error while creating object
   */
  public FeatureValue getFeatureValue(final Long featureId, final Long valueId) throws DataException {
    List<TSsdValue> ssdValues = new FeatureLoader(getServiceData()).getEntityObject(featureId).getTSsdValues();
    for (TSsdValue dbFeatureVal : ssdValues) {
      if (isTabvAttrValId(valueId, dbFeatureVal) || isDontCareAttrValueText(valueId, dbFeatureVal) ||
          isUsedFlagVal(valueId, dbFeatureVal)) {

        return createDataObject(dbFeatureVal);
      }
    }

    return null;
  }

  /**
   * @param valueId
   * @param dbFeatureVal
   * @throws DataException
   */
  private boolean isUsedFlagVal(final Long valueId, final TSsdValue dbFeatureVal) {
    return isUsedFlagY(valueId, dbFeatureVal) || isUsedFlagNo(valueId, dbFeatureVal) ||
        isUsedFlagNotSet(valueId, dbFeatureVal);
  }

  /**
   * @param valueId
   * @param dbFeatureVal
   * @return
   */
  private boolean isUsedFlagNotSet(final Long valueId, final TSsdValue dbFeatureVal) {
    return CommonUtils.isEqual(valueId, ApicConstants.ATTR_VAL_NOT_SET_VALUE_ID) &&
        CommonUtils.isEqual(dbFeatureVal.getUsedFlag(), ATTR_VAL_NOT_SET);
  }

  /**
   * @param valueId
   * @param dbFeatureVal
   * @return
   */
  private boolean isUsedFlagNo(final Long valueId, final TSsdValue dbFeatureVal) {
    return CommonUtils.isEqual(valueId, ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID) &&
        (ApicConstants.CODE_NO.equals(dbFeatureVal.getUsedFlag()));
  }

  /**
   * @param valueId
   * @param dbFeatureVal
   * @return
   */
  private boolean isUsedFlagY(final Long valueId, final TSsdValue dbFeatureVal) {
    return CommonUtils.isEqual(valueId, ApicConstants.ATTR_VAL_USED_VALUE_ID) &&
        ApicConstants.CODE_YES.equals(dbFeatureVal.getUsedFlag());
  }

  /**
   * @param valueId
   * @param dbFeatureVal
   * @return
   */
  private boolean isDontCareAttrValueText(final Long valueId, final TSsdValue dbFeatureVal) {
    return CommonUtils.isEqual(valueId, dbFeatureVal.getValueId()) &&
        CommonUtils.isEqual(dbFeatureVal.getValueText(), CDRConstants.DONT_CARE_ATTR_VALUE_TEXT);
  }

  /**
   * @param valueId
   * @param dbFeatureVal
   * @return
   */
  private boolean isTabvAttrValId(final Long valueId, final TSsdValue dbFeatureVal) {
    return (null != dbFeatureVal.getTabvAttrValue()) &&
        CommonUtils.isEqual(valueId, dbFeatureVal.getTabvAttrValue().getValueId());
  }

  /**
   * // dont care feature value
   *
   * @param value
   * @param dbFeatureVal
   * @return
   */
  private boolean isDontCareFeatureValue(final AttributeValue value, final TSsdValue dbFeatureVal) {
    return (null != value) && (null == dbFeatureVal.getTabvAttrValue()) &&
        (CDRConstants.DONT_CARE_ATTR_VALUE_TEXT.equals(dbFeatureVal.getValueText())) &&
        CommonUtils.isEqual(value.getName(), dbFeatureVal.getValueText());
  }

  /**
   * @param value
   * @param dbFeatureVal
   * @return
   */
  private boolean isValuePresent(final AttributeValue value, final TSsdValue dbFeatureVal) {
    return (null != value) && (null != dbFeatureVal.getTabvAttrValue()) &&
        CommonUtils.isEqual(value.getId(), dbFeatureVal.getTabvAttrValue().getValueId());
  }

  /**
   * @param value
   * @param dbFeatureVal
   * @return
   */
  private boolean isValueNotSet(final AttributeValue value, final TSsdValue dbFeatureVal) {
    return ATTR_VAL_NOT_SET.equals(dbFeatureVal.getUsedFlag()) && (value == null);
  }

  /**
   * @param value
   * @return
   */
  private boolean isUsedFlagValue(final AttributeValue value, final TSsdValue dbFeatureVal) {
    return (value != null) &&
        (ApicConstants.USED.equals(value.getName()) || ApicConstants.NOT_USED.equals(value.getName())) &&
        getYNCodeForUsedFlagValue(value.getName()).equals(dbFeatureVal.getUsedFlag());
  }

  /**
   * @param attrValName can be <USED> or <Not-USED>
   * @return "Y" or "N"
   */
  private String getYNCodeForUsedFlagValue(final String attrValName) {
    return ApicConstants.USED.equals(attrValName) ? ApicConstants.CODE_YES : ApicConstants.CODE_NO;
  }

  private boolean isBoolValueNFeaValUsedFlag(final AttributeValue value, final TSsdValue dbFeatureVal) {
    return ((value != null) && (value.getBoolvalue() != null)) && (dbFeatureVal.getUsedFlag() != null) &&
        dbFeatureVal.getUsedFlag().equals(getCodeForBoolValue(value));
  }

  /**
   * @param value
   * @return
   */
  private String getCodeForBoolValue(final AttributeValue value) {
    return "T".equals(value.getBoolvalue()) ? ApicConstants.CODE_YES : ApicConstants.CODE_NO;
  }


  /**
   * @param featureID featureID
   * @return the Feature value list
   * @throws DataException DataException
   */
  public List<FeatureValue> getFeatureValues(final long featureID) throws DataException {
    TSsdFeature tFeature = new FeatureLoader(getServiceData()).getEntityObject(featureID);
    List<TSsdValue> tSsdValues = tFeature.getTSsdValues();

    List<FeatureValue> feaValList = new ArrayList<>();
    for (TSsdValue tSsdValue : tSsdValues) {
      feaValList.add(createDataObject(tSsdValue));
    }

    return feaValList;
  }

  /**
   * @return Map of Attribute and values which are of type Don't Care. key - attribute id, value - attribute value
   *         object
   */
  public Map<Long, AttributeValueDontCare> getDontCareAttrValues() {
    Map<Long, AttributeValueDontCare> dontCareAttrValMap = new HashMap<>();

    TypedQuery<TSsdValue> dontCareValTypedQuery =
        getEntMgr().createNamedQuery(TSsdValue.NQ_GET_DONT_CARE_VALUES, TSsdValue.class);
    dontCareValTypedQuery.setParameter("dontCareValue", CDRConstants.DONT_CARE_ATTR_VALUE_TEXT);

    List<TSsdValue> resultList = dontCareValTypedQuery.getResultList();

    FeatureAttributeAdapterNew featureAdapter = new FeatureAttributeAdapterNew(getServiceData());
    if (CommonUtils.isNotEmpty(resultList)) {
      for (TSsdValue tSsdValue : resultList) {
        AttributeValueDontCare dontCareValue = featureAdapter
            .createDontCareAttrValue(tSsdValue.getTSsdFeature().getTabvAttribute().getAttrId(), tSsdValue.getValueId());
        dontCareAttrValMap.put(tSsdValue.getTSsdFeature().getTabvAttribute().getAttrId(), dontCareValue);
      }
    }

    return dontCareAttrValMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FeatureValue createDataObject(final TSsdValue entity) throws DataException {
    FeatureValue data = new FeatureValue();
    data.setId(entity.getValueId());
    data.setName(entity.getValueText());
    data.setUsedFlag(entity.getUsedFlag());

    if (null != entity.getTabvAttrValue()) {
      AttributeValue attrValue =
          new AttributeValueLoader(getServiceData()).getDataObjectByID(entity.getTabvAttrValue().getValueId());
      data.setAttrValId(attrValue.getId());
      data.setAttrValName(attrValue.getName());
    }

    return data;
  }


}
