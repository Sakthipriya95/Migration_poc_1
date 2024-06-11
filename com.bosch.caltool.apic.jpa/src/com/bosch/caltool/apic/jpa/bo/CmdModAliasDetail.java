/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.AliasDetail.ALIAS_DETAIL_TYPE;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;


/**
 * Command for inserting,deleting and updating the alias detail
 *
 * @author rgo7cob
 */
public class CmdModAliasDetail extends AbstractCommand {


  /**
   * alias detail object. One reference fir all.
   */
  private AliasDetail aliasDetail;


  /**
   * Old name
   */
  private String oldAliasName;


  /**
   * @param oldAliasName the oldAliasName to set
   */
  public void setOldAliasName(final String oldAliasName) {
    this.oldAliasName = oldAliasName;
  }


  /**
   * @param newAliasName the newAliasName to set
   */
  public void setNewAliasName(final String newAliasName) {
    this.newAliasName = newAliasName;
  }


  /**
   * new name
   */
  private String newAliasName;
  /**
   * attribute used for alias
   */
  private Attribute attribute;
  /**
   * attribute value for the alias
   */
  private AttributeValue attrValue;
  /**
   * alias definition associated with the alias
   */
  private AliasDefinition aliasDef;


  /**
   * @return the attribute
   */
  public Attribute getAttribute() {
    return this.attribute;
  }


  /**
   * @param attribute the attribute to set
   */
  public void setAttribute(final Attribute attribute) {
    this.attribute = attribute;
  }


  /**
   * @return the attrValue
   */
  public AttributeValue getAttrValue() {
    return this.attrValue;
  }


  /**
   * @param attrValue the attrValue to set
   */
  public void setAttrValue(final AttributeValue attrValue) {
    this.attrValue = attrValue;
  }

  /**
   * @return the aliasDef
   */
  public AliasDefinition getAliasDef() {
    return this.aliasDef;
  }


  /**
   * @param aliasDef the aliasDef to set
   */
  public void setAliasDef(final AliasDefinition aliasDef) {
    this.aliasDef = aliasDef;
  }


  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  private static final String ALIAS_DET_ENTITY_ID = "ALIAS_DET_ENTITY_ID";

  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Constructor to add a new Attribute - use this constructor for INSERT
   *
   * @param apicDataProvider data provider
   */
  public CmdModAliasDetail(final ApicDataProvider apicDataProvider) {
    super(apicDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor to delete or update existing Attribute
   *
   * @param apicDataProvider data provider
   * @param modifyAliasDet attribute
   * @param isDelete whether to delete or not
   */
  public CmdModAliasDetail(final ApicDataProvider apicDataProvider, final AliasDetail modifyAliasDet,
      final boolean isDelete) {
    super(apicDataProvider);

    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
      // the Attribute to be deleted, rember for undo
      // get the DB entity for the Attribute to be modified
      // ICDM-229 changes
      this.attribute = modifyAliasDet.getAttribute();
      this.attrValue = modifyAliasDet.getAttrValue();
      this.aliasDetail = modifyAliasDet;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      // the Attribute to be modified
      // get the DB entity for the Attribute to be modified
      // ICDM-229 changes
      this.attribute = modifyAliasDet.getAttribute();
      this.attrValue = modifyAliasDet.getAttrValue();
      this.aliasDetail = modifyAliasDet;
    }
    // initialize command with current values from UI
    setAliasDetToCmd(modifyAliasDet);
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifyAliasDet
   */
  /**
   * @param modifyAliasDet
   */
  private void setAliasDetToCmd(final AliasDetail modifyAliasDet) {

    this.oldAliasName = modifyAliasDet.getAliasName();
    this.newAliasName = this.oldAliasName;

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException CommandException
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    // create a new database entity
    // ICDM-229
    final TAliasDetail newDbAliasDetail = new TAliasDetail();
    TAliasDefinition dbAliasDefinition = getEntityProvider().getDbAliasDefinition(this.aliasDef.getID());
    newDbAliasDetail.setTAliasDefinition(dbAliasDefinition);
    newDbAliasDetail.setAliasName(this.newAliasName);
    if (this.attribute == null) {
      newDbAliasDetail.setTabvAttrValue(getEntityProvider().getDbValue(this.attrValue.getValueID()));
    }
    else {
      newDbAliasDetail.setTabvAttribute(getEntityProvider().getDbAttribute(this.attribute.getAttributeID()));
    }

    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, newDbAliasDetail, ALIAS_DET_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbAliasDetail);
    // Create the new Object and add it to the map.
    this.aliasDetail = new AliasDetail(getDataProvider(), newDbAliasDetail.getAliasDetailsId());
    if (this.aliasDetail.getAliasType() == ALIAS_DETAIL_TYPE.ATTRIBUTE_ALIAS) {
      this.aliasDef.getAliasDetAttrMap().put(this.aliasDetail.getAttribute().getID(), this.aliasDetail);
    }
    else {
      this.aliasDef.getAliasDetAttrValMap().put(this.aliasDetail.getAttrValue().getID(), this.aliasDetail);
    }
    if (dbAliasDefinition.getTAliasDetails() == null) {
      dbAliasDefinition.setTAliasDetails(new ArrayList<TAliasDetail>());
    }
    dbAliasDefinition.getTAliasDetails().add(newDbAliasDetail);

    // Icdm-461 Changes
    getChangedData().put(newDbAliasDetail.getAliasDetailsId(), new ChangedData(ChangeType.INSERT,
        newDbAliasDetail.getAliasDetailsId(), TAliasDetail.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    // Check for any parallel changes
    // ICDM-229 Changes for DB notifiacation

    // Icdm-461 Changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.aliasDetail.getID(), TAliasDetail.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.aliasDetail.getObjectDetails());
    final TAliasDetail modifiedAliasDet = getEntityProvider().getDbAliasDetail(this.aliasDetail.getID());
    validateStaleData(modifiedAliasDet);
    // Only set tha new alias name
    modifiedAliasDet.setAliasName(this.newAliasName);
    setUserDetails(COMMAND_MODE.UPDATE, modifiedAliasDet, ALIAS_DET_ENTITY_ID);

    // Icdm-461 Changes
    getChangedData().put(this.aliasDetail.getID(), chdata);


  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {

    // Check for any parallel changes
    // ICDM-229 Changes for DB notifiacation
    // Icdm-461 Changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.aliasDetail.getID(), TAliasDetail.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.aliasDetail.getObjectDetails());
    // get the alias detail
    final TAliasDetail dbAliasDet = getEntityProvider().getDbAliasDetail(this.aliasDetail.getID());

    validateStaleData(dbAliasDet);
    AliasDefinition aliasDefinition = this.aliasDetail.getAliasDefinition();

    removeAlDetFromAlDef(aliasDefinition, this.aliasDetail);
    getEntityProvider().deleteEntity(dbAliasDet);
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.DELETE, dbAliasDet, ALIAS_DET_ENTITY_ID);

    // Icdm-461 Changes
    getChangedData().put(this.aliasDetail.getID(), chdata);

  }


  /**
   * @param aliasDefinition aliasDefinition
   * @param aliasDetail aliasDetail
   */
  private void removeAlDetFromAlDef(final AliasDefinition aliasDefinition, final AliasDetail aliasDetail) {
    if (CommonUtils.isNull(aliasDetail.getAttrValue())) {
      aliasDefinition.getAliasDetAttrMap().remove(aliasDetail.getAttribute().getAttributeID());
    }
    else {
      aliasDefinition.getAliasDetAttrValMap().remove(aliasDetail.getAttrValue().getValueID());
    }

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // To be implemented

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    // to be implemented
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    // to be implemented
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldAliasName, this.newAliasName);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable

  }


  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        if (null != this.aliasDetail.getID()) {
          removeAlDetFromAlDef(this.aliasDef, this.aliasDetail);
        }
        break;
      case DELETE:
        AliasDefinition aliasDefinition = this.aliasDetail.getAliasDefinition();
        aliasDefinition.addAliasDetailToMap(this.aliasDetail);
        break;
      case UPDATE:
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
    return this.aliasDetail == null ? null : this.aliasDetail.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "AliasDetail";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    switch (this.commandMode) {
      case INSERT:
        detailsForCmdIns(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldAliasName, this.newAliasName, "Alias Name");
        break;
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
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
  private void detailsForCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details = new TransactionSummaryDetails();
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

    Long dbAliasDetID;
    dbAliasDetID = this.aliasDetail.getID();


    final TAliasDetail dbAliasDetail = getEntityProvider().getDbAliasDetail(dbAliasDetID);
    if (dbAliasDetail == null) {
      return " INVALID!";
    }

    return dbAliasDetail.getAliasName();
  }


}
