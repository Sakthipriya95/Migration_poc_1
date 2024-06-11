/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;

/**
 * @author bne4cob
 */
//ICDM-2569
public class CmdModFocusMatrixVersionAttr extends AbstractCommand {


  /**
   * Entity id
   */
  private static final String ENTITY_ID = "FOCUS_MATRIX_VERS_ATTR_ENTITY_ID";

  private final FocusMatrixVersion fmVersion;
  private final IPIDCAttribute projAttr;

  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Child command stack instance
   */
  private final ChildCommandStack childCmdStk = new ChildCommandStack(this);

  private FocusMatrixVersionAttr fmVersAttr;

  /**
   * @param fmVers parent focus matrix version
   * @param projAttr project attribute
   */
  protected CmdModFocusMatrixVersionAttr(final FocusMatrixVersion fmVers, final IPIDCAttribute projAttr) {
    super(fmVers.getDataCache().getDataProvider());

    this.fmVersion = fmVers;
    this.projAttr = projAttr;

    this.commandMode = COMMAND_MODE.INSERT;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    if (this.projAttr.isVariant()) {
      // create db row for variant level attributes
      createFmVerAttrForChildren();
    }
    else {
      createNewDbVersAttr();
    }
  }


  /**
   *
   */
  private void createNewDbVersAttr() {
    // Create DB row for current level attrs
    TFocusMatrixVersionAttr dbFmVersAttr = new TFocusMatrixVersionAttr();
    dbFmVersAttr.setTFocusMatrixVersion(getEntityProvider().getDbFocuMatrixVersion(this.fmVersion.getID()));

    if (this.projAttr instanceof PIDCAttributeSubVar) {
      PIDCVariant var = ((PIDCAttributeSubVar) this.projAttr).getPidcVariant();
      PIDCSubVariant subVar = ((PIDCAttributeSubVar) this.projAttr).getPidcSubVariant();
      dbFmVersAttr.setTabvProjectVariant(getEntityProvider().getDbPidcVariant(var.getID()));
      dbFmVersAttr.setTabvProjectSubVariant(getEntityProvider().getDbPidcSubVariant(subVar.getID()));
    }
    else if (this.projAttr instanceof PIDCAttributeVar) {
      PIDCVariant var = ((PIDCAttributeVar) this.projAttr).getPidcVariant();
      dbFmVersAttr.setTabvProjectVariant(getEntityProvider().getDbPidcVariant(var.getID()));
    }

    dbFmVersAttr.setTabvAttribute(getEntityProvider().getDbAttribute(this.projAttr.getAttribute().getID()));
    dbFmVersAttr.setUsed(this.projAttr.getIsUsedEnum().getDbType());

    AttributeValue attrVal = this.projAttr.getAttributeValue();
    if (attrVal != null) {
      dbFmVersAttr.setTabvAttrValue(getEntityProvider().getDbValue(attrVal.getID()));
    }
    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, dbFmVersAttr, ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbFmVersAttr);

    getEntityProvider().getDbFocuMatrixVersion(this.fmVersion.getID()).addTFocusMatrixVersionAttr(dbFmVersAttr);

    this.fmVersAttr = new FocusMatrixVersionAttr(getDataProvider(), dbFmVersAttr.getFmvAttrId());

    // Fm Version Attr object is not added to the parent collection, since this is only a one time action.

    getChangedData().put(dbFmVersAttr.getFmvAttrId(), new ChangedData(ChangeType.INSERT, dbFmVersAttr.getFmvAttrId(),
        TFocusMatrixVersionAttr.class, DisplayEventSource.COMMAND));

  }

  /**
   * @throws CommandException
   */
  private void createFmVerAttrForChildren() throws CommandException {
    if (this.projAttr instanceof PIDCAttribute) {
      PIDCVersion pidcVers = this.projAttr.getPidcVersion();
      for (PIDCVariant pidcVar : pidcVers.getVariantsMap(false).values()) {
        PIDCAttributeVar varAttr = pidcVar.getAttributes(false).get(this.projAttr.getAttribute().getID());
        CmdModFocusMatrixVersionAttr fmVersAttrChildCmd = new CmdModFocusMatrixVersionAttr(this.fmVersion, varAttr);
        this.childCmdStk.addCommand(fmVersAttrChildCmd);
      }
    }
    else if (this.projAttr instanceof PIDCAttributeVar) {
      PIDCVariant pidcVar = ((PIDCAttributeVar) this.projAttr).getPidcVariant();
      for (PIDCSubVariant subVar : pidcVar.getSubVariantsMap(false).values()) {
        PIDCAttributeSubVar svarAttr = subVar.getAttributes(false).get(this.projAttr.getAttribute().getID());
        CmdModFocusMatrixVersionAttr fmVersAttrChildCmd = new CmdModFocusMatrixVersionAttr(this.fmVersion, svarAttr);
        this.childCmdStk.addCommand(fmVersAttrChildCmd);
      }
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not applicable
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
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // Not applicable
    return false;
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
    // insert mode
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    TransactionSummaryDetails details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);

    this.summaryData.setObjectName(getPrimaryObjectIdentifier());
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
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return CommonUtils.isNull(this.fmVersAttr) ? null : this.fmVersAttr.getID();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Focus Matrix Version Attribute";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return (this.fmVersAttr == null) ? " INVALID!" : this.fmVersAttr.getName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.fmVersAttr != null) {
      getDataCache().addRemoveFocusMatrixVersionAttr(this.fmVersAttr, false);
    }
  }


  /**
   * @return the fmVersAttr
   */
  FocusMatrixVersionAttr getFmVersAttr() {
    return this.fmVersAttr;
  }

}
