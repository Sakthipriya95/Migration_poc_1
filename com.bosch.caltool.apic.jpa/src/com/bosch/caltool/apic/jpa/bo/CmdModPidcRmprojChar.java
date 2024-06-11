/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TPidcRmProjectCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;

/**
 * @author dja7cob Command for Pidc Project Char
 */
public class CmdModPidcRmprojChar extends AbstractCommand {

  /**
   * Initial version
   */
  private static final long FIRST_VERSION = 1L;

  private static final String PIDC_RM_PROJ_CHAR_ENTITY_ID = "PIDC_RM_DEF_ENTITY_ID";
  /**
   * PidcRmDefinition
   */
  private final PidcRmDefinition newPidcRmDef;

  private final PidcRmProjCharacter oldProjchar;

  /**
   * @param dataProvider ApicDataProvider instance
   * @param pidcRmDef
   * @param oldProjchar
   */
  protected CmdModPidcRmprojChar(final ApicDataProvider dataProvider, final PidcRmDefinition newPidcRmDef,
      final PidcRmProjCharacter oldProjchar) {
    super(dataProvider);
    this.newPidcRmDef = newPidcRmDef;
    this.oldProjchar = oldProjchar;
    this.commandMode = COMMAND_MODE.INSERT;
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
    TPidcRmProjectCharacter dbPidcRmProjChar = new TPidcRmProjectCharacter();

    dbPidcRmProjChar.setRbData(getEntityProvider().getDbRbData(this.oldProjchar.getRbDataId()));
    dbPidcRmProjChar.setRbShare(getEntityProvider().getDbRbData(this.oldProjchar.getRbShareId()));
    if (null != this.oldProjchar.getRelevant()) {
      dbPidcRmProjChar.setRelevantFlag(this.oldProjchar.getRelevant());
    }
    dbPidcRmProjChar.setTPidcRmDefinition(getEntityProvider().getDbRmDef(this.newPidcRmDef.getId()));
    dbPidcRmProjChar.setTRmProjectCharacter(getEntityProvider().getDbRmProjchar(this.oldProjchar.getProjCharId()));
    dbPidcRmProjChar.setVersion(FIRST_VERSION);
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbPidcRmProjChar);
    setUserDetails(COMMAND_MODE.INSERT, dbPidcRmProjChar, PIDC_RM_PROJ_CHAR_ENTITY_ID);
    getChangedData().put(this.oldProjchar.getId(), new ChangedData(ChangeType.INSERT, this.oldProjchar.getId(),
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

}
