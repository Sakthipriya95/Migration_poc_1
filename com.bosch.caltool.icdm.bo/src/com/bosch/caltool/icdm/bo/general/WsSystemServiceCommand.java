/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.general.TWsSystemService;
import com.bosch.caltool.icdm.model.general.WsSystemService;

/**
 * @author bne4cob
 */
public class WsSystemServiceCommand extends AbstractCommand<WsSystemService, WsSystemServiceLoader> {

  /**
   * Create/Delete constructor
   * 
   * @param serviceData service Data
   * @param inputData input Data
   * @param isCreate if true, mode is CREATE, else DELETE
   * @throws IcdmException error while initializing data
   */
  public WsSystemServiceCommand(final ServiceData serviceData, final WsSystemService inputData, final boolean isCreate)
      throws IcdmException {
    super(serviceData, inputData, new WsSystemServiceLoader(serviceData),
        isCreate ? COMMAND_MODE.CREATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWsSystemService entity = new TWsSystemService();

    new WsServiceLoader(getServiceData()).getEntityObject(getInputData().getServiceId()).addTWsSystemService(entity);
    new WsSystemLoader(getServiceData()).getEntityObject(getInputData().getSystemId()).addTWsSystemService(entity);

    setUserDetails(getCmdMode(), entity);

    getEm().persist(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TWsSystemService entity = new WsSystemServiceLoader(getServiceData()).getEntityObject(getInputData().getId());
    new WsServiceLoader(getServiceData()).getEntityObject(getInputData().getServiceId()).removeTWsSystemService(entity);
    new WsSystemLoader(getServiceData()).getEntityObject(getInputData().getSystemId()).removeTWsSystemService(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    // Not applicable
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // Not applicable
    return true;
  }

}

