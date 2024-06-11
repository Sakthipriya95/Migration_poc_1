/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;


import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.CmdModNodeAccessRight;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;


/**
 * Command for inserting,deleting and updating the alias detail
 *
 * @author rgo7cob
 */
public class CmdModQuestionaire extends AbstractCDRCommand {


  /**
   * alias detail object. One reference fir all.
   */
  private Questionnaire questionnaire;


  /**
   * @return the questionnaire
   */
  public Questionnaire getQuestionnaire() {
    return this.questionnaire;
  }


  /**
   * Stack for storing child commands executed after creating the PIDC entity
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Old name
   */
  private String oldNameEng;


  /**
   * Old name german
   */
  private String oldNameGer;


  /**
   * Old name german
   */
  private String oldDescEng;

  /**
   * Old name german
   */
  private String newDescEng;


  /**
   * Old name german
   */
  private String oldDescGer;


  /**
   * Old name german
   */
  private String newDescGer;


  /**
   * new name german
   */
  private String newNameGer;

  /**
   * Selected workPackage
   */
  private IcdmWorkPackage workPackage;

  /**
   * @param workPackage the workPackage to set
   */
  public void setWorkPackage(final IcdmWorkPackage workPackage) {
    this.workPackage = workPackage;
  }

  /**
   * @param attrValue the attrValue to set
   */
  public void setAttrValue(final AttributeValue attrValue) {
    this.attrValue = attrValue;
  }


  /**
   * attribute value for the alias
   */
  private AttributeValue attrValue;

  /**
   * @param newNameGer the newNameGer to set
   */
  public void setNewNameGer(final String newNameGer) {
    this.newNameGer = newNameGer;
  }


  /**
   * new name eng
   */
  private String newNameEng;

  /**
   * @param newNameEng the newNameEng to set
   */
  public void setNewNameEng(final String newNameEng) {
    this.newNameEng = newNameEng;
  }

  /**
   * @param newDescEng the newDescEng to set
   */
  public void setNewDescEng(final String newDescEng) {
    this.newDescEng = newDescEng;
  }

  /**
   * @param newDescGer the newDescGer to set
   */
  public void setNewDescGer(final String newDescGer) {
    this.newDescGer = newDescGer;
  }


  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  private static final String QUES_ENTITY_ID = "QUES_ENTITY_ID";

  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * vesrion command
   */
  private CmdModQuestionaireVer quesVrsnCmd;

  /**
   * apic data provider
   */
  private CDRDataProvider cdrDataProvider;


  /**
   * @return the quesVrsnCmd
   */
  public CmdModQuestionaireVer getQuesVrsnCmd() {
    return this.quesVrsnCmd;
  }


  /**
   * @param quesVrsnCmd the quesVrsnCmd to set
   */
  public void setQuesVrsnCmd(final CmdModQuestionaireVer quesVrsnCmd) {
    this.quesVrsnCmd = quesVrsnCmd;
  }


  /**
   * Constructor to add a new Attribute - use this constructor for INSERT
   *
   * @param apicDataProvider data provider
   */
  public CmdModQuestionaire(final CDRDataProvider apicDataProvider) {
    super(apicDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.quesVrsnCmd = new CmdModQuestionaireVer(getDataProvider());
    this.cdrDataProvider = apicDataProvider;
  }

  /**
   * Constructor to delete or update existing Attribute
   *
   * @param apicDataProvider data provider
   * @param questionnaire questionnaire
   * @param isDelete whether to delete or not
   */
  public CmdModQuestionaire(final CDRDataProvider apicDataProvider, final Questionnaire questionnaire,
      final boolean isDelete) {
    super(apicDataProvider);
    this.questionnaire = questionnaire;
    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
      this.quesVrsnCmd = new CmdModQuestionaireVer(getDataProvider(), questionnaire, null, true);
      // the Attribute to be deleted, rember for undo
      // get the DB entity for the Attribute to be modified
      // ICDM-229 changes
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      // the Attribute to be modified
      // get the DB entity for the Attribute to be modified
      // ICDM-229 changes

    }
    // initialize command with current values from UI
    setDetailsToCmd(questionnaire);
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifyAliasDet
   */
  /**
   * @param questionnaire
   */
  private void setDetailsToCmd(final Questionnaire questionnaire) {

    this.oldNameEng = questionnaire.getNameEng();
    this.newNameEng = this.oldNameEng;
    this.oldNameGer = questionnaire.getNameGer();
    this.newNameGer = this.oldNameGer;
    this.oldDescEng = questionnaire.getDescEng();
    this.newDescEng = this.oldDescEng;
    this.oldDescGer = questionnaire.getDescGer();
    this.newDescGer = this.oldDescGer;
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException CommandException
   */
  @SuppressWarnings("deprecation")
  @Override
  protected void executeInsertCommand() throws CommandException {

    final TQuestionnaire dbQuestionnaire = new TQuestionnaire();

    dbQuestionnaire.setDescEng(this.newDescEng);
    dbQuestionnaire.setDescGer(this.newDescGer);
    dbQuestionnaire.setDeletedFlag("N");
    // use child command to create workpackage division if not available
    TWorkpackageDivision dbWPDiv;

    dbWPDiv = getWorkPkgDiv();

    dbQuestionnaire.setTWorkpackageDivision(dbWPDiv);
    setUserDetails(COMMAND_MODE.INSERT, dbQuestionnaire, QUES_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbQuestionnaire);
    this.questionnaire = new Questionnaire(getDataProvider(), dbQuestionnaire.getQnaireId());
    getChangedData().put(dbQuestionnaire.getQnaireId(), new ChangedData(ChangeType.INSERT,
        dbQuestionnaire.getQnaireId(), TQuestionnaire.class, DisplayEventSource.COMMAND));
    this.cdrDataProvider.getQuestionnaireMap().put(dbQuestionnaire.getQnaireId(), this.questionnaire);
    createQuesVersionCmd();
    createAccessRights();
  }

  /**
   * @return
   */
  private TWorkpackageDivision getWorkPkgDiv() throws CommandException {
    TWorkpackageDivision dbWPDiv = null;

    Collection<WorkPackageDivision> wpDivs = getDataCache().getWrkPkgDivisionMap().values();
    for (WorkPackageDivision wpDiv : wpDivs) {
      if ((wpDiv.getDivision() == this.attrValue) && (wpDiv.getWorkPackage() == this.workPackage)) {
        dbWPDiv = getEntityProvider().getDbWrkPkgDiv(wpDiv.getID());
      }
    }
    if (null == dbWPDiv) {
      final CmdModWrkPkgDivision cmdWrkPkgDiv =
          new CmdModWrkPkgDivision(getDataProvider(), this.attrValue, this.workPackage);
      // Add the command
      this.childCmdStack.addCommand(cmdWrkPkgDiv);
      dbWPDiv = getEntityProvider().getDbWrkPkgDiv(cmdWrkPkgDiv.getPrimaryObjectID());
    }
    return dbWPDiv;
  }


  /**
   * create version for Questionnaire
   */
  private void createQuesVersionCmd() throws CommandException {
    this.quesVrsnCmd.setQuestionnaire(this.questionnaire);
    this.childCmdStack.addCommand(this.quesVrsnCmd);
  }

  /**
   * Create a node access entity with the current user as the owner and full privileges. Command for node access
   * creation is used
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createAccessRights() throws CommandException {

    final CmdModNodeAccessRight cmdNodeAccess = new CmdModNodeAccessRight(getDataProvider().getApicDataProvider(),
        this.questionnaire, getDataProvider().getApicDataProvider().getCurrentUser());
    cmdNodeAccess.setGrantOption(true);
    cmdNodeAccess.setIsOwner(true);
    cmdNodeAccess.setWriteAccess(true);
    this.childCmdStack.addCommand(cmdNodeAccess);
  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.questionnaire.getID(), TQuestionnaire.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.questionnaire.getObjectDetails());
    final TQuestionnaire dbQuestionnaire = getEntityProvider().getDbQuestionnaire(this.questionnaire.getID());
    validateStaleData(dbQuestionnaire);

    dbQuestionnaire.setDescEng(this.newDescEng);
    dbQuestionnaire.setDescGer(this.newDescGer);
    if (null != this.quesVrsnCmd) {
      createQuesVersionCmd();
    }
    // Icdm-461 Changes
    getChangedData().put(this.questionnaire.getID(), chdata);
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
        new ChangedData(ChangeType.UPDATE, this.questionnaire.getID(), TAliasDetail.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.questionnaire.getObjectDetails());
    final TQuestionnaire dbQuestionnaire = getEntityProvider().getDbQuestionnaire(this.questionnaire.getID());
    validateStaleData(dbQuestionnaire);
    dbQuestionnaire.setDeletedFlag("Y");
    setUserDetails(COMMAND_MODE.DELETE, dbQuestionnaire, QUES_ENTITY_ID);
    this.quesVrsnCmd.setQuestionnaire(this.questionnaire);

    // Icdm-461 Changes
    getChangedData().put(this.questionnaire.getID(), chdata);
    this.childCmdStack.addCommand(this.quesVrsnCmd);
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
    return isStringChanged(this.oldNameEng, this.newNameEng) || isStringChanged(this.oldNameGer, this.newNameGer) ||
        isStringChanged(this.oldDescEng, this.newDescEng) || isStringChanged(this.oldDescGer, this.newDescGer);

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
        if (null != this.questionnaire.getID()) {
          this.cdrDataProvider.getQuestionnaireMap().remove(this.questionnaire.getID());
        }
        break;
      case DELETE:
        this.cdrDataProvider.getAllQuestionares(false).add(this.questionnaire);
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
    return this.questionnaire == null ? null : this.questionnaire.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Questionnaire";
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
        addTransactionSummaryDetails(detailsList, this.oldNameEng, this.newNameEng, "Alias Name");
        break;
      case DELETE: // no details section necessary in case of delete (parent row is sufficient in transansions view)
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
  public String getPrimaryObjectIdentifier() {
    String identifier = null;
    if (CommonUtils.isNotNull(this.questionnaire)) {
      identifier = this.questionnaire.getName();
    }
    else if (CommonUtils.isNotNull(this.workPackage)) {
      identifier = this.attrValue == null ? this.workPackage.getName()
          : WorkPackageDivision.buildName(this.workPackage.getName(), this.attrValue.getName());
    }
    return identifier;
  }


}
