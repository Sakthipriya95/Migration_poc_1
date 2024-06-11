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
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvVariantsAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModProjVariantAttr- Command handles all db operations on INSERT, UPDATE, DELETE on Project Variant Attributes
 *
 * @author bne4cob
 */
public class CmdModProjVariantAttr extends AbstractCmdModProjAttr {

  /**
   * Variant's entity ID
   */
  private static final String VAR_ENTITY_ID = "VAR_ENTITY_ID";

  /**
   * Holds the DB variant attribute of the variant attribute
   */
  // ICDM-229
  private Long dbVarAttrId;

  /**
   * Holds the Attribute which needs to be set when creating new VariantAttr
   */
  // ICDM-229
  private final Long dbAttrId;

  /**
   * Holds the db project VARIANT which the attr belongs to
   */
  // ICDM-229
  private final Long dbPidcVarId;
  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor of this command. Should be used for INSERT/UPDATE actions.
   *
   * @param apicDataProvider the instance of ApicData provider
   * @param pidcAttributeVar original PIDC attribute
   */
  public CmdModProjVariantAttr(final ApicDataProvider apicDataProvider, final PIDCAttributeVar pidcAttributeVar) {

    super(apicDataProvider, pidcAttributeVar);

    if (this.commandMode != COMMAND_MODE.INSERT) {
      // ICDM-229
      this.dbVarAttrId = pidcAttributeVar.getID();
    }
    // ICDM-229
    this.dbPidcVarId = pidcAttributeVar.getPidcVariant().getVariantID();
    this.dbAttrId = pidcAttributeVar.getAttribute().getAttributeID();
  }

  /**
   * Constructor for deleting the variant attribute.
   *
   * @param apicDataProvider the instance of ApicData provider
   * @param pidcAttributeVar original PIDC attribute
   * @param delete delete the variant attribute
   */
  protected CmdModProjVariantAttr(final ApicDataProvider apicDataProvider, final PIDCAttributeVar pidcAttributeVar,
      final boolean delete) {
    this(apicDataProvider, pidcAttributeVar);
    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
      setUsed(pidcAttributeVar.getIsUsed());
      setValue(pidcAttributeVar.getAttributeValue());
    }
  }

  @Override
  protected final void executeInsertCommand() {

    createVariantAttr(getNewUsed(), getNewValue(), getNewIsVariant());
  }

  /**
   * Creates the variant attribute
   *
   * @param used used flag
   * @param value attribute value
   */
  private void createVariantAttr(final String used, final AttributeValue value, final String isSubVariant) {
    // Create new Variant Attribute entity
    // ICDM-229
    final TabvVariantsAttr dbVarAttr = new TabvVariantsAttr();
    final TabvAttribute dbAttr = getEntityProvider().getDbAttribute(this.dbAttrId);
    final TabvProjectVariant dbPidcVar = getEntityProvider().getDbPidcVariant(this.dbPidcVarId);
    dbVarAttr.setVersion(getProRevId().longValue());
    dbVarAttr.setTabvAttribute(dbAttr);
    dbVarAttr.setTPidcVersion(getEntityProvider().getDbPIDCVersion(dbPidcVar.getTPidcVersion().getPidcVersId()));
    dbVarAttr.setTabvProjectVariant(dbPidcVar);
    dbVarAttr.setUsed(getDbUsedFlag(used));
    dbVarAttr.setIsSubVariant(isSubVariant);
    dbVarAttr.setTabvAttrValue(getDbValue(value));
    // ICDM-260
    dbVarAttr.setPartNumber(getNewPartNumber());
    dbVarAttr.setSpecLink(getNewSpecLink());
    dbVarAttr.setDescription(getNewDesc());

    setUserDetails(this.commandMode, dbVarAttr, PATTR_ENTITY_ID);

    // Register this new VariantAttribute
    getEntityProvider().registerNewEntity(dbVarAttr);
    this.dbVarAttrId = dbVarAttr.getVarAttrId();

    if (dbPidcVar.getTabvVariantsAttrs() == null) {
      dbPidcVar.setTabvVariantsAttrs(new ArrayList<TabvVariantsAttr>());
    }
    dbPidcVar.getTabvVariantsAttrs().add(dbVarAttr);

    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVar, VAR_ENTITY_ID);

    // Create the object and add it to the collection
    final PIDCAttributeVar varAttr = new PIDCAttributeVar(getDataProvider(), dbVarAttr.getVarAttrId());
    getDataCache().getAllPidcVarAttrMap().put(dbVarAttr.getVarAttrId(), varAttr);

    getChangedData().put(this.dbVarAttrId,
        new ChangedData(ChangeType.INSERT, this.dbVarAttrId, TabvVariantsAttr.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {

    ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbVarAttrId, TabvVariantsAttr.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVaraintAttr(this.dbVarAttrId).getObjectDetails());

    // ICDM-108
    if (this.cmdAttrVal != null) {
      this.childCmdStack.addCommand(this.cmdAttrVal);

      getChangedData().putAll(this.cmdAttrVal.getChangedData());

      setValue(this.cmdAttrVal.getAttrValue());
      resetFocusMatrixDetails();
    }
    // ICDM-229
    final TabvVariantsAttr dbVarAttr = getEntityProvider().getDbPidcVarAttr(this.dbVarAttrId);
    final TabvProjectVariant dbPidcVar = getEntityProvider().getDbPidcVariant(this.dbPidcVarId);
    validateStaleData(dbVarAttr);

    dbVarAttr.setUsed(getDbUsedFlag(getNewUsed()));

    dbVarAttr.setIsSubVariant(getNewIsVariant());

    dbVarAttr.setTabvAttrValue(getDbValue(getNewValue()));

    if ((null != getNewValue()) ||
        ((null != getNewUsed()) && getNewUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType())) ||
        ((null != getNewUsed()) && getNewUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {
      resetFocusMatrixDetails();
    }

    setUserDetails(this.commandMode, dbVarAttr, PATTR_ENTITY_ID);
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVar, VAR_ENTITY_ID);

    // ICDM-260
    dbVarAttr.setPartNumber(getNewPartNumber());
    dbVarAttr.setSpecLink(getNewSpecLink());
    dbVarAttr.setDescription(getNewDesc());

    getChangedData().put(this.dbVarAttrId, chdata);

    // ICDM-1583 deleting unmapped pidc_a2l file (variant level- Changing the pver name)
    changeInPidcA2L();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeDeleteCommand() {
    deleteVariantAttr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {

    ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbVarAttrId, TabvVariantsAttr.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcVaraintAttr(this.dbVarAttrId).getObjectDetails());

    // ICDM-229
    final TabvVariantsAttr dbVarAttr = getEntityProvider().getDbPidcVarAttr(this.dbVarAttrId);
    final TabvProjectVariant dbPidcVar = getEntityProvider().getDbPidcVariant(this.dbPidcVarId);
    dbVarAttr.setUsed(getDbUsedFlag(getOldUsed()));

    dbVarAttr.setTabvAttrValue(getDbValue(getOldValue()));

    dbVarAttr.setIsSubVariant(getOldIsVariant());

    setUserDetails(this.commandMode, dbVarAttr, PATTR_ENTITY_ID);
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVar, VAR_ENTITY_ID);
    // icdm-260
    dbVarAttr.setPartNumber(getOldPartNumber());

    dbVarAttr.setSpecLink(getOldSpecLink());

    dbVarAttr.setDescription(getOldDesc());
    // ICDM-108
    this.childCmdStack.undoAll();
    getChangedData().put(this.dbVarAttrId, chdata);


  }

  /**
   * Method for pidcCompare
   *
   * @return the modifyPidcAttr
   */
  public PIDCAttributeVar getModifiedAttr() {
    return getDataCache().getPidcVaraintAttr(this.dbVarAttrId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoDeleteCommand() {
    createVariantAttr(getOldUsed(), getOldValue(), getOldIsVariant());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() {
    deleteVariantAttr();
  }

  /**
   * Deletes the variant attribute
   */
  private void deleteVariantAttr() {
    // ICDM-229
    final TabvVariantsAttr dbVarAttr = getEntityProvider().getDbPidcVarAttr(this.dbVarAttrId);
    final TabvProjectVariant dbPidcVar = getEntityProvider().getDbPidcVariant(this.dbPidcVarId);
    setUserDetails(this.commandMode, dbVarAttr, PATTR_ENTITY_ID);

    // iCDM-1098 , refresh the datacache
    TabvVariantsAttr refreshedVarAttr = (TabvVariantsAttr) getEntityProvider().refreshCacheObject(dbVarAttr);
    // delete the entity
    getEntityProvider().deleteEntity(refreshedVarAttr);
    dbPidcVar.getTabvVariantsAttrs().remove(refreshedVarAttr);
    // remove it from collections too
    getDataCache().getAllPidcVarAttrMap().remove(refreshedVarAttr.getVarAttrId());

    PIDCVersion pidcVrsn = getDataCache().getPidcVersion(dbPidcVar.getTPidcVersion().getPidcVersId());
    PIDCVariant variant = pidcVrsn.getVariantsMap().get(dbPidcVar.getVariantId());
    variant.resetChildrenLoaded();
    variant.getAttributes().remove(dbVarAttr.getVarAttrId());

    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVar, VAR_ENTITY_ID);

    getChangedData().put(this.dbVarAttrId,
        new ChangedData(ChangeType.DELETE, this.dbVarAttrId, TabvVariantsAttr.class, DisplayEventSource.COMMAND));

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
        getDataCache().getAllPidcVarAttrMap().remove(this.dbPidcVarId);
        break;
      case DELETE:
        final PIDCAttributeVar varAttr = new PIDCAttributeVar(getDataProvider(), this.dbPidcVarId);
        getDataCache().getAllPidcVarAttrMap().put(this.dbPidcVarId, varAttr);
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
    return "Variant Attribute";
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


}
