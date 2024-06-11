/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionDepenAttribute;


/**
 * @author mkl2cob
 */
public class CmdModQuesDepnAttr extends AbstractCDRCommand {

  private static final String QUES_ATTR_ENTITY_ID = "QUES_ATTR_ENTITY_ID";
  /**
   * Question instance
   */
  private final Question question;
  /**
   * QuestionDepenAttr
   */
  private QuestionDepenAttr questionAttr;
  /**
   * Attribute
   */
  private final Attribute attr;

  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor for INSERT
   *
   * @param dataProvider CDRDataProvider
   * @param question Question
   * @param attr Attribute
   */
  public CmdModQuesDepnAttr(final CDRDataProvider dataProvider, final Question question, final Attribute attr) {
    super(dataProvider);
    this.question = question;
    this.commandMode = COMMAND_MODE.INSERT;
    this.attr = attr;
  }

  /**
   * Constructor for DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param quesAttr QuestionDepenAttr
   */
  public CmdModQuesDepnAttr(final CDRDataProvider dataProvider, final QuestionDepenAttr quesAttr) {
    super(dataProvider);
    this.attr = quesAttr.getAttribute();
    this.questionAttr = quesAttr;
    this.question = this.questionAttr.getQuestion();
    this.commandMode = COMMAND_MODE.DELETE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        if (this.commandMode == COMMAND_MODE.INSERT) {
          getDataCache().getQuestionDepenAttributeMap().remove(this.questionAttr.getID());
        }
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
    final TQuestionDepenAttribute dbQuesAttr = new TQuestionDepenAttribute();
    TQuestion dbQuestion = getEntityProvider().getDbQuestion(this.question.getID());
    dbQuesAttr.setTQuestion(dbQuestion);
    dbQuesAttr.setTabvAttribute(getEntityProvider().getDbAttribute(this.attr.getAttributeID()));
    setUserDetails(COMMAND_MODE.INSERT, dbQuesAttr, QUES_ATTR_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbQuesAttr);
    // get the dependency list and add the newly created entity
    Set<TQuestionDepenAttribute> tQuestionDepenAttrList = dbQuestion.getTQuestionDepenAttributes();
    if (tQuestionDepenAttrList == null) {
      tQuestionDepenAttrList = new HashSet<>();
      dbQuestion.setTQuestionDepenAttributes(tQuestionDepenAttrList);
    }
    tQuestionDepenAttrList.add(dbQuesAttr);

    this.questionAttr = new QuestionDepenAttr(getDataProvider(), dbQuesAttr.getQattrDepenId());
    getChangedData().put(dbQuesAttr.getQattrDepenId(), new ChangedData(ChangeType.INSERT, dbQuesAttr.getQattrDepenId(),
        TQuestionDepenAttribute.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.questionAttr.getID(),
        TQuestionDepenAttribute.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.questionAttr.getObjectDetails());
    final TQuestionDepenAttribute dbQuestionAttr =
        getEntityProvider().getDbQuestionDepenAttr(this.questionAttr.getID());
    validateStaleData(dbQuestionAttr);
    TQuestion tques = getEntityProvider().getDbQuestion(this.question.getID());
    // remove the dependency from the question entity
    tques.getTQuestionDepenAttributes().remove(dbQuestionAttr);

    getEntityProvider().deleteEntity(dbQuestionAttr);
    setUserDetails(COMMAND_MODE.DELETE, dbQuestionAttr, QUES_ATTR_ENTITY_ID);
    getChangedData().put(this.questionAttr.getID(), chdata);

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
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Question Dependency Attribute" + getPrimaryObjectIdentifier());
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
        // add in question bo
        this.question.getDepenAttributes().add(this.questionAttr);
        break;
      case DELETE:
        // remove from cache map
        getDataCache().getQuestionDepenAttributeMap().remove(this.questionAttr.getID());
        // remove from question bo
        this.question.getDepenAttributes().remove(this.questionAttr);
        break;
      case UPDATE:
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
    return this.questionAttr == null ? null : this.questionAttr.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Question Dependency Attribute";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.attr == null ? null : this.attr.getName();
  }


  /**
   * @return the questionAttr
   */
  public QuestionDepenAttr getQuestionAttr() {
    return this.questionAttr;
  }

}
