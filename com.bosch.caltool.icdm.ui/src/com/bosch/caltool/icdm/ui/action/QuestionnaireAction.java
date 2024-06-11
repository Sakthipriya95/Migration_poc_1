/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.LongPredicate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.dialogs.DefineQuestionnaireDialog;
import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizard;
import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizardDialog;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespActionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespCopyData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespPasteDataWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuesRespCopyDataWrapper;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.PasteQuestionnaireResponseDialog;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CdfxExportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author dmr1cob
 */
public class QuestionnaireAction {


  /**
   *
   */
  private static final String COPY_PASTE_OF_QUESTIONNAIRE = "Copy/Paste of Questionnaire";
  /**
   *
   */
  private static final String FOR_THE_WORKPACKAGE = "For the following workpackage(s)";


  /**
   * Method to display Add/Edit Questionnaire Response Action
   *
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void setAddEditQnaire(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {

    final Action addEditQnaireRespAction = new Action() {

      @Override
      public void run() {
        DefineQuestionnaireDialog dialog =
            new DefineQuestionnaireDialog(Display.getDefault().getActiveShell(), pidcTreeNode);
        dialog.open();
      }
    };
    addEditQnaireRespAction.setText("Add/Edit Questionnaires");
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EDIT_BLACK_16X16);
    addEditQnaireRespAction.setImageDescriptor(imageDesc);

    addEditQnaireRespAction.setEnabled(qnaireAccessValidation(pidcTreeNode) &&
        CommonUtils.isNotEqual(pidcTreeNode.getName(), ApicConstants.DEFAULT_A2L_WP_NAME));
    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      addEditQnaireRespAction.setEnabled(false);
    }

    if (checkIfNodeIsUnderDeletedVarOfRvwQnaireTree(pidcTreeNode) ||
        checkIfNodeIsUnderDeletedVarOfA2LStructure(pidcTreeNode)) {
      addEditQnaireRespAction.setEnabled(false);
    }
    manager.add(addEditQnaireRespAction);
  }

  /**
   * @param pidcTreeNode
   * @return
   */
  private boolean checkIfNodeIsUnderDeletedVarOfA2LStructure(final PidcTreeNode pidcTreeNode) {
    return (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE) &&
        pidcTreeNode.getPidcVariant().isDeleted()) ||
        (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE) &&
            pidcTreeNode.getPidcVariant().isDeleted()) ||
        ((pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE) &&
            pidcTreeNode.getPidcVariant().isDeleted()) ||
            (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE) &&
                pidcTreeNode.getPidcVariant().isDeleted()));
  }

  /**
   * @param pidcTreeNode
   * @return
   */
  private boolean checkIfNodeIsUnderDeletedVarOfRvwQnaireTree(final PidcTreeNode pidcTreeNode) {
    return (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE) &&
        pidcTreeNode.getPidcVariant().isDeleted()) ||
        (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE) &&
            pidcTreeNode.getPidcVariant().isDeleted()) ||
        ((pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE) &&
            pidcTreeNode.getPidcVariant().isDeleted()) ||
            (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) &&
                pidcTreeNode.getPidcVariant().isDeleted()));
  }

  /**
   * @param pidcTreeNode pidc tree node on which the context menu is applicable
   * @return access flag(boolean) indicating whether context menu will be enabled for the user or not
   */
  public boolean qnaireAccessValidation(final PidcTreeNode pidcTreeNode) {
    boolean accessFlag = false;
    try {
      Long pidcId = pidcTreeNode.getPidcVersion().getPidcId();
      RvwQnaireResponseServiceClient rvwQnaireResponseServiceClient = new RvwQnaireResponseServiceClient();

      // If it is Qnaire Resp Node
      if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) ||
          pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE)) {
        long qmaireA2lRespId = pidcTreeNode.getA2lResponsibility().getId();
        long nodeA2lRespId = pidcTreeNode.getParentNode().getA2lResponsibility().getId();
        // If node Resp and Qnaire Resp is not same, it indicates it is a linked Qnaire from different Responsibility
        // In this case return true if either of the Responsibiles have access
        if (nodeA2lRespId != qmaireA2lRespId) {
          accessFlag = rvwQnaireResponseServiceClient.validateQnaireAccess(pidcId, nodeA2lRespId) ||
              rvwQnaireResponseServiceClient.validateQnaireAccess(pidcId, qmaireA2lRespId);
        }
        else {
          accessFlag = rvwQnaireResponseServiceClient.validateQnaireAccess(pidcId, nodeA2lRespId);
        }
      }
      // For all other Tree Nodes
      else {

        A2lResponsibility a2lResponsibility = pidcTreeNode.getA2lResponsibility();

        Long a2lRespId = a2lResponsibility != null ? a2lResponsibility.getId() : 0L;

        accessFlag = new RvwQnaireResponseServiceClient().validateQnaireAccess(pidcId, a2lRespId);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return accessFlag;
  }

  /**
   * Method to display Questionnaire Response Delete /Un Delete Action
   *
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void delOrUndelQnaireRespAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    RvwQnaireResponse qnaireResp = pidcTreeNode.getQnaireResp();
    final Action deleteQnaireRespAction = new Action() {

      @Override
      public void run() {
        try {
          qnaireResp.setDeletedFlag(!qnaireResp.isDeletedFlag());
          RvwQnaireResponseServiceClient rvwQnaireRespServiceClient = new RvwQnaireResponseServiceClient();
          rvwQnaireRespServiceClient.deleteUndeleteQuesResp(qnaireResp);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    ImageDescriptor imageDesc;

    if (qnaireResp.isDeletedFlag()) {
      deleteQnaireRespAction.setText("Un-Delete Questionnaire Response");
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNDO_DELETE_16X16);
    }
    else {
      deleteQnaireRespAction.setText("Delete Questionnaire Response");
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    }

    deleteQnaireRespAction.setImageDescriptor(imageDesc);
    deleteQnaireRespAction.setEnabled(
        genQuesValidation(qnaireResp) && qnaireAccessValidation(pidcTreeNode) && varNotDeleted(pidcTreeNode));
    manager.add(deleteQnaireRespAction);
  }

  /**
   * Copy link for CDR Questionnaire Response
   *
   * @param manager menu Manager
   * @param pidcTreeNode element
   */
  public void copyQnaireResponseLinkToClipBoard(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {

    final Action copyLink = new Action() {

      @Override
      public void run() {
        try {
          final Object linkObj = getQnaireRespObjectForLink(pidcTreeNode);
          LinkCreator linkCreator = new LinkCreator(linkObj);
          linkCreator.copyToClipBoard();
        }
        catch (ExternalLinkException | ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy Questionnaire Response Link");
    copyLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUESTIONNAIRE_ICON_WITH_LINK_16X16));
    copyLink.setEnabled(true);
    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      copyLink.setEnabled(false);
    }

    manager.add(copyLink);
  }

  /**
   * Copy link for CDR Questionnaire Response
   *
   * @param manager menu Manager
   * @param pidcTreeNode element
   */
  public void sendQnaireResponseLinkToEmail(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    Object linkObj = null;
    try {
      linkObj = getQnaireRespObjectForLink(pidcTreeNode);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    final Action sendLinkAction = new SendObjectLinkAction("Send Questionnaire Response Link", (IModel) linkObj);
    sendLinkAction.setEnabled(true);
    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      sendLinkAction.setEnabled(false);
    }
    manager.add(sendLinkAction);
  }

  /**
   * Update Questionnaire to new version
   *
   * @param manager menu Manager
   * @param pidcTreeNode element
   */
  public void updateQnaireToNewVersion(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {

    final Action updateVersion = new Action() {

      @Override
      public void run() {
        try {

          if (new RvwQnaireResponseServiceClient().isQnaireVersUpdateReq(pidcTreeNode.getQnaireRespId())) {
            StringBuilder message = new StringBuilder();
            message.append(
                "Existing answers will be saved in a baseline. Working Set will be replaced with the new version. Do you want to continue?");
            boolean canUpdate = MessageDialogUtils
                .getConfirmMessageDialogWithYesNo("Confirm to Update Questionnaire To New version", message.toString());
            if (canUpdate) {
              updateQnaireVersion(getQnaireRespUpdateData(pidcTreeNode));
            }
          }
          else {
            MessageDialogUtils.getInfoMessageDialog("Confirm to Update Questionnaire To New version",
                "Questionnaire response version is up to date, no update is required!");
          }

        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    updateVersion.setText("Update Questionnaire To Latest Active Version");
    updateVersion.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QNAIRE_VERSION_UPDATE));

    updateVersion.setEnabled(qnaireAccessValidation(pidcTreeNode));
    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      updateVersion.setEnabled(false);
    }

    manager.add(updateVersion);
  }

  /**
   * @param pidcTreeNode
   * @return getQnaireRespUpdateData
   */
  private QnaireRespActionData getQnaireRespUpdateData(final PidcTreeNode pidcTreeNode) {
    PidcVariant pidcVariant = pidcTreeNode.getPidcVariant();

    // for handling No-Variant case
    Long pidcVariantId = CommonUtils.isNull(pidcVariant) ? 0L : pidcVariant.getId();

    Long a2lRespId = pidcTreeNode.getQnaireResp().getA2lRespId();
    Long a2lWpId = pidcTreeNode.getQnaireResp().getA2lWpId();
    PidcVersion pidcVersion = pidcTreeNode.getPidcVersion();
    QnaireRespActionData qnaireRespUpdateData = new QnaireRespActionData();
    qnaireRespUpdateData.setTargetRespId(a2lRespId);
    qnaireRespUpdateData.setTargetWpId(a2lWpId);
    qnaireRespUpdateData.setTargetPidcVersion(pidcVersion);
    // for handling No-Variant case
    if (pidcVariantId > 0) {
      qnaireRespUpdateData.setTargetPidcVariant(pidcVariant);
    }
    qnaireRespUpdateData.setExistingTargetQnaireResp(pidcTreeNode.getQnaireResp());
    return qnaireRespUpdateData;
  }


  private void updateQnaireVersion(final QnaireRespActionData qnaireRespUpdateData) {
    try {

      ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, (final IProgressMonitor monitor) -> {

        monitor.beginTask("Updating Questionnaire Response version...", 100);
        monitor.worked(20);

        try {

          new RvwQnaireRespCopyServiceClient().updateQnaireRespVersion(qnaireRespUpdateData);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

    catch (InterruptedException exp) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  private Object getQnaireRespObjectForLink(final PidcTreeNode pidcTreeNode) throws ApicWebServiceException {

    // if variantId = -1, its <NO_VARIANT>
    if (pidcTreeNode.getPidcVariant().getId() == -1) {
      return pidcTreeNode.getQnaireResp();
    }
    List<RvwQnaireRespVariant> qnaireRespVarList =
        new RvwQnaireRespVariantServiceClient().getRvwQnaireRespVariantList(pidcTreeNode.getQnaireRespId());
    for (RvwQnaireRespVariant rvwQnaireRespVariant : qnaireRespVarList) {
      if (rvwQnaireRespVariant.getVariantId().equals(pidcTreeNode.getPidcVariant().getId())) {
        return rvwQnaireRespVariant;
      }
    }
    return null;
  }

  /**
   * @param pidcTreeNode
   * @return true if variant is not deleted
   */
  private boolean varNotDeleted(final PidcTreeNode pidcTreeNode) {
    PidcTreeNode varNode = pidcTreeNode.getParentNode().getParentNode().getParentNode();
    return !varNode.getPidcVariant().isDeleted();
  }

  /**
   * @param qnaireResp
   * @return true if it is not general questionnaire or if the general questionnaire can be deleted
   */
  private boolean genQuesValidation(final RvwQnaireResponse qnaireResp) {
    RvwQnaireResponseServiceClient quesRespClient = new RvwQnaireResponseServiceClient();
    boolean genQuesNotRequired = false;
    boolean isGnrlQnaire = (qnaireResp.getName().contains(ApicConstants.GENERAL_QUESTIONS) ||
        qnaireResp.getName().contains(ApicConstants.OBD_GENERAL_QUESTIONS));

    try {
      genQuesNotRequired = quesRespClient.isGenQuesNotRequired(qnaireResp.getA2lWpId(), qnaireResp.getA2lRespId(),
          qnaireResp.getVariantId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    // Enable Delete Qnaire Option if the sel Qnaireresp is not General Qnaire or selected Qnaire Resp is Gnrl Qnaire
    // with GenQnaireNotReq Qnaire Resp
    return !isGnrlQnaire || genQuesNotRequired;
  }


  /**
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void copyQnaireRespAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    IMenuManager subMenuMgr = new MenuManager("Copy");
    subMenuMgr.setRemoveAllWhenShown(true);
    subMenuMgr.addMenuListener(submenumgr -> {
      copyOnlyWrkngSetQnaireRespAction(submenumgr, pidcTreeNode);
      copyAllRevOfQnaireRespAction(submenumgr, pidcTreeNode);
    });
    subMenuMgr.setVisible(qnaireAccessValidation(pidcTreeNode));
    manager.add(subMenuMgr);
  }

  /**
   * Method to copy only working set of copied Questionnaire Response Action
   *
   * @param subMenuManager as input
   * @param pidcTreeNode as input
   */
  public void copyOnlyWrkngSetQnaireRespAction(final IMenuManager subMenuManager, final PidcTreeNode pidcTreeNode) {
    final Action copyOnlyWrkngSetQnaireRespAction = new Action() {

      @Override
      public void run() {
        // Removing the copied object after pasting
        ICDMClipboard.getInstance().setCopiedObject(null);
        ICDMClipboard icdmClipboardObj = ICDMClipboard.getInstance();
        icdmClipboardObj.setCopiedObject(pidcTreeNode);
        icdmClipboardObj.setCopyOnlyWorkingSet(true);
      }
    };
    copyOnlyWrkngSetQnaireRespAction.setText("Copy Only Working Set");
    copyOnlyWrkngSetQnaireRespAction.setEnabled(qnaireAccessValidation(pidcTreeNode));
    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      copyOnlyWrkngSetQnaireRespAction.setEnabled(false);
    }
    subMenuManager.add(copyOnlyWrkngSetQnaireRespAction);
  }


  /**
   * Method to copy all revision of copied Questionnaire Response Action
   *
   * @param subMenuManager as input
   * @param pidcTreeNode as input
   */
  public void copyAllRevOfQnaireRespAction(final IMenuManager subMenuManager, final PidcTreeNode pidcTreeNode) {
    final Action copyAllRevOfQnaireRespAction = new Action() {

      @Override
      public void run() {
        // Removing the copied object after pasting
        ICDMClipboard.getInstance().setCopiedObject(null);
        ICDMClipboard icdmClipboardObj = ICDMClipboard.getInstance();
        icdmClipboardObj.setCopiedObject(pidcTreeNode);
        icdmClipboardObj.setCopyOnlyWorkingSet(false);
      }
    };
    copyAllRevOfQnaireRespAction.setText("Copy Both Working Set and Baselines");
    copyAllRevOfQnaireRespAction.setEnabled(qnaireAccessValidation(pidcTreeNode));

    if (CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) {
      copyAllRevOfQnaireRespAction.setEnabled(false);
    }

    subMenuManager.add(copyAllRevOfQnaireRespAction);
  }

  /**
   * Method to paste copied Questionnaire Response Action
   *
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void pasteQnaireRespAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {

    boolean isCopiedObjPidcTreenode = ICDMClipboard.getInstance().getCopiedObject() instanceof PidcTreeNode;
    boolean isQnaireWPNode = pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE);
    Long destRespId =
        isQnaireWPNode ? pidcTreeNode.getA2lResponsibility().getId() : pidcTreeNode.getQnaireResp().getA2lRespId();

    final Action pasteQnaireRespAction = new Action() {

      @Override
      public void run() {
        if (pidcTreeNode.getSizeOfSelectedNodes() > 1) {
          MessageDialogUtils.getInfoMessageDialog("Confirm to Copy/Paste of a Questionnaire",
              "This feature is not supported. To paste to multiple WP, Please use 'Paste to Mutiple WP' feature in responsible node.");
        }
        else {
          PidcTreeNode copiedObject = (PidcTreeNode) ICDMClipboard.getInstance().getCopiedObject();
          if (CommonUtils.isEqual(pidcTreeNode.getNodeType(), PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) ||
              (CommonUtils.isEqual(pidcTreeNode.getNodeType(), PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE))) {
            copyPasteQnaireResponse(pidcTreeNode.getQnaireResp().getA2lRespId(),
                pidcTreeNode.getQnaireResp().getA2lWpId(), pidcTreeNode, copiedObject);
          }
          else if (isQnaireWPNode) {
            copyPasteQnaireResponse(pidcTreeNode.getA2lResponsibility().getId(),
                pidcTreeNode.getA2lWorkpackage().getId(), pidcTreeNode, copiedObject);
          }
        }
      }
    };
    pasteQnaireRespAction.setText("Paste");
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    pasteQnaireRespAction.setImageDescriptor(imageDesc);

    // Disable the paste option
    pasteQnaireRespAction.setEnabled(isCopiedObjPidcTreenode &&
        canEnablePasteAction(pidcTreeNode, (PidcTreeNode) ICDMClipboard.getInstance().getCopiedObject(), destRespId)
            .test(isQnaireWPNode ? pidcTreeNode.getA2lWorkpackage().getId()
                : pidcTreeNode.getQnaireResp().getA2lWpId()) &&
        CommonUtils.isNotEqual(pidcTreeNode.getName(), ApicConstants.DEFAULT_A2L_WP_NAME));


    manager.add(pasteQnaireRespAction);
  }

  /**
   * @param pidcTreeNode
   * @param copiedObject
   * @param destRespId
   * @return
   */
  private LongPredicate canEnablePasteAction(final PidcTreeNode pidcTreeNode, final PidcTreeNode copiedObject,
      final Long destRespId) {
    boolean isQnaireRespNode = false;
    return destWpId -> (qnaireAccessValidation(pidcTreeNode) && CommonUtils.isNotNull(copiedObject) &&
        (CommonUtils.isEqual(copiedObject.getNodeType(), PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE) ||
            CommonUtils.isEqual(copiedObject.getNodeType(), PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE)) &&
        (!isQnaireRespNode && !pidcTreeNode.getParentNode().getPidcVariant().isDeleted()) &&
        !pidcTreeNode.getParentNode().getPidcVariant().isDeleted() &&
        (CommonUtils.isNotEqual(destWpId, copiedObject.getQnaireResp().getA2lWpId()) ||
            CommonUtils.isNotEqual(copiedObject.getQnaireResp().getA2lRespId(), destRespId) ||
            CommonUtils.isNotEqual(copiedObject.getPidcVariant().getId(), pidcTreeNode.getPidcVariant().getId()) ||
            CommonUtils.isNotEqual(copiedObject.getPidcVersion().getId(), pidcTreeNode.getPidcVersion().getId())));
  }

  /**
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void pasteQnaireRespToMultipleWPAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    boolean isCopiedObjPidcTreenode = ICDMClipboard.getInstance().getCopiedObject() instanceof PidcTreeNode;

    boolean isQnaireRespNode = pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE) ||
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE);
    Long destRespId =
        isQnaireRespNode ? pidcTreeNode.getA2lResponsibility().getId() : pidcTreeNode.getQnaireResp().getA2lRespId();

    final Action pasteQnaireRespAction = new Action() {

      @Override
      public void run() {
        PidcTreeNode copiedObject = (PidcTreeNode) ICDMClipboard.getInstance().getCopiedObject();
        if (isSameCopiedQnaireLinkedInDest(copiedObject.getQnaireResp().getSecondaryQnaireLinkMap(),
            copiedObject.getPidcVariant().getId(), copiedObject.getParentNode().getA2lResponsibility().getId(),
            copiedObject.getParentNode().getA2lWorkpackage().getId()) &&
            isSameQnaireInDest(pidcTreeNode.getPidcVariant().getId(),
                pidcTreeNode.getParentNode().getA2lResponsibility().getId(),
                pidcTreeNode.getParentNode().getA2lWorkpackage().getId(), copiedObject)) {
          // if copied questionnaire response is a linked questionnaire and if user tries to copy linked questionnaire
          // to
          // its original variant itself, then
          // throw the error
          MessageDialogUtils.getErrorMessageDialog(COPY_PASTE_OF_QUESTIONNAIRE,
              "It is not possible to copy/paste 'Linked questionnaire response' to its parent. Kindly select different variant.");
        }
        else {
          PasteQuestionnaireResponseDialog multiplePasteQnaireResponseDialog =
              new PasteQuestionnaireResponseDialog(Display.getDefault().getActiveShell(), pidcTreeNode);
          multiplePasteQnaireResponseDialog.open();
          if (multiplePasteQnaireResponseDialog.getReturnCode() == 0) {
            Set<QnaireRespPasteDataWrapper> copyPasteMultipleQnaireResponse = copyPasteMultipleQnaireResponse(
                destRespId, multiplePasteQnaireResponseDialog.getSelectedWorkPackageSet(), pidcTreeNode, copiedObject);
            // To Handle Mulipt PasteAction and its Error / Display messages
            multiCopyPasteQnaireRespCopyData(copyPasteMultipleQnaireResponse);
          }
        }
      }
    };
    pasteQnaireRespAction.setText("Paste to Multiple WP");

    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    pasteQnaireRespAction.setImageDescriptor(imageDesc);

    pasteQnaireRespAction.setEnabled(isCopiedObjPidcTreenode &&
        canEnablePasteAction(pidcTreeNode, (PidcTreeNode) ICDMClipboard.getInstance().getCopiedObject(), destRespId)
            .test(isQnaireRespNode ? pidcTreeNode.getA2lResponsibility().getId()
                : pidcTreeNode.getQnaireResp().getA2lRespId()));

    manager.add(pasteQnaireRespAction);
  }


  /**
   * Method to handle the Copy Paste of Multiple Questionnaire response And its QnaireRespPasteDataWrapper with Error
   * and Display message
   */
  private Set<QnaireRespPasteDataWrapper> copyPasteMultipleQnaireResponse(final Long destRespId,
      final Set<A2lWorkPackage> destWpSet, final PidcTreeNode destPidcTreeNode, final PidcTreeNode srcPidcTreeNode) {

    Set<QnaireRespPasteDataWrapper> qnaireRespPasteDataWrapperSet = new HashSet<>();
    for (A2lWorkPackage a2lWorkPackage : destWpSet) {
      QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper = new QnaireRespPasteDataWrapper();

      qnaireRespPasteDataWrapper.setDestA2lWorkpackage(a2lWorkPackage);

      try {
        PidcVersion destPidcVers = destPidcTreeNode.getPidcVersion();
        QnaireRespCopyData qnaireRespCopyData = new QnaireRespCopyData();
        qnaireRespCopyData.setTargetRespId(destRespId);
        qnaireRespCopyData.setTargetWpId(a2lWorkPackage.getId());
        qnaireRespCopyData.setTargetPidcVersion(destPidcVers);

        // for handling No-Variant case
        PidcVariant destPidcVar = destPidcTreeNode.getPidcVariant();
        Long destPidcVarId = CommonUtils.isNull(destPidcVar) ? 0L : destPidcVar.getId();
        if (destPidcVarId > 0) {
          qnaireRespCopyData.setTargetPidcVariant(destPidcVar);
        }
        qnaireRespCopyData.setCopiedQnaireResp(srcPidcTreeNode.getQnaireResp());
        qnaireRespCopyData.setCopyOnlyWorkingSet(ICDMClipboard.getInstance().isCopyOnlyWorkingSet());


        // service call to fetch data related to copy
        RvwQnaireRespCopyServiceClient rvwQnaireRespCpySrvClient = new RvwQnaireRespCopyServiceClient();
        QuesRespCopyDataWrapper quesRespCopyDataWrapper = rvwQnaireRespCpySrvClient.getDataForCopyQnaireRespValidation(
            destPidcVers.getId(), destPidcVarId, a2lWorkPackage.getId(), destRespId, srcPidcTreeNode.getQnaireRespId(),
            srcPidcTreeNode.getPidcVersion().getId());

        copyPasteQnaireResp(qnaireRespPasteDataWrapper, qnaireRespCopyData, quesRespCopyDataWrapper);

        qnaireRespPasteDataWrapper.setQnaireRespCopyData(qnaireRespCopyData);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }

      qnaireRespPasteDataWrapperSet.add(qnaireRespPasteDataWrapper);
    }
    return qnaireRespPasteDataWrapperSet;
  }

  /**
   * Method to handle the Copy Paste of Multiple Questionnaire response And its Error and Display message
   *
   * @param copyPasteMultipleQnaireResponse as input
   */
  private void multiCopyPasteQnaireRespCopyData(final Set<QnaireRespPasteDataWrapper> copyPasteMultipleQnaireResponse) {
    StringBuilder linkedQnaireMsg = new StringBuilder();
    StringBuilder divMismatchMsg = new StringBuilder();
    StringBuilder genQuesDispMsg = new StringBuilder();
    StringBuilder alreadyAvaiQuesRespDispMsg = new StringBuilder();

    // set of QnaireRespCopyData for normal paste
    Set<QnaireRespCopyData> normalPasteQtRspDataSet = new HashSet<>();
    // set of qnaireRespCopyData for General Question Eqvalent
    Set<QnaireRespCopyData> genQPstQtRspDataSet = new HashSet<>();
    // set of qnaireRespCopyData for already available questionnaire response
    Set<QnaireRespCopyData> alrdyAvailPstQtRspDataSet = new HashSet<>();

    // looping though the set of QnaireRespPasteDataWrapper
    for (QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper : copyPasteMultipleQnaireResponse) {
      // To check and add the error message for Division Id Difference Questionnaire Response
      if (qnaireRespPasteDataWrapper.isCopiedQuestDivIdDiff()) {
        buildCopyPasteErrMsgForDiffDivIds(divMismatchMsg, qnaireRespPasteDataWrapper);
      }
      else {
        QnaireRespCopyData qnaireRespCopyData = qnaireRespPasteDataWrapper.getQnaireRespCopyData();
        qnaireRespCopyData.setExistingTargetQnaireResp(qnaireRespPasteDataWrapper.getQnaireRespWithSameQnaireInDest());
        // case for displaying the message if qnaire resp is general question Eqvlnt
        if (qnaireRespPasteDataWrapper.isCopiedQuesGnrlQuesEqvlnt()) {
          buildCopyPasteErrMsgForGenQuesEqvlnt(genQuesDispMsg, qnaireRespPasteDataWrapper);
          qnaireRespCopyData.setDestGeneralQuesResp(qnaireRespPasteDataWrapper.getDestGeneralQuesResp());
          genQPstQtRspDataSet.add(qnaireRespCopyData);
        }
        // case for displaying message if qnaire response is already available
        else if (qnaireRespPasteDataWrapper.isCopiedQuesAlreadyAvailable()) {
          buildCopyPasteMsgForAlreadyAvailQnaireResp(alreadyAvaiQuesRespDispMsg, qnaireRespPasteDataWrapper);
          alrdyAvailPstQtRspDataSet.add(qnaireRespCopyData);
        }
        // case for normal paste without user interferance
        else {
          normalPasteQtRspDataSet.add(qnaireRespCopyData);
        }
      }
    }
    // displaying of error message to the user
    // during copy/paste action
    showErrorMessage(linkedQnaireMsg, divMismatchMsg);

    pasteOfQnaireResponse(genQuesDispMsg, alreadyAvaiQuesRespDispMsg, normalPasteQtRspDataSet, genQPstQtRspDataSet,
        alrdyAvailPstQtRspDataSet);
  }

  /**
   * @param genQuesDispMsg
   * @param genAndAlreadyQuesRespDispMsg
   * @param alreadyAvaiQuesRespDispMsg
   * @param normalPasteQtRspDataSet
   * @param genQPstQtRspDataSet
   * @param genAndAlrdyPstQtRspDataSet
   * @param alrdyAvailPstQtRspDataSet
   */
  private void pasteOfQnaireResponse(final StringBuilder genQuesDispMsg, final StringBuilder alreadyAvaiQuesRespDispMsg,
      final Set<QnaireRespCopyData> normalPasteQtRspDataSet, final Set<QnaireRespCopyData> genQPstQtRspDataSet,
      final Set<QnaireRespCopyData> alrdyAvailPstQtRspDataSet) {
    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, (final IProgressMonitor monitor) -> {

        monitor.beginTask("Copying Questionnaire Response...", 100);
        monitor.worked(20);
        // paste of qnaireRespCopyData
        pasteOfQnaireRespCopyData(genQuesDispMsg, alreadyAvaiQuesRespDispMsg, normalPasteQtRspDataSet,
            genQPstQtRspDataSet, alrdyAvailPstQtRspDataSet);
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param alreadyAvaiQuesRespDispMsg
   * @param aleadyAvaiQuesRespCount
   * @param qnaireRespPasteDataWrapper
   */
  private void buildCopyPasteMsgForAlreadyAvailQnaireResp(final StringBuilder alreadyAvaiQuesRespDispMsg,
      final QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper) {
    if (CommonUtils.isEmptyString(alreadyAvaiQuesRespDispMsg.toString())) {
      alreadyAvaiQuesRespDispMsg.append(qnaireRespPasteDataWrapper.getIsCopiedQuesAlreadyAvailableMsg());
      alreadyAvaiQuesRespDispMsg.append("\n");
      alreadyAvaiQuesRespDispMsg.append(qnaireRespPasteDataWrapper.getDestA2lWorkpackage().getName());
    }
    else {
      alreadyAvaiQuesRespDispMsg.append("\n");
      alreadyAvaiQuesRespDispMsg.append(qnaireRespPasteDataWrapper.getDestA2lWorkpackage().getName());
      if (qnaireRespPasteDataWrapper.isDestRespDeleted()) {
        alreadyAvaiQuesRespDispMsg.append(" - Questionnaire Response in deleted state.");
      }
    }
  }

  /**
   * @param genQuesDispMsg
   * @param genQuesDispCount
   * @param qnaireRespPasteDataWrapper
   */
  private void buildCopyPasteErrMsgForGenQuesEqvlnt(final StringBuilder genQuesDispMsg,
      final QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper) {
    if (CommonUtils.isEmptyString(genQuesDispMsg.toString())) {
      genQuesDispMsg.append(qnaireRespPasteDataWrapper.getIsCopiedQuesGnrlQuesDispMsg());
      genQuesDispMsg.append("\n");
      genQuesDispMsg.append(qnaireRespPasteDataWrapper.getDestA2lWorkpackage().getName());
    }
    else {
      genQuesDispMsg.append("\n");
      genQuesDispMsg.append(qnaireRespPasteDataWrapper.getDestA2lWorkpackage().getName());
    }
  }

  /**
   * @param divMismatchMsg
   * @param divMisMatchCount
   * @param qnaireRespPasteDataWrapper
   */
  private void buildCopyPasteErrMsgForDiffDivIds(final StringBuilder divMismatchMsg,
      final QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper) {
    if (CommonUtils.isEmptyString(divMismatchMsg.toString())) {
      divMismatchMsg.append(qnaireRespPasteDataWrapper.getIsCopiedQuestDivIdDiffMsg());
      divMismatchMsg.append("\n");
      divMismatchMsg.append(qnaireRespPasteDataWrapper.getDestA2lWorkpackage().getName());
    }
    else {
      divMismatchMsg.append("\n");
      divMismatchMsg.append(qnaireRespPasteDataWrapper.getDestA2lWorkpackage().getName());
    }
  }


  /**
   * @param genQuesDispMsg
   * @param genAndAlreadyQuesRespDispMsg
   * @param normalPasteQtRspDataSet
   * @param genQPstQtRspDataSet
   * @param genAndAlrdyPstQtRspDataSet
   */
  private void pasteOfQnaireRespCopyData(final StringBuilder genQuesDispMsg,
      final StringBuilder alreadyAvaiQuesRespDispMsg, final Set<QnaireRespCopyData> normalPasteQtRspDataSet,
      final Set<QnaireRespCopyData> genQPstQtRspDataSet, final Set<QnaireRespCopyData> alrdyAvailPstQtRspDataSet) {

    // for paste of normal questionnaire response
    for (QnaireRespCopyData normalPasteOfQnaireResp : normalPasteQtRspDataSet) {
      pasteQnaireRespCopyData(normalPasteOfQnaireResp);
    }
    // for getting confirmation from user for overwriting the questionnaire response
    if (!CommonUtils.isNullOrEmpty(alrdyAvailPstQtRspDataSet)) {
      boolean userConForPaste = MessageDialogUtils.getConfirmMessageDialogWithYesNo(
          "Confirm to Copy Questionnaire Response", alreadyAvaiQuesRespDispMsg.toString());
      getConfirmAndPasteQnaireResp(alrdyAvailPstQtRspDataSet, userConForPaste);
    }
    // for getting confirmation from user for delete of general questions
    if (!CommonUtils.isNullOrEmpty(genQPstQtRspDataSet)) {
      boolean userConForPaste = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Delete General Questionnaire",
          genQuesDispMsg.toString());
      getConfirmAndPasteQnaireResp(genQPstQtRspDataSet, userConForPaste);
    }
  }

  /**
   * @param alrdyAvailPstQtRspDataSet
   * @param userConForPaste
   */
  private void getConfirmAndPasteQnaireResp(final Set<QnaireRespCopyData> qnaireRespCopyDataSet,
      final boolean userConForPaste) {
    if (userConForPaste) {
      // for paste of already available questionnaire response
      for (QnaireRespCopyData qnaireRespCopyData : qnaireRespCopyDataSet) {
        pasteQnaireRespCopyData(qnaireRespCopyData);
      }
    }
  }

  /**
   * @param linkedQnaireMsg
   * @param divMismatchMsg
   */
  private void showErrorMessage(final StringBuilder linkedQnaireMsg, final StringBuilder divMismatchMsg) {
    // for Displaying error message to user
    StringBuilder linkedDisplErrorMsg = new StringBuilder();
    if (CommonUtils.isNotEmptyString(linkedQnaireMsg.toString())) {
      linkedDisplErrorMsg.append(linkedQnaireMsg.toString());
      linkedDisplErrorMsg.append("\n");
    }
    if (CommonUtils.isNotEmptyString(divMismatchMsg.toString())) {
      linkedDisplErrorMsg.append(divMismatchMsg.toString());
    }
    if (CommonUtils.isNotEmptyString(linkedDisplErrorMsg.toString())) {
      MessageDialogUtils.getErrorMessageDialog(COPY_PASTE_OF_QUESTIONNAIRE, linkedDisplErrorMsg.toString());
    }
  }

  /**
   * @param qnaireRespCopyData
   */
  private void pasteQnaireRespCopyData(final QnaireRespCopyData qnaireRespCopyData) {
    try {
      if (CommonUtils.isNull(qnaireRespCopyData.getExistingTargetQnaireResp())) {
        new RvwQnaireRespCopyServiceClient().createQnaireResp(qnaireRespCopyData);
      }
      else {
        new RvwQnaireRespCopyServiceClient().updateQnaireResp(qnaireRespCopyData);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param qnaireRespPasteDataWrapper
   * @param qnaireRespCopyData
   * @param quesRespCopyDataWrapper
   */
  private void copyPasteQnaireResp(final QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper,
      final QnaireRespCopyData qnaireRespCopyData, final QuesRespCopyDataWrapper quesRespCopyDataWrapper) {
    if (CommonUtils.isEqual(quesRespCopyDataWrapper.getSrcPidcDivId(), quesRespCopyDataWrapper.getDestPidcDivId())) {
      // method that has validation and service cell to copy/paste qnaire response
      copyPasteQnaireRespValidation(qnaireRespCopyData, quesRespCopyDataWrapper, qnaireRespPasteDataWrapper);
    }
    else {
      StringBuilder divMisMatchMsg = new StringBuilder();
      divMisMatchMsg.append("Copy/Paste of questionnaire response is allowed only within the same division. ");
      divMisMatchMsg.append(
          "Kindly check whether both the source and destination PIDC's 'iCDM Questionnaire Config' attribute value are same before copy.");
      divMisMatchMsg.append("\n");
      divMisMatchMsg.append("\n");
      divMisMatchMsg.append(FOR_THE_WORKPACKAGE);
      qnaireRespPasteDataWrapper.setCopiedQuestDivIdDiff(true);
      qnaireRespPasteDataWrapper.setIsCopiedQuestDivIdDiffMsg(divMisMatchMsg.toString());
    }
  }

  private void copyPasteQnaireRespValidation(final QnaireRespCopyData qnaireRespCopyData,
      final QuesRespCopyDataWrapper quesRespCopyDataWrapper,
      final QnaireRespPasteDataWrapper qnaireRespPasteDataWrapper) {

    // Handling 'General question Equivalent' case(if the copied questionnaire is 'General question', then the
    // 'general
    // question equivalent flag' will be false)
    // General question response will not be available for some cases as per the common param configuration in the
    // database
    RvwQnaireResponse destGeneralQuesResp = quesRespCopyDataWrapper.getDestGeneralQuesResp();
    if (CommonUtils.isNotNull(destGeneralQuesResp) && !destGeneralQuesResp.isDeletedFlag() &&
        CommonUtils.isEqual(quesRespCopyDataWrapper.getIsCopiedQuesGnrlQuesEqvlnt(),
            CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue())) {

      StringBuilder generalQuesMsg = new StringBuilder();
      generalQuesMsg.append("The copied questionnaire ");
      generalQuesMsg.append(qnaireRespCopyData.getCopiedQnaireResp().getName());
      generalQuesMsg.append(
          " is equivalent to General Questionnaire. So the destination will no longer need the general questionnaire. Do you want to remove the general questionnaire in destination?");
      generalQuesMsg.append("\n");
      generalQuesMsg.append("\n");
      generalQuesMsg.append(FOR_THE_WORKPACKAGE);
      qnaireRespPasteDataWrapper
          .setCopiedQuesGnrlQuesEqvlnt(CommonUtils.isEqual(quesRespCopyDataWrapper.getIsCopiedQuesGnrlQuesEqvlnt(),
              CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue()));
      qnaireRespPasteDataWrapper.setIsCopiedQuesGnrlQuesDispMsg(generalQuesMsg.toString());
      qnaireRespPasteDataWrapper.setDestGeneralQuesResp(quesRespCopyDataWrapper.getDestGeneralQuesResp());

    }
    RvwQnaireResponse destRvwQnaireResp = quesRespCopyDataWrapper.getQnaireRespWithSameQnaireInDest();
    if (CommonUtils.isNotNull(destRvwQnaireResp)) {
      StringBuilder message = new StringBuilder(
          "The questionnaire response which you have selected for copying already exist(s) in the destination");
      // if same questionnaire already exist
      // check whether the qnaire resp in target is in deleted state
      if (destRvwQnaireResp.isDeletedFlag()) {
        qnaireRespPasteDataWrapper.setDestRespDeleted(true);
        // If deleted - undo delete
        destRvwQnaireResp.setDeletedFlag(false);
        qnaireRespCopyData.setUndoDelete(true);
      }
      message.append(". Do you like to proceed with copy?");
      message.append("\n");
      message.append("\n");
      message.append(FOR_THE_WORKPACKAGE);

      qnaireRespPasteDataWrapper.setCopiedQuesAlreadyAvailable(true);
      qnaireRespPasteDataWrapper.setIsCopiedQuesAlreadyAvailableMsg(message.toString());
      qnaireRespPasteDataWrapper.setQnaireRespWithSameQnaireInDest(destRvwQnaireResp);
    }
    else {
      qnaireRespPasteDataWrapper.setQnaireRespWithSameQnaireInDest(null);
    }
  }

  /*
   * This method loops through the secondary Qnaire Resp variants and checks if given Qnaire is available
   */
  private boolean isSameCopiedQnaireLinkedInDest(final Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap,
      final Long pidcVarId, final Long respId, final Long wpId) {
    for (Map.Entry<Long, Map<Long, Set<Long>>> varEntry : secondaryQnaireLinkMap.entrySet()) {
      if (CommonUtils.isEqual(varEntry.getKey(), pidcVarId)) {
        for (Map.Entry<Long, Set<Long>> respEntry : varEntry.getValue().entrySet()) {
          if (CommonUtils.isEqual(respEntry.getKey(), respId) && respEntry.getValue().contains(wpId)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean isSameQnaireInDest(final Long destVariantId, final Long destRespId, final Long destWpId,
      final PidcTreeNode srcPidcTreeNode) {
    return CommonUtils.isEqual(destVariantId, srcPidcTreeNode.getQnaireResp().getVariantId()) &&
        CommonUtils.isEqual(destRespId, srcPidcTreeNode.getParentNode().getA2lResponsibility().getId()) &&
        CommonUtils.isEqual(destWpId, srcPidcTreeNode.getParentNode().getA2lWorkpackage().getId());
  }

  /**
   * @param qnaireRespCopyData
   * @param srcPidcTreeNode
   * @param destPidcTreeNode
   * @throws ApicWebServiceException
   */
  private void copyPasteQnaireResponse(final Long destRespId, final Long destWpId, final PidcTreeNode destPidcTreeNode,
      final PidcTreeNode srcPidcTreeNode) {
    PidcVersion destPidcVers = destPidcTreeNode.getPidcVersion();
    // for handling No-Variant case
    PidcVariant destPidcVar = destPidcTreeNode.getPidcVariant();
    Long destPidcVarId = CommonUtils.isNull(destPidcVar) ? 0L : destPidcVar.getId();

    QnaireRespCopyData qnaireRespCopyData =
        getQnaireRespCopyData(destRespId, destWpId, destPidcVers, destPidcVar, destPidcVarId, srcPidcTreeNode);

    if (isSameCopiedQnaireLinkedInDest(srcPidcTreeNode.getQnaireResp().getSecondaryQnaireLinkMap(),
        srcPidcTreeNode.getPidcVariant().getId(), srcPidcTreeNode.getParentNode().getA2lResponsibility().getId(),
        srcPidcTreeNode.getParentNode().getA2lWorkpackage().getId()) &&
        isSameQnaireInDest(
            qnaireRespCopyData.getTargetPidcVariant() == null ? ApicConstants.NO_VARIANT_ID
                : qnaireRespCopyData.getTargetPidcVariant().getId(),
            qnaireRespCopyData.getTargetRespId(), qnaireRespCopyData.getTargetWpId(), srcPidcTreeNode)) {
      MessageDialogUtils.getErrorMessageDialog(COPY_PASTE_OF_QUESTIONNAIRE,
          "It is not possible to copy/paste 'Linked questionnaire response' to its parent itself. Kindly select different variant.");
    }
    else {
      // either copied qnResp is linked (destination is different variant) or non-linked questionnaire
      try {
        // service call to fetch data related to copy
        RvwQnaireRespCopyServiceClient rvwQnaireRespCpySrvClient = new RvwQnaireRespCopyServiceClient();
        QuesRespCopyDataWrapper quesRespCopyDataWrapper =
            rvwQnaireRespCpySrvClient.getDataForCopyQnaireRespValidation(destPidcVers.getId(), destPidcVarId, destWpId,
                destRespId, srcPidcTreeNode.getQnaireRespId(), srcPidcTreeNode.getPidcVersion().getId());

        // method that has validation and service call to copy/paste qnaire response
        copyPasteQnaireResponse(qnaireRespCopyData, rvwQnaireRespCpySrvClient, quesRespCopyDataWrapper);
      }
      catch (ApicWebServiceException | InvocationTargetException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
      catch (InterruptedException exp) {
        Thread.currentThread().interrupt();
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param destRespId
   * @param destWpId
   * @param destPidcVers
   * @param destPidcVar
   * @param destPidcVarId
   * @param qnaireRespCopyData
   * @param srcPidcTreeNode
   */
  private QnaireRespCopyData getQnaireRespCopyData(final Long destRespId, final Long destWpId,
      final PidcVersion destPidcVers, final PidcVariant destPidcVar, final Long destPidcVarId,
      final PidcTreeNode srcPidcTreeNode) {
    QnaireRespCopyData qnaireRespCopyData = new QnaireRespCopyData();

    qnaireRespCopyData.setTargetRespId(destRespId);
    qnaireRespCopyData.setTargetWpId(destWpId);
    qnaireRespCopyData.setTargetPidcVersion(destPidcVers);
    // for handling No-Variant case
    if (destPidcVarId > 0) {
      qnaireRespCopyData.setTargetPidcVariant(destPidcVar);
    }
    qnaireRespCopyData.setCopiedQnaireResp(srcPidcTreeNode.getQnaireResp());
    qnaireRespCopyData.setCopyOnlyWorkingSet(ICDMClipboard.getInstance().isCopyOnlyWorkingSet());

    return qnaireRespCopyData;
  }

  /**
   * @param qnaireRespCopyData
   * @param rvwQnaireRespCpySrvClient
   * @param quesRespCopyDataWrapper
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  private void copyPasteQnaireResponse(final QnaireRespCopyData qnaireRespCopyData,
      final RvwQnaireRespCopyServiceClient rvwQnaireRespCpySrvClient,
      final QuesRespCopyDataWrapper quesRespCopyDataWrapper)
      throws InvocationTargetException, InterruptedException {
    // Handling 'General question Equivalent' case(if the copied questionnaire is 'General question', then the
    // 'general
    // question equivalent flag' will be false)
    // General question response will not be available for some cases as per the common param configuration in the
    // database
    RvwQnaireResponse destGeneralQuesResp = quesRespCopyDataWrapper.getDestGeneralQuesResp();
    if (CommonUtils.isNotNull(destGeneralQuesResp) && !destGeneralQuesResp.isDeletedFlag() &&
        CommonUtils.isEqual(quesRespCopyDataWrapper.getIsCopiedQuesGnrlQuesEqvlnt(),
            CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue())) {
      boolean userConfmToDel = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Delete General Questionnaire",
          "The copied questionnaire " + qnaireRespCopyData.getCopiedQnaireResp().getName() +
              " is equivalent to General Questionnaire. So the destination will no longer need the general questionnaire. Do you want to remove the general questionnaire in destination?");
      if (userConfmToDel) {
        qnaireRespCopyData.setDestGeneralQuesResp(destGeneralQuesResp);
      }
    }

    RvwQnaireResponse destRvwQnaireResp = quesRespCopyDataWrapper.getQnaireRespWithSameQnaireInDest();
    if (CommonUtils.isEqual(quesRespCopyDataWrapper.getSrcPidcDivId(), quesRespCopyDataWrapper.getDestPidcDivId())) {
      ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, (final IProgressMonitor monitor) -> {

        monitor.beginTask("Copying Questionnaire Response...", 100);
        monitor.worked(20);
        try {
          pasteQnaireResponse(qnaireRespCopyData, rvwQnaireRespCpySrvClient, destRvwQnaireResp);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });

      if (quesRespCopyDataWrapper.isSameCopiedQnaireLinkedInDest()) {
        MessageDialogUtils.getWarningMessageDialog("ICDM Warning",
            "There’s a linked questionnaire response existing in the target which has a higher priority than the questionnaire response you have copied. Please unlink the questionnaire response if you want to use the copied one instead.");

      }
    }
    else {
      MessageDialogUtils.getErrorMessageDialog("Error: Copy/Paste Questionnaire Response",
          "Copy/Paste of questionnaire response is allowed only within the same division. Kindly check whether both the source and destination PIDC's 'iCDM Questionnaire Config' attribute value are same before copy.");
    }
  }

  /**
   * @param qnaireRespCopyData
   * @param rvwQnaireRespCpySrvClient
   * @param destRvwQnaireResp
   * @param isCopiedQuesGenQuesEquivalentFlag
   * @throws ApicWebServiceException
   */
  private void pasteQnaireResponse(final QnaireRespCopyData qnaireRespCopyData,
      final RvwQnaireRespCopyServiceClient rvwQnaireRespCpySrvClient, final RvwQnaireResponse destRvwQnaireResp)
      throws ApicWebServiceException {
    // if copied response does not exist in destination
    if (CommonUtils.isNull(destRvwQnaireResp)) {
      rvwQnaireRespCpySrvClient.createQnaireResp(qnaireRespCopyData);
    }
    else {
      StringBuilder message = new StringBuilder(
          "The questionnaire response which you have selected for copying is already exist in the destination");
      // if same questionnaire already exist
      // check whether the qnaire resp in target is in deleted state
      if (destRvwQnaireResp.isDeletedFlag()) {
        message.append(" but in deleted state");
        // If deleted - undo delete
        destRvwQnaireResp.setDeletedFlag(false);
        qnaireRespCopyData.setUndoDelete(true);
      }
      message.append(". Do you like to proceed with copy?");

      boolean canCopy = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Confirm to Copy Questionnaire Response",
          message.toString());
      if (canCopy) {
        // If user selects 'yes', then proceed with copy(update)
        qnaireRespCopyData.setExistingTargetQnaireResp(destRvwQnaireResp);
        rvwQnaireRespCpySrvClient.updateQnaireResp(qnaireRespCopyData);
      }
    }
  }

  /**
   * Method to display 100% cdfx response action
   *
   * @param manager as input
   * @param pidcTreeNode as input
   */
  public void setCdfxExport(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    final Action moveAction = new Action() {

      @Override
      public void run() {
// create wizard instance
        java.util.List<WpRespModel> wpRespModelList = new ArrayList<>();
        boolean containPreSelctedWP = false;
        if ((CommonUtils.isNotNull(pidcTreeNode.getA2lWorkpackage())) &&
            (CommonUtils.isNotNull(pidcTreeNode.getA2lResponsibility()))) {
          WpRespModel wpRespModel = new WpRespModel();
          wpRespModel.setWpName(pidcTreeNode.getA2lWorkpackage().getName());
          containPreSelctedWP = true;
          wpRespModel.setWpRespName(pidcTreeNode.getA2lResponsibility().getAliasName());
          wpRespModel.setA2lWpId(pidcTreeNode.getA2lWorkpackage().getId());
          wpRespModel.setA2lResponsibility(pidcTreeNode.getA2lResponsibility());
          wpRespModelList.add(wpRespModel);
        }


        CDFXExportWizard wizard =
            new CDFXExportWizard(pidcTreeNode, getCdfxReadinessConditionStr(), wpRespModelList, containPreSelctedWP);

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
    if ((CommonUtils.isNotNull(pidcTreeNode.getPidcA2l()) && !pidcTreeNode.getPidcA2l().isActive()) ||
        (CommonUtils.isNotNull(pidcTreeNode.getPidcVariant()) && pidcTreeNode.getPidcVariant().isDeleted())) {
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

}
