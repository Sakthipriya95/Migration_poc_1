/**
 *
 */
package com.bosch.caltool.icdm.ui.table.filters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgBcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * OutlineFilter-This class handles the filters based on Outline view selection
 *
 * @author adn1cob
 */
public class OutlineFilter extends AbstractViewerFilter {

  // Holds the filter text
  private String selectedNode = "";

  // members to indicate type of filters
  private boolean isFunctionComp;
  private boolean bCFC;
  private boolean isBaseComp;
  private boolean wpFlag;
  private A2LBaseComponents selectedBC;
  private CompPackage selectedCompPkg;
  private A2lWpObj workPkg;

  private A2LGroup a2lGroup;

  private Long mappingSource;
  // icdm-272
  private boolean isWpGroup;

  private A2lWorkPackageGroup workPkgGroup;

  private boolean isGrpFun;

  private String grpFunName;

  private boolean isNonDefinedFC;

  private boolean allNotAssigned;

  private boolean compPkg;

  private SortedSet<A2LParameter> notAssignedFun;

  private final A2LContentsEditor editor;
  // Icdm-949 new collection for Comp Package Outline Selection
  private Map<String, A2LBaseComponents> a2lBcMap;

  private final Map<Long, Set<CompPkgBc>> compBcValuesMap = new HashMap<>();

  private final A2LContentsEditorInput editorInput;

  private final A2lWpMapping a2lWpMapping;


  /**
   * @param editor A2LContentsEditor
   */
  public OutlineFilter(final A2LContentsEditor editor) {
    this.editor = editor;
    this.editorInput = (A2LContentsEditorInput) this.editor.getEditorInput();
    this.a2lWpMapping = this.editorInput.getA2lFileInfoBO().getA2lWpMapping();
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
    return this.workPkgGroup;
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
    this.bCFC = functionComp;
  }

  /**
   * Checks if it is function of a BC
   *
   * @return boolean isFunction
   */
  public boolean isBCFC() {
    return this.bCFC;
  }

  /**
   * @param wp
   */
  // ICDM-209 and ICDM-210
  private void setWPFlag(final boolean workPkg) {
    // TODO Auto-generated method stub
    this.wpFlag = workPkg;
  }


  /**
   * @return the isWP
   */
  public boolean isWP() {
    return this.wpFlag;
  }

  /**
   * @param wpGroup
   */
  private void setSelectedWp(final A2lWpObj workPackage) {
    // TODO Auto-generated method stub
    this.workPkg = workPackage;
  }


  /**
   * @return the wpGroup
   */
  public A2lWpObj getSelectedWorkPackage() {
    return this.workPkg;
  }


  /**
   * {@inheritDoc}
   */
  @Override
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
    // ICDM-984
    else if (element instanceof A2LBaseComponents) {
      final A2LBaseComponents a2lBaseComp = (A2LBaseComponents) element;
      return isValidBc(a2lBaseComp);
    }
    // ICDM-209 and ICDM-210

    else if ((element instanceof A2lWpObj) && (this.wpFlag)) {
      final A2lWpObj workPackage = (A2lWpObj) element;
      final StringBuilder grpName = new StringBuilder(workPackage.getWpGroupName() + ":" + this.workPkg.getWpName());
      return grpName.toString().equals(this.selectedNode);
    }
    else if ((element instanceof A2lWpObj) && (this.isWpGroup)) {
      final A2lWpObj workPackage = (A2lWpObj) element;
      return workPackage.getWpGroupName().equals(this.selectedNode);
    }

    else if ((element instanceof A2LGroup) && (this.wpFlag)) {
      final A2LGroup a2lGrp = (A2LGroup) element;
      return a2lGrp.getGroupName().equals(this.selectedNode);
    }
    // for root node selection
    return true;

  }

  /**
   * ICDM-984
   *
   * @param a2lBaseComp A2LBaseComponents
   * @return true if BC is a part of the CompPkg
   */
  private boolean isValidBc(final A2LBaseComponents a2lBaseComp) {

    if (this.compPkg) {
      Set<CompPkgBc> compBcValues = this.compBcValuesMap.get(getSelectedCompPkg().getId());
      for (com.bosch.caltool.icdm.model.comppkg.CompPkgBc cmpBc : compBcValues) {
        if (cmpBc.getBcName().equals(a2lBaseComp.getBcName())) {
          return true;
        }
      }
      return false;
    }
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
        if (characteristics.getName() == funcParam.getName()) {
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
    if (this.isFunctionComp || this.bCFC) {
      return setFcBCFilter(funcParam);
    }
    else if (this.isNonDefinedFC) {
      return setFilterSpf(funcParam);
    }

    else if (this.isBaseComp) {
      return setFilterForBcCharacteristics(funcParam.getDefFunction());
    }
    else if (this.compPkg) {
      return setFilterForCompPkg(funcParam.getDefFunction());
    }
    // root node FC
    // ICDM-209 and ICDM-210
    else if (this.wpFlag) {
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
    if (((funcParam.getDefFunction() != null) && funcParam.getDefFunction().getName().equalsIgnoreCase(funName)) ||
        (funcParam.getName().equals(funName) || funName.equals(ApicConstants.WORKPACKAGE) ||
            ((CommonUtils.isEqual(this.mappingSource, this.editorInput.getA2lFileInfoBO().getWpRootGrpAttrValueId())) &&
                (getA2lGroup() != null) && funName.equals(getA2lGroup().getRootName())))) {
      return true;
    }
    return false;
  }


  /**
   * icdm-272
   *
   * @param defFunction
   * @return
   */
  private boolean setWpGroupFilterForChar(final Function defFunction) {
    final A2lWorkPackageGroup workPkgGrp = getWorkPkgGrp();
    final Map<String, Long> workPackage = workPkgGrp.getWorkPackageMap();
    for (Entry<String, Long> pack : workPackage.entrySet()) {
      A2lWpObj a2lWorkPackage = this.a2lWpMapping.getWpMap().get(pack.getValue());
      final Map<String, String> functionMap = a2lWorkPackage.getFunctionMap();
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
      for (A2LBaseComponentFunctions a2lBaseComponentFunctions : getSelectedBC().getFunctionsList()) {
        if (defFunc.getName().equals(a2lBaseComponentFunctions.getName())) {
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
      Set<CompPkgBc> compBcValues = this.compBcValuesMap.get(getSelectedCompPkg().getId());
      for (com.bosch.caltool.icdm.model.comppkg.CompPkgBc bcObj : compBcValues) {
        String bcName = bcObj.getBcName();
        if (bcObj.getFcList().isEmpty() && CommonUtils.isNotNull(this.a2lBcMap.get(bcName)) &&
            CommonUtils.isNotNull(this.a2lBcMap.get(bcName).getFunctionMap()) &&
            this.a2lBcMap.get(bcName).getFunctionMap().containsKey(defFunc.getName())) {
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
    if (CommonUtils.isEqual(this.mappingSource, this.editorInput.getA2lFileInfoBO().getWpRootGrpAttrValueId())) {
      if (getA2lGroup() != null) {
        if (!getA2lGroup().getSubGrpMap().isEmpty()) {
          List<String> a2lGrpList = getA2lGroup().getSubGrpMap().get(getA2lGroup().getGroupName());
          for (String a2lGrpStr : a2lGrpList) {
            final Map<String, String> labelMap =
                this.editorInput.getA2lFileInfoBO().getA2lWpMapping().getA2lGrpMap().get(a2lGrpStr).getLabelMap();
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
   * @param func
   */
  private boolean isValidFunction(final Function func) {
    if (this.isFunctionComp || this.bCFC) {
      if (func.getName().equalsIgnoreCase(this.selectedNode)) {
        return true;
      }

    }
    else if (this.isBaseComp) {
      return filterBcFcValues(func);

    } // root node FC
      // ICDM-209 and ICDM-210
    else if (this.wpFlag) {
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
    if ((funcParam.getName().equalsIgnoreCase(funName)) || (funName.equals(ApicConstants.WORKPACKAGE) ||
        (CommonUtils.isEqual(this.mappingSource, this.editorInput.getA2lFileInfoBO().getWpRootGrpAttrValueId())))) {
      return true;
    }
    return false;
  }


  /**
   * @param funcParam
   * @return
   */
  private boolean setWpGrpFilterForFunctions(final Function funcParam) {
    final A2lWorkPackageGroup workPkgGrp = getWorkPkgGrp();
    final Map<String, Long> workPackage = workPkgGrp.getWorkPackageMap();
    for (Entry<String, Long> pack : workPackage.entrySet()) {
      A2lWpObj a2lWorkPackage = this.a2lWpMapping.getWpMap().get(pack.getValue());
      final Map<String, String> functionMap = a2lWorkPackage.getFunctionMap();
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
    final A2lWpObj selectWorkPkg = getSelectedWorkPackage();
    if ((selectWorkPkg != null) && (selectWorkPkg.getSource() != null)) {
      final Map<String, String> functionMap = getSelectedWorkPackage().getFunctionMap();
      if (selectWorkPkg.getSource().equalsIgnoreCase(ApicConstants.FC_WP_MAPPING)) {
        final String functionName = functionMap.get(funcParam.getName());
        if (functionName != null) {
          return true;
        }
      }
      return false;
    }
    else if (CommonUtils.isEqual(this.mappingSource, this.editorInput.getA2lFileInfoBO().getWpRootGrpAttrValueId())) {
      return true;
    }
    return false;
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
   * Icdm-469 Selection listener implementation for selections on outlineFilter
   *
   * @param selection selection
   */
  public void a2lOutlineSelectionListener(final ISelection selection) {
    // Validate and get the selections
    if ((selection != null) && !selection.isEmpty() && (selection instanceof IStructuredSelection)) {
      final Object first = ((IStructuredSelection) selection).getFirstElement();
      // Check if selected node is a Function
      if (first instanceof Function) {
        setFlagValues(true, false, false, false, false, false, false, false);
        final Function func = (Function) first;
        this.setFilterText(func.getName());
        this.allNotAssigned = false;
      }
      else if (first instanceof A2LBaseComponents) {
        setFlagValues(false, false, true, false, false, false, false, false);
        final A2LBaseComponents baseComp = (A2LBaseComponents) first;
        setSelectedBC(baseComp);
        this.setFilterText(baseComp.getBcName());
        this.allNotAssigned = false;
      }
      // Icdm-586
      else if (first instanceof A2LParameter) {
        setFlagValues(false, false, false, false, false, false, true, false);
        final A2LParameter charateristics = (A2LParameter) first;
        this.setFilterText(charateristics.getName());
        this.allNotAssigned = false;
      }
      else if (first instanceof CompPackage) {
        setFlagValues(false, false, false, false, false, false, false, true);
        final CompPackage selectedCompPkg = (CompPackage) first;
        setSelectedCompPkg(selectedCompPkg);
        loadBCMap();
        this.setFilterText(selectedCompPkg.getName());
        this.allNotAssigned = false;
      }
      else if (first instanceof A2LBaseComponentFunctions) {
        setFlagValues(false, true, false, false, false, false, false, false);
        final A2LBaseComponentFunctions func = (A2LBaseComponentFunctions) first;
        this.setFilterText(func.getName());
        this.allNotAssigned = false;
      }
      else if (first instanceof A2lWorkPackageGroup) {
        setFlagValues(false, false, false, false, true, false, false, false);
        final A2lWorkPackageGroup wpPkgGrp = (A2lWorkPackageGroup) first;
        setSelectedWorkPkgGrp(wpPkgGrp);
        this.setFilterText(wpPkgGrp.getGroupName());
        this.allNotAssigned = false;
      }

      // ICDM-209 and ICDM-210
      else if (first instanceof A2lWpObj) {
        setFlagValues(false, false, false, true, false, false, false, false);
        final A2lWpObj wpPackage = (A2lWpObj) first;
        setSelectedWp(wpPackage);
        final StringBuilder txt = new StringBuilder(wpPackage.getWpGroupName() + ":" + wpPackage.getWpName());
        this.setFilterText(txt.toString());
        this.allNotAssigned = false;
      }

      else if (first instanceof A2LGroup) {
        setFlagValues(false, false, false, true, false, false, false, false);
        final A2LGroup a2lGrp = (A2LGroup) first;
        setSelectedA2lGroup(a2lGrp);
        this.setFilterText(a2lGrp.getGroupName());
        this.allNotAssigned = false;
      }
      else if ((first instanceof String) && (first.equals(ApicConstants.UNASSIGNED_PARAM))) {
        setFlagValues(false, false, false, false, false, false, true, false);
        this.allNotAssigned = true;
        this.setFilterText(ApicConstants.UNASSIGNED_PARAM);
      }
      else if (first instanceof String) {
        setFlagValues(false, false, false, false, false, true, false, false);
        setFunName((String) first);
        this.setFilterText((String) first);
        this.allNotAssigned = false;
      }
      // clear the filters for all Data Types
      else {
        setFlagValues(false, false, false, false, false, false, false, false);
        this.setFilterText("");
        this.allNotAssigned = false;
      }
    }
  }

  /**
   * icdm-272 Set al flag values
   */

  private void setFlagValues(final boolean func, final boolean fcBC, final boolean bcFlag, final boolean wPkg,
      final boolean wpGroup, final boolean grpFun, final boolean sfc, final boolean isCompPkg) {
    setFCFlag(func);
    setBCFCFlag(fcBC);
    setBCFlag(bcFlag);
    setWPFlag(wPkg);
    setWPGroupFlag(wpGroup);
    setGrpFunctionFlag(grpFun);
    setSpecialFunFalg(sfc);
    setCompPkg(isCompPkg);
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
    this.workPkgGroup = wpPkgGrp;

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
   * @param specialFun
   */
  public void setNotAssignedFC(final SortedSet<A2LParameter> specialFun) {
    this.notAssignedFun = specialFun;

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
    this.a2lBcMap = this.editorInput.getA2lFileInfoBO().getA2lBcMap();
    if (!this.compBcValuesMap.containsKey(getSelectedCompPkg().getId())) {
      Set<CompPkgBc> compBcValues = getCompPkgData().getBcSet();
      this.compBcValuesMap.put(getSelectedCompPkg().getId(), compBcValues);
    }
  }

  private CompPkgData getCompPkgData() {
    CompPkgData cmpPkgData = new CompPkgData();
    try {
      cmpPkgData = new CompPkgBcServiceClient().getCompBcFcByCompId(getSelectedCompPkg().getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return cmpPkgData;
  }
}
