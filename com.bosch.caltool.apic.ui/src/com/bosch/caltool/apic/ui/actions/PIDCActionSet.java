/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AddSubVariantDialog;
import com.bosch.caltool.apic.ui.dialogs.AddVariantDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCAttrValueEditDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCExportDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCRenameDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCVariantValueDialog;
import com.bosch.caltool.apic.ui.dialogs.PIDCVersionDetailsDialog;
import com.bosch.caltool.apic.ui.dialogs.PasswordDialog;
import com.bosch.caltool.apic.ui.dialogs.PidcAddStructAttrDialog;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditor;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.editors.pages.FocusMatrixPage;
import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.editors.pages.PidcVersionsPage;
import com.bosch.caltool.apic.ui.jobs.PidcVcdmTransferJob;
import com.bosch.caltool.apic.ui.listeners.PIDCDetailsPageListener;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.PIDCDetailsPage;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeActionHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PidcWebFlowHandler;
import com.bosch.caltool.icdm.client.bo.apic.pidc.ProjectAttributesMovementModel;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixVersionClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixVersionClientBO.FM_REVIEW_STATUS;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.SendObjectLinkAction;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.FavoritesViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.FocusMatrixVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcDetStructureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.ProjectAttributesUpdationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.FavouritesServiceClient;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * @author dmo5cob This class has the set of action events for the tree view in PIDC Perspective
 */
public class PIDCActionSet {

  /**
   * String constant for Open pidc version
   */
  private static final String OPEN_PIDC_VERSION = "Open PIDC Version";
  /**
   *
   */
  private static final String VARIANT_ALREADY_MOVED = "Variant already moved";
  /**
   * Action instance to create new version
   */
  private Action newVersionAction;
  /**
   * Action instance to lock a version
   */
  private Action lockAction;
  /**
   * Action instance to edit a version
   */
  private Action editAction;
  /**
   * Map<Long, List<PIDCDetailsPageListener>>
   */
  private static Map<Long, List<PIDCDetailsPageListener>> pidcDetailsPageListeners = new HashMap<>();

  private static final String MOVE_TO_COMMON = "To Common";

  private static final String MOVE_TO_VARIANT = "To Variant";

  private static final String MOVE_TO_SUB_VARIANT = "To SubVariant";


  /**
   * Icdm-1115 new Constant for Move to Common of Variant attr defined as Virtual Structure
   */
  private static final String VIRTUAL_NODE_ERR =
      "The attribute is used as a Virtual Node. Please delete the Virtual Node first.";


  /**
   * Error message to display when predefined attributes are moved
   */
  private static final String PREDEF_ATTR_ERROR = "This attribute is part of a grouped attribute. It cannot be moved.";

  /**
   * the pver name error message
   */
  private static final String MODIFY_PVER_NAME_ERROR =
      "Cannot modify PVER name since a2l files are mapped to existing PVER name";
  /**
   * the var name in CDM system attr modify error message
   */
  private static final String MOVE_VARNAME_CDM_ERROR =
      "This attribute is relevant on variant level. It cannot not be moved to PIDC / Sub-Variant level.!";

  /**
   * Transfer to VCDM error msg for inactive versions of pidc
   */

  /**
   * Action for adding PID to favorites
   *
   * @param manager MenuManager
   * @param pidcVersion PidcVersion
   * @param addFav add favourite
   * @param viewer TreeViewer
   */
  public void setImportPIDCAction(final IMenuManager manager, final PidcVersion pidcVersion, final String addFav,
      final TreeViewer viewer) {

    // creates the import pidc action object
    ImportPIDCAction importPIDCAction = new ImportPIDCAction(viewer.getControl().getShell(), pidcVersion);
    importPIDCAction.setText(addFav);
    // Image desciptor for import pidc action
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.IMPORT_PID_28X30);
    importPIDCAction.setImageDescriptor(imageDesc);
    manager.add(importPIDCAction);
  }


  /**
   * @param menuMgr MenuManager
   * @param pidcAttr PidcVersionAttribute
   * @param pidcVer PidcVersion
   * @param pidcPage PIDCAttrPage
   */
  public void addMoveToVariantMenu(final MenuManager menuMgr, final PidcVersionAttribute pidcAttr,
      final PidcVersion pidcVer, final PIDCAttrPage pidcPage) {


    if (pidcAttr.isAtChildLevel()) {
      CDMLogger.getInstance().info(VARIANT_ALREADY_MOVED, Activator.PLUGIN_ID);

    }
    else {
      Action moveToVariantAction = new Action(ApicUiConstants.MOVE_TO_VARIANT, SWT.NONE) {

        @Override
        public void run() {
          moveProjAttrToVar(pidcAttr, pidcVer, pidcPage);
        }
      };

      moveToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
      menuMgr.add(moveToVariantAction);
    }
  }

  /**
   * @param pidcAttr
   * @param pidcVer
   * @param pidcPage
   */
  private void moveProjAttrToVar(final PidcVersionAttribute pidcAttr, final PidcVersion pidcVer,
      final PIDCAttrPage pidcPage) {
    // check if the pidc is unlocked in session
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    try {
      if (!apicBo.isPidcUnlockedInSession(pidcVer) && currUser.hasNodeWriteAccess(pidcVer.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        boolean resultFlag = pidcActionSet.showUnlockPidcDialog(pidcVer);

        if (resultFlag) {
          moveToVar(pidcAttr, pidcVer, pidcPage.getPidcVersionBO());
        }
      }
      else {
        moveToVar(pidcAttr, pidcVer, pidcPage.getPidcVersionBO());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcAttr
   */
  private void showMoveDownNotAllowed(final PidcVersionAttribute pidcAttr) {
    MessageDialogUtils.getInfoMessageDialog("Move to Variant",
        "The attribute : " + pidcAttr.getName() + " , should not be moved to the variant level.");
  }

  /**
   * @param pidcAttr PidcVersionAttribute
   * @param pidcVer PidcVersion
   * @param pidcVersionBO PIDC Page
   */
  public void moveToVar(final PidcVersionAttribute pidcAttr, final PidcVersion pidcVer,
      final PidcVersionBO pidcVersionBO) {
    PidcVersionAttributeBO handler = new PidcVersionAttributeBO(pidcAttr, pidcVersionBO);
    if (!CommonUtils.isNotEmpty(pidcVersionBO.getPidcDataHandler().getVariantMap())) {
      MessageDialogUtils.getInfoMessageDialog("Variant Info", "No variants defined for this project ID card");
    }
    else {
      List<IProjectAttribute> projAttr = new ArrayList<>();
      projAttr.add(pidcAttr);
      if (handler.canMoveDown() && validateIfPredef(projAttr, pidcVersionBO, MOVE_TO_VARIANT)) {
        if (handler.isVisible() && handler.isModifiable() && pidcVersionBO.isModifiable()) {

          ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcVersionBO);
          moveModel.setPidcVersion(pidcVer);
          moveModel.getPidcAttrsToBeMovedDown().put(pidcAttr.getAttrId(), pidcAttr);
          executeMoveCommand(moveModel);
        }
        else {
          showEditNotAllowedDialog(true);
        }
      }
      else {
        showMoveDownNotAllowed(pidcAttr);
      }
    }
  }

  /**
   * @param moveModel
   */
  private void executeMoveCommand(final ProjectAttributesMovementModel moveModel) {
    ProjectAttributesUpdationServiceClient client = new ProjectAttributesUpdationServiceClient();
    try {
      ProjectAttributesUpdationModel updationModel = moveModel.loadUpdationModel();
      client.updatePidcAttrs(updationModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcAttr
   * @param pidcVersionBO
   * @return
   */
  private boolean validateIfPredef(final List<IProjectAttribute> pidcAttrList, final PidcVersionBO pidcVersionBO,
      final String move) {

    for (IProjectAttribute pidcAttr : pidcAttrList) {
      if ((null != pidcAttr.getId()) && checkIfAttrIsPredef(pidcAttr, pidcVersionBO, move)) {
        CDMLogger.getInstance().errorDialog(PREDEF_ATTR_ERROR, Activator.PLUGIN_ID);
        return false;
      }
    }

    return true;
  }


  /**
   * @param attrId
   * @param predefAttrMap
   * @return
   */
  private boolean checkIfAttrIsPredef(final IProjectAttribute selectedAttr, final PidcVersionBO pidcVersionBO,
      final String move) {
    Long grpAttrValId = null;
    Long grpAttrId = null;
    boolean flag = false;

    for (Entry<Long, Map<Long, PredefinedAttrValue>> entryMap : pidcVersionBO.getPidcDataHandler().getPreDefAttrValMap()
        .entrySet()) {
      for (Entry<Long, PredefinedAttrValue> entry : entryMap.getValue().entrySet()) {
        if (entry.getValue().getPredefinedAttrId().equals(selectedAttr.getAttrId())) {
          grpAttrValId = entryMap.getKey();
          flag = true;
          break;
        }
      }
      if (flag) {
        break;
      }
    }

    if (null != grpAttrValId) {
      AttributeValue attributeValue = pidcVersionBO.getPidcDataHandler().getAttributeValue(grpAttrValId);
      grpAttrId = null != attributeValue ? attributeValue.getAttributeId() : null;
    }

    return checkIfPredef(selectedAttr, pidcVersionBO, move, grpAttrId);
  }


  /**
   * @param selectedAttr
   * @param pidcVersionBO
   * @param move
   * @param grpAttrId
   * @return
   */
  private boolean checkIfPredef(final IProjectAttribute selectedAttr, final PidcVersionBO pidcVersionBO,
      final String move, final Long grpAttrId) {
    boolean isPredef = false;
    for (IProjectAttribute projAttr : pidcVersionBO.getPredefAttrGrpAttrMap().keySet()) {
      isPredef = checkForPredefBasedOnMove(selectedAttr, pidcVersionBO, move, grpAttrId, projAttr);
      if (isPredef) {
        break;
      }
    }
    return isPredef;
  }


  /**
   * @param selectedAttr
   * @param pidcVersionBO
   * @param move
   * @param grpAttrId
   * @param isPredef
   * @param projAttr
   * @return
   */
  private boolean checkForPredefBasedOnMove(final IProjectAttribute selectedAttr, final PidcVersionBO pidcVersionBO,
      final String move, final Long grpAttrId, final IProjectAttribute projAttr) {
    boolean isPredef = false;
    if (selectedAttr.getAttrId().longValue() == projAttr.getAttrId().longValue()) {
      switch (move) {
        case MOVE_TO_VARIANT:
          if (checkIfSubVarIdOfSlctedAttrCointainsGrpAttrId(selectedAttr, pidcVersionBO, grpAttrId)) {
            break;
          }
          isPredef = checkIfPredefForMoveToVar(projAttr, selectedAttr);
          break;
        case MOVE_TO_SUB_VARIANT:
          isPredef = checkPredefForMoveToSubVariant(selectedAttr, projAttr);
          break;
        case MOVE_TO_COMMON:
          if (checkIfvarIdOfSlctedAttrContainsGrpAttrId(selectedAttr, pidcVersionBO, grpAttrId)) {
            break;
          }
          isPredef = checkIfPredefForCommon(selectedAttr, projAttr);
          break;
        default:
          break;
      }
    }
    return isPredef;
  }


  /**
   * @param selectedAttr
   * @param projAttr
   * @return
   */
  private boolean checkIfPredefForCommon(final IProjectAttribute selectedAttr, final IProjectAttribute projAttr) {
    return ((selectedAttr instanceof PidcVersionAttribute) && (projAttr instanceof PidcVariantAttribute)) ||
        checkPredefForMoveToSubVariant(selectedAttr, projAttr);
  }


  /**
   * @param selectedAttr
   * @param pidcVersionBO
   * @param grpAttrId
   * @return
   */
  private boolean checkIfvarIdOfSlctedAttrContainsGrpAttrId(final IProjectAttribute selectedAttr,
      final PidcVersionBO pidcVersionBO, final Long grpAttrId) {
    return (selectedAttr instanceof PidcVariantAttribute) &&
        (null != ((PidcVariantAttribute) selectedAttr).getVariantId()) && !pidcVersionBO.getPidcDataHandler()
            .getVariantAttributeMap().get(((PidcVariantAttribute) selectedAttr).getVariantId()).containsKey(grpAttrId);
  }


  /**
   * @param selectedAttr
   * @param projAttr
   * @return
   */
  private boolean checkPredefForMoveToSubVariant(final IProjectAttribute selectedAttr,
      final IProjectAttribute projAttr) {
    return (selectedAttr instanceof PidcVariantAttribute) && (projAttr instanceof PidcVariantAttribute);
  }


  /**
   * @param selectedAttr
   * @param pidcVersionBO
   * @param grpAttrId
   * @return
   */
  private boolean checkIfSubVarIdOfSlctedAttrCointainsGrpAttrId(final IProjectAttribute selectedAttr,
      final PidcVersionBO pidcVersionBO, final Long grpAttrId) {
    return (selectedAttr instanceof PidcSubVariantAttribute) &&
        (null != ((PidcSubVariantAttribute) selectedAttr).getSubVariantId()) &&
        !pidcVersionBO.getPidcDataHandler().getSubVariantAttributeMap()
            .get(((PidcSubVariantAttribute) selectedAttr).getSubVariantId()).containsKey(grpAttrId);
  }


  /**
   * @param projAttr
   * @param selectedAttr
   * @return true if selected attr can be moved to variant level
   */
  private boolean checkIfPredefForMoveToVar(final IProjectAttribute projAttr, final IProjectAttribute selectedAttr) {
    return (isPidcVersAttOrSubvarAttr(projAttr, selectedAttr) || isVarIdSame(projAttr, selectedAttr));
  }


  /**
   * @param projAttr
   * @param selectedAttr
   * @return
   */
  private boolean isVarIdSame(final IProjectAttribute projAttr, final IProjectAttribute selectedAttr) {
    return (selectedAttr instanceof PidcVariantAttribute) && (projAttr instanceof PidcSubVariantAttribute) &&
        (((PidcVariantAttribute) selectedAttr).getVariantId().longValue() == ((PidcSubVariantAttribute) projAttr)
            .getVariantId().longValue());
  }


  /**
   * @param projAttr
   * @param selectedAttr
   * @return
   */
  private boolean isPidcVersAttOrSubvarAttr(final IProjectAttribute projAttr, final IProjectAttribute selectedAttr) {
    return (isPidcVersionAttr(projAttr, selectedAttr)) || (isSubVarAttr(projAttr, selectedAttr));
  }

  private boolean isPidcVersionAttr(final IProjectAttribute projAttr, final IProjectAttribute selectedAttr) {
    return (selectedAttr instanceof PidcVersionAttribute) && (projAttr instanceof PidcVersionAttribute);
  }

  private boolean isSubVarAttr(final IProjectAttribute projAttr, final IProjectAttribute selectedAttr) {
    return (selectedAttr instanceof PidcSubVariantAttribute) && (projAttr instanceof PidcSubVariantAttribute) &&
        (((PidcSubVariantAttribute) selectedAttr).getVariantId().longValue() == ((PidcSubVariantAttribute) projAttr)
            .getVariantId().longValue());
  }

  /**
   * @param menuMgr menu manager
   * @param actionSet pidc action set
   * @param editableAttrVar editable PidcVariantAttribute
   * @param pidcPage PIDCAttrPage
   */
  public void addMoveToVariantMenu(final MenuManager menuMgr, final PIDCActionSet actionSet,
      final PidcVariantAttribute editableAttrVar, final PIDCAttrPage pidcPage) {

    // ICDM-2354
    if (checkPIDCLocked(pidcPage.getSelectedPidcVersion())) {

      PidcVariantBO variantHandler = new PidcVariantBO(pidcPage.getSelectedPidcVersion(),
          pidcPage.getSelectedPidcVariant(), pidcPage.getPidcDataHandler());
      PidcVariantAttributeBO varAttrhandler = new PidcVariantAttributeBO(editableAttrVar, variantHandler);
      List<IProjectAttribute> projAttr = new ArrayList<>();
      projAttr.add(editableAttrVar);
      if (varAttrhandler.isVisible() && pidcPage.getPidcVersionBO().isModifiable() &&
          editableAttrVar.isAtChildLevel() &&
          validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_VARIANT)) {


        SortedSet<PidcSubVariant> subVariantsSet = variantHandler.getSubVariantsSet();
        if (!subVariantsSet.isEmpty()) {
          PidcSubVariant pidcSubVar = subVariantsSet.first();
          PidcSubVariantBO subVarHandler =
              new PidcSubVariantBO(pidcPage.getSelectedPidcVersion(), pidcSubVar, pidcPage.getPidcDataHandler());
          PidcSubVariantAttribute editableAttrSubVar = subVarHandler.getAttributes().get(editableAttrVar.getAttrId());

          actionSet.addMoveToVariantMenu(menuMgr, editableAttrSubVar, pidcSubVar, pidcPage);
        }
      }
    }
  }

  /**
   * This method is to create right click menu on sub-variant attributes tableviewer to move a selected sub-variant
   * attribute to variant
   *
   * @param menuMgr instance
   * @param pidcSubVarAttr instance
   * @param pidcSubVar instance
   * @param pidcPage pidc page
   */
  // ICDM-123
  public void addMoveToVariantMenu(final MenuManager menuMgr, final PidcSubVariantAttribute pidcSubVarAttr,
      final PidcSubVariant pidcSubVar, final PIDCAttrPage pidcPage) {


    final Action moveToVariantAction = new Action(ApicUiConstants.MOVE_TO_VARIANT, SWT.NONE) {

      @Override
      public void run() {
        // check if the pidc is unlocked in session
        // ICDM-2354
        if (checkPIDCLocked(pidcPage.getSelectedPidcVersion())) {
          moveSubvarAttrTOVar(pidcSubVarAttr, pidcSubVar, pidcPage);
        }
        else {
          showEditNotAllowedDialog(true);
        }
      }
    };
    // ICDM 656
    moveToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
    menuMgr.add(moveToVariantAction);
  }

  /**
   * @param pidcSubVarAttr
   * @param pidcSubVar
   * @param pidcPage
   */
  private void moveSubvarAttrTOVar(final PidcSubVariantAttribute pidcSubVarAttr, final PidcSubVariant pidcSubVar,
      final PIDCAttrPage pidcPage) {
    PidcSubVariantAttributeBO subhandler = new PidcSubVariantAttributeBO(pidcSubVarAttr,

        new PidcSubVariantBO(pidcPage.getSelectedPidcVersion(), pidcSubVar, pidcPage.getPidcDataHandler()));
    List<IProjectAttribute> projAttr = new ArrayList<>();
    projAttr.add(pidcSubVarAttr);

    if (subhandler.isVisible() && pidcPage.getPidcVersionBO().isModifiable() &&
        validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_VARIANT)) {
      ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
      moveModel.setPidcVersion(pidcPage.getSelectedPidcVersion());
      moveModel.getPidcSubVarAttrsToBeMovedToVariant().put(pidcSubVarAttr.getAttrId(), pidcSubVarAttr);
      executeMoveCommand(moveModel);
    }
  }

  /**
   * This method is to create right click menu on variant attributes tableviewer to move a selected variant attribute to
   * sub-variant
   *
   * @param menuMgr instance
   * @param pidcAttrVar instance
   * @param pidcPage attribute page instance
   */
  // ICDM-123
  public void addMoveToSubVariantMenu(final MenuManager menuMgr, final PidcVariantAttribute pidcAttrVar,
      final PIDCAttrPage pidcPage) {

    if (pidcAttrVar.isAtChildLevel()) {
      CDMLogger.getInstance().info(ApicUiConstants.SUB_VARIANT_ALREADY_MOVED, Activator.PLUGIN_ID);

    }

    else {
      final Action moveToSubVarAction = new Action(ApicUiConstants.MOVE_TO_SUB_VARIANT, SWT.NONE) {

        @Override
        public void run() {

          if (!checkSubVarAvailableForVar(pidcPage)) {
            MessageDialogUtils.getInfoMessageDialog("Sub Variant Info", "No Subvariants defined for this variant!");
          }
          else {
            PidcVariantAttributeBO handler =
                new PidcVariantAttributeBO(pidcAttrVar, (PidcVariantBO) pidcPage.getProjectObjectBO());
            // check if the pidc is unlocked in session
            // ICDM-2354
            if (checkPIDCLocked(pidcPage.getSelectedPidcVersion())) {
              movePidcAttrToSubVar(pidcAttrVar, pidcPage, handler);
            }
            else {
              showEditNotAllowedDialog(true);
            }
          }
        }
      };
      moveToSubVarAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SUBVAR_28X30));
      menuMgr.add(moveToSubVarAction);
    }
  }

  /**
   * method to check Subvariant available for Variant
   */
  private boolean checkSubVarAvailableForVar(final PIDCAttrPage pidcPage) {
    return pidcPage.getPidcVersionBO().getPidcDataHandler().getSubVariantMap().values().stream()
        .anyMatch(subVar -> CommonUtils.isEqual(subVar.getPidcVariantId(), pidcPage.getSelectedPidcVariant().getId()));
  }

  /**
   * @param pidcAttrVar
   * @param pidcPage
   * @param handler
   */
  private void movePidcAttrToSubVar(final PidcVariantAttribute pidcAttrVar, final PIDCAttrPage pidcPage,
      final PidcVariantAttributeBO handler) {
    List<IProjectAttribute> projAttr = new ArrayList<>();
    projAttr.add(pidcAttrVar);

    if (handler.isVisible() && handler.isModifiable() && pidcPage.getPidcVersionBO().isModifiable() &&
        validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_SUB_VARIANT)) {
      String parameterValue;
      try {
        parameterValue = new CommonDataBO().getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID);

        if (pidcAttrVar.getAttrId().toString().equals(parameterValue)) {
          CDMLogger.getInstance().errorDialog(MOVE_VARNAME_CDM_ERROR, Activator.PLUGIN_ID);
        }
        else {
          ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
          moveModel.setPidcVersion(pidcPage.getSelectedPidcVersion());
          moveModel.getPidcVarAttrsToBeMovedDown().put(pidcAttrVar.getAttrId(), pidcAttrVar);
          executeMoveCommand(moveModel);

        }
      }
      catch (ApicWebServiceException ex) {
        CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param menuMgr menu manager
   * @param pidcAttrVar pidc attribute variant
   * @param pidcVer pidc version
   * @param selectedPidcVariant selected pidc variant
   * @param pidcPage pidc attribute page
   */
  public void addMoveToCommonMenu(final MenuManager menuMgr, final PidcVariantAttribute pidcAttrVar,
      final PidcVersion pidcVer, final PidcVariant selectedPidcVariant, final PIDCAttrPage pidcPage) {
    Action moveToCommonAction = new Action("Move To Common", SWT.NONE) {

      @Override
      public void run() {
        // check if the pidc is unlocked in session
        // ICDM-2354
        try {
          moveToCommon(pidcAttrVar, pidcVer, selectedPidcVariant, pidcPage, checkPIDCLocked(pidcVer),
              new CurrentUserBO());
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    moveToCommonAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16));
    menuMgr.add(moveToCommonAction);
  }


  /**
   * @param pidcAttrVar
   * @param pidcVer
   * @param selectedPidcVariant
   * @param pidcPage
   * @param isPidcUnLocked
   * @param currUser
   * @throws ApicWebServiceException
   */
  private void moveToCommon(final PidcVariantAttribute pidcAttrVar, final PidcVersion pidcVer,
      final PidcVariant selectedPidcVariant, final PIDCAttrPage pidcPage, final boolean isPidcUnLocked,
      final CurrentUserBO currUser)
      throws ApicWebServiceException {

    if (isPidcUnLocked && currUser.hasNodeWriteAccess(pidcVer.getPidcId())) {

      PidcVariantAttributeBO handler = new PidcVariantAttributeBO(pidcAttrVar,
          new PidcVariantBO(pidcVer, selectedPidcVariant, pidcPage.getPidcDataHandler()));
      List<IProjectAttribute> projAttr = new ArrayList<>();
      projAttr.add(pidcAttrVar);
      if (handler.isVisible() && pidcPage.getPidcVersionBO().isModifiable() &&
          validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_COMMON)) {
        moveAttrToCommon(pidcAttrVar, pidcPage, handler);
      }
      else {
        showEditNotAllowedDialog(true);
      }
    }
  }

  /**
   * @param pidcAttrVar
   * @param pidcPage
   * @param handler
   * @throws ApicWebServiceException
   */
  private void moveAttrToCommon(final PidcVariantAttribute pidcAttrVar, final PIDCAttrPage pidcPage,
      final PidcVariantAttributeBO handler)
      throws ApicWebServiceException {
    AttributeValue attributeValue = pidcPage.getPidcDataHandler().getAttributeValueMap().get(pidcAttrVar.getValueId());
    String oldPverName = null == attributeValue ? "" : attributeValue.getName();
    boolean isA2LMapped = checkIfA2LFileMappedAndThrowDialog(oldPverName, pidcPage.getPidcVersionBO());

    // checks if a2l is mapped
    if (isA2LMapped) {
      CDMLogger.getInstance().errorDialog(MODIFY_PVER_NAME_ERROR, Activator.PLUGIN_ID);
      return;
    }
    else if (pidcAttrVar.getAttrId().toString()
        .equals(new CommonDataBO().getParameterValue(CommonParamKey.VARIANT_IN_CUST_CDMS_ATTR_ID))) {
      CDMLogger.getInstance().errorDialog(MOVE_VARNAME_CDM_ERROR, Activator.PLUGIN_ID);
      return;
    }

    boolean isVirtual = false;
    // If the attr to be moved is defin as a virtual level struct attr, then it should not be moved to common
    // Icdm-1115 display error message if variant attr defined as virtual level structure is moved to
    // Common.
    for (PidcDetStructure pidcdetStruct : pidcPage.getPidcVersionBO().getVirtualLevelAttrs().values()) {
      if (pidcAttrVar.getAttrId().longValue() == pidcdetStruct.getAttrId().longValue()) {
        isVirtual = true;
      }
    }

    if (isVirtual) {
      CDMLogger.getInstance().errorDialog(PIDCActionSet.VIRTUAL_NODE_ERR, Activator.PLUGIN_ID);
      return;
    }

    if (checkMoveUpAllowed(pidcAttrVar, handler)) {

      ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
      moveModel.setPidcVersion(pidcPage.getSelectedPidcVersion());
      moveModel.getPidcVarAttrsToBeMovedUp().put(pidcAttrVar.getAttrId(), pidcAttrVar);
      executeMoveCommand(moveModel);
    }
  }

  /**
   * Method to check if a PIDC attribute can be moved to common, (if an attribute is used in sub-variant and is not
   * deleted, then it cannot be moved to common)
   *
   * @param attr - Attribute for which the check is done
   * @param handler
   */
  private boolean checkMoveUpAllowed(final IProjectAttribute attr, final PidcVariantAttributeBO handler) {

    // Get all variants of this pidc
    final Map<Long, PidcVariant> pidcVars = handler.getProjectObjectBO().getPidcDataHandler().getVariantMap();
    for (PidcVariant pidcVariant : pidcVars.values()) {
      // get all attributes of this variant
      PidcVariantBO varHan = new PidcVariantBO(handler.getProjectObjectBO().getPidcVersion(), pidcVariant,
          handler.getProjectObjectBO().getPidcDataHandler());
      final Map<Long, PidcVariantAttribute> varAttrs = varHan.getAttributesAll();
      for (PidcVariantAttribute varAttr : varAttrs.values()) {
        // If any attr is defined as sub-variant, attribute cannot be moved up
        if ((attr.getAttrId().longValue() == varAttr.getAttrId().longValue()) && varAttr.isAtChildLevel()) {

          return checkSubvariantDeletion(pidcVariant, attr, varHan);


        }

      }
    }
    return true;
  }

  /**
   * If subvariant is deleted, then allow move to common, else display error
   *
   * @param pidcVariant
   * @param attr
   * @param varHan
   */
  private boolean checkSubvariantDeletion(final PidcVariant pidcVariant, final IProjectAttribute attr,
      final PidcVariantBO varHan) {
    if (!isSubVariantDeleted(attr.getAttrId(), varHan)) {

      CDMLogger.getInstance().errorDialog("Attribute is defined as Sub-Variant for Variant : " + pidcVariant.getName(),
          Activator.PLUGIN_ID);
      return false;
    }
    return true;
  }

  /**
   * Method that checks if variant attribute is part of any undeleted subvariant If it is part of undeleted subvariant,
   * throw error If it is part of deleted subvariant, do nothing
   *
   * @param pidcVariant - pidcVariant to check
   * @param varHan
   * @param attribute - pidc attribute to check
   * @return true if subvariant is deleted, false if not deleted
   */
  private boolean isSubVariantDeleted(final Long attrId, final PidcVariantBO varHan) {
    for (PidcSubVariant subVar : varHan.getSubVariantsMap().values()) {
      if (subVar.isDeleted()) {
        continue;
      }
      PidcSubVariantBO subhan = new PidcSubVariantBO(varHan.getPidcVersion(), subVar, varHan.getPidcDataHandler());
      if (!subhan.getAttributes().isEmpty() && (subhan.getAttributes().get(attrId) != null)) {
        return false;
      }

    }
    return true;
  }

  /**
   * @param pidcVer This method is used to rename the opened editors' title , after renaming a pidc
   */
  // Update title while renaming PIDC
  public void renameEditorTitle(final PidcVersion pidcVer) {

    PIDCEditorInput input = new PIDCEditorInput(pidcVer, false);
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
      IEditorPart openedEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);
      if ((openedEditor instanceof PIDCEditor)) {
        PIDCEditor editor = (PIDCEditor) openedEditor;
        // ICDM-208
        String titleText = pidcVer.getName();
        editor.setEditorPartName(titleText);
        IFormPage activePageInstance = editor.getActivePageInstance();
        IManagedForm managedForm = activePageInstance.getManagedForm();
        // ICDM-168
        // managed Form null in case of PIDCPage as non scrollable form is used.
        if (managedForm != null) {
          managedForm.getForm().setText(titleText);
        }
        // ICDM-208
        if (activePageInstance instanceof PIDCAttrPage) {
          PIDCAttrPage pidcPage = (PIDCAttrPage) activePageInstance;
          pidcPage.setTitleText(titleText);
        }
        // No need to check for PIDCPage as selection listener is triggered after rename
        else if (activePageInstance instanceof PidcVersionsPage) {
          PidcVersionsPage versionsPage = (PidcVersionsPage) activePageInstance;
          versionsPage.setTitleText(titleText);
        }
        else if (activePageInstance instanceof NodeAccessRightsPage) {
          NodeAccessRightsPage rightsPage = (NodeAccessRightsPage) activePageInstance;
          rightsPage.setTitleText(titleText);
        }

      }
    }
  }

  /**
   * Refresh the favorites view
   */
  public void refreshFavoritesView() {
    Display.getDefault().asyncExec(() -> {
      try {
        // Favourites view
        FavoritesViewPart favViewPart = (FavoritesViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().showView(FavoritesViewPart.VIEW_ID);
        TreeViewer favViewer = favViewPart.getViewer();
        favViewer.refresh();
      }
      catch (PartInitException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    });
  }

  /**
   * Action for creating a variant
   *
   * @param manager manager defines menu manager
   * @param pidVersion pidc Version instance
   * @param strAddVar defines add variant string
   * @param viewer instance
   * @param pidcNode instance
   * @param projObjBO projectObjBO
   */
  public void setCreateVariantAction(final IMenuManager manager, final PidcVersion pidVersion, final String strAddVar,
      final TreeViewer viewer, final PIDCDetailsNode pidcNode, final AbstractProjectObjectBO projObjBO) {
    CurrentUserBO currUser = new CurrentUserBO();

    // rename action
    final Action addVariantAction = new Action() {

      @Override
      public void run() {
        openCreateVarDialog(pidVersion, viewer, pidcNode, projObjBO);
      }


    };
    addVariantAction.setText(strAddVar);
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30);
    addVariantAction.setImageDescriptor(imageDesc);

    PidcVersionBO pidcVersionBO = (PidcVersionBO) projObjBO;
    try {
      addVariantAction
          .setEnabled(pidcVersionBO.canCreateVariant() && currUser.hasNodeOwnerAccess(pidVersion.getPidcId()));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    manager.add(addVariantAction);
  }


  // ICDM-2408
  /**
   * @param manager manager defines menu manager
   * @param pidcVersion pidc Version instance
   * @param strStartWebflowJob defines Start Webflow job string
   */
  public void setPidcWebflowJobAction(final IMenuManager manager, final PidcVersion pidcVersion,
      final String strStartWebflowJob) {

    CurrentUserBO currUser = new CurrentUserBO();

    // webflow job action for PIDC
    final Action webflowPidcAction = new Action() {

      @Override
      public void run() {
        // check if the pidc is unlocked in session
        try {
          if (!new ApicDataBO().isPidcUnlockedInSession(pidcVersion) &&
              (null != currUser.getNodeAccessRight(pidcVersion.getPidcId())) &&
              currUser.hasNodeReadAccess(pidcVersion.getPidcId())) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(pidcVersion);
          }

          startPIDCWebFlowJob(pidcVersion);

        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }
    };
    webflowPidcAction.setText(strStartWebflowJob);
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.WEBFLOW_16X16);
    webflowPidcAction.setImageDescriptor(imageDesc);

    try {
      boolean enabled = (currUser.getNodeAccessRight(pidcVersion.getPidcId()) != null) &&
          (currUser.hasNodeReadAccess(pidcVersion.getPidcId())) && !pidcVersion.isDeleted();

      webflowPidcAction.setEnabled(enabled);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    manager.add(webflowPidcAction);
  }

  /**
   * @param pidVersion
   * @param apicDataProvider
   * @param currentUserRight
   * @throws ApicWebServiceException
   */
  private void startPIDCWebFlowJob(final PidcVersion pidcVersion) throws ApicWebServiceException {

    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    CommonDataBO dataBo = new CommonDataBO();
    String webFlowLink = "";

    if (apicBo.isPidcUnlockedInSession(pidcVersion) && (null != currUser.getNodeAccessRight(pidcVersion.getPidcId())) &&
        currUser.hasNodeReadAccess(pidcVersion.getPidcId())) {

      webFlowLink = dataBo.getParameterValue(CommonParamKey.WEB_FLOW_JOB_LINK)
          .replace(ApicUiConstants.WEB_FLOW_JOB_ELEMENT_ID, pidcVersion.getId().toString());
    }


    // Open link for Webflow job in internet explorer
    CommonActionSet action = new CommonActionSet();
    action.openExternalBrowser(webFlowLink);
  }


  // ICDM-1551
  /**
   * Refresh entire PIDC tree
   */
  public void refreshPIDCTreeView() {
    Display.getDefault().asyncExec(() -> {
      final IViewPart viewPartObj =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(PIDTreeViewPart.VIEW_ID);
      if ((viewPartObj instanceof PIDTreeViewPart)) {
        final PIDTreeViewPart pidViewPart = (PIDTreeViewPart) viewPartObj;
        pidViewPart.getViewer().refresh();
      }
    });
  }

  /**
   * @param pidVersion pidc version
   * @param viewer treeviewer
   * @param pidcNode pidcDetailsNode
   * @param projObjBO projectObj Bo
   */
  public void openCreateVarDialog(final PidcVersion pidVersion, final TreeViewer viewer, final PIDCDetailsNode pidcNode,
      final AbstractProjectObjectBO projObjBO) {
    // ICDM-2487 P1.27.101
    // check if the pidc is unlocked in session
    // ICDM-2354
    if (checkPIDCLocked(pidVersion)) {
      ApicDataBO apicDataBO = new ApicDataBO();
      Attribute varNameAttr = apicDataBO.getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.VARIANT_CODE_ATTR));
      final AddVariantDialog addVarDialog =
          new AddVariantDialog(Display.getDefault().getActiveShell(), projObjBO, varNameAttr, viewer, pidVersion,
              pidcNode == null ? null : pidcNode.getAttrValuesFromNodeTree((PidcVersionBO) projObjBO));
      addVarDialog.open();
    }
  }

  /**
   * @param pidVersion pidVersion
   * @return true if Pidc is not locked
   */
  public boolean checkPIDCLocked(final PidcVersion pidVersion) {

    ApicDataBO apicBo = new ApicDataBO();

    // if PIDC is locked and user has write access then show popup to unlock the PIDC
    if (!apicBo.isPidcUnlockedInSession(pidVersion) && checkUserHasWriteAccess(pidVersion)) {
      showUnlockPidcDialog(pidVersion);
    }

    return apicBo.isPidcUnlockedInSession(pidVersion);
  }


  /**
   * Method to check
   *
   * @param pidVersion pidVersion
   * @return true if user has write access
   */
  public boolean checkUserHasWriteAccess(final PidcVersion pidVersion) {

    boolean isUserHasWriteAccess = false;

    try {
      isUserHasWriteAccess = new CurrentUserBO().hasNodeWriteAccess(pidVersion.getPidcId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    return isUserHasWriteAccess;
  }


  /**
   * This method refreshes the PidDetailsTreeViewer
   */
  public void refreshPidDetailsTreeViewer() {
    IViewPart viewPartObj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(ApicUiConstants.PID_DETAILS_TREE_VIEW);
    if (viewPartObj instanceof PIDCDetailsViewPart) {
      PIDCDetailsViewPart pidcDetailsViewPart = (PIDCDetailsViewPart) viewPartObj;
      if (pidcDetailsViewPart.getCurrentPage() instanceof PIDCDetailsPage) {
        PIDCDetailsPage pidcDetaisPage = (PIDCDetailsPage) pidcDetailsViewPart.getCurrentPage();
        // Added notify listeners to re enable selection after rename of PIDC/Variant/Subvariant to provide selection
        // changed event to
        // PIDCPage to change page title after rename
        // ICDM-26/ICDM-168
        pidcDetaisPage.refreshNodes(); // icdm-911
        pidcDetaisPage.getViewer().refresh();
        pidcDetaisPage.getViewer().getControl().notifyListeners(SWT.Selection, null);
      }
    }
  }


  /**
   * This method sets right click menu for variant node to create a new sub-variant
   *
   * @param manager defines menu manager
   * @param pidVar instance
   * @param strAddSubVar defines add sub-variant name
   * @param viewer instance
   * @param editorPidcVer PidcVersion
   * @param projObjBO PidcDataHandler
   * @param selection selected virtual node
   */
  // ICDM-121
  public void setCreateSubVarAction(final IMenuManager manager, final PidcVariant pidVar, final String strAddSubVar,
      final TreeViewer viewer, final AbstractProjectObjectBO projObjBO, final PidcVersion editorPidcVer,
      final IStructuredSelection selection) {
    CurrentUserBO currUser = new CurrentUserBO();
    PIDCDetailsNode pidcNode;
    if (selection.getFirstElement() instanceof PidcVariant) {
      pidcNode = null;
    }
    else {
      pidcNode = (PIDCDetailsNode) selection.getFirstElement();
    }
    // create sub variant action
    final Action addVariantAction = new Action() {

      @Override
      public void run() {
        // ICDM-2430
        if (checkVariantCodingAttribute(projObjBO.getPidcDataHandler())) {
          // ICDM-2487 P1.27.101
          // check if the pidc is unlocked in session
          showUnlockDialog(editorPidcVer);

          showAddSubVarDialog(pidVar, viewer, editorPidcVer, projObjBO, pidcNode == null ? null
              : getAttrValueMapWithId(pidcNode.getLastLevelValuesFromNodeTree((PidcVersionBO) projObjBO)));
        }
        else {
          MessageDialogUtils.getInfoMessageDialog("Variant Coding",
              "Please set the attribute 'Variant Coding' to 'true' to  add sub variants to your project.");
        }
      }


    };
    addVariantAction.setText(strAddSubVar);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SUBVAR_28X30);
    addVariantAction.setImageDescriptor(imageDesc);

    PidcVersionBO pidcVersionBO = (PidcVersionBO) projObjBO;
    try {
      addVariantAction.setEnabled(pidcVersionBO.canCreateSubVariant() &&
          currUser.hasNodeOwnerAccess(editorPidcVer.getPidcId()) && !editorPidcVer.isDeleted() && !pidVar.isDeleted());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    manager.add(addVariantAction);

  }

  /**
   * @param attrAndValueMap
   * @return
   */
  private Map<Long, Long> getAttrValueMapWithId(final Map<Attribute, AttributeValue> attrAndValueMap) {
    Map<Long, Long> structAttrValueMap = new HashMap<>();
    for (Entry<Attribute, AttributeValue> attrValueEntry : attrAndValueMap.entrySet()) {
      if (null != attrValueEntry.getValue()) {
        structAttrValueMap.put(attrValueEntry.getKey().getId(), attrValueEntry.getValue().getId());
      }
    }
    return structAttrValueMap;
  }

  /**
   * @param pidVar
   * @param viewer
   * @param editorPidcVer
   * @param projObjBO
   * @param map
   */
  private void showAddSubVarDialog(final PidcVariant pidVar, final TreeViewer viewer, final PidcVersion editorPidcVer,
      final AbstractProjectObjectBO projObjBO, final Map<Long, Long> map) {
    ApicDataBO apicBo = new ApicDataBO();
    if (apicBo.isPidcUnlockedInSession(editorPidcVer)) {
      // Get Sub-Variant name attribute
      Long subVariantCodeAttr = (long) ApicConstants.SUB_VARIANT_CODE_ATTR;
      final Attribute subVarNameAttr = apicBo.getAllLvlAttrByLevel().get(subVariantCodeAttr);
      final AddSubVariantDialog addSubVarDialog = new AddSubVariantDialog(Display.getDefault().getActiveShell(),
          subVarNameAttr, viewer, pidVar, editorPidcVer, projObjBO, map);
      addSubVarDialog.open();
    }
  }

  /**
   * @param pidVersion
   * @param pidcActionSet
   */
  private void showUnlockDialog(final PidcVersion pidVersion) {
    // check if the pidc is unlocked in session
    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();
    final PIDCActionSet pidcActionSet = new PIDCActionSet();
    try {
      if (!apicBo.isPidcUnlockedInSession(pidVersion) && currUser.hasNodeWriteAccess(pidVersion.getPidcId())) {
        pidcActionSet.showUnlockPidcDialog(pidVersion);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param pidcDataHandler
   * @returns if the variant coding attribute is set to TRUE
   */
  private boolean checkVariantCodingAttribute(final PidcDataHandler pidcDataHandler) {
    ApicDataBO apicDataBO = new ApicDataBO();
    Long variantCodingAttrLevel = (long) ApicConstants.VARIANT_CODING_ATTR;
    Attribute varCodingAttr = apicDataBO.getAllLvlAttrByLevel().get(variantCodingAttrLevel);
    PidcVersionAttribute pidcVarCodingAttr = pidcDataHandler.getPidcVersAttrMap().get(varCodingAttr.getId());
    if (null == pidcVarCodingAttr) {
      return false;
    }
    return CommonUtils.isEqual(pidcVarCodingAttr.getUsedFlag(), PROJ_ATTR_USED_FLAG.YES.getDbType());
  }

  // ICDM-2408
  /**
   * @param manager manager defines menu manager
   * @param pidVar pidcVar instance
   * @param strWebflowJob strAddSubVar defines Start Webflow job string
   * @param pidcVersion pidc version instance
   */
  public void setVarWebflowJobAction(final IMenuManager manager, final PidcVariant pidVar,
      final PidcVersion pidcVersion, final String strWebflowJob) {

    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();

    // web flow job action for variant
    final Action webflowVarAction = new Action() {

      @Override
      public void run() {
        // check if the pidc is unlocked in session

        try {
          if (!apicBo.isPidcUnlockedInSession(pidcVersion) && currUser.hasNodeReadAccess(pidcVersion.getPidcId())) {

            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(pidcVersion);
          }
          startVariantWebflowJob(pidVar, pidcVersion);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    webflowVarAction.setText(strWebflowJob);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.WEBFLOW_16X16);
    webflowVarAction.setImageDescriptor(imageDesc);

    try {
      webflowVarAction.setEnabled((currUser.getNodeAccessRight(pidcVersion.getPidcId()) != null) &&
          (currUser.hasNodeReadAccess(pidcVersion.getPidcId())) && !pidVar.isDeleted() && !pidcVersion.isDeleted());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    manager.add(webflowVarAction);
  }

  /**
   * Show to be implemeneted dialog
   */
  public void showToBeImplementedDialog() {
    MessageDialogUtils.getInfoMessageDialog("To be implemented", "This feature is yet to be implemented!");
  }

  /**
   * @param pidcVar
   * @param apicDataProvider
   * @param pidcVer
   * @param currentUserRight
   */
  private void startVariantWebflowJob(final PidcVariant pidcVar, final PidcVersion pidcVer) {

    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currentUser = new CurrentUserBO();
    CommonDataBO commonData = new CommonDataBO();
    try {
      if (apicBo.isPidcUnlockedInSession(pidcVer) && (null != currentUser.getNodeAccessRight(pidcVer.getPidcId())) &&
          currentUser.hasNodeReadAccess(pidcVer.getPidcId())) {

        String webFlowLink;
        webFlowLink = commonData.getParameterValue(CommonParamKey.WEB_FLOW_JOB_LINK)
            .replace(ApicUiConstants.WEB_FLOW_JOB_ELEMENT_ID, pidcVar.getId().toString());

        // Open link for Webflow job in browser
        CommonActionSet action = new CommonActionSet();
        action.openExternalBrowser(webFlowLink);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param manager menu manager
   * @param pidVar pidc variant
   * @param pidcVersionBO pidc version BO
   * @param strRename name to be replaced with
   */
  public void setRenameVarAction(final IMenuManager manager, final PidcVariant pidVar, final String strRename,
      final PidcVersionBO pidcVersionBO) {
    final PidcVersion pidVersion = pidcVersionBO.getPidcVersion();

    // rename action
    final Action addRenameAction = new Action() {

      @Override
      public void run() {
        if (isVariantHasReviews(pidVar)) {
          return;
        }

        ApicDataBO apicBo = new ApicDataBO();
        CurrentUserBO currUser = new CurrentUserBO();

        renameVar(pidVar, pidcVersionBO, pidVersion, apicBo, currUser);
      }
    };
    addRenameAction.setText(strRename);
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RENAME_16X16);
    addRenameAction.setImageDescriptor(imageDesc);
    // Get the user info. Now
    try {
      enableDisableRenameAction(pidVar, pidcVersionBO, addRenameAction);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    manager.add(addRenameAction);
  }

  /**
   * @param pidVar
   * @param pidcVersionBO
   * @param pidVersion
   * @param apicBo
   * @param currUser
   */
  private void renameVar(final PidcVariant pidVar, final PidcVersionBO pidcVersionBO, final PidcVersion pidVersion,
      final ApicDataBO apicBo, final CurrentUserBO currUser) {
    // ICDM-2487 P1.27.101 - check if the pidc is unlocked in session
    try {
      if (!apicBo.isPidcUnlockedInSession(pidVersion) && currUser.hasNodeWriteAccess(pidVersion.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(pidVersion);
      }

      // ICDM-2354
      if (apicBo.isPidcUnlockedInSession(pidVersion)) {
        renameVariant(pidVar, pidcVersionBO);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidVar
   * @param pidcVersionBO
   * @param addRenameAction
   * @param currentUserRight
   * @throws ApicWebServiceException
   */
  private void enableDisableRenameAction(final PidcVariant pidVar, final PidcVersionBO pidcVersionBO,
      final Action addRenameAction)
      throws ApicWebServiceException {
    CurrentUserBO currUser = new CurrentUserBO();
    NodeAccess nodeAccessRight = currUser.getNodeAccessRight(pidcVersionBO.getPidcVersion().getPidcId());
    addRenameAction.setEnabled(checkNodeAccess(nodeAccessRight) && !pidVar.isDeleted() &&
        checkPidcVerStatus(pidcVersionBO, pidcVersionBO.getPidcVersion()));
  }

  /**
   * @param manager menu manager
   * @param pidVar pidc variant
   * @param pidcVersionBO pidcVersionBO
   * @param strDelete string to delete
   */
  public void setDeleteVarAction(final IMenuManager manager, final PidcVariant pidVar, final String strDelete,
      final PidcVersionBO pidcVersionBO) {

    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();
    PidcVersion pidVersion = pidcVersionBO.getPidcVersion();
    // delete action
    final Action addDeleteAction = new Action() {

      @Override
      public void run() {
        // ICDM-2487 P1.27.101
        // check if the pidc is unlocked in session
        try {
          if (!apicBo.isPidcUnlockedInSession(pidVersion) && currUser.hasNodeWriteAccess(pidVersion.getPidcId())) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(pidVersion);
          }

          // ICDM-2354
          if (apicBo.isPidcUnlockedInSession(pidVersion)) {
            deleteVarAction(pidVar, strDelete, pidVersion);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }
    };
    addDeleteAction.setText(strDelete);
    ImageDescriptor imageDesc;
    if (strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_ACTION)) {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNDO_DELETE_16X16);
    }
    else {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    }
    addDeleteAction.setImageDescriptor(imageDesc);
    enableDisableDeleteAction(pidcVersionBO, currUser, pidVersion, addDeleteAction);
    manager.add(addDeleteAction);


  }

  /**
   * @param pidcVersionBO
   * @param currUser
   * @param pidVersion
   * @param addDeleteAction
   */
  private void enableDisableDeleteAction(final PidcVersionBO pidcVersionBO, final CurrentUserBO currUser,
      final PidcVersion pidVersion, final Action addDeleteAction) {
    NodeAccess nodeAccessRight;
    try {
      nodeAccessRight = currUser.getNodeAccessRight(pidVersion.getPidcId());
      addDeleteAction.setEnabled((nodeAccessRight != null) && nodeAccessRight.isOwner() &&
          (CommonUtils.isEqual(pidVersion.getPidStatus(), PidcVersionStatus.IN_WORK.getDbStatus())) &&
          pidcVersionBO.isModifiable());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * ICDM-705
   *
   * @param pidcVariant PIDCVariant
   * @param strDelete String to know whether its delete or undelete
   * @param pidcVersion PidcVersion
   */
  public void deleteVarAction(final PidcVariant pidcVariant, final String strDelete, final PidcVersion pidcVersion) {

    pidcVariant.setDeleted(CommonUtils.isEqual(strDelete, ApicUiConstants.DELETE_ACTION));

    PidcVariantServiceClient pidcVarServiceClient = new PidcVariantServiceClient();
    try {
      PidcVariantData pidcVarData = new PidcVariantData();
      pidcVarData.setSrcPidcVar(pidcVariant);
      pidcVarData.setPidcVersion(pidcVersion);
      pidcVarServiceClient.update(pidcVarData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param manager defines IMenuManager
   * @param pidSubVar instance
   * @param strRename defines rename name
   * @param pidcVersionBO pidc version BO
   */
  // ICDM-121
  public void setRenameSubVarAction(final IMenuManager manager, final PidcSubVariant pidSubVar, final String strRename,
      final PidcVersionBO pidcVersionBO) {
    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();
    PidcVersion pidVersion = pidcVersionBO.getPidcVersion();
    PidcVariant pidcVariant = pidcVersionBO.getPidcDataHandler().getVariantMap().get(pidSubVar.getPidcVariantId());

    // rename action
    final Action addRenameAction = new Action() {

      @Override
      public void run() {

        // ICDM-2487 P1.27.101
        // check if the pidc is unlocked in session
        try {
          if (!apicBo.isPidcUnlockedInSession(pidVersion) && currUser.hasNodeWriteAccess(pidVersion.getPidcId())) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(pidVersion);
          }

          // ICDM-2354
          if (apicBo.isPidcUnlockedInSession(pidVersion)) {
            renameSubVariant(pidSubVar, pidcVersionBO);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    addRenameAction.setText(strRename);
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RENAME_16X16);
    addRenameAction.setImageDescriptor(imageDesc);

    NodeAccess nodeAccessRight;
    try {
      nodeAccessRight = currUser.getNodeAccessRight(pidVersion.getPidcId());
      addRenameAction.setEnabled(checkNodeAccess(nodeAccessRight) && checkPidcVerStatus(pidcVersionBO, pidVersion) &&
          checkVarSubvarStatus(pidSubVar, pidcVariant));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    manager.add(addRenameAction);

  }

  /**
   * @param pidSubVar
   * @param pidcVariant
   * @return
   */
  private boolean checkVarSubvarStatus(final PidcSubVariant pidSubVar, final PidcVariant pidcVariant) {
    return !pidSubVar.isDeleted() && !pidcVariant.isDeleted();
  }

  /**
   * @param pidcVersionBO
   * @param pidVersion
   * @return
   */
  private boolean checkPidcVerStatus(final PidcVersionBO pidcVersionBO, final PidcVersion pidVersion) {
    return (CommonUtils.isEqual(pidVersion.getPidStatus(), PidcVersionStatus.IN_WORK.getDbStatus())) &&
        pidcVersionBO.isModifiable();
  }

  /**
   * @param nodeAccessRight
   * @return
   */
  private boolean checkNodeAccess(final NodeAccess nodeAccessRight) {
    return (nodeAccessRight != null) && nodeAccessRight.isOwner();
  }

  /**
   * This method adds right click delete menu
   *
   * @param manager defines IMenuManager
   * @param pidSubVar instance
   * @param strDelete defines delete name
   * @param pidcVersionBO PIDC version BO
   */
  // ICDM-121
  public void setDeleteSubVarAction(final IMenuManager manager, final PidcSubVariant pidSubVar, final String strDelete,
      final PidcVersionBO pidcVersionBO) {

    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();
    PidcVersion pidVersion = pidcVersionBO.getPidcVersion();
    PidcVariant pidcVariant = pidcVersionBO.getPidcDataHandler().getVariantMap().get(pidSubVar.getPidcVariantId());
    // delete action
    final Action addDeleteAction = new Action() {

      @Override
      public void run() {

        // ICDM-2487 P1.27.101
        // check if the pidc is unlocked in session
        try {
          if (!apicBo.isPidcUnlockedInSession(pidVersion) && currUser.hasNodeWriteAccess(pidVersion.getPidcId())) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(pidVersion);
          }

          // ICDM-2354
          if (apicBo.isPidcUnlockedInSession(pidVersion)) {
            deleteSubVarAction(pidSubVar, strDelete);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    addDeleteAction.setText(strDelete);
    ImageDescriptor imageDesc;
    if (strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_ACTION)) {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNDO_DELETE_16X16);
    }
    else {
      imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
    }
    addDeleteAction.setImageDescriptor(imageDesc);
    enableDisableDelVarAction(pidVersion, addDeleteAction, pidcVersionBO, currUser, pidcVariant);
    manager.add(addDeleteAction);
  }

  /**
   * @param pidSubVar
   * @param pidVersion
   * @param currentUserRight
   * @param addDeleteAction
   * @param currUser
   * @param pidcVersionBO
   * @param pidcVariant
   */
  private void enableDisableDelVarAction(final PidcVersion pidVersion, final Action addDeleteAction,
      final PidcVersionBO pidcVersionBO, final CurrentUserBO currUser, final PidcVariant pidcVariant) {
    NodeAccess nodeAccessRight;
    try {
      nodeAccessRight = currUser.getNodeAccessRight(pidVersion.getPidcId());
      addDeleteAction.setEnabled(checkNodeAccess(nodeAccessRight) && checkPidcVerStatus(pidcVersionBO, pidVersion) &&
          !pidcVariant.isDeleted());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * ICDM-705
   *
   * @param pidcSubVariant PIDCSubVariant
   * @param strDelete String to know whether its delete or undelete
   */
  public void deleteSubVarAction(final PidcSubVariant pidcSubVariant, final String strDelete) {

    PidcSubVariantServiceClient pidcSubVarClient = new PidcSubVariantServiceClient();
    pidcSubVariant.setDeleted(strDelete.equalsIgnoreCase(ApicUiConstants.DELETE_ACTION));

    PidcSubVariantData pidcSubVarData = new PidcSubVariantData();
    pidcSubVarData.setSrcPidcSubVar(pidcSubVariant);
    try {
      pidcSubVarClient.update(pidcSubVarData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param versionsPage
   */
  private boolean showUnlockPidcDialog(final PidcVersionsPage versionsPage) {
    boolean resultFlag = false;
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    try {
      if (!apicBo.isPidcUnlockedInSession(versionsPage.getPidcVersion()) &&
          currUser.hasNodeWriteAccess(versionsPage.getPidcVersion().getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        resultFlag = pidcActionSet.showUnlockPidcDialog(versionsPage.getPidcVersion());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return resultFlag;
  }

  /**
   * This method creates edit version details action
   *
   * @param toolBarManager ToolBarManager
   * @param versionsPage VersionsPage
   */
  public void editVersionDetailsAction(final ToolBarManager toolBarManager, final PidcVersionsPage versionsPage) {
    // Get the user info NodeAccessRight currentUserRight =
    CurrentUserBO currUser = new CurrentUserBO();


    // Create edit action
    this.editAction = new Action(ApicUiConstants.EDIT, SWT.NONE) {

      @Override
      public void run() {
        // ICDM-2487 P1.27.101 // Check if the PIDC is locked
        // If PIDC is locked, open dialog to unlock the PIDC
        boolean resultFlag = showUnlockPidcDialog(versionsPage);
        // Perform action only if the PIDC is unlocked in session
        if (!resultFlag) {
          final PIDCVersionDetailsDialog vrsnDialog =
              new PIDCVersionDetailsDialog(Display.getDefault().getActiveShell(), versionsPage, true);
          vrsnDialog.open();
        }
        else {
          CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
        }
      }
    }; // Set
       // the
       // image
       // for
       // edit
       // action
    this.editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    try {
      this.editAction.setEnabled((null != currUser.getApicAccessRight()) &&
          currUser.hasNodeOwnerAccess(versionsPage.getPidcVersion().getPidcId()) &&
          !versionsPage.getPidcVersion().isDeleted() && (null != versionsPage.getSelectedPidcVersion()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    toolBarManager.add(this.editAction);
  }

  /**
   * This method creates new version action
   *
   * @param toolBarManager ToolBarManager
   * @param versionsPage VersionsPage
   */
  public void createNewVersionAction(final ToolBarManager toolBarManager, final PidcVersionsPage versionsPage) {
    // Get the user info NodeAccessRight currentUserRight =

    CurrentUserBO currUser = new CurrentUserBO();


    // Create new revision action
    this.newVersionAction = new Action(Messages.getString(IMessageConstants.NEWVERSION_LABEL), SWT.NONE) {

      @Override
      public void run() { // ICDM-2487 P1.27.101
        // Check if the PIDC is locked
        // If PIDC is locked, open dialog to unlock the PIDC
        boolean resultFlag = showUnlockPidcDialog(versionsPage);
        if (!resultFlag) {
          showVersionDialog(versionsPage);
        }
        else {
          CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);
        }
      }
    };
    // Set the image for PIDC attribute used question action
    this.newVersionAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NEW_VERSION_16X16));
    try {
      this.newVersionAction.setEnabled((null != currUser.getApicAccessRight()) &&
          currUser.hasNodeOwnerAccess(versionsPage.getPidcVersion().getPidcId()) &&
          !versionsPage.getPidcVersion().isDeleted() && (null != versionsPage.getSelectedPidcVersion()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    toolBarManager.add(this.newVersionAction);
  }

  /**
   * @param versionsPage
   */
  private void showVersionDialog(final PidcVersionsPage versionsPage) {
    // Perform action only if the PIDC is unlocked in session

    final PIDCVersionDetailsDialog vrsnDialog =
        new PIDCVersionDetailsDialog(Display.getDefault().getActiveShell(), versionsPage, false);
    vrsnDialog.open();


  }


  /**
   * @param versPage VersionsPage
   * @param versionDetails version name,desc
   */
  public void createNewRevision(final PidcVersionsPage versPage, final String[] versionDetails) {

    final PidcVersion selPidVersion = versPage.getSelectedPidcVersion();
    if (selPidVersion == null) {
      CDMLogger.getInstance().error("A reference PIDC version should be selected to create a new version",
          Activator.PLUGIN_ID);
      return;
    }
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Creating new revision...", 100);
        monitor.worked(20);
        PidcVersion newpidcVrsn = new PidcVersion();
        newpidcVrsn.setPidcId(selPidVersion.getPidcId());
        newpidcVrsn.setParentPidcVerId(selPidVersion.getId());
        newpidcVrsn.setName(versionDetails[0]);
        newpidcVrsn.setVersionName(versionDetails[0]);
        newpidcVrsn.setVersDescEng(versionDetails[1]);
        newpidcVrsn.setVersDescGer(versionDetails[2]);
        try {
          new PidcVersionServiceClient().createNewRevisionForPidcVers(newpidcVrsn);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

        monitor.worked(50);

      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      // Re-store Interrupted State
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param versionsPage pidcVersion Page
   */
  public void controlToolBarActions(final PidcVersionsPage versionsPage) {
    // TODO: Check if user privilege needs to be checked before enabling newVersionAction
    // since this code is called on refresh or delete operations which means user has access
    versionsPage.getActionSet().newVersionAction.setEnabled(true);

    versionsPage.getActionSet().lockAction
        .setEnabled(!versionsPage.getPidcVersion().getPidStatus().equals(PidcVersionStatus.LOCKED.getDbStatus()));
  }

  /**
   * The method inovkes dialog to rename sub-variant
   *
   * @param pidSubVar instance
   * @param pidcVersionBO pidcVersion BO
   * @throws ApicWebServiceException exception
   */
  public void renameSubVariant(final PidcSubVariant pidSubVar, final PidcVersionBO pidcVersionBO)
      throws ApicWebServiceException {
    PIDCRenameDialog renameDlg =
        new PIDCRenameDialog(Display.getDefault().getActiveShell(), pidSubVar, pidcVersionBO.getPidcVersion());
    renameDlg.open();

  }

  /**
   * This method is responsilbe to invoke open the pidc editor based on the selection of pidc in the treeviewer
   *
   * @param viewer instance
   * @param newlyCreatedPIDC true only when invoked from pidc creation wizrd
   */
  public void invokePIDCEditor(final TreeViewer viewer, final boolean newlyCreatedPIDC) {
    final ISelection selection = viewer.getSelection();
    if ((selection != null) && !selection.isEmpty()) {
      final Object element = ((IStructuredSelection) selection).getFirstElement();
      if (element instanceof PidcVersion) {
        openPIDCEditor((PidcVersion) element, newlyCreatedPIDC);
      }
    }
  }

  /**
   * This method is responsible to open the pidc editor
   *
   * @param newPidcVer selected PIDCVersion object
   * @param newlyCreatedPIDC newly createdPIDCVersion object
   * @return PIDCEditor obj
   */
  public PIDCEditor openPIDCEditor(final PidcVersion newPidcVer, final boolean newlyCreatedPIDC) {
    // ICDM-343
    PIDCEditor pidcEditor = null;
    PIDCEditorInput input;
    try {
      input = new PIDCEditorInput(newPidcVer, false);

      input.setNewlyCreatedPIDC(newlyCreatedPIDC);

      IEditorPart openEditor =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, PIDCEditor.EDITOR_ID);

      if (openEditor instanceof PIDCEditor) {
        pidcEditor = (PIDCEditor) openEditor;
      }
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    return pidcEditor;
  }

  /**
   * This method is responsible to open the pidc editor
   *
   * @param newPidcVer selected PIDCVersion object
   * @param ucItemId primary key of usecase item selected in outline view
   * @param newlyCreatedPIDC newly createdPIDCVersion object
   * @param projUseCase flag to check whether the editor should open in project use case section or normal use case
   * @return PIDCEditor obj
   */
  public PIDCEditor openPIDCEditor(final PidcVersion newPidcVer, final Long ucItemId, final boolean newlyCreatedPIDC,
      final boolean projUseCase) {
    // ICDM-343
    PIDCEditor pidcEditor = null;
    PIDCEditorInput input;
    try {
      input = new PIDCEditorInput(newPidcVer, false);
      input.setUcItemId(ucItemId);
      input.setNewlyCreatedPIDC(newlyCreatedPIDC);
      input.setProjUseCase(projUseCase);
      // flag to indicate that warn dialog should not appear while opening PIDC editor using usecase link
      input.setNotShowWarnDialog(true);
      IEditorPart openEditor =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, PIDCEditor.EDITOR_ID);

      if (openEditor instanceof PIDCEditor) {
        pidcEditor = (PIDCEditor) openEditor;
      }
    }

    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    return pidcEditor;
  }

  /**
   * @param manager Menu Manager
   * @param projectObjList project Object List
   */
  public void setAddToComparePIDCAction(final IMenuManager manager, final List<IProjectObject> projectObjList) {
    final Action createComparePIDCAction = new Action() {

      @Override
      public void run() {
        addToOpenPIDCCompareEditor(projectObjList);
      }
    };
    createComparePIDCAction.setText("Add to current Compare PIDC Editor");
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_ADD_16X16);
    createComparePIDCAction.setImageDescriptor(imageDesc);
    manager.add(createComparePIDCAction);

  }

  /**
   * @param projectObjList
   */
  private void addToOpenPIDCCompareEditor(final List<IProjectObject> projectObjList) {
    IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    if (activeEditor instanceof PIDCCompareEditor) {
      PIDCCompareEditor pidcCompareEditor = (PIDCCompareEditor) activeEditor;
      pidcCompareEditor.getComparePIDCPage().addToExistingCompareEditor(projectObjList);
    }
  }


  /**
   * The method inovkes dialog to rename variant
   *
   * @param pidVar instance
   * @param pidcVersionBO Pidc Version Handler
   * @throws ApicWebServiceException any error
   */
  public void renameVariant(final PidcVariant pidVar, final PidcVersionBO pidcVersionBO)
      throws ApicWebServiceException {
    PIDCRenameDialog renameDlg =
        new PIDCRenameDialog(Display.getDefault().getActiveShell(), pidVar, pidcVersionBO.getPidcVersion());
    renameDlg.open();
  }

  /**
   * @param pidcID :pidcard instance
   * @param pidcDetailsPage :page instance
   */
  public static void registerDetailsListener(final Long pidcID, final PIDCDetailsPageListener pidcDetailsPage) {
    List<PIDCDetailsPageListener> detailsPageListeners = pidcDetailsPageListeners.get(pidcID);
    if (detailsPageListeners == null) {
      detailsPageListeners = new ArrayList<>();
      detailsPageListeners.add(pidcDetailsPage);
      pidcDetailsPageListeners.put(pidcID, detailsPageListeners);
    }
    else {
      detailsPageListeners.add(pidcDetailsPage);
    }
  }


  /**
   * @param pidcID pid instance
   * @param pidcDetailsPage :page instance
   */
  public static void removeDetailsListener(final Long pidcID, final PIDCDetailsPageListener pidcDetailsPage) {
    List<PIDCDetailsPageListener> listeners = pidcDetailsPageListeners.get(pidcID);
    listeners.remove(pidcDetailsPage);
    if (listeners.isEmpty()) {
      pidcDetailsPageListeners.remove(pidcID);
    }

  }


  /**
   * This method creates new structure attribute
   *
   * @param manager : manager instance
   * @param level : level instance
   * @param pidcVersionBO : pidcard insttance
   */
  public void addNewStructureAttr(final IMenuManager manager, final long level, final PidcVersionBO pidcVersionBO) {
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    PidcVersion pidcVersion = pidcVersionBO.getPidcVersion();
    final Collection<IProjectAttribute> values = getStructureAttrs(pidcVersionBO);
    Action addNewStructAttrAction = new Action(Messages.getString(IMessageConstants.ADDSTRUCTATTR_LABEL), SWT.NONE) {

      /**
       * Open attribute selection dialog, if virtual structure display is enabled, else show message
       */
      @Override
      public void run() {
        unlockPIDC(currUser, apicBo, pidcVersion);
        // ICDM-2354
        if (apicBo.isPidcUnlockedInSession(pidcVersion)) {
          if (!PIDCDetailsViewPart.isVirtualStructureDisplayEnabled()) {// ICDM-1119
            CDMLogger.getInstance().warnDialog("Please activate 'Virtual Structure Display' to manage virtual nodes",
                Activator.PLUGIN_ID);
            return;
          }
          PidcAddStructAttrDialog addNewStructAttrDialog =
              new PidcAddStructAttrDialog(Display.getCurrent().getActiveShell(), level, pidcVersionBO, values);
          addNewStructAttrDialog.open();
        }
      }
    };
    // Set the image for add user action
    addNewStructAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VIR_NODE_ADD_16X16));
    try {
      addNewStructAttrAction.setEnabled(!values.isEmpty() && !pidcVersionBO.isDeleted() &&
          pidcVersionBO.isModifiable() && currUser.hasNodeOwnerAccess(pidcVersion.getPidcId()));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    manager.add(addNewStructAttrAction);
  }

  /**
   * @param currUser
   * @param apicBo
   * @param pidcVersion
   */
  private void unlockPIDC(final CurrentUserBO currUser, final ApicDataBO apicBo, final PidcVersion pidcVersion) {
    // ICDM-2487 P1.27.101
    // check if the pidc is unlocked in session
    try {
      if (!apicBo.isPidcUnlockedInSession(pidcVersion) && currUser.hasNodeWriteAccess(pidcVersion.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(pidcVersion);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param pidcVersionBO
   * @return This method gets the variant attributes to be filled in the Add Node dialog
   */
  private Collection<IProjectAttribute> getStructureAttrs(final PidcVersionBO pidcVersionBO) {

    final SortedSet<IProjectAttribute> tempSet = new TreeSet<>(pidcVersionBO.getAllVariantAttributes().values());

    // Removes the attributes already added as the virtual level nodes in the tree
    for (PidcDetStructure pidcdetStruct : pidcVersionBO.getPidcDataHandler().getPidcDetStructureMap().values()) {
      IProjectAttribute pidcAttr = pidcVersionBO.getAttributes(false).get(pidcdetStruct.getAttrId()) == null
          ? pidcVersionBO.getDeletedAttrsMap().get(pidcdetStruct.getAttrId())
          : pidcVersionBO.getAttributes(false).get(pidcdetStruct.getAttrId());
      if (pidcAttr != null) {
        tempSet.remove(pidcAttr);
      }
    }

    return tempSet;
  }


  /**
   * This method deletes a structure attribute
   *
   * @param manager : manager instance
   * @param pidcNode : pidcNode instance
   * @param pidcVersionBO : pidCardinstance
   */
  public void deleteStructureAttr(final IMenuManager manager, final PIDCDetailsNode pidcNode,
      final PidcVersionBO pidcVersionBO) {

    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    PidcVersion pidcVersion = pidcVersionBO.getPidcVersion();
    Action deleteStructAttrAction = new Action(Messages.getString(IMessageConstants.DELETESTRUCTATTR_LABEL), SWT.NONE) {

      @Override
      public void run() {
        // ICDM-2487 P1.27.101
        // check if the pidc is unlocked in session
        deleteStructAttr(pidcNode, currUser, apicBo, pidcVersion);
      }
    };
    // Set the image for delete the user
    deleteStructAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VIR_NODE_DEL_16X16));

    try {
      deleteStructAttrAction.setEnabled(!pidcVersionBO.isDeleted() && pidcVersionBO.isModifiable() &&
          currUser.hasNodeOwnerAccess(pidcVersion.getPidcId()) &&
          CommonUtils.isEqual(pidcVersion.getPidStatus(), PidcVersionStatus.IN_WORK.getDbStatus()));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    manager.add(deleteStructAttrAction);
  }


  /**
   * @param pidcNode
   * @param currUser
   * @param apicBo
   * @param pidcVersion
   */
  private void deleteStructAttr(final PIDCDetailsNode pidcNode, final CurrentUserBO currUser, final ApicDataBO apicBo,
      final PidcVersion pidcVersion) {
    try {
      if (!apicBo.isPidcUnlockedInSession(pidcVersion) && currUser.hasNodeWriteAccess(pidcVersion.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(pidcVersion);
      }
      // ICDM-2354
      if (apicBo.isPidcUnlockedInSession(pidcVersion)) {
        if (!PIDCDetailsViewPart.isVirtualStructureDisplayEnabled()) {// ICDM-1119
          CDMLogger.getInstance().warnDialog("Please activate 'Virtual Structure Display' to manage virtual nodes",
              Activator.PLUGIN_ID);
          return;
        }
        PidcDetStructureServiceClient pidcDetStructClient = new PidcDetStructureServiceClient();
        PidcDetStructure deletePidcDetStruct = pidcNode.getStructureObject();
        pidcDetStructClient.delete(deletePidcDetStruct);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Export dialog for pidc
   *
   * @param pidcVer selc pidcard
   */
  public void excelExport(final PidcVersion pidcVer) {
    final PIDCExportDialog exportDialog = new PIDCExportDialog(Display.getDefault().getActiveShell(), pidcVer);
    exportDialog.open();
  }

  /**
   * Icdm-1101 - new Action to Copy the Pidc Link to ClipBoard
   *
   * @param manager manager
   * @param pidcVersion pidCard version
   */
  public void copytoClipBoard(final IMenuManager manager, final PidcVersion pidcVersion) {
    // Copy Link Action
    final Action copyLink = new Action() {

      @Override
      public void run() {
        // ICDM-1649
        LinkCreator linkCreator = new LinkCreator(pidcVersion);
        try {
          linkCreator.copyToClipBoard();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy PIDC Version Link");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_LINK_12X12);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }

  // ICDM-1232
  /**
   * Action to create new outlook mail with Pidc Link
   *
   * @param manager menu manager
   * @param pidcVersion pid Card version
   */
  public void sendPidcVersionLinkInOutlook(final IMenuManager manager, final PidcVersion pidcVersion) {
    // ICDM-1649
    // PIDC Link in outlook Action
    final Action sendPidcLinkAction = new SendObjectLinkAction("Send PIDC Version Link", pidcVersion);

    manager.add(sendPidcLinkAction);
  }

  /**
   * @param manager menu manager
   * @param pidcVersion pidc version
   */
  public void createPidcShortcutAction(final IMenuManager manager, final PidcVersion pidcVersion) {
    final Action createPidcShortcutAction = new PIDCShortcutAction(pidcVersion);
    manager.add(createPidcShortcutAction);
  }

  /**
   * @param menuMgr MenuManager
   * @param selectedAttributes selected Project Attributes
   * @param pidcVer PIDCard version
   * @param pidcDataHndler Pidc Data Handler
   */
  // ICDM-1291
  public void addPidcSearchOption(final MenuManager menuMgr, final List<IProjectAttribute> selectedAttributes,
      final PidcVersion pidcVer, final PidcDataHandler pidcDataHndler) {
    // pidc search Action
    final PIDCSearchAction pidcSearchAction = new PIDCSearchAction(selectedAttributes, pidcVer, pidcDataHndler);
    menuMgr.add(pidcSearchAction);
  }


  /**
   * @return the editAction
   */
  public Action getEditAction() {
    return this.editAction;
  }

  /**
   * @return NewVersionAction
   */
  public Action getNewVersionAction() {
    return this.newVersionAction;
  }

  /**
   * @return LockAction
   */
  public Action getLockAction() {
    return this.lockAction;
  }

  /**
   * Method to move multiple attribute to variant
   *
   * @param menuMgr menu manager
   * @param selection selection
   * @param pidcVer pidc version
   * @param pidcPage pidc page
   */
  public void addMoveMulAttrToVariantMenu(final MenuManager menuMgr, final IStructuredSelection selection,
      final PidcVersion pidcVer, final PIDCAttrPage pidcPage) {
    final List<PidcNattableRowObject> selectedAttributes = selection.toList();
    boolean flag = true;
    for (PidcNattableRowObject attr : selectedAttributes) {
      IProjectAttribute pidcAttr = attr.getProjectAttributeHandler().getProjectAttr();
      if (pidcAttr.isAtChildLevel()) {
        CDMLogger.getInstance().info(VARIANT_ALREADY_MOVED, Activator.PLUGIN_ID);
        flag = false;
      }
    }
    if (flag) {
      Action moveMulAttrToVariantAction = new Action(ApicUiConstants.MOVE_TO_VARIANT, SWT.NONE) {

        @Override
        public void run() {
          moveMulAttrToVariant(pidcVer, pidcPage, selectedAttributes);

        }


      };
      moveMulAttrToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
      menuMgr.add(moveMulAttrToVariantAction);
    }

  }

  /**
   * @param selectedAttributes selected attributes
   * @param pidcVer pidc version
   * @param pidcPage pidc page
   */
  public void moveMulAttrToVariant(final PidcVersion pidcVer, final PIDCAttrPage pidcPage,
      final List<PidcNattableRowObject> selectedAttributes) {
    if (!CommonUtils.isNotEmpty(pidcPage.getPidcVersionBO().getPidcDataHandler().getVariantMap())) {
      MessageDialogUtils.getInfoMessageDialog("Variant Info", "No variants defined for this project ID card");
    }
    else {
      List<IProjectAttribute> projAttr = new ArrayList<>();
      selectedAttributes.forEach(rowObj ->

      projAttr.add(rowObj.getProjectAttributeHandler().getProjectAttr())

      );
      if (validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_VARIANT)) {
        ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
        moveModel.setPidcVersion(pidcVer);
        for (PidcNattableRowObject rowObject : selectedAttributes) {
          IProjectAttribute iProjectAttribute = rowObject.getProjectAttributeHandler().getProjectAttr();

          moveModel.getPidcAttrsToBeMovedDown().put(iProjectAttribute.getAttrId(),
              (PidcVersionAttribute) iProjectAttribute);

        }
        executeMoveCommand(moveModel);
      }
    }
  }

  /**
   * Method to move multiple attribute to pidc level
   *
   * @param menuMgr menu manager
   * @param selection selection
   * @param pidcVariant pidc variant
   * @param pidcPage pidc page
   */
  public void addMoveMulAttrToCommonMenu(final MenuManager menuMgr, final IStructuredSelection selection,
      final PidcVariant pidcVariant, final PIDCAttrPage pidcPage) {


    Action moveMulAttrToCommonAction = new Action("Move To Common", SWT.NONE) {

      @Override
      public void run() {
        final List<PidcNattableRowObject> selectedAttributes = selection.toList();
        moveMultipleAttrsToCommon(selectedAttributes, pidcVariant, pidcPage);
      }

    };
    moveMulAttrToCommonAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16));
    menuMgr.add(moveMulAttrToCommonAction);

  }

  /**
   * @param selectedAttributes selected attributes
   * @param pidcVariant pidc variant
   * @param pidcPage pidc page
   */
  public void moveMultipleAttrsToCommon(final List<PidcNattableRowObject> selectedAttributes,
      final PidcVariant pidcVariant, final PIDCAttrPage pidcPage) {


    boolean validate = validateMulAttrsToCommon(selectedAttributes, pidcPage.getPidcVersionBO());
    if (validate) {
      ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
      moveModel.setPidcVersion(pidcPage.getSelectedPidcVersion());
      for (PidcNattableRowObject rowObj : selectedAttributes) {
        IProjectAttribute iProjectAttribute = rowObj.getProjectAttributeHandler().getProjectAttr();
        PidcVariantAttribute varATtr = pidcPage.getPidcVersionBO().getPidcDataHandler().getVariantAttributeMap()
            .get(pidcVariant.getId()).get(iProjectAttribute.getAttrId());
        moveModel.getPidcVarAttrsToBeMovedUp().put(iProjectAttribute.getAttrId(), varATtr);
      }

      executeMoveCommand(moveModel);
    }
  }

  /**
   * @param selectedAttributes selected attributes
   * @param pidcVersionBO pidcversion bo
   * @return true if multiple attribut can be move to common
   */
  public boolean validateMulAttrsToCommon(final List<PidcNattableRowObject> selectedAttributes,
      final PidcVersionBO pidcVersionBO) {
    boolean isA2LMapped = false;
    // check if selected attribute is a predefined attribute
    List<IProjectAttribute> projAttr = new ArrayList<>();

    selectedAttributes.forEach(rowObj -> projAttr.add(rowObj.getProjectAttributeHandler().getProjectAttr()));

    if (validateIfPredef(projAttr, pidcVersionBO, MOVE_TO_COMMON)) {

      for (PidcNattableRowObject rowObj : selectedAttributes) {
        IProjectAttribute ipidcAttribute = rowObj.getProjectAttributeHandler().getProjectAttr();


        AttributeValue attributeValue =
            pidcVersionBO.getPidcDataHandler().getAttributeValueMap().get(ipidcAttribute.getValueId());
        if (null != attributeValue) {
          String oldPverName = attributeValue.getName();
          if (ipidcAttribute instanceof PidcVariantAttribute) {
            checkIfA2LFileMappedAndThrowDialog(oldPverName, pidcVersionBO);
            isA2LMapped = checkA2lExists((PidcVariantAttribute) ipidcAttribute, pidcVersionBO);
            break;
          }
        }

      }
    }
    else {
      return false;
    }
    if (isA2LMapped) {
      CDMLogger.getInstance().errorDialog(MODIFY_PVER_NAME_ERROR, Activator.PLUGIN_ID);
      return false;
    }
    boolean isvirtual = false;
    isvirtual = checkVirtualNode(isvirtual, selectedAttributes, pidcVersionBO);
    if (isvirtual) {
      CDMLogger.getInstance().errorDialog(PIDCActionSet.VIRTUAL_NODE_ERR, Activator.PLUGIN_ID);
      return false;
    }
    return true;
  }

  /**
   * Method to move multiple attribute to sub-variant
   *
   * @param menuMgr menu manager
   * @param selection selection
   * @param pidcPage pidc page
   */
  public void addMoveMulAttrToSubVarMenu(final MenuManager menuMgr, final IStructuredSelection selection,
      final PIDCAttrPage pidcPage) {
    final List<PidcNattableRowObject> selectedAttributes = selection.toList();
    boolean flag = true;
    for (PidcNattableRowObject rowObj : selectedAttributes) {
      IProjectAttribute attr = rowObj.getProjectAttributeHandler().getProjectAttr();
      if (attr.isAtChildLevel()) {
        CDMLogger.getInstance().info(VARIANT_ALREADY_MOVED, Activator.PLUGIN_ID);
        flag = false;
      }
    }
    if (flag) {
      final Action moveToSubVarAction = new Action(ApicUiConstants.MOVE_TO_SUB_VARIANT, SWT.NONE) {

        @Override
        public void run() {
          movePidcAttributeToSubvar(pidcPage, selectedAttributes);
        }
      };
      moveToSubVarAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SUBVAR_28X30));
      menuMgr.add(moveToSubVarAction);
    }
  }


  /**
   * @param pidcPage
   * @param selectedAttributes
   */
  private void movePidcAttributeToSubvar(final PIDCAttrPage pidcPage,
      final List<PidcNattableRowObject> selectedAttributes) {
    if (!checkSubVarAvailableForVar(pidcPage)) {
      MessageDialogUtils.getInfoMessageDialog("Sub Variant Info", "No Subvariants defined for this variant!");
    }
    else {
      List<IProjectAttribute> projAttr = new ArrayList<>();
      selectedAttributes.forEach(rowObj ->

      projAttr.add(rowObj.getProjectAttributeHandler().getProjectAttr())

      );
      if (validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_SUB_VARIANT)) {
        ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
        moveModel.setPidcVersion(pidcPage.getSelectedPidcVersion());
        for (PidcNattableRowObject rowObj : selectedAttributes) {
          IProjectAttribute iProjectAttribute = rowObj.getProjectAttributeHandler().getProjectAttr();
          moveModel.getPidcVarAttrsToBeMovedDown().put(iProjectAttribute.getAttrId(),
              (PidcVariantAttribute) iProjectAttribute);
        }
        executeMoveCommand(moveModel);
      }
    }
  }

  /**
   * Method to move multiple attribute to variant
   *
   * @param menuMgr
   * @param selection
   * @param pidcAttrTabViewer
   * @param selPIDCSubVar
   * @param pidcStatisticsLabel
   * @param pidcPage
   */
  public void addMoveMultAttrToVariantMenu(final MenuManager menuMgr, final IStructuredSelection selection,
      final PIDCAttrPage pidcPage) {
    final List<PidcNattableRowObject> selectedAttributes = selection.toList();
    Action moveMulAttrToVariantAction = new Action(ApicUiConstants.MOVE_TO_VARIANT, SWT.NONE) {

      @Override
      public void run() {

        ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(pidcPage.getPidcVersionBO());
        moveModel.setPidcVersion(pidcPage.getSelectedPidcVersion());

        List<IProjectAttribute> projAttr = new ArrayList<>();
        selectedAttributes.forEach(rowObj ->

        projAttr.add(rowObj.getProjectAttributeHandler().getProjectAttr())

        );
        if (validateIfPredef(projAttr, pidcPage.getPidcVersionBO(), MOVE_TO_VARIANT)) {
          for (PidcNattableRowObject rowObj : selectedAttributes) {
            IProjectAttribute iProjectAttribute = rowObj.getProjectAttributeHandler().getProjectAttr();
            moveModel.getPidcSubVarAttrsToBeMovedToVariant().put(iProjectAttribute.getAttrId(),
                (PidcSubVariantAttribute) iProjectAttribute);
          }

          executeMoveCommand(moveModel);


        }

      }
    };
    moveMulAttrToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
    menuMgr.add(moveMulAttrToVariantAction);
  }

  /**
   * @param pidcVer
   * @param isvirtual
   * @param selectedAttributes
   * @param pidcVersionBO
   * @return
   */
  private boolean checkVirtualNode(final boolean isvirtual, final List<PidcNattableRowObject> selectedAttributes,
      final PidcVersionBO pidcVersionBO) {
    boolean flag = isvirtual;
    for (PidcNattableRowObject rowObj : selectedAttributes) {
      IProjectAttribute pidcAttrVar = rowObj.getProjectAttributeHandler().getProjectAttr();
      for (PidcDetStructure pidcdetStruct : pidcVersionBO.getVirtualLevelAttrs().values()) {
        if (pidcAttrVar.getAttrId().equals(pidcdetStruct.getAttrId())) {
          flag = true;
        }

      }
    }
    return flag;
  }

  /**
   * @param showDialog boolean
   */
  public void showEditNotAllowedDialog(final boolean showDialog) {
    if (showDialog) {
      CDMLogger.getInstance().errorDialog("Edit not allowed!", Activator.PLUGIN_ID);
    }
  }


  /**
   * @param showDialog boolean
   */
  public void showInvalidSelectionDialog(final boolean showDialog) {
    if (showDialog) {
      CDMLogger.getInstance().errorDialog("Invalid Selection!", Activator.PLUGIN_ID);
    }
  }


  /**
   * @param pidcAttrVar
   * @param pidcVer
   * @param pidcVersionBO
   * @param isA2LMapped
   * @return
   */
  public boolean checkA2lExists(final PidcVariantAttribute pidcAttrVar, final PidcVersionBO pidcVersionBO) {
    SortedSet<SdomPVER> pVerSet = pidcVersionBO.getPVerSet();
    Attribute attr = pidcVersionBO.getPidcDataHandler().getAttribute(pidcAttrVar.getAttrId());
    if ((attr.getLevel() == ApicConstants.SDOM_PROJECT_NAME_ATTR) && (null != pidcAttrVar.getValue())) {
      for (SdomPVER pver : pVerSet) {

        if (!pidcVersionBO.getMappedA2LFiles(pver.getPverName(), pver.getPidcVersion().getId()).isEmpty()) {
          // checking if a2l files mapped
          return true;
        }
      }
    }
    return false;
  }

  // ICDM-1750
  /**
   * @param menuMgr
   * @param name
   */
  public void copyAttrValueMenu(final MenuManager menuMgr, final String name) {
    final Action copyValueAction = new Action() {

      @Override
      public void run() {
        CommonUiUtils.setTextContentsToClipboard(name);
      }
    };
    copyValueAction.setText("Copy text to Clipboard");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_EDIT_16X16);
    copyValueAction.setImageDescriptor(imageDesc);
    menuMgr.add(copyValueAction);
    copyValueAction.setEnabled(true);


  }

  /**
   * @param pidcNode
   * @return
   */
  public boolean hasCdrResults(final Long pidcVersionId) {
    try {
      return new CDRReviewResultServiceClient().hasPidcRevResults(pidcVersionId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * ICDM-1903
   *
   * @param menuMgr
   * @param pidcAttribute
   */
  public void markAttrHiddenMenu(final MenuManager menuMgr, final PidcVersionAttribute pidcAttribute,
      final PidcDataHandler pidcDataHndler) {
    final Action markHiddenAction = new Action() {

      @Override
      public void run() {
        updatePidcAttribute(pidcAttribute, pidcDataHndler);
      }
    };
    if (pidcAttribute.isAttrHidden()) {
      markHiddenAction.setText("Unmark as hidden");
    }
    else {
      markHiddenAction.setText("Mark as hidden");
    }
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.HIDE_16X16);
    markHiddenAction.setImageDescriptor(imageDesc);
    menuMgr.add(markHiddenAction);
    markHiddenAction.setEnabled(true);
  }


  /**
   * @param pidcAttribute
   * @param pidcDataHndler
   */
  private void updatePidcAttribute(final PidcVersionAttribute pidcAttribute, final PidcDataHandler pidcDataHndler) {
    ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
    updationModel.setPidcVersion(pidcDataHndler.getPidcVersionInfo().getPidcVersion());

    pidcAttribute.setAttrHidden(!pidcAttribute.isAttrHidden());
    pidcAttribute.setUsedFlag(ApicConstants.CODE_YES);
    if (pidcAttribute.getId() == null) {
      updationModel.getPidcAttrsToBeCreated().put(pidcAttribute.getAttrId(), pidcAttribute);
    }
    else {
      updationModel.getPidcAttrsToBeUpdated().put(pidcAttribute.getAttrId(), pidcAttribute);
    }

    ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
    try {
      upClient.updatePidcAttrs(updationModel);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  // ICDM-1701
  /**
   * @param manager
   * @param pidcVersion
   */
  public void openPidcFromCDRReport(final IMenuManager manager, final PidcVersion pidcVersion) {
    final Action openPidcAction = new Action() {

      @Override
      public void run() {
        PIDCActionSet.this.openPIDCEditor(pidcVersion, false);
      }
    };
    openPidcAction.setText(OPEN_PIDC_VERSION);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
    openPidcAction.setImageDescriptor(imageDesc);
    manager.add(openPidcAction);

  }

  /**
   * @param pidcVersion
   * @param focusMatrixVersion
   * @param focusMatrixPageNew
   */
  public void resetRvwStatusToNo(final com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion focusMatrixVersion,
      final FocusMatrixPage focusMatrixPageNew) {
    String rvwStatus = focusMatrixVersion.getRvwStatus();
    FM_REVIEW_STATUS rvwStatusEnum = FocusMatrixVersionClientBO.FM_REVIEW_STATUS.getStatus(rvwStatus);
    if (((rvwStatusEnum != FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NOT_DEFINED) &&
        (rvwStatusEnum != FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NO)) ||
        (rvwStatusEnum == FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NOT_DEFINED)) {
      confirmResetAndCallCommand(focusMatrixVersion, focusMatrixPageNew);
    }
    else {
      if (null != focusMatrixPageNew) {
        focusMatrixPageNew.getNoOption().setSelection(false);
      }
    }
  }

  /**
   * @param focusMatrixVersion FocusMatrixVersion
   * @param focusMatrixPageNew FocusMatrixPage
   */
  private void confirmResetAndCallCommand(
      final com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion focusMatrixVersion,
      final FocusMatrixPage focusMatrixPageNew) {
    boolean flag = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Confirm",
        "Focus Matrix review status will be reset . Continue ? ");
    if (flag) {
      if (null != focusMatrixPageNew) {
        focusMatrixPageNew.getRvwByTextField().setText("");
        focusMatrixPageNew.getRvwonTextField().setText("");
        focusMatrixPageNew.getRemarkTextField().setText("");
        focusMatrixPageNew.getLink().setText("");
      }

      FocusMatrixVersionServiceClient fmVersionServiceClient = new FocusMatrixVersionServiceClient();

      FocusMatrixVersion clonedFmVersion = focusMatrixVersion.clone();
      clonedFmVersion.setRvwStatus(FocusMatrixVersionClientBO.FM_REVIEW_STATUS.NO.getStatusStr());
      clonedFmVersion.setReviewedUser(null);
      clonedFmVersion.setReviewedDate(null);
      clonedFmVersion.setLink(null);
      clonedFmVersion.setRemark(null);
      try {
        fmVersionServiceClient.update(clonedFmVersion);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

    }
    else {
      if (null != focusMatrixPageNew) {
        focusMatrixPageNew.getNoOption().setSelection(false);
      }
    }
  }

  /**
   * @param pidVar
   */
  private boolean isVariantHasReviews(final PidcVariant pidVar) {


    PidcVariantServiceClient varClient = new PidcVariantServiceClient();
    try {
      Boolean hasReviews = varClient.hasReviews(pidVar);
      if (hasReviews.booleanValue()) {
        CDMLogger.getInstance().warnDialog(
            "You can't rename this variant because there are review results in other PIDC versions of this variant. Please send a mail to the iCDM hotline, containing the PIDC name and the old and new variant name",
            Activator.PLUGIN_ID);
      }
      return hasReviews;
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    return false;
  }


  /**
   * Icdm-1101 - new Action to Copy the Pidc Link to ClipBoard
   *
   * @param manager manager
   * @param pidcVer pidCard version
   */
  public void copyVarLinktoClipBoard(final IMenuManager manager, final PidcVariant pidVar) {
    // Copy Link Action
    final Action copyLink = new Action() {

      @Override
      public void run() {
        // ICDM-1649
        LinkCreator linkCreator = new LinkCreator(pidVar);
        try {
          linkCreator.copyToClipBoard();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy PIDC Variant Link");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_LINK_12X12);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }

  public void copySubVarLinktoClipBoard(final IMenuManager manager, final PidcSubVariant pidcSubVar) {
    // Copy Link Action
    final Action copyLink = new Action() {

      @Override
      public void run() {
        // ICDM-1649
        LinkCreator linkCreator = new LinkCreator(pidcSubVar);
        try {
          linkCreator.copyToClipBoard();
        }
        catch (ExternalLinkException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    };
    copyLink.setText("Copy PIDC Sub Variant Link");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_LINK_12X12);
    copyLink.setImageDescriptor(imageDesc);
    copyLink.setEnabled(true);
    manager.add(copyLink);
  }

  /**
   * Action to create new outlook mail with Pidc Link
   *
   * @param manager menu manager
   * @param pidcVer pid Card version
   */
  public void sendPidcVariantLinkInOutlook(final IMenuManager manager, final PidcVariant pidVar) {
    // ICDM-1649
    // PIDC Link in outlook Action
    final Action sendPidcVarLinkAction = new SendObjectLinkAction("Send PIDC Variant Link", pidVar);
    manager.add(sendPidcVarLinkAction);
  }

  /**
   * @param manager
   * @param pidSubVar
   */
  public void sendPidcSubVariantLinkInOutlook(final IMenuManager manager, final PidcSubVariant pidSubVar) {
    // ICDM-1649
    // PIDC Link in outlook Action
    final Action sendPidcVarLinkAction = new SendObjectLinkAction("Send PIDC Sub Variant Link", pidSubVar);
    manager.add(sendPidcVarLinkAction);
  }

  /**
   * @param menuMgr
   * @param editableAttr
   * @param selection
   * @param pidcVersionObj
   */
  public void addShowAttrValsOption(final MenuManager menuMgr, final PIDCAttrPage pidcPage,
      final IProjectAttribute editableAttr) {

    Action showvalsAction = new Action("Show attribute values", SWT.NONE) {

      @Override
      public void run() {
        final PIDCAttrValueEditDialog dialog =
            new PIDCAttrValueEditDialog(pidcPage.getNatTable().getShell(), pidcPage, editableAttr, null);
        dialog.setReadOnlyMode(true);
        dialog.open();
      }
    };
    showvalsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALUE_EDIT_28X30));
    menuMgr.add(showvalsAction);

  }

  /**
   * @param manager
   * @param pidcVars
   */
  public void addWebFlowJobForMulVar(final IMenuManager manager, final PidcVariant[] pidVars,
      final PidcVersionBO pidcVersionBO) {

    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();

    // web flow job action for variant
    final Action webflowVarAction = new Action() {

      @Override
      public void run() {

        try {
          if (!apicBo.isPidcUnlockedInSession(pidcVersionBO.getPidcVersion()) &&
              (null != currUser.getNodeAccessRight(pidcVersionBO.getPidcVersion().getPidcId())) &&
              currUser.hasNodeReadAccess(pidcVersionBO.getPidcVersion().getPidcId())) {
            showUnlockDialog(pidcVersionBO.getPidcVersion());
          }

          startWebFlowJobIfValid(pidVars, pidcVersionBO);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };
    setUIOptions(manager, pidcVersionBO.getPidcVersion(), checkForDelVar(pidVars), webflowVarAction);
  }

  /**
   * @param manager
   * @param pidcVer
   * @param hasDelVar
   * @param currentUserRight
   * @param webflowVarAction
   */
  private void setUIOptions(final IMenuManager manager, final PidcVersion pidcVer, final boolean hasDelVar,
      final Action webflowVarAction) {
    webflowVarAction.setText(ApicUiConstants.START_WEB_FLOW_JOB);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.WEBFLOW_16X16);
    webflowVarAction.setImageDescriptor(imageDesc);

    CurrentUserBO currentUser = new CurrentUserBO();

    try {
      webflowVarAction.setEnabled((currentUser.getNodeAccessRight(pidcVer.getPidcId()) != null) &&
          (currentUser.hasNodeReadAccess(pidcVer.getPidcId())) && !hasDelVar && !pidcVer.isDeleted());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    manager.add(webflowVarAction);
  }

  /**
   * @param pidVars
   * @param pidcVer
   * @param currentUserRight
   * @throws ApicWebServiceException
   */
  private void startWebFlowJobIfValid(final PidcVariant[] pidVars, final PidcVersionBO pidcVersionBO)
      throws ApicWebServiceException {
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();

    if (apicBo.isPidcUnlockedInSession(pidcVersionBO.getPidcVersion()) &&
        (null != currUser.getNodeAccessRight(pidcVersionBO.getPidcVersion().getPidcId())) &&
        currUser.hasNodeReadAccess(pidcVersionBO.getPidcVersion().getPidcId())) {
      Set<String> invalidAttrs = areVarValid(pidVars, pidcVersionBO);
      if (invalidAttrs.isEmpty()) {
        Long elementID = getElementID(pidVars);
        if (elementID != null) {
          startMulVariantWebflowJob(elementID);
        }
      }
      else {
        StringBuilder errorMsg = new StringBuilder();
        for (String attrName : invalidAttrs) {
          errorMsg.append(attrName);
          errorMsg.append("; ");
        }
        CDMLogger.getInstance().errorDialog(
            "No differences allowed in attribute values. These attributes must be unique: " + errorMsg,
            Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param pidVars
   * @param pidcVer
   * @return
   */
  private Set<String> areVarValid(final PidcVariant[] pidVars, final PidcVersionBO pidcVersionBO) {
    Set<String> inValidAttr = new HashSet<>();
    String[] attrIdsArr = getAttrIdsArray();
    for (String attrIdString : attrIdsArr) {
      Long attrId = Long.valueOf(attrIdString);
      if (pidcVersionBO.getAttributesAll().get(attrId).isAtChildLevel()) {
        getAttrIdAndValidate(pidVars, pidcVersionBO, inValidAttr, attrId);
      }
    }
    return inValidAttr;
  }


  /**
   * @param pidVars
   * @param pidcVersionBO
   * @param inValidAttr
   * @param attrId
   */
  private void getAttrIdAndValidate(final PidcVariant[] pidVars, final PidcVersionBO pidcVersionBO,
      final Set<String> inValidAttr, final Long attrId) {
    Long valId = null;
    boolean isAttrValFetch = false;
    boolean isValNotSet = false;
    for (PidcVariant var : pidVars) {
      PidcVariantBO variantHandler =
          new PidcVariantBO(pidcVersionBO.getPidcVersion(), var, pidcVersionBO.getPidcDataHandler());
      PidcVariantAttribute pidcAttributeVar = variantHandler.getAttributesAll().get(attrId);

      if (!isAttrValFetch) {
        if (pidcAttributeVar.isAtChildLevel()) {
          valId = getValIdForPidcAttrVarAtChildLvl(pidcVersionBO, attrId, valId, variantHandler);
        }
        else {
          if (null != pidcAttributeVar.getValue()) {
            valId = pidcAttributeVar.getValueId();
          }
        }
        if (valId == null) {
          isValNotSet = true;
        }
        isAttrValFetch = true;
      }
      validateAttr(attrId, variantHandler, inValidAttr, valId, isValNotSet, pidcAttributeVar);
    }
  }


  /**
   * @param pidcVersionBO
   * @param attrId
   * @param valId
   * @param variantHandler
   * @return
   */
  private Long getValIdForPidcAttrVarAtChildLvl(final PidcVersionBO pidcVersionBO, final Long attrId, Long valId,
      final PidcVariantBO variantHandler) {
    PidcSubVariantBO subVarHandler = new PidcSubVariantBO(pidcVersionBO.getPidcVersion(),
        variantHandler.getSubVariantsSet().first(), pidcVersionBO.getPidcDataHandler());
    PidcSubVariantAttribute subvarAttr = subVarHandler.getAttributesAll().get(attrId);
    if (null != subvarAttr.getValue()) {
      valId = subvarAttr.getValueId();
    }
    return valId;
  }


  /**
   * @param dataBo
   * @return
   */
  private String[] getAttrIdsArray() {
    CommonDataBO dataBo = new CommonDataBO();
    String[] attrIdsArr = null;
    try {
      attrIdsArr = dataBo.getParameterValue(CommonParamKey.WEB_FLOW_UNIQUE_ATTR).split(",");
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return attrIdsArr;
  }

  /**
   * @param attrId
   * @param var
   * @param inValidAttr
   * @param valId
   * @param isValNotSet
   * @param pidcAttributeVar
   */
  private void validateAttr(final Long attrId, final PidcVariantBO variantHandler, final Set<String> inValidAttr,
      final Long valId, final boolean isValNotSet, final PidcVariantAttribute pidcAttributeVar) {
    if (pidcAttributeVar.isAtChildLevel()) {
      for (PidcSubVariant subVar : variantHandler.getSubVariantsSet()) {
        PidcSubVariantBO subVarHandler =
            new PidcSubVariantBO(variantHandler.getPidcVersion(), subVar, variantHandler.getPidcDataHandler());
        PidcSubVariantAttribute subvarAttr = subVarHandler.getAttributesAll().get(attrId);
        collectInvalidattr(inValidAttr, valId, isValNotSet, subvarAttr);
      }
    }
    else {
      collectInvalidattr(inValidAttr, valId, isValNotSet, pidcAttributeVar);
    }
  }

  /**
   * @param inValidAttr
   * @param valId
   * @param isValNotSet
   * @param pidcAttributeVar
   */
  private void collectInvalidattr(final Set<String> inValidAttr, final Long valId, final boolean isValNotSet,
      final IProjectAttribute attr) {
    if (attr instanceof PidcVariantAttribute) {
      fetchVarInvalidAttr(inValidAttr, valId, isValNotSet, attr);
    }
    else if (attr instanceof PidcSubVariantAttribute) {
      fetchSubvarInvalidAttr(inValidAttr, valId, isValNotSet, attr);
    }
  }

  /**
   * @param inValidAttr
   * @param valId
   * @param isValNotSet
   * @param attr
   */
  private void fetchSubvarInvalidAttr(final Set<String> inValidAttr, final Long valId, final boolean isValNotSet,
      final IProjectAttribute attr) {
    PidcSubVariantAttribute subvarAttr = (PidcSubVariantAttribute) attr;
    if (isValNotSet) {
      if (null != subvarAttr.getValue()) {
        inValidAttr.add(subvarAttr.getName());
      }
    }
    else if ((null == subvarAttr.getValue()) ||
        ((null != subvarAttr.getValue()) && (!valId.equals(subvarAttr.getValueId())))) {
      inValidAttr.add(subvarAttr.getName());
    }
  }

  /**
   * @param inValidAttr
   * @param valId
   * @param isValNotSet
   * @param attr
   */
  private void fetchVarInvalidAttr(final Set<String> inValidAttr, final Long valId, final boolean isValNotSet,
      final IProjectAttribute attr) {
    PidcVariantAttribute varAttr = (PidcVariantAttribute) attr;
    if (isValNotSet) {
      if (null != varAttr.getValue()) {
        inValidAttr.add(varAttr.getName());
      }
    }
    else if ((null == varAttr.getValue()) || ((null != varAttr.getValue()) && (!valId.equals(varAttr.getValueId())))) {
      inValidAttr.add(varAttr.getName());
    }
  }

  /**
   * @param pidVars
   * @param hasDelVar
   * @return
   */
  private boolean checkForDelVar(final PidcVariant[] pidVars) {
    for (PidcVariant var : pidVars) {
      if (var.isDeleted()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param pidVars
   * @return
   */
  private Long getElementID(final PidcVariant[] pidVars) {
    PidcWebFlowHandler webFlowHandler = new PidcWebFlowHandler();
    Set<Long> pidcVarIdList = new HashSet<>();
    for (PidcVariant var : pidVars) {
      pidcVarIdList.add(var.getId());
    }
    return webFlowHandler.getElementIdForVars(pidcVarIdList);
  }

  /**
   * @param elementID
   * @return
   */
  private void startMulVariantWebflowJob(final long elementID) {
    String webFlowLink = getWebFlowLinkForMulVar(elementID);
    if (webFlowLink != null) {
      // Open link for Webflow job in browser
      CommonActionSet action = new CommonActionSet();
      action.openExternalBrowser(webFlowLink);
    }
  }

  /**
   * @param apicDataProvider
   * @param elementID
   * @return
   */
  public String getWebFlowLinkForMulVar(final long elementID) {
    try {
      return new CommonDataBO().getParameterValue(CommonParamKey.WEB_FLOW_JOB_LINK)
          .replace(ApicUiConstants.WEB_FLOW_JOB_ELEMENT_ID, String.valueOf(elementID));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * This method do validation for pver name to modify gor given pidc attribute<br>
   * 1. checks for pidc attribute has sdom level and the sdom pver name has not null value <br>
   * 2. checks the given IPIDCAttribute pidcAttribute for PIDC level <br>
   * 3. Get pver set and iterates each Pver name with old pver name <br>
   * 4. if pver have mapping to any a2l file throw error dialog <br>
   * 5. checks the given IPIDCAttribute pidcAttribute for Variant level 6. Get all variant and Iterate only active
   * variant <br>
   * 7. if other variants have the old pver name, then pver can be modified and return true <br>
   * 8. if other variants does not have the old pver name, then pver cannot be modified and return false
   *
   * @param pidcAttribute
   * @return
   */
  // ICDM-1872
  public boolean isPverNameEditable(final IProjectAttribute pidcAttribute, final AbstractProjectObjectBO projObjBO) {
    boolean a2lFileAlreadyMappedFlag = false;

    PidcDataHandler pidcDataHandler = projObjBO.getPidcDataHandler();
    if ((pidcDataHandler.getAttributeMap().get(pidcAttribute.getAttrId()).getLevel()
        .equals(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR))) && (null != pidcAttribute.getValueId())) {
      String oldPverName = pidcAttribute.getValue();
      if (pidcAttribute instanceof PidcVersionAttribute) {
        a2lFileAlreadyMappedFlag = checkIfA2LFileMappedAndThrowDialog(oldPverName, projObjBO);
      }
      else if (pidcAttribute instanceof PidcVariantAttribute) {

        boolean pverAvailableInOtherVariants =
            checkIfPverAvailableInOtherVariants(pidcAttribute, pidcDataHandler, oldPverName);
        // if pver name not available in other variants, then check for a2l file mapping
        if (!pverAvailableInOtherVariants) {
          a2lFileAlreadyMappedFlag = checkIfA2LFileMappedAndThrowDialog(oldPverName, projObjBO);
        }
      }
    }
    // if a2l file not mapped, then allow to change pver name (means valid to edit the pver name)
    return !a2lFileAlreadyMappedFlag;
  }


  /**
   * @param pidcAttribute
   * @param pidcDataHandler
   * @param oldPverName
   * @return
   */
  private boolean checkIfPverAvailableInOtherVariants(final IProjectAttribute pidcAttribute,
      final PidcDataHandler pidcDataHandler, final String oldPverName) {
    Map<Long, PidcVariant> variantMap = pidcDataHandler.getVariantMap();
    boolean pverAvailableInOtherVariants = false;
    String variantNameInPage = variantMap.get(((PidcVariantAttribute) pidcAttribute).getVariantId()).getName();
    for (PidcVariant pidcVariant : variantMap.values()) {
      if (!pidcVariant.isDeleted()) {
        String variantNameInSet = pidcVariant.getName();
        // check with other variants whether this pver name availabe for
        // other variants and
        // --> if available, allow to change pver name
        // --> if not available, check for a2l file mapping with version
        if (!CommonUtils.isEqualIgnoreCase(variantNameInSet, variantNameInPage)) {
          Map<Long, PidcVariantAttribute> map = pidcDataHandler.getVariantAttributeMap().get(pidcVariant.getId());
          AttributeValue attributeValue =
              pidcDataHandler.getAttributeValue(map.get(pidcAttribute.getAttrId()).getValueId());
          if (attributeValue != null) {
            String pverValue = attributeValue.getName();
            if (CommonUtils.isEqualIgnoreCase(pverValue, oldPverName)) {
              pverAvailableInOtherVariants = true;
              break;
            }
          }
        }
      }
    }
    return pverAvailableInOtherVariants;
  }

  /**
   * This method used to check if any a2l files mapped to this pidc version and if available throw error dialog
   */
  private boolean checkIfA2LFileMappedAndThrowDialog(final String oldPverName, final AbstractProjectObjectBO handler) {

    SortedSet<SdomPVER> pverSet = handler.getPVerSet();
    for (SdomPVER pver : pverSet) {
      if (CommonUtils.isEqualIgnoreCase(pver.getPverName(), oldPverName) &&
          !handler.getMappedA2LFiles(pver.getPverName(), pver.getPidcVersion().getId()).isEmpty()) {
        String message = "";
        try {
          message = new CommonDataBO().getMessage("PIDC_EDITOR", "A2L_MAP_ERROR");
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

        CDMLogger.getInstance().errorDialog(message, Activator.PLUGIN_ID);
        return true;
      }
    }
    return false;
  }

  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // ------------------ From PIDCActionSetNew
  // ---------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  // --------------------------------------------------------------------------------------------------------------------
  /**
   * @param manager IMenuManager
   * @param pidcTreeNode PidcTreeNode
   */
  public void addOpenPidcAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    OpenNewPidcAction openAction = new OpenNewPidcAction(pidcTreeNode.getPidcVersion(), OPEN_PIDC_VERSION);
    manager.add(openAction);
  }

  /**
   * This method is responsible to open the pidc editor
   *
   * @param newPidcVer selected PIDCVersion object
   */
  public void openPIDCEditor(final PidcVersion newPidcVer) {

    // TO-DO check for hiden
    if (newPidcVer.isDeleted()) {
      MessageDialogUtils.getInfoMessageDialog("Hidden PIDC Version", "This PIDC version is hidden !");
      CDMLogger.getInstance().info("This PIDC version is hidden !", Activator.PLUGIN_ID);
    }
    else {
      OpenNewPidcAction openAction = new OpenNewPidcAction(newPidcVer, OPEN_PIDC_VERSION);
      openAction.run();
    }

  }

  /**
   * @param manager IMenuManager
   * @param pidcVersion pidc version
   * @param pidc PIDC
   */
  public void addRenamePidcAction(final IMenuManager manager, final PidcVersion pidcVersion, final Pidc pidc) {
    // rename action
    final Action addRenameAction = new Action() {

      @Override
      public void run() {
        // Check if the pidc is unlocked in session
        CurrentUserBO currUser = new CurrentUserBO();
        ApicDataBO apicBo = new ApicDataBO();
        try {
          if (!apicBo.isPidcUnlockedInSession(pidcVersion) && currUser.hasNodeWriteAccess(pidc.getId())) {
            final PIDCActionSet pidcActionSet = new PIDCActionSet();
            pidcActionSet.showUnlockPidcDialog(pidcVersion);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        if (apicBo.isPidcUnlockedInSession(pidcVersion)) {
          // If PIDc is unlocked in session, rename pidc
          renamePIDC(pidcVersion, pidc);
        }
      }
    };
    addRenameAction.setText(ApicUiConstants.RENAME_PIDC_ACTION);
    // Image descriptor for Rename action
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RENAME_16X16);
    addRenameAction.setImageDescriptor(imageDesc);

    CurrentUserBO currUser = new CurrentUserBO();

    try {
      addRenameAction.setEnabled((null != currUser.getApicAccessRight()) && currUser.hasNodeOwnerAccess(pidc.getId()) &&
          !pidcVersion.isDeleted());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    manager.add(addRenameAction);
  }

  /**
   * @param pidcVersion PIDC version
   * @return true if user select to unlock PIDC
   */
  public boolean showUnlockPidcDialog(final PidcVersion pidcVersion) {

    String[] pidcNameArr = pidcVersion.getName().split("\\(");
    boolean unlockFlag = MessageDialogUtils.getQuestionMessageDialog("Unlock PIDC", "PIDC: " + pidcNameArr[0].trim() +
        " is locked at the moment and can't be edited. Do you want to unlock the PIDC?");

    PIDCEditor pidcEditor = null;

    if (unlockFlag) {

      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        IEditorPart openedEditor =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if ((openedEditor instanceof PIDCEditor)) {
          pidcEditor = (PIDCEditor) openedEditor;
        }
      }
      if (null != pidcEditor) {
        PIDCSessionLockAction pidcLockAction = new PIDCSessionLockAction(pidcVersion);
        pidcLockAction.run();
        pidcLockAction.setEnabled(true);
        if (pidcEditor.isAttributePage()) {
          pidcEditor.getPidcPage().getPidcLockAction().setActionProperties();
        }
        if (pidcEditor.isCocWpPage()) {
          pidcEditor.getPidcCoCWpPage().getPidcLockAction().setActionProperties();
        }
      }
      else {
        new ApicDataBO().setPidcUnLockedInSession(pidcVersion);
      }
    }

    return unlockFlag;
  }


  /**
   * This method is used to open pidc compare editor
   *
   * @param compareObjs list of versions to be compared
   * @param projObjBO AbstractProjectObjectBO
   * @return editor instance
   */
  public PIDCCompareEditor openCompareEditor(final List<IProjectObject> compareObjs,
      final AbstractProjectObjectBO projObjBO) {
    final PIDCCompareEditorInput input = new PIDCCompareEditorInput(compareObjs, projObjBO);
    PIDCCompareEditor compareEditor = null;
    try {
      IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
          PIDCCompareEditor.EDITOR_ID);
      if (openEditor instanceof PIDCCompareEditor) {
        compareEditor = (PIDCCompareEditor) openEditor;
      }

    }
    catch (PartInitException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return compareEditor;
  }

  /**
   * @param manager : manager instance
   * @param pidVars : selected pid variants
   * @param pidSubVars : selected pid sub variants
   * @param editorPIDVersion : selected PIDCard instance
   * @param pidcPage pidc attribute page
   */
  // ICDM-270
  public void addValue(final IMenuManager manager, final PidcVariant[] pidVars, final PidcSubVariant[] pidSubVars,
      final PidcVersion editorPIDVersion, final PIDCAttrPage pidcPage) {

    final Action addVariantValueAction = new Action() {

      @Override
      public void run() {
        PIDCVariantValueDialog variantValueDialog;
        if (pidSubVars == null) {
          variantValueDialog = new PIDCVariantValueDialog(Display.getDefault().getActiveShell(), pidVars, null,
              editorPIDVersion, pidcPage);
          if (pidVars != null) {
            checkPIDCLock(pidcPage.getProjectObjectBO());
            variantValueDialog.open();
          }
        }
        else if (pidVars == null) {
          variantValueDialog = new PIDCVariantValueDialog(Display.getDefault().getActiveShell(), null, pidSubVars,
              editorPIDVersion, pidcPage);
          checkPIDCLock(pidcPage.getProjectObjectBO());
          variantValueDialog.open();
        }
      }
    };

    addVariantValueAction.setText("Set Value");

    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.VALUE_EDIT_28X30);
    addVariantValueAction.setImageDescriptor(imageDesc);

    addVariantValueAction.setEnabled(pidcPage.getProjectObjectBO().isModifiable() &&
        (editorPIDVersion.getPidStatus().equals(PidcVersionStatus.IN_WORK.getDbStatus())));

    manager.add(addVariantValueAction);

  }


  /**
  *
  */
  private void checkPIDCLock(final AbstractProjectObjectBO projObjBO) {
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    try {
      if (!apicBo.isPidcUnlockedInSession(projObjBO.getPidcVersion()) &&
          currUser.hasNodeWriteAccess(projObjBO.getPidcVersion().getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(projObjBO.getPidcVersion());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param manager IMenuManager
   * @param compareObjs list of proj obj
   * @param projObjBO AbstractProjectObjectBO
   */
  public void setCompareAction(final IMenuManager manager, final List<IProjectObject> compareObjs,
      final AbstractProjectObjectBO projObjBO) {
    final Action createComparePIDCAction = new Action() {

      @Override
      public void run() {
        openCompareEditor(compareObjs, projObjBO);
      }
    };
    if (compareObjs.get(0) instanceof PidcVariant) {
      createComparePIDCAction.setText("Project Variant Compare");
    }
    else if (compareObjs.get(0) instanceof PidcSubVariant) {
      createComparePIDCAction.setText("Project Sub-Variant Compare");
    }
    else {
      createComparePIDCAction.setText("Compare PIDC");
    }

    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COMPARE_EDITOR_16X16);
    createComparePIDCAction.setImageDescriptor(imageDesc);
    manager.add(createComparePIDCAction);

  }


  /**
   * The method inovkes dialog to rename PIDC
   *
   * @param pidcVersion PidcVersion
   * @param pidc pidc
   */
  public void renamePIDC(final PidcVersion pidcVersion, final Pidc pidc) {
    PIDCRenameDialog renameDlg = new PIDCRenameDialog(Display.getDefault().getActiveShell(), pidcVersion, pidc);
    renameDlg.open();
  }

  /**
   * Method to copy Pidc version name to clipboard
   *
   * @param manager Menu Manager instance
   * @param pidcTreeNode Pidc tree node to be added to scratchpad
   */
  public void addCopyNameToClipboard(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    PIDCNameCopyAction copyToClipboard = new PIDCNameCopyAction(pidcTreeNode.getName(), "Copy Name to Clipboard");
    manager.add(copyToClipboard);
  }

  /**
   * Method to add Pidc version to scratchpad
   *
   * @param manager Menu Manager instance
   * @param pidcTreeNode Pidc tree node to be added to scratchpad
   */
  public void addToScratchPad(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.setAddToScrachPadAction(manager, pidcTreeNode.getPidcVersion());
  }

  /**
   * @param manager IMenuManager
   * @param pidcVersion PidcVersion
   * @param pidc pidc
   * @param strDelete string to be deleted
   */
  public void setDeleteAction(final IMenuManager manager, final PidcVersion pidcVersion, final Pidc pidc,
      final String strDelete) {
    // Get the user info
    CurrentUserBO currUser = new CurrentUserBO();
    boolean isOwner;
    try {
      isOwner = currUser.hasNodeOwnerAccess(pidc.getId());
      // delete action
      final Action addDeleteAction = new Action() {

        @Override
        public void run() {
          // Check if the pidc is unlocked in session
          ApicDataBO apicBo = new ApicDataBO();
          try {
            if (!apicBo.isPidcUnlockedInSession(pidcVersion) && currUser.hasNodeWriteAccess(pidc.getId())) {
              final PIDCActionSet pidcActionSet = new PIDCActionSet();
              pidcActionSet.showUnlockPidcDialog(pidcVersion);
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
          if (apicBo.isPidcUnlockedInSession(pidcVersion)) {
            // If PIDc is unlocked in session
            deleteAction(pidc, strDelete);
          }
        }
      };
      addDeleteAction.setText(strDelete);
      ImageDescriptor imageDesc;
      // Image descriptor
      if (strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_PIDC_ACTION)) {
        imageDesc = ImageManager.getImageDescriptor(ImageKeys.UNDO_DELETE_16X16);
      }
      else {
        imageDesc = ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16);
      }
      addDeleteAction.setImageDescriptor(imageDesc);
      addDeleteAction.setEnabled(isOwner);
      manager.add(addDeleteAction);
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param pidc PIDC
   * @param strDelete string to be deleted
   */
  public void deleteAction(final Pidc pidc, final String strDelete) {

    AttributeValue pidcNameVal = new AttributeValue();
    pidcNameVal.setId(pidc.getNameValueId());
    pidcNameVal.setDeleted(!strDelete.equalsIgnoreCase(ApicUiConstants.UN_DELETE_PIDC_ACTION));
    PidcTreeActionHandler treeActionHAndler = new PidcTreeActionHandler();
    treeActionHAndler.deleteUnDelPidc(pidcNameVal);
  }


  /**
   * @param manager IMenuManager
   * @param pidcTreeNode PidcTreeNode
   * @param setActiveVer active version
   */
  public void setActiveVerAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode,
      final String setActiveVer) {
    PidcVersion selPidcVer = pidcTreeNode.getPidcVersion();
    Pidc selPidc = pidcTreeNode.getPidc();
    // Get the user info
    CurrentUserBO currUser = new CurrentUserBO();
    boolean isOwner = false;
    try {
      isOwner = currUser.hasNodeOwnerAccess(pidcTreeNode.getPidc().getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    // set as active action
    final Action setActiveAction = new Action() {

      @Override
      public void run() {
        BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {
          PidcTreeActionHandler actionHandler = new PidcTreeActionHandler();
          selPidc.setProRevId(selPidcVer.getProRevId());
          actionHandler.setasActiveVer(selPidc);
        });
      }
    };
    setActiveAction.setText(setActiveVer);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.ACTIVE_PIDC_16X16);

    // creates the image description
    setActiveAction.setImageDescriptor(imageDesc);

    // Enable/disable set as active version action
    setActiveAction.setEnabled(!selPidcVer.isDeleted() && isOwner &&
        pidcTreeNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION));
    manager.add(setActiveAction);
  }


  /**
   * @param manager IMenuManager
   * @param pidcVers PidcVersion
   */
  public void setAddFavoritesAction(final IMenuManager manager, final PidcVersion pidcVers) {

    Set<Long> favPidcIdSet = new HashSet<>();
    try {
      favPidcIdSet = new FavouritesServiceClient().getFavouritePidcForUser(new CurrentUserBO().getUserID()).values()
          .stream().map(PidcFavourite::getPidcId).collect(Collectors.toSet());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    final Action addFavAction = new Action() {

      @Override
      public void run() {
        PidcTreeActionHandler treeActionHandler = new PidcTreeActionHandler();
        treeActionHandler.createPidcFavourite(pidcVers.getPidcId());
      }
    };
    addFavAction.setText("Add PIDC to Favorites");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FAV_ADD_28X30);

    // sets the image
    addFavAction.setImageDescriptor(imageDesc);
    addFavAction.setEnabled(!favPidcIdSet.contains(pidcVers.getPidcId()));
    manager.add(addFavAction);
  }

  /**
   * @param manager IMenuManager
   * @param pidcVersion PidcVersion
   */
  public void setRemoveFavoritesAction(final IMenuManager manager, final PidcVersion pidcVersion) {
    Set<Long> favPidcIdSet = new HashSet<>();
    try {
      favPidcIdSet = new FavouritesServiceClient().getFavouritePidcForUser(new CurrentUserBO().getUserID()).values()
          .stream().map(PidcFavourite::getPidcId).collect(Collectors.toSet());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    final Action addFavAction = new Action() {

      @Override
      public void run() {
        FavoritesViewPart favoritesView = (FavoritesViewPart) WorkbenchUtils.getView(FavoritesViewPart.VIEW_ID);

        Map<Long, PidcFavourite> favIdPidcFavMap = favoritesView.getDataHandler().getFavIdPidcFavMap();

        PidcFavourite pidcFvrt = new PidcFavourite();
        for (PidcFavourite fvs : favIdPidcFavMap.values()) {
          if (fvs.getPidcId().longValue() == pidcVersion.getPidcId().longValue()) {
            pidcFvrt = fvs;
            break;
          }
        }
        PidcTreeActionHandler treeActionHandler = new PidcTreeActionHandler();
        treeActionHandler.deletePidcFavourite(pidcFvrt);
      }
    };
    addFavAction.setText("Remove PIDC from Favorites");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FAV_DEL_28X30);

    // sets the image
    addFavAction.setImageDescriptor(imageDesc);
    addFavAction.setEnabled(favPidcIdSet.contains(pidcVersion.getPidcId()));
    manager.add(addFavAction);
  }

  /**
   * @param manager IMenuManager
   * @param pidcTreeNode PidcTreeNode
   */
  public void setRemoveFavoritesAction(final IMenuManager manager, final PidcTreeNode pidcTreeNode) {
    Set<Long> favPidcIdSet = new HashSet<>();
    try {
      favPidcIdSet = new FavouritesServiceClient().getFavouritePidcForUser(new CurrentUserBO().getUserID()).values()
          .stream().map(PidcFavourite::getPidcId).collect(Collectors.toSet());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    final Action addFavAction = new Action() {

      @Override
      public void run() {
        PidcTreeActionHandler treeActionHandler = new PidcTreeActionHandler();
        treeActionHandler.deletePidcFavourite(pidcTreeNode.getPidcFavourite());
      }
    };
    addFavAction.setText("Remove PIDC from Favorites");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FAV_DEL_28X30);

    // sets the image
    addFavAction.setImageDescriptor(imageDesc);
    addFavAction.setEnabled(favPidcIdSet.contains(pidcTreeNode.getPidc().getId()));
    manager.add(addFavAction);
  }

  /**
   * @param viewer IMenuManager
   * @param manager IMenuManager
   * @param pidcVersion PidcVersion
   */
  public void addImportPidcAction(final TreeViewer viewer, final IMenuManager manager, final PidcVersion pidcVersion) {

    // creates the import pidc action object
    ImportPIDCAction importPIDCAction = new ImportPIDCAction(viewer.getControl().getShell(), pidcVersion);
    importPIDCAction.setText("Import Excel");
    // Image desciptor for import pidc action
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.IMPORT_PID_28X30);
    importPIDCAction.setImageDescriptor(imageDesc);
    manager.add(importPIDCAction);
    // Get the user info
    CurrentUserBO currUser = new CurrentUserBO();
    boolean hasWriteAccess = false;
    try {
      hasWriteAccess = currUser.hasNodeWriteAccess(pidcVersion.getPidcId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    importPIDCAction.setEnabled(hasWriteAccess);
  }


  /**
   * @param manager IMenuManager
   * @param pidcVersion PidcVersion
   * @param exportAction export action string
   */
  public void setExportAction(final IMenuManager manager, final PidcVersion pidcVersion, final String exportAction) {
    final Action excelAction = new Action() {

      @Override
      public void run() {
        new PIDCActionSet().excelExport(pidcVersion);
      }
    };
    excelAction.setText(exportAction);
    excelAction.setEnabled(true);
    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16);
    excelAction.setImageDescriptor(imageDesc);
    manager.add(excelAction);
  }

  /**
   * @param manager IMenuManager
   * @param pidcVerInfo PidcVersionInfo
   */
  public void setTransfer2vCDMAction(final IMenuManager manager, final PidcVersionInfo pidcVerInfo) {
    // Get the user info
    CurrentUserBO userBO = new CurrentUserBO();

    // Transfer to vCDM action
    final Action transfer2vCDMAction = new Action() {

      @Override
      public void run() {
        ApicDataBO apicBo = new ApicDataBO();
        authenticateNTransferVcdmJob(pidcVerInfo, userBO, apicBo);
      }
    };

    transfer2vCDMAction.setText("Transfer to vCDM");

    ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.VCDM_16X16);
    transfer2vCDMAction.setImageDescriptor(imageDesc);
    // ICDM-2354
    try {
      transfer2vCDMAction.setEnabled(
          CommonUtils.isNotNull(pidcVerInfo) && userBO.hasNodeWriteAccess(pidcVerInfo.getPidcVersion().getPidcId()) &&
              !pidcVerInfo.getPidcVersion().isDeleted());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    manager.add(transfer2vCDMAction);
  }

  /**
   * @param pidcVerInfo
   * @param userBO
   * @param apicBo
   * @param isActive
   */
  private void authenticateNTransferVcdmJob(final PidcVersionInfo pidcVerInfo, final CurrentUserBO userBO,
      final ApicDataBO apicBo) {
    try {
      PidcVersion pidcVersion = pidcVerInfo.getPidcVersion();
      if (!apicBo.isPidcUnlockedInSession(pidcVersion) && userBO.hasNodeWriteAccess(pidcVersion.getPidcId())) {
        // check if the pidc is unlocked in session
        showUnlockPidcDialog(pidcVersion);
      }
      if (apicBo.isPidcUnlockedInSession(pidcVersion)) {

        // Icdm-515 Get the User info Details and Check if SSO is enabled
        CurrentUserBO currentUserBO = new CurrentUserBO();
        if (!currentUserBO.hasPassword()) {
          // SSO is enabled and the password dialog to be opened
          final PasswordDialog passwordDialog = new PasswordDialog(Display.getDefault().getActiveShell());
          passwordDialog.open();
        }

        // ICDM-220
        // Check again, and if password given, proceed with transfer
        if (currentUserBO.hasPassword()) {
          // Transfer to vcdm action
          Job job = new PidcVcdmTransferJob(new MutexRule(), pidcVerInfo.getPidc());
          CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
          job.schedule();
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }
}
