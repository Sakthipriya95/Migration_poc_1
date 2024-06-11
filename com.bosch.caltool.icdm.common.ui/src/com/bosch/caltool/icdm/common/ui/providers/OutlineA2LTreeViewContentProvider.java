/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackageGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpObj;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author apj4cob
 */
public class OutlineA2LTreeViewContentProvider implements ITreeContentProvider {

  /**
   * APIC data contents
   */
  private final A2LFileInfoBO a2lFileInfoBO;
  /**
   * A2lWPInfoBo contents
   */
  private final A2LWPInfoBO a2lwpInfoBO;
  /**
  *
  */
  private final PidcA2LBO pidcA2LBO;
  /**
   *
   */
  private final boolean showGroupChar;
  /**
   * show rule set nodes
   */
  private final boolean showRuleSet;

  private Map<String, A2LParameter> a2lParamMap;

  private final CdrReportDataHandler cdrReportDataHandler;
  private Map<String, ?> paramMap;

  /**
   * OutlinePIDCTreeViewContentProvider - Constructor
   *
   * @param a2lwpInfoBO A2lWpInfo
   * @param showGroupChar boolean
   * @param showRuleSet boolean
   * @param pidcA2Lbo PidcA2LBO
   * @param cdrReportDataHandler cdrReportDataHandler
   */
  public OutlineA2LTreeViewContentProvider(final A2LWPInfoBO a2lwpInfoBO, final PidcA2LBO pidcA2Lbo,
      final boolean showGroupChar, final boolean showRuleSet, final CdrReportDataHandler cdrReportDataHandler) {
    // Get APIC data
    this.a2lwpInfoBO = a2lwpInfoBO;
    this.a2lFileInfoBO = a2lwpInfoBO.getA2lFileInfoBo();
    this.pidcA2LBO = pidcA2Lbo;
    this.showGroupChar = showGroupChar;
    this.showRuleSet = showRuleSet;
    this.cdrReportDataHandler = cdrReportDataHandler;
  }


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // NA

  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // NA
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang. Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {

    // String object is the inidication for displaying root node
    if ((inputElement instanceof String) && "ROOT".equals((inputElement).toString())) {
      return getRootElementChild();
    }

    return getTreeElements(inputElement);
  }

  /**
   * @return the child elements of the ROOT element
   */
  private Object[] getRootElementChild() {
    List<Object> rootChildren = new ArrayList<>();
    // ICDM-2272
    rootChildren.add(this.pidcA2LBO.getA2lFile());
    if (this.showRuleSet) {
      rootChildren.add(ApicConstants.RULE_SET);
    }
    return rootChildren.toArray();
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    // ICDM-2272
    if ((parentElement instanceof A2LFile) &&
        ((A2LFile) parentElement).getId().equals(this.pidcA2LBO.getA2lFileBO().getA2LFileID())) {
      List<Object> rootChildren = new ArrayList<>();
      rootChildren.add(ApicConstants.BC_CONST);
      rootChildren.add(ApicConstants.FC_CONST);
      rootChildren.add(ApicConstants.A2L_WORK_PKG);
      rootChildren.add(ApicConstants.WP_RESPONSIBILITY);
      if (CommonUtils.isNotEmpty(this.a2lFileInfoBO.getUnassignedParams())) {
        rootChildren.add(ApicConstants.UNASSIGNED_PARAM);
      }
      return rootChildren.toArray();
    }
    if ((parentElement instanceof String) && ((String) parentElement).equals(this.pidcA2LBO.getA2LFileName())) {
      // ICDM-1266
      List<Object> rootChildren = new ArrayList<>();
      rootChildren.add(ApicConstants.BC_CONST);
      rootChildren.add(ApicConstants.FC_CONST);
      rootChildren.add(ApicConstants.A2L_WORK_PKG);
      rootChildren.add(ApicConstants.WP_RESPONSIBILITY);
      if (!this.a2lFileInfoBO.getUnassignedParams().isEmpty()) {
        rootChildren.add(ApicConstants.UNASSIGNED_PARAM);
      }
      return rootChildren.toArray();
    }
    return getTreeElements(parentElement);

  }

  /**
   * @return Object[]
   */
  public Object[] getFCNode() {

    if (CommonUtils.isNullOrEmpty(this.paramMap)) {
      return this.a2lFileInfoBO.getFunctionsOfLabelType(LabelType.DEF_CHARACTERISTIC).toArray();
    }
    Set<Function> funSet = resolveFcsForRuleSet();
    return funSet.toArray();
  }

  /**
   * @return Object[]
   */
  public Object[] getBCNode() {
    if (CommonUtils.isNullOrEmpty(this.paramMap)) {
      return this.a2lFileInfoBO.getA2lBCInfo().toArray();
    }
    return resolveA2lBcForRuleSet();
  }

  /**
   * Method to get the collection of workpkg nodes
   *
   * @param selVarGrp as input
   * @return responsible nodes
   */
  public Object[] getWorkPkgNode(final A2lVariantGroup selVarGrp) {
    Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap =
        this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap();
    if (null == this.paramMap) {
      // To display Wp based on selected VG
      return getWorkPkgWithoutParam(selVarGrp, a2lWpRespNodeMergedMap);
    }

    SortedSet<String> filteredWpSet = new TreeSet<>(ApicUtil::compare);

    Map<Long, Map<Long, String>> paramIdWithWpAndRespMap =
        this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap();

    for (Entry<String, Set<A2lWpResponsibility>> wpEntry : a2lWpRespNodeMergedMap.entrySet()) {
      // iterate through the workpacakages map
      for (Object parameter : this.paramMap.values()) {
        // iterate through the parameter map
        Map<Long, String> wpRespMap = getWpRespMap(paramIdWithWpAndRespMap, parameter);

        // get the wp resp map for the parameter
        if (wpRespMap != null) {
          // iterate through the wp resp map
          // get the workpackage id
          A2lWpResponsibility a2lresponsibility = wpEntry.getValue().iterator().next();
          Long wpRespId = a2lresponsibility.getId();
          // compare workpackage id's
          if (CommonUtils.isEqual(wpRespId, wpRespMap.keySet().iterator().next())) {
            // if the wp id is present in the wp map of the parameter, add it to the return collection
            filteredWpSet.add(wpEntry.getKey());
            break;
          }
        }
      }
    }
    return filteredWpSet.toArray();
  }


  /**
   * @param selVarGrp
   * @param a2lWpRespNodeMergedMap
   * @return
   */
  private Object[] getWorkPkgWithoutParam(final A2lVariantGroup selVarGrp,
      final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap) {
    // When Variant group is selected from structure view, display WP corresponding to the variant group along with
    // default WPs
    if (selVarGrp != null) {
      Collection<String> filteredWpSet = new HashSet<>();
      for (Entry<String, Set<A2lWpResponsibility>> wpEntry : a2lWpRespNodeMergedMap.entrySet()) {
        wpEntry.getValue().forEach(a2lWpResp -> {
          if (selVarGrp.getId().equals(a2lWpResp.getVariantGrpId()) || (null == a2lWpResp.getVariantGrpId())) {
            filteredWpSet.add(wpEntry.getKey());
          }
        });
      }
      return filteredWpSet.stream().sorted().toArray();
    }

    SortedSet<String> a2lWpRespSet = fetchA2lWpRespForDefaultNode(a2lWpRespNodeMergedMap);
    return a2lWpRespSet.toArray();
  }


  /**
   * @param a2lWpRespNodeMergedMap
   * @return
   */
  private SortedSet<String> fetchA2lWpRespForDefaultNode(
      final Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap) {
    SortedSet<String> a2lWpRespSet = new TreeSet<>(ApicUtil::compare);
    // when DEFAULT is selected in the structure view, WPs at variant group level should not be displayed
    if (!this.a2lwpInfoBO.getA2lWpDefnModel().getA2lVariantGroupMap().isEmpty()) {
      for (Entry<String, Set<A2lWpResponsibility>> wpEntry : a2lWpRespNodeMergedMap.entrySet()) {
        wpEntry.getValue().forEach(a2lWpResp -> {
          if (null == (a2lWpResp.getVariantGrpId())) {
            a2lWpRespSet.add(wpEntry.getKey());
          }
        });
      }
    }
    else {
      a2lWpRespSet.addAll(a2lWpRespNodeMergedMap.keySet());
    }
    return a2lWpRespSet;
  }

  /**
   * @param selectedA2lVarGroup
   * @return
   */
  private Object[] getWPNodeForParamAssignmentPage(final A2lVariantGroup selVarGrp) {
    SortedSet<String> filteredWpSet = new TreeSet<>(ApicUtil::compare);
    Map<String, Set<A2lWpResponsibility>> a2lWpRespNodeMergedMap =
        this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWpRespNodeMergedMap();

    // When Variant Group is selected from structure view, Display WP corresponding to the selected Variant Group along
    // with default WPs
    if (selVarGrp != null) {
      for (Entry<String, Set<A2lWpResponsibility>> wpEntry : a2lWpRespNodeMergedMap.entrySet()) {
        wpEntry.getValue().forEach(a2lWpResp -> {
          if (this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamAndRespPalMap().containsKey(a2lWpResp.getId()) &&
              (selVarGrp.getId().equals(a2lWpResp.getVariantGrpId()) || (null == a2lWpResp.getVariantGrpId()))) {
            filteredWpSet.add(wpEntry.getKey());
          }
        });
      }
      return filteredWpSet.toArray();
    }

    // When Default node is selected from structure view, Do not dispaly WPs assigned to variant groups
    for (Entry<String, Set<A2lWpResponsibility>> wpEntry : a2lWpRespNodeMergedMap.entrySet()) {
      wpEntry.getValue().forEach(a2lWpResp -> {
        if (this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamAndRespPalMap().containsKey(a2lWpResp.getId()) &&
            (null == a2lWpResp.getVariantGrpId())) {
          filteredWpSet.add(wpEntry.getKey());
        }
      });
    }
    return filteredWpSet.toArray();
  }

  /**
   * @param selectedA2lVarGroup as input
   * @param a2lWPResponsibleMap based on workpkg or params page selection
   * @return responsible nodes
   */
  public Object[] getRespNodeBasedOnVariantGroup(final A2lVariantGroup selectedA2lVarGroup,
      final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResponsibleMap) {

    Map<String, SortedSet<A2lWpResponsibility>> a2lWPResMapBasedOnVarGroup = new HashMap<>();
    constructWpRespMapBasedOnVarGrp(selectedA2lVarGroup, a2lWPResMapBasedOnVarGroup, a2lWPResponsibleMap);
    return this.a2lwpInfoBO.getRespTypeAndRespMapWithVarGroups(a2lWPResMapBasedOnVarGroup).keySet().toArray();
  }


  /**
   * @param selectedA2lVarGroup as input
   * @param a2lWpRespPalList based on workpkg or params page
   * @return workpackage nodes
   */
  public Object[] getWorkPkgBasedOnRespAndVarGrp(final A2lVariantGroup selectedA2lVarGroup,
      final SortedSet<A2lWpResponsibility> a2lWpRespPalList) {
    SortedSet<A2lWpResponsibility> a2lWpRespPalMapBasedOnRespAndVarGrp =
        getA2lWpRespPalMapBasedOnRespAndVarGrp(selectedA2lVarGroup, a2lWpRespPalList);
    return a2lWpRespPalMapBasedOnRespAndVarGrp.stream().sorted(
        (final A2lWpResponsibility o1, final A2lWpResponsibility o2) -> ModelUtil.compare(o1.getName(), o2.getName()))
        .toArray();
  }


  /**
   * @param selectedA2lVarGroup
   * @param a2lWPResMapBasedOnVarGroup
   */
  private void constructWpRespMapBasedOnVarGrp(final A2lVariantGroup selectedA2lVarGroup,
      final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResMapBasedOnVarGroup,
      final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResponsibleMap) {
    a2lWPResponsibleMap.entrySet().forEach(keyValue -> keyValue.getValue().forEach(a2lWpResponsibility -> {

      if (((selectedA2lVarGroup == null) && (a2lWpResponsibility.getVariantGrpId() == null)) ||
          ((selectedA2lVarGroup != null) && ((a2lWpResponsibility.getVariantGrpId() == null) ||
              isSelectedVarGrpEqual(selectedA2lVarGroup, a2lWpResponsibility)))) {
        fillWpRespMap(a2lWPResMapBasedOnVarGroup, keyValue, a2lWpResponsibility);
      }
    }));
  }

  private boolean isSelectedVarGrpEqual(final A2lVariantGroup selectedA2lVarGroup,
      final A2lWpResponsibility a2lWpResponsibility) {
    return (selectedA2lVarGroup != null) && (a2lWpResponsibility.getVariantGrpId() != null) &&
        a2lWpResponsibility.getVariantGrpId().equals(selectedA2lVarGroup.getId());
  }


  /**
   * @param a2lWPResMapBasedOnVarGroup
   * @param keyValue
   * @param a2lWpResponsibility
   */
  private void fillWpRespMap(final Map<String, SortedSet<A2lWpResponsibility>> a2lWPResMapBasedOnVarGroup,
      final Entry<String, SortedSet<A2lWpResponsibility>> keyValue, final A2lWpResponsibility a2lWpResponsibility) {
    if (a2lWPResMapBasedOnVarGroup.containsKey(keyValue.getKey())) {
      a2lWPResMapBasedOnVarGroup.get(keyValue.getKey()).add(a2lWpResponsibility);
    }
    else {
      SortedSet<A2lWpResponsibility> a2lWpRespPals = new TreeSet<>();
      a2lWpRespPals.add(a2lWpResponsibility);
      a2lWPResMapBasedOnVarGroup.put(keyValue.getKey(), a2lWpRespPals);
    }
  }


  /**
   * @param selectedA2lVarGroup
   * @param a2lWPResMapBasedOnVarGroup
   */
  private SortedSet<A2lWpResponsibility> getA2lWpRespPalMapBasedOnRespAndVarGrp(
      final A2lVariantGroup selectedA2lVarGroup, final SortedSet<A2lWpResponsibility> a2lWpRespPalList) {
    SortedSet<A2lWpResponsibility> a2lWpRespPals = new TreeSet<>();
    a2lWpRespPalList.forEach(a2lWpResponsibility -> {
      if (((selectedA2lVarGroup != null) && ((a2lWpResponsibility.getVariantGrpId() == null) ||
          a2lWpResponsibility.getVariantGrpId().equals(selectedA2lVarGroup.getId()))) ||
          ((selectedA2lVarGroup == null) && (a2lWpResponsibility.getVariantGrpId() == null))) {
        a2lWpRespPals.add(a2lWpResponsibility);
      }
    });
    return a2lWpRespPals;
  }

  /**
   * @return
   */
  private Set<String> getRespNames() {
    if (this.paramMap == null) {
      return this.a2lwpInfoBO.getA2lWpParamMappingModel().getA2lWPResponsibleMap().keySet();
    }
    Set<String> respNameSet = new HashSet<>();
    Map<Long, Map<Long, String>> paramIdWithWpAndRespMap =
        this.a2lwpInfoBO.getA2lWpParamMappingModel().getParamIdWithWpAndRespMap();
    for (Entry<String, ?> parameter : this.paramMap.entrySet()) {
      Object paramVal = parameter.getValue();
      Map<Long, String> wpRespMap = getWpRespMap(paramIdWithWpAndRespMap, paramVal);
      if (null != wpRespMap) {
        respNameSet.addAll(wpRespMap.values());
      }
    }
    return respNameSet;
  }


  /**
   * @param paramIdWithWpAndRespMap
   * @param wpRespMap
   * @param paramVal
   * @return
   */
  private Map<Long, String> getWpRespMap(final Map<Long, Map<Long, String>> paramIdWithWpAndRespMap,
      final Object paramVal) {
    Map<Long, String> wpRespMap = null;
    if (paramVal instanceof RuleSetParameter) {
      RuleSetParameter funcParam = (RuleSetParameter) paramVal;
      wpRespMap = paramIdWithWpAndRespMap.get(funcParam.getParamId());
    }
    else if (paramVal instanceof Parameter) {
      Parameter param = (Parameter) paramVal;
      wpRespMap = paramIdWithWpAndRespMap.get(param.getId());
    }
    return wpRespMap;
  }

  /**
   * Method to get the WorkPkg based on selected Resp name in outline view Applicable only for cdr report data
   *
   * @param respName as input
   * @return list of a2lWpResponsibility
   */
  public Object[] getParamsWPBasedOnRespForCDRReports(final String respName) {
    SortedSet<A2lWpResponsibility> wpBasedOnRespName = new TreeSet<>(
        (final A2lWpResponsibility o1, final A2lWpResponsibility o2) -> ApicUtil.compare(o1.getName(), o2.getName()));
    wpBasedOnRespName.addAll(this.cdrReportDataHandler.getA2lWPResponsibleMap().get(respName));
    return wpBasedOnRespName.toArray();
  }

  /**
   * Method to get the Responsibility based on selected Resp Type in outline view
   *
   * @param respTypeName as input
   * @return list of A2lResponsibility
   */
  public Object[] getRespBasedOnRespTypeForCDRReports(final String respTypeName) {
    SortedSet<A2lResponsibility> respBasedOnRespType = new TreeSet<>(
        (final A2lResponsibility o1, final A2lResponsibility o2) -> ApicUtil.compare(o1.getName(), o2.getName()));
    respBasedOnRespType.addAll(this.cdrReportDataHandler.getRespTypeAndRespMap().get(respTypeName));
    return respBasedOnRespType.toArray();
  }

  /**
   * Method to get the WorkPkg based on selected Resp name in outline view
   *
   * @param respName as input
   * @return list of a2lWpResponsibility
   */
  public Object[] getWPBasedOnResp(final String respName) {

    SortedSet<A2lWpResponsibility> wpBasedOnRespName = new TreeSet<>(
        (final A2lWpResponsibility o1, final A2lWpResponsibility o2) -> ApicUtil.compare(o1.getName(), o2.getName()));
    wpBasedOnRespName.addAll(this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap().get(respName));
    return wpBasedOnRespName.toArray();
  }


  /**
   * Method to get the WorkPkg based on selected Resp name in outline view Applicable only for a2l params page
   *
   * @param respName as input
   * @return list of a2lWpResponsibility
   */
  public Object[] getParamsWPBasedOnResp(final String respName) {
    SortedSet<A2lWpResponsibility> wpBasedOnRespName = new TreeSet<>(
        (final A2lWpResponsibility o1, final A2lWpResponsibility o2) -> ApicUtil.compare(o1.getName(), o2.getName()));
    wpBasedOnRespName.addAll(this.a2lwpInfoBO.getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(respName));
    return wpBasedOnRespName.toArray();
  }

  /**
   * @return Object[]
   */
  private Object[] getWpRootGrpElements() {
    if (CommonUtils.isNullOrEmpty(this.paramMap)) {
      return this.a2lFileInfoBO.getA2LGroupList().toArray();
    }
    return resolveGrpForRuleSet();
  }


  /**
   * @param parentElement
   */
  private Object[] getTreeElements(final Object parentElement) {
    if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.FC_CONST)) {
      return getFCNode();
    }
    if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.BC_CONST)) {
      return getBCNode();
    }
    if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.A2L_WORK_PKG)) {
      return fetchWorkPackageNode();
    }
    // Display all available Responsibility Types
    if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.WP_RESPONSIBILITY)) {
      if (null != this.cdrReportDataHandler) {
        return this.cdrReportDataHandler.getRespTypeAndRespMap().keySet().toArray();
      }
      else if (this.a2lwpInfoBO.getCurrentPage() == 2) {
        return fetchRespTypeForWp();
      }
      else if ((this.a2lwpInfoBO.getCurrentPage() == 3) || (this.a2lwpInfoBO.getCurrentPage() == 5)) {
        return fetchRespTypeNodeForWpParam();
      }
    }
    // Responsibility Type Node - Fetches all the Resp for given Type for CDR Report
    if ((parentElement instanceof String) && (null != this.cdrReportDataHandler) &&
        this.cdrReportDataHandler.getRespTypeAndRespMap().containsKey(parentElement)) {
      return getRespBasedOnRespTypeForCDRReports((String) parentElement);
    }
    // Display all WP for given Responsible for CDR Report
    if (isCDRResponsibility(parentElement)) {
      return getParamsWPBasedOnRespForCDRReports(((A2lResponsibility) parentElement).getName());
    }
    // Responsibility Type Node - Fetches all the Resp for given Type
    if (isWpRespType(parentElement)) {
      return getResponsibilityForRespType(parentElement);
    }
    // Display all WP for given Responsible
    if (isWpResponsibility(parentElement)) {
      return getWorkPackages(parentElement);
    }
    // Responsibility Type Node - Fetches all the Resp for given Type for WP Param
    if (isWpParamRespType(parentElement)) {
      return getRespFromRespTypeForWpParam(parentElement);
    }
    // Display all WP for given Responsible for WP Param
    if (isWpParamResponsibility(parentElement)) {
      return getWorkPackageParams(parentElement);
    }
    if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.RULE_SET)) {
      return this.a2lFileInfoBO.getAllUndelRuleSets(false).toArray();
    }
    // Icdm-469
    if ((parentElement instanceof String) && ((String) parentElement).equals(ApicConstants.UNASSIGNED_PARAM)) {
      return this.a2lFileInfoBO.getUnassignedParams().toArray();
    }
    // ICDM-209 and ICDM-210
    if ((parentElement instanceof String) && ((String) parentElement).equals(this.a2lFileInfoBO.getWpRootGroupName())) {
      return getWpRootGrpElements();
    }
    else if (parentElement instanceof A2LGroup) {
      return a2lGrpElem(parentElement);
    }
    if (parentElement instanceof A2LBaseComponents) {
      return getA2LBaseCompElem(parentElement);
    }
    if (parentElement instanceof A2lWorkPackageGroup) {
      final A2lWorkPackageGroup workPkgGrp = (A2lWorkPackageGroup) parentElement;
      return resolveWpForRuleSet(workPkgGrp);
    }
    else if (parentElement instanceof A2lWpObj) {
      return getA2lWpElem(parentElement);
    }
    return new Object[0];
  }


  /**
   * @param parentElement
   * @return
   */
  private Object[] getRespFromRespTypeForWpParam(final Object parentElement) {
    Set<A2lResponsibility> respSet =
        this.a2lwpInfoBO.getRespTypeAndRespMapFromRespNameSet(getRespNames()).get(parentElement);
    return getSortedRespSet(respSet);
  }


  /**
   * @param respSet
   * @return
   */
  private Object[] getSortedRespSet(final Set<A2lResponsibility> respSet) {
    SortedSet<A2lResponsibility> respSortedSet = new TreeSet<>(
        (final A2lResponsibility o1, final A2lResponsibility o2) -> ApicUtil.compare(o1.getName(), o2.getName()));
    respSortedSet.addAll(respSet);
    return respSortedSet.toArray();
  }


  /**
   * @param parentElement
   * @return
   */
  private Object[] getResponsibilityForRespType(final Object parentElement) {
    Set<A2lResponsibility> respSet = this.a2lwpInfoBO.getRespTypeAndRespMap().get(parentElement);
    return getSortedRespSet(respSet);
  }


  /**
   * @param parentElement
   * @return
   */
  private Object[] getWorkPackageParams(final Object parentElement) {
    if (isBasedOnVariantGrp()) {
      return getWorkPkgBasedOnRespAndVarGrp(this.a2lwpInfoBO.getSelectedA2lVarGroup(), this.a2lwpInfoBO
          .getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(((A2lResponsibility) parentElement).getName()));
    }
    return getParamsWPBasedOnResp(((A2lResponsibility) parentElement).getName());
  }


  /**
   * @param parentElement
   * @return
   */
  private Object[] getWorkPackages(final Object parentElement) {
    if (isBasedOnVariantGrp()) {
      return getWorkPkgBasedOnRespAndVarGrp(this.a2lwpInfoBO.getSelectedA2lVarGroup(), this.a2lwpInfoBO
          .getA2lWpDefnModel().getA2lWPResponsibleMap().get(((A2lResponsibility) parentElement).getName()));
    }
    return getWPBasedOnResp(((A2lResponsibility) parentElement).getName());
  }


  /**
   * @return
   */
  private Object[] fetchWorkPackageNode() {
    if (null != this.cdrReportDataHandler) {
      return getWorkPkgNodeForCDRReports();
    }
    // in WP-Parameter Assignment page WPs present in WP page but not mapped to any parameters should not be displayed
    if ((null == this.paramMap) && (this.a2lwpInfoBO.getCurrentPage() == 3)) {
      return getWPNodeForParamAssignmentPage(this.a2lwpInfoBO.getSelectedA2lVarGroup());
    }
    return getWorkPkgNode(this.a2lwpInfoBO.getSelectedA2lVarGroup());
  }


  /**
   * @return
   */
  private Object[] fetchRespTypeNodeForWpParam() {
    if (isBasedOnVariantGrp()) {
      return getRespNodeBasedOnVariantGroup(this.a2lwpInfoBO.getSelectedA2lVarGroup(),
          this.a2lwpInfoBO.getA2lWpParamMappingModel().getA2lWPResponsibleMap());
    }
    return this.a2lwpInfoBO.getRespTypeAndRespMapFromRespNameSet(getRespNames()).keySet().toArray();
  }


  /**
   * @return
   */
  private Object[] fetchRespTypeForWp() {
    if (isBasedOnVariantGrp()) {
      return getRespNodeBasedOnVariantGroup(this.a2lwpInfoBO.getSelectedA2lVarGroup(),
          this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap());
    }
    return this.a2lwpInfoBO.getRespTypeAndRespMap().keySet().toArray();
  }


  /**
   * @param parentElement
   * @return
   */
  private boolean isCDRResponsibility(final Object parentElement) {
    return (parentElement instanceof A2lResponsibility) && (null != this.cdrReportDataHandler) &&
        CommonUtils.isNotEmpty(
            this.cdrReportDataHandler.getA2lWPResponsibleMap().get(((A2lResponsibility) parentElement).getName()));
  }


  /**
   * @param parentElement
   * @return
   */
  private boolean isWpRespType(final Object parentElement) {
    return (parentElement instanceof String) && (this.a2lwpInfoBO.getCurrentPage() == 2) &&
        CommonUtils.isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMap().get(parentElement));
  }


  /**
   * @param parentElement
   * @return
   */
  private boolean isWpResponsibility(final Object parentElement) {
    return (parentElement instanceof A2lResponsibility) && (this.a2lwpInfoBO.getCurrentPage() == 2) &&
        CommonUtils.isNotEmpty(this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap()
            .get(((A2lResponsibility) parentElement).getName()));
  }


  /**
   * @param parentElement
   * @return
   */
  private boolean isWpParamRespType(final Object parentElement) {
    return (parentElement instanceof String) &&
        ((this.a2lwpInfoBO.getCurrentPage() == 3) || (this.a2lwpInfoBO.getCurrentPage() == 5)) &&
        (this.a2lwpInfoBO.getA2lWpParamMappingModel() != null) && CommonUtils
            .isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMapFromRespNameSet(getRespNames()).get(parentElement));
  }


  /**
   * @param parentElement
   * @return
   */
  private boolean isWpParamResponsibility(final Object parentElement) {
    return (parentElement instanceof A2lResponsibility) &&
        ((this.a2lwpInfoBO.getCurrentPage() == 3) || (this.a2lwpInfoBO.getCurrentPage() == 5)) &&
        (this.a2lwpInfoBO.getA2lWpParamMappingModel() != null) && CommonUtils.isNotEmpty(this.a2lwpInfoBO
            .getA2lWpParamMappingModel().getA2lWPResponsibleMap().get(((A2lResponsibility) parentElement).getName()));
  }


  /**
   * @return
   */
  private boolean isBasedOnVariantGrp() {
    return ((this.a2lwpInfoBO.getSelectedA2lVarGroup() != null) && !this.a2lwpInfoBO.isNotAssignedVarGrp()) ||
        ((this.a2lwpInfoBO.getSelectedA2lVarGroup() == null) && this.a2lwpInfoBO.isNotAssignedVarGrp());
  }


  /**
   * @return
   */
  private Object[] getWorkPkgNodeForCDRReports() {
    SortedSet<String> wpNameSet = new TreeSet<>(ApicUtil::compare);
    this.cdrReportDataHandler.getParamAndRespPalMap().keySet().stream()
        .forEach(wpRespId -> wpNameSet.add(this.cdrReportDataHandler.getA2lEditorDataProvider().getA2lWpInfoBO()
            .getA2lWpDefnModel().getWpRespMap().get(wpRespId).getName()));
    return wpNameSet.toArray();
  }


  /**
   * @return
   */
  private Object[] getA2lWpElem(final Object parentElement) {
    final A2lWpObj workPkg = (A2lWpObj) parentElement;
    final SortedSet<String> values = new TreeSet<>();

    if (CommonUtils.isNullOrEmpty(this.paramMap)) {
      values.addAll(workPkg.getFunctionMap().values());
    }
    else {
      getWPForEmptyParamSet(workPkg, values);
    }

    return values.toArray();
  }


  /**
   * @return Object[]
   */
  private Object[] a2lGrpElem(final Object parentElement) {
    Map<String, A2LGroup> groupMap = this.a2lFileInfoBO.getA2lWpMapping().getA2lGrpMap();
    List<A2LGroup> list = new ArrayList<>();
    final A2LGroup grp = (A2LGroup) parentElement;
    if (grp.getSubGrpMap().size() > 0) {
      if (CommonUtils.isNullOrEmpty(this.paramMap.keySet())) {
        return getA2lSubGrp(list, grp);
      }

      SortedSet<A2LGroup> validSubGrp = new TreeSet<>();

      return getValidSubGrp(groupMap, grp, validSubGrp);

    }
    if (this.showGroupChar) {
      if (CommonUtils.isNullOrEmpty(this.paramMap.keySet())) {
        return grp.getLabelMap().values().toArray();
      }

      SortedSet<String> validGrp = new TreeSet<>();
      for (String paramName : grp.getLabelMap().values()) {
        if (this.paramMap.keySet().contains(paramName)) {
          validGrp.add(paramName);
        }

      }
      return validGrp.toArray();
    }
    return Collections.emptyList().toArray();
  }


  /**
   * @param groupMap Map<String, A2LGroup>
   * @param grp A2LGroup
   * @param validSubGrp SortedSet<A2LGroup>
   * @return Object[]
   */
  public Object[] getValidSubGrp(final Map<String, A2LGroup> groupMap, final A2LGroup grp,
      final SortedSet<A2LGroup> validSubGrp) {
    for (String subGpName : grp.getSubGrpMap().get(grp.getGroupName())) {
      A2LGroup group = groupMap.get(subGpName);
      if (this.paramMap.keySet().contains(group.getGroupName())) {
        validSubGrp.add(group);
      }
    }
    return validSubGrp.toArray();
  }


  /**
   * @param list List<A2LGroup>
   * @param grp A2LGroup
   * @return Object[]
   */
  public Object[] getA2lSubGrp(final List<A2LGroup> list, final A2LGroup grp) {
    List<String> subGrpName = grp.getSubGrpMap().get(grp.getGroupName());
    for (String name : subGrpName) {
      list.add(this.a2lFileInfoBO.getA2lWpMapping().getA2lGrpMap().get(name));
    }
    return list.toArray();
  }


  /**
   *
   */
  private void getWPForEmptyParamSet(final A2lWpObj workPkg, final SortedSet<String> values) {
    for (String fcName : workPkg.getFunctionMap().values()) {
      Set<String> paramSet = this.paramMap.keySet();
      for (String param : paramSet) {
        A2LParameter a2lParameter = this.a2lParamMap.get(param);
        if (a2lParameter != null) {
          Function defFunction = a2lParameter.getDefFunction();
          if ((defFunction != null) && defFunction.getName().equals(fcName)) {
            values.add(fcName);
          }

        }

      }
    }
  }


  /**
   * @return
   */
  private Object[] getA2LBaseCompElem(final Object parentElement) {
    final A2LBaseComponents bcObj = (A2LBaseComponents) parentElement;
    if (CommonUtils.isNullOrEmpty(this.paramMap)) {
      return bcObj.getFunctionsList().toArray();
    }

    return resolveBcFcForRuleSet(bcObj);
  }


  /**
   * @param workPkgGrp
   * @return
   */
  private Object[] resolveWpForRuleSet(final A2lWorkPackageGroup workPkgGrp) {
    final SortedSet<A2lWpObj> values = new TreeSet<>();
    final Map<String, Long> workPackage = workPkgGrp.getWorkPackageMap();
    if (CommonUtils.isNullOrEmpty(this.paramMap)) {
      for (Entry<String, Long> pack : workPackage.entrySet()) {
        A2lWpObj a2lWorkPackage = this.a2lFileInfoBO.getA2lWpMapping().getWpMap().get(pack.getValue());
        values.add(a2lWorkPackage);
      }
    }
    else {
      for (Entry<String, Long> pack : workPackage.entrySet()) {
        A2lWpObj a2lWorkPackage = this.a2lFileInfoBO.getA2lWpMapping().getWpMap().get(pack.getValue());
        Set<String> paramSet = this.paramMap.keySet();
        for (String param : paramSet) {
          A2LParameter a2lParameter = this.a2lParamMap.get(param);
          if ((a2lParameter != null) && (a2lParameter.getDefFunction() != null) &&
              a2lWorkPackage.getFunctionMap().containsKey(a2lParameter.getDefFunction().getName())) {
            values.add(a2lWorkPackage);
          }
        }
      }

    }

    return values.toArray();
  }


  /**
   * @return
   */
  private Object[] resolveGrpForRuleSet() {
    SortedSet<A2LGroup> validGrp = new TreeSet<>();
    for (A2LGroup a2lGrp : this.a2lFileInfoBO.getA2LGroupList()) {
      while (a2lGrp.getSubGrpMap().size() > 0) {
        List<String> subGrpName = a2lGrp.getSubGrpMap().get(a2lGrp.getGroupName());
        for (String grp : subGrpName) {
          A2LGroup subGrp = this.a2lFileInfoBO.getA2lWpMapping().getA2lGrpMap().get(grp);
          if (a2lGrp.getSubGrpMap().size() > 0) {
            a2lGrp = subGrp;
          }
          else {
            addItemsToValidGp(validGrp, a2lGrp);
          }
        }
      }
      addItemsToValidGp(validGrp, a2lGrp);
    }
    return validGrp.toArray();
  }


  /**
   * @param validGrp SortedSet<A2LGroup>
   * @param a2lGrp A2LGroup
   */
  public void addItemsToValidGp(final SortedSet<A2LGroup> validGrp, final A2LGroup a2lGrp) {
    for (String labelName : a2lGrp.getLabelMap().values()) {
      if (this.paramMap.keySet().contains(labelName)) {
        validGrp.add(a2lGrp);
      }

    }
  }


  /**
   * @param bcObj
   * @return
   */
  private Object[] resolveBcFcForRuleSet(final A2LBaseComponents bcObj) {
    SortedSet<A2LBaseComponentFunctions> bcfcList = bcObj.getFunctionsList();
    Set<A2LBaseComponentFunctions> a2lBCInfo = new HashSet<>();
    for (A2LBaseComponentFunctions a2lBcFun : bcfcList) {
      Set<String> paramSet = this.paramMap.keySet();
      for (String paramName : paramSet) {
        A2LParameter a2lParameter = this.a2lParamMap.get(paramName);
        if (a2lParameter != null) {
          Function defFunction = a2lParameter.getDefFunction();
          if (a2lBcFun.getName().equals(defFunction.getName())) {
            a2lBCInfo.add(a2lBcFun);
          }
        }
      }
    }

    return a2lBCInfo.toArray();
  }


  /**
   * @return array of objects having params
   */
  private Object[] resolveA2lBcForRuleSet() {
    SortedSet<A2LBaseComponents> a2lBCInfo = new TreeSet<>();
    for (A2LBaseComponents a2lBc : this.a2lFileInfoBO.getA2lBCInfo()) {
      Collection<A2LBaseComponentFunctions> functionsList = a2lBc.getFunctionMap().values();
      for (A2LBaseComponentFunctions a2lBcFun : functionsList) {
        Set<String> paramSet = this.paramMap.keySet();
        for (String paramName : paramSet) {
          A2LParameter a2lParameter = this.a2lParamMap.get(paramName);
          if (a2lParameter != null) {
            Function defFunction = a2lParameter.getDefFunction();
            if (a2lBcFun.getName().equals(defFunction.getName())) {
              a2lBCInfo.add(a2lBc);

            }
          }
        }

      }
    }
    return a2lBCInfo.toArray();
  }


  /**
   * @return
   */
  private Set<Function> resolveFcsForRuleSet() {
    SortedSet<Function> funSet = new TreeSet<>();
    for (Function function : this.a2lFileInfoBO.getFunctionsOfLabelType(LabelType.DEF_CHARACTERISTIC)) {
      List<DefCharacteristic> defCharRefList = function.getDefCharRefList();
      for (DefCharacteristic defCharacteristic : defCharRefList) {
        if (this.paramMap.keySet().contains(defCharacteristic.getName())) {
          funSet.add(function);
        }
      }
    }
    return funSet;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    // Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    // ICDM-2272
    if ((element instanceof A2LFile) &&
        ((A2LFile) element).getId().equals(this.pidcA2LBO.getA2lFileBO().getA2LFileID())) {
      return true;
    }
    // Check if node has child nodes
    if ((element instanceof String) && ((String) element).equals(this.pidcA2LBO.getA2LFileName())) {
      return true;
    }
    if (isFcNode(element)) {
      return true;
    }
    if (isBcNode(element)) {
      return true;
    }
    if (isWorkPackageNode(element)) {
      return true;
    }
    A2lWpParamMappingModel a2lWpParamMappingModel = this.a2lwpInfoBO.getA2lWpParamMappingModel();
    if (isRespNode(element, a2lWpParamMappingModel)) {
      return true;
    }
    if (isCDRRespType(element)) {
      return true;
    }
    if (isA2lResponsibility(element, a2lWpParamMappingModel)) {
      return true;
    }
    if (isResponsibilityType(element)) {
      return true;
    }
    // ICDM-866 : Rule Set and CompPkg
    if (isRuleSetNode(element)) {
      return true;
    }

    // Icdm-469
    if (isUnAssignedParam(element)) {
      return true;

    }

    // ICDM-209 and ICDM-210
    if ((element instanceof String) && ((String) element).equals(this.a2lFileInfoBO.getWpRootGroupName()) &&
        CommonUtils.isNotEmpty(this.a2lFileInfoBO.getA2LGroupList())) {
      return true;
    }

    return isA2LGrpHasChildren(element);

  }


  /**
   * @param element
   * @return
   */
  private boolean isUnAssignedParam(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.UNASSIGNED_PARAM) &&
        CommonUtils.isNotEmpty(this.a2lFileInfoBO.getUnassignedParams());
  }


  /**
   * @param element
   * @return
   */
  private boolean isRuleSetNode(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.RULE_SET) &&
        (!this.a2lFileInfoBO.getAllUndelRuleSets(false).isEmpty());
  }


  /**
   * @param element
   * @return
   */
  private boolean isCDRRespType(final Object element) {
    return (element instanceof String) && (this.cdrReportDataHandler != null) &&
        (CommonUtils.isNotEmpty(this.cdrReportDataHandler.getRespTypeAndRespMap().get(element)));
  }


  /**
   * @param element
   * @return
   */
  private boolean isWorkPackageNode(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.A2L_WORK_PKG) &&
        CommonUtils.isNotEmpty(this.a2lwpInfoBO.getA2lWpDefnModel().getWpRespMap().values());
  }


  /**
   * @param element
   * @return
   */
  private boolean isBcNode(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.BC_CONST) &&
        CommonUtils.isNotEmpty(this.a2lFileInfoBO.getA2lBCInfo());
  }


  /**
   * @param element
   * @return
   */
  private boolean isFcNode(final Object element) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.FC_CONST) &&
        CommonUtils.isNotEmpty(this.a2lFileInfoBO.getFunctionsOfLabelType(LabelType.DEF_CHARACTERISTIC));
  }


  /**
   * @param element
   * @param a2lWpParamMappingModel
   * @return
   */
  private boolean isRespNode(final Object element, final A2lWpParamMappingModel a2lWpParamMappingModel) {
    return (element instanceof String) && ((String) element).equals(ApicConstants.WP_RESPONSIBILITY) &&
        ((this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap() != null) ||
            ((a2lWpParamMappingModel != null) && (a2lWpParamMappingModel.getA2lWPResponsibleMap() != null))) &&
        checkIsNotEmpty(a2lWpParamMappingModel);
  }


  /**
   * @param element
   * @return
   */
  private boolean isResponsibilityType(final Object element) {
    return (element instanceof String) &&
        (CommonUtils.isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMap().get(element)) ||
            CommonUtils.isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMapForWpParam().get(element)) ||
            CommonUtils.isNotEmpty(this.a2lwpInfoBO.getRespTypeAndRespMapFromRespNameSet(getRespNames()).get(element)));
  }


  /**
   * @param element
   * @param a2lWpParamMappingModel
   * @return
   */
  private boolean isA2lResponsibility(final Object element, final A2lWpParamMappingModel a2lWpParamMappingModel) {
    return (element instanceof A2lResponsibility) && (CommonUtils.isNotEmpty(
        this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap().get(((A2lResponsibility) element).getName())) ||
        CommonUtils.isNotEmpty(a2lWpParamMappingModel != null
            ? a2lWpParamMappingModel.getA2lWPResponsibleMap().get(((A2lResponsibility) element).getName()) : null));
  }


  /**
   * @param a2lWpParamMappingModel
   * @return
   */
  private boolean checkIsNotEmpty(final A2lWpParamMappingModel a2lWpParamMappingModel) {
    return CommonUtils.isNotEmpty(this.a2lwpInfoBO.getA2lWpDefnModel().getA2lWPResponsibleMap().keySet()) || CommonUtils
        .isNotEmpty(a2lWpParamMappingModel != null ? a2lWpParamMappingModel.getA2lWPResponsibleMap().keySet() : null);
  }

  /**
   * @param element Oject
   * @return boolean
   */
  public boolean isA2LBaseCompHasChildren(final Object element) {
    if ((element instanceof A2LBaseComponents) &&
        CommonUtils.isNotEmpty(((A2LBaseComponents) element).getFunctionsList())) {
      return true;
    }
    return isWPGrpHasChildren(element);
  }

  /**
   * @param element Object
   * @return boolean
   */
  public boolean isWPGrpHasChildren(final Object element) {
    if ((element instanceof A2lWorkPackageGroup) &&
        CommonUtils.isNotEmpty(((A2lWorkPackageGroup) element).getWorkPackage())) {
      return true;
    }
    return isWPHasChildren(element);

  }

  /**
   * @param element Object
   * @return boolean
   */
  public boolean isWPHasChildren(final Object element) {
    return (element instanceof A2lWpObj) && (((A2lWpObj) element).getFunctionMap().size() > 0);

  }

  /**
   * @param element Object
   * @return boolean
   */
  public boolean isA2LGrpHasChildren(final Object element) {
    if (element instanceof A2LGroup) {
      final A2LGroup grp = (A2LGroup) element;
      if (grp.getSubGrpMap().size() > 0) {
        return true;
      }
      return this.showGroupChar;
    }
    return isA2LBaseCompHasChildren(element);
  }


  /**
   * @return boolean
   */
  public boolean isNotNullAndEqual() {
    return (this.a2lFileInfoBO.getMappingSourceID() != null) &&
        CommonUtils.isEqual(this.a2lFileInfoBO.getMappingSourceID(),
            this.a2lFileInfoBO.getA2lWpMapping().getGroupMappingId()) &&
        (this.a2lFileInfoBO.getWpRootGroupName() != null);
  }


  /**
   * @param a2lParamMap from the editor
   */
  public void setA2lParamMap(final Map<String, A2LParameter> a2lParamMap) {
    this.a2lParamMap = a2lParamMap;
  }


  /**
   * @param paramMap parameter Map
   */
  public void setParamMap(final Map paramMap) {
    this.paramMap = paramMap;
  }

}
