/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * ICDM-2054 command for creating , deleting or updating attr value combination
 *
 * @author mkl2cob
 */
public class CmdModQuesAttrValComb extends AbstractCDRCommand {


  /**
   * QuesDepnValCombination
   */
  private final QuesDepnValCombination quesAttrValComb;

  /**
   * Stack for storing child commands executed after creating the PIDC entity
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Combiantion no
   */
  private Long combinationNum;

  /**
   * Question
   */
  private final Question question;

  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Constructor for INSERT
   *
   * @param dataProvider CDRDataProvider
   * @param question Question
   * @param attrValComb QuesDepnValCombination
   */
  public CmdModQuesAttrValComb(final CDRDataProvider dataProvider, final Question question,
      final QuesDepnValCombination attrValComb) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    // load attributes with this method
    question.getAttributes();

    this.question = question;
    this.quesAttrValComb = attrValComb;
  }

  /**
   * Constructor for DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param quesAttrValComb QuesDepnValCombination
   * @param delete isDelete
   */
  public CmdModQuesAttrValComb(final CDRDataProvider dataProvider, final QuesDepnValCombination quesAttrValComb,
      final boolean delete) {
    super(dataProvider);
    this.quesAttrValComb = quesAttrValComb;
    this.question = quesAttrValComb.getQuestion();


    if (delete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
      case DELETE:
        this.childCmdStack.rollbackAll(getExecutionMode());
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
  protected void executeInsertCommand() throws CommandException {

    // iterate the combinations
    Map<Attribute, AttributeValue> attrValCombiMap = this.quesAttrValComb.getAttrValMap();

    // set combination number and question
    this.quesAttrValComb.setCombinationId(this.combinationNum);
    for (Entry<Attribute, AttributeValue> attrVal : attrValCombiMap.entrySet()) {
      // create attr value combination commands
      // create QuestionDepnAttrValue with commands
      CmdModQuesDepnAttrValue cmdModDepnAttrValue = new CmdModQuesDepnAttrValue(getDataProvider(),
          this.question.getAttrDepnMap().get(attrVal.getKey()), attrVal.getValue(), this.quesAttrValComb);
      this.childCmdStack.addCommand(cmdModDepnAttrValue);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // create a set of Attribute from QuesDepnAttr
    Set<Attribute> attrSet = new HashSet<>();
    for (QuestionDepenAttr quesDepnAttr : this.quesAttrValComb.getQuesAttrValMap().keySet()) {
      attrSet.add(quesDepnAttr.getAttribute());
    }
    // find out the attributes that are added newly
    Set<Attribute> diffInAttrSet = CommonUtils.getDifference(this.quesAttrValComb.getAttrValMap().keySet(), attrSet);
    for (Attribute attr : diffInAttrSet) {
      AttributeValue attributeValue = this.quesAttrValComb.getAttrValMap().get(attr);
      // create QuestionDepnAttrValue with commands
      CmdModQuesDepnAttrValue cmdModDepnAttrValue = new CmdModQuesDepnAttrValue(getDataProvider(),
          this.question.getAttrDepnMap().get(attr), attributeValue, this.quesAttrValComb);
      this.childCmdStack.addCommand(cmdModDepnAttrValue);
    }
    // find out the attributes which are not newly created
    Set<Attribute> existingAttrSet =
        CommonUtils.getDifference(this.quesAttrValComb.getAttrValMap().keySet(), diffInAttrSet);
    for (Attribute attr : existingAttrSet) {
      AttributeValue attributeValue = this.quesAttrValComb.getAttrValMap().get(attr);
      // create QuestionDepnAttrValue with commands
      CmdModQuesDepnAttrValue cmdModDepnAttrValue = new CmdModQuesDepnAttrValue(getDataProvider(),
          this.quesAttrValComb.getQuesAttrValMap().get(this.question.getAttrDepnMap().get(attr)), attributeValue,
          this.quesAttrValComb);
      this.childCmdStack.addCommand(cmdModDepnAttrValue);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    for (Entry<QuestionDepenAttr, QuestionDepenAttrValue> attrVal : this.quesAttrValComb.getQuesAttrValMap()
        .entrySet()) {
      // create attr value combination commands
      // create QuestionDepnAttrValue with commands
      CmdModQuesDepnAttrValue cmdModDepnAttrValue =
          new CmdModQuesDepnAttrValue(getDataProvider(), attrVal.getValue(), this.quesAttrValComb);
      this.childCmdStack.addCommand(cmdModDepnAttrValue);
    }


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
    return super.getString("", "Question Attr Val Combination" + getPrimaryObjectIdentifier());
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
    // call the doPostCommit of child commands
    this.childCmdStack.doPostCommit();
    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
        this.quesAttrValComb.setAddedToExistingOnes(false);
        this.question.getQuesAttrValComb().put(this.quesAttrValComb.getCombinationId(), this.quesAttrValComb);
        break;
      case DELETE:
        this.question.getQuesAttrValComb().remove(this.quesAttrValComb.getCombinationId());
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
    return this.quesAttrValComb == null ? null : this.quesAttrValComb.getCombinationId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Question Attribute Value Combination";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.quesAttrValComb == null ? null : this.quesAttrValComb.toString();
  }


  /**
   * @param combinationNum the combinationNum to set
   */
  public void setCombinationNum(final Long combinationNum) {
    this.combinationNum = combinationNum;
  }

}
