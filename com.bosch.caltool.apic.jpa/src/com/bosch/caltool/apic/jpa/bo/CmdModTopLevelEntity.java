package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;

/**
 * Command class to insert, update, delete Super Groups.
 */
public class CmdModTopLevelEntity extends AbstractCmdModProjGroup {

  /*
   * entityID of the TabvTopLevelEntity
   */
  private final Long entityID;


  /**
   * Constructor to add a new {@link AttrSuperGroup} - use this constructor for INSERT
   *
   * @param apicDataProvider the Apic Data Provider
   * @param entityID entityID
   */
  public CmdModTopLevelEntity(final ApicDataProvider apicDataProvider, final Long entityID) {
    super(apicDataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.entityID = entityID;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Check for any parallel changes
    // ICDM-229 changes for DB notifications
    final TabvTopLevelEntity modifiedtopLevel = getEntityProvider().getDbTopLevelEntity(this.entityID);
    checkParallelChanges(modifiedtopLevel);

    // set ModifiedDate and User

    modifiedtopLevel.setLastModDate(getCurrentTime());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO: Not required at the moment
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    // Do nothing


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Check for any parallel changes
    // ICDM-229 changes for DB notifications
    final TabvTopLevelEntity modifiedtopLevel = getEntityProvider().getDbTopLevelEntity(this.entityID);
    checkParallelChanges(modifiedtopLevel);
    modifiedtopLevel.setLastModDate(getCurrentTime());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    // TODO: Not required at the moment
  }


  /**
   * Method checks for parallel changes in DB (using version )
   */
  private void checkParallelChanges(final TabvTopLevelEntity dbTop) {
    // check is parallel change happened
    final Long oldVersion = dbTop.getVersion();
    // Refresh cache
    TabvTopLevelEntity refreshedTop = (TabvTopLevelEntity) getEntityProvider().refreshCacheObject(dbTop);
    // get new version
    final Long newVersion = refreshedTop.getVersion();
    // log a warning in case of parallel changes
    if (!oldVersion.equals(newVersion)) {
      getEntityProvider().logger.warn("parallel change in Super Group: " + refreshedTop.getEntId());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Not Applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      default:
        // Do nothing
        break;
    }
  }

  /**
   * {@inheritDoc} returns the id of the super group that has been modified
   */
  @Override
  public Long getPrimaryObjectID() {

    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return this.entityID;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Top Level Entity";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // Do Nothing

  }


  /**
   * @return the entityID
   */
  public Long getEntityID() {
    return this.entityID;
  }


  @Override
  protected boolean dataChanged() {
    return true;

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
  public String getPrimaryObjectIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }
}
