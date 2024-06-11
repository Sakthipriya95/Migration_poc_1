/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TLink;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;


/**
 * ICDM-764 This class is to add links to usecase,usecase sections,attributes
 *
 * @author mkl2cob
 */
public class CmdModLinks extends AbstractCommand {

  private final AbstractDataObject abstractDataObj;

  private final String nodeType;

  // old & new values
  private String oldLink;
  private String newLink;

  private String oldDescEng;
  private String newDescEng;

  private String oldDescGer;
  private String newDescGer;

  /**
   * Unique entity id for setting user details
   */
  private static final String LINK_ENTITY_ID = "LINK_ENTITY_ID";

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Business object
   */
  private Link linkObj;

  /**
   * constructor for insert mode
   *
   * @param dataProvider ApicDataProvider
   * @param abstractDataObj ApicObject Could be usecase,usecase section or attribute
   */
  public CmdModLinks(final ApicDataProvider dataProvider, final AbstractDataObject abstractDataObj) {
    super(dataProvider);
    this.abstractDataObj = abstractDataObj;
    this.nodeType = this.abstractDataObj.getEntityType().getEntityTypeString();
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * constructor for update/delete mode
   *
   * @param dataProvider ApicDataProvider
   * @param link Link obj which has to be modified or deleted
   * @param isDelete boolean that tells whether mode is update or delete
   * @param node Parent node to which links are added
   */
  public CmdModLinks(final AbstractDataProvider dataProvider, final Link link, final boolean isDelete,
      final AbstractDataObject node) {

    super(dataProvider);
    this.linkObj = link;
    this.abstractDataObj = node;
    this.nodeType = this.abstractDataObj.getEntityType().getEntityTypeString();


    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    // initialize command with current values
    setOldNewValsToCommand(link);
  }

  /**
   * This method sets the old & new values
   *
   * @param obj
   */
  private void setOldNewValsToCommand(final Link link) {
    this.oldLink = link.getLink();
    this.newLink = this.oldLink;

    this.oldDescEng = link.getDescEng();
    this.newDescEng = this.oldDescEng;

    this.oldDescGer = link.getDescGer();
    this.newDescGer = this.oldDescGer;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        getDataCache().getLinks(this.abstractDataObj.getID(), this.nodeType).remove(this.linkObj.getID());
        break;
      case DELETE:
        getDataCache().getLinks(this.abstractDataObj.getID(), this.nodeType).put(this.linkObj.getID(), this.linkObj);
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

    final TLink dbLink = new TLink();
    dbLink.setNodeId(this.abstractDataObj.getID());
    dbLink.setNodeType(this.nodeType);
    setNewValues(dbLink);
    if (this.nodeType.equals(EntityType.ATTRIB_VALUE.name())) {
      TabvAttrValue dbValue = getEntityProvider().getDbValue(this.abstractDataObj.getID());
      dbLink.setTabvAttrValue(dbValue);
      dbValue.gettLinks().add(dbLink);
    }
    setUserDetails(this.commandMode, dbLink, LINK_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbLink);

    // create business object
    this.linkObj = new Link(getDataProvider(), dbLink.getLinkId());
    // put the BO in the cache map
    getDataCache().getLinks(this.abstractDataObj.getID(), this.nodeType).put(this.linkObj.getID(), this.linkObj);
    getDataCache().getLinkMap().put(this.linkObj.getID(), this.linkObj);
    // put changed data to changed data map
    getChangedData().put(this.linkObj.getID(),
        new ChangedData(ChangeType.INSERT, this.linkObj.getID(), TLink.class, DisplayEventSource.COMMAND));

    // ICDM-959
    // put the BO in the linkNodes map
    getDataCache().addToLinkNodeIdsMap(this.abstractDataObj.getID(), this.nodeType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final TLink modifiedLink = getEntityProvider().getDbLink(this.linkObj.getID());
    // create changed data & put the old values
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.linkObj.getID(), TLink.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.linkObj.getObjectDetails());
    validateStaleData(modifiedLink);
    setNewValues(modifiedLink);
    setUserDetails(this.commandMode, modifiedLink, LINK_ENTITY_ID);
    // put changed data to changed data map
    getChangedData().put(this.linkObj.getID(), chdata);
  }

  /**
   * updates the db entity with new values
   *
   * @param modifiedLink
   */
  private void setNewValues(final TLink modifiedLink) {
    if (!CommonUtils.isEqual(this.oldLink, this.newLink)) {
      modifiedLink.setLinkUrl(this.newLink);
    }
    if (!CommonUtils.isEqual(this.oldDescEng, this.newDescEng)) {
      modifiedLink.setDescEng(this.newDescEng);
    }
    if (!CommonUtils.isEqual(this.oldDescGer, this.newDescGer)) {
      modifiedLink.setDescGer(this.newDescGer);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final TLink dbLink = getEntityProvider().getDbLink(this.linkObj.getID());
    // create changed data & put the old values
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.linkObj.getID(), TLink.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.linkObj.getObjectDetails());
    getEntityProvider().deleteEntity(dbLink);
    getDataCache().getLinks(this.abstractDataObj.getID(), this.nodeType).remove(this.linkObj.getID());
    getDataCache().getLinkMap().remove(this.linkObj.getID());
    // put changed data to changed data map
    getChangedData().put(this.linkObj.getID(), chdata);
    // ICDM-959
    getDataCache().removeFrmLinkNodeIdsMap(this.abstractDataObj, this.nodeType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Not applicable

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
    boolean returnVal = false;
    if (!this.oldLink.equals(this.newLink) || !this.oldDescEng.equals(this.newDescEng) ||
        ((this.oldDescGer != null) && !this.oldDescGer.equals(this.newDescGer))) {
      returnVal = true;
    }
    return returnVal;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
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
        addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Description (English)");
        addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Description (German)");
        addTransactionSummaryDetails(detailsList, this.oldLink, this.newLink, "Link");
        break;
      case DELETE:
        addTransactionSummaryDetails(detailsList, this.oldDescEng, "", getPrimaryObjectType());
        break;
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setObjectType(getPrimaryObjectType());
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
    details.setNewValue(this.newDescEng);
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
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
    Long objID = null;
    switch (this.commandMode) {
      case INSERT:
        objID = this.linkObj == null ? null : this.linkObj.getID();
        break;
      case UPDATE:
      case DELETE:
        objID = this.linkObj.getID();
        break;
      default:
        // Do nothing
        break;
    }
    return objID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Link for " + this.nodeType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.abstractDataObj.getName();
  }


  /**
   * @param newLink the newLink to set
   */
  public void setLink(final String newLink) {
    this.newLink = newLink;
  }

  /**
   * @param newDescEng the newDescEng to set
   */
  public void setDescEng(final String newDescEng) {
    this.newDescEng = newDescEng;
  }


  /**
   * @param newDescGer the newDescGer to set
   */
  public void setDescGer(final String newDescGer) {
    this.newDescGer = newDescGer;
  }

}
