/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.CmdModNodeAccessRight;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * ICDM-1374 This is the command to create and update rule set
 *
 * @author mkl2cob
 */
public class CmdModRuleSet extends AbstractCDRCommand {


  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Transaction summary instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID = "RULE_SET";
  /*
   * old and new values
   */
  private String oldRuleSetName;
  private String newRuleSetName;
  private String oldRuleSetDescEng;
  private String newRuleSetDescEng;
  private String oldRuleSetDescGer;
  private String newRuleSetDescGer;

  /**
   * RuleSet instance
   */
  private RuleSet ruleSet;

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  /**
   * Constructor for INSERT command mode
   *
   * @param dataProvider AbstractDataProvider
   */
  public CmdModRuleSet(final CDRDataProvider dataProvider) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * @param dataProvider CDRDataProvider
   * @param ruleSet RuleSet
   * @param isDelete true if this is delete of rule set
   */
  public CmdModRuleSet(final CDRDataProvider dataProvider, final RuleSet ruleSet, final boolean isDelete) {
    super(dataProvider);

    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.ruleSet = ruleSet;
    // initialize command with current values from UI
    setFieldsToCommand();
  }

  /**
   * set old and new values of rule set
   */
  private void setFieldsToCommand() {
    this.oldRuleSetName = this.ruleSet.getName();
    this.newRuleSetName = this.oldRuleSetName;

    this.oldRuleSetDescEng = this.ruleSet.getDescription();
    this.newRuleSetDescEng = this.oldRuleSetDescEng;

    // TODO

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    TRuleSet dbRuleSet = new TRuleSet();

    setNewRuleSetDtls(dbRuleSet);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbRuleSet);

    this.ruleSet = new RuleSet(getDataProvider(), dbRuleSet.getRsetId());

    getDataCache().getAllRuleSetMap().put(this.ruleSet.getID(), this.ruleSet);

    getChangedData().put(this.ruleSet.getID(),
        new ChangedData(ChangeType.INSERT, this.ruleSet.getID(), TRuleSet.class, DisplayEventSource.COMMAND));

    createAccessRights();

    setUserDetails(this.commandMode, dbRuleSet, ENTITY_ID);

    // TODO dcn needed?

  }

  /**
   * this method sets the new details of ruleset
   *
   * @param dbRuleSet TRuleSet
   */
  private void setNewRuleSetDtls(final TRuleSet dbRuleSet) {
    dbRuleSet.setRsetName(this.newRuleSetName);
    dbRuleSet.setDescEng(this.newRuleSetDescEng);
    dbRuleSet.setDescGer(this.newRuleSetDescGer);
    dbRuleSet.setDeletedFlag(ApicConstants.CODE_NO);
  }

  /**
   * Create a node access entity with the current user as the owner and full privileges. Command for node access
   * creation is used
   *
   * @throws CommandException In case of parallel changes
   */
  private void createAccessRights() throws CommandException {

    final CmdModNodeAccessRight cmdNodeAccess = new CmdModNodeAccessRight(getDataProvider().getApicDataProvider(),
        this.ruleSet, getDataProvider().getApicDataProvider().getCurrentUser());
    cmdNodeAccess.setGrantOption(true);
    cmdNodeAccess.setIsOwner(true);
    cmdNodeAccess.setWriteAccess(true);
    this.childCmdStack.addCommand(cmdNodeAccess);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.ruleSet.getID(), TRuleSet.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getRuleSet(this.ruleSet.getID()).getObjectDetails());
    final TRuleSet modifiedRuleSet = getEntityProvider().getDbRuleSet(this.ruleSet.getID());
    validateStaleData(modifiedRuleSet);
    // Update modified data
    setNewRuleSetDtls(modifiedRuleSet);
    setUserDetails(this.commandMode, modifiedRuleSet, ENTITY_ID);
    getChangedData().put(this.ruleSet.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.ruleSet.getID(), TRuleSet.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getRuleSet(this.ruleSet.getID()).getObjectDetails());
    final TRuleSet deletedRuleSet = getEntityProvider().getDbRuleSet(this.ruleSet.getID());
    validateStaleData(deletedRuleSet);

    // set the deleted flag, do not delete the entity
    deletedRuleSet.setDeletedFlag(ApicConstants.YES);

    // The command mode is update since we are only updating the deleted flag and not deleting the entity
    setUserDetails(COMMAND_MODE.UPDATE, deletedRuleSet, ENTITY_ID);
    getChangedData().put(this.ruleSet.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TRuleSet dbRuleSet = getEntityProvider().getDbRuleSet(this.ruleSet.getID());
    validateStaleData(dbRuleSet);
    // unregister the rule set
    getEntityProvider().deleteEntity(dbRuleSet);
    // remove the rule set from the cache
    getDataCache().getAllRuleSetMap().remove(this.ruleSet.getID());
    getChangedData().put(this.ruleSet.getID(),
        new ChangedData(ChangeType.INSERT, this.ruleSet.getID(), TRuleSet.class, DisplayEventSource.COMMAND));

    // TODO Dcn for Top level entity
    this.childCmdStack.undoAll();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.ruleSet.getID(), TRuleSet.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getRuleSet(this.ruleSet.getID()).getObjectDetails());
    final TRuleSet modifiedRuleSet = getEntityProvider().getDbRuleSet(this.ruleSet.getID());
    validateStaleData(modifiedRuleSet);
    // Update modified data,
    setOldCpFields(modifiedRuleSet);
    setUserDetails(this.commandMode, modifiedRuleSet, ENTITY_ID);

    getChangedData().put(this.ruleSet.getID(), chdata);

  }

  /**
   * @param modifiedRuleSet TRuleSet
   */
  private void setOldCpFields(final TRuleSet modifiedRuleSet) {
    modifiedRuleSet.setRsetName(this.oldRuleSetName);
    modifiedRuleSet.setDescEng(this.oldRuleSetDescEng);
    modifiedRuleSet.setDescGer(this.oldRuleSetDescGer);
    modifiedRuleSet.setDeletedFlag(ApicConstants.CODE_NO);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.ruleSet.getID(), TRuleSet.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getRuleSet(this.ruleSet.getID()).getObjectDetails());
    final TRuleSet modifiedRuleSet = getEntityProvider().getDbRuleSet(this.ruleSet.getID());
    validateStaleData(modifiedRuleSet);
    // Update modified data,
    setOldCpFields(modifiedRuleSet);
    setUserDetails(this.commandMode, modifiedRuleSet, ENTITY_ID);

    getChangedData().put(this.ruleSet.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldRuleSetName, this.newRuleSetName) ||
        isStringChanged(this.oldRuleSetDescEng, this.newRuleSetDescEng) ||
        isStringChanged(this.oldRuleSetDescGer, this.newRuleSetDescGer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());
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
      case UPDATE:
        caseCmdUpd(detailsList);
        break;
      case DELETE: // no details section necessary in case of delete (parent row is sufficient in transansions view)
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
  private void caseCmdUpd(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldRuleSetName, this.newRuleSetName, "Name ");
    addTransactionSummaryDetails(detailsList, this.oldRuleSetDescEng, this.newRuleSetDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldRuleSetDescGer, this.newRuleSetDescGer, "Description (German)");
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
  protected void doPostCommit() {
    // Not applicable

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
  public Long getPrimaryObjectID() {
    return null == this.ruleSet ? null : this.ruleSet.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Rule Set";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier = "";
    if (null != this.ruleSet.getID()) {
      final TRuleSet dbCp = getEntityProvider().getDbRuleSet(this.ruleSet.getID());
      if (null != dbCp) {
        objectIdentifier = dbCp.getRsetName();
      }
    }
    return objectIdentifier;
  }


  /**
   * @param newRuleSetName the newRuleSetName to set
   */
  public void setNewRuleSetName(final String newRuleSetName) {
    this.newRuleSetName = newRuleSetName;
  }


  /**
   * @param newRuleSetDescEng the newRuleSetDescEng to set
   */
  public void setNewRuleSetDescEng(final String newRuleSetDescEng) {
    this.newRuleSetDescEng = newRuleSetDescEng;
  }


  /**
   * @param newRuleSetDescGer the newRuleSetDescGer to set
   */
  public void setNewRuleSetDescGer(final String newRuleSetDescGer) {
    // do not allow empty spaces, save as null
    if (newRuleSetDescGer != null) {
      if (newRuleSetDescGer.isEmpty()) {
        this.newRuleSetDescGer = "";
      }
      else {
        this.newRuleSetDescGer = newRuleSetDescGer;
      }
    }
  }
}
