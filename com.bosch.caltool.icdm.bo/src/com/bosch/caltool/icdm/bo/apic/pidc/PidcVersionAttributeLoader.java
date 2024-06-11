/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author bne4cob
 */
public class PidcVersionAttributeLoader extends AbstractProjectAttributeLoader<PidcVersionAttribute, TabvProjectAttr> {

  /**
   * @param serviceData Service Data
   */
  public PidcVersionAttributeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PROJ_ATTR, TabvProjectAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcVersionAttribute createDataObject(final TabvProjectAttr entity) throws DataException {

    PidcVersionAttribute data = new PidcVersionAttribute();

    setCommonFields(data, entity);

    data.setPidcVersId(entity.getTPidcVersion().getPidcVersId());

    Attribute attr = (new AttributeLoader(getServiceData())).getDataObjectByID(entity.getTabvAttribute().getAttrId());
    data.setAttrId(attr.getId());
    data.setName(attr.getName());
    data.setDescription(attr.getDescription());
    data.setValueType(attr.getValueType());
    data.setAtChildLevel(CommonUtilConstants.CODE_YES.equals(entity.getIsVariant()));
    data.setCanMoveDown(!(ApicConstants.CODE_NO).equals(entity.getTabvAttribute().getMoveDownYN()));
    data.setAttrHidden(ApicConstants.CODE_YES.equals(entity.getAttrHiddenFlag()));
    data.setFmAttrRemark(entity.getFmAttrRemark());
    boolean detailsHidden =
        data.isAttrHidden() && !isReadable(entity.getTPidcVersion().getTabvProjectidcard().getProjectId());

    if (detailsHidden) {
      data.setValue(ApicConstants.HIDDEN_VALUE);
    }
    else {
      PROJ_ATTR_USED_FLAG usedFlag = PROJ_ATTR_USED_FLAG.getType(entity.getUsed());
      data.setUsedFlag(usedFlag.getDbType());

      if ((usedFlag == PROJ_ATTR_USED_FLAG.YES) && (entity.getTabvAttrValue() != null)) {
        Long valueId = entity.getTabvAttrValue().getValueId();
        AttributeValue attrVal = (new AttributeValueLoader(getServiceData())).getDataObjectByID(valueId);
        data.setValue(attrVal.getName());
        data.setValueId(valueId);
      }

      if (!data.isAtChildLevel()) {
        data.setAdditionalInfoDesc(entity.getDescription());
        data.setPartNumber(entity.getPartNumber());
        data.setSpecLink(entity.getSpecLink());
      }
    }

    if (data.isAtChildLevel()) {
      data.setValue(ApicConstants.VARIANT_ATTR_DISPLAY_NAME);
      data.setUsedFlag(PROJ_ATTR_USED_FLAG.YES.getDbType());
    }

    data.setTransferToVcdm(ApicConstants.CODE_YES.equals(entity.getTrnsfrVcdmFlag()));
    data.setFocusMatrixApplicable(ApicConstants.CODE_YES.equals(entity.getFocusMatrixYn()));

    return data;
  }

  /**
   * @param attr
   * @return
   */
  protected PidcVersionAttribute createDataObject(final Long pidcVersionId, final Attribute attr) {
    PidcVersionAttribute data = new PidcVersionAttribute();

    data.setPidcVersId(pidcVersionId);
    data.setAttrId(attr.getId());
    data.setName(attr.getName());
    data.setUsedFlag(PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType());
    data.setAtChildLevel(false);
    data.setCanMoveDown(attr.isMoveDown());
    data.setValueType(attr.getValueType());
    return data;
  }

  /**
   * @param pidcVersionId
   * @return
   * @throws DataException
   */
  public String getSDOMPverName(final Long pidcVersionId) throws DataException {
    PidcVersionAttribute pidcVersionAttribute = getSDOMPverAttribute(pidcVersionId);
    String pvername = null;
    if (pidcVersionAttribute != null) {
      AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
      AttributeValue attVal = loader.getDataObjectByID(pidcVersionAttribute.getValueId());
      pvername = attVal.getName();
    }
    return pvername;
  }


  /**
   * @param pidcVersionId
   * @return
   */
  public PidcVersionAttribute getSDOMPverAttribute(final Long pidcVersionId) throws DataException {
    for (TabvProjectAttr entity : new PidcVersionLoader(getServiceData()).getEntityObject(pidcVersionId)
        .getTabvProjectAttrs()) {
      TabvAttribute attribute = entity.getTabvAttribute();
      if (attribute.getAttrNameEng().equalsIgnoreCase(ApicConstants.SDOM_PVER_ATTR_NAME)) {
        return createDataObject(entity);
      }
    }
    return null;
  }

  /**
   * Get all defined level of given PIDC Version
   *
   * @param pidcVersionId PIDC Version Id
   * @return Map. Key - Level, value - data object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionAttribute> getStructureAttributes(final Long pidcVersionId) throws DataException {
    return getStructureAttributes(getDefinedAttributes(pidcVersionId, null));
  }

  /**
   * Get all defined level of given PIDC Version
   *
   * @param pidcVersionId PIDC Version Id
   * @return Map. Key - attr id, value - data object
   * @throws DataException if object cannot be created
   */
  public Map<Long, PidcVersionAttribute> getStructureAttrForVersion(final Long pidcVersionId) throws DataException {
    return getStructureAttrForVer(getDefinedAttributes(pidcVersionId, null));
  }


  /**
   * @param definedAttributes
   * @return
   * @throws DataException
   */
  private Map<Long, PidcVersionAttribute> getStructureAttrForVer(final Map<Long, PidcVersionAttribute> projAttrMap)
      throws DataException {

    Map<Long, PidcVersionAttribute> retMap = new HashMap<>();

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    attrLoader.getAllLevelAttributeIds().forEach((level, attrId) -> {
      if (level > 0L) {
        retMap.put(projAttrMap.get(attrId).getAttrId(), projAttrMap.get(attrId));
      }
    });
    return retMap;
  }

  /**
   * Get all structure attributes for pidc version in the model
   *
   * @param model PIDC PidcVersionAttributeModel
   * @return Map. Key - Level, value - data object
   * @throws DataException if object cannot be created
   */
  Map<Long, PidcVersionAttribute> getStructureAttributes(final PidcVersionAttributeModel model) throws DataException {
    return getStructureAttributes(model.getPidcVersAttrMap());
  }

  /**
   * Get all defined level of given PIDC Version
   *
   * @param pidcVersionId PIDC Version Id
   * @return Map. Key - Level, value - data object
   * @throws DataException if object cannot be created
   */
  private Map<Long, PidcVersionAttribute> getStructureAttributes(final Map<Long, PidcVersionAttribute> projAttrMap)
      throws DataException {
    Map<Long, PidcVersionAttribute> retMap = new HashMap<>();

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    attrLoader.getAllLevelAttributeIds().forEach((level, attrId) -> {
      if (level > 0L) {
        retMap.put(level, projAttrMap.get(attrId));
      }
    });

    return retMap;
  }

  /**
   * Get all defined attributes of given PIDC Version
   *
   * @param pidcVersionId pidc Version Id
   * @return Map. Key - attribute ID, value - data object
   * @throws DataException if object cannot be created
   */
  private Map<Long, PidcVersionAttribute> getDefinedAttributes(final Long pidcVersionId,
      final PidcVersionAttributeModel model)
      throws DataException {

    getLogger().debug("Fetching defined attributes of PIDC version {} ...", pidcVersionId);

    Map<Long, PidcVersionAttribute> retMap = new HashMap<>();

    PidcVersionAttribute dataObj;
    TPidcVersion dbVers = (new PidcVersionLoader(getServiceData())).getEntityObject(pidcVersionId);
    for (TabvProjectAttr entity : dbVers.getTabvProjectAttrs()) {
      if (CommonUtilConstants.CODE_YES.equals(entity.getTabvAttribute().getDeletedFlag())) {
        continue;
      }
      dataObj = createDataObject(entity);
      retMap.put(dataObj.getAttrId(), dataObj);


      // Add hidden attribute to model, if passed
      if (dataObj.isAttrHidden() && (model != null)) {
        model.addHiddenAttrId(dataObj.getAttrId());
      }
    }

    getLogger().debug("Pidc Version: {}. Defined attributes count = {}", pidcVersionId, retMap.size());

    return retMap;
  }


  /**
   * Method to fetch the PidcVersionAttribute Map
   *
   * @param pidcVersionId long
   * @return Map<Long, PidcVersionAttribute>
   * @throws DataException
   */
  public Map<Long, PidcVersionAttribute> getPidcVersionAttribute(final long pidcVersionId) throws DataException {
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcLoader pidcLoader = new PidcLoader(getServiceData());
    PidcVersion pidcVersion = pidcVersionLoader.getDataObjectByID(pidcVersionId);
    Pidc pidc = pidcLoader.getDataObjectByID(pidcVersion.getPidcId());

    PidcVersionAttributeModel model = new PidcVersionAttributeModel(pidcVersion, pidc, false);
    loadAttributes(model);
    return model.getPidcVersAttrMap();
  }

  /**
   * Load all valid attributes of given PIDC Version to the model
   *
   * @throws DataException if object cannot be created
   */
  void loadAttributes(final PidcVersionAttributeModel model) throws DataException {

    Long pidcVersionId = model.getPidcVersion().getId();

    Map<Long, PidcVersionAttribute> defAttrMap = getDefinedAttributes(pidcVersionId, model);
    Map<Long, PidcVersionAttribute> pidcVersAttrMap = new HashMap<>(defAttrMap);

    // Add dummy project attributes, for not defined attributes
    for (Entry<Long, Attribute> attrEntry : model.getAllAttrMap().entrySet()) {

      Attribute attr = attrEntry.getValue();
      if (CommonUtils.isEqual(attr.getLevel(), Long.valueOf(ApicConstants.PROJECT_NAME_ATTR)) ||
          CommonUtils.isEqual(attr.getLevel(), Long.valueOf(ApicConstants.VARIANT_CODE_ATTR)) ||
          CommonUtils.isEqual(attr.getLevel(), Long.valueOf(ApicConstants.SUB_VARIANT_CODE_ATTR))) {
        continue;
      }

      if (!defAttrMap.containsKey(attrEntry.getKey())) {
        pidcVersAttrMap.put(attrEntry.getKey(), createDataObject(pidcVersionId, attrEntry.getValue()));
      }
    }

    getLogger().debug("Pidc Version: {}. Remaining attributes added. New count = {}", pidcVersionId,
        pidcVersAttrMap.size());

    removeInvisibleAttributes(model, pidcVersAttrMap);

    getLogger().debug("Pidc Version: {}. Invisible attributes removed. New count = {}", pidcVersionId,
        pidcVersAttrMap.size());

    model.setPidcVersAttrMap(pidcVersAttrMap);

  }


  /**
   * @param pidcVersId
   * @param allAttrMap
   * @param projAttrMap
   * @throws DataException
   */
  private void removeInvisibleAttributes(final PidcVersionAttributeModel model,
      final Map<Long, PidcVersionAttribute> projAttrMap)
      throws DataException {

    Long pidcNameValueId =
        (new PidcLoader(getServiceData())).getEntityObject(model.getPidc().getId()).getTabvAttrValue().getValueId();
    loadInvisibleAttributes(model, projAttrMap, pidcNameValueId, ApicConstants.PROJECT_NAME_ATTR);

    // remove all invisible attributes
    model.getPidcVersInvisibleAttrSet().forEach(projAttrMap::remove);

  }

  /**
   * @param pidcVersId
   * @param allAttrMap
   * @param invisibleAttrSet
   * @param avDepLdr
   * @param projAttrMap
   * @param pidcNameValueId
   * @throws DataException
   */
  private void loadInvisibleAttributes(final PidcVersionAttributeModel model,
      final Map<Long, PidcVersionAttribute> versAttrMap, final Long pidcNameValueId, final int expNameValLevel)
      throws DataException {

    Map<Long, IProjectAttribute> projAttrMap = new HashMap<>(versAttrMap);

    Set<Long> invisibleAttrSet = new HashSet<>();

    Map<Long, Map<Long, AttrNValueDependency>> allAttrDepMap =
        (new AttrNValueDependencyLoader(getServiceData())).getAttributeDependencies(model.getAllAttrMap().keySet());

    for (Entry<Long, Map<Long, AttrNValueDependency>> attrEntry : allAttrDepMap.entrySet()) {
      Long attrID = attrEntry.getKey();
      Map<Long, AttrNValueDependency> curAttrDepMap = attrEntry.getValue();
      if (!isVisible(pidcNameValueId, expNameValLevel, curAttrDepMap, projAttrMap, invisibleAttrSet)) {
        invisibleAttrSet.add(attrID);
      }
    }

    model.setPidcVersInvisibleAttrSet(invisibleAttrSet);

    getLogger().debug("Pidc Version: {}. Invisible attributes count = {}", model.getPidcVersion().getId(),
        invisibleAttrSet.size());

  }

  /**
   * Find APRJ Name of the given PIDC Version, from its attribute definitions
   *
   * @param pidcVersionId PIDC Version ID
   * @return aprj name
   * @throws DataException any error
   */
  public String getAprjName(final Long pidcVersionId) throws DataException {
    String aprjName = null;

    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    Long aprjAttrId = attrLdr.getLevelAttrId(Long.valueOf(ApicConstants.VCDM_APRJ_NAME_ATTR));

    PidcVersionAttribute dataObj = getDefinedAttributes(pidcVersionId, null).get(aprjAttrId);
    if (CommonUtils.isNotNull(dataObj) &&
        CommonUtils.isEqual(dataObj.getUsedFlag(), PROJ_ATTR_USED_FLAG.YES.getDbType()) &&
        CommonUtils.isNotNull(dataObj.getValueId())) {

      aprjName = dataObj.getValue();
    }

    getLogger().debug("PIDC Version: {}, APRJ Name = {}", pidcVersionId, aprjName);

    return aprjName;
  }

  /**
   * Gets the pidc wp type attribute.
   *
   * @param pidcVersId the pidc vers id
   * @return the pidc wp type attribute
   * @throws DataException the data exception
   * @throws NumberFormatException the number format exception
   */
  public AttributeValue getPidcWpTypeAttribute(final Long pidcVersId) throws DataException {
    // WP Type attribute
    return new AttributeValueLoader(getServiceData()).getAttributeValue(pidcVersId,
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_TYPE_ATTR_ID)));
  }

  /**
   * Gets the pidc wp root group attribute.
   *
   * @param pidcVersId the pidc vers id
   * @return the pidc wp type attribute
   * @throws DataException the data exception
   * @throws NumberFormatException the number format exception
   */
  public AttributeValue getPidcWpRootGroupAttribute(final Long pidcVersId) throws DataException {
    // WP Type attribute
    return new AttributeValueLoader(getServiceData()).getAttributeValue(pidcVersId,
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WP_ROOT_GROUP_ATTR_ID)));
  }


  /**
   * Gets the pidc Qnaire config attribute value
   *
   * @param pidcVersId the pidc vers id
   * @return the pidc Qnaire config attribute value
   * @throws DataException the data exception
   * @throws NumberFormatException the number format exception
   */
  public AttributeValue getPidcQnaireConfigAttrVal(final Long pidcVersId) throws DataException {
    // Qnaire Config attribute
    return new AttributeValueLoader(getServiceData()).getAttributeValue(pidcVersId,
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_QNAIRE_CONFIG_ATTR)));
  }

  /**
   * Gets the attribute value.
   *
   * @param pidcVersId the pidc vers id
   * @param attrId the attr id
   * @return the attribute value
   * @throws DataException the data exception
   */
  public boolean isAttributeUsed(final Long pidcVersId, final Long attrId) throws DataException {
    TypedQuery<TabvProjectAttr> typeQuery =
        getEntMgr().createNamedQuery(TabvProjectAttr.GET_PROJATTR_FOR_ATTRIBUTEID, TabvProjectAttr.class);
    typeQuery.setParameter("pidcVersId", pidcVersId);
    typeQuery.setParameter("attrId", attrId);
    TabvProjectAttr dbObj = typeQuery.getSingleResult();
    return createDataObject(dbObj).getUsedFlag().equals(PROJ_ATTR_USED_FLAG.YES.getDbType());
  }

  /**
   * @param pidcVersId
   * @param attrId
   * @return
   * @throws DataException
   */
  public PidcVersionAttribute getPidcVersionAttributeForAttr(final Long pidcVersId, final Long attrId)
      throws DataException {
    TabvProjectAttr dbObj;
    try {
      TypedQuery<TabvProjectAttr> typeQuery =
          getEntMgr().createNamedQuery(TabvProjectAttr.GET_PROJATTR_FOR_ATTRIBUTEID, TabvProjectAttr.class);
      typeQuery.setParameter("pidcVersId", pidcVersId);
      typeQuery.setParameter("attrId", attrId);
      dbObj = typeQuery.getSingleResult();

    }
    catch (NoResultException e) {
      getLogger().error(e.getLocalizedMessage() + " .No result found", e);
      return null;
    }
    return createDataObject(dbObj);
  }


  /**
   * Method to get all the pidcversionattributes - defined attributes, for other attribtues
   *
   * @param pidcVersionWithDetails
   * @param pidcVersionId pidc version id
   * @return pidcVersionWithDetails filled with pidc version attributes
   * @throws DataException
   */
  public void fillPidcVersionAttributes(final PidcVersionWithDetails pidcVersionWithDetails, final Long pidcVersionId)
      throws DataException {

    Map<Long, Attribute> attributeMap = new AttributeLoader(getServiceData()).getAllAttributes(true);

    // set attributes
    pidcVersionWithDetails.setAllAttributeMap(attributeMap);

    // Add dummy project attributes, for not defined attributes
    for (Entry<Long, Attribute> attrEntry : attributeMap.entrySet()) {

      Attribute attr = attrEntry.getValue();
      if (CommonUtils.isEqual(attr.getLevel(), Long.valueOf(ApicConstants.PROJECT_NAME_ATTR)) ||
          CommonUtils.isEqual(attr.getLevel(), Long.valueOf(ApicConstants.VARIANT_CODE_ATTR)) ||
          CommonUtils.isEqual(attr.getLevel(), Long.valueOf(ApicConstants.SUB_VARIANT_CODE_ATTR))) {
        continue;
      }

      if (!pidcVersionWithDetails.getPidcVersionAttributeMap().containsKey(attrEntry.getKey())) {
        pidcVersionWithDetails.getPidcVersionAttributeMap().put(attrEntry.getKey(),
            createDataObject(pidcVersionId, attrEntry.getValue()));
      }
    }
  }


  /**
   * to check whether the level attrs value is used or not
   *
   * @param attrValueIdList value of the attr
   * @return boolean
   * @throws IcdmException exception
   */
  public boolean isUsedInLevelAttribute(final List<Long> attrValueIdList) throws IcdmException {
    if (CommonUtils.isNotEmpty(attrValueIdList)) {
      String query =
          "SELECT val.prjAttrId from TabvProjectAttr val where val.tabvAttrValue.valueId in :attrValueIdList";
      final TypedQuery<Long> typeQuery = getEntMgr().createQuery(query, Long.class);
      typeQuery.setParameter("attrValueIdList", attrValueIdList);
      return !typeQuery.setMaxResults(1).getResultList().isEmpty();

    }
    throw new IcdmException("Attribute value list must not be empty");
  }

  /**
   * Gets the pidc feul type attribute value
   *
   * @param pidcVersId the pidc vers id
   * @param variantId variant id
   * @return the pidc fuel type attribute value
   * @throws DataException the data exception
   * @throws NumberFormatException the number format exception
   */
  public String getPidcFuelTypeAttrVal(final Long pidcVersId, final Long variantId) throws DataException {
    String fuelTypeAttrIdStr = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.FUELTYPE_ATTR_ID);
    Long fuelTypeAttrId = Long.valueOf(fuelTypeAttrIdStr);
    AttributeValue pidcAttrVal =
        new AttributeValueLoader(getServiceData()).getAttributeValue(pidcVersId, fuelTypeAttrId);
    String fuelTypeAttrVal = "";
    if ((pidcAttrVal == null) && (null != variantId)) {
      // get the attribute value from variant level
      PidcVariantAttribute variantAttribute =
          new PidcVariantAttributeLoader(getServiceData()).getVariantAttribute(variantId, fuelTypeAttrId);
      fuelTypeAttrVal = variantAttribute == null ? "" : variantAttribute.getValue();
    }
    else if (null != pidcAttrVal) {
      fuelTypeAttrVal = pidcAttrVal.getName();
    }
    return fuelTypeAttrVal;
  }

  /**
   * Gets the getCustomerAttrValForPidcVer attribute value
   *
   * @param pidcVersId the pidc vers id
   */
  public String getCustomerAttrValForPidcVer(final Long pidcVersId) throws DataException {
    String customerAttrIdStr =
        new CommonParamLoader(getServiceData()).getValue(CommonParamKey.CUSTOMER_OR_BRAND_ATTR_ID);
    Long customerTypeAttrId = Long.valueOf(customerAttrIdStr);
    AttributeValue pidcCustomerAttrVal =
        new AttributeValueLoader(getServiceData()).getAttributeValue(pidcVersId, customerTypeAttrId);
    return pidcCustomerAttrVal.getName();
  }
}