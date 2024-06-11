/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttFuncparam;


/**
 * Parent command to modify multiple rule set parameters
 *
 * @author dmo5cob
 */
public class CmdModMultipleRuleSetParam extends AbstractCDRCommand {


  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * child command stack
   */
  private final ChildCommandStack childCmdStack = new ChildCommandStack(this);
  /**
   * RuleSet instance
   */
  private final RuleSet ruleSet;

  /**
   * Map of parameter and function names
   */
  private Map<String, String> paramFuncNameMap;

  /**
   * key - param name , value - CDRFunction
   */
  private Map<String, CDRFunction> paramFuncObjMap;
  /**
   * key - param name , value - CDRFuncParameter
   */
  private Map<String, CDRFuncParameter> paramNameObjMap;

  /**
   * List of ruleset parameters
   */
  private List<RuleSetParameter> ruleSetParamList;

  /**
   * Constructor without function names specified
   *
   * @param dataProvider CDRDataProvider instance
   * @param ruleSet RuleSet instance
   * @param paramFuncNameMap Map<String, String>
   * @param paramFuncObjMap Map<String, CDRFunction>
   * @param paramNameObjMap Map<String, CDRFuncParameter>
   */
  public CmdModMultipleRuleSetParam(final CDRDataProvider dataProvider, final RuleSet ruleSet,
      final Map<String, String> paramFuncNameMap, final Map<String, CDRFunction> paramFuncObjMap,
      final Map<String, CDRFuncParameter> paramNameObjMap) {

    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.ruleSet = ruleSet;
    this.paramFuncNameMap = paramFuncNameMap;
    this.paramFuncObjMap = paramFuncObjMap;
    this.paramNameObjMap = paramNameObjMap;

  }

  /**
   * @param cdrDataProvider CDRDataProvider
   * @param ruleSetParamList List<RuleSetParameter>
   */
  public CmdModMultipleRuleSetParam(final CDRDataProvider cdrDataProvider,
      final List<RuleSetParameter> ruleSetParamList) {
    // ICDM-1426
    super(cdrDataProvider);
    this.commandMode = COMMAND_MODE.DELETE;
    this.ruleSetParamList = ruleSetParamList;
    this.ruleSet = ruleSetParamList.get(0).getRuleSet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    // Task 246879
    if (null != this.paramFuncNameMap) {
      fetchCDRParamsAndFuncs();
    }


    for (String paramName : this.paramNameObjMap.keySet()) {

      final CmdModRuleSetParam cmd = new CmdModRuleSetParam(getDataProvider(), this.ruleSet,
          this.paramNameObjMap.get(paramName), this.paramFuncObjMap.get(paramName));
      this.childCmdStack.addCommand(cmd);

    }
  }

  /**
   * Task 246879 fetch cdr parameters and functions into maps
   */
  private void fetchCDRParamsAndFuncs() {

    this.paramFuncObjMap = new HashMap<>();
    this.paramNameObjMap = new HashMap<>();
    // Delete the existing records in this temp table, if any
    final Query delQuery = getEntityProvider().getEm().createQuery("delete from GttFuncparam temp");
    delQuery.executeUpdate();
    GttFuncparam tempfuncParam;
    long recID = 1;

    // param name insertion into temporary table
    for (String paramName : this.paramFuncNameMap.keySet()) {
      // insert records into temporary table GttFuncparam
      tempfuncParam = new GttFuncparam();
      tempfuncParam.setId(recID);
      tempfuncParam.setParamName(paramName);

      getEntityProvider().registerNewEntity(tempfuncParam);
      recID++;
    }

    getEntityProvider().getEm().flush();
    // get param id , function name and id from database
    StringBuilder statement = new StringBuilder();
    statement.append(
        "SELECT distinct p.name,p.id,tFunction.name,tFunction.id FROM TParameter p,TFunctionversion funcVer,TFunction tFunction,GttFuncparam temp where tFunction.relevantName='Y' and tFunction.upperName=funcVer.funcNameUpper and funcVer.defcharname=p.name and temp.paramName=p.name");

    final Query qParameters = getEntityProvider().getEm().createQuery(statement.toString());
    // get the parameters list
    List dbParams = qParameters.getResultList();
    for (Object tParameter : dbParams) {
      if (tParameter instanceof Object[]) {

        Object[] tParamArr = (Object[]) tParameter;
        // generate the CDRFuncParameter object
        CDRFuncParameter cdrFuncParam = new CDRFuncParameter(getDataProvider(), (long) tParamArr[1]);
        String paramName = (String) tParamArr[0];
        this.paramNameObjMap.put(paramName, cdrFuncParam);

        String funcName = this.paramFuncNameMap.get(paramName);
        CDRFunction func;
        // generate CDRFunction object
        if (CommonUtils.isEmptyString(funcName)) {
          final CDRFunction cdrFunction =
              new CDRFunction(getDataProvider(), (long) tParamArr[3], (String) tParamArr[2]);
          getDataCache().getAllCDRFunctions().put(paramName, cdrFunction);
          func = cdrFunction;
        }
        else {
          // otherwise get the CDRFunction instance
          func = getDataProvider().getCDRFunction(funcName);
        }

        this.paramFuncObjMap.put(paramName, func);
      }

    }
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
    // ICDM-1426
    for (RuleSetParameter ruleSetParam : this.ruleSetParamList) {
      final CmdModRuleSetParam cmd = new CmdModRuleSetParam(getDataProvider(), ruleSetParam);
      this.childCmdStack.addCommand(cmd);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    this.childCmdStack.undoAll();


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Not applicable

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
  protected void rollBackDataModel() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.ruleSet == null ? null : this.ruleSet.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Multiple Ruleset Parameters";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.ruleSet.getName();
  }


}
