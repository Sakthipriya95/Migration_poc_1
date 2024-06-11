/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.UCP_ATTR_MAPPING_FLAGS;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UcpAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;
import com.bosch.caltool.usecase.ui.dialogs.AddUseCaseDialog;
import com.bosch.caltool.usecase.ui.dialogs.AddUseCaseGroupDialog;
import com.bosch.caltool.usecase.ui.dialogs.AddUseCaseSectionDialog;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditor;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;
import com.bosch.caltool.usecase.ui.editors.pages.UseCaseNatAttributesPage;
import com.bosch.caltool.usecase.ui.jobs.UseCaseExportJob;
import com.bosch.caltool.usecase.ui.util.IUIConstants;
import com.bosch.caltool.usecase.ui.views.UseCaseDetailsPage;
import com.bosch.caltool.usecase.ui.views.UseCaseDetailsViewPart;
import com.bosch.caltool.usecase.ui.views.UseCaseTreeViewPart;
import com.bosch.caltool.usecase.ui.views.listeners.UseCaseListener;


/**
 * @author dmo5cob This class has the set of action events for the tree view in PIDC Perspective
 */
public class UseCaseActionSet {

  private static Map<Long, List<UseCaseListener>> ucDetailsPageListeners = new ConcurrentHashMap<>();

  private static final String STR_ARROW = " >> ";

  /**
   * constant for Ok result in Dialog open
   */
  public static final int OK_STATUS = 0;
  
  private  UsecaseClientBO newlyCreatedUseCase;
  
  private UsecaseEditorModel useCaseEditorDataInput;
  

  /**
   * @param manager IMenuManager
   * @param item Use case Item
   */
  public void addExportMenu(final IMenuManager manager, final SortedSet<IUseCaseItemClientBO> items) {
    final Action addExportAction = new Action() {


      @Override
      public void run() {
        exportAction(items);
      }
    };

    addExportAction.setText("Excel Report");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16);
    addExportAction.setImageDescriptor(imageDesc);
    manager.add(addExportAction);
  }

  /**
   * @param items usecase items
   */
  public void exportAction(final SortedSet<IUseCaseItemClientBO> items) {
    String fileName = (items.size() == 1) ? items.first().getName() : "All Use Cases";
    fileName = FileNameUtil.formatFileName(fileName, ApicConstants.INVALID_CHAR_PTRN);
    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save Excel Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    fileDialog.setOverwrite(true);
    String fileSelected = fileDialog.open();

    if (fileSelected != null) {
      String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
      // Call for export the Usecase
      Job job = new UseCaseExportJob(new MutexRule(), items, fileSelected, fileExtn);
      com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance()
          .showView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROGRESS_VIEW);
      job.schedule();
    }
  }


  /**
   * @param manager IMenuManager
   * @param item Use case Item
   */

  public void addUseCaseGroup(final IMenuManager manager, final UseCaseGroupClientBO item) {
    final Action addUCGroupAction = new Action() {

      @Override
      public void run() {
        AddUseCaseGroupDialog addUCGrpDialog = new AddUseCaseGroupDialog(Display.getDefault().getActiveShell(), item);
        addUCGrpDialog.open();

      }
    };
    CurrentUserBO currUser = new CurrentUserBO();
    // Add conditions for deleted Value also icdm-358
    addUCGroupAction.setEnabled(false);
    if (null == item) {
      try {
        if (currUser.canCreateUseCase()) {
          addUCGroupAction.setEnabled(true);
        }
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    else if (item.isModifiable() && item.canCreateSubGroup()) {
      addUCGroupAction.setEnabled(true);
    }
    addUCGroupAction.setText("Add Use Case Group");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.UC_GRP_28X30);
    addUCGroupAction.setImageDescriptor(imageDesc);
    manager.add(addUCGroupAction);
  }


  /**
   * @param manager IMenuManager
   * @param item item
   */
  public void addUseCases(final IMenuManager manager, final Object item) {
    final Action addUCAction = new Action() {

      @Override
      public void run() {
        AddUseCaseDialog addUcDialog = new AddUseCaseDialog(Display.getDefault().getActiveShell(), item);
        int returnValue = addUcDialog.open();

        // if creation of new usecase is successful then open the created use case in usecase editor
        if ((returnValue == 0) && addUcDialog.isUserPrefToOpen()) {
          setUCEditorDataInputWithProgressBar(addUcDialog);
          UseCaseTreeViewActionSet actionSet = new UseCaseTreeViewActionSet();
          actionSet.openUseCaseEditor(newlyCreatedUseCase,  useCaseEditorDataInput);
        }
      }
    };
    addUCAction.setText("Add Use Case");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.UC_28X30);
    addUCAction.setImageDescriptor(imageDesc);
    // Add condition for deleted Element icdm-358
    addUCAction.setEnabled((item instanceof UseCaseGroupClientBO) && ((UseCaseGroupClientBO) item).isModifiable() &&
        ((UseCaseGroupClientBO) item).canCreateUseCase());
    manager.add(addUCAction);
    
  }

  private void setUCEditorDataInputWithProgressBar(AddUseCaseDialog addUcDialog) {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Creating New Use Case ...", 100);
        newlyCreatedUseCase = new UsecaseClientBO(addUcDialog.getNewlyCreatedUseCase(), addUcDialog.getUcGrp());
        try {

          useCaseEditorDataInput = new UseCaseServiceClient().getUseCaseEditorData(newlyCreatedUseCase.getId());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error("Error in invoking thread to open progress bar for creating the new usecase !", e);
      Thread.currentThread().interrupt();
    }}

  


  /**
   * @param manager IMenuManager
   * @param ucSectionBO useCaseItem
   * @param viewer viewer
   * @param usecaseEditorModel UsecaseEditorModel
   * @param usecaseBO UsecaseClientBO
   */
  public void addUseCaseSection(final IMenuManager manager, final UseCaseSectionClientBO ucSectionBO,
      final UsecaseEditorModel usecaseEditorModel, final UsecaseClientBO usecaseBO) {
    final Action addUCSectAction = new Action() {

      @Override
      public void run() {
        AddUseCaseSectionDialog addUcDialog =
            new AddUseCaseSectionDialog(Display.getDefault().getActiveShell(), ucSectionBO, usecaseBO);
        addUcDialog.open();
      }
    };
    addUCSectAction.setText("Add Use Case Section");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.UCS_28X30);
    addUCSectAction.setImageDescriptor(imageDesc);
    // Add condition for delete for icdm-358
    if ((null != ucSectionBO) && ucSectionBO.isModifiable()) {
      addUCSectAction.setEnabled(true);
    }

    addUCSectAction.setEnabled(usecaseBO.isModifiable());

    manager.add(addUCSectAction);

  }


  /**
   * @param editorUseCase refresh Use Case Details Page icdm-358
   */
  public void refreshUsecaseDetails(final UsecaseClientBO editorUseCase, final boolean isDelete) {

    final List<UseCaseListener> listeners = ucDetailsPageListeners.get(editorUseCase.getUseCase().getId());
    if (listeners != null) {
      for (UseCaseListener customDetailsPageListener : listeners) {
        customDetailsPageListener.refreshTreeViewer(false);
        if (isDelete) {
          // Incase of a delete disable the update and delete
          final UseCaseDetailsPage ucDetail = (UseCaseDetailsPage) customDetailsPageListener;
          ucDetail.getDeleteAction().setEnabled(false);
          ucDetail.getUpdateAction().setEnabled(false);
          manipulateButtonsInTreeView();

        }
      }
    }
  }

  /**
   * disable buttons in usecase tree view
   */
  private void manipulateButtonsInTreeView() {
    final IViewPart viewPartObj =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IUIConstants.USECASE_TREE_VIEW);
    if ((viewPartObj instanceof UseCaseTreeViewPart)) {
      final UseCaseTreeViewPart ucViewPart = (UseCaseTreeViewPart) viewPartObj;
      ucViewPart.createUpdateAction().setEnabled(false);
      ucViewPart.createDeleteAction().setEnabled(false);
    }
  }


  /**
   * Renames all editors open with the given PIDCard
   *
   * @param useCaseItem IUseCaseItemClientBO
   */
  public void renameAllEditorWithUseCase(final IUseCaseItemClientBO useCaseItem) {

    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {

      final IEditorReference[] editorReferences =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
      for (IEditorReference iEditorReference : editorReferences) {
        try {
          final IEditorInput editorInput = iEditorReference.getEditorInput();
          if (editorInput instanceof UseCaseEditorInput) {
            renameUCEditor(useCaseItem, iEditorReference, editorInput);
          }
        }
        catch (PartInitException exp) {
          CDMLogger.getInstance().warn(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    }
  }

  /**
   * @param useCaseItem
   * @param iEditorReference
   * @param editorInput
   */
  private void renameUCEditor(final IUseCaseItemClientBO useCaseItem, IEditorReference iEditorReference,
      final IEditorInput editorInput) {
    final UseCaseEditorInput ucEditorInput = (UseCaseEditorInput) editorInput;
    // Data Base Change Notification for Use case Update the editor title
    final IEditorPart editorPart = iEditorReference.getEditor(false);
    if (editorPart instanceof UseCaseEditor) {
      final UseCaseEditor editor = (UseCaseEditor) editorPart;
      final UseCaseNatAttributesPage ucNatPage = editor.getNatAttrPage();
      final NodeAccessRightsPage rightsPage = editor.getRightsPage();
      setUCNatPageTitleText(useCaseItem, ucEditorInput, editor, ucNatPage, rightsPage);
      // ICDM 952 To enable selection in the UseCaseDetailsViewPart which refreshed the NatTable in turn
      final IViewPart viewPartObj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("com.bosch.caltool.usecase.ui.views.UseCaseDetailsViewPart");
      if ((viewPartObj instanceof UseCaseDetailsViewPart)) {
        setCurrentPage(viewPartObj);
      }
    }
  }

  /**
   * @param useCaseItem
   * @param ucEditorInput
   * @param editor
   * @param ucNatPage
   * @param rightsPage
   */
  private void setUCNatPageTitleText(final IUseCaseItemClientBO useCaseItem, final UseCaseEditorInput ucEditorInput,
      final UseCaseEditor editor, final UseCaseNatAttributesPage ucNatPage, final NodeAccessRightsPage rightsPage) {
    StringBuilder titleText = new StringBuilder();
    if (ucEditorInput.getSelectedUseCase().getUseCase().getId().equals(useCaseItem.getID())) {
      // Change Editor Title
      titleText.append(ucNatPage.getUseCase().getName());
      editor.setEditorPartName(titleText.toString());
      rightsPage.setTitleText(titleText.toString());
      // Change PIDCPage Title since PIDCPage has no setActive method,need to update title and refresh
      // contents manually
      ucNatPage.setTitleText(titleText.toString());
    }

    else {
      IUseCaseItemClientBO ucItem = useCaseItem;
      titleText.append(ucItem.getName());

      while ((ucItem instanceof UseCaseSectionClientBO)) {
        if (!ucItem.getName().equals(useCaseItem.getName())) {
          titleText.delete(0, titleText.length());
          titleText.append(ucItem.getName() + STR_ARROW + titleText);
        }
        ucItem = ucItem.getParent();
      }
      if (null != ucItem) {
        titleText.delete(0, titleText.length());
        titleText.append(ucItem.getName() + STR_ARROW + titleText);
      }
      if ((null != ucItem) && (ucItem.getID().longValue() == ucEditorInput.getSelectedUseCase().getID().longValue())) {
        rightsPage.setTitleText(titleText.toString());
        ucNatPage.setTitleText(titleText.toString());
      }
    }
  }

  /**
   * @param viewPartObj
   */
  private void setCurrentPage(final IViewPart viewPartObj) {
    final UseCaseDetailsViewPart ucViewPart = (UseCaseDetailsViewPart) viewPartObj;
    if (ucViewPart.getCurrentPage() instanceof UseCaseDetailsPage) {
      UseCaseDetailsPage currentPage = (UseCaseDetailsPage) ucViewPart.getCurrentPage();
      currentPage.getViewer().getControl().notifyListeners(SWT.Selection, null);
    }
  }



  /**
   * This method refreshes the UsecaseAttributesPage table viewer
   *
   * @param editorUseCase AbstractUseCaseItem
   */
  public void refreshEditorWithUseCase(final UsecaseClientBO editorUseCase) {

    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
      final IEditorReference[] editorReferences =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
      getCurrentPageAndRefresh(editorUseCase, editorReferences);
    }
  }

  /**
   * @param editorUseCase
   * @param editorReferences
   */
  private void getCurrentPageAndRefresh(final UsecaseClientBO editorUseCase, final IEditorReference[] editorReferences) {
    for (IEditorReference iEditorReference : editorReferences) {
      try {
        final IEditorInput editorInput = iEditorReference.getEditorInput();
        // check if it is PIDC Editor
        if (editorInput instanceof UseCaseEditorInput) {
          final UseCaseEditorInput ucEditorInput = (UseCaseEditorInput) editorInput;
          if (ucEditorInput.getSelectedUseCase().getID().equals(editorUseCase.getID())) {
            final IEditorPart editorPart = iEditorReference.getEditor(false);
            if (!(editorPart instanceof UseCaseEditor)) {
              continue;
            }
            // ICDM 952 To enable selection in the UseCaseDetailsViewPart which refreshed the NatTable in turn
            final IViewPart viewPartObj = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .findView("com.bosch.caltool.usecase.ui.views.UseCaseDetailsViewPart");
            if ((viewPartObj instanceof UseCaseDetailsViewPart)) {
              final UseCaseDetailsViewPart ucViewPart = (UseCaseDetailsViewPart) viewPartObj;
              UseCaseDetailsPage currentPage = (UseCaseDetailsPage) ucViewPart.getCurrentPage();
              currentPage.getViewer().getControl().notifyListeners(SWT.Selection, null);
            }
          }
        }
      }
      catch (PartInitException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }

    }
  }

  /**
   * @param useCase refresh the Use case Tree After Changes
   */
  public void refreshUsecaseTree(final IUseCaseItemClientBO useCase) {
    final IViewPart viewPartObj =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IUIConstants.USECASE_TREE_VIEW);
    if ((viewPartObj instanceof UseCaseTreeViewPart)) {
      final UseCaseTreeViewPart ucViewPart = (UseCaseTreeViewPart) viewPartObj;
      ucViewPart.getViewer().refresh(useCase);
    }

  }


  /**
   * @param editorUseCase IUseCaseItemClientBO
   * @param useCaseDetailsPage useCaseDetailsPage
   */
  public static void registerDetailsListener(final IUseCaseItemClientBO editorUseCase,
      final UseCaseDetailsPage useCaseDetailsPage) {
    Long objId;
    if (editorUseCase instanceof UsecaseClientBO) {
      objId = ((UsecaseClientBO) editorUseCase).getID();
    }
    else {
      objId = ((UseCaseGroupClientBO) editorUseCase).getID();
    }
    List<UseCaseListener> detailsPageListeners = ucDetailsPageListeners.get(objId);
    if (detailsPageListeners == null) {
      detailsPageListeners = new ArrayList<>();
      detailsPageListeners.add(useCaseDetailsPage);
      ucDetailsPageListeners.put(objId, detailsPageListeners);
    }
    else {
      detailsPageListeners.add(useCaseDetailsPage);
    }

  }


  /**
   * @param pidcDetailsPage pidcDetailsPage
   * @param useCaseId useCaseId
   */
  public static void removeDetailsListener(final Long useCaseId, final UseCaseListener pidcDetailsPage) {
    List<UseCaseListener> listeners = ucDetailsPageListeners.get(useCaseId);
    listeners.remove(pidcDetailsPage);
    if (listeners.isEmpty()) {
      ucDetailsPageListeners.remove(useCaseId);
    }

  }

  /**
   * @param manager - menu manager
   * @param selectedUcpAttrMap selected ucpAttr from user
   */
  public void setQuotationNotRelevantAction(final IMenuManager manager, final Map<Long, UcpAttr> selectedUcpAttrMap) {

    final Action quotationNotRelevantAction = new Action() {

      @Override
      public void run() {
        UcpAttrServiceClient ucpAttrClient = new UcpAttrServiceClient();
        try {
          setQuotNotRelevantFlag(selectedUcpAttrMap);
          ucpAttrClient.update(selectedUcpAttrMap);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };

    quotationNotRelevantAction.setText("Mark as Quotation not Relevant");
    manager.add(quotationNotRelevantAction);

  }

  /**
   * @param manager - menu manager
   * @param selectedUcpAttrMap selected ucpAttr from user
   */
  public void setQuotationRelevantAction(final IMenuManager manager, final Map<Long, UcpAttr> selectedUcpAttrMap) {

    final Action quotationRelevantAction = new Action() {

      @Override
      public void run() {
        UcpAttrServiceClient ucpAttrClient = new UcpAttrServiceClient();
        try {
          setQuotRelevantFlag(selectedUcpAttrMap);
          ucpAttrClient.update(selectedUcpAttrMap);
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    };

    quotationRelevantAction.setText("Mark as Quotation Relevant");
    manager.add(quotationRelevantAction);

  }

  private void setQuotNotRelevantFlag(final Map<Long, UcpAttr> selectedUcpAttrMap) {
    for (UcpAttr ucpAttr : selectedUcpAttrMap.values()) {
      Long quotationRelevant = 0L;
      quotationRelevant = UCP_ATTR_MAPPING_FLAGS.QUOTATION_RELEVANT.removeFlag(quotationRelevant);
      ucpAttr.setMappingFlags(quotationRelevant);
    }
  }

  private void setQuotRelevantFlag(final Map<Long, UcpAttr> selectedUcpAttrMap) {
    for (UcpAttr ucpAttr : selectedUcpAttrMap.values()) {
      Long quotationRelevant = 0L;
      quotationRelevant = UCP_ATTR_MAPPING_FLAGS.QUOTATION_RELEVANT.setFlag(quotationRelevant);
      ucpAttr.setMappingFlags(quotationRelevant);
    }
  }

  /**
   * @param menuMgr - menu manager
   * @param ucpAttrList - list of UcpAttrs to be created
   */
  public void setMapAllAction(final IMenuManager menuMgr, final List<UcpAttr> ucpAttrList) {

    final Action mapAllAction = new Action() {

      @Override
      public void run() {
        UcpAttrServiceClient ucpAttrServiceClient = new UcpAttrServiceClient();
        try {
          ucpAttrServiceClient.create(ucpAttrList);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };

    mapAllAction.setText("Map to all Use Case/Section(s)");
    mapAllAction.setEnabled(CommonUtils.isNotEmpty(ucpAttrList));
    menuMgr.add(mapAllAction);
  }

  /**
   * @param menuMgr - menu manager
   * @param unMapAllAttrsSet set of UcpAttrs to be unmapped
   */
  public void setUnmapAllAction(final MenuManager menuMgr, final Set<UcpAttr> unMapAllAttrsSet) {

    final Action unMapAllAction = new Action() {

      @Override
      public void run() {
        UcpAttrServiceClient ucpAttrServiceClient = new UcpAttrServiceClient();
        try {
          ucpAttrServiceClient.delete(unMapAllAttrsSet);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };

    unMapAllAction.setText("Un-Map all Use Case/Section(s)");
    unMapAllAction.setEnabled(CommonUtils.isNotEmpty(unMapAllAttrsSet));
    menuMgr.add(unMapAllAction);
  }


}
