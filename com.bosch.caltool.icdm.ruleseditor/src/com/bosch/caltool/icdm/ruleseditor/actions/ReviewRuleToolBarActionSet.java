/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ParamNatTable;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ReviewParamToolBarFilters;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.CustomTreeViewer;


/**
 * @author rgo7cob
 */
// ICDM-500
public class ReviewRuleToolBarActionSet { // NOPMD by dmo5cob on 4/2/14 2:29 PM


  private static final String READY_FOR_SERIES = "READY_FOR_SERIES";

  private static final String CDR_RULE = "CDR_RULE";

  /**
   * status Line Manager
   */
  protected final IStatusLineManager statusLineManager;

  /**
   * Page instance
   */
  protected final AbstractFormPage page;


  /**
   * Defines the Action for saving Review data
   */
  protected Action saveReviewDetails;

  private CustomFilterGridLayer<T> filterGridLayer;

  /**
   * @param statusLineManager statusLineManager
   */
  public ReviewRuleToolBarActionSet(final IStatusLineManager statusLineManager, final AbstractFormPage page) {
    this.statusLineManager = statusLineManager;
    this.page = page;
    if (this.page instanceof ParamNatTable) {
      this.filterGridLayer = ((ParamNatTable) this.page).getCustomFilterGridLayer();
    }
  }

  /**
   * @return saveReviewData
   */
  public Action getSaveReviewData() {
    return this.saveReviewDetails;
  }


  // iCDM-650
  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void ruleCompleteFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For Rule is complete
    final Action ruleCompleteActn = new Action("Rule:Complete", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setRuleCompleteFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Review Rule exists
    ruleCompleteActn.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_16X16));
    ruleCompleteActn.setChecked(true);
    toolBarManager.add(ruleCompleteActn);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(ruleCompleteActn, ruleCompleteActn.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void ruleInCompleteFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For Rule incomplete
    final Action ruleInCompleteAction = new Action(
        "A rule is incomplete when\r\n" + "- One of the columns 'lower limit' or 'upper limit' is missing.\r\n" +
            "- There's only a reference value and 'Exact match to reference value' is not checked.",
        SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setRuleInCompleteFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Review Rule not exists
    ruleInCompleteAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    ruleInCompleteAction.setChecked(true);
    toolBarManager.add(ruleInCompleteAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(ruleInCompleteAction, ruleInCompleteAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void paramWithDepnAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {

    final Action paramDepnAction = new Action("Parameter with dependencies", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setParamWithDepn(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };

    paramDepnAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_DEPN_16X16));
    paramDepnAction.setChecked(true);
    toolBarManager.add(paramDepnAction);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(paramDepnAction, paramDepnAction.isChecked());
  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void ruleNotExistsFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {

    final Action ruleNotExistsAction = new Action("Rule not exists", SWT.TOGGLE) {

      @Override
      public void run() {

        toolBarFilters.setNoRuleExistsSearch(isChecked());


        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image
    ruleNotExistsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    ruleNotExistsAction.setChecked(true);
    toolBarManager.add(ruleNotExistsAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(ruleNotExistsAction, ruleNotExistsAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void paramWithNoDepnAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {

    final Action noParamDepnAction = new Action("Parameter without dependencies", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setParamWithOutDepn(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };

    noParamDepnAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_NO_DEPN_16X16));
    noParamDepnAction.setChecked(true);
    toolBarManager.add(noParamDepnAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(noParamDepnAction, noParamDepnAction.isChecked());
  }

  /**
   * This method creates PIDC attribute used filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void curveFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For curve Value
    final Action curveAction = new Action("Curve", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setCurveFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Curve Filter
    curveAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CURVE_16X16));

    curveAction.setChecked(true);
    toolBarManager.add(curveAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(curveAction, curveAction.isChecked());
  }

  /**
   * This method creates PIDC attribute mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */

  public void mapFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For Map Value
    final Action mapAction = new Action("Map", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setMapFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Map Filter
    mapAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.MAP_16X16));

    mapAction.setChecked(true);
    toolBarManager.add(mapAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(mapAction, mapAction.isChecked());
  }

  /**
   * This method creates PIDC attribute non-mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  // ICDM-179
  public void valueFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Value
    final Action valueAction = new Action("Value", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setValueFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Value Type
    valueAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALUE_16X16));

    valueAction.setChecked(true);
    toolBarManager.add(valueAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(valueAction, valueAction.isChecked());
  }

  /**
   * This method creates PIDC attribute used not known filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void asciiFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For ascii
    final Action asciiAction = new Action("Ascii", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setAsciiFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Ascii
    asciiAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ASCII_16X16));

    asciiAction.setChecked(true);
    toolBarManager.add(asciiAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(asciiAction, asciiAction.isChecked());
  }

  /**
   * This method creates PIDC attribute not used filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void valueBlockFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For Value Block
    final Action valueBlockAction = new Action("Value Block", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setValueBlkFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Value Block
    valueBlockAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VALBLK_16X16));
    // ICDM-278
    valueBlockAction.setChecked(true);
    toolBarManager.add(valueBlockAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(valueBlockAction, valueBlockAction.isChecked());
  }

  // ICDM-2439
  /**
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void complianceFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For compliance parameters
    final Action complianceAction = new Action("Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setComplianceFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for compliance Filter
    complianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));

    complianceAction.setChecked(true);
    toolBarManager.add(complianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(complianceAction, complianceAction.isChecked());
  }

  // ICDM-2439
  /**
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void nonComplianceFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For compliance parameters
    final Action nonComplianceAction = new Action("Non Compliance Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setNonComplianceFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for compliance Filter
    nonComplianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_NON_COMPLIANCE_16X16));

    nonComplianceAction.setChecked(true);
    toolBarManager.add(nonComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonComplianceAction, nonComplianceAction.isChecked());
  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   * @param paramNatTable instance
   */
  public void axisPointFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Axis Point
    final Action axisPointAction = new Action("Axis Points", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }

        toolBarFilters.setAxisFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Axis Point
    axisPointAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.AXIS_16X16));
    axisPointAction.setChecked(true);
    toolBarManager.add(axisPointAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(axisPointAction, axisPointAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void screwFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Screw class
    final Action screwAction = new Action("Class:Screw", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setScrewFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
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
   * @param paramNatTable viewer
   */
  public void nailFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Nail class
    final Action nailAction = new Action("Class:Nail", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setNailFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
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
   * @param paramNatTable viewer
   */
  public void rivetFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Rivet class
    final Action rivetAction = new Action("Class:Rivet", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setRivetFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
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
  *
  */
  private void applyColumnFilter() {
    if (null != this.filterGridLayer) {
      // Toolbar filter for all Columns
      this.filterGridLayer.getComboGlazedListsFilterStrategy().applyToolBarFilterInAllColumns(false);
      this.filterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(this.filterGridLayer.getSortableColumnHeaderLayer()));
      ((ParamNatTable) this.page).setStatusBarMessage(((ParamNatTable) this.page).getGroupByHeaderLayer(), false);

    }


  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void manualFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For Ready for series Manual
    String labelName = "";
    try {
      labelName = new CommonDataBO().getMessage(CDR_RULE, READY_FOR_SERIES);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    final Action manualAction = new Action(labelName + ":NO", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setManualFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for raedy for series Manual
    manualAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.MANUAL_16X16));
    // Icdm-1087 - disabled the Action
    manualAction.setEnabled(true);
    manualAction.setChecked(true);
    toolBarManager.add(manualAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(manualAction, manualAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void automaticFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {

    String labelName = "";
    try {
      labelName = new CommonDataBO().getMessage(CDR_RULE, READY_FOR_SERIES);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    // Filter For ready for series Automatic
    final Action automaticAction = new Action(labelName + ":YES", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }

        toolBarFilters.setAutoFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for ready for series Automatic
    automaticAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.AUTOMATIC_16X16));
    automaticAction.setChecked(true);
    // Icdm-1087 - disabled the Action
    automaticAction.setEnabled(true);
    toolBarManager.add(automaticAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(automaticAction, automaticAction.isChecked());
  }

  /**
   * // Filter For the Undefined - Review Type Icdm-654
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void reviewUnDefFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    String labelName = "";
    try {
      labelName = new CommonDataBO().getMessage(CDR_RULE, READY_FOR_SERIES);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    final Action undefAction = new Action(labelName + ":Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }

        toolBarFilters.setRvwUnDefinedFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for Ready for series Automatic
    undefAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    // Icdm-1087 - disabled the Action
    undefAction.setEnabled(true);
    undefAction.setChecked(true);
    toolBarManager.add(undefAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(undefAction, undefAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void undefinedFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Undefined class
    final Action undefinedAction = new Action("Class:Undefined", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setNoClassFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for rivet class
    undefinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNDEFINED_16X16));
    undefinedAction.setChecked(true);
    toolBarManager.add(undefinedAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(undefinedAction, undefinedAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param paramNatTable viewer
   */
  public void codeYesFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter for Code Yes
    final Action codeYesAction = new Action("Codeword:Yes", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setYesFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
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
   * @param paramNatTable viewer
   */
  public void codeNoFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For Code No
    final Action codeNoAction = new Action("Codeword:No", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setNoFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
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
   * ICdm-1056 Edit rule dialog
   *
   * @return is saved or not
   */
  public boolean saveReviewDataChanges() {
    return true;
  }


  /**
   * @param toolBarManager toolBarManager
   * @param detailsPage detailsPage
   */
  public void saveReviewData(final ToolBarManager toolBarManager, final AbstractFormPage detailsPage) {
    this.saveReviewDetails = new Action() {

      @Override
      public void run() {
        if (detailsPage instanceof DetailsPage) {
          final DetailsPage pageInstance = (DetailsPage) detailsPage;
          if (saveReviewDataChanges()) {
            pageInstance.getFcTableViewer().refresh();
          }
        }
      }
    };
    this.saveReviewDetails.setText("Save");
    // Set the image for save
    this.saveReviewDetails.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SAVE_28X30));

    this.saveReviewDetails.setEnabled(false);

    toolBarManager.add(this.saveReviewDetails);

  }


  /**
   * @param viewer
   */
  private void setStatus(final Viewer viewer) {
    if (viewer instanceof GridTableViewer) {
      final CustomGridTableViewer customTableViewer = (CustomGridTableViewer) viewer;
      customTableViewer.setStatusLineManager(ReviewRuleToolBarActionSet.this.statusLineManager);
    }
    if (viewer instanceof CustomTreeViewer) {
      final CustomTreeViewer customTreeViewer = (CustomTreeViewer) viewer;
      customTreeViewer.setStatusLineManager(ReviewRuleToolBarActionSet.this.statusLineManager);
    }
    viewer.refresh();
  }

  /**
   * @param paramNatTable
   * @param viewer
   */
  private void refreshViewer(final CustomNATTable paramNatTable, final Viewer viewer) {
    if (viewer == null) {
      applyColumnFilter();
      paramNatTable.refresh();
    }
    else {
      setStatus(viewer);
    }
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   * @param object
   */
  public void blackListFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For compliance parameters
    final Action blackListAction = new Action("Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setBlackListFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for compliance Filter
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
   * @param object
   */
  public void nonBlackListFilterAction(final ToolBarManager toolBarManager,
      final ReviewParamToolBarFilters toolBarFilters, final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For compliance parameters
    final Action nonBlackListAction = new Action("Non Black List Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setNonBlackListFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for compliance Filter
    nonBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));

    nonBlackListAction.setChecked(true);
    toolBarManager.add(nonBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonBlackListAction, nonBlackListAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewParamToolBarFilters
   * @param paramNatTable CustomNATTable
   * @param viewer Viewer
   */
  public void qSSDFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For qSSD parameters
    final Action qSSDFilterAction = new Action("QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setqSSDFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for qSSD Filter
    qSSDFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));

    qSSDFilterAction.setChecked(true);
    toolBarManager.add(qSSDFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(qSSDFilterAction, qSSDFilterAction.isChecked());

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters ReviewParamToolBarFilters
   * @param paramNatTable CustomNATTable
   * @param viewer Viewer
   */
  public void nonQSSDFilterAction(final ToolBarManager toolBarManager, final ReviewParamToolBarFilters toolBarFilters,
      final CustomNATTable paramNatTable, final Viewer viewer) {
    // Filter For non qSSD parameters
    final Action nonQSSDFilterAction = new Action("Non QSSD Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        if (ReviewRuleToolBarActionSet.this.page instanceof DetailsPage) {
          ((DetailsPage) ReviewRuleToolBarActionSet.this.page).promptToSave();
        }
        toolBarFilters.setNonQSSDFlag(isChecked());
        refreshViewer(paramNatTable, viewer);
      }
    };
    // Set the image for non qSSD Filter
    nonQSSDFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));

    nonQSSDFilterAction.setChecked(true);
    toolBarManager.add(nonQSSDFilterAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(nonQSSDFilterAction, nonQSSDFilterAction.isChecked());
  }
}
