/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewFilesSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.RuleSetSltnPage;
import com.bosch.caltool.cdr.ui.wizards.pages.WorkpackageSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LGroup;
import com.bosch.caltool.icdm.model.a2l.CompliParamOutput;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class WorkpackageSelectionPageResolver implements IReviewUIDataResolver {


  private CalDataReviewWizard calDataReviewWizard;

  private ReviewFilesSelectionWizardPage reviewFileSelWizPage;


  private Map<String, Characteristic> characteristicsMap = new HashMap<>();

  /**
   * @param calDataReviewWizard wizard
   */
  public WorkpackageSelectionPageResolver(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
    this.reviewFileSelWizPage = this.calDataReviewWizard.getWpSelWizPage().getReviewFileSelWizPage();

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void processNextPressed() {

    if ((this.calDataReviewWizard.getWpSelWizPage().getRuleSetRadio().getSelection() ||
        this.calDataReviewWizard.getWpSelWizPage().getSecondaryRulesButton().getSelection()) &&
        (this.calDataReviewWizard.getAllRuleSet().isEmpty())) {
      try {
        this.calDataReviewWizard.setAllRuleSet(new CDRHandler().getAllRuleSet());
        this.calDataReviewWizard.getRuleSetSelPage().getPrimaryRuleSetTabViewer()
            .setInput(this.calDataReviewWizard.getAllRuleSet());
        this.calDataReviewWizard.getRuleSetSelPage().getSecRuleSetTabViewer()
            .setInput(this.calDataReviewWizard.getAllRuleSet());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }

    IWizardPage nextPage = this.calDataReviewWizard.getWpSelWizPage().getNextPage();
    if (!this.calDataReviewWizard.isDeltaReview() && (nextPage instanceof ReviewFilesSelectionWizardPage)) {
      this.reviewFileSelWizPage = (ReviewFilesSelectionWizardPage) nextPage;
      this.reviewFileSelWizPage.getAddFileButton().setEnabled(true);
      this.reviewFileSelWizPage.getSelectedFilesPath().clear();
      this.reviewFileSelWizPage.getFilesList().removeAll();
    }

    // For Loading Ruleset selection Page
    if (this.calDataReviewWizard.isDeltaReview() &&
        (this.calDataReviewWizard.getWpSelWizPage().getRuleSetRadio().getSelection() ||
            this.calDataReviewWizard.getWpSelWizPage().getSecondaryRulesButton().getSelection()) &&
        !this.calDataReviewWizard.isProjectDataDeltaReview()) {
      this.calDataReviewWizard.getRuleSetSelPage().getRuleSetSltnPageResolver().fillUIData(this.calDataReviewWizard);
    }
    // For Loading SSD Rule Set selection Page
    if (this.calDataReviewWizard.getWpSelWizPage().getSsdRuleButton().getSelection()) {
      List<SSDReleaseIcdmModel> mappedSSDReleases = null;
      if (this.calDataReviewWizard.getCdrWizardUIModel().getMappedSSDReleases() == null) {
        try {
          if ((this.calDataReviewWizard.getCdrWizardUIModel().getSsdSWVersionId() == null) &&
              (this.calDataReviewWizard.getCdrWizardUIModel().getPidcA2lId() != null)) {
            Long ssdSoftwareVersionId = new PidcA2lServiceClient()
                .getById(this.calDataReviewWizard.getCdrWizardUIModel().getPidcA2lId()).getSsdSoftwareVersionId();
            this.calDataReviewWizard.getCdrWizardUIModel().setSsdSWVersionId(ssdSoftwareVersionId);
          }
          if (this.calDataReviewWizard.getCdrWizardUIModel().getSsdSWVersionId() != null) {
            mappedSSDReleases =
                new CDRHandler().getSSDReleases(this.calDataReviewWizard.getCdrWizardUIModel().getSsdSWVersionId());
            this.calDataReviewWizard.getCdrWizardUIModel().setMappedSSDReleases(mappedSSDReleases);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else {
        mappedSSDReleases = this.calDataReviewWizard.getCdrWizardUIModel().getMappedSSDReleases();
      }

      this.calDataReviewWizard.getSsdRuleSelPage().getSsdTabViewer().setInput(mappedSSDReleases);
      if ((this.calDataReviewWizard.getCdrWizardUIModel().getSsdSWVersionId() != null) &&
          ((mappedSSDReleases == null) || mappedSSDReleases.isEmpty())) {
        String message = "No matching SSD releases.Please select SSD file to perform the review";
        this.calDataReviewWizard.getSsdRuleSelPage().setWarnMessage(message);
      }
    }

    if (this.calDataReviewWizard.isDeltaReview() &&
        this.calDataReviewWizard.getWpSelWizPage().getCommonRuleRadio().getSelection() &&
        !this.calDataReviewWizard.isProjectDataDeltaReview()) {
      if (nextPage instanceof ReviewFilesSelectionWizardPage) {
        this.reviewFileSelWizPage = (ReviewFilesSelectionWizardPage) nextPage;
        this.reviewFileSelWizPage.getReviewFilesSelectionPageResolver().fillUIData(this.calDataReviewWizard);
        this.reviewFileSelWizPage.canFlipToNextPage();
      }

    }
    else {
      this.calDataReviewWizard.getWpSelWizPage().setReviewFunctions();
    }
    if (!this.calDataReviewWizard.getWpSelWizPage().getReviewFuncsSet().isEmpty()) {
      this.calDataReviewWizard.getWpSelWizPage().setReviewFunctions();
    }
    // Icdm-739 and Icdm-800 Handling invalid params and functions for Work package/Group function selection also
    if (this.calDataReviewWizard.getWpSelWizPage().getLabFunRadio().getSelection() ||
        this.calDataReviewWizard.getWpSelWizPage().getWorkPackageRadio().getSelection() ||
        this.calDataReviewWizard.getWpSelWizPage().getA2lRadio().getSelection()) {
      this.calDataReviewWizard.getWpSelWizPage().setInvalidFunParam();
    }

    this.calDataReviewWizard.getCdrWizardUIModel()
        .setUnassignedParamsInReview(this.calDataReviewWizard.getWpSelWizPage().getReviewFuncsSet());
    this.calDataReviewWizard.getCdrWizardUIModel().setSecondaryRuleSetIds(new HashSet<>());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void processBackPressed() {
    // Not applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
    WorkpackageSelectionWizardPage wpSelWizPage = this.calDataReviewWizard.getWpSelWizPage();
    this.reviewFileSelWizPage = wpSelWizPage.getReviewFileSelWizPage();

    // Next Pressed validation
    if (wpSelWizPage.getCommonRuleRadio().getSelection()) {
      calDataReviewWizard.getCdrWizardUIModel().setCommonRulesPrimary(true);
    }
    if (wpSelWizPage.getLabFunRadio().getSelection() && (wpSelWizPage.getFileName() != null) &&
        wpSelWizPage.getFileName().contains(".")) {
      this.calDataReviewWizard.getFunLabFromParser(wpSelWizPage.getFileName());
    }

    wpSelWizPage.enableReviewFunctionLists();


    if (wpSelWizPage.getCompliRadio().getSelection()) {

      CompliParamOutput compliParamValues;
      try {
        compliParamValues = wpSelWizPage.getCdrHandler().getCompliParamValues();
        List<String> compliLabelList = new ArrayList<>();
        for (String compliLabel : compliParamValues.getCompliParamMap().keySet()) {
          compliLabelList.add(compliLabel);
        }
        this.calDataReviewWizard.getCdrWizardUIModel().setLabelList(compliLabelList);
        this.calDataReviewWizard.getCdrWizardUIModel().setCompliLabelList(compliLabelList);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }

      if (calDataReviewWizard.getCdrWizardUIModel().getSelReviewFuncs() != null) {
        this.calDataReviewWizard.getCdrWizardUIModel().getSelReviewFuncs().clear();
      }
    }

    RuleSetSltnPage ruleSetSelPage = (RuleSetSltnPage) this.calDataReviewWizard.getPage("Select the Rule Set");

    if (wpSelWizPage.getCommonRuleRadio().getSelection()) {
      ruleSetSelPage.getPrimaryRuleSetTabViewer().getGrid().setEnabled(false);
      this.calDataReviewWizard.getCdrWizardUIModel().setCommonRulesPrimary(true);
    }
    else {
      ruleSetSelPage.getPrimaryRuleSetTabViewer().getGrid().setEnabled(true);
      this.calDataReviewWizard.getCdrWizardUIModel().setCommonRulesPrimary(false);
    }

    if (CommonUtils.isNull(calDataReviewWizard.getCdrWizardUIModel().getObdFlag()) &&
        calDataReviewWizard.getCdrWizardUIModel().isPidcDivAppForOBDOpt() && CommonUtils.isNotEqual(
            calDataReviewWizard.getCdrWizardUIModel().getReviewType(), CDRConstants.REVIEW_TYPE.TEST.getDbType())) {

      calDataReviewWizard.getCdrWizardUIModel().setObdFlag(getOBDFlag(wpSelWizPage));
    }

  }

  private String getOBDFlag(final WorkpackageSelectionWizardPage wpSelWizPage) {

    if (wpSelWizPage.getNoOBDRadio().getSelection()) {
      return CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType();
    }

    return wpSelWizPage.getOnlyOBDRadio().getSelection() ? CDRConstants.OBD_OPTION.ONLY_OBD_LABELS.getDbType()
        : CDRConstants.OBD_OPTION.BOTH_OBD_AND_NON_OBD_LABELS.getDbType();
  }

  /**
   * Added to improve performance (To avoid repetative calling of characteristicsMap)
   */
  public void setCharacteristicMap() {
    if (this.characteristicsMap.isEmpty()) {
      this.characteristicsMap = this.calDataReviewWizard.getA2lEditiorDataHandler().getCharacteristicsMap();
    }
  }

  /**
   * @param selectedElement list of a2l Groups
   * @return the function list
   */
  public SortedSet<Function> resolveFunsForA2lGrp(final List selectedElement) {

    A2LGroup a2lGroup = (A2LGroup) selectedElement.get(0);
    return getFuncsForGrp(a2lGroup);
  }


  /**
   * @param a2lGroup a2lGroup object
   * @return the functions list for a group
   */
  public SortedSet<Function> getFuncsForGrp(final A2LGroup a2lGroup) {
    Set<String> funcNameStr = new HashSet<>();
    SortedSet<Function> funList = new TreeSet<>();

    for (String label : a2lGroup.getLabelMap().values()) {
      Characteristic charObj = this.characteristicsMap.get(label);
      Function func = charObj.getDefFunction();

      if (func == null) {
        // In general, this would not happen
        continue;
      }
      if (!funcNameStr.contains(func.getName())) {
        funcNameStr.add(func.getName());
        funList.add(func);
      }

    }
    return funList;
  }

  /**
   * Get the un-assigned param map
   *
   * @return un assigned params. Key - A2L group name, value - set of params
   */
  public Map<String, Set<String>> getGroupUnassignedMap() {

    Map<String, Set<String>> grpParamMap = new ConcurrentHashMap<>();

    SortedSet<A2LParameter> unassignedParams = getUnassignedParams();

    // get all a2l groups
    for (A2LGroup a2lGroup : this.calDataReviewWizard.getA2lEditiorDataHandler().getA2lWpMapping().getA2lGroupList()) {
      // get unassigned params
      for (A2LParameter a2lParam : unassignedParams) {
        addToGroupParamMap(grpParamMap, a2lGroup, a2lParam);
      }
    }
    return grpParamMap;
  }


  /**
   * @param grpParamMap
   * @param a2lGroup
   * @param a2lParam
   */
  private void addToGroupParamMap(final Map<String, Set<String>> grpParamMap, final A2LGroup a2lGroup,
      final A2LParameter a2lParam) {
    if (a2lGroup.getLabelMap().get(a2lParam.getName()) != null) {
      Set<String> paramSet = grpParamMap.get(a2lGroup.getGroupName());
      if (paramSet == null) {
        paramSet = new HashSet<>();
      }
      paramSet.add(a2lParam.getName());
      grpParamMap.put(a2lGroup.getGroupName(), paramSet);
    }
  }


  private SortedSet<A2LParameter> getUnassignedParams() {
    SortedSet<A2LParameter> unAssignedParams = new TreeSet<>();
    // iterate and fill the maps
    for (Characteristic characteristic : this.characteristicsMap.values()) {
      Function defFunction = characteristic.getDefFunction();
      if (defFunction == null) {
        // Icdm-586
        A2LParameter param = new A2LParameter(null, characteristic, null, false, null, false, false);
        unAssignedParams.add(param);
      }
    }
    return unAssignedParams;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CalDataReviewWizard getInput() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void fillUIData(final CalDataReviewWizard rvwWizard) {
    WorkpackageSelectionWizardPage wpSelWizPage = rvwWizard.getWpSelWizPage();
    CDRWizardUIModel cdrWizardUIModel = rvwWizard.getCdrWizardUIModel();

    // UI fill for fetching the rules
    if (cdrWizardUIModel.isCommonRulesPrimary()) {
      wpSelWizPage.getCommonRuleRadio().setSelection(true);
      wpSelWizPage.getRuleSetRadio().setSelection(false);

      wpSelWizPage.getSecondaryRulesButton().setText(" include RuleSet Rules");
      wpSelWizPage.getSecondaryRulesButton().setEnabled(true);
      if ((null == cdrWizardUIModel.getPrimaryRuleSetId()) && ((null != cdrWizardUIModel.getSecondaryRuleSetIds()) &&
          !cdrWizardUIModel.getSecondaryRuleSetIds().isEmpty())) {
        wpSelWizPage.getSecondaryRulesButton().setSelection(true);
      }
    }
    else {
      wpSelWizPage.getCommonRuleRadio().setSelection(false);
      wpSelWizPage.getRuleSetRadio().setSelection(true);

      wpSelWizPage.getSecondaryRulesButton().setText(" include Common Rules");
      wpSelWizPage.getSecondaryRulesButton().setEnabled(true);
      Map<Long, String> secRuleSetMap = cdrWizardUIModel.getSecRuleSetMap();
      if ((null != cdrWizardUIModel.getPrimaryRuleSetId()) &&
          ((null != cdrWizardUIModel.getSecondaryRuleSetIds()) &&
              !cdrWizardUIModel.getSecondaryRuleSetIds().isEmpty()) &&
          secRuleSetMap.containsValue(CDRConstants.RULE_SOURCE.COMMON_RULES.getDbVal())) {
        wpSelWizPage.getSecondaryRulesButton().setSelection(true);
      }
    }
    wpSelWizPage.getSsdRuleButton().setSelection(isSsdRuleButtonSelected(cdrWizardUIModel));
    // UI fill for functions/Labels
    if (cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.A2L_FILE.getDbType())) {
      clearRadioSelection();
      wpSelWizPage.getA2lRadio().setSelection(true);
      cdrWizardUIModel.getSelReviewFuncs().forEach(selfuncName -> {
        wpSelWizPage.getReviewFuncList().add(selfuncName);
        wpSelWizPage.getReviewFuncsSet().add(selfuncName);
      });

      cdrWizardUIModel.getAvailableFunctions().forEach(availfuncName -> {
        wpSelWizPage.getWrkPkgFuncsList().add(availfuncName);
        wpSelWizPage.getWrkPkgFuncsSet().add(availfuncName);
      });
      wpSelWizPage.getWorkingPkg().setEnabled(false);
      wpSelWizPage.getWorkingPkgBrowse().setEnabled(false);

    }
    else if (cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType()) ||
        cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.FUN_FILE.getDbType())) {
      clearRadioSelection();
      wpSelWizPage.getLabFunRadio().setSelection(true);
      wpSelWizPage.getWpLabel().setText("LAB/FUN file");
      if (cdrWizardUIModel.getFunLabFilePath() != null) {
        String funLabFilePath = cdrWizardUIModel.getFunLabFilePath();
        boolean isValidFilePath = CommonUtils.isValidFilePath().test(funLabFilePath);
        wpSelWizPage.getWorkingPkg().setText(isValidFilePath ? funLabFilePath : "");
        wpSelWizPage.getWorkingPkg().setEnabled(true);
        wpSelWizPage.getWorkingPkg().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        wpSelWizPage.getWorkingPkgBrowse().setEnabled(true);
        if (isValidFilePath) {
          cdrWizardUIModel.getSelReviewFuncs().forEach(selfuncName -> {
            wpSelWizPage.getReviewFuncList().add(selfuncName);
            wpSelWizPage.getReviewFuncsSet().add(selfuncName);
          });
        }
        if (cdrWizardUIModel.getSelReviewFuncs().isEmpty() && new File(cdrWizardUIModel.getFunLabFilePath()).exists()) {
          this.calDataReviewWizard.setContentChanged(true);
          if (cdrWizardUIModel.getFunLabFilePath().contains(".")) {
            this.calDataReviewWizard.getFunLabFromParser(cdrWizardUIModel.getFunLabFilePath());
          }
        }
      }


    }
    else if (cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType())) {
      clearRadioSelection();
      wpSelWizPage.getParamRadio().setSelection(true);
      wpSelWizPage.getWorkingPkg().setEnabled(false);
      wpSelWizPage.getWorkingPkgBrowse().setEnabled(false);
    }
    else if (cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType())) {
      clearRadioSelection();
      wpSelWizPage.getCompliRadio().setSelection(true);
      wpSelWizPage.getWorkingPkg().setEnabled(false);
      wpSelWizPage.getWorkingPkgBrowse().setEnabled(false);
    }
    else if (cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.WP.getDbType())) {
      clearRadioSelection();
      wpSelWizPage.getWorkPackageRadio().setSelection(true);
      wpSelWizPage.getWpLabel().setText("Work package");
      String mulWpRespName = cdrWizardUIModel.getMulWPRespNames() == null ? "" : cdrWizardUIModel.getMulWPRespNames();
      wpSelWizPage.getWorkingPkg()
          .setText(cdrWizardUIModel.getWpRespName() == null ? mulWpRespName : cdrWizardUIModel.getWpRespName());
      wpSelWizPage.getWorkingPkg().setEnabled(true);
      wpSelWizPage.getWorkingPkgBrowse().setEnabled(true);
      wpSelWizPage.getWorkingPkg().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      wpSelWizPage.getReviewFuncList().getList().removeAll();
      cdrWizardUIModel.getSelReviewFuncs().forEach(selfuncName -> {
        wpSelWizPage.getReviewFuncList().add(selfuncName);
        wpSelWizPage.getReviewFuncsSet().add(selfuncName);
      });

      if (cdrWizardUIModel.getAvailableFunctions() != null) {
        cdrWizardUIModel.getAvailableFunctions().forEach(availfuncName -> {
          wpSelWizPage.getWrkPkgFuncsList().add(availfuncName);
          wpSelWizPage.getWrkPkgFuncsSet().add(availfuncName);
        });
      }
    }

    setRvwCtrlRadioBtns(cdrWizardUIModel, wpSelWizPage);
  }


  /**
   * @param cdrWizardUIModel
   * @param wpSelWizPage
   */
  private void setRvwCtrlRadioBtns(final CDRWizardUIModel cdrWizardUIModel,
      final WorkpackageSelectionWizardPage wpSelWizPage) {

    String obdFlag = cdrWizardUIModel.getObdFlag();
    if (CommonUtils.isNotNull(obdFlag)) {
      if (CommonUtils.isEqualIgnoreCase(obdFlag, CDRConstants.OBD_OPTION.ONLY_OBD_LABELS.getDbType())) {
        wpSelWizPage.setRvwContentCtrls(true, false, false);
      }
      else if (CommonUtils.isEqualIgnoreCase(obdFlag, CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType())) {
        wpSelWizPage.setRvwContentCtrls(false, true, false);
      }
      else if (CommonUtils.isEqualIgnoreCase(obdFlag,
          CDRConstants.OBD_OPTION.BOTH_OBD_AND_NON_OBD_LABELS.getDbType())) {
        wpSelWizPage.setRvwContentCtrls(false, false, true);
      }
    }
  }


  /**
   * @param cdrWizardUIModel CDRWizardUIModel
   * @return whether Ssd Rule Button is to be selected or not
   */
  public boolean isSsdRuleButtonSelected(final CDRWizardUIModel cdrWizardUIModel) {
    return (null != cdrWizardUIModel.getSsdReleaseId()) ||
        ((null != cdrWizardUIModel.getSsdRuleFilePath()) && cdrWizardUIModel.getSsdRuleFilePath().isEmpty()) ||
        (null != cdrWizardUIModel.getSsdSWVersionId());
  }


  private void clearRadioSelection() {
    this.calDataReviewWizard.getWpSelWizPage().getWorkPackageRadio().setSelection(false);
    this.calDataReviewWizard.getWpSelWizPage().getA2lRadio().setSelection(false);
    this.calDataReviewWizard.getWpSelWizPage().getCompliRadio().setSelection(false);
    this.calDataReviewWizard.getWpSelWizPage().getParamRadio().setSelection(false);
    this.calDataReviewWizard.getWpSelWizPage().getLabFunRadio().setSelection(false);
  }

}
