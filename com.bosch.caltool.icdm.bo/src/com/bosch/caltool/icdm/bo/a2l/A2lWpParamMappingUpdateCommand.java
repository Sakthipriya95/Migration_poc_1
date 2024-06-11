/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;

/**
 * @author pdh2cob
 */
public class A2lWpParamMappingUpdateCommand extends AbstractSimpleCommand {

  private final A2lWpParamMappingUpdateModel inputData;

  private final Set<Long> createdA2lWpParamMappingIds = new HashSet<>();


  /**
   * @param serviceData service data
   * @param inputData - A2lWpParamMappingUpdateModel
   * @throws IcdmException icdm exception
   */
  public A2lWpParamMappingUpdateCommand(final ServiceData serviceData, final A2lWpParamMappingUpdateModel inputData)
      throws IcdmException {
    super(serviceData);
    this.inputData = inputData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {


    for (A2lWpParamMapping a2lWpParamMappingObj : this.inputData.getA2lWpParamMappingToBeCreated()) {
      A2lWpParamMappingCommand cmd = new A2lWpParamMappingCommand(getServiceData(), a2lWpParamMappingObj, false, false);
      executeChildCommand(cmd);
      this.createdA2lWpParamMappingIds.add(cmd.getNewData().getId());
    }
    for (A2lWpParamMapping a2lWpParamMappingObj : this.inputData.getA2lWpParamMappingToBeUpdated().values()) {
      A2lWpParamMappingCommand cmd = new A2lWpParamMappingCommand(getServiceData(), a2lWpParamMappingObj, true, false);
      executeChildCommand(cmd);

    }
    for (A2lWpParamMapping a2lWpParamMappingObj : this.inputData.getA2lWpParamMappingToBeDeleted().values()) {
      A2lWpParamMappingCommand cmd = new A2lWpParamMappingCommand(getServiceData(), a2lWpParamMappingObj, false, true);
      executeChildCommand(cmd);
    }
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
   * @return the createdA2lWpParamMappingIds
   */
  public Set<Long> getCreatedA2lWpParamMappingIds() {
    return new HashSet<>(this.createdA2lWpParamMappingIds);
  }


}
