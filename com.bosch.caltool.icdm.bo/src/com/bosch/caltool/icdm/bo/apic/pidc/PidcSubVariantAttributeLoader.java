/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
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
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author bne4cob
 */
public class PidcSubVariantAttributeLoader
    extends AbstractProjectAttributeLoader<PidcSubVariantAttribute, TabvProjSubVariantsAttr> {

  /**
   * @param serviceData Service Data
   */
  public PidcSubVariantAttributeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.SUBVAR_ATTR, TabvProjSubVariantsAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcSubVariantAttribute createDataObject(final TabvProjSubVariantsAttr entity) throws DataException {
    return createDataObject(entity, null);
  }

  /**
   * Create data object from entity using model
   */
  private PidcSubVariantAttribute createDataObject(final TabvProjSubVariantsAttr entity,
      final PidcVersionAttributeModel model)
      throws DataException {

    PidcSubVariantAttribute data = new PidcSubVariantAttribute();

    setCommonFields(data, entity);
    data.setVariantId(entity.getTabvProjectVariant().getVariantId());
    data.setPidcVersionId(entity.getTPidcVersion().getPidcVersId());
    data.setVariantName(entity.getTabvProjectVariant().getTabvAttrValue().getTextvalueEng());
    data.setSubVariantId(entity.getTabvProjectSubVariant().getSubVariantId());
    data.setSubVariantName(entity.getTabvProjectSubVariant().getTabvAttrValue().getTextvalueEng());

    Attribute attr = (new AttributeLoader(getServiceData())).getDataObjectByID(entity.getTabvAttribute().getAttrId());
    data.setAttrId(attr.getId());
    data.setName(attr.getName());
    data.setDescription(attr.getDescription());
    data.setValueType(attr.getValueType());

    data.setAttrHidden(isHiddenAttribute(model, attr.getId()));
    data.setSubVarNameValId(entity.getTabvProjectSubVariant().getTabvAttrValue().getValueId());
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

      data.setAdditionalInfoDesc(entity.getDescription());
      data.setPartNumber(entity.getPartNumber());
      data.setSpecLink(entity.getSpecLink());

    }

    // No child level further
    data.setAtChildLevel(false);

    return data;
  }

  /**
   * @param variantId Variant ID
   * @throws DataException error while creating object
   */
  void loadAttributes(final PidcVersionAttributeModel model) throws DataException {

    Long subvariantId;
    for (Entry<Long, PidcSubVariant> svarEntry : model.getSubVariantMap().entrySet()) {
      subvariantId = svarEntry.getKey();
      getLogger().debug("Fetching attributes of sub variant {} ...", subvariantId);

      Map<Long, PidcSubVariantAttribute> svarAttrMap = new HashMap<>(getDefinedAttributes(subvariantId, model));
      removeInvisibleAttributes(svarEntry.getValue(), model, svarAttrMap);
      model.setSubVariantAttributeMap(subvariantId, svarAttrMap);

      getLogger().debug("Pidc Sub Variant: {}. Final attribute count = {}", subvariantId, svarAttrMap.size());
    }
  }

  /**
   * @param pidcVersId
   * @param allAttrMap
   * @param projAttrMap
   * @throws DataException
   */
  private void removeInvisibleAttributes(final PidcSubVariant svar, final PidcVersionAttributeModel model,
      final Map<Long, PidcSubVariantAttribute> svarAttrMap)
      throws DataException {

    Long svarNameValueId =
        (new PidcSubVariantLoader(getServiceData())).getEntityObject(svar.getId()).getTabvAttrValue().getValueId();
    loadInvisibleAttributes(svar, model, svarAttrMap, svarNameValueId);

    // remove all invisible attributes
    model.getSubVariantInvisbleAttributeSet(svar.getId()).forEach(svarAttrMap::remove);

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
  private void loadInvisibleAttributes(final PidcSubVariant svar, final PidcVersionAttributeModel model,
      final Map<Long, PidcSubVariantAttribute> svarAttrMap, final Long svarNameValueId)
      throws DataException {

    // Input project attribute map is combination of variant attributes and parent pidc version attributes
    Map<Long, IProjectAttribute> projAttrMap = new HashMap<>(model.getPidcVersAttrMap());
    projAttrMap.putAll(model.getVariantAttributeMap(svar.getPidcVariantId()));
    projAttrMap.putAll(svarAttrMap);

    Set<Long> invisibleAttrSet = new HashSet<>();

    Map<Long, Map<Long, AttrNValueDependency>> allAttrDepMap =
        (new AttrNValueDependencyLoader(getServiceData())).getAttributeDependencies(svarAttrMap.keySet());

    for (Entry<Long, Map<Long, AttrNValueDependency>> attrEntry : allAttrDepMap.entrySet()) {
      Long attrID = attrEntry.getKey();
      Map<Long, AttrNValueDependency> curAttrDepMap = attrEntry.getValue();
      if (!isVisible(svarNameValueId, ApicConstants.SUB_VARIANT_CODE_ATTR, curAttrDepMap, projAttrMap,
          invisibleAttrSet)) {
        invisibleAttrSet.add(attrID);
      }
    }

    model.setSubVariantInvisbleAttributeSet(svar.getId(), invisibleAttrSet);

    getLogger().debug("Pidc sub variant: {}. Invisible attributes count = {}", svar.getId(), invisibleAttrSet.size());

  }

  /**
   * Get all defined attributes of given PIDC Sub Variant
   *
   * @param subvariantId pidc sub variant Id
   * @param model
   * @return Map. Key - attribute ID, value - data object
   * @throws DataException if object cannot be created
   */
  private Map<Long, PidcSubVariantAttribute> getDefinedAttributes(final Long subvariantId,
      final PidcVersionAttributeModel model)
      throws DataException {
    getLogger().debug("Fetching defined attributes of Sub variant {} ...", subvariantId);

    Map<Long, PidcSubVariantAttribute> retMap = new HashMap<>();
    PidcSubVariantAttribute data;

    for (TabvProjSubVariantsAttr entity : new PidcSubVariantLoader(getServiceData()).getEntityObject(subvariantId)
        .getTabvProjSubVariantsAttrs()) {

      if (CommonUtilConstants.CODE_YES.equals(entity.getTabvAttribute().getDeletedFlag())) {
        continue;
      }
      data = createDataObject(entity, model);
      retMap.put(data.getAttrId(), data);
    }

    getLogger().debug("Pidc Sub Variant: {}. Attribute count = {}", subvariantId, retMap.size());

    return retMap;
  }

  /**
   * @param pidcSubVariantId Long
   * @return Map<Long, PidcSubVariantAttribute>
   * @throws IcdmException
   */
  public Map<Long, PidcSubVariantAttribute> getSubVarAttrForSubVarId(final Long pidcSubVariantId) throws IcdmException {
    getLogger().debug("Fetching defined attributes of sub variant {} ...", pidcSubVariantId);


    // get the pidc variant
    PidcSubVariantLoader pidcSubVariantLoader = new PidcSubVariantLoader(getServiceData());
    TabvProjectSubVariant pidcSubVariant = pidcSubVariantLoader.getEntityObject(pidcSubVariantId);
    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    // load the project attribute model
    PidcVersionAttributeModel projectAttrModel =
        pidcAttrLoader.createModel(pidcSubVariant.getTPidcVersion().getPidcVersId(), LOAD_LEVEL.L5_SUBVAR_ATTRS);
    Map<Long, PidcSubVariantAttribute> retMap = projectAttrModel.getSubVariantAttributeMap(pidcSubVariantId);


    getLogger().debug("Pidc SubVariant: {}. Attribute count = {}", pidcSubVariantId, retMap.size());

    return retMap;
  }


  /**
   * Method to fetch sub variant attribute for given pidc version id and attribute id
   *
   * @param subVariantId
   * @param attributeId
   * @return
   * @throws DataException
   */
  public PidcSubVariantAttribute getSubVariantAttribute(final Long subVariantId, final Long attributeId)
      throws DataException {
    TypedQuery<TabvProjSubVariantsAttr> typeQuery = getEntMgr()
        .createNamedQuery(TabvProjSubVariantsAttr.GET_SUB_VARATTR_FOR_ATTRIBUTEID, TabvProjSubVariantsAttr.class);
    typeQuery.setParameter("subVariantId", subVariantId);
    typeQuery.setParameter("attrId", attributeId);
    TabvProjSubVariantsAttr dbObj = typeQuery.getSingleResult();
    return createDataObject(dbObj);

  }


  /**
   * @param pidcVersionWithDetails
   * @param model
   * @throws DataException
   */
  public void fillPidcSubVariantAttributes(final PidcVersionWithDetails pidcVersionWithDetails,
      final PidcVersionAttributeModel model)
      throws DataException {
    // Add dummy project attributes, for not defined attributes
    for (PidcSubVariant pidcSubVariant : pidcVersionWithDetails.getPidcSubVariantMap().values()) {
      Map<Long, PidcSubVariantAttribute> subVarAttrs = getDefinedAttributes(pidcSubVariant.getId(), model);
      pidcVersionWithDetails.getPidcSubVariantAttributeMap().get(pidcSubVariant.getId())
          .putAll(new HashMap<>(subVarAttrs));

    }
  }

}
