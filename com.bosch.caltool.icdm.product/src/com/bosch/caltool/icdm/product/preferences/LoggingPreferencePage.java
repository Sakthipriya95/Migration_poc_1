/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.preferences;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLoggerUtil;
import com.bosch.caltool.icdm.logger.LogFileInfo;
import com.bosch.caltool.icdm.product.util.ICDMConstants;


/**
 * Preference page for logger settings.
 */

public class LoggingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  /**
   * No. of columns in the composite of the page
   */
  private static final int COMPGRID_COL_COUNT = 3;
  /**
   * File name preference key
   */
  private static final String PREF_LOG_FILE_NAME = "logFileName";
  /**
   * File path preference key
   */
  private static final String PREF_LOG_FILE_PATH = "logFilePath";
  /**
   * Default selection preference key
   */
  private static final String PREF_DEF_LOG_PATH_SEL = "defaultLogFilePathSel";
  /**
   * Log file path text.
   */
  private Text logFilePath;
  /**
   * Open Log button
   */
  private Button logOpenBtn;
  /**
   * Check box for default log enablement.
   */
  private Button useDefaultLog;
  /**
   * Previous log file path set in pref page.
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /**
   * Text instance for log file name.
   */
  private Text logFileText;

  /**
   * Apply option.
   */
  private boolean applyPerformed;

  /**
   * The Default Constructor.
   */
  public LoggingPreferencePage() {
    super();
  }

  /**
   * The Parameterized Constructor.
   *
   * @param title the title of this page
   */
  public LoggingPreferencePage(final String title) {
    super(title);

  }

  /**
   * The Parameterized Constructor.
   *
   * @param title the title of this page
   * @param image image
   */
  public LoggingPreferencePage(final String title, final ImageDescriptor image) {
    super(title, image);

  }

  /**
   * init the store with default values
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final void init(final IWorkbench workbench) {
    this.preference.setDefault(PREF_DEF_LOG_PATH_SEL, true);
    setPreferenceStore(this.preference);
  }

  /**
   * Create UI components
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected final Control createContents(final Composite parent) {
    final Composite top = new Composite(parent, SWT.NONE);
    top.setLayout(new GridLayout(1, false));
    top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
    createChkBox(top);
    createLogFileElements(top);

    updateLogPathFrmModel();
    return top;
  }

  /**
   * Creates check box element.
   *
   * @param firstComp composite
   */
  private void createChkBox(final Composite firstComp) {
    final GridData gridData = new GridData(SWT.FILL);
    this.useDefaultLog = new Button(firstComp, SWT.CHECK);
    this.useDefaultLog.setText(ICDMConstants.USE_DEFAULT_LOG_LOCATION);
    this.useDefaultLog.setSelection(true);
    this.useDefaultLog.setLayoutData(gridData);
    this.useDefaultLog.setEnabled(false);
    this.useDefaultLog.setSelection(true);
    this.useDefaultLog.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final boolean isChecked = LoggingPreferencePage.this.useDefaultLog.getSelection();

        if (isChecked) {
          LoggingPreferencePage.this.logFilePath.setText(LogFileInfo.getInstance().getLogFilePath());
          LoggingPreferencePage.this.preference.setValue(PREF_LOG_FILE_PATH,
              LogFileInfo.getInstance().getLogFilePath());
          LoggingPreferencePage.this.logFilePath.setEditable(false);
          LoggingPreferencePage.this.preference.setValue(PREF_DEF_LOG_PATH_SEL, true);
          LoggingPreferencePage.this.logOpenBtn.setEnabled(true);

          LoggingPreferencePage.this.logFileText.setText(LogFileInfo.getInstance().getLogFileName());
          LoggingPreferencePage.this.preference.setValue(PREF_LOG_FILE_NAME,
              LogFileInfo.getInstance().getLogFileName());
          LoggingPreferencePage.this.logFileText.setEditable(false);
          LoggingPreferencePage.this.logFileText.setEnabled(false);

          setMessage(null);
          setErrorMessage(null);
        }
        else {
          LoggingPreferencePage.this.preference.setValue(PREF_DEF_LOG_PATH_SEL, false);
          LoggingPreferencePage.this.logFilePath.setEditable(true);
          LoggingPreferencePage.this.logOpenBtn.setEnabled(true);
          LoggingPreferencePage.this.logFileText.setEditable(true);
          LoggingPreferencePage.this.logFileText.setEnabled(true);
          setMessage(ICDMConstants.ENTR_LOG_FILE_LOC);
        }

      }
    });
  }

  /**
   * Create log file path text and log file name elements.
   *
   * @param firstComp composite
   * @param parent parent composite
   */
  private void createLogFileElements(final Composite firstComp) {
    final Composite secComp = new Composite(firstComp, SWT.NONE);
    final GridLayout layout = new GridLayout(COMPGRID_COL_COUNT, false);
    secComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
    secComp.setLayout(layout);
    final Label logFileLabel = new Label(secComp, SWT.NONE);
    logFileLabel.setText("Log file path:");
    this.logFilePath = new Text(secComp, SWT.BORDER);
    this.logFilePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
    this.logFilePath.setText(LogFileInfo.getInstance().getLogFilePath());
    this.logFilePath.setEditable(false);
    this.logFilePath.addModifyListener(event -> {
      validateFolderPath(LoggingPreferencePage.this.logFilePath.getText());
      LoggingPreferencePage.this.applyPerformed = false;
    });

    this.logOpenBtn = new Button(secComp, SWT.PUSH);
    this.logOpenBtn.setText("Open");
    this.logOpenBtn.setEnabled(true);
    this.logOpenBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * Open directory selection dialog
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        CommonUiUtils.openFile(LoggingPreferencePage.this.logFilePath.getText());
      }
    });

    final Label logFileName = new Label(secComp, SWT.NONE);
    logFileName.setText("Log File Name:");
    this.logFileText = new Text(secComp, SWT.BORDER);
    this.logFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
    this.logFileText.setText(LogFileInfo.getInstance().getLogFileName());
    this.logFileText.setEditable(false);
    this.logFileText.addModifyListener(event -> {
      LoggingPreferencePage.this.preference.setValue(PREF_LOG_FILE_NAME,
          LoggingPreferencePage.this.logFileText.getText().trim());
      LoggingPreferencePage.this.applyPerformed = false;
    });
    new Label(secComp, SWT.NONE);
  }

  /**
   * Validates folder path.
   *
   * @param folderPath folder Path
   */
  private void validateFolderPath(final String folderPath) {
    if (!this.useDefaultLog.getSelection()) {
      if ((folderPath != null) && (folderPath.length() > 0)) {
        final File folder = new File(folderPath.trim());

        if (folder.exists() && folder.isDirectory()) {
          setErrorMessage(null);
          setValid(true);
          this.preference.setValue(PREF_LOG_FILE_PATH, folderPath);
        }
        else {
          setErrorMessage("The path '" + this.logFilePath.getText() + "' does not exist/is not a directory");
          setValid(false);
        }

      }
      else {
        setErrorMessage("Log file path cannot be empty");
        setValid(false);
      }
    }
    isValid();
  }

  /**
   * Set the preference
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected final void performApply() {
    this.applyPerformed = true;
    updateModel();
  }

  /**
   * Reset the changes
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final boolean performCancel() {
    final boolean selected = this.useDefaultLog.getSelection();
    if (selected) {
      setDefaults();
    }
    else {
      this.useDefaultLog.setSelection(false);
      this.preference.setValue(PREF_DEF_LOG_PATH_SEL, false);
      final String filePath = this.preference.getString(PREF_LOG_FILE_PATH);
      final String fileName = this.preference.getString(PREF_LOG_FILE_NAME);
      if (CommonUtils.isEmptyString(filePath) || CommonUtils.isEmptyString(fileName)) {
        setDefaults();
      }
      else {
        this.logFilePath.setText(filePath);
        this.preference.setValue(filePath, PREF_LOG_FILE_PATH);
        this.logFileText.setText(fileName);
        this.preference.setValue(fileName, PREF_LOG_FILE_NAME);
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void performDefaults() {
    setDefaults();
  }

  /**
   * Updates log file path from model.
   */
  private void updateLogPathFrmModel() {
    final boolean selected = this.preference.getBoolean(PREF_DEF_LOG_PATH_SEL);
    if (selected) {
      setDefaults();
    }
    else {
      this.useDefaultLog.setSelection(false);
      this.preference.setValue(PREF_DEF_LOG_PATH_SEL, false);
      final String filePath = this.preference.getString(PREF_LOG_FILE_PATH);
      final String fileName = this.preference.getString(PREF_LOG_FILE_NAME);
      this.logFilePath.setEditable(true);
      this.logFilePath.setEnabled(true);
      this.logOpenBtn.setEnabled(true);
      this.logFileText.setEditable(true);
      this.logFileText.setEnabled(true);

      if (CommonUtils.isEmptyString(filePath) || CommonUtils.isEmptyString(fileName)) {
        setDefaults();
      }
      else {
        this.logFilePath.setText(filePath);
        this.preference.setValue(filePath, PREF_LOG_FILE_PATH);
        this.logFileText.setText(fileName);
        this.preference.setValue(fileName, PREF_LOG_FILE_NAME);
      }
    }
  }

  /**
   * Updates log file path in CalDataAnalysisListContainer.
   */
  private void updateModel() {
    final boolean selected = this.useDefaultLog.getSelection();

    if (selected) {
      this.preference.setValue(PREF_LOG_FILE_PATH, LogFileInfo.getInstance().getLogFilePath());
      this.preference.setValue(PREF_LOG_FILE_NAME, LogFileInfo.getInstance().getLogFileName());
      this.preference.setValue(PREF_DEF_LOG_PATH_SEL, true);
      CDMLoggerUtil.setLogFile(LogFileInfo.getInstance().getLogFilePath());
    }
    else {
      this.preference.setValue(PREF_LOG_FILE_PATH, this.logFilePath.getText());
      this.preference.setValue(PREF_LOG_FILE_NAME, this.logFilePath.getText());
      this.preference.setValue(PREF_DEF_LOG_PATH_SEL, false);
      CDMLoggerUtil.setLogFile(this.logFilePath.getText() + this.logFilePath.getText());
    }
  }

  /**
   * Sets UI elements to default log location.
   */
  private void setDefaults() {
    this.useDefaultLog.setSelection(true);
    this.logFilePath.setText(LogFileInfo.getInstance().getLogFilePath());
    this.logFilePath.setEditable(false);
    this.preference.setValue(PREF_LOG_FILE_PATH, LogFileInfo.getInstance().getLogFilePath());
    this.logFileText.setText(LogFileInfo.getInstance().getLogFileName());
    this.logFileText.setEditable(false);
    this.preference.setValue(PREF_LOG_FILE_NAME, LogFileInfo.getInstance().getLogFileName());
    this.logOpenBtn.setEnabled(true);
    this.useDefaultLog.setSelection(true);
    this.preference.setValue(PREF_DEF_LOG_PATH_SEL, true);
    setMessage(null);
  }

  /**
   * Set the preference to the store
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final boolean performOk() {
    if (!this.applyPerformed) {
      updateModel();
    }
    return super.performOk();
  }

}
