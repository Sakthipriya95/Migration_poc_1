/*
 * CmdModCmpPkg.java Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;

/**
 * CmdModCmpPkgBCFC.java - Command handles db operations on INSERT,UPDATE & DELETE on FC tagged to a BC in a component
 * pkg
 *
 * @author dmo5cob
 */
@Deprecated
public class CmdModCmpPkgBCFC extends AbstractCPCommand {

  /**
   * the Component package BC object instance
   */
  private final CompPkgBc compPkgBc;

  /**
   * the Component package BC object instance
   */
  private CompPkgBcFc compPkgBcFc;
  /**
   * Other old and new parameters
   */
  private String oldFCName;
  /**
   * New FC name
   */
  private String newFCName;
  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  /**
   * Transaction summary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID = "COMP_PKG_BC_FC";

  /**
   * Constructor to add a new fc - use this constructor for INSERT
   *
   * @param cpDataProvider data provider
   * @param compPkgBC selected BC obj
   */
  public CmdModCmpPkgBCFC(final CPDataProvider cpDataProvider, final CompPkgBc compPkgBC) {
    super(cpDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.compPkgBc = compPkgBC;

  }

  /**
   * Constructor to delete or update existing cmp pkg
   *
   * @param cpDataProvider data provider
   * @param modifyFC the updated cmppkg
   * @param isDelete whether to delete or not
   * @param compPkgBC selected BC obj
   */
  public CmdModCmpPkgBCFC(final CPDataProvider cpDataProvider, final CompPkgBcFc modifyFC, final boolean isDelete,
      final CompPkgBc compPkgBC) {
    super(cpDataProvider);
    this.compPkgBc = compPkgBC;
    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.compPkgBcFc = modifyFC;

    // initialize command with current values from UI
    setCpFieldsToCommand(modifyFC);
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifycmp pkg
   */
  private void setCpFieldsToCommand(final CompPkgBcFc modifyFC) {
    // Name
    this.oldFCName = modifyFC.getFcName();
    this.newFCName = this.oldFCName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() {
    // create a new database entity
    final TCompPkgBcFc newDbBCFC = new TCompPkgBcFc();

    setNewCpFields(newDbBCFC);
    newDbBCFC.setTCompPkgBc(getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID()));
    setUserDetails(this.commandMode, newDbBCFC, ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbBCFC);

    getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID()).getTCompPkgBcFcs().add(newDbBCFC);

    // add the new FC to the list of all CompPkgs
    final CompPkgBcFc compPkgFcObj = new CompPkgBcFc(getDataProvider(), newDbBCFC.getCompBcFcId());

    this.compPkgBcFc = compPkgFcObj;

    this.compPkgBc.getCompPkgBcFcsMap().put(this.compPkgBcFc.getID(), this.compPkgBcFc);
    getChangedData().put(this.compPkgBcFc.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkgBcFc.getID(), TCompPkgBcFc.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {

    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.compPkgBc.getID(), TCompPkgBcFc.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.compPkgBc.getCompPkgBcFcsMap().get(this.compPkgBcFc.getID()).getObjectDetails());
    final TCompPkgBcFc deletedFC = getEntityProvider().getDbCompPkgBcFc(this.compPkgBcFc.getID());
    validateStaleData(deletedFC);

    // delete the entity
    getEntityProvider().deleteEntity(deletedFC);

    getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID()).getTCompPkgBcFcs().remove(deletedFC);

    this.compPkgBc.getCompPkgBcFcsMap().remove(this.compPkgBcFc.getID());

    getChangedData().put(this.compPkgBcFc.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TCompPkgBcFc dbFC = getEntityProvider().getDbCompPkgBcFc(this.compPkgBcFc.getID());
    validateStaleData(dbFC);
    // unregister the cp
    getEntityProvider().deleteEntity(dbFC);

    this.compPkgBc.getCompPkgBcFcsMap().remove(dbFC.getCompBcFcId());

    getChangedData().put(this.compPkgBcFc.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkgBcFc.getID(), TCompPkgBcFc.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    final TCompPkgBcFc newDbCpBcFc = new TCompPkgBcFc();

    newDbCpBcFc.setFcName(this.oldFCName);

    newDbCpBcFc.setTCompPkgBc(getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID()));
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbCpBcFc);

    getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID()).getTCompPkgBcFcs().add(newDbCpBcFc);
    // add the new CompPkgBcFc to the list in BC
    // (the constructor of CompPkg will add the new object)
    final CompPkgBcFc compPkgBcFcObj = new CompPkgBcFc(getDataProvider(), newDbCpBcFc.getCompBcFcId());

    this.compPkgBcFc = compPkgBcFcObj;
    this.compPkgBc.getCompPkgBcFcsMap().put(this.compPkgBcFc.getID(), this.compPkgBcFc);
    getChangedData().put(this.compPkgBcFc.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkgBcFc.getID(), TCompPkgBcFc.class, DisplayEventSource.COMMAND));

  }

  /**
   * Method sets the required fields of the TCompPkg entity
   */
  private void setNewCpFields(final TCompPkgBcFc dbFC) {
    // set name and desc fields
    dbFC.setFcName(this.newFCName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());
  }

  /**
   * Set the new Name
   *
   * @param newFCName The new fc name
   */
  public void setBCFCName(final String newFCName) {
    this.newFCName = newFCName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable
  }

  /**
   * Get newly added fc to enable selection after adding
   *
   * @return TCompPkgBcFC getNewDbCp
   */
  public TCompPkgBcFc getNewDbFC() {
    return getEntityProvider().getDbCompPkgBcFc(this.compPkgBcFc.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        this.compPkgBc.getCompPkgBcFcsMap().remove(this.compPkgBcFc.getID());
        getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID()).getTCompPkgBcFcs().remove(this.compPkgBcFc);
        break;
      case UPDATE:
      case DELETE:
      default:
        // Do nothing
        break;
    }
  }

  /**
   * {@inheritDoc} return the id of the new attr in case of insert & update mode, return the id of the old attr in case
   * of delete mode
   */
  @Override
  public Long getPrimaryObjectID() {
    return null == this.compPkgBcFc ? null : this.compPkgBcFc.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Function";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case DELETE:
        addTransactionSummaryDetails(detailsList, this.oldFCName, "", "FC Name");
        break;
      case UPDATE:
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier;
    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = this.newFCName;
        break;
      case UPDATE:
      case DELETE:
        objectIdentifier = this.oldFCName;
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }
    return objectIdentifier;
  }

}
