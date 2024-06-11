/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.general.TUserLoginInfo;
import com.bosch.caltool.icdm.model.general.UserLoginInfo;

/**
 * @author msp5cob
 */
public class UserLoginInfoCommand extends AbstractCommand<UserLoginInfo, UserLoginInfoLoader> {

  /**
   * Create/Update constructor
   *
   * @param serviceData service Data
   * @param inputData input Data
   * @param isCreate if true, mode is CREATE, else UPDATE
   * @throws IcdmException error while initializing data
   */
  public UserLoginInfoCommand(final ServiceData serviceData, final UserLoginInfo inputData, final boolean isCreate)
      throws IcdmException {
    super(serviceData, inputData, new UserLoginInfoLoader(serviceData),
        isCreate ? COMMAND_MODE.CREATE : COMMAND_MODE.UPDATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TUserLoginInfo entity = new TUserLoginInfo();
    setValuesToEntity(entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TUserLoginInfo entity) {
    entity.setUserNtId(getInputData().getUserNtId());
    entity.setAzureLoginCount(getInputData().getAzureLoginCount());
    entity.setLdapLoginCount(getInputData().getLdapLoginCount());

    setUserDetails(getCmdMode(), entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TUserLoginInfo entity = new UserLoginInfoLoader(getServiceData()).getEntityObject(getInputData().getId());
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
