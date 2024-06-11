/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizard.pages.resolver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.WorkpackageSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;

/**
 * @author say8cob
 */
public class ProjectDataSelectionPageResolver implements IReviewUIDataResolver {

  private CalDataReviewWizard calDataReviewWizard;

  private ProjectDataSelectionWizardPage prjDataSelWizPage;

  final StringBuilder prgMsgForA2lDownload = new StringBuilder("Downloading A2l File ...");

  final StringBuilder prgMsgForFuncFetching = new StringBuilder("Fetching Functions for WorkPackages ...");


  Map<String, Set<String>> functionParamMap = new HashMap<>();

  Map<Long, A2lWpDefnVersion> a2lWpDefMap = new HashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void processNextPressed() {

    if (this.calDataReviewWizard.getProjectSelWizPage().getOffRevRadio().getSelection() &&
        CommonUtils.isEqualIgnoreCase(this.calDataReviewWizard.getProjectSelWizPage().getAuditor().getText(),
            this.calDataReviewWizard.getProjectSelWizPage().getCalEngineer().getText())) {
      CDMLogger.getInstance().errorDialog(
          "For official reviews, the 'Calibration Engineer' and 'Auditor' should be different users",
          Activator.PLUGIN_ID);
      this.calDataReviewWizard.getProjectSelWizPage().setPageComplete(false);

      return;
    }


    try {
      Long pidcA2lId = ProjectDataSelectionPageResolver.this.calDataReviewWizard.getPidcA2LBO().getPidcA2lId();
      this.a2lWpDefMap = getWpDefMap(pidcA2lId);
      if (!this.a2lWpDefMap.isEmpty() && !isWpDefnVersActive()) {
        CDMLogger.getInstance().errorDialog(
            "Active version is not available for the selected A2l. Please set an active version to continue!",
            Activator.PLUGIN_ID);
        this.calDataReviewWizard.getProjectSelWizPage().setPageComplete(false);
        return;

      }
      this.calDataReviewWizard.getProjectSelWizPage().setPageComplete(true);
      this.calDataReviewWizard.getContainer().run(true, false, monitor -> {


        monitor.beginTask(ProjectDataSelectionPageResolver.this.prgMsgForA2lDownload.toString(), 100);
        monitor.worked(40);
        // To Load the A2l Editor Data Provider
        if ((ProjectDataSelectionPageResolver.this.calDataReviewWizard.getPidcA2LBO() != null) &&
            (ProjectDataSelectionPageResolver.this.calDataReviewWizard.getA2lEditiorDataHandler() == null)) {
          try {
            A2LEditorDataProvider a2lEditorDataProvider =
                new A2LEditorDataProvider(ProjectDataSelectionPageResolver.this.calDataReviewWizard.getPidcA2LBO());
            if (a2lEditorDataProvider.getA2lFileInfoBO().getA2lFileInfo() == null) {
              ProjectDataSelectionPageResolver.this.calDataReviewWizard.getProjectSelWizPage().setPageComplete(false);
              return;
            }
            ProjectDataSelectionPageResolver.this.calDataReviewWizard.setA2lEditorDP(a2lEditorDataProvider);
            ProjectDataSelectionPageResolver.this.calDataReviewWizard
                .setA2lEditiorDataHandler(a2lEditorDataProvider.getA2lFileInfoBO());
            // if no def are available , create a working set and active version
            createA2lWpDefinitionVersion(pidcA2lId);
          }
          catch (IcdmException | ApicWebServiceException e) {
            CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            ProjectDataSelectionPageResolver.this.calDataReviewWizard.getProjectSelWizPage().setPageComplete(false);
            return;
          }
        }
        CDRWizardUIModel cdrWizardUIModel =
            ProjectDataSelectionPageResolver.this.calDataReviewWizard.getCdrWizardUIModel();

        cdrWizardUIModel.setA2lWpDefVersId(getActiveWpDefVers());
        monitor.worked(60);
        saveA2Linfo(monitor, cdrWizardUIModel);
        monitor.worked(90);
        monitor.done();

      });

      // For 399482
      setSsdRuleButton();
    }
    catch (InvocationTargetException | InterruptedException | ApicWebServiceException e1) {
      showErrorDialog(e1);
      Thread.currentThread().interrupt();
      return;
    }
    showWarngMsg();

    // Added to Avoid default enabling of Workpackage page next button
    disableDefaultWp();

    // enable the Rvw content controls if the review is not test review and if the PIDC division attribute is mapped to
    // Common Para DIVISION_WITH_OBD_OPTION
    enableDisableOBDOption();
  }

  /**
  *
  */
  private void enableDisableOBDOption() {
    boolean enableRvwCntGrpComp = this.calDataReviewWizard.getCdrWizardUIModel().isPidcDivAppForOBDOpt() &&
        !this.calDataReviewWizard.getProjectSelWizPage().getTestRevRadio().getSelection();
    WorkpackageSelectionWizardPage wpSelWizPage = this.calDataReviewWizard.getWpSelWizPage();
    Composite rvwCntSelGrpComp = wpSelWizPage.getRvwCntSelCtrlGrp();

    // To hide/unHide the group composite:
    GridData gridData = (GridData) rvwCntSelGrpComp.getLayoutData();
    gridData.exclude = !enableRvwCntGrpComp;
    rvwCntSelGrpComp.setVisible(enableRvwCntGrpComp);
    wpSelWizPage.getParentComp().layout(true, true);
  }

  /**
   *
   */
  private void showWarngMsg() {
    if (CommonUtils
        .isNull(ProjectDataSelectionPageResolver.this.calDataReviewWizard.getPidcA2LBO().getSsdSoftwareVersionID())) {
      ProjectDataSelectionPageResolver.this.calDataReviewWizard.getSsdRuleSelPage().setWarnMessage(
          "A2l file is not mapped to any SSD sofware version so no matching SSD releases are found. Please select SSD file to perform review");
    }
  }

  /**
   * @param e1
   */
  private void showErrorDialog(final Exception e1) {
    if (e1.getLocalizedMessage() != null) {
      CDMLogger.getInstance().errorDialog(e1.getLocalizedMessage(), e1, Activator.PLUGIN_ID);
    }
    ProjectDataSelectionPageResolver.this.calDataReviewWizard.getProjectSelWizPage().setPageComplete(false);
  }

  /**
   * @return
   * @return
   */
  private Long getActiveWpDefVers() {
    return ProjectDataSelectionPageResolver.this.a2lWpDefMap.values().stream().filter(A2lWpDefnVersion::isActive)
        .collect(Collectors.toList()).get(0).getId();
  }


  /**
   * @throws ApicWebServiceException
   */
  private void setSsdRuleButton() throws ApicWebServiceException {
    if (new CDRHandler().getValuesByPidcVersAttribute(
        this.calDataReviewWizard.getCdrWizardUIModel().getSelectedPidcVerId(),
        Long.parseLong(new CommonDataBO().getParameterValue(CommonParamKey.SSD_PROJ_NODE_ATTR_ID))) == null) {
      this.calDataReviewWizard.getWpSelWizPage().getSsdRuleButton().setVisible(false);
    }
  }


  /**
   * create a wp def version working set if it's not available
   *
   * @param pidcA2lId
   * @param a2lwpInfoBO
   * @throws ApicWebServiceException
   */
  private void createA2lWpDefinitionVersion(final Long pidcA2lId) throws ApicWebServiceException {
    if (ProjectDataSelectionPageResolver.this.a2lWpDefMap.isEmpty()) {

      A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();

      a2lWpDefnVersion.setVersionName(ApicConstants.WORKING_SET_NAME);
      a2lWpDefnVersion.setActive(false);
      a2lWpDefnVersion.setWorkingSet(true);
      a2lWpDefnVersion.setParamLevelChgAllowedFlag(false);
      a2lWpDefnVersion.setPidcA2lId(pidcA2lId);
      A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();

      // Creates working set - default Resp Pal, Pidc Resps & Param mapping
      client.create(a2lWpDefnVersion, this.calDataReviewWizard.getA2lEditorDataProvider().getPidcA2LBO().getPidcA2l());
      CDMLogger.getInstance().info(
          "WorkingSet with default Work Package-Responsibility defintion has been created for the selected A2L file",
          Activator.PLUGIN_ID);

      // TODO can we avoid service call ?
      // 499373 - WP definition version is not mapped to review result, when doing delta review on an A2L
      // that does not have any definitions versions before
      ProjectDataSelectionPageResolver.this.a2lWpDefMap = getWpDefMap(pidcA2lId);
    }
  }


  /**
   * @param monitor
   * @param cdrWizardUIModel
   */
  private void saveA2Linfo(final IProgressMonitor monitor, final CDRWizardUIModel cdrWizardUIModel) {
    if (ProjectDataSelectionPageResolver.this.calDataReviewWizard.isDeltaReview() &&
        (cdrWizardUIModel.getSourceType() != null) &&
        (ProjectDataSelectionPageResolver.this.calDataReviewWizard.getA2lEditiorDataHandler() != null)) {
      A2LFileInfo a2lFileInfo =
          ProjectDataSelectionPageResolver.this.calDataReviewWizard.getA2lEditiorDataHandler().getA2lFileInfo();
      if (cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.A2L_FILE.getDbType()) &&
          ((cdrWizardUIModel.getAvailableFunctions() == null) || cdrWizardUIModel.getAvailableFunctions().isEmpty())) {

        cdrWizardUIModel.setAvailableFunctions(
            getAvailableFun(cdrWizardUIModel, getAllFunctions(a2lFileInfo.getAllModulesFunctions())));
        // for delata review if a2l file is changed , then set new a2l functions
        Display.getDefault().asyncExec(() -> {
          if (ProjectDataSelectionPageResolver.this.calDataReviewWizard.isA2lFileChanged()) {
            ProjectDataSelectionPageResolver.this.calDataReviewWizard.getWpSelWizPage().getA2lRadio()
                .setSelection(true);
            ProjectDataSelectionPageResolver.this.calDataReviewWizard.getWpSelWizPage().getA2lRadio()
                .notifyListeners(SWT.Selection, new Event());
          }
        });

      }
      monitor.worked(80);


      functionsForSelectedWP(monitor, cdrWizardUIModel);
    }
  }


  /**
   * @param cdrWizardUIModel
   * @param availableFunctions
   * @param allFunctions
   * @return
   */
  private SortedSet<String> getAvailableFun(final CDRWizardUIModel cdrWizardUIModel, final String[] allFunctions) {
    SortedSet<String> availableFunctions = new TreeSet<>();
    for (String function : allFunctions) {
      if (!cdrWizardUIModel.getSelReviewFuncs().contains(function)) {
        availableFunctions.add(function);
      }
    }
    return availableFunctions;
  }


  /**
   * @param isActive
   * @return
   */
  private boolean isWpDefnVersActive() {
    boolean isActive = false;
    for (A2lWpDefnVersion wpDefVers : this.a2lWpDefMap.values()) {
      if (wpDefVers.isActive()) {
        isActive = true;
      }
    }
    return isActive;
  }


  /**
   * @param monitor
   * @param cdrWizardUIModel
   */
  private void functionsForSelectedWP(final IProgressMonitor monitor, final CDRWizardUIModel cdrWizardUIModel) {
    if ((cdrWizardUIModel.getSourceType().equalsIgnoreCase(CDRConstants.CDR_SOURCE_TYPE.WP.getDbType())) &&
        (cdrWizardUIModel.getAvailableFunctions() == null)) {
      monitor.setTaskName(ProjectDataSelectionPageResolver.this.prgMsgForFuncFetching.toString());

      SortedSet<String> availableFunctions = new TreeSet<>();
      // fetching functions for the selected Wps
      SortedSet<String> activeA2lWpdefVerfunctions = getWpFunctions();
      SortedSet<String> selReviewFuncs = cdrWizardUIModel.getSelReviewFuncs();

      activeA2lWpdefVerfunctions.forEach(function -> {
        if (!selReviewFuncs.contains(function)) {
          availableFunctions.add(function);
        }
      });

      // Remove 'not mapped function' in new Active WP def version from Selected Review Function list of parent review
      selReviewFuncs.removeIf(selFunc -> !(activeA2lWpdefVerfunctions.contains(selFunc)));

      cdrWizardUIModel.setFunctionMap(ProjectDataSelectionPageResolver.this.functionParamMap);
      cdrWizardUIModel.setAvailableFunctions(availableFunctions);
    }
  }

  /**
   *
   */
  private void disableDefaultWp() {
    if (this.calDataReviewWizard.getCdrWizardUIModel().getSourceType() == null) {
      this.calDataReviewWizard.getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.WP.getDbType());
    }
  }

  /**
   * @param pidcA2lId
   * @return
   * @throws ApicWebServiceException
   */
  private Map<Long, A2lWpDefnVersion> getWpDefMap(final Long pidcA2lId) throws ApicWebServiceException {
    return new A2lWpDefinitionVersionServiceClient().getWPDefnVersForPidcA2l(pidcA2lId);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void processBackPressed() {
    // No implementation
  }

  private void clearCDRWizardModelForProjectDataPage() {
    if (this.calDataReviewWizard != null) {
      this.calDataReviewWizard.getCdrWizardUIModel().setReviewType(null);
      this.calDataReviewWizard.getCdrWizardUIModel().setDescription(null);
      this.calDataReviewWizard.getCdrWizardUIModel().setSelParticipantsIds(new HashSet<>());
      this.calDataReviewWizard.getCdrWizardUIModel().setSelAuditorId(null);
      this.calDataReviewWizard.getCdrWizardUIModel().setSelCalEngineerId(null);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setInput(final CalDataReviewWizard calDataReviewWizard) {
    clearCDRWizardModelForProjectDataPage();
    Map<String, User> apicUserMap;


    Set<Long> selParticipantIdList = new HashSet<>();

    this.calDataReviewWizard = calDataReviewWizard;
    this.prjDataSelWizPage = calDataReviewWizard.getProjectSelWizPage();
    // Set Review Type
    setReviewType(calDataReviewWizard);
    // Set Description
    if (!this.prjDataSelWizPage.getDescriptions().getText()
        .equals(this.calDataReviewWizard.getCdrWizardUIModel().getDescription())) {
      calDataReviewWizard.getCdrWizardUIModel().setDescription(this.prjDataSelWizPage.getDescriptions().getText());
    }

    // Set Participants

    try {
      List<String> userNameList = new ArrayList<>();
      if (!calDataReviewWizard.getCdrWizardUIModel().getParticipantUserNameList().isEmpty()) {

        userNameList.addAll(calDataReviewWizard.getCdrWizardUIModel().getParticipantUserNameList());
      }

      String auditorUserName = calDataReviewWizard.getCdrWizardUIModel().getAuditorUserName();
      if ((auditorUserName != null) && !auditorUserName.isEmpty()) {

        userNameList.add(auditorUserName);
      }
      String calEngUserName = calDataReviewWizard.getCdrWizardUIModel().getCalEngUserName();
      if ((calEngUserName != null) && !calEngUserName.isEmpty()) {
        userNameList.add(calEngUserName);

      }

      apicUserMap = this.prjDataSelWizPage.getCdrHandler().getApicUsersMap(userNameList);
      if (!calDataReviewWizard.getCdrWizardUIModel().getParticipantUserNameList().isEmpty()) {
        calDataReviewWizard.getCdrWizardUIModel().getParticipantUserNameList().forEach(participant -> {
          User user = apicUserMap.get(participant);
          selParticipantIdList.add(user.getId());

        });

      }
      if ((auditorUserName != null) && !auditorUserName.isEmpty()) {
        calDataReviewWizard.getCdrWizardUIModel().setSelAuditorId(apicUserMap.get(auditorUserName).getId());
      }
      if ((calEngUserName != null) && !calEngUserName.isEmpty()) {
        calDataReviewWizard.getCdrWizardUIModel().setSelCalEngineerId(apicUserMap.get(calEngUserName).getId());
      }
      calDataReviewWizard.getCdrWizardUIModel().setSelParticipantsIds(selParticipantIdList);
      if (calDataReviewWizard.getCdrWizardUIModel().getSourceType() == null) {
        calDataReviewWizard.getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    if (!calDataReviewWizard.isDeltaReview() || calDataReviewWizard.isProjectDataDeltaReview()) {
      this.calDataReviewWizard.getCdrWizardUIModel().setLabelList(new ArrayList<>());
    }


  }

  /**
   * @param calDataReviewWizard
   */
  private void setReviewType(final CalDataReviewWizard calDataReviewWizard) {
    ProjectDataSelectionWizardPage projectSelWizPage = calDataReviewWizard.getProjectSelWizPage();
    calDataReviewWizard.getCdrWizardUIModel().setReviewType(projectSelWizPage.getStartRevRadio().getSelection()
        ? CDRConstants.REVIEW_TYPE.START.getDbType() : isOfficialReview(projectSelWizPage));
  }


  /**
   * @param projectSelWizPage
   * @return
   */
  private String isOfficialReview(final ProjectDataSelectionWizardPage projectSelWizPage) {
    return projectSelWizPage.getOffRevRadio().getSelection() ? CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType()
        : CDRConstants.REVIEW_TYPE.TEST.getDbType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CalDataReviewWizard getInput() {
    return this.calDataReviewWizard;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fillUIData(final CalDataReviewWizard calDataRvwWizard) {


    this.calDataReviewWizard = calDataRvwWizard;
    this.prjDataSelWizPage = calDataRvwWizard.getProjectSelWizPage();

    CDRWizardUIModel cdrWizardUIModel = calDataRvwWizard.getCdrWizardUIModel();

    if (cdrWizardUIModel.getAuditorUserFullName() != null) {
      this.prjDataSelWizPage.getAuditor().setText(cdrWizardUIModel.getAuditorUserFullName());
    }
    if (cdrWizardUIModel.getCalEngUserFullName() != null) {
      this.prjDataSelWizPage.getCalEngineer().setText(cdrWizardUIModel.getCalEngUserFullName());
    }
    fillParticipantsList(cdrWizardUIModel.getParticipantUserFullNameList());

    fillSelParticipants(cdrWizardUIModel.getParticipantUserNameList());

    if (cdrWizardUIModel.getDescription() != null) {
      this.prjDataSelWizPage.getDescriptions().setText(cdrWizardUIModel.getDescription());
    }
    if (cdrWizardUIModel.getSelectedPidcVariantName() != null) {
      this.prjDataSelWizPage.setVariantDecorator(true);

      this.prjDataSelWizPage.getVariantName().setText(cdrWizardUIModel.getSelectedPidcVariantName());
    }

    this.prjDataSelWizPage.setVariantDecorator(calDataRvwWizard.isHasVariant());

    if (cdrWizardUIModel.getProjectName() != null) {
      this.prjDataSelWizPage.getProjectName().setText(cdrWizardUIModel.getProjectName());
    }
    setReviewRadioSelection(calDataRvwWizard, cdrWizardUIModel.getReviewType());

    try {

      if (isVarDeleted(cdrWizardUIModel.getSelectedPidcVariantId())) {
        this.prjDataSelWizPage.getA2lNameCombo().removeAll();
        this.calDataReviewWizard.getCdrWizardUIModel().setPidcA2lId(null);
        this.calDataReviewWizard.getCdrWizardUIModel().setA2lFileId(null);
      }
      else {
        this.prjDataSelWizPage.getA2lNameCombo().removeAll();
        SortedSet<String> a2lFileNames = new TreeSet<>();
        String selA2lFileForDisp = "";
        selA2lFileForDisp = findActiveA2lFileNames(cdrWizardUIModel, a2lFileNames, selA2lFileForDisp);
        this.prjDataSelWizPage.getA2lNameComboViewer().setContentProvider(ArrayContentProvider.getInstance());
        // Adding a2l file name to the combo
        this.prjDataSelWizPage.getA2lNameComboViewer().setInput(a2lFileNames);
        // enable filtering A2lFile name when A2lFile name is typed on the combo
        this.prjDataSelWizPage.setA2lComboContentProposal();
        if (!(CommonUtils.isEmptyString(selA2lFileForDisp))) {
          // Set selection of a2l file in combo
          selectA2lFileInCombo(selA2lFileForDisp);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    this.prjDataSelWizPage.getProjectDataSelectionWizardPageValidator().checkNextBtnEnable();
  }

  /**
   * @param cdrWizardUIModel
   * @param a2lFileNames
   * @param selA2lFileForDisp
   * @return
   * @throws ApicWebServiceException
   */
  private String findActiveA2lFileNames(final CDRWizardUIModel cdrWizardUIModel, final SortedSet<String> a2lFileNames,
      String selA2lFileForDisp)
      throws ApicWebServiceException {
    Map<Long, PidcA2l> a2lFileBySdom = new CDRHandler().getA2LFileBySdom(cdrWizardUIModel.getSelectedPidcVerId(),
        cdrWizardUIModel.getSelectedReviewPverName());
    for (PidcA2l pidcA2l : a2lFileBySdom.values()) {
      if (pidcA2l.isActive()) {
        a2lFileNames.add(pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " + pidcA2l.getName());
        if ((cdrWizardUIModel.getA2lFileId() != null) &&
            cdrWizardUIModel.getA2lFileId().equals(pidcA2l.getA2lFileId())) {
          selA2lFileForDisp =
              pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " + pidcA2l.getName();
        }
      }
    }
    return selA2lFileForDisp;
  }

  /**
   * @param selPidcVarId
   * @param varDeleted
   * @param var
   * @return
   * @throws ApicWebServiceException
   */
  private boolean isVarDeleted(final Long selPidcVarId) throws ApicWebServiceException {
    boolean varDeleted = false;
    if (CommonUtils.isNotNull(selPidcVarId)) {
      PidcVariant var = new PidcVariantServiceClient().get(selPidcVarId);
      if (var.isDeleted()) {
        varDeleted = true;
        CDMLogger.getInstance().infoDialog(
            "Variant " + var.getName() + " is deleted. Please select another variant to continue.",
            Activator.PLUGIN_ID);
      }
    }
    return varDeleted;
  }

  /**
   * @param selA2lFileForDisp
   */
  private void selectA2lFileInCombo(final String selA2lFileForDisp) {
    String[] items = this.prjDataSelWizPage.getA2lNameCombo().getItems();
    for (int count = 0; count < items.length; count++) {
      if (items[count].equals(selA2lFileForDisp)) {
        this.prjDataSelWizPage.getA2lNameCombo().select(count);
      }
    }
  }

  /**
   * @param calDataRvwWizard
   * @param rvwType
   */
  private void setReviewRadioSelection(final CalDataReviewWizard calDataRvwWizard, final String rvwType) {
    if (CommonUtils.isNotNull(rvwType)) {
      if (CommonUtils.isEqualIgnoreCase(rvwType, CDRConstants.REVIEW_TYPE.START.getDbType())) {
        setRvwTypeRadioSel(calDataRvwWizard.getProjectSelWizPage(), true, false, false);
      }
      else if (CommonUtils.isEqualIgnoreCase(rvwType, CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType())) {
        setRvwTypeRadioSel(calDataRvwWizard.getProjectSelWizPage(), false, true, false);
      }
      else if (CommonUtils.isEqualIgnoreCase(rvwType, CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
        setRvwTypeRadioSel(calDataRvwWizard.getProjectSelWizPage(), false, false, true);
      }
    }
  }


  /**
   * @param calDataRvwWizard
   */
  private void setRvwTypeRadioSel(final ProjectDataSelectionWizardPage projectSelWizPage,
      final boolean startRvwRadioSel, final boolean offRvwRadioSel, final boolean testRvwRadioSel) {
    projectSelWizPage.getStartRevRadio().setSelection(startRvwRadioSel);
    projectSelWizPage.getOffRevRadio().setSelection(offRvwRadioSel);
    projectSelWizPage.getTestRevRadio().setSelection(testRvwRadioSel);
  }

  /**
   * @param partcpantUserNameList
   */
  private void fillSelParticipants(final List<String> partcpantUserNameList) {
    if (CommonUtils.isNotEmpty(partcpantUserNameList)) {
      try {
        Map<String, User> userName = new UserServiceClient().getApicUserByUsername(partcpantUserNameList);
        this.prjDataSelWizPage.getSelParticipants().addAll(userName.values());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param partcpntUserFullNameList
   */
  private void fillParticipantsList(final List<String> partcpntUserFullNameList) {
    if (CommonUtils.isNotEmpty(partcpntUserFullNameList)) {
      this.prjDataSelWizPage.getParticipantsList().removeAll();
      for (String participantName : partcpntUserFullNameList) {
        this.prjDataSelWizPage.getParticipantsList().add(participantName);
      }
    }
  }

  /**
   * @return all functions of the selected A2l file
   */
  private String[] getAllFunctions(final Map<String, Function> allModulesFunctions) {
    final SortedSet<Function> funcListOfLabelType = new TreeSet<>();
    // iterate over all functions
    for (Function function : allModulesFunctions.values()) {
      if (function != null) {
        final LabelList defLabel = function.getLabelList(LabelType.DEF_CHARACTERISTIC);
        if ((defLabel != null) && (!defLabel.isEmpty())) {
          funcListOfLabelType.add(function);
        }
      }
    }
    Iterator<Function> functions = funcListOfLabelType.iterator();
    String[] a2lFunctions = new String[funcListOfLabelType.size()];
    String funcName;
    int index = 0;
    while (functions.hasNext()) {
      Function selFunc = functions.next();
      funcName = selFunc.getName();
      a2lFunctions[index] = funcName;
      index++;
    }
    return a2lFunctions;
  }


  private SortedSet<String> getWpFunctions() {
    Map<WpRespModel, List<Long>> workPackageRespMap = this.calDataReviewWizard.getWorkPackageRespMap();
    SortedSet<String> functionSet = new TreeSet<>();
    Set<Long> paramIdSet = new HashSet<>();
    this.calDataReviewWizard.getCdrWizardUIModel().getRvwWpAndRespModelSet()
        .forEach(wpAndRespModelSet -> workPackageRespMap.keySet().stream().forEach(val -> {
          if (wpAndRespModelSet.getA2lWpId().equals(val.getA2lWpId()) &&
              wpAndRespModelSet.getA2lRespId().equals(val.getA2lResponsibility().getId())) {
            paramIdSet.addAll(workPackageRespMap.get(val));
          }
        }));


    Map<Long, Parameter> paramMap = new HashMap<>();
    try {
      paramMap = new CDRHandler().getParamMapUsingParamIdSet(paramIdSet);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    if (!paramMap.isEmpty()) {
      functionSet = resolveFunctionsForA2lWorkPackageResp(paramMap);


    }
    return functionSet;
  }


  private SortedSet<String> resolveFunctionsForA2lWorkPackageResp(final Map<Long, Parameter> paramMap) {
    SortedSet<String> funcNameSet = new TreeSet<>();
    Map<String, Characteristic> characteristicsMap =
        this.calDataReviewWizard.getA2lEditiorDataHandler().getCharacteristicsMap();
    for (Parameter parameter : paramMap.values()) {
      Characteristic charObj = characteristicsMap.get(parameter.getName());
      Function func = charObj.getDefFunction();
      String functionName = "";
      if (func == null) {
        if (CommonUtils.isNotEmpty(this.calDataReviewWizard.getA2lEditiorDataHandler().getUnassignedParams())) {
          // Not assigned will be added if the parameter is not assigned to any parameters
          functionName = ApicConstants.NOT_ASSIGNED;
          funcNameSet.add(functionName);
        }
      }
      else {
        functionName = func.getName();
      }
      if (!funcNameSet.contains(functionName)) {
        funcNameSet.add(functionName);
      }
      createFuncParamMap(functionName, parameter.getName());

    }
    return funcNameSet;
  }

  private void createFuncParamMap(final String function, final String paramName) {

    if (this.functionParamMap.containsKey(function)) {
      Set<String> paramSet = this.functionParamMap.get(function);
      paramSet.add(paramName);
      this.functionParamMap.put(function, paramSet);
    }
    else {
      Set<String> paramSet = new HashSet<>();
      paramSet.add(paramName);
      this.functionParamMap.put(function, paramSet);
    }

  }


  /**
   * @return the a2lWpDefMap
   */
  public Map<Long, A2lWpDefnVersion> getA2lWpDefMap() {
    return this.a2lWpDefMap;
  }


  /**
   * @param a2lWpDefMap the a2lWpDefMap to set
   */
  public void setA2lWpDefMap(final Map<Long, A2lWpDefnVersion> a2lWpDefMap) {
    this.a2lWpDefMap = a2lWpDefMap;
  }
}
