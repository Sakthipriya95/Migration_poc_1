/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.framework.HandlerRegistry;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ICnsApplicabilityCheckerChangeData;
import com.bosch.caltool.icdm.client.bo.framework.ICnsRefresherDce;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.AbstractPageBookView;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.cns.ICnsListener;


/**
 * ICDM UI Listener implementation for display change events. The listener will forward the event to all ui components,
 * if they require a UI refresh for the given change event.
 * <p>
 * The refresh occurs asynchronously.
 *
 * @author bne4cob
 */
public class CnsUiListener implements ICnsListener {

  /**
   * Keeps track of data handlers and their refrsh applicability for this change event processing activity
   */
  private final ConcurrentMap<IClientDataHandler, Boolean> processedHandlerMap = new ConcurrentHashMap<>();

  /**
   * Keeps the valid changes identified for the processed handlers, for re-use. This ensures that valid changes remain
   * the same, when same handler is used in multiple UI components
   */
  private final Map<IClientDataHandler, Map<IModelType, Map<Long, ChangeData<?>>>> handlerValidChangeMap =
      new HashMap<>();

  /**
   * Iterates through all views and editors of the application. If the component requires a refresh, initiates it.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final void onChangeNotification(final DisplayChangeEvent dce) {
    CDMLogger.getInstance().debug("UI-DCE-Listener : Display change event received. Source = {}", dce.getSource());

    HandlerRegistry.INSTANCE.getAllSingletonDataHandlerSet().forEach(hndlr -> refreshHandler(dce, hndlr));

    Arrays.stream(PlatformUI.getWorkbench().getWorkbenchWindows())
        .forEach(window -> Arrays.stream(window.getPages()).forEach(page -> {
          sentChangeToViews(dce, page);
          sendChangeToEditors(dce, page);
        }));

  }

  /**
   * Send the change event to all views
   *
   * @param event DisplayChangeEvent
   * @param page workbench page
   */
  private void sendChangeToEditors(final DisplayChangeEvent dce, final IWorkbenchPage page) {
    // iCDM-241
    IWorkbenchPart editorPart;
    for (IEditorReference edRef : page.getEditorReferences()) {
      editorPart = edRef.getPart(false);

      // Handle child pages
      if (editorPart instanceof AbstractFormEditor) {
        ((AbstractFormEditor) editorPart).getPages().forEach(childPage -> sendChangesToUI(dce, childPage));
      }

      // Handle Editor
      sendChangesToUI(dce, editorPart);
    }

  }

  /**
   * Send the change event to all editors
   *
   * @param event DisplayChangeEvent
   * @param page work bench page
   */
  private void sentChangeToViews(final DisplayChangeEvent dce, final IWorkbenchPage page) {
    IWorkbenchPart viewPart;
    for (IViewReference viewReference : page.getViewReferences()) {
      viewPart = viewReference.getPart(false);

      // Handle child pages if applicable
      if (viewPart instanceof AbstractPageBookView) {
        ((AbstractPageBookView) viewPart).getPages().forEach(childPage -> sendChangesToUI(dce, childPage));
      }

      // Handle View
      sendChangesToUI(dce, viewPart);
    }
  }

  /**
   * Send the change event to the UI Part
   *
   * @param event DisplayChangeEvent
   * @param uiObject Workbench Part
   */
  private void sendChangesToUI(final DisplayChangeEvent dce, final Object uiObject) {
    if (uiObject instanceof IDceRefresher) {

      final IDceRefresher dceRefresher = (IDceRefresher) uiObject;
      DisplayChangeEvent validDce = refreshHandler(dce, dceRefresher.getDataHandler());

      if (validDce != null) {
        String uiType = uiObject.getClass().getSimpleName();
        String uiTitle =
            uiObject instanceof IWorkbenchPart ? ((IWorkbenchPart) uiObject).getTitle() : uiObject.toString();

        CDMLogger.getInstance().debug("UI-DCE-Listener : Refresh applicable to WB Part - {} {} ", uiType, uiTitle);

        CommonUiUtils.getInstance().getDisplay().asyncExec(() -> {
          dceRefresher.refreshUI(validDce);
          CDMLogger.getInstance().info("{}({}) refresh completed", uiType, uiTitle);
        });

      }

    }

  }


  /**
   * Checks whether the incoming display change event is applicable for the client data handler. If so, it refreshes.
   * Also a new display change event is returned, containing only valid changes for the UI component
   *
   * @param dce
   * @param handler
   * @return
   */
  private DisplayChangeEvent refreshHandler(final DisplayChangeEvent dce, final IClientDataHandler handler) {

    DisplayChangeEvent ret = null;

    if (handler != null) {

      CDMLogger.getInstance().debug("Client handler {} : checking refresh applicability ...", handler);

      // Avoid merge of same refresh handler multiple times, if used in diferent UI components
      Boolean status = this.processedHandlerMap.get(handler);

      if (status == null) {
        // Refresh status not computed before
        CDMLogger.getInstance().debug(" Client handler {} : Is refresh applicable check started", handler);
        Map<IModelType, Map<Long, ChangeData<?>>> validChgMap = isRefreshApplicableToHandler(dce, handler);
        status = !validChgMap.isEmpty();
        CDMLogger.getInstance().debug(" Client handler {} : Is refresh applicable = {}", handler, status);

        if (status) {
          // if there is valid change data, refresh the handler
          this.handlerValidChangeMap.put(handler, validChgMap);
          CDMLogger.getInstance().debug(" Client handler {} : Refresh started", handler);
          ret = createValidDataDce(dce, validChgMap);
          ICnsRefresherDce rfshr = handler.getCnsRefresherDce();
          rfshr.refresh(ret);
          CDMLogger.getInstance().debug(" Client handler {} : Refresh completed", handler);
        }
        this.processedHandlerMap.put(handler, status);
      }
      else if (status) {
        // If the client handler has valid changes but the refresh has already happened,
        // just return the applicable change data
        CDMLogger.getInstance().debug(" Client handler {} : Is refresh applicable = true. Refresh already completed.",
            handler);
        ret = createValidDataDce(dce, this.handlerValidChangeMap.get(handler));
      }

    }

    return ret;
  }

  /**
   * @param dce
   * @param validChgMap
   * @return
   */
  private DisplayChangeEvent createValidDataDce(final DisplayChangeEvent originalDce,
      final Map<IModelType, Map<Long, ChangeData<?>>> validChgMap) {
    DisplayChangeEvent ret = new DisplayChangeEvent();
    ret.setSource(originalDce.getSource());
    ret.setConsChangeData(validChgMap);
    return ret;
  }

  /**
   * Check whether refresh is applicable to the handler
   *
   * @param dce input change event
   * @param clientHndlr handler
   * @return valid changes
   */
  private Map<IModelType, Map<Long, ChangeData<?>>> isRefreshApplicableToHandler(final DisplayChangeEvent dce,
      final IClientDataHandler clientHndlr) {

    Map<IModelType, Map<Long, ChangeData<?>>> allValidChDtaMap = new HashMap<>();
    if (CommonUtils.isNullOrEmpty(clientHndlr.getCnsApplicabilityCheckersChangeData())) {
      return allValidChDtaMap;
    }
    for (Entry<IModelType, ICnsApplicabilityCheckerChangeData> chkrEntry : clientHndlr
        .getCnsApplicabilityCheckersChangeData().entrySet()) {

      Map<Long, ChangeData<?>> chDataMap = dce.getConsChangeData().get(chkrEntry.getKey());
      if (chDataMap == null) {
        continue;
      }
      Map<Long, ChangeData<?>> validChDtaMap = new HashMap<>();
      ICnsApplicabilityCheckerChangeData chkr = chkrEntry.getValue();
      for (ChangeData<?> chData : chDataMap.values()) {
        if (chkr.isRefreshApplicable(chData)) {
          validChDtaMap.put(chData.getObjId(), chData);
        }
      }
      if (!validChDtaMap.isEmpty()) {
        allValidChDtaMap.put(chkrEntry.getKey(), validChDtaMap);
      }
    }

    return allValidChDtaMap;
  }
}
