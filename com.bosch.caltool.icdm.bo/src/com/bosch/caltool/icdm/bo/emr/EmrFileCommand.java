/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.emr.EmrFile;

/**
 * @author bru2cob
 */
public class EmrFileCommand extends AbstractCommand<EmrFile, EmrFileLoader> {


  /**
   * @param dataProvider
   * @throws IcdmException
   */
  public EmrFileCommand(final ServiceData serviceData, final EmrFile inputFile, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, inputFile, new EmrFileLoader(serviceData), isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    TEmrFile dbEmrFile = new TEmrFile();
    PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());
    IcdmFilesLoader fileLoader = new IcdmFilesLoader(getServiceData());
    dbEmrFile.setDeletedFlag(getInputData().getDeletedFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    dbEmrFile.setDescription(getInputData().getDescription());
    dbEmrFile.setIsVariant(getInputData().getIsVariant() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    dbEmrFile.setLoadedWithoutErrorsFlag(
        getInputData().getLoadedWithoutErrorsFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);

    TabvIcdmFile tabvIcdmFile = fileLoader.getEntityObject(getInputData().getIcdmFileId());
    dbEmrFile.setTabvIcdmFile(tabvIcdmFile);
    Set<TEmrFile> tEmrFiles = tabvIcdmFile.getTEmrFiles();
    if (null == tEmrFiles) {
      tEmrFiles = new HashSet<>();
    }
    tEmrFiles.add(dbEmrFile);

    versionLoader.validateId(getInputData().getPidcVersId());
    TPidcVersion tPidcVersion = versionLoader.getEntityObject(getInputData().getPidcVersId());
    dbEmrFile.setTPidcVersion(tPidcVersion);
    tPidcVersion.addTEmrFile(dbEmrFile);


    setUserDetails(COMMAND_MODE.CREATE, dbEmrFile);

    persistEntity(dbEmrFile);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    EmrFileLoader fileLoader = new EmrFileLoader(getServiceData());
    TEmrFile dbEmrFile = fileLoader.getEntityObject(getInputData().getId());
    dbEmrFile.setDescription(getInputData().getDescription());
    dbEmrFile.setLoadedWithoutErrorsFlag(
        getInputData().getLoadedWithoutErrorsFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    dbEmrFile.setIsVariant(getInputData().getIsVariant() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    dbEmrFile.setDeletedFlag(getInputData().getDeletedFlag() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    setUserDetails(COMMAND_MODE.UPDATE, dbEmrFile);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not Implemented
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not Implemented
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
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Not Implemented
  }


}
