/*
 * CmdModCmpPkg.java Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;

/**
 * CmdModCmpPkgBC.java - Command handles db operations on INSERT,UPDATE & DELETE on component package BC
 *
 * @author dmo5cob
 */
@Deprecated
public class CmdModCmpPkgBC extends AbstractCPCommand {

  /**
   * the Component package BC object instance
   */
  private CompPkgBc compPkgBc;
  /**
   * Other old and new parameters
   */
  private String oldCpBCName;
  /**
   * New bc name
   */
  private String newCpBCName;

  /**
   * Other old and new parameters
   */
  private Long oldBCSeqNo;

  /**
   * New sequence number
   */
  private Long newBCSeqNo;


  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Other old and new parameters
   */

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  /**
   * Transaction summary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Component package
   */
  private final CompPkg compPkg;
  /**
   * Move direction up
   */
  private boolean moveUp;
  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID = "COMP_PKG_BC";

  /**
   * Constructor to add a new cmp pkg - use this constructor for INSERT
   *
   * @param cpDataProvider data provider
   * @param compPkg selected cmp pkg
   */
  public CmdModCmpPkgBC(final CPDataProvider cpDataProvider, final CompPkg compPkg) {
    super(cpDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.compPkg = compPkg;

    this.newBCSeqNo = Long.valueOf(0);
  }

  /**
   * Constructor to delete or update existing cmp pkg
   *
   * @param cpDataProvider data provider
   * @param modifyCp the updated cmppkg
   * @param isDelete whether to delete or not
   * @param moveUp move up/down
   * @param compPkg selected cmp pkg
   */
  public CmdModCmpPkgBC(final CPDataProvider cpDataProvider, final CompPkgBc modifyCp, final boolean isDelete,
      final boolean moveUp, final CompPkg compPkg) {
    super(cpDataProvider);
    this.compPkg = compPkg;
    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.compPkgBc = modifyCp;

    this.moveUp = moveUp;

    // initialize command with current values from UI
    setCpFieldsToCommand(modifyCp);
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifycmp pkg
   */
  private void setCpFieldsToCommand(final CompPkgBc modifyCp) {
    // Name
    this.oldCpBCName = modifyCp.getBcName();
    this.newCpBCName = this.oldCpBCName;

    this.oldBCSeqNo = modifyCp.getBcSeqNo();
    this.newBCSeqNo = this.oldBCSeqNo;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() {
    // create a new database entity

    final TCompPkgBc newDbCpBC = new TCompPkgBc();
    Long seqNo = null;
    if ((null != this.compPkg.getCompPkgBcs()) && this.compPkg.getCompPkgBcs().isEmpty()) {
      seqNo = this.newBCSeqNo + 1;
    }
    else if ((null != this.compPkg.getCompPkgBcs()) && !this.compPkg.getCompPkgBcs().isEmpty()) {
      final SortedSet<CompPkgBc> setCmpPkgBcs = this.compPkg.getCompPkgBcs();
      seqNo = setCmpPkgBcs.last().getBcSeqNo() + 1;
    }
    setNewCpFields(newDbCpBC, seqNo);
    newDbCpBC.setTCompPkg(getEntityProvider().getDbCompPkg(this.compPkg.getID()));
    setUserDetails(this.commandMode, newDbCpBC, ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbCpBC);
    this.newBCSeqNo = newDbCpBC.getBcSeqNo();

    getEntityProvider().getDbCompPkg(this.compPkg.getID()).getTCompPkgBcs().add(newDbCpBC);

    // add the new CompPkg to the list of all CompPkgs
    // (the constructor of CompPkg will add the new object)
    final CompPkgBc compPkgBcObj = new CompPkgBc(getDataProvider(), newDbCpBC.getCompBcId());

    this.compPkgBc = compPkgBcObj;
    this.compPkg.getCompPkgBcsMap().put(this.compPkgBc.getID(), this.compPkgBc);
    getChangedData().put(this.compPkgBc.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkgBc.getID(), TCompPkgBc.class, DisplayEventSource.COMMAND));
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
        new ChangedData(ChangeType.UPDATE, this.compPkgBc.getID(), TCompPkgBc.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.compPkg.getCompPkgBcsMap().get(this.compPkgBc.getID()).getObjectDetails());
    final TCompPkgBc modifiedCp = getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID());
    validateStaleData(modifiedCp);


    if (!this.compPkg.getCompPkgBcs().isEmpty() && (this.compPkg.getCompPkgBcs().size() > 1)) {
      final SortedSet<CompPkgBc> setOfBCs = this.compPkg.getCompPkgBcs();
      for (CompPkgBc compPkgBcObj : setOfBCs) {
        if ((this.moveUp && compPkgBcObj.getBcSeqNo().equals(modifiedCp.getBcSeqNo() - 1)) ||
            (!this.moveUp && compPkgBcObj.getBcSeqNo().equals(modifiedCp.getBcSeqNo() + 1))) {
          final Long seqNo = modifiedCp.getBcSeqNo();
          modifiedCp.setBcSeqNo(Long.valueOf(0));// Temporarily set this
          getEntityProvider().getEm().flush();
          final TCompPkgBc bcWithLowerSqNo = getEntityProvider().getDbCompPkgBc(compPkgBcObj.getID());
          bcWithLowerSqNo.setBcSeqNo(seqNo);
          getEntityProvider().getEm().flush();
          if (this.moveUp) {
            modifiedCp.setBcSeqNo(seqNo - 1);
          }
          else {
            modifiedCp.setBcSeqNo(seqNo + 1);
          }
          getEntityProvider().getEm().flush();
          break;
        }
      }

    }
    this.newBCSeqNo = modifiedCp.getBcSeqNo();
    setUserDetails(this.commandMode, modifiedCp, ENTITY_ID);


    getChangedData().put(this.compPkgBc.getID(), chdata);


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
        new ChangedData(ChangeType.DELETE, this.compPkgBc.getID(), TCompPkgBc.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.compPkg.getCompPkgBcsMap().get(this.compPkgBc.getID()).getObjectDetails());
    final TCompPkgBc deletedCp = getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID());
    validateStaleData(deletedCp);
    // If the BC has FCs mapped to it then delete them first
    if ((null != this.compPkgBc.getCompPkgBcFcs()) && !this.compPkgBc.getCompPkgBcFcs().isEmpty()) {
      for (CompPkgBcFc fc : this.compPkgBc.getCompPkgBcFcs()) {
        CmdModCmpPkgBCFC command = new CmdModCmpPkgBCFC(getDataProvider(), fc, true, this.compPkgBc);
        this.childCmdStack.addCommand(command);
      }
    }

    Long seqNo = deletedCp.getBcSeqNo();
    deletedCp.setBcSeqNo(Long.valueOf(0));// temporarily set the seq no:
    getEntityProvider().getEm().flush();
    if (!this.compPkg.getCompPkgBcs().isEmpty() && (this.compPkg.getCompPkgBcs().size() > 1)) {
      for (CompPkgBc bc : this.compPkg.getCompPkgBcs()) {
        if (bc.getBcSeqNo() > seqNo) {
          getEntityProvider().getDbCompPkgBc(bc.getID())
              .setBcSeqNo(getEntityProvider().getDbCompPkgBc(bc.getID()).getBcSeqNo() - 1);
          getEntityProvider().getEm().flush();
        }
      }
    }
    // delete the entity
    getEntityProvider().deleteEntity(deletedCp);


    getEntityProvider().getDbCompPkg(this.compPkg.getID()).getTCompPkgBcs().remove(deletedCp);

    this.compPkg.getCompPkgBcsMap().remove(this.compPkgBc.getID());

    getChangedData().put(this.compPkgBc.getID(), chdata);


  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TCompPkgBc dbCp = getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID());
    validateStaleData(dbCp);
    // unregister the cp
    getEntityProvider().deleteEntity(dbCp);

    this.compPkg.getCompPkgBcsMap().remove(dbCp.getCompBcId());
    getChangedData().put(this.compPkgBc.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkgBc.getID(), TCompPkgBc.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.compPkgBc.getID(), TCompPkgBc.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.compPkg.getCompPkgBcsMap().get(this.compPkgBc.getID()).getObjectDetails());
    final TCompPkgBc modifiedCp = getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID());
    validateStaleData(modifiedCp);
    // Update modified data,
    setOldCpFields(modifiedCp);
    // set ModifiedDate and User
    setUserDetails(this.commandMode, modifiedCp, ENTITY_ID);


    getChangedData().put(this.compPkgBc.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    // create a new database entity

    final TCompPkgBc newDbCpBC = new TCompPkgBc();
    Long seqNo = null;
    if ((null != this.compPkg.getCompPkgBcs()) && this.compPkg.getCompPkgBcs().isEmpty()) {
      seqNo = this.newBCSeqNo + 1;
    }
    else if ((null != this.compPkg.getCompPkgBcs()) && !this.compPkg.getCompPkgBcs().isEmpty()) {
      final SortedSet<CompPkgBc> setCmpPkgBcs = this.compPkg.getCompPkgBcs();
      seqNo = setCmpPkgBcs.last().getBcSeqNo() + 1;
    }
    newDbCpBC.setBcName(this.oldCpBCName);

    newDbCpBC.setBcSeqNo(seqNo);
    newDbCpBC.setTCompPkg(getEntityProvider().getDbCompPkg(this.compPkg.getID()));

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbCpBC);
    this.newBCSeqNo = newDbCpBC.getBcSeqNo();

    getEntityProvider().getDbCompPkg(this.compPkg.getID()).getTCompPkgBcs().add(newDbCpBC);

    // add the new CompPkg to the list of all CompPkgs
    // (the constructor of CompPkg will add the new object)
    final CompPkgBc compPkgBcObj = new CompPkgBc(getDataProvider(), newDbCpBC.getCompBcId());

    this.compPkgBc = compPkgBcObj;
    this.compPkg.getCompPkgBcsMap().put(this.compPkgBc.getID(), this.compPkgBc);
    getChangedData().put(this.compPkgBc.getID(),
        new ChangedData(ChangeType.INSERT, this.compPkgBc.getID(), TCompPkgBc.class, DisplayEventSource.COMMAND));
  }

  /**
   * Method sets the required fields of the TCompPkg entity
   */
  private void setNewCpFields(final TCompPkgBc dbCp, final Long seqNo) {
    // set name and desc fields
    dbCp.setBcName(this.newCpBCName);

    dbCp.setBcSeqNo(seqNo);
  }

  /**
   * @param dbCp
   */
  private void setOldCpFields(final TCompPkgBc dbCp) {
    // set name and desc fields
    dbCp.setBcName(this.oldCpBCName);

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

    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());

  }

  /**
   * Set the new English Name
   *
   * @param newCpBCName The new cp bc name in english
   */
  public void setCpBCName(final String newCpBCName) {

    this.newCpBCName = newCpBCName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable

  }

  /**
   * Get newly added BC to enable selection after adding
   *
   * @return TCompPkgBc getNewDbCp
   */
  public TCompPkgBc getNewDbCpBC() {
    return getEntityProvider().getDbCompPkgBc(this.compPkgBc.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        this.compPkg.getCompPkgBcsMap().remove(this.compPkgBc.getID());
        getEntityProvider().getDbCompPkg(this.compPkg.getID()).getTCompPkgBcs().remove(this.compPkgBc);
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
    return null == this.compPkgBc ? null : this.compPkgBc.getID();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Base Component";
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
        addTransactionSummaryDetails(detailsList, this.oldCpBCName, this.newCpBCName, "BC Name");
        addTransactionSummaryDetails(detailsList, null == this.oldBCSeqNo ? "" : this.oldBCSeqNo.toString(),
            null == this.newBCSeqNo ? "" : this.newBCSeqNo.toString(), "Sequence");
        break;
      case DELETE:
        addTransactionSummaryDetails(detailsList, this.oldCpBCName, "", "BC Name");
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
    String objectIdentifier;
    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = this.newCpBCName;
        break;
      case UPDATE:
      case DELETE:
        objectIdentifier = this.oldCpBCName;
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }
    return objectIdentifier;
  }

}
