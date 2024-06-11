/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;

/**
 * @author bru2cob
 */
public class CmdModWrkPkgDivision extends AbstractCDRCommand {

  private static final String WRKPKG_DIV_ENTITY_ID = "WRKPKG_DIV_ENTITY_ID";

  /**
   * attribute value for the alias
   */
  private final AttributeValue attrValue;
  /**
   * Selected workPackage
   */
  private final IcdmWorkPackage workPackage;

  private WorkPackageDivision wrkPkgDiv;
  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor to add a new Attribute - use this constructor for INSERT
   *
   * @param apicDataProvider data provider
   * @param attrValue attr value
   * @param workPackage wp
   */
  public CmdModWrkPkgDivision(final CDRDataProvider apicDataProvider, final AttributeValue attrValue,
      final IcdmWorkPackage workPackage) {
    super(apicDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.workPackage = workPackage;
    this.attrValue = attrValue;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        if (null != this.wrkPkgDiv.getID()) {
          getDataCache().getWrkPkgDivisionMap().remove(this.wrkPkgDiv.getID());
          getDataCache().getMappedWrkPkgMap().get(this.attrValue).remove(this.wrkPkgDiv);
        }
        break;
      default:
        // Do nothing
        break;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    TWorkpackageDivision dbWPDiv = new TWorkpackageDivision();
    dbWPDiv.setTabvAttrValue(getEntityProvider().getDbValue(this.attrValue.getValueID()));
    dbWPDiv.setTWorkpackage(getEntityProvider().getDbIcdmWorkPackage(this.workPackage.getID()));
    setUserDetails(COMMAND_MODE.INSERT, dbWPDiv, WRKPKG_DIV_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbWPDiv);
    this.wrkPkgDiv = new WorkPackageDivision(getDataProvider(), dbWPDiv.getWpDivId());
    getChangedData().put(dbWPDiv.getWpDivId(), new ChangedData(ChangeType.INSERT, dbWPDiv.getWpDivId(),
        TWorkpackageDivision.class, DisplayEventSource.COMMAND));
    getDataLoader().addToMappedWpMap(this.wrkPkgDiv);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // NOT-APPLICABLE
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
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    if (this.commandMode == COMMAND_MODE.INSERT) {
      final TransactionSummaryDetails details = new TransactionSummaryDetails();
      details.setOldValue("");
      details.setNewValue(getPrimaryObjectIdentifier());
      details.setModifiedItem(getPrimaryObjectType());
      detailsList.add(details);
    }

    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
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
    return this.wrkPkgDiv == null ? null : this.wrkPkgDiv.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Workpackage Division";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.wrkPkgDiv.getName();
  }
}
