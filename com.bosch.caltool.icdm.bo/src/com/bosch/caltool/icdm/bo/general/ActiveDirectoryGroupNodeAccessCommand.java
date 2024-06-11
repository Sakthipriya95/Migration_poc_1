package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;


/**
 * Command class for ActiveDirectoryGroupNodeAccess
 *
 * @author SSN9COB
 */
public class ActiveDirectoryGroupNodeAccessCommand
    extends AbstractCommand<ActiveDirectoryGroupNodeAccess, ActiveDirectoryGroupNodeAccessLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public ActiveDirectoryGroupNodeAccessCommand(final ServiceData serviceData,
      final ActiveDirectoryGroupNodeAccess input, final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new ActiveDirectoryGroupNodeAccessLoader(serviceData),
        (resolveCommandModeA(isDelete, isUpdate)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TActiveDirectoryGroupNodeAccess entity = new TActiveDirectoryGroupNodeAccess();

    setEntityDetails(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setEntityDetails(final TActiveDirectoryGroupNodeAccess entity) {
    entity.setNodeId(getInputData().getNodeId());
    entity.setNodeType(getInputData().getNodeType());
    TActiveDirectoryGroup activeDirectoryGroup =
        (new ActiveDirectoryGroupLoader(getServiceData())).getEntityObject(getInputData().getAdGroup().getId());
    entity.setActiveDirectoryGroup(activeDirectoryGroup);
    entity.setReadright(CommonUtils.getBooleanCode(getInputData().isRead()));
    entity.setWriteright(CommonUtils.getBooleanCode(getInputData().isWrite()));
    entity.setGrantright(CommonUtils.getBooleanCode(getInputData().isGrant()));
    entity.setOwner(CommonUtils.getBooleanCode(getInputData().isOwner()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    ActiveDirectoryGroupNodeAccessLoader loader = new ActiveDirectoryGroupNodeAccessLoader(getServiceData());
    TActiveDirectoryGroupNodeAccess entity = loader.getEntityObject(getInputData().getId());

    setEntityDetails(entity);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    ActiveDirectoryGroupNodeAccessLoader loader = new ActiveDirectoryGroupNodeAccessLoader(getServiceData());
    TActiveDirectoryGroupNodeAccess entity = loader.getEntityObject(getInputData().getId());

    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    //  Check if user has access in group (affected)
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return isObjectChanged(getInputData().isOwner(), getOldData().isOwner()) ||
        isObjectChanged(getInputData().isGrant(), getOldData().isGrant()) ||
        isObjectChanged(getInputData().isWrite(), getOldData().isWrite()) ||
        isObjectChanged(getInputData().isRead(), getOldData().isRead());
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
