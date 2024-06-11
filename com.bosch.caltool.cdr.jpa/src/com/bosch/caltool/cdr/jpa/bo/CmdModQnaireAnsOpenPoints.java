/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.CommandErrorCodes;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswer;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireAnswerOpl;


/**
 * @author bru2cob
 */
public class CmdModQnaireAnsOpenPoints extends AbstractCDRCommand {

  private static final String QUES_ANS_OPEN_POINTS_ENTITY_ID = "QUES_ANS_OPEN_POINTS_ENTITY_ID";
  /**
   * new open point
   */
  private String newOpenPoint;
  /**
   * old open point
   */
  private String oldOpenPoint;
  /**
   * new measure
   */
  private String newMeasure;
  /**
   * old measure
   */
  private String oldMeasure;
  /**
   * new responsible
   */
  private ApicUser newResponsible;
  /**
   * old responsible
   */
  private ApicUser oldResponsible;
  /**
   * new completion date
   */
  private String oldCompletionDate;
  /**
   * old completion date
   */
  private String newCompletionDate;
  /**
   * new result
   */
  private String newResult;
  /**
   * old result
   */
  private String oldResult;
  /**
   * ReviewQnaireAnswer
   */
  private final ReviewQnaireAnswer qAnswer;
  /**
   * qnaire open point obj
   */
  private QnaireAnsOpenPoint qnaireAnsOpenPoint;
  /**
   * TransactionSummary instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * command for INSERT
   *
   * @param dataProvider CDRDataProvider
   * @param rvwQnaireAns ReviewQnaireAnswer
   */
  public CmdModQnaireAnsOpenPoints(final CDRDataProvider dataProvider, final ReviewQnaireAnswer rvwQnaireAns) {
    super(dataProvider);
    this.qAnswer = rvwQnaireAns;
    this.qnaireAnsOpenPoint = null;
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * command for UPDATE/DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param rvwQnaireAns ReviewQnaireAnswer
   * @param ansOpenPointObj obj to be updated
   * @param isDelete isdelte
   */
  public CmdModQnaireAnsOpenPoints(final CDRDataProvider dataProvider, final ReviewQnaireAnswer rvwQnaireAns,
      final QnaireAnsOpenPoint ansOpenPointObj, final boolean isDelete) {
    super(dataProvider);
    this.qAnswer = rvwQnaireAns;
    this.qnaireAnsOpenPoint = ansOpenPointObj;
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;

    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    setOldValues();
  }

  /**
   * Set the old values
   */
  private void setOldValues() {
    this.oldOpenPoint = this.qnaireAnsOpenPoint.getOpenPoints();
    this.oldCompletionDate = this.qnaireAnsOpenPoint.getCompletionDateAsString();
    this.oldMeasure = this.qnaireAnsOpenPoint.getMeasure();
    this.oldResponsible = this.qnaireAnsOpenPoint.getResponsible();
    this.oldResult = this.qnaireAnsOpenPoint.getResult();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        if (null != this.qnaireAnsOpenPoint) {
          getDataCache().getQnaireAnsOpenPointMap().remove(this.qnaireAnsOpenPoint.getID());
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
    final TRvwQnaireAnswerOpl dbQnAnsOpenPoint = new TRvwQnaireAnswerOpl();
    setNewFeildValues(dbQnAnsOpenPoint);

    TRvwQnaireAnswer dbQuesAns = getEntityProvider().getDbReviewQnaireAnswer(this.qAnswer.getID());
    dbQnAnsOpenPoint.setTRvwQnaireAnswer(dbQuesAns);

    getEntityProvider().registerNewEntity(dbQnAnsOpenPoint);

    setUserDetails(COMMAND_MODE.INSERT, dbQnAnsOpenPoint, QUES_ANS_OPEN_POINTS_ENTITY_ID);

    this.qnaireAnsOpenPoint =
        new QnaireAnsOpenPoint(getDataProvider(), this.qAnswer, dbQnAnsOpenPoint.getOpenPointsId());

    ReviewQnaireAnswer qsAns = getDataCache().getReviewQnaireAnsMap().get(this.qAnswer.getID());
    qsAns.getOpenPointsList().add(this.qnaireAnsOpenPoint);

    Set<TRvwQnaireAnswerOpl> tRvwAnsOpenPoints = dbQuesAns.getTQnaireAnsOpenPoints();
    if (tRvwAnsOpenPoints == null) {
      tRvwAnsOpenPoints = new HashSet<TRvwQnaireAnswerOpl>();
    }
    tRvwAnsOpenPoints.add(dbQnAnsOpenPoint);

    dbQuesAns.setTQnaireAnsOpenPoints(tRvwAnsOpenPoints);

    getDataCache().getQnaireAnsOpenPointMap().put(this.qnaireAnsOpenPoint.getID(), this.qnaireAnsOpenPoint);

    getChangedData().put(dbQnAnsOpenPoint.getOpenPointsId(), new ChangedData(ChangeType.INSERT,
        dbQnAnsOpenPoint.getOpenPointsId(), TRvwQnaireAnswerOpl.class, DisplayEventSource.COMMAND));
  }

  /**
   * Set the new values
   *
   * @param dbQnAnsOpenPoint
   * @throws CommandException
   */
  private void setNewFeildValues(final TRvwQnaireAnswerOpl dbQnAnsOpenPoint) throws CommandException {
    try {
      if ((null != this.newCompletionDate) && !"".equalsIgnoreCase(this.newCompletionDate)) {
        dbQnAnsOpenPoint
            .setCompletionDate(DateFormat.convertStringToTimestamp(DateFormat.DATE_FORMAT_09, this.newCompletionDate));
      }
    }
    catch (ParseException e) {
      throw new CommandException(CommandErrorCodes.PRSING_INVLD_DATE, e);
    }
    dbQnAnsOpenPoint.setMeasure(this.newMeasure);
    dbQnAnsOpenPoint.setResult(this.newResult);
    if (null != this.newResponsible) {
      dbQnAnsOpenPoint.setTabvApicUser(getEntityProvider().getDbApicUser(this.newResponsible.getID()));
    }
    dbQnAnsOpenPoint.setOpenPoints(this.newOpenPoint);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.qnaireAnsOpenPoint.getID(),
        TRvwQnaireAnswerOpl.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.qnaireAnsOpenPoint.getObjectDetails());
    final TRvwQnaireAnswerOpl dbQuesAnsOpenPoint =
        getEntityProvider().getDbQnaireAnsOpenPoint(this.qnaireAnsOpenPoint.getID());
    validateStaleData(dbQuesAnsOpenPoint);
    // update the fields
    setNewFeildValues(dbQuesAnsOpenPoint);
    setUserDetails(COMMAND_MODE.UPDATE, dbQuesAnsOpenPoint, QUES_ANS_OPEN_POINTS_ENTITY_ID);
    getChangedData().put(this.qnaireAnsOpenPoint.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.qnaireAnsOpenPoint.getID(),
        TRvwQnaireAnswerOpl.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.qnaireAnsOpenPoint.getObjectDetails());
    final TRvwQnaireAnswerOpl dbQuesAnsOpenPoint =
        getEntityProvider().getDbQnaireAnsOpenPoint(this.qnaireAnsOpenPoint.getID());

    TRvwQnaireAnswer dbQuesAns = getEntityProvider().getDbReviewQnaireAnswer(this.qAnswer.getID());
    dbQuesAns.getTQnaireAnsOpenPoints().remove(dbQuesAnsOpenPoint);


    getEntityProvider().deleteEntity(dbQuesAnsOpenPoint);
    // put changed data to changed data map
    getChangedData().put(this.qnaireAnsOpenPoint.getID(), chdata);


    ReviewQnaireAnswer qsAns = getDataCache().getReviewQnaireAnsMap().get(this.qAnswer.getID());
    qsAns.getOpenPointsList().remove(this.qnaireAnsOpenPoint);

    getDataCache().getQnaireAnsOpenPointMap().remove(this.qnaireAnsOpenPoint.getID());
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
    return isStringChanged(this.oldCompletionDate, this.newCompletionDate) ||
        isStringChanged(this.oldMeasure, this.newMeasure) || isStringChanged(this.oldResult, this.newResult) ||
        !CommonUtils.isEqual(this.oldResponsible, this.newResponsible) ||

        isStringChanged(this.oldOpenPoint, this.newOpenPoint);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Review Question Answer Open Points" + getPrimaryObjectIdentifier());
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
    addTransactionSummaryDetails(detailsList, this.oldCompletionDate, this.newCompletionDate, "Completion Date");
    addTransactionSummaryDetails(detailsList, this.oldMeasure, this.newMeasure, "Measure");
    addTransactionSummaryDetails(detailsList, this.oldResult, this.newResult, "Result");
    addTransactionSummaryDetails(detailsList, this.oldResponsible == null ? null : this.oldResponsible.getUserName(),
        this.newResponsible == null ? null : this.newResponsible.getUserName(), "Responsible");
    addTransactionSummaryDetails(detailsList, this.oldOpenPoint, this.newOpenPoint, "Open Points");
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
    return this.qnaireAnsOpenPoint == null ? null : this.qnaireAnsOpenPoint.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Review Question Answer Open Points";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.qnaireAnsOpenPoint == null ? null : this.qnaireAnsOpenPoint.getName();
  }


  /**
   * @param newOpenPoint the newOpenPoint to set
   */
  public void setNewOpenPoint(final String newOpenPoint) {
    this.newOpenPoint = newOpenPoint;
  }


  /**
   * @param newMeasure the newMeasure to set
   */
  public void setNewMeasure(final String newMeasure) {
    this.newMeasure = newMeasure;
  }


  /**
   * @param newResponsible the newResponsible to set
   */
  public void setNewResponsible(final ApicUser newResponsible) {
    this.newResponsible = newResponsible;
  }


  /**
   * @param newCompletionDate the newCompletionDate to set
   */
  public void setNewCompletionDate(final String newCompletionDate) {
    this.newCompletionDate = newCompletionDate;
  }


  /**
   * @param newResult the newResult to set
   */
  public void setNewResult(final String newResult) {
    this.newResult = newResult;
  }


}
