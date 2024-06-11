/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.model.query.Filter;
import com.bosch.boot.ssd.api.util.ConstantFunctionsList;
import com.bosch.boot.ssd.api.util.LabelCreationUtils;
import com.bosch.boot.ssd.api.util.ValidateRuleUtil;
import com.bosch.boot.ssd.api.util.ValidationConstants;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.IConstantFuncMessages;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.ICtrlStatementMessages;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.ILblFuncMessages;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.IMathFuncMessages;
import com.bosch.boot.ssd.api.validaterules.messagesinterfaces.LabelNameExtraction;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.Module;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.checkssd.datamodel.SSDFile;
import com.bosch.checkssd.datamodel.SSDStatement;
import com.bosch.checkssd.datamodel.a2lvariable.A2LData;
import com.bosch.checkssd.datamodel.a2lvariable.A2LVariable;
import com.bosch.checkssd.datamodel.constant.ConstText;
import com.bosch.checkssd.datamodel.constant.ConstValue;
import com.bosch.checkssd.datamodel.constant.Constant;
import com.bosch.checkssd.datamodel.control.Control;
import com.bosch.checkssd.datamodel.control.CtrlFidCheck;
import com.bosch.checkssd.datamodel.control.CtrlFor;
import com.bosch.checkssd.datamodel.control.CtrlIf;
import com.bosch.checkssd.datamodel.control.CtrlLabel;
import com.bosch.checkssd.datamodel.control.UseCaseContactInfo;
import com.bosch.checkssd.datamodel.evaluatessdrules.EvaluateConstantRules;
import com.bosch.checkssd.datamodel.evaluatessdrules.EvaluateControlRules;
import com.bosch.checkssd.datamodel.evaluatessdrules.EvaluateLabelRules;
import com.bosch.checkssd.datamodel.mathterm.ConstFunctions;
import com.bosch.checkssd.datamodel.mathterm.IMathTermElement;
import com.bosch.checkssd.datamodel.mathterm.MathFunction;
import com.bosch.checkssd.datamodel.mathterm.MathTerm;
import com.bosch.checkssd.datamodel.mathterm.SSDString;
import com.bosch.checkssd.datamodel.rule.CheckRule;
import com.bosch.checkssd.datamodel.util.SSDGenericsForRules;
import com.bosch.checkssd.datamodel.util.SSDModelUtils;
import com.bosch.checkssd.exception.CheckSSDException;
import com.bosch.checkssd.reports.CheckSSDReport;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;
import com.bosch.checkssd.reports.reportmodel.ReportValueModel;
import com.bosch.ssd.parser.ParserSSD;

/**
 * @author TAB1JA
 */
@Service
public class ValidateRuleInvokerService
    implements IConstantFuncMessages, ICtrlStatementMessages, IMathFuncMessages, ILblFuncMessages, LabelNameExtraction {

  @Autowired
  private SSDFileEditorService ssdFileEditorService;

  private CheckSSDReport rptInstance;

  private final SSDModelUtils utilInst = new SSDModelUtils();

  private A2LFileInfo a2lFileInfo;

  private Map<String, CalData> calDataMap;

  private List<String> labelListName;

  private ILoggerAdapter logger;

  private SSDFile ssdFileModel;

  private String existingErrors;

  private boolean isError;

  private Map<Integer, String> lineNoAndError;

  private final SSDGenericsForRules ssdGnrRulesInstance = new SSDGenericsForRules();

  private final List<String> nplNameList = new CopyOnWriteArrayList<>();

  private LabelCreationUtils labelUtils;

  private A2LData a2lData;

  /**
   * @param plogger Iloggeradapter instance
   */
  private void initializeObjects(final ILoggerAdapter plogger) {

    this.logger = plogger;
    this.utilInst.setSsdGnrRulesInstance(this.ssdGnrRulesInstance);
    this.rptInstance = new CheckSSDReport();
    this.utilInst.setRptInstance(this.rptInstance);
    this.utilInst.setSsdLogger(this.logger);
    this.a2lFileInfo = new A2LFileInfo();
    Module module = new Module();
    this.a2lFileInfo.getModuleMap().put("DIM", module);
    this.calDataMap = new HashMap<>();
    this.labelUtils = new LabelCreationUtils(this.a2lFileInfo, this.calDataMap, this.logger);
    this.lineNoAndError = new HashMap<>();
    this.labelUtils.createA2lFileInfo();
    this.a2lData = new A2LData(this.a2lFileInfo);
    this.utilInst.setA2lData(this.a2lData);
    this.utilInst.setCalDataMap(this.calDataMap);
    this.labelListName = new ArrayList<>();

  }

  /**
   * @param filePath - file path
   * @param plogger Iloggeradapter instance
   * @return - status of the rule validation
   */
  public String validateRules(final String filePath, final ILoggerAdapter plogger) {
    initializeObjects(plogger);
    ParserSSD parser = new ParserSSD(this.utilInst);
    try {
      setMessageDesc("");
      setSsdFileModel(parser.parseSSDFile(filePath, this.logger, this.rptInstance.getRptMsgInstance()));
      this.utilInst.setSsdFileInstance(getSsdFileModel());
      getSsdFileModel().setVarMap(this.utilInst.validateVarMap());

      Set<String> uniqueLabelNameSet = new HashSet<>();
      if (!getSsdFileModel().getVarMap().isEmpty()) {
        addAllVariableToMap(uniqueLabelNameSet);
      }

      readSSDStatements();

      readConstantValues();

      uniqueLabelNameSet.addAll(this.labelListName);
      // add all the labels from WCLMult Map
      for (List<A2LVariable> variables : getSsdFileModel().getWildCardMap().values()) {
        for (A2LVariable variable : variables) {
          uniqueLabelNameSet.add(variable.getLabelName());

        }
      }
      if (!uniqueLabelNameSet.isEmpty()) {
        // Fetch from DB the label Categories for each label.
        List<String> uniqueLabelNameList = new ArrayList<>(uniqueLabelNameSet);
        getLabelUtils().setLabelCategoryMap(getLabelCategory(uniqueLabelNameList));
        for (String uniqueLabel : uniqueLabelNameSet) {
          getLabelUtils().getCharacteristics(uniqueLabel, this.utilInst);
        }
      }

      formErrorMessage();

      List<SSDStatement> stmtList = this.ssdFileModel.getStatements();
      this.utilInst.setRptInstance(this.rptInstance);
      evaluateRules(stmtList);
      this.logger.info("Evaluation of SSD Rules has been completed");
      this.utilInst.lblRulesValidation(getSsdFileModel());

      for (SSDStatement stmt : this.utilInst.getSsdFileInstance().getStatements()) {
        getErrorInfo(stmt);
      }
      this.logger.info("Validation of SSD Rules has been completed");
      if ((getExistingErrors() != null) && !getExistingErrors().isEmpty()) {
        return getExistingErrors();
      }
      return null;// noerrors
    }
    catch (CheckSSDException cssdException) {
      return cssdException.getMessage();
    }
    catch (Exception e) {
      setMessageDesc("Unknown Exeption thrown : " + e.getMessage());
      this.logger.error(e.getMessage(), e);
      return e.getMessage();
    }
  }

  /**
   * @throws CheckSSDException
   */
  private void formErrorMessage() throws CheckSSDException {
    if ((getSsdFileModel() != null) && getSsdFileModel().getStatements().isEmpty()) {
      this.isError = true;
      String currentError = "No valid rule found. Please check the syntax !!";
      String errorMsgs = getInstance().getExistingErrors();
      if (!errorMsgs.contains(currentError)) {
        setMessageDesc(errorMsgs + currentError);
      }
      setMessageDesc(currentError);
      throw new CheckSSDException(currentError);
    }
  }

  /**
   *
   */
  private void readConstantValues() {
    for (List<Constant> constObj : getSsdFileModel().getConstants().values()) {
      for (Constant variable : constObj) {
        if (variable instanceof ConstValue) {
          handleConstValueStmt(variable);
        }
        else if (variable instanceof ConstText) {
          handleConstTextStmt(variable);
        }
      }
    }
  }

  /**
   *
   */
  private void readSSDStatements() {
    for (SSDStatement statement : getSsdFileModel().getStatements()) {
      if (statement instanceof A2LVariable) {
        A2LVariable valObj = (A2LVariable) statement;
        for (CheckRule rule : valObj.getRuleList()) {
          getLabelFromLablFunc(rule);
        }
      }
      else if (statement instanceof Control) {
        processCtrlStmtsubStmts(statement);
      }
    }
  }

  /**
   * @param uniqueLabelNameSet
   */
  private void addAllVariableToMap(final Set<String> uniqueLabelNameSet) {
    // Add all the variable map
    for (List<A2LVariable> variables : getSsdFileModel().getVarMap().values()) {
      for (A2LVariable variable : variables) {
        if (!uniqueLabelNameSet.contains(variable.getLabelName())) {
          uniqueLabelNameSet.add(variable.getLabelName());
        }
      }
    }
  }


  /**
   * @throws ParameterInvalidException
   * @throws ResourceNotFoundException
   */
  private Map<String, String> getLabelCategory(final List<String> labelList)
      throws ParameterInvalidException, ResourceNotFoundException {
    if ((labelList == null) || labelList.isEmpty()) {
      throw new ParameterInvalidException();
    }

    Map<String, String> labelWithCategory = new HashMap<>();
    int listSize = 1000;
    AtomicInteger counter = new AtomicInteger();

    // Partition the list with size of 1000
    Collection<List<String>> partitions =
        labelList.stream().collect(Collectors.groupingBy(index -> counter.getAndDecrement() / listSize)).values();

    // iterate the sublists and get the category for the labels
    for (List<String> subList : partitions) {
      Filter filter = new Filter();
      filter.setLabelName(subList);

      labelWithCategory.putAll(this.ssdFileEditorService.getLabelByType(filter));
    }


    if (!labelWithCategory.isEmpty()) {
      return labelWithCategory;
    }

    throw new ResourceNotFoundException();
  }


  /**
   * @param stmtList
   * @throws CheckSSDException
   */
  private void evaluateRules(final List<SSDStatement> stmtList) throws CheckSSDException {

    for (SSDStatement stmt : stmtList) {
      try {
        if (stmt instanceof Constant) {
          evaluateConstantstmts(stmt);
        }
        else if (stmt instanceof A2LVariable) {
          evalauteLabelStmts(stmt);
        }
        else if (stmt instanceof Control) {
          evaluateCtrlStatements(stmt);
        }
      }
      catch (Exception e) {
        throw new CheckSSDException(CheckSSDException.ERROR_DUE_TO_EVALUATION, e);
      }


    }

  }


  /**
   * @param stmt
   * @throws CheckSSDException
   */
  private void evalauteLabelStmts(final SSDStatement stmt) throws CheckSSDException {
    this.a2lData = new A2LData(this.a2lFileInfo);
    this.utilInst.setA2lData(this.a2lData);
    this.utilInst.setCalDataMap(this.calDataMap);

    getLabelFromLblStmt(stmt);

    EvaluateLabelRules evalLabelRule = new EvaluateLabelRules();

    evalLabelRule.evaluateRules(getSsdFileModel(), (A2LVariable) stmt, this.utilInst);
  }


  /**
   * @param stmt
   * @param labelName
   * @throws CheckSSDException
   */
  private void evaluateConstantstmts(final SSDStatement stmt) throws CheckSSDException {

    getLabelFromConst(stmt);

    EvaluateConstantRules evaluateConstantRule = new EvaluateConstantRules();
    this.utilInst.setForFlag(false);
    this.a2lData = new A2LData(this.a2lFileInfo);
    this.utilInst.setA2lData(this.a2lData);
    this.utilInst.setCalDataMap(this.calDataMap);
    evaluateConstantRule.evaluateRules(this.ssdFileModel, (Constant) stmt, false, this.utilInst);
  }


  /**
   * @param stmt
   * @throws CheckSSDException
   */
  private void evaluateCtrlStatements(final SSDStatement stmt) throws CheckSSDException {
    processCtrlStmtsubStmts(stmt);
    EvaluateControlRules evalControlRule = new EvaluateControlRules();
    this.utilInst.setForFlag(false);
    this.a2lData = new A2LData(this.a2lFileInfo);
    this.utilInst.setA2lData(this.a2lData);
    this.utilInst.setCalDataMap(this.calDataMap);
    evalControlRule.evaluateRules(this.ssdFileModel, (Control) stmt, this.utilInst);

    if (stmt instanceof CtrlFor) {
      this.utilInst.setForLooplevel(-1);
    }
  }


  /**
   * @param stmt stmt
   */
  public void getErrorInfo(final SSDStatement stmt) {
    if (stmt.getError() > 0) {
      this.isError = true;
    }
    if (stmt instanceof Constant) {
      handleConstStatement(stmt);
    }
    else if (stmt instanceof A2LVariable) {
      handleA2lVariableStmt(stmt);
    }
    else if (stmt instanceof Control) {
      handleControlStmt(stmt);
    }

  }


  /**
   * @param stmt
   */
  private void handleConstStatement(final SSDStatement stmt) {


    if (stmt instanceof ConstValue) {
      handleConstValStmt(stmt);
    }
    else {
      handleArrayMatrixConstant(stmt);
    }


  }


  /**
   * @param stmt
   */
  private void handleA2lVariableStmt(final SSDStatement stmt) {
    this.nplNameList.clear();
    String functionName = "";
    A2LVariable a2lVariableInst = ((A2LVariable) stmt);
    String labelName = "";
    String labelType = "";
    if (a2lVariableInst.getA2lCharacteristic() != null) {
      labelName = a2lVariableInst.getA2lCharacteristic().getName();
      labelType = a2lVariableInst.getA2lCharacteristic().getType();
    }
    List<CheckRule> ruleList = a2lVariableInst.getRuleList();
    this.nplNameList.addAll(a2lVariableInst.getSsdModelUtils().getRptInstance().getNplNameList());
    if (a2lVariableInst.isForFlag() && !ruleList.isEmpty()) {
      CheckRule rule = ruleList.get(0);
      ruleList.clear();
      ruleList.add(rule);
    }
    if (!ruleList.isEmpty()) {
      for (CheckRule rule : ruleList) {
        int errorNumber = 0;
        checkErrorInStatement(stmt, a2lVariableInst, labelName, labelType, rule, errorNumber);
      }
    }

  }

  /**
   * @param stmt
   * @param a2lVariableInst
   * @param labelName
   * @param labelType
   * @param rule
   * @param errorNumber
   */
  private void checkErrorInStatement(final SSDStatement stmt, final A2LVariable a2lVariableInst, final String labelName,
      final String labelType, final CheckRule rule, int errorNumber) {
    String functionName;
    if (rule.getError() > 0) {
      errorNumber = rule.getError();
      this.isError = true;
    }
    else if (a2lVariableInst.getRuleList().get(0).getError() > 0) {
      errorNumber = a2lVariableInst.getRuleList().get(0).getError();
      this.isError = true;
    }

    functionName = ValidateRuleUtil.getLabelRuleKeyWord(rule.getRuleType());
    if (((A2LVariable) stmt).getA2lCharacteristic() == null) {
      ((A2LVariable) stmt).setError(ReportMessages.P_TABTYPE);
    }

    if (!printLblFuncErrors(functionName, errorNumber, labelName, labelType, stmt.getLineNo())) {
      printConstFuncErrors(functionName, errorNumber, labelName, labelType, stmt.getLineNo());
    }
  }


  /**
   * @param stmt
   */
  private void handleConstValStmt(final SSDStatement stmt) {
    MathTerm statement = ((ConstValue) stmt).getMathTerm();
    List<IMathTermElement> constFunction = statement.getMathElement();
    this.nplNameList.clear();
    this.nplNameList.addAll(statement.getSsdModelUtils().getRptInstance().getNplNameList());
    if (!constFunction.isEmpty()) {
      if ((stmt.getError() > 0) ||
          ((constFunction.get(0) instanceof ConstFunctions) ||
              (statement.getMathElement().get(0) instanceof ConstFunctions)) ||
          (constFunction.get(0) instanceof MathFunction)) {
        handleSSDStringAndConstantFuncErrors(stmt, constFunction);
      }
      else if (!statement.getSsdModelUtils().getRptInstance().getValueModelList().isEmpty()) {
        for (ReportValueModel model : statement.getSsdModelUtils().getRptInstance().getValueModelList()) {
          if ((model.getErrCode() > 0) && (constFunction.get(0) instanceof ConstFunctions)) {
            this.isError = true;
            handleConstantFuncErrors(stmt, constFunction, model.getErrCode());
          }
        }
      }
    }
  }

  /**
   * @param stmt
   * @param constFunction
   */
  private void handleSSDStringAndConstantFuncErrors(final SSDStatement stmt, List<IMathTermElement> constFunction) {
    if (constFunction.get(0) instanceof SSDString) {
      handleSSDStringType(stmt, constFunction);
    }
    else if (constFunction.get(0) instanceof MathFunction) {
      int errNo = 0;
      if (stmt.getError() > 0) {
        errNo = stmt.getError();
      }
      printMathFuncErrors(((MathFunction) constFunction.get(0)).getFunction().name(), constFunction.get(0),
          stmt.getLineNo(), errNo);
    }
    else if ((constFunction instanceof ConstFunctions) || (constFunction.get(0) instanceof ConstFunctions)) {
      handleConstantFuncErrors(stmt, constFunction, ((ConstFunctions) constFunction.get(0)).getErr());
    }
  }


  /**
   * @param stmt
   * @param mathFunc
   */
  private void handleSSDStringType(final SSDStatement stmt, final List<IMathTermElement> mathFunc) {
    this.isError = true;
    if (stmt.getError() == 3) {
      String error = getExistingErrors();
      if (((ConstValue) stmt).getConstFunc() == null) {
        setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getLineNo() + ':' +
            " Label / Constant used is not appropriate ");
        getLineNoAndError().put(stmt.getLineNo(), " Label / Constant used is not appropriate ");
      }
      else {
        setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getLineNo() + ':' +
            ValidationConstants.IMPROPER_SYNTAX_FOUND + "for " + mathFunc.get(0) + ValidationConstants.PROPER_SYNTAX +
            mathFunc.get(0) + ':');
        getLineNoAndError().put(stmt.getLineNo(), ValidationConstants.IMPROPER_SYNTAX_FOUND + "for " + mathFunc.get(0) +
            ValidationConstants.PROPER_SYNTAX + mathFunc.get(0) + ':');
      }
    }
    printMathFuncErrors(null, mathFunc.get(0), stmt.getLineNo(), 0);
  }

  /**
   * @param stmt
   * @param mathFunc
   * @param errorCode
   */
  private void handleConstantFuncErrors(final SSDStatement stmt, final List<IMathTermElement> mathFunc,
      final int errorCode) {

    if (errorCode > 0) {
      this.isError = true;
      String lblName = "";
      String lblType = "";
      String functionName = "";
      ConstantFunctionsList constantFunctionList = new ConstantFunctionsList();
      functionName = constantFunctionList.getFunctionName(mathFunc.get(0));
      IMathTermElement iMathTermElement = ((ConstValue) stmt).getMathTerm().getMathElement().get(0);

      Map<String, String> lblAndTypeMap = getLabelNameAndType(iMathTermElement);
      if (lblAndTypeMap.size() == 1) {
        lblName = (String) lblAndTypeMap.keySet().toArray()[0];
        lblType = lblAndTypeMap.get(lblName);
      }
      if (functionName != null) {
        boolean printMsgSuccess = printConstFuncErrors(functionName, errorCode, lblName, lblType, stmt.getLineNo());
        if (!printMsgSuccess) {
          printLblFuncErrors(functionName, errorCode, lblName, lblType, stmt.getLineNo());
        }

      }

    }
  }

  /**
   * @param stmt
   */
  private void processCtrlStmtsubStmts(final SSDStatement stmt) {

    if (stmt instanceof CtrlFor) {
      processForStatements(stmt);
    }
    else if ((stmt instanceof CtrlFidCheck) && getInstance().getUtilInst().isSaveSSDSwitch()) {
      getInstance().getUtilInst().getTmpFileInstance().createFidConvWriters();
    }
    if (stmt instanceof CtrlIf) {
      processIFStatements(stmt);
    }
    if ((stmt instanceof UseCaseContactInfo) && (((UseCaseContactInfo) stmt).getStmtList() != null)) {
      for (SSDStatement substmt : ((UseCaseContactInfo) stmt).getStmtList()) {
        processSubStmts(substmt);
      }
    }
    if ((stmt instanceof CtrlLabel) && (((CtrlLabel) stmt).getSsdStmtList() != null)) {
      for (SSDStatement substmt : ((CtrlLabel) stmt).getSsdStmtList()) {
        processSubStmts(substmt);
      }
    }
  }


  /**
   * @param stmt
   */
  private void processIFStatements(final SSDStatement stmt) {
    if (((CtrlIf) stmt).getCondTerm() != null) {
      getLabelNameFromFunc(((CtrlIf) stmt).getCondTerm().getMathElement());
    }
    if (((CtrlIf) stmt).getIfStatements() != null) {
      for (SSDStatement stmts : ((CtrlIf) stmt).getIfStatements()) {
        processSubStmts(stmts);
      }
      if (((CtrlIf) stmt).getElseStatements() != null) {
        for (SSDStatement stmts : ((CtrlIf) stmt).getElseStatements()) {
          processSubStmts(stmts);
        }
      }
    }
  }


  /**
   * @param stmt
   */
  private void processForStatements(final SSDStatement stmt) {
    getInstance().getUtilInst().setForLooplevel(0);
    if (getInstance().getUtilInst().isSaveSSDSwitch()) {
      getInstance().getUtilInst().getTmpFileInstance().createForLoopWriters();
    }
    if ((((CtrlFor) stmt).getLimitList() != null)) {
      List<MathTerm> limitList = ((CtrlFor) stmt).getLimitList();
      for (MathTerm term : limitList) {
        if (term != null) {
          getLabelNameFromFunc(term.getMathElement());
        }
      }
    }
    if (((CtrlFor) stmt).getStatements() != null) {
      List<SSDStatement> forStatements = new ArrayList<>();
      if (((CtrlFor) stmt).getStatements() != null) {
        forStatements.addAll(((CtrlFor) stmt).getStatements());
      }
      if (((CtrlFor) stmt).getReplaceSSDStmtList() != null) {
        forStatements.addAll(((CtrlFor) stmt).getReplaceSSDStmtList());
      }
      for (SSDStatement stmts : forStatements) {
        processSubStmts(stmts);
      }
    }
  }

  /**
   * @param stmts
   */
  private void processSubStmts(final SSDStatement stmts) {
    if (stmts instanceof Constant) {
      getLabelFromConst(stmts);
    }
    else if (stmts instanceof A2LVariable) {
      getLabelFromLblStmt(stmts);
    }
    else {
      processCtrlStmtsubStmts(stmts);
    }
  }

  /**
   * @param stmt
   */
  private void getLabelFromLblStmt(final SSDStatement stmt) {
    A2LVariable valObj = (A2LVariable) stmt;
    String labelName = valObj.getLabelName();
    if (!getInstance().getLabelUtils().getA2lFileInfo().getModuleMap().get("DIM").getCharacteristicsMap().isEmpty()) {
      ((A2LVariable) stmt)
          .setA2lCharacteristic(getInstance().getLabelUtils().getCharacteristics(labelName, this.utilInst));
      if (this.calDataMap.containsKey(labelName)) {
        ((A2LVariable) stmt).setCalDataPhy(this.calDataMap.get(labelName).getCalDataPhy());
      }
    }
    for (CheckRule rule : valObj.getRuleList()) {
      getLabelFromLablFunc(rule);
    }
  }


  /**
   * @return the secondMessage
   */
  public String getExistingErrors() {
    if (this.existingErrors == null) {
      this.existingErrors = "";
    }
    return this.existingErrors;
  }


  /**
   * @param msg the secondMessage to set
   */
  public void setExistingErrors(final String msg) {
    String message = msg;
    if (message == null) {
      message = "";
    }
    this.existingErrors = message;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ValidateRuleInvokerService getInstance() {

    return this;
  }


  /**
   * @return the lineNoAndError
   */
  @Override
  public Map<Integer, String> getLineNoAndError() {
    return this.lineNoAndError;
  }


  /**
   * @param lineNoAndError the lineNoAndError to set
   */
  @Override
  public void setLineNoAndError(final Map<Integer, String> lineNoAndError) {
    this.lineNoAndError = lineNoAndError;
  }


  /**
   * @param name name
   */
  public void addLabelListName(final String name) {
    if (this.labelListName == null) {
      this.labelListName = new ArrayList<>();
    }
    this.labelListName.add(name);
  }


  /**
   * @param labelListName the labelListName to set
   */
  public void setLabelListName(final List<String> labelListName) {
    this.labelListName = new ArrayList<>(labelListName);
  }


  /**
   * @return the isError
   */
  public boolean isError() {
    return this.isError;
  }


  /**
   * @param isError the isError to set
   */
  public void setError(final boolean isError) {
    this.isError = isError;
  }


  /**
   * @return the utilInst
   */
  public SSDModelUtils getUtilInst() {
    return this.utilInst;
  }


  /**
   * @return the labelUtils
   */
  public LabelCreationUtils getLabelUtils() {
    return this.labelUtils;
  }


  /**
   * @param labelUtils the labelUtils to set
   */
  public void setLabelUtils(final LabelCreationUtils labelUtils) {
    this.labelUtils = labelUtils;
  }


  /**
   * @return the ssdFileModel
   */
  public SSDFile getSsdFileModel() {
    return this.ssdFileModel;
  }


  /**
   * @param ssdFileModel the ssdFileModel to set
   */
  public void setSsdFileModel(final SSDFile ssdFileModel) {
    this.ssdFileModel = ssdFileModel;
  }


}

