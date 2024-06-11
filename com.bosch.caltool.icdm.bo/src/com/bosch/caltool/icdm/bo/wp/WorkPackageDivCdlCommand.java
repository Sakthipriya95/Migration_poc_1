/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TRegion;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivisionCdl;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;

/**
 * @author apj4cob
 */
public class WorkPackageDivCdlCommand extends AbstractCommand<WorkpackageDivisionCdl, WorkpackageDivisionCdlLoader> {

  /**
   *
   */
  private static final String CREATE = "CREATE";
  /**
   *
   */
  private static final String DELETE = "DELETE";
  /**
   *
   */
  private static final String UPDATE = "UPDATE";

  /**
   * Constructor for creating a WorkPackageDivisionCdl
   *
   * @param serviceData service Data
   * @param inputData WorkPackageDivisionCdl to create
   * @throws IcdmException any exception
   */
  public WorkPackageDivCdlCommand(final ServiceData serviceData, final WorkpackageDivisionCdl inputData)
      throws IcdmException {
    super(serviceData, inputData, new WorkpackageDivisionCdlLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * Constructor for updating/delete a WorkPackageDivisionCdl
   *
   * @param serviceData service Data
   * @param inputData WorkPackageDivisionCdl to update
   * @param update if true updates
   * @throws IcdmException any exception
   */
  public WorkPackageDivCdlCommand(final ServiceData serviceData, final WorkpackageDivisionCdl inputData,
      final boolean update) throws IcdmException {
    super(serviceData, inputData, new WorkpackageDivisionCdlLoader(serviceData),
        update ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWorkpackageDivisionCdl dbWorkPackageDivCdl = new TWorkpackageDivisionCdl();

    dbWorkPackageDivCdl.setTRegion(new RegionLoader(getServiceData()).getEntityObject(getInputData().getRegionId()));
    dbWorkPackageDivCdl.setTWorkpackageDivision(
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(getInputData().getWpDivId()));
    dbWorkPackageDivCdl.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()));

    setMapping(dbWorkPackageDivCdl, CREATE);

    setUserDetails(COMMAND_MODE.CREATE, dbWorkPackageDivCdl);

    persistEntity(dbWorkPackageDivCdl);

  }

  /**
   * @param dbWorkPackageDivCdl
   * @param cmd String
   */
  private void setMapping(final TWorkpackageDivisionCdl dbWorkPackageDivCdl, final String cmd) {

    TWorkpackageDivision dbWorkPackageDiv =
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(getInputData().getWpDivId());
    TRegion dbRegion = new RegionLoader(getServiceData()).getEntityObject(getInputData().getRegionId());
    TabvApicUser dbApicUser = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());
    if (cmd.equals(UPDATE) || cmd.equals(DELETE)) {

      dbWorkPackageDiv.removeTWorkpackageDivisionCdl(dbWorkPackageDivCdl);

      dbRegion.removeTWorkpackageDivisionCdl(dbWorkPackageDivCdl);

      dbApicUser.removeTWorkpackageDivisionCdl(dbWorkPackageDivCdl);

    }
    if (cmd.equals(CREATE) || cmd.equals(UPDATE)) {
      dbWorkPackageDiv.addTWorkpackageDivisionCdl(dbWorkPackageDivCdl);

      dbRegion.addTWorkpackageDivisionCdl(dbWorkPackageDivCdl);

      dbApicUser.addTWorkpackageDivisionCdl(dbWorkPackageDivCdl);

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TWorkpackageDivisionCdl dbWorkPackageDivCdl =
        (new WorkpackageDivisionCdlLoader(getServiceData())).getEntityObject(getInputData().getId());
    if(isObjectChanged(getInputData().getRegionId(),getOldData().getRegionId())){
    dbWorkPackageDivCdl.setTRegion(new RegionLoader(getServiceData()).getEntityObject(getInputData().getRegionId()));
    }
    if(isObjectChanged(getInputData().getUserId(),getOldData().getUserId())){
    dbWorkPackageDivCdl.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()));
    }
    if(isObjectChanged(getInputData().getWpDivId(),getOldData().getWpDivId())){dbWorkPackageDivCdl.setTWorkpackageDivision(
        new WorkPackageDivisionLoader(getServiceData()).getEntityObject(getInputData().getWpDivId()));
    }
    setMapping(dbWorkPackageDivCdl, UPDATE);

    setUserDetails(COMMAND_MODE.UPDATE, dbWorkPackageDivCdl);

    persistEntity(dbWorkPackageDivCdl);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    WorkpackageDivisionCdlLoader cdlLoader = new WorkpackageDivisionCdlLoader(getServiceData());
    TWorkpackageDivisionCdl dbWorkPackageDivCdl = cdlLoader.getEntityObject(getInputData().getId());
    getEm().remove(dbWorkPackageDivCdl);
    setMapping(dbWorkPackageDivCdl, DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().getRegionId(), getOldData().getRegionId()) ||
        isObjectChanged(getInputData().getUserId(), getOldData().getUserId()) ||
        isObjectChanged(getInputData().getWpDivId(), getOldData().getWpDivId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // No Action

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // No Action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // No Action
    return true;
  }

}
