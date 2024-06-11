/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;

/**
 * @author bru2cob
 */
public class CmdModPredefinedAttrVal extends AbstractCommand {

  /**
   * Dependant attribute obj
   */
  private Attribute valAttribute;
  /**
   * Dependant attribute value obj
   */
  private AttributeValue valAttrValue;
  /**
   * AttributeValue object for which the dependency is created
   */
  private AttributeValue grpAttrValue;
  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID_INS = "GRP_PREDEFINED_ATTR_VAL_INS_ID";
  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID_UPD = "GRP_PREDEFINED_ATTR_VAL_UPD_ID";

  private PredefinedAttrValue predefinedAttrVal;

  private Long oldDepenValID;
  private Long newDepenValID;
  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor to insert value
   *
   * @param dataProvider data provider
   * @param valAttribute validity attribute
   * @param valAttrValue validity attribute value
   * @param grpAttrValue group attribute value for which the validity is added
   */
  public CmdModPredefinedAttrVal(final AbstractDataProvider dataProvider, final Attribute valAttribute,
      final AttributeValue valAttrValue, final AttributeValue grpAttrValue) {
    super(dataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.valAttribute = valAttribute;
    this.valAttrValue = valAttrValue;
    this.grpAttrValue = grpAttrValue;
  }

  /**
   * Constructor for update/delete
   *
   * @param dataProvider data provider
   * @param predefinedAttrVal validity entry to be deleted/updated
   * @param isDelete true if delete
   */
  public CmdModPredefinedAttrVal(final AbstractDataProvider dataProvider, final PredefinedAttrValue predefinedAttrVal,
      final boolean isDelete) {
    super(dataProvider);

    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
      this.predefinedAttrVal = predefinedAttrVal;
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      this.predefinedAttrVal = predefinedAttrVal;

    }
    setDependencyFieldsToCommand(predefinedAttrVal);
  }

  /**
   * @param modifyAttrDepency
   */
  private void setDependencyFieldsToCommand(final PredefinedAttrValue modpredefinedAttrVal) {
    // Attr Dependency ValueID    
    //Task 229131
    if (null != modpredefinedAttrVal.getPredefinedAttributeValue()) {
      this.oldDepenValID = modpredefinedAttrVal.getPredefinedAttributeValue().getValueID();
    }
    else {
      this.oldDepenValID = null;
    }

    this.newDepenValID = this.oldDepenValID;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // create a new database entity

    final TPredefinedAttrValue newDbPreDefinedAttrVal = new TPredefinedAttrValue();
    // set the values to this entity
    setNewAttrDepencyFields(newDbPreDefinedAttrVal, false);
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbPreDefinedAttrVal);

    setUserDetails(COMMAND_MODE.INSERT, newDbPreDefinedAttrVal, ENTITY_ID_INS);

    Long valueID = this.grpAttrValue.getValueID();
    if (getEntityProvider().getDbValue(valueID).gettGroupAttrValue() == null) {
      getEntityProvider().getDbValue(valueID).settGroupAttrValue(new ArrayList<TPredefinedAttrValue>());
    }
    getEntityProvider().getDbValue(valueID).gettGroupAttrValue().add(newDbPreDefinedAttrVal);

    this.predefinedAttrVal = new PredefinedAttrValue(getDataProvider(), newDbPreDefinedAttrVal.getPreAttrValId());
    getChangedData().put(newDbPreDefinedAttrVal.getPreAttrValId(), new ChangedData(ChangeType.INSERT,
        newDbPreDefinedAttrVal.getPreAttrValId(), TWorkpackageDivision.class, DisplayEventSource.COMMAND));

  }

  /**
   * @param newDbPreDefinedAttrVal
   * @param isUpdate
   */
  private void setNewAttrDepencyFields(final TPredefinedAttrValue newDbPreDefinedAttrVal, final boolean isUpdate) {
    if (isUpdate) {
      if (null != this.newDepenValID) {
        final TabvAttrValue dbDepenAttrValue = getEntityProvider().getDbValue(this.newDepenValID);
        newDbPreDefinedAttrVal.setPreDefAttrVal(dbDepenAttrValue);
      }

    }
    else {
      // Value dependency, valueID will be filled
      final Long valueID = this.grpAttrValue.getValueID();
      final TabvAttrValue dbAttrValue = getEntityProvider().getDbValue(valueID);
      newDbPreDefinedAttrVal.setGrpAttrVal(dbAttrValue);
      final TabvAttribute dbDepenAttr = getEntityProvider().getDbAttribute(this.valAttribute.getID());
      newDbPreDefinedAttrVal.setPreDefinedAttr(dbDepenAttr);
      // Populate the dependent ValueID
      if (null != this.valAttrValue) {
        final TabvAttrValue dbDepenAttrValue = getEntityProvider().getDbValue(this.valAttrValue.getID());
        newDbPreDefinedAttrVal.setPreDefAttrVal(dbDepenAttrValue);
      }
    }
    setUserDetails(this.commandMode, newDbPreDefinedAttrVal, ENTITY_ID_UPD);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    final TPredefinedAttrValue modDbDependency = getEntityProvider().getDbPredAttrValue(this.predefinedAttrVal.getID());
    validateStaleData(modDbDependency);
    // Update modified data
    setNewAttrDepencyFields(modDbDependency, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.predefinedAttrVal.getID(),
        TPredefinedAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.predefinedAttrVal.getObjectDetails());
    final TPredefinedAttrValue dbPredefinedVal = getEntityProvider().getDbPredAttrValue(this.predefinedAttrVal.getID());
    getEntityProvider().getDbValue(this.predefinedAttrVal.getGroupedAttrValue().getID()).gettGroupAttrValue()
        .remove(dbPredefinedVal);

    getEntityProvider().deleteEntity(dbPredefinedVal);
    // put changed data to changed data map
    getChangedData().put(this.predefinedAttrVal.getID(), chdata);


    getDataCache().getPredAttrValMap().remove(dbPredefinedVal.getPreAttrValId());
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
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {

    return (((this.oldDepenValID == null) ? Long.valueOf(0)
        : this.oldDepenValID.longValue()) == ((this.newDepenValID == null) ? Long.valueOf(0)
            : this.newDepenValID.longValue())) ? false : true;
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

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, String.valueOf(this.oldDepenValID),
            String.valueOf(this.newDepenValID), "Attribute value");
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
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.predefinedAttrVal == null ? null : this.predefinedAttrVal.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Attribute value Dependency";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    TPredefinedAttrValue dbPredefinedVal = getEntityProvider().getDbPredAttrValue(this.predefinedAttrVal.getID());
    if (null != dbPredefinedVal) {
      dbPredefinedVal.getGrpAttrVal().getTabvAttribute().getAttrNameEng();
    }
    return null;
  }


  /**
   * @param newDepenValID the newDepenValID to set
   */
  public void setNewDepenValID(final Long newDepenValID) {
    this.newDepenValID = newDepenValID;
  }

}
