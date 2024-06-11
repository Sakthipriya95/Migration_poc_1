/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author mkl2cob
 */
public class CompHEXCDFOutlineFilter {

  // Holds the filter text
  private String selectedNode = "";

  // members to indicate type of filters
  private boolean isFunctionComp;
  private boolean bcFc;
  private boolean isBaseComp;
  private boolean workPckg;
  private boolean isRespPal;
  private boolean isRespOrWP;
  private boolean isRespType;
  private A2LBaseComponents selectedBC;
  private A2lWpObj workPackage;
  private CompPackage selectedCompPkg;
  private A2LGroup a2lGroup;

  private Long mappingSource;
  // icdm-272
  private boolean isWpGroup;

  private A2lWorkPackageGroup workPkgGrp;

  private boolean isGrpFun;

  private String grpFunName;

  private boolean isNonDefinedFC;

  private boolean allNotAssigned;

  private SortedSet<A2LParameter> notAssignedFun;

  private boolean compPkg;

  // Icdm-949 new Collection for Comp package Outline Selection
  private Map<String, A2LBaseComponents> a2lBcMap;

  /**
  *
  */
  private final CustomFilterGridLayer<CompHexWithCDFParam> compRprtFilterGridLayer;

  private final A2LFileInfoBO a2lFileInfoBO;

  private final Map<Long, Set<CompPkgBc>> compBcValuesMap = new HashMap<>();
  private CdrReportDataHandler cdrReportDataHandler;

  /**
   * Instantiates a new comp HEXCDF outline filter.
   *
   * @param compRprtFilterGridLayer the comp rprt filter grid layer
   * @param a2lFile the a 2 l file
   * @param a2lEditorDP the a 2 l editor DP
   * @param cdrReportDataHandler cdrReportDataHandler
   */
  public CompHEXCDFOutlineFilter(final CustomFilterGridLayer<CompHexWithCDFParam> compRprtFilterGridLayer,
      final A2LFile a2lFile, final A2LEditorDataProvider a2lEditorDP, final CdrReportDataHandler cdrReportDataHandler) {
    this.a2lFileInfoBO = a2lEditorDP.getA2lFileInfoBO();
    this.compRprtFilterGridLayer = compRprtFilterGridLayer;
    this.cdrReportDataHandler = cdrReportDataHandler;

  }

  /**
   * @return the grpFunName
   */
  public String getGrpFunName() {
    return this.grpFunName;
  }


  /**
   * @return the workPkgGrp
   */
  public A2lWorkPackageGroup getWorkPkgGrp() {
    return this.workPkgGrp;
  }


  /**
   * @return the a2lGroup
   */
  public A2LGroup getA2lGroup() {
    return this.a2lGroup;
  }


  /**
   * @return the selectedBC
   */
  public A2LBaseComponents getSelectedBC() {
    return this.selectedBC;
  }


  /**
   * @param selectedBC the selectedBC to set
   */
  public void setSelectedBC(final A2LBaseComponents selectedBC) {
    this.selectedBC = selectedBC;
  }

  /**
   * @return the isBC
   */
  public boolean isBC() {
    return this.isBaseComp;
  }

  /**
   * Sets if it is BC
   *
   * @param baseComp flag
   */
  public void setBCFlag(final boolean baseComp) {
    this.isBaseComp = baseComp;
  }


  /**
   * Sets if it is FUNCTION
   *
   * @param functionComp fc flag
   */
  public void setFCFlag(final boolean functionComp) {
    this.isFunctionComp = functionComp;
  }

  /**
   * Checks if it is function
   *
   * @return boolean isFunction
   */
  public boolean isFC() {
    return this.isFunctionComp;
  }

  /**
   * Sets if it is BC FUNCTION
   *
   * @param functionComp fc flag
   */
  public void setBCFCFlag(final boolean functionComp) {
    this.bcFc = functionComp;
  }

  /**
   * Checks if it is function of a BC
   *
   * @return boolean isFunction
   */
  public boolean isBCFC() {
    return this.bcFc;
  }

  /**
   * @param wp
   */
  // ICDM-209 and ICDM-210
  private void setWPFlag(final boolean workPkg) {
    this.workPckg = workPkg;
  }


  /**
   * @return the isWP
   */
  public boolean isWP() {
    return this.workPckg;
  }

  /**
   * @param wpGroup
   */
  private void setSelectedWp(final A2lWpObj workPackage) {
    this.workPackage = workPackage;
  }


  /**
   * @return the wpGroup
   */
  public A2lWpObj getSelectedWorkPackage() {
    return this.workPackage;
  }


  private class CompHexCDFMatcher<E> implements Matcher<E> {


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof CompHexWithCDFParam) {
        final CompHexWithCDFParam compParam = (CompHexWithCDFParam) element;
        return isValidCompParam(compParam);
      }
      // for root node selection
      return true;
    }
  }

  /**
   * Icdm-469 Selection listener implementation for selections on outlineFilter
   *
   * @param selection selection
   */
  public void a2lOutlineSelectionListener(final ISelection selection) {
    // Validate and get the selections
    if (isStructuredSelection(selection)) {
      resetAllFlagValues();
      final Object first = ((IStructuredSelection) selection).getFirstElement();

      // Check if selected node is a Function
      if (first instanceof Function) {
        setFCFlag(true);
        final Function func = (Function) first;
        this.selectedNode = func.getName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof A2LBaseComponents) {
        setBCFlag(true);
        final A2LBaseComponents baseComp = (A2LBaseComponents) first;
        setSelectedBC(baseComp);
        this.selectedNode = baseComp.getBcName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // Icdm-586
      else if (first instanceof A2LParameter) {
        setSpecialFunFalg(true);
        final A2LParameter charateristics = (A2LParameter) first;
        this.selectedNode = charateristics.getName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof CompPackage) {
        setCompPkg(true);
        final CompPackage selctdCompPkg = (CompPackage) first;
        setSelectedCompPkg(selctdCompPkg);
        loadBCMap();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof A2LBaseComponentFunctions) {
        setBCFCFlag(true);
        final A2LBaseComponentFunctions func = (A2LBaseComponentFunctions) first;
        this.selectedNode = func.getName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof A2lWorkPackageGroup) {
        setWPGroupFlag(true);
        final A2lWorkPackageGroup wpPkgGrp = (A2lWorkPackageGroup) first;
        setSelectedWorkPkgGrp(wpPkgGrp);
        this.selectedNode = wpPkgGrp.getGroupName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }

      // ICDM-209 and ICDM-210
      else if (first instanceof A2lWpObj) {
        setWPFlag(true);
        final A2lWpObj wpPackage = (A2lWpObj) first;
        setSelectedWp(wpPackage);
        final StringBuilder txt = new StringBuilder(wpPackage.getWpGroupName() + ":" + wpPackage.getWpName());
        this.selectedNode = txt.toString();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }

      else if (first instanceof A2LGroup) {
        setWPFlag(true);
        final A2LGroup a2lGrp = (A2LGroup) first;
        setSelectedA2lGroup(a2lGrp);
        this.selectedNode = a2lGrp.getGroupName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if ((first instanceof A2lWpResponsibility)) {
        setWPFlag(true);
        setRespPal(true);
        A2lWpResponsibility a2lWpResp = (A2lWpResponsibility) first;
        this.selectedNode = a2lWpResp.getName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (isUnassignedParam(first)) {
        setSpecialFunFalg(true);
        this.allNotAssigned = true;
        this.selectedNode = ApicConstants.UNASSIGNED_PARAM;
        // TODO: Check if true or false
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(true);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (first instanceof A2lResponsibility) {
        setGrpFunctionFlag(true);
        setRespOrWP(true);
        setFunName(((A2lResponsibility) first).getName());
        this.selectedNode = ((A2lResponsibility) first).getName();
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      else if (isRespTypeNode(first)) {
        setGrpFunctionFlag(true);
        setRespType(true);
        setFunName((String) first);
        this.selectedNode = (String) first;
        this.allNotAssigned = false;
        this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(false);
        this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      }
      // clear the filters for all Data Types
      else {
        displayAllRecords();
      }
    }
  }

  /**
   * @param selection
   * @return
   */
  private boolean isStructuredSelection(final ISelection selection) {
    return (selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection);
  }

  /**
   * @param first
   * @return
   */
  private boolean isUnassignedParam(final Object first) {
    return (first instanceof String) && !validateRootNodes(first) && (first.equals(ApicConstants.UNASSIGNED_PARAM));
  }

  /**
   * @param first
   * @return
   */
  private boolean isRespTypeNode(final Object first) {
    return (first instanceof String) && !validateRootNodes(first);
  }

  /**
   *
   */
  private void displayAllRecords() {
    this.selectedNode = "";
    this.allNotAssigned = false;
    this.compRprtFilterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(true);
    this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
  }

  private boolean validateRootNodes(final Object first) {
    return isA2lWpOrFcOrBC(first) || isWpRespOrRulesetOrComp(first);
  }

  /**
   * @param first
   * @return
   */
  private boolean isWpRespOrRulesetOrComp(final Object first) {
    return first.equals(ApicConstants.WP_RESPONSIBILITY) || first.equals(ApicConstants.RULE_SET) ||
        first.equals(ApicConstants.COMP);
  }

  /**
   * @param first
   * @return
   */
  private boolean isA2lWpOrFcOrBC(final Object first) {
    return first.equals(ApicConstants.A2L_WORK_PKG) || first.equals(ApicConstants.FC_CONST) ||
        first.equals(ApicConstants.BC_CONST);
  }


  /**
   * @param compParam
   * @return
   */
  public boolean isValidCompParam(final CompHexWithCDFParam compParam) {
    if (this.isFunctionComp || this.bcFc) {
      return setFcBCFilter(compParam);
    }
    else if (this.isNonDefinedFC) {
      return setFilterSpf(compParam);
    }
    else if (this.isRespPal) {
      return setFilterBasedOnRespPal(compParam);
    }
    else if (this.isRespType) {
      return setFilterBasedOnRespType(compParam);
    }
    else if (this.isRespOrWP) {
      return setFilterBasedOnRespNWp(compParam);
    }
    else if (this.isBaseComp) {
      return setFilterForBcCharacteristics(compParam.getFuncName());
    }
    else if (this.compPkg) {
      return setFilterForCompPkg(compParam);
    } // root node FC
    else if (this.workPckg) {
      return setWpfilterforCharecteristics(compParam);
    }
    else if (this.isWpGroup) {
      return setWpGroupFilterForChar(compParam);
    }
    else if (this.isGrpFun) {
      return setCharFilter(compParam);
    }
    return false;
  }

  /**
   * @param compParam
   * @return
   */
  private boolean setFilterBasedOnRespNWp(final CompHexWithCDFParam compParam) {
    // Workpackages under "WorkPackage" node and the resposibility names under "RESP" node both are string
    // check whether the selected element present in A2lWpRespNodeMergedMap , if not check in resp map
    if (this.cdrReportDataHandler.getA2lEditorDataProvider().getA2lWpInfoBO().getA2lWpDefnModel()
        .getA2lWpRespNodeMergedMap().containsKey(this.selectedNode)) {
      for (A2lWpResponsibility wpResp : this.cdrReportDataHandler.getA2lEditorDataProvider().getA2lWpInfoBO()
          .getA2lWpDefnModel().getA2lWpRespNodeMergedMap().get(this.selectedNode)) {
        if (wpResp.getName().equals(this.selectedNode)) {
          // filter if selected element is WP name
          return this.selectedNode.equals(this.cdrReportDataHandler.getWpName(compParam.getParamName()));
        }
      }
    }
    // filter if selected element is a responsibility name
    return this.selectedNode.equals(this.cdrReportDataHandler.getRespName(compParam.getParamName()));
  }

  /**
   * @param compParam
   * @return
   */
  private boolean setFilterBasedOnRespType(final CompHexWithCDFParam compParam) {
    // filter if selected element is a responsibility Type
    return this.selectedNode.equals(this.cdrReportDataHandler.getRespType(compParam.getParamName()));
  }

  /**
   * @param compParam
   * @return
   */
  private boolean setFilterBasedOnRespPal(final CompHexWithCDFParam compParam) {
    return this.selectedNode.equals(this.cdrReportDataHandler.getWpName(compParam.getParamName()));
  }

  /**
   * @param compParam icdm-272
   */
  private boolean setCharFilter(final CompHexWithCDFParam compParam) {
    final String funName = getGrpFunName();
    return ((compParam != null) &&
        ((compParam.getFuncName().equals(funName)) || (compParam.getFuncName().equals(funName) ||
            funName.equals(ApicConstants.WORKPACKAGE) || validateA2lGrp(funName))));
  }

  /**
   * @param funName
   * @return
   */
  private boolean validateA2lGrp(final String funName) {
    return CommonUtils.isEqual(this.mappingSource, getMappingSource()) && (getA2lGroup() != null) &&
        funName.equals(getA2lGroup().getRootName());
  }

  /**
   * *
   *
   * @param compParam
   * @return
   */
  private boolean setWpGroupFilterForChar(final CompHexWithCDFParam compParam) {
    final A2lWorkPackageGroup workPkgGroup = getWorkPkgGrp();
    final Map<String, Long> wrkPackage = workPkgGroup.getWorkPackageMap();
    for (Entry<String, Long> pack : wrkPackage.entrySet()) {
      final Map<String, String> functionMap =
          this.a2lFileInfoBO.getA2lWpMapping().getWpMap().get(pack.getValue()).getFunctionMap();
      if ((compParam != null) && (functionMap.get(compParam.getFuncName()) != null)) {
        return true;
      }
    }
    return false;
  }

  private Long getMappingSource() {
    try {

      return Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.GROUP_MAPPING_ID));
    }
    catch (NumberFormatException e) {
      CDMLogger.getInstance().errorDialog("Error occured while getting mapping definition", e, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return 0L;
  }

  /**
   * @param funcParam
   * @param defFunc
   */
  private boolean setWpfilterforCharecteristics(final CompHexWithCDFParam compParam) {
    if (CommonUtils.isEqual(this.mappingSource, getMappingSource())) {
      return checkForGrpMatching(compParam);
    }
    if ((getSelectedWorkPackage() != null) && (compParam != null)) {
      final Map<String, String> functionMap = getSelectedWorkPackage().getFunctionMap();
      final String functionName = functionMap.get(compParam.getFuncName());
      if (functionName != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param compParam
   * @return
   */
  private boolean checkForGrpMatching(final CompHexWithCDFParam compParam) {
    if (getA2lGroup() != null) {
      if (!getA2lGroup().getSubGrpMap().isEmpty()) {
        String grpName = getA2lGroup().getGroupName();
        List<String> a2lGrpList = getA2lGroup().getSubGrpMap().get(grpName);
        for (String a2lGrp : a2lGrpList) {
          final Map<String, String> labelMap =
              this.a2lFileInfoBO.getA2lWpMapping().getA2lGrpMap().get(a2lGrp).getLabelMap();
          if (labelMap.get(compParam.getFuncName()) != null) {
            return true;
          }
        }
      }
      else {
        final Map<String, String> labelMap = getA2lGroup().getLabelMap();
        final String labelName = labelMap.get(compParam.getFuncName());
        if (labelName != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Icdm-949 If the CompPkg has BCFC association and the Fc name is equal to the Def function name return true. If the
   * CompPkg has no BCFC association them get all the BC's of the A2l File and get the Function name of the A2lBC and if
   * the FC name is same as Def function name return true
   *
   * @param compParam
   */
  private boolean setFilterForCompPkg(final CompHexWithCDFParam compParam) {
    if ((getSelectedCompPkg() != null) && (compParam != null)) {
      Set<CompPkgBc> compBcValues = this.compBcValuesMap.get(getSelectedCompPkg().getId());
      for (CompPkgBc bcObj : compBcValues) {
        String bcName = bcObj.getBcName();
        if (bcObj.getFcList().isEmpty() && CommonUtils.isNotNull(this.a2lBcMap.get(bcName)) &&
            CommonUtils.isNotNull(this.a2lBcMap.get(bcName).getFunctionMap()) &&
            this.a2lBcMap.get(bcName).getFunctionMap().containsKey(compParam.getFuncName())) {
          return true;
        }
        for (CompPkgFc compPkgFC : bcObj.getFcList()) {
          if (compParam.getFuncName().equals(compPkgFC.getName())) {
            return true;
          }
        }
      }
    }

    return false;
  }


  /**
   * @param funcName
   */
  private boolean setFilterForBcCharacteristics(final String funcName) {
    if ((getSelectedBC() != null) && (funcName != null)) {
      for (A2LBaseComponentFunctions a2lBaseComponentFunctions : getSelectedBC().getFunctionsList()) {
        if (funcName.equals(a2lBaseComponentFunctions.getName())) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @param compParam
   * @return
   */
  private boolean setFilterSpf(final CompHexWithCDFParam compParam) {
    if (isAllNotAssigned()) {
      return showNotAssignedFC(compParam);
    }
    return this.selectedNode.equals(compParam.getFuncName());

  }

  /**
   * Show all the Not Assigned Fc's
   *
   * @param compParam
   */
  private boolean showNotAssignedFC(final CompHexWithCDFParam compParam) {
    if ((this.notAssignedFun != null) && !this.notAssignedFun.isEmpty()) {
      for (A2LParameter characteristics : this.notAssignedFun) {
        if (CommonUtils.isEqual(characteristics.getName(), compParam.getFuncName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param compParam
   * @return
   */
  private boolean setFcBCFilter(final CompHexWithCDFParam compParam) {

    return this.selectedNode.equals(compParam.getFuncName());

  }


  /**
   * icdm-272 Set al flag values
   *
   * @param isRespName
   */

  private void resetAllFlagValues() {
    setFCFlag(false);
    setBCFCFlag(false);
    setBCFlag(false);
    setWPFlag(false);
    setWPGroupFlag(false);
    setGrpFunctionFlag(false);
    setSpecialFunFalg(false);
    setCompPkg(false);
    setRespPal(false);
    setRespOrWP(false);
    setRespType(false);
  }


  /**
   * @param sfc
   */
  private void setSpecialFunFalg(final boolean sfc) {
    this.isNonDefinedFC = sfc;

  }


  /**
   * @param first
   */
  private void setFunName(final String funName) {
    this.grpFunName = funName;

  }


  /**
   * @param b
   */
  private void setGrpFunctionFlag(final boolean grpFun) {
    this.isGrpFun = grpFun;

  }


  /**
   * @param wpPkgGrp
   */
  private void setSelectedWorkPkgGrp(final A2lWorkPackageGroup wpPkgGrp) {
    this.workPkgGrp = wpPkgGrp;

  }


  /**
   * @param b
   */
  private void setWPGroupFlag(final boolean isWpGroup) {
    this.isWpGroup = isWpGroup;

  }


  /**
   * @param a2lGroup
   */
  private void setSelectedA2lGroup(final A2LGroup a2lGroup) {

    this.a2lGroup = a2lGroup;
  }


  /**
   * @param mappingSource mappingSource
   */
  public void setWpType(final Long mappingSource) {
    this.mappingSource = mappingSource;
  }


  /**
   * Icdm-469
   *
   * @return the alln
   */
  public boolean isAllNotAssigned() {
    return this.allNotAssigned;
  }


  /**
   * @param specialFun .
   */
  public void setNotAssignedFC(final SortedSet<A2LParameter> specialFun) {
    this.notAssignedFun = specialFun;

  }

  /**
   * @return .
   */
  public Matcher<CompHexWithCDFParam> getOutlineMatcher() {
    return new CompHexCDFMatcher<>();
  }

  /**
   * @return the isCompPkg
   */
  public boolean isCompPkg() {
    return this.compPkg;
  }


  /**
   * @param isCompPkg the isCompPkg to set
   */
  public void setCompPkg(final boolean isCompPkg) {
    this.compPkg = isCompPkg;
  }


  /**
   * @return the selectedCompPkg
   */
  public CompPackage getSelectedCompPkg() {
    return this.selectedCompPkg;
  }


  /**
   * @param selectedCompPkg the selectedCompPkg to set
   */
  public void setSelectedCompPkg(final CompPackage selectedCompPkg) {
    this.selectedCompPkg = selectedCompPkg;
  }

  /**
   * Icdm-949 load the Bc map for the Comp Package
   */
  public void loadBCMap() {
    if (this.a2lFileInfoBO != null) {
      this.a2lBcMap = this.a2lFileInfoBO.getA2lBcMap();
      if (!this.compBcValuesMap.containsKey(getSelectedCompPkg().getId())) {
        Set<CompPkgBc> compBcValues = this.a2lFileInfoBO.getCompPkgData(getSelectedCompPkg().getId()).getBcSet();
        this.compBcValuesMap.put(getSelectedCompPkg().getId(), compBcValues);
      }
    }
  }


  /**
   * @return the isRespPal
   */
  public boolean isRespPal() {
    return this.isRespPal;
  }


  /**
   * @param isRespPal the isRespPal to set
   */
  public void setRespPal(final boolean isRespPal) {
    this.isRespPal = isRespPal;
  }


  /**
   * @return the cdrReportDataHandler
   */
  public CdrReportDataHandler getCdrReportDataHandler() {
    return this.cdrReportDataHandler;
  }


  /**
   * @param cdrReportDataHandler the cdrReportDataHandler to set
   */
  public void setCdrReportDataHandler(final CdrReportDataHandler cdrReportDataHandler) {
    this.cdrReportDataHandler = cdrReportDataHandler;
  }


  /**
   * @return the isResponsible
   */
  public boolean isRespOrWP() {
    return this.isRespOrWP;
  }


  /**
   * @param isRespOrWP the isResponsible to set
   */
  public void setRespOrWP(final boolean isRespOrWP) {
    this.isRespOrWP = isRespOrWP;
  }


  /**
   * @param isRespType the isRespType to set
   */
  public void setRespType(final boolean isRespType) {
    this.isRespType = isRespType;
  }
}
