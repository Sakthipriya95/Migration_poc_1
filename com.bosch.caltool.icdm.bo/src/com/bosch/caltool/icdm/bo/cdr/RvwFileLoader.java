/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFile;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RvwFile;


/**
 * Loader class for Review Files
 *
 * @author bru2cob
 */
public class RvwFileLoader extends AbstractBusinessObject<RvwFile, TRvwFile> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwFileLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RES_FILE, TRvwFile.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwFile createDataObject(final TRvwFile entity) throws DataException {
    RvwFile object = new RvwFile();

    setCommonFields(object, entity);

    object.setResultId(entity.getTRvwResult() == null ? null : entity.getTRvwResult().getResultId());
    object.setFileId(entity.getTabvIcdmFile().getFileId());
    object.setRvwParamId(entity.getTRvwParameter() == null ? null : entity.getTRvwParameter().getRvwParamId());
    object.setFileType(entity.getFileType());
    object.setName(entity.getTabvIcdmFile().getFileName());
    object.setNodeType(entity.getTabvIcdmFile().getNodeType());
    return object;
  }

  /**
   * The method returns the unzipped files as a map, with key as the file name including the relative path, value as the
   * file as byte array.
   *
   * @param icdm file id
   * @return the unzipped files as a map
   * @throws DataException icdm exception
   */
  public final Map<String, byte[]> getFiles(final TabvIcdmFile tabvIcdmFile) throws DataException {
    if ((tabvIcdmFile != null) && (tabvIcdmFile.getTabvIcdmFileData() != null)) {
      final byte[] zippedFile = tabvIcdmFile.getTabvIcdmFileData().getFileData();
      try {
        return ZipUtils.unzip(zippedFile);
      }
      catch (IOException e) {
        throw new DataException(e.getCause().getMessage(), e);
      }
    }
    return null;
  }

  /**
   * Get Review Files records using ResultId
   *
   * @return Map. Key - id, Value - RvwFile object
   * @throws DataException error while retrieving data
   */
  public Map<Long, RvwFile> getByResultId(final TRvwResult entityObject) throws DataException {
    Map<Long, RvwFile> objMap = new ConcurrentHashMap<>();
    Set<TRvwFile> dbObj = entityObject.getTRvwFiles();
    for (TRvwFile entity : dbObj) {
      objMap.put(entity.getRvwFileId(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * Get Review Files records using ResultId
   *
   * @return Map. Key - id, Value - RvwFile object
   * @throws DataException error while retrieving data
   */
  public Map<String, RvwFile> getFileNamesByResultId(final TRvwResult entityObject) throws DataException {
    Map<String, RvwFile> objMap = new ConcurrentHashMap<>();
    Set<TRvwFile> dbObj = entityObject.getTRvwFiles();
    for (TRvwFile entity : dbObj) {
      objMap.put(entity.getTabvIcdmFile().getFileName(), createDataObject(entity));
    }
    return objMap;
  }

  /**
   * @param entity
   * @return
   */
  public Map<Long, List<RvwFile>> getParamFiles(final TRvwParameter dbParam) throws DataException {
    Set<TRvwFile> tRvwFiles = dbParam.getTRvwFiles();
    Map<Long, List<RvwFile>> icdmFilesMap = new ConcurrentHashMap<>();
    for (TRvwFile entity : tRvwFiles) {
      List<RvwFile> files = icdmFilesMap.get(dbParam.getRvwParamId());
      if (null == files) {
        files = new ArrayList<>();
        icdmFilesMap.put(dbParam.getRvwParamId(), files);
      }
      files.add(createDataObject(entity));
    }
    return icdmFilesMap;

  }

  /**
   * Get Review Files records using ResultId
   *
   * @param entityObject review result
   * @return Map. Key - File type, Value - RvwFile object
   * @throws DataException error while retrieving data
   */
  public Map<String, List<RvwFile>> getFiles(final TRvwResult entityObject) throws DataException {
    Set<TRvwFile> tRvwFiles = entityObject.getTRvwFiles();
    Map<String, List<RvwFile>> icdmFilesMap = new ConcurrentHashMap<>();
    for (TRvwFile entity : tRvwFiles) {
      String fileType = entity.getFileType();
      List<RvwFile> files = icdmFilesMap.get(fileType);
      if (null == files) {
        files = new ArrayList<>();
        icdmFilesMap.put(fileType, files);
      }
      files.add(createDataObject(entity));
    }
    return icdmFilesMap;
  }

  /**
   * Returns byte array of file data.
   *
   * @param fileId Rvw file Id
   * @return byte array of file
   * @throws DataException dataException
   */
  public byte[] getRvwFile(final Long fileId) throws DataException {
    TRvwFile rvwFile = getEntityObject(fileId);
    if (rvwFile != null) {
      IcdmFilesLoader fileLdr = new IcdmFilesLoader(getServiceData());
      Map<String, byte[]> files = fileLdr.getFiles(rvwFile.getTabvIcdmFile().getFileId());
      if (CommonUtils.isNotEmpty(files)) {
        for (String key : files.keySet()) {
          return files.get(key);
        }
      }
    }
    return null;
  }
}
