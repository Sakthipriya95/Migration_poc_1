/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.PIDCCocWpPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcCocWpBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class PIDCCocWpActionSet {

  /**
   * Constant defining the Variant already moved name
   */
  private static final String VARIANT_ALREADY_MOVED = "Coc Work Package already at variant level";

  /**
   * Constant defining the Sub-Variant already moved name
   */
  public static final String SUB_VARIANT_ALREADY_MOVED = "Coc Work Package already at sub-variant level";

  /**
   * Constant defining the Sub-Variant already deleted name
   */
  public static final String SUB_VARIANT_ALREADY_ARE_DELETED = "All the Sub-Variant(s) are deleted";

  private final PidcCocWpBO cocWpBO;

  private final PIDCCocWpPage pidcCocWpPage;

  private final PIDCActionSet pidcActionset = new PIDCActionSet();

  private final PidcVersion selPidcVers;

  /**
   * @param cocWpBO as input
   * @param pidcCocWpPage as input
   */
  public PIDCCocWpActionSet(final PidcCocWpBO cocWpBO, final PIDCCocWpPage pidcCocWpPage) {
    this.cocWpBO = cocWpBO;
    this.pidcCocWpPage = pidcCocWpPage;
    this.selPidcVers = this.pidcCocWpPage.getSelectedPidcVersion();
  }


  /**
   * Action to Move the WP from PIDC Level to Variant Level
   *
   * @param menuMgr MenuManager
   * @param selection IStructuredSelection
   */
  public void addMoveToVariantMenu(final MenuManager menuMgr, final IStructuredSelection selection) {

    final List<PidcVersCocWp> selectedPidcVersCocWpList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selectedPidcVersCocWpList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    boolean flag = true;
    for (PidcVersCocWp pidcVersCocWp : selectedPidcVersCocWpList) {
      if (pidcVersCocWp.isAtChildLevel()) {
        CDMLogger.getInstance().info(VARIANT_ALREADY_MOVED, Activator.PLUGIN_ID);
        flag = false;
        break;
      }
    }
    if (flag) {
      Action moveToVariantAction = new Action(ApicUiConstants.MOVE_TO_VARIANT, SWT.NONE) {

        @Override
        public void run() {

          // check if the PIDC is locked and user has write access then show unlock pidc dialog
          if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers)) {
            PIDCCocWpActionSet.this.cocWpBO.pidcVerCocWPMoveToVariant(selectedPidcVersCocWpList);
          }
        }
      };

      moveToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
      moveToVariantAction
          .setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) && this.cocWpBO.isVariantAvailable() &&
              this.pidcActionset.checkUserHasWriteAccess(PIDCCocWpActionSet.this.selPidcVers));
      menuMgr.add(moveToVariantAction);
    }
  }

  /**
   * @param menuMgr MenuManager
   * @throws ApicWebServiceException exception
   */
  public void addMoveToVariantMenuFromSubVariant(final MenuManager menuMgr, final IStructuredSelection selection) {
    final List<PidcSubVarCocWp> selectedPidcSubVarCocWpList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selectedPidcSubVarCocWpList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    Action moveToVariantAction = new Action(ApicUiConstants.MOVE_TO_VARIANT, SWT.NONE) {

      @Override
      public void run() {
        // check if the PIDC is locked and user has write access then show unlock pidc dialog
        if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers)) {
          PIDCCocWpActionSet.this.cocWpBO.pidcSubVarCocWpMoveToVariant(selectedPidcSubVarCocWpList,
              PIDCCocWpActionSet.this.pidcCocWpPage.getSelectedPidcVariant());
        }
      }
    };

    moveToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VARIANT_28X30));
    moveToVariantAction.setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) &&
        this.cocWpBO.isVariantAvailable() && this.pidcActionset.checkUserHasWriteAccess(this.selPidcVers));
    menuMgr.add(moveToVariantAction);
  }

  /**
   * @param menuMgr MenuManager
   */
  public void addMoveToPidcMenuFromVariant(final MenuManager menuMgr, final IStructuredSelection selection) {
    final List<PidcVariantCocWp> selectedPidcVariantCocWpList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selectedPidcVariantCocWpList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    Action moveToVariantAction = new Action(ApicUiConstants.MOVE_TO_COMMON, SWT.NONE) {

      @Override
      public void run() {
        // check if the PIDC is locked and user has write access then show unlock pidc dialog
        if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers)) {
          PIDCCocWpActionSet.this.cocWpBO.pidcVarCocWpMoveToCommon(selectedPidcVariantCocWpList);
        }
      }
    };

    moveToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16));
    moveToVariantAction.setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) &&
        this.cocWpBO.isVariantAvailable() && this.pidcActionset.checkUserHasWriteAccess(this.selPidcVers));
    menuMgr.add(moveToVariantAction);
  }

  /**
   * @param menuMgr MenuManager
   * @param selection conains selected value
   */
  public void addMoveToSubvariantMenuFromVariant(final MenuManager menuMgr, final IStructuredSelection selection) {

    final List<PidcVariantCocWp> selectedPidcVarsCocWpList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selectedPidcVarsCocWpList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    boolean flag = true;
    for (PidcVariantCocWp pidcVarsCocWp : selectedPidcVarsCocWpList) {
      if (pidcVarsCocWp.isAtChildLevel()) {
        CDMLogger.getInstance().info(SUB_VARIANT_ALREADY_MOVED, Activator.PLUGIN_ID);
        flag = false;
        break;
      }
    }

    if (flag) {
      Action moveToVariantAction = new Action(ApicUiConstants.MOVE_TO_SUB_VARIANT, SWT.NONE) {

        @Override
        public void run() {
          // check if the PIDC is locked and user has write access then show unlock pidc dialog
          if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers)) {
            PIDCCocWpActionSet.this.cocWpBO.pidcVarCocWpMoveToSubVariant(selectedPidcVarsCocWpList);

          }
        }
      };

      moveToVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SUBVAR_28X30));
      moveToVariantAction.setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) &&
          this.pidcActionset.checkUserHasWriteAccess(PIDCCocWpActionSet.this.selPidcVers) &&
          this.cocWpBO.isSubVariantAvailForGivenVar(this.pidcCocWpPage.getSelectedPidcVariant().getId()));
      menuMgr.add(moveToVariantAction);
    }

  }


  /**
   * @param menuMgr MenuManager
   * @param selection IStructuredSelection
   */
  public void addUsedFlagYesMenu(final MenuManager menuMgr, final IStructuredSelection selection) {

    List<IProjectCoCWP> selIProjectCoCWPList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selIProjectCoCWPList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    Action setUsedFlagToYesAction = new Action(ApicUiConstants.SET_TO_YES, SWT.NONE) {

      @Override
      public void run() {
        // check if the PIDC is locked and user has write access then show unlock pidc dialog
        if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers) &&
            PIDCCocWpActionSet.this.cocWpBO.isOkToUpdUsedFlag(true, selIProjectCoCWPList.get(0))) {
          PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
          PIDCCocWpActionSet.this.cocWpBO.setUsedFlagToUpdate(CoCWPUsedFlag.YES.getDbType(), selIProjectCoCWPList,
              pidcCocWpUpdationInputModel);
          pidcCocWpUpdationInputModel.setInvokedOnUsedFlagUpd(true);
          PIDCCocWpActionSet.this.cocWpBO.updatePidcCocWPs(pidcCocWpUpdationInputModel);
        }
      }
    };

    setUsedFlagToYesAction.setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) &&
        this.pidcActionset.checkUserHasWriteAccess(PIDCCocWpActionSet.this.selPidcVers));
    menuMgr.add(setUsedFlagToYesAction);
  }


  /**
   * @param menuMgr MenuManager
   * @param selection IStructuredSelection
   */
  public void addUsedFlagNotDefinedMenu(final MenuManager menuMgr, final IStructuredSelection selection) {

    List<IProjectCoCWP> selIProjectCoCWPList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selIProjectCoCWPList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    Action setUsedFlagToNotDefinedAction = new Action(ApicUiConstants.SET_TO_NOT_DEFINED, SWT.NONE) {

      @Override
      public void run() {
        // check if the PIDC is locked and user has write access then show unlock pidc dialog
        if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers) &&
            PIDCCocWpActionSet.this.cocWpBO.isOkToUpdUsedFlag(true, selIProjectCoCWPList.get(0))) {
          PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
          PIDCCocWpActionSet.this.cocWpBO.setUsedFlagToUpdate(CoCWPUsedFlag.NOT_DEFINED.getDbType(),
              selIProjectCoCWPList, pidcCocWpUpdationInputModel);
          pidcCocWpUpdationInputModel.setInvokedOnUsedFlagUpd(true);
          PIDCCocWpActionSet.this.cocWpBO.updatePidcCocWPs(pidcCocWpUpdationInputModel);
        }
      }
    };

    setUsedFlagToNotDefinedAction.setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) &&
        this.pidcActionset.checkUserHasWriteAccess(PIDCCocWpActionSet.this.selPidcVers));
    menuMgr.add(setUsedFlagToNotDefinedAction);
  }


  /**
   * @param menuMgr MenuManager
   * @param selection IStructuredSelection
   */
  public void addUsedFlagNoMenu(final MenuManager menuMgr, final IStructuredSelection selection) {

    List<IProjectCoCWP> selIProjectCoCWPList = selection.toList();
    List<IProjectCoCWP> delIProjectCoCWPList =
        selIProjectCoCWPList.stream().filter(IProjectCoCWP::isDeleted).collect(Collectors.toList());

    Action setUsedFlagToNoAction = new Action(ApicUiConstants.SET_TO_NO, SWT.NONE) {

      @Override
      public void run() {
        // check if the PIDC is locked and user has write access then show unlock pidc dialog
        if (PIDCCocWpActionSet.this.pidcActionset.checkPIDCLocked(PIDCCocWpActionSet.this.selPidcVers) &&
            PIDCCocWpActionSet.this.cocWpBO.isOkToUpdUsedFlag(true, selIProjectCoCWPList.get(0))) {

          PIDCCocWpUpdationInputModel pidcCocWpUpdationInputModel = new PIDCCocWpUpdationInputModel();
          PIDCCocWpActionSet.this.cocWpBO.setUsedFlagToUpdate(CoCWPUsedFlag.NO.getDbType(), selIProjectCoCWPList,
              pidcCocWpUpdationInputModel);
          pidcCocWpUpdationInputModel.setInvokedOnUsedFlagUpd(true);
          PIDCCocWpActionSet.this.cocWpBO.updatePidcCocWPs(pidcCocWpUpdationInputModel);
        }
      }
    };

    setUsedFlagToNoAction.setEnabled(CommonUtils.isNullOrEmpty(delIProjectCoCWPList) &&
        this.pidcActionset.checkUserHasWriteAccess(PIDCCocWpActionSet.this.selPidcVers));
    menuMgr.add(setUsedFlagToNoAction);

  }
}
