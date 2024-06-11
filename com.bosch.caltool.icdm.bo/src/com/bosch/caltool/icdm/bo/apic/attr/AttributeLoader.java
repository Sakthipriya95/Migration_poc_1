/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;


/**
 * @author bne4cob
 */
public class AttributeLoader extends AbstractBusinessObject<Attribute, TabvAttribute> {

  /**
   *
   */
  private static final String SESSKEY_ALL_ATTRIBUTES = "ALL_ATTRIBUTES";
  /**
   *
   */
  private static final String SESSKEY_MAX_STRUCT_LEVEL = "MAX_STRUCT_LEVEL";
  /**
   *
   */
  private static final String SESSKEY_LEVEL_ATTRIBUTES = "LEVEL_ATTRIBUTES";

  // TODO move the constant to ApicConstants task : 263995
  /**
   * Minimum level of structure attribute
   */
  public static final long MIN_STRUCT_ATTR_LEVEL = 1L;


  /**
   * @param serviceData Service Data
   */
  public AttributeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ATTRIBUTE, TabvAttribute.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Attribute createDataObject(final TabvAttribute entity) throws DataException {
    Attribute ret = new Attribute();

    setCommonFields(ret, entity);
    ret.setAttrGrpId(entity.getTabvAttrGroup().getGroupId());

    ret.setName(getLangSpecTxt(entity.getAttrNameEng(), entity.getAttrNameGer()));
    ret.setNameEng(entity.getAttrNameEng());
    ret.setNameGer(entity.getAttrNameGer());
    ret.setEadmName(entity.getEadmName());
    ret.setDescription(getLangSpecTxt(entity.getAttrDescEng(), entity.getAttrDescGer()));
    ret.setDescriptionEng(entity.getAttrDescEng());
    ret.setDescriptionGer(entity.getAttrDescGer());

    ret.setValueType(AttributeValueType.getType(entity.getTabvAttrValueType().getValueTypeId()).getDisplayText());

    ret.setValueTypeId(entity.getTabvAttrValueType().getValueTypeId());

    ret.setLevel(entity.getAttrLevel() == null ? null : entity.getAttrLevel().longValue());
    ret.setDeleted(yOrNToBoolean(entity.getDeletedFlag()));
    ret.setUnit(entity.getUnits());
    ret.setChangeComment(entity.getChangeComment());
    ret.setCharacteristicId(entity.gettCharacteristic() == null ? null : entity.gettCharacteristic().getCharId());
    ret.setCharStr(entity.gettCharacteristic() == null ? null
        : getLangSpecTxt(entity.gettCharacteristic().getCharNameEng(), entity.gettCharacteristic().getCharNameGer()));
    ret.setFormat(entity.getFormat());
    ret.setMandatory(yOrNToBoolean(entity.getMandatory()));
    ret.setNormalized(yOrNToBoolean(entity.getNormalizedFlag()));
    ret.setWithPartNumber(yOrNToBoolean(entity.getPartNumberFlag()));
    ret.setWithSpecLink(yOrNToBoolean(entity.getSpecLinkFlag()));
    ret.setExternal(ApicConstants.EXTERNAL.equals(entity.getAttrSecurity()));
    ret.setExternalValue(ApicConstants.EXTERNAL.equals(entity.getValueSecurity()));
    ret.setGroupedAttr(yOrNToBoolean(entity.getGroupFlag()));
    // Checked with No, since the field is optional
    ret.setMoveDown(!(ApicConstants.CODE_NO).equals(entity.getMoveDownYN()));
    ret.setAddValByUserFlag(yOrNToBoolean(entity.getAddValuesByUsersFlag()));
    return ret;
  }

  /**
   * @return
   */
  Set<Long> internalGetAllAttrIds() {
    Set<Long> retSet = new HashSet<>();

    final TypedQuery<TabvAttrSuperGroup> query =
        getEntMgr().createNamedQuery(TabvAttrSuperGroup.NQ_GET_ALL_SUPER_GROUPS, TabvAttrSuperGroup.class);

    for (TabvAttrSuperGroup dbSuperGrp : query.getResultList()) {
      for (TabvAttrGroup dbGrp : dbSuperGrp.getTabvAttrGroups()) {
        for (TabvAttribute entity : dbGrp.getTabvAttributes()) {
          retSet.add(entity.getAttrId());
        }
      }
    }

    getLogger().debug("Attributes retrieved from DB. Count = {}", retSet.size());

    return retSet;
  }

  Set<Long> getAllAttributeIds() {
    Object data = getServiceData().retrieveData(getClass(), SESSKEY_ALL_ATTRIBUTES);
    if (data == null) {
      data = internalGetAllAttrIds();
      getServiceData().storeData(getClass(), SESSKEY_ALL_ATTRIBUTES, data);
    }

    return (Set<Long>) data;
  }

  /**
   * Get all attributes
   *
   * @return all attributes. key - attribute ID, value - attribute
   * @throws DataException if attribute could not be found
   */
  public Map<Long, Attribute> getAllAttributes() throws DataException {
    return getAllAttributes(false);
  }

  /**
   * @param includeDeleted if true, deleted attributes are also included
   * @return key - attribute ID, value - attribute
   * @throws DataException if attribute could not be found
   */
  public Map<Long, Attribute> getAllAttributes(final boolean includeDeleted) throws DataException {
    Map<Long, Attribute> retMap = new HashMap<>();

    for (Long attrId : getAllAttributeIds()) {
      TabvAttribute entity = getEntityObject(attrId);
      if (includeDeleted || CommonUtilConstants.CODE_NO.equals(entity.getDeletedFlag())) {
        retMap.put(attrId, createDataObject(entity));
      }
    }
    return retMap;
  }

  /**
   * @return level attributes. key - level, value - attribute
   * @throws DataException if attribute could not be found
   */
  public Map<Long, Attribute> getAllLevelAttributes() throws DataException {
    Map<Long, Attribute> retMap = new HashMap<>();
    for (Entry<Long, Long> entry : getAllLevelAttributeIds().entrySet()) {
      retMap.put(entry.getKey(), getDataObjectByID(entry.getValue()));
    }
    return retMap;
  }

  /**
   * @return level attributes. key - level, value - attribute ID
   * @throws DataException any error while creating data object
   */
  public Map<Long, Long> getAllLevelAttributeIds() throws DataException {

    Object data = getServiceData().retrieveData(getClass(), SESSKEY_LEVEL_ATTRIBUTES);
    if (data == null) {
      Map<Long, Long> levelAttrIdMap = new HashMap<>();
      for (Attribute attr : getAllAttributes().values()) {
        if ((attr.getLevel() != null) && (attr.getLevel() != 0L)) {
          levelAttrIdMap.put(attr.getLevel(), attr.getId());
        }
      }
      data = levelAttrIdMap;
      getServiceData().storeData(getClass(), SESSKEY_LEVEL_ATTRIBUTES, data);
    }


    return (Map<Long, Long>) data;
  }

  /**
   * @param level level
   * @return attribute ID with the given level
   * @throws DataException error
   */
  public Long getLevelAttrId(final Long level) throws DataException {
    return getAllLevelAttributeIds().get(level);
  }

  /**
   * @return maximum structure attribute level
   * @throws DataException any error while creating data object
   */
  public long getMaxStructAttrLevel() throws DataException {

    Object data = getServiceData().retrieveData(getClass(), SESSKEY_MAX_STRUCT_LEVEL);
    if (data == null) {
      long maxStructAttrLevel = AttributeLoader.MIN_STRUCT_ATTR_LEVEL - 1;
      for (long level : getAllLevelAttributeIds().keySet()) {
        if (level > maxStructAttrLevel) {
          maxStructAttrLevel = level;
        }
      }
      data = maxStructAttrLevel;
      getServiceData().storeData(getClass(), SESSKEY_MAX_STRUCT_LEVEL, data);
    }

    return (long) data;

  }

  /**
   * @return Set of attribute ids which has uncleared values
   * @throws DataException any error while creating data object
   */
  public List<Long> getAllAttrsWithUnClearedVals() throws DataException {

    TypedQuery<Long> tQuery = getEntMgr().createNamedQuery(TabvAttribute.GET_UNCLEARED, Long.class);
    return tQuery.getResultList();
  }

  /**
   * @return Attribute Export Model - contains AttrrGroupModel, Map<Long, Map<Long, AttributeValue>>
   * @throws DataException
   */
  public AttrExportModel getAtrrExportModel() throws DataException {

    AttrExportModel attrExportModel = new AttrExportModel();
    // Get the attributte group model
    AttrSuperGroupLoader attrGrpLoader = new AttrSuperGroupLoader(getServiceData());
    AttrGroupModel ret = attrGrpLoader.getAttrGroupModel(true);

    attrExportModel.setAttrGroup(ret);

    // Get all existing attributes
    AttributeLoader attrloader = new AttributeLoader(getServiceData());
    AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());

    // Get all attributValue objects for AttributeIds
    Map<Long, Map<Long, AttributeValue>> attrValMap =
        attributeValueLoader.getValuesByAttribute(attrloader.getAllAttributeIds());

    attrExportModel.setAllAttrValuesMap(attrValMap);

    return attrExportModel;
  }

  /**
   * @param allFeatures
   */
  public SortedSet<Attribute> getMappedAttr(final ConcurrentMap<Long, Feature> allFeatures) throws DataException {
    SortedSet<Attribute> attrSet = new TreeSet<>();
    Map<Long, Attribute> getAllAttr = getAllAttributes();
    for (Feature feature : allFeatures.values()) {
      Attribute attribute = getAllAttr.get(feature.getAttrId());
      if (attribute != null) {
        attrSet.add(attribute);
      }
    }
    return attrSet;
  }

  /**
   * @return Icdm questionnaire config attribute
   * @throws IcdmException exception in retrieving attribute
   */
  public Attribute getQnaireConfigAttr() throws IcdmException {
    return getDataObjectByID(
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR)));
  }


  /**
   * Get all Quotation relevant Attributes mapped to usecase for the given MCR_ID - Open API service
   *
   * @return Map of Quotation relevant Attributes - key - Attr id, value -Attribute model
   * @throws DataException error while retrieving data
   */
  public Map<Long, Attribute> getQuotRelAttrByMcrId(final String mcrId) throws DataException {

    Map<Long, Attribute> attributesMap = new ConcurrentHashMap<>();

    TypedQuery<TabvAttribute> tQuery = getEntMgr()
        .createNamedQuery(TabvAttribute.GET_QUO_REL_ATTR_BY_MCR_IDS, TabvAttribute.class).setParameter(1, mcrId);

    List<TabvAttribute> dbObj = tQuery.getResultList();
    for (TabvAttribute entity : dbObj) {
      attributesMap.put(entity.getAttrId(), createDataObject(entity));
    }
    return attributesMap;
  }

}
