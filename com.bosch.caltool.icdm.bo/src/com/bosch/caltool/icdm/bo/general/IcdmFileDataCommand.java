/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFileData;
import com.bosch.caltool.icdm.model.general.IcdmFileData;

/**
 * @author bru2cob
 */
public class IcdmFileDataCommand extends AbstractCommand<IcdmFileData, IcdmFileDataLoader> {


  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  public IcdmFileDataCommand(final ServiceData serviceData, final IcdmFileData inputDef) throws IcdmException {
    super(serviceData, inputDef, new IcdmFileDataLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData Service Data
   * @param inputData inputData
   * @param isUpdate is update
   * @param isDelete is delete
   * @throws IcdmException error
   */
  public IcdmFileDataCommand(final ServiceData serviceData, final IcdmFileData inputData, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, inputData, new IcdmFileDataLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {


    TabvIcdmFileData fileData = new TabvIcdmFileData();

    TabvIcdmFile entityObject = new IcdmFilesLoader(getServiceData()).getEntityObject(getInputData().getIcdmFileId());
    fileData.setTabvIcdmFile(entityObject);
    entityObject.setTabvIcdmFileData(fileData);

    byte[] zippedFileBytes = null;
    try {
      final Map<String, byte[]> fileMap = new HashMap<>();
      fileMap.put(entityObject.getFileName(), getInputData().getFileData());
      zippedFileBytes = ZipUtils.createZip(fileMap);

    }
    catch (IOException e) {
      throw new CommandException(e.getMessage(), e);
    }
    fileData.setFileData(zippedFileBytes);

    persistEntity(fileData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    IcdmFileDataLoader loader = new IcdmFileDataLoader(getServiceData());
    TabvIcdmFileData entity = loader.getEntityObject(getInputData().getId());
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    // Not required
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // Not relevant as this command will only be invoked as a child command
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Not required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isRelevantForCns() {
    // Changes by File data command is not relevant for CNS
    return false;
  }


}
