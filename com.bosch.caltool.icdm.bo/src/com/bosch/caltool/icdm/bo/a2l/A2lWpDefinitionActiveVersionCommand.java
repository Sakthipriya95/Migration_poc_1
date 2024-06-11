/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;


/**
 * Creates active wp defn version
 *
 * @author dja7cob
 */
public class A2lWpDefinitionActiveVersionCommand extends AbstractSimpleCommand {

  private final A2lWpDefnVersion inputObj;
  private A2lWpDefnVersion createdWpDefnVersObj;

  /**
   * @param serviceData the service data
   * @param inputObj the input obj
   * @throws IcdmException the icdm exception
   */
  public A2lWpDefinitionActiveVersionCommand(final ServiceData serviceData, final A2lWpDefnVersion inputObj)
      throws IcdmException {

    super(serviceData);
    this.inputObj = inputObj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    A2lWpDefnVersion existingActiveWp =
        new A2lWpDefnVersionLoader(getServiceData()).getActiveVersion(this.inputObj.getPidcA2lId());
    if (existingActiveWp != null) {
      // update the already existing active to false
      existingActiveWp.setActive(false);
      A2lWpDefinitionVersionCommand updateCmd =
          new A2lWpDefinitionVersionCommand(getServiceData(), existingActiveWp, true, false, false);
      executeChildCommand(updateCmd);
    }

    A2lWpDefinitionVersionCommand createCmd =
        new A2lWpDefinitionVersionCommand(getServiceData(), this.inputObj, false, false, false);
    createCmd.setNewVersionProc(true);
    createCmd.setWpDefnVersForFinStatusTakeOver(existingActiveWp);
    executeChildCommand(createCmd);

    this.createdWpDefnVersObj = createCmd.getNewData();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * Gets the new data.
   *
   * @return the new data
   */
  public A2lWpDefnVersion getNewData() {
    return this.createdWpDefnVersObj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Refresh the entity after the creation, as the child records are created via stored procedure
    refreshEntity();
  }

  /**
   * Used to refresh the newly created active version
   */
  public void refreshEntity() {
    A2lWpDefnVersionLoader ldr = new A2lWpDefnVersionLoader(getServiceData());
    TA2lWpDefnVersion entity = ldr.getEntityObject(getNewData().getId());
    getEm().refresh(entity);
    entity.getTA2lWpResponsibility();
  }
}
