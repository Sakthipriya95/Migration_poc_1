/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messagesinterfaces;

import java.util.List;

import com.bosch.boot.ssd.api.service.ValidateRuleInvokerService;
import com.bosch.checkssd.datamodel.SSDStatement;
import com.bosch.checkssd.datamodel.a2lvariable.A2LVariable;
import com.bosch.checkssd.datamodel.constant.ConstText;
import com.bosch.checkssd.datamodel.constant.ConstValue;
import com.bosch.checkssd.datamodel.constant.Constant;
import com.bosch.checkssd.datamodel.constantfunctions.AxsvalFunc;
import com.bosch.checkssd.datamodel.constantfunctions.CaseFunc;
import com.bosch.checkssd.datamodel.constantfunctions.FIncrDecrFunc;
import com.bosch.checkssd.datamodel.constantfunctions.IFFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MPMaxMinFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MaxAxsPtFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MaxMinFunc;
import com.bosch.checkssd.datamodel.constantfunctions.NumFunc;
import com.bosch.checkssd.datamodel.constantfunctions.SearchFctFunction;
import com.bosch.checkssd.datamodel.constantfunctions.SrcFunc;
import com.bosch.checkssd.datamodel.constantfunctions.VblkFunc;
import com.bosch.checkssd.datamodel.constantfunctions.VersionFunction;
import com.bosch.checkssd.datamodel.mathterm.ConstFunctions;
import com.bosch.checkssd.datamodel.mathterm.IMathTermElement;
import com.bosch.checkssd.datamodel.mathterm.MathFunction;
import com.bosch.checkssd.datamodel.mathterm.MathTerm;
import com.bosch.checkssd.datamodel.mathterm.SSDString;
import com.bosch.checkssd.datamodel.rule.AsciicountRule;
import com.bosch.checkssd.datamodel.rule.AxsCmpRule;
import com.bosch.checkssd.datamodel.rule.BitRule;
import com.bosch.checkssd.datamodel.rule.BitVBLKRule;
import com.bosch.checkssd.datamodel.rule.CaseRule;
import com.bosch.checkssd.datamodel.rule.CheckRule;
import com.bosch.checkssd.datamodel.rule.DinhRule;
import com.bosch.checkssd.datamodel.rule.FIncrDecrRule;
import com.bosch.checkssd.datamodel.rule.IFRule;
import com.bosch.checkssd.datamodel.rule.MapCmpRule;
import com.bosch.checkssd.datamodel.rule.VblkRule;

/**
 * LabelNameExtraction
 *
 * @author SMN6KOR
 */
public interface LabelNameExtraction {

  /**
   * @return - instance
   */
  ValidateRuleInvokerService getInstance();


  /**
   * @param constObj
   */
  default void handleConstTextStmt(final Constant constObj) {
    ConstFunctions constFunc = ((ConstText) constObj).getConstFunc();

    if (constFunc != null) {
      ConstFunctions func = constFunc;

      if (func instanceof VblkFunc) {
        List<IMathTermElement> varList = ((VblkFunc) func).getVariable().getMathElement();
        getLabelNameFromFunc(varList);
      }
      else if (func instanceof MPMaxMinFunc) {
        // to be handled
      }
      else if (func instanceof SrcFunc) {
        List<IMathTermElement> varList = ((SrcFunc) func).getVariable().getMathElement();
        getLabelNameFromFunc(varList);
      }
      else if (func instanceof VersionFunction) {
        // to be handled
      }
      else if (func instanceof SearchFctFunction) {
        // to be handled
      }
    }
  }

  /**
   * @param constObj
   */
  default void handleConstValueStmt(final Constant constObj) {
    ConstValue valObj = (ConstValue) constObj;
    MathTerm term = valObj.getMathTerm();
    if (term != null) {
      List<IMathTermElement> mathElement = term.getMathElement();
      int lastElement = mathElement.size() - 1;
      if (mathElement.get(lastElement) instanceof SSDString) {
        SSDString str = (SSDString) mathElement.get(lastElement);
        if (str.getStrVal().equals(",")) {
          mathElement.remove(lastElement);
        }
        getLabelFromConstFunc(mathElement);
      }
      MathTerm tmpTerm = getInstance().getUtilInst().setNewTermData(term);
      List<IMathTermElement> tmpMathElement = tmpTerm.getMathElement();
      if (tmpMathElement.get(0) instanceof MathFunction) {
        getLabelInfoFromMathFunc(tmpMathElement);
      }
      else if (mathElement.get(0) instanceof ConstFunctions) {
        getLabelFromConstFunc(mathElement);
      }
    }
  }

  /**
   * @param mathElement
   */
  default void getLabelFromConstFunc(final List<IMathTermElement> mathElement) {
    if (mathElement.get(0) instanceof ConstFunctions) {
      ConstFunctions mathFunc = ((ConstFunctions) mathElement.get(0));
      if (mathFunc instanceof IFFunc) {
        ifuncLabelExtration(mathFunc);
      }
      else if ((mathFunc instanceof CaseFunc) && (((CaseFunc) mathFunc).getCheckVar() != null)) {
        List<IMathTermElement> variable = ((CaseFunc) mathFunc).getCheckVar().getMathElement();
        getLabelNameFromFunc(variable);
      }
      else if ((mathFunc instanceof NumFunc) && (((NumFunc) mathFunc).getVariable() != null) &&
          !((NumFunc) mathFunc).getVariable().getMathElement().isEmpty()) {
        List<IMathTermElement> variable = ((NumFunc) mathFunc).getVariable().getMathElement();
        getLabelNameFromFunc(variable);
      }
      else if ((mathFunc instanceof MaxAxsPtFunc) && (((MaxAxsPtFunc) mathFunc).getA2lVarTrm() != null) &&
          !((MaxAxsPtFunc) mathFunc).getA2lVarTrm().getMathElement().isEmpty()) {
        List<IMathTermElement> variable = ((MaxAxsPtFunc) mathFunc).getA2lVarTrm().getMathElement();
        getLabelNameFromFunc(variable);
      }
      else {
        checkOtherConstFuncForLabelName(mathFunc);
      }

    }
    else if (mathElement.get(0) instanceof SSDString) {
      getLabelNameFromFunc(mathElement);
    }
  }

  /**
   * @param mathElement
   */
  default void checkOtherConstFuncForLabelName(final ConstFunctions mathFunc) {
    if ((mathFunc instanceof FIncrDecrFunc)) {
      getLblFromFincrDecrFunc(mathFunc);
    }
    else if ((mathFunc instanceof SrcFunc) && (((SrcFunc) mathFunc).getVariable() != null)) {
      List<IMathTermElement> varList = ((SrcFunc) mathFunc).getVariable().getMathElement();
      getLabelNameFromFunc(varList);
    }
    else if ((mathFunc instanceof AxsvalFunc) && (((AxsvalFunc) mathFunc).getVariable() != null)) {
      List<IMathTermElement> varList = ((AxsvalFunc) mathFunc).getVariable().getMathElement();
      getLabelNameFromFunc(varList);
    }
    else if ((mathFunc instanceof VblkFunc) && (((VblkFunc) mathFunc).getVariable() != null)) {
      List<IMathTermElement> varList = ((VblkFunc) mathFunc).getVariable().getMathElement();
      getLabelNameFromFunc(varList);
    }
    else if ((mathFunc instanceof MPMaxMinFunc) && (((MPMaxMinFunc) mathFunc).getVariable() != null)) {
      List<IMathTermElement> varList = ((MPMaxMinFunc) mathFunc).getVariable().getMathElement();
      getLabelNameFromFunc(varList);
    }
    else if ((mathFunc instanceof MaxMinFunc) && (((MaxMinFunc) mathFunc).getTermsList() != null)) {
      List<MathTerm> variable = ((MaxMinFunc) mathFunc).getTermsList();
      for (MathTerm mathTerm : variable) {
        getLabelNameFromFunc(mathTerm.getMathElement());
      }
    }
  }


  /**
   * @param mathFunc
   */
  default void getLblFromFincrDecrFunc(final ConstFunctions mathFunc) {
    if (((FIncrDecrFunc) mathFunc).getTerm() != null) {
      List<IMathTermElement> termList = ((FIncrDecrFunc) mathFunc).getTerm().getMathElement();
      getLabelNameFromFunc(termList);
    }
    if (((FIncrDecrFunc) mathFunc).getVar() != null) {
      List<IMathTermElement> varList = ((FIncrDecrFunc) mathFunc).getVar().getMathElement();
      getLabelNameFromFunc(varList);
    }

  }

  /**
   * @param rule
   */
  default void getLabelFromLablFunc(final CheckRule rule) {
    if (rule instanceof IFRule) {
      getLblFromIFRule(rule);
    }
    else if (rule instanceof CaseRule) {
      getLabelFromCaseRule(rule);
    }
    else if (rule instanceof FIncrDecrRule) {
      getLabelFromFincrDecrRule(rule);
    }
    else if ((rule instanceof AxsCmpRule) && (((AxsCmpRule) rule).getAxsPtCmpTerm() != null)) {
      getLabelNameFromFunc(((AxsCmpRule) rule).getAxsPtCmpTerm().getMathElement());
    }
    else if ((rule instanceof MapCmpRule) && (((MapCmpRule) rule).getzCompterm() != null)) {
      getLabelNameFromFunc(((MapCmpRule) rule).getzCompterm().getMathElement());
    }
    else if ((rule instanceof DinhRule) && (((DinhRule) rule).getA2lVar() != null)) {
      getLabelNameFromFunc(((DinhRule) rule).getA2lVar().getMathElement());
    }
    else {
      checkOtherFuncForLabelName(rule);
    }


  }

  /**
   * @param rule
   */
  default void checkOtherFuncForLabelName(final CheckRule rule) {
    if ((rule instanceof VblkRule) && (((VblkRule) rule).getCompTerm().get(0) != null)) {
      for (MathTerm mathterm : ((VblkRule) rule).getCompTerm().get(0)) {
        getLabelNameFromFunc(mathterm.getMathElement());
      }
    }
    else if ((rule instanceof BitVBLKRule) && (((BitVBLKRule) rule).getIndexAndBitValList().get(0) != null)) {
      for (MathTerm mathterm : ((BitVBLKRule) rule).getIndexAndBitValList().get(0)) {
        getLabelNameFromFunc(mathterm.getMathElement());
      }
    }
    else if ((rule instanceof AsciicountRule) && (((AsciicountRule) rule).getA2LVariable() != null)) {
      A2LVariable valObj = ((AsciicountRule) rule).getA2LVariable();
      getInstance().addLabelListName(valObj.getLabelName());
    }
    else if ((rule instanceof BitRule) && (((BitRule) rule).getIndexAndBitValList().get(0) != null)) {
      for (MathTerm mathterm : ((BitRule) rule).getIndexAndBitValList().get(0)) {
        getLabelNameFromFunc(mathterm.getMathElement());
      }
    }

  }


  /**
   * @param rule
   */
  default void getLblFromIFRule(final CheckRule rule) {

    IFRule ifRule = (IFRule) rule;
    if ((ifRule.getCompTrem() != null) && !ifRule.getCompTrem().getMathElement().isEmpty()) {
      getLabelNameFromFunc(ifRule.getCompTrem().getMathElement());
    }
    if ((ifRule.getFalseCond() != null) && !ifRule.getFalseCond().getMathElement().isEmpty()) {
      getLabelNameFromFunc(ifRule.getCompTrem().getMathElement());
    }

  }

  /**
   * @param rule
   */
  default void getLabelFromFincrDecrRule(final CheckRule rule) {
    if (((FIncrDecrRule) rule).getTerm() != null) {
      getLabelNameFromFunc(((FIncrDecrRule) rule).getTerm().getMathElement());
    }
    if (((FIncrDecrRule) rule).getVar() != null) {
      getLabelNameFromFunc(((FIncrDecrRule) rule).getVar().getMathElement());
    }

  }

  /**
   * @param rule
   */
  default void getLabelFromCaseRule(final CheckRule rule) {
    if ((((CaseRule) rule).getCheckVar() != null) && !((CaseRule) rule).getCheckVar().getMathElement().isEmpty()) {
      getLabelNameFromFunc(((CaseRule) rule).getCheckVar().getMathElement());
    }
    if (((CaseRule) rule).getCompareAssignVarlist() != null) {

      for (int i = 0; i < ((CaseRule) rule).getCompareAssignVarlist().size(); i++) {
        if (((CaseRule) rule).getCompareAssignVarlist().get(i) != null) {
          getLabelfromFunc(rule, i);
        }
      }
    }

  }


  /**
   * @param rule
   * @param i
   */
  default void getLabelfromFunc(final CheckRule rule, final int i) {
    for (int j = 0; j < ((CaseRule) rule).getCompareAssignVarlist().get(i).size(); j++) {

      if (((CaseRule) rule).getCompareAssignVarlist().get(i).get(j) != null) {
        getLabelNameFromFunc(((CaseRule) rule).getCompareAssignVarlist().get(i).get(j).getMathElement());
      }

    }
  }

  /**
   * @param variable
   */
  default void getLabelNameFromFunc(final List<IMathTermElement> variable) {
    if ((variable != null) && !variable.isEmpty()) {
      for (IMathTermElement element : variable) {
        if ((element instanceof SSDString)) {
          getInstance().addLabelListName(element.getMathTermElement());
        }
      }
    }
  }

  /**
   * @param tmpMathElement
   */
  default void getLabelInfoFromMathFunc(final List<IMathTermElement> tmpMathElement) {
    MathTerm mathFunc = ((MathFunction) tmpMathElement.get(0)).getParameter();
    if (mathFunc != null) {
      List<IMathTermElement> parameters = ((MathFunction) tmpMathElement.get(0)).getParameter().getMathElement();
      getLabelNameFromFunc(parameters);
    }
    if (((MathFunction) tmpMathElement.get(0)).getBinParam() != null) {
      List<IMathTermElement> binparameters = ((MathFunction) tmpMathElement.get(0)).getBinParam().getMathElement();
      getLabelNameFromFunc(binparameters);
    }
  }


  /**
   * @param mathFunc
   */
  default void ifuncLabelExtration(final ConstFunctions mathFunc) {

    if (((IFFunc) mathFunc).getCondition() != null) {
      for (IMathTermElement termElement : ((IFFunc) mathFunc).getCondition().getMathElement()) {
        if (termElement instanceof SSDString) {
          getInstance().addLabelListName(termElement.getMathTermElement());
        }
      }
    }
    if (((IFFunc) mathFunc).getTrueTerm() != null) {
      for (IMathTermElement termElement : ((IFFunc) mathFunc).getTrueTerm().getMathElement()) {
        if (termElement instanceof SSDString) {
          getInstance().addLabelListName(termElement.getMathTermElement());
        }
      }
    }
  }


  /**
   * @param stmt
   */
  default void getLabelFromConst(final SSDStatement stmt) {
    Constant constObj = (Constant) stmt;
    if (constObj instanceof ConstValue) {
      handleConstValueStmt(constObj);
    }
    else if (constObj instanceof ConstText) {
      handleConstTextStmt(constObj);
    }
  }

}