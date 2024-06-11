/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TQuestion;
import com.bosch.caltool.icdm.database.entity.apic.TQuestionnaireVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * ICDM-1954 Command to create, edit , delete question/headings
 *
 * @author mkl2cob
 */
public class CmdModQuestion extends AbstractCDRCommand {

  /**
   * Constant for one
   */
  private static final int ONE_CONST = 1;
  /**
   * constant for level 3
   */
  private static final int LEVEL_THREE_CONST = 3;
  /**
   * constant for level 2
   */
  private static final int LEVEL_TWO_CONST = 2;
  private static final String QUES_ENTITY_ID = "QUESTION_ENTITY_ID";
  /**
   * Question instance
   */
  private Question question;

  /**
   * CmdModQuesConfig
   */
  private final CmdModQuesConfig cmdQuesConfig;

  /**
   * key- attr, value- set of attr value
   */
  private List<QuesDepnValCombination> quesDepnValCombList;

  /**
   * key- attr, value- set of attr value
   */
  private List<QuesDepnValCombination> deleteQuesDepnValList;

  /**
   * key- attr, value- set of attr value
   */
  private List<QuesDepnValCombination> editQuesDepnValList;

  /**
   * set of attributes
   */
  private Set<Attribute> attrSet;

  /**
   * set of attributes
   */
  private Set<QuestionDepenAttr> deleteAttrSet;

  /**
   * boolean to indicate if it is heading or a question
   */
  private final boolean isHeading;

  /**
   * question has positive result or not
   */
  private String oldPositiveResult;
  /**
   * question has positive result or not
   */
  private String newPositiveResult;

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
  private String oldDescGer;

  /**
   * question name in English
   */
  private String newQuesNameEng;
  /**
   * question name in Germany
   */
  private String newQuesNameGer;

  /**
   * question descritpion in English
   */
  private String newDescEng;

  /**
   * question description in Germany
   */
  private String newDescGer;

  /**
   * Old heading flag
   */
  private boolean oldHeadingFlag;
  /**
   * New Heading flag
   */
  private boolean newHeadingFlag;

  /**
   * dependent Question for a Question.
   */
  private Question depQuestion;

  /**
   * dep Question response for a Question.
   */
  private String depQuestionResp;


  /**
   * Stack for storing child commands
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * parent question
   */
  private final Question parent;
  /**
   * question number
   */
  private Long qNumber;
  private final QuestionnaireVersion qVersion;
  private boolean isLevelChange;
  private int newLevel;
  private boolean onlyDepChange;
  private boolean deletedFlag;

  /**
   * Constructor for UPDATE/DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param question Question
   * @param delete true if the question has to be deleted
   * @param isHeading true if it is a heading
   */
  public CmdModQuestion(final CDRDataProvider dataProvider, final Question parent, final Question question,
      final boolean delete, final boolean isHeading, final boolean isLevelChange, final int newLevel) {
    super(dataProvider);
    if (!isHeading && (question.getQuesConfig() == null)) {
      // INSERT
      this.cmdQuesConfig = new CmdModQuesConfig(dataProvider);
    }
    else {
      // UPDATE
      this.cmdQuesConfig = new CmdModQuesConfig(dataProvider, question.getQuesConfig());
    }
    this.question = question;
    this.parent = parent;
    this.isHeading = isHeading;
    this.qVersion = question.getQuestionnaireVersion();
    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.isLevelChange = isLevelChange;
    this.newLevel = newLevel;
    setOldValues();
  }

  /**
   * Constructor for INSERT
   *
   * @param dataProvider CDRDataProvider
   * @param isHeading true if it is heading
   * @param parent parent question or heading
   * @param qNumber question number
   * @param qVersion QuestionnaireVersion
   */
  public CmdModQuestion(final CDRDataProvider dataProvider, final boolean isHeading, final Question parent,
      final Long qNumber, final QuestionnaireVersion qVersion) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.cmdQuesConfig = new CmdModQuesConfig(dataProvider);
    this.isHeading = isHeading;
    this.parent = parent;
    this.qNumber = qNumber;
    this.qVersion = qVersion;

  }

  /**
   * set old values
   */
  private void setOldValues() {
    this.oldNameEng = this.question.getNameEng();
    this.oldNameGer = this.question.getNameGer();
    this.oldDescEng = this.question.getDescEng();
    this.oldDescGer = this.question.getDescGer();
    this.oldHeadingFlag = this.question.isHeading();
    this.oldPositiveResult = this.question.getPositiveResult();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        this.childCmdStack.rollbackAll(getExecutionMode());
        getDataCache().getQuestionMap().remove(this.question.getID());
        getDataCache().getQuestionConfigMap().remove(this.question.getQuesConfig().getID());
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
    final TQuestion dbQues = new TQuestion();
    dbQues.setHeadingFlag(this.isHeading ? ApicConstants.YES : ApicConstants.CODE_NO);
    dbQues.setPositiveResult(this.newPositiveResult);
    setNameDesc(dbQues);
    TQuestionnaireVersion dbQVers = getEntityProvider().getDbQuestionnaireVersion(this.qVersion.getID());
    dbQues.setTQuestionnaireVersion(dbQVers);

    dbQues.setDeletedFlag(ApicConstants.CODE_NO);
    if (this.deletedFlag) {
      dbQues.setDeletedFlag(ApicConstants.YES);
    }
    reorderQNumber(dbQues);
    setUserDetails(COMMAND_MODE.INSERT, dbQues, QUES_ENTITY_ID);
    getEntityProvider().registerNewEntity(dbQues);
    // add the question to the questionnaire version
    Set<TQuestion> tQuestions = dbQVers.getTQuestions();
    if (tQuestions == null) {
      tQuestions = new HashSet<>();
      dbQVers.setTQuestions(tQuestions);
    }
    tQuestions.add(dbQues);
    if (null != this.parent) {
      // if parent is not null
      TQuestion parentDbQuestion = getEntityProvider().getDbQuestion(this.parent.getID());
      dbQues.setTQuestion(parentDbQuestion);
      // add this question as a child to its parent
      Set<TQuestion> childQuestions = parentDbQuestion.getTQuestions();
      if (childQuestions == null) {
        childQuestions = new HashSet<>();
        parentDbQuestion.setTQuestions(childQuestions);
      }
      parentDbQuestion.getTQuestions().add(dbQues);
    }
    if (!this.isHeading) {// if it is not a heading, store the configuration
      createQuestionConfig(dbQues);
    }
    this.question = new Question(getDataProvider(), dbQues.getQId());

    if (!this.isHeading) {// if it is not a heading, store the attr dependency
      createAttrDepnAndVals();
    }
    setDepQuestionAndResp(dbQues);
    getChangedData().put(dbQues.getQId(),
        new ChangedData(ChangeType.INSERT, dbQues.getQId(), TQuestion.class, DisplayEventSource.COMMAND));

  }

  /**
   * Set the dependent Question and Response
   *
   * @param dbQues dbQuestion
   */
  private void setDepQuestionAndResp(final TQuestion dbQues) {
    if (null != this.depQuestion) {
      dbQues.setDepQuestion(getEntityProvider().getDbQuestion(this.depQuestion.getID()));
      dbQues.setDepQuesResponse(this.depQuestionResp);
    }
    else {
      dbQues.setDepQuestion(null);
      dbQues.setDepQuesResponse(null);
    }
  }

  /**
   * ICDM-2054 create attr dependencies and values
   *
   * @throws CommandException from child command
   */
  private void createAttrDepnAndVals() throws CommandException {
    if (null != this.attrSet) {
      // create QuestionDepnAttribute
      for (Attribute attr : this.attrSet) {
        CmdModQuesDepnAttr cmdDepnAttr = new CmdModQuesDepnAttr(getDataProvider(), this.question, attr);
        this.childCmdStack.addCommand(cmdDepnAttr);
        this.question.getAttrDepnMap().put(attr, cmdDepnAttr.getQuestionAttr());
      }
    }

    if (null != this.quesDepnValCombList) {
      long combinationNum = this.question.getMaxCombNumber() + 1;
      for (QuesDepnValCombination quesDepnValComb : this.quesDepnValCombList) {
        CmdModQuesAttrValComb cmdQuesDepnValComb =
            new CmdModQuesAttrValComb(getDataProvider(), this.question, quesDepnValComb);
        cmdQuesDepnValComb.setCombinationNum(combinationNum);
        combinationNum++;
        this.childCmdStack.addCommand(cmdQuesDepnValComb);
      }
    }
  }

  /**
   * @param ques TQuestion
   */
  private void setNameDesc(final TQuestion ques) {
    ques.setQNameEng(this.newQuesNameEng);
    ques.setQNameGer(this.newQuesNameGer);
    ques.setQHintEng(this.newDescEng);
    ques.setQHintGer(this.newDescGer);
  }

  /**
   * create the TQuestionConfig
   *
   * @param dbQues TQuestion
   * @throws CommandException
   */
  private void createQuestionConfig(final TQuestion dbQues) throws CommandException {
    this.cmdQuesConfig.setDbQues(dbQues);
    this.childCmdStack.addCommand(this.cmdQuesConfig);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.question.getID(), TQuestion.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.question.getObjectDetails());
    final TQuestion dbQuestion = getEntityProvider().getDbQuestion(this.question.getID());
    validateStaleData(dbQuestion);
    if (this.onlyDepChange) {
      setDepQuestionAndResp(dbQuestion);
    }

    else {
      // update the name and description
      if (isStringChanged(this.oldNameEng, this.newQuesNameEng) ||
          isStringChanged(this.oldNameGer, this.newQuesNameGer) || isStringChanged(this.oldDescEng, this.newDescEng) ||
          isStringChanged(this.oldDescGer, this.newDescGer)) {
        setNameDesc(dbQuestion);
      }
      if (this.oldHeadingFlag != this.newHeadingFlag) {
        if (this.newHeadingFlag) {
          dbQuestion.setHeadingFlag(ApicConstants.YES);
        }
        else {
          dbQuestion.setHeadingFlag(ApicConstants.CODE_NO);
        }
      }
      if (isStringChanged(this.oldPositiveResult, this.newPositiveResult)) {
        dbQuestion.setPositiveResult(this.newPositiveResult);
      }
      // ICDM-1972 question number change
      reorderQNumber(dbQuestion);
      if (!this.isHeading) {// if it is not a heading, store the configuration
        createQuestionConfig(dbQuestion);
        createAttrDepnAndVals();
        editAttrDepnAndVals();
        deleteAttrDepnAndVals();
      }
      setDepQuestionAndResp(dbQuestion);
    }
    setUserDetails(COMMAND_MODE.UPDATE, dbQuestion, QUES_ENTITY_ID);
    getChangedData().put(this.question.getID(), chdata);
  }


  /**
   * ICDM-1972
   *
   * @param dbQuestion TQuestion
   */
  private void reorderQNumber(final TQuestion dbQuestion) {
    Long oldQNo = dbQuestion.getQNumber();
    SortedSet<Question> childQuestions;
    if ((this.parent == null) || (this.newLevel == 1)) {
      childQuestions = this.qVersion.getFirstLevelQuestions();
    }
    else {
      childQuestions = this.parent.getChildQuestions();
    }
    if (oldQNo == null) {
      oldQNo = (long) (childQuestions.size() + 1);
    }
    if ((ApicUtil.compare(this.qNumber, oldQNo) == ApicConstants.OBJ_EQUAL_CHK_VAL) && !this.isLevelChange) {
      if (this.commandMode == COMMAND_MODE.INSERT) {
        dbQuestion.setQNumber(this.qNumber);
      }
      return;
    }
    if (this.isLevelChange) {
      levelChangeReOrder(dbQuestion, oldQNo, childQuestions);
    }
    else {
      reorderWithinSameParent(dbQuestion, oldQNo, childQuestions);
    }
  }

  /**
   * @param dbQuestion
   * @param oldQNo
   * @param childQuestions
   */
  private void reorderWithinSameParent(final TQuestion dbQuestion, final Long oldQNo,
      final SortedSet<Question> childQuestions) {
    // set 0 as quetion number to avoid unique constraint exception
    dbQuestion.setQNumber(0L);
    getEntityProvider().getEm().flush();
    if (oldQNo > this.qNumber) {
      // if the old question number is greater than new question number
      // make a sorted set with reverse order
      SortedSet<Question> reverseChildQuestions = new TreeSet<>(Collections.reverseOrder());
      reverseChildQuestions.addAll(childQuestions);
      for (Question ques : reverseChildQuestions) {
        long qNum = ques.getQNum();
        if ((qNum >= this.qNumber) && (qNum < oldQNo)) {
          // from new question number to old question number , increment the question numbers by 1
          getEntityProvider().getDbQuestion(ques.getID()).setQNumber(qNum + 1);
          getEntityProvider().getEm().flush();
        }

      }
    }
    else {
      for (Question ques : childQuestions) {
        long qNum = ques.getQNum();
        if ((qNum <= this.qNumber) && (qNum > oldQNo)) {
          // from new question number to old question number , decrement the question numbers by 1
          getEntityProvider().getDbQuestion(ques.getID()).setQNumber(qNum - 1);
          getEntityProvider().getEm().flush();
        }

      }
    }
    dbQuestion.setQNumber(this.qNumber);
  }

  /**
   * Changing the level of questions/headings
   *
   * @param dbQuestion
   * @param oldQNo
   * @param childQuestions
   */
  private void levelChangeReOrder(final TQuestion dbQuestion, final Long oldQNo,
      final SortedSet<Question> childQuestions) {
    Long currentQsNum = this.question.getQNum();
    int qsLevel = this.question.getQuestionLevel();
    Question imediateParemt = this.question.getParentQuestion();
    // set 0 as quetion number to avoid unique constraint exception
    dbQuestion.setQNumber(0L);
    dbQuestion.setTQuestion(null);
    getEntityProvider().getEm().flush();
    TQuestion parentDbQuestion = getEntityProvider().getDbQuestion(this.parent.getID());
    if (qsLevel == ONE_CONST) {
      // moving the qs up , then the below qs numbers have to be decremented
      for (Question selQues : this.qVersion.getFirstLevelQuestions()) {
        long qNum = selQues.getQNum();
        if (qNum > currentQsNum) {
          getEntityProvider().getDbQuestion(selQues.getID()).setQNumber(qNum - ONE_CONST);
          getEntityProvider().getEm().flush();
        }
      }
      SortedSet<Question> reverseChildQuestions = new TreeSet<>(Collections.reverseOrder());
      reverseChildQuestions.addAll(childQuestions);
      for (Question ques : reverseChildQuestions) {
        long qNum = ques.getQNum();
        if (qNum >= this.qNumber) {
          getEntityProvider().getDbQuestion(ques.getID()).setQNumber(qNum + ONE_CONST);
          getEntityProvider().getEm().flush();
        }
      }
      dbQuestion.setTQuestion(parentDbQuestion);
      dbQuestion.setQNumber(this.qNumber);
      getEntityProvider().getEm().flush();
      parentDbQuestion.getTQuestions().add(dbQuestion);
    }
    else if ((qsLevel == LEVEL_TWO_CONST) || (qsLevel == LEVEL_THREE_CONST)) {
      // selected qs parent's other second level childrens number has to be decremented
      for (Question ques : imediateParemt.getChildQuestions()) {
        long qNum = ques.getQNum();
        if (qNum > oldQNo) {
          getEntityProvider().getDbQuestion(ques.getID()).setQNumber(qNum - ONE_CONST);
          getEntityProvider().getEm().flush();
        }
      }
      SortedSet<Question> reverseChildQuestions = new TreeSet<>(Collections.reverseOrder());
      reverseChildQuestions.addAll(childQuestions);
      for (Question ques : reverseChildQuestions) {
        long qNum = ques.getQNum();
        if (qNum >= this.qNumber) {
          getEntityProvider().getDbQuestion(ques.getID()).setQNumber(qNum + 1);
          getEntityProvider().getEm().flush();
        }
      }
      if (((qsLevel == LEVEL_THREE_CONST) && (this.parent.getParentQuestion() == null)) ||
          ((qsLevel == LEVEL_TWO_CONST) && (this.newLevel == LEVEL_THREE_CONST))) {
        dbQuestion.setQNumber(this.qNumber);
        dbQuestion.setTQuestion(parentDbQuestion);
        getEntityProvider().getEm().flush();
        getEntityProvider().getDbQuestion(imediateParemt.getID()).getTQuestions().remove(dbQuestion);
        parentDbQuestion.getTQuestions().add(dbQuestion);
      }
      else {
        dbQuestion.setQNumber(this.qNumber);
        getEntityProvider().getEm().flush();
        parentDbQuestion.getTQuestions().remove(dbQuestion);
      }
    }
  }

  /**
   * editing the attribute dependency value combinations
   *
   * @throws CommandException
   */
  private void editAttrDepnAndVals() throws CommandException {
    if (null != this.editQuesDepnValList) {
      // create QuestionDepnAttribute
      for (QuesDepnValCombination quesDepnValComb : this.editQuesDepnValList) {
        CmdModQuesAttrValComb cmdQuesDepnValComb = new CmdModQuesAttrValComb(getDataProvider(), quesDepnValComb, false);
        this.childCmdStack.addCommand(cmdQuesDepnValComb);
      }
    }
  }

  /**
   * delete the specified attributes and val combinations
   *
   * @throws CommandException
   */
  private void deleteAttrDepnAndVals() throws CommandException {
    if (null != this.deleteAttrSet) {
      // create QuestionDepnAttribute
      for (QuestionDepenAttr quesAttr : this.deleteAttrSet) {
        CmdModQuesDepnAttr cmdDepnAttr = new CmdModQuesDepnAttr(getDataProvider(), quesAttr);
        this.childCmdStack.addCommand(cmdDepnAttr);
      }
    }

    if (null != this.deleteQuesDepnValList) {
      for (QuesDepnValCombination quesDepnValComb : this.deleteQuesDepnValList) {
        CmdModQuesAttrValComb cmdQuesDepnValComb = new CmdModQuesAttrValComb(getDataProvider(), quesDepnValComb, true);
        this.childCmdStack.addCommand(cmdQuesDepnValComb);
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.question.getID(), TQuestion.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.question.getObjectDetails());
    final TQuestion dbQuestion = getEntityProvider().getDbQuestion(this.question.getID());
    validateStaleData(dbQuestion);
    dbQuestion.setDeletedFlag(ApicConstants.YES);
    setUserDetails(COMMAND_MODE.DELETE, dbQuestion, QUES_ENTITY_ID);
    getChangedData().put(this.question.getID(), chdata);
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
    return super.getString("", "Question" + getPrimaryObjectIdentifier());
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
    addTransactionSummaryDetails(detailsList, this.oldNameEng, this.newQuesNameEng, "Question Name English");
    addTransactionSummaryDetails(detailsList, this.oldNameGer, this.newQuesNameGer, "Question Name German");
    addTransactionSummaryDetails(detailsList, this.oldDescEng, this.newDescEng, "Question Hint English");
    addTransactionSummaryDetails(detailsList, this.oldDescGer, this.newDescGer, "Question Hint German");
    addTransactionSummaryDetails(detailsList, this.oldPositiveResult, this.newPositiveResult,
        "Question Positive Result");
    addTransactionSummaryDetails(detailsList, String.valueOf(this.oldHeadingFlag),
        String.valueOf(this.newHeadingFlag), "Heading");
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
        caseUpdPostCommit();
        break;
      case DELETE:
        this.qVersion.getAllQuestions().remove(this.question);
        if (this.question.getParentQuestion() != null) {
          this.question.getParentQuestion().getChildQuestions().remove(this.question);
        }
        break;
      default:
        // Do nothing
        break;
    }
    this.childCmdStack.doPostCommit();
  }

  /**
   * 
   */
  private void caseUpdPostCommit() {
    // clear and load the questions for questionnaire version for new order
    this.qVersion.getAllQuestions().clear();
    this.qVersion.getAllQuestions();
    // for reorder
    if (this.parent != null) {
      this.parent.getChildQuestions().clear();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.question == null ? null : this.question.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return this.isHeading ? "Heading" : "Question";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.newQuesNameEng;
  }


  /**
   * @param newQuesNameEng the newQuesNameEng to set
   */
  public void setNewQuesNameEng(final String newQuesNameEng) {
    this.newQuesNameEng = newQuesNameEng;
  }


  /**
   * @param newQuesNameGer the newQuesNameGer to set
   */
  public void setNewQuesNameGer(final String newQuesNameGer) {
    this.newQuesNameGer = newQuesNameGer;
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
   * @param newPositiveResult the newPositiveResult to set
   */
  public void setNewPositiveResult(final String newPositiveResult) {
    this.newPositiveResult = newPositiveResult;
  }

  /**
   * @return the cmdQuesConfig
   */
  public CmdModQuesConfig getCmdQuesConfig() {
    return this.cmdQuesConfig;
  }


  /**
   * @param quesDepnValCombList the quesDepnValCombList to set
   */
  public void setQuesDepnValCombList(final List<QuesDepnValCombination> quesDepnValCombList) {
    this.quesDepnValCombList = quesDepnValCombList;
  }


  /**
   * @param attrSet the attrSet to set
   */
  public void setAttrSet(final Set<Attribute> attrSet) {
    this.attrSet = attrSet;
  }


  /**
   * @param deleteQuesDepnValList the deleteQuesDepnValList to set
   */
  public void setDeleteQuesDepnValList(final List<QuesDepnValCombination> deleteQuesDepnValList) {
    this.deleteQuesDepnValList = deleteQuesDepnValList;
  }


  /**
   * @param deleteAttrSet the deleteAttrSet to set
   */
  public void setDeleteAttrSet(final Set<QuestionDepenAttr> deleteAttrSet) {
    this.deleteAttrSet = deleteAttrSet;
  }

  /**
   * @return the question
   */
  public Question getQuestion() {
    return this.question;
  }

  /**
   * @param editQuesDepnValList the editQuesDepnValList to set
   */
  public void setEditQuesDepnValList(final List<QuesDepnValCombination> editQuesDepnValList) {
    this.editQuesDepnValList = editQuesDepnValList;
  }


  /**
   * @param newHeadingFlag the newHeadingFlag to set
   */
  public void setNewHeadingFlag(final boolean newHeadingFlag) {
    this.newHeadingFlag = newHeadingFlag;
  }


  /**
   * @param item
   */
  public void setQuestionNo(final String item) {
    this.qNumber = Long.parseLong(item);

  }


  /**
   * @param depQuestion the depQuestion to set
   */
  public void setDepQuestion(final Question depQuestion) {
    this.depQuestion = depQuestion;
  }


  /**
   * @param depQuestionResp the depQuestionResp to set
   */
  public void setDepQuestionResp(final String depQuestionResp) {
    this.depQuestionResp = depQuestionResp;
  }


  /**
   * @param onlyDepChange onlyDepChange
   */
  public void setOnlyDepChange(final boolean onlyDepChange) {

    this.onlyDepChange = onlyDepChange;
  }

  /**
   * @param deletedFlag deletedFlag
   */
  public void setDeletedFlag(final boolean deletedFlag) {
    this.deletedFlag = deletedFlag;

  }
}
