/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.cdr.ui.actions.CdrReportGenerationAction;
import com.bosch.caltool.cdr.ui.actions.DataAssessmentReportAction;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.IPIDCTreeViewActionSet;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.TreeViewSelectnRespWP;
import com.bosch.caltool.icdm.ui.jobs.A2LFileLoaderJob;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */
public class PIDCTreeViewActionSet implements IPIDCTreeViewActionSet {

  private PidcTreeNode currentSelection;

  /**
   * {@inheritDoc}
   */
  @Override
  public IDoubleClickListener getDoubleClickListener() {

    return event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selectedNode = selection.getFirstElement();
      if ((selectedNode instanceof PidcTreeNode) &&
          ((PidcTreeNode) selectedNode).getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
        PIDCTreeViewActionSet.this.currentSelection = (PidcTreeNode) selectedNode;
        openA2LFile(PIDCTreeViewActionSet.this.currentSelection.getPidcA2l().getId());
      }
    };
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
   * {@inheritDoc}
   */
  @Override
  public void openFunctionSelectionDialog(final List<Function> funcList, final String paramName) {
    //

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createRuleAction(final IMenuManager menuManagr, final Object selectedObj) {
    // Not Applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRightClickOfPidcTreeNode(final IMenuManager manager, final TreeViewer viewer,
      final PidcTreeNode pidcTreeNode) {
    // ICDM-773
    final A2LActionSet actionset = new A2LActionSet();
    // Get the current selection and add actions to it
    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      // Define all actions
      if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        actionset.uploadA2lAction(manager, pidcTreeNode);
      }
      else if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
        // start review process action
        reviewProcessAction(manager, pidcTreeNode, actionset, selection);
      }
      else if (isNodeUnderRvwQnaireTree(pidcTreeNode)) {
        // Add Qnaire Actions for Nodes under rvw Qnaire
        pidcTreeNode.setSizeOfSelectedNodes(viewer.getStructuredSelection().toArray().length);
        addQnaireActionInContextMenu(manager, pidcTreeNode);
      }
      else if (isNodeUnderA2LStruct(pidcTreeNode)) {
        // Add context menu for Node under PIDC a2l Structures
        pidcTreeNode.setSizeOfSelectedNodes(viewer.getStructuredSelection().toArray().length);
        addQnaireActionInContextMenu(manager, pidcTreeNode);

        if (!(pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE))) {
          addContextMenuForPidcA2lStructure(manager, pidcTreeNode);
        }
      }
    }
  }

  /**
   * @param manager
   * @param pidcTreeNode
   */
  private void addQnaireActionInContextMenu(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    QuestionnaireAction qnaireAction = new QuestionnaireAction();
    manager.add(new Separator());
    qnaireAction.setAddEditQnaire(manager, pidcTreeNode);

    PIDC_TREE_NODE_TYPE nodeType = pidcTreeNode.getNodeType();
    if ((nodeType.equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE)) ||
        nodeType.equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE)) {

      addQnaireRespNodeActions(manager, pidcTreeNode);
    }

    if (CommonUtils.isEqual(nodeType, PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) ||
        CommonUtils.isEqual(nodeType, PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE)) {
      // Copy/Paste actions
      manager.add(new Separator());
      qnaireAction.pasteQnaireRespAction(manager, pidcTreeNode);
    }

    addContextMenuForRespNode(manager, pidcTreeNode, qnaireAction);
  }

  /**
   * @param pidcTreeNode
   * @return
   */
  private boolean isNodeUnderRvwQnaireTree(final PidcTreeNode pidcTreeNode) {
    return pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE);
  }

  /**
   * @param pidcTreeNode
   * @return
   */
  private boolean isNodeUnderA2LStruct(final PidcTreeNode pidcTreeNode) {
    return pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE);
  }

  /**
   * @param pidcTreeNode
   * @param manager
   */
  private void lockWPContextMenu(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    final Action lockWPAction = new Action() {

      @Override
      public void run() {

        TreeViewSelectnRespWP selWpResp = new TreeViewSelectnRespWP();

        Long pidcVariantID = pidcTreeNode.getPidcVariant().getId();
        selWpResp.setVariantID(pidcVariantID);

        selWpResp.setPidcA2lID(pidcTreeNode.getPidcA2l().getId());

        // Map of responsibility id,workpackage id and A2lWPRespModel
        Map<Long, Map<Long, A2lWPRespModel>> respWpA2lWpRespModelMap = new HashMap<>();
        selWpResp.setRespWpA2lWpRespModelMap(respWpA2lWpRespModelMap);

        Map<Long, A2lWPRespModel> wpA2lWpRespModelMap = new HashMap<>();
        respWpA2lWpRespModelMap.put(pidcTreeNode.getA2lResponsibility().getId(), wpA2lWpRespModelMap);

        if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE) &&
            CommonUtils.isNotNull(pidcTreeNode.getA2lStructureModel())) {

          populateWpRespModelForRespNode(pidcTreeNode, pidcTreeNode.getA2lStructureModel().getRespWPA2lWpRespModelMap(),
              wpA2lWpRespModelMap);


        }
        else if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) &&
            CommonUtils.isNotNull(pidcTreeNode.getA2lStructureModel())) {

          populateWpRespModelForWPNode(pidcTreeNode, pidcTreeNode.getA2lStructureModel().getRespWPA2lWpRespModelMap(),
              wpA2lWpRespModelMap);

        }

        new CDRReviewResultServiceClient().updateSelWorkpackageStatus(selWpResp);
      }
    };

    lockWPAction.setText("Mark Workpackage Responsibility as Finished");
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.ALL_16X16);
    lockWPAction.setImageDescriptor(imageDesc);
    lockWPAction.setEnabled(
        new QuestionnaireAction().qnaireAccessValidation(pidcTreeNode) && !pidcTreeNode.getPidcVariant().isDeleted());
    manager.add(lockWPAction);
  }

  private void populateWpRespModelForWPNode(final PidcTreeNode selPidcTreeNode,
      final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWPA2lWpRespModelMap,
      final Map<Long, A2lWPRespModel> wpA2lWpRespModelMap) {
    Long pidcVariantID = selPidcTreeNode.getPidcVariant().getId();
    Long wpID = selPidcTreeNode.getA2lWorkpackage().getId();
    Long respID = selPidcTreeNode.getA2lResponsibility().getId();
    if (isWPAvailable(respWPA2lWpRespModelMap, pidcVariantID, respID, wpID)) {
      wpA2lWpRespModelMap.put(wpID,
          selPidcTreeNode.getA2lStructureModel().getRespWPA2lWpRespModelMap().get(pidcVariantID).get(respID).get(wpID));
    }
    else {
      wpA2lWpRespModelMap.put(wpID, createDummyA2lWpRespModel(wpID, respID));
    }
  }

  private void populateWpRespModelForRespNode(final PidcTreeNode selpidcTreeNode,
      final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWPA2lWpRespModelMap,
      final Map<Long, A2lWPRespModel> wpA2lWpRespModelMap) {
    Long pidcVariantID = selpidcTreeNode.getPidcVariant().getId();
    Long respID = selpidcTreeNode.getA2lResponsibility().getId();
    if (isRespAvailable(respWPA2lWpRespModelMap, pidcVariantID, respID)) {
      Map<Long, A2lWPRespModel> wpRespModelMap =
          selpidcTreeNode.getA2lStructureModel().getRespWPA2lWpRespModelMap().get(pidcVariantID).get(respID);
      for (Entry<Long, A2lWPRespModel> wpRespEntry : wpRespModelMap.entrySet()) {
        wpA2lWpRespModelMap.put(wpRespEntry.getKey(), wpRespEntry.getValue());
      }
    }
    else {
      for (Long wpID : selpidcTreeNode.getA2lStructureModel().getVarRespWpQniareMap().get(pidcVariantID).get(respID)
          .keySet()) {
        wpA2lWpRespModelMap.put(wpID, createDummyA2lWpRespModel(wpID, respID));
      }
    }

  }

  private A2lWPRespModel createDummyA2lWpRespModel(final Long wpID, final Long respID) {
    A2lWPRespModel a2lWPRespModel = new A2lWPRespModel();
    a2lWPRespModel.setA2lRespId(respID);
    a2lWPRespModel.setA2lWpId(wpID);
    return a2lWPRespModel;
  }

  private boolean isRespAvailable(final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWPA2lWpRespModelMap,
      final Long variantID, final Long respID) {
    return respWPA2lWpRespModelMap.containsKey(variantID) && respWPA2lWpRespModelMap.get(variantID).containsKey(respID);

  }

  private boolean isWPAvailable(final Map<Long, Map<Long, Map<Long, A2lWPRespModel>>> respWPA2lWpRespModelMap,
      final Long variantID, final Long respID, final Long wpID) {
    return respWPA2lWpRespModelMap.containsKey(variantID) &&
        respWPA2lWpRespModelMap.get(variantID).containsKey(respID) &&
        respWPA2lWpRespModelMap.get(variantID).get(respID).containsKey(wpID);

  }

  /**
   * @param manager
   * @param pidcTreeNode
   */
  private void addContextMenuForPidcA2lStructure(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    PidcVariant pidcVariant;
    PidcA2l pidcA2l;
    if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)) {
      // for responsibility node under pidc a2l
      pidcVariant = pidcTreeNode.getParentNode().getPidcVariant();
      pidcA2l = pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
    }
    else if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE)) {
      // for workpackage node under pidc a2l
      pidcVariant = pidcTreeNode.getParentNode().getParentNode().getPidcVariant();
      pidcA2l = pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcA2l();
    }
    else {
      // for Variant node under pidc a2l
      pidcVariant = pidcTreeNode.getPidcVariant();
      pidcA2l = pidcTreeNode.getParentNode().getPidcA2l();
      manager.add(new Separator());
      manager.add(new DataAssessmentReportAction(pidcTreeNode));
    }
    manager.add(new Separator());
    manager.add(new CdrReportGenerationAction(pidcA2l, pidcVariant, pidcTreeNode.getA2lResponsibility(),
        pidcTreeNode.getA2lWorkpackage()));

    // condition valid for responsibility and workpackage node under a pidc
    if (!pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE)) {
      QuestionnaireAction qnaireAction = new QuestionnaireAction();
      manager.add(new Separator());
      lockWPContextMenu(manager, pidcTreeNode);
      manager.add(new Separator());
      setCdfxContextMenu(manager, pidcTreeNode, qnaireAction);


    }
  }


  /**
   * @param manager
   * @param pidcTreeNode
   * @param qnaireAction
   */
  private void setCdfxContextMenu(final IMenuManager manager, final PidcTreeNode pidcTreeNode,
      final QuestionnaireAction qnaireAction) {
    if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE)) {
      pidcTreeNode.setPidcA2l(pidcTreeNode.getParentNode().getParentNode().getPidcA2l());
    }
    else if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE)) {
      pidcTreeNode.setPidcA2l(pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcA2l());

    }
    qnaireAction.setCdfxExport(manager, pidcTreeNode);
  }

  /**
   * @param manager
   * @param pidcTreeNode
   * @param qnaireAction
   */
  private void addContextMenuForRespNode(final IMenuManager manager, final PidcTreeNode pidcTreeNode,
      final QuestionnaireAction qnaireAction) {
    if ((pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE)) ||
        (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE))) {
      manager.add(new Separator());
      qnaireAction.pasteQnaireRespToMultipleWPAction(manager, pidcTreeNode);
    }
  }

  /**
   * Add actions for Qnaire resp node - delete/undelete, copy, paste, external link actions
   *
   * @param manager menu manager
   * @param pidcTreeNode node
   */
  private void addQnaireRespNodeActions(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    QuestionnaireAction qnaireAction = new QuestionnaireAction();

    // Delete/Un-delete action
    manager.add(new Separator());
    qnaireAction.delOrUndelQnaireRespAction(manager, pidcTreeNode);

    // Update qnaire to new version action
    manager.add(new Separator());
    qnaireAction.updateQnaireToNewVersion(manager, pidcTreeNode);

    // Copy/Paste actions
    manager.add(new Separator());
    if (!pidcTreeNode.getQnaireResp().isDeletedFlag()) {
      qnaireAction.copyQnaireRespAction(manager, pidcTreeNode);
    }
    qnaireAction.pasteQnaireRespAction(manager, pidcTreeNode);

    // External link actions
    manager.add(new Separator());
    qnaireAction.copyQnaireResponseLinkToClipBoard(manager, pidcTreeNode);
    qnaireAction.sendQnaireResponseLinkToEmail(manager, pidcTreeNode);
  }

  /**
   * @param manager
   * @param pidcTreeNode
   * @param actionset
   * @param selection
   */
  private void reviewProcessAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode,
      final A2LActionSet actionset, final IStructuredSelection selection) {
    if (selection.size() == 1) {
      CdrActionSet cdrActionSet = new CdrActionSet();
      PidcA2l pidcA2l = pidcTreeNode.getPidcA2l();
      cdrActionSet.setReviewProcessAction(manager, pidcTreeNode);
      cdrActionSet.startMonicaReview(manager, pidcA2l);

      manager.add(new Separator());
      cdrActionSet.setCompliReviewAction(manager, pidcTreeNode);

      manager.add(new Separator());
      cdrActionSet.addCdrReportGenerationAction(manager, pidcA2l);
      cdrActionSet.compareHEXWithLatestRvwData(manager, pidcA2l);
      // 100% cdfx export action
      if (checkIfUserCanCDFXDelivery(pidcA2l)) {
        cdrActionSet.cdfxExportAction(manager, pidcTreeNode);
      }
      manager.add(new Separator());
      cdrActionSet.dataAssessmentReportAction(manager, pidcTreeNode);

      manager.add(new Separator());
      cdrActionSet.dataAssessmentBaselineAction(manager, pidcTreeNode);
      cdrActionSet.openWPArchiveListAction(manager, pidcTreeNode);

      manager.add(new Separator());
      actionset.copytoClipBoard(manager, pidcTreeNode.getPidcA2l());
      actionset.sendA2lFileLinkInOutlook(manager, pidcTreeNode.getPidcA2l());

      manager.add(new Separator());
      manager.add(new A2lExportWithWpAction(pidcTreeNode.getPidcA2l()));

      manager.add(new Separator());
      manager.add(new DownloadArtifactsAction(pidcTreeNode.getPidcA2l()));

      manager.add(new Separator());
    }
    if (selection.size() > 1) {
      List<PidcA2l> selPidcA2l = new ArrayList<>();
      for (Object sel : selection.toList()) {
        if (sel instanceof PidcTreeNode) {
          selPidcA2l.add(((PidcTreeNode) sel).getPidcA2l());
        }
      }
      manager.add(new CompareA2lAction(selPidcA2l));
      manager.add(new Separator());
    }
  }

  /**
   * Method to check if user can do cdfx delivery
   *
   * @param pidcA2l
   */
  private boolean checkIfUserCanCDFXDelivery(final PidcA2l pidcA2l) {
    boolean cdfxDeliveryAllowed = false;
    try {
      cdfxDeliveryAllowed = new CurrentUserBO().canPerformCdfxDelivery(pidcA2l.getProjectId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return cdfxDeliveryAllowed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openA2LFile(final Long pidcA2lId) {
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    Job job = new A2LFileLoaderJob(new PidcA2LBO(pidcA2lId, null), true);
    job.schedule();
    // ICDM-2608
    Job.getJobManager().resume();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openReviewResult(final CDRReviewResult cdrResult, final String paramName, final Long variantId) {
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
  public void openReviewResult(final RvwVariant rvwResult, final String paramName) {
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
