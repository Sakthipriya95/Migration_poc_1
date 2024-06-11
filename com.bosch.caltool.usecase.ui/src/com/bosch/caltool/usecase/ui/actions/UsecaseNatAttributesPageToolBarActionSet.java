/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.actions;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.usecase.ui.editors.pages.UseCaseNatAttributesPage;
import com.bosch.caltool.usecase.ui.table.filters.UseCaseAttrPageNatToolBarFilters;


/**
 * @author jvi6cob
 */
public class UsecaseNatAttributesPageToolBarActionSet {

  private final UseCaseNatAttributesPage page;
  private final CustomFilterGridLayer<T> filterGridLayer;

  /**
   * @param useCaseNatAttributesPage page
   * @param filterGridLayer CustomFilterGridLayer<T>
   */
  public UsecaseNatAttributesPageToolBarActionSet(final UseCaseNatAttributesPage useCaseNatAttributesPage,
      final CustomFilterGridLayer<T> filterGridLayer) {

    this.page = useCaseNatAttributesPage;
    this.filterGridLayer = filterGridLayer;
  }

  /**
   * This method creates Use Case mapping 'All' filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createUseCaseAttrAllFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create attribute all filter action
    Action attrAllAction = new Action("Use Case mapping 'All'", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrAllSelectedFlag(isChecked());
        controlFilterAction();
      }
    };
    // Set the image for attribute all mandatory filter action
    attrAllAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_ALL_16X16));
    attrAllAction.setChecked(true);
    toolBarManager.add(attrAllAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrAllAction, attrAllAction.isChecked());


  }

  /**
   * This method calls another function to make the selection change and refreshes the grid viewer
   *
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  private void controlFilterAction() {
    this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    this.filterGridLayer.getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
    this.page.setStatusBarMessage(false, false);
  }

  /**
   * This method creates Use Case mapping 'Any' filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createUseCaseAttrAnyFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create attribute any filter action
    Action attrAnyAction = new Action("Use Case mapping 'Any'", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrAnySelectedFlag(isChecked());
        controlFilterAction();
      }
    };
    // Set the image for attribute any mandatory filter action
    attrAnyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_ANY_16X16));
    attrAnyAction.setChecked(true);
    toolBarManager.add(attrAnyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrAnyAction, attrAnyAction.isChecked());


  }

  /**
   * This method creates Use Case mapping 'None' filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createUseCaseAttrNoneFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create attribute none filter action
    Action attrNoneAction = new Action("Use Case mapping 'None'", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNoneSelectedFlag(isChecked());
        controlFilterAction();
      }
    };
    // Set the image for attribute all mandatory filter action
    attrNoneAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_NONE_16X16));
    attrNoneAction.setChecked(true);
    toolBarManager.add(attrNoneAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrNoneAction, attrNoneAction.isChecked());


  }

  /**
   * This method creates Mandatory Attributes filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createAttrMandatoryFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create mandatory filter action
    Action attrMandatoryAction = new Action("Mandatory Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrMandatorySel(isChecked());
        controlFilterAction();
      }

    };
    // Set the image for attribute mandatory filter action
    attrMandatoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_MANDATORY_16X16));
    attrMandatoryAction.setChecked(true);
    toolBarManager.add(attrMandatoryAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrMandatoryAction, attrMandatoryAction.isChecked());

  }

  /**
   * This method creates Non-Mandatory Attributes filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createAttrNonMandatoryFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create non-mandatory filter action
    Action attrNonMandatoryAction = new Action("Non-Mandatory Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNonMandatorySel(isChecked());
        controlFilterAction();
      }
    };
    // Set the image for attribute non-mandatory filter action
    attrNonMandatoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_MANDATORY_28X30));
    attrNonMandatoryAction.setChecked(true);
    toolBarManager.add(attrNonMandatoryAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrNonMandatoryAction, attrNonMandatoryAction.isChecked());

  }

  /**
   * This method creates Dependent Attributes filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createAttrDepenFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create mandatory filter action
    Action attrDepAction = new Action("Dependent Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrDepSel(isChecked());
        controlFilterAction();
      }

    };
    // Set the image for attribute mandatory filter action
    attrDepAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DEPN_ATTR_28X30));
    attrDepAction.setChecked(true);
    toolBarManager.add(attrDepAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrDepAction, attrDepAction.isChecked());

  }

  /**
   * This method creates Non-dependent Attributes filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createAttrNonDepenFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {
    // Create mandatory filter action
    Action attrNonDepAction = new Action("Non-dependent Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNonDepSel(isChecked());
        controlFilterAction();
      }

    };
    // Set the image for attribute mandatory filter action
    attrNonDepAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_NONDEP_16X16));
    attrNonDepAction.setChecked(true);
    toolBarManager.add(attrNonDepAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(attrNonDepAction, attrNonDepAction.isChecked());
  }

  /**
   * This method creates Quotation releveant Attributes filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createQuotationRelevantFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {

    Action quotRelevantAction = new Action("Quotation relevant Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQuotationRelevantUcAttr(isChecked());
        controlFilterAction();
      }
    };

    quotRelevantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUOTATION_FLAG));
    quotRelevantAction.setChecked(true);

    toolBarManager.add(quotRelevantAction);

    // Add the default state to filters map
    this.page.addToToolBarFilterMap(quotRelevantAction, quotRelevantAction.isChecked());
  }

  /**
   * This method creates Non Quoatation relevant Attributes filter action
   *
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters UseCaseAttrPageNatToolBarFilters
   */
  public void createQuotationNotRelevantFilterAction(final ToolBarManager toolBarManager,
      final UseCaseAttrPageNatToolBarFilters toolBarFilters) {

    Action quotNotRelevantAction = new Action("Non Quotation relevant Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQuotationNotRelevantUcAttr(isChecked());
        controlFilterAction();
      }
    };

    quotNotRelevantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUOTATION_NOT_RELEVANT_FLAG));
    quotNotRelevantAction.setChecked(true);

    toolBarManager.add(quotNotRelevantAction);

    // Add the default state to filters map
    this.page.addToToolBarFilterMap(quotNotRelevantAction, quotNotRelevantAction.isChecked());
  }


}
