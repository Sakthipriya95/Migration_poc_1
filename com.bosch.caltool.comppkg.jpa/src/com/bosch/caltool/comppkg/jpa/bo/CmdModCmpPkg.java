package com.bosch.caltool.comppkg.jpa.bo;

/*
 * CmdModCmpPkg.java Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.CmdModMultipleLinks;
import com.bosch.caltool.apic.jpa.bo.CmdModNodeAccessRight;
import com.bosch.caltool.apic.jpa.bo.CmdModTopLevelEntity;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPkgType;

/**
 * CmdModCmpPkg.java - Command handles all db operations on INSERT, UPDATE, DELETE on component package
 *
 * @author dmo5cob
 */
@Deprecated
public class CmdModCmpPkg extends AbstractCPCommand {

  /**
   * the Component package object instance
   */
  private CompPkg compPkg;
  /**
   * Other old and new parameters
   */
  private String oldCpName;
  /**
   * New name
   */
  private String newCpName;

  /**
   * Old description
   */
  private String oldCpDescEng;
  /**
   * New description
   */
  private String newCpDescEng;

  /**
   * Old german description
   */
  private String oldCpDescGer;
  /**
   * New german description
   */
  private String newCpDescGer;

  /**
   * Old Type
   */
  private CompPkgType oldType;

  /**
   * new type
   */
  private CompPkgType newType;
  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  /**
   * Transaction summary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * CmdModMultipleLinks instance
   */
  private CmdModMultipleLinks cmdMultipleLinks;

  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID = "COMP_PKG";

  /**
   * Constructor to add a new cmp pkg - use this constructor for INSERT
   *
   * @param cpDataProvider data provider
   */
  public CmdModCmpPkg(final CPDataProvider cpDataProvider) {
    super(cpDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.newType = CompPkgType.NORMAL;
  }

  /**
   * Constructor to delete or update existing cmp pkg
   *
   * @param cpDataProvider data provider
   * @param modifyCp obj to be modified
   * @param isDelete whether to delete or not
   */
  public CmdModCmpPkg(final CPDataProvider cpDataProvider, final CompPkg modifyCp, final boolean isDelete) {
    super(cpDataProvider);

    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.compPkg = modifyCp;
    // initialize command with current values from UI
    setCpFieldsToCommand(modifyCp);
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifycmp pkg
   */
  private void setCpFieldsToCommand(final CompPkg modifyCp) {
    // Name
    this.oldCpName = modifyCp.getCompPkgName();
    this.newCpName = this.oldCpName;
    // ENGLISH Desc
    this.oldCpDescEng = modifyCp.getDescEng();
    this.newCpDescEng = this.oldCpDescEng;
    // GERMAN Desc
    this.oldCpDescGer = modifyCp.getDescGer();
    this.newCpDescGer = this.oldCpDescGer;
    // Component Package type
    this.oldType = modifyCp.getType();
    this.newType = this.oldType;

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // create a new database entity

    final TCompPkg newDbCp = new TCompPkg();
    setNewCpFields(newDbCp);
    setUserDetails(this.commandMode, newDbCp, ENTITY_ID);
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbCp);

    // add the new CompPkg to the list of all CompPkgs
    // (the constructor of CompPkg will add the new object)
    final CompPkg cpObj = new CompPkg(getDataProvider(), newDbCp.getCompPkgId());
    this.compPkg = cpObj;
    getDataProvider().getDataCache().getAllCompPkgsMap().put(cpObj.getID(), cpObj);
    getChangedData().put(cpObj.getID(),
        new ChangedData(ChangeType.INSERT, cpObj.getID(), TCompPkg.class, DisplayEventSource.COMMAND));
    createAccessRights();

    // icdm-933 Dcn for Top level entity
    CmdModTopLevelEntity cmdTopLevel =
        new CmdModTopLevelEntity(getDataProvider().getApicDataProvider(), ApicConstants.TOP_LVL_ENT_ID_COMP_PKG);

    this.childCmdStack.addCommand(cmdTopLevel);
  }


  /**
   * Create a node access entity with the current user as the owner and full privileges. Command for node access
   * creation is used
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createAccessRights() throws CommandException {

    final CmdModNodeAccessRight cmdNodeAccess = new CmdModNodeAccessRight(getDataProvider().getApicDataProvider(),
        this.compPkg, getDataProvider().getApicDataProvider().getCurrentUser());
    cmdNodeAccess.setGrantOption(true);
    cmdNodeAccess.setIsOwner(true);
    cmdNodeAccess.setWriteAccess(true);
    this.childCmdStack.addCommand(cmdNodeAccess);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.compPkg.getID(), TCompPkg.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getCompPkg(this.compPkg.getID()).getObjectDetails());
    final TCompPkg modifiedCp = getEntityProvider().getDbCompPkg(this.compPkg.getID());
    validateStaleData(modifiedCp);
    // Update modified data
    setNewCpFields(modifiedCp);

    if (null != this.cmdMultipleLinks) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }

    setUserDetails(this.commandMode, modifiedCp, ENTITY_ID);
    getChangedData().put(this.compPkg.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.compPkg.getID(), TCompPkg.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getCompPkg(this.compPkg.getID()).getObjectDetails());
    final TCompPkg deletedCp = getEntityProvider().getDbCompPkg(this.compPkg.getID());
    validateStaleData(deletedCp);

    // set the deleted flag, do not delete the entity
    deletedCp.setDeletedFlag(ApicConstants.YES);

    // The command mode is update since we are only updating the deleted flag and not deleting the entity
    setUserDetails(COMMAND_MODE.UPDATE, deletedCp, ENTITY_ID);
    getChangedData().put(this.compPkg.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TCompPkg dbCp = getEntityProvider().getDbCompPkg(this.compPkg.getID());
    validateStaleData(dbCp);
    // unregister the cp
    getEntityProvider().deleteEntity(dbCp);
    // remove the comp pkgs from the list
    getDataProvider().getDataCache().getAllCompPkgsMap().remove(this.compPkg.getID());
    getChangedData().put(this.compPkg.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkg.getID(), TCompPkg.class, DisplayEventSource.COMMAND));

    // icdm-933 Dcn for Top level entity
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.compPkg.getID(), TCompPkg.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getCompPkg(this.compPkg.getID()).getObjectDetails());
    final TCompPkg modifiedCp = getEntityProvider().getDbCompPkg(this.compPkg.getID());
    validateStaleData(modifiedCp);
    // Update modified data,
    setOldCpFields(modifiedCp);
    setUserDetails(this.commandMode, modifiedCp, ENTITY_ID);

    getChangedData().put(this.compPkg.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.compPkg.getID(), TCompPkg.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getCompPkg(this.compPkg.getID()).getObjectDetails());
    final TCompPkg deletedCp = getEntityProvider().getDbCompPkg(this.compPkg.getID());
    validateStaleData(deletedCp);
    /* UNDO DELETE for this main cp */
    // set old cp fields,also sets to old modified user and date
    setOldCpFields(deletedCp);
    setUserDetails(COMMAND_MODE.UPDATE, deletedCp, ENTITY_ID);

    getChangedData().put(this.compPkg.getID(), chdata);
  }

  /**
   * Method sets the required fields of the TCompPkg entity
   */
  private void setNewCpFields(final TCompPkg dbCp) {
    // set name and desc fields
    dbCp.setCompPkgName(this.newCpName);
    dbCp.setDescEng(this.newCpDescEng);
    dbCp.setDescGer(this.newCpDescGer);
    // new attribute, deleted flag is NO
    dbCp.setDeletedFlag(ApicConstants.CODE_NO);
    dbCp.setCompPkgType(this.newType.getLiteral());
  }

  /**
   * @param dbCp
   */
  private void setOldCpFields(final TCompPkg dbCp) {
    // set name and desc fields
    dbCp.setCompPkgName(this.oldCpName);
    dbCp.setDescEng(this.oldCpDescEng);
    dbCp.setDescGer(this.oldCpDescGer);
    // set deleted flag
    dbCp.setDeletedFlag(ApicConstants.CODE_NO);
    dbCp.setDeletedFlag(this.oldType.getLiteral());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldCpName, this.newCpName) || isStringChanged(this.oldCpDescEng, this.newCpDescEng) ||
        isStringChanged(this.oldCpDescGer, this.newCpDescGer) || (this.oldType != this.newType) ||
        (this.cmdMultipleLinks != null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());
  }

  /**
   * Set the new English Name
   *
   * @param newCpName The new cp name in english
   */
  public void setCpName(final String newCpName) {
    this.newCpName = newCpName;
  }

  /**
   * Set the new attrName desc
   *
   * @param newCpDescEng new english description
   */
  public void setCpDescEng(final String newCpDescEng) {
    this.newCpDescEng = newCpDescEng;
  }

  /**
   * Set the german desc
   *
   * @param descGer German description
   */
  public void setCpDescGer(final String descGer) {
    // do not allow empty spaces, save as null
    if (descGer != null) {
      if (descGer.isEmpty()) {
        this.newCpDescGer = "";
      }
      else {
        this.newCpDescGer = descGer;
      }
    }
  }

  /**
   * Set the type of component package
   *
   * @param type type of component package
   */
  public void setCpType(final CompPkgType type) {
    this.newType = type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable
  }

  /**
   * Get newly added cp to enable selection after adding
   *
   * @return TCompPkg getNewDbCp
   */
  private TCompPkg getNewDbCp() {
    return getEntityProvider().getDbCompPkg(this.compPkg.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        getDataProvider().getDataCache().getAllCompPkgsMap().remove(getNewDbCp());
        break;
      case UPDATE:
      case DELETE:
      default:
        // Do nothing
        break;
    }
  }

  /**
   * {@inheritDoc} return the id of the new attr in case of insert & update mode, return the id of the old attr in case
   * of delete mode
   */
  @Override
  public Long getPrimaryObjectID() {
    return null == this.compPkg ? null : this.compPkg.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Component Package";
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
        addTransactionSummaryDetails(detailsList, this.oldCpName, this.newCpName, "Name ");
        addTransactionSummaryDetails(detailsList, this.oldCpDescEng, this.newCpDescEng, "Description (English)");
        addTransactionSummaryDetails(detailsList, this.oldCpDescGer, this.newCpDescGer, "Description (German)");
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
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier = "";
    if (null != this.compPkg.getID()) {
      final TCompPkg dbCp = getEntityProvider().getDbCompPkg(this.compPkg.getID());
      if (null != dbCp) {
        objectIdentifier = dbCp.getCompPkgName();
      }
    }
    return objectIdentifier;
  }

  /**
   * @param cmdMultipleLinks CmdModMultipleLinks
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;
  }
}
