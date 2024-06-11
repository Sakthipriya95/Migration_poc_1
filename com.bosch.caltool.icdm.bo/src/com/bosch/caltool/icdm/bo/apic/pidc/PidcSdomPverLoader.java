/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;


/**
 * @author bne4cob
 */
public class PidcSdomPverLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData Service Data
   */
  public PidcSdomPverLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Get all SDOM PVER names associated with the given PIDC version
   *
   * @param pidcVersId PIDC version ID
   * @return set of SDOM PVER names
   * @throws IcdmException error while retrieving data
   */
  public SortedSet<String> getSdomPverNamesByPidcVers(final Long pidcVersId) throws IcdmException {
    getLogger().debug("Fetching SDOM PVER names for pidc version {}...", pidcVersId);

    SortedSet<String> retSet = new TreeSet<>();

    PidcVersionAttribute sdomPverAttr =
        new PidcVersionAttributeLoader(getServiceData()).getSDOMPverAttribute(pidcVersId);

    if (null != sdomPverAttr) {
      if (sdomPverAttr.isAtChildLevel()) {
        // SDOM Project attribute defined at Variant level
        getLogger().debug("SDOM PVER of pidc version '{}' configured at variant level", pidcVersId);

        Map<Long, PidcVariant> variantMap = new PidcVariantLoader(getServiceData()).getVariants(pidcVersId, false);


        PidcVariantAttributeLoader varAttrLoader = new PidcVariantAttributeLoader(getServiceData());


        retSet = variantMap.values().stream().map(pidcVariant -> {
          try {
            return varAttrLoader.getSDOMPverAttribute(pidcVariant.getId());
          }
          catch (DataException e) {
            getLogger().error(e.getMessage(), e);
          }
          return null;
        }).filter(va -> (va != null) && (va.getValueId() != null)).map(PidcVariantAttribute::getValue)
            .collect(Collectors.toCollection(TreeSet::new));

      }
      else {
        // SDOM Project attribute defined at PIDC Version level
        getLogger().debug("SDOM PVER of pidc version '{}' is defined at PIDC level/not defined", pidcVersId);
        if (null != sdomPverAttr.getValueId()) {
          retSet.add(sdomPverAttr.getValue());
        }
      }
    }

    getLogger().debug("SDOM PVERS identified for pidc version '{}' = {}. List : {}", pidcVersId, retSet.size(), retSet);

    return retSet;
  }


  /**
   * Get SDOM PVERs by PIDC
   *
   * @param pidcId PIDC ID
   * @return the Map of pver names associated with a PIDC. Key - pidc version ID, value set of SDOM-PVER names
   * @throws IcdmException - error during data retrieval
   */
  public Map<Long, SortedSet<String>> getSdomPverByPidc(final Long pidcId) throws IcdmException {
    Map<Long, SortedSet<String>> retMap = new HashMap<>();

    Map<Long, PidcVersion> pidcVerMap = new PidcVersionLoader(getServiceData()).getAllPidcVersions(pidcId);

    SortedSet<String> retSet;
    for (Long pidcVersionId : pidcVerMap.keySet()) {
      retSet = getSdomPverNamesByPidcVers(pidcVersionId);
      retMap.put(pidcVersionId, retSet);
      getLogger().info("Number of sdom pvers found = {}", retSet.size());
    }

    return retMap;
  }

  /**
   * Get all SDOM PVER associated with the given PIDC version
   *
   * @param pidcVersId PIDC version ID
   * @return Set of SDOM PVERs
   * @throws IcdmException error while retrieving data
   */
  public SortedSet<SdomPVER> getSdomPverByPidcVers(final Long pidcVersId) throws IcdmException {
    getLogger().debug("Fetching SDOM PVERs for pidc version {}...", pidcVersId);

    PidcVersionAttributeModel projModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS);

    final Long spAttrId =
        new AttributeLoader(getServiceData()).getLevelAttrId(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR));
    PidcVersionAttribute spVersAttr = projModel.getPidcVersAttr(spAttrId);

    Set<Long> spValIdSet = new HashSet<>();
    if (spVersAttr.isAtChildLevel()) {
      // SDOM Project attribute defined at Variant level
      getLogger().debug("SDOM PVER of pidc version '{}' configured at variant level", pidcVersId);

      spValIdSet = projModel.getVariantMap().keySet().stream().map(vId -> projModel.getVariantAttribute(vId, spAttrId))
          .filter(Objects::nonNull).map(PidcVariantAttribute::getValueId).filter(Objects::nonNull)
          .collect(Collectors.toSet());

    }
    else {
      // SDOM Project attribute defined at PIDC Version level
      getLogger().debug("SDOM PVER of pidc version '{}' defined at PIDC level/not defined", pidcVersId);
      if (null != spVersAttr.getValueId()) {
        spValIdSet.add(spVersAttr.getValueId());
      }
    }

    getLogger().debug("SDOM PVER attribute values identified : {}", spValIdSet);

    SortedSet<SdomPVER> retSet = spValIdSet.stream().map(projModel.getRelevantAttrValueMap()::get)
        .map(v -> createDataObject(projModel.getPidcVersion(), v)).collect(Collectors.toCollection(TreeSet::new));

    getLogger().debug("SDOM PVERS identified for pidc version '{}' = {}. List : {}", pidcVersId, retSet.size(), retSet);

    return retSet;
  }


  /**
   * Create an SDOM PVER data object
   */
  private SdomPVER createDataObject(final PidcVersion pidcVersion, final AttributeValue attrVal) {
    SdomPVER sdomPver = new SdomPVER();
    sdomPver.setPverName(attrVal.getName());
    sdomPver.setDescription(attrVal.getDescription());
    sdomPver.setPidcVersion(pidcVersion);
    sdomPver.setSdomPverAttrVal(attrVal);

    return sdomPver;
  }

  /**
   * Get the SDOM PVER for the given PIDC/Variant
   *
   * @param pidcVersId PIDC Version ID
   * @param variantId Variant ID
   * @return SDOM PVER name
   * @throws IcdmException error while retrieving data
   */
  public String getSdomPverName(final Long pidcVersId, final Long variantId) throws IcdmException {

    getLogger().debug("Fetching SDOM PVER for PIDC Version '{}' and variant '{}'", pidcVersId, variantId);

    PidcVersionAttributeModel projModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS);

    final Long spAttrId =
        new AttributeLoader(getServiceData()).getLevelAttrId(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR));
    PidcVersionAttribute spVersAttr = projModel.getPidcVersAttr(spAttrId);

    String retPverName = "";

    if (spVersAttr.isAtChildLevel()) {
      // SDOM Project attribute defined at Variant level
      getLogger().debug("SDOM PVER of pidc version '{}' is defined at variant level", pidcVersId);

      if (variantId == null) {
        throw new InvalidInputException("SDOM PVER attribute is defined at variant level. Variant ID is mandatory");
      }

      PidcVariantAttribute spVarAttr = projModel.getVariantAttribute(variantId, spAttrId);
      if ((spVarAttr != null) && (spVarAttr.getValueId() != null)) {
        retPverName = spVarAttr.getValue();
      }

    }
    else {
      // SDOM Project attribute defined at PIDC Version level
      getLogger().debug("SDOM PVER of pidc version '{}' is defined at PIDC level/not defined", pidcVersId);
      if (null != spVersAttr.getValueId()) {
        retPverName = spVersAttr.getValue();
      }
    }

    getLogger().info("SDOM PVER for PIDC Version '{}' and variant '{}' is : {}", pidcVersId, variantId, retPverName);

    return retPverName;
  }

}
