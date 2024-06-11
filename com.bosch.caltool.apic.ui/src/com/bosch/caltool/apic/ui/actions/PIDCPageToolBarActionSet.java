/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.table.filters.PIDCPageToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.PIDCHistoryViewPart;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.common.ui.actions.FilterAction;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.pidcrequestor.PIDCRequestor;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRequestorServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author mga1cob
 */
// ICDM-107
public class PIDCPageToolBarActionSet {


  /**
   *
   */
  private static final String PIDC_EDITOR = "PIDC_EDITOR";

  private final CustomFilterGridLayer pidcFilterGridLayer;

  /**
   * Variant flag buffer when selected on PIDC
   */
  private final boolean pidcVarFlag = true;
  /**
   * Non Variant flag buffer when selected on variant
   */
  private final boolean pidcNonVarFlag = true;

  /**
   * Sub-variant flag buffer when selected on Variant
   */
  private final boolean varSubvarFlag = true;
  /**
   * Non Sub-variant flag buffer when selected on Variant
   */
  private final boolean varNonSubvarFlag = true;
  /**
   * Stores whether the oldSelection is PIDCard or not
   */
  private boolean oldPIDSelection = true;

  /**
   * Defines pidc attribute used flag filter action
   */
  private Action attrUsedAction;
  /**
   * Defines pidc attribute not known flag filter action
   */
  private Action notKnownAction;
  /**
   * Defines pidc attribute not used flag filter action
   */
  private Action attrNotUsedAction;
  /**
   * Defines pidc attribute value non variant filter action
   */
  private Action nonVariantAction;
  /**
   * Defines pidc attribute value not defined filter action
   */
  private Action notDefinedAction;
  /**
   * Defines pidc attribute mandatory filter action
   */
  // ICDM-179
  private Action attrMandtoryAction;
  /**
   * Defines pidc attribute non-mandatory filter action
   */
  // ICDM-278
  private Action attrNonMandtoryAction;
  /**
   * Defines pidc attribute value defined filter action
   */
  private Action definedAction;
  /**
   * Defines pidc attribute value variant filter action
   */
  private Action variantAction;

  private Action attrDepAction;

  private Action groupedAttrAction;
  private Action notGroupedAttrAction;
  private Action attrNonDepAction;
  private Action notClearFilterAction;
  private Action clearFilterAction;
  private Action structFilAction;
  private Action newAttrFilAction;
  private Action notStructFilAction;
  private Action allAttrAction;

  private Action usecaseAndMandatoryAttrAction;

  private Action nonUsecaseAndMandatoryAttrAction;

  private Action quotRelevantAction;

  private Action quotNotRelevantAction;


  private final PIDCAttrPage pidcPage;


  /**
   * @param pidcFilterGridLayer - filter grid layer
   * @param pidcPage - pidc page instance
   */
  public PIDCPageToolBarActionSet(final CustomFilterGridLayer pidcFilterGridLayer, final PIDCAttrPage pidcPage) {
    this.pidcFilterGridLayer = pidcFilterGridLayer;
    this.pidcPage = pidcPage;
  }


  /**
   * This method creates non variant filter action
   *
   * @param toolBarManager instance
   * @param isValue Value/Attr
   */
  public void pidcRequestorAction(final ToolBarManager toolBarManager, final boolean isValue) {

    Action pidcRequestorAction = new Action("PIDC Requestor", SWT.BUTTON1) {

      @Override
      public void run() {
        getPIDCRequestor(isValue);
      }
    };
    // Set the image for non variant filter action
    pidcRequestorAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_REQUESTOR_16X16));
    toolBarManager.add(pidcRequestorAction);
  }

  /**
   * @param isValue value/attr
   */
  private void getPIDCRequestor(final boolean isValue) {

    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save PIDC Requestor");
    fileDialog.setFilterExtensions(new String[] { "xlsm" });
    fileDialog.setFilterIndex(0);
    fileDialog.setOverwrite(true);
    final PIDCRequestor pidcRqstr = new PIDCRequestor(CDMLogger.getInstance(), new APICWebServiceClient());
    String fileName = pidcRqstr.getExcelFilename(fileDialog.getFilterPath() + "\\");
    fileDialog.setFileName(fileName);
    final String fileSelected = fileDialog.open();
    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    try {
      dialog.run(true, true,
          (final IProgressMonitor monitor) -> createPIDCRequestorExcelFile(isValue, pidcRqstr, fileSelected, monitor));
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param isValue
   * @param pidcRqstr
   * @param fileSelected
   * @param monitor
   */
  private void createPIDCRequestorExcelFile(final boolean isValue, final PIDCRequestor pidcRqstr,
      final String fileSelected, final IProgressMonitor monitor) {
    try {
      monitor.beginTask("Saving file ...", 100);
      if (CommonUtils.isNotNull(fileSelected)) {
        PidcRequestorServiceClient client = new PidcRequestorServiceClient();
        monitor.worked(50);
        byte[] bytes;
        bytes = client.getPidcRequestorFile(CommonUtils.getSystemUserTempDirPath());

        if (CommonUtils.isNotNull(bytes)) {
          InputStream fileStream = new ByteArrayInputStream(bytes);
          String valueOrAttr = isValue ? PIDCRequestor.NEW_VALUE : PIDCRequestor.NEW_ATTRIBUTE;
          pidcRqstr.createExcelFile(fileStream, valueOrAttr);
          pidcRqstr.writeExcelFile(fileSelected);
          CDMLogger.getInstance().info("The PIDC Requestor excel report saved successfully at : " + fileSelected,
              Activator.PLUGIN_ID);
          monitor.worked(80);
          monitor.subTask("Opening file ...");
          CommonUiUtils.openFile(fileSelected);
          fileStream.close();
          monitor.worked(100);
        }
        else {
          CDMLogger.getInstance().info(" The PIDC Requestor excel template is not configured !", Activator.PLUGIN_ID);
        }
      }
      monitor.done();
    }
    catch (InvalidFormatException | IOException | ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * ICDM-1270 This method creates pidc change history action
   *
   * @param toolBarManager instance
   * @param pidcAttrPage instance
   */
  public void createPidcHistoryAction(final ToolBarManager toolBarManager, final PIDCAttrPage pidcAttrPage) {

    Action pidcHistoryAction = new Action(ApicUiConstants.PIDC_HISTORY_TITLE, SWT.BUTTON1) {


      @Override
      public void run() {
        createPIDCHistory(pidcAttrPage);
      }
    };
    // Set the image for pidc history action
    pidcHistoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_HISTORY_16X16));
    toolBarManager.add(pidcHistoryAction);
  }


  /**
   * @param pidcAttrPage
   */
  private void createPIDCHistory(final PIDCAttrPage pidcAttrPage) {
    try {
      final PIDCHistoryViewPart pidcHistoryVwPrt = (PIDCHistoryViewPart) PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow().getActivePage().showView(ApicUiConstants.PIDC_HISTORY_VIEW_ID,
              String.valueOf(PIDCPageToolBarActionSet.this.pidcPage.getSelectedPidcVersion().getId()),
              IWorkbenchPage.VIEW_ACTIVATE);
      pidcHistoryVwPrt.setPidCard(PIDCPageToolBarActionSet.this.pidcPage.getSelectedPidcVersion());
      pidcHistoryVwPrt.setPartTitle(ApicUiConstants.PIDC_HISTORY_TITLE + " : " +
          PIDCPageToolBarActionSet.this.pidcPage.getSelectedPidcVersion().getName());
      pidcHistoryVwPrt.setTitleDescription();
      pidcHistoryVwPrt.populateHistoryTable();

      // 235129 - adding the selected pidc attribute into pidc history view when opening history view for first time

      IStructuredSelection selection = (IStructuredSelection) pidcAttrPage.getSelectionProvider().getSelection();

      if ((null != selection) && (null != selection.getFirstElement())) {
        final PidcNattableRowObject compareRowObject = (PidcNattableRowObject) selection.getFirstElement();
        IProjectAttribute pidcAttribute = compareRowObject.getProjectAttributeHandler().getProjectAttr();
        pidcHistoryVwPrt.setSelectedPidcAttr(pidcAttribute);
      }
      // ICDM-2614
      // enabling the pidc and attr filters in the pidc history view
      pidcHistoryVwPrt.pidcAndAttrChangesFlag(true);

      setHistoryView(pidcAttrPage);
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * Sets the corresponding history view part
   */
  private void setHistoryView(final PIDCAttrPage pidPage) {
    if (null != PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()) {
      IViewReference[] views = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
      for (IViewReference view : views) {
        if (view.getId().equals(ApicUiConstants.PIDC_HISTORY_VIEW_ID) && (null != view.getSecondaryId()) &&
            view.getSecondaryId().equals(String.valueOf(pidPage.getSelectedPidcVersion().getId()))) {
          // 235129 - changed from get pidc version name to get Version id in above if check
          pidPage.setHistoryView((PIDCHistoryViewPart) view.getView(false));
        }
      }
    }
  }


  /**
   * This method creates PIDC attribute mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcAttrMandatoryFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute mandatory filter action
    this.attrMandtoryAction = new Action("Mandatory", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrMandatorySel(PIDCPageToolBarActionSet.this.attrMandtoryAction.isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.attrMandtoryAction.getText(),
            PIDCPageToolBarActionSet.this.attrMandtoryAction.isChecked());
        applyPidcFilter();
      }

    };
    // Set the image for PIDC attribute mandatory filter action
    this.attrMandtoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_MANDATORY_16X16));
    this.attrMandtoryAction.setChecked(true);
    toolBarManager.add(this.attrMandtoryAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.attrMandtoryAction, this.attrMandtoryAction.isChecked());

  }

  /**
   *
   */
  private void applyPidcFilter() {
    PIDCPageToolBarActionSet.this.pidcFilterGridLayer.getFilterStrategy().applyToolBarFilterInAllColumns(false);
    PIDCPageToolBarActionSet.this.pidcFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
        new FilterAppliedEvent(PIDCPageToolBarActionSet.this.pidcFilterGridLayer.getSortableColumnHeaderLayer()));
  }


  /**
   * @param toolBarManager toolbar manager
   * @param toolBarFilters toolbar's viewer filter
   */
  public void createQuotationRelevantFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {

    this.quotRelevantAction = new Action("Quotation relevant Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQuotationRelevantUcAttr(isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(getText(), isChecked());
        applyPidcFilter();
      }
    };

    this.quotRelevantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUOTATION_FLAG));
    this.quotRelevantAction.setChecked(true);

    toolBarManager.add(this.quotRelevantAction);

    // Add the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.quotRelevantAction, this.quotRelevantAction.isChecked());
  }


  /**
   * @param toolBarManager toolbar manager
   * @param toolBarFilters toolbar's viewer filter
   */
  public void createQuotationNotRelevantFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {

    this.quotNotRelevantAction = new Action("Non Quotation relevant Attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setQuotationNotRelevantUcAttr(isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(getText(), isChecked());
        applyPidcFilter();
      }
    };

    this.quotNotRelevantAction
        .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.QUOTATION_NOT_RELEVANT_FLAG));
    this.quotNotRelevantAction.setChecked(true);

    toolBarManager.add(this.quotNotRelevantAction);

    // Add the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.quotNotRelevantAction, this.quotNotRelevantAction.isChecked());
  }


  /**
   * This method creates PIDC attribute mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcNonUsecaseAndAttrMandatoryFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute mandatory filter action
    this.nonUsecaseAndMandatoryAttrAction = new Action("Attributes not mapped to project usecases", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setNonUsecaseAndMantoryAttr(PIDCPageToolBarActionSet.this.nonUsecaseAndMandatoryAttrAction.isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.nonUsecaseAndMandatoryAttrAction.getText(),
            PIDCPageToolBarActionSet.this.nonUsecaseAndMandatoryAttrAction.isChecked());
        applyPidcFilter();
      }
    };
    // Set the image for PIDC attribute mandatory filter action
    this.nonUsecaseAndMandatoryAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_UC_16X16));
    toolBarManager.add(this.nonUsecaseAndMandatoryAttrAction);

    String enable = PlatformUI.getPreferenceStore().getString(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED);
    if (!CommonUtils.isEmptyString(enable) && enable.equalsIgnoreCase(ApicConstants.CODE_YES)) {
      this.nonUsecaseAndMandatoryAttrAction.setChecked(false);
      this.nonUsecaseAndMandatoryAttrAction.run();
    }
    else {
      this.nonUsecaseAndMandatoryAttrAction.setChecked(true);
    }
    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.nonUsecaseAndMandatoryAttrAction,
        this.nonUsecaseAndMandatoryAttrAction.isChecked());
  }

  /**
   * This method creates PIDC attribute mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcUsecaseAndAttrMandatoryFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute mandatory filter action
    this.usecaseAndMandatoryAttrAction = new Action("Attributes mapped to project usecases", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters
            .setUsecaseAndMantoryAttr(PIDCPageToolBarActionSet.this.usecaseAndMandatoryAttrAction.isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.usecaseAndMandatoryAttrAction.getText(),
            PIDCPageToolBarActionSet.this.usecaseAndMandatoryAttrAction.isChecked());
        applyPidcFilter();
      }
    };
    // Set the image for PIDC attribute mandatory filter action
    this.usecaseAndMandatoryAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UC_16X16));
    toolBarManager.add(this.usecaseAndMandatoryAttrAction);
    this.usecaseAndMandatoryAttrAction.setChecked(true);
    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.usecaseAndMandatoryAttrAction,
        this.usecaseAndMandatoryAttrAction.isChecked());
  }


  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void pidcAttrDepenFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // ICdm-478 Filter for attributes controlling the visibility of other attributes
    this.attrDepAction = new Action(CommonUIConstants.FILTER_DEPENDENT_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrDependency(PIDCPageToolBarActionSet.this.attrDepAction.isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.attrDepAction.getText(),
            PIDCPageToolBarActionSet.this.attrDepAction.isChecked());

        applyPidcFilter();
      }


    };
    // Set the image for PIDC attribute mandatory filter action
    this.attrDepAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DEPN_ATTR_28X30));
    // ICDM-278
    this.attrDepAction.setChecked(true);
    toolBarManager.add(this.attrDepAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.attrDepAction, this.attrDepAction.isChecked());

  }


  /**
   * Icdm-478 Filter for attribute dependents
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void pidcAttrNotDepenFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    this.attrNonDepAction = new Action(CommonUIConstants.FILTER_NON_DEPENDENT_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNonDep(PIDCPageToolBarActionSet.this.attrNonDepAction.isChecked());
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.attrNonDepAction.getText(),
            PIDCPageToolBarActionSet.this.attrNonDepAction.isChecked());

        applyPidcFilter();
      }
    };
    // Set the image for PIDC Not Dependent Filter
    this.attrNonDepAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTR_NONDEP_16X16));
    // ICDM-278
    this.attrNonDepAction.setChecked(true);
    toolBarManager.add(this.attrNonDepAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.attrNonDepAction, this.attrNonDepAction.isChecked());

  }

  /**
   * Icdm-832
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void createClearFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create Filter for cleared values in an attribute

    String grpName = PIDC_EDITOR;
    String name = "CLEARED_VAL_FILTER";

    this.clearFilterAction = new Action(CommonUiUtils.getMessage(grpName, name), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setClearSel(PIDCPageToolBarActionSet.this.clearFilterAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.clearFilterAction.getText(),
            PIDCPageToolBarActionSet.this.clearFilterAction.isChecked());

        applyPidcFilter();

      }
    };


    // Set the image for non defined filter action
    this.clearFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_16X16));
    // ICDM-278
    this.clearFilterAction.setChecked(true);

    toolBarManager.add(this.clearFilterAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.clearFilterAction, this.clearFilterAction.isChecked());

  }

  /**
   * Icdm-832
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void createNotClearFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create Filter for not cleared values in an attribute
    String grpName = PIDC_EDITOR;
    String name = "NOT_CLEARED_VAL_FILTER";

    this.notClearFilterAction = new Action(CommonUiUtils.getMessage(grpName, name), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotClearSel(PIDCPageToolBarActionSet.this.notClearFilterAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.notClearFilterAction.getText(),
            PIDCPageToolBarActionSet.this.notClearFilterAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for non defined filter action
    this.notClearFilterAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CLEAR_FILTER_16X16));
    // ICDM-278
    this.notClearFilterAction.setChecked(true);
    toolBarManager.add(this.notClearFilterAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.notClearFilterAction, this.notClearFilterAction.isChecked());
  }

  /**
   * This method creates PIDC attribute used filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcAttrUsedFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute used filter action
    this.attrUsedAction = new Action(Messages.getString(IMessageConstants.USED_LABEL), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrUsedFlag(PIDCPageToolBarActionSet.this.attrUsedAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.attrUsedAction.getText(),
            PIDCPageToolBarActionSet.this.attrUsedAction.isChecked());

        applyPidcFilter();
      }
    };
    // Set the image for PIDC attribute used filter action
    this.attrUsedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.USED_16X16));
    // ICDM-278
    this.attrUsedAction.setChecked(true);
    toolBarManager.add(this.attrUsedAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.attrUsedAction, this.attrUsedAction.isChecked());

  }

  /**
   * This method creates PIDC attribute not used filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcAttrNotUsedFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute not used filter action
    this.attrNotUsedAction = new Action(Messages.getString(IMessageConstants.NOTUSED_LABEL), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrNotUsedFlag(PIDCPageToolBarActionSet.this.attrNotUsedAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.attrNotUsedAction.getText(),
            PIDCPageToolBarActionSet.this.attrNotUsedAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for PIDC attribute not used filter action
    this.attrNotUsedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NOT_USED_16X16));

    this.attrNotUsedAction.setChecked(true);
    toolBarManager.add(this.attrNotUsedAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.attrNotUsedAction, this.attrNotUsedAction.isChecked());

  }

  /**
   * This method creates PIDC attribute used not known filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcAttrNotKnownFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute used not known filter action
    this.notKnownAction = new Action(Messages.getString(IMessageConstants.UNKNOWN_LABEL), SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setAttrUsedNotDefined(PIDCPageToolBarActionSet.this.notKnownAction.isChecked());

        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.notKnownAction.getText(),
            PIDCPageToolBarActionSet.this.notKnownAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for PIDC attribute used not known filter action
    this.notKnownAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FILTER_QUESTION_16X16));

    this.notKnownAction.setChecked(true);
    toolBarManager.add(this.notKnownAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.notKnownAction, this.notKnownAction.isChecked());
  }


  /**
   * This method creates defined filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  // ICDM-278
  public void definedFilterAction(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {
    // Create pidc attribute value defined filter action
    this.definedAction = new Action(CommonUIConstants.FILTER_VALUE_DEFINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setDefinedSel(PIDCPageToolBarActionSet.this.definedAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.definedAction.getText(),
            PIDCPageToolBarActionSet.this.definedAction.isChecked());

        applyPidcFilter();


      }
    };
    // Set the image for defined filter action
    this.definedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_DEFINED_28X30));
    // ICDM-278
    this.definedAction.setChecked(true);
    toolBarManager.add(this.definedAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.definedAction, this.definedAction.isChecked());
  }


  /**
   * This method creates non defined filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void notDefinedFilterAction(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {

    // Create pidc attribute value not defined filter action
    this.notDefinedAction = new Action(CommonUIConstants.FILTER_VALUE_NOT_DEFINED, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotDefinedSel(PIDCPageToolBarActionSet.this.notDefinedAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.notDefinedAction.getText(),
            PIDCPageToolBarActionSet.this.notDefinedAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for non defined filter action
    this.notDefinedAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_DEFINED_28X30));
    // ICDM-278
    this.notDefinedAction.setChecked(true);
    toolBarManager.add(this.notDefinedAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.notDefinedAction, this.notDefinedAction.isChecked());
  }

  /**
   * This method creates variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void variantFilterAction(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {
    // Create pidc attribute value variant filter action
    this.variantAction = new Action(CommonUIConstants.FILTER_VARIANT_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setVariantSel(PIDCPageToolBarActionSet.this.variantAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.variantAction.getText(),
            PIDCPageToolBarActionSet.this.variantAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for variant filter action
    this.variantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SHOW_VARIANTS_28X30));
    this.variantAction.setChecked(true);
    toolBarManager.add(this.variantAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.variantAction, this.variantAction.isChecked());
  }

  /**
   * This method creates non variant filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void nonVariantFilterAction(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {
    // Create pidc attribute value non variant filter action
    this.nonVariantAction = new Action(CommonUIConstants.FILTER_NON_VARIANT_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNonVariantSel(PIDCPageToolBarActionSet.this.nonVariantAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.nonVariantAction.getText(),
            PIDCPageToolBarActionSet.this.nonVariantAction.isChecked());

        applyPidcFilter();

      }

    };
    // Set the image for non variant filter action
    this.nonVariantAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_VARIANT_28X30));
    this.nonVariantAction.setChecked(true);
    toolBarManager.add(this.nonVariantAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.nonVariantAction, this.nonVariantAction.isChecked());
  }


  /**
   * This method creates PIDC attribute non-mandatory filter action
   *
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void pidcAttrNonMandatoryFilterAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // Create PIDC attribute non-mandatory filter action
    this.attrNonMandtoryAction = new Action("Non-Mandatory", SWT.TOGGLE) {

      @Override
      public void run() {
        controlNonMandatoryAttrAction(toolBarFilters);
      }
    };
    // Set the image for PIDC attribute non-mandatory filter action
    this.attrNonMandtoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_MANDATORY_28X30));

    this.attrNonMandtoryAction.setChecked(true);
    controlNonMandatoryAttrAction(toolBarFilters);


    toolBarManager.add(this.attrNonMandtoryAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.attrNonMandtoryAction, this.attrNonMandtoryAction.isChecked());


  }

  private void controlNonMandatoryAttrAction(final PIDCPageToolBarFilters toolBarFilters) {
    toolBarFilters.setAttrNonMandatorySel(PIDCPageToolBarActionSet.this.attrNonMandtoryAction.isChecked());
    // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
    // during variant/subvariant selections in details page
    PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
        PIDCPageToolBarActionSet.this.attrNonMandtoryAction.getText(),
        PIDCPageToolBarActionSet.this.attrNonMandtoryAction.isChecked());


    applyPidcFilter();


  }

  /**
   * Show all attributes including invisible attributes
   *
   * @param toolBarManager toolbarmgr
   * @param toolBarFilters filters
   */
  public void showAllAttributesAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    String grpName = PIDC_EDITOR;
    String name = "MISSING_DEP_TOOLBAR_FILTER";
    this.allAttrAction = new FilterAction(CommonUiUtils.getMessage(grpName, name),
        ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16), false) {

      @Override
      public void run() {
        toolBarFilters.setAllAttrVisible(PIDCPageToolBarActionSet.this.allAttrAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.allAttrAction.getText(),
            PIDCPageToolBarActionSet.this.allAttrAction.isChecked());

        applyPidcFilter();

      }
    };

    // Set the image for all attributes filter action
    toolBarManager.add(this.allAttrAction);

    if (!this.allAttrAction.isChecked()) {
      this.allAttrAction.run();
    }

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.allAttrAction, this.allAttrAction.isChecked());
  }

  /**
   * Method to filter the Structed Attributes
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void structFilterAction(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {
    // Create Filter for cleared values in an attribute
    this.structFilAction = new Action(CommonUIConstants.FILTER_STRUCTURED_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setStructSel(PIDCPageToolBarActionSet.this.structFilAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.structFilAction.getText(),
            PIDCPageToolBarActionSet.this.structFilAction.isChecked());

        applyPidcFilter();

      }
    };


    // Set the image for Structured attr filter action
    this.structFilAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.VIR_NODE_16X16));
    // ICDM-278
    this.structFilAction.setChecked(true);
    this.structFilAction.setEnabled(false);
    toolBarManager.add(this.structFilAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.structFilAction, this.structFilAction.isChecked());

  }

  /**
   * Not structed attr filter action
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   */
  public void notstructFilterAction(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {
    this.notStructFilAction = new Action(CommonUIConstants.FILTER_NOT_STRUCTURED_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNotStructSel(PIDCPageToolBarActionSet.this.notStructFilAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.notStructFilAction.getText(),
            PIDCPageToolBarActionSet.this.notStructFilAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for non structed filter action
    this.notStructFilAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_VIR_NODE_16X16));
    // ICDM-278
    this.notStructFilAction.setChecked(true);
    this.notStructFilAction.setEnabled(false);
    toolBarManager.add(this.notStructFilAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.notStructFilAction, this.notStructFilAction.isChecked());

  }


  /**
   * @param toolBarManager - tool bar manager
   * @param toolBarFilters - tool bar filter
   */
  public void newAttributesFilter(final ToolBarManager toolBarManager, final PIDCPageToolBarFilters toolBarFilters) {

    this.newAttrFilAction = new Action(CommonUIConstants.FILTER_NEW_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setNewAttrVisible(PIDCPageToolBarActionSet.this.newAttrFilAction.isChecked());
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.newAttrFilAction.getText(),
            PIDCPageToolBarActionSet.this.newAttrFilAction.isChecked());

        applyPidcFilter();

      }

    };
    // Set the image for new attribute
    this.newAttrFilAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NEW_VERSION_16X16));
    this.newAttrFilAction.setChecked(true);
    this.newAttrFilAction.setEnabled(true);
    toolBarManager.add(this.newAttrFilAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.newAttrFilAction, this.newAttrFilAction.isChecked());

  }

  /**
   * @return the oldPIDSelectionBuf
   */
  public boolean isOldPIDSelection() {
    return this.oldPIDSelection;
  }

  /**
   * @param oldPIDSelectionBuf the oldPIDSelectionBuf to set
   */
  public void setOldPIDSelection(final boolean oldPIDSelectionBuf) {
    this.oldPIDSelection = oldPIDSelectionBuf;
  }

  /**
   * @return the pidcVarFlagBuf
   */
  public boolean isPidcVarFlag() {
    return this.pidcVarFlag;
  }

  /**
   * @return the pidcNonVarFlagBuf
   */
  public boolean isPidcNonVarFlag() {
    return this.pidcNonVarFlag;
  }

  /**
   * @return the varSubvarFlagBuf
   */
  public boolean isVarSubvarFlag() {
    return this.varSubvarFlag;
  }

  /**
   * @return the varNonSubvarFlagBuf
   */
  public boolean isVarNonSubvarFlag() {
    return this.varNonSubvarFlag;
  }


  /**
   * @return the nonVariantAction
   */
  public Action getNonVariantAction() {
    return this.nonVariantAction;
  }


  /**
   * @return the variantAction
   */
  public Action getVariantAction() {
    return this.variantAction;
  }


  /**
   * @return the structFilAction
   */
  public Action getStructFilAction() {
    return this.structFilAction;
  }


  /**
   * @return the allAttrAction
   */
  public Action getAllAttrAction() {
    return this.allAttrAction;
  }

  /**
   * @return the notStructFilAction
   */
  public Action getNotStructFilAction() {
    return this.notStructFilAction;
  }

  // ICDM-2625
  /**
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void showAllGroupedAttrAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // ICdm-2625 Filter for attributes which are grouped
    this.groupedAttrAction = new Action(CommonUIConstants.FILTER_GROUPED_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        controlGroupedAttrFilterAction(toolBarFilters);
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.groupedAttrAction.getText(),
            PIDCPageToolBarActionSet.this.groupedAttrAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for grouped attributes filter action
    this.groupedAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.GROUPED_ATTR_16X16));
    this.groupedAttrAction.setChecked(true);
    toolBarManager.add(this.groupedAttrAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.groupedAttrAction, this.groupedAttrAction.isChecked());
  }

  // ICDM-2625
  private void controlGroupedAttrFilterAction(final PIDCPageToolBarFilters toolBarFilters) {
    toolBarFilters.setAttrGrouped(PIDCPageToolBarActionSet.this.groupedAttrAction.isChecked());
  }

  // ICDM-2625
  /**
   * @param toolBarManager instance
   * @param toolBarFilters instance
   */
  public void showAllNotGrpAttrAction(final ToolBarManager toolBarManager,
      final PIDCPageToolBarFilters toolBarFilters) {
    // ICdm-2625 Filter for attributes which are not grouped
    this.notGroupedAttrAction = new Action(CommonUIConstants.FILTER_NOT_GROUPED_ATTRIBUTES, SWT.TOGGLE) {

      @Override
      public void run() {
        controlNotGroupedAttrFilterAction(toolBarFilters);
        // add action text and its state to map, Map is used to maintain all the filter states. This will be triggered
        // during variant/subvariant selections in details page
        PIDCPageToolBarActionSet.this.pidcPage.getToolBarFilterStateMap().put(
            PIDCPageToolBarActionSet.this.notGroupedAttrAction.getText(),
            PIDCPageToolBarActionSet.this.notGroupedAttrAction.isChecked());

        applyPidcFilter();

      }
    };
    // Set the image for grouped attributes filter action
    this.notGroupedAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NON_GROUPED_ATTR_16X16));
    this.notGroupedAttrAction.setChecked(true);
    toolBarManager.add(this.notGroupedAttrAction);

    // Adding the default state to filters map
    this.pidcPage.addToToolBarFilterMap(this.notGroupedAttrAction, this.notGroupedAttrAction.isChecked());
  }

  // ICDM-2625
  private void controlNotGroupedAttrFilterAction(final PIDCPageToolBarFilters toolBarFilters) {
    toolBarFilters.setAttrNotGrouped(PIDCPageToolBarActionSet.this.notGroupedAttrAction.isChecked());
  }


  /**
   * @return the attrUsedAction
   */
  public Action getAttrUsedAction() {
    return this.attrUsedAction;
  }


  /**
   * @param attrUsedAction the attrUsedAction to set
   */
  public void setAttrUsedAction(final Action attrUsedAction) {
    this.attrUsedAction = attrUsedAction;
  }


  /**
   * @return the notKnownAction
   */
  public Action getNotKnownAction() {
    return this.notKnownAction;
  }


  /**
   * @param notKnownAction the notKnownAction to set
   */
  public void setNotKnownAction(final Action notKnownAction) {
    this.notKnownAction = notKnownAction;
  }


  /**
   * @return the attrNotUsedAction
   */
  public Action getAttrNotUsedAction() {
    return this.attrNotUsedAction;
  }


  /**
   * @param attrNotUsedAction the attrNotUsedAction to set
   */
  public void setAttrNotUsedAction(final Action attrNotUsedAction) {
    this.attrNotUsedAction = attrNotUsedAction;
  }


  /**
   * @return the notDefinedAction
   */
  public Action getNotDefinedAction() {
    return this.notDefinedAction;
  }


  /**
   * @param notDefinedAction the notDefinedAction to set
   */
  public void setNotDefinedAction(final Action notDefinedAction) {
    this.notDefinedAction = notDefinedAction;
  }


  /**
   * @return the attrMandtoryAction
   */
  public Action getAttrMandtoryAction() {
    return this.attrMandtoryAction;
  }


  /**
   * @param attrMandtoryAction the attrMandtoryAction to set
   */
  public void setAttrMandtoryAction(final Action attrMandtoryAction) {
    this.attrMandtoryAction = attrMandtoryAction;
  }


  /**
   * @return the attrNonMandtoryAction
   */
  public Action getAttrNonMandtoryAction() {
    return this.attrNonMandtoryAction;
  }


  /**
   * @param attrNonMandtoryAction the attrNonMandtoryAction to set
   */
  public void setAttrNonMandtoryAction(final Action attrNonMandtoryAction) {
    this.attrNonMandtoryAction = attrNonMandtoryAction;
  }


  /**
   * @return the definedAction
   */
  public Action getDefinedAction() {
    return this.definedAction;
  }


  /**
   * @param definedAction the definedAction to set
   */
  public void setDefinedAction(final Action definedAction) {
    this.definedAction = definedAction;
  }


  /**
   * @return the attrDepAction
   */
  public Action getAttrDepAction() {
    return this.attrDepAction;
  }


  /**
   * @param attrDepAction the attrDepAction to set
   */
  public void setAttrDepAction(final Action attrDepAction) {
    this.attrDepAction = attrDepAction;
  }


  /**
   * @return the groupedAttrAction
   */
  public Action getGroupedAttrAction() {
    return this.groupedAttrAction;
  }


  /**
   * @param groupedAttrAction the groupedAttrAction to set
   */
  public void setGroupedAttrAction(final Action groupedAttrAction) {
    this.groupedAttrAction = groupedAttrAction;
  }


  /**
   * @return the notGroupedAttrAction
   */
  public Action getNotGroupedAttrAction() {
    return this.notGroupedAttrAction;
  }


  /**
   * @param notGroupedAttrAction the notGroupedAttrAction to set
   */
  public void setNotGroupedAttrAction(final Action notGroupedAttrAction) {
    this.notGroupedAttrAction = notGroupedAttrAction;
  }


  /**
   * @return the attrNonDepAction
   */
  public Action getAttrNonDepAction() {
    return this.attrNonDepAction;
  }


  /**
   * @param attrNonDepAction the attrNonDepAction to set
   */
  public void setAttrNonDepAction(final Action attrNonDepAction) {
    this.attrNonDepAction = attrNonDepAction;
  }


  /**
   * @return the notClearFilterAction
   */
  public Action getNotClearFilterAction() {
    return this.notClearFilterAction;
  }


  /**
   * @param notClearFilterAction the notClearFilterAction to set
   */
  public void setNotClearFilterAction(final Action notClearFilterAction) {
    this.notClearFilterAction = notClearFilterAction;
  }


  /**
   * @return the clearFilterAction
   */
  public Action getClearFilterAction() {
    return this.clearFilterAction;
  }


  /**
   * @param clearFilterAction the clearFilterAction to set
   */
  public void setClearFilterAction(final Action clearFilterAction) {
    this.clearFilterAction = clearFilterAction;
  }


  /**
   * @return the newAttrFilAction
   */
  public Action getNewAttrFilAction() {
    return this.newAttrFilAction;
  }


  /**
   * @param newAttrFilAction the newAttrFilAction to set
   */
  public void setNewAttrFilAction(final Action newAttrFilAction) {
    this.newAttrFilAction = newAttrFilAction;
  }


  /**
   * @param nonVariantAction the nonVariantAction to set
   */
  public void setNonVariantAction(final Action nonVariantAction) {
    this.nonVariantAction = nonVariantAction;
  }


  /**
   * @param variantAction the variantAction to set
   */
  public void setVariantAction(final Action variantAction) {
    this.variantAction = variantAction;
  }


  /**
   * @param structFilAction the structFilAction to set
   */
  public void setStructFilAction(final Action structFilAction) {
    this.structFilAction = structFilAction;
  }


  /**
   * @param notStructFilAction the notStructFilAction to set
   */
  public void setNotStructFilAction(final Action notStructFilAction) {
    this.notStructFilAction = notStructFilAction;
  }


  /**
   * @param allAttrAction the allAttrAction to set
   */
  public void setAllAttrAction(final Action allAttrAction) {
    this.allAttrAction = allAttrAction;
  }


  /**
   * @return the usecaseAndMandatoryAttrAction
   */
  public Action getUsecaseAndMandatoryAttrAction() {
    return this.usecaseAndMandatoryAttrAction;
  }


  /**
   * @param usecaseAndMandatoryAttrAction the usecaseAndMandatoryAttrAction to set
   */
  public void setUsecaseAndMandatoryAttrAction(final Action usecaseAndMandatoryAttrAction) {
    this.usecaseAndMandatoryAttrAction = usecaseAndMandatoryAttrAction;
  }


  /**
   * @return the nonUsecaseAndMandatoryAttrAction
   */
  public Action getNonUsecaseAndMandatoryAttrAction() {
    return this.nonUsecaseAndMandatoryAttrAction;
  }


  /**
   * @param nonUsecaseAndMandatoryAttrAction the nonUsecaseAndMandatoryAttrAction to set
   */
  public void setNonUsecaseAndMandatoryAttrAction(final Action nonUsecaseAndMandatoryAttrAction) {
    this.nonUsecaseAndMandatoryAttrAction = nonUsecaseAndMandatoryAttrAction;
  }


  /**
   * @return the quotRelevantAction
   */
  public Action getQuotRelevantAction() {
    return this.quotRelevantAction;
  }


  /**
   * @return the quotNotRelevantAction
   */
  public Action getQuotNotRelevantAction() {
    return this.quotNotRelevantAction;
  }


}
