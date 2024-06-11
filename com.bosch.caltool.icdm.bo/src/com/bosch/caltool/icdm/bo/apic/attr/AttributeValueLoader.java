/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.FeatureLoader;
import com.bosch.caltool.icdm.bo.cdr.FeatureValueLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TMandatoryAttr;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.database.entity.apic.TSsdValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;


/**
 * @author bne4cob
 */
public class AttributeValueLoader extends AbstractBusinessObject<AttributeValue, TabvAttrValue> {

  /**
   * @param serviceData Service Data
   */
  public AttributeValueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ATTRIB_VALUE, TabvAttrValue.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AttributeValue createDataObject(final TabvAttrValue entity) throws DataException {
    AttributeValue data = new AttributeValue();
    setCommonFields(data, entity);

    Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(entity.getTabvAttribute().getAttrId());
    data.setAttributeId(attr.getId());

    AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
    data.setValueType(attr.getValueType());
    String dispVal = AttrValueTextResolver.getStringValue(entity, getServiceData().getLanguageObj(), false);
    data.setNameRaw(dispVal);
    // Add unit to number type display value
    if (valueType == AttributeValueType.NUMBER) {
      String unit = attr.getUnit();
      data.setUnit(unit);
      // ICDM-1248
      if (!CommonUtils.isEqual(CommonUtils.checkNull(unit).trim(), ApicConstants.ATTRVAL_EMPTY_UNIT)) {
        dispVal += " " + unit;
      }
    }
    data.setName(dispVal);

    setDataValue(entity, data, valueType);
    // Added as Replacement to method AttributeValue.getNumberValue(boolean)
    if (entity.getNumvalue() != null) {
      data.setNumValue(entity.getNumvalue());
    }
    data.setDescription(getLangSpecTxt(entity.getValueDescEng(), entity.getValueDescGer()));
    data.setDescriptionEng(entity.getValueDescEng());
    data.setDescriptionGer(entity.getValueDescGer());

    data.setChangeComment(entity.getChangeComment());

    data.setClearingStatus(entity.getClearingStatus());
    data.setDeleted(ApicConstants.CODE_YES.equals(entity.getDeletedFlag()));

    if (entity.gettCharacteristicValue() != null) {
      data.setCharacteristicValueId(entity.gettCharacteristicValue().getCharValId());
      data.setCharStr(getLangSpecTxt(entity.gettCharacteristicValue().getValNameEng(),
          entity.gettCharacteristicValue().getValNameGer()));
    }

    return data;
  }

  /**
   * @param entity
   * @param data
   * @param valueType
   * @throws DataException
   */
  private void setDataValue(final TabvAttrValue entity, final AttributeValue data, final AttributeValueType valueType)
      throws DataException {
    switch (valueType) {
      case BOOLEAN:
        data.setBoolvalue(entity.getBoolvalue());
        break;
      case DATE:
        data.setDateValue(timestamp2String(entity.getDatevalue()));
        break;
      case NUMBER:
        data.setNumValue(entity.getNumvalue());
        break;
      case TEXT:
        data.setTextValueEng(entity.getTextvalueEng());
        data.setTextValueGer(entity.getTextvalueGer());
        break;
      case HYPERLINK:
        data.setTextValueEng(entity.getTextvalueEng());
        break;
      case ICDM_USER:
        setUserIdVal(entity, data);
        break;
      default:
        data.setOtherValue(entity.getOthervalue());
        break;
    }
  }

  /**
   * @param entity tabVAttrVal
   * @param data attributeValue
   * @throws DataException
   */
  private void setUserIdVal(final TabvAttrValue entity, final AttributeValue data) throws DataException {
    data.setTextValueEng(entity.getTextvalueEng());
    if (entity.getTabvApicUser() != null) {
      data.setUserId(entity.getTabvApicUser().getUserId());
      data.setOtherValue(new UserLoader(getServiceData()).getUsernameById(data.getUserId()));
    }
  }

  /**
   * @return Hidden value Ids for quotation attribute
   */
  public Set<Long> getQuotationHiddenValueIds() {
    CommonParamLoader cpLoader = new CommonParamLoader(getServiceData());
    String valueIds = cpLoader.getValue(CommonParamKey.QUOT_VALUE_HIDDEN_STATUS);

    Set<Long> valueIdSet = new HashSet<>();
    if (null != valueIds) {
      String[] valueIdsArray = valueIds.split(",");
      // ICDM-2513, to pass comma seperated value-id as collection, the set has been used
      for (String attrValueId : valueIdsArray) {
        // trimming function a
        valueIdSet.add(Long.valueOf(attrValueId.trim()));
      }
    }
    return valueIdSet;
  }

  /**
   * get all values for the given attributes
   *
   * @param attrIdSet set of attributes
   * @return Map. Key - attribute Id; value - Map.(key - attrvalueid, value - AttributeValue)
   * @throws DataException error while retrieving data
   */
  public Map<Long, Map<Long, AttributeValue>> getValuesByAttribute(final Set<Long> attrIdSet) throws DataException {
    Map<Long, AttributeValue> attrValMap;
    Map<Long, Map<Long, AttributeValue>> retMap = new HashMap<>();

    for (Long attrId : attrIdSet) {
      attrValMap = getAttrValues(attrId);
      retMap.put(attrId, attrValMap);
    }
    return retMap;
  }


  /**
   * @param attrId attrId
   * @return
   * @throws InvalidInputException
   * @throws DataException
   */
  public Map<Long, AttributeValue> getAttrValues(final Long attrId) throws DataException {

    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    Map<Long, AttributeValue> attrValMap;
    AttributeValue attributeValue;
    attrLdr.validateId(attrId);

    TabvAttribute dbAttr = attrLdr.getEntityObject(attrId);
    attrValMap = new HashMap<>();

    for (TabvAttrValue entity : dbAttr.getTabvAttrValues()) {
      attributeValue = createDataObject(entity);
      attrValMap.put(entity.getValueId(), attributeValue);
    }
    return attrValMap;
  }

  /**
   * @param attrId attribute id
   * @return Map key-attribute id Value-Map key-attribute value id ,value-set of dependencies
   * @throws DataException DataException
   */
  public Map<Long, Map<Long, Set<AttrNValueDependency>>> getAttrDepMapForAllAttr(final List<Long> attrId)
      throws DataException {
    Map<Long, Map<Long, Set<AttrNValueDependency>>> depMap = new HashMap<>();
    for (Long longAttrId : attrId) {
      AttributeLoader attrLdr = new AttributeLoader(getServiceData());
      AttributeValue attributeValue;
      attrLdr.validateId(longAttrId);

      TabvAttribute dbAttr = attrLdr.getEntityObject(longAttrId);

      AttrNValueDependencyLoader depLoader = new AttrNValueDependencyLoader(getServiceData());
      Map<Long, Set<AttrNValueDependency>> retMap = new HashMap<>();
      for (TabvAttrValue entity : dbAttr.getTabvAttrValues()) {
        attributeValue = new AttributeValueLoader(getServiceData()).getDataObjectByID(entity.getValueId());
        Set<AttrNValueDependency> valueDependencies = depLoader.getValueDependencies(attributeValue.getId(), false);
        if (!valueDependencies.isEmpty()) {

          retMap.put(attributeValue.getId(), valueDependencies);
        }
      }
      depMap.put(longAttrId, retMap);
    }
    return depMap;
  }

  /**
   * Method to get groupedAttrValidity for given attribute value
   *
   * @param attrValue
   * @return Map<Long, PredefinedValidity>
   * @throws DataException
   */
  public Map<Long, PredefinedValidity> getPredefinedValidityForAttrValue(final Long attrValueId) throws DataException {

    Map<Long, PredefinedValidity> predefinedValMap = new HashMap<>();

    TabvAttrValue tabvAttrValue = getEntityObject(attrValueId);

    List<TPredefinedValidity> grpAttrValList = tabvAttrValue.gettGroupAttrValidity();

    PredefinedValidityLoader predefValLoader = new PredefinedValidityLoader(getServiceData());

    for (TPredefinedValidity validity : grpAttrValList) {
      PredefinedValidity predefValidity = predefValLoader.createDataObject(validity);

      if (predefValidity != null) {
        predefinedValMap.put(predefValidity.getId(), predefValidity);
      }
    }

    return predefinedValMap;


  }


  /**
   * @param attrId attribute id
   * @return Attribute values - used
   */
  public AttributeValue createAttrValUsedObject(final Long attrId) {
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setAttributeId(attrId);
    attributeValue.setName(ApicConstants.USED);
    attributeValue.setId(ApicConstants.ATTR_VAL_USED_VALUE_ID);
    attributeValue.setDescription(ApicConstants.USED);
    return attributeValue;
  }

  /**
   * @param attrId attribute id
   * @return Attribute values - Not used
   */
  public AttributeValue createAttrValNotUsedObject(final Long attrId) {
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setAttributeId(attrId);
    attributeValue.setName(ApicConstants.NOT_USED);
    attributeValue.setId(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID);
    attributeValue.setDescription(ApicConstants.NOT_USED);
    return attributeValue;
  }

  /**
   * Gets the attribute value.
   *
   * @param pidcVersId the pidc vers id
   * @param attrId the attr id
   * @return the attribute value
   * @throws DataException the data exception
   */
  public AttributeValue getAttributeValue(final Long pidcVersId, final Long attrId) throws DataException {

    AttributeValue createDataObject = null;

    TypedQuery<TabvProjectAttr> typeQuery =
        getEntMgr().createNamedQuery(TabvProjectAttr.GET_PROJATTR_FOR_ATTRIBUTEID, TabvProjectAttr.class);
    typeQuery.setParameter("pidcVersId", pidcVersId);
    typeQuery.setParameter("attrId", attrId);
    List<TabvProjectAttr> resultList = typeQuery.getResultList();
    if (CommonUtils.isNotEmpty(resultList)) {
      TabvProjectAttr dbObj = resultList.get(0);
      if (dbObj.getTabvAttrValue() != null) {
        createDataObject = createDataObject(dbObj.getTabvAttrValue());
      }
    }
    return createDataObject;
  }

  /**
   * @param projNameAttrLvl
   * @return
   * @throws DataException
   */
  public Set<String> getExistingPidcNames(final int projNameAttrLvl) throws DataException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute projNameAttr = attrLoader.getAllLevelAttributes().get(Long.valueOf(projNameAttrLvl));
    return getPidcNames(projNameAttr.getId());
  }

  /**
   * @param id
   * @throws InvalidInputException
   */
  private Set<String> getPidcNames(final Long attrId) throws InvalidInputException {
    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    attrLdr.validateId(attrId);
    TabvAttribute dbAttr = attrLdr.getEntityObject(attrId);
    Set<String> pidcNameSet = new HashSet<>();
    for (TabvAttrValue entity : dbAttr.getTabvAttrValues()) {
      pidcNameSet.add(entity.getTextvalueEng());
    }
    return pidcNameSet;
  }

  /**
   * @param pidcVersionId Pidc Version ID
   * @param a2lFileId A2l File Id
   * @return sdom pver attribute value Id used
   * @throws DataException data retrieval error
   */
  public long getSdomPverAttrValueId(final Long pidcVersionId, final Long a2lFileId) throws DataException {
    final Long spAttrId =
        new AttributeLoader(getServiceData()).getLevelAttrId(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR));

    TypedQuery<TabvAttrValue> query =
        getEntMgr().createNamedQuery(TabvAttrValue.NQ_GET_SDOM_PVER_VAL_BY_PROJ_N_A2L_ID, TabvAttrValue.class)
            .setParameter("pidcVersionId", pidcVersionId).setParameter("a2lFileId", a2lFileId)
            .setParameter("sdomPverAttrId", spAttrId);
    return query.getSingleResult().getValueId();
  }

  /**
   * @param attrId
   * @return
   */
  public SortedSet<AttributeValue> getFeatureMappedAttrValues(final Long attrId) throws DataException {
    FeatureLoader loader = new FeatureLoader(getServiceData());

    Map<Long, FeatureValue> feaValMap = new HashMap<>();
    SortedSet<AttributeValue> attrValRet = new TreeSet<>();
    Feature feature = loader.getFeature(attrId);
    assertValidFeature(feature, attrId);

    FeatureValueLoader feaValLoder = new FeatureValueLoader(getServiceData());

    List<TSsdValue> ssdValues = new FeatureLoader(getServiceData()).getEntityObject(feature.getId()).getTSsdValues();
    setFeaValMap(feaValMap, feaValLoder, ssdValues);


    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    Set<Long> attrIdSet = new HashSet<>();
    attrIdSet.add(attrId);
    Map<Long, AttributeValue> attrValues = attrValLoader.getAttrValues(attrId);

    for (AttributeValue attrVal : attrValues.values()) {
      if (CommonUtils.isNotNull(feaValMap.get(attrVal.getId())) && !attrVal.isDeleted()) {
        attrValRet.add(attrVal);
      }
    }

    // Add AttrVal models of used and not used
    if (CommonUtils.isNotNull(feaValMap.get(ApicConstants.ATTR_VAL_USED_VALUE_ID))) {
      attrValRet.add(attrValLoader.createAttrValUsedObject(attrId));
    }
    if (CommonUtils.isNotNull(feaValMap.get(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID))) {
      attrValRet.add(attrValLoader.createAttrValNotUsedObject(attrId));
    }
    return attrValRet;

  }

  /**
   * @param feature
   * @param attrId
   */
  private void assertValidFeature(final Feature feature, final Long attrId) throws DataException {
    if (feature == null) {
      Attribute attr = new AttributeLoader(getServiceData()).getDataObjectByID(attrId);
      throw new DataNotFoundException("FEAVAL.FEATURE_VAL_MISSING", attr.getName());
    }

  }

  /**
   * @param feaValMap
   * @param feaValLoder
   * @param ssdValues
   * @throws DataException
   */
  private void setFeaValMap(final Map<Long, FeatureValue> feaValMap, final FeatureValueLoader feaValLoder,
      final List<TSsdValue> ssdValues)
      throws DataException {
    for (TSsdValue dbFeatureVal : ssdValues) {
      if (null != dbFeatureVal.getTabvAttrValue()) {
        feaValMap.put(dbFeatureVal.getTabvAttrValue().getValueId(),
            feaValLoder.getDataObjectByID(dbFeatureVal.getValueId()));
      }
      else if (null != dbFeatureVal.getUsedFlag()) {
        if (ApicConstants.CODE_YES.equals(dbFeatureVal.getUsedFlag())) {
          feaValMap.put(ApicConstants.ATTR_VAL_USED_VALUE_ID, feaValLoder.getDataObjectByID(dbFeatureVal.getValueId()));
        }
        else if (ApicConstants.CODE_NO.equals(dbFeatureVal.getUsedFlag())) {
          feaValMap.put(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID,
              feaValLoder.getDataObjectByID(dbFeatureVal.getValueId()));
        }
        else if (dbFeatureVal.getUsedFlag().equals("?")) {
          feaValMap.put(ApicConstants.ATTR_VAL_NOT_SET_VALUE_ID,
              feaValLoder.getDataObjectByID(dbFeatureVal.getValueId()));
        }
      }
    }
  }


  /**
   * @param attributeValueId
   * @return list of mandatory attributes for attribute value id
   */
  public List<TMandatoryAttr> getMandatoryAttributesForAttributeValue(final Long attributeValueId) {
    return getEntityObject(attributeValueId).getTMandatoryAttrs();
  }

  /**
   * @param pidcVersId
   * @return
   * @throws DataException
   */
  public boolean isQnaireConfigValUsedInRvw(final Long pidcVersId) {
    List<TRvwQnaireResponse> qnaireRespList =
        new RvwQnaireResponseLoader(getServiceData()).getQnaireRespList(pidcVersId);
    return CommonUtils.isNotEmpty(qnaireRespList);
  }

  /**
   * Query to get Sdom pver names
   *
   * @param pidcId Long id
   * @param sdomPverAttrId Long id
   * @return SortedSet<String> PVER Name
   */
  public Set<String> getPVERNameSet(final Long pidcId, final Long sdomPverAttrId) {
    Set<String> pverNameSet = new HashSet<>();

    final Query query = getEntMgr().createNamedQuery(TabvAttrValue.NNQ_FIND_ALL_SDOM_PVER_NAMES_BY_PIDC_ID);
    query.setParameter(1, pidcId);
    query.setParameter(2, sdomPverAttrId);
    query.setParameter(3, pidcId);
    query.setParameter(4, sdomPverAttrId);

    for (Object objects : query.getResultList()) {
      pverNameSet.add(objects.toString());
    }
    return pverNameSet;
  }


  /**
   * @return Map of tree level attribute values. Key - attribute id, value - map of values, with key as value id
   * @throws IcdmException error while retrieving data
   */
  public Map<Long, Map<Long, AttributeValue>> getAllPidTreeLvlAttrValMap() throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Map<Long, Set<AttributeValue>> attrValMap = new HashMap<>();
    for (Attribute attr : attrLoader.getAllLevelAttributes().values()) {

      if (attr.getLevel() < 1L) {
        continue;
      }

      Collection<AttributeValue> attrValList =
          getValuesByAttribute(new HashSet<>(Arrays.asList(attr.getId()))).get(attr.getId()).values();

      Set<AttributeValue> attrValListSet = new HashSet<>();
      attrValListSet.addAll(attrValList);
      attrValMap.put(attr.getId(), attrValListSet);
    }


    Set<Long> attrIdSet = attrLoader.getAllLevelAttributes().values().stream()
        .filter(a -> a.getLevel().longValue() >= 1L).map(Attribute::getId).collect(Collectors.toSet());

    return getValuesByAttribute(attrIdSet);

  }


  /**
   * @param attributeId Long
   * @param value String
   * @return AttributeValue with given value for given attribute id
   * @throws DataException
   */
  public AttributeValue getValueByName(final Long attributeId, final String value) throws DataException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    TabvAttribute tabvAttribute = attrLoader.getEntityObject(attributeId);
    for (TabvAttrValue tabvAttrValue : tabvAttribute.getTabvAttrValues()) {
      if (CommonUtils.isEqual(tabvAttrValue.getTextvalueEng(), value)) {
        return createDataObject(tabvAttrValue);
      }
    }
    return null;
  }
}

