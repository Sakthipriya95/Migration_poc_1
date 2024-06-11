/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaire;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdModPidcVersion.java - Command handles all db operations on INSERT, UPDATE on PIDVersion
 *
 * @author dmo5cob
 */
public class CmdModQuestionaireVer extends AbstractCDRCommand {


  /**
   *
   */
  private static final String INVALID_ID = " INVALID!";
  /**
   * PIDCVersion object
   */
  private QuestionnaireVersion quesVersion;

  /**
   * @return the quesVersion
   */
  public QuestionnaireVersion getQuesVersion() {
    return this.quesVersion;
  }


  /**
   * The version from which the new version is created
   */
  private QuestionnaireVersion parentVersion;


  /**
   * @return the parentVersion
   */
  public QuestionnaireVersion getParentVersion() {
    return this.parentVersion;
  }


  /**
   * @param parentVersion the parentVersion to set
   */
  public void setParentVersion(final QuestionnaireVersion parentVersion) {
    this.parentVersion = parentVersion;
  }


  /**
   * Unique entity id for setting user details
   */
  private static final String QUES_VRSN_ENTITY_ID = "QUES_VRSN_ENTITY_ID";
  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * questionare
   */
  private Questionnaire questionnaire;
  /**
   * new Desc eng
   */
  private String newDescEng;
  /**
   * new Desc Ger
   */
  private String newDescGer;
  /**
   * old desc eng
   */
  private String oldDescEng;
  /**
   * old desc ger
   */
  private String oldDescGer;

  /**
   * major version number
   */
  private Long majorVersNum;

  /**
   * oldInworkFlag oldInworkFlag
   */
  private String oldInworkFlag;
  /**
   * newInworkFlag newInworkFlag
   */
  private String newInworkFlag;
  /**
   * oldLinkHidden oldLinkHidden
   */
  private String oldLinkHidden;
  /**
   * newLinkHidden newLinkHidden
   */
  private String newLinkHidden;
  /**
   * oldLinkRelevant oldLinkRelevant
   */
  private String oldLinkRelevant;
  /**
   * newLinkRelevant newLinkRelevant
   */
  private String newLinkRelevant;
  /**
   * oldMesHidden oldMesHidden
   */
  private String oldMesHidden;
  /**
   * newMesHidden newMesHidden
   */
  private String newMesHidden;
  /**
   * oldMesRelevant oldMesRelevant
   */
  private String oldMesRelevant;
  /**
   * newMesRelevant newMesRelevant
   */
  private String newMesRelevant;
  /**
   * oldOPHidden oldOPHidden
   */
  private String oldOPHidden;
  /**
   * newOPHidden newOPHidden
   */
  private String newOPHidden;
  /**
   * oldOPRelevant oldOPRelevant
   */
  private String oldOPRelevant;
  /**
   * newOPRelevant newOPRelevant
   */
  private String newOPRelevant;
  /**
   * oldRemarkHidden oldRemarkHidden
   */
  private String oldRemarkHidden;
  /**
   * newRemarkHidden newRemarkHidden
   */
  private String newRemarkHidden;
  /**
   * oldRemarkRelevant oldRemarkRelevant
   */
  private String oldRemarkRelevant;
  /**
   * newRemarkRelevant newRemarkRelevant
   */
  private String newRemarkRelevant;
  /**
   * oldResRelevant oldResRelevant
   */
  private String oldResRelevant;
  /**
   * newResRelevant newResRelevant
   */
  private String newResRelevant;

  /**
   * oldResRelevant oldResRelevant
   */
  private String oldResHidden;

  /**
   * Child commands
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);
  /**
   * oldSeriesRelevant oldSeriesRelevant
   */
  private String oldSeriesRelevant;
  /**
   * newSeriesRelevant newSeriesRelevant
   */
  private String newSeriesRelevant;
  /**
   * oldSeriesHidden oldSeriesHidden
   */
  private String oldSeriesHidden;
  /**
   * newSeriesHidden newSeriesHidden
   */
  private String newSeriesHidden;

  // ICDM-2191
  /**
   * oldMeasuresRelevant value
   */
  private String oldMeasuresRelevant;
  /**
   * newMeasuresRelevant value
   */
  private String newMeasuresRelevant;
  /**
   * oldMeasuresHidden value
   */
  private String oldMeasuresHidden;
  /**
   * newMeasuresHidden value
   */
  private String newMeasuresHidden;
  /**
   * oldResponsibleRelevant value
   */
  private String oldResponsibleRelevant;
  /**
   * newResponsibleRelevant value
   */
  private String newResponsibleRelevant;
  /**
   * oldResponsibleHidden value
   */
  private String oldResponsibleHidden;
  /**
   * newResponsibleHidden value
   */
  private String newResponsibleHidden;
  /**
   * oldCompletionDateRelevant value
   */
  private String oldCompletionDateRelevant;
  /**
   * newCompletionDateRelevant value
   */
  private String newCompletionDateRelevant;
  /**
   * oldCompletionDateHidden value
   */
  private String oldCompletionDateHidden;
  /**
   * newCompletionDateHidden value
   */
  private String newCompletionDateHidden;


  /**
   * @return the majorVersNum
   */
  public Long getMajorVersNum() {
    return this.majorVersNum;
  }


  /**
   * @param majorVersNum the majorVersNum to set
   */
  public void setMajorVersNum(final Long majorVersNum) {
    this.majorVersNum = majorVersNum;
  }


  /**
   * minor version number
   */
  private Long minorVersNum;


  /**
   * @return the minorVersNum
   */
  public Long getMinorVersNum() {
    return this.minorVersNum;
  }


  /**
   * @param minorVersNum the minorVersNum to set
   */
  public void setMinorVersNum(final Long minorVersNum) {
    this.minorVersNum = minorVersNum;
  }


  /**
   * active flag
   */
  private String activeFlag;

  /**
   * old active flag
   */
  private String oldActiveFlag;


  /**
   * @return the activeFlag
   */
  public String getActiveFlag() {
    return this.activeFlag;
  }


  /**
   * @param activeFlag the activeFlag to set
   */
  public void setActiveFlag(final String activeFlag) {
    this.activeFlag = activeFlag;
  }


  /**
   * Constructor to create first version of PIDC. Set the parent PIDC using the setter method.
   *
   * @param dataProvider ApicDataProvider
   */
  CmdModQuestionaireVer(final CDRDataProvider dataProvider) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor to create first version of PIDC. Set the parent PIDC using the setter method.
   *
   * @param dataProvider ApicDataProvider
   * @param questionnaire Questionnaire
   */
  public CmdModQuestionaireVer(final CDRDataProvider dataProvider, final Questionnaire questionnaire) {
    super(dataProvider);
    this.questionnaire = questionnaire;
    this.parentVersion = questionnaire.getWorkingSet();
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor for update/delete actions
   *
   * @param dataProvider the Apic Data provider
   * @param questionnaire questionnaire
   * @param quesVersion quesVersion
   * @param delete whether the command is to delete the PIDC or not
   */
  public CmdModQuestionaireVer(final CDRDataProvider dataProvider, final Questionnaire questionnaire,
      final QuestionnaireVersion quesVersion, final boolean delete) {
    super(dataProvider);

    this.questionnaire = questionnaire;
    this.quesVersion = quesVersion;

    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
      // set all the fields
      setFields();
    }
  }


  /**
   * set the fields from ui.
   */
  private void setFields() {
    this.oldDescEng = this.quesVersion.getDescEng();
    this.newDescEng = this.oldDescEng;
    this.oldDescGer = this.quesVersion.getDescGer();
    this.newDescGer = this.oldDescGer;
    this.activeFlag = this.quesVersion.getActiveFlag();
    this.oldActiveFlag = this.activeFlag;

    this.newInworkFlag = this.quesVersion.getInworkFlag();
    this.oldInworkFlag = this.newInworkFlag;
    this.newLinkHidden = this.quesVersion.getLinkHiddenFlag();
    this.oldLinkHidden = this.newLinkHidden;

    this.newLinkRelevant = this.quesVersion.getLinkRelevantFlag();
    this.oldLinkRelevant = this.newLinkRelevant;

    this.newMesHidden = this.quesVersion.getMeasurementHiddenFlag();
    this.oldMesHidden = this.newMesHidden;

    this.newMesRelevant = this.quesVersion.getMeasurementRelevantFlag();
    this.oldMesRelevant = this.newMesRelevant;

    this.newOPHidden = this.quesVersion.getOpenPointsHiddenFlag();
    this.oldOPHidden = this.newOPHidden;

    this.newOPRelevant = this.quesVersion.getOpenPointsHiddenFlag();
    this.oldOPRelevant = this.newOPRelevant;

    this.newRemarkHidden = this.quesVersion.getRemarksHiddenFlag();
    this.oldRemarkHidden = this.newRemarkHidden;

    this.newRemarkRelevant = this.quesVersion.getRemarkRelevantFlag();
    this.oldRemarkRelevant = this.newRemarkRelevant;

    this.newResRelevant = this.quesVersion.getResultRelevantFlag();
    this.oldResRelevant = this.newResRelevant;

    this.newResHidden = this.quesVersion.getResultHiddenFlag();
    this.oldResHidden = this.newResHidden;

    this.newSeriesHidden = this.quesVersion.getSeriesHiddenFlag();
    this.oldSeriesHidden = this.newSeriesHidden;

    this.newSeriesRelevant = this.quesVersion.getSeriesRelevantFlag();
    this.oldSeriesRelevant = this.newSeriesRelevant;

    // ICDM-2191
    this.newMeasuresHidden = this.quesVersion.getMeasureHiddenFlag();
    this.oldMeasuresHidden = this.newMeasuresHidden;

    this.newMeasuresRelevant = this.quesVersion.getMeasureRelaventFlag();
    this.oldMeasuresRelevant = this.newMeasuresRelevant;

    this.newResponsibleHidden = this.quesVersion.getResponsibleHiddenFlag();
    this.oldResponsibleHidden = this.newResponsibleHidden;

    this.newResponsibleRelevant = this.quesVersion.getResponsibleHiddenFlag();
    this.oldResponsibleRelevant = this.newResponsibleRelevant;

    this.newCompletionDateHidden = this.quesVersion.getCompletionDateHiddenFlag();
    this.oldCompletionDateHidden = this.newCompletionDateHidden;

    this.newCompletionDateRelevant = this.quesVersion.getCompletionDateRelaventFlag();
    this.oldCompletionDateRelevant = this.newCompletionDateRelevant;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    createQuesVer();
    updateQuestionDep();
  }


  /**
   * @param cmdVersion
   */
  /**
   * @param cmdVersion
   */
  private void updateQuestionDep() throws CommandException {
    SortedSet<Question> workSetQuestionSet = this.questionnaire.getWorkingSet().getAllQuestions();
    // clear all to get all questions. load from db
    this.quesVersion.getAllQuestions().clear();
    // Iterate all Working set Questions
    for (Question workSetQuestion : workSetQuestionSet) {
      Question depTobeAdded = null;
      Question mainQuestion = null;
      // get the dep question in Working set.
      Question workSetDepQues = workSetQuestion.getDependentQuestion();
      if (workSetDepQues != null) {

        // Iterate the new version Questions
        for (Question newVersionQues : this.quesVersion.getAllQuestions()) {
          // get the dep Id to be added
          if (CommonUtils.isEqual(newVersionQues.getName(), workSetDepQues.getName())) {
            depTobeAdded = newVersionQues;
          }
          // get the question to which it has to be added
          if (CommonUtils.isEqual(newVersionQues.getName(), workSetQuestion.getName())) {
            mainQuestion = newVersionQues;
          }
        }
        // Create a command to update the Dep Id's
        if ((depTobeAdded != null) && (mainQuestion != null)) {
          CmdModQuestion command = new CmdModQuestion(getDataProvider(), mainQuestion.getParentQuestion(), mainQuestion,
              false, mainQuestion.isHeading(), false, mainQuestion.getQuestionLevel());
          command.setOnlyDepChange(true);
          command.setDepQuestion(depTobeAdded);
          command.setDepQuestionResp(mainQuestion.getDepQuesResponse());
          this.childCmdStack.addCommand(command);

        }

      }

    }

  }

  /**
   * create Version
   */
  private void createQuesVer() throws CommandException {

    TQuestionnaireVersion dbQuesVer = new TQuestionnaireVersion();
    TQuestionnaire dbQuestionnaire = getEntityProvider().getDbQuestionnaire(this.questionnaire.getID());
    if (CommonUtils.isNotNull(this.parentVersion)) {
      TQuestionnaireVersion dbParentVer = getEntityProvider().getDbQuestionnaireVersion(this.parentVersion.getID());
      initalizeFieldsUsingParent(dbQuesVer, dbParentVer);
    }
    // check for no versions. If it is Working set (set the inwork flag to Y)
    else {
      initalizeFields(dbQuesVer);
      dbQuesVer.setInworkFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    }
    // initialize the fields

    // set the questionnaire for the version
    dbQuesVer.setTQuestionnaire(dbQuestionnaire);
    // Set the parent version for child.
    // Set the questions null for now.
    setUserDetails(this.commandMode, dbQuesVer, QUES_VRSN_ENTITY_ID);

    getEntityProvider().registerNewEntity(dbQuesVer);
    this.quesVersion = new QuestionnaireVersion(getDataProvider(), dbQuesVer.getQnaireVersId());

    if (dbQuestionnaire.getTQuestionnaireVersions() == null) {
      dbQuestionnaire.setTQuestionnaireVersions(new HashSet<TQuestionnaireVersion>());
    }


    dbQuestionnaire.getTQuestionnaireVersions().add(dbQuesVer);
    Questionnaire quesBo = getDataProvider().getQuestionnaireMap().get(this.questionnaire.getID());
    quesBo.getAllVersions().add(this.quesVersion);
    if (ApicConstants.YES.equals(this.activeFlag)) {
      resetQuesVer(dbQuestionnaire.getTQuestionnaireVersions(), dbQuesVer);
      dbQuesVer.setActiveFlag(ApicConstants.YES);
      quesBo.setActiveQnaireVersion(this.quesVersion);
    }
    // Set the questions
    if (CommonUtils.isNotNull(this.parentVersion)) {
      setQuestions(this.parentVersion.getFirstLevelQuestions(), null);
    }

    getChangedData().put(this.quesVersion.getID(), new ChangedData(ChangeType.INSERT, this.quesVersion.getID(),
        TQuestionnaireVersion.class, DisplayEventSource.COMMAND));

  }

  /**
   * @param sortedSet
   * @param parentVersion2
   */
  private void setQuestions(final SortedSet<Question> quesSet, final Question parentQuestion) throws CommandException {
    for (Question question : quesSet) {
      boolean heading = question.isHeading();
      CmdModQuestion cmdQues =
          new CmdModQuestion(getDataProvider(), heading, parentQuestion, question.getQNum(), this.quesVersion);
      setQuesCmdFields(question, cmdQues);
      this.childCmdStack.addCommand(cmdQues);
      if (!question.getChildQuestions().isEmpty()) {
        setQuestions(question.getChildQuestions(), cmdQues.getQuestion());
      }
    }

  }

  /**
   * @param question
   * @param cmdQues
   */
  private void setQuesCmdFields(final Question question, final CmdModQuestion cmdQues) {
    cmdQues.setNewQuesNameEng(question.getNameEng());
    cmdQues.setNewDescEng(question.getDescEng());
    cmdQues.setNewQuesNameGer(question.getNameGer());
    cmdQues.setNewDescGer(question.getDescGer());
    cmdQues.setNewPositiveResult(question.getPositiveResult());
    cmdQues.setDeletedFlag(question.isDeleted());
    cmdQues.setDepQuestion(question.getDependentQuestion());
    cmdQues.setDepQuestionResp(question.getDepQuesResponse());
    cmdQues.getCmdQuesConfig().setResultConfig(question.getResult());
    cmdQues.getCmdQuesConfig().setOpnPointsConfig(question.getOpenPoints());
    cmdQues.getCmdQuesConfig().setMeasurementConfig(question.getMeasurement());
    cmdQues.getCmdQuesConfig().setRemarkConfig(question.getRemark());
    cmdQues.getCmdQuesConfig().setSeriesConfig(question.getSeries());
    cmdQues.getCmdQuesConfig().setLinkConfig(question.getLink());
    // ICDM-2191
    cmdQues.getCmdQuesConfig().setMeasureConfig(question.getMeasure());
    cmdQues.getCmdQuesConfig().setResponsibleConfig(question.getResponsible());
    cmdQues.getCmdQuesConfig().setCompletionDateConfig(question.getCompletionDate());

    cmdQues.setAttrSet(question.getAttributes());
    List<QuesDepnValCombination> quesValCombolist =
        getQuesValCombolist(question, question.getQuesAttrValComb().values());
    cmdQues.setQuesDepnValCombList(quesValCombolist);
  }

  /**
   * @param question
   * @param collection
   * @return the collection of Ques dep value combo list
   */
  private List<QuesDepnValCombination> getQuesValCombolist(final Question question,
      final Collection<QuesDepnValCombination> collection) {
    List<QuesDepnValCombination> quesDepList = new ArrayList<>();
    for (QuesDepnValCombination quesDepnValCombination : collection) {
      QuesDepnValCombination newquesVal = new QuesDepnValCombination(question, false);
      newquesVal.setAttrValMap(quesDepnValCombination.getAttrValMap());
      quesDepList.add(newquesVal);
    }
    return quesDepList;
  }

  /**
   * @param dbQuesVer
   * @param dbParentVer
   */
  private void initalizeFieldsUsingParent(final TQuestionnaireVersion dbQuesVer,
      final TQuestionnaireVersion dbParentVer) {
    dbQuesVer.setActiveFlag(this.activeFlag);
    dbQuesVer.setDescEng(this.newDescEng);
    dbQuesVer.setDescGer(this.newDescGer);
    dbQuesVer.setInworkFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setLinkHiddenFlag(dbParentVer.getLinkHiddenFlag());
    dbQuesVer.setLinkRelevantFlag(dbParentVer.getLinkRelevantFlag());
    dbQuesVer.setMajorVersionNum(this.majorVersNum);
    dbQuesVer.setMeasurementHiddenFlag(dbParentVer.getMeasurementHiddenFlag());
    dbQuesVer.setMeasurementRelevantFlag(dbParentVer.getMeasurementRelevantFlag());
    dbQuesVer.setMinorVersionNum(this.minorVersNum);
    dbQuesVer.setOpenPointsHiddenFlag(dbParentVer.getOpenPointsHiddenFlag());
    dbQuesVer.setOpenPointsRelevantFlag(dbParentVer.getOpenPointsRelevantFlag());
    dbQuesVer.setRemarkRelevantFlag(dbParentVer.getRemarkRelevantFlag());
    dbQuesVer.setRemarksHiddenFlag(dbParentVer.getRemarksHiddenFlag());
    dbQuesVer.setResultHiddenFlag(dbParentVer.getResultHiddenFlag());
    dbQuesVer.setResultRelevantFlag(dbParentVer.getResultRelevantFlag());
    dbQuesVer.setSeriesHiddenFlag(dbParentVer.getSeriesHiddenFlag());
    dbQuesVer.setSeriesRelevantFlag(dbParentVer.getSeriesRelevantFlag());
    // ICDM-2191
    dbQuesVer.setMeasureHiddenFlag(dbParentVer.getMeasureHiddenFlag());
    dbQuesVer.setMeasureRelaventFlag(dbParentVer.getMeasureRelaventFlag());
    dbQuesVer.setResponsibleHiddenFlag(dbParentVer.getResponsibleHiddenFlag());
    dbQuesVer.setResponsibleRelaventFlag(dbParentVer.getResponsibleRelaventFlag());
    dbQuesVer.setCompletionDateHiddenFlag(dbParentVer.getCompletionDateHiddenFlag());
    dbQuesVer.setCompletionDateRelaventFlag(dbParentVer.getCompletionDateRelaventFlag());
  }


  /**
   * @param dbQuesVer
   * @param set of Ques versions
   */
  private void resetQuesVer(final Set<TQuestionnaireVersion> quesVersion, final TQuestionnaireVersion dbQuesVer) {
    TQuestionnaireVersion selectedQuesVer = null;
    for (TQuestionnaireVersion tQuestionnaireVersion : quesVersion) {
      if (ApicConstants.YES.equals(tQuestionnaireVersion.getActiveFlag()) &&
          !(tQuestionnaireVersion.getQnaireVersId() == dbQuesVer.getQnaireVersId())) {
        selectedQuesVer = tQuestionnaireVersion;
      }
    }
    if (null != selectedQuesVer) {
      selectedQuesVer.setActiveFlag(ApicConstants.CODE_NO);
    }
  }

  /**
   * @param dbQuesVer questionnaire version
   * @throws CommandException
   */
  private void initalizeFields(final TQuestionnaireVersion dbQuesVer) throws CommandException {
    // Set the default values for all.
    dbQuesVer.setActiveFlag(this.activeFlag);
    dbQuesVer.setDescEng(this.newDescEng);
    dbQuesVer.setDescGer(this.newDescGer);
    dbQuesVer.setInworkFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setLinkHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setLinkRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setMajorVersionNum(this.majorVersNum);
    dbQuesVer.setMeasurementHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setMeasurementRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setMinorVersionNum(this.minorVersNum);
    dbQuesVer.setOpenPointsHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setOpenPointsRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setRemarkRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setRemarksHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setResultHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setResultRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setSeriesHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setSeriesRelevantFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    // ICDM-2191
    dbQuesVer.setMeasureHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setMeasureRelaventFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setResponsibleHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setResponsibleRelaventFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());
    dbQuesVer.setCompletionDateHiddenFlag(CommonUtilConstants.BOOLEAN_MODE.NO.getBinaryValue());
    dbQuesVer.setCompletionDateRelaventFlag(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue());


  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.quesVersion.getID(), TQuestionnaireVersion.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.quesVersion.getObjectDetails());

    final TQuestionnaireVersion updateQuesVer = getEntityProvider().getDbQuestionnaireVersion(this.quesVersion.getID());
    TQuestionnaire dbQuestionnaire = getEntityProvider().getDbQuestionnaire(this.questionnaire.getID());
    Questionnaire quesBo = getDataProvider().getQuestionnaireMap().get(this.questionnaire.getID());
    // if active flag yes then reset all.
    if (ApicConstants.YES.equals(this.activeFlag)) {
      resetQuesVer(dbQuestionnaire.getTQuestionnaireVersions(), updateQuesVer);
      quesBo.setActiveQnaireVersion(this.quesVersion);
    }
    // if active flag No then make active null
    else {
      quesBo.setActiveQnaireVersion(null);
    }

    updateQuesVer.setActiveFlag(this.activeFlag);

    updateQuesVer.setDescEng(this.newDescEng);
    updateQuesVer.setDescGer(this.newDescGer);
    updateQuesVer.setInworkFlag(this.newInworkFlag);
    updateQuesVer.setLinkHiddenFlag(this.newLinkHidden);
    updateQuesVer.setLinkRelevantFlag(this.newLinkRelevant);
    updateQuesVer.setMeasurementHiddenFlag(this.newMesHidden);
    updateQuesVer.setMeasurementRelevantFlag(this.newMesRelevant);
    updateQuesVer.setOpenPointsHiddenFlag(this.newOPHidden);
    updateQuesVer.setOpenPointsRelevantFlag(this.newOPRelevant);
    updateQuesVer.setRemarkRelevantFlag(this.newRemarkRelevant);
    updateQuesVer.setRemarksHiddenFlag(this.newRemarkHidden);
    updateQuesVer.setResultHiddenFlag(this.newResHidden);
    updateQuesVer.setResultRelevantFlag(this.newResRelevant);
    updateQuesVer.setSeriesHiddenFlag(this.newSeriesHidden);
    updateQuesVer.setSeriesRelevantFlag(this.newSeriesRelevant);
    // ICDM-2191
    updateQuesVer.setMeasureHiddenFlag(this.newMeasuresHidden);
    updateQuesVer.setMeasureRelaventFlag(this.newMeasuresRelevant);
    updateQuesVer.setResponsibleHiddenFlag(this.newResponsibleHidden);
    updateQuesVer.setResponsibleRelaventFlag(this.newResponsibleRelevant);
    updateQuesVer.setCompletionDateHiddenFlag(this.newCompletionDateHidden);
    updateQuesVer.setCompletionDateRelaventFlag(this.newCompletionDateRelevant);


    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, updateQuesVer, QUES_VRSN_ENTITY_ID);
    getChangedData().put(this.quesVersion.getID(), chdata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.quesVersion.getID(), TQuestionnaireVersion.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.quesVersion.getObjectDetails());
    // get the db questionnare
    final TQuestionnaire dbQuestionnaire = getEntityProvider().getDbQuestionnaire(this.questionnaire.getID());
    final TQuestionnaireVersion deletedQuesVer =
        getEntityProvider().getDbQuestionnaireVersion(this.quesVersion.getID());

    validateStaleData(deletedQuesVer);
    // remove ques from version
    dbQuestionnaire.getTQuestionnaireVersions().remove(deletedQuesVer);
    // delete the entity.
    getEntityProvider().deleteEntity(deletedQuesVer);
    this.questionnaire.getAllVersions().remove(this.quesVersion);

    getChangedData().put(this.quesVersion.getID(), chdata);
    setUserDetails(COMMAND_MODE.UPDATE, deletedQuesVer, QUES_VRSN_ENTITY_ID);
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
    return isStringChanged(this.newDescEng, this.oldDescEng) || isStringChanged(this.newDescGer, this.oldDescGer) ||
        isStringChanged(this.activeFlag, this.oldActiveFlag) ||
        isStringChanged(this.newInworkFlag, this.oldInworkFlag) ||
        isStringChanged(this.newLinkHidden, this.oldLinkHidden) ||
        isStringChanged(this.newMesHidden, this.oldMesHidden) ||
        isStringChanged(this.newMesRelevant, this.oldMesRelevant) ||
        isStringChanged(this.newOPHidden, this.oldOPHidden) ||
        isStringChanged(this.newOPRelevant, this.oldOPRelevant) ||
        isStringChanged(this.newRemarkHidden, this.oldRemarkHidden) ||
        isStringChanged(this.newRemarkRelevant, this.oldRemarkRelevant) ||
        isStringChanged(this.newResHidden, this.oldResHidden) ||
        isStringChanged(this.newResRelevant, this.oldResRelevant) ||
        isStringChanged(this.newSeriesHidden, this.oldSeriesHidden) ||
        isStringChanged(this.newSeriesRelevant, this.oldSeriesRelevant) ||
        isStringChanged(this.newMeasuresHidden, this.oldMeasuresHidden) ||
        isStringChanged(this.newMeasuresRelevant, this.oldMeasuresRelevant) ||
        isStringChanged(this.newResponsibleHidden, this.oldResponsibleHidden) ||
        isStringChanged(this.newResponsibleRelevant, this.oldResponsibleRelevant) ||
        isStringChanged(this.newCompletionDateHidden, this.oldCompletionDateHidden) ||
        isStringChanged(this.newCompletionDateRelevant, this.oldCompletionDateRelevant);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    String commandModeText = getPrimaryObjectType();

    return super.getString(commandModeText, getPrimaryObjectIdentifier());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {


    SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        addTransForUpdate(detailsList);
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
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }


  /**
   * @param detailsList
   */
  private void addTransForUpdate(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Description (German)");
    addTransactionSummaryDetails(detailsList, this.oldActiveFlag, this.activeFlag, "Active Flag");
    addTransactionSummaryDetails(detailsList, this.oldInworkFlag, this.newInworkFlag, "InWork Flag");
    addTransactionSummaryDetails(detailsList, this.oldLinkHidden, this.newLinkHidden, "Link Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldLinkRelevant, this.newLinkRelevant, "Link Relevant Flag");
    addTransactionSummaryDetails(detailsList, this.oldMesHidden, this.newMesHidden, "Measuremnet Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldMesRelevant, this.newMesRelevant, "Measuremnet Relevant Flag");
    addTransactionSummaryDetails(detailsList, this.oldOPHidden, this.newOPHidden, "Open Point Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldOPRelevant, this.newOPRelevant, "Open Point Relevant Flag");
    addTransactionSummaryDetails(detailsList, this.oldRemarkHidden, this.newRemarkHidden, "Remark Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldRemarkRelevant, this.newRemarkRelevant, "Remark Relevant Flag");
    addTransactionSummaryDetails(detailsList, this.oldResHidden, this.newResHidden, "Result Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldResRelevant, this.newResRelevant, "Result Relevant Flag");
    addTransactionSummaryDetails(detailsList, this.oldSeriesHidden, this.newSeriesHidden, "Series Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldSeriesRelevant, this.newSeriesRelevant, "Series Relevant Flag");
    addTransactionSummaryDetails(detailsList, this.oldResponsibleRelevant, this.newResponsibleRelevant,
        "Responsible Relavent Flag");
    addTransactionSummaryDetails(detailsList, this.oldResponsibleHidden, this.newResponsibleHidden,
        "Responsible Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldMeasuresRelevant, this.newMeasuresRelevant,
        "Measures Relavent Flag");
    addTransactionSummaryDetails(detailsList, this.oldMeasuresHidden, this.newMeasuresHidden, "Measures Hidden Flag");
    addTransactionSummaryDetails(detailsList, this.oldCompletionDateRelevant, this.newCompletionDateRelevant,
        "Completion Date Relavent Flag");
    addTransactionSummaryDetails(detailsList, this.oldCompletionDateHidden, this.newCompletionDateHidden,
        "Completion Date Hidden Flag");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {

    switch (this.commandMode) {
      case INSERT:
        SortedSet<Question> allQuestions = new TreeSet<>(this.quesVersion.getAllQuestions());
        this.quesVersion.getAllQuestions().clear();
        this.quesVersion.getAllQuestions().addAll(allQuestions);
        break;
      case UPDATE:
      case DELETE:
      default:
        break;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.quesVersion == null ? null : this.quesVersion.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Questionnaire Version";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    Long dbpidcVrsID;
    switch (this.commandMode) {
      case INSERT:
        dbpidcVrsID = this.quesVersion.getID();
        final TQuestionnaireVersion dbVrsn = getEntityProvider().getDbQuestionnaireVersion(dbpidcVrsID);
        if (dbVrsn == null) {
          return INVALID_ID;
        }
        return dbVrsn.getDescEng();
      case UPDATE:
      case DELETE:
        dbpidcVrsID = this.quesVersion.getID();
        final TQuestionnaireVersion dbUpdateVrsn = getEntityProvider().getDbQuestionnaireVersion(dbpidcVrsID);
        if (dbUpdateVrsn == null) {
          return INVALID_ID;
        }
        return dbUpdateVrsn.getDescEng();
      default:
        // Do nothing
        break;
    }
    return INVALID_ID;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        this.questionnaire.getAllVersions().remove(this.quesVersion);
        break;
      case DELETE:
        this.questionnaire.getAllVersions().add(this.quesVersion);
        break;
      case UPDATE:
      default:
        // Do nothing
        break;
    }


  }


  /**
   * @param questionnaire questionnaire
   */
  public void setQuestionnaire(final Questionnaire questionnaire) {
    this.questionnaire = questionnaire;

  }

  /**
   * @return the newDescEng
   */
  public String getNewDescEng() {
    return this.newDescEng;
  }


  /**
   * @param newDescEng the newDescEng to set
   */
  public void setNewDescEng(final String newDescEng) {
    this.newDescEng = newDescEng;
  }


  /**
   * @return the newDescGer
   */
  public String getNewDescGer() {
    return this.newDescGer;
  }


  /**
   * @param newDescGer the newDescGer to set
   */
  public void setNewDescGer(final String newDescGer) {
    this.newDescGer = newDescGer;
  }


  /**
   * @return the oldDescGer
   */
  public String getOldDescGer() {
    return this.oldDescGer;
  }


  /**
   * @param oldDescGer the oldDescGer to set
   */
  public void setOldDescGer(final String oldDescGer) {
    this.oldDescGer = oldDescGer;
  }


  /**
   * @return the newInworkFlag
   */
  public String getNewInworkFlag() {
    return this.newInworkFlag;
  }


  /**
   * @param newInworkFlag the newInworkFlag to set
   */
  public void setNewInworkFlag(final String newInworkFlag) {
    this.newInworkFlag = newInworkFlag;
  }


  /**
   * @return the newLinkHidden
   */
  public String getNewLinkHidden() {
    return this.newLinkHidden;
  }


  /**
   * @param newLinkHidden the newLinkHidden to set
   */
  public void setNewLinkHidden(final String newLinkHidden) {
    this.newLinkHidden = newLinkHidden;
  }


  /**
   * @return the newLinkRelevant
   */
  public String getNewLinkRelevant() {
    return this.newLinkRelevant;
  }


  /**
   * @param newLinkRelevant the newLinkRelevant to set
   */
  public void setNewLinkRelevant(final String newLinkRelevant) {
    this.newLinkRelevant = newLinkRelevant;
  }


  /**
   * @return the newMesHidden
   */
  public String getNewMesHidden() {
    return this.newMesHidden;
  }


  /**
   * @param newMesHidden the newMesHidden to set
   */
  public void setNewMesHidden(final String newMesHidden) {
    this.newMesHidden = newMesHidden;
  }

  /**
   * @return the newMesRelevant
   */
  public String getNewMesRelevant() {
    return this.newMesRelevant;
  }


  /**
   * @param newMesRelevant the newMesRelevant to set
   */
  public void setNewMesRelevant(final String newMesRelevant) {
    this.newMesRelevant = newMesRelevant;
  }


  /**
   * @return the newOPHidden
   */
  public String getNewOPHidden() {
    return this.newOPHidden;
  }


  /**
   * @param newOPHidden the newOPHidden to set
   */
  public void setNewOPHidden(final String newOPHidden) {
    this.newOPHidden = newOPHidden;
  }

  /**
   * @param newOPHidden the newOPHidden to set
   */
  public void setNewOPRelevant(final String newOPRelevant) {
    this.newOPRelevant = newOPRelevant;
  }


  /**
   * @return the newOPRelevant
   */
  public String getNewOPRelevant() {
    return this.newOPRelevant;
  }


  /**
   * @return the newRemarkHidden
   */
  public String getNewRemarkHidden() {
    return this.newRemarkHidden;
  }


  /**
   * @param newRemarkHidden the newRemarkHidden to set
   */
  public void setNewRemarkHidden(final String newRemarkHidden) {
    this.newRemarkHidden = newRemarkHidden;
  }


  /**
   * @return the newRemarkRelevant
   */
  public String getNewRemarkRelevant() {
    return this.newRemarkRelevant;
  }


  /**
   * @param newRemarkRelevant the newRemarkRelevant to set
   */
  public void setNewRemarkRelevant(final String newRemarkRelevant) {
    this.newRemarkRelevant = newRemarkRelevant;
  }


  /**
   * @return the newResRelevant
   */
  public String getNewResRelevant() {
    return this.newResRelevant;
  }


  /**
   * @param newResRelevant the newResRelevant to set
   */
  public void setNewResRelevant(final String newResRelevant) {
    this.newResRelevant = newResRelevant;
  }


  /**
   * @return the newSeriesRelevant
   */
  public String getNewSeriesRelevant() {
    return this.newSeriesRelevant;
  }


  /**
   * @param newSeriesRelevant the newSeriesRelevant to set
   */
  public void setNewSeriesRelevant(final String newSeriesRelevant) {
    this.newSeriesRelevant = newSeriesRelevant;
  }


  /**
   * @return the newSeriesHidden
   */
  public String getNewSeriesHidden() {
    return this.newSeriesHidden;
  }


  /**
   * @param newSeriesHidden the newSeriesHidden to set
   */
  public void setNewSeriesHidden(final String newSeriesHidden) {
    this.newSeriesHidden = newSeriesHidden;
  }


  /**
   * @return the oldResHidden
   */
  public String getOldResHidden() {
    return this.oldResHidden;
  }


  /**
   * @param oldResHidden the oldResHidden to set
   */
  public void setOldResHidden(final String oldResHidden) {
    this.oldResHidden = oldResHidden;
  }


  /**
   * newResRelevant newResRelevant
   */
  private String newResHidden;

  /**
   * @return the newResHidden
   */
  public String getNewResHidden() {
    return this.newResHidden;
  }


  /**
   * @return the oldMeasuresRelevant
   */
  public String getOldMeasuresRelevant() {
    return this.oldMeasuresRelevant;
  }


  /**
   * @param oldMeasuresRelevant the oldMeasuresRelevant to set
   */
  public void setOldMeasuresRelevant(final String oldMeasuresRelevant) {
    this.oldMeasuresRelevant = oldMeasuresRelevant;
  }


  /**
   * @return the newMeasuresRelevant
   */
  public String getNewMeasuresRelevant() {
    return this.newMeasuresRelevant;
  }


  /**
   * @param newMeasuresRelevant the newMeasuresRelevant to set
   */
  public void setNewMeasuresRelevant(final String newMeasuresRelevant) {
    this.newMeasuresRelevant = newMeasuresRelevant;
  }


  /**
   * @return the oldMeasuresHidden
   */
  public String getOldMeasuresHidden() {
    return this.oldMeasuresHidden;
  }


  /**
   * @param oldMeasuresHidden the oldMeasuresHidden to set
   */
  public void setOldMeasuresHidden(final String oldMeasuresHidden) {
    this.oldMeasuresHidden = oldMeasuresHidden;
  }


  /**
   * @return the newMeasuresHidden
   */
  public String getNewMeasuresHidden() {
    return this.newMeasuresHidden;
  }


  /**
   * @param newMeasuresHidden the newMeasuresHidden to set
   */
  public void setNewMeasuresHidden(final String newMeasuresHidden) {
    this.newMeasuresHidden = newMeasuresHidden;
  }


  /**
   * @return the oldResponsibleRelevant
   */
  public String getOldResponsibleRelevant() {
    return this.oldResponsibleRelevant;
  }


  /**
   * @param oldResponsibleRelevant the oldResponsibleRelevant to set
   */
  public void setOldResponsibleRelevant(final String oldResponsibleRelevant) {
    this.oldResponsibleRelevant = oldResponsibleRelevant;
  }


  /**
   * @return the newResponsibleRelevant
   */
  public String getNewResponsibleRelevant() {
    return this.newResponsibleRelevant;
  }


  /**
   * @param newResponsibleRelevant the newResponsibleRelevant to set
   */
  public void setNewResponsibleRelevant(final String newResponsibleRelevant) {
    this.newResponsibleRelevant = newResponsibleRelevant;
  }


  /**
   * @return the oldResponsibleHidden
   */
  public String getOldResponsibleHidden() {
    return this.oldResponsibleHidden;
  }


  /**
   * @param oldResponsibleHidden the oldResponsibleHidden to set
   */
  public void setOldResponsibleHidden(final String oldResponsibleHidden) {
    this.oldResponsibleHidden = oldResponsibleHidden;
  }


  /**
   * @return the newResponsibleHidden
   */
  public String getNewResponsibleHidden() {
    return this.newResponsibleHidden;
  }


  /**
   * @param newResponsibleHidden the newResponsibleHidden to set
   */
  public void setNewResponsibleHidden(final String newResponsibleHidden) {
    this.newResponsibleHidden = newResponsibleHidden;
  }


  /**
   * @return the oldCompletionDateRelevant
   */
  public String getOldCompletionDateRelevant() {
    return this.oldCompletionDateRelevant;
  }


  /**
   * @param oldCompletionDateRelevant the oldCompletionDateRelevant to set
   */
  public void setOldCompletionDateRelevant(final String oldCompletionDateRelevant) {
    this.oldCompletionDateRelevant = oldCompletionDateRelevant;
  }


  /**
   * @return the newCompletionDateRelevant
   */
  public String getNewCompletionDateRelevant() {
    return this.newCompletionDateRelevant;
  }


  /**
   * @param newCompletionDateRelevant the newCompletionDateRelevant to set
   */
  public void setNewCompletionDateRelevant(final String newCompletionDateRelevant) {
    this.newCompletionDateRelevant = newCompletionDateRelevant;
  }


  /**
   * @return the oldCompletionDateHidden
   */
  public String getOldCompletionDateHidden() {
    return this.oldCompletionDateHidden;
  }


  /**
   * @param oldCompletionDateHidden the oldCompletionDateHidden to set
   */
  public void setOldCompletionDateHidden(final String oldCompletionDateHidden) {
    this.oldCompletionDateHidden = oldCompletionDateHidden;
  }


  /**
   * @return the newCompletionDateHidden
   */
  public String getNewCompletionDateHidden() {
    return this.newCompletionDateHidden;
  }


  /**
   * @param newCompletionDateHidden the newCompletionDateHidden to set
   */
  public void setNewCompletionDateHidden(final String newCompletionDateHidden) {
    this.newCompletionDateHidden = newCompletionDateHidden;
  }


  /**
   * @param newResHidden the newResHidden to set
   */
  public void setNewResHidden(final String newResHidden) {
    this.newResHidden = newResHidden;
  }
}
