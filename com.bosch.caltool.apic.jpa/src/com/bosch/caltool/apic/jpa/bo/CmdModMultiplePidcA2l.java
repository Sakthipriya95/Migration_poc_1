/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;


/**
 * @author dmo5cob ICDM-1565
 */
@Deprecated
public class CmdModMultiplePidcA2l extends AbstractCommand {

  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * Selected PIDC version
   */
  private final PIDCVersion selPidcVersion;
  /**
   * A2l files list
   */
  private final List<A2LFile> a2lFiles;

  /**
   * Constant for dummy ID value
   */
  public static final Long VALUE_ID = 0L;
  /**
   * storing the child commands for doing post commit changes
   */
  private final List<CmdModPidcA2l> cmdlist = new ArrayList<>();

  /**
   * Create a new command for inserting records
   *
   * @param dataProvider apic data provider
   * @param pidcVersion PIDCVersion
   * @param a2lFiles list of a2l files
   */
  public CmdModMultiplePidcA2l(final ApicDataProvider dataProvider, final PIDCVersion pidcVersion,
      final List<A2LFile> a2lFiles) {
    super(dataProvider);
    // set the command mode - UPDATE
    this.commandMode = COMMAND_MODE.UPDATE;
    this.selPidcVersion = pidcVersion;
    // Task 290992 : Mutable members should not be stored or returned directly
    this.a2lFiles = new ArrayList<>(a2lFiles);


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


    for (A2LFile a2lFile : this.a2lFiles) {

      CmdModPidcA2l command;
      // if the selected a2l file is not mapped to any version , insert command should be called else update
      if (a2lFile.getPidcA2l() == null) {
        command = new CmdModPidcA2l(getDataProvider(), this.selPidcVersion, a2lFile);
      }
      else {
        // if version id is null (dummy version selected) then delete else update
        if (VALUE_ID.equals(this.selPidcVersion.getID())) {
          command = new CmdModPidcA2l(getDataProvider(), a2lFile.getPidcA2l(), true, false);
        }
        else {
          command = new CmdModPidcA2l(getDataProvider(), a2lFile.getPidcA2l(), false, false);
        }
      }

      command.setNewPidcVersion(this.selPidcVersion);
      if (null != a2lFile.getPidcA2l()) {
        command.setNewSoftwareProjId(a2lFile.getPidcA2l().getSsdSoftwareProjID());
        command.setNewSoftwareVersId(a2lFile.getPidcA2l().getSsdSoftwareVersionID());
        command.setNewSoftwareVersion(a2lFile.getPidcA2l().getSsdSoftwareVersion());
      }
      this.childCmdStack.addCommand(command);
      this.cmdlist.add(command);

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
  public Long getPrimaryObjectID() {
    return this.selPidcVersion.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Multiple A2l Mapping";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.selPidcVersion.getName();
  }


}
