/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ui.editors.pages.WPLabelAssignNatPage;
import com.bosch.caltool.icdm.ui.table.filters.WPLabelAssignToolBarFilter;
import com.bosch.caltool.nattable.CustomNATTable;

/**
 * The Class WPLabelAssignToolBarFilterActionSet.
 *
 * @author apj4cob
 */
public class WPLabelAssignToolBarFilterActionSet {

  /** Instance of WPLabelAssignPage page. */
  private final WPLabelAssignNatPage page;

  /** The is responsible not assigned. */
  private Action isResponsibleNotAssigned;

  /** The is responsible assigned. */
  private Action isResponsibleAssigned;

  /** The is responsible inherited. */
  private Action isResponsibleInherited;


  /** The is WP not assigned. */
  private Action isWPNotAssigned;

  /** The is WP assigned. */
  private Action isWPAssigned;

  /** The with LAB param action. */
  private Action withLABParamAction;

  /** The without LAB param action. */
  private Action withoutLABParamAction;

  /** The is name at customer assigned. */
  private Action isNameAtCustomerAssigned;

  /** The is name at customer not assigned. */
  private Action isNameAtCustomerNotAssigned;

  /** The is name at customer inherited. */
  private Action isNameAtCustomerInherited;
  /** The variant group filter action. */
  private Action isVariantGroupAssignedAction;
  /** The hide variant group filter action. */
  private Action hideVariantGroupAssignedAction;
  /** The compliance parameter filter action. */
  private Action isComplianceAction;
  /** The non compliance parameter filter action. */
  private Action isNonComplianceAction;


  /**
   * Instantiates a new WP label assign tool bar filter action set.
   *
   * @param page WPLabelAssignPage
   */
  public WPLabelAssignToolBarFilterActionSet(final WPLabelAssignNatPage page) {
    super();
    this.page = page;
  }

  /**
   * Method to apply column filter for all columns.
   */
  public void applyFilter() {
    WPLabelAssignToolBarFilterActionSet.this.page.getCustomFilterGridLayer().getFilterStrategy()
        .applyToolBarFilterInAllColumns(false);
    WPLabelAssignToolBarFilterActionSet.this.page.getCustomFilterGridLayer().getSortableColumnHeaderLayer()
        .fireLayerEvent(new FilterAppliedEvent(
            WPLabelAssignToolBarFilterActionSet.this.page.getCustomFilterGridLayer().getSortableColumnHeaderLayer()));
    WPLabelAssignToolBarFilterActionSet.this.page
        .setStatusBarMessage(WPLabelAssignToolBarFilterActionSet.this.page.getGroupByHeaderLayer(), false);
    WPLabelAssignToolBarFilterActionSet.this.page.getNatTable().redraw();
  }

  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolbarFilter toolBarFilters
   */
  public void complianceFilterAction(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {

    this.isComplianceAction = new Action(CommonUIConstants.FILTER_COMPLIANCE_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolbarFilter.setComplianceFlag(true);
        }
        else {
          toolbarFilter.setComplianceFlag(false);
        }
        applyFilter();
      }
    };
    // Set the image for compliance filter
    this.isComplianceAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    this.isComplianceAction.setChecked(true);
    toolBarManager.add(this.isComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isComplianceAction, this.isComplianceAction.isChecked());
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param pidcVersion
   */
  public void par2WPAssignmentAction(final ToolBarManager toolBarManager, final A2LWPInfoBO a2lWPInfoBO) {
    WpRespActionSet importAction = new WpRespActionSet();

    Action par2WPAction = importAction.par2WPAssignmentAction(a2lWPInfoBO, a2lWPInfoBO.getPidcA2lBo().getPidcVersion());
    toolBarManager.add(par2WPAction);
  }


  // ICDM-2439
  /**
   * @param toolBarManager toolBarManager
   * @param toolbarFilter toolBarFilter
   */
  public void nonComplianceFilterAction(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {

    this.isNonComplianceAction = new Action(CommonUIConstants.FILTER_NON_COMPLIANCE_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolbarFilter.setNonComplianceFlag(true);
        }
        else {
          toolbarFilter.setNonComplianceFlag(false);
        }

        applyFilter();
      }
    };
    // Set the image for compliance filter
    this.isNonComplianceAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_TYPE_NON_COMPLIANCE_16X16));
    this.isNonComplianceAction.setChecked(true);
    toolBarManager.add(this.isNonComplianceAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isNonComplianceAction, this.isNonComplianceAction.isChecked());
  }


  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void readOnlyAction(final ToolBarManager toolBarManager, final WPLabelAssignToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action readOnlyAction = new Action("READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setReadOnlyParam(isChecked());
        applyFilter();
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
   * @param toolBarManager
   * @param toolBarFilters
   * @param natTable
   */
  public void notReadOnlyAction(final ToolBarManager toolBarManager, final WPLabelAssignToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {
    Action notReadOnlyAction = new Action("Not READ Only Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotReadOnlyParam(isChecked());
        applyFilter();
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
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void dependentParamAction(final ToolBarManager toolBarManager, final WPLabelAssignToolBarFilter toolBarFilters,
      final CustomNATTable natTable) {

    Action dependentAction = new Action("Dependent Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDependentParam(isChecked());
        applyFilter();
      }
    };
    // Set the image for compliance filter
    dependentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_DEPN_16X16));
    dependentAction.setChecked(true);
    toolBarManager.add(dependentAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(dependentAction, dependentAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param natTable NAT table
   */
  public void notdependentParamAction(final ToolBarManager toolBarManager,
      final WPLabelAssignToolBarFilter toolBarFilters, final CustomNATTable natTable) {

    Action notDependentAction = new Action("Not Dependent Parameters", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotDependentParam(isChecked());
        applyFilter();
      }
    };
    // Set the image for not dependent params filter
    notDependentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PARAM_NO_DEPN_16X16));
    notDependentAction.setChecked(true);
    toolBarManager.add(notDependentAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(notDependentAction, notDependentAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolbarFilter toolBarFilters
   */
  public void qSSDFilterAction(final WPLabelAssignToolBarFilter toolbarFilter, final ToolBarManager toolBarManager) {

    Action isQSSDAction = new Action(CommonUIConstants.FILTER_QSSD_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolbarFilter.setqSSDFlag(true);
        }
        else {
          toolbarFilter.setqSSDFlag(false);
        }
        applyFilter();
      }
    };
    // Set the image for nonQSSD filter
    isQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QSSD_LABEL));
    isQSSDAction.setChecked(true);
    toolBarManager.add(isQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isQSSDAction, isQSSDAction.isChecked());
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolbarFilter toolBarFilters
   */
  public void nonQSSDFilterAction(final WPLabelAssignToolBarFilter toolbarFilter, final ToolBarManager toolBarManager) {

    Action isNonQSSDAction = new Action(CommonUIConstants.FILTER_NON_QSSD_PARAMETERS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter.setNonQSSDFlag(isChecked());
        applyFilter();
      }
    };
    // Set the image for non QSSD filter
    isNonQSSDAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_QSSD_LABEL));
    isNonQSSDAction.setChecked(true);
    toolBarManager.add(isNonQSSDAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isNonQSSDAction, isNonQSSDAction.isChecked());
  }

  /**
   * Responsible assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void responsibleAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isResponsibleAssigned = new Action(CommonUIConstants.FILTER_IS_RESPONSIBLE_ASSIGNED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter
            .setResponsibleAssigned(WPLabelAssignToolBarFilterActionSet.this.isResponsibleAssigned.isChecked());
        applyFilter();
      }
    };
    this.isResponsibleAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WITH_STATUS_16X16));
    toolBarManager.add(this.isResponsibleAssigned);
    this.isResponsibleAssigned.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isResponsibleAssigned, this.isResponsibleAssigned.isChecked());

  }

  /**
   * Responsible inherited filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void responsibleInheritedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isResponsibleInherited = new Action(CommonUIConstants.FILTER_IS_RESPONSIBLE_ASSIGNED_INHERITED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter
            .setResponsibleInherited(WPLabelAssignToolBarFilterActionSet.this.isResponsibleInherited.isChecked());
        applyFilter();
      }
    };
    this.isResponsibleInherited.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WP_LABEL_RESP_INHERITED));
    toolBarManager.add(this.isResponsibleInherited);
    this.isResponsibleInherited.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isResponsibleInherited, this.isResponsibleInherited.isChecked());
  }

  /**
   * Name at customer assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void nameAtCustomerAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isNameAtCustomerAssigned = new Action(CommonUIConstants.FILTER_IS_NAME_AT_CUSTOMER_ASSIGNED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter
            .setWpNameAtCustAssigned(WPLabelAssignToolBarFilterActionSet.this.isNameAtCustomerAssigned.isChecked());
        applyFilter();
      }
    };
    this.isNameAtCustomerAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUSTOMER_NAME_SET));
    toolBarManager.add(this.isNameAtCustomerAssigned);
    this.isNameAtCustomerAssigned.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isNameAtCustomerAssigned, this.isNameAtCustomerAssigned.isChecked());

  }

  /**
   * Name at customer not assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void nameAtCustomerNotAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isNameAtCustomerNotAssigned =
        new Action(CommonUIConstants.FILTER_IS_NAME_AT_CUSTOMER_ASSIGNED_NO, SWT.TOGGLE) {

          @Override
          public void run() {
            toolbarFilter.setWpNameAtCustNotAssigned(
                WPLabelAssignToolBarFilterActionSet.this.isNameAtCustomerNotAssigned.isChecked());
            applyFilter();
          }
        };
    this.isNameAtCustomerNotAssigned
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUSTOMER_NAME_NOT_SET));

    toolBarManager.add(this.isNameAtCustomerNotAssigned);
    this.isNameAtCustomerNotAssigned.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isNameAtCustomerNotAssigned, this.isNameAtCustomerNotAssigned.isChecked());
  }

  /**
   * Name at customer inherited filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */

  public void nameAtCustomerInheritedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isNameAtCustomerInherited = new Action(CommonUIConstants.FILTER_IS_NAME_AT_CUSTOMER_INHERITED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter
            .setWpNameAtCustInherited(WPLabelAssignToolBarFilterActionSet.this.isNameAtCustomerInherited.isChecked());
        applyFilter();
      }
    };
    this.isNameAtCustomerInherited
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CUSTOMER_NAME_INHERITED));
    toolBarManager.add(this.isNameAtCustomerInherited);
    this.isNameAtCustomerInherited.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isNameAtCustomerInherited, this.isNameAtCustomerInherited.isChecked());
  }

  /**
   * Checks if is WP not assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void isWPNotAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isWPNotAssigned = new Action(CommonUIConstants.FILTER_IS_WP_ASSIGNED_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter.setWPNotAssigned(WPLabelAssignToolBarFilterActionSet.this.isWPNotAssigned.isChecked());
        applyFilter();
      }


    };

    this.isWPNotAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_NOT_ASSGND_16X16));
    toolBarManager.add(this.isWPNotAssigned);
    this.isWPNotAssigned.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isWPNotAssigned, this.isWPNotAssigned.isChecked());

  }

  /**
   * Checks if is WP assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void isWPAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter, final ToolBarManager toolBarManager) {
    this.isWPAssigned = new Action(CommonUIConstants.FILTER_IS_WP_ASSIGNED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter.setWPAssigned(WPLabelAssignToolBarFilterActionSet.this.isWPAssigned.isChecked());
        applyFilter();
      }
    };

    this.isWPAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_ASSGND_16X16));
    toolBarManager.add(this.isWPAssigned);
    this.isWPAssigned.setChecked(true);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isWPAssigned, this.isWPAssigned.isChecked());
  }

  /**
   * Responsible not assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void responsibleNotAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isResponsibleNotAssigned = new Action(CommonUIConstants.FILTER_IS_RESPONSIBLE_ASSIGNED_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolbarFilter
            .setResponsibleNotAssigned(WPLabelAssignToolBarFilterActionSet.this.isResponsibleNotAssigned.isChecked());
        applyFilter();
      }
    };
    this.isResponsibleNotAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_STATUS_16X16));
    this.isResponsibleNotAssigned.setChecked(true);
    toolBarManager.add(this.isResponsibleNotAssigned);
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isResponsibleNotAssigned, this.isResponsibleNotAssigned.isChecked());
  }

  /**
   * Gets the checks if is responsible not assigned.
   *
   * @return the isResponsibleNotAssigned
   */
  public Action getIsResponsibleNotAssigned() {
    return this.isResponsibleNotAssigned;
  }


  /**
   * Gets the checks if is responsible assigned.
   *
   * @return the isResponsibleAssigned
   */
  public Action getIsResponsibleAssigned() {
    return this.isResponsibleAssigned;
  }


  /**
   * Gets the checks if is WP not assigned.
   *
   * @return the isWPNotAssigned
   */
  public Action getIsWPNotAssigned() {
    return this.isWPNotAssigned;
  }


  /**
   * Gets the checks if is WP assigned.
   *
   * @return the isWPAssigned
   */
  public Action getIsWPAssigned() {
    return this.isWPAssigned;
  }

  /**
   * Gets the checks if is responsible inherited.
   *
   * @return the isResponsibleInherited
   */
  public Action getIsResponsibleInherited() {
    return this.isResponsibleInherited;
  }

  /**
   * Creates the with LAB param action.
   *
   * @param toolbarFilter the toolbar filter
   * @param toolBarManager the tool bar manager
   */
  public void createWithLABParamAction(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {

    this.withLABParamAction = new Action(CommonUIConstants.FILTER_LAB_PARAM, SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolbarFilter.setWithLabParam(true);
        }
        else {
          toolbarFilter.setWithLabParam(false);
        }
        applyFilter();
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
   * ICDM-841.
   *
   * @param toolbarFilter toolbarfilter instance
   * @param toolBarManager toolbarmng instance
   */
  public void createWithoutLABParamAction(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.withoutLABParamAction = new Action(CommonUIConstants.FILTER_NOT_LAB_PARAM, SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolbarFilter.setWithoutLabParam(true);
        }
        else {
          toolbarFilter.setWithoutLabParam(false);
        }
        applyFilter();
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
   * Variant Group assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void variantGroupAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.isVariantGroupAssignedAction = new Action(CommonUIConstants.FILTER_SHOW_VARIANT_GROUP_ONLY, SWT.TOGGLE) {

      @Override
      public void run() {
        if (WPLabelAssignToolBarFilterActionSet.this.isVariantGroupAssignedAction.isChecked()) {
          toolbarFilter.setVariantGrp(true);
        }
        else {
          toolbarFilter.setVariantGrp(false);
        }
        applyFilter();
      }
    };
    this.isVariantGroupAssignedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16));
    toolBarManager.add(this.isVariantGroupAssignedAction);
    this.isVariantGroupAssignedAction.setChecked(true);
    if (CommonUtils.isNotNull(this.page.getA2lWPInfoBO().getSelectedA2lVarGroup())) {
      this.isVariantGroupAssignedAction.setEnabled(true);
    }
    else {
      this.isVariantGroupAssignedAction.setEnabled(false);
    }
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.isVariantGroupAssignedAction, this.isVariantGroupAssignedAction.isChecked());

  }

  /**
   * Hide Variant Group assigned filter.
   *
   * @param toolbarFilter WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void hideVariantGroupAssignedFilter(final WPLabelAssignToolBarFilter toolbarFilter,
      final ToolBarManager toolBarManager) {
    this.hideVariantGroupAssignedAction = new Action(CommonUIConstants.FILTER_HIDE_VARIANT_GROUP_ONLY, SWT.TOGGLE) {

      @Override
      public void run() {
        if (WPLabelAssignToolBarFilterActionSet.this.hideVariantGroupAssignedAction.isChecked()) {
          toolbarFilter.setNotVariantGrp(true);
        }
        else {
          toolbarFilter.setNotVariantGrp(false);
        }
        applyFilter();
      }
    };
    this.hideVariantGroupAssignedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.HIDE_16X16));
    toolBarManager.add(this.hideVariantGroupAssignedAction);
    this.hideVariantGroupAssignedAction.setChecked(true);
    if (CommonUtils.isNotNull(this.page.getA2lWPInfoBO().getSelectedA2lVarGroup())) {
      this.hideVariantGroupAssignedAction.setEnabled(true);
    }
    else {
      this.hideVariantGroupAssignedAction.setEnabled(false);
    }
    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(this.hideVariantGroupAssignedAction,
        this.hideVariantGroupAssignedAction.isChecked());

  }

  /**
   * Gets the without LAB param action.
   *
   * @return the without LAB param action
   */
  public Action getWithoutLABParamAction() {
    return this.withoutLABParamAction;
  }

  /**
   * Gets the with LAB param action.
   *
   * @return the with LAB param action
   */
  public Action getWithLABParamAction() {
    return this.withLABParamAction;
  }


  /**
   * Gets the checks if is name at customer assigned.
   *
   * @return the isNameAtCustomerAssigned
   */
  public Action getIsNameAtCustomerAssigned() {
    return this.isNameAtCustomerAssigned;
  }


  /**
   * Sets the checks if is name at customer assigned.
   *
   * @param isNameAtCustomerAssigned the isNameAtCustomerAssigned to set
   */
  public void setIsNameAtCustomerAssigned(final Action isNameAtCustomerAssigned) {
    this.isNameAtCustomerAssigned = isNameAtCustomerAssigned;
  }


  /**
   * Gets the checks if is name at customer not assigned.
   *
   * @return the isNameAtCustomerNotAssigned
   */
  public Action getIsNameAtCustomerNotAssigned() {
    return this.isNameAtCustomerNotAssigned;
  }


  /**
   * Sets the checks if is name at customer not assigned.
   *
   * @param isNameAtCustomerNotAssigned the isNameAtCustomerNotAssigned to set
   */
  public void setIsNameAtCustomerNotAssigned(final Action isNameAtCustomerNotAssigned) {
    this.isNameAtCustomerNotAssigned = isNameAtCustomerNotAssigned;
  }


  /**
   * Gets the checks if is name at customer inherited.
   *
   * @return the isNameAtCustomerInherited
   */
  public Action getIsNameAtCustomerInherited() {
    return this.isNameAtCustomerInherited;
  }


  /**
   * Sets the checks if is name at customer inherited.
   *
   * @param isNameAtCustomerInherited the isNameAtCustomerInherited to set
   */
  public void setIsNameAtCustomerInherited(final Action isNameAtCustomerInherited) {
    this.isNameAtCustomerInherited = isNameAtCustomerInherited;
  }

  /**
   * @return the isVariantGroupAssignedAction
   */
  public Action getIsVariantGroupAssignedAction() {
    return this.isVariantGroupAssignedAction;
  }


  /**
   * @param isVariantGroupAssignedAction the isVariantGroupAssignedAction to set
   */
  public void setIsVariantGroupAssignedAction(final Action isVariantGroupAssignedAction) {
    this.isVariantGroupAssignedAction = isVariantGroupAssignedAction;
  }

  /**
   * @return the isComplianceAction
   */
  public Action getIsComplianceAction() {
    return this.isComplianceAction;
  }


  /**
   * @param isComplianceAction the isComplianceAction to set
   */
  public void setIsComplianceAction(final Action isComplianceAction) {
    this.isComplianceAction = isComplianceAction;
  }


  /**
   * @return the isNonComplianceAction
   */
  public Action getIsNonComplianceAction() {
    return this.isNonComplianceAction;
  }


  /**
   * @param isNonComplianceAction the isNonComplianceAction to set
   */
  public void setIsNonComplianceAction(final Action isNonComplianceAction) {
    this.isNonComplianceAction = isNonComplianceAction;
  }

  /**
   * @param toolBarFilters WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void blackListFilterAction(final WPLabelAssignToolBarFilter toolBarFilters,
      final ToolBarManager toolBarManager) {

    Action isBlackListAction = new Action("Black List Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolBarFilters.setBlackListFlag(true);
        }
        else {
          toolBarFilters.setBlackListFlag(false);
        }
        applyFilter();
      }
    };
    // Set the image for compliance filter
    isBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.BLACK_LIST_LABEL));
    isBlackListAction.setChecked(true);
    toolBarManager.add(isBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isBlackListAction, isBlackListAction.isChecked());
  }

  /**
   * @param toolBarFilters WPLabelAssignToolBarFilter
   * @param toolBarManager ToolBarManager
   */
  public void nonBlackListFilterAction(final WPLabelAssignToolBarFilter toolBarFilters,
      final ToolBarManager toolBarManager) {

    Action isNonBlackListAction = new Action("Non Black List Parameter", SWT.TOGGLE) {

      @Override
      public void run() {
        if (isChecked()) {
          toolBarFilters.setNonBlackListFlag(true);
        }
        else {
          toolBarFilters.setNonBlackListFlag(false);
        }

        applyFilter();
      }
    };
    // Set the image for compliance filter
    isNonBlackListAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_BLACK_LIST_LABEL));
    isNonBlackListAction.setChecked(true);
    toolBarManager.add(isNonBlackListAction);

    // Adding the default state to filters map
    this.page.addToToolBarFilterMap(isNonBlackListAction, isNonBlackListAction.isChecked());
  }


  /**
   * @return Action
   */
  public Action getHideVariantGroupAssignedAction() {
    return this.hideVariantGroupAssignedAction;
  }


  /**
   * @param hideVariantGroupAssignedAction Action
   */
  public void setHideVariantGroupAssignedAction(final Action hideVariantGroupAssignedAction) {
    this.hideVariantGroupAssignedAction = hideVariantGroupAssignedAction;
  }
}
