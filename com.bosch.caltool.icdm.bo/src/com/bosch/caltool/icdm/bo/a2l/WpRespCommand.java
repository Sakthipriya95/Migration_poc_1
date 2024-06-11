/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResp;
import com.bosch.caltool.icdm.model.a2l.WpResp;

/**
 * @author mkl2cob
 */
public class WpRespCommand extends AbstractCommand<WpResp, WpRespLoader> {


  /**
   * @param serviceData ServiceData
   * @param inputData WpResp
   * @param update boolean
   * @throws IcdmException Exception
   */
  public WpRespCommand(final ServiceData serviceData, final WpResp inputData, final boolean update)
      throws IcdmException {
    super(serviceData, inputData, new WpRespLoader(serviceData), update ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWpResp dbWpResp = new TWpResp();
    dbWpResp.setRespName(getInputData().getRespName());
    setUserDetails(COMMAND_MODE.CREATE, dbWpResp);
    getEm().persist(dbWpResp);
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
    // TODO Auto-generated method stub

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
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
