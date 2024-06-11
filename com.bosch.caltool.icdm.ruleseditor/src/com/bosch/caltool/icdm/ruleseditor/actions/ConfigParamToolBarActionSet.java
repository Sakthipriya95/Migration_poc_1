/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.ruleseditor.pages.ConfigBasedRulesPage;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ConfigParamToolBarFilters;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.CustomTreeViewer;


/**
 * @author rgo7cob Icdm=1073 - Tool bar for Check all
 */
// ICDM-500
public class ConfigParamToolBarActionSet { // NOPMD by dmo5cob on 4/2/14 2:29 PM

  /**
   * status Line Manager
   */
  private final IStatusLineManager statusLineManager;

  private final ConfigBasedRulesPage page;


  /**
   * @param statusLineManager statusLineManager
   */
  public ConfigParamToolBarActionSet(final IStatusLineManager statusLineManager, final ConfigBasedRulesPage page) {
    this.statusLineManager = statusLineManager;
    this.page = page;

  }


  /**
   * @param paramTableViewer paramTableViewer
   * @param configBasedRulesPage configBasedRulesPage
   * @param rulesTableViewer rulesTableViewer
   * @param toolBarManager toolBarManager
   */
  public void checkAllParams(final CustomGridTableViewer paramTableViewer,
      final ConfigBasedRulesPage configBasedRulesPage, final CustomGridTableViewer rulesTableViewer) {


    GridItem[] items = paramTableViewer.getGrid().getItems();
    for (GridItem gridItem : items) {
      ConfigBasedParam obj = (ConfigBasedParam) gridItem.getData();
      obj.setChecked(true);
      gridItem.setChecked(ConfigBasedRulesPage.CHECK_BOX_COL_IDX, true);
      if (!this.page.getSelectedParamList().contains(obj.getParameter())) {
        this.page.getSelectedParamList().add(obj.getParameter());
      }
      this.page.getEditor().getEditorInput().getConfigParamMap().put(obj.getParameter(), obj);
    }
    configBasedRulesPage.setAttrTabViewerInput();
    rulesTableViewer.setInput("");
    rulesTableViewer.refresh();
    paramTableViewer.setInput(this.page.getEditor().getEditorInput().getConfigParamMap().values());
  }

  /**
   * @param paramTableViewer paramTableViewer
   * @param configBasedRulesPage configBasedRulesPage
   * @param rulesTableViewer rulesTableViewer
   * @param toolBarManager toolBarManager
   */
  public void uncheckAllParams(final CustomGridTableViewer paramTableViewer,
      final ConfigBasedRulesPage configBasedRulesPage, final CustomGridTableViewer rulesTableViewer) {

    GridItem[] items = paramTableViewer.getGrid().getItems();
    for (GridItem gridItem : items) {
      ConfigBasedParam obj = (ConfigBasedParam) gridItem.getData();
      obj.setChecked(false);
      gridItem.setChecked(ConfigBasedRulesPage.CHECK_BOX_COL_IDX, false);
      if (this.page.getSelectedParamList().contains(obj.getParameter())) {
        this.page.getSelectedParamList().remove(obj.getParameter());
      }
      this.page.getEditor().getEditorInput().getConfigParamMap().put(obj.getParameter(), obj);
    }
    configBasedRulesPage.setAttrTabViewerInput();
    rulesTableViewer.setInput("");
    rulesTableViewer.refresh();
    paramTableViewer.setInput(this.page.getEditor().getEditorInput().getConfigParamMap().values());
  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void includedParamFilterAction(final ToolBarManager toolBarManager,
      final ConfigParamToolBarFilters toolBarFilters, final Viewer viewer) {

    final Action includedAction = new Action("Parameters included", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setIncludedInSearch(isChecked());
        setStatus(viewer);
      }
    };
    // Set the image
    includedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.INCLUDE_PARAM_16X16));
    includedAction.setChecked(true);
    toolBarManager.add(includedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(includedAction, includedAction.isChecked());

  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void defaultRuleFilterAction(final ToolBarManager toolBarManager,
      final ConfigParamToolBarFilters toolBarFilters, final Viewer viewer) {

    final Action defaultRuleAction = new Action("Default Rule", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setDefaultRuleSearch(isChecked());
        setStatus(viewer);
      }
    };
    // Set the image
    defaultRuleAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REVIEW_RULES_16X16));
    defaultRuleAction.setChecked(true);
    toolBarManager.add(defaultRuleAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(defaultRuleAction, defaultRuleAction.isChecked());

  }


  /**
   * This method creates rule not filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void ruleNotExistsFilterAction(final ToolBarManager toolBarManager,
      final ConfigParamToolBarFilters toolBarFilters, final Viewer viewer) {

    final Action ruleNotExistsAction = new Action("Rule not exists", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoRuleExistsSearch(isChecked());
        setStatus(viewer);
      }
    };
    // Set the image
    ruleNotExistsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_RULES_EXIST_16X16));
    ruleNotExistsAction.setChecked(true);
    toolBarManager.add(ruleNotExistsAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(ruleNotExistsAction, ruleNotExistsAction.isChecked());

  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void nonDefaultRuleFilterAction(final ToolBarManager toolBarManager,
      final ConfigParamToolBarFilters toolBarFilters, final Viewer viewer) {

    final Action nonDefaultRuleAction = new Action("Non Default Rules", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNonDefaultRuleSearch(isChecked());
        setStatus(viewer);
      }
    };
    // Set the image
    nonDefaultRuleAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DEFAULT_REVIEW_RULES_16X16));
    nonDefaultRuleAction.setChecked(true);
    toolBarManager.add(nonDefaultRuleAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonDefaultRuleAction, nonDefaultRuleAction.isChecked());

  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param viewer instance
   */
  public void notIncludedParamFilterAction(final ToolBarManager toolBarManager,
      final ConfigParamToolBarFilters toolBarFilters, final Viewer viewer) {

    final Action notIncludedAction = new Action("Parameters not included", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNotIncludedInSearch(isChecked());
        setStatus(viewer);
      }
    };
    // Set the image
    notIncludedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_INCLUDED_PARAM_16X16));
    notIncludedAction.setChecked(true);
    toolBarManager.add(notIncludedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notIncludedAction, notIncludedAction.isChecked());

  }

  /**
   * @param viewer
   */
  private void setStatus(final Viewer viewer) {
    if (viewer instanceof GridTableViewer) {
      final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
      customTableViewer.setStatusLineManager(this.statusLineManager);
    }
    if (viewer instanceof CustomTreeViewer) {
      final CustomTreeViewer customTreeViewer = (CustomTreeViewer) viewer;
      customTreeViewer.setStatusLineManager(this.statusLineManager);
    }
    viewer.refresh();
  }
}
