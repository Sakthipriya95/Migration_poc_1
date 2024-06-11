/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;

import com.bosch.caltool.icdm.common.ui.filters.AbstractPatternFilter;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.nattable.CustomComboGlazedListsFilterStrategy;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGlazedListsFilterStrategy;

/**
 * @author dmo5cob
 */
public class ResetFiltersAction extends Action {

  /**
   * ResetFiltersAction constant
   */
  private static final String RESET_FILTERS = "Reset All Filters";
  /**
   * ToolBarManager instance
   */
  private final IToolBarManager tooBarMngr;
  /**
   * Set of tableviewers in a page
   */
  private final Set<Object> refreshCompSet;
  /**
   * Set of type filter texts in a page
   */
  private final Set<Text> filterTxtSet;
  /**
   * Set of AbstractViewerFilter instances in a page
   */
  private Set<ViewerFilter> viewerFltrs;
  /**
   * CustomGlazedListsFilterStrategy instance in a page
   */
  private CustomGlazedListsFilterStrategy<?> customFilterStrategy;
  /**
   * CustomComboGlazedListsFilterStrategy instance in a page
   */
  private CustomComboGlazedListsFilterStrategy<?> customComboFilterStrategy;


  private final Map<Action, Boolean> filterMap;

  /**
   * @param tooBarMngr toolBarManager instance
   * @param refreshCmpntSet set of tableViewers in the page
   * @param filterTxtList set of filter texts in the page
   */
  public ResetFiltersAction(final IToolBarManager tooBarMngr, final Set<Object> refreshCmpntSet,
      final Set<Text> filterTxtList, final Map<Action, Boolean> filtersMap) {
    super(RESET_FILTERS, ImageManager.getImageDescriptor(ImageKeys.RESET_FILTERS_16X16));
    setToolTipText(RESET_FILTERS);
    // set description
    setDescription(RESET_FILTERS);
    setEnabled(false);
    this.refreshCompSet = refreshCmpntSet == null ? new HashSet<>() : new HashSet<>(refreshCmpntSet);
    this.tooBarMngr = tooBarMngr;
    this.filterTxtSet = filterTxtList == null ? new HashSet<>() : new HashSet<>(filterTxtList);
    this.filterMap = filtersMap;
  }

  /**
   * Refresh the table viewer
   */
  @Override
  public final void run() {
    // In case of gridtableviewers
    if ((this.viewerFltrs != null) && !this.viewerFltrs.isEmpty()) {
      resetPredefinedFilters(true);
      resetOutLineFilter();
      resetTextFilters();
      resetTableViewer();
    } // In case of Nat Table
    if (this.customFilterStrategy != null) {
      resetTextFilters();
      this.customFilterStrategy.resetAllFilters();
      resetPredefinedFilters(true);
      resetOutLineFilter();
      resetTableViewer();
    }

    if (this.customComboFilterStrategy != null) {
      resetTextFilters();
      this.customComboFilterStrategy.resetAllFilters();
      resetPredefinedFilters(true);
      resetOutLineFilter();
      resetTableViewer();
    }

    // update toolbar
    this.tooBarMngr.update(true);
    setEnabled(false);
  }

  /**
   * Reset predefined filters
   */
  private void resetPredefinedFilters(final boolean activate) {
    // get toolbar items
    IContributionItem[] items = this.tooBarMngr.getItems();
    for (IContributionItem iContributionItem : items) {
      if (iContributionItem instanceof ActionContributionItem) {
        ActionContributionItem actionItem = (ActionContributionItem) iContributionItem;
        IAction action = actionItem.getAction();
        // Reset the filters
        if (isActionOtherThanClearFilter(action) && isActionOtherThanAllAttrsFilter(action) &&
            checkIfFilterStatesChanged(action) && !(action instanceof HelpIconAction)) {
          action.setChecked(this.filterMap.get(action));
          // execute filter condition
          if (activate) {
            action.run();
          }
        }
      }
    }
  }


  /**
   * Reset text filters
   */
  private void resetTextFilters() {
    // clear the type filters
    if ((null != this.filterTxtSet) && !this.filterTxtSet.isEmpty()) {
      for (Text txt : this.filterTxtSet) {
        txt.setText("");
      }
    }
  }

  /**
   * Reset outline filters
   */
  private void resetOutLineFilter() {
    boolean flag = false;
    // iterate thru the filtrs
    for (ViewerFilter filter : this.viewerFltrs) {
      if (filter instanceof AbstractViewerFilter) {
        AbstractViewerFilter abstFilter = (AbstractViewerFilter) filter;
        if (!abstFilter.getFilterText().isEmpty() &&
            !abstFilter.getFilterText().equals(AbstractViewerFilter.DUMMY_FILTER_TXT)) {
          abstFilter.setFilterText("");
          flag = true;
        }
      }
    }
    if (this.customFilterStrategy != null) {
      if (this.customFilterStrategy.getOutlineFilterMatcherEditor().getMatcherEditors().isEmpty()) {
        flag = true;
      }
    }

    if (this.customComboFilterStrategy != null) {
      if (this.customComboFilterStrategy.getOutlineFilterMatcherEditor().getMatcherEditors().isEmpty()) {
        flag = true;
      }
    }

    if (flag) {
      // refresh outline tree to remove the selection
      CommonActionSet act = new CommonActionSet();
      act.refreshOutlinePages(true);
    }
  }

  /**
   * reset Table
   */
  @SuppressWarnings("rawtypes")
  private void resetTableViewer() {

    // refresh the tableviewers
    if ((null != this.refreshCompSet) && !this.refreshCompSet.isEmpty()) {
      for (Object obj : this.refreshCompSet) {
        // GridTableViewer
        if (obj instanceof GridTableViewer) {
          GridTableViewer tableViewer = (GridTableViewer) obj;
          tableViewer.refresh();
        } // FilteredTree
        else if (obj instanceof FilteredTree) {
          FilteredTree tableTree = (FilteredTree) obj;
          tableTree.getViewer().refresh();
          tableTree.getFilterControl().setText("");
        } // NatTable
        else if (obj instanceof NatTable) {
          NatTable natTable = (NatTable) obj;
          natTable.refresh();
        } // GridLayer
        else if (obj instanceof GridLayer) {
          ((CustomFilterGridLayer) obj).getFilterStrategy().applyToolBarFilterInAllColumns(false);
        }
      }
    }
  }

  /**
   * This method checks if all of the filters are unchecked then the clearfilter button is disabled
   */
  private boolean checkIfAllFiltersDisabled() {
    // Check all predefined filter actions
    IContributionItem[] items = this.tooBarMngr.getItems();
    int count = 0;
    int actionsCount = 0;
    // iterate thru the toolbar items
    for (IContributionItem iContributionItem : items) {
      if (iContributionItem instanceof ActionContributionItem) {
        ActionContributionItem actionItem = (ActionContributionItem) iContributionItem;
        // Get the action instance
        IAction action = actionItem.getAction();
        // check if action is enabled
        if (isActionOtherThanClearFilter(action) && isActionOtherThanAllAttrsFilter(action) && action.isEnabled()) {
          actionsCount++;
        }
        if (isActionOtherThanClearFilter(action) && !checkIfFilterStatesChanged(action) && action.isEnabled() &&
            isActionOtherThanAllAttrsFilter(action)) {
          count++;
        }
      }
    }
    // If all of the predefined filters are checked then the reset filter button is disabled
    if (count == actionsCount) {
      // If the type text filters are empty then the reset filter button is disabled
      for (Text txtFltr : this.filterTxtSet) {
        if ((null != txtFltr) && !txtFltr.getText().isEmpty()) {
          return false;
        }
      }
      // If all of the type outline filter is checked then the reset filter button is disabled
      for (ViewerFilter filter : this.viewerFltrs) {
        if (filter instanceof AbstractViewerFilter) {
          AbstractViewerFilter absFilter = (AbstractViewerFilter) filter;
          if (!absFilter.getFilterText().isEmpty() &&
              !absFilter.getFilterText().equals(AbstractViewerFilter.DUMMY_FILTER_TXT)) {
            return false;
          }
        } // AbstractPatternFilter
        else if (filter instanceof AbstractPatternFilter) {
          AbstractPatternFilter pFilter = (AbstractPatternFilter) filter;
          if (!pFilter.getFilterText().isEmpty() &&
              !pFilter.getFilterText().equals(AbstractViewerFilter.DUMMY_FILTER_TXT)) {
            return false;
          }
        }

      }
      // nat table
      return !(((this.customFilterStrategy != null) &&
          !this.customFilterStrategy.getOutlineFilterMatcherEditor().getMatcherEditors().isEmpty()) ||
          ((this.customComboFilterStrategy != null) &&
              !this.customComboFilterStrategy.getOutlineFilterMatcherEditor().getMatcherEditors().isEmpty()));
    }
    return false;
  }

  /**
   * Method to check if filter state has changed from its default state
   *
   * @param action
   * @return true if filter state is different from default state, false if filter state is same as default state
   */
  private boolean checkIfFilterStatesChanged(final IAction action) {

    if (!CommonUtils.isNullOrEmpty(this.filterMap) && (this.filterMap.get(action) != null) &&
        this.filterMap.get(action).equals(action.isChecked())) {
      return false;
    }
    return true;
  }


  /**
   * Enable reset filter action
   */
  public void enableResetFilterAction() {
    setEnabled(!checkIfAllFiltersDisabled());
  }


  /**
   * @param action
   * @return
   */
  private boolean isActionOtherThanClearFilter(final IAction action) {
    return !(action instanceof ResetFiltersAction);
  }

  /**
   * @param action
   * @return
   */
  private boolean isActionOtherThanAllAttrsFilter(final IAction action) {
    return !((action instanceof FilterAction) && !((FilterAction) action).isEnabledByDefault());


  }

  /**
   * @return the viewerFltrs
   */
  public Set<ViewerFilter> getViewerFltrs() {
    return this.viewerFltrs;
  }


  /**
   * @param viewerFltrs the viewerFltrs to set
   */
  public void setViewerFltrs(final Set<ViewerFilter> viewerFltrs) {
    this.viewerFltrs = viewerFltrs;
  }


  /**
   * @return the customFilterStrategy
   */
  public CustomGlazedListsFilterStrategy<?> getCustomFilterStrategy() {
    return this.customFilterStrategy;
  }


  /**
   * @param customFilterStrategy the customFilterStrategy to set
   */
  public void setCustomFilterStrategy(final CustomGlazedListsFilterStrategy<?> customFilterStrategy) {
    this.customFilterStrategy = customFilterStrategy;
  }

  /**
   * @return the customComboFilterStrategy
   */
  public CustomComboGlazedListsFilterStrategy<?> getCustomComboFilterStrategy() {
    return this.customComboFilterStrategy;
  }


  /**
   * @param customComboFilterStrategy the customComboFilterStrategy to set
   */
  public void setCustomComboFilterStrategy(final CustomComboGlazedListsFilterStrategy<?> customComboFilterStrategy) {
    this.customComboFilterStrategy = customComboFilterStrategy;
  }


}
