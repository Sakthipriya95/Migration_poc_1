/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrUploadError;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;

/**
 * @author bru2cob
 */
public class EmrUploadErrorLoader extends AbstractBusinessObject<EmrUploadError, TEmrUploadError> {

  /**
   * @param serviceData Service Data
   */
  public EmrUploadErrorLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_UPLOAD_ERR, TEmrUploadError.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrUploadError createDataObject(final TEmrUploadError entity) throws DataException {
    EmrUploadError uploadError = new EmrUploadError();
    uploadError.setErrorMessage(entity.getErrorMessage());
    uploadError.setFileId(entity.getTEmrFile().getEmrFileId());
    uploadError.setId(entity.getErrorId());
    uploadError.setErrorData(entity.getErrorData());
    uploadError.setErrorCategory(entity.getErrorCategory());
    uploadError.setRowNumber(entity.getRowNumber());
    uploadError.setVersion(entity.getVersion());
    return uploadError;
  }

  /**
   * @return the Map of upload errors with id as key
   * @throws DataException DataException
   */
  public Map<Long, EmrUploadError> getAllUploadErrors() throws DataException {
    Map<Long, EmrUploadError> uploadErrorMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrUploadError> tQuery = getEntMgr().createNamedQuery(TEmrUploadError.GET_ALL, TEmrUploadError.class);

    List<TEmrUploadError> dbUploadError = tQuery.getResultList();

    for (TEmrUploadError dbEmrUploadError : dbUploadError) {
      uploadErrorMap.put(dbEmrUploadError.getErrorId(), createDataObject(dbEmrUploadError));
    }
    return uploadErrorMap;
  }

  /**
   * Get the list of errors for specific emr file
   *
   * @param emrFileId emr File Id
   * @return List of EmrUploadError
   * @throws DataException error while loading data
   */
  public List<EmrUploadError> getErrorDataForFile(final Long emrFileId) throws DataException {
    List<EmrUploadError> errorList = new ArrayList<>();
    Collection<EmrUploadError> errorDatas = getAllUploadErrors().values();
    for (EmrUploadError errorData : errorDatas) {
      if (errorData.getFileId().longValue() == emrFileId.longValue()) {
        errorList.add(errorData);
      }
    }
    return errorList;
  }
}
