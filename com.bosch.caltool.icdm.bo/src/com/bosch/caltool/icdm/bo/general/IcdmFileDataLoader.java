/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFileData;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.IcdmFileData;

/**
 * @author bru2cob
 */
public class IcdmFileDataLoader extends AbstractBusinessObject<IcdmFileData, TabvIcdmFileData> {

  /**
   * @param serviceData Service Data
   */
  public IcdmFileDataLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ICDM_FILE_DATA, TabvIcdmFileData.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IcdmFileData createDataObject(final TabvIcdmFileData entity) throws DataException {
    IcdmFileData fileData = new IcdmFileData();
    fileData.setFileData(entity.getFileData());
    fileData.setIcdmFileId(entity.getTabvIcdmFile().getFileId());
    fileData.setId(entity.getFileDataId());
    fileData.setVersion(entity.getVersion());
    return fileData;
  }


  /**
   * @param fileId
   * @return
   * @throws DataException
   */
  public final Set<IcdmFileData> getFileData(final Long fileId) throws DataException {
    Set<IcdmFileData> fileDataSet = new HashSet<>();
    TypedQuery<TabvIcdmFileData> query =
        getEntMgr().createNamedQuery(TabvIcdmFileData.GET_FILE_DATA, TabvIcdmFileData.class);
    query.setParameter("fileId", fileId);
    List<TabvIcdmFileData> fileDataList = query.getResultList();
    if (fileDataList != null) {
      for (TabvIcdmFileData tabvIcdmFileData : fileDataList) {
        fileDataSet.add(createDataObject(tabvIcdmFileData));
      }
    }
    return fileDataSet;
  }


}
