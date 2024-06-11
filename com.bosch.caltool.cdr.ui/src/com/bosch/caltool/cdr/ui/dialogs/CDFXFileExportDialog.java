/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class CDFXFileExportDialog extends AbstractDialog {

  /**
   *
   */
  private static final int FILENAME_INIT_SIZE = 45;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * Section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;
  private CdrReportDataHandler cdrReportData;
  private String fileSelected;
  private boolean revParamsWithstatusScore;
  private String selectedStatus;

  /**
   * @param selectedStatus the selectedStatus to set
   */
  public void setSelectedStatus(final String selectedStatus) {
    this.selectedStatus = selectedStatus;
  }

  private boolean allParamsStatusScore;
  private boolean allParamStatusFile;


  private Button okBtn;

  /**
   * cdr result object
   */
  private CDRReviewResult cdrResult;

  private Button revAllParamStatScore;

  private Button revAllParamStatFromFile;

  private boolean includeReviewComment = true;

  /**
   * @return the allParamStatusFile
   */
  public boolean isAllParamStatusFile() {
    return this.allParamStatusFile;
  }


  /**
   * @param allParamStatusFile the allParamStatusFile to set
   */
  public void setAllParamStatusFile(final boolean allParamStatusFile) {
    this.allParamStatusFile = allParamStatusFile;
  }


  /**
   * @return the revParamsWithstatusScore
   */
  public boolean isRevParamsWithstatusScore() {
    return this.revParamsWithstatusScore;
  }


  /**
   * @param revParamsWithstatusScore the revParamsWithstatusScore to set
   */
  public void setRevParamsWithstatusScore(final boolean revParamsWithstatusScore) {
    this.revParamsWithstatusScore = revParamsWithstatusScore;
  }


  /**
   * @return the allParamsStatusScore
   */
  public boolean isAllParamsStatusScore() {
    return this.allParamsStatusScore;
  }


  /**
   * @param allParamsStatusScore the allParamsStatusScore to set
   */
  public void setAllParamsStatusScore(final boolean allParamsStatusScore) {
    this.allParamsStatusScore = allParamsStatusScore;
  }


  /**
   * @return the selectedStatus
   */
  public String getSelectedStatus() {
    return this.selectedStatus;
  }


  /**
   * @param parentShell parentShell
   * @param result result
   * @param resultData
   */
  public CDFXFileExportDialog(final Shell parentShell, final CDRReviewResult result) {
    super(parentShell);
    this.cdrResult = result;
  }

  /**
   * @param parentShell parentShell
   */
  public CDFXFileExportDialog(final Shell parentShell, final CdrReportDataHandler cdrReportData) {
    super(parentShell);
    this.cdrReportData = cdrReportData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("CDFX File Dialog");
    super.configureShell(newShell);
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Generate CDFX File");

    // Set the message
    setMessage("Generate Cdfx file with latest Check Values", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.composite = getFormToolkit().createComposite(parent);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    createSection();

    return this.composite;
  }

  /**
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "CDFX File");
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    // set the client
    this.section.setClient(this.form);
  }

  /**
   * create the form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // create the controls for File Name selection
    createFileNameControls();
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Select option for export");

    // create the controls for choosing HistoryInfo
    createStatusFromScoreChk();
    createEmptyLabel(2);

    createStatusAllParam();
    createEmptyLabel(2);
    createAllPramStatFromFile();
    createEmptyLabel(1);
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Use Comment from review");
    final Button includeReviewCommentBtn = new Button(this.form.getBody(), SWT.CHECK);
    includeReviewCommentBtn.setSelection(true);
    includeReviewCommentBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        CDFXFileExportDialog.this.includeReviewComment = includeReviewCommentBtn.getSelection();
      }
    });
    // create the control to choose HistoryStatus
    final GridLayout gridLayout = new GridLayout();
    // 3 columns for the layout
    gridLayout.numColumns = 3;
    gridLayout.verticalSpacing = 10;
    gridLayout.horizontalSpacing = 10;
    gridLayout.makeColumnsEqualWidth = false;
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

  }

  /**
   * create empty labels
   */
  private void createEmptyLabel(final int noOfLabels) {
    for (int i = 0; i < noOfLabels; i++) {
      LabelUtil.getInstance().createLabel(this.form.getBody(), "");
    }
  }

  /**
   *
   */
  private void createAllPramStatFromFile() {
    this.revAllParamStatFromFile = new Button(this.form.getBody(), SWT.RADIO);
    CommonDataBO dataBo = new CommonDataBO();
    try {

      this.revAllParamStatFromFile
          .setText(dataBo.getMessage(CDRConstants.CDFX_EXPORT_DIALOG_GROUP, "ALLPARAM_STATUS_FILE"));

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.revAllParamStatFromFile.setLayoutData(gridData);

    // add the listener
    this.revAllParamStatFromFile.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDFXFileExportDialog.this.allParamStatusFile = CDFXFileExportDialog.this.revAllParamStatFromFile.getSelection();
        if (CDFXFileExportDialog.this.revParamsWithstatusScore) {
          CDFXFileExportDialog.this.allParamsStatusScore = false;
          CDFXFileExportDialog.this.revParamsWithstatusScore = false;
        }

        checkSaveBtnEnable();
      }
    });

  }

  /**
   *
   */
  private void createStatusAllParam() {
    this.revAllParamStatScore = new Button(this.form.getBody(), SWT.RADIO);
    CommonDataBO dataBo = new CommonDataBO();
    try {

      this.revAllParamStatScore
          .setText(dataBo.getMessage(CDRConstants.CDFX_EXPORT_DIALOG_GROUP, "ALLPARAM_STATUS_SCORE"));

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    this.revAllParamStatScore.setLayoutData(gridData);

    // add the listener
    this.revAllParamStatScore.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDFXFileExportDialog.this.allParamsStatusScore = CDFXFileExportDialog.this.revAllParamStatScore.getSelection();
        if (CDFXFileExportDialog.this.revParamsWithstatusScore) {
          CDFXFileExportDialog.this.allParamStatusFile = false;
          CDFXFileExportDialog.this.revParamsWithstatusScore = false;
        }

        checkSaveBtnEnable();
      }
    });
  }

  /**
   *
   */
  private void createStatusFromScoreChk() {


    final Button revStatFromScoreChk = new Button(this.form.getBody(), SWT.RADIO);
    // ICDM-2584
    CommonDataBO dataBo = new CommonDataBO();
    try {
      if (CommonUtils.isNotNull(CDFXFileExportDialog.this.cdrResult)) {
        revStatFromScoreChk
            .setText(dataBo.getMessage(CDRConstants.CDFX_EXPORT_DIALOG_GROUP, "REVIWED_PARAM_STATUS_SCORE"));
      }
      else {
        revStatFromScoreChk
            .setText(dataBo.getMessage(CDRConstants.CDFX_EXPORT_DIALOG_GROUP, "REVIWED_PARAM_STATUS_SCORE_WITH_LOCK"));
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    revStatFromScoreChk.setLayoutData(gridData);

    // add the listener
    revStatFromScoreChk.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDFXFileExportDialog.this.revParamsWithstatusScore = revStatFromScoreChk.getSelection();
        if (CDFXFileExportDialog.this.revParamsWithstatusScore) {

          CDFXFileExportDialog.this.allParamStatusFile = false;
          CDFXFileExportDialog.this.allParamStatusFile = false;
        }

        checkSaveBtnEnable();
      }
    });


  }

  /**
  *
  */
  private void createFileNameControls() {

    LabelUtil.getInstance().createLabel(this.form.getBody(), "File Name");
    final Text fileText = TextUtil.getInstance().createText(this.form.getBody(), false, "");
    fileText.setEditable(false);
    fileText.setEnabled(true);
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.widthHint = 350;
    fileText.setLayoutData(txtGridData);
    fileText.setEditable(false);

    Button browseBtn = new Button(this.form.getBody(), SWT.NONE);
    browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for
                                                                                               // browse
                                                                                               // button

    browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        StringBuilder fileNameBuff = new StringBuilder(FILENAME_INIT_SIZE);
        String previousFile;
        if (CommonUtils.isNotNull(CDFXFileExportDialog.this.cdrResult)) {
          fileNameBuff.append(CDFXFileExportDialog.this.cdrResult.getName());
        }
        else {
          fileNameBuff.append(CDFXFileExportDialog.this.cdrReportData.getPidcA2l().getName())
              .append("_LatestReviewData_");
        }
        fileNameBuff.append(ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_05));
        String fileName = fileNameBuff.toString();
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
        final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE | SWT.MULTI);
        fileDialog.setText("Create Cdfx file");
        fileDialog.setFilterExtensions(new String[] { "*.cdfx" });
        fileDialog.setFilterNames(new String[] { "CDFX File(*.cdfx)" });
        fileDialog.setFileName(fileName);
        previousFile = CDFXFileExportDialog.this.fileSelected;
        CDFXFileExportDialog.this.fileSelected = fileDialog.open();

        if (CDFXFileExportDialog.this.fileSelected != null) {
          fileText.setText(CDFXFileExportDialog.this.fileSelected);
        }
        else {
          CDFXFileExportDialog.this.fileSelected = previousFile;
        }
        checkSaveBtnEnable();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // TODO Auto-generated method stub

      }
    });

  }

  /**
   * @return the fileSelected
   */
  public String getFileSelected() {
    return this.fileSelected;
  }


  /**
   * @param fileSelected the fileSelected to set
   */
  public void setFileSelected(final String fileSelected) {
    this.fileSelected = fileSelected;
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * This method enables/disables the ok button
   */
  protected void checkSaveBtnEnable() {
    if (this.okBtn != null) {
      this.okBtn.setEnabled(this.fileSelected != null);
    }
  }


  /**
   * @return the includeReviewComment
   */
  public boolean getIncludeReviewComment() {
    return this.includeReviewComment;
  }


}
