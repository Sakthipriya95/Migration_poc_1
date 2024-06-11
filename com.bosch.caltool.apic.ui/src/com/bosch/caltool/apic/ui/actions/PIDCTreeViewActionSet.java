package com.bosch.caltool.apic.ui.actions;


/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditor;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.IPIDCTreeViewActionSet;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.FavoritesViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This class implements functions related to PIDCcardtree
 *
 * @author dmo5cob ICDM-143
 */
public class PIDCTreeViewActionSet implements IPIDCTreeViewActionSet {


  /**
   * To open the PIDCCard on double click {@inheritDoc}
   */
  @Override
  public IDoubleClickListener getDoubleClickListener() {

    return event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      final Object selectedNode = selection.getFirstElement();

      if (selectedNode instanceof PidcVersion) {
        openPIDCEditor(selectedNode);
      }
      else if (selectedNode instanceof PidcTreeNode) {
        PidcTreeNode treeNode = (PidcTreeNode) selectedNode;
        if ((treeNode.getNodeType() == PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
            (treeNode.getNodeType() == PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
          OpenNewPidcAction openAction = new OpenNewPidcAction(treeNode.getPidcVersion(), "Open PIDC Version");
          openAction.run();
        }
      }
    };
  }

  /**
   * @param selectedNode
   */
  private void openPIDCEditor(final Object selectedNode) {
    final PidcVersion selectedPid = (PidcVersion) selectedNode;

    // log the details in log file while opening
    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Opening PID Card " + selectedPid.getName() + " in editor ....");
    }

    final PIDCActionSet actionset = new PIDCActionSet();
    // Open the pidc editor with selected pidcard
    final PIDCEditor openPIDCEditor = actionset.openPIDCEditor(selectedPid, false);
    // ICDM-2182
    if (CommonUtils.isNotNull(openPIDCEditor)) {
      // set focus to the editor opened
      openPIDCEditor.setFocus();
      CDMLogger.getInstance().debug("PID Card opened in the editor");
    }
  }

  /**
   * @param manager
   * @param pidVersions
   * @param viewer
   */
  public void setAddToComparePIDCAction(final IMenuManager manager, final IStructuredSelection selection) {
    final Action createComparePIDCAction = new Action() {

      @Override
      public void run() {
        // open compare pidc
        addToOpenPIDCCompareEditor(selection);
      }
    };
    createComparePIDCAction.setText("Add to current Compare PIDC Editor");
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_ADD_16X16);
    createComparePIDCAction.setImageDescriptor(imageDesc);
    manager.add(createComparePIDCAction);

  }

  /**
   * Add pidc to already opened compare editor
   *
   * @param selection
   */
  protected void addToOpenPIDCCompareEditor(final IStructuredSelection selection) {
    IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    List<IProjectObject> prjList = new ArrayList<>();
    if (activeEditor instanceof PIDCCompareEditor) {
      if (selection.getFirstElement() instanceof PidcTreeNode) {
        PidcTreeNode node = (PidcTreeNode) selection.getFirstElement();
        // add active/other version pidc to list
        if (node.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
            node.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
          prjList.add(node.getPidcVersion());
        }
      }
      PIDCCompareEditor pidcCompareEditor = (PIDCCompareEditor) activeEditor;
      pidcCompareEditor.getComparePIDCPage().addToExistingCompareEditor(prjList);
    }
  }


  /**
   * Add right click menu to pidcard {@inheritDoc}
   */
  @Override
  public void onRightClickOfPidcTreeNode(final IMenuManager manager, final TreeViewer viewer,
      final PidcTreeNode pidcTreeNode) {

    IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

    final PIDCActionSet actionSet = new PIDCActionSet();

    if (!selection.isEmpty()) {
      if (selection.size() == 1) {

        if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
            (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION))) {

          /* Add to Favorite action */
          actionSet.setAddFavoritesAction(manager, pidcTreeNode.getPidcVersion());

          final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
          if ((activePage != null) &&
              activePage.getActivePart().getSite().getId().equalsIgnoreCase(FavoritesViewPart.class.getName())) {
            actionSet.setRemoveFavoritesAction(manager, pidcTreeNode);
          }
          manager.add(new Separator());

          // Copy link of PIDC to Clip Board
          new PIDCActionSet().copytoClipBoard(manager, pidcTreeNode.getPidcVersion());

          // Send link of PIDC as email
          new PIDCActionSet().sendPidcVersionLinkInOutlook(manager, pidcTreeNode.getPidcVersion());

          manager.add(new Separator());
          // Create shortcut of a PIDC
          new PIDCActionSet().createPidcShortcutAction(manager, pidcTreeNode.getPidcVersion());

          // copy name to clipboard
          actionSet.addCopyNameToClipboard(manager, pidcTreeNode);
          /* Add a separator */
          manager.add(new Separator());

          /* Start webflow job Action */
          PidcVariantServiceClient variantServiceClient = new PidcVariantServiceClient();
          Map<Long, PidcVariant> variantMap;
          try {
            variantMap = variantServiceClient.getVariantsForVersion(pidcTreeNode.getPidcVersion().getId(), false);


            if (!pidcTreeNode.getPidcVersion().isDeleted() &&
                (variantMap.isEmpty() || isAllVariantsDeleted(variantMap)) && ("Y".equalsIgnoreCase(
                    new CommonDataBO().getParameterValue(CommonParamKey.WEB_FLOW_JOB_CREATION_ACTIVE)))) {
              /* Add a separator */
              manager.add(new Separator());
              /* Start webflow job Action */
              actionSet.setPidcWebflowJobAction(manager, pidcTreeNode.getPidcVersion(),
                  ApicUiConstants.START_WEB_FLOW_JOB);
            }
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }

          // menu seperator
          manager.add(new Separator());

          if (!pidcTreeNode.getPidcVersion().isDeleted()) {
            actionSet.setExportAction(manager, pidcTreeNode.getPidcVersion(), ApicUiConstants.EXPORT_ACTION);
            actionSet.addImportPidcAction(viewer, manager, pidcTreeNode.getPidcVersion());
          }
          // menu seperator
          manager.add(new Separator());
          actionSet.setTransfer2vCDMAction(manager, pidcTreeNode.getPidcVerInfo());

          manager.add(new Separator());
          // add to scratchpad
          actionSet.addToScratchPad(manager, pidcTreeNode);
          final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();
          copyActionSet.setCopyAction(manager, pidcTreeNode.getPidcVersion(), ApicUiConstants.COPY, null);
          // menu seperator
          manager.add(new Separator());


          if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
            actionsForPidcActVer(manager, pidcTreeNode, actionSet);
          }
          else {
            actionSet.setActiveVerAction(manager, pidcTreeNode, ApicUiConstants.SET_ACTIVE_VER);
          }
          // menu seperator
          manager.add(new Separator());

        }
        else if (pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE)) {
          actionsForLvlAttrNode(manager, viewer, pidcTreeNode);
        }
      }
      addCompareActions(manager, selection);
    }


  }

  /**
   * @param manager
   * @param viewer
   * @param pidcTreeNode
   */
  private void actionsForLvlAttrNode(final IMenuManager manager, final TreeViewer viewer,
      final PidcTreeNode pidcTreeNode) {
    ApicDataBO apicDataBO = new ApicDataBO();
    if (pidcTreeNode.getLevel() == apicDataBO.getPidcStructMaxLvl()) {
      PIDCCreationWizardOpnAction pidcCreationWizardOpnAction = new PIDCCreationWizardOpnAction(pidcTreeNode, viewer);
      manager.add(pidcCreationWizardOpnAction);
      // copy existing pidc
      final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();
      /* Add Paste PIDC Action */
      copyActionSet.setPasteAction(manager, pidcTreeNode, ApicUiConstants.PASTE, viewer, null);
    }
  }

  /**
   * @param manager
   * @param pidcTreeNode
   * @param actionSet
   * @param viewer
   */
  private void actionsForPidcActVer(final IMenuManager manager, final PidcTreeNode pidcTreeNode,
      final PIDCActionSet actionSet) {
    actionSet.addRenamePidcAction(manager, pidcTreeNode.getPidcVersion(), pidcTreeNode.getPidc());

    if (pidcTreeNode.getPidc().isDeleted()) {
      /* UnDelete Action */
      actionSet.setDeleteAction(manager, pidcTreeNode.getPidcVersion(), pidcTreeNode.getPidc(),
          ApicUiConstants.UN_DELETE_PIDC_ACTION);
    }
    else {
      /* Delete Action */
      actionSet.setDeleteAction(manager, pidcTreeNode.getPidcVersion(), pidcTreeNode.getPidc(),
          ApicUiConstants.DELETE_PIDC_ACTION);
    }
  }


  /**
   * @param manager
   * @param viewer
   * @param actionSet
   */
  private void setCompareAction(final IMenuManager manager, final IStructuredSelection selection) {
    List<IProjectObject> compareList = new ArrayList<>();
    if (!selection.isEmpty()) {

      for (Object selObj : selection.toList()) {
        if (selection.getFirstElement() instanceof PidcTreeNode) {
          if (((PidcTreeNode) selObj).getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
              ((PidcTreeNode) selObj).getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
            compareList.add(((PidcTreeNode) selObj).getPidcVersion());
          }
        }

      }


      final Action compareAction = new Action() {

        @Override
        public void run() {
          final PIDCCompareEditorInput input = new PIDCCompareEditorInput(compareList, null);
          try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
                PIDCCompareEditor.EDITOR_ID);
          }
          catch (PartInitException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }

        }
      };

      compareAction.setText("Compare PIDC");

      ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_16X16);
      compareAction.setImageDescriptor(imageDesc);
      manager.add(compareAction);


    }

  }


  /**
   * @param pidcVersion
   * @return
   */
  private boolean isAllVariantsDeleted(final Map<Long, PidcVariant> variantMap) {
    for (PidcVariant pidcVar : variantMap.values()) {
      if (!pidcVar.isDeleted()) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param manager
   * @param viewer
   * @param actionSet
   */
  private void addCompareActions(final IMenuManager manager, final IStructuredSelection selection) {

    boolean allPidcFlag = true;
    if (!selection.isEmpty() && (selection.size() >= 1)) {
      Iterator<?> pidcSet = selection.iterator();
      while (pidcSet.hasNext()) {
        Object selObj = pidcSet.next();
        if ((selObj instanceof PidcTreeNode) &&
            !(((PidcTreeNode) selObj).getNodeType().equals(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
                ((PidcTreeNode) selObj).getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION))) {
          allPidcFlag = false;
          break;

        }
      }
    }
    // For single pidc selection
    if (allPidcFlag && !selection.isEmpty()) {
      manager.add(new Separator());
      if (selection.size() >= 2) {
        setCompareAction(manager, selection);
      }
      IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
      if (activeEditor instanceof PIDCCompareEditor) {
        PIDCCompareEditorInput editorInput = ((PIDCCompareEditor) activeEditor).getEditorInput();
        if (editorInput.getCompareObjs().get(0) instanceof PidcVersion) {
          setAddToComparePIDCAction(manager, selection);
        }
      }
    }
  }


  /**
   * Add Key listener {@inheritDoc}
   */
  @Override
  public void getKeyListenerToViewer(final Object selectedObj, final int keyCode, final int stateMask,
      final TreeViewer viewer) {

    // // ICDM-705
    final PIDCActionSet actionset = new PIDCActionSet();
    final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();
    if (selectedObj != null) {
      if ((selectedObj instanceof PidcTreeNode) &&
          (((PidcTreeNode) selectedObj).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION) ||
              ((PidcTreeNode) selectedObj).getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION))) {
        renameDeletePIDC(selectedObj, keyCode, actionset);
      }
      // ctrl shortcut can be used to copy paste the pidcard
      if (stateMask == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_CTRL) {
        copyPastePIDCard(selectedObj, keyCode, viewer, copyActionSet);
      }
    }
  }

  /**
   * @param selectedObj
   * @param keyCode
   * @param viewer
   * @param copyActionSet
   */
  private void copyPastePIDCard(final Object selectedObj, final int keyCode, final TreeViewer viewer,
      final PIDCCopyActionSet copyActionSet) {
    if (keyCode == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_COPY) {
      // copy key pressed
      ICDMClipboard.getInstance().setCopiedObject(((PidcTreeNode) selectedObj).getPidcVersion());
    }
    else if (keyCode == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_PASTE) {
      // paste key pressed
      // Get the user info
      CurrentUserBO currUserBo = new CurrentUserBO();
      boolean canCreatePidc = false;
      try {
        canCreatePidc = currUserBo.canCreatePIDC();
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      // pidcard should be modifiable by the owner
      if (canCreatePidc && !((PidcVersion) ICDMClipboard.getInstance().getCopiedObject()).isDeleted() &&
          ICDMClipboard.getInstance().isPasteAllowed(ICDMClipboard.getInstance().getCopiedObject()) &&
          isPasteAllowedInThisLevel(selectedObj)) {
        copyActionSet.pasteAction(selectedObj, viewer, null);
      }
      else {
        // if the user does not have access to paste
        CDMLogger.getInstance().warnDialog("Paste Not Allowed!", Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param selectedObj
   * @return true if the selected tree node is a valid level for paste
   */
  private boolean isPasteAllowedInThisLevel(final Object selectedObj) {
    if (selectedObj instanceof PidcTreeNode) {
      PidcTreeNode pidTreeNode = (PidcTreeNode) selectedObj;
      ApicDataBO apicDataBO = new ApicDataBO();
      if (apicDataBO.getPidcStructMaxLvl() == pidTreeNode.getLevel()) {
        return true;
      }
    }
    return false;
  }

  /**
   * ICDM-705
   *
   * @param selectedObj Selected Object
   * @param keyCode Key Code
   * @param actionset PIDCActionSet
   */
  private void renameDeletePIDC(final Object selectedObj, final int keyCode, final PIDCActionSet actionset) {
    final PidcVersion pidcVer = ((PidcTreeNode) selectedObj).getPidcVersion();
    // Get the user info
    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUserBo = new CurrentUserBO();
    Pidc selPidc = ((PidcTreeNode) selectedObj).getPidc();
    NodeAccess currUserNodeAccess = null;
    try {
      currUserNodeAccess = currUserBo.getNodeAccessRight(selPidc.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    // pidcard should be modifiable by the owner
    if ((currUserNodeAccess != null) && currUserNodeAccess.isOwner() && !pidcVer.isDeleted()) {
      // checking for access rights
      if (keyCode == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_RENAME) {
        renameKeyListener(actionset, pidcVer, apicBo, currUserBo, selPidc);
      }
      else if (keyCode == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_DELETE) {
        deleteKeyListener(actionset, pidcVer, apicBo, currUserBo, selPidc);
      }
    }
    else {
      if ((keyCode == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_RENAME) ||
          (keyCode == com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.KEY_DELETE)) {
        // if the user does not have access to delete or rename
        CDMLogger.getInstance().warnDialog("Insufficient privileges to do this operation!", Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param actionset
   * @param pidcVer
   * @param apicBo
   * @param currUserBo
   * @param selPidc
   */
  private void deleteKeyListener(final PIDCActionSet actionset, final PidcVersion pidcVer, final ApicDataBO apicBo,
      final CurrentUserBO currUserBo, final Pidc selPidc) {
    // if delete key is pressed
    try {
      if (!apicBo.isPidcUnlockedInSession(pidcVer) &&
          (currUserBo.hasApicWriteAccess() && currUserBo.hasNodeWriteAccess(selPidc.getId()))) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(pidcVer);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    if (apicBo.isPidcUnlockedInSession(pidcVer)) {
      // If PIDc is unlocked in session
      actionset.deleteAction(selPidc, ApicUiConstants.DELETE_PIDC_ACTION);
    }
  }

  /**
   * @param actionset
   * @param pidcVer
   * @param apicBo
   * @param currUserBo
   * @param selPidc
   */
  private void renameKeyListener(final PIDCActionSet actionset, final PidcVersion pidcVer, final ApicDataBO apicBo,
      final CurrentUserBO currUserBo, final Pidc selPidc) {
    // if rename key is pressed
    // Check if the pidc is unlocked in session
    try {
      if (!apicBo.isPidcUnlockedInSession(pidcVer) &&
          (currUserBo.hasApicWriteAccess() && currUserBo.hasNodeWriteAccess(selPidc.getId()))) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(pidcVer);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    if (apicBo.isPidcUnlockedInSession(pidcVer)) {
      // If PIDc is unlocked in session, rename pidc
      actionset.renamePIDC(pidcVer, selPidc);
    }
  }


  /**
   * Right click menu for APRJ {@inheritDoc} DMR1COB
   */
  @Override
  public void onRightClickOfAPRJnew(final Set<PidcVersion> pidcVerSet) {
    final PIDCActionSet actionset = new PIDCActionSet();
    // open all the pidcards which has APRJ
    for (PidcVersion selPidcVer : pidcVerSet) {
      // open the editor with that PIDC
      actionset.openPIDCEditor(selPidcVer);

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openFunctionSelectionDialog(final List<Function> funcList, final String paramName) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createRuleAction(final IMenuManager menuManagr, final Object selectedObj) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openA2LFile(final Long pidcA2lId) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openReviewResult(final CDRReviewResult cdrResult, final String paramName, final Long variantId) {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void openReviewResult(final RvwVariant rvwResult, final String paramName) {
    // TODO Auto-generated method stub

  }

  // Method to compare pidc,pidc variant and pidc sub variant from scratch pad view
  /**
   * {@inheritDoc}
   */
  @Override
  public void openComparePidcEditor(final IMenuManager manager, final List<IProjectObject> compareList) {
    if (!compareList.isEmpty()) {
      PIDCCompareEditorInput input = null;
      if (compareList.get(0) instanceof PidcVariant) {
        ProjectHandlerInit handlerInit = getProjBOForPidVar(((PidcVariant) compareList.get(0)).getPidcVersionId());
        if (null != handlerInit) {
          input = new PIDCCompareEditorInput(compareList, handlerInit.getProjectObjectBO());
        }
      }
      else if (compareList.get(0) instanceof PidcSubVariant) {
        ProjectHandlerInit handlerInit =
            getProjBOForPidSubVar(((PidcSubVariant) compareList.get(0)).getPidcVersionId());
        if (null != handlerInit) {
          input = new PIDCCompareEditorInput(compareList, handlerInit.getProjectObjectBO());
        }
      }
      else if (compareList.get(0) instanceof PidcVersion) {
        input = new PIDCCompareEditorInput(compareList, null);
      }
      // open compare editor for pid/pidc var/pidc subvar
      try {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
            PIDCCompareEditor.EDITOR_ID);
      }
      catch (PartInitException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }

    }
  }

  /**
   * @param pidcVersionId Long
   * @return ProjectHandlerInit
   */
  // Method to initialise PidcVersionBo for Pidc Sub Variant compare from scratch pad view
  public ProjectHandlerInit getProjBOForPidSubVar(final Long pidcVersionId) {
    PidcDataHandler dataHandler = new PidcDataHandler();
    PidcDetailsLoader loader = new PidcDetailsLoader(dataHandler);
    dataHandler = loader.loadDataModel(pidcVersionId);
    return new ProjectHandlerInit(dataHandler.getPidcVersionInfo().getPidcVersion(),
        dataHandler.getPidcVersionInfo().getPidcVersion(), dataHandler, ApicConstants.LEVEL_PIDC_VERSION);

  }

  /**
   * @param pidcVersionId Long
   * @return ProjectHandlerInit
   */
  // Method to initilaise PidcVersionBo for PidcVariant Compare from Scratch pad View
  public ProjectHandlerInit getProjBOForPidVar(final Long pidcVersionId) {
    PidcDataHandler dataHandler = new PidcDataHandler();
    PidcDetailsLoader loader = new PidcDetailsLoader(dataHandler);
    dataHandler = loader.loadDataModel(pidcVersionId);
    return new ProjectHandlerInit(dataHandler.getPidcVersionInfo().getPidcVersion(),
        dataHandler.getPidcVersionInfo().getPidcVersion(), dataHandler, ApicConstants.LEVEL_PIDC_VERSION);

  }
}
