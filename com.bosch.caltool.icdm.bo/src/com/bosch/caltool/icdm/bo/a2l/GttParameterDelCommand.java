/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author apj4cob
 */
public class GttParameterDelCommand extends AbstractSimpleCommand {

  /**
   * Command to delete existing record in the temp table GttParameter ,if any
   * 
   * @param serviceData ServiceData
   * @throws IcdmException icdm execption
   */
  public GttParameterDelCommand(final ServiceData serviceData) throws IcdmException {
    super(serviceData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    // Delete the existing records in this temp table, if any
    final Query delQuery = getEm().createQuery("delete from GttParameter temp");
    delQuery.executeUpdate();

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
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return true;
  }

}
