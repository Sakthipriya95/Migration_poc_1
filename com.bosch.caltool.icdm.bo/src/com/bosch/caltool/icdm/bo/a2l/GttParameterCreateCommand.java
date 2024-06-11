/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.GttParameter;

/**
 * @author apj4cob
 */
public class GttParameterCreateCommand extends AbstractSimpleCommand {


  private final GttParameter inputData;

  /**
   * Command class to create entries for all the parameters
   *
   * @param serviceData Service Data
   * @throws IcdmException throws IcdmExecption
   */
  public GttParameterCreateCommand(final ServiceData serviceData, final GttParameter inputData) throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    createGttParameter();
  }

  /**
   *
   */
  public void createGttParameter() {
    // Create entities for all the parameters
    getEm().persist(this.inputData);

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
    return true;
  }

}
