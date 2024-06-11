/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackage;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * @author bne4cob
 */
public class WorkPackageCommand extends AbstractCommand<WorkPkg, WorkPkgLoader> {


  /**
   * Constructor for creating a WorkPackage
   *
   * @param serviceData service Data
   * @param input WorkPackage to create
   * @throws IcdmException any exception
   */
  public WorkPackageCommand(final ServiceData serviceData, final WorkPkg input) throws IcdmException {
    super(serviceData, input, new WorkPkgLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * Constructor for updating/delete WorkPackage
   *
   * @param servData service Data
   * @param input WorkPackage to update
   * @param update if true updates data
   * @throws IcdmException any exception
   */
  public WorkPackageCommand(final ServiceData servData, final WorkPkg input, final boolean update)
      throws IcdmException {
    super(servData, input, new WorkPkgLoader(servData), update ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void create() throws IcdmException {

    TWorkpackage entity = new TWorkpackage();

    entity.setDescEng(getInputData().getWpDescEng());
    entity.setDescGer(getInputData().getWpDescGer());
    entity.setWpNameE(getInputData().getWpNameEng());
    entity.setWpNameG(getInputData().getWpNameGer());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws CommandException {

    TWorkpackage entity = (new WorkPkgLoader(getServiceData())).getEntityObject(getInputData().getId());

    entity.setDescEng(getInputData().getWpDescEng());
    entity.setDescGer(getInputData().getWpDescGer());
    entity.setWpNameE(getInputData().getWpNameEng());
    entity.setWpNameG(getInputData().getWpNameGer());
    entity.setDeleteFlag(getInputData().isDeleted() ? CommonUtilConstants.CODE_YES : null);

    setUserDetails(COMMAND_MODE.UPDATE, entity);

    persistEntity(entity);

  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException when object is not found
   */
  @Override
  protected void doPostCommit() throws DataException {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isObjectChanged(getInputData().isDeleted(), getOldData().isDeleted()) || validateWpName() ||
        validateWpDesc();
  }

  /**
   * @return
   */
  private boolean validateWpDesc() {
    return isObjectChanged(getInputData().getWpDescEng(), getOldData().getWpDescEng()) ||
        isObjectChanged(getInputData().getWpDescGer(), getOldData().getWpDescGer());
  }

  /**
   * @return
   */
  private boolean validateWpName() {
    return isObjectChanged(getInputData().getWpNameEng(), getOldData().getWpNameEng()) ||
        isObjectChanged(getInputData().getWpNameGer(), getOldData().getWpNameGer());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }

}
