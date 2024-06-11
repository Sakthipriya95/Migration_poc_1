/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmProjectCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;

/**
 * @author rgo7cob
 */
public class PidcRmProjCharCommand extends AbstractCommand<PidcRmProjCharacter, PidcRmProjCharacterLoader> {


  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  public PidcRmProjCharCommand(final ServiceData serviceData, final PidcRmProjCharacter inputDef, final boolean update)
      throws IcdmException {
    super(serviceData, inputDef, new PidcRmProjCharacterLoader(serviceData),
        update ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE);
  }


  @Override
  public void create() throws IcdmException {

    TPidcRmProjectCharacter dbpidRmProjChar = new TPidcRmProjectCharacter();

    dbpidRmProjChar.setRelevantFlag(getInputData().getRelevant());
    PidcRmDefinitionLoader pidRmDefLoader = new PidcRmDefinitionLoader(getServiceData());

    TPidcRmDefinition tpidcRmDefintion = pidRmDefLoader.getEntityObject(getInputData().getRmDefId());
    dbpidRmProjChar.setTPidcRmDefinition(tpidcRmDefintion);
    // set it to the Rm defintion
    if (tpidcRmDefintion.getTPidcRmProjectCharacters() == null) {
      Set<TPidcRmProjectCharacter> rmProjCharSet = new HashSet<>();
      rmProjCharSet.add(dbpidRmProjChar);
      tpidcRmDefintion.setTPidcRmProjectCharacters(rmProjCharSet);
    }
    else {
      tpidcRmDefintion.addTPidcRmProjectCharacter(dbpidRmProjChar);
    }
    RmCategoryLoader categoryLoader = new RmCategoryLoader(getServiceData());
    dbpidRmProjChar.setRbShare(categoryLoader.getEntityObject(getInputData().getRbShareId()));
    dbpidRmProjChar.setRbData(categoryLoader.getEntityObject(getInputData().getRbDataId()));

    RmProjectCharacterLoader projCharLoader = new RmProjectCharacterLoader(getServiceData());

    dbpidRmProjChar.setTRmProjectCharacter(projCharLoader.getEntityObject(getInputData().getProjCharId()));

    // Currently the var and sub var are not sey
    setUserDetails(COMMAND_MODE.CREATE, dbpidRmProjChar);

    persistEntity(dbpidRmProjChar);
  }


  @Override
  protected void delete() {
    // No action
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  protected void update() throws IcdmException {
    PidcRmProjCharacterLoader versLoader = new PidcRmProjCharacterLoader(getServiceData());
    TPidcRmProjectCharacter dbpidRmProjChar = versLoader.getEntityObject(getInputData().getId());
    dbpidRmProjChar.setRelevantFlag(getInputData().getRelevant());
    PidcRmDefinitionLoader pidRmDefLoader = new PidcRmDefinitionLoader(getServiceData());

    dbpidRmProjChar.setTPidcRmDefinition(pidRmDefLoader.getEntityObject(getInputData().getRmDefId()));

    RmCategoryLoader categoryLoader = new RmCategoryLoader(getServiceData());
    dbpidRmProjChar.setRbShare(categoryLoader.getEntityObject(getInputData().getRbShareId()));
    dbpidRmProjChar.setRbData(categoryLoader.getEntityObject(getInputData().getRbDataId()));

    RmProjectCharacterLoader projCharLoader = new RmProjectCharacterLoader(getServiceData());

    dbpidRmProjChar.setTRmProjectCharacter(projCharLoader.getEntityObject(getInputData().getProjCharId()));

    setUserDetails(COMMAND_MODE.UPDATE, dbpidRmProjChar);
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException when object is not found
   */
  @Override
  protected void doPostCommit() throws DataException {
    // No action
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
