/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.cdr.jpa.bo.CDRSecondaryResult.RULE_SOURCE;
import com.bosch.caltool.cdr.jpa.bo.review.ReviewRuleSetData;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;

/**
 * @author bne4cob
 */
public class CmdModCDRSeconadryResult extends AbstractCDRCommand {

  /**
   * Review result BO created/being updated
   */
  private CDRResult reviewResult;


  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  private RuleSet ruleSet;

  private CDRSecondaryResult secondaryResult;


  /**
   * ssd release id
   */
  private Long ssdRelID;


  /**
   * ssd file review
   */
  private boolean ssdFileReview;


  private long ssdVersionID;


  private ReviewRuleSetData ruleSetData;


  private static final String SEC_RES_ENTITY_ID = "SEC_RES_ENTITY_ID";


  /**
   * @param dataProvider data provider
   * @param cdrResult cdrResult
   * @param ruleSet ruleSet
   */
  public CmdModCDRSeconadryResult(final CDRDataProvider dataProvider, final CDRResult cdrResult,
      final RuleSet ruleSet) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.reviewResult = cdrResult;
    this.ruleSet = ruleSet;
  }


  // Icdm-877
  /**
   * Consrtuctor for Delete Result Delete the Review Result
   *
   * @param dataProvider CDRDataProvider
   * @param secondaryResult secondaryResult
   */
  public CmdModCDRSeconadryResult(final CDRDataProvider dataProvider, final CDRSecondaryResult secondaryResult) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.DELETE;
    this.secondaryResult = secondaryResult;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      // remove the secondary result
      this.reviewResult.getSecondaryResults().remove(this.secondaryResult);


    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    createSecondaryResult();
  }


  /**
   * create the secondary Result.
   */
  private void createSecondaryResult() {
    // create new objact
    final TRvwResultsSecondary dbSecondaryResult = new TRvwResultsSecondary();

    dbSecondaryResult.setSource(RULE_SOURCE.COMMON_RULES.getDbVal());
    // set the Rule set.
    if (CommonUtils.isNotNull(this.ruleSet)) {
      dbSecondaryResult.setTRuleSet(getEntityProvider().getDbRuleSet(this.ruleSet.getID()));
      dbSecondaryResult.setSource(RULE_SOURCE.RULE_SET.getDbVal());
    }
    dbSecondaryResult.setSsdVersID(this.ssdVersionID);
    if (CommonUtils.isNotNull(this.ssdRelID) && (this.ssdRelID != 0l) && !this.ssdFileReview) {
      dbSecondaryResult.setSource(RULE_SOURCE.SSD_RELEASE.getDbVal());
      dbSecondaryResult.setSsdReleaseID(this.ssdRelID);

    }
    else if (this.ssdFileReview) {
      dbSecondaryResult.setSource(RULE_SOURCE.SSD_FILE.getDbVal());
    }


    // set the result
    final TRvwResult dbResult = getEntityProvider().getDbCDRResult(this.reviewResult.getID());

    dbSecondaryResult.setTRvwResult(dbResult);

    // set the user details
    setUserDetails(COMMAND_MODE.INSERT, dbSecondaryResult, SEC_RES_ENTITY_ID);

    getEntityProvider().registerNewEntity(dbSecondaryResult);

    // create the new object
    this.secondaryResult = new CDRSecondaryResult(getDataProvider(), dbSecondaryResult.getSecReviewId());

    // set the secondary result to the actual result.

    // get the secondary result set of the result.
    Set<TRvwResultsSecondary> secondaryResultSet =
        getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwResultsSecondaries();
    if (secondaryResultSet == null) {
      secondaryResultSet = new HashSet<>();
    }
    // add it to the set.
    secondaryResultSet.add(dbSecondaryResult);

    // set the secondary to the db result.
    dbResult.setTRvwResultsSecondaries(secondaryResultSet);

    // attach it to the data model.
    SortedSet<CDRSecondaryResult> reviewResultSet = this.reviewResult.getSecondaryResults();
    if (reviewResultSet == null) {
      reviewResultSet = new TreeSet<>();
    }
    reviewResultSet.add(this.secondaryResult);
    getDataCache().getRuleDataMap().put(this.ruleSetData, this.secondaryResult);
    // add it to the change data
    getChangedData().put(this.secondaryResult.getID(), new ChangedData(ChangeType.INSERT, this.secondaryResult.getID(),
        TRvwResultsSecondary.class, DisplayEventSource.COMMAND));

  }


  /**
   * {@inheritDoc} Icdm -877 do the delete Command
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.secondaryResult.getID(),
        TRvwResultsSecondary.class, DisplayEventSource.COMMAND);
    Map<String, String> oldObjectDetails =
        getDataCache().getCDRResSecondary(this.secondaryResult.getID()).getObjectDetails();
    chdata.setOldDataDetails(oldObjectDetails);
    deleteSecondaryResult();
    getChangedData().put(this.secondaryResult.getID(), chdata);
  }


  /**
   * Delete the Secondary Result finally
   */
  private void deleteSecondaryResult() {
    final TRvwResultsSecondary dbSecondaryResult = getEntityProvider().getDbResSecondary(this.secondaryResult.getID());
    // Get the resut and delete the relationship
    if (dbSecondaryResult.getTRvwResult() != null) {
      dbSecondaryResult.getTRvwResult().getTRvwResultsSecondaries().remove(dbSecondaryResult);
    }

    // Remove the data model object from cdr result.

    getDataCache().getAllCDRSecResults().remove(this.secondaryResult.getID());
    // Delete the entity.
    getEntityProvider().deleteEntity(dbSecondaryResult);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    deleteSecondaryResult();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    createSecondaryResult();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    if (this.commandMode == COMMAND_MODE.INSERT) {
      createInsertTxnSummary(detailsList);
    }

    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }


  /**
   * create Insert Transaction Summary
   *
   * @param detailsList parent level summary
   */
  private void createInsertTxnSummary(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // Not application for insert only command
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return true;
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "Review Result :" + getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Icdm-741 check removed for the parent result null
    // No need to Refresh the tree only for Review Type update
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.secondaryResult == null ? null : this.secondaryResult.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "CDR Secondary Result";
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
  public String getPrimaryObjectIdentifier() {
    return "Secondary Result";
  }


  /**
   * @param ssdRelID ssdRelID
   */
  public void setSsdRelID(final Long ssdRelID) {
    this.ssdRelID = ssdRelID;

  }


  /**
   * @param ssdFileReview ssdFileReview
   */
  public void setSSdFileReview(final boolean ssdFileReview) {
    this.ssdFileReview = ssdFileReview;
  }


  /**
   * @param ssdVersionID ssdVersionID
   */
  public void setSSDVersionID(final long ssdVersionID) {
    this.ssdVersionID = ssdVersionID;

  }


  /**
   * @param ruleSetData ruleSetData
   */
  public void setRuleSetData(final ReviewRuleSetData ruleSetData) {
    this.ruleSetData = ruleSetData;

  }

}
