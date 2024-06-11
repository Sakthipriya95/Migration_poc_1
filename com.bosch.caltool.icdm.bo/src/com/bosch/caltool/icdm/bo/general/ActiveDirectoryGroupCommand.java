package com.bosch.caltool.icdm.bo.general;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;


/**
 * Command class for ActiveDirectoryGroup
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupCommand extends AbstractCommand<ActiveDirectoryGroup, ActiveDirectoryGroupLoader> {


  private final boolean isSync;


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @param isSync if isDelete and isSync are true then delete all users for the Group
   * @throws IcdmException error when initializing
   */
  public ActiveDirectoryGroupCommand(final ServiceData serviceData, final ActiveDirectoryGroup input,
      final boolean isUpdate, final boolean isDelete, final boolean isSync) throws IcdmException {
    super(serviceData, input, new ActiveDirectoryGroupLoader(serviceData), (resolveCommandModeA(isDelete, isUpdate)));
    this.isSync = isSync;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TActiveDirectoryGroup entity = new TActiveDirectoryGroup();

    entity.setGroupName(getInputData().getGroupName());
    entity.setGroupSid(getInputData().getGroupSid());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    ActiveDirectoryGroupLoader loader = new ActiveDirectoryGroupLoader(getServiceData());
    TActiveDirectoryGroup entity = loader.getEntityObject(getInputData().getId());

    entity.setGroupName(getInputData().getGroupName());
    entity.setGroupSid(getInputData().getGroupSid());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    if (this.isSync) {
      deleteUsersByGroup(getInputData().getId());
    }
    else {
      ActiveDirectoryGroupLoader loader = new ActiveDirectoryGroupLoader(getServiceData());
      TActiveDirectoryGroup entity = loader.getEntityObject(getInputData().getId());

      getEm().remove(entity);
    }
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


  /**
   * Delete all Users of an AD Group
   *
   * @param adGroupId adGroup Id
   */
  public void deleteUsersByGroup(final Long adGroupId) {

    TActiveDirectoryGroup adGroup = new ActiveDirectoryGroupLoader(getServiceData()).getEntityObject(adGroupId);
    Query query = getEm().createNamedQuery(TActiveDirectoryGroupUser.NQ_DELETE_BY_GROUP_ID);
    query.setParameter("groupId", adGroup);

    query.executeUpdate();

  }

}
