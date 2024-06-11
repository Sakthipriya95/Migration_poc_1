/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.CmdModMultipleLinks;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;


/**
 * ICDM-1980 command changes for questionnaire response
 *
 * @author mkl2cob
 */
public class CmdModRvwQAnswer extends AbstractCDRCommand {

  private static final String RVW_QUES_ANS_ENTITY_ID = "RVW_QUES_ANS_ENTITY_ID";
  /**
   * Transaction summary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * ReviewQnaireAnswer
   */
  private final ReviewQnaireAnswer qAnswer;
  /**
   * new remarks
   */
  private String newRemarks;
  /**
   * new result
   */
  private String newResult;
  /**
   * new Series
   */
  private String newSeries;
  /**
   * new Measurement
   */
  private String newMeasurement;

  /**
   * new remarks
   */
  private String oldRemarks;
  /**
   * new result
   */
  private String oldResult;
  /**
   * new Series
   */
  private String oldSeries;
  /**
   * new Measurement
   */
  private String oldMeasurement;


  /**
   * command for editing multiple links
   */
  protected CmdModMultipleLinks cmdMultipleLinks;

  /**
   * command for editing multiple open points
   */
  protected CmdModMulQnaireAnsOpenPoints cmdMultipleOP;

  /**
   * Stack for storing child commands executed after creating the quesition response
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * command for INSERT/UPDATE
   *
   * @param dataProvider CDRDataProvider
   * @param rvwQnaireAns ReviewQnaireAnswer
   * @param isDelete true if delete
   */
  public CmdModRvwQAnswer(final CDRDataProvider dataProvider, final ReviewQnaireAnswer rvwQnaireAns,
      final boolean isDelete) {
    super(dataProvider);
    this.qAnswer = rvwQnaireAns;
    if (rvwQnaireAns.getID() == null) {
      // if the id is null
      this.commandMode = COMMAND_MODE.INSERT;
    }
    else if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
      setOldValues();
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
      setOldValues();
    }
  }

  /**
   * set existing values
   */
  private void setOldValues() {
    this.oldMeasurement = this.qAnswer.getMeasurement();
    this.oldRemarks = this.qAnswer.getRemark();
    this.oldResult = this.qAnswer.getResult();
    this.oldSeries = this.qAnswer.getSeries();

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        getDataCache().getReviewQnaireAnsMap().remove(this.qAnswer.getID());
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
    final TRvwQnaireAnswer dbRvwQuesAns = new TRvwQnaireAnswer();
    dbRvwQuesAns.setTQuestion(getEntityProvider().getDbQuestion(this.qAnswer.getQuestion().getID()));
    // ICDM-2404
//    TRvwQnaireResponse dbResponse = getEntityProvider().getDbQnaireResponse(this.qAnswer.getQuesResponse().getID());
//    dbRvwQuesAns.setTRvwQnaireResponse(dbResponse);
//    // add the review question answer to version's list
//    dbResponse.addTRvwQnaireAnswer(dbRvwQuesAns);
    getEntityProvider().registerNewEntity(dbRvwQuesAns);
    // create BO
    this.qAnswer.setID(dbRvwQuesAns.getRvwAnswerId());
    setResponseFields(dbRvwQuesAns);
    setUserDetails(COMMAND_MODE.INSERT, dbRvwQuesAns, RVW_QUES_ANS_ENTITY_ID);

    getChangedData().put(dbRvwQuesAns.getRvwAnswerId(), new ChangedData(ChangeType.INSERT,
        dbRvwQuesAns.getRvwAnswerId(), TRvwQnaireAnswer.class, DisplayEventSource.COMMAND));
  }

  /**
   * set the response fields
   *
   * @param dbRvwQuesAns TRvwQnaireAnswer
   * @throws CommandException
   */
  private void setResponseFields(final TRvwQnaireAnswer dbRvwQuesAns) throws CommandException {
    dbRvwQuesAns.setRemark(this.newRemarks);
    dbRvwQuesAns.setResult(this.newResult);
    dbRvwQuesAns.setSeries(this.newSeries);
    dbRvwQuesAns.setMeasurement(this.newMeasurement);
    // execute link command
    if (null != this.cmdMultipleLinks) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }
    if (null != this.cmdMultipleOP) {
      this.childCmdStack.addCommand(this.cmdMultipleOP);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.qAnswer.getID(), TRvwQnaireAnswer.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.qAnswer.getObjectDetails());
    final TRvwQnaireAnswer dbQuesAns = getEntityProvider().getDbReviewQnaireAnswer(this.qAnswer.getID());
    validateStaleData(dbQuesAns);
    // update the fields
    setResponseFields(dbQuesAns);
    setUserDetails(COMMAND_MODE.UPDATE, dbQuesAns, RVW_QUES_ANS_ENTITY_ID);
    getChangedData().put(this.qAnswer.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.qAnswer.getID(), TRvwQnaireAnswer.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.qAnswer.getObjectDetails());
    final TRvwQnaireAnswer dbRvwQuesAns = getEntityProvider().getDbReviewQnaireAnswer(this.qAnswer.getID());

    validateStaleData(dbRvwQuesAns);
    getEntityProvider().deleteEntity(dbRvwQuesAns);
    setUserDetails(COMMAND_MODE.DELETE, dbRvwQuesAns, RVW_QUES_ANS_ENTITY_ID);
    getChangedData().put(this.qAnswer.getID(), chdata);
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
    return isStringChanged(this.oldMeasurement, this.newMeasurement) ||
        isStringChanged(this.oldRemarks, this.newRemarks) || isStringChanged(this.oldResult, this.newResult) ||
        isStringChanged(this.oldSeries, this.newSeries) || CommonUtils.isNotNull(this.cmdMultipleLinks) ||
        CommonUtils.isNotNull(this.cmdMultipleOP);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Review Question Answer" + getPrimaryObjectIdentifier());
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
        caseCmdUpd(detailsList);
        break;
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
  private void caseCmdUpd(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldResult, this.newResult, "Result");
    addTransactionSummaryDetails(detailsList, this.oldMeasurement, this.newMeasurement, "Measurement");
    addTransactionSummaryDetails(detailsList, this.oldSeries, this.newSeries, "Series");
    addTransactionSummaryDetails(detailsList, this.oldRemarks, this.newRemarks, "Remarks");
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
        // ICDM-2404
        this.qAnswer.getQuesResponse().setRefreshChildren();
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
  public Long getPrimaryObjectID() {
    return this.qAnswer == null ? null : this.qAnswer.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Review Question Answer";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.qAnswer == null ? null : this.qAnswer.getName();
  }

  /**
   * @param cmdMultipleLinks the cmdMultipleLinks to set
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;
  }

  /**
   * @param newRemarks the newRemarks to set
   */
  public void setNewRemarks(final String newRemarks) {
    this.newRemarks = newRemarks;
  }


  /**
   * @param newResult the newResult to set
   */
  public void setNewResult(final String newResult) {
    this.newResult = newResult;
  }


  /**
   * @param newSeries the newSeries to set
   */
  public void setNewSeries(final String newSeries) {
    this.newSeries = newSeries;
  }


  /**
   * @param newMeasurement the newMeasurement to set
   */
  public void setNewMeasurement(final String newMeasurement) {
    this.newMeasurement = newMeasurement;
  }


  /**
   * @param cmdMultipleOP the cmdMultipleOP to set
   */
  public void setCmdMultipleOP(final CmdModMulQnaireAnsOpenPoints cmdMultipleOP) {
    this.cmdMultipleOP = cmdMultipleOP;
  }

}
