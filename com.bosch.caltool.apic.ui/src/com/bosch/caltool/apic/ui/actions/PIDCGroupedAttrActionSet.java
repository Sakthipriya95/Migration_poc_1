/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValueAndValidtyModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedAttrValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * This class has the set of common util methods for the grouped attributes implementation
 *
 * @author dmo5cob
 */

public class PIDCGroupedAttrActionSet {


  private final PidcVersionBO pidcVersionBO;
  private final PidcDataHandler pidcdataHandler;

  /**
   * @param pidcDataHandler PidcDataHandler
   * @param pidcVersionBO AbstractProjectHandler
   */
  public PIDCGroupedAttrActionSet(final PidcDataHandler pidcDataHandler, final PidcVersionBO pidcVersionBO) {
    this.pidcVersionBO = pidcVersionBO;
    this.pidcdataHandler = pidcDataHandler;
  }


  private List<IProjectAttribute> getAsList(final Set<IProjectAttribute> grpAttrSet) {
    List<IProjectAttribute> projAttrSet = new ArrayList<>();
    for (IProjectAttribute iProjectAttribute : grpAttrSet) {
      projAttrSet.add(iProjectAttribute);
    }
    return projAttrSet;
  }

  /**
   * ICDM-2625
   *
   * @param pidcVersion PIDCVersion
   * @param allPIDCAttrMap Map<Long, PidcVersionAttribute> - contains all attributes including invisible attrs
   * @param groupedAttributes list of grouped attributes
   * @return SortedSet<AttributeValue> the set of attr values which are predefined attrs of a grpd attr for which value
   *         needs to be set.
   */
  @SuppressWarnings("unused")
  public List<GroupdAttrPredefAttrModel> getAllPidcGrpAttrVal(final PidcVersion pidcVersion,
      final Map<Long, PidcVersionAttribute> allPIDCAttrMap, final List<IProjectAttribute> groupedAttributes) {
    List<GroupdAttrPredefAttrModel> finalCollectionForDialog = new ArrayList<>();
    List<IProjectAttribute> pidcGroupedAttributes;

    // if grouped attributes is not null, it means the method is being called for set value action. Only for those
    // edited grouped attributes, list should be returned
    if (groupedAttributes == null) {
      pidcGroupedAttributes = getAsList(this.pidcVersionBO.fillApplicableGroupedAttributes(true));
    }
    else {
      // get values from map again.. this will load latest data after setting value to grouped attribute
      Set<IProjectAttribute> grpAttrSet = this.pidcVersionBO.fillApplicableGroupedAttributes(true);
      // get only necessary attributes for set value action
      pidcGroupedAttributes = new ArrayList<>();
      for (IProjectAttribute iProjectAttribute : grpAttrSet) {
        if (groupedAttributes.contains(iProjectAttribute)) {
          pidcGroupedAttributes.add(iProjectAttribute);
        }
      }
    }
    if ((null != pidcVersion) && (null != pidcGroupedAttributes)) {

      // get value ids of grouped attributes
      Set<Long> grpAttrValueIdSet = getValueIds(pidcGroupedAttributes);


      Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap = new HashMap<>();
      Map<Long, Map<Long, PredefinedAttrValue>> predefAttrValueMap = new HashMap<>();

      // Value id should not be empty
      if (!CommonUtils.isNullOrEmpty(grpAttrValueIdSet)) {
        try {
          // service to fetch predefined attribute values and validity for value ids

          PredefinedAttrValueAndValidtyModel model =
              new PredefinedAttrValueServiceClient().getPredefinedAttrValuesAndValidityForValueSet(grpAttrValueIdSet);

          predefinedValidityMap = model.getPredefinedValidityMap();
          predefAttrValueMap = model.getPredefinedAttrValueMap();

        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }

      for (IProjectAttribute ipidcGrpdAttr : pidcGroupedAttributes) {
        if (ipidcGrpdAttr instanceof PidcVariantAttribute) {
          // Check whether the grouped attribute is in variant level
          // Get the corresponding grouped attribute in variant level
          PidcVariantAttribute grpAttrInVarLvl = (PidcVariantAttribute) ipidcGrpdAttr;
          if ((null != grpAttrInVarLvl.getValue()) && this.pidcVersionBO.checkGroupedAttrValueValidity(
              this.pidcdataHandler.getAttributeValueMap().get(grpAttrInVarLvl.getValueId()), predefinedValidityMap) &&
              (null != predefAttrValueMap.get(grpAttrInVarLvl.getValueId()))) {
            // Validations if the grouped attribute is in variant level
            validationsForVariant(allPIDCAttrMap, finalCollectionForDialog, grpAttrInVarLvl, predefAttrValueMap);
          }
        }
        else if (ipidcGrpdAttr instanceof PidcSubVariantAttribute) {
          // Check whether the grouped attribute is in sub variant level
          // Get the corresponding grouped attribute in sub variant level
          PidcSubVariantAttribute grpAttrInSubVarLvl = (PidcSubVariantAttribute) ipidcGrpdAttr;
          validationsForGrpdAttrInSubVar(allPIDCAttrMap, finalCollectionForDialog, grpAttrInSubVarLvl,
              predefAttrValueMap, predefinedValidityMap);

        }
        else if (ipidcGrpdAttr instanceof PidcVersionAttribute) {
          PidcVersionAttribute grpdAttrInPidcLvl = (PidcVersionAttribute) ipidcGrpdAttr;
          if ((null != grpdAttrInPidcLvl.getValue()) && this.pidcVersionBO.checkGroupedAttrValueValidity(
              this.pidcdataHandler.getAttributeValueMap().get(grpdAttrInPidcLvl.getValueId()), predefinedValidityMap) &&
              (null != predefAttrValueMap.get(grpdAttrInPidcLvl.getValueId()))) {
            // If the grouped attribute is in PIDC level
            // Perform validations for grouped attribute in PIDc level
            checkforPIDCLevel(grpdAttrInPidcLvl, allPIDCAttrMap, finalCollectionForDialog,
                new HashSet<>(predefAttrValueMap.get(grpdAttrInPidcLvl.getValueId()).values()));
          }
        }
      }
    }

    return finalCollectionForDialog;
  }


  /**
   * @param pidcGroupedAttributes
   * @return set of value ids of grouped attributes
   */
  private Set<Long> getValueIds(final List<IProjectAttribute> pidcGroupedAttributes) {

    Set<Long> valueIdSet = new HashSet<>();

    for (IProjectAttribute projAttr : pidcGroupedAttributes) {
      if (projAttr.getValueId() != null) {
        valueIdSet.add(projAttr.getValueId());
      }
    }

    return valueIdSet;
  }

  /**
   * @param allPIDCAttrMap
   * @param finalCollectionForDialog
   * @param grpAttrInSubVarLvl
   */
  private void validationsForGrpdAttrInSubVar(final Map<Long, PidcVersionAttribute> allPIDCAttrMap,
      final List<GroupdAttrPredefAttrModel> finalCollectionForDialog, final PidcSubVariantAttribute grpAttrInSubVarLvl,
      final Map<Long, Map<Long, PredefinedAttrValue>> predefAttrValueMap,
      final Map<Long, Map<Long, PredefinedValidity>> predefinedValidityMap) {

    AttributeValue attrValue = this.pidcdataHandler.getAttributeValue(grpAttrInSubVarLvl.getValueId());

    Map<Long, PredefinedAttrValue> preDefinedAttrValues = predefAttrValueMap.get(grpAttrInSubVarLvl.getValueId());
    if ((!this.pidcdataHandler.getSubVariantMap().get(grpAttrInSubVarLvl.getSubVariantId()).isDeleted()) &&
        (null != grpAttrInSubVarLvl.getValue()) &&
        this.pidcVersionBO.checkGroupedAttrValueValidity(attrValue, predefinedValidityMap) &&
        (null != preDefinedAttrValues)) {
      Map<IProjectAttribute, PredefinedAttrValue> modelMapForGrpAttrInSubVarLvl = new ConcurrentHashMap<>();

      for (PredefinedAttrValue predefndAttrValSubVar : preDefinedAttrValues.values()) {


        // Predef attr(pidc lvl) for grouped attr(sub var level)
        PidcVersionAttribute predefAttrPidcLvl = allPIDCAttrMap.get(predefndAttrValSubVar.getPredefinedAttrId());
        // Check if the predefined attribute is at variant level (here grouped attribute is also in variant
        // level)
        if (null != predefAttrPidcLvl) {
          PidcVersionAttributeBO pidcVersionHandler = new PidcVersionAttributeBO(predefAttrPidcLvl,
              new PidcVersionBO(this.pidcVersionBO.getPidcVersion(), this.pidcdataHandler));
          if (pidcVersionHandler.isNotInvisibleAttr(predefndAttrValSubVar.getPredefinedAttrId(), grpAttrInSubVarLvl)) {
            if (predefAttrPidcLvl.isAtChildLevel()) {
              // If the predef attr for grouped attr is in var level
              ifPredefAttrIsInVarLvl(grpAttrInSubVarLvl, predefndAttrValSubVar, predefAttrPidcLvl,
                  modelMapForGrpAttrInSubVarLvl);
            }
            else {
              // If the predefined attribute is in PIDC level
              modelMapForGrpAttrInSubVarLvl.put(predefAttrPidcLvl, predefndAttrValSubVar);
            }
          }
        }
      }
      if (CommonUtils.isNotEmpty(modelMapForGrpAttrInSubVarLvl)) {
        GroupdAttrPredefAttrModel grpAttrPredefAttr =
            new GroupdAttrPredefAttrModel(grpAttrInSubVarLvl, modelMapForGrpAttrInSubVarLvl);
        finalCollectionForDialog.add(grpAttrPredefAttr);
      }
    }
  }


  /**
   * @param finalCollectionForDialog
   * @param allPIDCAttrMap
   * @param pidcAttr
   * @param pidcVersion
   */
  private void checkforPIDCLevel(final PidcVersionAttribute pidcAttr,
      final Map<Long, PidcVersionAttribute> allPIDCAttrMap,
      final List<GroupdAttrPredefAttrModel> finalCollectionForDialog,
      final Set<PredefinedAttrValue> preDefinedAttrValueSet) {


    if ((null != pidcAttr.getValue()) && (null != preDefinedAttrValueSet)) {
      Map<IProjectAttribute, PredefinedAttrValue> modelMapForGrpAttrInPidcLvl =
          new HashMap<IProjectAttribute, PredefinedAttrValue>();
      for (PredefinedAttrValue predefAttrValForGrpAttrInPidcLvl : preDefinedAttrValueSet) {
        PidcVersionAttributeBO pidcVersionHandler =
            new PidcVersionAttributeBO(allPIDCAttrMap.get(predefAttrValForGrpAttrInPidcLvl.getPredefinedAttrId()),
                new PidcVersionBO(this.pidcVersionBO.getPidcVersion(), this.pidcdataHandler));
        if (pidcVersionHandler.isNotInvisibleAttr(predefAttrValForGrpAttrInPidcLvl.getPredefinedAttrId(), pidcAttr)) {
          // Check the level of predefined attribute of the grouped attribute in PIDC level
          modelMapForGrpAttrInPidcLvl = checkPidcGrpAttrPredfndAttrLevel(allPIDCAttrMap,
              predefAttrValForGrpAttrInPidcLvl, modelMapForGrpAttrInPidcLvl);
        }
      }
      if (CommonUtils.isNotEmpty(modelMapForGrpAttrInPidcLvl)) {
        GroupdAttrPredefAttrModel grpAttrPredefAttr =
            new GroupdAttrPredefAttrModel(pidcAttr, modelMapForGrpAttrInPidcLvl);
        finalCollectionForDialog.add(grpAttrPredefAttr);
      }
    }
  }


  /**
   * @param pidcVersion
   * @param allPIDCAttrMap
   * @param finalCollectionForDiLOG
   * @param predefAttrValForGrpAttrInPidcLvl
   * @param pidcGrpAttrMap
   * @return
   */
  private Map<IProjectAttribute, PredefinedAttrValue> checkPidcGrpAttrPredfndAttrLevel(
      final Map<Long, PidcVersionAttribute> allPIDCAttrMap, final PredefinedAttrValue predefAttrValForGrpAttrInPidcLvl,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMapForGrpAttrInPidcLvl) {

    // Predefined attribute (pidc level) of the grouped attribute in PIDC level
    PidcVersionAttribute predefAttrPidcInstance =
        allPIDCAttrMap.get(predefAttrValForGrpAttrInPidcLvl.getPredefinedAttrId());
    if (null != predefAttrPidcInstance) {
      if (predefAttrPidcInstance.isAtChildLevel()) {
        // If Predefined attribute of the grouped attribute in PIDC level is in Variant level
        ifPredefAttrofPidcGrpIsInVarLvl(predefAttrValForGrpAttrInPidcLvl, predefAttrPidcInstance,
            modelMapForGrpAttrInPidcLvl);
      }
      else if (((null == predefAttrValForGrpAttrInPidcLvl.getPredefinedValue()) &&
          !predefAttrPidcInstance.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) ||
          CommonUtils.isNotEqual(predefAttrPidcInstance.getValue(),
              predefAttrValForGrpAttrInPidcLvl.getPredefinedValue())) {
        // Task 229131
        // If Predefined attribute of the grouped attribute in PIDC level is in PIDC level
        modelMapForGrpAttrInPidcLvl.put(predefAttrPidcInstance, predefAttrValForGrpAttrInPidcLvl);
      }
    }
    return modelMapForGrpAttrInPidcLvl;
  }


  /**
   * @param pidcVersion
   * @param finalCollectionForDialog
   * @param predefAttrValForGrpAttrInPidcLvl
   * @param predefAttrPidcInstance
   * @param modelMapForGrpAttrInPidcLvl
   */
  private void ifPredefAttrofPidcGrpIsInVarLvl(final PredefinedAttrValue predefAttrValForGrpAttrInPidcLvl,
      final PidcVersionAttribute predefAttrPidcInstance,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMapForGrpAttrInPidcLvl) {


    // If the predefined attribute is in variant/sub variant level and the grouped attribute is in pidc level
    // add the grouped attribute to the set
    SortedSet<PidcVariant> pidcAllVariantSet = this.pidcVersionBO.getVariantsSet();
    // Iterate over all variants
    for (PidcVariant pidcVariantOfPredefAttr : pidcAllVariantSet) {

      // Predefined attribute (variant level) of the grouped attribute in PIDC level
      PidcVariantAttribute predefAttrVarInstance = this.pidcdataHandler.getVariantAttributeMap()
          .get(pidcVariantOfPredefAttr.getId()).get(predefAttrPidcInstance.getAttrId());

      if (null != predefAttrVarInstance) {
        // Check if predef attr is in sub variant
        if (predefAttrVarInstance.isAtChildLevel()) {
          // If the Predefined attribute of the grouped attribute in PIDC level is in sub variant level
          ifPredefAttrPidcGrpAttrIsInSubVarLvl(predefAttrValForGrpAttrInPidcLvl, predefAttrPidcInstance,
              pidcVariantOfPredefAttr, modelMapForGrpAttrInPidcLvl);
        }
        else {
          modelMapForGrpAttrInPidcLvl.put(predefAttrVarInstance, predefAttrValForGrpAttrInPidcLvl);
        }
      }
    }
  }


  /**
   * @param predefAttrValForGrpAttrInPidcLvl
   * @param predefAttrPidcInstance
   * @param pidcVariantOfPredefAttr
   * @param modelMapForGrpAttrInPidcLvl
   * @param grpAttrInPidcLvl
   */
  private void ifPredefAttrPidcGrpAttrIsInSubVarLvl(final PredefinedAttrValue predefAttrValForGrpAttrInPidcLvl,
      final PidcVersionAttribute predefAttrPidcInstance, final PidcVariant pidcVariantOfPredefAttr,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMapForGrpAttrInPidcLvl) {
    PidcVariantBO vHandler =
        new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), pidcVariantOfPredefAttr, this.pidcdataHandler);

    SortedSet<PidcSubVariant> pidcSubVariantSet = vHandler.getSubVariantsSet();
    for (PidcSubVariant pidcSubVar : pidcSubVariantSet) {
      // Predefined attribute (sub variant level) for the grouped attribute (PIDC level)
      PidcSubVariantAttribute predefAttrSubVarInstance = this.pidcdataHandler.getSubVariantAttributeMap()
          .get(pidcSubVar.getId()).get(predefAttrPidcInstance.getAttrId());

      if (null != predefAttrSubVarInstance) {
        modelMapForGrpAttrInPidcLvl.put(predefAttrSubVarInstance, predefAttrValForGrpAttrInPidcLvl);
      }
    }
  }

  /**
   * @param grpAttrInSubVarLvl
   * @param predefndAttrValSubVar
   * @param predefAttrPidcLvl
   * @param modelMap
   * @param pidcVersion
   */
  private void ifPredefAttrIsInVarLvl(final PidcSubVariantAttribute grpAttrInSubVarLvl,
      final PredefinedAttrValue predefndAttrValSubVar, final PidcVersionAttribute predefAttrPidcLvl,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMap) {

    PidcVariantAttribute predefAttrVarInstance = this.pidcdataHandler.getVariantAttributeMap()
        .get(grpAttrInSubVarLvl.getVariantId()).get(predefAttrPidcLvl.getAttrId());

    if (null != predefAttrVarInstance) {
      // Check if predef attr is in sub variant
      if (predefAttrVarInstance.isAtChildLevel()) {
        // If predef attr for grpd attr(sub var lvl) is in sub var level
        ifPredefAttrIsInSubVar(grpAttrInSubVarLvl, predefndAttrValSubVar, predefAttrPidcLvl, modelMap);
      }
      else {

        modelMap.put(predefAttrVarInstance, predefndAttrValSubVar);
      }
    }
  }

  /**
   * @param grpAttrpredefAttrCollection
   * @param grpAttrInSubVarLvl
   * @param predefndAttrValSubVar
   * @param predefAttrPidcLvl
   * @param modelMap
   */
  private void ifPredefAttrIsInSubVar(final PidcSubVariantAttribute grpAttrInSubVarLvl,
      final PredefinedAttrValue predefndAttrValSubVar, final PidcVersionAttribute predefAttrPidcLvl,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMap) {
    PidcSubVariantBO subVarHdlr = new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(),
        this.pidcdataHandler.getSubVariantMap().get(grpAttrInSubVarLvl.getSubVariantId()), this.pidcdataHandler);
    // Predef attr(sub var lvl) for groupd attr(sub var lvl)
    PidcSubVariantAttribute predefAttrSubVarInstance = subVarHdlr.getAttributesAll().get(predefAttrPidcLvl.getAttrId());
    if (null != predefAttrSubVarInstance) {
      if (((null == predefndAttrValSubVar.getPredefinedValue()) &&
          !predefAttrSubVarInstance.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) ||
          CommonUtils.isNotEqual(predefAttrSubVarInstance.getValue(), predefndAttrValSubVar.getPredefinedValue())) {
        // Task 229131
        modelMap.put(predefAttrSubVarInstance, predefndAttrValSubVar);
      }
    }
  }

  /**
   * @param allPIDCAttrMap
   * @param finalCollectionForDialog
   * @param grpAttrInVarLvl
   * @param pidcGrpAttr
   * @param currPidcVariant
   */
  private void validationsForVariant(final Map<Long, PidcVersionAttribute> allPIDCAttrMap,
      final List<GroupdAttrPredefAttrModel> finalCollectionForDialog, final PidcVariantAttribute grpAttrInVarLvl,
      final Map<Long, Map<Long, PredefinedAttrValue>> predefAttrValueMap) {

    // If the grouped attribute is at the variant level
    // Get all predefined attributes of the grouped variant attribute's value
    if ((!this.pidcdataHandler.getVariantMap().get(grpAttrInVarLvl.getVariantId()).isDeleted()) &&
        (null != grpAttrInVarLvl.getValue())) {

      Map<Long, PredefinedAttrValue> preDefinedAttrValues = predefAttrValueMap.get(grpAttrInVarLvl.getValueId());
      if (null != preDefinedAttrValues) {

        Map<IProjectAttribute, PredefinedAttrValue> modelMap = new ConcurrentHashMap<>();

        for (PredefinedAttrValue predefndAttrValforGrpAttrInVar : preDefinedAttrValues.values()) {


          // Get the predefined attribute (pidc level) for the grouped attr in var level
          PidcVersionAttribute predefndAttrforGrpAttrInVar =
              allPIDCAttrMap.get(predefndAttrValforGrpAttrInVar.getPredefinedAttrId());
          if (null != predefndAttrforGrpAttrInVar) {
            PidcVersionAttributeBO pidcVersionHandler = new PidcVersionAttributeBO(predefndAttrforGrpAttrInVar,
                new PidcVersionBO(this.pidcVersionBO.getPidcVersion(), this.pidcdataHandler));
            if (pidcVersionHandler.isNotInvisibleAttr(predefndAttrValforGrpAttrInVar.getPredefinedAttrId(),
                grpAttrInVarLvl)) {
              // Check if the predefined attribute is at variant level (here grouped attribute is also in variant
              // level)
              // Both are in same level
              if (predefndAttrforGrpAttrInVar.isAtChildLevel()) {
                // If the predefined attribute of the grouped attribute in variant level is in variant level
                ifPredefAttrForVarGrpAttrIsInVarLvl(grpAttrInVarLvl, predefndAttrValforGrpAttrInVar,
                    predefndAttrforGrpAttrInVar, modelMap);
              }
              else {
                // If the predefined attribute is in PIDC level
                modelMap.put(predefndAttrforGrpAttrInVar, predefndAttrValforGrpAttrInVar);
              }
            }
          }
        }
        if (CommonUtils.isNotEmpty(modelMap)) {
          GroupdAttrPredefAttrModel grpAttrPredefAttr = new GroupdAttrPredefAttrModel(grpAttrInVarLvl, modelMap);
          finalCollectionForDialog.add(grpAttrPredefAttr);
        }
      }
    }
  }


  /**
   * @param grpAttrInVarLvl
   * @param predefndAttrValforGrpAttrInVar
   * @param predefndAttrforGrpAttrInVar
   * @param modelMap
   * @param pidcVersion
   */
  private void ifPredefAttrForVarGrpAttrIsInVarLvl(final PidcVariantAttribute grpAttrInVarLvl,
      final PredefinedAttrValue predefndAttrValforGrpAttrInVar, final PidcVersionAttribute predefndAttrforGrpAttrInVar,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMap) {
    PidcVariantBO varHan = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
        this.pidcdataHandler.getVariantMap().get(grpAttrInVarLvl.getVariantId()), this.pidcdataHandler);
    // Predefined att(var level) for grouped attr(var level)
    PidcVariantAttribute predefAttrGrpAttrInVarVariantInstance =
        varHan.getAttributesAll().get(predefndAttrforGrpAttrInVar.getAttrId());
    if (null != predefAttrGrpAttrInVarVariantInstance) {
      // Check if predef attr is in sub variant
      if (predefAttrGrpAttrInVarVariantInstance.isAtChildLevel()) {

        // If the predefined attribute of the grouped attr in var level is in sub var level
        ifPredefAttrVarGrpAttrIsInSubVarLvl(predefndAttrValforGrpAttrInVar,
            this.pidcdataHandler.getVariantMap().get(grpAttrInVarLvl.getVariantId()), predefndAttrforGrpAttrInVar,
            modelMap);
      }
      else if (((null == predefndAttrValforGrpAttrInVar.getPredefinedValue()) && !predefAttrGrpAttrInVarVariantInstance
          .getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) ||
          CommonUtils.isNotEqual(predefAttrGrpAttrInVarVariantInstance.getValue(),
              predefndAttrValforGrpAttrInVar.getPredefinedValue())) {
        // Task 229131
        modelMap.put(predefAttrGrpAttrInVarVariantInstance, predefndAttrValforGrpAttrInVar);
      }
    }
  }

  /**
   * @param finalCollectionForDialog
   * @param predefndAttrValforGrpAttrInVar
   * @param PidcVariant
   * @param predefndAttrforGrpAttrInVar
   * @param modelMap
   */
  private void ifPredefAttrVarGrpAttrIsInSubVarLvl(final PredefinedAttrValue predefndAttrValforGrpAttrInVar,
      final PidcVariant PidcVariant, final PidcVersionAttribute predefndAttrforGrpAttrInVar,
      final Map<IProjectAttribute, PredefinedAttrValue> modelMap) {
    PidcVariantBO varHan = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
        this.pidcdataHandler.getVariantMap().get(PidcVariant.getId()), this.pidcdataHandler);
    // Sub variants of PidcVariant in which the grouped attribute is present
    SortedSet<PidcSubVariant> pidcSubVariantSet = varHan.getSubVariantsSet();

    for (PidcSubVariant currPidcSubVar : pidcSubVariantSet) {
      PidcSubVariantBO subvarHan = new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(),
          this.pidcdataHandler.getSubVariantMap().get(currPidcSubVar.getId()), this.pidcdataHandler);
      // Predefined att(sub var level) for grouped attr(var level)
      PidcSubVariantAttribute predefAttrInSubVarLvlSubVarInstance =
          subvarHan.getAttributesAll().get(predefndAttrforGrpAttrInVar.getAttrId());
      if (null != predefAttrInSubVarLvlSubVarInstance) {
        modelMap.put(predefAttrInSubVarLvlSubVarInstance, predefndAttrValforGrpAttrInVar);
      }
    }
  }

  /**
   * ICDM-2625
   *
   * @param attributeValue AttributeValue
   * @param allPIDCAttrMap final Map<Long, PidcVersionAttribute> - contains all attributes including invisible attrs
   * @param grpdAttr instance
   * @param predefinedAttrValMap pred def attribute value map
   * @return Boolean
   */
  public boolean checkAllPredefAttrVal(final AttributeValue attributeValue,
      final Map<Long, PidcVersionAttribute> allPIDCAttrMap, final IProjectAttribute grpdAttr,
      final Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValMap) {

    boolean isValidAttr = false;
    Set<PredefinedAttrValue> preDefinedAttrValueSet = getPredefinedAttrValSet(attributeValue, predefinedAttrValMap);

    if (CommonUtils.isNotEmpty(preDefinedAttrValueSet) &&
        !isallPredefinedAttrInSetInvisible(allPIDCAttrMap, preDefinedAttrValueSet)) {
      for (PredefinedAttrValue predefAttrVal : preDefinedAttrValueSet) {
        PidcVersionAttribute predfnAttr = allPIDCAttrMap.get(predefAttrVal.getPredefinedAttrId());
        if (null != predfnAttr) {
          isValidAttr = validateIfApplicable(predefAttrVal, predfnAttr, grpdAttr);
          if (isValidAttr) {
            return isValidAttr;
          }
        }
      }
    }
    return isValidAttr;
  }

  /**
   * @param attrValue
   * @param predefinedAttrValueMap
   * @return
   */
  public Set<PredefinedAttrValue> getPredefinedAttrValSet(final AttributeValue attrValue,
      final Map<Long, Map<Long, PredefinedAttrValue>> predefinedAttrValueMap) {
    Set<PredefinedAttrValue> preDefinedAttrValueSet = null;
    if (CommonUtils.isNullOrEmpty(predefinedAttrValueMap)) {
      AttributeValueClientBO valBO = new AttributeValueClientBO(attrValue);
      preDefinedAttrValueSet = valBO.getPreDefinedAttrValueSet();
    }
    else {
      Map<Long, PredefinedAttrValue> preDefinedAttrValues = predefinedAttrValueMap.get(attrValue.getId());
      if (!CommonUtils.isNullOrEmpty(preDefinedAttrValues)) {
        preDefinedAttrValueSet = new HashSet<>(preDefinedAttrValues.values());
      }
    }
    return preDefinedAttrValueSet;
  }

  /**
   * @param allPIDCAttrMap all pidsc attriibute map
   * @param preDefinedAttrValueSet pre-def attr set
   * @return true if all the pre-def attr in set is invisible
   */
  public boolean isallPredefinedAttrInSetInvisible(final Map<Long, PidcVersionAttribute> allPIDCAttrMap,
      final Set<PredefinedAttrValue> preDefinedAttrValueSet) {
    boolean isallPredefinedAttrInSetInvisible = false;
    // ALM - 606382
    int invisiblePredefAttrCount = 0;
    for (PredefinedAttrValue predefnAttrVal : preDefinedAttrValueSet) {
      PidcVersionAttribute predfnAttr = allPIDCAttrMap.get(predefnAttrVal.getPredefinedAttrId());
      if (isAttrInvisible(predfnAttr)) {
        invisiblePredefAttrCount++;
      }
    }
    if (preDefinedAttrValueSet.size() == invisiblePredefAttrCount) {
      isallPredefinedAttrInSetInvisible = true;
    }
    return isallPredefinedAttrInSetInvisible;
  }

  private boolean isAttrInvisible(final PidcVersionAttribute predfnAttr) {
    boolean isAttrInvisible = false;
    Set<Long> varInvisibleAttrSet = this.pidcdataHandler.getPidcVersInvisibleAttrSet();
    return CommonUtils.isNotEmpty(varInvisibleAttrSet) ? varInvisibleAttrSet.contains(predfnAttr.getAttrId())
        : isAttrInvisible;
  }

  /**
   * @param booleanList
   * @param predefAttrVal
   * @param preDfndAttr
   * @param grpdAttr
   * @return
   */
  private boolean validateIfApplicable(final PredefinedAttrValue predefAttrVal, final PidcVersionAttribute preDfndAttr,
      final IProjectAttribute grpAttr) {
    if (preDfndAttr.isAtChildLevel()) {

      if (grpAttr instanceof PidcVersionAttribute) {
        return true;
      }
      else if (grpAttr instanceof PidcVariantAttribute) {
        PidcVariantBO varHan = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
            this.pidcdataHandler.getVariantMap().get(((PidcVariantAttribute) grpAttr).getVariantId()),
            this.pidcdataHandler);
        PidcVariantAttribute varAttr = varHan.getAllVarAttribute(preDfndAttr.getAttrId());

        if (null != varAttr) {
          if (varAttr.isAtChildLevel()) {
            return true;
          }
          if ((null != predefAttrVal.getPredefinedValue()) && (null != varAttr.getValue())) {
            if (!varAttr.getValue().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
              return true;
            }
          }
          else if (!preDfndAttr.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
            return true;
          }
        }
      }
      else if (grpAttr instanceof PidcSubVariantAttribute) {
        PidcSubVariantBO subVarHandler = new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(),
            this.pidcdataHandler.getSubVariantMap().get(((PidcSubVariantAttribute) grpAttr).getSubVariantId()),
            this.pidcdataHandler);
        PidcSubVariantAttribute subvarAttr = subVarHandler.getAttributesAll().get(preDfndAttr.getAttrId());

        if (null != subvarAttr) {

          if ((null != predefAttrVal.getPredefinedValue()) && (null != subvarAttr.getValue())) {
            if (!subvarAttr.getValue().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
              return true;
            }
          }
          else if (!preDfndAttr.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
            return true;
          }

        }
        else {
          return true;
        }
      }
    }
    else {
      if (grpAttr instanceof PidcVersionAttribute) {

        if ((null != predefAttrVal.getPredefinedValue()) && (null != (preDfndAttr).getValue())) {
          if (!(preDfndAttr).getValue().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
            return true;
          }
        }
        else if (!preDfndAttr.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
          return true;
        }
      }
      else {
        return true;
      }
    }
    return false;
  }


  /**
   * @param grpdAttr instance
   * @param preDefndAttr instance
   * @return boolean
   */
  public boolean checkIfGrpdAttrsAtVarLevel(final IProjectAttribute grpdAttr, final PidcVersionAttribute preDefndAttr) {

    // If the grouped attr is at variant level and the predefined attr is not at the variant level(its in PIDC level)
    // In this case the predefined attr should be moved down to var level
    if ((grpdAttr instanceof PidcVariantAttribute) && !preDefndAttr.isAtChildLevel()) {
      return true;
    }
    // else if the If the grouped attr is at variant level and the predefined attr is at the sub variant level
    // do nothing since the predefined attr should not be moved up to higher level
    else if (grpdAttr instanceof PidcVariantAttribute) {
      PidcVariantAttribute varAttr = (PidcVariantAttribute) grpdAttr;
      if (varAttr.isAtChildLevel()) {
        // check if at subvar level
        PidcVariantBO varHan = new PidcVariantBO(this.pidcVersionBO.getPidcVersion(),
            this.pidcdataHandler.getVariantMap().get(((PidcVariantAttribute) grpdAttr).getVariantId()),
            this.pidcdataHandler);
        PidcSubVariant subVar = varHan.getSubVariantsSet().first();
        PidcSubVariantBO subVarHan =
            new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(), subVar, this.pidcdataHandler);
        PidcSubVariantAttribute subVarAttr = subVarHan.getAttributesAll().get(preDefndAttr.getAttrId());
        if (null != subVarAttr) {
          return true;
        }
      }
      else {
        return false;
      }
    }
    return false;
  }


  /**
   * @param grpdAttr instance
   * @param preDefndAttr instance
   * @return boolean
   */
  public boolean checkIfGrpdAttrsAtSubVarLevel(final IProjectAttribute grpdAttr,
      final PidcVersionAttribute preDefndAttr) {

    if (grpdAttr instanceof PidcSubVariantAttribute) {

      // check if at subvar level
      PidcSubVariant subVar =
          this.pidcdataHandler.getSubVariantMap().get(((PidcSubVariantAttribute) grpdAttr).getSubVariantId());
      PidcSubVariantBO subVarHan =
          new PidcSubVariantBO(this.pidcVersionBO.getPidcVersion(), subVar, this.pidcdataHandler);
      PidcSubVariantAttribute subVarAttr = subVarHan.getAttributesAll().get(preDefndAttr.getAttrId());
      if (null == subVarAttr) {
        return true;
      }
    }
    return false;

  }

  /**
   * @param grpdAttr instance
   * @param preDefndAttr instance
   * @return boolean
   */
  public boolean checkIfGrpdAttrsAtPIDCLevel(final IProjectAttribute grpdAttr,
      final PidcVersionAttribute preDefndAttr) {
    return (grpdAttr instanceof PidcVersionAttribute) && !preDefndAttr.isAtChildLevel();

  }
}
