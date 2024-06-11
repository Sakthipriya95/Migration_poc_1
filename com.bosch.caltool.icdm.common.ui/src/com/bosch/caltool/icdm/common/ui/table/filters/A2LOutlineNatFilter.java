/*
 * ' * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * This classes supports filter capability on NatTable
 *
 * @author jvi6cob
 */
public class A2LOutlineNatFilter {

  // Holds the filter text
  private String selectedNode = "";

  private Long selectedWpId;

  private String selectedResponsible;

  private String selectedResponsibilityType;
  // members to indicate type of filters
  private boolean isFunctionComp;
  private boolean bcFc;
  private boolean isBaseComp;
  private boolean workPckg;
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

  private final CustomFilterGridLayer<T> filterGridLayer;

  // Icdm-949
  private boolean compPkg;

  private boolean isRuleSet;

  private RuleSet selRuleSet;

  private final A2LFile a2lFile;

  private final A2LFileInfoBO a2lEditorModelBO;

  private final A2lWpMapping a2lWpMapping;

  private final A2LWPInfoBO a2lwpInfoBO;

  private final CdrReportDataHandler cdrReportDataHandler;

  /**
   * @param customFilterGridLayer customFilterGridLayer
   * @param a2lFile a2l file
   * @param a2lwpInfoBO a2l workpackage Info BO
   * @param cdrReportDataHandler cdr Report handler
   */
  public A2LOutlineNatFilter(final CustomFilterGridLayer<T> customFilterGridLayer, final A2LFile a2lFile,
      final A2LWPInfoBO a2lwpInfoBO, final CdrReportDataHandler cdrReportDataHandler) {
    this.filterGridLayer = customFilterGridLayer;
    this.a2lFile = a2lFile;
    this.a2lEditorModelBO = a2lwpInfoBO.getA2lFileInfoBo();
    this.a2lWpMapping = a2lwpInfoBO.getA2lFileInfoBo().getA2lWpMapping();
    this.a2lwpInfoBO = a2lwpInfoBO;
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
    // NA
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
    // NA
    this.workPackage = workPackage;
  }


  /**
   * @return the wpGroup
   */
  public A2lWpObj getSelectedWorkPackage() {
    return this.workPackage;
  }


  /**
   * @param element .
   * @return .
   */
  // @Override
  protected boolean selectElement(final Object element) {
    // Check if is Function Parameters
    // Icdm-586
    if (element instanceof A2LParameter) {
      final A2LParameter funcParam = (A2LParameter) element;
      return isValidCharacteristics(funcParam.getCharacteristic());
    }
    else if (element instanceof Function) {
      final Function funcParam = (Function) element;
      return isValidFunction(funcParam);
    }
    // ICDM-209 and ICDM-210

    else if ((element instanceof A2lWpObj) && (this.workPckg)) {
      final A2lWpObj workPkg = (A2lWpObj) element;
      final StringBuilder grpName = new StringBuilder(workPkg.getWpGroupName() + ":" + workPkg.getWpName());
      return grpName.toString().equals(this.selectedNode);
    }
    else if ((element instanceof A2lWpObj) && (this.isWpGroup)) {
      final A2lWpObj workPkg = (A2lWpObj) element;
      return workPkg.getWpGroupName().equals(this.selectedNode);
    }
    else if ((element instanceof A2LGroup) && (this.workPckg)) {
      final A2LGroup a2lGrp = (A2LGroup) element;
      return a2lGrp.getGroupName().equals(this.selectedNode);
    }
    // for root node selection
    return true;

  }


  /**
   * Show all the Not Assigned Fc's
   *
   * @param funcParam
   */
  private boolean showNotAssignedFC(final Characteristic funcParam) {
    if ((this.notAssignedFun != null) && !this.notAssignedFun.isEmpty()) {
      for (A2LParameter characteristics : this.notAssignedFun) {
        if (CommonUtils.isEqual(characteristics.getName(), funcParam.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param funcParam
   */
  private boolean isValidCharacteristics(final Characteristic funcParam) {
    if (this.isFunctionComp || this.bcFc) {
      return setFcBCFilter(funcParam);
    }
    else if (this.isNonDefinedFC) {
      return setFilterSpf(funcParam);
    }

    else if (this.isBaseComp) {
      return setFilterForBcCharacteristics(funcParam.getDefFunction());
    }
    else if (this.isRuleSet) {
      return setFilterForRuleSet(funcParam);
    }
    else if (this.compPkg) {
      return setFilterForCompPkg(funcParam.getDefFunction());
    } // root node FC
      // ICDM-209 and ICDM-210
    else if (this.workPckg) {
      return setWpfilterforCharecteristics(funcParam, funcParam.getDefFunction());
    }
    else if (this.isWpGroup) {
      return setWpGroupFilterForChar(funcParam.getDefFunction());
    }
    else if (this.isGrpFun) {
      // icdm-272
      return setCharFilter(funcParam);
    }
    return false;
  }

  /**
   * @param funcParam
   * @return true if the param names are equal.
   */
  private boolean setFilterForRuleSet(final Characteristic funcParam) {
    Set<String> paramNameSet = this.a2lEditorModelBO.getRuleSetParamNameSet(this.selRuleSet.getId());
    return paramNameSet.contains(funcParam.getName());
  }

  /**
   * @param funcParam
   */
  private boolean setFcBCFilter(final Characteristic funcParam) {
    final Function defFunc = funcParam.getDefFunction();
    return (defFunc != null) && this.selectedNode.equalsIgnoreCase(defFunc.getName());
  }


  /**
   * @param funcParam
   */
  private boolean setFilterSpf(final Characteristic funcParam) {
    if (isAllNotAssigned()) {
      return showNotAssignedFC(funcParam);
    }
    return (funcParam.getName() != null) && this.selectedNode.equals(funcParam.getName());
  }

  /**
   * @param funcParam icdm-272
   */
  private boolean setCharFilter(final Characteristic funcParam) {
    final String funName = getGrpFunName();

    Long grpMappingId = getGroupMappingId();
    return ((funcParam.getDefFunction() != null) && funcParam.getDefFunction().getName().equalsIgnoreCase(funName)) ||
        (funcParam.getName().equals(funName) || funName.equals(ApicConstants.WORKPACKAGE) ||
            (CommonUtils.isEqual(this.mappingSource, grpMappingId) && (getA2lGroup() != null) &&
                funName.equals(getA2lGroup().getRootName())));

  }

  private Long getGroupMappingId() {
    CommonDataBO commonBo = new CommonDataBO();

    Long grpMappingId = null;
    try {
      grpMappingId = Long.valueOf(commonBo.getParameterValue(CommonParamKey.GROUP_MAPPING_ID));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return grpMappingId;
  }


  /**
   * icdm-272
   *
   * @param defFunction
   * @return
   */
  private boolean setWpGroupFilterForChar(final Function defFunction) {
    final A2lWorkPackageGroup workPkgGroup = getWorkPkgGrp();
    final Map<String, Long> wrkPackage = workPkgGroup.getWorkPackageMap();
    for (Entry<String, Long> pack : wrkPackage.entrySet()) {
      final Map<String, String> functionMap = this.a2lWpMapping.getWpMap().get(pack.getValue()).getFunctionMap();
      if ((defFunction != null) && (functionMap.get(defFunction.getName()) != null)) {
        return true;
      }
    }
    return false;
  }


  /**
   * @param defFunc
   */
  private boolean setFilterForBcCharacteristics(final Function defFunc) {
    if ((getSelectedBC() != null) && (defFunc != null)) {
      for (String funcName : getSelectedBC().getFunctionMap().keySet()) {
        if (defFunc.getName().equalsIgnoreCase(funcName)) {
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
   * @param defFunc
   */
  private boolean setFilterForCompPkg(final Function defFunc) {
    if ((getSelectedCompPkg() != null) && (defFunc != null)) {

      Set<CompPkgBc> compBcValues = this.a2lEditorModelBO.getCompPkgBCSet(getSelectedCompPkg().getId());
      Map<String, A2LBaseComponents> a2lBcMap = this.a2lEditorModelBO.getA2lBcMap();
      for (CompPkgBc bcObj : compBcValues) {
        String bcName = bcObj.getBcName();
        if (bcObj.getFcList().isEmpty() && CommonUtils.isNotNull(a2lBcMap.get(bcName)) &&
            CommonUtils.isNotNull(a2lBcMap.get(bcName).getFunctionMap()) &&
            a2lBcMap.get(bcName).getFunctionMap().containsKey(defFunc.getName())) {
          return true;
        }
        for (CompPkgFc compPkgFC : bcObj.getFcList()) {
          if (defFunc.getName().equals(compPkgFC.getName())) {
            return true;
          }
        }
      }
    }

    return false;
  }


  /**
   * @param funcParam
   * @param defFunc
   */
  private boolean setWpfilterforCharecteristics(final Characteristic funcParam, final Function defFunc) {
    if (CommonUtils.isEqual(this.mappingSource, getGroupMappingId())) {
      if (getA2lGroup() != null) {
        return getLabelForA2lGrp(funcParam);
      }
    }
    else {
      if ((getSelectedWorkPackage() != null) && (defFunc != null)) {
        final Map<String, String> functionMap = getSelectedWorkPackage().getFunctionMap();
        final String functionName = functionMap.get(defFunc.getName());
        if (functionName != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param funcParam
   * @return
   */
  private boolean getLabelForA2lGrp(final Characteristic funcParam) {
    if (!getA2lGroup().getSubGrpMap().isEmpty()) {
      List<String> a2lGrpList = getA2lGroup().getSubGrpMap().get(getA2lGroup().getGroupName());
      for (String a2lGrpStr : a2lGrpList) {
        final Map<String, String> labelMap = this.a2lWpMapping.getA2lGrpMap().get(a2lGrpStr).getLabelMap();
        if (labelMap.get(funcParam.getName()) != null) {
          return true;
        }
      }
    }
    else {
      final Map<String, String> labelMap = getA2lGroup().getLabelMap();
      final String labelName = labelMap.get(funcParam.getName());
      if (labelName != null) {
        return true;
      }
    }
    return false;
  }


  /**
   * @param func
   */
  private boolean isValidFunction(final Function func) {
    if (this.isFunctionComp || this.bcFc) {
      if (func.getName().equalsIgnoreCase(this.selectedNode)) {
        return true;
      }

    }
    else if (this.isBaseComp) {
      return filterBcFcValues(func);

    } // root node FC
      // ICDM-209 and ICDM-210
    else if (this.workPckg) {
      return setWpFilterForFunctions(func);

    }
    else if (this.isWpGroup) {
      return setWpGrpFilterForFunctions(func);
    }

    else if (this.isGrpFun) {
      // icdm-272
      return setFunctionFilter(func);
    }
    else if (this.compPkg) {
      return setFilterForCompPkg(func);
    }
    return false;

  }


  /**
   * @param funcParam
   */
  private boolean filterBcFcValues(final Function funcParam) {
    if (getSelectedBC() != null) {
      for (A2LBaseComponentFunctions a2lBaseComponentFunctions : getSelectedBC().getFunctionsList()) {
        if (funcParam.getName().equals(a2lBaseComponentFunctions.getName())) {
          return true;
        }
      }

    }
    return false;
  }


  /**
   * icdm-272
   *
   * @param funcParam
   * @return
   */
  private boolean setFunctionFilter(final Function funcParam) {
    final String funName = getGrpFunName();
    return funcParam.getName().equalsIgnoreCase(funName) || funName.equals(ApicConstants.WORKPACKAGE) ||
        CommonUtils.isEqual(this.mappingSource, getGroupMappingId());

  }


  /**
   * @param funcParam
   * @return
   */
  private boolean setWpGrpFilterForFunctions(final Function funcParam) {
    final A2lWorkPackageGroup workPkgGroup = getWorkPkgGrp();
    final Map<String, Long> wrkPackage = workPkgGroup.getWorkPackageMap();
    for (Entry<String, Long> pack : wrkPackage.entrySet()) {
      final Map<String, String> functionMap = this.a2lWpMapping.getWpMap().get(pack.getValue()).getFunctionMap();
      if (functionMap.get(funcParam.getName()) != null) {
        return true;
      }

    }

    return false;
  }

  /**
   * @param funcParam
   */
  private boolean setWpFilterForFunctions(final Function funcParam) {
    final A2lWpObj workPkg = getSelectedWorkPackage();
    if ((workPkg != null) && (workPkg.getSource() != null)) {
      final Map<String, String> functionMap = getSelectedWorkPackage().getFunctionMap();
      if (workPkg.getSource().equalsIgnoreCase(ApicConstants.FC_WP_MAPPING)) {
        final String functionName = functionMap.get(funcParam.getName());
        if (functionName != null) {
          return true;
        }
      }
      return false;
    }
    else if (CommonUtils.isEqual(this.mappingSource, getGroupMappingId())) {
      return true;
    }
    return false;
  }


  private class A2lParamMatcher<E> implements Matcher<E> {


    /**
     * {@inheritDoc}
     *
     * @return boolean value
     */
    @Override
    public boolean matches(final E element) {
      if (element instanceof A2LParameter) {
        final A2LParameter funcParam = (A2LParameter) element;
        return isValidA2lParameter(funcParam);
      }
      else if (element instanceof Function) {
        final Function funcParam = (Function) element;
        return isValidFunction(funcParam);
      }
      // ICDM-209 and ICDM-210

      else if ((element instanceof A2lWpObj) && (A2LOutlineNatFilter.this.workPckg)) {
        final A2lWpObj workPkg = (A2lWpObj) element;
        final StringBuilder grpName = new StringBuilder(workPkg.getWpGroupName() + ":" + workPkg.getWpName());
        return grpName.toString().equals(A2LOutlineNatFilter.this.selectedNode);
      }
      else if ((element instanceof A2lWpObj) && (A2LOutlineNatFilter.this.isWpGroup)) {
        final A2lWpObj workPkg = (A2lWpObj) element;
        return workPkg.getWpGroupName().equals(A2LOutlineNatFilter.this.selectedNode);
      }
      else if ((element instanceof A2LGroup) && (A2LOutlineNatFilter.this.workPckg)) {
        final A2LGroup a2lGrp = (A2LGroup) element;
        return a2lGrp.getGroupName().equals(A2LOutlineNatFilter.this.selectedNode);
      }
      else if (element instanceof A2LWpRespExt) {
        final A2LWpRespExt a2lWpResp = (A2LWpRespExt) element;
        if (a2lWpResp.isA2lGrp() &&
            a2lWpResp.getIcdmA2lGroup().getGrpName().equals(A2LOutlineNatFilter.this.selectedNode)) {
          return true;
        }
        return a2lWpResp.getWpResource().equals(A2LOutlineNatFilter.this.selectedNode) ||
            (getWpAndGrpName(a2lWpResp).equals(A2LOutlineNatFilter.this.selectedNode));
      }
      else if (element instanceof A2lWpResponsibility) {
        final A2lWpResponsibility wpRespPal = (A2lWpResponsibility) element;
        return filterWpBasedOnResp(wpRespPal);
      }
      else if (element instanceof A2LWpParamInfo) {

        final A2LWpParamInfo a2lWpParamInfo = (A2LWpParamInfo) element;
        return isValidA2lWpParamInfo(a2lWpParamInfo);
      }

      // for root node selection
      return true;
    }

    /**
     * @param a2lWpParamInfo
     * @return
     */
    private boolean isValidA2lWpParamInfo(final A2LWpParamInfo a2lWpParamInfo) {
      String wpRespUser = A2LOutlineNatFilter.this.a2lwpInfoBO.getWpRespUser(a2lWpParamInfo);
      String wpRespType = A2LOutlineNatFilter.this.a2lwpInfoBO.getWpRespType(a2lWpParamInfo);

      if ((A2LOutlineNatFilter.this.selectedResponsibilityType != null) &&
          wpRespType.equals(A2LOutlineNatFilter.this.selectedResponsibilityType)) {
        return true;
      }

      // Added for BC Filtering in Wp Param Mapping page
      if (hasBcFunctions(a2lWpParamInfo)) {
        for (A2LBaseComponentFunctions functions : A2LOutlineNatFilter.this.selectedBC.getFunctionsList()) {
          if (functions.getName().equals(a2lWpParamInfo.getFuncName())) {
            return true;
          }
        }
      }

      String wpRespName = !wpRespUser.equals("") ? wpRespUser : wpRespType;
      if ((A2LOutlineNatFilter.this.selectedNode != null) && A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel()
          .getA2lWpRespNodeMergedMap().containsKey(A2LOutlineNatFilter.this.selectedNode)) {
        for (A2lWpResponsibility wpResp : A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel()
            .getA2lWpRespNodeMergedMap().get(A2LOutlineNatFilter.this.selectedNode)) {
          if (wpResp.getId().equals(a2lWpParamInfo.getWpRespId())) {
            return true;
          }
        }
      }
      else if (isWpAndResp(a2lWpParamInfo, wpRespName)) {
        return true;
      }
      else if ((A2LOutlineNatFilter.this.selectedNode != null) &&
          (ApicConstants.WP_RESPONSIBILITY.equals(A2LOutlineNatFilter.this.selectedNode) ||
              ApicConstants.A2L_WORK_PKG.equals(A2LOutlineNatFilter.this.selectedNode))) {
        return true;
      }
      else if ((A2LOutlineNatFilter.this.selectedResponsible != null) &&
          (A2LOutlineNatFilter.this.selectedWpId == null)) {

        A2lResponsibility a2lResp = A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel()
            .getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId());
        if (a2lResp != null) {
          if (A2LOutlineNatFilter.this.selectedResponsible.equals(ApicConstants.WP_RESPONSIBILITY)) {
            return true;
          }
          return filterWpLabelsBasedOnResp(a2lWpParamInfo);
        }
      }
      else if ((A2LOutlineNatFilter.this.selectedWpId != null) && (a2lWpParamInfo.getWpRespId() != null) &&
          (A2LOutlineNatFilter.this.selectedResponsible == null) &&
          a2lWpParamInfo.getWpRespId().equals(A2LOutlineNatFilter.this.selectedWpId)) {
        return true;
      }
      else if (a2lWpParamInfo.getParamName().equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      else if (a2lWpParamInfo.getFuncName().equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      else if (((a2lWpParamInfo.getFuncName() == null) || a2lWpParamInfo.getFuncName().isEmpty()) &&
          ApicConstants.UNASSIGNED_PARAM.equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      return false;
    }

    /**
     * @param a2lWpParamInfo
     * @param wpRespName
     * @return
     */
    private boolean isWpAndResp(final A2LWpParamInfo a2lWpParamInfo, String wpRespName) {
      return (A2LOutlineNatFilter.this.selectedWpId != null) && (a2lWpParamInfo.getWpRespId() != null) &&
          (A2LOutlineNatFilter.this.selectedResponsible != null) &&
          a2lWpParamInfo.getWpRespId().equals(A2LOutlineNatFilter.this.selectedWpId) &&
          wpRespName.equals(A2LOutlineNatFilter.this.selectedResponsible);
    }

    /**
     * @param a2lWpParamInfo
     * @return
     */
    private boolean hasBcFunctions(final A2LWpParamInfo a2lWpParamInfo) {
      return (A2LOutlineNatFilter.this.selectedNode != null) && (a2lWpParamInfo.getFuncName() != null) &&
          (A2LOutlineNatFilter.this.selectedBC != null) &&
          (((A2LOutlineNatFilter.this.selectedBC).getFunctionsList() != null) ||
              !A2LOutlineNatFilter.this.selectedBC.getFunctionsList().isEmpty());
    }

    /**
     * @param element
     * @return
     */
    private boolean isValidA2lParameter(final A2LParameter funcParam) {

      // Added for Reports editor
      if (null != A2LOutlineNatFilter.this.cdrReportDataHandler) {

        return isCdrReportsMatches(funcParam);
      }
      // Filter based on selected BC in parameter page
      if (isCDRBc(funcParam)) {
        for (A2LBaseComponentFunctions functions : A2LOutlineNatFilter.this.selectedBC.getFunctionsList()) {
          if (functions.getName().equals(funcParam.getDefFunction().getName())) {
            return true;
          }
        }
      }
      // Filtering based on Wp selected inside a Resp in outline view
      if (isValidParamWorkPackage(funcParam)) {
        return true;
      }
      // Filtering in A2l Param page based on Wp selection
      if (isValidA2lParam(funcParam)) {
        Long wpId = (Long) A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
            .get(funcParam.getParamId()).keySet().toArray()[0];
        for (A2lWpResponsibility wpResp : A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel()
            .getA2lWpRespNodeMergedMap().get(A2LOutlineNatFilter.this.selectedNode)) {
          if (wpResp.getId().equals(wpId)) {
            return true;
          }
        }
      }
      if ((funcParam.getDefFunction() == null) &&
          ApicConstants.NOT_ASSIGNED.equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      if ((funcParam.getDefFunction() != null) &&
          funcParam.getDefFunction().getName().equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      if (funcParam.getName().equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      if (isValidParamRespType(funcParam)) {
        Set<A2lResponsibility> a2lResponsibilitySet = A2LOutlineNatFilter.this.a2lwpInfoBO
            .getRespTypeAndRespMapForWpParam().get(A2LOutlineNatFilter.this.selectedResponsibilityType);
        for (A2lResponsibility a2lResponsibility : a2lResponsibilitySet) {
          if (A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
              .get(funcParam.getParamId()).containsValue(a2lResponsibility.getName())) {
            return true;
          }
        }
      }

      // Condition filter for filterinng in param page in a2l editor after selection of a WP / Resp in outline
      // filter
      // For Resp Selection in outline filter selectedWpId will be null && selectedResponsible will be available
      // For Workpackage Selection in outline filter selectedWpId && selectedResponsible will be available
      // selectedWpId && selectedResponsible will be checked along with getParamIdWithWpAndRespMap()
      return isValidParamResp(funcParam);
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isValidParamResp(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
          .containsKey(funcParam.getParamId())) &&
          (((A2LOutlineNatFilter.this.selectedWpId != null) &&
              A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
                  .get(funcParam.getParamId()).containsKey(A2LOutlineNatFilter.this.selectedWpId)) ||
              (A2LOutlineNatFilter.this.selectedWpId == null)) &&
          ((A2LOutlineNatFilter.this.selectedResponsible != null) &&
              A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
                  .get(funcParam.getParamId()).containsValue(A2LOutlineNatFilter.this.selectedResponsible));
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isValidParamRespType(final A2LParameter funcParam) {
      return A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
          .containsKey(funcParam.getParamId()) && (A2LOutlineNatFilter.this.selectedResponsibilityType != null) &&
          (A2LOutlineNatFilter.this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam()
              .containsKey(A2LOutlineNatFilter.this.selectedResponsibilityType));
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isValidA2lParam(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.selectedNode != null) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap()
              .containsKey(A2LOutlineNatFilter.this.selectedNode) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
              .containsKey(funcParam.getParamId());
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isValidParamWorkPackage(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
          .containsKey(funcParam.getParamId())) &&
          (A2LOutlineNatFilter.this.selectedWpId != null) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
              .get(funcParam.getParamId()).containsKey(A2LOutlineNatFilter.this.selectedWpId) &&
          (A2LOutlineNatFilter.this.selectedResponsible != null) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap()
              .get(funcParam.getParamId()).containsValue(A2LOutlineNatFilter.this.selectedResponsible);
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isCdrReportsMatches(final A2LParameter funcParam) {
      // Filter based on selected BC in parameter page
      if (isCDRBc(funcParam)) {
        for (A2LBaseComponentFunctions functions : A2LOutlineNatFilter.this.selectedBC.getFunctionsList()) {
          if (functions.getName().equals(funcParam.getDefFunction().getName())) {
            return true;
          }
        }
      }
      // Filtering based on Wp selected inside a Resp in outline view
      if (isCDRWp(funcParam)) {
        return true;
      }
      // Filtering in A2l Param page based on Wp selection
      if (isA2lParamForWp(funcParam)) {
        Long wpId = (Long) A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap()
            .get(funcParam.getParamId()).keySet().toArray()[0];
        for (A2lWpResponsibility wpResp : A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel()
            .getA2lWpRespNodeMergedMap().get(A2LOutlineNatFilter.this.selectedNode)) {
          if (wpResp.getA2lWpId().equals(wpId)) {
            return true;
          }
        }
      }
      if ((funcParam.getDefFunction() == null) &&
          ApicConstants.NOT_ASSIGNED.equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      if ((funcParam.getDefFunction() != null) &&
          funcParam.getDefFunction().getName().equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      if (funcParam.getName().equals(A2LOutlineNatFilter.this.selectedNode)) {
        return true;
      }
      if (isCDRRespType(funcParam)) {
        Set<A2lResponsibility> a2lResponsibilitySet = A2LOutlineNatFilter.this.cdrReportDataHandler
            .getRespTypeAndRespMap().get(A2LOutlineNatFilter.this.selectedResponsibilityType);
        for (A2lResponsibility a2lResponsibility : a2lResponsibilitySet) {
          if (A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap().get(funcParam.getParamId())
              .containsValue(a2lResponsibility.getName())) {
            return true;
          }
        }
      }

      return isCDRWpForResp(funcParam);
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isCDRWpForResp(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap()
          .containsKey(funcParam.getParamId())) &&
          (((A2LOutlineNatFilter.this.selectedWpId != null) &&
              A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap().get(funcParam.getParamId())
                  .containsKey(A2LOutlineNatFilter.this.selectedWpId)) ||
              (A2LOutlineNatFilter.this.selectedWpId == null)) &&
          ((A2LOutlineNatFilter.this.selectedResponsible != null) &&
              A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap().get(funcParam.getParamId())
                  .containsValue(A2LOutlineNatFilter.this.selectedResponsible));
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isCDRRespType(final A2LParameter funcParam) {
      return A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap()
          .containsKey(funcParam.getParamId()) && (A2LOutlineNatFilter.this.selectedResponsibilityType != null) &&
          (A2LOutlineNatFilter.this.cdrReportDataHandler.getRespTypeAndRespMap()
              .containsKey(A2LOutlineNatFilter.this.selectedResponsibilityType));
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isA2lParamForWp(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.selectedNode != null) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap()
              .containsKey(A2LOutlineNatFilter.this.selectedNode) &&
          A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap()
              .containsKey(funcParam.getParamId());
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isCDRWp(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap()
          .containsKey(funcParam.getParamId())) &&
          (A2LOutlineNatFilter.this.selectedWpId != null) &&
          A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap().get(funcParam.getParamId())
              .containsKey(A2LOutlineNatFilter.this.selectedWpId) &&
          (A2LOutlineNatFilter.this.selectedResponsible != null) &&
          A2LOutlineNatFilter.this.cdrReportDataHandler.getParamIdWithWpAndRespMap().get(funcParam.getParamId())
              .containsValue(A2LOutlineNatFilter.this.selectedResponsible);
    }

    /**
     * @param funcParam
     * @return
     */
    private boolean isCDRBc(final A2LParameter funcParam) {
      return (A2LOutlineNatFilter.this.selectedNode != null) && (funcParam.getDefFunction() != null) &&
          (A2LOutlineNatFilter.this.selectedBC != null) &&
          (((A2LOutlineNatFilter.this.selectedBC).getFunctionsList() != null) ||
              !A2LOutlineNatFilter.this.selectedBC.getFunctionsList().isEmpty());
    }

    private boolean filterWpBasedOnResp(final A2lWpResponsibility wpRespPal) {
      if ((A2LOutlineNatFilter.this.selectedResponsibilityType != null) && A2LOutlineNatFilter.this.a2lwpInfoBO
          .getA2lResponsibilityModel().getA2lResponsibilityMap().containsKey(wpRespPal.getA2lRespId())) {
        String respType = A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel().getA2lResponsibilityMap()
            .get(wpRespPal.getA2lRespId()).getRespType();
        if (WpRespType.getType(respType).getDispName().equals(A2LOutlineNatFilter.this.selectedResponsibilityType)) {
          return true;
        }
      }
      // Filtering in Wp Defnition page
      if (isWpDefPage(wpRespPal)) {
        return true;
      }
      else if (A2LOutlineNatFilter.this.selectedResponsible != null) {
        if (A2LOutlineNatFilter.this.selectedResponsible.equals(ApicConstants.WP_RESPONSIBILITY)) {
          return true;
        }
        if ((wpRespPal.getA2lRespId() != null) && A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel()
            .getA2lResponsibilityMap().containsKey(wpRespPal.getA2lRespId())) {
          return isWpResponsibility(wpRespPal);
        }
      }
      else {
        return wpRespPal.getId().equals(A2LOutlineNatFilter.this.selectedWpId);
      }
      return false;
    }

    /**
     * @param wpRespPal
     * @return
     */
    private boolean isWpResponsibility(final A2lWpResponsibility wpRespPal) {
      A2lResponsibility a2lResp = A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel()
          .getA2lResponsibilityMap().get(wpRespPal.getA2lRespId());
      if (a2lResp.getUserId() != null) {
        User user =
            A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel().getUserMap().get(a2lResp.getUserId());
        if (user != null) {
          return user.getDescription().equals(A2LOutlineNatFilter.this.selectedResponsible);
        }
      }
      if ((null != a2lResp.getLFirstName()) || (null != a2lResp.getLLastName()) || (null != a2lResp.getLDepartment())) {
        return A2LOutlineNatFilter.this.a2lwpInfoBO.getLFullName(a2lResp)
            .equals(A2LOutlineNatFilter.this.selectedResponsible);
      }
      String respType = A2LOutlineNatFilter.this.a2lwpInfoBO.getRespTypeName(a2lResp);
      return CommonUtils.isEqual(respType, A2LOutlineNatFilter.this.selectedResponsible);
    }

    /**
     * @param wpRespPal
     * @return
     */
    private boolean isWpDefPage(final A2lWpResponsibility wpRespPal) {
      return (A2LOutlineNatFilter.this.selectedNode != null) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap()
              .containsKey(A2LOutlineNatFilter.this.selectedNode) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap()
              .get(A2LOutlineNatFilter.this.selectedNode).contains(wpRespPal);
    }

    private boolean filterWpLabelsBasedOnResp(final A2LWpParamInfo a2lWpParamInfo) {
      if ((a2lWpParamInfo.getA2lRespId() != null) && (a2lWpParamInfo.getWpRespId() != null) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getWpRespMap()
              .containsKey(a2lWpParamInfo.getWpRespId()) &&
          A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel().getA2lResponsibilityMap()
              .containsKey(A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getWpRespMap()
                  .get(a2lWpParamInfo.getWpRespId()).getA2lRespId())) {

        if (!a2lWpParamInfo.isWpRespInherited()) {
          A2lResponsibility a2lResponsibility = A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel()
              .getA2lResponsibilityMap().get(a2lWpParamInfo.getA2lRespId());
          return CommonUtils.isEqual(a2lResponsibility.getName(), A2LOutlineNatFilter.this.selectedResponsible);
        }

        A2lResponsibility a2lResp = A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lResponsibilityModel()
            .getA2lResponsibilityMap().get(A2LOutlineNatFilter.this.a2lwpInfoBO.getA2lWpDefnModel().getWpRespMap()
                .get(a2lWpParamInfo.getWpRespId()).getA2lRespId());
        return CommonUtils.isEqual(a2lResp.getName(), A2LOutlineNatFilter.this.selectedResponsible);
      }
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
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      resetAllFlagValues();
      final Object first = ((IStructuredSelection) selection).getFirstElement();

      // Check if selected node is a Function
      if (first instanceof Function) {
        setFCFlag(true);
        final Function func = (Function) first;
        this.selectedNode = func.getName();
        this.selectedWpId = null;
        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.allNotAssigned = false;
        setSelectedBC(new A2LBaseComponents());
        refreshFilter(false);
      }
      else if (first instanceof A2LBaseComponents) {
        setBCFlag(true);
        final A2LBaseComponents baseComp = (A2LBaseComponents) first;
        setSelectedBC(baseComp);
        this.selectedNode = baseComp.getBcName();
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      // Icdm-586
      else if (first instanceof A2LParameter) {
        setSpecialFunFalg(true);
        final A2LParameter charateristics = (A2LParameter) first;
        this.selectedNode = charateristics.getName();
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if (first instanceof CompPackage) {
        setCompPkg(true);
        setSelectedCompPkg((CompPackage) first);
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if (first instanceof RuleSet) {
        setRuleSet(true);
        final RuleSet ruleSet = (RuleSet) first;
        setSelectedRuleSet(ruleSet);
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if (first instanceof A2LBaseComponentFunctions) {
        setBCFCFlag(true);
        final A2LBaseComponentFunctions func = (A2LBaseComponentFunctions) first;
        this.selectedNode = func.getName();
        this.allNotAssigned = false;
        this.selectedWpId = null;
        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        setSelectedBC(new A2LBaseComponents());
        refreshFilter(false);
      }
      else if (first instanceof A2lWorkPackageGroup) {
        setWPGroupFlag(true);
        final A2lWorkPackageGroup wpPkgGrp = (A2lWorkPackageGroup) first;
        setSelectedWorkPkgGrp(wpPkgGrp);
        this.selectedNode = wpPkgGrp.getGroupName();
        this.allNotAssigned = false;
        refreshFilter(false);
      }

      // ICDM-209 and ICDM-210
      else if (first instanceof A2lWpObj) {
        setWPFlag(true);
        final A2lWpObj wpPackage = (A2lWpObj) first;
        setSelectedWp(wpPackage);
        final StringBuilder txt = new StringBuilder(wpPackage.getWpGroupName() + ":" + wpPackage.getWpName());
        this.selectedNode = txt.toString();
        this.allNotAssigned = false;
        refreshFilter(false);
      }

      else if (first instanceof A2LGroup) {
        setWPFlag(true);
        final A2LGroup a2lGrp = (A2LGroup) first;
        setSelectedA2lGroup(a2lGrp);
        this.selectedNode = a2lGrp.getGroupName();
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      // ICDM-2272
      // Clear All Filters For FC,WP,BC
      else if (checkClearAllFilters(first)) {
        clearFilter();
      }

      else if ((first instanceof String) && (first.equals(ApicConstants.UNASSIGNED_PARAM))) {
        setSpecialFunFalg(true);
        this.allNotAssigned = true;
        this.selectedNode = ApicConstants.UNASSIGNED_PARAM;
        refreshFilter(false);
      }
      else if ((first instanceof String) && (ApicConstants.A2L_WORK_PKG.equals(first))) {
        setWPFlag(true);

        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = ApicConstants.A2L_WORK_PKG;
        refreshFilter(true);
      }
      else if (isRESPNode(first)) {
        setWPFlag(true);

        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = ApicConstants.WP_RESPONSIBILITY;
        refreshFilter(true);
      }
      // A2l Responsibility Type Selection
      else if (isResponsibilityTypeNode(first)) {
        setWPFlag(true);
        this.selectedResponsible = null;
        this.selectedResponsibilityType = (String) first;
        this.selectedWpId = null;
        this.selectedNode = null;
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if (isResponsiblityNode(first)) {
        setWPFlag(true);
        this.selectedResponsible = ((A2lResponsibility) first).getName();
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = null;
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if ((first instanceof String) &&
          (this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap().containsKey(first))) {
        setWPFlag(true);
        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = (String) first;
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if (first instanceof String) {
        setGrpFunctionFlag(true);
        setFunName((String) first);
        this.selectedNode = (String) first;
        this.allNotAssigned = false;
        refreshFilter(false);
      }
      else if (first instanceof A2lWpResponsibility) {
        A2lWpResponsibility a2lWpResponsibility = (A2lWpResponsibility) first;
        setA2lWpResponsibility(a2lWpResponsibility);
      }
      // clear the filters for all Data Types
      else {
        clearFilter();
      }
    }


  }

  /**
   * @param first
   * @return
   */
  private boolean isResponsiblityNode(final Object first) {
    return (first instanceof A2lResponsibility) && (this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap()
        .containsKey(((A2lResponsibility) first).getName()) ||
        this.a2lwpInfoBO.getA2lWpParamMappingModel().getA2lWPResponsibleMap()
            .containsKey(((A2lResponsibility) first).getName()));
  }

  /**
   * @param first
   * @return
   */
  private boolean isResponsibilityTypeNode(final Object first) {
    return (first instanceof String) && (this.a2lwpInfoBO.getRespTypeAndRespMap().containsKey(first) ||
        this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam().containsKey(first) || ((this.cdrReportDataHandler != null) &&
            (this.cdrReportDataHandler.getRespTypeAndRespMap().containsKey(first))));
  }

  /**
   * @param first
   * @return
   */
  private boolean isRESPNode(final Object first) {
    return (first instanceof String) && (ApicConstants.WP_RESPONSIBILITY.equals(first));
  }

  /**
   * @param first
   * @return
   */
  private boolean checkClearAllFilters(final Object first) {
    return ((first instanceof A2LFile) && (((A2LFile) first).getFilename().equals(this.a2lFile.getFilename()))) ||
        ((first instanceof String) && (first.equals(ApicConstants.RULE_SET) || (first.equals(ApicConstants.FC_CONST) ||
            (first.equals(ApicConstants.BC_CONST) || first.equals(ApicConstants.COMP)))));
  }

  /**
   * @param a2lWpResponsibility
   */
  private void setA2lWpResponsibility(final A2lWpResponsibility a2lWpResponsibility) {
    if (this.cdrReportDataHandler != null) {
      this.selectedResponsible = a2lWpResponsibility.getMappedWpRespName();
      this.selectedResponsibilityType = null;
      // store wp id as selectedWpId ,only in data review report
      this.selectedWpId = a2lWpResponsibility.getA2lWpId();
      this.selectedNode = null;
    }
    else {
      // store wp resp id as selectedWpId
      if ((this.a2lwpInfoBO.getCurrentPage() == 3) || (this.a2lwpInfoBO.getCurrentPage() == 5)) {
        this.selectedResponsible = a2lWpResponsibility.getMappedWpRespName();
        this.selectedResponsibilityType = null;
        this.selectedWpId = a2lWpResponsibility.getId();
        this.selectedNode = null;
      }
      else if (this.a2lwpInfoBO.getCurrentPage() == 2) {
        this.selectedWpId = a2lWpResponsibility.getId();
        this.selectedResponsibilityType = null;
        this.selectedNode = null;
        this.selectedResponsible = null;
      }
    }
    refreshFilter(false);
  }

  /**
   *
   */
  private void clearFilter() {

    this.selectedNode = "";
    this.selectedWpId = null;
    this.selectedResponsible = null;
    this.selectedResponsibilityType = null;
    this.allNotAssigned = false;
    refreshFilter(true);
  }

  /**
   *
   */
  private void refreshFilter(final boolean flag) {
    this.filterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(flag);
    this.filterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
  }


  /**
   * @param ruleSet
   */
  private void setSelectedRuleSet(final RuleSet ruleSet) {
    this.selRuleSet = ruleSet;

  }

  private void resetAllFlagValues() {
    setFCFlag(true);
    setBCFCFlag(true);
    setBCFlag(true);
    setWPFlag(true);
    setWPGroupFlag(true);
    setGrpFunctionFlag(true);
    setSpecialFunFalg(true);
    setCompPkg(true);
    setRuleSet(true);
  }

  /**
   * @param isRuleSet
   */
  private void setRuleSet(final boolean isRuleSet) {
    this.isRuleSet = isRuleSet;
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
    this.notAssignedFun = specialFun == null ? null : new TreeSet<>(specialFun);

  }

  /**
   * @return .
   */
  public Matcher<A2LParameter> getOutlineMatcher() {

    return new A2lParamMatcher<>();
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
   * Gets the wp and grp name.
   *
   * @param a2lWpResp the a 2 l wp resp
   * @return the wp Grp name
   */
  public String getWpAndGrpName(final A2LWpRespExt a2lWpResp) {
    return CommonUtils.concatenate(a2lWpResp.getWpResource(), ":", a2lWpResp.getWorkPackage().getName());
  }

}
