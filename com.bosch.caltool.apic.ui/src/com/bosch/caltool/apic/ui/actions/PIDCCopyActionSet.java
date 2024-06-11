/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.function.IntPredicate;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.apic.ui.dialogs.SubVariantPasteDialog;
import com.bosch.caltool.apic.ui.dialogs.VariantPasteDialog;
import com.bosch.caltool.apic.ui.util.VariantUIConstants;
import com.bosch.caltool.apic.ui.wizards.PIDCCreationWizard;
import com.bosch.caltool.apic.ui.wizards.PIDCCreationWizardDialog;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcCreationHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This class has the set of copy & paste action events for the tree view in PIDC Perspective
 *
 * @author mga1cob
 */
// ICDM-150
public class PIDCCopyActionSet {


  /**
   * Action for copy PIDC/Variant/Sub-Variant
   *
   * @param manager defines IMenuManager
   * @param projectObject defines ApicObject
   * @param copyName defines name for copy pidc
   * @param pidcVersionBO Pidc Version Handler
   */
  // ICDM-150
  public void setCopyAction(final IMenuManager manager, final IProjectObject projectObject, final String copyName,
      final PidcVersionBO pidcVersionBO) {
    // add PIDC/Variant/Sub-Variant copy action
    final Action copyAction = new Action() {

      @Override
      public void run() {
        ICDMClipboard.getInstance().setCopiedObject(projectObject);
      }

    };
    setCpyPasteActionToMenu(manager, copyName, copyAction, ImageManager.getImageDescriptor(ImageKeys.COPY_16X16),
        projectObject, true, pidcVersionBO);
  }


  /**
   * Action for copy PIDC
   *
   * @param manager defines IMenuManager
   * @param selObject selected Object
   * @param pasteName defines name for copy pidc
   * @param viewer instance
   * @param pidcVersionBO Pidc Version Handler
   */
  // ICDM-150
  public void setPasteAction(final IMenuManager manager, final Object selObject, final String pasteName,
      final TreeViewer viewer, final PidcVersionBO pidcVersionBO) {
    // add PIDC/Variant/Sub-Variant paste action
    final Action pastePIDCAction = new Action() {

      @Override
      public void run() {
        pasteAction(selObject, viewer, pidcVersionBO);
      }
    };
    setCpyPasteActionToMenu(manager, pasteName, pastePIDCAction, ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16),
        selObject, false, pidcVersionBO);
  }

  /**
   * @param selObject Selected node
   * @param viewer TreeViewer
   * @param pidcVersionBO PidcVersionHandler
   */
  public void pasteAction(final Object selObject, final TreeViewer viewer, final PidcVersionBO pidcVersionBO) {
    // Get copied object
    final Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
    // Paste PIDC
    if ((copiedObject instanceof PidcVersion) && (selObject instanceof PidcTreeNode)) {
      invokePIDCPasteDialog(selObject, viewer, copiedObject);
    }
    // Paste Variant
    else if (copiedObject instanceof PidcVariant) {
      final PidcVariant copiedVariant = (PidcVariant) copiedObject;
      pasteVariant(selObject, viewer, copiedVariant, pidcVersionBO);
    }
    else if ((copiedObject instanceof PIDCDetailsNode) && ((PIDCDetailsNode) copiedObject).isVariantNode()) {
      // Paste variant as details node
      final PidcVariant copiedVariant = ((PIDCDetailsNode) copiedObject).getPidcVariant();
      pasteVariant(selObject, viewer, copiedVariant, pidcVersionBO);
    }
    // Paste Sub-Variant
    else if (copiedObject instanceof PidcSubVariant) {
      final PidcSubVariant copiedSubVar = (PidcSubVariant) copiedObject;
      pasteSubVariant(selObject, viewer, copiedSubVar, pidcVersionBO);
    }
  }


  /**
   * This method add the copy/paste action to menu manager
   *
   * @param manager
   * @param copyName
   * @param action
   * @param imageDescriptor
   * @param pidcVersionBO
   */
  private void setCpyPasteActionToMenu(final IMenuManager manager, final String copyName, final Action action,
      final ImageDescriptor imageDescriptor, final Object selObject, final boolean isCopy,
      final PidcVersionBO pidcVersionBO) {
    // Set action name
    action.setText(copyName);
    // Set the image for the action
    action.setImageDescriptor(imageDescriptor);
    action.setEnabled(
        (isCopy) || isPasteAllowed(ICDMClipboard.getInstance().getCopiedObject(), selObject, pidcVersionBO));
    manager.add(action);
  }


  /**
   * @param copiedObject copied Object
   * @param selObject selected Object
   * @param pidcVersionBO Pidc Version Handler
   * @return true if paste is allowed
   */
  public boolean isPasteAllowed(final Object copiedObject, final Object selObject, final PidcVersionBO pidcVersionBO) {
    if (copiedObject instanceof PidcVariant) {
      // Get the copied variant
      final PidcVariant pidcVar = (PidcVariant) copiedObject;
      if (selObject instanceof PidcVersion) {
        if (isPasteAllowedOnPidcVersNode(selObject, pidcVar, pidcVersionBO)) {
          // true if PIDC are same & selected PIDC not deleted
          return true;
        }
      }
      else if (selObject instanceof PidcVariant) {
        if (isPasteAllowedOnVarNode(selObject, pidcVar, pidcVersionBO)) {
          // true if PIDC variants are not same, PIDC are same & selected PIDC,Variant not deleted
          return true;
        }
      }
    }
    else if (copiedObject instanceof PidcVersion) {
      return true;
    }
    else if (copiedObject instanceof PidcSubVariant) {
      if (selObject instanceof PidcSubVariant) {
        if (isPasteAllowedOnSubVarNode(selObject, (PidcSubVariant) copiedObject, pidcVersionBO)) {
          // true if PIDC are same & selected PIDC not deleted
          return true;
        }
      }
      else if ((selObject instanceof PidcVariant) &&
          isPasteAllowedOnVarNode(selObject, (PidcSubVariant) copiedObject, pidcVersionBO)) {
        // true if PIDC variants are not same, PIDC are same & selected PIDC,Variant not deleted
        return true;
      }
    }
    return false;
  }


  /**
   * @param selObject
   * @param copiedObject
   * @param pidcVersionBO
   * @return
   */
  private boolean isPasteAllowedOnSubVarNode(final Object selObject, final PidcSubVariant copiedObject,
      final PidcVersionBO pidcVersionBO) {
    PidcSubVariant pidcSubVar = (PidcSubVariant) selObject;
    PidcVariant pidcVariant = pidcVersionBO.getPidcDataHandler().getVariantMap().get(pidcSubVar.getPidcVariantId());
    PidcSubVariantBO pidcSubVarHandler =
        new PidcSubVariantBO(pidcVersionBO.getPidcVersion(), pidcSubVar, pidcVersionBO.getPidcDataHandler());
    return checkPidcSubvar(pidcSubVar, copiedObject) && !pidcVariant.isDeleted() &&
        !pidcVersionBO.getPidcVersion().isDeleted() && pidcSubVarHandler.isModifiable();
  }


  /**
   * @param pidcSubVar
   * @param copiedObject
   * @return
   */
  private boolean checkPidcSubvar(final PidcSubVariant pidcSubVar, final PidcSubVariant copiedObject) {
    return (pidcSubVar.getId().longValue() != copiedObject.getId().longValue()) &&
        (pidcSubVar.getPidcVariantId().longValue() == copiedObject.getPidcVariantId().longValue()) &&
        !pidcSubVar.isDeleted();
  }

  /**
   * @param selObject
   * @param copiedObject
   * @param pidcVersionBO
   * @return
   */
  private boolean isPasteAllowedOnVarNode(final Object selObject, final PidcSubVariant copiedObject,
      final PidcVersionBO pidcVersionBO) {
    PidcVariant pidcVar = (PidcVariant) selObject;
    PidcVariantBO pidcVarHandler =
        new PidcVariantBO(pidcVersionBO.getPidcVersion(), pidcVar, pidcVersionBO.getPidcDataHandler());
    return (pidcVar.getId().longValue() == copiedObject.getPidcVariantId().longValue()) && !pidcVar.isDeleted() &&
        !(pidcVersionBO.getPidcVersion().isDeleted() && pidcVarHandler.isModifiable());

  }


  /**
   * @param selObject
   * @param pidcVar
   * @param pidcVersionBO
   * @return
   */
  private boolean isPasteAllowedOnVarNode(final Object selObject, final PidcVariant pidcVar,
      final PidcVersionBO pidcVersionBO) {
    PidcVariantBO pidcVarHandler =
        new PidcVariantBO(pidcVersionBO.getPidcVersion(), (PidcVariant) selObject, pidcVersionBO.getPidcDataHandler());
    return checkPidcVar(pidcVar, selObject) &&
        !(pidcVersionBO.getPidcVersion().isDeleted() && pidcVarHandler.isModifiable());
  }

  /**
   * @param pidcVar
   * @param selObject
   * @return
   */
  private boolean checkPidcVar(final PidcVariant pidcVar, final Object selObject) {
    return (pidcVar.getId().longValue() != ((PidcVariant) selObject).getId().longValue()) &&
        (pidcVar.getPidcVersionId().longValue() == ((PidcVariant) selObject).getPidcVersionId().longValue()) &&
        !((PidcVariant) selObject).isDeleted();
  }

  /**
   * @param selObject
   * @param pidcVar
   * @param pidcVersionBO
   * @return
   */
  private boolean isPasteAllowedOnPidcVersNode(final Object selObject, final PidcVariant pidcVar,
      final PidcVersionBO pidcVersionBO) {
    return (pidcVar.getPidcVersionId().longValue() == ((PidcVersion) selObject).getId().longValue()) &&
        !((PidcVersion) selObject).isDeleted() && pidcVersionBO.isModifiable();
  }


  /**
   * This method invokes pidc paste dialog
   *
   * @param destObject
   * @param viewer
   * @param copiedObject
   */
  private void invokePIDCPasteDialog(final Object destObject, final TreeViewer viewer, final Object copiedObject) {
    final PidcVersion pidcVerToCopy = (PidcVersion) copiedObject;

    PIDCCreationWizard createPidcWizard =
        new PIDCCreationWizard((PidcTreeNode) destObject, viewer, true, pidcVerToCopy);
    PIDCCreationWizardDialog createPidcWizardDlg =
        new PIDCCreationWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), createPidcWizard);
    createPidcWizardDlg.create();
    int returnValue = createPidcWizardDlg.open();

    if (returnValue == 0) {
      PidcVersion newlyCreatedVer = new PidcCreationHandler().getActiveVersion(createPidcWizard.getNewlyCreatedPidc());
      OpenNewPidcAction openAction = new OpenNewPidcAction(newlyCreatedVer, "Open PIDC Version");
      openAction.run();
    }
  }


  /**
   * This method retuns active shell
   *
   * @return shell
   */
  private Shell getActiveShell() {
    return Display.getCurrent().getActiveShell();
  }

  /**
   * This method paste the copied variant to destination tree node
   *
   * @param destObject
   * @param viewer
   * @param copiedVariant
   * @param pidcVersionBO
   */
  // ICDM-150
  private void pasteVariant(final Object destObject, final TreeViewer viewer, final PidcVariant copiedVariant,
      final PidcVersionBO pidcVersionBO) {
    if (destObject instanceof PidcVersion) {
      invokeVariantPasteDialog(viewer, copiedVariant, (PidcVersion) destObject, pidcVersionBO);
    }
    else if (destObject instanceof PidcVariant) {
      // ICDM-222 progress for pasting PIDC, Variant,Subvariant
      try {
        VariantActionSet varActionSet = new VariantActionSet();
        int userCopySelection = varActionSet.userCnfmToPasteSubVarAlongWithVar(copiedVariant, pidcVersionBO);

        if (isNotToCancelCopy().test(userCopySelection)) {
          int userConfmToOverrideVarAttr = varActionSet.userCnfmToOverrideAllVarAttrVal(userCopySelection);
          int userConfmToOverrideVarNSubVarAttr =
              varActionSet.userCnfmToOverrideAllVarNSubVarAttrVal(userCopySelection);
          copyVariant(destObject, copiedVariant, pidcVersionBO, userCopySelection, userConfmToOverrideVarAttr,
              userConfmToOverrideVarNSubVarAttr);
        }
      }
      catch (InvocationTargetException | InterruptedException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        Thread.currentThread().interrupt();
      }


    }

  }


  /**
   * @param destObject
   * @param copiedVariant
   * @param pidcVersionBO
   * @param varActionSet
   * @param userCopySelection
   * @param userConfmToOverrideVarAttr
   * @param userConfmToOverrideVarNSubVarAttr
   * @param sourceSubVarSet
   * @param userCnfmCopyToDelSubVar
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  private void copyVariant(final Object destObject, final PidcVariant copiedVariant, final PidcVersionBO pidcVersionBO,
      final int userCopySelection, final int userConfmToOverrideVarAttr, final int userConfmToOverrideVarNSubVarAttr)
      throws InvocationTargetException, InterruptedException {


    if (isNotToCancelCopy().test(userConfmToOverrideVarAttr) &&
        isNotToCancelCopy().test(userConfmToOverrideVarNSubVarAttr)) {
      VariantActionSet varActionSet = new VariantActionSet();
      SortedSet<PidcSubVariant> sourceSubVarSet =
          pidcVersionBO.getSubVariantsforSelVariant(copiedVariant.getId(), false);
      int userCnfmCopyToDelSubVar = varActionSet.userCnfmCopyToDeletedSubVar(copiedVariant, (PidcVariant) destObject,
          pidcVersionBO, sourceSubVarSet, userCopySelection);
      ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, monitor -> {

        monitor.beginTask("Copying Variant...", 100);
        monitor.worked(20);
        try {
          PidcVersion pidcVersion = pidcVersionBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion();
          // if usercopySelection is yes, then copy variant along with sub-variant
          PidcVarPasteOutput pidcVarOutputData = (userCopySelection == VariantUIConstants.COPY_SEL_YES)
              ? varActionSet.updateVarAlongWithSubVar(copiedVariant, (PidcVariant) destObject, pidcVersion,
                  sourceSubVarSet, userCnfmCopyToDelSubVar, false, userConfmToOverrideVarNSubVarAttr)
              : updateVariant(destObject, copiedVariant, userConfmToOverrideVarAttr, pidcVersion);

          // inform user about value not copied for already available attribute's value
          if (CommonUtils.isNotNull(pidcVarOutputData)) {
            varActionSet.attributeValAlreadyExistInfoDialog(pidcVarOutputData, userConfmToOverrideVarAttr);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
    }
  }

  /**
   * @return
   */
  private IntPredicate isNotToCancelCopy() {
    return userSelection -> ((userSelection != VariantUIConstants.COPY_SEL_CANCEL) &&
        (userSelection != VariantUIConstants.MSG_DIALOG_CLOSE_BUTTON));
  }


  /**
   * @param destObject
   * @param copiedVariant
   * @param userConfmToOverrideVarAttr
   * @param pidcVersion
   * @return
   * @throws ApicWebServiceException
   */
  private PidcVarPasteOutput updateVariant(final Object destObject, final PidcVariant copiedVariant,
      final int userConfmToOverrideVarAttr, final PidcVersion pidcVersion)
      throws ApicWebServiceException {
    PidcVarPasteOutput pidcVarOutputData;
    PidcVariantCopyServiceClient pidcVarClient = new PidcVariantCopyServiceClient();
    PidcVariantData pidcVarCreationData = new PidcVariantData();
    pidcVarCreationData.setSrcPidcVar(copiedVariant);
    pidcVarCreationData.setPidcVersion(pidcVersion);
    pidcVarCreationData.setDestPidcVar((PidcVariant) destObject);
    if (userConfmToOverrideVarAttr == VariantUIConstants.COPY_SEL_OVERRIDE) {
      pidcVarCreationData.setOverrideAllVarAttr(true);
    }
    pidcVarOutputData = pidcVarClient.update(pidcVarCreationData);
    return pidcVarOutputData;
  }

  /**
   * This method is resposnible to open variant paste dialog
   *
   * @param viewer TreeViewer
   * @param copiedVariant PIDCVariant
   * @param destObject PIDCard
   * @param pidcVersionBO
   */
  private void invokeVariantPasteDialog(final TreeViewer viewer, final PidcVariant copiedVariant,
      final PidcVersion destObject, final PidcVersionBO pidcVersionBO) {
    final VariantPasteDialog pasteVarDialog =
        new VariantPasteDialog(getActiveShell(), pidcVersionBO, copiedVariant, viewer, destObject);
    pasteVarDialog.open();
  }


  /**
   * This method paste the copied sub-variant to destination tree node
   *
   * @param destObject
   * @param viewer
   * @param copiedSubVar
   * @param pidcVersionBO
   */
  // ICDM-150
  private void pasteSubVariant(final Object destObject, final TreeViewer viewer, final PidcSubVariant copiedSubVar,
      final PidcVersionBO pidcVersionBO) {
    if (destObject instanceof PidcVariant) {
      invokeSubVariantPasteDialog(viewer, copiedSubVar, pidcVersionBO);
    }
    else if (destObject instanceof PidcSubVariant) {

      // ICDM-222 progress for pasting PIDC, Variant,Subvariant
      try {

        ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
        dialog.run(true, true, monitor -> {

          monitor.beginTask("Copying SubVariant...", 100);
          monitor.worked(20);
          new SubvariantActionSet().copySubVariant(destObject, copiedSubVar);
          monitor.worked(100);
          monitor.done();

        });
      }
      catch (InvocationTargetException | InterruptedException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }

    }
  }


  /**
   * This method is resposnible to open variant paste dialog
   *
   * @param viewer TreeViewer
   * @param copiedSubVar PIDCSubVariant
   * @param pidcVersionBO
   */
  private void invokeSubVariantPasteDialog(final TreeViewer viewer, final PidcSubVariant copiedSubVar,
      final PidcVersionBO pidcVersionBO) {
    final SubVariantPasteDialog pasteSubVarDialog =
        new SubVariantPasteDialog(getActiveShell(), null, copiedSubVar, viewer, pidcVersionBO);
    pasteSubVarDialog.open();
  }
}
