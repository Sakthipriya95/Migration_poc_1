/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.PTTypeLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpPtTypeRelv;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;

/**
 * @author bne4cob
 */
public class FC2WPDefRelvPTtypeCommand extends AbstractCommand<FC2WPRelvPTType, FC2WPRelvPTTypeLoader> {

  /**
   * @param serviceData Service Data
   * @param inputData inputData
   * @param isDelete create/delete
   * @throws IcdmException failure during initialization
   */
  public FC2WPDefRelvPTtypeCommand(final ServiceData serviceData, final FC2WPRelvPTType inputData,
      final boolean isDelete) throws IcdmException {

    super(serviceData, inputData, new FC2WPRelvPTTypeLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : COMMAND_MODE.CREATE);

  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  protected void create() throws CommandException, DataException {
    TFc2wpPtTypeRelv dbMapType = new TFc2wpPtTypeRelv();

    TFc2wpDefinition dbMapping =
        new FC2WPDefLoader(getServiceData()).getEntityObject(getInputData().getFcwpDefId());
    dbMapType.setTFc2wpDefinition(dbMapping);
    dbMapType.setTPowerTrainType(new PTTypeLoader(getServiceData()).getEntityObject(getInputData().getPtTypeId()));
    dbMapping.addTFc2wpPtTypeRelv(dbMapType);
    setUserDetails(COMMAND_MODE.CREATE, dbMapType);
    persistEntity(dbMapType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws CommandException {
    // Not Applicable
  }

  /**
   * @return true, if the user has privileges to execute this command
   */
  @Override
  protected boolean hasPrivileges() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws CommandException {

    TFc2wpPtTypeRelv entity = new FC2WPRelvPTTypeLoader(getServiceData()).getEntityObject(getInputData().getId());
    TFc2wpDefinition defn =
        new FC2WPDefLoader(getServiceData()).getEntityObject(entity.getTFc2wpDefinition().getFcwpDefId());
    defn.removeTFc2wpPtTypeRelv(entity);
    getEm().remove(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
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
