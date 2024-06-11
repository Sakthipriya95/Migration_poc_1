/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcImportHandler;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author jvi6cob
 */
public class ImportExcelWizardPage extends WizardPage {


  private Text filePathText;
  private String fileSelected;
  private final PidcVersion selPidcVer;
  private boolean isSuccess = true;
  private PidcImportCompareData compareData;


  /**
   * @param pageName
   * @param pidcVersion
   */
  protected ImportExcelWizardPage(final String pageName, final PidcVersion pidcVersion) {
    super(pageName);
    this.selPidcVer = pidcVersion;
    setTitle("Import Project ID Card");
    setDescription("Please select excel file to import");
    this.compareData = new PidcImportCompareData();
    setPageComplete(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    initializeDialogUnits(parent);
    Composite projectGroup = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;

    projectGroup.setLayout(layout);
    projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    setControl(projectGroup);
    new Label(projectGroup, SWT.NONE).setText("Select file : ");

    this.filePathText = new Text(projectGroup, SWT.BORDER);
    GridData directoryPathData = new GridData(SWT.FILL, SWT.NONE, true, false);
    directoryPathData.widthHint = new PixelConverter(this.filePathText).convertWidthInCharsToPixels(25);
    this.filePathText.setLayoutData(directoryPathData);

    final Button button = new Button(projectGroup, SWT.PUSH);
    button.setText("Browse");
    button.addSelectionListener(new SelectionAdapter() {


      @Override
      public void widgetSelected(final SelectionEvent event) {
        FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Import PID Card Excel");
        fileDialog.setFilterExtensions(new String[] { "*.xlsx", "*.xls" });
        fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
        ImportExcelWizardPage.this.fileSelected = fileDialog.open();
        if (ImportExcelWizardPage.this.fileSelected == null) {
          return;
        }
        // disable finish button when user browses to another file
        WizardPage compareWizardPage = (WizardPage) ImportExcelWizardPage.this.getWizard().getPages()[1];
        compareWizardPage.setPageComplete(false);
        //

        ImportExcelWizardPage.this.filePathText.setText(ImportExcelWizardPage.this.fileSelected);
        File selectedFile = new File(ImportExcelWizardPage.this.filePathText.getText());

        if (selectedFile.exists()) {

          parseExcelFile();
          getContainer().updateButtons();
          ImportExcelWizardPage.this.setPageComplete(ImportExcelWizardPage.this.isSuccess);
          if (ImportExcelWizardPage.this.isSuccess) {
            getContainer().showPage(getNextPage());
            nextPressed();
          }
        }
      }
    });
    setButtonLayoutData(button);
  }


  private void parseExcelFile() {

    // To prevent analysis of already selected PIDCard
    // if ImportExcelWizardPage's first page completed, then return

    try {
      ImportExcelWizardPage.this.getWizard().getContainer().run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          monitor.beginTask("Reviewing Changes...", 100);
          monitor.worked(50);
          PidcImportHandler pidcImportHandler = new PidcImportHandler();
          try {
            ImportExcelWizardPage.this.compareData = pidcImportHandler
                .importPidcExcel(ImportExcelWizardPage.this.selPidcVer, ImportExcelWizardPage.this.fileSelected);
            ImportExcelWizardPage.this.isSuccess = true;
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            ImportExcelWizardPage.this.isSuccess = false;
          }
          catch (InvalidInputException exp) {
            CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
            ImportExcelWizardPage.this.isSuccess = false;
          }
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return the filePathText
   */
  public Text getFilePathText() {
    return this.filePathText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPageComplete() {
    return getWizard().getPages()[1].isPageComplete();
  }

  /**
   * {@inheritDoc}
   */
  public void nextPressed() {

    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        CompareImportWizardPage compareWizardPage =
            (CompareImportWizardPage) ImportExcelWizardPage.this.getWizard().getPages()[1];
        compareWizardPage.setViewerInput(ImportExcelWizardPage.this.compareData);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    boolean canProceed = false;
    String fileText = getFilePathText().getText();
    if ((fileText != null) && !fileText.isEmpty() && this.isSuccess) {
      canProceed = true;
    }
    return canProceed;
  }

  /**
   * @return the selPidcVer
   */
  public PidcVersion getSelPidcVer() {
    return this.selPidcVer;
  }
}
