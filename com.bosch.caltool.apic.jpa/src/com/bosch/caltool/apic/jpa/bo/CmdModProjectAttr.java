/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Command handles all db operations on INSERT, UPDATE, DELETE on Project Attributes
 *
 * @author bne4cob
 */
public class CmdModProjectAttr extends AbstractCmdModProjAttr {

  /**
   * Stack for storing child commands executed after creating the PIDC entity
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);
  /**
   * PIDC's entity id
   */
  private static final String PIDC_VRSN_ENTITY_ID = "PIDC_VRSN_ENTITY_ID";

  /**
   * The DB Project attribute corresponding to the project attribute.
   */
  // ICDM-229
  private Long dbProjAttrID;

  /**
   * The attribute ID of the PIDC attribute
   */
  private final Long attrID;
  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor of this command. Should be used for insert and update actions.
   *
   * @param dataProvider the instance of ApicData provider
   * @param pidcAttr original PIDC attribute
   */
  public CmdModProjectAttr(final ApicDataProvider dataProvider, final PIDCAttribute pidcAttr) {

    super(dataProvider, pidcAttr);

    this.attrID = pidcAttr.attrID;

    switch (this.commandMode) {
      case INSERT:
        // set used flag
        setNewUsed(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
        break;

      case UPDATE:
      case DELETE:
        this.dbProjAttrID = pidcAttr.getID();
        break;

      default:
        break;
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    // ICDM-108
    if (this.cmdAttrVal != null) {
      this.childCmdStack.addCommand(this.cmdAttrVal);
      setValue(this.cmdAttrVal.getAttrValue());
    }
    // create the project attrs
    createProjAttr(getNewUsed(), getNewValue(), getNewIsVariant());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {

    ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbProjAttrID, TabvProjectAttr.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.modifyPidcAttr.getObjectDetails());
    // ICDM-108
    if (this.cmdAttrVal != null) {
      this.childCmdStack.addCommand(this.cmdAttrVal);

      getChangedData().putAll(this.cmdAttrVal.getChangedData());

      setValue(this.cmdAttrVal.getAttrValue());

      // reset the focus matrix details
      resetFocusMatrixDetails();

    }
    // ICDM-229
    final TabvProjectAttr dbProjAttr = getEntityProvider().getDbPidcAttr(this.dbProjAttrID);
    validateStaleData(dbProjAttr);
    // Only the hidden flag is editable when the user has APIC_WRITE and no access to PIDC
    if (!getModifyPidcAttr().isReadable()) {
      dbProjAttr.setAttrHiddenFlag(null != getNewHiddenFlag() ? getNewHiddenFlag() : dbProjAttr.getAttrHiddenFlag());
    }
    else {

      dbProjAttr.setUsed(getDbUsedFlag(getNewUsed()));

      dbProjAttr.setAttrHiddenFlag(null == getNewHiddenFlag() ? dbProjAttr.getAttrHiddenFlag() : getNewHiddenFlag());

      dbProjAttr
          .setTrnsfrVcdmFlag(null == getNewTrnfrVcdmFlag() ? dbProjAttr.getTrnsfrVcdmFlag() : getNewTrnfrVcdmFlag());

      dbProjAttr
          .setFocusMatrixYn(null == getNewFMRelevantFlag() ? dbProjAttr.getFocusMatrixYn() : getNewFMRelevantFlag());

      dbProjAttr.setIsVariant(getNewIsVariant());

      dbProjAttr.setTabvAttrValue(getDbValue(getNewValue()));

      if ((null != getNewValue()) ||
          ((null != getNewUsed()) && getNewUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType())) ||
          ((null != getNewUsed()) && getNewUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {
        resetFocusMatrixDetails();
      }

      dbProjAttr.setPartNumber(getNewPartNumber());

      dbProjAttr.setSpecLink(getNewSpecLink());

      dbProjAttr.setDescription(getNewDesc());
    }
    setUserDetails(this.commandMode, dbProjAttr, PATTR_ENTITY_ID);

    // iCDM-2379
    Long attrId = Long.valueOf(getDataProvider().getParameterValue(ApicConstants.PIDC_DIVISION_ATTR));
    if ((null != getOldValue()) && (null != getOldValue().getID()) &&
        (getOldValue().getAttribute().getID().equals(attrId))) {
      if (getDataLoader().hasProjectAttributeUsedInReview(dbProjAttr.getTPidcVersion().getPidcVersId(),
          getOldValue().getID())) {
        throw new CommandException("The " + getOldValue().getAttribute().getName() +
            " attribute cannot be edited as it has been used in the Review Questionnaire !");
      }
    }

    final TPidcVersion dbPidcVersion =
        getEntityProvider().getDbPIDCVersion(this.modifyPidcAttr.getPidcVersion().getID());
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVersion, PIDC_VRSN_ENTITY_ID);

    getChangedData().put(this.dbProjAttrID, chdata);
    // ICDM-1583 deleting unmapped pidc_a2l file (PIDC level- Changing the pver name)
    changeInPidcA2L();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeDeleteCommand() {
    deleteProjAttr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    deleteProjAttr();
    // ICDM-108
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {

    ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.dbProjAttrID, TabvProjectAttr.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.modifyPidcAttr.getObjectDetails());

    // ICDM-229
    final TabvProjectAttr dbProjAttr = getEntityProvider().getDbPidcAttr(this.dbProjAttrID);
    dbProjAttr.setUsed(getDbUsedFlag(getOldUsed()));

    dbProjAttr.setIsVariant(getOldIsVariant());

    dbProjAttr.setTabvAttrValue(getDbValue(getOldValue()));

    dbProjAttr.setPartNumber(getOldPartNumber());

    dbProjAttr.setSpecLink(getOldSpecLink());

    dbProjAttr.setDescription(getOldDesc());

    setUserDetails(this.commandMode, dbProjAttr, PATTR_ENTITY_ID);

    final TPidcVersion dbPidcVersion =
        getEntityProvider().getDbPIDCVersion(this.modifyPidcAttr.getPidcVersion().getID());
    setUserDetails(COMMAND_MODE.UPDATE, dbPidcVersion, PIDC_VRSN_ENTITY_ID);

    // ICDM-108
    this.childCmdStack.undoAll();
    getChangedData().put(this.dbProjAttrID, chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoDeleteCommand() {
    createProjAttr(getOldUsed(), getOldValue(), getOldIsVariant());
  }

  /**
   * Create project attribute entity and add to the data provider's map
   *
   * @param used used flag
   * @param value attribute value
   */
  private void createProjAttr(final String used, final AttributeValue value, final String isVariant) {
    // ICDM-229
    final TabvProjectAttr dbProjAttr = new TabvProjectAttr();

    setUserDetails(this.commandMode, dbProjAttr, PATTR_ENTITY_ID);

    final PIDCVersion pidcVersion = this.modifyPidcAttr.getPidcVersion();
    final TPidcVersion dbPidVersion = getEntityProvider().getDbPIDCVersion(pidcVersion.getID());

    dbProjAttr.setVersion(getProRevId().longValue());

    final TabvAttribute dbAttribute = getEntityProvider().getDbAttribute(this.attrID);

    dbProjAttr.setTabvAttribute(dbAttribute);

    dbProjAttr.setTPidcVersion(dbPidVersion);
    dbProjAttr.setUsed(getDbUsedFlag(used));

    dbProjAttr.setIsVariant(isVariant);

    dbProjAttr.setTabvAttrValue(getDbValue(value));

    dbProjAttr.setVersion(Long.valueOf(1));

    dbProjAttr.setPartNumber(getNewPartNumber());

    dbProjAttr.setSpecLink(getNewSpecLink());

    dbProjAttr.setDescription(getNewDesc());

    dbProjAttr.setAttrHiddenFlag(getNewHiddenFlag());

    dbProjAttr.setTrnsfrVcdmFlag(getNewTrnfrVcdmFlag());

    dbProjAttr.setFocusMatrixYn(getNewFMRelevantFlag());

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbProjAttr);
    this.dbProjAttrID = dbProjAttr.getPrjAttrId();

    if (dbPidVersion.getTabvProjectAttrs() == null) {
      dbPidVersion.setTabvProjectAttrs(new ArrayList<TabvProjectAttr>());
    }
    dbPidVersion.getTabvProjectAttrs().add(dbProjAttr);

    setUserDetails(COMMAND_MODE.UPDATE, dbPidVersion, PIDC_VRSN_ENTITY_ID);

    this.modifyPidcAttr = new PIDCAttribute(getDataProvider(), dbProjAttr.getPrjAttrId());

    getChangedData().put(this.dbProjAttrID,
        new ChangedData(ChangeType.INSERT, this.dbProjAttrID, TabvProjectAttr.class, DisplayEventSource.COMMAND));

  }

  /**
   * Delete project attribute entity and remove from the data provider's map
   */
  private void deleteProjAttr() {
    // ICDM-229
    final TabvProjectAttr delDBProjAttr = getEntityProvider().getDbPidcAttr(this.dbProjAttrID);

    setUserDetails(this.commandMode, delDBProjAttr, PATTR_ENTITY_ID);

    // unregister the Project Attribute
    getEntityProvider().deleteEntity(delDBProjAttr);
    final PIDCVersion pidcVersion = this.modifyPidcAttr.getPidcVersion();
    final TPidcVersion dbPidVersion = getEntityProvider().getDbPIDCVersion(pidcVersion.getID());
    dbPidVersion.getTabvProjectAttrs().remove(delDBProjAttr);

    setUserDetails(COMMAND_MODE.UPDATE, dbPidVersion, PIDC_VRSN_ENTITY_ID);

    // Remove the Project Attribute from data model
    getDataCache().getAllPidcAttrMap().remove(delDBProjAttr.getPrjAttrId());

    getChangedData().put(this.dbProjAttrID,
        new ChangedData(ChangeType.DELETE, this.dbProjAttrID, TabvProjectAttr.class, DisplayEventSource.COMMAND));
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
   */
  protected void markFMDeleted() {
    try {
      final FocusMatrixVersion fmVersion = this.modifyPidcAttr.getPidcVersion().getFocusMatrixWorkingSetVersion();
      ConcurrentMap<Long, FocusMatrixDetails> fmDetailsMap =
          fmVersion.getFocusMatrixItemMap().get(this.modifyPidcAttr.getAttribute().getID());
      if ((null != fmDetailsMap) && !fmDetailsMap.isEmpty()) {
        for (FocusMatrixDetails fmDetails : fmDetailsMap.values()) {
          CmdModFocusMatrix cmd = new CmdModFocusMatrix(getDataProvider(), fmVersion, fmDetails.getUcpaId(),
              fmDetails.getUseCaseId(), fmDetails.getUseCaseSectionId(), fmDetails.getAttributeId(), true);
          cmd.setDeletedFlag(true);
          this.childCmdStack.addCommand(cmd);
        }
      }
    }
    catch (CommandException e) {
      getDataProvider().getLogger().error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        getDataCache().getAllPidcAttrMap().remove(this.dbProjAttrID);
        break;
      case DELETE:
        // Creating a New Object to add the object to the map
        new PIDCAttribute(getDataProvider(), this.dbProjAttrID);
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
    return "PIDC Attribute";
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


  /**
   * Method for pidcCompare
   *
   * @return the modifyPidcAttr
   */
  public PIDCAttribute getModifyPidcAttr() {
    return (PIDCAttribute) this.modifyPidcAttr;
  }


}
