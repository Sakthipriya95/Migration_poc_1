/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrUploadError;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;

/**
 * @author bru2cob
 */
public class EmrUploadErrorCommand extends AbstractCommand<EmrUploadError, EmrUploadErrorLoader> {

  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  protected EmrUploadErrorCommand(final ServiceData serviceData, final EmrUploadError inputDef, final boolean isCreate)
      throws IcdmException {
    super(serviceData, inputDef, new EmrUploadErrorLoader(serviceData),
        isCreate ? COMMAND_MODE.CREATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TEmrUploadError uploadError = new TEmrUploadError();
    uploadError.setErrorMessage(getInputData().getErrorMessage());
    uploadError.setErrorData(getInputData().getErrorData());
    uploadError.setErrorCategory(getInputData().getErrorCategory());
    uploadError.setRowNumber(getInputData().getRowNumber());
    TEmrFile tEmrFile = new EmrFileLoader(getServiceData()).getEntityObject(getInputData().getFileId());
    uploadError.setTEmrFile(tEmrFile);
    tEmrFile.addTEmrUploadError(uploadError);
    setUserDetails(COMMAND_MODE.CREATE, uploadError);

    persistEntity(uploadError);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TEmrUploadError entity = new EmrUploadErrorLoader(getServiceData()).getEntityObject(getInputData().getId());

    TEmrFile emrFile = new EmrFileLoader(getServiceData()).getEntityObject(entity.getTEmrFile().getEmrFileId());
    emrFile.getTEmrUploadErrors().remove(entity);
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
    // TODO Auto-generated method stub
    return false;
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
