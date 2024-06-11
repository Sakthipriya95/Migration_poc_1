/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefinitionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;

/**
 * @author dja7cob Command to add new Pidc Risk Definition
 */
public class CmdModPidcRmDefinition extends AbstractCommand {

  /**
   *
   */
  private static final long FIRST_VERSION = 1L;
  /**
   * Unique entity id for setting user details
   */
  private static final String PIDC_RM_DEF_ENTITY_ID = "PIDC_RM_DEF_ENTITY_ID";
  /**
   * PidcRmDefinition
   */
  private final PidcRmDefinition oldPidcRmDef;

  private final PIDCVersion pidcVersion;

  private PidcRmDefinition newPidcRmDef;

  private final ApicDataProvider dataProvider;

  private final PidcRmDefinitionLoader loader;

  /**
   * @param apicDataProvider
   * @param oldPidcRmDef PidcRmDefinition
   * @param pidcVersion
   * @param loader
   */
  protected CmdModPidcRmDefinition(final ApicDataProvider dataProvider, final PidcRmDefinition oldPidcRmDef,
      final PIDCVersion pidcVersion, final PidcRmDefinitionLoader loader) {
    super(dataProvider);
    this.dataProvider = dataProvider;
    this.commandMode = COMMAND_MODE.INSERT;
    this.oldPidcRmDef = oldPidcRmDef;
    this.pidcVersion = pidcVersion;
    this.loader = loader;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    final TPidcRmDefinition dbPidcRmDef = new TPidcRmDefinition();

    dbPidcRmDef.setRmNameEng(this.oldPidcRmDef.getRmNameEng());
    if (null != this.oldPidcRmDef.getRmNameGer()) {
      dbPidcRmDef.setRmNameGer(this.oldPidcRmDef.getRmNameGer());
    }
    if (null != this.oldPidcRmDef.getRmDescEng()) {
      dbPidcRmDef.setRmDescEng(this.oldPidcRmDef.getRmDescEng());
    }
    if (null != this.oldPidcRmDef.getRmDescGer()) {
      dbPidcRmDef.setRmDescGer(this.oldPidcRmDef.getRmDescGer());
    }
    dbPidcRmDef.setTPidcVersion(getEntityProvider().getDbPIDCVersion(this.pidcVersion.getID()));
    if (null != this.oldPidcRmDef.getIsVariant()) {
      dbPidcRmDef.setIsVariant(this.oldPidcRmDef.getIsVariant());

    }
    dbPidcRmDef.setVersion(FIRST_VERSION);

    setUserDetails(COMMAND_MODE.INSERT, dbPidcRmDef, PIDC_RM_DEF_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbPidcRmDef);
    this.newPidcRmDef = new PidcRmDefinition();
    try {
      setNewPidcRmDef(this.loader.createDataObject(dbPidcRmDef));
    }
    catch (DataException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    getChangedData().put(this.oldPidcRmDef.getId(), new ChangedData(ChangeType.INSERT, this.oldPidcRmDef.getId(),
        TPidcRmDefinition.class, DisplayEventSource.COMMAND));
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
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return the newPidcRmDef
   */
  public PidcRmDefinition getNewPidcRmDef() {
    return this.newPidcRmDef;
  }

  /**
   * @param newPidcRmDef the newPidcRmDef to set
   */
  public void setNewPidcRmDef(final PidcRmDefinition newPidcRmDef) {
    this.newPidcRmDef = newPidcRmDef;
  }

}
