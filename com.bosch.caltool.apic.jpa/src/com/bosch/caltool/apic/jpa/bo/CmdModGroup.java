/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Command class to insert and update Groups.
 */
public class CmdModGroup extends AbstractCmdModProjGroup {

  private static final String GRP_ENTITY_ID = "GRP_ENTITY_ID";

  /**
   * the DB Attribute to be modified ICDM-229 changes
   */
  private Long modifyDbGroupID;

  /**
   * the DB Attribute to be inserted
   */
  private Long newDbGroupId;


  private AttrSuperGroup attrSuperGroup;


  /**
   * Constructor to add a new {@link AttrGroup} - use this constructor for INSERT
   *
   * @param dataProvider the Apic Data Provider
   * @param attrSuperGroup Parent Super Group of created Group
   */
  public CmdModGroup(final ApicDataProvider dataProvider, final AttrSuperGroup attrSuperGroup) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.attrSuperGroup = attrSuperGroup;
  }

  /**
   * Constructor to delete or update existing {@link AttrGroup}
   *
   * @param apicDataProvider the Apic Data Provider
   * @param modifyAttrGroup the Group to be modified
   * @param isDelete flag indicating update
   */
  public CmdModGroup(final ApicDataProvider apicDataProvider, final AttrGroup modifyAttrGroup, final boolean isDelete) {
    super(apicDataProvider);

    // Set the appropriate command mode
    if (!isDelete) {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      // the AttrGroup to be modified
      // get the DB entity for the AttrGroup to be modified
      // Changes for ICDM-229
      this.modifyDbGroupID = modifyAttrGroup.getGroupID();
    }
    // initialize command with current values from UI
    setAttributeFieldsToCommand(modifyAttrGroup);
  }

  @Override
  protected void executeInsertCommand() throws CommandException {
    // create a new database entity
    // ICDM-229
    final TabvAttrGroup newDbGroup = new TabvAttrGroup();
    setNewAttributeFields(newDbGroup);

    // Constants
    newDbGroup.setDeletedFlag(ApicConstants.CODE_NO);
    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, newDbGroup, GRP_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbGroup);
    this.newDbGroupId = newDbGroup.getGroupId();
    // add the new attributeGroup to the Map of all attributeGroups
    // (the constructor of AttrGroup will add the new object)
    final AttrGroup dbAttr = new AttrGroup(getDataProvider(), newDbGroup.getGroupId());
    getDataProvider().getAllGroups().put(dbAttr.getGroupID(), dbAttr);

    final AttrSuperGroup attrSuperGrp =
        getDataProvider().getAllSuperGroups().get(this.attrSuperGroup.getSuperGroupID());
    attrSuperGrp.getGroups().add(dbAttr);

  }

  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Check for any parallel changes
    // Changes for ICDM-229 starts for DB notification
    final TabvAttrGroup modifiedDbGroup = getEntityProvider().getDbGroup(this.modifyDbGroupID);
    checkParallelChanges(modifiedDbGroup);
    // Update modified data
    setNewAttributeFields(modifiedDbGroup);

    if (null != this.cmdMultipleLinks) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedDbGroup, GRP_ENTITY_ID);

    // Changes for ICDM-229 ends for DB notification
  }

  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO: Not required at the moment
  }

  @Override
  protected void undoInsertCommand() {
    // Check for any parallel changes
    final TabvAttrGroup dbGroup = getEntityProvider().getDbGroup(this.newDbGroupId);
    checkParallelChanges(dbGroup);
    // unregister the attr
    getEntityProvider().deleteEntity(dbGroup);
    // remove the AttrGroup from the map of all AttrGroups
    final AttrGroup dbAttr = new AttrGroup(getDataProvider(), dbGroup.getGroupId());
    getDataProvider().getAllGroups().remove(dbAttr.getGroupID());

    final AttrSuperGroup attrSuperGrp =
        getDataProvider().getAllSuperGroups().get(this.attrSuperGroup.getSuperGroupID());
    attrSuperGrp.getGroups().remove(dbAttr);

  }

  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Check for any parallel changes

    // Changes for ICDM-229 starts for DB notification
    final TabvAttrGroup modifiedDbGroup = getEntityProvider().getDbGroup(this.modifyDbGroupID);
    checkParallelChanges(modifiedDbGroup);
    // Update modified data,
    setOldAttributeFields(modifiedDbGroup);
    // sets to old modified user and date
    setUserDetails(COMMAND_MODE.UPDATE, modifiedDbGroup, GRP_ENTITY_ID);

    // Changes for ICDM-229 starts for DB notification

  }

  @Override
  protected void undoDeleteCommand() {
    // TODO Not required at the moment

  }

  private void setNewAttributeFields(final TabvAttrGroup dbAttr) {

    dbAttr.setGroupNameEng(this.newNameEng);// not nullable
    dbAttr.setGroupNameGer(this.newNameGer);
    dbAttr.setGroupDescEng(this.newDescEng);
    dbAttr.setGroupDescGer(this.newDescGer);

    if (this.attrSuperGroup != null) {
      final long dbAttrID = this.attrSuperGroup.getSuperGroupID();
      dbAttr.setTabvAttrSuperGroup(getEntityProvider().getDbSuperGroup(dbAttrID));
    }

  }

  /**
   * @param dbAttr
   */
  private void setOldAttributeFields(final TabvAttrGroup dbAttr) {

    dbAttr.setGroupNameEng(this.oldNameEng);
    dbAttr.setGroupNameGer(this.oldNameGer);
    dbAttr.setGroupDescEng(this.oldDescEng);
    dbAttr.setGroupDescGer(this.oldDescGer);

    if (this.attrSuperGroup != null) {
      final long dbAttrID = this.attrSuperGroup.getSuperGroupID();
      dbAttr.setTabvAttrSuperGroup(getEntityProvider().getDbSuperGroup(dbAttrID));
    }
    // Constants
    dbAttr.setDeletedFlag(ApicConstants.CODE_NO);

  }

  private void setAttributeFieldsToCommand(final AttrGroup modifyAttrGroup) {
    // ENGLISH Name
    this.oldNameEng = modifyAttrGroup.getGroupNameEng();
    this.newNameEng = this.oldNameEng;
    // ENGLISH Desc
    this.oldDescEng = modifyAttrGroup.getGroupDescEng();
    this.newDescEng = this.oldDescEng;

    // GERMAN Name
    this.oldNameGer = modifyAttrGroup.getGroupNameGer();
    this.newNameGer = this.oldNameGer;
    // GERMAN Desc
    this.oldDescGer = modifyAttrGroup.getGroupDescGer();
    this.newDescGer = this.oldDescGer;

  }

  /**
   * Method checks for parallel chages in DB (using version )
   */
  private void checkParallelChanges(final TabvAttrGroup dbAttr) {
    // check is parallel change happened
    final Long oldVersion = dbAttr.getVersion();
    // Refresh cache
    TabvAttrGroup refreshedDbAttr = (TabvAttrGroup) getEntityProvider().refreshCacheObject(dbAttr);
    // get new version
    final Long newVersion = refreshedDbAttr.getVersion();
    // log a warning in case of parallel changes
    if (!oldVersion.equals(newVersion)) {
      getEntityProvider().logger.warn("parallel change in Super Group: " + refreshedDbAttr.getGroupId());
    }
  }

  @Override
  protected void doPostCommit() {
    // TODO Not Applicable
  }

  /**
   * Get newly added group to enable selection after adding
   *
   * @return TabvAttribute newDbAttr
   */
  public TabvAttrGroup getNewDbGrp() {
    return getEntityProvider().getDbGroup(this.newDbGroupId);
  }

  /**
   * {@inheritDoc} 177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        caseCmdIns();
        break;
      default:
        // Do nothing
        break;
    }
  }

  /**
   * 
   */
  private void caseCmdIns() {
    getDataProvider().getAllGroups().remove(this.newDbGroupId);
    final AttrSuperGroup attrSuperGrp =
        getDataProvider().getAllSuperGroups().get(this.attrSuperGroup.getSuperGroupID());
    final AttrGroup newGroup = new AttrGroup(getDataProvider(), this.newDbGroupId);
    attrSuperGrp.getGroups().remove(newGroup);
  }

  /**
   * {@inheritDoc} return the id of the new attr group in case of insert & update mode, return null in case of delete
   * mode
   */
  @Override
  public Long getPrimaryObjectID() {

    if (this.commandMode == COMMAND_MODE.INSERT) {
      return this.newDbGroupId;
    }

    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return this.modifyDbGroupID;
    }

    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Attribute Group";
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
        caseCmdInsert(detailsList, details);
        break;
      case UPDATE:
        caseCmdUpdate(detailsList);
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
  private void caseCmdUpdate(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Description (German)");
    addTransactionSummaryDetails(detailsList, this.oldNameEng, this.newNameEng, "Name (English)");
    addTransactionSummaryDetails(detailsList, this.oldNameGer, this.newNameGer, "Name  (German)");
    this.summaryData.setObjectName(this.newNameEng);
  }

  /**
   * @param detailsList
   * @param details
   */
  private void caseCmdInsert(final SortedSet<TransactionSummaryDetails> detailsList,
      final TransactionSummaryDetails details) {
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
    return this.newNameEng;
  }

  /**
   * @param cmdMultipleLinks CmdModMultipleLinkss
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;
  }

}
