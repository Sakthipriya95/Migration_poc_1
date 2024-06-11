/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.a2l.jpa.A2LEditorDataProvider;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;


/**
 * @author bne4cob
 */
public class CmdModCDRRvwFunction extends AbstractCDRCommand {

  /**
   * Parent review result of this review funciton entry
   */
  private final CDRResult reviewResult;

  /**
   * Function of the result function entity
   */
  private final CDRFunction function;

  /**
   * Review function entity ID
   */
  private static final String ENTITY_ID = "RVW_FUNCTION";

  /**
   * The created result function object
   */
  private CDRResultFunction resFunction;

  /**
   * Field which represents an existing cdrResultFunction
   */
  private CDRResultFunction cdrResultFunction;
  private A2LEditorDataProvider a2lEditorDP;
  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Command to create a new CDR Rvw Function record
   *
   * @param dataProvider CDR data provider
   * @param reviewResult review result
   * @param function function object
   * @param a2lEditorDP
   */
  public CmdModCDRRvwFunction(final CDRDataProvider dataProvider, final CDRResult reviewResult,
      final CDRFunction function, final A2LEditorDataProvider a2lEditorDP) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.a2lEditorDP = a2lEditorDP;
    this.reviewResult = reviewResult;
    this.function = function;
  }

  /**
   * Command used to update or delete an existing function
   *
   * @param dataProvider
   * @param reviewResult
   * @param cdrResultFunction
   * @param isUpdate
   */
  public CmdModCDRRvwFunction(final CDRDataProvider dataProvider, final CDRResult reviewResult,
      final CDRResultFunction cdrResultFunction, final boolean isUpdate) {
    super(dataProvider);
    if (isUpdate) {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    else {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    this.reviewResult = reviewResult;
    this.cdrResultFunction = cdrResultFunction;
    this.function = cdrResultFunction.getCDRFunction();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      this.reviewResult.getResFunctionMap().remove(this.resFunction.getID());
      getDataCache().getCDRResultFunctionMap().remove(this.resFunction.getID());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    final TRvwFunction dbRvwFun = new TRvwFunction();
    dbRvwFun.setTFunction(getEntityProvider().getDbFunction(this.function.getID()));
    dbRvwFun.setTRvwResult(getEntityProvider().getDbCDRResult(this.reviewResult.getID()));
    // ICDM-1720
    SortedSet<Function> a2lFunctions = this.a2lEditorDP.getFunctionsOfLabelType(null);
    String funcVer = null;
    for (Function selFunc : a2lFunctions) {
      if (selFunc.getName().equals(this.function.getName())) {
        funcVer = selFunc.getFunctionVersion();
      }
    }
    dbRvwFun.setTFuncVers(funcVer);
    setUserDetails(COMMAND_MODE.INSERT, dbRvwFun, ENTITY_ID);

    getEntityProvider().registerNewEntity(dbRvwFun);
    Set<TRvwFunction> tRvwFunctions = getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwFunctions();
    if (tRvwFunctions == null) {
      tRvwFunctions = new HashSet<TRvwFunction>();
    }
    tRvwFunctions.add(dbRvwFun);

    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).setTRvwFunctions(tRvwFunctions);

    this.resFunction = new CDRResultFunction(getDataProvider(), dbRvwFun.getRvwFunId());
    // NOPMD
    Map<Long, CDRResultFunction> resFunctionMap = this.reviewResult.getResFunctionMap();
    resFunctionMap.put(this.resFunction.getID(), this.resFunction);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    TRvwFunction dbCDRResFunction = getEntityProvider().getDbCDRResFunction(this.cdrResultFunction.getID());
    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwFunctions().remove(dbCDRResFunction);
    getEntityProvider().deleteEntity(dbCDRResFunction);
    this.reviewResult.getResFunctionMap().remove(this.cdrResultFunction.getID());
    getDataCache().getCDRResultFunctionMap().remove(this.cdrResultFunction.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    final TRvwFunction dbRvwFun = getEntityProvider().getDbCDRResFunction(this.resFunction.getID());

    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwFunctions().remove(dbRvwFun);

    getEntityProvider().deleteEntity(dbRvwFun);

    this.reviewResult.getResFunctionMap().remove(this.resFunction.getID());
    getDataCache().getCDRResultFunctionMap().remove(this.resFunction.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // Not application for insert only command
    return false;
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
  protected void doPostCommit() {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.resFunction == null ? null : this.resFunction.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Review Function";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        // Not Applicable
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        addTransactionSummaryDetails(detailsList, getPrimaryObjectIdentifier(), "", getPrimaryObjectType());
        break;
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
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.function.getName();
  }

}
