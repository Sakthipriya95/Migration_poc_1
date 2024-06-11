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

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttrNValueDependencyLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;


/**
 * @author bne4cob
 */
public class PidcVariantAttributeLoader extends AbstractProjectAttributeLoader<PidcVariantAttribute, TabvVariantsAttr> {

  /**
   * @param serviceData Service Data
   */
  public PidcVariantAttributeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.VAR_ATTR, TabvVariantsAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcVariantAttribute createDataObject(final TabvVariantsAttr entity) throws DataException {
    return createDataObject(entity, null);
  }

  /**
   * Create data object from entity using model
   */
  private PidcVariantAttribute createDataObject(final TabvVariantsAttr entity, final PidcVersionAttributeModel model)
      throws DataException {

    PidcVariantAttribute data = new PidcVariantAttribute();

    setCommonFields(data, entity);

    data.setVariantId(entity.getTabvProjectVariant().getVariantId());
    data.setVariantName(entity.getTabvProjectVariant().getTabvAttrValue().getTextvalueEng());

    Attribute attr = (new AttributeLoader(getServiceData())).getDataObjectByID(entity.getTabvAttribute().getAttrId());
    data.setAttrId(attr.getId());
    data.setName(attr.getName());
    data.setDescription(attr.getDescription());
    data.setValueType(attr.getValueType());

    data.setPidcVersionId(entity.getTPidcVersion().getPidcVersId());
    data.setAtChildLevel(CommonUtilConstants.CODE_YES.equals(entity.getIsSubVariant()));
    data.setVarNameValId(entity.getTabvProjectVariant().getTabvAttrValue().getValueId());
    data.setAttrHidden(isHiddenAttribute(model, attr.getId()));
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
      data.setValue(ApicConstants.SUB_VARIANT_ATTR_DISPLAY_NAME);
      data.setUsedFlag(PROJ_ATTR_USED_FLAG.YES.getDbType());
    }

    return data;
  }

  /**
   * @param variantId Variant ID
   * @throws DataException error while creating object
   */
  void loadAttributes(final PidcVersionAttributeModel model) throws DataException {

    for (Long variantId : model.getVariantMap().keySet()) {
      getLogger().debug("Fetching attributes of variant {} ...", variantId);

      Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>(getDefinedAttributes(variantId, model));
      removeInvisibleAttributes(variantId, model, varAttrMap);
      model.setVariantAttributeMap(variantId, varAttrMap);

      getLogger().debug("Pidc Variant: {}. Final attribute count = {}", variantId, varAttrMap.size());
    }
  }


  /**
   * @param pidcVariantId
   * @return
   * @throws DataException
   */
  public String getSDOMPverName(final Long pidcVariantId) throws DataException {
    PidcVariantAttribute pidcVariantAttribute = getSDOMPverAttribute(pidcVariantId);
    String pvername = null;
    if (pidcVariantAttribute != null) {
      AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
      AttributeValue attVal = loader.getDataObjectByID(pidcVariantAttribute.getValueId());
      pvername = attVal.getName();
    }
    return pvername;
  }

  /**
   * @param variantId
   * @return
   * @throws DataException
   */
  public PidcVariantAttribute getSDOMPverAttribute(final Long variantId) throws DataException {
    for (TabvVariantsAttr entity : new PidcVariantLoader(getServiceData()).getEntityObject(variantId)
        .getTabvVariantsAttrs()) {
      TabvAttribute attribute = entity.getTabvAttribute();
      if (attribute.getAttrNameEng().equalsIgnoreCase(ApicConstants.SDOM_PVER_ATTR_NAME)) {
        return createDataObject(entity);
      }
    }
    return null;
  }

  /**
   * @param pidcVersId
   * @param allAttrMap
   * @param projAttrMap
   * @throws DataException
   */
  private void removeInvisibleAttributes(final Long variantId, final PidcVersionAttributeModel model,
      final Map<Long, PidcVariantAttribute> varAttrMap)
      throws DataException {

    Long varNameValueId =
        (new PidcVariantLoader(getServiceData())).getEntityObject(variantId).getTabvAttrValue().getValueId();
    loadInvisibleAttributes(variantId, model, varAttrMap, varNameValueId);

    // remove all invisible attributes
    model.getVariantInvisbleAttributeSet(variantId).forEach(varAttrMap::remove);

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
  private void loadInvisibleAttributes(final Long variantId, final PidcVersionAttributeModel model,
      final Map<Long, PidcVariantAttribute> varAttrMap, final Long varNameValueId)
      throws DataException {

    // Input project attribute map is combination of variant attributes and parent pidc version attributes
    Map<Long, IProjectAttribute> projAttrMap = new HashMap<>(model.getPidcVersAttrMap());
    projAttrMap.putAll(varAttrMap);

    Set<Long> invisibleAttrSet = new HashSet<>();

    Map<Long, Map<Long, AttrNValueDependency>> allAttrDepMap =
        (new AttrNValueDependencyLoader(getServiceData())).getAttributeDependencies(varAttrMap.keySet());

    for (Entry<Long, Map<Long, AttrNValueDependency>> attrEntry : allAttrDepMap.entrySet()) {
      Long attrID = attrEntry.getKey();
      Map<Long, AttrNValueDependency> curAttrDepMap = attrEntry.getValue();
      if (!isVisible(varNameValueId, ApicConstants.VARIANT_CODE_ATTR, curAttrDepMap, projAttrMap, invisibleAttrSet)) {
        invisibleAttrSet.add(attrID);
      }
    }

    model.setVariantInvisbleAttributeSet(variantId, invisibleAttrSet);

    getLogger().debug("Pidc variant: {}. Invisible attributes count = {}", variantId, invisibleAttrSet.size());

  }

  /**
   * Get all defined attributes of given PIDC Variant
   *
   * @param variantId pidc variant Id
   * @return Map. Key - attribute ID, value - data object
   * @throws DataException if object cannot be created
   */
  private Map<Long, PidcVariantAttribute> getDefinedAttributes(final Long variantId,
      final PidcVersionAttributeModel model)
      throws DataException {
    getLogger().debug("Fetching defined attributes of variant {} ...", variantId);

    Map<Long, PidcVariantAttribute> retMap = new HashMap<>();
    PidcVariantAttribute data;

    for (TabvVariantsAttr entity : new PidcVariantLoader(getServiceData()).getEntityObject(variantId)
        .getTabvVariantsAttrs()) {

      if (CommonUtilConstants.CODE_YES.equals(entity.getTabvAttribute().getDeletedFlag())) {
        continue;
      }
      data = createDataObject(entity, model);
      retMap.put(data.getAttrId(), data);
    }

    getLogger().debug("Pidc Variant: {}. Attribute count = {}", variantId, retMap.size());

    return retMap;
  }

  /**
   * @param variantId Long
   * @return Map<Long, PidcVariant>
   * @throws IcdmException
   */
  public Map<Long, PidcVariantAttribute> getVarAttrForVariant(final Long variantId) throws IcdmException {
    getLogger().debug("Fetching defined attributes of variant {} ...", variantId);

    // get the pidc variant
    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    PidcVariant pidcVariant = pidcVariantLoader.getDataObjectByID(variantId);
    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    // load the project attribute model
    PidcVersionAttributeModel projectAttrModel =
        pidcAttrLoader.createModel(pidcVariant.getPidcVersionId(), LOAD_LEVEL.L3_VAR_ATTRS);
    Map<Long, PidcVariantAttribute> retMap = projectAttrModel.getVariantAttributeMap(variantId);

    getLogger().debug("Pidc Variant: {}. Attribute count = {}", variantId, retMap.size());

    return retMap;
  }


  /**
   * Method to fetch variant attribute for given pidc version id and attribute id
   *
   * @param variantId
   * @param attributeId
   * @return
   * @throws DataException
   */
  public PidcVariantAttribute getVariantAttribute(final Long variantId, final Long attributeId) throws DataException {
    TypedQuery<TabvVariantsAttr> typeQuery =
        getEntMgr().createNamedQuery(TabvVariantsAttr.GET_VARATTR_FOR_ATTRIBUTEID, TabvVariantsAttr.class);
    typeQuery.setParameter("variantId", variantId);
    typeQuery.setParameter("attrId", attributeId);
    List<TabvVariantsAttr> resultList = typeQuery.getResultList();
    if (CommonUtils.isNotEmpty(resultList)) {
      return createDataObject(resultList.get(0));
    }
    return null;
  }


  /**
   * @param pidcVersionWithDetails
   * @param model
   * @throws DataException
   */
  public void fillPidcVariantAttributes(final PidcVersionWithDetails pidcVersionWithDetails,
      final PidcVersionAttributeModel model)
      throws DataException {
    // Add dummy project attributes, for not defined attributes
    for (PidcVariant pidcVariant : pidcVersionWithDetails.getPidcVariantMap().values()) {
      Map<Long, PidcVariantAttribute> varAttrs = getDefinedAttributes(pidcVariant.getId(), model);
      pidcVersionWithDetails.getPidcVariantAttributeMap().get(pidcVariant.getId()).putAll(new HashMap<>(varAttrs));
    }
  }

}
