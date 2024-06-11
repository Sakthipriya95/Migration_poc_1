/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.IcdmFiles;

/**
 * @author bru2cob
 */
public class IcdmFilesLoader extends AbstractBusinessObject<IcdmFiles, TabvIcdmFile> {

  /**
   * @param serviceData Service Data
   */
  public IcdmFilesLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.ICDM_FILE, TabvIcdmFile.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IcdmFiles createDataObject(final TabvIcdmFile entity) throws DataException {
    IcdmFiles icdmFile = new IcdmFiles();
    icdmFile.setFileCount(entity.getFileCount());
    icdmFile.setId(entity.getFileId());
    icdmFile.setName(entity.getFileName());
    icdmFile.setNodeId(entity.getNodeId());
    icdmFile.setNodeType(entity.getNodeType());
    icdmFile.setCreatedDate(entity.getCreatedDate());
    icdmFile.setVersion(entity.getVersion());
    return icdmFile;
  }

  /**
   * The method returns the unzipped files as a map, with key as the file name including the relative path, value as the
   * file as byte array.
   *
   * @param icdmFileId icdm file id
   * @return the unzipped files as a map
   * @throws DataException icdm exception
   */
  public final Map<String, byte[]> getFiles(final Long icdmFileId) throws DataException {

    final byte[] zippedFile = getEntityObject(icdmFileId).getTabvIcdmFileData().getFileData();
    try {
      return ZipUtils.unzip(zippedFile);
    }
    catch (IOException e) {
      throw new DataException(e.getCause().getMessage(), e);
    }
  }

  /**
   * Get the file, using node id and type
   *
   * @param nodeId node id
   * @param nodeType type
   * @return Map of file. Key - file name, value - file object
   * @throws DataException error
   */
  public final Map<String, byte[]> getFiles(final Long nodeId, final String nodeType) throws DataException {

    TypedQuery<TabvIcdmFile> query = getEntMgr().createNamedQuery(TabvIcdmFile.GET_FILE, TabvIcdmFile.class);
    query.setParameter("nodeId", nodeId);
    query.setParameter("nodeType", nodeType);
    List<TabvIcdmFile> resList = query.getResultList();
    if (!resList.isEmpty()) {
      TabvIcdmFile entity = resList.get(0);
      return getFiles(entity.getFileId());
    }

    throw new InvalidInputException("Invalid nodeId or type - Node ID = " + nodeId + "; Type = " + nodeType);

  }


}
