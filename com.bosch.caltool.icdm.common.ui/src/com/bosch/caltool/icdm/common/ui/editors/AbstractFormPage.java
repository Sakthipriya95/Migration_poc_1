/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.editors;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.ResetFiltersAction;
import com.bosch.caltool.icdm.common.ui.filters.AbstractPatternFilter;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.listeners.IDceRefresher;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.nattable.CustomComboGlazedListsFilterStrategy;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGlazedListsFilterStrategy;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.CustomTreeViewer;


/**
 * Abstraction for FormPage
 *
 * @author adn1cob
 */
// iCDM-241
public abstract class AbstractFormPage extends FormPage implements IDceRefresher {

  /**
   * help link key prefix for views
   */
  private static final String HELP_LINK_PREFIX = "EDITOR_";

  /**
   * Clear all filters action instance
   */
  private ResetFiltersAction resetFiltersAction;


  /**
   * Map to hold the filter action text and its default state (checked - true or false)
   */
  private final Map<Action, Boolean> toolBarFilterMap = new ConcurrentHashMap<>();

  /**
   * All the type filter text instances in the page
   */
  private final Set<Text> filterTxtSet = new HashSet<>();
  /**
   * All the tableviewers in the page
   */
  private final Set<Object> refreshComponentSet = new HashSet<>();
  /**
   * All the filters in the page
   */
  private final Set<ViewerFilter> viewerFilters = new HashSet<>();

  /**
   * @param editor editor
   * @param formID page id
   * @param title title
   */
  public AbstractFormPage(final FormEditor editor, final String formID, final String title) {
    super(editor, formID, title);
  }

  // icdm-253
  /**
   * @param formID id
   * @param title title
   */
  public AbstractFormPage(final String formID, final String title) {
    super(formID, title);

  }


  /**
   * Status updation based on selection in the view part
   *
   * @param viewPartID putline/details view part id
   * @param viewer selected table viewer
   */
  // ICDM-343
  protected void initializeViewSiteStatusLineManager(final String viewPartID, final GridTableViewer viewer) {
    CustomGridTableViewer customGridTableViewer = (CustomGridTableViewer) viewer;
    IViewSite viewPartSite =
        (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewPartID).getSite();
    IStatusLineManager statusLineManagerViewPart = viewPartSite.getActionBars().getStatusLineManager();
    customGridTableViewer.setStatusLineManager(statusLineManagerViewPart);

  }

  /**
   * Status updation based on selection in the view part
   *
   * @param viewer selected table viewer
   */
  public void initializeEditorStatusLineManager(final Viewer viewer) {
    if (viewer instanceof CustomGridTableViewer) {
      CustomGridTableViewer customGridTableViewer = (CustomGridTableViewer) viewer;
      IStatusLineManager editorStatus = getEditorSite().getActionBars().getStatusLineManager();
      customGridTableViewer.setStatusLineManager(editorStatus);
    }
    if (viewer instanceof CustomTreeViewer) {
      CustomTreeViewer customTreeViewer = (CustomTreeViewer) viewer;
      IStatusLineManager editorStatus = getEditorSite().getActionBars().getStatusLineManager();
      customTreeViewer.setStatusLineManager(editorStatus);
    }

  }

  /**
   * input for status line
   *
   * @param viewer Selected gridtable viewer
   */
  // ICDM-343
  public void setStatusBarMessage(final Viewer viewer) {
    if (!(viewer instanceof CustomGridTableViewer) && !(viewer instanceof CustomTreeViewer)) {
      return;
    }
    if (viewer instanceof GridTableViewer) {
      GridTableViewer gridViewer = (GridTableViewer) viewer;
      int totalItemCount = getTotalCount(viewer);
      int filteredItemCount = gridViewer.getGrid().getItemCount();
      initializeEditorStatusLineManager(gridViewer);
      CustomGridTableViewer customGridTable = (CustomGridTableViewer) gridViewer;
      customGridTable.updateStatusBar(totalItemCount, filteredItemCount);
    }
    if (viewer instanceof TreeViewer) {
      TreeViewer treeViewer = (TreeViewer) viewer;
      int totalItemCount = getTotalCount(viewer);
      int filteredItemCount = treeViewer.getTree().getItemCount();
      initializeEditorStatusLineManager(treeViewer);
      CustomTreeViewer customGridTable = (CustomTreeViewer) treeViewer;
      customGridTable.updateStatusBar(totalItemCount, filteredItemCount);
    }

  }

  /**
   * Gets the total row count
   *
   * @param viewer
   * @return
   */
  private int getTotalCount(final Viewer viewer) {
    Collection<?> items = null;
    int totalItemCount = 0;
    if ((null != viewer.getInput()) && !(viewer.getInput().toString().isEmpty())) {
      items = (Collection<?>) viewer.getInput();
    }
    if (items != null) {
      totalItemCount = items.size();
    }
    return totalItemCount;
  }

  /**
   * // ICDM-1207
   */
  protected void addResetFiltersAction() {
    // add seperator
    getToolBarManager().add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

    this.resetFiltersAction =
        new ResetFiltersAction(getToolBarManager(), getRefreshComponentSet(), getFilterTxtSet(), this.toolBarFilterMap);
    this.resetFiltersAction.setViewerFltrs(getViewerFilters());

    getToolBarManager().add(this.resetFiltersAction);
    getToolBarManager().update(true);

  }

  /**
   * add Help icon if its applicable
   *
   * @param toolBarManager ToolBarManager
   */
  protected void addHelpAction(final ToolBarManager toolBarManager) {
    Class<?> parentClass = getMainUIClass();
    String parentName = parentClass == null ? "" : parentClass.getSimpleName() + "_";
    String suffixForHelpKey = HELP_LINK_PREFIX + parentName + getClass().getSimpleName();
    new CommonActionSet().addHelpAction(toolBarManager, suffixForHelpKey);
  }

  /**
   * @return
   */
  protected Class<?> getMainUIClass() {
    FormEditor editor = getEditor();
    return editor == null ? null : editor.getClass();
  }

  /**
   * @return ToolBarManager instance
   */
  protected abstract IToolBarManager getToolBarManager();


  /**
   * @return the clearAction
   */
  public ResetFiltersAction getResetFiltersAction() {
    return this.resetFiltersAction;
  }


  /**
   * @return the filterTxtSet
   */
  public Set<Text> getFilterTxtSet() {
    return this.filterTxtSet;
  }


  /**
   * @return the viewerFilters
   */
  private Set<ViewerFilter> getViewerFilters() {

    for (Object obj : getRefreshComponentSet()) {

      ViewerFilter[] vwrFilters = null;
      vwrFilters = setViewFilterSelection(obj, vwrFilters);

      if (null != vwrFilters) {
        for (ViewerFilter viewerFilter : vwrFilters) {
          if (viewerFilter instanceof AbstractViewerFilter) {
            ((AbstractViewerFilter) viewerFilter).setResetFiltersAction(getResetFiltersAction());
            this.viewerFilters.add(viewerFilter);
          }
          else if (viewerFilter instanceof AbstractPatternFilter) {
            ((AbstractPatternFilter) viewerFilter).setResetFilters(getResetFiltersAction());
            this.viewerFilters.add(viewerFilter);
          }
        }
      }
    }

    return this.viewerFilters;
  }

  /**
   * @param obj
   * @param vwrFilters
   * @return
   */
  private ViewerFilter[] setViewFilterSelection(final Object obj, ViewerFilter[] vwrFilters) {

    CustomGlazedListsFilterStrategy<?> natFilterStrategy;
    CustomComboGlazedListsFilterStrategy<?> comboNatFilterStrategy;

    if (obj instanceof GridTableViewer) {
      GridTableViewer tableVwr = (GridTableViewer) obj;
      vwrFilters = tableVwr.getFilters();
    }
    else if (obj instanceof CustomGridTableViewer) {
      CustomGridTableViewer tableVwr = (CustomGridTableViewer) obj;
      vwrFilters = tableVwr.getFilters();
    }
    else if (obj instanceof FilteredTree) {
      FilteredTree filTree = (FilteredTree) obj;
      vwrFilters = filTree.getViewer().getFilters();
    }
    else if (obj instanceof CustomTreeViewer) {
      // ICDM-1185
      CustomTreeViewer tableVwr = (CustomTreeViewer) obj;
      vwrFilters = tableVwr.getFilters();
    }
    else if (obj instanceof CustomFilterGridLayer) {
      CustomFilterGridLayer<?> tableVwr = (CustomFilterGridLayer<?>) obj;

      natFilterStrategy = tableVwr.getFilterStrategy();
      this.resetFiltersAction.setCustomFilterStrategy(natFilterStrategy);

      comboNatFilterStrategy = tableVwr.getComboGlazedListsFilterStrategy();
      this.resetFiltersAction.setCustomComboFilterStrategy(comboNatFilterStrategy);
    }

    return vwrFilters;
  }

  /**
   * @return the refreshComponentSet
   */
  public Set<Object> getRefreshComponentSet() {
    return this.refreshComponentSet;
  }


  /**
   * Method to add to toolBarFilterMap
   *
   * @param action Action
   * @param defaultState default State
   */
  public void addToToolBarFilterMap(final Action action, final Boolean defaultState) {
    this.toolBarFilterMap.put(action, defaultState);
  }

  /**
   * @return data handler
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return null;
  }

  /**
   * @param dce DisplayChangeEvent
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    // Default implementation - no actions
  }

}
