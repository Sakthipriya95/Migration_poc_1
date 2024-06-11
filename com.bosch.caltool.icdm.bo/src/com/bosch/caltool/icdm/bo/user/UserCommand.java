/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.text.ParseException;
import java.util.Locale;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.TopLevelEntityCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author mkl2cob
 */
public class UserCommand extends AbstractCommand<User, UserLoader> {


  /**
   * Constructor to create a user. This also create an 'APIC_READ' system access to the user
   *
   * @param serviceData Service Data
   * @param inputData user to create
   * @throws IcdmException any error
   */
  public UserCommand(final ServiceData serviceData, final User inputData) throws IcdmException {
    super(serviceData, inputData, new UserLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData
   * @param inputData
   * @param isUpdate
   * @throws IcdmException
   */
  public UserCommand(final ServiceData serviceData, final User inputData, final boolean isUpdate) throws IcdmException {
    super(serviceData, inputData, new UserLoader(serviceData), isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  @Override
  public void create() throws IcdmException {

    TabvApicUser entity = new TabvApicUser();

    // set the userName
    entity.setUsername(getInputData().getName().toUpperCase(Locale.getDefault()));

    // set the other attributes
    entity.setFirstname(getInputData().getFirstName());
    entity.setLastname(getInputData().getLastName());
    entity.setDepartment(getInputData().getDepartment());

    // set the Created User and date information
    setUserDetails(COMMAND_MODE.CREATE, entity);

    // register the new Entity in the EntityManager to get the ID
    persistEntity(entity);


    ApicAccessRight sysAccess = new ApicAccessRight();
    sysAccess.setUserId(getObjId());
    sysAccess.setModuleName(ApicConstants.APIC_MODULE_NAME);
    sysAccess.setAccessRight(ApicConstants.APIC_READ_ACCESS);

    ApicAccessRightCommand sysAccessCmd = new ApicAccessRightCommand(getServiceData(), sysAccess);
    executeChildCommand(sysAccessCmd);

    TopLevelEntityCommand tleCmd = new TopLevelEntityCommand(getServiceData(), ApicConstants.TOP_LVL_ENT_ID_USER);
    executeChildCommand(tleCmd);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TabvApicUser modifiedUser = new UserLoader(getServiceData()).getEntityObject(getInputData().getId());
    setNewValues(modifiedUser);
    setUserDetails(COMMAND_MODE.UPDATE, modifiedUser);
  }

  /**
   * @param user
   * @throws IcdmException
   * @throws ParseException
   */
  private void setNewValues(final TabvApicUser modifiedUser) throws IcdmException {
    if (isObjectChanged(getOldData().getFirstName(), getInputData().getFirstName())) {
      modifiedUser.setFirstname(getInputData().getFirstName());
    }
    if (isObjectChanged(getOldData().getLastName(), getInputData().getLastName())) {
      modifiedUser.setLastname(getInputData().getLastName());
    }
    if (isObjectChanged(getOldData().getDepartment(), getInputData().getDepartment())) {
      modifiedUser.setDepartment(getInputData().getDepartment());
    }
    if (isObjectChanged(getOldData().getDisclaimerAcceptedDate(), getInputData().getDisclaimerAcceptedDate())) {
      try {
        modifiedUser.setDisclaimerAcceptnceDate(string2timestamp(getInputData().getDisclaimerAcceptedDate()));
      }
      catch (ParseException e) {
        throw new IcdmException("Error in parsing Disclaimer Accepted Date format : " + e.getMessage());
      }
    }
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
    return isObjectChanged(getOldData().getFirstName(), getInputData().getFirstName()) ||
        (isObjectChanged(getOldData().getLastName(), getInputData().getLastName())) ||
        (isObjectChanged(getOldData().getDepartment(), getInputData().getDepartment()));
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
