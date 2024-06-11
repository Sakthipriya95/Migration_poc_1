/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.EditFC2WPMappingDialog;
import com.bosch.caltool.icdm.ui.dialogs.FC2WPPTtypeSettingsDialog;
import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditorInput;
import com.bosch.caltool.icdm.ui.editors.pages.FC2WPNatFormPage;
import com.bosch.caltool.icdm.ui.jobs.FC2WPExcelExportJob;
import com.bosch.caltool.icdm.ui.table.filters.FC2WPNatToolBarFilters;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGlazedListsDataProvider;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * The Class FC2WPNatToolBarActionSet.
 *
 * @author gge6cob
 */
public class FC2WPNatToolBarActionSet {

  /** Instance of parameters form page. */
  private final FC2WPNatFormPage page;

  /** action to add new functions */
  private Action addFC2WPAction;

  private Action editFC2WPAction;

  /** codeyes action. */
  private Action codeIsInICDMA2L;

  /** codeno action. */
  private Action codeIsNotInICDMA2L;

  /** The code IsFcInSdom - Yes **/
  private Action codeIsFcInSdom;
  /** The code IsFcInSdom - No **/
  private Action codeIsFcNotInSdom;

  /** The filter grid layer. */
  private final CustomFilterGridLayer<T> filterGridLayer;

  /** The code is deleted. */
  private Action codeIsDeleted;

  /** The code is not deleted. */
  private Action codeIsNotDeleted;

  /** The code contact assigned. */
  private Action codeContactAssigned;

  /** The code contact not assigned. */
  private Action codeContactNotAssigned;

  /** The code WP assigned. */
  private Action codeWPAssigned;

  /** The code WP not assigned. */
  private Action codeWPNotAssigned;

  /** The code revelant Pttype. */
  private Action codeRevelantPTtype;

  /** The code not revelant Pttype. */
  private Action codeNotRevelantPTtype;

  private Action deleteFC2WPAction;
  private final FC2WPEditorInput fc2wpEditorInput;

  /** The code IsFcWithParams - Yes **/
  private Action codeIsFcWithParams;

  /** The code IsFcWithParams - No **/
  private Action codeIsFcWithoutParams;

  /**
   * Instantiates a new FC2WP nat tool bar action set.
   *
   * @param fc2wpNatFormPage2 page
   * @param filterGridLayer the filter grid layer
   * @param editorInput FC2WPEditorInput
   */
  public FC2WPNatToolBarActionSet(final FC2WPNatFormPage fc2wpNatFormPage2,
      final CustomFilterGridLayer<T> filterGridLayer, final FC2WPEditorInput editorInput) {
    this.page = fc2wpNatFormPage2;
    this.filterGridLayer = filterGridLayer;
    this.fc2wpEditorInput = editorInput;
  }

  /**
   * Method to apply column filter for all columns
   */
  public void applyColumnFilter() {
    // Toolbar filter for all Columns : IMP to trigger filter events in NAT table
    FC2WPNatToolBarActionSet.this.filterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    FC2WPNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(FC2WPNatToolBarActionSet.this.filterGridLayer.getSortableColumnHeaderLayer()));
  }


  /**
   * Checks if is in ICDM filter action.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isFcNotInSdomFilterAction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {

    // Filter action For Code Yes
    this.codeIsFcNotInSdom = new Action(CommonUIConstants.FILTER_IS_FC_IN_SDOM_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setFcNotInSdomFlag(isChecked());
        applyColumnFilter();

        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsFcNotInSdom.getText(),
            FC2WPNatToolBarActionSet.this.codeIsFcNotInSdom.isChecked());
      }
    };
    // set icon for button
    this.codeIsFcNotInSdom.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC_NOT_IN_SDOM_16X16));


    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsFcNotInSdom.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsFcNotInSdom.setChecked(isChecked);
        toolBarFilters.setFcNotInSdomFlag(isChecked);
      }
      else {
        this.codeIsFcNotInSdom.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsFcNotInSdom);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsFcNotInSdom, this.codeIsFcNotInSdom.isChecked());
  }


  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void isFcWithParamsFilterAction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {

    // Filter action For Code Yes
    this.codeIsFcWithParams = new Action(CommonUIConstants.FILTER_IS_FC_WITH_PARAMS, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setIsFcWithParams(isChecked());
        applyColumnFilter();

        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsFcWithParams.getText(),
            FC2WPNatToolBarActionSet.this.codeIsFcWithParams.isChecked());
      }
    };
    // set icon for button
    this.codeIsFcWithParams.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC_WITH_PARAMS));


    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsFcWithParams.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsFcWithParams.setChecked(isChecked);
        toolBarFilters.setIsFcWithParams(isChecked);
      }
      else {
        this.codeIsFcWithParams.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsFcWithParams);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsFcWithParams, this.codeIsFcWithParams.isChecked());
  }

  /**
   * @param toolBarManager
   * @param toolBarFilters
   */
  public void isFcWithoutParamsFilterAction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {

    // Filter action For Code No
    this.codeIsFcWithoutParams = new Action(CommonUIConstants.FILTER_IS_FC_WITH_PARAMS_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setIsFcWithoutParams(isChecked());
        applyColumnFilter();

        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsFcWithoutParams.getText(),
            FC2WPNatToolBarActionSet.this.codeIsFcWithoutParams.isChecked());
      }
    };
    // set icon for button
    this.codeIsFcWithoutParams.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC_WITH_PARAMS_NO));


    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsFcWithoutParams.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsFcWithoutParams.setChecked(isChecked);
        toolBarFilters.setIsFcWithoutParams(isChecked);
      }
      else {
        this.codeIsFcWithoutParams.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsFcWithoutParams);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsFcWithoutParams, this.codeIsFcWithoutParams.isChecked());
  }

  /**
   * Checks if is in ICDM filter action.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isFcSdomFilterAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {

    // Filter action For Code Yes
    this.codeIsFcInSdom = new Action(CommonUIConstants.FILTER_IS_FC_IN_SDOM_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setFcInSdomFlag(isChecked());
        applyColumnFilter();

        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsFcInSdom.getText(),
            FC2WPNatToolBarActionSet.this.codeIsFcInSdom.isChecked());
      }
    };
    // set icon for button
    this.codeIsFcInSdom.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC_IN_SDOM_16X16));


    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsFcInSdom.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsFcInSdom.setChecked(isChecked);
        toolBarFilters.setFcInSdomFlag(isChecked);
      }
      else {
        this.codeIsFcInSdom.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsFcInSdom);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsFcInSdom, this.codeIsFcInSdom.isChecked());
  }

  /**
   * Checks if IsInICDMA2L filter action.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isNotInICDMFilterAction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {
    // Filter For Code No
    this.codeIsNotInICDMA2L = new Action(CommonUIConstants.FILTER_IS_IN_ICDM_A2L_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotInICDMA2LFlag(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsNotInICDMA2L.getText(),
            FC2WPNatToolBarActionSet.this.codeIsNotInICDMA2L.isChecked());
      }
    };
    // set icon for button
    this.codeIsNotInICDMA2L.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_NOT_FOUND_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsNotInICDMA2L.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsNotInICDMA2L.setChecked(isChecked);
        toolBarFilters.setNotInICDMA2LFlag(isChecked);
      }
      else {
        this.codeIsNotInICDMA2L.setChecked(false);
      }
    }

    toolBarManager.add(this.codeIsNotInICDMA2L);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsNotInICDMA2L, this.codeIsNotInICDMA2L.isChecked());

  }

  /**
   * Checks if is in ICDM filter action.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isInICDMFilterAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {

    // Filter action For Code No
    this.codeIsInICDMA2L = new Action(CommonUIConstants.FILTER_IS_IN_ICDM_A2L_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setInICDMA2LFlag(isChecked());
        applyColumnFilter();

        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsInICDMA2L.getText(),
            FC2WPNatToolBarActionSet.this.codeIsInICDMA2L.isChecked());
      }
    };
    // set icon for button
    this.codeIsInICDMA2L.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_IS_FOUND_16X16));


    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsInICDMA2L.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsInICDMA2L.setChecked(isChecked);
        toolBarFilters.setInICDMA2LFlag(isChecked);
      }
      else {
        this.codeIsInICDMA2L.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsInICDMA2L);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsInICDMA2L, this.codeIsInICDMA2L.isChecked());
  }


  /**
   * Checks if is deleted.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isDeleted(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {

    this.codeIsDeleted = new Action(CommonUIConstants.FILTER_IS_DELETED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDeletedFlag(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsDeleted.getText(),
            FC2WPNatToolBarActionSet.this.codeIsDeleted.isChecked());
      }
    };

    // set icon for button
    this.codeIsDeleted.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_DELETED_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsDeleted.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsDeleted.setChecked(isChecked);
        toolBarFilters.setDeletedFlag(isChecked);
      }
      else {
        this.codeIsDeleted.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsDeleted);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsDeleted, this.codeIsDeleted.isChecked());
  }

  /**
   * Checks if is not deleted.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isNotDeleted(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {

    this.codeIsNotDeleted = new Action(CommonUIConstants.FILTER_IS_DELETED_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotDeletedFlag(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeIsNotDeleted.getText(),
            FC2WPNatToolBarActionSet.this.codeIsNotDeleted.isChecked());
      }
    };
    // set icon for button
    this.codeIsNotDeleted.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_NOT_DELETED_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeIsNotDeleted.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeIsNotDeleted.setChecked(isChecked);
        toolBarFilters.setNotDeletedFlag(isChecked);
      }
      else {
        this.codeIsNotDeleted.setChecked(true);
      }
    }

    toolBarManager.add(this.codeIsNotDeleted);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeIsNotDeleted, this.codeIsNotDeleted.isChecked());
  }


  /**
   * Checks if is contact assigned.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isContactAssigned(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    this.codeContactAssigned = new Action(CommonUIConstants.FILTER_IS_CONTACT_ASSIGNED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setContactAssigned(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeContactAssigned.getText(),
            FC2WPNatToolBarActionSet.this.codeContactAssigned.isChecked());
      }
    };
    // set icon for button
    this.codeContactAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WITH_STATUS_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeContactAssigned.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeContactAssigned.setChecked(isChecked);
        toolBarFilters.setContactAssigned(isChecked);
      }
      else {
        this.codeContactAssigned.setChecked(true);
      }
    }

    toolBarManager.add(this.codeContactAssigned);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeContactAssigned, this.codeContactAssigned.isChecked());
  }


  /**
   * Checks if is contact not assigned.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isContactNotAssigned(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    this.codeContactNotAssigned = new Action(CommonUIConstants.FILTER_IS_CONTACT_ASSIGNED_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setContactNotAssigned(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeContactNotAssigned.getText(),
            FC2WPNatToolBarActionSet.this.codeContactNotAssigned.isChecked());
      }
    };
    // set icon for button
    this.codeContactNotAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_STATUS_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeContactNotAssigned.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeContactNotAssigned.setChecked(isChecked);
        toolBarFilters.setContactNotAssigned(isChecked);
      }
      else {
        this.codeContactNotAssigned.setChecked(true);
      }
    }

    toolBarManager.add(this.codeContactNotAssigned);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeContactNotAssigned, this.codeContactNotAssigned.isChecked());
  }


  /**
   * Checks if is WP assigned.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isWPAssigned(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    this.codeWPAssigned = new Action(CommonUIConstants.FILTER_IS_WP_ASSIGNED_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWPAssigned(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeWPAssigned.getText(),
            FC2WPNatToolBarActionSet.this.codeWPAssigned.isChecked());
      }
    };
    // set icon for button
    this.codeWPAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_ASSGND_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeWPAssigned.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeWPAssigned.setChecked(isChecked);
        toolBarFilters.setWPAssigned(isChecked);
      }
      else {
        this.codeWPAssigned.setChecked(true);
      }
    }

    toolBarManager.add(this.codeWPAssigned);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeWPAssigned, this.codeWPAssigned.isChecked());
  }

  /**
   * Checks if is WP not assigned.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isWPNotAssigned(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    this.codeWPNotAssigned = new Action(CommonUIConstants.FILTER_IS_WP_ASSIGNED_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWPNotAssigned(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeWPNotAssigned.getText(),
            FC2WPNatToolBarActionSet.this.codeWPNotAssigned.isChecked());
      }
    };
    // set icon for button
    this.codeWPNotAssigned.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_NOT_ASSGND_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeWPNotAssigned.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeWPNotAssigned.setChecked(isChecked);
        toolBarFilters.setWPNotAssigned(isChecked);
      }
      else {
        this.codeWPNotAssigned.setChecked(true);
      }
    }

    toolBarManager.add(this.codeWPNotAssigned);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeWPNotAssigned, this.codeWPNotAssigned.isChecked());
  }

  /**
   * Checks if is revelant P ttype.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isRevelantPTtype(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    this.codeRevelantPTtype = new Action(CommonUIConstants.FILTER_HAS_RELEVANT_PT_TYPE_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setIsRevelantPTtype(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeRevelantPTtype.getText(),
            FC2WPNatToolBarActionSet.this.codeRevelantPTtype.isChecked());
      }
    };
    // set icon for button
    this.codeRevelantPTtype.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_PT_RELEVANT_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeRevelantPTtype.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeRevelantPTtype.setChecked(isChecked);
        toolBarFilters.setIsRevelantPTtype(isChecked);
      }
      else {
        this.codeRevelantPTtype.setChecked(true);
      }
    }

    toolBarManager.add(this.codeRevelantPTtype);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeRevelantPTtype, this.codeRevelantPTtype.isChecked());
  }

  /**
   * Checks if is not revelant Pttype.
   *
   * @param toolBarManager the tool bar manager
   * @param toolBarFilters the tool bar filters
   */
  public void isNotRevelantPTtype(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    this.codeNotRevelantPTtype = new Action(CommonUIConstants.FILTER_HAS_RELEVANT_PT_TYPE_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotRevelantPTtype(isChecked());
        applyColumnFilter();
        // set toolbarFilterCurrStateMap
        // map contains current state of each filter
        FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap().put(
            FC2WPNatToolBarActionSet.this.codeNotRevelantPTtype.getText(),
            FC2WPNatToolBarActionSet.this.codeNotRevelantPTtype.isChecked());
      }
    };
    // set icon for button
    this.codeNotRevelantPTtype
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_PT_NOT_RELEVANT_16X16));
    if (null != FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()) {
      Boolean isChecked = FC2WPNatToolBarActionSet.this.page.getToolBarFilterCurrStateMap()
          .get(FC2WPNatToolBarActionSet.this.codeNotRevelantPTtype.getText());
      if (null != isChecked) {
        FC2WPNatToolBarActionSet.this.codeNotRevelantPTtype.setChecked(isChecked);
        toolBarFilters.setNotRevelantPTtype(isChecked);
      }
      else {
        this.codeNotRevelantPTtype.setChecked(true);
      }
    }

    toolBarManager.add(this.codeNotRevelantPTtype);

    // Adding the default state of toolbar filter
    // to common filters map
    this.page.addToToolBarFilterMap(this.codeNotRevelantPTtype, this.codeNotRevelantPTtype.isChecked());
  }


  /**
   * Edits the FC2WP mapping.
   *
   * @param toolBarformManager the tool barform manager
   */
  public void editFC2WPMapping(final ToolBarManager toolBarformManager) {
    this.editFC2WPAction = new Action("Edit FC2WP Mapping") {

      @Override
      public void run() {
        EditFC2WPMappingDialog fc2WpMapingDialog = new EditFC2WPMappingDialog(Display.getCurrent().getActiveShell(),
            FC2WPNatToolBarActionSet.this.page.getSelMappingList(),
            FC2WPNatToolBarActionSet.this.page.getSelFc2wpDefBo(),
            FC2WPNatToolBarActionSet.this.page.getSelFc2wpMapDetail(),
            FC2WPNatToolBarActionSet.this.page.getSelFc2wpDef());
        fc2WpMapingDialog.open();
        FC2WPNatToolBarActionSet.this.page.getNatTable().redraw();

      }
    };
    // set icon for button
    this.editFC2WPAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarformManager.add(this.editFC2WPAction);
    if (null != this.page.getCurrentSelection()) {
      this.editFC2WPAction.setEnabled(this.fc2wpEditorInput.getFC2WPDefBO().isModifiable());
    }
    else {
      this.editFC2WPAction.setEnabled(false);
    }
  }


  /**
   * Delete FC2WP mapping.
   *
   * @param toolBarformManager the tool barform manager
   */
  public void deleteFC2WPMapping(final ToolBarManager toolBarformManager) {
    this.deleteFC2WPAction = new Action("Delete FC2WP Mapping") {

      @Override
      public void run() {
        boolean confirm;
        if (FC2WPNatToolBarActionSet.this.page.getCurrentSelection().isDeleted()) {
          // already deleted
          MessageDialogUtils.getInfoMessageDialog(
              "Delete FC2WP mapping : " + FC2WPNatToolBarActionSet.this.page.getCurrentSelection().getFunctionName(),
              "The mapping had been deleted already.");
        }
        else {
          // when setting to active, show the confirm dialog that the active version's flag will be set to false
          confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo(
              "Confirm deleting the FC2WP mapping : " +
                  FC2WPNatToolBarActionSet.this.page.getCurrentSelection().getFunctionName(),
              "The selected FC2WP mapping will be deleted. Do you still want to delete it?");

          if (confirm) {
            deleteFC2WPthruService(FC2WPNatToolBarActionSet.this.page.getCurrentSelection());

          }
        }
      }
    };
    // set icon for button
    this.deleteFC2WPAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETED_ITEMS_ICON_16X16));
    toolBarformManager.add(this.deleteFC2WPAction);
    if (null != this.page.getCurrentSelection()) {
      this.deleteFC2WPAction.setEnabled(this.fc2wpEditorInput.getFC2WPDefBO().isModifiable());
    }
    else {
      this.deleteFC2WPAction.setEnabled(false);
    }
  }


  /**
   * Update FC2WP thru service.
   *
   * @return the FC2WP vers mapping
   */
  private FC2WPMappingWithDetails deleteFC2WPthruService(final FC2WPMapping currentSelection) {
    FC2WPMappingWithDetails fc2wpVersMapping = null;

    try {
      FC2WPMapping fc2WpMappingClone = currentSelection.clone();
      fc2WpMappingClone.setDeleted(true);
      FC2WPMappingServiceClient servClient = new FC2WPMappingServiceClient();
      ArrayList<FC2WPMapping> mappingList = new ArrayList<>();
      mappingList.add(fc2WpMappingClone);
      fc2wpVersMapping = servClient.updateFC2WPMapping(mappingList);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    catch (CloneNotSupportedException exp) {
      CDMLogger.getInstance()
          .error("Error in cloning FC2WP for updating FC2WP Mapping with  ID :" + currentSelection.getId(), exp);
    }
    return fc2wpVersMapping;
  }

  /**
   * Sets the power train preference.
   *
   * @param toolBarformManager the toolbar form manager
   * @param fc2wpNatFormPage the fc2wp nat form page
   * @param toolBarFilters the tool bar filters
   */
  public void setPowerTrainPreference(final ToolBarManager toolBarformManager, final FC2WPNatFormPage fc2wpNatFormPage,
      final FC2WPNatToolBarFilters toolBarFilters) {
    final Action editPTPreference = new Action("PT Type Settings") {

      @Override
      public void run() {

        FC2WPPTtypeSettingsDialog fc2WpMapingDialog =
            new FC2WPPTtypeSettingsDialog(Display.getCurrent().getActiveShell(), toolBarFilters,
                FC2WPNatToolBarActionSet.this.codeRevelantPTtype, FC2WPNatToolBarActionSet.this.page.getEditorInput(),
                FC2WPNatToolBarActionSet.this.fc2wpEditorInput.getFC2WPDefBO().getRelPTTypeData());
        fc2WpMapingDialog.open();
      }
    };
    // set icon for button
    editPTPreference.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SETTINGS_16X16));
    toolBarformManager.add(editPTPreference);
  }

  /**
   * @return the editFC2WPAction
   */
  public Action getEditFC2WPAction() {
    return this.editFC2WPAction;
  }

  /**
   * @return the deleteFC2WPAction
   */
  public Action getDeleteFC2WPAction() {
    return this.deleteFC2WPAction;
  }

  /**
   * @return the codeIsNotInICDMA2L
   */
  public Action getCodeIsNotInICDMA2L() {
    return this.codeIsNotInICDMA2L;
  }

  /**
   * Export fc2wp report to excel.
   *
   * @param editorInput the editor input
   * @param fc2wpPage instance of FC2WPNatFormPage
   */
  public void exportFC2WPReportToExcel(final FC2WPEditorInput editorInput, final FC2WPNatFormPage fc2wpPage) {

    String date = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_10);

    String fileName = CommonUtils.concatenate(editorInput.getName(), "_", date);
    fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");

    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save FC-WP Excel Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXTNS);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(fileName);
    fileDialog.setOverwrite(true);
    final String fileSelected = fileDialog.open();

    if (fileSelected != null) {
      final String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];

      // Get the filtered data available in table to export to excel
      SortedSet<FC2WPMapping> filteredData = new TreeSet<>();
      CustomGlazedListsDataProvider<T> dataProvider = fc2wpPage.getCustomFilterGridLayer().getBodyDataProvider();
      for (Object obj : dataProvider.getList()) {
        if (obj instanceof CompareFC2WPRowObject) {
          CompareFC2WPRowObject rowObj = (CompareFC2WPRowObject) obj;
          filteredData.add(rowObj.getColumnDataMapper().getColumnIndexFC2WPMap().get(CommonUIConstants.COLUMN_INDEX_2));
        }
      }

      final Job job = new FC2WPExcelExportJob(editorInput.getNatTableHeaderMap(), editorInput.getFC2WPMappingResult(),
          filteredData, fileSelected, fileExtn);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      job.schedule();
    }
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isDifferentRowAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isDifferentAction = new Action("is different", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_16X16));
    isDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isNotDifferentRowAction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("is not different", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_OK_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isWpDiffAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("Workpackage with Difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWpDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_ASSGND_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isWpNotDiffAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("Workpackage without Difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setWpNotDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_WP_NOT_ASSGND_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isContactDiffAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("Contact with Difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setContactDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.WITH_STATUS_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isContactNotDiffAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("Contact without Difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setContactNotDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NO_STATUS_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isAgreedDiffAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("Is agreed with Difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAgreedDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_YES_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isNotAgreedAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action("Is agreed without Difference", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAgreedNotDifferent(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CHECKBOX_NO_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isInIcdmA2lAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action(CommonUIConstants.FILTER_IS_IN_ICDM_A2L_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setInIcdmA2l(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_IS_FOUND_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void hasReleventPTTypeAction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action(CommonUIConstants.FILTER_HAS_RELEVANT_PT_TYPE_YES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setHasRelevantPTType(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_PT_RELEVANT_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void isNotInIcdmA2lAction(final ToolBarManager toolBarManager, final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action(CommonUIConstants.FILTER_IS_IN_ICDM_A2L_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotInIcdmA2l(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_NOT_FOUND_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FC2WPNatToolBarFilters
   */
  public void hasNotRevelantPTtypeaction(final ToolBarManager toolBarManager,
      final FC2WPNatToolBarFilters toolBarFilters) {
    // initialize action
    Action isNotDifferentAction = new Action(CommonUIConstants.FILTER_HAS_RELEVANT_PT_TYPE_NO, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setHasNoRelevantPTType(isChecked());
        applyColumnFilter();
      }
    };
    // set icon for button
    isNotDifferentAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICON_PT_NOT_RELEVANT_16X16));
    isNotDifferentAction.setChecked(true);
    // add action to toolbar manager
    toolBarManager.add(isNotDifferentAction);

  }


  /**
   * @param toolBarformManager ToolBarManager
   */
  public void addFC2WPMapping(final ToolBarManager toolBarformManager) {
    // initialize action
    this.addFC2WPAction = new Action("Add FC2WP Mapping") {

      @Override
      public void run() {
        EditFC2WPMappingDialog fc2WpMapingDialog = new EditFC2WPMappingDialog(Display.getCurrent().getActiveShell(),
            FC2WPNatToolBarActionSet.this.page.getSelFc2wpDefBo(),
            FC2WPNatToolBarActionSet.this.page.getSelFc2wpMapDetail(),
            FC2WPNatToolBarActionSet.this.page.getSelFc2wpDef(), true);
        fc2WpMapingDialog.open();
      }
    };
    // set icon for button
    this.addFC2WPAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    // add action to toolbar manager
    toolBarformManager.add(this.addFC2WPAction);

    this.addFC2WPAction.setEnabled(this.fc2wpEditorInput.getFC2WPDefBO().isModifiable());
  }

  /**
   * @return the addFC2WPAction
   */
  public Action getAddFC2WPAction() {
    return this.addFC2WPAction;
  }


  /**
   * @param addFC2WPAction the addFC2WPAction to set
   */
  public void setAddFC2WPAction(final Action addFC2WPAction) {
    this.addFC2WPAction = addFC2WPAction;
  }

}
