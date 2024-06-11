/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Command class to insert, update, delete Super Groups.
 */
public class CmdModSuperGroup extends AbstractCmdModProjGroup {

  private static final String SGRP_ENTITY_ID = "SGRP_ENTITY_ID";

  /**
   * the DB Attribute to be modified
   */
  private Long mdfyDbSprGrpID;

  /**
   * the DB Attribute to be inserted
   */
  private Long newDbSprGroupID;

  /**
   * the Command For Top level Entity
   */
  private CmdModTopLevelEntity cmdTopLevel;

  /**
   * Constructor to add a new {@link AttrSuperGroup} - use this constructor for INSERT
   *
   * @param apicDataProvider the Apic Data Provider
   */
  public CmdModSuperGroup(final ApicDataProvider apicDataProvider) {
    super(apicDataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor to delete or update existing {@link AttrSuperGroup}
   *
   * @param apicDataProvider the Apic Data Provider
   * @param modAttrSuperGrp the SuperGroup to be modified
   * @param isDelete flag indicating update
   */
  public CmdModSuperGroup(final ApicDataProvider apicDataProvider, final AttrSuperGroup modAttrSuperGrp,
      final boolean isDelete) {
    super(apicDataProvider);

    // Set the appropriate command mode
    if (!isDelete) {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      // get the DB entity for the Attribute to be modified
      // ICDM-229 Changes
      this.mdfyDbSprGrpID = modAttrSuperGrp.getSuperGroupID();
    }
    // initialize command with current values from UI
    setAttributeFieldsToCommand(modAttrSuperGrp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // create a new database entity
    // ICDM-229
    final TabvAttrSuperGroup newDbSuperGroup = new TabvAttrSuperGroup();
    setNewAttributeFields(newDbSuperGroup);
    // Constants as of now
    newDbSuperGroup.setDeletedFlag(ApicConstants.CODE_NO);
    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, newDbSuperGroup, SGRP_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbSuperGroup);
    this.newDbSprGroupID = newDbSuperGroup.getSuperGroupId();
    // add the new AttrSuperGroup to the list of all AttrSuperGroups
    // (the constructor of AttrSuperGroup will add the new object)
    final AttrSuperGroup dbAttr = new AttrSuperGroup(getDataProvider(), newDbSuperGroup.getSuperGroupId());
    getDataProvider().getAllSuperGroups().put(dbAttr.getSuperGroupID(), dbAttr);
    // icdm-474 Dcn for Top level entity
    this.cmdTopLevel = new CmdModTopLevelEntity(getDataProvider(), ApicConstants.TOP_LVL_ENT_ID_SUPER_GRP);
    this.childCmdStack.addCommand(this.cmdTopLevel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Check for any parallel changes
    // ICDM-229 changes for DB notifications
    final TabvAttrSuperGroup modifiedSrpGrp = getEntityProvider().getDbSuperGroup(this.mdfyDbSprGrpID);
    checkParallelChanges(modifiedSrpGrp);
    // Update modified data
    setNewAttributeFields(modifiedSrpGrp);
    if (null != this.cmdMultipleLinks) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedSrpGrp, SGRP_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO: Not required at the moment
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TabvAttrSuperGroup dbSuperGroup = getEntityProvider().getDbSuperGroup(this.newDbSprGroupID);
    checkParallelChanges(dbSuperGroup);
    // unregister the attr
    getEntityProvider().deleteEntity(dbSuperGroup);
    // remove the NodeAccessRight from the list of all NodeAccessRights
    getDataProvider().getAllSuperGroups().remove(dbSuperGroup.getSuperGroupId());
    // icdm-474 Dcn for Top level entity
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Check for any parallel changes
    // ICDM-229 changes for DB notifications
    final TabvAttrSuperGroup modifiedSrpGrp = getEntityProvider().getDbSuperGroup(this.mdfyDbSprGrpID);
    checkParallelChanges(modifiedSrpGrp);
    // Update modified data
    setOldAttributeFields(modifiedSrpGrp);

    this.childCmdStack.undoAll();

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedSrpGrp, SGRP_ENTITY_ID);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    // TODO: Not required at the moment
  }

  /**
   * Method sets the required fields of the {@link TabvAttrSuperGroup} entity with old values Used for undo
   */
  private void setOldAttributeFields(final TabvAttrSuperGroup dbAttr) {

    dbAttr.setSuperGroupNameEng(this.oldNameEng);
    dbAttr.setSuperGroupNameGer(this.oldNameGer);
    dbAttr.setSuperGroupDescEng(this.oldDescEng);
    dbAttr.setSuperGroupDescGer(this.oldDescGer);

    // Constants
    dbAttr.setDeletedFlag(ApicConstants.CODE_NO);

  }

  /**
   * @param cmdMultipleLinks the cmdMultipleLinks to set
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;
  }

  /**
   * Set required fields to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifyAttribute
   */
  private void setAttributeFieldsToCommand(final AttrSuperGroup modAttrSuperGrp) {
    // ENGLISH Name
    this.oldNameEng = modAttrSuperGrp.getSuperGroupNameEng();
    this.newNameEng = this.oldNameEng;
    // ENGLISH Desc
    this.oldDescEng = modAttrSuperGrp.getSuperGroupDescEng();
    this.newDescEng = this.oldDescEng;

    // GERMAN Name
    this.oldNameGer = modAttrSuperGrp.getSuperGroupNameGer();
    this.newNameGer = this.oldNameGer;
    // GERMAN Desc
    this.oldDescGer = modAttrSuperGrp.getSuperGroupDescGer();
    this.newDescGer = this.oldDescGer;

  }

  /**
   * Method sets the required fields of the {@link TabvAttrSuperGroup} entity with new values
   */
  private void setNewAttributeFields(final TabvAttrSuperGroup dbAttr) {

    dbAttr.setSuperGroupNameEng(this.newNameEng);
    dbAttr.setSuperGroupNameGer(this.newNameGer);
    dbAttr.setSuperGroupDescEng(this.newDescEng);
    dbAttr.setSuperGroupDescGer(this.newDescGer);

  }

  /**
   * Method checks for parallel changes in DB (using version )
   */
  private void checkParallelChanges(final TabvAttrSuperGroup dbAttr) {
    // check is parallel change happened
    final Long oldVersion = dbAttr.getVersion();
    // Refresh cache
    TabvAttrSuperGroup refreshedSuperGrp = (TabvAttrSuperGroup) getEntityProvider().refreshCacheObject(dbAttr);
    // get new version
    final Long newVersion = refreshedSuperGrp.getVersion();
    // log a warning in case of parallel changes
    if (!oldVersion.equals(newVersion)) {
      getEntityProvider().logger.warn("parallel change in Super Group: " + refreshedSuperGrp.getSuperGroupId());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Not Applicable
  }

  /**
   * Get newly added Super group to enable selection after adding
   *
   * @return TabvAttribute newDbAttr
   */
  public TabvAttrSuperGroup getNewSuperDbGrp() {
    // ICDM-229
    return getEntityProvider().getDbSuperGroup(this.newDbSprGroupID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        getDataProvider().getAllSuperGroups().remove(this.newDbSprGroupID);
        break;
      default:
        // Do nothing
        break;
    }
  }

  /**
   * {@inheritDoc} returns the id of the super group that has been modified
   */
  @Override
  public Long getPrimaryObjectID() {

    if (this.commandMode == COMMAND_MODE.INSERT) {
      return this.newDbSprGroupID;
    }

    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return this.mdfyDbSprGrpID;
    }

    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Super Group";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {


    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    final TransactionSummaryDetails details = new TransactionSummaryDetails();

    switch (this.commandMode) {
      case INSERT:
        details.setOldValue("");
        details.setNewValue(getPrimaryObjectIdentifier());
        details.setModifiedItem(getPrimaryObjectType());
        detailsList.add(details);
        break;
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
    addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Description (German)");
    addTransactionSummaryDetails(detailsList, this.oldNameEng, this.newNameEng, "Name (English)");
    addTransactionSummaryDetails(detailsList, this.oldNameGer, this.newNameGer, "Name  (German)");
    this.summaryData.setObjectName(this.newNameEng);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.newNameEng;
  }

}
