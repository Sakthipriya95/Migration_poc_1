/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjSubVariantsAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModProjSubVariantAttr.java-Command handles all db operations on INSERT, UPDATE, DELETE on Project Subvariant
 *
 * @author adn1cob
 */
public class CmdModProjSubVariantAttr extends AbstractCmdModProjAttr {

  /**
   * Sub Variant's entity ID
   */
  private static final String SUBVAR_ENTITY_ID = "SUBVAR_ENTITY_ID";

  /**
   * Holds the DB sub-variant attribute
   */
  // ICDM-229
  private Long dbSubVarAttrId;

  /**
   * Holds the Attribute which needs to be set when creating new SubVariantAttr
   */

  private final Long dbAttrId;

  /**
   * Holds the db project Sub-VARIANT which the attr belongs to
   */

  private final Long dbPidcSubVarId;
  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Constructor - CmdModProjSubVariantAttr - INSERT new sub-variant attr
   *
   * @param apicDataProvider dataprovider
   * @param pidcAttrSubVar subVarAttr
   */
  public CmdModProjSubVariantAttr(final ApicDataProvider apicDataProvider, final PIDCAttributeSubVar pidcAttrSubVar) {

    super(apicDataProvider, pidcAttrSubVar);
    // Icdm-229
    if (this.commandMode != COMMAND_MODE.INSERT) {

      this.dbSubVarAttrId = pidcAttrSubVar.getID();
    }
    this.dbPidcSubVarId = pidcAttrSubVar.getPidcSubVariant().getSubVariantID();
    this.dbAttrId = pidcAttrSubVar.getAttribute().getAttributeID();

  }

  /**
   * Constructor - CmdModProjSubVariantAttr - UPDATE, DELETE SubVariantAttr
   *
   * @param apicDataProvider dataprovider
   * @param pidcAttrSubVar subVarAttr
   * @param toDelete true if delete, false otherwise
   */
  protected CmdModProjSubVariantAttr(final ApicDataProvider apicDataProvider, final PIDCAttributeSubVar pidcAttrSubVar,
      final boolean toDelete) {
    this(apicDataProvider, pidcAttrSubVar);
    if (toDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
      setUsed(pidcAttrSubVar.getIsUsed());
      setValue(pidcAttrSubVar.getAttributeValue());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    createSubVariantAttr(getNewUsed(), getNewValue());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.dbSubVarAttrId, TabvProjSubVariantsAttr.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcSubVaraintAttr(this.dbSubVarAttrId).getObjectDetails());

    if (this.cmdAttrVal != null) {
      this.childCmdStack.addCommand(this.cmdAttrVal);

      getChangedData().putAll(this.cmdAttrVal.getChangedData());

      setValue(this.cmdAttrVal.getAttrValue());
      resetFocusMatrixDetails();
    }
    // Icdm-229
    final TabvProjSubVariantsAttr dbSubVarAttr = getEntityProvider().getDbPidcSubVarAttr(this.dbSubVarAttrId);
    final TabvProjectSubVariant dbPidcSubVar = getEntityProvider().getDbPidcSubVariant(this.dbPidcSubVarId);
    validateStaleData(dbSubVarAttr);

    // Set used and value appropriatly
    dbSubVarAttr.setUsed(getDbUsedFlag(getNewUsed()));
    dbSubVarAttr.setTabvAttrValue(getDbValue(getNewValue()));

    if ((null != getNewValue()) ||
        ((null != getNewUsed()) && getNewUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType())) ||
        ((null != getNewUsed()) && getNewUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {
      resetFocusMatrixDetails();
    }

    setUserDetails(this.commandMode, dbSubVarAttr, PATTR_ENTITY_ID);
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcSubVar, SUBVAR_ENTITY_ID);

    // ICDM-260
    dbSubVarAttr.setPartNumber(getNewPartNumber());
    dbSubVarAttr.setSpecLink(getNewSpecLink());
    dbSubVarAttr.setDescription(getNewDesc());

    getChangedData().put(this.dbSubVarAttrId, chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    deleteSubVariantAttr();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() {
    deleteSubVariantAttr();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {

    ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbSubVarAttrId, TabvVariantsAttr.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcSubVaraintAttr(this.dbSubVarAttrId).getObjectDetails());

    // Set used and value appropriatly
    // Icdm-229
    final TabvProjSubVariantsAttr dbSubVarAttr = getEntityProvider().getDbPidcSubVarAttr(this.dbSubVarAttrId);
    final TabvProjectSubVariant dbPidcSubVar = getEntityProvider().getDbPidcSubVariant(this.dbPidcSubVarId);
    dbSubVarAttr.setUsed(getDbUsedFlag(getOldUsed()));
    dbSubVarAttr.setTabvAttrValue(getDbValue(getOldValue()));

    setUserDetails(this.commandMode, dbSubVarAttr, PATTR_ENTITY_ID);
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcSubVar, SUBVAR_ENTITY_ID);

    dbSubVarAttr.setPartNumber(getOldPartNumber());
    dbSubVarAttr.setSpecLink(getOldSpecLink());
    dbSubVarAttr.setDescription(getOldDesc());

    // AttributeValue should be undo
    this.childCmdStack.undoAll();

    getChangedData().put(this.dbSubVarAttrId, chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    createSubVariantAttr(getOldUsed(), getOldValue());

  }

  /**
   * Create new sub-variant attribute
   *
   * @param used
   * @param value
   */
  private void createSubVariantAttr(final String used, final AttributeValue value) {
    // Create new Sub-Variant Attribute entity
    // Icdm-229
    final TabvProjSubVariantsAttr dbSubVarAttr = new TabvProjSubVariantsAttr();
    final TabvProjectSubVariant dbPidcSubVar = getEntityProvider().getDbPidcSubVariant(this.dbPidcSubVarId);
    final TabvAttribute dbAttr = getEntityProvider().getDbAttribute(this.dbAttrId);
    dbSubVarAttr.setVersion(getProRevId().longValue());
    dbSubVarAttr.setTabvAttribute(dbAttr);

    dbSubVarAttr.setTPidcVersion(getEntityProvider().getDbPIDCVersion(dbPidcSubVar.getTPidcVersion().getPidcVersId()));

    dbSubVarAttr.setTabvProjectVariant(
        getEntityProvider().getDbPidcVariant(dbPidcSubVar.getTabvProjectVariant().getVariantId()));
    dbSubVarAttr.setTabvProjectSubVariant(dbPidcSubVar);
    dbSubVarAttr.setUsed(getDbUsedFlag(used));
    dbSubVarAttr.setTabvAttrValue(getDbValue(value));
    // ICDM-260
    dbSubVarAttr.setPartNumber(getNewPartNumber());
    dbSubVarAttr.setSpecLink(getNewSpecLink());
    dbSubVarAttr.setDescription(getNewDesc());
    setUserDetails(this.commandMode, dbSubVarAttr, PATTR_ENTITY_ID);

    // Register this new SubVariantAttribute
    getEntityProvider().registerNewEntity(dbSubVarAttr);
    this.dbSubVarAttrId = dbSubVarAttr.getSubVarAttrId();
    // Add subvariant to the list of SubVariantsAttrs
    if (dbPidcSubVar.getTabvProjSubVariantsAttrs() == null) {
      dbPidcSubVar.setTabvProjSubVariantsAttrs(new ArrayList<TabvProjSubVariantsAttr>());
    }
    dbPidcSubVar.getTabvProjSubVariantsAttrs().add(dbSubVarAttr);

    setUserDetails(COMMAND_MODE.UPDATE, dbPidcSubVar, SUBVAR_ENTITY_ID);

    // Create the object and add it to the collection
    final PIDCAttributeSubVar subVarAttr = new PIDCAttributeSubVar(getDataProvider(), dbSubVarAttr.getSubVarAttrId());
    getDataCache().getAllPidcSubVarAttrMap().put(dbSubVarAttr.getSubVarAttrId(), subVarAttr);

    getChangedData().put(this.dbSubVarAttrId, new ChangedData(ChangeType.INSERT, this.dbSubVarAttrId,
        TabvProjSubVariantsAttr.class, DisplayEventSource.COMMAND));

  }

  /**
   * Delete subvariant attribute
   */
  private void deleteSubVariantAttr() {

    //Fetching the subvariant
    PIDCSubVariant subVar = getDataCache().getPidcSubVaraint(this.dbPidcSubVarId);

    final TabvProjSubVariantsAttr dbSubVarAttr = getEntityProvider().getDbPidcSubVarAttr(this.dbSubVarAttrId);
    final TabvProjectSubVariant dbPidcSubVar = getEntityProvider().getDbPidcSubVariant(this.dbPidcSubVarId);
    setUserDetails(this.commandMode, dbSubVarAttr, PATTR_ENTITY_ID);

    // iCDM-1098; refresh the object
    // ICDM-1402
    TabvProjSubVariantsAttr refreshedSubVarAttr =
        (TabvProjSubVariantsAttr) getEntityProvider().refreshCacheObject(dbSubVarAttr);
    // delete the entity
    getEntityProvider().deleteEntity(refreshedSubVarAttr);
    dbPidcSubVar.getTabvProjSubVariantsAttrs().remove(refreshedSubVarAttr);
    // remove it from collections too
    getDataCache().getAllPidcSubVarAttrMap().remove(refreshedSubVarAttr.getSubVarAttrId());
    
    //Resetting the childrenLoaded flag to false for refresh the parent variant structure
    subVar.resetChildrenLoaded();

    setUserDetails(COMMAND_MODE.UPDATE, dbPidcSubVar, SUBVAR_ENTITY_ID);
    getChangedData().put(this.dbSubVarAttrId, new ChangedData(ChangeType.DELETE, this.dbSubVarAttrId,
        TabvProjSubVariantsAttr.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // iCDM-834
    // Notify icdm hotline ,if value clearance required
    CmdModAttributeValue cmdAttrValue = getCmdAttrVal();
    if ((cmdAttrValue != null) && cmdAttrValue.isClearanceRequired()) {
      cmdAttrValue.sendMailNotification();
    }

  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        getDataCache().getAllPidcSubVarAttrMap().remove(this.dbPidcSubVarId);
        break;
      case DELETE:
        final PIDCAttributeSubVar subVarAttr = new PIDCAttributeSubVar(getDataProvider(), this.dbPidcSubVarId);
        getDataCache().getAllPidcSubVarAttrMap().put(this.dbPidcSubVarId, subVarAttr);
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
  public String getPrimaryObjectType() {
    return "Sub-Variant Attribute";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
        caseCmdUpd(detailsList);
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
    String oldVal = "";
    String newVal = "";
    if (null != getOldValue()) {
      oldVal = getOldValue().getValue();
    }
    if (null != getNewValue()) {

      newVal = getNewValue().getValue();
    }
    addTransactionSummaryDetails(detailsList, oldVal, newVal, "Value");
    addTransactionSummaryDetails(detailsList, getOldUsed(), getNewUsed(), "Used Flag");
    addTransactionSummaryDetails(detailsList, getOldIsVariant(), getNewIsVariant(), "Is Variant ?");
    addTransactionSummaryDetails(detailsList, getOldPartNumber(), getNewPartNumber(), "Part Number");
    addTransactionSummaryDetails(detailsList, getOldSpecLink(), getNewSpecLink(), "Specification");
    addTransactionSummaryDetails(detailsList, getOldDesc(), getNewDesc(), "Description");
  }

  // ICDM-1599
  /**
   * Method for pidcCompare
   *
   * @return the modifyPidcAttr
   */
  public PIDCAttributeSubVar getModifiedAttr() {
    return getDataCache().getPidcSubVaraintAttr(this.dbPidcSubVarId);
  }
}
