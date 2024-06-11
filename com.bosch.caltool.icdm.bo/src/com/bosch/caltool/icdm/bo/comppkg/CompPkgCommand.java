/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comppkg;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.TopLevelEntityCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;

/**
 * @author say8cob
 */
public class CompPkgCommand extends AbstractCommand<CompPackage, CompPackageLoader> {

  private final String COMP_PKG_DEFAULT_TYPE = "N";

  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CompPkgCommand(final ServiceData serviceData, final CompPackage input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CompPackageLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCompPkg entity = new TCompPkg();
    entity.setCompPkgName(getInputData().getName());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());
    entity.setDeletedFlag(booleanToYorN(getInputData().isDeleted()));
    entity.setCompPkgType(this.COMP_PKG_DEFAULT_TYPE);
    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
    TopLevelEntityCommand tleCmd = new TopLevelEntityCommand(getServiceData(), ApicConstants.TOP_LVL_ENT_ID_COMP_PKG);
    executeChildCommand(tleCmd);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    CompPackageLoader loader = new CompPackageLoader(getServiceData());
    TCompPkg entity = loader.getEntityObject(getInputData().getId());
    entity.setCompPkgName(getInputData().getName());
    entity.setDescEng(getInputData().getDescEng());
    entity.setDescGer(getInputData().getDescGer());
    entity.setDeletedFlag(booleanToYorN(getInputData().isDeleted()));
    entity.setCompPkgType(this.COMP_PKG_DEFAULT_TYPE);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * Soft Delete of Component Package
   */
  @Override
  protected void delete() throws IcdmException {
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
}
