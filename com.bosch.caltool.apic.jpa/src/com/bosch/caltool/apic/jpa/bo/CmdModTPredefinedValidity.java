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
import com.bosch.caltool.icdm.database.entity.apic.TPredefinedValidity;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;

/**
 * @author bru2cob
 */
public class CmdModTPredefinedValidity extends AbstractCommand {

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
  private static final String ENTITY_ID = "GRP_ATTR_VAL_DEP_ID";

  private PredefinedAttrValuesValidity predefinedAttrValidity;
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
  public CmdModTPredefinedValidity(final AbstractDataProvider dataProvider, final Attribute valAttribute,
      final AttributeValue valAttrValue, final AttributeValue grpAttrValue) {
    super(dataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.valAttribute = valAttribute;
    this.valAttrValue = valAttrValue;
    this.grpAttrValue = grpAttrValue;
  }

  /**
   * Constructor for delete
   *
   * @param dataProvider data provider
   * @param predefinedAttrValidity validity entry to be deleted
   */
  public CmdModTPredefinedValidity(final AbstractDataProvider dataProvider,
      final PredefinedAttrValuesValidity predefinedAttrValidity) {
    super(dataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.DELETE;
    this.predefinedAttrValidity = predefinedAttrValidity;
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

    final TPredefinedValidity newDbPreDefinedVal = new TPredefinedValidity();
    // set the values to this entity
    setNewAttrDepencyFields(newDbPreDefinedVal);
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbPreDefinedVal);

    setUserDetails(COMMAND_MODE.INSERT, newDbPreDefinedVal, ENTITY_ID);

    Long valueID = this.grpAttrValue.getValueID();
    if (getEntityProvider().getDbValue(valueID).gettGroupAttrValidity() == null) {
      getEntityProvider().getDbValue(valueID).settGroupAttrValidity(new ArrayList<TPredefinedValidity>());
    }
    getEntityProvider().getDbValue(valueID).gettGroupAttrValidity().add(newDbPreDefinedVal);

    this.predefinedAttrValidity =
        new PredefinedAttrValuesValidity(getDataProvider(), newDbPreDefinedVal.getValidityId());
    getChangedData().put(newDbPreDefinedVal.getValidityId(), new ChangedData(ChangeType.INSERT,
        newDbPreDefinedVal.getValidityId(), TWorkpackageDivision.class, DisplayEventSource.COMMAND));

  }

  /**
   * @param newDbPreDefinedVal
   * @param isUpdate
   */
  private void setNewAttrDepencyFields(final TPredefinedValidity newDbPreDefinedVal) {
    // Value dependency, valueID will be filled
    final Long valueID = this.grpAttrValue.getValueID();
    final TabvAttrValue dbAttrValue = getEntityProvider().getDbValue(valueID);
    newDbPreDefinedVal.setGrpAttrVal(dbAttrValue);
    final TabvAttribute dbDepenAttr = getEntityProvider().getDbAttribute(this.valAttribute.getID());
    newDbPreDefinedVal.setValidityAttribute(dbDepenAttr);
    // Populate the dependent ValueID
    if (null != this.valAttrValue) {
      final TabvAttrValue dbDepenAttrValue = getEntityProvider().getDbValue(this.valAttrValue.getID());
      newDbPreDefinedVal.setValidityValue(dbDepenAttrValue);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.predefinedAttrValidity.getID(),
        TPredefinedValidity.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.predefinedAttrValidity.getObjectDetails());
    final TPredefinedValidity dbPredefinedVal =
        getEntityProvider().getDbPredValidity(this.predefinedAttrValidity.getID());
    getEntityProvider().getDbValue(this.predefinedAttrValidity.getGroupedAttrValue().getID()).gettGroupAttrValidity()
        .remove(dbPredefinedVal);
    getEntityProvider().deleteEntity(dbPredefinedVal);
    // put changed data to changed data map
    getChangedData().put(this.predefinedAttrValidity.getID(), chdata);

    // remove from map
    getDataCache().getPredAttrValValidityMap().remove(dbPredefinedVal.getValidityId());
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
    // TODO Auto-generated method stub
    return false;
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

    if (this.commandMode == COMMAND_MODE.INSERT) {
      final TransactionSummaryDetails details = new TransactionSummaryDetails();
      details.setOldValue("");
      details.setNewValue(getPrimaryObjectIdentifier());
      details.setModifiedItem(getPrimaryObjectType());
      detailsList.add(details);
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);


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
    return this.predefinedAttrValidity == null ? null : this.predefinedAttrValidity.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Dependency validity";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    TPredefinedValidity dbPredefinedVal = getEntityProvider().getDbPredValidity(this.predefinedAttrValidity.getID());
    if (null != dbPredefinedVal) {
      dbPredefinedVal.getGrpAttrVal().getTabvAttribute().getAttrNameEng();
    }
    return null;
  }

}
