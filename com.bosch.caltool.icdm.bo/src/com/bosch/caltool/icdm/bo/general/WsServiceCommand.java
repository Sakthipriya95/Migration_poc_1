/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.general.TWsService;
import com.bosch.caltool.icdm.model.general.WsService;

/**
 * @author bne4cob
 */
public class WsServiceCommand extends AbstractCommand<WsService, WsServiceLoader> {

  /**
   * Create/Update constructor
   *
   * @param serviceData service Data
   * @param inputData input Data
   * @param isCreate if true, mode is CREATE, else UPDATE
   * @throws IcdmException error while initializing data
   */
  public WsServiceCommand(final ServiceData serviceData, final WsService inputData, final boolean isCreate)
      throws IcdmException {
    super(serviceData, inputData, new WsServiceLoader(serviceData),
        isCreate ? COMMAND_MODE.CREATE : COMMAND_MODE.UPDATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWsService entity = new TWsService();
    setValuesToEntity(entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TWsService entity) {
    entity.setServMethod(getInputData().getServMethod());
    entity.setServUri(getInputData().getServUri());
    entity.setServiceScope(getInputData().getServiceScope());
    entity.setServDesc(getInputData().getDescription());
    entity.setModule(getInputData().getModule());
    entity.setDeleteFlag(booleanToYorN(getInputData().isDeleted()));

    setUserDetails(getCmdMode(), entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TWsService entity = new WsServiceLoader(getServiceData()).getEntityObject(getInputData().getId());
    setValuesToEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not applicable
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
