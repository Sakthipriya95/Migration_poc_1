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
import com.bosch.caltool.icdm.database.entity.apic.TabvUseCaseGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModUseCaseGroup - Command handles all db operations on INSERT, UPDATE, DELETE on usecase group
 *
 * @author bne4cob
 */
public class CmdModUseCaseGroup extends AbstractCmdModUseCaseItem {

  /**
   * UCG Entity ID key
   */
  private static final String UCG_ENTITY_ID = "UCG_ENTITY_ID";

  /**
   * Use Case group being updated/created
   */
  private UseCaseGroup ucg;
  /**
   * Command For Top level Entity
   */
  private CmdModTopLevelEntity cmdTopLevel;
  /**
   * Store the transactionSummary
   */
  // ICDM-722
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * command for inserting records
   *
   * @param dataProvider apic data provider
   * @param parentUcg parent of this use case group. If this is a top level group, provide null
   */
  public CmdModUseCaseGroup(final ApicDataProvider dataProvider, final UseCaseGroup parentUcg) {
    super(dataProvider, parentUcg);
  }

  /**
   * command for updating/deleting records
   *
   * @param dataProvider apic data provider
   * @param ucGroup the group to modify/delete
   * @param deleteFlag if delete/un-delete is not to be done, set this as null. else set either ApicConstants.YES or
   *          ApicConstants.NO
   */
  public CmdModUseCaseGroup(final ApicDataProvider dataProvider, final UseCaseGroup ucGroup, final String deleteFlag) {
    super(dataProvider, ucGroup, deleteFlag);
    this.ucg = ucGroup;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {


    final TabvUseCaseGroup dbUcg = new TabvUseCaseGroup();

    dbUcg.setNameEng(getNewNameEng());
    dbUcg.setNameGer(getNewNameGer());
    dbUcg.setDescEng(getNewDescEng());
    dbUcg.setDescGer(getNewDescGer());

    setUserDetails(COMMAND_MODE.INSERT, dbUcg, UCG_ENTITY_ID);

    if (getParent() != null) {
      dbUcg.setTabvUseCaseGroup(getEntityProvider().getDbUseCaseGroup(getParent().getID()));
    }
    getEntityProvider().registerNewEntity(dbUcg);

    if (getParent() != null) {
      getEntityProvider().getDbUseCaseGroup(getParent().getID()).getTabvUseCaseGroups().add(dbUcg);
    }

    this.ucg = new UseCaseGroup(getDataProvider(), dbUcg.getGroupId());

    setUcItem(this.ucg);

    getDataCache().getAllUseCaseGroupMap().put(dbUcg.getGroupId(), this.ucg);

    // Add this use case group to the parent UCG, if this ucg is not a top level UCG
    if (getParent() != null) {
      ((UseCaseGroup) getParent()).getChildGroups().add(this.ucg);
    }
    // icdm-474 Dcn for Top level entity
    this.cmdTopLevel = new CmdModTopLevelEntity(getDataProvider(), ApicConstants.TOP_LVL_ENT_ID_UCG);
    this.childCmdStack.addCommand(this.cmdTopLevel);

    getChangedData().put(this.ucg.getID(),
        new ChangedData(ChangeType.INSERT, this.ucg.getID(), TabvUseCaseGroup.class, DisplayEventSource.COMMAND));
  }

  /**
   * Updates the name, description etc. of the use case group
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, getUcItem().getID(), TabvUseCaseGroup.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getUseCaseGroup(getUcItem().getID()).getObjectDetails());


    final TabvUseCaseGroup dbUcg = getEntityProvider().getDbUseCaseGroup(getUcItem().getID());
    validateStaleData(dbUcg);

    if (isNameChangedEng()) {
      dbUcg.setNameEng(getNewNameEng());
    }
    if (isNameChangedGer()) {
      dbUcg.setNameGer(getNewNameGer());
    }
    if (isDescChangedEng()) {
      dbUcg.setDescEng(getNewDescEng());
    }
    if (isDescChangedGer()) {
      dbUcg.setDescGer(getNewDescGer());
    }


    setUserDetails(COMMAND_MODE.UPDATE, dbUcg, UCG_ENTITY_ID);
    getChangedData().put(getUcItem().getID(), chdata);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Command for Delete Use Case Group. Delete here is only a soft-delete, meaning only the delete flag is set to true.
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, getUcItem().getID(), TabvUseCaseGroup.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getUseCaseGroup(getUcItem().getID()).getObjectDetails());

    final TabvUseCaseGroup dbUcg = getEntityProvider().getDbUseCaseGroup(getUcItem().getID());
    validateStaleData(dbUcg);


    if (isDeletedFlagChanged()) {
      // Set Deleted Flag Yes to the Use Case Group
      dbUcg.setDeletedFlag(ApicConstants.YES);
    }

    setUserDetails(COMMAND_MODE.DELETE, dbUcg, UCG_ENTITY_ID);
    getChangedData().put(getUcItem().getID(), chdata);
  }

  /**
   * Remove the UCG created from the database
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    final TabvUseCaseGroup dbUcg = getEntityProvider().getDbUseCaseGroup(getUcItem().getID());
    getEntityProvider().deleteEntity(dbUcg);

    if (getParent() != null) {
      getEntityProvider().getDbUseCaseGroup(getParent().getID()).getTabvUseCaseGroups().remove(dbUcg);
    }

    final UseCaseGroup ucgrp = getDataProvider().getUseCaseGroup(getUcItem().getID());

    if (getParent() != null) {
      ((UseCaseGroup) getParent()).getChildGroups().remove(ucgrp);
    }

    getDataCache().getAllUseCaseGroupMap().remove(ucgrp.getID());
    // icdm-474 Dcn for Top level entity
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    final TabvUseCaseGroup dbUcg = getEntityProvider().getDbUseCaseGroup(getUcItem().getID());
    validateStaleData(dbUcg);

    if (isNameChangedEng()) {
      dbUcg.setNameEng(getOldNameEng());
    }
    if (isNameChangedGer()) {
      dbUcg.setNameGer(getOldNameGer());
    }
    if (isDescChangedEng()) {
      dbUcg.setDescEng(getOldDescEng());
    }
    if (isDescChangedGer()) {
      dbUcg.setDescGer(getOldDescGer());
    }

    setUserDetails(COMMAND_MODE.UPDATE, dbUcg, UCG_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException stale data
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    final TabvUseCaseGroup dbUcg = getEntityProvider().getDbUseCaseGroup(getUcItem().getID());
    validateStaleData(dbUcg);

    // Set Deleted Flag No to the Use Case Group
    if (isDeletedFlagChanged()) {
      dbUcg.setDeletedFlag(ApicConstants.CODE_NO);
    }

    setUserDetails(COMMAND_MODE.DELETE, dbUcg, UCG_ENTITY_ID);
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
  public TabvUseCaseGroup getUcDbGrp() {
    // ICDM-229
    return getEntityProvider().getDbUseCaseGroup(this.ucg.getID());
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-177
  @Override
  protected void rollBackDataModel() {

    if (this.commandMode == COMMAND_MODE.INSERT) {
      getDataCache().getAllUseCaseGroupMap().remove(this.ucg.getID());
      if (getParent() == null) {
        // Top level UCG
        getDataCache().getTopLevelUCGSet().remove(this.ucg);
      }
      else {
        ((UseCaseGroup) getParent()).getChildGroups().remove(this.ucg);
      }
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * return the usecase group id which is subjected to modification
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.ucg == null ? null : this.ucg.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Use Case Group";
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-722
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


}
