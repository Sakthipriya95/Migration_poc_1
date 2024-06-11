/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comppkg;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgResponse;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author say8cob
 */
public class CompPkgCreateCommand extends AbstractSimpleCommand {


  private final CompPkgResponse inputData;


  /**
   * @param serviceData
   * @param compPkgResponse
   * @throws IcdmException
   */
  public CompPkgCreateCommand(final ServiceData serviceData, final CompPkgResponse compPkgResponse)
      throws IcdmException {
    super(serviceData);
    this.inputData = compPkgResponse;
  }


  private void createNodeAccess(final CompPackage compPackage) throws IcdmException {
    NodeAccess pidcNodeAccess = new NodeAccess();
    pidcNodeAccess.setOwner(true);
    pidcNodeAccess.setGrant(true);
    pidcNodeAccess.setWrite(true);
    pidcNodeAccess.setRead(true);
    pidcNodeAccess.setNodeId(compPackage.getId());
    pidcNodeAccess.setNodeType(ApicConstants.COMP_PKG_NODE_TYPE);
    pidcNodeAccess.setUserId(getServiceData().getUserId());
    pidcNodeAccess.setVersion(1L);
    NodeAccessCommand nodeAccessCommand = new NodeAccessCommand(getServiceData(), pidcNodeAccess);
    executeChildCommand(nodeAccessCommand);
    getInputData().setNodeAccess(nodeAccessCommand.getNewData());
  }

  private CompPackage createCompPkg() throws IcdmException {
    CompPackage compPackage = getInputData().getCompPackage();
    CompPkgCommand cmd = new CompPkgCommand(getServiceData(), compPackage, false, false);
    executeChildCommand(cmd);
    return cmd.getNewData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    CompPackage createCompPkg = createCompPkg();
    getInputData().setCompPackage(createCompPkg);
    createNodeAccess(createCompPkg);
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
    return true;
  }


  /**
   * @return the inputData
   */
  public CompPkgResponse getInputData() {
    return this.inputData;
  }
}
