/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.AbstractProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.DataNotFoundException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * FV to AV model & vice versa for new data model
 *
 * @author dja7cob
 */
public class FeatureAttributeAdapterNew extends AbstractSimpleBusinessObject {

  /**
   *
   */
  private static final String NEW_LINE = "\n";
  /**
   * ICDM-2019 set the value not set attr
   */
  private final SortedSet<AttributeValueModel> attrMissingInFeatureMapping = new TreeSet<>();
  /**
   * set the valu not set attr
   */
  private final SortedSet<AttributeValueModel> valueNotSetAttr = new TreeSet<>();
  /**
   * ICDM-2019 VALUE not DEFINED error message constant
   */
  public static final String VALUE_NOT_DEFINED = "VALUE NOT DEFINED => please define a value in the PIDC";

  /**
   * ICDM-2019 ATTRIBUTE not MAPPED error message constant
   */
  public static final String ATTRIBUTE_NOT_MAPPED = "ATTRIBUTE NOT MAPPED => please contact the iCDM Hotline";

  /**
   * ICDM-2019 VALUE not MAPPED error message constant
   */
  public static final String VALUE_NOT_MAPPED = "VALUE NOT MAPPED => please contact the iCDM Hotline";

  private final Map<String, AttributeValue> subVarAttrMap = new HashMap<>();

  private boolean unUsedAttrAvail;

  private Long subVarId;


  /**
   * @param serviceData service data instance
   */
  public FeatureAttributeAdapterNew(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @return the valueNotSetAttr
   */
  public SortedSet<AttributeValueModel> getValueNotSetAttr() {
    return new TreeSet<>(this.valueNotSetAttr);
  }

  /**
   * @param featureIdSet Set of feature ids from ssd
   * @param pidcElementId input pidc element id
   * @return map of attribute value, feature value model
   * @throws IcdmException Exception
   */
  public Map<Feature, FeatureValueModel> getFeatureValues(final Set<Long> featureIdSet, final Long pidcElementId)
      throws IcdmException {

    getLogger().info("Resolving feature values of project. Pidc Element Input = {}; Features input = {}", pidcElementId,
        featureIdSet);

    // Get Features for input feature ids
    // Map of attr id, feature
    Map<Long, Feature> featureMap = (new FeatureLoader(getServiceData())).fetchFeatures(featureIdSet);

    getLogger().info("Attributes identified from features = {}", featureMap.keySet());

    if (pidcElementId == null) {
      if (CommonUtils.isNotEmpty(featureMap)) {
        throw new InvalidInputException("FEAVAL.PIDC_ELEMENT_MISSING");
      }
      return null;
    }

    return processValidElementId(pidcElementId, featureMap);
  }


  /**
   * @param pidcElementId
   * @param attrIdFeatureMap
   * @return
   * @throws IcdmException
   */
  private Map<Feature, FeatureValueModel> processValidElementId(final Long pidcElementId,
      final Map<Long, Feature> attrIdFeatureMap)
      throws IcdmException {

    // PIDC version & variant Loaders
    PidcVariantLoader varLoader = new PidcVariantLoader(getServiceData());
    PidcVersionLoader verLoader = new PidcVersionLoader(getServiceData());

    Map<Long, IProjectAttribute> projAttrMap;

    // check whether the input eement is version / variant
    if (varLoader.isValidId(pidcElementId)) {
      getLogger().debug("Pidc Element {} identified as variant", pidcElementId);
      PidcVariant selPidcVariant = varLoader.getDataObjectByID(pidcElementId);
      projAttrMap = fetchProjtAttrs(selPidcVariant);
    }
    else if (verLoader.isValidId(pidcElementId)) {
      getLogger().debug("Pidc Element {} identified as PIDC Version", pidcElementId);
      PidcVersion selPidcVersion = verLoader.getDataObjectByID(pidcElementId);
      projAttrMap = fetchProjtAttrs(selPidcVersion);
    }
    else {
      throw new InvalidInputException("FEAVAL.PIDC_ELEM_INVALID", pidcElementId);
    }


    // Get map of attribute value model and corresponding feature value model for attributes
    return getFeatureValues(projAttrMap, attrIdFeatureMap);
  }


  /**
   * @param projAttrMap
   * @param attrWithoutVal
   * @param attrIdFeatureMap
   * @param values
   * @return
   * @throws IcdmException
   */
  private Map<Feature, FeatureValueModel> getFeatureValues(final Map<Long, IProjectAttribute> projAttrMap,
      final Map<Long, Feature> attrIdFeatureMap)
      throws IcdmException {

    getLogger().debug("Resolving attribute values from project attributes ...");

    Map<Feature, FeatureValueModel> retMap = new HashMap<>();

    FeatureValueLoader loader = new FeatureValueLoader(getServiceData());

    SortedSet<Attribute> missingAttrSet = new TreeSet<>();
    SortedSet<Attribute> hiddenAttrSet = new TreeSet<>();
    SortedSet<Attribute> childLvlattrSet = new TreeSet<>();
    Set<AttributeValue> featValMissingAttrValSet = new HashSet<>();

    for (Entry<Long, Feature> featCompliEntry : attrIdFeatureMap.entrySet()) {
      Long attrId = featCompliEntry.getKey();

      AttributeValue attrVal = getAttrValueFromProjAttr(attrId, projAttrMap, missingAttrSet, hiddenAttrSet);

      if (attrVal != null) {
        getLogger().debug("Attribute {}, value identified is - {}", attrId, attrVal.getId());

        Feature feature = featCompliEntry.getValue();

        // Get the feature value of feature
        FeatureValue featVal = loader.getFeatureValueWithoutDontCare(feature.getId(), attrVal.getId());

        if (featVal == null) {
          getLogger().debug("Attribute {}, feature {} - feature value mapping not found for attribute value - {}",
              attrId, feature.getId(), attrVal.getId());
          featValMissingAttrValSet.add(attrVal);
        }
        else {
          // Create the model object and set the details
          FeatureValueModel feaValModel = new FeatureValueModel();
          feaValModel.setFeatureId(BigDecimal.valueOf(feature.getId()));
          feaValModel.setFeatureText(feature.getName());
          feaValModel.setValueId(BigDecimal.valueOf(featVal.getId()));
          feaValModel.setValueText(featVal.getName());

          getLogger().debug("Feature {}, Feature value identified is - {}", feature.getId(), featVal.getId());

          retMap.put(feature, feaValModel);
        }
      }
    }

    throwExceptionsIfRequired(missingAttrSet, hiddenAttrSet, childLvlattrSet, featValMissingAttrValSet);

    return retMap;
  }


  private AttributeValue getAttrValueFromProjAttr(final Long attrId, final Map<Long, IProjectAttribute> attrMap,
      final SortedSet<Attribute> missingAttrSet, final SortedSet<Attribute> hiddenAttrSet)
      throws DataException {

    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());
    AttributeValueLoader attrValLdr = attributeValueLoader;

    IProjectAttribute projAttr = attrMap.get(attrId);

    if (null == projAttr) {
      // Attribute not found in map, missing due to missing dependency
      getLogger().info("Attribute not found - {}", attrId);
      missingAttrSet.add(attrLdr.getDataObjectByID(attrId));
      return null;
    }

    if (AbstractProjectAttributeLoader.isDetailsHiddenToCurrentUser(projAttr)) {
      // Attribute not accessible to current user
      getLogger().info("Attribute details hidden to current user - {}", attrId);
      hiddenAttrSet.add(attrLdr.getDataObjectByID(attrId));
      return null;
    }

    if (ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType().equals(projAttr.getUsedFlag()) ||
        ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType().equals(projAttr.getUsedFlag())) {
      // Attribute used flag is new or not defined
      getLogger().info("Attribute not defined yet - {}", attrId);
      missingAttrSet.add(attrLdr.getDataObjectByID(attrId));
      return null;
    }

    if (projAttr.isAtChildLevel() && (projAttr instanceof PidcVariantAttribute)) {
      // Attribute is defined at child level (cannot be considered)
      getLogger().info("Attribute defined at child level - {}", attrId);


      // fill all the Sub var Attr values for the Variant attribute
      PidcVariantAttribute pidcVarAttr = (PidcVariantAttribute) projAttr;
      PidcVariantLoader varAttrLoader = new PidcVariantLoader(getServiceData());
      TabvProjectVariant subVarEntity = varAttrLoader.getEntityObject(pidcVarAttr.getVariantId());
      List<TabvProjSubVariantsAttr> tabvProjSubVariantsAttrs = subVarEntity.getTabvProjSubVariantsAttrs();

      String returnKey = "";
      for (TabvProjSubVariantsAttr tabvProjSubVariantsAttr : tabvProjSubVariantsAttrs) {

        if (tabvProjSubVariantsAttr.getTabvProjectSubVariant().getDeletedFlag().equals("N") &&
            tabvProjSubVariantsAttr.getTabvProjectVariant().getDeletedFlag().equals("N")) {
          AttributeValue attrVal = null;

          if (tabvProjSubVariantsAttr.getTabvAttrValue() == null) {
            Long attributeId = tabvProjSubVariantsAttr.getTabvAttribute().getAttrId();

            if (ApicConstants.CODE_YES.equals(tabvProjSubVariantsAttr.getUsed())) {
              if (attributeId != null) {
                attrVal = attrValLdr.createAttrValUsedObject(attributeId);
              }
              else {
                attrVal = attrValLdr.createAttrValUsedObject(projAttr.getAttrId());
              }
            }
            else {
              if (attributeId != null) {
                attrVal = attrValLdr.createAttrValNotUsedObject(attributeId);
              }
              else {
                attrVal = attrValLdr.createAttrValNotUsedObject(projAttr.getAttrId());
              }
            }
          }
          else {

            attrVal = attributeValueLoader.getDataObjectByID(tabvProjSubVariantsAttr.getTabvAttrValue().getValueId());
          }
          // set the sub var id whwn it is null
          if (this.subVarId == null) {
            this.subVarId = tabvProjSubVariantsAttr.getTabvProjectSubVariant().getSubVariantId();
          }
          String subVarAttrKey =
              tabvProjSubVariantsAttr.getTabvProjectSubVariant().getSubVariantId() + "-" + pidcVarAttr.getAttrId();
          if ((this.subVarAttrMap.get(subVarAttrKey) == null) && attrVal.getAttributeId().equals(attrId)) {
            this.subVarAttrMap.put(subVarAttrKey, attrVal);
          }

          String[] splitKey = subVarAttrKey.split("-");
          Long subVariantId = Long.valueOf(splitKey[0]);
          // use the return key as the Sub varaint of the current iteration.
          if (subVariantId.equals(this.subVarId) &&
              attrId.equals(tabvProjSubVariantsAttr.getTabvAttribute().getAttrId())) {
            returnKey = subVarAttrKey;
          }
        }
      }


      return this.subVarAttrMap.get(returnKey);


    }

    AttributeValue retAttrVal;

    if (null == projAttr.getValueId()) {
      // Value Id is null. Consider for used flag based resolution
      if (ApicConstants.CODE_YES.equals(projAttr.getUsedFlag())) {
        retAttrVal = attrValLdr.createAttrValUsedObject(projAttr.getAttrId());
      }
      else {
        retAttrVal = attrValLdr.createAttrValNotUsedObject(projAttr.getAttrId());
      }
    }
    else {
      // Value found !!!
      retAttrVal = attrValLdr.getDataObjectByID(projAttr.getValueId());
    }

    if (retAttrVal == null) {
      getLogger().info("Attribute value still not found. Could be corrupted PIDC definition - {}", attrId);
      missingAttrSet.add(attrLdr.getDataObjectByID(attrId));
    }

    return retAttrVal;
  }


  private void throwExceptionsIfRequired(final SortedSet<Attribute> missingAttrSet,
      final SortedSet<Attribute> hiddenAttrSet, final SortedSet<Attribute> childLvlattrSet,
      final Set<AttributeValue> featValMissingAttrValSet)
      throws UnAuthorizedAccessException, DataException {

    if (!missingAttrSet.isEmpty()) {
      StringBuilder attrNames = new StringBuilder();
      for (Attribute attr : missingAttrSet) {
        attrNames.append('\n').append(attr.getName());
      }
      throw new DataNotFoundException("FEAVAL.ATTR_VALUE_NOT_DEFINED", attrNames.toString());
    }

    if (!hiddenAttrSet.isEmpty()) {
      StringBuilder attrStr = new StringBuilder();
      for (Attribute attr : hiddenAttrSet) {
        attrStr.append('\n').append(attr.getName());
      }
      throw new UnAuthorizedAccessException("FEAVAL.INSUFFICIENT_PIDC_ACCESS", attrStr.toString());
    }

    if (!childLvlattrSet.isEmpty()) {
      StringBuilder attrStr = new StringBuilder();
      for (Attribute attr : childLvlattrSet) {
        attrStr.append('\n').append(attr.getName());
      }
      throw new DataNotFoundException("FEAVAL.ATTR_DEFINED_AT_CHILD_LVL", attrStr.toString());
    }

    if (!featValMissingAttrValSet.isEmpty()) {
      StringBuilder attrStr = new StringBuilder();
      AttributeLoader attrLdr = new AttributeLoader(getServiceData());
      for (AttributeValue val : featValMissingAttrValSet) {
        attrStr.append('\n').append(attrLdr.getDataObjectByID(val.getAttributeId()).getName()).append(" - ")
            .append(val.getName());
      }
      throw new DataNotFoundException("FEAVAL.FEATURE_VAL_MISSING", attrStr.toString());
    }

  }


  /**
   * @param selPidcVersion
   * @param verLoader
   * @param attProjattrMap
   * @throws IcdmException
   */
  private Map<Long, IProjectAttribute> fetchProjtAttrs(final PidcVersion selPidcVersion) throws IcdmException {

    ProjectAttributeLoader projAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcVerAttrModel =
        projAttrLoader.createModel(selPidcVersion.getId(), LOAD_LEVEL.L1_PROJ_ATTRS);

    return new HashMap<>(pidcVerAttrModel.getPidcVersAttrMap());
  }

  /**
   * @param selPidcVariant
   * @param verLoader
   * @param projattrMap
   * @throws IcdmException
   */
  private Map<Long, IProjectAttribute> fetchProjtAttrs(final PidcVariant selPidcVariant) throws IcdmException {

    PidcVersionAttributeModel pidcVerAttrModel = (new ProjectAttributeLoader(getServiceData()))
        .createModel(selPidcVariant.getPidcVersionId(), LOAD_LEVEL.L3_VAR_ATTRS);

    final Map<Long, IProjectAttribute> projAttrMap = new HashMap<>(pidcVerAttrModel.getPidcVersAttrMap());
    projAttrMap.putAll(pidcVerAttrModel.getVariantAttributeMap(selPidcVariant.getId()));

    return projAttrMap;
  }


  /**
   * @param feaModelList feaModelList
   * @param serviceData serviceData
   * @return the attr value list
   * @throws DataException DataException
   */
  public SortedSet<AttributeValueModel> getAttrValSet(final List<FeatureValueModel> feaModelList,
      final ServiceData serviceData)
      throws DataException {

    SortedSet<AttributeValueModel> attrValSet = new TreeSet<>();

    for (FeatureValueModel featureValueModel : feaModelList) {
      AttributeValueModel attrValue = getAttrValue(serviceData, featureValueModel);
      if (attrValue != null) {
        attrValSet.add(attrValue);
      }
    }
    return attrValSet;
  }

  /**
   * @param serviceData
   * @param featureValueModel
   * @return
   * @throws DataException
   */
  public AttributeValueModel getAttrValue(final ServiceData serviceData, final FeatureValueModel featureValueModel)
      throws DataException {
    Feature featureObj = getFeatureFromFeaModel(serviceData, featureValueModel);


    AttributeLoader attrLoader = new AttributeLoader(serviceData);
    Attribute attrObj;
    if (featureObj.getAttrId() != null) {
      attrObj = attrLoader.getDataObjectByID(featureObj.getAttrId());
    }
    else {
      return null;
    }


    AttributeValueLoader attrValLdr = new AttributeValueLoader(serviceData);
    FeatureValue feaValObj = getFeaValFromFeaValModel(serviceData, featureValueModel);
    Long attrValId = feaValObj.getAttrValId();
    AttributeValue attrValObj = null;
    if (null == attrValId) {
      // Value Id is null. Consider for used flag based resolution
      if (ApicConstants.CODE_YES.equals(feaValObj.getUsedFlag())) {
        attrValObj = attrValLdr.createAttrValUsedObject(featureObj.getAttrId());
      }
      else if (ApicConstants.CODE_NO.equals(feaValObj.getUsedFlag())) {
        attrValObj = attrValLdr.createAttrValNotUsedObject(featureObj.getAttrId());
      }
      else if (CDRConstants.DONT_CARE_ATTR_VALUE_TEXT.equals(feaValObj.getName())) {
        attrValObj = createDontCareAttrValue(featureObj.getAttrId(), feaValObj.getId());
      }
    }
    else {
      attrValObj = attrValLdr.getDataObjectByID(attrValId);
    }

    AttributeValueModel attrvalModel = new AttributeValueModel();
    attrvalModel.setAttr(attrObj);
    attrvalModel.setValue(attrValObj);
    return attrvalModel;
  }


  /**
   * @param attrValModelSet attrValModelSet
   * @return the feature value model text
   * @throws DataException DataException
   */
  public List<FeatureValueModel> getFeaValModelList(final SortedSet<AttributeValueModel> attrValModelSet)
      throws DataException {

    List<FeatureValueModel> feaModelList = new ArrayList<>();

    for (AttributeValueModel attrValModel : attrValModelSet) {
      getFeatureValModelList(feaModelList, attrValModel);
    }
    return feaModelList;

  }


  /**
   * @param attrValModelSet attrValModelSet
   * @return the feature value model text
   * @throws DataException DataException
   */
  public List<FeatureValueModel> getFeaValModelList(final Set<AttributeValueModel> attrValModelSet)
      throws DataException {

    List<FeatureValueModel> feaModelList = new ArrayList<>();

    for (AttributeValueModel attrValModel : attrValModelSet) {
      getFeatureValModelList(feaModelList, attrValModel);
    }
    return feaModelList;

  }

  /**
   * @param feaModelList
   * @param attrValModel
   * @throws DataException
   */
  private void getFeatureValModelList(final List<FeatureValueModel> feaModelList,
      final AttributeValueModel attrValModel)
      throws DataException {
    FeatureValueModel feaValModel = new FeatureValueModel();
    FeatureLoader feaLoader = new FeatureLoader(getServiceData());
    FeatureValueLoader valueLoader = new FeatureValueLoader(getServiceData());
    ConcurrentMap<Long, Feature> fetchAllFeatures = feaLoader.fetchAllFeatures();
    Feature feature = fetchAllFeatures.get(attrValModel.getAttr().getId());

    if (feature != null) {
      feaValModel.setFeatureId(new BigDecimal(feature.getId()));
      FeatureValue feaValue = valueLoader.getFeatureValue(feature.getId(), attrValModel.getValue());
      if (feaValue != null) {
        feaValModel.setValueId(new BigDecimal(feaValue.getId()));
        feaValModel.setValueText(feaValue.getName());
      }
      feaValModel.setFeatureText(feature.getName());

      feaModelList.add(feaValModel);
    }
  }

  /**
   * @param serviceData
   * @param featureValueModel
   * @return
   * @throws DataException
   */
  private FeatureValue getFeaValFromFeaValModel(final ServiceData serviceData,
      final FeatureValueModel featureValueModel)
      throws DataException {
    BigDecimal feaValId = featureValueModel.getValueId();
    FeatureValueLoader feaValLoader = new FeatureValueLoader(serviceData);
    return feaValLoader.getDataObjectByID(feaValId.longValue());

  }


  /**
   * @param serviceData
   * @param featureValueModel
   * @return
   * @throws DataException
   */
  private Feature getFeatureFromFeaModel(final ServiceData serviceData, final FeatureValueModel featureValueModel)
      throws DataException {
    BigDecimal featureId = featureValueModel.getFeatureId();
    FeatureLoader feaLoader = new FeatureLoader(serviceData);
    return feaLoader.getDataObjectByID(featureId.longValue());

  }

  /**
   * Converts the attribute value model to feature value model
   *
   * @param attrValModSet set of AttrVal Model
   * @return the Feature Value Map, key - attribute value model; value - associated feature value model
   * @throws IcdmException IcdmException if Feature-Attribute or Value-Attribute value mapping is missing
   */
  public Map<AttributeValueModel, FeatureValueModel> createFeaValModel(final Set<AttributeValueModel> attrValModSet)
      throws IcdmException {
    this.attrMissingInFeatureMapping.clear();
    // Map for Attribute Val Model
    Map<AttributeValueModel, FeatureValueModel> feaValModelMap = new ConcurrentHashMap<>();
    for (AttributeValueModel attrValueModel : attrValModSet) {
      FeatureValueModel feaValModel;
      try {
        feaValModel = createFeaValModel(attrValueModel);
      }
      catch (IcdmException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        continue;
      }
      feaValModelMap.put(attrValueModel, feaValModel);
    }
    if (!this.valueNotSetAttr.isEmpty() || !this.attrMissingInFeatureMapping.isEmpty()) {
      throw new IcdmException("Feature Attribute Mapping missing");
    }
    return feaValModelMap;
  }

  /**
   * @param attrValueModel AttributeValueModel
   * @return FeatureValueModel
   */
  private FeatureValueModel createFeaValModel(final AttributeValueModel attrValueModel) throws IcdmException {

    Feature feature = new FeatureLoader(getServiceData()).getFeature(attrValueModel.getAttr().getId());

    // Check for Feature attr mapping
    showErrMsgAttrNotMapped(attrValueModel, feature);

    FeatureValue feaValue = null;
    if (this.unUsedAttrAvail && (attrValueModel.getValue() == null)) {
      this.valueNotSetAttr.remove(attrValueModel);

      FeatureValueLoader loader = new FeatureValueLoader(getServiceData());
      List<FeatureValue> featureValues = loader.getFeatureValues(feature.getId());
      for (FeatureValue featureValue : featureValues) {
        // check if the value is same - Attr value id and Feature value Attr Value id OR if the value is used flag ??
        if (isAttrValAndFeaValSame(attrValueModel, featureValue) || isNotSetUsedFlag(attrValueModel, featureValue)) {
          feaValue = featureValue;
          break;
        }
      }
      FeatureValueModel feaValModel = new FeatureValueModel();
      if (feaValue != null) {
        feaValModel.setFeatureId(BigDecimal.valueOf(feature.getId()));
        feaValModel.setFeatureText(feature.getName());
        feaValModel.setValueId(BigDecimal.valueOf(feaValue.getId()));
        feaValModel.setValueText(feaValue.getName());
      }

      return feaValModel;
    }

    feaValue = getFeaVal(attrValueModel, feature, new FeatureValueLoader(getServiceData()));
    // If NULL here, neither a rule for the selected valueid nor USED flag mapping rule nor default rule is available
    if (CommonUtils.isNull(feaValue)) {
      // Proper error message will be constructed in the client side
      String errorMsg = CommonUtils.concatenate("Attribute Value Model for not associated feature value:" +
          attrValueModel.getAttr().getId() + ":" + attrValueModel.getAttr().getName() + ":" +
          attrValueModel.getValue().getId() + ":" + attrValueModel.getValue().getName());

      throw new IcdmException(errorMsg, 1);
    }

    // Create the model object and set the details
    FeatureValueModel feaValModel = new FeatureValueModel();
    feaValModel.setFeatureId(BigDecimal.valueOf(feature.getId()));
    feaValModel.setFeatureText(feature.getName());
    feaValModel.setValueId(BigDecimal.valueOf(feaValue.getId()));
    feaValModel.setValueText(feaValue.getName());

    return feaValModel;
  }

  /**
   * @param attrValueModel
   * @param featureValue
   * @return
   */
  private boolean isNotSetUsedFlag(final AttributeValueModel attrValueModel, final FeatureValue featureValue) {
    // check if the value is same - Attr value id and Feature value Attr Value id
    return (CommonUtils.isNull(attrValueModel.getValue()) && (CommonUtils.isNotNull(featureValue.getUsedFlag())) &&
        CommonUtils.isEqual("?", featureValue.getUsedFlag()));
  }

  /**
   * @param attrValueModel
   * @param feaValue
   * @param featureValue
   * @return
   */
  private boolean isAttrValAndFeaValSame(final AttributeValueModel attrValueModel, final FeatureValue featureValue) {
    // check if the value is same - Attr value id and Feature value Attr Value id
    return ((CommonUtils.isNotNull(attrValueModel.getValue())) &&
        (CommonUtils.isNotNull(featureValue.getAttrValId())) &&
        CommonUtils.isEqual(featureValue.getAttrValId(), attrValueModel.getValue().getId()));
  }

  /**
   * @param attrValueModel
   * @param feature
   * @param feaValLoader
   * @return
   * @throws DataException
   */
  private FeatureValue getFeaVal(final AttributeValueModel attrValueModel, final Feature feature,
      final FeatureValueLoader feaValLoader)
      throws DataException {

    FeatureValue feaValue;
    feaValue = feaValLoader.getFeatureValue(feature.getId(), attrValueModel.getValue());

    if (CommonUtils.isNull(feaValue)) {

      // If the attr's is used/not used ,such attrvalue is stored as an instance of AttributeValueUsed, NotUSed
      if (CommonUtils.isEqual(attrValueModel.getValue().getId(), ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID)) {
        feaValue = feaValLoader.getFeatureValue(feature.getId(), ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID);
      }
      else if (CommonUtils.isEqual(attrValueModel.getValue().getId(), ApicConstants.ATTR_VAL_NOT_SET_VALUE_ID)) {
        feaValue = feaValLoader.getFeatureValue(feature.getId(), ApicConstants.ATTR_VAL_NOT_SET_VALUE_ID);
      }
      else {
        // if feaValue null here means, value is not mapped, in this case check if used flag mapping is available and
        // set
        // the value_id to indicate used flag
        feaValue = feaValLoader.getFeatureValue(feature.getId(), ApicConstants.ATTR_VAL_USED_VALUE_ID);
      }
    }

    return feaValue;
  }

  /**
   * @param attrValueModel
   * @param feature
   * @throws IcdmException
   */
  private void showErrMsgAttrNotMapped(final AttributeValueModel attrValueModel, final Feature feature)
      throws IcdmException {

    if (feature == null) {
      String errorMsg = ATTRIBUTE_NOT_MAPPED;
      boolean contains = this.valueNotSetAttr.contains(attrValueModel);
      if (contains) {
        for (AttributeValueModel attrValmodel : this.valueNotSetAttr) {
          if (attrValueModel.equals(attrValmodel)) {
            String existingErrorMsg = attrValmodel.getErrorMsg();
            attrValmodel.setErrorMsg(existingErrorMsg + ";" + errorMsg);
            break;
          }
        }
      }
      else {
        attrValueModel.setErrorMsg(errorMsg);
      }
      this.attrMissingInFeatureMapping.add(attrValueModel);
      this.valueNotSetAttr.add(attrValueModel);
      throw new IcdmException(CommonUtils.concatenate("The attribute '", attrValueModel.getAttr().getName(),
          "' is not associated with a feature"));
    }
    // ICDM-2019
    // If attr val model not present throw exception
    if (this.valueNotSetAttr.contains(attrValueModel) && !this.unUsedAttrAvail) {
      throw new IcdmException(attrValueModel.getErrorMsg());
    }
  }


  /**
   * Converts the feature value model to attribute value model
   *
   * @param featureValModSet featureValModSet a set of Feature Value Model classes
   * @return the Map<FeatureValueModel, AttributeValueModel> with Key as FeatureValueModel Obj
   * @throws IcdmException for Invalid Conditions
   */
  public Map<FeatureValueModel, AttributeValueModel> createAttrValModel(final Set<FeatureValueModel> featureValModSet)
      throws IcdmException {
    // Map for Attribute Val Model
    Map<FeatureValueModel, AttributeValueModel> attrValModelMap = new ConcurrentHashMap<>();
    for (FeatureValueModel featureValueModel : featureValModSet) {
      // Call the Method to get the Attr Val Model Obj
      AttributeValueModel attrValModel = createAttrValModel(featureValueModel);
      // Put the Value to the Map.
      attrValModelMap.put(featureValueModel, attrValModel);
    }
    return attrValModelMap;
  }

  /**
   * @param featureValueModel FeatureValueModel
   * @return AttributeValueModel
   */
  public AttributeValueModel createAttrValModel(final FeatureValueModel featureValueModel) throws IcdmException {
    FeatureLoader feaLoader = new FeatureLoader(getServiceData());
    Feature feature = feaLoader.getDataObjectByID(featureValueModel.getFeatureId().longValue());

    if (CommonUtils.isNull(feature)) {
      throw new IcdmException(CommonUtils.concatenate("Invalid feature ID - '", featureValueModel.getFeatureId()));
    }
    // Check for Feature attr mapping
    if (CommonUtils.isNull(feature.getAttrId())) {
      throw new IcdmException(
          CommonUtils.concatenate("Feature '", feature.getName(), "' is not associated to any attribute"));
    }
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute selAttr = attrLoader.getDataObjectByID(feature.getAttrId());
    // Check for Valid Attr-feature mapping
    if (CommonUtils.isNull(selAttr)) {
      throw new IcdmException(
          CommonUtils.concatenate("Feature '", feature.getName(), "'  is mapped to an Invalid attribute"));
    }

    // Get the Feature Value from the Feature Value model
    FeatureValueLoader feaValLoader = new FeatureValueLoader(getServiceData());

    FeatureValue featureValue = feaValLoader.getDataObjectByID(featureValueModel.getValueId().longValue());
    AttributeValueModel attrValModel;
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    // If the features whose used flag=N,Y ,such attrvalue is stored as an instance of AttributeValueNotUsed,
    // AttributeValueUsed
    if (CommonUtils.isNull(featureValue.getAttrValId()) && (!CommonUtils.isNull(featureValue.getUsedFlag()))) {

      if (ApicConstants.CODE_NO.equals(featureValue.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(selAttr);
        attrValModel.setValue(attrValLoader.createAttrValNotUsedObject(selAttr.getId()));
        return attrValModel;
      }
      else if (ApicConstants.CODE_YES.equals(featureValue.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(selAttr);
        attrValModel.setValue(attrValLoader.createAttrValUsedObject(selAttr.getId()));
        return attrValModel;
      }

      else if ("?".equals(featureValue.getUsedFlag())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(selAttr);
        attrValModel.setValue(null);
        return attrValModel;
      }
      else {
        throw new IcdmException(
            CommonUtils.concatenate("Invalid feature value ID - '", featureValueModel.getValueId()));
      }
    }
    else if (CommonUtils.isNull(featureValue.getAttrValId())) { // if here, valueId is null and used flag also not
      // defined in ssd
      if (CDRConstants.DONT_CARE_ATTR_VALUE_TEXT.equals(featureValue.getName())) {
        attrValModel = new AttributeValueModel();
        attrValModel.setAttr(selAttr);
        AttributeValueDontCare dontCareAttrVal = createDontCareAttrValue(selAttr.getId(), featureValue.getId());
        attrValModel.setValue(dontCareAttrVal);
        return attrValModel;
      }
// Check for Attr Value-feature value mapping
      throw new IcdmException(CommonUtils.concatenate("Value '", featureValue.getName(), "' of feature '",
          feature.getName(), "' is not associated to any attribute value"));
    }

    AttributeValue selattrVal = attrValLoader.getDataObjectByID(featureValue.getAttrValId());

    // Check for Valid Attr Value-feature value mapping
    if (CommonUtils.isNull(selattrVal)) {
      throw new IcdmException(CommonUtils.concatenate("Value \"", featureValue.getName(), "\" of feature \"",
          feature.getName(), "\" is mapped to an invalid attribute value"));
    }

    // Create and return a new Attr Val model Obj
    attrValModel = new AttributeValueModel();
    attrValModel.setAttr(selAttr);
    attrValModel.setValue(selattrVal);
    return attrValModel;
  }

  /**
   * create the Feature Value Model for the given attributes, from the Project Variant
   *
   * @param amoSet set of IAttributeMappedObject
   * @param pidcVariant pidcVariant
   * @param labelNames
   * @param paramsToRemove
   * @return the FeatureValueModel Map
   * @throws IcdmException IcdmException
   */
  public Set<FeatureValueModel> createAllFeaValModel(final Set<IParameter> amoSet, final PidcVariant pidcVariant,
      final Set<String> labelNames, final List<IParameter> paramsToRemove)
      throws IcdmException {
    PidcVersionLoader pidcVerLoader = new PidcVersionLoader(getServiceData());
    // Create the Attr Val Model Set with Pidcard,Param attr and PIdc Variant
    Map<Long, AttributeValueModel> attrValModMap =
        createAttrValModSet(pidcVerLoader.getDataObjectByID(pidcVariant.getPidcVersionId()), pidcVariant, labelNames,
            amoSet, paramsToRemove);
    // Create the Feature Val Model Set
    return new HashSet<>(createAllFeaValModel(new HashSet<AttributeValueModel>(attrValModMap.values())).values());
  }

  /**
   * @param attrValModSet
   * @param selPidcVer
   * @param labelNames
   * @param amoSet
   * @param paramsToRemove
   * @param cdrFuncParameter
   * @return the Attr Val Model Map. key - Attribute ID, value - attrvaluemodel
   */
  private Map<Long, AttributeValueModel> createAttrValModSet(final PidcVersion selPidcVer, final PidcVariant pidVar,
      final Set<String> labelNames, final Set<IParameter> amoSet, final List<IParameter> paramsToRemove)
      throws IcdmException {

    // Refresh the attributes map of pidc and variant once and then disable the refresh during the iterations
    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    PidcVersionAttributeModel pidcModel;
    if (CommonUtils.isNotNull(pidVar)) {
      pidcModel = pidcAttrLoader.createModel(selPidcVer.getId(), LOAD_LEVEL.L3_VAR_ATTRS);
    }
    else {
      pidcModel = pidcAttrLoader.createModel(selPidcVer.getId(), LOAD_LEVEL.L1_PROJ_ATTRS);
    }
    Map<Long, AttributeValueModel> attrValModMap = new ConcurrentHashMap<>();
    ParameterRuleFetcher paramRuleFecther = new ParameterRuleFetcher(getServiceData());
    Map<String, List<Attribute>> paramAttrMap = paramRuleFecther.getAttributes(amoSet);
    if (CommonUtils.isNotEmpty(paramAttrMap)) {
      for (IParameter amo : amoSet) {
        if (null != paramAttrMap.get(amo.getName())) {
          for (Attribute attr : paramAttrMap.get(amo.getName())) {
            createAttrVal(pidVar, labelNames, paramsToRemove, attrValModMap, amo, attr, pidcModel);
          }
        }
      }
    }
    if (CommonUtils.isNotEmpty(this.valueNotSetAttr)) {

      StringBuilder errMessage = new StringBuilder("Please define a value in the PIDC for those Attributes." +
          NEW_LINE + "Afterwards you can proceed with the review.");
      errMessage.append(NEW_LINE);
      // ICDM-2019
      // Cannot throw exception here as Feature/value to attribute/value mapping wont be checked if exception is thrown
      for (AttributeValueModel attrValueModel : this.valueNotSetAttr) {
        errMessage.append(attrValueModel.getAttr().getName());
        errMessage.append(NEW_LINE);
      }

      throw new IcdmException(errMessage.toString());
    }
    return attrValModMap;
  }

  /**
   * @param pidVar
   * @param labelNames
   * @param paramsToRemove
   * @param attrValModMap
   * @param amo
   * @param attr
   * @throws IcdmException
   */
  private void createAttrVal(final PidcVariant pidVar, final Set<String> labelNames,
      final List<IParameter> paramsToRemove, final Map<Long, AttributeValueModel> attrValModMap, final IParameter amo,
      final Attribute attr, final PidcVersionAttributeModel pidcModel)
      throws IcdmException {
    AttributeValueModel attrValModel;
    // Create the Attr Model If the Variant is selected
    if (CommonUtils.isNotNull(pidVar)) {
      attrValModel = createModelForVarAttr(attr, pidVar, amo, labelNames, pidcModel);
    }
    // Create the Attr Model If No Variant is present
    else {
      attrValModel = createModelForPIDCAttr(attr, pidcModel);
    }


    if (null == attrValModel) {
      if (null != paramsToRemove) {
        paramsToRemove.add(amo);
      }
    }
    else {
      attrValModMap.put(attr.getId(), attrValModel);
    }
  }

  /**
   * @param paramAttr
   * @param pidcAttr
   * @return the AttributeValueModel from the PIDC attr
   */
  private AttributeValueModel createModelForPIDCAttr(final Attribute attr, final PidcVersionAttributeModel pidcModel)
      throws IcdmException {
    IProjectAttribute pidcAttr = pidcModel.getPidcVersAttr(attr.getId());

    assertAttrNotNull(pidcAttr, attr);

    if (pidcAttr == null) {
      AttributeValueModel atttrUnused = getAtttrUnused(attr);
      if (atttrUnused != null) {
        return atttrUnused;
      }
      return null;

    }
    // If the features whose used flag=N,Y ,such attrvalue is stored as an instance of
    // AttributeValueUsed, AttributeValueNotUsed
    AttributeValueModel attrValModel;
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    attrValModel = new AttributeValueModel();
    attrValModel.setAttr(attr);
    if (CommonUtils.isNull(pidcAttr.getValueId())) {

      if (ApicConstants.HIDDEN_VALUE.equals(pidcAttr.getValue())) {
        throw new IcdmException("CDR.HIDDEN_ATTRIBUTES");
      }
      else if (ApicConstants.CODE_NO.equals(pidcAttr.getUsedFlag())) {

        attrValModel.setValue(attrValLoader.createAttrValNotUsedObject(attr.getId()));
        return attrValModel;
      }
      else if (ApicConstants.CODE_YES.equals(pidcAttr.getUsedFlag())) {

        attrValModel.setValue(attrValLoader.createAttrValUsedObject(attr.getId()));
        return attrValModel;
      }

      else {

        attrValModel.setValue(null);
        this.valueNotSetAttr.add(attrValModel);

        return attrValModel;
      }
    }

    assertValueNotNull(attrValLoader.getDataObjectByID(pidcAttr.getValueId()), attr, pidcAttr.getUsedFlag());

    attrValModel.setValue(attrValLoader.getDataObjectByID(pidcAttr.getValueId()));
    return attrValModel;
  }

  /**
   * @param amo
   * @param amoSet
   * @param labelNames
   * @param paramAttr
   * @param pidcAttr
   * @return the AttributeValueModel from the PIDC attr
   * @throws IcdmException
   */
  private AttributeValueModel createModelForVarAttr(final Attribute attr, final PidcVariant pidVariant,
      final IParameter amo, final Set<String> labelNames, final PidcVersionAttributeModel pidcModel)
      throws IcdmException {
    AttributeValueModel attrValModel;
    AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
    // If the attribute is varaint coded attribute
    if (attr.getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(attrValLoader.getDataObjectByID(pidVariant.getNameValueId()));
      return attrValModel;
    }
    // first get the project attributes
    // Refresh of attributes disabled, since refresh is done in createAttrValModSet() before iterations


    IProjectAttribute projAttr = pidcModel.getPidcVersAttrMap().get(attr.getId());


    assertAttrNotNull(projAttr, attr);


    if (projAttr == null) {
      AttributeValueModel atttrUnused = getAtttrUnused(attr);
      if (atttrUnused != null) {
        return atttrUnused;
      }
      return null;


    }
    // Check if variant
    if (projAttr.isAtChildLevel()) {
      // Refresh of attributes disabled, since refresh is done in createAttrValModSet() before iterations
      projAttr = pidcModel.getAllVariantAttributeMap().get(pidVariant.getId()).get(attr.getId());
      assertAttrNotNull(projAttr, attr);
      if (projAttr == null) {
        return null;
      }
      // check if sub-variant
      if (projAttr.isAtChildLevel()) {
        labelNames.remove(amo.getName());
        return null;
      }
    }
    attrValModel = new AttributeValueModel();
    attrValModel.setAttr(attr);
    if (CommonUtils.isNull(projAttr.getValueId())) {

      if (ApicConstants.HIDDEN_VALUE.equals(projAttr.getValue())) {
        throw new IcdmException("CDR.HIDDEN_ATTRIBUTES");
      }
      else if (ApicConstants.CODE_NO.equals(projAttr.getUsedFlag())) {
        attrValModel.setValue(attrValLoader.createAttrValNotUsedObject(attr.getId()));
        return attrValModel;
      }
      else if (ApicConstants.CODE_YES.equals(projAttr.getUsedFlag())) {

        attrValModel.setValue(attrValLoader.createAttrValUsedObject(attr.getId()));
        return attrValModel;
      }
      else {
        attrValModel.setValue(null);
        this.valueNotSetAttr.add(attrValModel);

        return attrValModel;
      }
    }

    attrValModel.setValue(attrValLoader.getDataObjectByID(projAttr.getValueId()));
    return attrValModel;
  }


  /**
   * @param selAttrId
   * @param featureValueId
   * @return AttributeValueDontCare object
   */
  public AttributeValueDontCare createDontCareAttrValue(final Long selAttrId, final Long featureValueId) {
    AttributeValueDontCare dontCareAttrVal = new AttributeValueDontCare();
    dontCareAttrVal.setAttributeId(selAttrId);
    dontCareAttrVal.setId(featureValueId);
    dontCareAttrVal.setName(CDRConstants.DONT_CARE_ATTR_VALUE_TEXT);
    dontCareAttrVal.setTextValueEng(CDRConstants.DONT_CARE_ATTR_VALUE_TEXT);
    return dontCareAttrVal;
  }


  /**
   * Converts the attribute value model to feature value model which also includes values which are not set
   *
   * @param attrValModSet set of AttrVal Model
   * @return the Feature Value Map, key - attribute value model; value - associated feature value model
   * @throws IcdmException IcdmException if Feature-Attribute or Value-Attribute value mapping is missing
   */
  public Map<AttributeValueModel, FeatureValueModel> createAllFeaValModel(final Set<AttributeValueModel> attrValModSet)
      throws IcdmException {
    this.attrMissingInFeatureMapping.clear();
    // Map for Attribute Val Model
    Map<AttributeValueModel, FeatureValueModel> feaValModelMap = new ConcurrentHashMap<>();
    for (AttributeValueModel attrValueModel : attrValModSet) {
      feaValModelMap.put(attrValueModel, createFeaValModel(attrValueModel));
    }

    return feaValModelMap;
  }

  /**
   * create the Feature Value Model for the given attributes, from the Project ID Card
   *
   * @param amoSet set of IAttributeMappedObject
   * @param pidcVer PIDC version
   * @return the FeatureValueModel Map Key - Attribute Value model; value-associated feature value model
   * @throws IcdmException IcdmException if Feature-Attribute or Value-Attribute value mapping is missing
   */
  public Set<FeatureValueModel> createAllFeaValModel(final Set<IParameter> amoSet, final PidcVersion pidcVer)
      throws IcdmException {
    // Create the Attr Val Model Set with Pidcard,Param attr. PIdc Variant not available
    Map<Long, AttributeValueModel> attrValModMap = createAttrValModSet(pidcVer, null, null, amoSet, null);
    // Create the Feature Val Model Set
    return new HashSet<>(createAllFeaValModel(new HashSet<AttributeValueModel>(attrValModMap.values())).values());
  }

  /**
   * Assert value not null
   *
   * @param value attribute value
   * @param attr attribute
   * @param usedFlag
   * @throws IcdmException if value is null
   */
  private void assertValueNotNull(final AttributeValue value, final Attribute attr, final String usedFlag)
      throws IcdmException {
    if (CommonUtils.isNull(value) && (getAtttrUnused(attr) == null) &&
        (usedFlag.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType()) ||
            usedFlag.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {

      AttributeValueModel attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(null);
      CommonUtils.concatenate("Value is not set for the attribute '", attr.getName(),
          "' in the selected project. Review cannot be done.");
      attrValModel.setErrorMsg(VALUE_NOT_DEFINED);
      this.valueNotSetAttr.add(attrValModel);

    }
  }

  /**
   * Assert projAttr not null
   *
   * @param projAttr IPIDCAttribute
   * @param attr attribute
   * @throws IcdmException if value is null
   */
  private void assertAttrNotNull(final IProjectAttribute projAttr, final Attribute attr) throws IcdmException {
    if (CommonUtils.isNull(projAttr) && (getAtttrUnused(attr) == null)) {
      AttributeValueModel attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(null);
      CommonUtils.concatenate("Value is not set for the attribute '", attr.getName(),
          "' in the selected project. Review cannot be done.");
      attrValModel.setErrorMsg(VALUE_NOT_DEFINED);
      this.valueNotSetAttr.add(attrValModel);

    }
  }

  /**
   * @param attr
   * @throws DataException
   */
  private AttributeValueModel getAtttrUnused(final Attribute attr) throws DataException {
    Feature feature = new FeatureLoader(getServiceData()).getFeature(attr.getId());
    if (feature != null) {
      Collection<FeatureValue> values = new FeatureValueLoader(getServiceData()).getFeatureValues(feature.getId());

      for (FeatureValue featureValue : values) {

        if ((featureValue.getUsedFlag() != null) && "?".equals(featureValue.getUsedFlag())) {
          this.unUsedAttrAvail = true;
          AttributeValueModel attrValModel = new AttributeValueModel();

          attrValModel.setValue(null);

          attrValModel.setAttr(attr);

          return attrValModel;
        }

      }

    }

    return null;
  }


  /**
   * @return the subVarAttrMap
   */
  public Map<String, AttributeValue> getSubVarAttrMap() {
    return this.subVarAttrMap;
  }


  /**
   * @return the subVarId
   */
  public Long getSubVarId() {
    return this.subVarId;
  }


  /**
   * @param subVarId the subVarId to set
   */
  public void setSubVarId(final Long subVarId) {
    this.subVarId = subVarId;
  }
}