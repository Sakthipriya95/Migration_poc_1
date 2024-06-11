/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.FunctionSelectionDialog;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditorInput;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.actions.IPIDCTreeViewActionSet;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.ruleseditor.actions.OpenRulesEditorAction;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This class contains actions of PIDC Tree view related to calibration data review.
 *
 * @author bru2cob
 */
// ICDM-523
public class PIDCTreeViewActionSet implements IPIDCTreeViewActionSet {

  /**
   * Single function to open.
   */
  private static final int SINGLE_FUNCTION = 1;

  /**
   * constructor
   */
  public PIDCTreeViewActionSet() {
    // Mandatory empty constructor
  }


  /**
   * This method opens the result editor when the cdr result is double clicked on the pidc tree {@inheritDoc}
   */
  @Override
  public IDoubleClickListener getDoubleClickListener() {
    return event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selectedNode = selection.getFirstElement();

      if (selectedNode instanceof PidcTreeNode) {
        PidcTreeNode pidcTreeNode = (PidcTreeNode) selectedNode;
        switch (pidcTreeNode.getNodeType()) {
          case REV_RES_NODE:
            openReviewResultEditor(pidcTreeNode);
            break;
          case QNAIRE_RESP_NODE:
          case PIDC_A2L_QNAIRE_RESP_NODE:
            new CdrActionSet().openQuestionnaireResponseEditor(pidcTreeNode);
            break;
          case RVW_RES_TITLE_NODE_NEW:
          case RVW_WORKPACAKGES_TITLE_NODE:
          case RVW_RESPONSIBILITIES_TITLE_NODE:
          case RVW_RESPONSIBILITY_NODE:
          case RVW_OTHER_RVW_SCOPES_TITLE_NODE:
          case RVW_OTHER_RVW_SCOPES_NODE:
          case RVW_RESP_WP_NODE:
          case REV_RES_WP_GRP_NODE:
          case CDR_VAR_NODE:
            openReviewResultListEditor(pidcTreeNode);
            break;
          default:
            break;
        }
      }
    };
  }


  /**
   * @param cdrActionSet
   * @param pidcTreeNode
   */
  private void openReviewResultEditor(final PidcTreeNode pidcTreeNode) {
    CdrActionSet cdrActionSet = new CdrActionSet();
    if (CommonUtils.isEqual(
        CDRConstants.REVIEW_STATUS.getType(pidcTreeNode.getReviewResult().getRvwStatus()).toString(),
        REVIEW_STATUS.OPEN.toString())) {
      CDRHandler cdrHandler = new CDRHandler();
      // Method to Open Delta/Cancelled Review Dialog
      cdrActionSet.openDeltaOrCancelledReview(pidcTreeNode.getReviewResult(), cdrHandler, pidcTreeNode);
    }
    else {
      cdrActionSet.openRvwRestEditorBasedOnObjInstance(pidcTreeNode);
    }
  }


  /**
   * @param input
   */
  // ICDM-1998
  private void openReviewResultListEditor(final PidcTreeNode pidcTreeNode) {
    CDMLogger.getInstance().debug("Opening the review results of the PIDC version/variant...");

    ReviewListEditorInput input = new ReviewListEditorInput(pidcTreeNode);

    IEditorPart openEditor;
    try {
      openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
          ReviewListEditor.EDITOR_ID);
      if (openEditor instanceof CdrReportEditor) {
        ReviewListEditor cdrReportEditor = (ReviewListEditor) openEditor;
        // set focus to the editor opened
        cdrReportEditor.setFocus();
        CDMLogger.getInstance().debug("PIDC Version/Variant Review Results opened in editor");
      }
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param manager
   * @param viewer
   * @param cdrActionSet
   * @param selection
   * @param editorInput
   */
  // ICDM-2081 : changed the modifier from private to public
  public void setConMenuForResult(final IMenuManager manager, final TreeViewer viewer,
      final IStructuredSelection selection) {

    final CdrActionSet cdrActionSet = new CdrActionSet();
    if (selection.size() == 1) {
      // icdm-1249
      ReviewResultData cdrResult = null;
      if (selection.getFirstElement() instanceof ReviewResultData) {
        cdrResult = (ReviewResultData) selection.getFirstElement();
        cdrResult.setLinkedVar(cdrResult.getPidcVariant());// first variant
      }
      else if (selection.getFirstElement() instanceof ReviewVariantModel) {
        ReviewVariantModel rvwVar = (ReviewVariantModel) selection.getFirstElement();
        cdrResult = rvwVar.getReviewResultData();
        // set the linked variant
        cdrResult.setLinkedVar(rvwVar.getReviewResultData().getPidcVariant());
      }

      // ICDM-1998
      cdrActionSet.openReviewResult(manager, selection.getFirstElement());
      manager.add(new Separator());

      cdrActionSet.copytoClipBoard(manager, selection.getFirstElement());
      // ICDM-1232
      cdrActionSet.sendCdrResultLinkInOutlook(manager, selection.getFirstElement());

      manager.add(new Separator());
      // cdr result export action

      cdrActionSet.setResultExportAction(manager, selection.getFirstElement());
      manager.add(new Separator());


      // start delta review action
      cdrActionSet.setDeltaReviewAction(manager, selection.getFirstElement());
      manager.add(new Separator());

      new CopyResToVarAction(manager, selection.getFirstElement(), viewer);
      new DetachRvwVarAction(manager, selection.getFirstElement(), viewer);
      manager.add(new Separator());
      // Icdm-877 Context Menu for the Delete of the Review.
      cdrActionSet.deleteReviewAction(manager, selection.getFirstElement());

    }
    else {
      cdrActionSet.setResultExportAction(manager, selection);

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void getKeyListenerToViewer(final Object selectedObject, final int keyCode, final int stateMask,
      final TreeViewer viewer) {
    // Not Applicable

  }


  /**
   * Opens review result
   */
  // iCDM-713
  @Override
  public void openReviewResult(final CDRReviewResult cdrResult, final String paramName, final Long variantId) {
    RvwVariant rvwVariant = null;
    if (CommonUtils.isNotNull(variantId)) {
      try {
        rvwVariant = new RvwVariantServiceClient().getRvwVariantByResultNVarId(cdrResult.getId(), variantId);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
      }
    }
    new CdrActionSet().openReviewResultEditor(rvwVariant, null, cdrResult, paramName);
  }


  /**
   * Opens review result using review variant
   *
   * @param rvwVar RvwVariant
   * @param paramName param Name
   */


  @Override
  public void openReviewResult(final RvwVariant rvwVar, final String paramName) {
    new CdrActionSet().openReviewResultEditor(rvwVar, null, null, paramName);
  }


  /**
   * Icdm-1379- new method for Showing the Rules Editor New method for opening Function selection dialog {@inheritDoc}
   */
  @Override
  public void openFunctionSelectionDialog(final List<Function> funcList, final String paramName) {
    ReviewActionSet paramActionSet = new ReviewActionSet();

    if (funcList.size() == SINGLE_FUNCTION) {
      Parameter parameter;
      try {
        parameter = new ParameterServiceClient().getParameterOnlyByName(paramName);
        paramActionSet.openRulesEditor(new ReviewParamEditorInput<>(funcList.get(0)), parameter);

      }
      catch (ApicWebServiceException | PartInitException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      openRulesEditor(funcList.get(0), paramName);
      return;
    }
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    FunctionSelectionDialog dialog = new FunctionSelectionDialog(shell, funcList, paramName);
    dialog.open();

  }

  /**
   * open the rules editor and select the function
   *
   * @param function
   * @param paramName
   */
  private void openRulesEditor(final Function function, final String paramName) {

    try {
      ReviewParamEditorInput<?, ?> input = new ReviewParamEditorInput<>(function);
      String baseParamName;
      Parameter cdrFuncParam = null;
      if ((paramName != null) && ApicUtil.isVariantCoded(paramName)) {
        baseParamName = ApicUtil.getBaseParamName(paramName);
        cdrFuncParam = new com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient()
            .getParameterOnlyByName(baseParamName);
        input.setCdrFuncParam(cdrFuncParam);
      }
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        new ReviewActionSet().openRulesEditor(input, null);
      }

    }
    catch (PartInitException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createRuleAction(final IMenuManager menuManagr, final Object selectedObj) {
    final Action openEditorAction = new OpenRulesEditorAction(null, selectedObj, false, null, true, null);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16);
    openEditorAction.setImageDescriptor(imageDesc);
    openEditorAction.setText("Create Rule using Check Value");
    menuManagr.add(openEditorAction);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRightClickOfPidcTreeNode(final IMenuManager manager, final TreeViewer viewer,
      final PidcTreeNode pidcTreeNode) {

    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
    if (selection.size() == 1) {

      switch (pidcTreeNode.getNodeType()) {
        case ACTIVE_PIDC_VERSION:
          openWPArchiveListAction(manager, pidcTreeNode);
          addOpenReviewListAction(manager, pidcTreeNode);
          new CdrActionSet().setProjDataDeltaReviewActionPIDCLevel(manager, pidcTreeNode);
          break;

        case RVW_WORKPACAKGES_TITLE_NODE:
        case RVW_RESPONSIBILITIES_TITLE_NODE:
        case RVW_RESPONSIBILITY_NODE:
        case RVW_RESP_WP_NODE:
        case REV_RES_WP_GRP_NODE:
          addOpenReviewListAction(manager, pidcTreeNode);
          openWPArchiveListAction(manager, pidcTreeNode);
          break;

        case RVW_RES_TITLE_NODE_NEW:
        case OTHER_PIDC_VERSION:
        case RVW_OTHER_RVW_SCOPES_TITLE_NODE:
        case RVW_OTHER_RVW_SCOPES_NODE:
          addOpenReviewListAction(manager, pidcTreeNode);
          break;


        case CDR_VAR_NODE:
          addOpenReviewListAction(manager, pidcTreeNode);
          // creating Start Delta Review context menu
          if (!CDRConstants.NO_VARIANT.equalsIgnoreCase(pidcTreeNode.getName())) {
            new CdrActionSet().setProjDataDeltaReviewAction(manager, pidcTreeNode);
          }
          break;

        case REV_RES_NODE:
          setConMenuForResult(manager, viewer, selection);
          break;
        // for A2l Structure Nodes
        case PIDC_A2L_VAR_NODE:
        case PIDC_A2L_RESPONSIBILITY_NODE:
        case PIDC_A2L_WP_NODE:
          addOpenReviewListAction(manager, pidcTreeNode);
          manager.add(new Separator());
          openWPArchiveListAction(manager, pidcTreeNode);
          break;
        default:
          break;
      }
    }
  }

  /**
   * @param manager
   * @param pidcTreeNode
   */
  private void openWPArchiveListAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    WPArchivalListEditorOpenAction openAction = new WPArchivalListEditorOpenAction(pidcTreeNode);
    manager.add(openAction);
  }


  /**
   * @param manager Menu Manager
   * @param pidcTreeNode Pidc Tree Node
   */
  private void addOpenReviewListAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    ReviewListEditorOpenAction openAction = new ReviewListEditorOpenAction(pidcTreeNode);
    manager.add(openAction);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openA2LFile(final Long pidcA2lId) {
    // Not Applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRightClickOfAPRJnew(final Set<PidcVersion> pidCards) {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void openComparePidcEditor(final IMenuManager manager, final List<IProjectObject> compareList) {
    // Not Applicable

  }
}
