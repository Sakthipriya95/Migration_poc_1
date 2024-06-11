/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lFileinfo;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.PidcA2lDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * @author and4cob
 */
public class PidcA2lDetailsByAPRJLoader extends AbstractSimpleBusinessObject {

  private AttributeLoader attrLdr;
  /**
   * 'PVER name in SDOM' attribute ID
   */
  private Long sdomPverAttrId;

  /**
   * 'APRJ name in vCDM' attribute ID
   */
  private Long aprjNameAttrId;

  private final Set<Long> pidcVersSetWithoutVcdmTransfer = new HashSet<>();

  /**
   * @param serviceData Service Data
   */
  public PidcA2lDetailsByAPRJLoader(final ServiceData serviceData) {
    super(serviceData);
    initialize();
  }

  /**
   *
   */
  private void initialize() {
    this.attrLdr = new AttributeLoader(getServiceData());
    try {
      this.aprjNameAttrId = this.attrLdr.getLevelAttrId(Long.valueOf(ApicConstants.VCDM_APRJ_NAME_ATTR));

      this.sdomPverAttrId = this.attrLdr.getLevelAttrId(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR));
    }
    catch (DataException e) {
      getLogger().error(e.getMessage(), e);
    }
  }

  /**
   * Get the pidc A2L details from vcdm APRJ name, variant name and vcdm A2l file ID
   *
   * @param vcdmA2lFileId - vcdm a2l file id
   * @param variantName - input variant name
   * @param aprjName - aprj name
   * @return data checker model map containing the pidc A2L details with Key as PIDC Version ID
   * @throws IcdmException - error while retreiving
   */
  public Map<Long, PidcA2lDetails> getPidcDetails(final String aprjName, final String variantName,
      final Long vcdmA2lFileId)
      throws IcdmException {

    getLogger().info("Inputs : aprjName = {}, variantName = {}, vcdmA2lFileId = {}", aprjName, variantName,
        vcdmA2lFileId);

    getLogger().debug("Checking if APRJ Name is available...");

    Long aprjNameValueId = null;
    List<TabvAttrValue> tabvAttrValList = this.attrLdr.getEntityObject(this.aprjNameAttrId).getTabvAttrValues();
    for (TabvAttrValue tabvAttrValue : tabvAttrValList) {
      if (aprjName.equals(tabvAttrValue.getTextvalueEng())) {
        aprjNameValueId = tabvAttrValue.getValueId();
        break;
      }
    }

    if (aprjNameValueId == null) {
      throw new InvalidInputException("APRJ Name '" + aprjName + "' not found in iCDM");
    }

    getLogger().debug("APRJ name value ID in iCDM is : {}", aprjNameValueId);

    getLogger().debug("Fetching MvTa2lFileinfo objects for vCDM A2L File ID '{}'...", vcdmA2lFileId);

    // query to fetch the contents of TA2L_FileInfo for the given vCDM A2L FileID
    TypedQuery<MvTa2lFileinfo> ta2lFileInfoQuery =
        getEntMgr().createNamedQuery(MvTa2lFileinfo.NNQ_A2L_FILEINFO_BY_A2L_FILE_ID, MvTa2lFileinfo.class);
    ta2lFileInfoQuery.setParameter(1, vcdmA2lFileId);
    List<MvTa2lFileinfo> a2lFileInfoList = ta2lFileInfoQuery.getResultList();

    if (CommonUtils.isNullOrEmpty(a2lFileInfoList)) {
      throw new InvalidInputException("vCDM a2l file with ID '" + vcdmA2lFileId + "' not found");
    }

    getLogger().debug("MvTa2lFileinfo objects fetched = {}", a2lFileInfoList.size());

    TypedQuery<Long> pidcVerQry = getEntMgr().createNamedQuery(TPidcVersion.NQ_GET_PIDC_VERS_BY_APRJ_N_A2L, Long.class);

    List<Long> a2lFileInfoIdList =
        a2lFileInfoList.stream().map(a2lFileInfo -> Long.valueOf(a2lFileInfo.getId())).collect(Collectors.toList());
    pidcVerQry.setParameter("a2lFileId", a2lFileInfoIdList);
    pidcVerQry.setParameter("aprjValId", aprjNameValueId);// value id for Aprj Name attribute

    List<Long> pidcVersIdList = pidcVerQry.getResultList();

    // add pidcversion list to hashset to avoid duplicate entries
    Set<Long> pidcVersIdSet = new HashSet<>(pidcVersIdList);

    getLogger().debug("PIDC Version IDs fetched = {}, IDs = {}", pidcVersIdSet.size(), pidcVersIdSet);

    Map<Long, PidcA2lDetails> pidcA2lDetailsMap =
        getMappedPidcA2lDetailsFromPidcVersionList(variantName, a2lFileInfoList, pidcVersIdSet);

    getLogger().info("Validating vCDM transfer for created PIDC elements..");
    validateVCDMTransferIfEmptyResponse(pidcA2lDetailsMap, pidcVersIdSet);

    return pidcA2lDetailsMap;

  }

  /**
   * This method throws exception if none of the mapped pidc versions have been transferred to vCDM
   *
   * @param pidcA2lDetailsMap collected pidc A2L Details map
   * @param pidcVersIdSet identified pidc versions mapped to aprj name
   * @throws IcdmException
   */
  private void validateVCDMTransferIfEmptyResponse(final Map<Long, PidcA2lDetails> pidcA2lDetailsMap,
      final Set<Long> pidcVersIdSet)
      throws IcdmException {

    if (pidcA2lDetailsMap.isEmpty() && CommonUtils.isNotEmpty(pidcVersIdSet) &&
        (pidcVersIdSet.size() == this.pidcVersSetWithoutVcdmTransfer.size())) {
      throw new IcdmException("vCDM Transfer was not done from iCDM for the given APRJ.");
    }
  }

  /**
   * @param variantName
   * @param a2lFileInfoList
   * @param pidcVersIdSet
   * @param retMap
   * @throws IcdmException
   */
  private Map<Long, PidcA2lDetails> getMappedPidcA2lDetailsFromPidcVersionList(final String variantName,
      final List<MvTa2lFileinfo> a2lFileInfoList, final Set<Long> pidcVersIdSet)
      throws IcdmException {

    PidcLoader pidcLoader = new PidcLoader(getServiceData());

    Map<Long, PidcA2lDetails> retMap = new HashMap<>();

    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    for (Long pidcVersId : pidcVersIdSet) {

      getLogger().debug("Checking PIDC Version : {}...", pidcVersId);

      PidcVersion pidcVersion = pidcVersLoader.getDataObjectByID(pidcVersId);

      Long aprjId = pidcLoader.getDataObjectByID(pidcVersion.getPidcId()).getAprjId();

      // check for a2l mappings in pidc versions only if pidc has been transferred to vcdm,
      if (aprjId == null) {
        getLogger().warn("vCDM transfer has not happened from this PIDC Version. PIDC elements cannot be identified");
        this.pidcVersSetWithoutVcdmTransfer.add(pidcVersId);
      }
      else {
        PidcVersionAttributeModel pidcVersionAttributeModel =
            new ProjectAttributeLoader(getServiceData()).createModel(pidcVersId, LOAD_LEVEL.L3_VAR_ATTRS);

        if (variantName == null) {
          if (!pidcVersionAttributeModel.getVariantMap().isEmpty()) {
            // if variant name is not given as input and pidc version has variants
            getLogger().debug("Variant map is not empty for {}", pidcVersion.getId());
            continue;
          }
          // if 'PVER name in SDOM' is defined at pidc version level
          checkSdomPverAttrAtLevels(pidcVersionAttributeModel, retMap, a2lFileInfoList, variantName);
        }
        else {
          // if 'PVER name in SDOM' is defined at variant level
          checkSdomPverAttrAtLevels(pidcVersionAttributeModel, retMap, a2lFileInfoList, variantName);
        }


      }
    }

    getLogger().info("PIDC Elements found = {}. Element IDs : {}", retMap.size(), retMap.keySet());

    return retMap;
  }


  /**
   * @param pidcVersionAttributeModel - pidcVersionAttributeModel
   * @param a2lDetailsMap - response map
   * @param mvTa2lFileinfo - entity object
   * @param variantName - input variant name
   * @throws DataException
   */
  private void checkSdomPverAttrAtLevels(final PidcVersionAttributeModel pidcVersionAttributeModel,
      final Map<Long, PidcA2lDetails> a2lDetailsMap, final List<MvTa2lFileinfo> mvTa2lFileinfoList,
      final String variantName)
      throws DataException {

    PidcVersionAttribute pidcVersionAttribute = pidcVersionAttributeModel.getPidcVersAttr(this.sdomPverAttrId);
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());

    if (pidcVersionAttribute.isAtChildLevel()) {

      getLogger().debug("Pidc Version - {} : PVER name in SDOM is defined at Variant Level",
          pidcVersionAttribute.getPidcVersId());

      // check at variant level
      for (MvTa2lFileinfo mvTa2lFileinfo : mvTa2lFileinfoList) {

        PidcA2l pidcA2l =
            pidcA2lLoader.getPidcA2l(mvTa2lFileinfo.getId(), pidcVersionAttributeModel.getPidcVersion().getPidcId());

        if ((pidcA2l != null) && pidcA2l.isActive() &&
            CommonUtils.isEqual(pidcA2l.getPidcVersId(), pidcVersionAttributeModel.getPidcVersion().getId())) {
          checkAtVarLevel(pidcVersionAttributeModel, a2lDetailsMap, mvTa2lFileinfo, variantName, pidcA2l);
        }

      }
    }
    else {
      getLogger().debug("Pidc Version - {} : PVER name in SDOM is defined at Pidc Version Level",
          pidcVersionAttribute.getPidcVersId());

      for (MvTa2lFileinfo mvTa2lFileinfo : mvTa2lFileinfoList) {

        PidcA2l pidcA2l =
            pidcA2lLoader.getPidcA2l(mvTa2lFileinfo.getId(), pidcVersionAttributeModel.getPidcVersion().getPidcId());

        if ((pidcA2l != null) && pidcA2l.isActive() &&
            CommonUtils.isEqual(pidcA2l.getPidcVersId(), pidcVersionAttributeModel.getPidcVersion().getId()) &&
            mvTa2lFileinfo.getSdomPverName().equals(pidcVersionAttribute.getValue())) {

          PidcA2lDetails pidcA2lDetails =
              createPidcA2lDetailsModel(pidcVersionAttributeModel.getPidcVersion(), null, mvTa2lFileinfo, pidcA2l);
          a2lDetailsMap.put(pidcVersionAttributeModel.getPidcVersion().getId(), pidcA2lDetails);

        }
      }
    }
    getLogger().info("PIDC A2L Details map size : {}", a2lDetailsMap.size());
  }

  /**
   * @param pidcVersAttrModel - pidcVersionAttributeModel
   * @param a2lDetailsMap - response map
   * @param ta2lFileinfo - entity object
   * @param variantName - input variant name
   * @throws DataException
   */
  private void checkAtVarLevel(final PidcVersionAttributeModel pidcVersAttrModel,
      final Map<Long, PidcA2lDetails> a2lDetailsMap, final MvTa2lFileinfo ta2lFileinfo, final String variantName,
      final PidcA2l pidcA2l) {

    for (PidcVariant pidcVariant : pidcVersAttrModel.getVariantMap().values()) {

      Map<Long, PidcVariantAttribute> pidcVariantAttrMap =
          pidcVersAttrModel.getVariantAttributeMap(pidcVariant.getId());
      PidcVariantAttribute pidcVariantAttribute = pidcVariantAttrMap.get(this.sdomPverAttrId);

      if (variantName.equalsIgnoreCase(pidcVariant.getName()) &&
          CommonUtils.isEqual(pidcVariantAttribute.getValue(), ta2lFileinfo.getSdomPverName())) {
        getLogger().debug("PVER name in SDOM is defined at variant level");
        PidcA2lDetails pidcA2lDetails =
            createPidcA2lDetailsModel(pidcVersAttrModel.getPidcVersion(), pidcVariant, ta2lFileinfo, pidcA2l);
        a2lDetailsMap.put(pidcVariant.getId(), pidcA2lDetails);
      }
    }
  }

  /**
   * @param pidcVersion - pidc version object
   * @param mvTa2lFileinfo - entity object
   * @return curPidcElement
   * @throws DataException
   */
  private PidcA2lDetails createPidcA2lDetailsModel(final PidcVersion pidcVersion, final PidcVariant variant,
      final MvTa2lFileinfo mvTa2lFileinfo, final PidcA2l pidcA2l) {

    PidcA2lDetails pidcA2lDetails = new PidcA2lDetails();

    pidcA2lDetails.setPidcVersionId(pidcVersion.getId().longValue());
    pidcA2lDetails.setPverName(mvTa2lFileinfo.getSdomPverName());
    pidcA2lDetails.setPidcVersionName(pidcVersion.getName());
    pidcA2lDetails.setPverVariant(mvTa2lFileinfo.getSdomPverVariant());
    pidcA2lDetails.setPverRevision(mvTa2lFileinfo.getSdomPverRevision());
    pidcA2lDetails.setPidcA2lId(pidcA2l.getId());
    pidcA2lDetails.setPidcA2lName(pidcA2l.getName());

    if (variant != null) {
      pidcA2lDetails.setPidcVariantId(variant.getId());
      pidcA2lDetails.setPidcVariantName(variant.getName());
    }
    getLogger().debug("PIDC A2L Details created - Pidc version id : {}, Pidc variant id : {}, Pidc A2L Id : {}",
        pidcA2lDetails.getPidcVersionId(), pidcA2lDetails.getPidcVariantId(), pidcA2lDetails.getPidcA2lId());
    return pidcA2lDetails;
  }

}
