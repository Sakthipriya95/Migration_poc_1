/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.PreCalSelectVariantDialog;
import com.bosch.caltool.icdm.common.ui.jobs.PreCalDataRetrieveJob;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.precal.PreCalAttrValResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author dmo5cob
 */
public class PreCalAttrValWizardPage extends WizardPage {

  /**
   * Task start state
   */
  private static final int START_STATE = 10;

  /**
   * Task intermediate state
   */
  private static final int INTERMEDIATE_STATE = 90;

  /**
   * Task complete state
   */
  private static final int COMPLETE_STATE = 100;

  /**
   * Width hint
   */
  private static final int WIDTH_HINT = 2;

  /**
   * Width hint
   */
  private static final int WIDTH_HINT_200 = 200;

  /**
   * GridTableViewer instance
   */
  private GridTableViewer tableViewer;
  /**
   * exceptionOccured flag
   */
  private boolean exceptionOccured;
  /**
   * incorrectValue flag
   */
  private boolean incorrectValue;
  /**
   * Button instance
   */
  private Button btnSelectVariant;

  /**
   * Description for the wizard page
   */
  private static final String PAGE_DESCRIPTION = "Attribute values to be used to generate the data";

  /**
   * @param pageName Page Name
   * @param a2lFileName A2L File Name
   */
  public PreCalAttrValWizardPage(final String pageName, final String a2lFileName) {
    super(pageName);
    setTitle("Get Pre-Calibration Data for : " + a2lFileName);
    setDescription(PAGE_DESCRIPTION);
    setPageComplete(false);
  }

  private PidcA2LBO getPidcA2LBO() {
    return getDataHandler().getPidcA2LBO();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

  /**
   * {@inheritDoc} createControl
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    final Composite workArea = new Composite(parent, SWT.NONE);
    final GridLayout gridLayout = new GridLayout(WIDTH_HINT, false);
    workArea.setLayout(gridLayout);
    workArea.setLayoutData(GridDataUtil.getInstance().getGridData());
    setControl(workArea);
    createAttrListControl(workArea);
    // create a button to select the value for variant attributes
    this.btnSelectVariant = new Button(workArea, SWT.PUSH);
    this.btnSelectVariant.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.VARIANT_VALUE_16X16));
    this.btnSelectVariant.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc} on Variant select button selction
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        PreCalAttrValResponse prjAttrValSet = getDataHandler().getProjAttrValDetails();
        if (prjAttrValSet == null) {
          return;
        }
        PreCalAttrValWizardPage.this.exceptionOccured = false;
        PreCalSelectVariantDialog dialog = new PreCalSelectVariantDialog(Display.getCurrent().getActiveShell(),
            getPidcA2LBO().getPidcVersion(), new TreeSet<>(prjAttrValSet.getAttrMap().values()),
            prjAttrValSet.getPidcAttrMap(), PreCalAttrValWizardPage.this);

        dialog.open();
        getContainer().updateButtons();

      }
    });
    this.btnSelectVariant.setToolTipText("Select a variant from the list");
  }

  /**
   * Create project control
   */
  private void createAttrListControl(final Composite workArea) {

    this.tableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(workArea,
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    this.tableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.tableViewer.getGrid().setLinesVisible(true);
    this.tableViewer.getGrid().setHeaderVisible(true);
    // create columns
    createTabColumns();
    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createTabColumns() {
    // activate the tooltip support for the viewer
    ColumnViewerToolTipSupport.enableFor(this.tableViewer, ToolTip.NO_RECREATE);
    // Attribute column
    createAttrCol();
    // Value column
    createValueCol();
  }

  /**
   * Value column
   */
  private void createValueCol() {
    final GridViewerColumn valueColumn = new GridViewerColumn(this.tableViewer, SWT.NONE);
    valueColumn.getColumn().setText("Value");
    valueColumn.getColumn().setWidth(WIDTH_HINT_200);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return getDataHandler().getProjAttrValDisplayTxt((Attribute) element);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return getDataHandler().getProjAttrValDisplayToolTip((Attribute) element);
      }
    });
  }


  /**
   * Attr col
   */
  private void createAttrCol() {
    final GridViewerColumn attrColumn = new GridViewerColumn(this.tableViewer, SWT.NONE);
    attrColumn.getColumn().setText("Attribute");
    attrColumn.getColumn().setWidth(WIDTH_HINT_200);
    attrColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return ((Attribute) element).getName();
      }
    });
  }

  /**
   * @return checks if thr are not defined attrs
   */
  public boolean checkIfNotDefinedAttrsPresent() {
    StringBuilder attrs = new StringBuilder();
    GridItem[] gridItemsList = PreCalAttrValWizardPage.this.tableViewer.getGrid().getItems();
    for (GridItem gridItem : gridItemsList) {
      if (gridItem.getText(1).equals(ApicConstants.ATTR_NOT_DEFINED)) {
        attrs.append("[" + gridItem.getText(0) + "]");
      }
    }
    if (attrs.length() > 0) {
      setErrorMessage(
          "The value of few attribute(s) are not defined! Please configure project id card appropriately. ");
      PreCalAttrValWizardPage.this.exceptionOccured = true;
    }
    else {
      setErrorMessage(null);
      setDescription(PAGE_DESCRIPTION);
    }

    return this.exceptionOccured;
  }

  /**
   * @return checks if thr are not defined attrs
   */
  public boolean checkIfHiddenAttrsPresent() {
    StringBuilder attrs = new StringBuilder();
    GridItem[] gridItemsList = PreCalAttrValWizardPage.this.tableViewer.getGrid().getItems();
    for (GridItem gridItem : gridItemsList) {
      if (gridItem.getText(1).equals(ApicConstants.HIDDEN_VALUE)) {
        attrs.append("[" + gridItem.getText(0) + "]");
      }
    }
    if (attrs.length() > 0) {
      setErrorMessage("Export cannot be done since the value of few attribute(s) are hidden . ");
      PreCalAttrValWizardPage.this.exceptionOccured = true;
    }
    else {
      setErrorMessage(null);
      setDescription(PAGE_DESCRIPTION);
    }

    return this.exceptionOccured;
  }

  /**
   * Check if all the features/attrs which SSD needs are available in the PIDC or invisible due to some dependency
   *
   * @return boolean
   */
  private boolean checkForInvisibleAttrs() {

    PreCalAttrValResponse projAttrValDetails = getDataHandler().getProjAttrValDetails();

    SortedSet<Attribute> attrNotPresent = new TreeSet<>();

    for (Entry<Long, Attribute> attrEntry : projAttrValDetails.getAttrMap().entrySet()) {
      PidcVersionAttribute pidcAttr = projAttrValDetails.getPidcAttrMap().get(attrEntry.getKey());
      if ((pidcAttr == null) || (pidcAttr.isAtChildLevel() && (getDataHandler().getSelVariant() != null) &&
          !projAttrValDetails.getPidcVarAttrMap().containsKey(attrEntry.getKey()))) {

        attrNotPresent.add(attrEntry.getValue());
      }

    }

    // Checking for attrs which are not visible due to dependencies
    if (!attrNotPresent.isEmpty()) {
      StringBuilder attrs = new StringBuilder();
      // Iterate over each attribute
      for (Attribute attr : attrNotPresent) {
        attrs.append("[" + attr.getName() + "]").append("\n");
      }
      // show error msg
      setErrorMessage(
          "The following attribute(s) are not visible due to dependencies. Please configure project id card appropriately !  " +
              attrs.toString());
      this.exceptionOccured = true;
      return true;
    }
    setErrorMessage(null);
    setDescription(PAGE_DESCRIPTION);
    return false;
  }

  /**
   *
   */
  private void updateVarValBtnState() {
    PreCalAttrValResponse projAttrValDetails = getDataHandler().getProjAttrValDetails();
    Map<Long, PidcVersionAttribute> pidcAttrMap = projAttrValDetails.getPidcAttrMap();
    boolean flag = false;
    if (pidcAttrMap != null) {
      for (PidcVersionAttribute pAttr : pidcAttrMap.values()) {
        if (pAttr.isAtChildLevel()) {
          flag = true;
          break;
        }
      }
    }
    this.btnSelectVariant.setEnabled(flag);
  }


  /**
   * Update the attribute value table, based on the selection
   */
  public void updateAttrValTableViewer() {
    Display.getDefault().syncExec(() -> {
      PreCalAttrValResponse projAttrValDetails = getDataHandler().getProjAttrValDetails();
      SortedSet<Attribute> sortedAttrs = new TreeSet<>();
      sortedAttrs.addAll(projAttrValDetails.getAttrMap().values());
      PreCalAttrValWizardPage.this.getTableViewer().setInput(sortedAttrs);

      // Check imcomplete attribute definitions
      if (!PreCalAttrValWizardPage.this.checkForInvisibleAttrs() &&
          !PreCalAttrValWizardPage.this.checkIfNotDefinedAttrsPresent()) {
        PreCalAttrValWizardPage.this.checkIfHiddenAttrsPresent();

      }
      PreCalAttrValWizardPage.this.updateVarValBtnState();

    });
  }

  /**
   * {@inheritDoc} canFlipToNextPage
   */
  @Override
  public boolean canFlipToNextPage() {
    GridItem[] gridItemsList = this.tableViewer.getGrid().getItems();
    for (GridItem gridItem : gridItemsList) {
      if (gridItem.getText(1).isEmpty() || ApicConstants.VARIANT_SELECT.equals(gridItem.getText(1)) ||
          ApicConstants.HIDDEN_VALUE.equals(gridItem.getText(1)) ||
          ApicConstants.ATTR_NOT_DEFINED.equals(gridItem.getText(1))) {
        return false;
      }
    }

    return !this.exceptionOccured;
  }

  /**
   * NA
   */
  public void backPressed() {
    // Not applicable
  }

  /**
   * {@inheritDoc} getNextPage
   */
  @Override
  public IWizardPage getNextPage() {
    if (!this.exceptionOccured && !this.incorrectValue) {
      return super.getNextPage();
    }
    return getWizard().getPage("Recommended Values");
  }

  /**
   * @param shell Shell
   */
  public void nextPressed(final Shell shell) {
    fetchRecomValuesJob(shell);
  }

  /**
   * @param shell Shell
   */
  public void fetchRecomValuesJob(final Shell shell) {
    PreCalDataRetrieveJob retrieveJob =
        new PreCalDataRetrieveJob("CDR Retrieving Data", System.currentTimeMillis(), this) {

          /**
           * {@inheritDoc} fetchRecomValuesJob run
           */
          @Override
          protected IStatus run(final IProgressMonitor monitor) {
            // ICdm-976 pass the shell Object
            return fetchRecommendedVal(monitor, shell);
          }

          /**
           * {@inheritDoc} on cancel
           */
          @Override
          protected void canceling() {
            super.canceling();
            getContainer().updateButtons();
          }
        };
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    retrieveJob.schedule();
  }


  /**
   * @param monitor
   * @param shell
   */
  private IStatus fetchRecommendedVal(final IProgressMonitor monitor, final Shell shell) {
    monitor.beginTask("Fetching Caldata information", COMPLETE_STATE);

    getDataHandler().getCalDataMap().clear();
    monitor.worked(START_STATE);
    // TODO:Check whether isCanceled can nested further inside
    if (monitor.isCanceled()) {
      return Status.CANCEL_STATUS;
    }
    String exceptionStr = "";
    // ICdm-976 pass the shell Object
    try {
      getDataHandler().fetchPreCalData();
    }
    catch (Exception ex) {
      exceptionStr = ex.getMessage();
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    monitor.worked(INTERMEDIATE_STATE);
    final PreCalDataExportWizard wizard = getWizard();
    final PreCalRecommendedValuesPage recomValPage = (PreCalRecommendedValuesPage) wizard.getNextPage(this);
    // ICdm-976 move the code for Showing the warning dialog to the end
    recomValPage.setInput(false, shell);
    recomValPage.setAddedJob(true);
    getDataHandler().setFlagChanged(false);
    monitor.done();
    if (getDataHandler().getCalDataMap().values().isEmpty() && exceptionStr.isEmpty()) {
      exceptionStr = "Export cannot be done since no parameter has rules satisfying the filter criteria.";
    }
    if (!CommonUtils.isEmptyString(exceptionStr)) {
      CDMLogger.getInstance().warnDialog(exceptionStr, null, Activator.PLUGIN_ID);
    }

    return Status.OK_STATUS;
  }

  /**
   * @return the tableViewer
   */
  public GridTableViewer getTableViewer() {
    return this.tableViewer;
  }

  /**
   * @return the exceptionOccured
   */
  public boolean isExceptionOccured() {
    return this.exceptionOccured;
  }

  /**
   * @param exceptionOccured the exceptionOccured to set
   */
  public void setExceptionOccured(final boolean exceptionOccured) {
    this.exceptionOccured = exceptionOccured;
  }

  private PreCalDataWizardDataHandler getDataHandler() {
    return getWizard().getDataHandler();
  }

}
