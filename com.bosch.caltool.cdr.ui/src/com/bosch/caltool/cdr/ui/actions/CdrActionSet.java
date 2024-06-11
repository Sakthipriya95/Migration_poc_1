/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.CompliReviewDialog;
import com.bosch.caltool.cdr.ui.dialogs.MonicaReviewDialog;
import com.bosch.caltool.cdr.ui.dialogs.ReviewResultExportDialog;
import com.bosch.caltool.cdr.ui.dialogs.ReviewRuleExcelExportDialog;
import com.bosch.caltool.cdr.ui.editors.QnaireRespEditorInput;
import com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.jobs.CdrReportExcelExportJob;
import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizard;
import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizardDialog;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizardDialog;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapperCloseable;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeNodeChildren;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarRvwDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.model.cdr.ReviewResultEditorData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.SdomPverServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CdfxExportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * Action classes related to CDR
 *
 * @author bru2cob
 */
// ICDM-523
public class CdrActionSet {

  /**
   *
   */
  private static final String CONS_TBL_DEFN_OPEN_TAG = "<td>";

  /**
   *
   */
  private static final String CONS_TBL_DEFN_CLOSE_TAG = "</td>";

  /**
   * Constant for start delta review
   */
  private static final String START_DELTA_REVIEW = "Start Delta Review";

  /**
   * Instance of calDataReviewWizardNew
   */
  private CalDataReviewWizard calDataReviewWizard;

  /**
   * Append _filter for the data review report file name if the report is generated from resp or Wp of a2lStructure View
   */
  public static final String SUFFIX_FOR_DATARVW_RPRT_FROM_RESP_WP = "_filter";
  /**
   * when first instanceof PidcTreeNode is true,Nat filtering takes place
   */
  boolean isPidcTreeNode = false;


  /**
   * Create the action to start the data review. The action is also added to the context menu.
   *
   * @param manager menu manager
   * @param firstElement selected a2lfile
   */
  public void setReviewProcessAction(final IMenuManager manager, final Object firstElement) {
    final Action moveAction = new Action() {

      /**
       * Start review
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {

        CdrActionSet.this.calDataReviewWizard = new CalDataReviewWizard(true, false, null);
        CalDataReviewWizardDialog calDtWizardDialog = new CalDataReviewWizardDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), CdrActionSet.this.calDataReviewWizard);
        calDtWizardDialog.create();
        // get selected a2l file and pid card
        PidcTreeNode pidcTreeNode = (PidcTreeNode) firstElement;
        // set Needed data for CaldataWizard
        setDataInCalDataWizard(pidcTreeNode);
        // open caldata wizard if division attribute is set in PIDC
        openCalDataWizDialog(calDtWizardDialog, pidcTreeNode);

      }
    };
    moveAction.setText("Start Calibration Data Review");
    if (CommonUtils.isNotNull(((PidcTreeNode) firstElement).getPidcA2l()) &&
        !((PidcTreeNode) firstElement).getPidcA2l().isActive()) {
      moveAction.setEnabled(false);
    }

    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.START_CDRREVIEW_16X16);
    moveAction.setImageDescriptor(imageDesc);
    moveAction.setEnabled(((PidcTreeNode) firstElement).getPidcA2l().isActive());
    manager.add(moveAction);
  }

  /**
   * @param pidcTreeNode
   */
  private void setDataInCalDataWizard(final PidcTreeNode pidcTreeNode) {

    try {
      CdrActionSet.this.calDataReviewWizard.setHasVaraint(pidcTreeNode.getPidcVersion().getId(), false);
    }
    catch (ApicWebServiceException e1) {
      CDMLogger.getInstance().errorDialog(e1.getLocalizedMessage(), e1, Activator.PLUGIN_ID);
    }
    // load data for project selection page
    loadProjectDataSelectionPage(pidcTreeNode);
  }

  /**
   * Actionset to start 100% CDFX export
   *
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void cdfxExportAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    final Action moveAction = new Action() {

      /**
       * Start review
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // create wizard instance
        List<WpRespModel> wpRespModelList = new ArrayList<>();
        CDFXExportWizard wizard =
            new CDFXExportWizard(pidcTreeNode, getCdfxReadinessConditionStr(), wpRespModelList, false);
        // create wizard dialog instance
        CDFXExportWizardDialog rvwReportDialog =
            new CDFXExportWizardDialog(Display.getCurrent().getActiveShell(), wizard);
        rvwReportDialog.setMinimumPageSize(250, 250);
        rvwReportDialog.create();
        // open report dialog
        rvwReportDialog.open();
      }
    };
    moveAction.setText("100% CDFx Export");
    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      moveAction.setEnabled(false);
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    moveAction.setImageDescriptor(imageDesc);
    manager.add(moveAction);

  }

  /**
   * @return
   */
  private String getCdfxReadinessConditionStr() {
    String htmlContentStr = "";
    try {
      htmlContentStr = FileIOUtil.convertHtmlByteToString(
          new CdfxExportServiceClient().getCdfxReadinessConditionFile(CommonUtils.getSystemUserTempDirPath()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return htmlContentStr;
  }


  private void loadProjectDataSelectionPage(final PidcTreeNode pidcTreeNode) {

    // SSD SW Version Id for SSDRuleSelection Page
    Long ssdSoftwareVersionId = pidcTreeNode.getPidcA2l().getSsdSoftwareVersionId();
    CdrActionSet.this.calDataReviewWizard.getCdrWizardUIModel().setSsdSWVersionId(ssdSoftwareVersionId);
    // Setting Pidc Data
    this.calDataReviewWizard.getCdrWizardUIModel().setPidcA2lId(pidcTreeNode.getPidcA2l().getId());
    this.calDataReviewWizard.getCdrWizardUIModel().setA2lFileId(pidcTreeNode.getPidcA2l().getA2lFileId());
    this.calDataReviewWizard.getCdrWizardUIModel().setSelectedPidcVerId(pidcTreeNode.getPidcVersion().getId());
    this.calDataReviewWizard.getProjectSelWizPage().setProjectName(pidcTreeNode.getPidcVersion().getName());
    if (!this.calDataReviewWizard.getProjectSelWizPage().getStartRevRadio().getSelection() &&
        !this.calDataReviewWizard.getProjectSelWizPage().getTestRevRadio().getSelection() &&
        !this.calDataReviewWizard.getProjectSelWizPage().getOffRevRadio().getSelection()) {
      this.calDataReviewWizard.getProjectSelWizPage().getStartRevRadio().setSelection(true);
    }
    this.calDataReviewWizard.setA2lEditiorDataHandler(null);
    // Download a2L file
    PidcA2LBO pidcA2LBO = new PidcA2LBO(pidcTreeNode.getPidcA2l().getId(), null);
    this.calDataReviewWizard.setPidcA2LBO(pidcA2LBO);
    this.calDataReviewWizard.getProjectSelWizPage().setVariantDecorator(this.calDataReviewWizard.isHasVariant());
  }


  /**
   * Creates the 'Export to excel' menu action
   *
   * @param manager menu manager
   * @param firstElement firstElement in the selection
   */
  public void setResultExportAction(final IMenuManager manager, final Object firstElement) {


    final Action exportAction = new Action() {

      /**
       * Save result to an excel document
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDRReviewResult cdrResult = getCdrResult(firstElement);
        Set<CDRReviewResult> results = new HashSet<>();
        results.add(cdrResult);
        ReviewResultExportDialog exportDiallog =
            new ReviewResultExportDialog(Display.getDefault().getActiveShell(), results);
        exportDiallog.open();
      }
    };
    exportAction.setText("Excel Report");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16);
    exportAction.setImageDescriptor(imageDesc);
    manager.add(exportAction);

    // Check review status before enabling the menu item
    CDRReviewResult cdrResult = getCdrResult(firstElement);
    // Export is not possible for cancelled reviews(i.e, incomplete reviews)
    if ((cdrResult != null) &&
        (CDRConstants.REVIEW_STATUS.OPEN == CDRConstants.REVIEW_STATUS.getType(cdrResult.getRvwStatus()))) {
      exportAction.setEnabled(false);
    }

  }

  private CDRReviewResult getCdrResult(final Object firstElement) {
    CDRReviewResult cdrResult = null;
    if (firstElement instanceof PidcTreeNode) {
      cdrResult = getCdrResultFromTreeNode(firstElement);
    }
    else {
      final ReviewResultData resultData = getCdrResult(firstElement, false);
      if (resultData != null) {
        cdrResult = resultData.getCdrReviewResult();
      }
    }
    return cdrResult;
  }

  private PidcVarRvwDetails getResultObjectForExtLink(final Object selection) {
    PidcVarRvwDetails ret = new PidcVarRvwDetails();
    if (selection instanceof PidcTreeNode) {
      PidcTreeNode treeNode = (PidcTreeNode) selection;
      ret.setReviewResult(treeNode.getReviewResult());
      ret.setReviewVariant(treeNode.getReviewVarResult());
    }
    else if (selection instanceof ReviewResultData) {
      ret.setReviewResult(((ReviewResultData) selection).getCdrReviewResult());
    }
    else if (selection instanceof ReviewVariantModel) {
      ReviewVariantModel rvwVarMdl = (ReviewVariantModel) selection;
      ret.setReviewResult(rvwVarMdl.getReviewResultData().getCdrReviewResult());
      ret.setReviewVariant(rvwVarMdl.getRvwVariant());
    }

    return ret;
  }

  /**
   * Get the cdr result obj
   *
   * @param firstElement
   * @return
   */
  private ReviewResultData getCdrResult(final Object firstElement, final boolean setVariant) {
    ReviewResultData cdrResult = null;
    if (firstElement instanceof ReviewResultData) {
      cdrResult = (ReviewResultData) firstElement;
    }
    else if (firstElement instanceof ReviewVariantModel) {
      cdrResult = ((ReviewVariantModel) firstElement).getReviewResultData();
      if (setVariant) {
        cdrResult.setLinkedVar(((ReviewVariantModel) firstElement).getReviewResultData().getPidcVariant());
      }
    }
    return cdrResult;
  }

  private CDRReviewResult getCdrResultFromTreeNode(final Object firstElement) {
    CDRReviewResult cdrResult = null;
    if (firstElement instanceof PidcTreeNode) {
      cdrResult = ((PidcTreeNode) firstElement).getReviewResult();
    }
    return cdrResult;
  }

  /**
   * Creats the Delta review menu action
   *
   * @param manager menu manager
   * @param firstElement selected review item
   */
  public void setDeltaReviewAction(final IMenuManager manager, final Object firstElement) {


    final Action deltaReviewAction = new Action() {

      /**
       * Open delta review wizard
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CDRHandler cdrHandler = new CDRHandler();

        CDRReviewResult result = getCdrResult(firstElement);

        // Method to Open Delta/Cancelled Review Dialog
        if ((result != null) && !("<MONICA_REPORT>").equalsIgnoreCase(result.getGrpWorkPkg())) {
          openDeltaOrCancelledReview(result, cdrHandler, firstElement);
        }
        else {
          openDeltaOrCancelledMonicaReview(result);
        }
      }
    };
    deltaReviewAction.setText(START_DELTA_REVIEW);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELTA_REVIEW_28X30);
    deltaReviewAction.setImageDescriptor(imageDesc);
    // Disable delta review for a result which is in OPEN state

    CDRReviewResult result = getCdrResult(firstElement);
    if ((result != null) && (CDRConstants.REVIEW_STATUS.getType(result.getRvwStatus()) == REVIEW_STATUS.OPEN)) {
      deltaReviewAction.setEnabled(false);
    }
    manager.add(deltaReviewAction);
  }


  /**
   * @param cdrHandler
   * @param pidcTreeNode
   * @throws ApicWebServiceException
   * @throws NumberFormatException
   */
  private boolean getPidcDivisionAttributes(final Long pidcVersionId) throws ApicWebServiceException {
    return new CDRHandler().getPidcDivisionAttributes(pidcVersionId);
  }

  /**
   * Method to fetch the CDRWizardUI Model for Delta and Cancelled Review
   *
   * @param cdrHandler
   * @param isReviewAttributeSet
   * @param pidcTreeNode
   * @param deltaReviewType
   * @return
   * @throws ApicWebServiceException
   */
  private boolean getCDRWizardUiModel(final CDRHandler cdrHandler, final CDRReviewResult cdrReviewResult,
      final DELTA_REVIEW_TYPE deltaReviewType)
      throws ApicWebServiceException {
    CdrActionSet.this.calDataReviewWizard = new CalDataReviewWizard(true, true, deltaReviewType);
    boolean isReviewAttributeSet = false;

    CDRWizardUIModel reviewResultForDeltaReview = cdrHandler.getReviewResultForDeltaReview(cdrReviewResult.getId());
    if (reviewResultForDeltaReview != null) {
      CdrActionSet.this.calDataReviewWizard.setCdrWizardUIModel(reviewResultForDeltaReview);
      CdrActionSet.this.calDataReviewWizard.setParentCDRResultId(cdrReviewResult.getId());
      isReviewAttributeSet = getPidcDivisionAttributes(reviewResultForDeltaReview.getSelectedPidcVerId());
    }

    return isReviewAttributeSet;
  }


  /**
   * Creats the Delta review menu action for project data delta review
   *
   * @param manager menu manager
   * @param pidcTreeNode Pidc Tree Node
   */
  public void setProjDataDeltaReviewAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {


    final Action deltaReviewAction = new Action() {

      /**
       * Open delta review wizard
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        try {

          // To Open Project delta Review
          openProjDataDeltaReviewWizard(pidcTreeNode);
        }
        catch (NumberFormatException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }


    };
    deltaReviewAction.setText(START_DELTA_REVIEW);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELTA_REVIEW_28X30);
    deltaReviewAction.setImageDescriptor(imageDesc);
    manager.add(deltaReviewAction);
  }

  /**
   * Creats the Delta review menu action for project data delta review
   *
   * @param manager menu manager
   * @param pidcTreeNode Pidc Tree Node
   */
  public void setProjDataDeltaReviewActionPIDCLevel(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {


    final Action deltaReviewAction = new Action() {

      /**
       * Open delta review wizard
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {

        try {
          if (new CDRHandler().hasVariant(pidcTreeNode.getPidcVersion().getId(), false)) {
            CDMLogger.getInstance().infoDialog(
                "Please start the Project Data delta review on a variant node below the \"Review Results\"",
                Activator.PLUGIN_ID);
          }
          else {
            // To Open Project delta Review
            openProjDataDeltaReviewWizard(pidcTreeNode);
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }


    };
    deltaReviewAction.setText(START_DELTA_REVIEW);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELTA_REVIEW_28X30);
    deltaReviewAction.setImageDescriptor(imageDesc);
    deltaReviewAction.setEnabled(true);
    manager.add(deltaReviewAction);
  }

  /**
   * Open project data delta review wizard
   *
   * @param pidcTreeNode Pidc Tree Node
   */
  protected void openProjDataDeltaReviewWizard(final PidcTreeNode pidcTreeNode) {

    PidcTreeNodeHandler pidcTreeNodeHandler = new PidcTreeNodeHandler(true);
    CdrActionSet.this.calDataReviewWizard =
        new CalDataReviewWizard(false, null, true, DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW);
    CdrActionSet.this.calDataReviewWizard.setPidcTreeNodeHandler(pidcTreeNodeHandler);

    CalDataReviewWizardDialog calDataWizardDialog = new CalDataReviewWizardDialog(
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), CdrActionSet.this.calDataReviewWizard);
    calDataWizardDialog.create();

    // set details in wizard for the project data delta review
    setUIDataForProjDataSelPage(pidcTreeNode, pidcTreeNodeHandler);

    // open caldata wizard if division attribute is set in PIDC
    openCalDataWizDialog(calDataWizardDialog, pidcTreeNode);
  }

  /**
   * @param pidcTreeNode
   * @param pidcTreeNodeHandler
   */
  private void setUIDataForProjDataSelPage(final PidcTreeNode pidcTreeNode,
      final PidcTreeNodeHandler pidcTreeNodeHandler) {
    if (pidcTreeNode.getPidcA2l() != null) {
      this.calDataReviewWizard.getCdrWizardUIModel().setPidcA2lId(pidcTreeNode.getPidcA2l().getId());
      this.calDataReviewWizard.getCdrWizardUIModel().setA2lFileId(pidcTreeNode.getPidcA2l().getA2lFileId());
      this.calDataReviewWizard.getCdrWizardUIModel()
          .setSelectedReviewPverName(pidcTreeNode.getPidcA2l().getSdomPverName());
    }
    else if (pidcTreeNode.getPidcA2l() == null) {
      if ((pidcTreeNode.getSdomA2lMap() != null) && !pidcTreeNode.getSdomA2lMap().isEmpty()) {
        this.calDataReviewWizard.getCdrWizardUIModel()
            .setSelectedReviewPverName(pidcTreeNode.getSdomA2lMap().entrySet().iterator().next().getKey());
      }
      else {
        PidcTreeNodeChildren childNodesAvailblty = pidcTreeNodeHandler.getPidcNodeChildAvailblty(pidcTreeNode);
        if ((childNodesAvailblty.getPidcSdomPverSet() != null) && !childNodesAvailblty.getPidcSdomPverSet().isEmpty()) {
          this.calDataReviewWizard.getCdrWizardUIModel()
              .setSelectedReviewPverName(childNodesAvailblty.getPidcSdomPverSet().iterator().next().getPverName());
        }
      }
    }
    this.calDataReviewWizard.getCdrWizardUIModel().setSelectedPidcVerId(pidcTreeNode.getPidcVersion().getId());
    this.calDataReviewWizard.getProjectSelWizPage().setProjectName(pidcTreeNode.getPidcVersion().getName());
    Long pidcVariantId = null;
    if (pidcTreeNode.getPidcVariant() != null) {
      pidcVariantId = pidcTreeNode.getPidcVariant().getId();
      String sdomPverName;
      try {
        sdomPverName =
            new SdomPverServiceClient().getSDOMPverName(pidcTreeNode.getPidcVersion().getId(), pidcVariantId);
        this.calDataReviewWizard.getCdrWizardUIModel().setSelectedReviewPverName(sdomPverName);
        // To check the version having varinats
        CdrActionSet.this.calDataReviewWizard.setHasVaraint(pidcTreeNode.getPidcVersion().getId(), false);
      }
      catch (ApicWebServiceException e1) {
        CDMLogger.getInstance().errorDialog(e1.getLocalizedMessage(), e1, Activator.PLUGIN_ID);
      }

    }

    CdrActionSet.this.calDataReviewWizard.fillProjectDataDeltaaReviewWizardData(pidcTreeNode.getPidcVariant(),
        pidcTreeNode.getPidcVersion());

    this.calDataReviewWizard.getProjectSelWizPage().getProjectDataSelectionPageResolver()
        .fillUIData(this.calDataReviewWizard);
  }


  /**
   * @param calDataWizardDialog
   * @param pidcTreeNode
   */
  private void openCalDataWizDialog(final CalDataReviewWizardDialog calDataWizardDialog,
      final PidcTreeNode pidcTreeNode) {
    boolean isReviewAttributeSet;
    try {
      isReviewAttributeSet = getPidcDivisionAttributes(pidcTreeNode.getPidcVersion().getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      isReviewAttributeSet = false;
    }
    if (isReviewAttributeSet) {
      // check and assign whether Pidc division attribute value is mapped to common param DIVISION_WITH_OBD_OPTION
      CdrActionSet.this.calDataReviewWizard.getCdrWizardUIModel()
          .setPidcDivAppForOBDOpt(new CDRHandler().isDivIdAppForOBDOption(pidcTreeNode.getPidcVersion().getId()));
      calDataWizardDialog.open();
    }
  }


  /**
   * Action for delete the CDR review. The action invokes the CDR command in delete mode
   *
   * @param manager manager
   * @param firstElement firstElement
   */
  // Icdm-877
  public void deleteReviewAction(final IMenuManager manager, final Object firstElement) {

    final Action delRvwAction = new Action() {

      /**
       * Delete the review
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {
          final ReviewResultData cdrResult = getCdrResult(firstElement, true);
          CDRReviewResult cdrReviewResult = getCDRRvwResult(firstElement, cdrResult);

          ReviewResultDeleteValidation reviewResultDeleteValidation;
          if (cdrReviewResult != null) {
            // check whether review can be deleted
            try {
              // Service Call to get Review Result Deletion Validation
              reviewResultDeleteValidation =
                  new CDRReviewResultServiceClient().reviewResultDeleteValidation(cdrReviewResult.getId());
              if (reviewResultDeleteValidation.isDeletable()) {
                deleteRvwResult(cdrReviewResult);
              }
              else {
                // Icdm-1203 - Display error message
                displayErrorMessage(cdrReviewResult, reviewResultDeleteValidation);
              }
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
          }
        });
      }
    };
    delRvwAction.setText("Delete Review");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    delRvwAction.setImageDescriptor(imageDesc);

    manager.add(delRvwAction);
  }

  /**
   * @param cdrReviewResult
   * @param reviewResultDeleteValidation
   */
  private void displayErrorMessage(final CDRReviewResult cdrReviewResult,
      final ReviewResultDeleteValidation reviewResultDeleteValidation) {
    if ((CDRConstants.REVIEW_TYPE.getType(cdrReviewResult.getReviewType()) == CDRConstants.REVIEW_TYPE.OFFICIAL) ||
        (CDRConstants.REVIEW_TYPE.getType(cdrReviewResult.getReviewType()) == CDRConstants.REVIEW_TYPE.START)) {
      CDMLogger.getInstance().errorDialog(
          "The Review is Start/Official type and cannot be deleted.\nIf you are the review creator, please first modify the review to 'Test' review  (Open Review Result -> Goto 'Review Information' page -> Set 'Review type' to 'Test') and then try delete.",
          Activator.PLUGIN_ID);
    }
    // check is result has child reviews
    else if (reviewResultDeleteValidation.isHasChildReview()) {
      CDMLogger.getInstance().errorDialog("The Review has child Reviews and cannot be deleted", Activator.PLUGIN_ID);
    }
    // check if user has rights to delete
    else if (!reviewResultDeleteValidation.isCanUsrDelReview()) {
      CDMLogger.getInstance().errorDialog("User does not has the access to delete the review", Activator.PLUGIN_ID);
    }
    // check if there there is attachments
    else if (reviewResultDeleteValidation.isHasChildAttachment()) {
      checkIfFilesAttachedForParams(reviewResultDeleteValidation);
    }
    else if (reviewResultDeleteValidation.isHasAttchments()) {

      CDMLogger.getInstance().errorDialog(
          "Deletion failed . The files attached to the review should be deleted before deleting the review !",
          Activator.PLUGIN_ID);
    }
    else if (reviewResultDeleteValidation.isUsedInCDFXDelivery()) {
      CDMLogger.getInstance().errorDialog("Deletion failed . The Review has been used in CDFX Delivery !",
          Activator.PLUGIN_ID);
    }
    else {
      CDMLogger.getInstance().errorDialog("Delete of the Review failed", Activator.PLUGIN_ID);
    }
  }

  /**
   * @param cdrReviewResult
   * @throws ApicWebServiceException
   */
  private void deleteRvwResult(final CDRReviewResult cdrReviewResult) throws ApicWebServiceException {
    String message = "";
    Map<Long, ReviewVariantModel> reviewVarMap = new RvwVariantServiceClient().getReviewVarMap(cdrReviewResult.getId());
    if (reviewVarMap.size() > 1) {
      message = CommonUtils.concatenate(message,
          "The result is attached to following variants.Do you still want to delete the review? ", "\n");
      for (ReviewVariantModel rvwVar : reviewVarMap.values()) {
        message = CommonUtils.concatenate(message, rvwVar.getRvwVariant().getName(), "\n");
      }
    }
    // Icdm-2166 check for size less than 2- No var size 0 and with one var allow delete
    if ((reviewVarMap.size() < 2) || (!CommonUtils.isEmptyString(message) &&
        MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Delete review", message))) {
      new CDRReviewResultServiceClient().deleteReviewResult(cdrReviewResult, false);
    }
  }

  /**
   * @param firstElement
   * @param cdrResult
   * @return
   */
  private CDRReviewResult getCDRRvwResult(final Object firstElement, final ReviewResultData cdrResult) {
    CDRReviewResult cdrReviewResult = new CDRReviewResult();
    if (cdrResult == null) {
      if (firstElement instanceof PidcTreeNode) {
        cdrReviewResult = ((PidcTreeNode) firstElement).getReviewResult();
      }
    }
    else {
      cdrReviewResult = cdrResult.getCdrReviewResult();
    }
    return cdrReviewResult;
  }


  /**
   * @param reviewResultDeleteValidation CDRResult
   */
  private void checkIfFilesAttachedForParams(final ReviewResultDeleteValidation reviewResultDeleteValidation) {

    if (reviewResultDeleteValidation.gettRvwParamSet().length() > 0) {
      CDMLogger.getInstance().errorDialog(
          "Deletion failed .The following parameter(s) have files attached , and needs to be deleted before deleting the review \n" +
              reviewResultDeleteValidation.gettRvwParamSet(),
          Activator.PLUGIN_ID);

    }
  }


  /**
   * opens the review result editor
   *
   * @param manager Menu Manager
   * @param firstElement selected item
   */
  // ICDM-1998
  public void openReviewResult(final IMenuManager manager, final Object firstElement) {
    // open review result link
    final Action openRvwResLink = new Action() {

      /**
       * the action to open the review result
       */
      @Override
      public void run() {
        CDRReviewResult reviewResult = getCdrResult(firstElement);

        if ((null != reviewResult) &&
            (CDRConstants.REVIEW_STATUS.getType(reviewResult.getRvwStatus()) == REVIEW_STATUS.OPEN)) {
          CDRHandler cdrHandler = new CDRHandler();
          // Method to Open Delta/Cancelled Review Dialog
          openDeltaOrCancelledReview(reviewResult, cdrHandler, firstElement);
        }
        else {
          openRvwRestEditorBasedOnObjInstance(firstElement);
        }
      }


    };
    openRvwResLink.setText("Open Review Result");
    Image reviewResultImage = null;
    if (firstElement instanceof ReviewVariantModel) {
      reviewResultImage = CommonUiUtils.getInstance().getImageForCDRReslt(firstElement);
    }
    else if (firstElement instanceof PidcTreeNode) {
      reviewResultImage = CommonUiUtils.getInstance().getImageForCDRResult((PidcTreeNode) firstElement);
    }
    openRvwResLink.setImageDescriptor(ImageManager.getImageDescriptor(reviewResultImage));
    manager.add(openRvwResLink);
  }

  /**
   * @param firstElement Object
   */
  public void openRvwRestEditorBasedOnObjInstance(final Object firstElement) {
    this.isPidcTreeNode = false;
    if (firstElement instanceof PidcTreeNode) {
      this.isPidcTreeNode = true;
      PidcTreeNode pidcTreeNode = (PidcTreeNode) firstElement;
      openReviewResultEditor(pidcTreeNode.getReviewVarResult(), pidcTreeNode, pidcTreeNode.getReviewResult(), null);

    }
    else if (firstElement instanceof ReviewVariantModel) {
      RvwVariant rvwVar = ((ReviewVariantModel) firstElement).getRvwVariant();
      ReviewVariantModel rvwVarModel = (ReviewVariantModel) firstElement;
      CDRReviewResult cdrReviewResult = rvwVarModel.getReviewResultData().getCdrReviewResult();
      openReviewResultEditor(rvwVar.getResultId() == null ? null : rvwVar, null, cdrReviewResult, null);
    }
    else if (firstElement instanceof ReviewOutput) {
      ReviewOutput reviewOutput = (ReviewOutput) firstElement;
      openReviewResultEditor(reviewOutput.getRvwVariant(), null, reviewOutput.getCdrResult(), null);
    }
    else if (firstElement instanceof MonicaReviewOutputData) {
      MonicaReviewOutputData monicaReviewOutData = (MonicaReviewOutputData) firstElement;
      if ((null != monicaReviewOutData.getRvwVariant()) || (null != monicaReviewOutData.getCdrReviewResult())) {
        openReviewResultEditor(monicaReviewOutData.getRvwVariant(), null, monicaReviewOutData.getCdrReviewResult(),
            null);
      }
    }
  }


  /**
   * Method to Open Delta/Cancelled Review Dialog
   *
   * @param cdrReviewResult Review Result
   * @param cdrHandler data review handler
   * @param selectedObject selected Object
   */
  public void openDeltaOrCancelledReview(final CDRReviewResult cdrReviewResult, final CDRHandler cdrHandler,
      final Object selectedObject) {

    IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    Shell parent = activeWindow.getShell();
    boolean isReviewAttributeSet = false;
    Long ssdSoftwareVersionId = null;


    PidcTreeNodeHandler pidcTreeNodeHandler = null;
    DELTA_REVIEW_TYPE deltaReviewType = null;
    if (cdrReviewResult.getId() != null) {
      deltaReviewType = DELTA_REVIEW_TYPE.DELTA_REVIEW;
    }

    try {
      isReviewAttributeSet = getCDRWizardUiModel(cdrHandler, cdrReviewResult, deltaReviewType);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      isReviewAttributeSet = false;
    }
    CDRWizardUIModel cdrWizardUIModel = reassignVariant(selectedObject);
    CalDataReviewWizardDialog calDtWizardDialog =
        new CalDataReviewWizardDialog(parent, CdrActionSet.this.calDataReviewWizard);
    calDtWizardDialog.create();
    try {
      this.calDataReviewWizard.setHasVaraint(this.calDataReviewWizard.getCdrWizardUIModel().getSelectedPidcVerId(),
          false);
    }
    catch (ApicWebServiceException e1) {
      CDMLogger.getInstance().errorDialog(e1.getLocalizedMessage(), e1, Activator.PLUGIN_ID);
    }
    if (cdrWizardUIModel.getPidcA2lId() != null) {
      // SSD SW Version Id for SSDRuleSelection Page
      if (cdrWizardUIModel.getSsdSWVersionId() != null) {
        ssdSoftwareVersionId = cdrWizardUIModel.getSsdSWVersionId();
      }

      this.calDataReviewWizard.getCdrWizardUIModel().setSsdSWVersionId(ssdSoftwareVersionId);

      PidcA2LBO pidcA2LBO = new PidcA2LBO(cdrWizardUIModel.getPidcA2lId(), null);
      CdrActionSet.this.calDataReviewWizard.setPidcA2LBO(pidcA2LBO);
    }
    CdrActionSet.this.calDataReviewWizard.setPidcTreeNodeHandler(pidcTreeNodeHandler);
    CdrActionSet.this.calDataReviewWizard.getProjectSelWizPage().getProjectDataSelectionPageResolver()
        .fillUIData(CdrActionSet.this.calDataReviewWizard);

    // check and assign whether Pidc division attribute value is mapped to common param DIVISION_WITH_OBD_OPTION
    CdrActionSet.this.calDataReviewWizard.getCdrWizardUIModel().setPidcDivAppForOBDOpt(
        new CDRHandler().isDivIdAppForOBDOption(this.calDataReviewWizard.getCdrWizardUIModel().getSelectedPidcVerId()));

    if (isReviewAttributeSet) {
      calDtWizardDialog.open();
    }
  }


  /**
   * Delta review for review results reassigned to another variant should contain the later variant info while doing
   * delta review.
   *
   * @param selectedObject
   * @return
   */
  private CDRWizardUIModel reassignVariant(final Object selectedObject) {
    CDRWizardUIModel cdrWizardUIModel = CdrActionSet.this.calDataReviewWizard.getCdrWizardUIModel();
    if (selectedObject instanceof PidcTreeNode) {
      PidcTreeNode pidcTreeNode = (PidcTreeNode) selectedObject;
      PidcVariant pidcVariant = pidcTreeNode.getParentNode().getPidcVariant();
      if ((null != cdrWizardUIModel.getSelectedPidcVariantId()) &&
          !cdrWizardUIModel.getSelectedPidcVariantId().equals(pidcVariant.getId())) {
        cdrWizardUIModel.setSelectedPidcVariantId(pidcVariant.getId());
        cdrWizardUIModel.setSelectedPidcVariantName(pidcVariant.getName());
        cdrWizardUIModel.setRvwVariant(pidcTreeNode.getReviewVarResult());
      }
    }
    else if (selectedObject instanceof ReviewVariantModel) {
      ReviewVariantModel rvwVariantModel = (ReviewVariantModel) selectedObject;
      if ((null != cdrWizardUIModel.getSelectedPidcVariantId()) &&
          !cdrWizardUIModel.getSelectedPidcVariantId().equals(rvwVariantModel.getRvwVariant().getId())) {
        cdrWizardUIModel.setSelectedPidcVariantId(rvwVariantModel.getRvwVariant().getVariantId());
        cdrWizardUIModel.setSelectedPidcVariantName(rvwVariantModel.getRvwVariant().getVariantName());
        cdrWizardUIModel.setRvwVariant(rvwVariantModel.getRvwVariant());
      }
    }
    return cdrWizardUIModel;
  }


  /**
   * @param reviewResult CDR Result
   */
  protected void openDeltaOrCancelledMonicaReview(final CDRReviewResult reviewResult) {
    IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    Shell parent = activeWindow.getShell();
    CDRWizardUIModel cdrWizardUIModel = null;
    PidcA2l pidcA2l = null;
    CDRReviewResultServiceClient cdrReviewResultServiceClient = new CDRReviewResultServiceClient();
    PidcA2lServiceClient pidcA2lServiceClient = new PidcA2lServiceClient();
    try {
      cdrWizardUIModel = cdrReviewResultServiceClient.getReviewResultForDeltaReview(reviewResult.getId(),
          CommonUtils.getICDMTmpFileDirectoryPath());
      pidcA2l = pidcA2lServiceClient.getById(cdrWizardUIModel.getPidcA2lId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    MonicaReviewDialog monicaReviewDialog = new MonicaReviewDialog(pidcA2l, parent, true);
    monicaReviewDialog.setCdrWizardUIModel(cdrWizardUIModel);
    monicaReviewDialog.create();
    monicaReviewDialog.open();
  }


  /**
   * Open review result editor using review variant
   *
   * @param rvwVar review variant
   * @param pidcTreeNode when called from Pidc Tree view
   * @param reviewResult CDRReviewResult
   * @param paramName CDRResultParameter
   */
  public void openReviewResultEditor(final RvwVariant rvwVar, final PidcTreeNode pidcTreeNode,
      final CDRReviewResult reviewResult, final String paramName) {
    ReviewResultEditorInput input = new ReviewResultEditorInput();
    input.setReviewResult(reviewResult);
    if (null != pidcTreeNode) {
      input.setReviewResult(pidcTreeNode.getReviewResult());
    }
    input.setRvwVariant(rvwVar);
    // set workpackage and responsibility
    if ((null != pidcTreeNode) && (null != pidcTreeNode.getParentNode()) &&
        (null != pidcTreeNode.getParentNode().getA2lWorkpackage())) {
      input.setParentA2lWorkpackage(pidcTreeNode.getParentNode().getA2lWorkpackage());
      if (null != pidcTreeNode.getParentNode().getParentNode().getA2lResponsibility()) {
        input.setParentA2lResponsible(pidcTreeNode.getParentNode().getParentNode().getA2lResponsibility());
      }
    }

    IEditorPart findEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);
    try {
      if (null == findEditor) {
        // open for the first time
        openRvwEditorFirstTime(rvwVar, input, reviewResult, paramName);
      }
      else {
        // already opened review result
        openReviewEditor(input, paramName);
      }
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param rvwVar
   * @param input
   * @param cdrReviewResult
   * @param param
   * @throws PartInitException
   */
  private void openRvwEditorFirstTime(final RvwVariant rvwVar, final ReviewResultEditorInput input,
      final CDRReviewResult cdrReviewResult, final String paramName) {
    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Opening Review result", 100);
        monitor.worked(20);
        // to identify if the editor is already opened
        // returns Null if the editor is in closed state
        try {
          // create the editor input
          ReviewResultEditorData response = new CDRReviewResultServiceClient().getRvwResultEditorData(
              rvwVar == null ? cdrReviewResult.getId() : rvwVar.getResultId(), rvwVar == null ? null : rvwVar.getId());
          input.setReviewResult(response.getReviewResult());
          input.setRvwVariant(rvwVar);
          ReviewResultClientBO resultData = new ReviewResultClientBO(response, rvwVar);
          input.setResultData(resultData);

          // Setting Workpackage Id for ReviewResultEditorInput for Workpackage
          if (CommonUtils.isNull(input.getParentA2lResponsible()) &&
              CommonUtils.isNull(input.getParentA2lWorkpackage()) &&
              CommonUtils.isEqual("WP", input.getReviewResult().getSourceType())) {
            A2lWorkPackage parentA2lWorkpackage = null;
            input.setParentA2lWorkpackage(parentA2lWorkpackage);
            Set<RvwResultWPandRespModel> respModels = response.getA2lWpRespSet();
            respModels.forEach(rvwResp -> input.setParentA2lWorkpackage(rvwResp.getA2lWorkPackage()));
          }
        }

        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
        monitor.worked(50);
        monitor.done();
      });
    }
    catch (InvocationTargetException |

        InterruptedException exp) {
      CDMLogger.getInstance().error("Error in invoking thread to open progress bar for opening Review Result!", exp,
          Activator.PLUGIN_ID);

      Thread.currentThread().interrupt();

    }
    try {
      if (CommonUtils.isNotNull(input.getReviewResult())) {
        openReviewEditor(input, paramName);
      }
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

  }


  /**
   * @param input
   * @param param
   * @param response
   * @throws PartInitException
   */
  private void openReviewEditor(final ReviewResultEditorInput input, final String paramName) throws PartInitException {
    ReviewResultEditor reviewResultEditor;
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {

      // open the editor if it is already not opened
      IEditorPart newlyOpenedEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .openEditor(input, ReviewResultEditor.EDITOR_ID);
      reviewResultEditor = (ReviewResultEditor) newlyOpenedEditor;

      setFocusAndOutlineSelection(input, reviewResultEditor, paramName);
    }
  }


  /**
   * @param input
   * @param reviewResultEditor
   * @param editorAlreadyOpen
   * @param param
   */
  private void setFocusAndOutlineSelection(final ReviewResultEditorInput input,
      final ReviewResultEditor reviewResultEditor, final String paramName) {
    // Logic to update the outline selection if a review result editor is already opened
    // updating the a2l workpackage and a2l responsible based on tree selection
    reviewResultEditor.getEditorInput().setParentA2lResponsible(input.getParentA2lResponsible());
    reviewResultEditor.getEditorInput().setParentA2lWorkpackage(input.getParentA2lWorkpackage());
    // update rvw variant if the variant node is changed in tree view
    reviewResultEditor.getEditorInput().setRvwVariant(input.getRvwVariant());
    // For setting outline selection and filter the review result parameter
    reviewResultEditor.setReviewResultOutlineSelection(this.isPidcTreeNode);

    setSelectedParameter(reviewResultEditor, paramName);

    // set focus to the editor
    reviewResultEditor.setFocus();
    // ICDM-1254, 1249
    CommonUiUtils.forceActive(reviewResultEditor.getEditorSite().getShell());
  }

  /**
   * @param reviewResultEditor
   * @param param
   */
  private void setSelectedParameter(final ReviewResultEditor reviewResultEditor, final String paramName) {
    ReviewResultClientBO resultData = reviewResultEditor.getEditorInput().getResultData();
    // ICDM-2145
    com.bosch.caltool.icdm.model.cdr.CDRResultParameter resParam =
        getResultParameter(resultData.getResponse().getParamMap().values(), paramName);

    if (null != resParam) {
      reviewResultEditor.getEditorInput().setCdrReviewResultParam(resParam);
      reviewResultEditor.getReviewResultParamListPage().setActive(true);
    }
  }

  /**
   * @param resParams
   * @param paramName
   * @return
   */
  private CDRResultParameter getResultParameter(final Collection<CDRResultParameter> resParams,
      final String paramName) {

    CDRResultParameter resParam = null;
    if (null != paramName) {
      for (CDRResultParameter cdrParam : resParams) {
        if (CommonUtils.isEqual(cdrParam.getName(), paramName)) {
          resParam = cdrParam;
        }
      }
    }
    return resParam;
  }

  /**
   * opens the Parent Review for the given parameter
   *
   * @param menuManagr Menu Manager
   * @param firstElement selected item
   * @param resultData result data model
   */
  // iCDM-2186
  public void openParentReviewResult(final IMenuManager menuManagr, final Object firstElement,
      final ReviewResultClientBO resultData) {
    final CDRResultParameter cdrResultParameter = (CDRResultParameter) firstElement;

    final CDRResultParameter parentParam = resultData.getParentParam(cdrResultParameter);
    final Action parentRvwResAction = new Action() {

      /**
       * the action for parent review
       */
      @Override
      public void run() {
        if ((null == parentParam) || (null == parentParam.getResultId())) {
          MessageDialogUtils.getErrorMessageDialog("No Parent Review",
              "The Parent Review is not available for the given parameter : " + cdrResultParameter.getName());
        }
        else {
          try {
            RvwVariant rvwVariant = null;
            CDRReviewResult reviewResult = new CDRReviewResultServiceClient().getById(parentParam.getResultId());
            if (CommonUtils.isNotNull(reviewResult.getPrimaryVariantId())) {
              rvwVariant = new RvwVariantServiceClient().getRvwVariantByResultNVarId(reviewResult.getId(),
                  reviewResult.getPrimaryVariantId());
            }
            openReviewResultEditor(rvwVariant, null, reviewResult, null);
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }

        }
      }
    };
    menuManagr.add(new Separator());
    parentRvwResAction.setText("Open Parent Review");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RVW_RES_MAIN_16X16);
    parentRvwResAction.setImageDescriptor(imageDesc);
    menuManagr.add(parentRvwResAction);
  }

  /**
   * Copy link for CDR review results -iCDM-1249
   *
   * @param manager menu Manager
   * @param firstElement element
   */
  public void copytoClipBoard(final IMenuManager manager, final Object firstElement) {
    // Copy Link Action
    final PidcVarRvwDetails cdrResult = getResultObjectForExtLink(firstElement);
    final Object linkObj =
        cdrResult.getReviewVariant() == null ? cdrResult.getReviewResult() : cdrResult.getReviewVariant();
    final Action copyLink = new Action() {

      @Override
      public void run() {
        // ICDM-1649
        try {
          LinkCreator linkCreator = new LinkCreator(linkObj);
          linkCreator.copyToClipBoard();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy Review Result Link");
    copyLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_RES_LINK_16X16));
    // disable link for open reviews
    boolean enabled = CDRConstants.REVIEW_STATUS.OPEN != CDRConstants.REVIEW_STATUS
        .getType(cdrResult.getReviewResult().getRvwStatus());
    copyLink.setEnabled(enabled);

    manager.add(copyLink);
  }


  // ICDM-1232
  /**
   * Action to create new outlook mail with CDR Result Link
   *
   * @param manager menu manager
   * @param selectedElement CDR Result
   */
  public void sendCdrResultLinkInOutlook(final IMenuManager manager, final Object selectedElement) {
    // ICDM-1649
    final PidcVarRvwDetails cdrResult = getResultObjectForExtLink(selectedElement);

    // CDR Result in outlook Action
    final IModel linkObj =
        cdrResult.getReviewVariant() == null ? cdrResult.getReviewResult() : cdrResult.getReviewVariant();
    final Action linkAction = new SendObjectLinkAction("Send Review Result Link", linkObj);

    boolean linkEnabled = CDRConstants.REVIEW_STATUS.OPEN != CDRConstants.REVIEW_STATUS
        .getType(cdrResult.getReviewResult().getRvwStatus());
    linkAction.setEnabled(linkEnabled);

    manager.add(linkAction);
  }

  // ICDM-1703
  /**
   * @param cdrData Cdr Report Data Handler
   */
  public void exportCdrReportToExcel(final CdrReportDataHandler cdrData) {
    String fileName = getExcelFileName(cdrData);
    // open the file selection dialog
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save Excel Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXTNS);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    fileDialog.setOverwrite(true);
    final String fileSelected = fileDialog.open();

    if (fileSelected != null) {
      final String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
      // job to export the cdr report
      final Job job = new CdrReportExcelExportJob(cdrData, fileSelected, fileExtn, true);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      job.schedule();

    }


  }

  /**
   * @param cdrData CdrReportDataHandler
   * @return file name
   */
  public String getExcelFileName(final CdrReportDataHandler cdrData) {
    String varName = "";
    if (cdrData.getPidcVariant() != null) {
      varName = CommonUtils.concatenate("_", cdrData.getPidcVariant().getName());
    }
    // create the file name
    String fileName = CommonUtils.concatenate("CDR_Report_", cdrData.getPidcVers().getName(), varName,
        "_" + cdrData.getPidcA2l().getSdomPverName(), "_", cdrData.getPidcA2l().getSdomPverVarName(), "_",
        ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_10, cdrData.getReportGenDate()));
    fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
    // If 'Generate Data Review Report' call from Variant/Resp/WP, then append '_filter' at the end of file name
    return cdrData.getCdrReport().isToGenDataRvwRprtForWPResp() ? fileName.concat(SUFFIX_FOR_DATARVW_RPRT_FROM_RESP_WP)
        : fileName;
  }

  /**
   * This method creates Export dialog for Review Rules
   *
   * @param selectedObject selected pid card
   * @param paramDataProvider ParameterDataProvider
   * @param paramColDataProvider ParamCollectionDataProvider
   */
  // ICDM-1539, Excel Export for Review Rule
  public void excelExport(final ParamCollection selectedObject, final ParameterDataProvider<?, ?> paramDataProvider,
      final ParamCollectionDataProvider paramColDataProvider) {
    final ReviewRuleExcelExportDialog reviewRuleDialog = new ReviewRuleExcelExportDialog(
        Display.getDefault().getActiveShell(), selectedObject, paramDataProvider, paramColDataProvider);
    reviewRuleDialog.open();
  }

  /**
   * Open questionnaire editor
   *
   * @param version Questionnaire Version
   */
  public void openQuestionnaireEditor(final QuestionnaireVersion version) {
    try {
      final ReviewQuestionaireEditorInput quesInput = new ReviewQuestionaireEditorInput(version);
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(quesInput,
            ReviewQuestionaireEditor.EDITOR_ID);
      }
    }
    catch (PartInitException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }

  }

  /**
   * This method opens questionnaire response editor from PIDC Tree View Part
   *
   * @param pidcTreeNode questionnaire response node
   */
  // iCDM-1982
  public void openQuestionnaireResponseEditor(final PidcTreeNode pidcTreeNode) {
    // Check linked qnaire resp is already available with same qnaire resp name
    validateLinkedQnaireResponse(pidcTreeNode);

    openQuestionnaireResponseEditor(pidcTreeNode.getQnaireRespId(), pidcTreeNode.getPidcVariant());
  }

  /**
   * @param quesRespId questionnaire response id
   */
  public void openQuesRespEditorWithId(final Long quesRespId) {
    CDMLogger.getInstance().debug("Opening Questionnaire Response editor : {}", quesRespId);
    try {
      QnaireRespEditorInput qNaireResponseEditorInput = new QnaireRespEditorInput(quesRespId, null);
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(qNaireResponseEditorInput, QnaireResponseEditor.EDITOR_ID);
        if (openEditor instanceof QnaireResponseEditor) {
          openEditor.setFocus();
          CDMLogger.getInstance().debug("Questionnaire Response opened in the editor");
        }
      }
    }
    catch (PartInitException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method opens questionnaire response editor from external link
   *
   * @param qNaireRespId - qnaire resp id
   * @param variant linked variant
   */
  public void openQuestionnaireResponseEditor(final Long qNaireRespId, final PidcVariant variant) {
    CDMLogger.getInstance().debug("Opening Questionnaire Response editor : {}", qNaireRespId);
    try {

      IEditorReference[] openEditorReferences =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();

      QnaireRespEditorInput editorInput =
          getEditorInputIfEditorOpenedAlready(openEditorReferences, qNaireRespId, variant);

      if (editorInput == null) {
        editorInput = new QnaireRespEditorInput(qNaireRespId, variant);
      }
      IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .openEditor(editorInput, QnaireResponseEditor.EDITOR_ID);
      CDMLogger.getInstance().debug("Questionnaire Response opened in the editor");
      editorPart.setFocus();
    }
    catch (PartInitException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }
  }

  private QnaireRespEditorInput getEditorInputIfEditorOpenedAlready(final IEditorReference[] openEditorReferences,
      final Long qNaireRespId, final PidcVariant variant)
      throws PartInitException {
    if (CommonUtils.isNotEmpty(openEditorReferences)) {
      for (IEditorReference iEditorReference : openEditorReferences) {
        if (iEditorReference.getEditorInput() instanceof QnaireRespEditorInput) {
          QnaireRespEditorInput editorInput = (QnaireRespEditorInput) iEditorReference.getEditorInput();
          if (CommonUtils.isEqual(editorInput.getEditorInputSelection().getRvwQnaireResponse().getId(), qNaireRespId) &&
              CommonUtils.isEqual(
                  editorInput.getEditorInputSelection().getLinkedVariantId(editorInput.getEditorInputSelection()),
                  variant.getId())) {
            return editorInput;
          }
        }
      }
    }
    return null;
  }

  /**
   * @param pidcTreeNode selected pidc tree node
   */
  private void validateLinkedQnaireResponse(final PidcTreeNode pidcTreeNode) {
    if (CommonUtils.isNotNull(pidcTreeNode.getQnaireResp())) {
      RvwQnaireResponse qnaireResponse = pidcTreeNode.getQnaireResp();
      if (isLinkedQnaireRespAvailable(pidcTreeNode, qnaireResponse)) {
        StringBuilder warnMsg = new StringBuilder();
        warnMsg.append("Linked qnaire response is available for the selected questionnaire: \n");
        warnMsg.append(qnaireResponse.getName());
        warnMsg.append(" - ");
        warnMsg.append(qnaireResponse.getPrimaryVarRespWpName());
        CDMLogger.getInstance().warnDialog(warnMsg.toString(), Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * @param pidcTreeNode selected pidc tree node
   * @param qnaireResponse qnaire response
   * @return whether qnaire resp is already linked
   */
  private boolean isLinkedQnaireRespAvailable(final PidcTreeNode pidcTreeNode, final RvwQnaireResponse qnaireResponse) {
    return (isPrimaryVarIdNotEqual(pidcTreeNode, qnaireResponse) ||
        isPrimaryRespIdNotEqual(pidcTreeNode, qnaireResponse) || isPrimaryWpIdNotEqual(pidcTreeNode, qnaireResponse));
  }

  /**
   * @param pidcTreeNode
   * @param qnaireResponse
   * @return
   */
  private boolean isPrimaryVarIdNotEqual(final PidcTreeNode pidcTreeNode, final RvwQnaireResponse qnaireResponse) {
    return CommonUtils.isNotNull(qnaireResponse.getVariantId()) &&
        CommonUtils.isNotNull(pidcTreeNode.getPidcVariant()) &&
        !qnaireResponse.getVariantId().equals(pidcTreeNode.getPidcVariant().getId());
  }

  private boolean isPrimaryRespIdNotEqual(final PidcTreeNode pidcTreeNode, final RvwQnaireResponse qnaireResponse) {
    return !qnaireResponse.getA2lRespId().equals(pidcTreeNode.getParentNode().getA2lResponsibility().getId());
  }

  private boolean isPrimaryWpIdNotEqual(final PidcTreeNode pidcTreeNode, final RvwQnaireResponse qnaireResponse) {
    return !qnaireResponse.getA2lWpId().equals(pidcTreeNode.getParentNode().getA2lWorkpackage().getId());
  }

  /**
   * Export selected review results to excel files
   *
   * @param manager Menu Manager
   * @param selection selected item
   */
  public void setResultExportAction(final IMenuManager manager, final IStructuredSelection selection) {
    final Action exportAction = new Action() {

      /**
       * Save result to an excel document
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        exportSelectedReviews(selection);

      }

    };
    exportAction.setText("Excel Report");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16);
    exportAction.setImageDescriptor(imageDesc);
    manager.add(exportAction);

  }


  /**
   * Compliance review of hex fiels
   *
   * @param manager Menu Manager
   * @param pidcTreeNode selected item
   */
  public void setCompliReviewAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    final Action compliAction = new Action() {

      /**
       * Compliance review of hex file
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        CompliReviewDialog calDataRvwWizardDialog = new CompliReviewDialog(Display.getCurrent().getActiveShell());
        calDataRvwWizardDialog.create();
        calDataRvwWizardDialog.setValues(pidcTreeNode);
        calDataRvwWizardDialog.open();

      }

    };
    compliAction.setText("Generate SSD Compliance Report of Hex File(s)");

    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      compliAction.setEnabled(false);
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPLI_HEX_16X16);
    compliAction.setImageDescriptor(imageDesc);
    manager.add(compliAction);

  }

  /**
   * Export selected review results to excel files
   *
   * @param selection selection
   */
  public void exportSelectedReviews(final IStructuredSelection selection) {

    Set<CDRReviewResult> revResults = new HashSet<>();
    if (selection.size() > 0) {
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
        CDRReviewResult cdrResult = getCdrResult(iterator.next());
        if ((cdrResult != null) &&
            (CDRConstants.REVIEW_STATUS.OPEN != CDRConstants.REVIEW_STATUS.getType(cdrResult.getRvwStatus()))) {
          revResults.add(cdrResult);
        }
      }
      ReviewResultExportDialog exportDiallog =
          new ReviewResultExportDialog(Display.getDefault().getActiveShell(), revResults);
      exportDiallog.open();
    }
  }


  /**
   * @param manager menu manager
   * @param pidcA2l A2L file
   */
  public void addCdrReportGenerationAction(final IMenuManager manager, final PidcA2l pidcA2l) {
    // CDR Report action
    manager.add(new CdrReportGenerationAction(pidcA2l, null, null, null));
  }

  /**
   * @param manager Menu Manager
   * @param pidcA2l Pidc A2l
   */
  public void compareHEXWithLatestRvwData(final IMenuManager manager, final PidcA2l pidcA2l) {
    manager.add(new CompareHexWithRvwDataAction(pidcA2l));
  }

  /**
   * Actionset to start data assessment
   *
   * @param manager Menu Manager
   * @param pidcTreeNode PIDC data
   */
  public void dataAssessmentReportAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    manager.add(new DataAssessmentReportAction(pidcTreeNode));
  }

  /**
   * Actionset to view data assessment Baselines
   *
   * @param manager Menu Manager
   * @param pidcTreeNode PIDC data
   */
  public void dataAssessmentBaselineAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    manager.add(new DataAssessmentBaselineAction(pidcTreeNode));
  }

  /**
   * @param manager manager
   * @param pidcTreeNode pidcTreeNode
   */
  public void openWPArchiveListAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    WPArchivalListEditorOpenAction openAction = new WPArchivalListEditorOpenAction(pidcTreeNode);
    manager.add(openAction);
  }

  /**
   * @param manager Menu Manager
   * @param pidcA2l Pidc A2l
   */
  public void startMonicaReview(final IMenuManager manager, final PidcA2l pidcA2l) {
    manager.add(new MonicaReviewAction(pidcA2l));
  }

  /**
   * Trigger mail in Background
   *
   * @param userList List of user to whom mail has to be send
   * @param reviewOutput reviewOutput model
   */
  public void notifyRvwParticipants(final Set<User> userList, final ReviewOutput reviewOutput) {

    final Thread job = new Thread("NotifyRvwParticipants") {

      @Override
      public void run() {
        triggerRvwInfoMailToUsers(userList, reviewOutput);
      }
    };
    job.start();
  }


  /**
   * @param userList List of users to whom review completion mail has to be sent
   * @param reviewOutput reviewOutput model
   */
  public void triggerRvwInfoMailToUsers(final Set<User> userList, final ReviewOutput reviewOutput) {

    Set<String> userEmailList = new HashSet<>();
    try (LdapAuthenticationWrapperCloseable ldap = new LdapAuthenticationWrapperCloseable()) {
      for (User user : userList) {
        userEmailList.add(ldap.getUserDetails(user.getName()).getEmailAddress());
      }
      // Trigger mail to the receipients
      triggerMailNotification(userEmailList, ldap.getUserDetails(reviewOutput.getRvwCreatedUser()), reviewOutput);
    }
    catch (LdapException ex) {
      CDMLogger.getInstance().errorDialog(ex.getLocalizedMessage(), ex, Activator.PLUGIN_ID);
    }
  }


  /**
   * Triggers mail notification to rvw participants mail address
   *
   * @param reviewOutput
   * @param toUserMailAddrSet
   * @param ccUserInfo
   */
  private void triggerMailNotification(final Set<String> toUserMailAddrSet, final UserInfo ccUserInfo,
      final ReviewOutput reviewOutput) {

    // Create the mail sender with from and to addresses
    CommonDataBO commonBO = new CommonDataBO();
    MailHotline mailHotline;

    try {
      boolean mailEnabled = CommonUtils.isEqual(ApicConstants.CODE_YES,
          commonBO.getParameterValue(CommonParamKey.MAIL_NOTIFICATION_ENABLED));
      String fromEmail = commonBO.getParameterValue(CommonParamKey.ICDM_HOTLINE_TO);
      mailHotline = new MailHotline(fromEmail, toUserMailAddrSet, mailEnabled);

      StringBuilder mailSubject = new StringBuilder("#Review Completed - ");
      mailSubject.append(reviewOutput.getPidcVersion().getName());
      if (CommonUtils.isNotEmptyString(reviewOutput.getWorkPackageGroupName())) {
        mailSubject.append(" - ").append(reviewOutput.getWorkPackageGroupName());
      }
      mailHotline.setSubject(mailSubject.toString());
      mailHotline.setContent(getMailContent(reviewOutput, ccUserInfo));
      mailHotline.setCcAddr(ccUserInfo.getEmailAddress());

      mailHotline.sendMail();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error("Mail to Review Participants got failed!", e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param mailHotline
   * @param reviewOutput
   * @param ccUserInfo
   * @return
   */
  private String getMailContent(final ReviewOutput reviewOutput, final UserInfo ccUserInfo) {

    StringBuilder builder = new StringBuilder();
    builder.append("<html>");
    builder.append(
        "<style>table, th, td { border:1px solid black; border-collapse: collapse; padding:5px 10px 5px;}</style>");
    builder.append("<p>");
    builder.append("Hello Together,");
    builder.append(MailHotline.TWO_LINE_BREAKS);
    builder.append("Calibration Data Review is completed successfully in iCDM. Please find below the review details. ");
    builder.append(MailHotline.TWO_LINE_BREAKS);
    builder.append("<table>");
    builder.append("<tr><th> Created on </th>");
    builder.append("<th> PIDC Version </th>");
    builder.append("<th> Variant </th>");
    builder.append("<th> A2L File </th>");
    builder.append("<th> Created User </th>");
    builder.append("<th> Calibration Engineer </th>");
    String auditorName = reviewOutput.getAuditorName();
    if (CommonUtils.isNotEmptyString(auditorName)) {
      builder.append("<th> Auditor </th>");
    }
    builder.append("<th> Additional Participants </th>");
    builder.append("<th> Number of functions reviewed </th>");
    builder.append("<th> Number of parameters reviewed </th></tr>");
    builder.append("<tr><td>" + new Date() + CONS_TBL_DEFN_CLOSE_TAG);

    PidcVersion pidcVersion = reviewOutput.getPidcVersion();
    builder.append(CONS_TBL_DEFN_OPEN_TAG + new LinkCreator(pidcVersion).getHtmlText() + CONS_TBL_DEFN_CLOSE_TAG);
    String pidcVariantName = reviewOutput.getPidcVariantName();
    String varNameColmValue = CommonUtils.isNull(pidcVariantName) ? CDRConstants.NO_VARIANT : pidcVariantName;
    builder.append(CONS_TBL_DEFN_OPEN_TAG + varNameColmValue + CONS_TBL_DEFN_CLOSE_TAG);
    builder.append(CONS_TBL_DEFN_OPEN_TAG + reviewOutput.getA2lFileName() + CONS_TBL_DEFN_CLOSE_TAG);
    builder.append(CONS_TBL_DEFN_OPEN_TAG + ccUserInfo.getSurName() + ", " + ccUserInfo.getGivenName() + " (" +
        ccUserInfo.getDepartment() + ") " + CONS_TBL_DEFN_CLOSE_TAG);
    builder.append(CONS_TBL_DEFN_OPEN_TAG + reviewOutput.getCalEngineerName() + CONS_TBL_DEFN_CLOSE_TAG);
    if (CommonUtils.isNotEmptyString(auditorName)) {
      builder.append(CONS_TBL_DEFN_OPEN_TAG + auditorName + CONS_TBL_DEFN_CLOSE_TAG);
    }

    // Additional Rvw Participants
    List<RvwParticipant> addPartcpntsList = reviewOutput.getRvwParticipantsList().stream()
        .filter(rvwparcpnts -> CommonUtils.isEqual(CDRConstants.REVIEW_USER_TYPE.getType(rvwparcpnts.getActivityType()),
            CDRConstants.REVIEW_USER_TYPE.ADDL_PARTICIPANT))
        .collect(Collectors.toList());
    builder.append("<td><ul>");
    for (RvwParticipant participant : addPartcpntsList) {
      builder.append("<li>" + participant.getName() + "</li>");
    }
    builder.append("</ul></td>");
    builder.append(CONS_TBL_DEFN_OPEN_TAG + reviewOutput.getNoOfReviewedFunctions() + CONS_TBL_DEFN_CLOSE_TAG);
    builder.append(CONS_TBL_DEFN_OPEN_TAG + reviewOutput.getNoOfReviewedParam() + "</td></tr></table>");

    builder.append(MailHotline.TWO_LINE_BREAKS);
    builder.append("Thank You!");
    builder.append(MailHotline.TWO_LINE_BREAKS);
    builder.append("This is an auto generated mail from iCDM client");
    builder.append("</p>");
    builder.append("</html>");

    return builder.toString();
  }

}
