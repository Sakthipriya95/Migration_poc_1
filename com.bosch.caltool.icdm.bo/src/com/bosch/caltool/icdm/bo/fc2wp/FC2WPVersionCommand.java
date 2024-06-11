/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;

/**
 * @author bne4cob
 */
public class FC2WPVersionCommand extends AbstractCommand<FC2WPVersion, FC2WPVersionLoader> {

  private Long refFcwpDefId;

  /**
   * Constructor for creating a FC2WP Version
   *
   * @param serviceData service Data
   * @param inputData FC2WPVersion to create
   * @throws IcdmException any exception
   */
  public FC2WPVersionCommand(final ServiceData serviceData, final FC2WPVersion inputData) throws IcdmException {
    super(serviceData, inputData, new FC2WPVersionLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * Constructor for updating/delete a FC2WP Version
   *
   * @param serviceData service Data
   * @param inputData FC2WPVersion to update
   * @param update if true updates the mapping
   * @throws IcdmException any exception
   */
  public FC2WPVersionCommand(final ServiceData serviceData, final FC2WPVersion inputData, final boolean update)
      throws IcdmException {
    super(serviceData, inputData, new FC2WPVersionLoader(serviceData),
        update ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TFc2wpDefVersion dbVers = new TFc2wpDefVersion();

    dbVers.setDescEng(getInputData().getDescEng());
    dbVers.setDescGer(getInputData().getDescGer());

    if (getInputData().isWorkingSet()) {
      dbVers.setInWorkFlag(CommonUtilConstants.CODE_YES);
      dbVers.setDescEng(FC2WPVersionLoader.WORKING_SET);
      dbVers.setMajorVersionNum(0L);
      dbVers.setMinorVersionNum(0L);
    }
    else {
      dbVers.setMajorVersionNum(getInputData().getMajorVersNo());
      dbVers.setMinorVersionNum(getInputData().getMinorVersNo());
    }

    dbVers.setActiveFlag(getInputData().isActive() ? CommonUtilConstants.CODE_YES : null);
    dbVers.setArchReleaseSdom(getInputData().getArchReleaseSdom());

    TFc2wpDefinition dbFcWpDef =
        (new FC2WPDefLoader(getServiceData())).getEntityObject(getInputData().getFcwpDefId());
    dbFcWpDef.addTFc2wpDefVersion(dbVers);


    setUserDetails(COMMAND_MODE.CREATE, dbVers);

    persistEntity(dbVers);

    if (!getInputData().isWorkingSet() || (this.refFcwpDefId != null)) {
      copyMappings();
    }

    if (getInputData().isActive()) {
      resetExistingActiveVersion(dbVers);
    }
  }

  /**
   * @throws IcdmException
   */
  private void copyMappings() throws IcdmException {
    Long refDefId =
        (this.refFcwpDefId == null) || (this.refFcwpDefId == 0L) ? getInputData().getFcwpDefId() : this.refFcwpDefId;

    FC2WPVersionLoader versLoader = new FC2WPVersionLoader(getServiceData());
    FC2WPVersion wsVersion = versLoader.getWorkingSetVersion(refDefId);

    FC2WPMappingLoader mappingLoader = new FC2WPMappingLoader(getServiceData());
    FC2WPMappingWithDetails fc2wpVersMapping = mappingLoader.getFC2WPMapping(wsVersion.getId());
    for (FC2WPMapping mapping : fc2wpVersMapping.getFc2wpMappingMap().values()) {
      FC2WPMapping mappingToCreate = null;
      try {
        mappingToCreate = mapping.clone();
      }
      catch (CloneNotSupportedException e) {
        throw new CommandException("Error occured while creating mapping", e);
      }

      mappingToCreate.setFcwpVerId(getNewData().getId());

      FC2WPMappingCommand mappingCommand = new FC2WPMappingCommand(getServiceData(), mappingToCreate);
      executeChildCommand(mappingCommand);
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    FC2WPVersionLoader versLoader = new FC2WPVersionLoader(getServiceData());
    TFc2wpDefVersion dbVersToUpd = versLoader.getEntityObject(getInputData().getId());

    if (isObjectChanged(getInputData().isActive(), getOldData().isActive())) {
      dbVersToUpd.setActiveFlag(getInputData().isActive() ? CommonUtilConstants.CODE_YES : null);
      resetExistingActiveVersion(dbVersToUpd);
    }
    if (isObjectChanged(getInputData().getArchReleaseSdom(), getOldData().getArchReleaseSdom())) {
      dbVersToUpd.setArchReleaseSdom(getInputData().getArchReleaseSdom());
    }
    if (isObjectChanged(getInputData().getDescEng(), getOldData().getDescEng())) {
      dbVersToUpd.setDescEng(getInputData().getDescEng());
    }
    if (isObjectChanged(getInputData().getDescGer(), getOldData().getDescGer())) {
      dbVersToUpd.setDescGer(getInputData().getDescGer());
    }

    setUserDetails(COMMAND_MODE.UPDATE, dbVersToUpd);
  }

  /**
   * @param dbVers
   * @param versLoader
   * @throws DataException
   * @throws IcdmException
   */
  private void resetExistingActiveVersion(final TFc2wpDefVersion dbVers) throws DataException, IcdmException {
    boolean isNotUpdated = true;
    if ((getOldData() != null) && !isObjectChanged(getInputData().isActive(), getOldData().isActive())) {
      isNotUpdated = false;
    }
    if (getInputData().isActive() && isNotUpdated) {
      FC2WPVersionLoader versLoader = new FC2WPVersionLoader(getServiceData());
      // Set the existing active version as not active
      for (FC2WPVersion vers : versLoader.getVersionsByDefID(getInputData().getFcwpDefId())) {
        // Find the other active version
        if (!CommonUtils.isEqual(vers.getId(), dbVers.getFcwpVerId()) && vers.isActive()) {
          vers.setActive(false);
          FC2WPVersionCommand cmd = new FC2WPVersionCommand(getServiceData(), vers, true);
          executeChildCommand(cmd);
          break;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().isActive(), getOldData().isActive()) ||
        isObjectChanged(getInputData().getArchReleaseSdom(), getOldData().getArchReleaseSdom()) ||
        isObjectChanged(getInputData().getDescEng(), getOldData().getDescEng()) ||
        isObjectChanged(getInputData().getDescGer(), getOldData().getDescGer());
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

  /**
   * @param refFcwpDefId the refFcwpDefId to set
   */
  public void setRefFcwpDefId(final Long refFcwpDefId) {
    this.refFcwpDefId = refFcwpDefId;
  }

}
