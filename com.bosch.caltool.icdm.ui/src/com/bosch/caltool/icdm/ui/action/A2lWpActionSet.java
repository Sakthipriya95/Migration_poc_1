/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpRespResolver;
import com.bosch.caltool.icdm.common.ui.dialogs.AddUserResponsibleDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.AddWpRespNonBoschDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditWpRespDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizardDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityCopyModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.SetWpRespDialog;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.editors.pages.A2LParametersPage;
import com.bosch.caltool.icdm.ui.editors.pages.A2lWPDefinitionPage;
import com.bosch.caltool.icdm.ui.table.filters.A2lWPDefToolBarFilter;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author pdh2cob
 */
public class A2lWpActionSet {

  /**
   *
   */
  private static final String GET_PRE_CALIBRATION_DATA_ERROR =
      "Get Pre-Calibration Data cannot be done since param mappings are not available for the following work package(s) " +
          "\n";

  private final A2LWPInfoBO a2lWPInfoBO;

  private final A2lWPDefinitionPage page;

  private Action deleteA2lWpResp;

  private Action addA2lWpResp;

  private Action editA2lWpResp;

  private Action addCustomization;

  private Action removeCustomization;

  private Action isVariantGroupWpAction;

  private Action otherVariantGroupWpAction;

  private Action notMappedToVGAction;

  private ParamWpRespResolver respResolver;

  private Action importFromA2lGrpAction;

  private Action addActiveVersAction;


  /**
   * @param a2lWPInfoBO - instance of A2LWPInfoBO
   * @param page
   */
  public A2lWpActionSet(final A2LWPInfoBO a2lWPInfoBO, final A2lWPDefinitionPage page) {
    this.a2lWPInfoBO = a2lWPInfoBO;
    this.page = page;
  }

  /**
   * @param toolBarManager
   */
  public void addA2lWpRespAction(final ToolBarManager toolBarManager) {

    this.addA2lWpResp = new Action() {

      @Override
      public void run() {
        CreateEditWpRespDialog wpRespDialog = new CreateEditWpRespDialog(Display.getCurrent().getActiveShell(),
            A2lWpActionSet.this.a2lWPInfoBO, new A2lWpResponsibility(), false);
        wpRespDialog.open();
        if (null != wpRespDialog.getNew2lWpRespObj()) {
          A2lWpActionSet.this.page.setSelectedRow(wpRespDialog.getNew2lWpRespObj());
        }
      }
    };
    this.addA2lWpResp.setText("Add Workpackage");
    this.addA2lWpResp.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addA2lWpResp.setEnabled(this.a2lWPInfoBO.isEditable());
    toolBarManager.add(this.addA2lWpResp);

  }

  /**
   * @param selection
   * @param mgr
   */
  public void addCopyAction(final IStructuredSelection selection, final IMenuManager mgr) {
    Action copyAction = new Action() {

      @Override
      public void run() {
        if (selection.size() == 1) {
          CopyDataToClipboardCommand copyCommand =
              new CopyDataToClipboardCommand("\t", System.getProperty("line.separator"),
                  A2lWpActionSet.this.page.getA2lwpdefinitionNattable().getConfigRegistry());
          A2lWpActionSet.this.page.getA2lwpdefinitionNattable().doCommand(copyCommand);
          final A2lWpResponsibility copiedObj = (A2lWpResponsibility) (selection.getFirstElement());

          A2lWpResponsibilityCopyModel copyModel = new A2lWpResponsibilityCopyModel();
          copyModel.setSelectedA2lWpResp(copiedObj);
          int[] selectedColumnPositions = A2lWpActionSet.this.page.getCustomFilterGridLayer().getBodyLayer()
              .getSelectionLayer().getSelectedColumnPositions();
          copyModel.setSelectedColumns(selectedColumnPositions);

          ICDMClipboard.getInstance().setCopiedObject(copyModel);

        }
      }
    };
    boolean isEnable = validateAndConstructCopyDisplayText(copyAction);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    copyAction.setImageDescriptor(imageDesc);
    copyAction.setEnabled(this.a2lWPInfoBO.isEditable() && isEnable);
    mgr.add(copyAction);

  }

  /**
   * @param copyAction
   * @param isEnable
   * @return
   */
  private boolean validateAndConstructCopyDisplayText(final Action copyAction) {
    boolean isEnable = true;

    int[] selColumnPositions = A2lWpActionSet.this.page.getCustomFilterGridLayer().getBodyLayer().getSelectionLayer()
        .getSelectedColumnPositions();

    if (contains(selColumnPositions, 3) && contains(selColumnPositions, 4)) {
      copyAction.setText("Copy Responsibility and Name at Customer");
    }
    else if (contains(selColumnPositions, 3)) {
      copyAction.setText("Copy Responsibility ");
    }
    else if (contains(selColumnPositions, 4)) {
      copyAction.setText("Copy Name at Customer");
    }
    else {
      copyAction.setText("Copy");
      isEnable = false;
    }
    return isEnable;
  }

  private boolean contains(final int[] selColumn, final int number) {
    return Arrays.stream(selColumn).anyMatch(i -> i == number);
  }

  /**
   * @param selection IStructuredSelection
   * @param mgr IMenuManager
   */
  public void addCustomizationAction(final IStructuredSelection selection, final IMenuManager mgr) {
    this.addCustomization = new Action() {

      @Override
      public void run() {
        List<A2lWpResponsibility> a2lWpRespList = selection.toList();

        SetWpRespDialog wpRespDialog =
            new SetWpRespDialog(Display.getCurrent().getActiveShell(), true, A2lWpActionSet.this.a2lWPInfoBO);
        wpRespDialog.open();
        if (wpRespDialog.getA2lResp() != null) {
          createCustomizedWpResp(a2lWpRespList, wpRespDialog);
        }
      }

    };

    this.addCustomization.setText("Customize to Variant Group");
    if ((selection.size() == CommonUIConstants.SINGLE_SELECTION) &&
        this.a2lWPInfoBO.isCustomizable((A2lWpResponsibility) selection.getFirstElement())) {
      this.addCustomization.setEnabled(true);
    }
    else if ((selection.size() > CommonUIConstants.SINGLE_SELECTION) && this.a2lWPInfoBO.isEditable() &&
        (this.a2lWPInfoBO.getSelectedA2lVarGroup() != null)) {
      this.addCustomization.setEnabled(true);
    }
    else {
      this.addCustomization.setEnabled(false);
    }
    mgr.add(this.addCustomization);
  }

  /**
   * @param a2lWpRespList
   * @param wpRespDialog
   * @throws ApicWebServiceException
   */
  private void createCustomizedWpResp(final List<A2lWpResponsibility> a2lWpRespList,
      final SetWpRespDialog wpRespDialog) {

    A2lResponsibility a2lResponsibility = wpRespDialog.getA2lResp();
    A2lVariantGroup selectedA2lVarGroup = A2lWpActionSet.this.a2lWPInfoBO.getSelectedA2lVarGroup();
    List<A2lWpResponsibility> a2lWpRespListToCreate = new ArrayList<>();

    a2lWpRespList.forEach(wpResp -> {
      if (!checkA2lWpRespExsist(wpResp)) {
        A2lWpResponsibility newA2lWpResp = new A2lWpResponsibility();
        newA2lWpResp.setA2lRespId(a2lResponsibility.getId());
        newA2lWpResp.setVariantGrpId(selectedA2lVarGroup.getId());
        newA2lWpResp.setA2lWpId(wpResp.getA2lWpId());
        newA2lWpResp.setWpDefnVersId(wpResp.getWpDefnVersId());
        a2lWpRespListToCreate.add(newA2lWpResp);
      }
    });

    try {
      if (a2lWpRespListToCreate.isEmpty()) {
        MessageDialogUtils.getWarningMessageDialog("Warning", "WP Definition(s) already exists");
      }
      else {
        new A2lWpResponsibilityServiceClient().create(a2lWpRespListToCreate,
            A2lWpActionSet.this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
      }

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }


  private boolean checkA2lWpRespExsist(final A2lWpResponsibility wpResp) {
    Map<Long, Map<Long, A2lWpResponsibility>> varGrpRespMap;
    if (this.a2lWPInfoBO.getParamWpRespResolver() == null) {
      intializeWpRespResolver();
      varGrpRespMap = this.respResolver.getVarGrpRespMap();
    }
    else {
      varGrpRespMap = this.a2lWPInfoBO.getParamWpRespResolver().getVarGrpRespMap();
    }
    Map<Long, A2lWpResponsibility> a2lWpRespMap = varGrpRespMap.get(this.a2lWPInfoBO.getSelectedA2lVarGroup().getId());
    return (a2lWpRespMap != null) && a2lWpRespMap.containsKey(wpResp.getA2lWpId());
  }

  private void intializeWpRespResolver() {

    this.respResolver = new ParamWpRespResolver(this.a2lWPInfoBO.getA2lWpDefnModel().getA2lVariantGroupMap(),
        this.a2lWPInfoBO.getA2lWpDefnModel().getWpRespMap(),
        this.a2lWPInfoBO.getA2lWpParamMappingModel().getA2lWpParamMapping());
  }

  /**
   * @param selection IStructuredSelection
   * @param mgr IMenuManager
   */
  public void removeCustomizationAction(final IStructuredSelection selection, final IMenuManager mgr) {
    this.removeCustomization = new Action() {

      @Override
      public void run() {
        Set<Long> wpRespIds = new HashSet<>();
        List<A2lWpResponsibility> wpRespList = selection.toList();
        wpRespList.forEach(wpResp -> {
          if (wpResp.getVariantGrpId() != null) {
            wpRespIds.add(wpResp.getId());
          }
        });
        if (wpRespIds.isEmpty()) {
          MessageDialogUtils.getWarningMessageDialog("Warning", "WP Definition(s) cannot be removed");
        }
        else {
          removeWpResp(wpRespIds);
        }
      }
    };
    this.removeCustomization.setText("Revert Customization");
    if ((selection.size() == CommonUIConstants.SINGLE_SELECTION) && this.a2lWPInfoBO.isEditable() &&
        (((A2lWpResponsibility) selection.getFirstElement()).getVariantGrpId() != null)) {
      this.removeCustomization.setEnabled(true);
    }
    else if ((selection.size() > CommonUIConstants.SINGLE_SELECTION) && this.a2lWPInfoBO.isEditable()) {
      this.removeCustomization.setEnabled(true);
    }
    else {
      this.removeCustomization.setEnabled(false);
    }
    mgr.add(this.removeCustomization);
  }

  /**
   * @param wpRespIds
   */
  private void removeWpResp(final Set<Long> wpRespIds) {
    try {
      new A2lWpResponsibilityServiceClient().delete(wpRespIds, this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param selection
   * @param mgr
   * @param toolBarManager
   */
  public void addPasteAction(final IStructuredSelection selection, final IMenuManager mgr) {

    Action pasteAction = new Action() {

      @Override
      public void run() {
        paste(selection);
        // Removing the copied object after pasting
        ICDMClipboard.getInstance().setCopiedObject(null);
      }
    };
    constructPasteDisplayText(pasteAction);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PASTE_16X16);
    pasteAction.setImageDescriptor(imageDesc);
    Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
    pasteAction.setEnabled(this.a2lWPInfoBO.isEditable() && (copiedObject != null) &&
        (copiedObject instanceof A2lWpResponsibilityCopyModel));
    mgr.add(pasteAction);

  }

  /**
   * @param pasteAction
   * @param isEnable
   */
  private void constructPasteDisplayText(final Action pasteAction) {
    Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
    if (copiedObject instanceof A2lWpResponsibilityCopyModel) {
      final A2lWpResponsibilityCopyModel copiedModel = (A2lWpResponsibilityCopyModel) copiedObject;
      int[] selColumnPositions = copiedModel.getSelectedColumns();
      if (contains(selColumnPositions, 3) && contains(selColumnPositions, 4)) {
        pasteAction.setText("Paste Responsibility and Name at Customer");
      }
      else if (contains(selColumnPositions, 3)) {
        pasteAction.setText("Paste Responsibility ");
      }
      else if (contains(selColumnPositions, 4)) {
        pasteAction.setText("Paste Name at Customer");
      }
      else {
        pasteAction.setText("Paste");
      }
    }
    else {
      pasteAction.setText("Paste");
    }
  }

  /**
   * @param selection
   * @param mgr
   * @param toolBarManager
   */
  public void addTakeOverAction(final IStructuredSelection selection, final IMenuManager mgr) {

    Action takeOverAction = new Action() {

      @Override
      public void run() {
        List<A2lWpResponsibility> filteredList = new ArrayList<>();

        filteredList.addAll(A2lWpActionSet.this.page.getCustomFilterGridLayer().getBodyDataProvider().getList());
        // selected & default a2lwpresp should not be taken over
        filteredList.remove(selection.getFirstElement());
        filteredList.removeIf(e -> e.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME));

        if (!MessageDialogUtils.getConfirmMessageDialog("Take Over For All Filtered Records",
            "Press OK to continue to take over selected Workpackage and Responsibility for all filtered rows . No of workpackage responsibilities to be updated : " +
                filteredList.size())) {
          return;
        }
        if (selection.getFirstElement() != null) {
          A2lWpResponsibility selectedWpResp = (A2lWpResponsibility) selection.getFirstElement();
          A2lWpResponsibilityCopyModel model = new A2lWpResponsibilityCopyModel();
          model.setSelectedA2lWpResp(selectedWpResp);
          model.setSelectedColumns(new int[] { 1, 2 });
          saveWpResp(filteredList, model);
        }

      }
    };
    takeOverAction.setText("Take over for all records in filtered selection");
    takeOverAction.setEnabled(this.a2lWPInfoBO.isEditable());
    mgr.add(takeOverAction);

  }

  /**
   * @param selection
   */
  public void paste(final IStructuredSelection selection) {
    @SuppressWarnings("unchecked")
    // take contents from the internal clipboard

    // List of row objects selected for pasting the copied Workpackage or Responsibility
    List<A2lWpResponsibility> rowObjsToUpdate = new ArrayList<>(selection.toList());
    if (rowObjsToUpdate.isEmpty()) {
      CDMLogger.getInstance().errorDialog("No items to paste !", Activator.PLUGIN_ID);
      return;
    }
    Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();

    if (copiedObject instanceof A2lWpResponsibilityCopyModel) {
      final A2lWpResponsibilityCopyModel copiedModel = (A2lWpResponsibilityCopyModel) copiedObject;
      saveWpResp(rowObjsToUpdate, copiedModel);
    }

  }


  /**
   * @param rowObjsToUpdate
   * @param copiedA2lWpRespPal
   */
  private void saveWpResp(final List<A2lWpResponsibility> rowObjsToUpdate,
      final A2lWpResponsibilityCopyModel copiedModel) {
    List<A2lWpResponsibility> objectsToUpdate = new ArrayList<>();
    for (A2lWpResponsibility a2lWpResponsibility : rowObjsToUpdate) {
      A2lWpResponsibility clonedWpResp = a2lWpResponsibility.clone();

      int[] selectedColumns = copiedModel.getSelectedColumns();

      for (int col : selectedColumns) {
        switch (col) {
          case 3:
            clonedWpResp.setA2lRespId(copiedModel.getSelectedA2lWpResp().getA2lRespId());
            break;

          case 4:
            clonedWpResp.setWpNameCust(copiedModel.getSelectedA2lWpResp().getWpNameCust());
            break;

          default:
            break;
        }
      }
      objectsToUpdate.add(clonedWpResp);
    }
    if (CommonUtils.isNotEmpty(objectsToUpdate)) {
      try {
        new A2lWpResponsibilityServiceClient().update(objectsToUpdate, this.a2lWPInfoBO.getPidcA2lBo().getPidcA2l());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

  }


  /**
   * @param toolBarManager
   */
  public void editA2lWpRespAction(final ToolBarManager toolBarManager) {

    this.editA2lWpResp = new Action() {

      @Override
      public void run() {
        CreateEditWpRespDialog wpRespDialog = new CreateEditWpRespDialog(Display.getCurrent().getActiveShell(),
            A2lWpActionSet.this.a2lWPInfoBO, A2lWpActionSet.this.page.getSelectedRow(), true);
        wpRespDialog.open();
        if (null != wpRespDialog.getNew2lWpRespObj()) {
          A2lWpActionSet.this.page.setSelectedRow(wpRespDialog.getNew2lWpRespObj());
        }
      }
    };
    this.editA2lWpResp.setText("Edit Workpackage");
    this.editA2lWpResp.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.editA2lWpResp.setEnabled(false);
    toolBarManager.add(this.editA2lWpResp);

  }

  /**
   * @param toolBarManager
   * @param pidcA2l
   */
  public void deleteA2lWpRespAction(final ToolBarManager toolBarManager, final PidcA2l pidcA2l) {

    this.deleteA2lWpResp = new Action() {

      @Override
      public void run() {

        if (MessageDialogUtils.getConfirmMessageDialog("Delete Workpackage",
            "This will also delete the parameters mapped to the workpackage. Click OK to proceed")) {

          IStructuredSelection selectedRow =
              (IStructuredSelection) A2lWpActionSet.this.page.getSelectionProvider().getSelection();
          List<A2lWpResponsibility> deleteList = selectedRow.toList();
          if (!selectedRow.isEmpty()) {
            try {
              Set<Long> a2lWpRespIdSet = new HashSet<>();
              for (A2lWpResponsibility wpresp : deleteList) {
                a2lWpRespIdSet.add(wpresp.getId());
              }
              new A2lWpResponsibilityServiceClient().delete(a2lWpRespIdSet, pidcA2l);
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
            }
          }
          A2lWpActionSet.this.editA2lWpResp.setEnabled(false);
          return;
        }
      }
    };
    this.deleteA2lWpResp.setText("Delete Workpackage");
    this.deleteA2lWpResp.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteA2lWpResp.setEnabled(false);
    toolBarManager.add(this.deleteA2lWpResp);

  }

  /**
   * @param toolBarManager
   */
  public void loadFromExternalFileAction(final ToolBarManager toolBarManager) {
    WpRespActionSet importAction = new WpRespActionSet();
    Long pidcVersionId = ((A2LContentsEditorInput) this.page.getEditorInput()).getPidcVersion().getId();
    Long a2lFileId = ((A2LContentsEditorInput) this.page.getEditorInput()).getA2lFile().getId();
    Long selectedWpDefnVersionId = this.a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId();
    Action action = importAction.createImportWpRespFromExcelAction(pidcVersionId, a2lFileId, selectedWpDefnVersionId,
        this.a2lWPInfoBO);
    action.setEnabled(this.a2lWPInfoBO.isEditable());
    toolBarManager.add(action);
  }

  /**
   * @param toolBarManager ToolBarManager
   */
  public void loadFromA2lGroupsAction(final ToolBarManager toolBarManager) {

    WpRespActionSet importAction = new WpRespActionSet();

    this.importFromA2lGrpAction = importAction.createImportFromA2lGroupsAction(this.a2lWPInfoBO);
    toolBarManager.add(this.importFromA2lGrpAction);
  }


  /**
   * @param toolBarManager
   */
  public void loadFromFC2WPAction(final ToolBarManager toolBarManager) {

    WpRespActionSet importAction = new WpRespActionSet();
    Long pidcVersionId = ((A2LContentsEditorInput) this.page.getEditorInput()).getPidcVersion().getId();
    Long a2lFileId = ((A2LContentsEditorInput) this.page.getEditorInput()).getA2lFile().getId();
    Long selectedWpDefnVersionId = this.a2lWPInfoBO.getA2lWpDefnModel().getSelectedWpDefnVersionId();

    Action action = importAction.createImportWpRespFromFC2WPAction(pidcVersionId, a2lFileId, selectedWpDefnVersionId,
        true, this.a2lWPInfoBO);
    action.setEnabled(this.a2lWPInfoBO.isEditable());
    toolBarManager.add(action);
  }

  /**
   * This method adds the right click option to set responsibility to WP.
   *
   * @param menuMgr MenuManager instance
   */
  public void setBoschResponsibleAction(final MenuManager menuMgr, final IStructuredSelection selection) {

    List<A2lWpResponsibility> a2lWpRespList = selection.toList();
    final Action setResp = new Action() {

      @Override
      public void run() {
        AddUserResponsibleDialog addBoshUserAsRespDialog =
            new AddUserResponsibleDialog(Display.getCurrent().getActiveShell(), A2lWpActionSet.this.a2lWPInfoBO);
        addBoshUserAsRespDialog.open();
        if (addBoshUserAsRespDialog.getSelectedUser() != null) {
          A2lResponsibility boschWpResp =
              A2lWpActionSet.this.a2lWPInfoBO.createBoschResponsible(addBoshUserAsRespDialog.getSelectedUser());
          callUpdateA2lWpRespPal(boschWpResp, a2lWpRespList);
        }
      }
    };
    setResp.setText(IUIConstants.ENTER_BOSCH_RESP);
    setResp.setEnabled(this.a2lWPInfoBO.isEditable());
    menuMgr.add(setResp);

  }


  /**
   * This method adds the right click option to set responsibility to WP
   *
   * @param menuMgr MenuManager instance
   * @param parentShell nattable shell
   * @param selection IStructuredSelection
   */
  public void setCustomerResponsibleAction(final MenuManager menuMgr, final Shell parentShell,
      final IStructuredSelection selection) {

    List<A2lWpResponsibility> a2lWpRespList = selection.toList();
    final Action setResp = new Action() {

      @Override
      public void run() {
        AddWpRespNonBoschDialog respDialog = new AddWpRespNonBoschDialog(Display.getCurrent().getActiveShell(),
            A2lWpActionSet.this.a2lWPInfoBO, WpRespType.CUSTOMER, A2lWpActionSet.this.a2lWPInfoBO
                .getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpRespList.get(0).getA2lRespId()));
        respDialog.open();
        A2lResponsibility selectedA2lResp = respDialog.getSelectedA2lResp();
        callUpdateA2lWpRespPal(selectedA2lResp, a2lWpRespList);
      }
    };
    setResp.setText(IUIConstants.ENTER_CUSTOMER_RESP);
    setResp.setEnabled(this.a2lWPInfoBO.isEditable());
    menuMgr.add(setResp);

  }

  /**
   * This method adds the right click option to set Others responsibility to WP
   *
   * @param menuMgr MenuManager instance
   * @param selection IStructuredSelection
   */
  public void setOtherResponsibleAction(final MenuManager menuMgr, final IStructuredSelection selection) {

    List<A2lWpResponsibility> a2lWpRespList = selection.toList();
    final Action setResp = new Action() {

      @Override
      public void run() {
        AddWpRespNonBoschDialog respDialog = new AddWpRespNonBoschDialog(Display.getCurrent().getActiveShell(),
            A2lWpActionSet.this.a2lWPInfoBO, WpRespType.OTHERS, A2lWpActionSet.this.a2lWPInfoBO
                .getA2lResponsibilityModel().getA2lResponsibilityMap().get(a2lWpRespList.get(0).getA2lRespId()));
        respDialog.open();
        A2lResponsibility selectedA2lResp = respDialog.getSelectedA2lResp();
        callUpdateA2lWpRespPal(selectedA2lResp, a2lWpRespList);
      }
    };
    setResp.setText(IUIConstants.ENTER_OTHER_RESP);
    setResp.setEnabled(this.a2lWPInfoBO.isEditable());
    menuMgr.add(setResp);
  }


  /**
   * @param selectedA2lResp selected A2L Resp
   * @param a2lWpRespList A2lWPResponsibilty list
   */
  protected void callUpdateA2lWpRespPal(final A2lResponsibility selectedA2lResp,
      final List<A2lWpResponsibility> a2lWpRespList) {
    for (A2lWpResponsibility a2lWpResp : a2lWpRespList) {
      if (selectedA2lResp != null) {
        a2lWpResp.setA2lRespId(selectedA2lResp.getId());
        A2lWpActionSet.this.a2lWPInfoBO.updateA2lWpRespPal(a2lWpResp);
      }
    }
  }


  /**
   * Method to apply column filter for all columns.
   */
  public void applyFilter() {
    A2lWpActionSet.this.page.getCustomFilterGridLayer().getFilterStrategy().applyToolBarFilterInAllColumns(false);
    A2lWpActionSet.this.page.getCustomFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(A2lWpActionSet.this.page.getCustomFilterGridLayer().getSortableColumnHeaderLayer()));
    A2lWpActionSet.this.page.setStatusBarMessage(A2lWpActionSet.this.page.getGroupByHeaderLayer(), false);
    A2lWpActionSet.this.page.getA2lwpdefinitionNattable().redraw();
  }

  /**
   * Filter to Show Work Packages customized at variant group level
   *
   * @param toolbarFilter A2lWPDefToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void variantGroupWpFilter(final A2lWPDefToolBarFilter toolbarFilter, final ToolBarManager toolBarManager) {
    this.isVariantGroupWpAction = new Action(CommonUIConstants.FILTER_SHOW_VARIANT_GROUP_WP_ONLY, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter.setSelVariantGrp(A2lWpActionSet.this.isVariantGroupWpAction.isChecked());
        applyFilter();
      }
    };
    this.isVariantGroupWpAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16));
    toolBarManager.add(this.isVariantGroupWpAction);
    this.isVariantGroupWpAction.setChecked(true);
    this.isVariantGroupWpAction.setEnabled(CommonUtils.isNotNull(this.a2lWPInfoBO.getSelectedA2lVarGroup()));
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isVariantGroupWpAction, this.isVariantGroupWpAction.isChecked());

  }

  /**
   * Filter to Show Work package customization in other variant groups (not 'clicked' by default)
   *
   * @param toolbarFilter A2lWPDefToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void otherVariantGroupWpFilter(final A2lWPDefToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.otherVariantGroupWpAction = new Action(CommonUIConstants.FILTER_SHOW_OTHER_VARIANT_GROUP_WP_ONLY, SWT.TOGGLE) {

      @Override
      public void run() {
        if (A2lWpActionSet.this.otherVariantGroupWpAction.isChecked()) {
          toolbarFilter.setOtherVariantGrp(true);
        }
        else {
          toolbarFilter.setOtherVariantGrp(false);
        }
        applyFilter();
      }
    };
    this.otherVariantGroupWpAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HIDE_16X16));
    toolBarManager.add(this.otherVariantGroupWpAction);
    this.otherVariantGroupWpAction.setChecked(true);
    this.otherVariantGroupWpAction.setEnabled(CommonUtils.isNotNull(this.a2lWPInfoBO.getSelectedA2lVarGroup()));
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.otherVariantGroupWpAction, this.otherVariantGroupWpAction.isChecked());

  }

  /**
   * Filter to Show non-customized Work Packages(WP at default or Pidc level)
   *
   * @param toolbarFilter A2lWPDefToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void showNotAtVGLevelFilter(final A2lWPDefToolBarFilter toolbarFilter, final ToolBarManager toolBarManager) {
    this.notMappedToVGAction = new Action(CommonUIConstants.FILTER_SHOW_NON_VARIANT_GROUP_WP_ONLY, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter.setNotAtVGLevel(A2lWpActionSet.this.notMappedToVGAction.isChecked());
        applyFilter();
      }
    };
    this.notMappedToVGAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SUP_GRP_28X30));
    toolBarManager.add(this.notMappedToVGAction);
    this.notMappedToVGAction.setChecked(true);
    this.isVariantGroupWpAction.setEnabled(CommonUtils.isNotNull(this.a2lWPInfoBO.getSelectedA2lVarGroup()));
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.notMappedToVGAction, this.notMappedToVGAction.isChecked());

  }

  /**
   * @return the deleteA2lWpResp
   */
  public Action getDeleteA2lWpResp() {
    return this.deleteA2lWpResp;
  }


  /**
   * @param deleteA2lWpResp the deleteA2lWpResp to set
   */
  public void setDeleteA2lWpResp(final Action deleteA2lWpResp) {
    this.deleteA2lWpResp = deleteA2lWpResp;
  }


  /**
   * @return the addA2lWpResp
   */
  public Action getAddA2lWpResp() {
    return this.addA2lWpResp;
  }


  /**
   * @param addA2lWpResp the addA2lWpResp to set
   */
  public void setAddA2lWpResp(final Action addA2lWpResp) {
    this.addA2lWpResp = addA2lWpResp;
  }


  /**
   * @return the editA2lWpResp
   */
  public Action getEditA2lWpResp() {
    return this.editA2lWpResp;
  }


  /**
   * @param editA2lWpResp the editA2lWpResp to set
   */
  public void setEditA2lWpResp(final Action editA2lWpResp) {
    this.editA2lWpResp = editA2lWpResp;
  }


  /**
   * @return Action
   */
  public Action getIsVariantGroupWpAction() {
    return this.isVariantGroupWpAction;
  }


  /**
   * @param isVariantGroupWpAction Action
   */
  public void setIsVariantGroupWpAction(final Action isVariantGroupWpAction) {
    this.isVariantGroupWpAction = isVariantGroupWpAction;
  }


  /**
   * @return Action
   */
  public Action getOtherVariantGroupWpAction() {
    return this.otherVariantGroupWpAction;
  }


  /**
   * @param otherVariantGroupWpAction Action
   */
  public void setOtherVariantGroupWpAction(final Action otherVariantGroupWpAction) {
    this.otherVariantGroupWpAction = otherVariantGroupWpAction;
  }


  /**
   * @return Action
   */
  public Action getNotMappedToVGAction() {
    return this.notMappedToVGAction;
  }

  /**
   * @return Action
   */
  public Action getImportFromA2lGrpVGAction() {
    return this.importFromA2lGrpAction;
  }

  /**
   * @param importFromA2lGrpAction the importFromA2lGrpAction to set
   */
  public void setImportFromA2lGrpVGAction(final Action importFromA2lGrpAction) {
    this.importFromA2lGrpAction = importFromA2lGrpAction;
  }


  /**
   * @param notMappedToVGAction Action
   */
  public void setNotMappedToVGAction(final Action notMappedToVGAction) {
    this.notMappedToVGAction = notMappedToVGAction;
  }

  /**
   * @param menuMgr
   * @param selection
   * @param a2lParametersPage
   */
  public void getPreCalibrationData(final MenuManager menuMgr, final IStructuredSelection selection,
      final A2LParametersPage a2lParametersPage) {

    final Action exportCdfxAction = new Action() {

      @Override
      public void run() {
        CDMLogger.getInstance().debug("Retrieving A2L parameters for the selected work packages ...");

        StringBuilder wpWithoutParams = new StringBuilder();
        List<A2LParameter> a2lParams = getParamsFromA2lWpSelection(selection, wpWithoutParams);

        String errorMsg = wpWithoutParams.toString();
        if (!errorMsg.isEmpty()) {
          MessageDialogUtils.getErrorMessageDialog("iCDM Error", GET_PRE_CALIBRATION_DATA_ERROR + errorMsg);
          return;
        }

        CDMLogger.getInstance().debug("A2L parameters retrieved. Params size : {}", a2lParams.size());
        Shell parent = Display.getCurrent().getActiveShell();
        PreCalDataExportWizard exportCDFWizard =
            new PreCalDataExportWizard(a2lParams, A2lWpActionSet.this.a2lWPInfoBO.getA2lFileInfoBo(),
                A2lWpActionSet.this.a2lWPInfoBO.getPidcA2lBo(), a2lParametersPage);
        PreCalDataExportWizardDialog exportCDFWizardDialog = new PreCalDataExportWizardDialog(parent, exportCDFWizard);
        exportCDFWizardDialog.create();
        exportCDFWizardDialog.open();
      }
    };
    exportCdfxAction.setText("Get Pre-Calibration Data");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16);
    exportCdfxAction.setImageDescriptor(imageDesc);
    menuMgr.add(exportCdfxAction);
  }


  /**
   * @param selection
   * @param wpWithoutParams
   * @return
   */
  private List<A2LParameter> getParamsFromA2lWpSelection(final IStructuredSelection selection,
      final StringBuilder wpWithoutParams) {
    Object firstElement = selection.getFirstElement();
    List<A2lWpParamMapping> paramMappingList = new ArrayList<>();
    if (firstElement instanceof A2lWpResponsibility) {
      if (A2lWpActionSet.this.respResolver == null) {
        intializeWpRespResolver();
      }

      List<A2lWpResponsibility> a2lWpList = selection.toList();
      for (A2lWpResponsibility wpResp : a2lWpList) {
        List<A2lWpParamMapping> paramMapping =
            A2lWpActionSet.this.respResolver.getWpRespParamMapping().get(wpResp.getId());
        if (CommonUtils.isNotEmpty(paramMapping)) {
          paramMappingList.addAll(paramMapping);
        }
        else {
          wpWithoutParams.append(wpResp.getName()).append("\n");
        }
      }
    }
    List<A2LParameter> a2lParams = new ArrayList<>();
    for (A2lWpParamMapping paramMapping : paramMappingList) {
      a2lParams.add(A2lWpActionSet.this.a2lWPInfoBO.getA2lFileInfoBo().getA2lParamByName(paramMapping.getName()));
    }
    return a2lParams;
  }

  /**
   * @param toolBarManager ToolBarManager
   */
  public void addActiveVersionAction(final ToolBarManager toolBarManager) {
    WpRespActionSet importAction = new WpRespActionSet();

    this.addActiveVersAction = importAction.addActiveVersionAction(this.a2lWPInfoBO);
    toolBarManager.add(this.addActiveVersAction);
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param pidcVersion
   */
  public void par2WPAssignmentAction(final ToolBarManager toolBarManager, final PidcVersion pidcVersion) {
    WpRespActionSet importAction = new WpRespActionSet();

    Action par2WPAction = importAction.par2WPAssignmentAction(this.a2lWPInfoBO, pidcVersion);
    toolBarManager.add(par2WPAction);
  }

  /**
   * @param menuMgr menu manager
   * @param selection selected WPs
   */
  public void addExportParamsAsLabAction(final MenuManager menuMgr, final IStructuredSelection selection) {

    CDMLogger.getInstance().debug("Retrieving A2L parameters for the selected work packages ...");

    StringBuilder wpWithoutParams = new StringBuilder();
    List<A2LParameter> a2lParams = getParamsFromA2lWpSelection(selection, wpWithoutParams);

    String errorMsg = wpWithoutParams.toString();

    CDMLogger.getInstance().debug("A2L parameters retrieved. Params size : {}", a2lParams.size());

    menuMgr.add(new LabFunExportAction(
        a2lParams.stream().map(A2LParameter::getName).collect(Collectors.toCollection(HashSet::new)), null,
        OUTPUT_FILE_TYPE.LAB, errorMsg));
  }

  /**
   * @param toolBarManager ToolBarManager
   */
  public void createWpsFromFunctionsAction(final ToolBarManager toolBarManager) {

    WpRespActionSet importAction = new WpRespActionSet();
    toolBarManager.add(importAction.createWpsFromFunctionsAction(this.a2lWPInfoBO));
  }

  /**
   * @param toolBarManager ToolBarManager
   */
  public void createResetWorkSplitAction(final ToolBarManager toolBarManager) {

    WpRespActionSet importAction = new WpRespActionSet();
    toolBarManager.add(importAction.restWorkSplitAction(this.a2lWPInfoBO));
  }
}
