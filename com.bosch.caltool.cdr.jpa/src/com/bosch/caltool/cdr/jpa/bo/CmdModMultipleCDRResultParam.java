/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_FLAG;


/**
 * ICDM-920 Parent command to modify multiple review parameters
 *
 * @author mkl2cob
 */
public class CmdModMultipleCDRResultParam extends AbstractCDRCommand {


  /**
   * Parameter list to be updated
   */
  private final List<CDRResultParameter> paramList;

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * tells whether it is flag or comment update
   */
  private final boolean isRvwScoreUpdate;

  /**
   * first parameter of the list
   */
  private final CDRResultParameter resParam;

  /**
   * Review result of the set of result parameters
   */
  private final CDRResult reviewResult;

  /**
   * New Review flag to be set
   */
  private String newReviewedScore;

  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * New review comments to be set
   */
  private String newReviewComment;

  /**
   * new sec rvw result state
   */
  // Task 236308
  private RESULT_FLAG newSecResultStateFlag;

  /**
   * ICDM-920
   *
   * @param cdrDataProvider CDRDataProvider
   * @param paramList List of Cdr parameters which are to be updated
   * @param isFlagUpdate true when review flag is updated
   */
  public CmdModMultipleCDRResultParam(final CDRDataProvider cdrDataProvider, final List<CDRResultParameter> paramList,
      final boolean isFlagUpdate) {

    super(cdrDataProvider);
    this.paramList = paramList;
    this.resParam = this.paramList.get(0);
    this.reviewResult = this.resParam.getCDRResult();
    this.commandMode = COMMAND_MODE.UPDATE;
    this.isRvwScoreUpdate = isFlagUpdate;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    final Iterator<CDRResultParameter> iterator = this.paramList.iterator();
    CDRResultParameter resParameter;

    while (iterator.hasNext()) {

      resParameter = iterator.next();

      final CmdModCDRResultParam cmd = new CmdModCDRResultParam(getDataProvider(), resParameter, false, true);
      if (this.isRvwScoreUpdate) {
        cmd.setNewReviewScore(this.newReviewedScore);
      }
      else {
        cmd.setReviewComment(this.newReviewComment);
      }
      cmd.setNewSecResultStateFlag(this.newSecResultStateFlag);

      this.childCmdStack.addCommand(cmd);

    }
    final TRvwResult dbRvwRes = getEntityProvider().getDbCDRResult(this.reviewResult.getID());

    // iCDM-1424
    if (this.isRvwScoreUpdate) {
      resultStatusUpdate(dbRvwRes);
    }

  }

  /**
   * This method updates the result status if needed
   *
   * @param dbRvwParam
   * @param dbRvwRes
   * @throws CommandException
   */
  private void resultStatusUpdate(final TRvwResult dbRvwRes) throws CommandException {

    // Set new changes
    if (this.newReviewedScore.equalsIgnoreCase(DATA_REVIEW_SCORE.S_8.getDbType()) ||
        this.newReviewedScore.equalsIgnoreCase(DATA_REVIEW_SCORE.S_9.getDbType())) {
      // iCDM-665
      if (this.reviewResult.isAllParamsReviewed()) {
        CmdModCDRResult cmdResult = new CmdModCDRResult(getDataProvider(), this.reviewResult,
            CDRConstants.REVIEW_STATUS.CLOSED, false, this.reviewResult.getSrResult());

        this.childCmdStack.addCommand(cmdResult);
        if (cmdResult.getErrorCause() == ERROR_CAUSE.NONE) {
          CDMLogger.getInstance().info("Review Status is modified successfully");
        }
      }
    }
    else {
      // iCDM-665
      if (!dbRvwRes.getRvwStatus().equals(CDRConstants.REVIEW_STATUS.IN_PROGRESS.getDbType())) {
        CmdModCDRResult cmdResult = new CmdModCDRResult(getDataProvider(), this.reviewResult,
            CDRConstants.REVIEW_STATUS.IN_PROGRESS, false, this.reviewResult.getSrResult());
        this.childCmdStack.addCommand(cmdResult);
        if (cmdResult.getErrorCause() == ERROR_CAUSE.NONE) {
          CDMLogger.getInstance().info("Review Status is modified successfully");
        }
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not applicable


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
    this.childCmdStack.undoAll();

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
    return true;
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
  protected void rollBackDataModel() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.reviewResult == null ? null : this.reviewResult.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Multiple CDR Result Parameters";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.reviewResult.getName();
  }

  /**
   * @param reviewScore new review flag
   */
  public void setReviewScore(final String reviewScore) {
    this.newReviewedScore = reviewScore;

  }

  /**
   * @param comments new review comments
   */
  public void setReviewComment(final String comments) {
    this.newReviewComment = comments;

  }


  /**
   * @param newSecResultStateFlag the newSecResultStateFlag to set
   */
  public void setNewSecResultStateFlag(final RESULT_FLAG newSecResultStateFlag) {
    this.newSecResultStateFlag = newSecResultStateFlag;
  }

}
