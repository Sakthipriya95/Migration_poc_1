/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author mkl2cob
 */
public class CmdModMultipleA2LWPResp extends AbstractA2LCommand {

  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);
  /**
   * list of A2LWpResponsibility
   */
  private final List<A2LWpResponsibility> respList;
  /**
   * WPResponsibility
   */
  private final WPResponsibility wpRespObj;

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor for UPDATE
   *
   * @param dataProvider AbstractDataProvider
   * @param respList List<A2LWpResponsibility>
   * @param wpRespObj WPResponsibility
   */
  public CmdModMultipleA2LWPResp(final AbstractDataProvider dataProvider, final List<A2LWpResponsibility> respList,
      final WPResponsibility wpRespObj) {
    super(dataProvider);
    // Task 290992 : Mutable members should not be stored or returned directly
    this.respList = new ArrayList<A2LWpResponsibility>(respList);
    this.wpRespObj = wpRespObj;
    this.commandMode = COMMAND_MODE.UPDATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // Not applicable

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
    for (A2LWpResponsibility a2lWpResp : this.respList) {
      CmdModA2LWPResp cmdA2lWpResp = new CmdModA2LWPResp(getDataProvider(), this.wpRespObj, a2lWpResp);
      this.childCmdStack.addCommand(cmdA2lWpResp);
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
    return CommonUtils.isNotEmpty(this.respList);
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
  public Long getPrimaryObjectID() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Multiple A2L WP Responsibility";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.respList.get(0).getA2lResponsibility().getPidcA2l().getA2LFileName();
  }

}
