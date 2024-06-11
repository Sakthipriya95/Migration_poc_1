/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.editors.pages.RiskEvaluationPage;
import com.bosch.caltool.apic.ui.table.filters.RiskEvalToolBarFilter;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * The Class RiskEvalToolBarActionSet.
 *
 * @author gge6cob
 */
public class RiskEvalToolBarActionSet {


  /** The Constant SHOW_ALL. */
  private static final String SHOW_ALL = "Show all Project Character(s)";

  /** The Constant IS_RELEVANT_YES. */
  private static final String IS_RELEVANT_YES = "is Relevant : Yes";

  /** The Constant IS_RELEVANT_NO. */
  private static final String IS_RELEVANT_NO = "is Relevant : No";

  /** The Constant IS_RELEVANT_NOTDEF. */
  private static final String IS_RELEVANT_NOTDEF = "is Relevant : Not Defined";

  /** The filter grid layer. */
  private final CustomFilterGridLayer<PidcRMCharacterMapping> filterGridLayer;

  /** The code show all children. */
  private Action codeShowAllChildren;

  /** The page. */
  private final RiskEvaluationPage page;

  /** The code is relevant. */
  private Action codeIsRelevant;

  /** The code is relevant not def. */
  private Action codeIsRelevantNotDef;

  /** The code is not relevant. */
  private Action codeIsNotRelevant;

  /**
   * Instantiates a new risk eval tool bar action set.
   *
   * @param riskFilterGridLayer the risk filter grid layer
   * @param page the page
   */
  public RiskEvalToolBarActionSet(final CustomFilterGridLayer<PidcRMCharacterMapping> riskFilterGridLayer,
      final RiskEvaluationPage page) {
    this.page = page;
    this.filterGridLayer = riskFilterGridLayer;
  }

  /**
   * Method to apply column filter for all columns.
   */
  public void applyColumnFilter() {
    // Toolbar filter for all Columns : IMP to trigger filter events in NAT table
    RiskEvalToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    RiskEvalToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(RiskEvalToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
  }

  /**
   * Show all children.
   *
   * @param toolBarformManager the tool barform manager
   * @param toolBarFilters the tool bar filters
   */
  public void showAllChildren(final ToolBarManager toolBarformManager, final RiskEvalToolBarFilter toolBarFilters) {
    this.codeShowAllChildren = new Action(SHOW_ALL, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setShowAllEntries(isChecked());
        applyColumnFilter();
      }
    };

    this.codeShowAllChildren.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16));
    this.codeShowAllChildren.setChecked(false);
    toolBarformManager.add(this.codeShowAllChildren);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeShowAllChildren, this.codeShowAllChildren.isChecked());
  }

  /**
   * Checks if is relevant.
   *
   * @param toolBarformManager the tool barform manager
   * @param toolBarFilters the tool bar filters
   */
  public void isRelevant(final ToolBarManager toolBarformManager, final RiskEvalToolBarFilter toolBarFilters) {
    this.codeIsRelevant = new Action(IS_RELEVANT_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setRelevant(isChecked());
        applyColumnFilter();
      }
    };

    this.codeIsRelevant.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.USED_16X16));
    this.codeIsRelevant.setChecked(true);
    toolBarformManager.add(this.codeIsRelevant);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsRelevant, this.codeIsRelevant.isChecked());
  }

  /**
   * Checks if is relevant not defined.
   *
   * @param toolBarformManager the tool barform manager
   * @param toolBarFilters the tool bar filters
   */
  public void isRelevantNotDefined(final ToolBarManager toolBarformManager,
      final RiskEvalToolBarFilter toolBarFilters) {
    this.codeIsRelevantNotDef = new Action(IS_RELEVANT_NOTDEF, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setRelevantNotDefined(isChecked());
        applyColumnFilter();
      }
    };

    this.codeIsRelevantNotDef.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FILTER_QUESTION_16X16));
    this.codeIsRelevantNotDef.setChecked(true);
    toolBarformManager.add(this.codeIsRelevantNotDef);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsRelevantNotDef, this.codeIsRelevantNotDef.isChecked());
  }

  /**
   * Checks if is not relevant.
   *
   * @param toolBarformManager the tool barform manager
   * @param toolBarFilters the tool bar filters
   */
  public void isNotRelevant(final ToolBarManager toolBarformManager, final RiskEvalToolBarFilter toolBarFilters) {
    this.codeIsNotRelevant = new Action(IS_RELEVANT_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotRelevant(isChecked());
        applyColumnFilter();
      }
    };

    this.codeIsNotRelevant.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_USED_16X16));
    this.codeIsNotRelevant.setChecked(true);
    toolBarformManager.add(this.codeIsNotRelevant);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsNotRelevant, this.codeIsNotRelevant.isChecked());
  }

  /**
   * Gets the show all entries action.
   *
   * @return the show all entries action
   */
  public Action getShowAllEntriesAction() {
    return this.codeShowAllChildren;
  }
}
