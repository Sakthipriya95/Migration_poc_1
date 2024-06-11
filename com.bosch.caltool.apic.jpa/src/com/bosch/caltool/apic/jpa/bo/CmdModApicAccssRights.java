/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicAccessRight;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author mkl2cob
 */
public class CmdModApicAccssRights extends AbstractCommand {

  private final ApicUser apicUser;
  /**
   * ApicAccessRight
   */
  private ApicAccessRight apicAccessRight;

  /**
   * @param dataProvider AbstractDataProvider
   * @param apicUser ApicUser
   */
  protected CmdModApicAccssRights(final AbstractDataProvider dataProvider, final ApicUser apicUser) {
    super(dataProvider);
    this.apicUser = apicUser;
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // Do Nothing
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    // Create new Entity Manager for this transaction. To avoid Concurrent Modification exception.
    TabvApicAccessRight dbAccessRight;
    dbAccessRight = new TabvApicAccessRight();
    TabvApicUser dbApicUser = getEntityProvider().getDbApicUser(this.apicUser.getID());
    dbAccessRight.setTabvApicUser(dbApicUser);
    dbAccessRight.setAccessRight(ApicConstants.APIC_READ_ACCESS);
    dbAccessRight.setModuleName(ApicConstants.APIC_MODULE_NAME);
    dbAccessRight.setCreatedDate(ApicUtil.getCurrentUtcTime());
    dbAccessRight.setCreatedUser(this.apicUser.getName());
    dbAccessRight.setVersion(Long.valueOf(1));
    getEntityProvider().registerNewEntity(dbAccessRight);
    this.apicAccessRight = new ApicAccessRight(getDataProvider(), dbAccessRight.getAccessrightId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return "Access Rights of" + this.apicUser.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.apicAccessRight == null ? null : this.apicAccessRight.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Apic User Access Rights";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.apicAccessRight.getAccessRight();
  }

}
