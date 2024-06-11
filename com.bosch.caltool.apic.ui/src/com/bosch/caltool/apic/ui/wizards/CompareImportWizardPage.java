/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.apic.ui.sorter.PIDCImportTabViewerSorter;
import com.bosch.caltool.apic.ui.table.filters.PIDCImportAttrFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author jvi6cob
 */
public class CompareImportWizardPage extends WizardPage {

  /**
   * Constant string for pidc
   */
  private static final String PIDC = "PIDC : ";
  private PIDCImportTabViewerSorter pidcAttrTabSorter;
  private GridTableViewer pidcAttrTabViewer;
  private final PidcVersion pidcVer;

  private Text filePathText;

  private Label exportLabel;
  private Button exportBrowseButton;
  private Button enableButton;
  private String fileSelected;
  private String fileExtn;

  /**
   * To avoid .xlsx.xlsx in default export file
   */
  private String defaultFilePathWithOutExtn;
  private String defaultFilePathWithExtn;
  private Text filterTxt;
  private PIDCImportAttrFilter pidcAttrFilter;
  private PidcImportCompareData compareData;

  /**
   * @param pageName String
   * @param pidcVersion PIDCard version
   */
  protected CompareImportWizardPage(final String pageName, final PidcVersion pidcVersion) {
    super(pageName);
    setTitle("Verify changes");
    setDescription(PIDC + pidcVersion.getName());
    setPageComplete(false);
    this.pidcVer = pidcVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    Composite workArea = new Composite(parent, SWT.NONE);
    setControl(workArea);
    workArea.setLayout(new GridLayout());
    workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
    createTableGroup(workArea);
    createExportGroup(workArea);
  }


  /**
   * @param workArea
   */
  private void createTableGroup(final Composite workArea) {
    Group compareImportGroup = new Group(workArea, SWT.NONE);
    compareImportGroup.setFont(workArea.getFont());
    compareImportGroup.setText("Changes");
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    compareImportGroup.setLayoutData(gridData);
    compareImportGroup.setLayout(new GridLayout(1, false));

    this.filterTxt = new Text(compareImportGroup, SWT.BORDER);
    this.filterTxt.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.setEnabled(true);
    addModifyListenerForFilterTxt();
    this.pidcAttrTabViewer =
        new GridTableViewer(compareImportGroup, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    constructTable();

  }

  /**
   * @param workArea
   */
  private void createExportGroup(final Composite workArea) {
    Group excelExportGroup = new Group(workArea, SWT.NONE);
    excelExportGroup.setFont(workArea.getFont());
    excelExportGroup.setText("Save results to");
    excelExportGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    excelExportGroup.setLayout(new GridLayout(1, false));

    Composite excelGroupComposite = new Composite(excelExportGroup, SWT.NONE);
    excelGroupComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    int numColumn = 3;
    excelGroupComposite.setLayout(new GridLayout(numColumn, false));

    this.enableButton = new Button(excelGroupComposite, SWT.CHECK);
    this.enableButton.setText("Use Default Location");
    GridData enableData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    enableData.horizontalSpan = numColumn;
    this.enableButton.setLayoutData(enableData);
    this.enableButton.setSelection(true);
    this.enableButton.setEnabled(false);


    this.exportLabel = new Label(excelGroupComposite, SWT.NONE);
    this.exportLabel.setText("Select Location");

    this.filePathText = new Text(excelGroupComposite, SWT.BORDER);
    GridData directoryPathData = new GridData(SWT.FILL, SWT.CENTER, true, false);
    directoryPathData.horizontalSpan = numColumn - 2;
    directoryPathData.horizontalIndent = 0;
    directoryPathData.widthHint = new PixelConverter(this.filePathText).convertWidthInCharsToPixels(25);
    this.filePathText.setLayoutData(directoryPathData);
    this.filePathText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = CompareImportWizardPage.this.filePathText.getText();
        if ((text == null) || text.isEmpty()) {
          CompareImportWizardPage.this.setPageComplete(false);
        }
        else {
          CompareImportWizardPage.this.fileSelected = text;
          CompareImportWizardPage.this.setPageComplete(true);
        }

      }
    });

    this.exportBrowseButton = new Button(excelGroupComposite, SWT.PUSH);
    this.exportBrowseButton.setText("Browse");
    this.exportBrowseButton.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        String PIDName = CompareImportWizardPage.this.pidcVer.getName().trim();

        String fileName = PIDName;
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");

        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        fileDialog.setText("Save Compare Import Excel Report");
        fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXTNS);
        fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
        fileDialog.setFilterIndex(0);
        fileDialog.setFileName(fileName + "_ImportCompareReport");
        fileDialog.setOverwrite(true);
        CompareImportWizardPage.this.fileSelected = fileDialog.open();
        CompareImportWizardPage.this.fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
        if (CompareImportWizardPage.this.fileSelected != null) {
          CompareImportWizardPage.this.setPageComplete(true);
          CompareImportWizardPage.this.filePathText.setText(CompareImportWizardPage.this.fileSelected);
        }
      }
    });
    setButtonLayoutData(this.exportBrowseButton);

    this.enableButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        updateEnableState(CompareImportWizardPage.this.enableButton.getSelection());
      }
    });
    updateEnableState(this.enableButton.getSelection());

  }

  /**
   * @param selection
   */
  private void updateEnableState(final boolean enabled) {
    this.exportLabel.setEnabled(!enabled);
    this.filePathText.setEnabled(!enabled);
    this.exportBrowseButton.setEnabled(!enabled);
    if (enabled && (this.defaultFilePathWithExtn != null)) {
      this.filePathText.setText(this.defaultFilePathWithExtn);
      this.fileSelected = this.defaultFilePathWithOutExtn;
      this.fileExtn = ExcelConstants.FILTER_EXTNS[0];
    }
    setPageComplete(!((this.fileSelected == null) || this.fileSelected.isEmpty()));
  }

  private void constructTable() {
    this.pidcAttrTabSorter = new PIDCImportTabViewerSorter();

    this.pidcAttrTabViewer.setContentProvider(new ArrayContentProvider());
    this.pidcAttrTabViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.pidcAttrTabViewer.getGrid().setLinesVisible(true);
    this.pidcAttrTabViewer.getGrid().setHeaderVisible(true);

    CompareImportPageColCreator colcreator =
        new CompareImportPageColCreator(this.pidcAttrTabViewer, this.pidcAttrTabSorter);
    colcreator.createPIDCAttrTabViewerColumns();

    this.pidcAttrTabViewer.setInput("");

    this.pidcAttrTabViewer.setComparator(this.pidcAttrTabSorter);

    this.pidcAttrFilter = new PIDCImportAttrFilter();
    this.pidcAttrTabViewer.addFilter(this.pidcAttrFilter);
  }

  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = CompareImportWizardPage.this.filterTxt.getText().trim();
        CompareImportWizardPage.this.pidcAttrFilter.setFilterText(text);
        CompareImportWizardPage.this.pidcAttrTabViewer.refresh();
      }
    });
  }

  /**
   * @param compareData PidcImportCompareData
   */
  public void setViewerInput(final PidcImportCompareData compareData) {
    this.compareData = compareData;

    if ((null != compareData) && (!compareData.getVerAttrImportData().isEmpty() ||
        !compareData.getVarAttrImportData().isEmpty() || !compareData.getSubvarAttrImportData().isEmpty())) {
      List<ProjectImportAttr<?>> compareAttrs = new ArrayList<>();
      compareAttrs.addAll(compareData.getVerAttrImportData().values());
      compareData.getVarAttrImportData().values().forEach(varMap -> compareAttrs.addAll(varMap.values()));
      compareData.getSubvarAttrImportData().values().forEach(varMap -> compareAttrs.addAll(varMap.values()));

      this.pidcAttrTabViewer.setInput(compareAttrs);
      String tempDir = System.getProperty("user.home");
      if (!tempDir.endsWith(File.separator)) {
        tempDir += File.separator;
      }

      this.defaultFilePathWithOutExtn =
          tempDir + this.pidcVer.getName().trim().replaceAll("[^a-zA-Z0-9]+", "_") + "_ImportCompareReport";
      this.defaultFilePathWithExtn = this.defaultFilePathWithOutExtn + "." + ExcelConstants.FILTER_EXTNS[0];
      this.fileSelected = this.defaultFilePathWithOutExtn;
      this.fileExtn = ExcelConstants.FILTER_EXTNS[0];
      this.filePathText.setText(this.defaultFilePathWithExtn);

      this.enableButton.setEnabled(true);


      setPageComplete(true);
      setErrorMessage(null);
      setDescription(PIDC + this.pidcVer.getName());
    }
    else

    {
      this.pidcAttrTabViewer.setInput("");
      this.fileSelected = null;
      this.fileExtn = null;
      this.filePathText.setText("");

      this.enableButton.setEnabled(false);
      this.exportLabel.setEnabled(false);
      this.filePathText.setEnabled(false);
      this.exportBrowseButton.setEnabled(false);


      setPageComplete(false);
      setErrorMessage(null);
      setDescription(PIDC + this.pidcVer.getName() + " No changes found");
    }
  }

  /**
   * @return the pidcAttrTabViewer
   */
  public GridTableViewer getPidcAttrTabViewer() {
    return this.pidcAttrTabViewer;
  }

  /**
   *
   */
  public void clearViewerContents() {
    this.pidcAttrTabViewer.setInput("");

  }


  /**
   * @return the fileSelected
   */
  public String getFileSelected() {
    return this.fileSelected;
  }


  /**
   * @return the fileExtn
   */
  public String getFileExtn() {
    return this.fileExtn;
  }

  /**
   * @return the compareData
   */
  public PidcImportCompareData getCompareData() {
    return this.compareData;
  }

  /**
   * @param compareData the compareData to set
   */
  public void setCompareData(final PidcImportCompareData compareData) {
    this.compareData = compareData;
  }


}
