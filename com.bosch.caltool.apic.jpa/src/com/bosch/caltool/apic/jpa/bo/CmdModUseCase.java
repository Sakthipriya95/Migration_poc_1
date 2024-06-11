/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCase;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModUseCase- Command handles all db operations on INSERT, UPDATE, DELETE on usecase
 *
 * @author bru2cob icdm-356
 */
public class CmdModUseCase extends AbstractCmdModUseCaseItem {

  /**
   * UC Entity ID
   */
  private static final String UC_ENTITY_ID = "UC_ENTITY_ID";
  private UseCase useCase;


  /**
   * Store the transactionSummary - ICDM-722
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Create a new command for inserting records
   *
   * @param dataProvider apic data provider
   * @param ucGroup parent
   */
  public CmdModUseCase(final ApicDataProvider dataProvider, final UseCaseGroup ucGroup) {
    super(dataProvider, ucGroup);

  }

  /**
   * Create a new command for updating/deleting records
   *
   * @param dataProvider apic data provider
   * @param useCase useCase
   * @param deleteFlag if delete/un-delete is not to be done, set this as null. else set either ApicConstants.YES or
   *          ApicConstants.NO
   */
  public CmdModUseCase(final ApicDataProvider dataProvider, final UseCase useCase, final String deleteFlag) {
    super(dataProvider, useCase, deleteFlag);
    this.useCase = useCase;
    // ICDM-1558
    this.oldFocusMatrxMapping = this.useCase.isFocusMatrixRelevant(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    this.childCmdStack.clear();

    // Create new DB Usecase attribute
    final TabvUseCase dbUc = new TabvUseCase();

    // Set Usecase details
    dbUc.setNameEng(getNewNameEng());
    dbUc.setNameGer(getNewNameGer());
    dbUc.setDescEng(getNewDescEng());
    dbUc.setDescGer(getNewDescGer());
    if (this.newFocusMatrxMapping) {
      dbUc.setFocusMatrixRelevant(ApicConstants.YES);
    }
    else {
      dbUc.setFocusMatrixRelevant(ApicConstants.CODE_NO);
    }

    // Insert new Usecase
    setUserDetails(COMMAND_MODE.INSERT, dbUc, UC_ENTITY_ID);

    if (getParent() != null) {
      dbUc.setTabvUseCaseGroup(getEntityProvider().getDbUseCaseGroup(getParent().getID()));
    }
    getEntityProvider().registerNewEntity(dbUc);

    if (getParent() != null) {
      getEntityProvider().getDbUseCaseGroup(getParent().getID()).getTabvUseCases().add(dbUc);
    }

    this.useCase = new UseCase(getDataProvider(), dbUc.getUseCaseId());

    setUcItem(this.useCase);
    getDataCache().getAllUseCaseMap().put(dbUc.getUseCaseId(), this.useCase);

    // Add use case to the parent
    if (getParent() != null) {
      ((UseCaseGroup) getParent()).getUseCases().add(this.useCase);
    }
    // Add use case Rights icdm-368
    createAccessRights();

    // Add changed data
    getChangedData().put(this.useCase.getID(),
        new ChangedData(ChangeType.INSERT, this.useCase.getID(), TabvUseCase.class, DisplayEventSource.COMMAND));
  }

  /**
   * create access rights for the new use case icdm-368
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */

  private void createAccessRights() throws CommandException {

    final CmdModNodeAccessRight cmdNodeAccess =
        new CmdModNodeAccessRight(getDataProvider(), this.useCase, getDataProvider().getCurrentUser());

    // Grant req access rights for usecase
    cmdNodeAccess.setGrantOption(true);
    cmdNodeAccess.setIsOwner(true);
    cmdNodeAccess.setWriteAccess(true);
    this.childCmdStack.addCommand(cmdNodeAccess);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {

    // Modified Usecase data
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, getUcItem().getID(), TabvUseCase.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getUseCase(getUcItem().getID()).getObjectDetails());

    final TabvUseCase dbUc = getEntityProvider().getDbUseCase(getUcItem().getID());
    validateStaleData(dbUc);

    if (isNameChangedEng()) {
      dbUc.setNameEng(getNewNameEng());
    }
    if (isNameChangedGer()) {
      dbUc.setNameGer(getNewNameGer());
    }
    if (isDescChangedEng()) {
      dbUc.setDescEng(getNewDescEng());
    }
    if (isDescChangedGer()) {
      dbUc.setDescGer(getNewDescGer());
    }
    if (isFocusMatrixMappingChanged()) { // ICDM-1558
      if (this.newFocusMatrxMapping) {
        dbUc.setFocusMatrixRelevant(ApicConstants.YES);
      }
      else {
        dbUc.setFocusMatrixRelevant(ApicConstants.CODE_NO);
      }
    }
    if (isLastconfirmationdateChanged()) {
      dbUc.setLastConfirmationDate(getLastConfirmationDate());
    }
    if (this.cmdMultipleLinks != null) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }
    setUserDetails(COMMAND_MODE.UPDATE, dbUc, UC_ENTITY_ID);
    getChangedData().put(getUcItem().getID(), chdata);
  }

  /**
   * {@inheritDoc} Added Delete Command for use case icdm-299
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, getUcItem().getID(), TabvUseCase.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getUseCase(getUcItem().getID()).getObjectDetails());

    // Get Usecase DB attribute
    final TabvUseCase dbUc = getEntityProvider().getDbUseCase(getUcItem().getID());
    validateStaleData(dbUc);
    if (isDeletedFlagChanged()) {
      // Set Deleted Flag Yes to the Use Case
      dbUc.setDeletedFlag(ApicConstants.YES);
    }

    setUserDetails(COMMAND_MODE.DELETE, dbUc, UC_ENTITY_ID);
    getChangedData().put(getUcItem().getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() {
    final TabvUseCase dbUc = getEntityProvider().getDbUseCase(getUcItem().getID());
    getEntityProvider().deleteEntity(dbUc);

    if (getParent() != null) {
      getEntityProvider().getDbUseCaseGroup(getParent().getID()).getTabvUseCases().remove(dbUc);
    }

    getDataCache().getAllUseCaseMap().remove(getUcItem().getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    final TabvUseCase dbUc = getEntityProvider().getDbUseCase(getUcItem().getID());
    validateStaleData(dbUc);

    if (isNameChangedEng()) {
      dbUc.setNameEng(getOldNameEng());
    }
    if (isNameChangedGer()) {
      dbUc.setNameGer(getOldNameGer());
    }
    if (isDescChangedEng()) {
      dbUc.setDescEng(getOldDescEng());
    }
    if (isDescChangedGer()) {
      dbUc.setDescGer(getOldDescGer());
    }
    if (isFocusMatrixMappingChanged()) { // ICDM-1558
      if (this.oldFocusMatrxMapping) {
        dbUc.setFocusMatrixRelevant(ApicConstants.YES);
      }
      else {
        dbUc.setFocusMatrixRelevant(ApicConstants.CODE_NO);
      }
    }
    setUserDetails(COMMAND_MODE.UPDATE, dbUc, UC_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException while validating stale data
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    final TabvUseCase dbUc = getEntityProvider().getDbUseCase(getUcItem().getID());
    validateStaleData(dbUc);
    if (isDeletedFlagChanged()) {
      // Set Deleted Flag No to the Use Case
      dbUc.setDeletedFlag(ApicConstants.CODE_NO);
    }

    setUserDetails(COMMAND_MODE.DELETE, dbUc, UC_ENTITY_ID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void doPostCommit() {
    // Not applicable
  }

  /**
   * Get newly added UC group to enable selection after adding
   *
   * @return TabvAttribute newDbAttr
   */
  public TabvUseCase getDbUseCase() {
    // ICDM-229
    return getEntityProvider().getDbUseCase(this.useCase.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    this.childCmdStack.rollbackAll(getExecutionMode());

    // Switch based on command mode
    switch (this.commandMode) {
      case INSERT:
        getDataCache().getAllUseCaseMap().remove(this.useCase.getID());
        if (getParent() != null) {
          ((UseCaseGroup) getParent()).getUseCases().remove(this.useCase);
        }
        break;
      default:
        // Do nothing
        break;
    }
  }


  /**
   * {@inheritDoc} return the usecase id that has been subjected to modification
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.useCase == null ? null : this.useCase.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Use Case";
  }

  /**
   * ICDM 722 {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {


    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    // Switch based on command mode : Insert or update
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        caseCmdUpd(detailsList);
        break;
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        if (isDeletedFlagChanged()) {
          this.summaryData.setOperation("DELETE");
        }
        break;
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
    addTransactionSummaryDetails(detailsList, getOldDescEng(), getNewDescEng(), "Description (English)");
    addTransactionSummaryDetails(detailsList, getOldDescGer(), getNewDescGer(), "Description (German)");
    addTransactionSummaryDetails(detailsList, getOldNameEng(), getNewNameEng(), "Name (English)");
    addTransactionSummaryDetails(detailsList, getOldNameGer(), getNewNameGer(), "Name  (German)");
    addTransactionSummaryDetails(detailsList, Boolean.toString(this.oldFocusMatrxMapping),
        Boolean.toString(this.newFocusMatrxMapping), "Relevant to Focus Matrix");
    this.summaryData.setObjectName(getNewNameEng());
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * @param cmdMultipleLinks CmdModMultipleLinks
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;
  }
}
