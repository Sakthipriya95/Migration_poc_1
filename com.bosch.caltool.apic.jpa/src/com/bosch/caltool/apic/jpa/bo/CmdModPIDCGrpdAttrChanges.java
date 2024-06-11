/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataObject;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author dmo5cob
 */
public class CmdModPIDCGrpdAttrChanges extends AbstractCommand {

  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * list of commands to be added as child command
   */
  private final List<AbstractCmdModProjAttr> cmdPrjAttrList = new ArrayList<AbstractCmdModProjAttr>();

  /**
   * AbstractDataObject instance
   */
  private final AbstractDataObject dataObj;

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * @param dataProvider AbstractDataProvider
   * @param dataObj AbstractDataObject
   */
  public CmdModPIDCGrpdAttrChanges(final AbstractDataProvider dataProvider, final AbstractDataObject dataObj) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.UPDATE;
    this.dataObj = dataObj;
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
  protected void executeInsertCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    for (AbstractCmdModProjAttr cmd : this.cmdPrjAttrList) {
      this.childCmdStack.addCommand(cmd);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

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
    this.childCmdStack.undoAll();

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
    // command to be executed only if the command list is not empty
    return CommonUtils.isNotEmpty(this.cmdPrjAttrList);
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
    // call the doPostCommit of child commands
    this.childCmdStack.doPostCommit();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.dataObj.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Multiple Attribute Values";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.dataObj.getName();
  }

  /**
   * @param command CmdModLinks
   * @throws CommandException CommandException
   */
  public void addChildCommand(final AbstractCmdModProjAttr command) throws CommandException {
    this.cmdPrjAttrList.add(command);
  }


  /**
   * @return List<CmdModLinks>
   */
  public List<AbstractCmdModProjAttr> getCommandList() {
    return this.cmdPrjAttrList;
  }

}
