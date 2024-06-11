/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.AbstractProjectObject;
import com.bosch.caltool.apic.jpa.bo.CmdModLinks;
import com.bosch.caltool.apic.jpa.bo.Link;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;


/**
 * Command to create/delte questionnaire response
 *
 * @author bne4cob
 */
// ICDM-2404
public class CmdModQuestionnaireResponse extends AbstractCDRCommand {

  /**
   * Quesitonnaire version for which response is to be created
   */
  private final QuestionnaireVersion qnaireVers;

  /**
   * Project object to which qnaire version is to be attached
   */
  private final AbstractProjectObject<?> projObj;

  /**
   * Response object created/to be deleted
   */
  private QuestionnaireResponse qnaireResponse;

  /**
   * Child commands execution
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor to create a response object
   *
   * @param dataProvider data provider
   * @param qnaireVers qnaire version
   * @param projObj project object
   */
  protected CmdModQuestionnaireResponse(final CDRDataProvider dataProvider, final QuestionnaireVersion qnaireVers,
      final AbstractProjectObject<?> projObj) {
    super(dataProvider);
    this.qnaireVers = qnaireVers;
    this.projObj = projObj;
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor for DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param rvwQues Review Questionnaire
   */
  public CmdModQuestionnaireResponse(final CDRDataProvider dataProvider, final QuestionnaireResponse qnaireResponse,
      final AbstractProjectObject<?> projObj) {
    super(dataProvider);
    this.qnaireResponse = qnaireResponse;
    this.qnaireVers = qnaireResponse.getQNaireVersion();
    this.projObj = projObj;
    this.commandMode = COMMAND_MODE.DELETE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    this.childCmdStack.rollbackAll(getExecutionMode());

    if (isGeneralQnaireVers()) {
      getDataCache().setRemoveProjGeneralQnaireVers(this.projObj, null);
    }
    else {
      getDataCache().addRemoveProjQnaireResponse(this.projObj, this.qnaireResponse, false);

    }

    if (this.qnaireResponse != null) {
      getDataCache().addRemoveQnaireResponse(this.qnaireResponse, false);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    TRvwQnaireResponse dbQnaireResp = new TRvwQnaireResponse();
//    if (isVariantLevel()) {
//      dbQnaireResp.setTabvProjectVariant(getEntityProvider().getDbPidcVariant(this.projObj.getID()));
//      dbQnaireResp
//          .setTPidcVersion(getEntityProvider().getDbPIDCVersion(((PIDCVariant) this.projObj).getPidcVersion().getID()));
//    }
//    else {
//      dbQnaireResp.setTPidcVersion(getEntityProvider().getDbPIDCVersion(this.projObj.getID()));
//    }
//    dbQnaireResp.setTQuestionnaireVersion(getEntityProvider().getDbQuestionnaireVersion(this.qnaireVers.getID()));
    setUserDetails(COMMAND_MODE.INSERT, dbQnaireResp, "ENTITY_ID");

    getEntityProvider().registerNewEntity(dbQnaireResp);
    getEntityProvider().getEm().flush();
    this.qnaireResponse = new QuestionnaireResponse(getDataProvider(), dbQnaireResp.getQnaireRespId());
    if (isGeneralQnaireVers()) {
      getDataCache().setRemoveProjGeneralQnaireVers(this.projObj, this.qnaireVers);
    }
    else {
      getDataCache().addRemoveProjQnaireResponse(this.projObj, this.qnaireResponse, true);
      mapGenQnaireVersion();
    }
    getChangedData().put(dbQnaireResp.getQnaireRespId(), new ChangedData(ChangeType.INSERT,
        dbQnaireResp.getQnaireRespId(), TRvwQnaireResponse.class, DisplayEventSource.COMMAND));

  }

  /**
   * @throws CommandException
   */
  private void mapGenQnaireVersion() throws CommandException {
    if (getDataCache().getProjGenQnaireVers(this.projObj) == null) {
      QuestionnaireVersion genQnaireVers = getDataCache().getGeneralQnaireActiveVersion();
      if (genQnaireVers != null) {
        CmdModQuestionnaireResponse genRespCreationCmd =
            new CmdModQuestionnaireResponse(getDataProvider(), genQnaireVers, this.projObj);
        this.childCmdStack.addCommand(genRespCreationCmd);
      }
    }
  }

  /**
   * @return true if a general qnaire
   */
  private boolean isGeneralQnaireVers() {
    return this.qnaireVers.isGeneralType();
  }

  /**
   * @return true, if this response being created is mapped to a pidc variant
   */
  private boolean isVariantLevel() {
    return this.projObj instanceof PIDCVariant;
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
    // load all the questionnaire responses is not done already
    this.qnaireResponse.getAllQuestionResponses(true);
    Collection<ReviewQnaireAnswer> qsAnsSet = this.qnaireResponse.getDefinedQAMap().values();
    // iterate through the answers to be deleted
    if ((qsAnsSet != null) && !qsAnsSet.isEmpty()) {
      for (ReviewQnaireAnswer qsAns : qsAnsSet) {
        // delete the links
        deleteQsAnsLink(qsAns);
        // delete the open points
        deleteQsAnsOP(qsAns);
        // delete the answer
        deleteQsAns(qsAns);
      }
    }
    // delete the questionnaire response
    deleteQsResponse();

  }

  /**
   * @param qsAns
   * @throws CommandException
   */
  private void deleteQsAns(final ReviewQnaireAnswer qsAns) throws CommandException {
    CmdModRvwQAnswer cmdRvwQAns = new CmdModRvwQAnswer(getDataProvider(), qsAns, true);
    this.childCmdStack.addCommand(cmdRvwQAns);
    checkCommandStatus(cmdRvwQAns);

  }

  /**
   * @param qsAns
   * @throws CommandException
   */
  private void deleteQsAnsOP(final ReviewQnaireAnswer qsAns) throws CommandException {
    List<QnaireAnsOpenPoint> openPointsList = new ArrayList<QnaireAnsOpenPoint>();
    openPointsList.addAll(qsAns.getOpenPointsList());
    if (!openPointsList.isEmpty()) {
      for (QnaireAnsOpenPoint selOP : openPointsList) {
        CmdModQnaireAnsOpenPoints openPointQs = new CmdModQnaireAnsOpenPoints(getDataProvider(), qsAns, selOP, true);
        this.childCmdStack.addCommand(openPointQs);
        checkCommandStatus(openPointQs);
      }
    }
  }

  /**
   * @param qsAns
   * @throws CommandException
   */
  private void deleteQsAnsLink(final ReviewQnaireAnswer qsAns) throws CommandException {

    Set<Link> linkSet = qsAns.getLinks();
    if ((linkSet != null) && !linkSet.isEmpty()) {
      for (Link selLink : linkSet) {
        CmdModLinks command =
            new CmdModLinks(getDataProvider().getApicDataProvider(), selLink, true, this.qnaireResponse);
        this.childCmdStack.addCommand(command);
        checkCommandStatus(command);
      }
    }


  }

  /**
   * @throws CommandException
   */
  private void deleteQsResponse() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.DELETE, this.qnaireResponse.getID(), TRvwQnaireResponse.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.qnaireResponse.getObjectDetails());
    final TRvwQnaireResponse dbRvwQues = getEntityProvider().getDbQnaireResponse(this.qnaireResponse.getID());
//    if (null != this.qnaireResponse.getPidcVariant()) {
//      TabvProjectVariant dbVar = getEntityProvider().getDbPidcVariant(this.qnaireResponse.getPidcVariant().getID());
//      dbVar.getTRvwQnaireResponses().remove(dbRvwQues);
//    }
//    TPidcVersion dbPidcVer = getEntityProvider().getDbPIDCVersion(this.qnaireResponse.getPidcVersion().getID());
//    dbPidcVer.getTRvwQnaireResponses().remove(dbRvwQues);
//
//    TQuestionnaireVersion dbQuestionnaireVersion =
//        getEntityProvider().getDbQuestionnaireVersion(this.qnaireResponse.getQNaireVersion().getID());
//    dbQuestionnaireVersion.getTRvwQnaireResponses().remove(dbRvwQues);
    Long respID = this.qnaireResponse.getID();


    validateStaleData(dbRvwQues);
    getEntityProvider().deleteEntity(dbRvwQues);
    setUserDetails(COMMAND_MODE.DELETE, dbRvwQues, "ENTITY_ID");

    // remove from cache
    getDataCache().getProjQnaireResponseMap(this.projObj).remove(this.qnaireVers.getQuestionnaire().getID());

    getDataCache().getQnaireResponseMap().remove(respID);


    getChangedData().put(this.qnaireResponse.getID(), chdata);
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
    return super.getString("", "Questionnaire Response" + getPrimaryObjectIdentifier());
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
    // No action

  }

  /**
   * @param cdrCommand
   * @throws CommandException
   */
  private void checkCommandStatus(final AbstractDataCommand cdrCommand) throws CommandException {
    if (cdrCommand.getErrorCause() != ERROR_CAUSE.NONE) {
      throw new CommandException(cdrCommand.getErrorMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.qnaireResponse == null ? null : this.qnaireResponse.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Questionnaire Response";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.qnaireVers == null ? null : this.qnaireVers.getName();
  }

}
