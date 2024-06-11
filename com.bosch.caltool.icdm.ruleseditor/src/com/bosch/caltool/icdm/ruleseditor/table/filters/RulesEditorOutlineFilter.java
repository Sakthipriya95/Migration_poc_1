/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
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
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * icdm-2266 outline filter for rules editor
 *
 * @author mkl2cob
 */
public class RulesEditorOutlineFilter extends AbstractViewerFilter {

  // Holds the filter text
  private String selectedNode = "";

  private Long selectedWpId;

  private String selectedResponsible;

  private String selectedResponsibilityType;

  // members to indicate type of filters
  private boolean isFunctionComp;
  /**
   * bc'S function
   */
  private boolean bCFC;
  /**
   * base component
   */
  private boolean isBaseComp;
  /**
   * wp flag
   *
   * @deprecated not used
   */
  @Deprecated
  private boolean wpFlag;
  /**
   * selected base component
   */
  private A2LBaseComponents selectedBC;

  /**
   * selected wp
   */
  private A2lWpObj workPackage;

  /**
   * A2LGroup
   */
  private A2LGroup a2lGroup;

  // icdm-272
  /**
   * is Wp group
   *
   * @deprecated not used
   */
  @Deprecated
  private boolean isWpGroup;

  /**
   * selected wp group
   *
   * @deprecated not used
   */
  @Deprecated
  private A2lWorkPackageGroup workPkgGrp;

  /**
   * group function
   */
  private boolean isGrpFun;

  /**
   * group function name
   */
  private String grpFunName;

  /**
   * is non defined FC
   */
  private boolean isNonDefinedFC;

  /**
   * all parameters not assigned
   */
  private boolean allNotAssigned;

  /**
   * ReviewParamEditorInput
   */
  private final ReviewParamEditorInput editorInput;

  /**
   * flag indicates to show all parameters of A2L file when true
   */
  private boolean isA2LfileNode;
  private final CustomFilterGridLayer<T> filterGridLayer;

  private A2LWPInfoBO a2lwpInfoBO = null;

  /**
   * Constructor
   *
   * @param customFilterGridLayer CustomFilterGridLayer
   * @param editorInput ReviewParamEditorInput
   */
  public RulesEditorOutlineFilter(final CustomFilterGridLayer customFilterGridLayer,
      final ReviewParamEditorInput editorInput) {
    this.editorInput = editorInput;
    this.filterGridLayer = customFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    if (element instanceof RuleSetParameter) {
      RuleSetParameter funcParam = (RuleSetParameter) element;
      // Filtering based on Wp selected inside a Resp in outline view
      Long paramId = funcParam.getParamId();
      return filterWorkpackageAndResp(funcParam.getName(), paramId);
    }
    // Check if is Function Parameters
    else if (element instanceof IParameter) {
      IParameter funcParam = (IParameter) element;
      // Filtering based on Wp selected inside a Resp in outline view
      Long paramId = funcParam.getId();
      return filterWorkpackageAndResp(funcParam.getName(), paramId);
    }
    if (element instanceof ConfigBasedParam) {
      final ConfigBasedParam funcParam = (ConfigBasedParam) element;
      return isValidParam(funcParam.getParameter().getName());
    }
    if (element instanceof ReviewRule) {
      final ReviewRule cdrRule = (ReviewRule) element;
      return isValidParam(cdrRule.getParameterName());
    }
    // for root node selection
    return true;


  }

  /**
   * @param funcParam
   * @param paramId
   * @return
   */
  private boolean filterWorkpackageAndResp(final String paramName, final Long paramId) {

    if (isRespType(paramId)) {
      Set<A2lResponsibility> a2lResponsibilitySet =
          this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam().get(this.selectedResponsibilityType);
      for (A2lResponsibility a2lResponsibility : a2lResponsibilitySet) {
        if (this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap().get(paramId)
            .containsValue(a2lResponsibility.getName())) {
          return true;
        }
      }
    }

    Map<Long, String> wpRespMap =
        this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap().get(paramId);
    if (isResponsible(wpRespMap)) {
      return true;
    }

    Set<A2lWpResponsibility> wpRespMergedMap =
        this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap().get(this.selectedNode);
    // Filtering in A2l Param page based on Wp selection
    if ((this.selectedNode != null) && (wpRespMergedMap != null) && (wpRespMap != null)) {
      Long wpId = (Long) wpRespMap.keySet().toArray()[0];
      for (A2lWpResponsibility wpResp : wpRespMergedMap) {
        if (wpResp.getId().equals(wpId)) {
          return true;
        }
      }
    }
    if (isRespOrWp(wpRespMap)) {
      return true;
    }


    return isValidParam(paramName);
  }


  /**
   * @param wpRespMap
   * @return
   */
  private boolean isRespOrWp(final Map<Long, String> wpRespMap) {
    return (wpRespMap != null) &&
        (((this.selectedWpId != null) && wpRespMap.containsKey(this.selectedWpId)) || (this.selectedWpId == null)) &&
        ((this.selectedResponsible != null) && wpRespMap.containsValue(this.selectedResponsible));
  }

  /**
   * @param wpRespMap
   * @return
   */
  private boolean isResponsible(final Map<Long, String> wpRespMap) {
    return (wpRespMap != null) && (this.selectedWpId != null) && wpRespMap.containsKey(this.selectedWpId) &&
        (this.selectedResponsible != null) && wpRespMap.containsValue(this.selectedResponsible);
  }

  /**
   * @param paramId
   * @return
   */
  private boolean isRespType(final Long paramId) {
    return this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap().containsKey(paramId) &&
        (this.selectedResponsibilityType != null) &&
        (this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam().containsKey(this.selectedResponsibilityType));
  }

  /**
   * @param paramName
   * @return
   */
  private boolean isValidParam(final String paramName) {

    if (ApicConstants.WP_RESPONSIBILITY.equals(this.selectedNode) ||
        ApicConstants.A2L_WORK_PKG.equals(this.selectedNode)) {
      return true;
    }

    if (this.editorInput.getA2lDataProvider() != null) {

      ConcurrentMap<String, ConcurrentMap<String, Characteristic>> varCodedMap =
          this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getVarCodedMap();
      // variant coded parameters
      // ICDM-2270
      ConcurrentMap<String, Characteristic> varCodedCollection = null;
      if (varCodedMap != null) {
        varCodedCollection = varCodedMap.get(paramName);
        if ((varCodedCollection != null) && this.isA2LfileNode) {
          return true;
        }
      }
      A2LParameter a2lParameter =
          this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getA2lParamMap(null).get(paramName);
      Function defFunction = null;

      if (null != a2lParameter) {
        if (this.isA2LfileNode) {
          return true;
        }
        defFunction = a2lParameter.getDefFunction();
      }

      return isValidOtherParam(paramName, varCodedCollection, a2lParameter, defFunction);
    }
    return false;
  }

  /**
   * @param paramName
   * @param varCodedCollection
   * @param a2lParameter
   * @param defFunction
   * @return
   */
  private boolean isValidOtherParam(final String paramName,
      final ConcurrentMap<String, Characteristic> varCodedCollection, final A2LParameter a2lParameter,
      final Function defFunction) {
    if (this.isFunctionComp || this.bCFC) {
      return filterFCBC(defFunction, varCodedCollection);
    }
    else if (this.isNonDefinedFC) {
      return setFilterSpf(paramName);
    }

    else if (this.isBaseComp) {
      return setFilterForBcCharacteristics(defFunction, varCodedCollection);
    }
    else if (this.wpFlag) {
      return setWpfilterforCharecteristics(paramName, defFunction, varCodedCollection);
    }
    else if (this.isWpGroup) {
      return setWpGroupFilterForChar(defFunction, varCodedCollection);
    }
    else if (this.isGrpFun) {
      return setCharFilter(a2lParameter, varCodedCollection);
    }
    else {
      return false;
    }
  }

  /**
   * icdm-272
   *
   * @param defFunction
   * @param varCodedCollection
   * @return
   * @deprecated not used
   */
  @Deprecated
  private boolean setWpGroupFilterForChar(final Function defFunction,
      final ConcurrentMap<String, Characteristic> varCodedCollection) {
    if (null == varCodedCollection) {
      return filterWPGrpForNonVarCodedParams(defFunction);
    }
    return filterWPGrpForVarCodedParams(varCodedCollection);
  }

  /**
   * @param varCodedCollection
   * @return
   * @deprecated not used
   */
  @Deprecated
  private boolean filterWPGrpForVarCodedParams(final ConcurrentMap<String, Characteristic> varCodedCollection) {
    final Map<String, Long> workPackageMap = this.workPkgGrp.getWorkPackageMap();
    for (Entry<String, Long> pack : workPackageMap.entrySet()) {
      final Map<String, String> functionMap = this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getA2lWpMapping()
          .getWpMap().get(pack.getValue()).getFunctionMap();
      for (String funcName : varCodedCollection.keySet()) {
        if (functionMap.get(funcName) != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param defFunction
   * @return
   * @deprecated not used
   */
  @Deprecated
  private boolean filterWPGrpForNonVarCodedParams(final Function defFunction) {
    final Map<String, Long> workPackageMap = this.workPkgGrp.getWorkPackageMap();
    for (Entry<String, Long> pack : workPackageMap.entrySet()) {
      final Map<String, String> functionMap = this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getA2lWpMapping()
          .getWpMap().get(pack.getValue()).getFunctionMap();
      if ((defFunction != null) && (functionMap.get(defFunction.getName()) != null)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param varCodedCollection ConcurrentMap<String, Characteristic>
   * @param defFun Function
   */
  private boolean filterFCBC(final Function defFunc, final ConcurrentMap<String, Characteristic> varCodedCollection) {
    if (varCodedCollection == null) {
      return filterFCBCForNonVarCodeParams(defFunc);
    }
    return filterFCBCForVarCodeParams(varCodedCollection);
  }

  /**
   * @param varCodedCollection
   * @return
   */
  private boolean filterFCBCForVarCodeParams(final ConcurrentMap<String, Characteristic> varCodedCollection) {
    for (String funcName : varCodedCollection.keySet()) {
      if (this.selectedNode.equals(funcName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param defFunc
   */
  private boolean filterFCBCForNonVarCodeParams(final Function defFunc) {
    return (defFunc != null) && this.selectedNode.equals(defFunc.getName());
  }

  /**
   * Sets the filter text
   *
   * @param text text to search
   */
  @Override
  public void setFilterText(final String text) {
    this.selectedNode = text;
    setFilterText(text, false);
  }

  /**
   * @param paramName
   */
  private boolean setFilterSpf(final String paramName) {
    if (isAllNotAssigned()) {
      return showNotAssignedFC(paramName);
    }
    return (paramName != null) && this.selectedNode.equals(paramName);
  }

  /**
   * Show all the Not Assigned Fc's
   *
   * @param paramName
   */
  private boolean showNotAssignedFC(final String paramName) {
    SortedSet<A2LParameter> unassignedParams =
        this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getUnassignedParams();
    if ((unassignedParams != null) && !unassignedParams.isEmpty()) {
      for (A2LParameter characteristics : unassignedParams) {
        if (CommonUtils.isEqual(characteristics.getName(), paramName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param a2lParameter
   * @param varCodedCollection
   */
  private boolean setCharFilter(final A2LParameter a2lParameter,
      final ConcurrentMap<String, Characteristic> varCodedCollection) {
    if (null == varCodedCollection) {
      return filterCharForNonVarCodedParam(a2lParameter);
    }
    return filterGrpFunForVarCodedParam(varCodedCollection);
  }

  /**
   * @param varCodedCollection
   * @return
   */
  private boolean filterGrpFunForVarCodedParam(final ConcurrentMap<String, Characteristic> varCodedCollection) {
    final String funName = this.grpFunName;
    for (Entry<String, Characteristic> param : varCodedCollection.entrySet()) {
      String funcName = param.getKey();
      if (((!funcName.equals(ApicConstants.UNASSIGNED_PARAM)) && funcName.equals(funName)) ||
          (checkForGrpMapping(funName, funcName))) {
        return true;
      }
    }
    return false;
  }

  private boolean checkForGrpMapping(final String funName, final String funcName) {

    if (funcName.equals(funName) || funName.equals(ApicConstants.WORKPACKAGE)) {
      return true;
    }

    Long grpMappingId = null;
    try {
      grpMappingId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.GROUP_MAPPING_ID));
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return (CommonUtils.isEqual(this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getMappingSourceID(),
        grpMappingId)) && (this.a2lGroup != null) && funName.equals(this.a2lGroup.getRootName());
  }

  /**
   * @param a2lParameter
   * @return
   */
  private boolean filterCharForNonVarCodedParam(final A2LParameter a2lParameter) {
    if (a2lParameter == null) {
      return false;
    }

    final String funName = this.grpFunName;
    if (((a2lParameter.getDefFunction() != null) && a2lParameter.getDefFunction().getName().equals(funName)) ||
        a2lParameter.getName().equals(funName) || funName.equals(ApicConstants.WORKPACKAGE)) {
      return true;
    }

    Long grpMappingId = null;
    try {
      grpMappingId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.GROUP_MAPPING_ID));
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return CommonUtils.isEqual(this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getMappingSourceID(),
        grpMappingId) && (this.a2lGroup != null) && funName.equals(this.a2lGroup.getRootName());
  }

  /**
   * @param defFunc
   * @param varCodedCollection
   */
  private boolean setFilterForBcCharacteristics(final Function defFunc,
      final ConcurrentMap<String, Characteristic> varCodedCollection) {
    if (varCodedCollection == null) {
      return filterBCForNonVarCodedParams(defFunc);
    }
    return filterBCForVarCodedParams(varCodedCollection);
  }

  /**
   * @param varCodedCollection
   * @return
   */
  private boolean filterBCForVarCodedParams(final ConcurrentMap<String, Characteristic> varCodedCollection) {
    if (this.selectedBC != null) {
      for (String funcName : varCodedCollection.keySet()) {
        for (A2LBaseComponentFunctions a2lBaseComponentFunctions : this.selectedBC.getFunctionsList()) {
          if (funcName.equals(a2lBaseComponentFunctions.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param defFunc
   * @return
   */
  private boolean filterBCForNonVarCodedParams(final Function defFunc) {
    if ((this.selectedBC != null) && (defFunc != null)) {
      for (A2LBaseComponentFunctions a2lBaseComponentFunctions : this.selectedBC.getFunctionsList()) {
        if (defFunc.getName().equals(a2lBaseComponentFunctions.getName())) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @param paramName
   * @param defFunc
   * @param varCodedCollection
   * @deprecated not used
   */
  @Deprecated
  private boolean setWpfilterforCharecteristics(final String paramName, final Function defFunc,
      final ConcurrentMap<String, Characteristic> varCodedCollection) {
    if (varCodedCollection == null) {
      return filterWPForNonVarCodedParams(paramName, defFunc);
    }
    return filterWPForVarCodedParams(defFunc, varCodedCollection);
  }

  /**
   * @param defFunc
   * @param varCodedCollection
   * @return
   * @deprecated not used
   */
  @Deprecated
  private boolean filterWPForVarCodedParams(final Function defFunc,
      final ConcurrentMap<String, Characteristic> varCodedCollection) {
    Long mappingSourceId = null;
    try {
      mappingSourceId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.GROUP_MAPPING_ID));
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, com.bosch.caltool.icdm.common.util.Activator.PLUGIN_ID);
    }
    if (CommonUtils.isEqual(this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getMappingSourceID(),
        mappingSourceId)) {
      // Soanr Qube fix made for not null
      if (CommonUtils.isNotNull(this.a2lGroup)) {
        // Soanr Qube fix for checking empty map.
        if (CommonUtils.isNotEmpty(this.a2lGroup.getSubGrpMap())) {
          List<String> subGrpMap = this.a2lGroup.getSubGrpMap().get(this.a2lGroup.getGroupName());
          for (String subGrpString : subGrpMap) {
            final Map<String, String> labelMap = this.editorInput.getA2lDataProvider().getA2lFileInfoBO()
                .getA2lWpMapping().getA2lGrpMap().get(subGrpString).getLabelMap();
            // icdm-2270
            for (Characteristic param : varCodedCollection.values()) {
              if (labelMap.get(param.getName()) != null) {
                return true;
              }
            }
          }
        }
        else {
          final Map<String, String> labelMap = this.a2lGroup.getLabelMap();
          // icdm-2270
          for (Characteristic param : varCodedCollection.values()) {
            final String labelName = labelMap.get(param.getName());
            if (labelName != null) {
              return true;
            }
          }
        }
      }
    }
    else {
      if ((this.workPackage != null) && (defFunc != null)) {
        final Map<String, String> functionMap = this.workPackage.getFunctionMap();
        for (String funcName : varCodedCollection.keySet()) {
          // icdm-2270
          final String functionName = functionMap.get(funcName);
          if (functionName != null) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * @param paramName
   * @param defFunc
   * @return
   * @deprecated not used
   */
  @Deprecated
  private boolean filterWPForNonVarCodedParams(final String paramName, final Function defFunc) {
    Long mappingSourceId = null;
    try {
      mappingSourceId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.GROUP_MAPPING_ID));
    }
    catch (NumberFormatException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, com.bosch.caltool.icdm.common.util.Activator.PLUGIN_ID);
    }
    if (CommonUtils.isEqual(this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getMappingSourceID(),
        mappingSourceId)) {
      if (CommonUtils.isNotNull(this.a2lGroup)) {
        if (CommonUtils.isNotEmpty(this.a2lGroup.getSubGrpMap())) {
          List<String> subGrpMap = this.a2lGroup.getSubGrpMap().get(this.a2lGroup.getGroupName());
          for (String subGrpString : subGrpMap) {
            final Map<String, String> labelMap = this.editorInput.getA2lDataProvider().getA2lFileInfoBO()
                .getA2lWpMapping().getA2lGrpMap().get(subGrpString).getLabelMap();
            if (labelMap.get(paramName) != null) {
              return true;
            }
          }
        }
        else {
          final Map<String, String> labelMap = this.a2lGroup.getLabelMap();
          final String labelName = labelMap.get(paramName);
          if (labelName != null) {
            return true;
          }
        }
      }
    }
    else {
      if ((this.workPackage != null) && (defFunc != null)) {
        final Map<String, String> functionMap = this.workPackage.getFunctionMap();
        final String functionName = functionMap.get(defFunc.getName());
        if (functionName != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Icdm-469 Selection listener implementation for selections on outlineFilter
   *
   * @param selection selection
   */
  public void a2lOutlineSelectionListener(final ISelection selection) {
    if ((this.editorInput.getA2lDataProvider() != null)) {
      this.a2lwpInfoBO = this.editorInput.getA2lDataProvider().getA2lWpInfoBO();
    }
    // Validate and get the selections
    if ((selection == null) || selection.isEmpty()) {// ICDM-2270
      // for empty selection , show all parameters
      this.setFilterText("");
    }
    else if (selection instanceof IStructuredSelection) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      this.isA2LfileNode = false;
      // Check if selected node is a Function
      if (first instanceof Function) {
        setFlagValues(true, false, false, false, false, false, false);
        final Function func = (Function) first;
        this.setFilterText(func.getName());
        this.allNotAssigned = false;
        applyOutlineFilter(false);
      }
      else if (first instanceof A2LBaseComponents) {
        // if selected node is a A2LBaseComponents
        setFlagValues(false, false, true, false, false, false, false);
        final A2LBaseComponents baseComp = (A2LBaseComponents) first;
        setSelectedBC(baseComp);
        this.setFilterText(baseComp.getBcName());
        this.allNotAssigned = false;
        applyOutlineFilter(false);
      }
      // Icdm-586
      else if (first instanceof A2LParameter) {
        // if selected node is a A2LParameter
        setFlagValues(false, false, false, false, false, false, true);
        final A2LParameter charateristics = (A2LParameter) first;
        this.setFilterText(charateristics.getName());
        this.allNotAssigned = false;
        applyOutlineFilter(false);
      }
      else if (first instanceof A2LBaseComponentFunctions) {
        // if selected node is a A2LBaseComponentFunctions
        setFlagValues(false, true, false, false, false, false, false);
        final A2LBaseComponentFunctions func = (A2LBaseComponentFunctions) first;
        this.setFilterText(func.getName());
        this.allNotAssigned = false;
        applyOutlineFilter(false);
      }
      else if (first instanceof A2lWorkPackageGroup) {
        // if selected node is a WorkPackageGroup
        setFlagValues(false, false, false, false, true, false, false);
        final A2lWorkPackageGroup wpPkgGrp = (A2lWorkPackageGroup) first;
        setSelectedWorkPkgGrp(wpPkgGrp);
        this.setFilterText(wpPkgGrp.getGroupName());
        this.allNotAssigned = false;
        applyOutlineFilter(false);
      }
      else if ((first instanceof String) && (ApicConstants.A2L_WORK_PKG.equals(first))) {
        setFlagValues(false, false, false, false, false, false, false);

        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = ApicConstants.A2L_WORK_PKG;
        this.setFilterText(this.selectedNode);
        applyOutlineFilter(true);
      }
      else if ((first instanceof String) && (ApicConstants.WP_RESPONSIBILITY.equals(first))) {
        setFlagValues(false, false, false, false, false, false, false);

        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = ApicConstants.WP_RESPONSIBILITY;
        this.setFilterText(this.selectedNode);
        applyOutlineFilter(true);
      }
      else if ((first instanceof String) && (this.a2lwpInfoBO.getRespTypeAndRespMap().containsKey(first) ||
          this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam().containsKey(first))) {
        setFlagValues(false, false, false, false, false, false, false);
        this.selectedResponsibilityType = (String) first;
        this.selectedResponsible = null;
        this.selectedWpId = null;
        this.selectedNode = null;
        this.allNotAssigned = false;
        this.setFilterText(this.selectedResponsibilityType);
        applyOutlineFilter(false);
      }
      else if ((first instanceof A2lResponsibility) && (this.a2lwpInfoBO != null) &&
          (this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap()
              .containsKey(((A2lResponsibility) first).getName()) ||
              this.a2lwpInfoBO.getA2lWpParamMappingModel().getA2lWPResponsibleMap()
                  .containsKey(((A2lResponsibility) first).getName()))) {
        setFlagValues(false, false, false, false, false, false, false);
        this.selectedResponsible = ((A2lResponsibility) first).getName();
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = null;
        this.allNotAssigned = false;
        this.setFilterText(this.selectedResponsible);
        applyOutlineFilter(false);
      }
      else if ((first instanceof String) && (this.a2lwpInfoBO != null) &&
          (this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap().containsKey(first))) {
        setFlagValues(false, false, false, false, false, false, false);
        this.selectedResponsible = null;
        this.selectedResponsibilityType = null;
        this.selectedWpId = null;
        this.selectedNode = (String) first;
        this.allNotAssigned = false;
        this.setFilterText(this.selectedNode);
        applyOutlineFilter(false);
      }
      else if (first instanceof String) {
        setFlagValues(false, false, false, false, false, false, false);
        setFunName((String) first);
        this.selectedNode = (String) first;
        this.allNotAssigned = false;
        this.setFilterText(this.selectedNode);
        applyOutlineFilter(false);
      }
      else if ((first instanceof A2lWpResponsibility) && (this.a2lwpInfoBO != null)) {
        setFlagValues(false, false, false, false, false, false, false);

        A2lWpResponsibility a2lWpResponsibility = (A2lWpResponsibility) first;
        if ((this.a2lwpInfoBO.getCurrentPage() == 3) || (this.a2lwpInfoBO.getCurrentPage() == 5)) {
          this.selectedResponsible = a2lWpResponsibility.getMappedWpRespName();
          this.selectedWpId = a2lWpResponsibility.getId();
          this.selectedNode = null;
        }
        else if (this.a2lwpInfoBO.getCurrentPage() == 2) {
          this.selectedWpId = a2lWpResponsibility.getId();
          this.selectedNode = null;
          this.selectedResponsible = null;
        }
        this.selectedResponsibilityType = null;
        applyOutlineFilter(false);
      }
      else {
        setValuesOnOutlineFilterSel(first);
      }
    }


  }

  /**
   *
   */
  private void applyOutlineFilter(final boolean filterFlag) {
    if (null != this.filterGridLayer) {
      this.filterGridLayer.getFilterStrategy().applyOutlineFilterInAllColumns(filterFlag);
      this.filterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
    }
  }


  private void setValuesOnOutlineFilterSel(final Object first) {
    // ICDM-209 and ICDM-210
    if (first instanceof A2lWpObj) {
      // if selected node is a WorkPackage
      setFlagValues(false, false, false, true, false, false, false);
      final A2lWpObj wpPackage = (A2lWpObj) first;
      setSelectedWp(wpPackage);
      final StringBuilder txt = new StringBuilder(50);
      txt.append(wpPackage.getWpGroupName()).append(":").append(wpPackage.getWpName());
      this.setFilterText(txt.toString());
      this.allNotAssigned = false;
      applyOutlineFilter(false);
    }

    else if (first instanceof A2LGroup) {
      // if selected node is a A2LGroup
      setFlagValues(false, false, false, true, false, false, false);
      final A2LGroup a2lGrp = (A2LGroup) first;
      setSelectedA2lGroup(a2lGrp);
      this.setFilterText(a2lGrp.getGroupName());
      this.allNotAssigned = false;
      applyOutlineFilter(false);
    }
    else if ((first instanceof A2LFile) &&
        ((A2LFile) first).getFilename().equals(this.editorInput.getA2lFile().getFilename())) {
      setFlagValues(false, false, false, false, false, false, false);
      this.setFilterText(((A2LFile) first).getFilename());
      this.isA2LfileNode = true;
      this.allNotAssigned = false;
      applyOutlineFilter(false);
    }
    // Clear All Filters For FC,WP,BC
    else if ((first instanceof String) && (first.equals(ApicConstants.WORKPACKAGE) ||
        (first.equals(ApicConstants.FC_CONST) || (first.equals(ApicConstants.BC_CONST))))) {

      setFlagValues(false, false, false, false, false, false, false);
      this.setFilterText((String) first);
      this.isA2LfileNode = true;
      this.allNotAssigned = false;
      applyOutlineFilter(false);
    }
    else if ((first instanceof String) && (first.equals(ApicConstants.UNASSIGNED_PARAM))) {
      // if selected node is a UNASSIGNED_PARAM
      setFlagValues(false, false, false, false, false, false, true);
      this.allNotAssigned = true;
      this.setFilterText(ApicConstants.UNASSIGNED_PARAM);
      applyOutlineFilter(false);
    }
    else if (first instanceof String) {
      // if selected node is a String
      setFlagValues(false, false, false, false, false, true, false);
      setFunName((String) first);
      this.setFilterText((String) first);
      this.allNotAssigned = false;
      applyOutlineFilter(false);
    }
    // clear the filters for all Data Types
    else {
      setFlagValues(false, false, false, false, false, false, false);
      this.setFilterText("");
      this.allNotAssigned = false;
      applyOutlineFilter(false);
    }
  }

  /**
   * icdm-272 Set al flag values
   */

  private void setFlagValues(final boolean func, final boolean fcBC, final boolean bcFlag, final boolean wPkg,
      final boolean wpGroup, final boolean grpFun, final boolean sfc) {
    setFCFlag(func);
    setBCFCFlag(fcBC);
    setBCFlag(bcFlag);
    setWPFlag(wPkg);
    setWPGroupFlag(wpGroup);
    setGrpFunctionFlag(grpFun);
    setSpecialFunFalg(sfc);
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
   * Sets if it is BC FUNCTION
   *
   * @param functionComp fc flag
   */
  public void setBCFCFlag(final boolean functionComp) {
    this.bCFC = functionComp;
  }


  /**
   * @param wp
   */
  private void setWPFlag(final boolean workPkg) {
    this.wpFlag = workPkg;
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
   * @param selectedBC the selectedBC to set
   */
  public void setSelectedBC(final A2LBaseComponents selectedBC) {
    this.selectedBC = selectedBC;
  }

  /**
   * @param wpGroup
   */
  private void setSelectedWp(final A2lWpObj workPackage) {
    this.workPackage = workPackage;
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
   * @return .
   */
  public Matcher getOutlineMatcher() {
    return new ParamMatcher<IParameter>();
  }

  private class ParamMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof IParameter) {
        return selectElement(element);
      }
      return false;
    }
  }
}
