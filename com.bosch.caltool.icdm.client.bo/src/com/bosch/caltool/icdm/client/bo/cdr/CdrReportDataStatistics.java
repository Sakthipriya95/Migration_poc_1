/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;

/**
 * @author dja7cob
 */
public class CdrReportDataStatistics {

  // ICDM-2170 ; ICDM-2329
  /**
   * CdrReportData
   */
  private final CdrReportDataHandler cdrReportData;


  private final Map<Long, A2LParameter> allParamMap = new HashMap<>();

  /**
   * @param cdrReportData CDR report data
   */
  public CdrReportDataStatistics(final CdrReportDataHandler cdrReportData) {
    this.cdrReportData = cdrReportData;
  }

  /**
   * @param selection Selection in Navigator view
   * @return review details array. element[0] - reviewedCount, element[1] - paramCount
   */
  public int[] getStatistics(final Object selection) {

    Set<String> paramSet;

    // Check if selected node is a Function
    if (selection instanceof Function) {
      paramSet = getParamListfromFunction((Function) selection);
    }
    // Check if selected node is a Base Component
    else if (selection instanceof A2LBaseComponents) {
      paramSet = getParamListFromA2lBc(selection);
    }
    else if (selection instanceof A2LBaseComponentFunctions) {
      paramSet = getParamListfromBcFc((A2LBaseComponentFunctions) selection);
    }
    // Check if selected node is a Parameter
    else if (selection instanceof A2LParameter) {
      paramSet = getParamListFromParam(selection);
    }
    else if (selection instanceof CompPackage) {
      paramSet = getParamSetCompPkg(selection);
    }
    else if (selection instanceof A2LGroup) {
      paramSet = getParamListFromA2lGrp(selection);
    }
    else if (selection instanceof A2lWpObj) {
      paramSet = getParamListFromWp(selection);
    }
    else if (selection instanceof A2lWorkPackageGroup) {
      paramSet = getParamListFromWpGrp(selection);
    }
    else if (selection instanceof A2lWpResponsibility) {
      paramSet = getParamListFromRespPal(selection);
    }
    else if (selection instanceof String) {
      paramSet = getParamListFromString(selection);
    }
    else if (selection instanceof ParamWpResponsibility) {
      paramSet = getParamListFromParamWpResp(selection);
    }
    else {
      paramSet = getAllA2lParamNames();
    }

    return getReviewDetails(paramSet);
  }

  /**
   * @param selection
   * @return
   */
  private Set<String> getParamListFromParamWpResp(final Object selection) {
    ParamWpResponsibility paramWpResp = (ParamWpResponsibility) selection;
    Set<String> paramSet = new HashSet<>();
    fillAllParamMap();
    Set<Long> paramIdSet =
        this.cdrReportData.getWpRespParamMap().get(paramWpResp.getWpId()).get(paramWpResp.getRespId());
    if (null != paramIdSet) {
      for (Long paramId : paramIdSet) {
        paramSet.add(this.allParamMap.get(paramId).getName());
      }
    }
    return paramSet;
  }

  /**
   * @param selection
   * @return
   */
  private Set<String> getParamListFromRespPal(final Object selection) {
    // Workpackages are shown using a2lWpResponsibility Object under their responsibilities under the node "RESP"
    A2lWpResponsibility selectedRespPal = (A2lWpResponsibility) selection;
    Set<String> paramSet = new HashSet<>();
    fillAllParamMap();

    for (A2LParameter a2lParam : this.allParamMap.values()) {
      if (this.cdrReportData.getParamIdWithWpAndRespMap().containsKey(a2lParam.getParamId()) &&
          this.cdrReportData.getParamIdWithWpAndRespMap().get(a2lParam.getParamId())
              .containsKey(selectedRespPal.getA2lWpId()) &&
          (selectedRespPal.getMappedWpRespName() != null) && this.cdrReportData.getParamIdWithWpAndRespMap()
              .get(a2lParam.getParamId()).containsValue(selectedRespPal.getMappedWpRespName())) {
        paramSet.add(a2lParam.getName());
      }
    }
    return paramSet;
  }

  /**
   *
   */
  private void fillAllParamMap() {
    if (this.allParamMap.isEmpty()) {
      for (A2LParameter a2lParam : a2lFileInfoBO().getA2lParamMap(null).values()) {
        this.allParamMap.put(a2lParam.getParamId(), a2lParam);
      }
    }
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamListFromString(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected string
    // FC,WP,BC
    // All parameters will be considered
    String selString = (String) navigatorSel;
    Set<String> paramSet;
    Set<A2lWpResponsibility> wpSet = this.cdrReportData.getA2lEditorDataProvider().getA2lWpInfoBO().getA2lWpDefnModel()
        .getA2lWpRespNodeMergedMap().get(selString);

    if (checkIfOutlineRootNode(selString)) {
      paramSet = getParamListAllFunctions();
    }
    else if (!CommonUtils.isNullOrEmpty(wpSet)) {
      // Workpackages are displayed as string under "Workpackges" node in outline page
      paramSet = getParamListFromRespPalAsString(wpSet);
    }
    else if (ApicConstants.UNASSIGNED_PARAM.equalsIgnoreCase(selString)) {
      paramSet = getUnassignedParams();
    }
    else {
      paramSet = filterBasedOnResponsible(selString);
    }

    return paramSet;
  }

  /**
   * @param selString
   * @param wpSet
   * @return
   */
  private Set<String> getParamListFromRespPalAsString(final Set<A2lWpResponsibility> wpSet) {
    Set<String> paramSet = new HashSet<>();
    fillAllParamMap();
    for (A2lWpResponsibility wpRespPal : wpSet) {
      Set<Long> paramIdSet = new HashSet<>();
      for (Set<Long> idSet : this.cdrReportData.getWpRespParamMap().get(wpRespPal.getA2lWpId()).values()) {
        paramIdSet.addAll(idSet);
      }
      if (!paramIdSet.isEmpty()) {
        for (Long paramId : paramIdSet) {
          paramSet.add(this.allParamMap.get(paramId).getName());
        }
      }
    }
    return paramSet;
  }

  /**
   * @param selString
   * @return
   */
  private boolean checkIfOutlineRootNode(final String selString) {
    return isBcOrFc(selString) || isA2lWpOrCompOrWpResp(selString);
  }

  /**
   * @param selString
   * @return
   */
  private boolean isA2lWpOrCompOrWpResp(final String selString) {
    return selString.equalsIgnoreCase(ApicConstants.A2L_WORK_PKG) || selString.equalsIgnoreCase(ApicConstants.COMP) ||
        selString.equalsIgnoreCase(ApicConstants.WP_RESPONSIBILITY);
  }

  /**
   * @param selString
   * @return
   */
  private boolean isBcOrFc(final String selString) {
    return selString.equalsIgnoreCase(ApicConstants.BC_CONST) || selString.equalsIgnoreCase(ApicConstants.FC_CONST);
  }

  /**
   * @param selString
   * @return
   */
  private Set<String> filterBasedOnResponsible(final String selString) {
    Set<String> paramSet = new HashSet<>();
    fillAllParamMap();
    for (Entry<Long, Map<Long, String>> entry : this.cdrReportData.getParamIdWithWpAndRespMap().entrySet()) {
      Map<Long, String> respMap = entry.getValue();
      for (String respName : respMap.values()) {
        if (selString.equals(respName)) {
          paramSet.add(this.allParamMap.get(entry.getKey()).getName());
        }
      }
    }
    return paramSet;
  }

  /**
   * @return un assigned parameters
   */
  private Set<String> getUnassignedParams() {
    return a2lFileInfoBO().getUnassignedParams().stream().map(A2LParameter::getName).collect(Collectors.toSet());
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamListFromWpGrp(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected WP group
    final A2lWorkPackageGroup wpGrp = (A2lWorkPackageGroup) navigatorSel;
    List<A2lWpObj> wpList = new ArrayList<>();
    List<String> funcList = new ArrayList<>();
    for (Long key : wpGrp.getWorkPackageMap().values()) {
      wpList.add(a2lFileInfoBO().getA2lWpMapping().getWpMap().get(key));
    }
    for (A2lWpObj wp : wpList) {
      funcList.addAll(wp.getFunctionMap().values());
    }

    return getParamListWpFunc(funcList);
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamListFromWp(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected work package
    final A2lWpObj wrkPckg = (A2lWpObj) navigatorSel;
    List<String> funcList = new ArrayList<>(wrkPckg.getFunctionMap().values());
    return getParamListWpFunc(funcList);
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamListFromA2lGrp(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected A2L group
    final A2LGroup a2lGrp = (A2LGroup) navigatorSel;
    return new HashSet<>(a2lGrp.getLabelMap().values());
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamSetCompPkg(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected component package
    final CompPackage compPkg = (CompPackage) navigatorSel;
    Set<Function> cpFunctionSet =
        this.cdrReportData.getA2lEditorDataProvider().getA2lFileInfoBO().getCompPkgFunctions(compPkg.getId());

    return getParamListfromFunction(cpFunctionSet);
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamListFromParam(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected parameter
    final A2LParameter a2lParameter = (A2LParameter) navigatorSel;
    return new HashSet<>(Arrays.asList(a2lParameter.getName()));
  }

  /**
   * @param navigatorSel
   * @return
   */
  private Set<String> getParamListFromA2lBc(final Object navigatorSel) {
    // Get the reviewed parameters count for the selected A2L Base component
    final A2LBaseComponents baseComp = (A2LBaseComponents) navigatorSel;
    SortedSet<A2LBaseComponentFunctions> bcFuncList = baseComp.getFunctionsList();
    return getParamListfromA2lBcFuncs(bcFuncList);
  }

  /**
   * @return
   */
  private Set<String> getParamListWpFunc(final List<String> funcList) {
    return getParamListfromFunction(funcList);
  }

  /**
   * @return
   */
  private Set<String> getParamListAllFunctions() {
    return getParamListfromFunction(a2lFileInfoBO().getAllFunctionMap().values());
  }

  /**
   * @param a2lBcFc
   * @return
   */
  private Set<String> getParamListfromBcFc(final A2LBaseComponentFunctions a2lBcFc) {
    return getParamListfromFunction(a2lBcFc.getName());
  }

  private Set<String> getParamListfromFunction(final String funName) {
    return getParamListfromFunction(a2lFileInfoBO().getFunctionByName(funName));
  }

  /**
   * @param function
   * @return
   */
  private Set<String> getParamListfromFunction(final Collection<Function> functionCol) {
    Set<String> paramSet = new HashSet<>();
    functionCol.forEach(funName -> paramSet.addAll(getParamListfromFunction(funName)));
    return paramSet;
  }

  /**
   * @param function
   * @return
   */
  private Set<String> getParamListfromFunction(final Function function) {
    Set<String> paramSet = new HashSet<>();
    if (function != null) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      // Get parameter names
      if (defCharRefList != null) {
        defCharRefList.stream().map(DefCharacteristic::getName).forEach(paramSet::add);
      }
    }
    return paramSet;
  }

  /**
   * @param function
   * @return
   */
  private Set<String> getParamListfromFunction(final List<String> functionList) {
    Set<String> paramSet = new HashSet<>();
    functionList.forEach(funName -> paramSet.addAll(getParamListfromFunction(funName)));
    return paramSet;
  }

  /**
   * @param paramSet
   * @return
   */
  private int[] getReviewDetails(final Set<String> paramSet) {
    // Get the reviewed parameter count
    // Fetch reviewed data from cdrReportData
    int paramCount = paramSet.size();
    int reviewedCount = 0;
    for (String parameter : paramSet) {
      String reviewData = this.cdrReportData.isReviewedStr(parameter);
      if (ApicConstants.REVIEWED.equalsIgnoreCase(reviewData)) {
        reviewedCount++;
      }
    }
    int percReviewed = reviewedCount == 0 ? 0 : Integer.divideUnsigned(reviewedCount * 100, paramCount);
    // Store reviewed parameters count , total parameter count and reviewed percentage in an array
    return new int[] { reviewedCount, paramCount, percReviewed };
  }

  /**
   * Review statistics for the complete a2l file
   *
   * @return array. element[0] - reviewedCount, element[1] - paramCount
   */
  public int[] getA2lReviewDetails() {
    Set<String> paramSet = getAllA2lParamNames();

    int totalA2lParams = paramSet.size();
    int a2lReviewedCount = 0;
    for (String parameter : paramSet) {
      String reviewData = this.cdrReportData.isReviewedStr(parameter);
      if (ApicConstants.REVIEWED.equalsIgnoreCase(reviewData)) {
        a2lReviewedCount++;
      }
    }
    // Store reviewed parameters count and total parameter count in an array
    return new int[] { a2lReviewedCount, totalA2lParams };
  }


  /**
   * reviewed parameters with bosch responsibility count and total parameter count in an array
   * 
   * @return percentage
   */
  public String getReviewedParameterWithBoschResp() {
    return this.cdrReportData.getReviewedParameterWithBoschResp(getAllA2lParamNames());
  }

  /**
   * count of total parameters in bosch responsibility
   * 
   * @return count
   */
  public int getParameterInBoschResp() {
    return this.cdrReportData.getParameterInBoschResp(getAllA2lParamNames());
  }


  /**
   * reviewed parameters with bosch responsibility count and total parameter count in an array
   * 
   * @return percentage
   */
  public String getReviewedParameterWithBoschRespForComplQnaire() {
    return this.cdrReportData.getRvwParamWithBoschRespForCompletedQnaire(getAllA2lParamNames());
  }

  /**
   * reviewed parameters count in bosch responsibility
   * 
   * @return count
   */
  public int getParameterInBoschRespRvwed() {
    return this.cdrReportData.getParameterInBoschRespRvwed(getAllA2lParamNames());
  }

  /**
   * @return count of qnaire with negaive answers count
   */
  public int getQnaireWithNegativeAnswersCount() {
    return this.cdrReportData.getQnaireWithNegativeAnswersCount();
  }


  private Set<String> getAllA2lParamNames() {
    return a2lFileInfoBO().getA2lParamMap(null).values().stream().map(A2LParameter::getName)
        .collect(Collectors.toSet());
  }

  private A2LFileInfoBO a2lFileInfoBO() {
    return this.cdrReportData.getA2lEditorDataProvider().getA2lFileInfoBO();
  }

  /**
   * @param bcFuncList
   * @param a2lFunctions
   * @return
   */
  private Set<String> getParamListfromA2lBcFuncs(final SortedSet<A2LBaseComponentFunctions> bcFuncList) {
    List<String> funcList = bcFuncList.stream().map(A2LBaseComponentFunctions::getName).collect(Collectors.toList());
    return getParamListfromFunction(funcList);
  }

}
