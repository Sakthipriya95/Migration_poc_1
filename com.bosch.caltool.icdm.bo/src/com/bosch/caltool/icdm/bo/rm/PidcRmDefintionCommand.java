/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;

/**
 * @author bne4cob
 */
public class PidcRmDefintionCommand extends AbstractCommand<PidcRmDefinition, PidcRmDefinitionLoader> {


  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  public PidcRmDefintionCommand(final ServiceData serviceData, final PidcRmDefinition inputDef) throws IcdmException {
    super(serviceData, inputDef, new PidcRmDefinitionLoader(serviceData), COMMAND_MODE.CREATE);
  }

  @Override
  public void create() throws IcdmException {

    TPidcRmDefinition dbDef = new TPidcRmDefinition();
    PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion dbPidcVersion = versionLoader.getEntityObject(getInputData().getPidcVersId());
    dbDef.setTPidcVersion(dbPidcVersion);

    dbDef.setRmDescEng(getInputData().getRmDescEng());

    dbDef.setRmDescGer(getInputData().getRmDescGer());

    dbDef.setRmNameEng(getInputData().getRmNameEng());

    dbDef.setRmNameGer(getInputData().getRmNameGer());

    if (getInputData().getIsVariant() != null) {
      dbDef.setIsVariant(getInputData().getIsVariant());
    }
    Set<TPidcRmDefinition> tPidcRmDefs = dbPidcVersion.gettPidcRmDefinitions();
    if (tPidcRmDefs == null) {
      tPidcRmDefs = new HashSet<>();
      dbPidcVersion.settPidcRmDefinitions(tPidcRmDefs);
    }
    dbPidcVersion.gettPidcRmDefinitions().add(dbDef);

    // Currently the var and sub var are not sey
    setUserDetails(COMMAND_MODE.CREATE, dbDef);

    persistEntity(dbDef);
  }


  @Override
  protected void delete() {
    // No action
  }

  /**
   * {@inheritDoc}
   *
   * @throws IcdmException IcdmException
   */
  @Override
  protected void update() throws IcdmException {
    PidcRmDefinitionLoader versLoader = new PidcRmDefinitionLoader(getServiceData());
    TPidcRmDefinition dbDef = versLoader.getEntityObject(getInputData().getId());
    dbDef.setRmDescEng(getInputData().getRmDescEng());

    dbDef.setRmDescGer(getInputData().getRmDescGer());

    dbDef.setRmNameEng(getInputData().getRmNameEng());

    dbDef.setRmNameGer(getInputData().getRmNameGer());

    if (getInputData().getIsVariant() != null) {
      dbDef.setIsVariant(getInputData().getIsVariant());
    }

    setUserDetails(COMMAND_MODE.UPDATE, dbDef);

  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException when object is not found
   */
  @Override
  protected void doPostCommit() throws DataException {
    // Not action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
