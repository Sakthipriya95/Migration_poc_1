/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrPidcVariant;
import com.bosch.caltool.icdm.database.entity.apic.emr.VEmrDetailsAll;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrData;
import com.bosch.caltool.icdm.model.emr.EmrDataExternalResponse;
import com.bosch.caltool.icdm.model.emr.EmrFileDetails;
import com.bosch.caltool.icdm.model.emr.EmsEmrVariants;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author dja7cob
 */
public class EmrDataExternalFetcher extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData serviceData
   */
  public EmrDataExternalFetcher(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Validates inputs and fetches the EMR sheet data
   *
   * @param pidcVersId pidc version id
   * @param variantId pidc variant id
   * @return EmrFileContentResponse
   * @throws IcdmException Exception in getting response
   */
  public EmrDataExternalResponse fetchEmrDataExternal(final Long pidcVersId, final Long variantId)
      throws IcdmException {
    getLogger().debug("Fetching EMR sheet data for PIDC Version ID : {}, PIDC Variant ID : {}", pidcVersId, variantId);

    validateInputs(pidcVersId, variantId);

    EmrDataExternalResponse response = fetchEmrDataExternalResponse(pidcVersId, variantId);

    getLogger().debug(
        "EMR sheet data retrieved completed successfully. Number of EMR file detail records : {}, Number of EMR Sheet Data records : {}",
        response.getEmrFileDetailsMap().size(), response.getEmrDataMap().size());
    return response;
  }

  /**
   * Validates whether the pidc version id, pidc variant id (if available in input) are valid. Also validates whether
   * the input pidc variant belongs to the input pidc version
   *
   * @param pidcVersId pidc version id
   * @param variantId pidc variant id
   * @throws IcdmException invalid input
   */
  private void validateInputs(final Long pidcVersId, final Long variantId) throws IcdmException {
    getLogger().debug("Validating inputs...");

    // Validate Pidc version id
    new PidcVersionLoader(getServiceData()).validateId(pidcVersId);

    // Validate variant id
    if (null != variantId) {
      PidcVariant pidcVariant = new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId);
      if (!pidcVersId.equals(pidcVariant.getPidcVersionId())) {
        throw new IcdmException(
            "Input PIDC Variant ID : " + variantId + " does not belong to input PIDC Version ID : " + pidcVersId);
      }
    }

    // Check access rights
    authenticateUser(pidcVersId);

    getLogger().debug("Input validation successful. PIDC Version ID : {}. PIDC Variant ID : {}", pidcVersId, variantId);
  }

  /**
   * @param pidcVersId
   * @throws IcdmException
   */
  private void authenticateUser(final Long pidcVersId) throws IcdmException {
    boolean hasAccessToEmr = isPidcVersionReadable(pidcVersId) || hasEmrAccess();
    if (!hasAccessToEmr) {
      throw new UnAuthorizedAccessException(
          "Insufficient access rights to view EMR Data for PIDC Version with ID : " + pidcVersId);
    }
  }

  /**
   * @return
   * @throws IcdmException
   */
  private boolean hasEmrAccess() throws IcdmException {
    try {
      boolean isEmrAdmin = new NodeAccessLoader(getServiceData()).isCurrentUserRead(
          Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.EMR_NODE_ID)));

      getLogger().debug("Is User EMR Admin : {}", isEmrAdmin);

      return isEmrAdmin;
    }
    catch (NumberFormatException e) {
      throw new IcdmException(e.getMessage(), e);
    }
  }

  /**
   * @param pidcVersId
   * @return
   * @throws DataException
   */
  private boolean isPidcVersionReadable(final Long pidcVersId) throws DataException {

    boolean hasPidcReadAccess =
        new ApicAccessRightLoader(getServiceData()).isCurrentUserApicWrite() || new NodeAccessLoader(getServiceData())
            .isCurrentUserRead(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersId).getPidcId());

    getLogger().debug("User has read access to PIDC : {}", hasPidcReadAccess);

    return hasPidcReadAccess;
  }

  /**
   * Fetches the EMR sheet data and constructs the response of the service
   *
   * @param pidcVersId pidc version id
   * @param variantId pidc variant id
   * @return EmrFileContentResponse response with EMR sheet data
   * @throws DataException Exception in fetching EMR file content response
   */
  private EmrDataExternalResponse fetchEmrDataExternalResponse(final Long pidcVersId, final Long variantId)
      throws DataException {
    EmrDataExternalResponse response = new EmrDataExternalResponse();

    response.setEmrFileDetailsMap(fetchEmrFileDetails(pidcVersId, variantId));
    response.setEmsEmrVariantsMap(fetchEmsVariantDetails(pidcVersId, variantId));
    response.setEmrDataMap(fetchEmrFileDataMap(pidcVersId, variantId));

    return response;
  }

  /**
   * @param pidcVersId
   * @param variantId
   * @return
   */
  private Map<Long, Map<Long, EmsEmrVariants>> fetchEmsVariantDetails(final Long pidcVersId, final Long variantId) {

    Map<Long, Map<Long, EmsEmrVariants>> emsVariantMap = new HashMap<>();

    getLogger().debug("Fetching EMS and variant Details...");

    TypedQuery<TEmrPidcVariant> tQuery;
    if (null != variantId) {
      tQuery = getEntMgr().createNamedQuery(TEmrPidcVariant.GET_BY_PIDC_VARIANT, TEmrPidcVariant.class);
      tQuery.setParameter(TEmrPidcVariant.QP_VARIANT_ID, variantId);
    }
    else {
      tQuery = getEntMgr().createNamedQuery(TEmrPidcVariant.GET_BY_PIDC_VERSION, TEmrPidcVariant.class);
      tQuery.setParameter(TEmrPidcVariant.QP_PIDC_VERS_ID, pidcVersId);
    }

    List<TEmrPidcVariant> resultList = tQuery.getResultList();
    getLogger().debug("EMR Pidc Variant Details retrieved successfully. Number of records : {}", resultList.size());

    for (TEmrPidcVariant emrPidcVar : resultList) {

      long emrFileId = emrPidcVar.getTEmrFile().getEmrFileId();
      long emsId = emrPidcVar.getTEmrEmissionStandard().getEmsId();

      emsVariantMap.computeIfAbsent(emrFileId, newMap -> new HashMap<Long, EmsEmrVariants>())
          .computeIfAbsent(emsId, newEms -> initialiseEmsDetails(emrFileId, emsId)).getVariantIds()
          .add(emrPidcVar.getTabvProjectVariant().getVariantId());
    }

    getLogger().debug("EMS and variant Details added to Response Map");

    return emsVariantMap;
  }

  /**
   * @param emrFileId
   * @param emsId
   * @return
   */
  private EmsEmrVariants initialiseEmsDetails(final long emrFileId, final long emsId) {
    EmsEmrVariants emsDetails = new EmsEmrVariants();
    emsDetails.setEmrFileId(emrFileId);
    emsDetails.setEmsId(emsId);
    emsDetails.setVariantIds(new HashSet<>());
    return emsDetails;
  }

  /**
   * Fetches the EMR file contents from V_EMR_DETAILS_ALL based on the pidc variant (if available in input) / pidc
   * version
   *
   * @param pidcVersId pidc version id
   * @param variantId pidc variant id
   * @return Map of EMR file contents key - EMR file id; Value - Map of < key - EMR file data id, Value - EMR file
   *         contents)
   */
  private Map<Long, Map<Long, EmrData>> fetchEmrFileDataMap(final Long pidcVersId, final Long variantId) {

    getLogger().debug("Fetching EMR File Contents...");

    TypedQuery<VEmrDetailsAll> tQuery;
    if (null != variantId) {
      tQuery = getEntMgr().createNamedQuery(VEmrDetailsAll.NQ_GET_BY_PIDC_VAR_ID, VEmrDetailsAll.class);
      tQuery.setParameter(VEmrDetailsAll.QP_VARIANT_ID, variantId);
    }
    else {
      tQuery = getEntMgr().createNamedQuery(VEmrDetailsAll.NQ_GET_BY_PIDC_VERS_ID, VEmrDetailsAll.class);
      tQuery.setParameter(VEmrDetailsAll.QP_PIDC_VERS_ID, pidcVersId);
    }

    List<VEmrDetailsAll> resultList = tQuery.getResultList();
    getLogger().debug("EMR File Data retrieved successfully. Number of records : {}", resultList.size());
    return fillEmrDataMap(resultList);
  }

  /**
   * Fills the EMR content map in the service response
   *
   * @param resultList list of EMR file contents from DB
   * @return EMR file contents map
   */
  private Map<Long, Map<Long, EmrData>> fillEmrDataMap(final List<VEmrDetailsAll> resultList) {
    Map<Long, Map<Long, EmrData>> retMap = new HashMap<>();

    for (VEmrDetailsAll emrDetailEntity : resultList) {
      retMap.computeIfAbsent(emrDetailEntity.getEmrFileId(), newMap -> new HashMap<Long, EmrData>())
          .put(emrDetailEntity.getFileDataId(), createEmrDataDataObj(emrDetailEntity));
    }
    getLogger().debug("EMR File Contents added to response map");
    return retMap;
  }

  /**
   * Fetches the EMR file details from T_EMR_FILE based on the pidc version / pidc variant (if available in input)
   *
   * @param pidcVersId pidc version id
   * @param variantId pidc variant id
   * @return Map of key - emr file id, value - EmrFileDetails object
   * @throws DataExceptionExcpetion in creating EmrFileDetails object
   */
  private Map<Long, Set<EmrFileDetails>> fetchEmrFileDetails(final Long pidcVersId, final Long variantId)
      throws DataException {

    getLogger().debug("Fetching EMR File Details...");

    TypedQuery<TEmrFile> tQuery;
    if (null != variantId) {
      tQuery = getEntMgr().createNamedQuery(TEmrFile.GET_BY_PIDC_VARIANT, TEmrFile.class);
      tQuery.setParameter(TEmrFile.QP_VARIANT_ID, variantId);
    }
    else {
      tQuery = getEntMgr().createNamedQuery(TEmrFile.GET_BY_PIDC_VERSION, TEmrFile.class);
      tQuery.setParameter(TEmrFile.QP_PIDC_VERS_ID, pidcVersId);
    }

    List<TEmrFile> resultList = tQuery.getResultList();
    getLogger().debug("EMR File Details retrieved successfully. Number of records : {}", resultList.size());

    Map<Long, Set<EmrFileDetails>> emrFileDetailsMap = new HashMap<>();
    for (TEmrFile emrFileEntity : resultList) {
      emrFileDetailsMap.put(emrFileEntity.getEmrFileId(), getEmrFileDetailsSet(emrFileEntity, variantId));
    }

    getLogger().debug("EMR File Details added to Response Map");

    return emrFileDetailsMap;
  }

  /**
   * @param variantId
   * @param emrFileEntity
   * @param variantId
   * @return
   * @throws DataException
   */
  private Set<EmrFileDetails> getEmrFileDetailsSet(final TEmrFile emrFileEntity, final Long variantId)
      throws DataException {
    if (null != variantId) {
      return new HashSet<>(Arrays.asList(createEmrFileDetailsObj(emrFileEntity, variantId)));
    }

    Set<TEmrPidcVariant> tEmrPidcVariants = emrFileEntity.getTEmrPidcVariants();
    if (ApicConstants.CODE_NO.equals(emrFileEntity.getIsVariant()) || tEmrPidcVariants.isEmpty()) {
      return new HashSet<>(Arrays.asList(createEmrFileDetailsObj(emrFileEntity, null)));
    }

    Set<EmrFileDetails> retSet = new HashSet<>();
    for (TEmrPidcVariant emrPidcvar : tEmrPidcVariants) {
      retSet.add(createEmrFileDetailsObj(emrFileEntity, emrPidcvar.getTabvProjectVariant().getVariantId()));
    }
    return retSet;
  }


  /**
   * Creates EMR file content object
   *
   * @param vEmrDetailsEntity Entity object
   * @return EmrFileContents object
   */
  private EmrData createEmrDataDataObj(final VEmrDetailsAll vEmrDetailsEntity) {
    EmrData emrFileContent = new EmrData();

    emrFileContent.setEmrFileId(vEmrDetailsEntity.getEmrFileId());
    emrFileContent.setEmrFileDataId(vEmrDetailsEntity.getFileDataId());
    emrFileContent.setCategoryName(vEmrDetailsEntity.getCatName());
    emrFileContent.setColumnName(vEmrDetailsEntity.getColumnName());
    emrFileContent.setColValue(vEmrDetailsEntity.getColValue());
    emrFileContent
        .setValueNum(null == vEmrDetailsEntity.getValueNum() ? null : vEmrDetailsEntity.getValueNum().toString());
    emrFileContent.setValueText(vEmrDetailsEntity.getValueText());
    emrFileContent.setMeasureUnitName(vEmrDetailsEntity.getMeasureUnitName());
    emrFileContent.setEmsId(vEmrDetailsEntity.getEmsId());
    emrFileContent.setEmissionStandardName(vEmrDetailsEntity.getEmissionStandardName());
    emrFileContent.setTestcaseName(vEmrDetailsEntity.getTestcaseName());
    emrFileContent.setTestIdentifier(vEmrDetailsEntity.getTestIdentifier());
    return emrFileContent;
  }

  /**
   * Creates EMR file details object
   *
   * @param emrFileEntity Entity object
   * @param variantId input pidc variant id
   * @return EmrFileDetails object
   * @throws DataException
   */
  private EmrFileDetails createEmrFileDetailsObj(final TEmrFile emrFileEntity, final Long variantId)
      throws DataException {
    EmrFileDetails emrFileDetails = new EmrFileDetails();

    emrFileDetails.setEmrFileId(emrFileEntity.getEmrFileId());
    emrFileDetails.setEmrFileName(emrFileEntity.getTabvIcdmFile().getFileName());
    emrFileDetails.setPidcVersId(emrFileEntity.getTPidcVersion().getPidcVersId());
    emrFileDetails.setPidcVersName(new PidcVersionLoader(getServiceData())
        .getDataObjectByID(emrFileEntity.getTPidcVersion().getPidcVersId()).getName());

    if (null != variantId) {
      PidcVariant variant = new PidcVariantLoader(getServiceData()).getDataObjectByID(variantId);
      emrFileDetails.setVariantId(variant.getId());
      emrFileDetails.setVariantName(variant.getName());
    }

    emrFileDetails.setLoadedWithoutErrorsFlag(yOrNToBoolean(emrFileEntity.getLoadedWithoutErrorsFlag()));
    emrFileDetails.setDeletedFlag(yOrNToBoolean(emrFileEntity.getDeletedFlag()));

    return emrFileDetails;
  }
}
