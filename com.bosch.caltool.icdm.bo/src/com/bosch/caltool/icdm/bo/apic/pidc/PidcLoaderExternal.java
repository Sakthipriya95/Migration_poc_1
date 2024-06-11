/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.CharacteristicLoader;
import com.bosch.caltool.icdm.bo.uc.UcpAttrLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributesV2;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;


/**
 * @author bne4cob
 */
public class PidcLoaderExternal extends PidcLoader {

  /**
   * @param serviceData Service Data
   */
  public PidcLoaderExternal(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param pidcId PIDC Id
   * @param ucIdSet set of use case IDs
   * @param pidcVersID PIDC Version id
   * @return PIDC Details
   * @throws IcdmException various errors
   */
  public PidcVersionWithAttributes getProjectIdCardWithAttrs(final Long pidcId, final Set<Long> ucIdSet,
      final Long pidcVersID)
      throws IcdmException {

    PidcVersionAttributeModel model =
        (new ProjectAttributeLoader(getServiceData())).createModelByPidc(pidcId, pidcVersID);

    // PIDC version info, level attributes
    PidcVersionInfo pidcVersWithStruct =
        (new PidcVersionLoader(getServiceData())).getPidcVersionWithStructureAttributes(model);
    if (pidcVersWithStruct == null) {
      // Level attribute is hidden to current user
      throw new UnAuthorizedAccessException("The PIDC with Id " + pidcId + " is not accessible to current user");
    }
    PidcVersionWithAttributes ret = new PidcVersionWithAttributes();
    ret.setPidcVersionInfo(pidcVersWithStruct);

    // Fetch use case attributes
    boolean ucAttrFetch = !CommonUtils.isNullOrEmpty(ucIdSet);
    Set<Long> ucAttrSet = new HashSet<>();
    if (ucAttrFetch) {
      ucAttrSet.addAll(getUcAttrs(ucIdSet));
    }

    // Fill PIDC version level attributes
    fillPidcVersAttrs(ret, model, ucAttrFetch, ucAttrSet);

    // Variants and attributes
    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantMap = new HashMap<>();

    // Variant and sub variant ids map
    Map<Long, Set<Long>> varWithSubVarIds = new HashMap<>();
    // sub variants and attributes
    Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantMap = new HashMap<>();

    ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> varObj;
    for (PidcVariant variant : model.getVariantMap().values()) {
      varObj = new ProjectObjectWithAttributes<>();
      varObj.setProjectObject(variant);

      // Get Pidc Variant level attributes
      fillVarAttrs(model, ucAttrFetch, ucAttrSet, varObj, variant);

      variantMap.put(variant.getId(), varObj);
      // Task 269417
      createSubVarMapWithAttr(variant, model, ucAttrFetch, ucAttrSet, subVariantMap, varWithSubVarIds);
    }

    ret.setVariantMap(variantMap);
    ret.setVarWithSubVarIds(varWithSubVarIds);
    ret.setSubVariantMap(subVariantMap);


    if (ucAttrFetch) {
      Map<Long, Attribute> attrMapToAdd = new HashMap<>();
      Map<Long, AttributeValue> valMapToAdd = new HashMap<>();

      // Exclude attributes and values not relevant to use case and level attributes
      Set<Long> attrToChkSet = new HashSet<>(ucAttrSet);
      ret.getPidcVersionInfo().getLevelAttrMap().values().forEach(pidcAttr -> attrToChkSet.add(pidcAttr.getAttrId()));

      // Find relevant attributes
      model.getRelevantAttributeMap().forEach((attrId, attr) -> {
        if (attrToChkSet.contains(attrId)) {
          attrMapToAdd.put(attrId, attr);
        }
      });

      // Find relevant values
      model.getRelevantAttrValueMap().values().forEach(val -> {
        if (attrToChkSet.contains(val.getAttributeId())) {
          valMapToAdd.put(val.getId(), val);
        }
      });

      ret.setAttributeMap(attrMapToAdd);
      ret.setAttributeValueMap(valMapToAdd);
    }
    else {
      ret.setAttributeMap(model.getRelevantAttributeMap());
      ret.setAttributeValueMap(model.getRelevantAttrValueMap());
    }
    return ret;
  }

  /**
   * @param pidcId PIDC Id
   * @param ucIdSet set of use case IDs
   * @param pidcVersID PIDC Version id
   * @return PIDC Details
   * @throws IcdmException various errors
   */
  public PidcVersionWithAttributesV2 getProjectIdCardV2WithAttrs(final Long pidcId, final Set<Long> ucIdSet,
      final Long pidcVersID)
      throws IcdmException {
    PidcVersionWithAttributesV2 pidcAttrV2 = new PidcVersionWithAttributesV2();
    PidcVersionWithAttributes pidcWithAttrs = getProjectIdCardWithAttrs(pidcId, ucIdSet, pidcVersID);
    setPidcAttrFields(pidcAttrV2, pidcWithAttrs);
    CharacteristicLoader characteristicLoader = new CharacteristicLoader(getServiceData());
    Map<Long, Attribute> attributeMap = pidcWithAttrs.getAttributeMap();
    Map<Long, Characteristic> charMap = new HashMap<>();
    for (Attribute attr : attributeMap.values()) {
      if (null != attr.getCharacteristicId()) {
        Characteristic characteristic = characteristicLoader.getDataObjectByID(attr.getCharacteristicId());
        charMap.put(characteristic.getId(), characteristic);
      }
    }
    pidcAttrV2.setCharacteristicMap(charMap);
    return pidcAttrV2;
  }


  /**
   * @param pidcAttrV2 {@link PidcVersionWithAttributesV2} object
   * @param pidcWithAttrs {@link PidcVersionWithAttributes} object
   */
  private void setPidcAttrFields(final PidcVersionWithAttributesV2 pidcAttrV2,
      final PidcVersionWithAttributes pidcWithAttrs) {
    pidcAttrV2.setAttributeMap(pidcWithAttrs.getAttributeMap());
    pidcAttrV2.setAttributeValueMap(pidcWithAttrs.getAttributeValueMap());
    pidcAttrV2.setPidcAttributeMap(pidcWithAttrs.getPidcAttributeMap());
    pidcAttrV2.setPidcVersionInfo(pidcWithAttrs.getPidcVersionInfo());
    pidcAttrV2.setSubVariantMap(pidcWithAttrs.getSubVariantMap());
    pidcAttrV2.setVariantMap(pidcWithAttrs.getVariantMap());
    pidcAttrV2.setVarWithSubVarIds(pidcWithAttrs.getVarWithSubVarIds());
  }

  /**
   * Task 269417
   *
   * @param model PidcVersionAttributeModel
   * @param variant PidcVariant
   * @param ret
   * @param ucAttrFetch
   * @param ucAttrSet
   * @param subVariantObjMap
   * @param varWithSubVarIds
   */
  private void createSubVarMapWithAttr(final PidcVariant variant, final PidcVersionAttributeModel model,
      final boolean ucAttrFetch, final Set<Long> ucAttrSet,
      final Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantObjMap,
      final Map<Long, Set<Long>> varWithSubVarIds) {

    Map<Long, PidcSubVariant> subVariantMap = model.getSubVariantMap(variant.getId());
    // create set of sub variant ids
    Set<Long> subVarIds = new HashSet<>();
    subVarIds.addAll(subVariantMap.keySet());
    // add the set to the map with variant id
    varWithSubVarIds.put(variant.getId(), subVarIds);
    ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> svarObj;

    for (Entry<Long, PidcSubVariant> subVar : subVariantMap.entrySet()) {
      svarObj = new ProjectObjectWithAttributes<>();
      svarObj.setProjectObject(subVar.getValue());

      // Get Pidc sub Variant level attributes
      fillSubVarAttrs(model, ucAttrFetch, ucAttrSet, svarObj);

      subVariantObjMap.put(subVar.getKey(), svarObj);

    }

  }


  /**
   * Task 269417
   *
   * @param model
   * @param ucAttrFetch
   * @param ucAttrSet
   * @param svarObj
   */
  private void fillSubVarAttrs(final PidcVersionAttributeModel model, final boolean ucAttrFetch,
      final Set<Long> ucAttrSet, final ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute> svarObj) {
    Map<Long, PidcSubVariantAttribute> svarAttrMap =
        model.getSubVariantAttributeMap(svarObj.getProjectObject().getId());
    if (ucAttrFetch) {
      Map<Long, PidcSubVariantAttribute> mapToAdd = new HashMap<>();
      ucAttrSet.forEach(attrId -> {
        PidcSubVariantAttribute varAttr = svarAttrMap.get(attrId);
        if (varAttr != null) {
          mapToAdd.put(attrId, varAttr);
        }
      });
      svarObj.setProjectAttrMap(mapToAdd);
    }
    else {
      svarObj.setProjectAttrMap(svarAttrMap);
    }
  }


  private void fillPidcVersAttrs(final PidcVersionWithAttributes ret, final PidcVersionAttributeModel model,
      final boolean ucAttrFetch, final Set<Long> ucAttrSet) {
    Map<Long, PidcVersionAttribute> pidcVersAttrMap = model.getPidcVersAttrMap();
    if (ucAttrFetch) {
      Map<Long, PidcVersionAttribute> mapToAdd = new HashMap<>();
      ucAttrSet.forEach(attrId -> {
        PidcVersionAttribute versAttr = pidcVersAttrMap.get(attrId);
        if (versAttr != null) {
          mapToAdd.put(attrId, versAttr);
        }
      });
      ret.setPidcAttributeMap(mapToAdd);
    }
    else {
      // Get PIDC Version level attributes
      ret.setPidcAttributeMap(pidcVersAttrMap);
    }
  }

  private void fillVarAttrs(final PidcVersionAttributeModel model, final boolean ucAttrFetch, final Set<Long> ucAttrSet,
      final ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute> varObj, final PidcVariant variant) {

    Map<Long, PidcVariantAttribute> varAttrMap = model.getVariantAttributeMap(variant.getId());
    if (ucAttrFetch) {
      Map<Long, PidcVariantAttribute> mapToAdd = new HashMap<>();
      ucAttrSet.forEach(attrId -> {
        PidcVariantAttribute varAttr = varAttrMap.get(attrId);
        if (varAttr != null) {
          mapToAdd.put(attrId, varAttr);
        }
      });
      varObj.setProjectAttrMap(mapToAdd);
    }
    else {
      varObj.setProjectAttrMap(varAttrMap);
    }
  }

  private Set<Long> getUcAttrs(final Set<Long> ucIdSet) throws DataException {
    Set<Long> ucAttrSet = new HashSet<>();
    UcpAttrLoader ucpAttrLoader = new UcpAttrLoader(getServiceData());
    for (Long ucId : ucIdSet) {
      ucAttrSet.addAll(ucpAttrLoader.getMappedAttributesUseCase(ucId));
    }
    getLogger().debug("Total attributes linked to the input use cases = {}", ucAttrSet.size());
    return ucAttrSet;
  }


  public ExternalPidcVersionWithAttributes setExtPidcVersWithDetails(final PidcVersionWithAttributes versAttr) {
    ExternalPidcVersionWithAttributes extPidcWithAttrs = new ExternalPidcVersionWithAttributes();

    extPidcWithAttrs.setAttributeMap(versAttr.getAttributeMap());
    extPidcWithAttrs.setAttributeValueMap(versAttr.getAttributeValueMap());
    extPidcWithAttrs.setPidcAttributeMap(versAttr.getPidcAttributeMap());
    extPidcWithAttrs.setSubVariantMap(versAttr.getSubVariantMap());
    extPidcWithAttrs.setVariantMap(versAttr.getVariantMap());
    extPidcWithAttrs.setVarWithSubVarIds(versAttr.getVarWithSubVarIds());

    extPidcWithAttrs.setPidcVersionInfo(
        (new PidcVersionLoader(getServiceData())).setExtPidcVersInfoDetails(versAttr.getPidcVersionInfo()));
    return extPidcWithAttrs;
  }


}
