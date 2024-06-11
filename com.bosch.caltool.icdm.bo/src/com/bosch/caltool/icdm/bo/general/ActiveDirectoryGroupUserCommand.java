package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;


/**
 * Command class for ActiveDirectoryGroupUser
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupUserCommand
    extends AbstractCommand<ActiveDirectoryGroupUser, ActiveDirectoryGroupUserLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public ActiveDirectoryGroupUserCommand(final ServiceData serviceData, final ActiveDirectoryGroupUser input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new ActiveDirectoryGroupUserLoader(serviceData),
        (resolveCommandModeA(isDelete, isUpdate)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TActiveDirectoryGroupUser entity = new TActiveDirectoryGroupUser();
    TActiveDirectoryGroup adGroup =
        (new ActiveDirectoryGroupLoader(getServiceData())).getEntityObject(getInputData().getAdGroup().getId());
    entity.setActiveDirectoryGroup(adGroup);
    entity.setUserName(getInputData().getUsername());
    entity.setIsIcdmUser(getInputData().getIsIcdmUser());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    ActiveDirectoryGroupUserLoader loader = new ActiveDirectoryGroupUserLoader(getServiceData());
    TActiveDirectoryGroupUser entity = loader.getEntityObject(getInputData().getId());
    TActiveDirectoryGroup adGroup =
        (new ActiveDirectoryGroupLoader(getServiceData())).getEntityObject(getInputData().getAdGroup().getId());
    entity.setActiveDirectoryGroup(adGroup);
    entity.setUserName(getInputData().getUsername());
    entity.setIsIcdmUser(getInputData().getIsIcdmUser());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    ActiveDirectoryGroupUserLoader loader = new ActiveDirectoryGroupUserLoader(getServiceData());
    TActiveDirectoryGroupUser entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // Auto-generated method stub
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
    // Auto-generated method stub
  }

}
