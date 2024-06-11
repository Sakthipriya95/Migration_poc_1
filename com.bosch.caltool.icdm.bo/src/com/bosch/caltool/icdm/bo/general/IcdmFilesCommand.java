/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.model.general.IcdmFiles;;

/**
 * @author bru2cob
 */
public class IcdmFilesCommand extends AbstractCommand<IcdmFiles, IcdmFilesLoader> {


  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  public IcdmFilesCommand(final ServiceData serviceData, final IcdmFiles inputDef, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, inputDef, new IcdmFilesLoader(serviceData), isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData
   * @param inputDef
   * @param isUpdate
   * @param isDelete
   * @throws IcdmException
   */
  public IcdmFilesCommand(final ServiceData serviceData, final IcdmFiles inputDef, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, inputDef, new IcdmFilesLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvIcdmFile icdmFile = new TabvIcdmFile();
    icdmFile.setFileCount(getInputData().getFileCount());
    icdmFile.setFileName(getInputData().getName());
    icdmFile.setNodeId(getInputData().getNodeId());
    icdmFile.setNodeType(getInputData().getNodeType());
    setUserDetails(COMMAND_MODE.CREATE, icdmFile);

    persistEntity(icdmFile);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(getServiceData());
    TabvIcdmFile dbIcdmFile = fileLoader.getEntityObject(getInputData().getId());
    dbIcdmFile.setNodeId(getInputData().getNodeId());
    setUserDetails(COMMAND_MODE.UPDATE, dbIcdmFile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    IcdmFilesLoader loader = new IcdmFilesLoader(getServiceData());
    TabvIcdmFile entity = loader.getEntityObject(getInputData().getId());
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }
}
