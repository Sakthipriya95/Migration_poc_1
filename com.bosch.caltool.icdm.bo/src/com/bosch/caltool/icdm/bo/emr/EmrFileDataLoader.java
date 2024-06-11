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
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFileData;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.emr.EmrFileData;

/**
 * @author bru2cob
 */
public class EmrFileDataLoader extends AbstractBusinessObject<EmrFileData, TEmrFileData> {

  /**
   * @param serviceData Service Data
   */
  public EmrFileDataLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_FILE_DATA, TEmrFileData.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrFileData createDataObject(final TEmrFileData entity) throws DataException {
    EmrFileData fileData = new EmrFileData();
    fileData.setCategoryId(entity.getTEmrCategory().getCatId());
    fileData.setColId(entity.getTEmrColumn().getColId());
    if (null != entity.getTEmrColumnValue()) {
      fileData.setColValId(entity.getTEmrColumnValue().getColValueId());
    }
    fileData.setEmissionStdProcedureId(entity.getTEmrEmissionStandardProcedure().getEmsId());
    if (null != entity.getTEmrEmissionStandardTestcase()) {
      fileData.setEmissionStdTestcaseId(entity.getTEmrEmissionStandardTestcase().getEmsId());
    }
    fileData.setFileId(entity.getTEmrFile().getEmrFileId());
    fileData.setFuelTypeNumber(entity.getFuelTypeNumber());
    fileData.setId(entity.getFileDataId());
    if (null != entity.getTEmrMeasureUnit()) {
      fileData.setMeasureUnitId(entity.getTEmrMeasureUnit().getMuId());
    }
    fileData.setValueNum(entity.getValueNum());
    fileData.setValueText(entity.getValueText());
    fileData.setVersion(entity.getVersion());
    return fileData;
  }

  /**
   * @return the Map of file data with id as key
   * @throws DataException DataException
   */
  public Map<Long, EmrFileData> getAllFileDatas() throws DataException {
    Map<Long, EmrFileData> fileDataMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrFileData> tQuery = getEntMgr().createNamedQuery(TEmrFileData.GET_ALL, TEmrFileData.class);

    List<TEmrFileData> dbFileData = tQuery.getResultList();

    for (TEmrFileData dbEmrFileData : dbFileData) {
      fileDataMap.put(dbEmrFileData.getFileDataId(), createDataObject(dbEmrFileData));
    }
    return fileDataMap;
  }

  /**
   * Get the file data for specific emr file
   *
   * @param emrFileId emr File Id
   * @return List of EmrFileData
   * @throws DataException error while retrieving data
   */
  public List<EmrFileData> getFileDataForFile(final Long emrFileId) throws DataException {
    List<EmrFileData> fileDataList = new ArrayList<>();
    Collection<EmrFileData> fileDatas = getAllFileDatas().values();
    for (EmrFileData fileData : fileDatas) {
      if (fileData.getFileId().longValue() == emrFileId.longValue()) {
        fileDataList.add(fileData);
      }
    }
    return fileDataList;
  }
}
