/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;


/**
 * @author mkl2cob
 */
class CmdModQuesDepnAttrValue extends AbstractCDRCommand {

  private static final String QUES_ATTR_VAL_ENTITY_ID = "QUES_ATTR_VAL_ENTITY_ID";
  /**
   * QuestionDepenAttr
   */
  private final QuestionDepenAttr depnAttr;
  /**
   * AttributeValue
   */
  private final AttributeValue attrVal;
  /**
   * QuestionDepenAttrValue
   */
  private QuestionDepenAttrValue questionDepenAttrValue;

  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * QuesDepnValCombination
   */
  private final QuesDepnValCombination depnValCombi;

  /**
   * Constructor for INSERT mode
   *
   * @param dataProvider CDRDataProvider
   * @param depnAttr QuestionDepenAttr
   * @param attrVal AttributeValue
   * @param depnValCombi QuesDepnValCombination
   */
  public CmdModQuesDepnAttrValue(final CDRDataProvider dataProvider, final QuestionDepenAttr depnAttr,
      final AttributeValue attrVal, final QuesDepnValCombination depnValCombi) {
    super(dataProvider);
    this.depnAttr = depnAttr;
    this.attrVal = attrVal;
    this.commandMode = COMMAND_MODE.INSERT;
    this.depnValCombi = depnValCombi;
  }

  /**
   * Constructor for DELETE mode
   *
   * @param dataProvider CDRDataProvider
   * @param value QuestionDepenAttrValue
   * @param quesAttrValComb QuesDepnValCombination
   */
  public CmdModQuesDepnAttrValue(final CDRDataProvider dataProvider, final QuestionDepenAttrValue value,
      final QuesDepnValCombination quesAttrValComb) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.DELETE;
    this.questionDepenAttrValue = value;
    this.attrVal = value.getAttributeValue();
    this.depnAttr = value.getQuesDepnAttr();
    this.depnValCombi = quesAttrValComb;
  }

  /**
   * Constructor for UPDATE mode
   *
   * @param dataProvider CDRDataProvider
   * @param quesDepnAttrVal QuestionDepenAttrValue
   * @param attrVal AttributeValue
   * @param depnValCombi QuesDepnValCombination
   */
  public CmdModQuesDepnAttrValue(final CDRDataProvider dataProvider, final QuestionDepenAttrValue quesDepnAttrVal,
      final AttributeValue attrVal, final QuesDepnValCombination depnValCombi) {
    super(dataProvider);
    this.questionDepenAttrValue = quesDepnAttrVal;
    this.attrVal = attrVal;
    this.depnAttr = quesDepnAttrVal.getQuesDepnAttr();
    this.commandMode = COMMAND_MODE.UPDATE;
    this.depnValCombi = depnValCombi;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        getDataCache().getQuestionDepenAttributeMap().remove(this.questionDepenAttrValue.getID());
        break;
      case UPDATE:
      case DELETE:
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
    final TQuestionDepenAttrValue dbQuesAttrValue = new TQuestionDepenAttrValue();
    TQuestionDepenAttribute dbQuesDepenAttr = getEntityProvider().getDbQuestionDepenAttr(this.depnAttr.getID());
    dbQuesAttrValue.setTQuestionDepenAttribute(dbQuesDepenAttr);
    dbQuesAttrValue.setTabvAttrValue(getEntityProvider().getDbValue(this.attrVal.getValueID()));
    dbQuesAttrValue.setQCombiNum(this.depnValCombi.getCombinationId());
    setUserDetails(COMMAND_MODE.INSERT, dbQuesAttrValue, QUES_ATTR_VAL_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbQuesAttrValue);
    // get the list from ques depn attr
    Set<TQuestionDepenAttrValue> tQuestionDepenAttrValues = dbQuesDepenAttr.getTQuestionDepenAttrValues();
    if (null == tQuestionDepenAttrValues) {
      tQuestionDepenAttrValues = new HashSet<>();
      dbQuesDepenAttr.setTQuestionDepenAttrValues(tQuestionDepenAttrValues);
    }
    // add the created entity to the list
    dbQuesDepenAttr.getTQuestionDepenAttrValues().add(dbQuesAttrValue);
    this.questionDepenAttrValue = new QuestionDepenAttrValue(getDataProvider(), dbQuesAttrValue.getDepenAttrValId());
    getChangedData().put(dbQuesAttrValue.getDepenAttrValId(), new ChangedData(ChangeType.INSERT,
        dbQuesAttrValue.getDepenAttrValId(), TQuestionDepenAttrValue.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.questionDepenAttrValue.getID(),
        TQuestionDepenAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.questionDepenAttrValue.getObjectDetails());
    final TQuestionDepenAttrValue dbQuestionAttrVal =
        getEntityProvider().getDbQuestionDepenAttrValue(this.questionDepenAttrValue.getID());
    validateStaleData(dbQuestionAttrVal);
    // update the attribute value
    dbQuestionAttrVal.setTabvAttrValue(getEntityProvider().getDbValue(this.attrVal.getID()));
    setUserDetails(COMMAND_MODE.UPDATE, dbQuestionAttrVal, QUES_ATTR_VAL_ENTITY_ID);
    getChangedData().put(this.questionDepenAttrValue.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.questionDepenAttrValue.getID(),
        TQuestionDepenAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.questionDepenAttrValue.getObjectDetails());
    final TQuestionDepenAttrValue dbQuestionAttrVal =
        getEntityProvider().getDbQuestionDepenAttrValue(this.questionDepenAttrValue.getID());
    // remove the entity from the depn attr value list
    dbQuestionAttrVal.getTQuestionDepenAttribute().getTQuestionDepenAttrValues().remove(dbQuestionAttrVal);
    validateStaleData(dbQuestionAttrVal);
    getEntityProvider().deleteEntity(dbQuestionAttrVal);
    setUserDetails(COMMAND_MODE.DELETE, dbQuestionAttrVal, QUES_ATTR_VAL_ENTITY_ID);
    getChangedData().put(this.questionDepenAttrValue.getID(), chdata);
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
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Question Dependency Attribute Value" + getPrimaryObjectIdentifier());
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
      case DELETE:
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
    switch (this.commandMode) {
      case INSERT:
        // add in question dependency attr map
        this.depnValCombi.getQuesAttrValMap().put(this.depnAttr, this.questionDepenAttrValue);
        this.depnAttr.getQuestion().getQuesAttrValComb().put(this.depnValCombi.getCombinationId(), this.depnValCombi);
        break;
      case UPDATE:
        this.depnValCombi.getQuesAttrValMap().put(this.depnAttr, this.questionDepenAttrValue);
        break;
      case DELETE:
        // remove from cache
        getDataCache().getQuestionDepenAttributeMap().remove(this.questionDepenAttrValue.getID());
        this.depnValCombi.getAttrValMap().remove(this.depnAttr);
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
  public Long getPrimaryObjectID() {
    return this.questionDepenAttrValue == null ? null : this.questionDepenAttrValue.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Question Dependency Attribute Value";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.attrVal == null ? null : this.attrVal.getName();
  }

}
