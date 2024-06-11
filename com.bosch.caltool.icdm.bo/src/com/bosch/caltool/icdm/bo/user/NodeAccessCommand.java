/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;


/**
 * @author bne4cob
 */
public class NodeAccessCommand extends AbstractCommand<NodeAccess, NodeAccessLoader> {

  /**
   * @param serviceData service Data
   * @param inputData input Data
   * @throws IcdmException exception from command
   */
  public NodeAccessCommand(final ServiceData serviceData, final NodeAccess inputData) throws IcdmException {
    super(serviceData, inputData, new NodeAccessLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * @param serviceData service Data
   * @param inputData input Data
   * @param isUpdate check for update or delete
   * @throws IcdmException exception from command
   */
  public NodeAccessCommand(final ServiceData serviceData, final NodeAccess inputData, final boolean isUpdate)
      throws IcdmException {
    super(serviceData, inputData, new NodeAccessLoader(serviceData),
        isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.DELETE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvApicNodeAccess dbAccess = new TabvApicNodeAccess();
    dbAccess.setNodeId(getInputData().getNodeId());
    dbAccess.setNodeType(getInputData().getNodeType());
    TabvApicUser dbApicUser = (new UserLoader(getServiceData())).getEntityObject(getInputData().getUserId());
    dbAccess.setTabvApicUser(dbApicUser);
    dbApicUser.addTabvApicNodeAccess(dbAccess);
    setAccessRight(dbAccess);
    setUserDetails(COMMAND_MODE.CREATE, dbAccess);
    persistEntity(dbAccess);
  }

  /**
   * @param dbAccess
   */
  private void setAccessRight(final TabvApicNodeAccess dbAccess) {
    if (getInputData().isOwner()) {
      dbAccess.setOwner(CommonUtilConstants.CODE_YES);
      dbAccess.setGrantright(CommonUtilConstants.CODE_YES);
      dbAccess.setWriteright(CommonUtilConstants.CODE_YES);
      dbAccess.setReadright(CommonUtilConstants.CODE_YES);
    }
    else if (getInputData().isGrant()) {
      dbAccess.setOwner(null);
      dbAccess.setGrantright(CommonUtilConstants.CODE_YES);
      dbAccess.setWriteright(CommonUtilConstants.CODE_YES);
      dbAccess.setReadright(CommonUtilConstants.CODE_YES);
    }
    else if (getInputData().isWrite()) {
      dbAccess.setOwner(null);
      dbAccess.setGrantright(null);
      dbAccess.setWriteright(CommonUtilConstants.CODE_YES);
      dbAccess.setReadright(CommonUtilConstants.CODE_YES);
    }
    else if (getInputData().isRead()) {
      dbAccess.setOwner(null);
      dbAccess.setGrantright(null);
      dbAccess.setWriteright(null);
      dbAccess.setReadright(CommonUtilConstants.CODE_YES);
    }
    else {
      dbAccess.setOwner(null);
      dbAccess.setGrantright(null);
      dbAccess.setWriteright(null);
      dbAccess.setReadright(null);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    NodeAccessLoader loader = new NodeAccessLoader(getServiceData());
    TabvApicNodeAccess dbNodeAccess = loader.getEntityObject(getInputData().getId());

    isReadModified(dbNodeAccess);
    isWriteModified(dbNodeAccess);
    isGrantModified(dbNodeAccess);
    isOwnerModified(dbNodeAccess);

    setUserDetails(COMMAND_MODE.UPDATE, dbNodeAccess);
  }

  /**
   * @param dbAccess TabvApicNodeAccess
   */
  public void isReadModified(final TabvApicNodeAccess dbAccess) {
    if (isObjectChanged(getInputData().isRead(), getOldData().isRead())) {
      if (getInputData().isRead()) {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(null);
        dbAccess.setWriteright(null);
        dbAccess.setReadright(CommonUtilConstants.CODE_YES);
      }
      else {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(null);
        dbAccess.setWriteright(null);
        dbAccess.setReadright(null);
      }
    }
  }

  /**
   * @param dbAccess TabvApicNodeAccess
   */
  public void isWriteModified(final TabvApicNodeAccess dbAccess) {
    if (isObjectChanged(getInputData().isWrite(), getOldData().isWrite())) {
      if (getInputData().isWrite()) {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(null);
        dbAccess.setWriteright(CommonUtilConstants.CODE_YES);
        dbAccess.setReadright(CommonUtilConstants.CODE_YES);
      }
      else {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(null);
        dbAccess.setWriteright(null);
        dbAccess.setReadright(dbAccess.getReadright());
      }
    }
  }

  /**
   * @param dbAccess TabvApicNodeAccess
   */
  public void isGrantModified(final TabvApicNodeAccess dbAccess) {
    if (isObjectChanged(getInputData().isGrant(), getOldData().isGrant())) {
      if (getInputData().isGrant()) {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(CommonUtilConstants.CODE_YES);
        dbAccess.setWriteright(CommonUtilConstants.CODE_YES);
        dbAccess.setReadright(CommonUtilConstants.CODE_YES);
      }
      else {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(null);
        dbAccess.setWriteright(dbAccess.getWriteright());
        dbAccess.setReadright(dbAccess.getReadright());
      }
    }
  }

  /**
   * @param dbAccess TabvApicNodeAccess
   */
  public void isOwnerModified(final TabvApicNodeAccess dbAccess) {
    if (isObjectChanged(getInputData().isOwner(), getOldData().isOwner())) {
      if (getInputData().isOwner()) {
        dbAccess.setOwner(CommonUtilConstants.CODE_YES);
        dbAccess.setGrantright(CommonUtilConstants.CODE_YES);
        dbAccess.setWriteright(CommonUtilConstants.CODE_YES);
        dbAccess.setReadright(CommonUtilConstants.CODE_YES);
      }
      else {
        dbAccess.setOwner(null);
        dbAccess.setGrantright(dbAccess.getGrantright());
        dbAccess.setWriteright(dbAccess.getWriteright());
        dbAccess.setReadright(dbAccess.getReadright());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    NodeAccessLoader loader = new NodeAccessLoader(getServiceData());
    TabvApicNodeAccess dbNodeAccess = loader.getEntityObject(getInputData().getId());
    new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()).getTabvApicNodeAccesses()
        .remove(dbNodeAccess);
    getEm().remove(dbNodeAccess);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // IMPORTANT : Update current user's node access details if input data is of current user
    User currentUser = new UserLoader(getServiceData()).getDataObjectCurrentUser();
    if (getInputData().getUserId().equals(currentUser.getId())) {
      new NodeAccessLoader(getServiceData()).resetNodeAccessCache();
    }
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
    // TODO Auto-generated method stub
  }
}
