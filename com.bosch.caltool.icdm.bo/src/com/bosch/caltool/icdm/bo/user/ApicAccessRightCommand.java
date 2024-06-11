/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;


/**
 * @author bne4cob
 */
public class ApicAccessRightCommand extends AbstractCommand<ApicAccessRight, ApicAccessRightLoader> {

  /**
   * Constructor to create an system access to the user
   *
   * @param serviceData Service Data
   * @param inputData system access right to create
   * @throws IcdmException any error
   */
  public ApicAccessRightCommand(final ServiceData serviceData, final ApicAccessRight inputData) throws IcdmException {
    super(serviceData, inputData, new ApicAccessRightLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    TabvApicAccessRight entity = new TabvApicAccessRight();
    TabvApicUser dbUser = new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId());
    entity.setTabvApicUser(dbUser);
    entity.setAccessRight(getInputData().getAccessRight());
    entity.setModuleName(getInputData().getModuleName());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);

    Set<TabvApicAccessRight> dbUserRightsSet = dbUser.getTabvApicAccessRights();
    if (dbUserRightsSet == null) {
      dbUserRightsSet = new HashSet<>();
      dbUser.setTabvApicAccessRights(dbUserRightsSet);
    }
    dbUserRightsSet.add(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

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
    // TODO Auto-generated method stub
    return false;
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
