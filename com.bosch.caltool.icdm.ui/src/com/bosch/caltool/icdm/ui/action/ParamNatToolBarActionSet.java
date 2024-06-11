/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.ui.editors.pages.A2LParametersPage;
import com.bosch.caltool.icdm.ui.table.filters.ParamNatToolBarFilter;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * {@link ParamNatToolBarActionSet} is a variant of {@link ParamToolBarActionSet} which accepts a
 * {@link CustomFilterGridLayer} in its constructor to filter NatTable
 *
 * @author jvi6cob
 */
public class ParamNatToolBarActionSet {

  /**
   * Instance of paramnatform page
   */
  private final A2LParametersPage page;


  private final CustomFilterGridLayer<T> filterGridLayer;
  /**
   * withStatusFilter action
   */
  private Action withStatusFilterAction;
  /**
   * withoutStatusFilter action
   */
  private Action withoutStatusFilterAction;
  /**
   * withValueFilter action
   */
  private Action withValueFilterAction;
  /**
   * withoutValueFilter action
   */
  private Action withoutValueFilterAction;
  // ICDM-841
  /**
   * withLABParam action
   */
  private Action withLABParamAction;
  /**
   * withoutLABParam action
   */
  private Action withoutLABParamAction;

  /**
   * @param page page
   */
  public ParamNatToolBarActionSet(final A2LParametersPage page, final CustomFilterGridLayer<T> filterGridLayer) {
    this.page = page;
    this.filterGridLayer = filterGridLayer;
  }


  /**
   * @return the withStatusFilterAction
   */
  public Action getWithStatusFilterAction() {
    return this.withStatusFilterAction;
  }


  /**
   * @return the withoutStatusFilterAction
   */
  public Action getWithoutStatusFilterAction() {
    return this.withoutStatusFilterAction;
  }


  /**
   * @return the withValueFilterAction
   */
  public Action getWithValueFilterAction() {
    return this.withValueFilterAction;
  }


  /**
   * @return the withoutValueFilterAction
   */
  public Action getWithoutValueFilterAction() {
    return this.withoutValueFilterAction;
  }

  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void complianceFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action complianceAction = new Action(CommonUIConstants.FILTER_COMPLIANCE_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setComplianceFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    complianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    complianceAction.setChecked(true);
    toolBarManager.add(complianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(complianceAction, complianceAction.isChecked());
  }

  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void nonComplianceFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action nonComplianceAction = new Action(CommonUIConstants.FILTER_NON_COMPLIANCE_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonComplianceFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    nonComplianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_NON_COMPLIANCE_16X16));
    nonComplianceAction.setChecked(true);
    toolBarManager.add(nonComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonComplianceAction, nonComplianceAction.isChecked());
  }

  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void readOnlyAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action readOnlyAction = new Action("READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReadOnlyParam(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    readOnlyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.READ_ONLY_16X16));
    readOnlyAction.setChecked(true);
    toolBarManager.add(readOnlyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(readOnlyAction, readOnlyAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void rivetFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters) {
    Action rivetAction = new Action(CommonUIConstants.FILTER_RIVET, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setRivetFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for rivet class
    rivetAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RIVET_28X30));
    rivetAction.setChecked(true);
    toolBarManager.add(rivetAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(rivetAction, rivetAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramTableViewer parametersTableViewer
   */
  public void nailFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters) {
    Action nailAction = new Action(CommonUIConstants.FILTER_NAIL, SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNailFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Nail Class
    nailAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NAIL_28X30));
    nailAction.setChecked(true);
    toolBarManager.add(nailAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nailAction, nailAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void screwFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters) {
    Action screwAction = new Action(CommonUIConstants.FILTER_SCREW, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setScrewFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Screw Class
    screwAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SCREW_28X30));
    screwAction.setChecked(true);
    toolBarManager.add(screwAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(screwAction, screwAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void codeYesFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters) {

    Action codeYesAction = new Action(CommonUIConstants.FILTER_CODEWORD_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setYesFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for code Yes
    codeYesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.YES_28X30));
    codeYesAction.setChecked(true);
    toolBarManager.add(codeYesAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(codeYesAction, codeYesAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void codeNoFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters) {
    // Filter For Code No
    Action codeNoAction = new Action(CommonUIConstants.FILTER_CODEWORD_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNoFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Code No
    codeNoAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_28X30));
    codeNoAction.setChecked(true);
    toolBarManager.add(codeNoAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(codeNoAction, codeNoAction.isChecked());

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramTableViewer parametersTableViewer
   */
  public void classUndefinedFilterAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {
    Action classUnDefinedAction = new Action(CommonUIConstants.FILTER_UNDEFINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setUndefinedFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Class Undefined
    classUnDefinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    classUnDefinedAction.setChecked(true);
    toolBarManager.add(classUnDefinedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(classUnDefinedAction, classUnDefinedAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void createWithStatusFilterAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {
    this.withStatusFilterAction = new Action(CommonUIConstants.FILTER_STATUS_AVAILABLE, SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolBarFilters.setWithStatusFlag(true);
        }
        else {
          toolBarFilters.setWithStatusFlag(false);
        }
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Class Undefined
    this.withStatusFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WITH_STATUS_16X16));
    this.withStatusFilterAction.setChecked(true);
    this.withStatusFilterAction.setEnabled(false);
    toolBarManager.add(this.withStatusFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.withStatusFilterAction, this.withStatusFilterAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void createWithoutStatusFilterAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {
    this.withoutStatusFilterAction = new Action(CommonUIConstants.FILTER_STATUS_NOT_AVAILABLE, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWithoutStatusFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Class Undefined
    this.withoutStatusFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_STATUS_16X16));
    this.withoutStatusFilterAction.setChecked(true);
    this.withoutStatusFilterAction.setEnabled(false);
    toolBarManager.add(this.withoutStatusFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.withoutStatusFilterAction, this.withoutStatusFilterAction.isChecked());

  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void createWithValueFilterAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {
    this.withValueFilterAction = new Action(CommonUIConstants.FILTER_VALUE_AVAILABLE, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWithValueFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Class Undefined
    this.withValueFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_CALDATA_VAL_14X14));
    this.withValueFilterAction.setChecked(true);
    this.withValueFilterAction.setEnabled(false);
    toolBarManager.add(this.withValueFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.withValueFilterAction, this.withValueFilterAction.isChecked());

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ParamNatToolBarFilter
   */
  public void createWithoutValueFilterAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {
    this.withoutValueFilterAction = new Action(CommonUIConstants.FILTER_VALUE_NOT_AVAILABLE, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWithoutValueFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    // Set the image for Class Undefined
    this.withoutValueFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_CALDATA_VAL_14X14));
    this.withoutValueFilterAction.setChecked(true);
    this.withoutValueFilterAction.setEnabled(false);
    toolBarManager.add(this.withoutValueFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.withoutValueFilterAction, this.withoutValueFilterAction.isChecked());

  }

  /**
   * ICDM-841
   *
   * @param toolBarManager toolbarmng instance
   * @param toolBarFilters toolbarfilter instance
   */
  public void createWithLABParamAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {

    this.withLABParamAction = new Action(CommonUIConstants.FILTER_LAB_PARAM, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWithLabParam(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    this.withLABParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.LAB_16X16));
    this.withLABParamAction.setChecked(true);
    this.withLABParamAction.setEnabled(false);
    toolBarManager.add(this.withLABParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.withLABParamAction, this.withLABParamAction.isChecked());
  }

  /**
   * ICDM-841
   *
   * @param toolBarManager toolbarmng instance
   * @param toolBarFilters toolbarfilter instance
   */
  public void createWithoutLABParamAction(final ToolBarManager toolBarManager,
      final ParamNatToolBarFilter toolBarFilters) {
    this.withoutLABParamAction = new Action(CommonUIConstants.FILTER_NOT_LAB_PARAM, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWithoutLabParam(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
      }
    };
    this.withoutLABParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.LAB_DEL_16X16));
    this.withoutLABParamAction.setChecked(true);
    this.withoutLABParamAction.setEnabled(false);
    toolBarManager.add(this.withoutLABParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.withoutLABParamAction, this.withoutLABParamAction.isChecked());
  }


  /**
   * @return the withLABParamAction
   */
  public Action getWithLABParamAction() {
    return this.withLABParamAction;
  }


  /**
   * @return the withoutLABParamAction
   */
  public Action getWithoutLABParamAction() {
    return this.withoutLABParamAction;
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void notReadOnlyAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {
    Action notReadOnlyAction = new Action("Not READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNotReadOnlyParam(isChecked());

        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    notReadOnlyAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_READ_ONLY_16X16));
    notReadOnlyAction.setChecked(true);
    toolBarManager.add(notReadOnlyAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notReadOnlyAction, notReadOnlyAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void depnParamAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {
    Action depnParamAction = new Action("Dependent Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setDepnParamFlag(isChecked());

        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    depnParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_DEPN_16X16));
    depnParamAction.setChecked(true);
    toolBarManager.add(depnParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(depnParamAction, depnParamAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void notDepnParamAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {
    Action notDepnParamAction = new Action("Not Dependent Parameters", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNotDepnParamFlag(isChecked());

        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    notDepnParamAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_NO_DEPN_16X16));
    notDepnParamAction.setChecked(true);
    toolBarManager.add(notDepnParamAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notDepnParamAction, notDepnParamAction.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void blackListFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action blackListAction = new Action("Black List Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setBlackListFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    blackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BLACK_LIST_LABEL));
    blackListAction.setChecked(true);
    toolBarManager.add(blackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(blackListAction, blackListAction.isChecked());
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void nonBlackListFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action nonBlackListAction = new Action("Non Black List Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonBlackListFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for compliance filter
    nonBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));
    nonBlackListAction.setChecked(true);
    toolBarManager.add(nonBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonBlackListAction, nonBlackListAction.isChecked());
  }


  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ParamNatToolBarFilter
   * @param natTable CustomNATTable
   */
  public void qSSDFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {
    Action qSSDAction = new Action("QSSD Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setqSSDFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for qSSD filter
    qSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));
    qSSDAction.setChecked(true);
    toolBarManager.add(qSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(qSSDAction, qSSDAction.isChecked());
  }


  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ParamNatToolBarFilter
   * @param natTable CustomNATTable
   */
  public void nonQSSDFilterAction(final ToolBarManager toolBarManager, final ParamNatToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {
    Action nonQSSDAction = new Action("Non QSSD Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonQSSDFlag(isChecked());
        ParamNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
        ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
            new FilterAppliedEvent(ParamNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
        ParamNatToolBarActionSet.this.page
            .setStatusBarMessage(ParamNatToolBarActionSet.this.page.getGroupByHeaderLayer(), false);
        natTable.redraw();
      }
    };
    // Set the image for non QSSD Label
    nonQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));
    nonQSSDAction.setChecked(true);
    toolBarManager.add(nonQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonQSSDAction, nonQSSDAction.isChecked());


  }


}
