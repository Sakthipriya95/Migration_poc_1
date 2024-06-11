/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrPidcVariant;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrUploadError;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;

/**
 * @author bru2cob
 */
public class EmrFileLoader extends AbstractBusinessObject<EmrFile, TEmrFile> {

  /**
   * @param serviceData Service Data
   */
  public EmrFileLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_FILE, TEmrFile.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrFile createDataObject(final TEmrFile entity) throws DataException {
    EmrFile file = new EmrFile();
    file.setDeletedFlag(ApicConstants.CODE_YES.equalsIgnoreCase(entity.getDeletedFlag()));
    file.setDescription(entity.getDescription());
    file.setIcdmFileId(entity.getTabvIcdmFile().getFileId());
    file.setId(entity.getEmrFileId());
    file.setIsVariant(ApicConstants.CODE_YES.equalsIgnoreCase(entity.getIsVariant()));
    file.setLoadedWithoutErrorsFlag(ApicConstants.CODE_YES.equalsIgnoreCase(entity.getLoadedWithoutErrorsFlag()));
    file.setPidcVersId(entity.getTPidcVersion().getPidcVersId());
    file.setCreatedDate(entity.getCreatedDate());
    file.setVersion(entity.getVersion());
    file.setName(entity.getTabvIcdmFile().getFileName());
    return file;
  }

  /**
   * Returns set of EMR file-variant mapping
   *
   * @param pidVersId pid versid
   * @return the map of emr files pidc variant mapping
   * @throws DataException DataException
   */

  public Map<Long, EmrFileMapping> getEmrFileVariantMapping(final Long pidVersId) throws DataException {
    Map<Long, EmrFileMapping> emrFileMappingMap = new HashMap<>();

    // PidcVersion Loader
    PidcVersionLoader loader = new PidcVersionLoader(getServiceData());
    TPidcVersion pidcVersionObj = loader.getEntityObject(pidVersId);

    Set<TEmrFile> dbFiles = pidcVersionObj.getTEmrFiles();

    // PidcVariant Loader
    if (CommonUtils.isNotEmpty(dbFiles)) {
      PidcVariantLoader variantLdr = new PidcVariantLoader(getServiceData());
      for (TEmrFile emrFileEntity : dbFiles) {
        EmrFileMapping emrFileMapping = new EmrFileMapping();
        emrFileMapping.setEmrFile(createDataObject(emrFileEntity));
        emrFileMapping.setFileUpload(emrFileEntity.getTabvIcdmFile().getTabvIcdmFileData() != null);

        if (CommonUtils.isNotEmptyString(emrFileEntity.getIsVariant()) &&
            (CommonUtilConstants.CODE_YES).equals(emrFileEntity.getIsVariant())) {
          getPidcVariants(variantLdr, emrFileEntity, emrFileMapping);
        }
        else {
          emrFileMapping.getVariantSet().addAll(variantLdr.getVariants(pidVersId, false).values());
        }
        emrFileMappingMap.put(emrFileMapping.getEmrFile().getId(), emrFileMapping);
      }
    }
    return emrFileMappingMap;
  }

  /**
   * @param variantLdr
   * @param emrFileEntity
   * @param emrFileMapping
   * @throws DataException
   */
  private void getPidcVariants(final PidcVariantLoader variantLdr, final TEmrFile emrFileEntity,
      final EmrFileMapping emrFileMapping)
      throws DataException {
    Set<TEmrPidcVariant> variants = emrFileEntity.getTEmrPidcVariants();
    for (TEmrPidcVariant dbvariant : variants) {
      PidcVariant variant = variantLdr.getDataObjectByID(dbvariant.getTabvProjectVariant().getVariantId());
      if (!variant.isDeleted()) {
        emrFileMapping.getVariantSet().add(variant);
      }
    }
  }


  /**
   * @param emrFileId Long
   * @return List<EmrUploadError>
   * @throws DataException DataException
   */
  public List<EmrUploadError> getEMRFileUploadErrors(final Long emrFileId) throws DataException {
    TEmrFile tEmrFile = getEntityObject(emrFileId);
    List<EmrUploadError> errorList = new ArrayList<>();
    EmrUploadErrorLoader errorLoader = new EmrUploadErrorLoader(getServiceData());
    for (TEmrUploadError tUploadError : tEmrFile.getTEmrUploadErrors()) {
      errorList.add(errorLoader.createDataObject(tUploadError));
    }

    return errorList;
  }

  /**
   * Returns byte array of file data.
   *
   * @param fileId EMR file Id
   * @return byte array of file
   * @throws DataException dataException
   */
  public byte[] getEmrFile(final Long fileId) throws DataException {
    byte[] fileData = null;
    TEmrFile emrFile = getEntityObject(fileId);
    if (emrFile != null) {
      IcdmFilesLoader fileLdr = new IcdmFilesLoader(getServiceData());
      Map<String, byte[]> files = fileLdr.getFiles(emrFile.getTabvIcdmFile().getFileId());
      Optional<byte[]> data = files.values().stream().findFirst();
      if (data.isPresent()) {
        fileData = data.get();
      }
    }
    return fileData;
  }
}
