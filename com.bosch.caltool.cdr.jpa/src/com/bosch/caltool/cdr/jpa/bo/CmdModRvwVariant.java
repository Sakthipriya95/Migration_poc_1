/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;


/**
 * ICdm-1214
 *
 * @author rgo7cob Command for creation and deletion of Review Attr Values
 */
public class CmdModRvwVariant extends AbstractCDRCommand {


  /**
   * Parent review result
   */
  private final CDRResult reviewResult;


  /**
   * Review function entity ID
   */
  private static final String ENTITY_ID = "RVW_USR";

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * pidc variant
   */
  private final PIDCVariant variant;


  /**
   * cdr review variant
   */
  private CDRReviewVariant cdrRvwVaraint;


  /**
   * @param dataProvider data provider
   * @param reviewResult review result parent
   * @param variant variant
   */
  public CmdModRvwVariant(final CDRDataProvider dataProvider, final CDRResult reviewResult, final PIDCVariant variant) {

    super(dataProvider);

    this.commandMode = COMMAND_MODE.INSERT;

    this.reviewResult = reviewResult;

    this.variant = variant;

  }

  /**
   * @param dataProvider data provider
   * @param cdrRvwVaraint cdrRvwVaraint
   */
  public CmdModRvwVariant(final CDRDataProvider dataProvider, final CDRReviewVariant cdrRvwVaraint) {

    super(dataProvider);

    this.commandMode = COMMAND_MODE.DELETE;

    this.reviewResult = cdrRvwVaraint.getResult();

    this.cdrRvwVaraint = cdrRvwVaraint;

    this.variant = cdrRvwVaraint.getVariant();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // No object added to the Cache
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // get the db result
    final TRvwResult dbResult = getEntityProvider().getDbCDRResult(this.reviewResult.getID());
    // get the db variant
    final TabvProjectVariant dbVar = getEntityProvider().getDbPidcVariant(this.variant.getID());
    // Create the Review Attr Val Entity
    final TRvwVariant dbRvwVar = new TRvwVariant();
    // Set the Result
    dbRvwVar.setTRvwResult(dbResult);
    // Set the variant
    dbRvwVar.setTabvProjectVariant(dbVar);

    setUserDetails(COMMAND_MODE.INSERT, dbRvwVar, ENTITY_ID);

    getEntityProvider().registerNewEntity(dbRvwVar);
    // Add the Review Variant to the Result.
    Set<TRvwVariant> tRvwValSet = getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwVariants();
    if (tRvwValSet == null) {
      tRvwValSet = new HashSet<TRvwVariant>();
    }
    tRvwValSet.add(dbRvwVar);
    // create the new object
    this.cdrRvwVaraint = new CDRReviewVariant(getDataProvider(), dbRvwVar.getRvwVarId());
    // Add to the Map
    this.reviewResult.getReviewVarMap().put(this.cdrRvwVaraint.getID(), this.cdrRvwVaraint);

    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).setTRvwVariants(tRvwValSet);

    getChangedData().put(this.cdrRvwVaraint.getID(),
        new ChangedData(ChangeType.INSERT, this.cdrRvwVaraint.getID(), TRvwVariant.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.cdrRvwVaraint.getID(), TRvwVariant.class, DisplayEventSource.COMMAND);
    // get the review variant
    TRvwVariant dbRvwVar = getEntityProvider().getDbRvwVaraint(this.cdrRvwVaraint.getID());
    TRvwResult dbCDRResult = getEntityProvider().getDbCDRResult(this.reviewResult.getID());
    dbCDRResult.getTRvwVariants().remove(dbRvwVar);
    // get the review var set.
    final Set<TRvwVariant> rvwVarSet = dbCDRResult.getTRvwVariants();
    // Delete the Review variant From Result
    if (rvwVarSet != null) {
      rvwVarSet.remove(dbRvwVar);
      this.reviewResult.getReviewVarMap().remove(this.cdrRvwVaraint.getID());
    }
    // Delete the Entity
    getEntityProvider().deleteEntity(dbRvwVar);

    // Icdm-461 Changes
    getChangedData().put(this.cdrRvwVaraint.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Not implemented for now

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // No implementation required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // No implementation required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", ": type '" + this.cdrRvwVaraint + "', User '" + getPrimaryObjectIdentifier() + "'");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.cdrRvwVaraint == null ? null : this.cdrRvwVaraint.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Review variant";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        addTransactionSummaryDetails(detailsList, getPrimaryObjectIdentifier(), "", this.cdrRvwVaraint.toString());
        break;
      default:
        // Do nothing
        break;
    }
    this.summaryData.setObjectName("Review Variant");
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
    details.setModifiedItem(this.cdrRvwVaraint.toString());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String returnStr;
    switch (this.commandMode) {
      case INSERT:
      case DELETE:
        returnStr = this.reviewResult.getName();
        break;
      default:
        returnStr = "";
    }
    return returnStr;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

}
