/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrEmissionStandard;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrFile;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrPidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;

/**
 * @author bru2cob
 */
public class EmrPidcVariantCommand extends AbstractCommand<EmrPidcVariant, EmrPidcVariantLoader> {

  /**
   * @param serviceData service Data
   * @param dataObjectByID input Definition
   * @throws IcdmException error when initializing
   */
  public EmrPidcVariantCommand(final ServiceData serviceData, final EmrPidcVariant dataObjectByID)
      throws IcdmException {
    super(serviceData, dataObjectByID, new EmrPidcVariantLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData service Data
   * @param dataObjectByID input Definition
   * @param isDelete boolean
   * @throws IcdmException error when initializing
   */
  public EmrPidcVariantCommand(final ServiceData serviceData, final EmrPidcVariant dataObjectByID,
      final boolean isDelete) throws IcdmException {
    super(serviceData, dataObjectByID, new EmrPidcVariantLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TEmrPidcVariant dbEpVar = new TEmrPidcVariant();
    dbEpVar.setEmrVariant(getInputData().getEmrVariant());

    PidcVariantLoader pidcVariantloader = new PidcVariantLoader(getServiceData());
    pidcVariantloader.validateId(getInputData().getPidcVariantId());
    pidcVariantloader.getEntityObject(getInputData().getPidcVariantId()).addTEmrPidcVariant(dbEpVar);
    new EmrEmissionStandardLoader(getServiceData()).getEntityObject(getInputData().getEmissionStdId())
        .addTEmrPidcVariant(dbEpVar);
    new EmrFileLoader(getServiceData()).getEntityObject(getInputData().getEmrFileId()).addTEmrPidcVariant(dbEpVar);
    setUserDetails(COMMAND_MODE.CREATE, dbEpVar);
    persistEntity(dbEpVar);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not yet implemented
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TEmrPidcVariant entity = new EmrPidcVariantLoader(getServiceData()).getEntityObject(getInputData().getId());

    TabvProjectVariant projVar =
        new PidcVariantLoader(getServiceData()).getEntityObject(entity.getTabvProjectVariant().getVariantId());
    projVar.getTEmrPidcVariants().remove(entity);

    TEmrEmissionStandard emrEmissionStd =
        new EmrEmissionStandardLoader(getServiceData()).getEntityObject(entity.getTEmrEmissionStandard().getEmsId());
    emrEmissionStd.getTEmrPidcVariants().remove(entity);

    TEmrFile emrFile = new EmrFileLoader(getServiceData()).getEntityObject(entity.getTEmrFile().getEmrFileId());
    emrFile.getTEmrPidcVariants().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not yet Implemented
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
    // Not yet Implemented
  }
}