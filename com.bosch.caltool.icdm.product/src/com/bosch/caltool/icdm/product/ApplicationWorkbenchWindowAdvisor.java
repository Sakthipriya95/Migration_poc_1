/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.part.EditorInputTransfer;

import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.LinkProcessor;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditor;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.WindowOpenMode;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.caltool.icdm.product.util.WelcomePagePref;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bne4cob
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  


  /**
   * Constant for the Application window open mode
   */
  private static final String APPLN_WINDOW_OPEN_MODE = "-WMODE";


  /**
   * SB length for title
   */
  private static final int SB_TITLE_INIT_LEN = 50;
  
  /**
   * Activity id for adding/ removing citi tool hotline support from iCDM
   */
  private static final String CITI_ENABLE_ACTIVITY = "com.bosch.caltool.icdm.product.cityEnable";

  /**
   * @param configurer the IWorkbenchWindowConfigurer instance
   */
  public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
    super(configurer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
    return new ApplicationActionBarAdvisor(configurer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void preWindowOpen() {
    final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

    configurer.setShowCoolBar(true);
    configurer.setShowStatusLine(true);
    configurer.setShowPerspectiveBar(true);
    configurer.setShowProgressIndicator(true);


    // Find title

    StringBuilder title = new StringBuilder(SB_TITLE_INIT_LEN);
    title.append(ICDMConstants.ICDM_TITLE_BASE);

    String icdmEnv = null;
    try {
      icdmEnv = new CommonDataBO().getParameterValue(CommonParamKey.ICDM_ENV_CODE);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    CDMLogger.getInstance().info("iCDM environment is : {}", icdmEnv);

    // Add server info to title
    if (!CommonUtils.isEmptyString(icdmEnv)) {
      title.append(" : ").append(icdmEnv);
    }

    // Set application title
    configurer.setTitle(title.toString());

    addDropToEditor(configurer);

    // Set default status message
    setStatusBarDefaultMessage();

  }

  /**
   * The below method enables dropping on the editor title area
   *
   * @param configurer
   */
  // ICDM-1695
  private void addDropToEditor(final IWorkbenchWindowConfigurer configurer) {
    configurer.addEditorAreaTransfer(EditorInputTransfer.getInstance());
    configurer.addEditorAreaTransfer(LocalSelectionTransfer.getTransfer());
    configurer.configureEditorAreaDropListener(new DropTargetListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void dropAccept(final DropTargetEvent event) {
        // NOT_USED
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void drop(final DropTargetEvent event) {
        CTabItem cTabItemToDrop = findDropTargetWidget(event);
        if (cTabItemToDrop != null) {
          performDropOnCompareEditor(event, cTabItemToDrop);
        }
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void dragOver(final DropTargetEvent event) {
        // NOT USED
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void dragOperationChanged(final DropTargetEvent event) {
        // NOT USED
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void dragLeave(final DropTargetEvent event) {
        // NOT USED
      }

      /**
       * This method adds move feature without pressing ctrl. Leaving it to the default implementation will cause move
       * to happen only on pressing ctrl while dragging and dropping {@inheritDoc}
       */
      @Override
      public void dragEnter(final DropTargetEvent event) {
        addDragEnter(event);
      }
    });
  }

  /**
   * @param event
   * @return
   */
  private CTabItem findDropTargetWidget(final DropTargetEvent event) {
    DropTarget dropTarget = (DropTarget) event.widget;
    Composite composite = (Composite) dropTarget.getControl();
    Control[] tabList = composite.getTabList();
    CTabItem cTabItemToDrop = null;
    for (Control control : tabList) {
      if (control instanceof CTabFolder) {
        CTabFolder cTabFolder = (CTabFolder) control;
        CTabItem[] items = cTabFolder.getItems();
        cTabItemToDrop = getItemToDrop(event, items);
      }
    }
    return cTabItemToDrop;
  }

  /**
   * @param event
   * @param cTabItemToDrop
   * @param items
   * @return
   */
  private CTabItem getItemToDrop(final DropTargetEvent event, final CTabItem[] items) {
    CTabItem cTabItemToDrop = null;
    for (CTabItem cTabItem : items) {
      Rectangle mappedBounds = cTabItem.getDisplay().map(cTabItem.getParent(), null, cTabItem.getBounds());
      if (mappedBounds.contains(event.x, event.y)) {
        cTabItemToDrop = cTabItem;
        break;
      }
    }
    return cTabItemToDrop;
  }


  /**
   * @param event
   * @param cTabItemToDrop
   */
  @SuppressWarnings({ "restriction", "unchecked" })
  private void performDropOnCompareEditor(final DropTargetEvent event, final CTabItem cTabItemToDrop) {
    StructuredSelection droppedObject = (StructuredSelection) event.data;
    Object obj = cTabItemToDrop.getData("modelElement");
    if (obj instanceof PartImpl) {
      PartImpl part = (PartImpl) obj;
      IEditorReference editorReference =
          (IEditorReference) part.getTransientData().get(ICDMConstants.WORKBENCH_PART_REFERENCE);
      if (null != editorReference) {
        IEditorPart editorToDrop = editorReference.getEditor(false);
        if (editorToDrop instanceof PIDCCompareEditor) {
          PIDCCompareEditor pidcCompareEditor = (PIDCCompareEditor) editorToDrop;
          String errorMsg = pidcCompareEditor.getComparePIDCPage().addToExistingCompareEditor(droppedObject.toList());
          if (errorMsg.isEmpty()) {
            try {
              PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                  .openEditor(pidcCompareEditor.getEditorInput(), PIDCCompareEditor.EDITOR_ID, true);
            }
            catch (PartInitException exp) {
              CDMLogger.getInstance().error("Error while dropping content to Compare Editor", exp);
            }
          }
          else {
            event.detail = DND.DROP_NONE;
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void postWindowCreate() {

    configureCitiBotVisibility();

    // Set the window maximized during launch.
    final IWorkbenchWindowConfigurer conf = getWindowConfigurer();
    WindowOpenMode wMode = getApplnWindowOpenMode();
    if (wMode == WindowOpenMode.MIN) {
      conf.getWindow().getShell().setMinimized(true);
    }
    else {
      conf.getWindow().getShell().setMaximized(true);
    }

    // Remove unwanted options in Window->Preferences
    final PreferenceManager prefMgr = PlatformUI.getWorkbench().getPreferenceManager();
    prefMgr.remove(ICDMConstants.PREF_ID_DEBUG_OPTION);
    prefMgr.remove(ICDMConstants.PREF_ID_JAVA_OPTION);
    prefMgr.remove(ICDMConstants.PREF_ID_TEAM_OPTION);
    prefMgr.remove(ICDMConstants.PREF_ID_ANT_PREF_OPTION);
    prefMgr.remove(ICDMConstants.PREF_ID_PLUG_IN_OPTION);
    // iCDM-1394
    IPreferenceNode pref = prefMgr.find(ICDMConstants.PREF_ID_WORKBENCH_OPTION);
    // check and remove sub-preferences workbench pref is enabled
    if (pref != null) {
      // Get sub preferences under 'General' preferences
      for (IPreferenceNode prefSubNode : pref.getSubNodes()) {
        // remove irrelavant sub-preferences
        pref.remove(prefSubNode.getId());
      }
    }
    // Remove unwanted perspectivies in Window->Open Perspective
    // ICDM-105
    removeUnwantedPerspectives();
  }

  /**
   *
   */
  private void configureCitiBotVisibility() {
    IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
    IActivityManager activityManager = workbenchActivitySupport.getActivityManager();

    Set enabledActivityIds = new HashSet(activityManager.getEnabledActivityIds());

    try {
      if (new CurrentUserBO().canAccessCitiToolHotline()) {
        enabledActivityIds.add(CITI_ENABLE_ACTIVITY);
      }
      else {
        enabledActivityIds.remove(CITI_ENABLE_ACTIVITY);
      }
      workbenchActivitySupport.setEnabledActivityIds(enabledActivityIds);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug(e.getMessage(), e);
    }
  }

  /**
   * @param args cmd args
   * @return WindowOpenMode
   */
  private WindowOpenMode getApplnWindowOpenMode() {
    String[] args = Platform.getCommandLineArgs();

    String openMode = null;
    int index = 0;
    if (args != null) {
      while (index < args.length) {
        if (APPLN_WINDOW_OPEN_MODE.equals(args[index]) && ((index + 1) < args.length)) {
          openMode = args[index + 1];
          CDMLogger.getInstance().info("Window Open Mode : {}", openMode);
          break;
        }
        index++;
      }
    }
    return openMode == null ? null : WindowOpenMode.valueOf(openMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void postWindowOpen() {

    // Set default status message
    setStatusBarDefaultMessage();
    // Added for opening PIDCard from URL click

    // Get the URL object - iCDM-1241
    URLObject urlObj = getIDFromURL();
    if (urlObj != null) {
      // close intro page before opening url
      CommonUiUtils.hideIntroViewPart();

      // ICDM-1649
      LinkProcessor linkOpener = new LinkProcessor(urlObj.getURLAsString());
      try {
        linkOpener.openLink();
      }
      catch (ExternalLinkException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    else {
      // Display welcome page if preferences is set to true
      IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
      if ((null == prefStore.getString(IUIConstants.WELCOMEPAGE)) ||
          prefStore.getString(IUIConstants.WELCOMEPAGE).isEmpty() ||
          prefStore.getString(IUIConstants.WELCOMEPAGE).equals(WelcomePagePref.SHOW_WELCOME_PAGE.getText())) {
        prefStore.putValue(IUIConstants.WELCOMEPAGE, WelcomePagePref.SHOW_WELCOME_PAGE.getText());
        PlatformUI.getWorkbench().getIntroManager().showIntro(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
            false);
      }
      else {
        prefStore.putValue(IUIConstants.WELCOMEPAGE, WelcomePagePref.NO_WELCOME_PAGE.getText());
        // hide intro page if display welcome page preference is set to false
        CommonUiUtils.hideIntroViewPart();
      }
    }
  }

  /**
   * @return URLObject
   */
  private URLObject getIDFromURL() {
    String[] args = Platform.getCommandLineArgs();

    URLObject uriObj = null;
    ILinkType objType;
    int index = 0;
    if (args != null) {
      while (index < args.length) {
        // ICDM-1649
        objType = LinkRegistry.INSTANCE.getLinkTypeForKey(args[index]);

        if ((objType != null) && ((index + 1) < args.length)) {
          String objId = args[index + 1];
          if (CommonUtils.isNotEmptyString(objId)) {
            uriObj = new URLObject(objType, objId);
          }
          break;
        }
        index++;
      }
    }
    return uriObj;
  }


  /**
   * Removes the unwanted perspectives from your iCDM application
   */
  // ICDM-105
  private void removeUnwantedPerspectives() {
    final IPerspectiveRegistry perspRegistry = PlatformUI.getWorkbench().getPerspectiveRegistry();
    final IPerspectiveDescriptor[] perspDescriptors = perspRegistry.getPerspectives();

    // Add required perspectivies for this application
    List<String> reqPersps = new ArrayList<>();
    reqPersps.add(ICDMConstants.ID_PERSP_ICDM);
    reqPersps.add(ICDMConstants.ID_PERSP_PIDC);
    reqPersps.add(ICDMConstants.ID_PERSP_USECASE);
    reqPersps.add(ICDMConstants.ID_PERSP_CDR);

    List<IPerspectiveDescriptor> removePerspDesc = new ArrayList<>();

    // Add the perspective descriptors with the not matching perspective ids to the list
    for (IPerspectiveDescriptor perspectiveDescriptor : perspDescriptors) {
      if (!reqPersps.contains(perspectiveDescriptor.getId())) {
        removePerspDesc.add(perspectiveDescriptor);
      }
    }

    // If the list is non-empty then remove all such perspectives from the IExtensionChangeHandler
    if ((perspRegistry instanceof IExtensionChangeHandler) && !removePerspDesc.isEmpty()) {
      IExtensionChangeHandler extChgHandler = (IExtensionChangeHandler) perspRegistry;
      extChgHandler.removeExtension(null, removePerspDesc.toArray());
    }
  }

  /**
   * Method which sets default message on the status bar.
   */
  private final void setStatusBarDefaultMessage() {
    // Set the status message
    final IStatusLineManager stsLineMgr = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
    stsLineMgr.setMessage("Welcome to iCDM !");
  }

  /**
   * @param event
   */
  private void addDragEnter(final DropTargetEvent event) {
    if (event.detail == DND.DROP_DEFAULT) {
      if ((event.operations & DND.DROP_MOVE) != 0) {
        event.detail = DND.DROP_MOVE;
      }
      else if ((event.operations & DND.DROP_COPY) != 0) {
        event.detail = DND.DROP_COPY;
      }
      else {
        event.detail = DND.DROP_NONE;
      }
    }
  }
}

