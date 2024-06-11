/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.AttributeValueNotUsed;
import com.bosch.caltool.apic.jpa.bo.AttributeValueUsed;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * ICdm-1214
 *
 * @author rgo7cob Command for creation and deletion of Review Attr Values
 */
public class CmdModRvwAttrValue extends AbstractCDRCommand {


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

  private final AttributeValue attrValue;

  /**
   * cdrRvwAttrVal coming for deletion
   */
  private CDRReviewAttrValue cdrRvwAttrVal;


  private Attribute attribute;


  /**
   * @param dataProvider data provider
   * @param reviewResult review result parent
   * @param attrValue attrValue
   * @param attribute .
   */
  public CmdModRvwAttrValue(final CDRDataProvider dataProvider, final CDRResult reviewResult,
      final AttributeValue attrValue, final Attribute attribute) {

    super(dataProvider);

    this.commandMode = COMMAND_MODE.INSERT;

    this.reviewResult = reviewResult;

    this.attrValue = attrValue;

    this.attribute = attribute;
  }

  /**
   * @param dataProvider data provider
   * @param reviewResult review result parent
   * @param cdrRvwAttrVal cdrRvwAttrVal
   */
  public CmdModRvwAttrValue(final CDRDataProvider dataProvider, final CDRResult reviewResult,
      final CDRReviewAttrValue cdrRvwAttrVal) {

    super(dataProvider);

    this.commandMode = COMMAND_MODE.DELETE;

    this.reviewResult = reviewResult;

    this.cdrRvwAttrVal = cdrRvwAttrVal;

    this.attrValue = cdrRvwAttrVal.getAttrValue();
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

    final TRvwResult dbResult = getEntityProvider().getDbCDRResult(this.reviewResult.getID());

    // Create the Review Attr Val Entity
    final TRvwAttrValue dbRvwAttrVal = new TRvwAttrValue();

    // Set the Result
    dbRvwAttrVal.setTRvwResult(dbResult);
    final TabvAttrValue dbAttrVal = getDataProvider().getEntityProvider().getDbValue(this.attrValue.getID());
    // Set the attr Val
    dbRvwAttrVal.setTabvAttrValue(dbAttrVal);
    // ICDM-1317
    // Set the used flag
    if (this.attrValue instanceof AttributeValueUsed) {
      dbRvwAttrVal.setUsed(ApicConstants.YES);
    }
    else if (this.attrValue instanceof AttributeValueNotUsed) {
      dbRvwAttrVal.setUsed(ApicConstants.CODE_NO);
    }

    dbRvwAttrVal
        .setTabvAttribute(getDataProvider().getEntityProvider().getDbAttribute(this.attribute.getAttributeID()));
    setUserDetails(COMMAND_MODE.INSERT, dbRvwAttrVal, ENTITY_ID);

    getEntityProvider().registerNewEntity(dbRvwAttrVal);

    // Add the Review Attr Value to the Result.
    Set<TRvwAttrValue> tRvwValSet = getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwAttrValue();
    if (tRvwValSet == null) {
      tRvwValSet = new HashSet<TRvwAttrValue>();
    }
    tRvwValSet.add(dbRvwAttrVal);

    // Add to the Map
    this.cdrRvwAttrVal = new CDRReviewAttrValue(getDataProvider(), dbRvwAttrVal.getRvwAttrvalId());

    this.reviewResult.getReviewAttrValMap().put(this.cdrRvwAttrVal.getID(), this.cdrRvwAttrVal);

    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).setTRvwAttrValue(tRvwValSet);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    TRvwAttrValue dbRvwAttrVal = getEntityProvider().getDbRvwAttrVal(this.cdrRvwAttrVal.getID());
    final Set<TRvwAttrValue> rvwUsrSet =
        getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwAttrValue();
    // Delete the Review Attr Val From Result
    if (rvwUsrSet != null) {
      rvwUsrSet.remove(dbRvwAttrVal);
      this.reviewResult.getReviewAttrValMap().remove(this.cdrRvwAttrVal.getID());
    }

    // Delete the Entity
    getEntityProvider().deleteEntity(dbRvwAttrVal);


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
    return super.getString("", ": type '" + this.cdrRvwAttrVal + "', User '" + getPrimaryObjectIdentifier() + "'");
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
    return this.cdrRvwAttrVal == null ? null : this.cdrRvwAttrVal.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Review participant";
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
        addTransactionSummaryDetails(detailsList, getPrimaryObjectIdentifier(), "", this.cdrRvwAttrVal.toString());
        break;
      default:
        // Do nothing
        break;
    }
    this.summaryData.setObjectName("Participant");
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
    details.setModifiedItem(this.cdrRvwAttrVal.toString());
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
