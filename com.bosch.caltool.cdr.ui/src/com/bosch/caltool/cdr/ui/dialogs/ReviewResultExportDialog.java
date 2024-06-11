/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.jobs.ReviewResultExportMainJob;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.report.excel.RvwResultExportInput;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * @author hnu1cob
 */
public class ReviewResultExportDialog extends AbstractDialog {

  /**
   * Number of columns in the dialog area
   */
  private static final int NO_OF_COLUMNS = 2;
  /**
   * Ok Button instance
   */
  private Button okBtn;
  private Composite topComp;
  private FormToolkit formToolkit;
  private Text filePathText;
  private Form form;
  private Set<CDRReviewResult> reviewResultSet;
  private String filePath;
  private ReviewResultClientBO resultClientBO;
  private Button btnAutomaticOpen;
  private boolean openAutomatically;
  private Button onlyVisibleColsBtn;
  private Button onlyFilteredRowsBtn;
  private Button selectedWpRowsBtn;
  private org.eclipse.swt.widgets.List wrkPkgList;
  private Button workPkgBrowse;
  private java.util.List<A2lWorkPackage> selectedWpList = new ArrayList<>();
  java.util.List<A2lWorkPackage> wpListForReview = new ArrayList<>();
  private final boolean fromEditor;
  private boolean isMultipleSelection;
  private Button separateReportButton;
  private String fileExtn;
  
  private Button rdNoQnaire;
  private Button rdInclQnaireWrkSet;
  private Button rdQnaireWrkSetOrBaseLine;

  /**
   * @param parentShell
   * @param revResults
   */
  public ReviewResultExportDialog(final Shell parentShell, final Set<CDRReviewResult> revResults) {
    super(parentShell);
    this.reviewResultSet = new HashSet<>(revResults);
    this.isMultipleSelection = this.reviewResultSet.size() > 1;
    this.fromEditor = false;
  }

  /**
   * @param activeShell
   * @param resultClientBO
   */
  public ReviewResultExportDialog(final Shell activeShell, final ReviewResultClientBO resultClientBO) {
    super(activeShell);
    this.resultClientBO = resultClientBO;
    this.fromEditor = true;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);


    final String title = "Review Result Export";
    // Set the title
    setTitle(title);

    final String dialogMessage = "Export the selected review result(s)";
    // Set the message
    setMessage(dialogMessage, IMessageProvider.INFORMATION);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Review Result Export");
  }

  /**
   * Creates the Dialog Area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.topComp = (Composite) super.createDialogArea(parent);
    this.topComp.setLayout(new GridLayout());
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.topComp.setLayoutData(gridData);
    createComposite();
    return this.topComp;
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

  /**
   * This method initializes composite
   *
   * @param parent
   */
  private void createComposite() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    Composite composite = getFormToolkit().createComposite(this.topComp);
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    createSection(composite);
  }

  /**
   * This method initializes section
   *
   * @param parentComposite
   */
  private void createSection(final Composite parentComposite) {
    Section section =
        getFormToolkit().createSection(parentComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setLayoutData(GridDataUtil.getInstance().getGridData());
    section.setLayout(new GridLayout());
    section.setExpanded(true);
    section.setText("Export Options");
    section.getDescriptionControl().setEnabled(false);
    createForm(section);
    section.setClient(this.form);
  }

  /**
   * This method initializes Form
   *
   * @param section
   */

  private void createForm(final Section section) {
    final GridLayout gridLayout = new GridLayout();
    this.form = getFormToolkit().createForm(section);
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    createDialog();
  }

  /**
   *
   */
  private void createDialog() {
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COLUMNS;

    createExportOptions();
    createOutputFileSection();
  }

  /**
   *
   */
  private void createOpenAutomicallyButton(final Group exportFileGrp) {
    this.btnAutomaticOpen = new Button(exportFileGrp, SWT.CHECK);
    this.btnAutomaticOpen.setText("Open automatically?");
    this.btnAutomaticOpen.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        ReviewResultExportDialog.this.openAutomatically = ReviewResultExportDialog.this.btnAutomaticOpen.getSelection();
      }
    });
  }

  /**
   *
   */
  private void createExportOptions() {
    final Group rvwEditorRowsGrp = new Group(this.form.getBody(), SWT.NONE);
    rvwEditorRowsGrp.setText("Review Editor Filter");
    rvwEditorRowsGrp.setLayout(new GridLayout(1, true));
    rvwEditorRowsGrp.setLayoutData(GridDataUtil.getInstance().getGridData());
    
    final Composite editorFilterComposite = createComposite(rvwEditorRowsGrp, 1);
    final Group exportRowsGrp = new Group(editorFilterComposite, SWT.NONE);
    exportRowsGrp.setText("Rows to Export");
    exportRowsGrp.setLayout(new GridLayout(1, true));
    exportRowsGrp.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite rowsComposite = createComposite(exportRowsGrp, 3);

    Button allRows = new Button(rowsComposite, SWT.RADIO);
    allRows.setText("All rows");
    allRows.setSelection(true);

    this.onlyFilteredRowsBtn = new Button(rowsComposite, SWT.RADIO);
    this.onlyFilteredRowsBtn.setText("Only filtered rows");
    this.onlyFilteredRowsBtn.setEnabled(this.fromEditor);

    this.selectedWpRowsBtn = new Button(rowsComposite, SWT.RADIO);
    this.selectedWpRowsBtn.setText("By work package");
    // disable WP selection when multiple review results are selected
    this.selectedWpRowsBtn.setEnabled(!this.isMultipleSelection);

    this.selectedWpRowsBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ReviewResultExportDialog.this.wrkPkgList
            .setEnabled(ReviewResultExportDialog.this.selectedWpRowsBtn.getSelection());
        ReviewResultExportDialog.this.workPkgBrowse
            .setEnabled(ReviewResultExportDialog.this.selectedWpRowsBtn.getSelection());
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // unused

      }
    });

    final Composite selectWPComposite = createComposite(exportRowsGrp, 3);

    createWrkPackageControl(selectWPComposite);

    final Group exportOptionsGrp = new Group(editorFilterComposite, SWT.NONE);
    exportOptionsGrp.setText("Columns to Export");
    exportOptionsGrp.setLayout(new GridLayout(2, true));
    exportOptionsGrp.setLayoutData(GridDataUtil.getInstance().getGridData());

    Button allColumns = new Button(exportOptionsGrp, SWT.RADIO);
    allColumns.setText("All columns");
    allColumns.setSelection(true);

    this.onlyVisibleColsBtn = new Button(exportOptionsGrp, SWT.RADIO);
    this.onlyVisibleColsBtn.setText("Only visible columns");
    this.onlyVisibleColsBtn.setEnabled(this.fromEditor);
    
    final Group includeQnaireGrp = new Group(this.form.getBody(), SWT.NONE);
    includeQnaireGrp.setText("Include Questionnaire Details");
    includeQnaireGrp.setLayout(new GridLayout(1, true));
    includeQnaireGrp.setLayoutData(GridDataUtil.getInstance().getGridData());
    
    final Composite exportOptionComposite = createComposite(includeQnaireGrp, 1);
    
    this.rdNoQnaire  = new Button(exportOptionComposite, SWT.RADIO);
    this.rdNoQnaire.setText("No Questionnaire Details");
    this.rdNoQnaire.setSelection(true);
    
    this.rdInclQnaireWrkSet = new Button(exportOptionComposite, SWT.RADIO);
    this.rdInclQnaireWrkSet.setText("Include Questionnaires (Working Set)");
    
    this.rdQnaireWrkSetOrBaseLine = new Button(exportOptionComposite, SWT.RADIO);
    this.rdQnaireWrkSetOrBaseLine.setText("Include Questionnaires (Latest Baseline or Working Set)");
    
  }

  /**
   * @param newComposite as input
   * @return
   */
  private Composite createComposite(final Composite newComposite, final int numColumns) {
    final Composite composite = new Composite(newComposite, SWT.NONE);
    composite.setLayout(new GridLayout(numColumns, false));
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    return composite;
  }


  private void createWrkPackageControl(final Composite selectWPComposite) {
    this.wrkPkgList =
        new org.eclipse.swt.widgets.List(selectWPComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);

    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    this.wrkPkgList.setLayoutData(data);
    this.wrkPkgList.setEnabled(false);
    // composite for aligining the selection and delete buttons
    final Composite wrkPkgBtComp = new Composite(selectWPComposite, SWT.NONE);
    final GridLayout layoutWrkPkgComp = new GridLayout();
    layoutWrkPkgComp.numColumns = 1;
    layoutWrkPkgComp.makeColumnsEqualWidth = false;
    layoutWrkPkgComp.marginWidth = 0;
    layoutWrkPkgComp.marginTop = 0;
    wrkPkgBtComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    wrkPkgBtComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    wrkPkgBtComp.setLayout(layoutWrkPkgComp);

    this.workPkgBrowse = getFormToolkit().createButton(wrkPkgBtComp, "", SWT.PUSH);
    this.workPkgBrowse.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    this.workPkgBrowse.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.workPkgBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    this.workPkgBrowse.setEnabled(false);

    this.workPkgBrowse.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {

          fillWorkpackages();

          final RvwExportWPSelectionDialog wrkPkgSelDialog =
              new RvwExportWPSelectionDialog(Display.getCurrent().getActiveShell(),
                  ReviewResultExportDialog.this.wpListForReview, ReviewResultExportDialog.this.selectedWpList);

          if (wrkPkgSelDialog.open() == 0) {
            ReviewResultExportDialog.this.selectedWpList = wrkPkgSelDialog.getSelectedWorkpackages();
            java.util.List<String> wpList = new ArrayList<>();
            ReviewResultExportDialog.this.selectedWpList.stream().forEach(wp -> wpList.add(wp.getName()));
            ReviewResultExportDialog.this.wrkPkgList.setItems(wpList.toArray(new String[0]));
          }
        });
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // UNUSED
      }

    });
  }

  private void fillWorkpackages() {
    if (CommonUtils.isNull(ReviewResultExportDialog.this.resultClientBO)) {
      ReviewResultExportDialog.this.resultClientBO = new CDRHandler()
          .getRevResultClientBo(ReviewResultExportDialog.this.reviewResultSet.iterator().next().getId());
    }

    this.resultClientBO.getResponse().getA2lWpSet().stream().forEach(wpResponse -> {
      if (!this.wpListForReview.contains(wpResponse.getA2lWorkPackage())) {
        this.wpListForReview.add(wpResponse.getA2lWorkPackage());
      }
    });
  }

  private void createOutputFileSection() {


    Group exportFileGrp = new Group(this.form.getBody(), SWT.NONE);
    exportFileGrp.setText("Output File Options");
    GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 1;
    exportFileGrp.setLayout(gridLayout1);
    exportFileGrp.setLayoutData(GridDataUtil.getInstance().getGridData());


    this.separateReportButton = new Button(exportFileGrp, SWT.CHECK);
    this.separateReportButton.setText("Create separate report for each work package");
    this.separateReportButton.setEnabled(!this.isMultipleSelection);

    final Composite selectWPComposite = createComposite(exportFileGrp, 3);

    Label exportToLbl = new Label(selectWPComposite, SWT.NONE);
    exportToLbl.setText("Export file path : ");
    this.filePathText = new Text(selectWPComposite, SWT.BORDER);
    this.filePathText.setLayoutData(GridDataUtil.getInstance().getTextGridData());

    Button browseExportPath = new Button(selectWPComposite, SWT.PUSH);
    browseExportPath.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    browseExportPath.setToolTipText("Browse the output path");
    browseExportPath.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        DirectoryDialog directorydialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
        directorydialog.setFilterPath(CommonUtils.getUserDirPath());
        String selectedDirectory = directorydialog.open();

        if (!CommonUtils.isEmptyString(selectedDirectory)) {
          ReviewResultExportDialog.this.filePathText.setText(selectedDirectory);
          ReviewResultExportDialog.this.filePath = selectedDirectory;
          ReviewResultExportDialog.this.okBtn.setEnabled(true);
        }
      }
    });
    createOpenAutomicallyButton(exportFileGrp);
    
  }

  /**
   * create button for Button Bar
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(false);
    // creating cancel button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if ((this.filePath != null) && isWpSelectionOK()) {

      RvwResultExportInput exportInput = new RvwResultExportInput();

      exportInput.setFileExt(ExcelConstants.FILTER_EXTNS[0]);
      exportInput.setFilePath(this.filePath);
      exportInput.setOnlyFiltered(this.onlyFilteredRowsBtn.getSelection());
      exportInput.setExportOnlyVisibleCols(this.onlyVisibleColsBtn.getSelection());
      exportInput.setOpenAutomatically(this.openAutomatically);
      exportInput.setSelectedReviewResultSet(this.reviewResultSet);
      exportInput.setSelectedWpList(this.selectedWpList);
      exportInput.setSeparateExportForWp(this.separateReportButton.getSelection());
      exportInput.setResultClientBO(this.resultClientBO);
      exportInput.setFromEditor(this.fromEditor);
      exportInput.setOnlyRvwResult(this.rdNoQnaire.getSelection());
      exportInput.setOnlyRvwResAndQnaireWrkSet(this.rdInclQnaireWrkSet.getSelection());
      exportInput.setOnlyRvwResAndQnaireLstBaseline(this.rdQnaireWrkSetOrBaseLine.getSelection());
      
      Job job = new ReviewResultExportMainJob(new MutexRule(), exportInput);
      CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
      job.schedule();

      super.okPressed();
    }
  }

  private boolean isWpSelectionOK() {
    boolean validate = true;
    if (this.selectedWpRowsBtn.getSelection() && this.selectedWpList.isEmpty()) {
      validate = false;
      CDMLogger.getInstance().errorDialog("Please select work packages to export!", Activator.PLUGIN_ID);
    }
    return validate;
  }


  /**
   * @return the fileExtn
   */
  public String getFileExtn() {
    return this.fileExtn;
  }

  /**
   * @return the reviewResultSet
   */
  public Set<CDRReviewResult> getReviewResultSet() {
    return this.reviewResultSet;
  }


  /**
   * @return the filePath
   */
  public String getFilePath() {
    return this.filePath;
  }


  /**
   * @return the resultClientBO
   */
  public ReviewResultClientBO getResultClientBO() {
    return this.resultClientBO;
  }


  /**
   * @return the openAutomatically
   */
  public boolean isOpenAutomatically() {
    return this.openAutomatically;
  }


  /**
   * @return the onlyVisibleColsBtn
   */
  public Button getOnlyVisibleColsBtn() {
    return this.onlyVisibleColsBtn;
  }


  /**
   * @return the onlyFilteredRowsBtn
   */
  public Button getOnlyFilteredRowsBtn() {
    return this.onlyFilteredRowsBtn;
  }


  /**
   * @return the selectedWpList
   */
  public java.util.List<A2lWorkPackage> getSelectedWpList() {
    return this.selectedWpList;
  }


  /**
   * @return the fromEditor
   */
  public boolean isFromEditor() {
    return this.fromEditor;
  }


  /**
   * @return the separateReportButton
   */
  public Button getSeparateReportButton() {
    return this.separateReportButton;
  }

  
  /**
   * @return the rdNoQnaire
   */
  public Button getRdNoQnaire() {
    return rdNoQnaire;
  }

  
  /**
   * @param rdNoQnaire the rdNoQnaire to set
   */
  public void setRdNoQnaire(Button rdNoQnaire) {
    this.rdNoQnaire = rdNoQnaire;
  }

  
  /**
   * @return the rdInclQnaireWrkSet
   */
  public Button getRdInclQnaireWrkSet() {
    return rdInclQnaireWrkSet;
  }

  
  /**
   * @param rdInclQnaireWrkSet the rdInclQnaireWrkSet to set
   */
  public void setRdInclQnaireWrkSet(Button rdInclQnaireWrkSet) {
    this.rdInclQnaireWrkSet = rdInclQnaireWrkSet;
  }

  
  /**
   * @return the rdQnaireWrkSetOrBaseLine
   */
  public Button getRdQnaireWrkSetOrBaseLine() {
    return rdQnaireWrkSetOrBaseLine;
  }

  
  /**
   * @param rdQnaireWrkSetOrBaseLine the rdQnaireWrkSetOrBaseLine to set
   */
  public void setRdQnaireWrkSetOrBaseLine(Button rdQnaireWrkSetOrBaseLine) {
    this.rdQnaireWrkSetOrBaseLine = rdQnaireWrkSetOrBaseLine;
  }


}
