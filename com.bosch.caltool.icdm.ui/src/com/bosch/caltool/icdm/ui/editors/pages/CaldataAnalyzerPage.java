/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultModel;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultSummary;
import com.bosch.caltool.icdm.ui.action.CaldataAnalyzerEditorActionSet;
import com.bosch.caltool.icdm.ui.sorters.CaldataAnalyzerFilterSorter;
import com.bosch.caltool.icdm.ui.views.providers.CaldatAnalyzerFilesTabContentProvider;
import com.bosch.caltool.icdm.ui.wizards.CaldataAnalyzerWizard;
import com.bosch.caltool.icdm.ui.wizards.CaldataAnalyzerWizardDialog;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author pdh2cob
 */
public class CaldataAnalyzerPage extends AbstractFormPage {

  /**
   * Toolbar manager
   */
  private ToolBarManager toolBarManager;

  private ToolBarManager fileToolbarManager;

  private List<String> outputDirectoryList = new ArrayList<>();

  /**
   * Form
   */
  private Form topForm;

  private Form filesForm;

  /**
   * Non scrollable form
   */
  private Form detailsForm;

  private AbstractViewerSorter sorter;
  /**
   * Composite instance for base layout
   */
  private SashForm mainComposite;

  private Section resultSection;

  private Form resultForm;

  /**
   * Section instance
   */
  private Section topSection;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  private Button newAnalysisBtn;

  private Button restartAnalysisBtn;


  /**
   * Filter model for all filters
   */
  private CaldataAnalyzerFilterModel caldataAnalyzerFilterModel = new CaldataAnalyzerFilterModel();

  /**
   * Section for showing statistics based on caldata analysis
   */
  private Section detailsSection;

  private Section filesSection;

  private Form nonScrollableForm;

  /**
   * GridTableViewer instance
   */
  private GridTableViewer fileTabViewer;

  private CaldataAnalyzerResultModel resultModel = new CaldataAnalyzerResultModel();

  private CaldataAnalyzerEditorActionSet actionSet = new CaldataAnalyzerEditorActionSet(this);

  private List<CaldataAnalyzerResultFileModel> selectedResultFiles = new ArrayList<>();

  private static final int FILE_NAME_COLUMN_IDX = 0;

  private static final int FILE_SIZE_COLUMN_IDX = 1;

  private StyledText lblSummaryLeft;

  private StyledText lblSummaryRight;


  /**
   * @param editor - editor instance
   * @param title - Page title
   */
  public CaldataAnalyzerPage(final FormEditor editor, final String title) {
    super(editor, title, title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText("Calibration Data Analysis");
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;

    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.VERTICAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    // create composite
    createComposite();
  }


  private void createToolbarActions() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.topSection);
    this.actionSet = new CaldataAnalyzerEditorActionSet(this);
    this.actionSet.saveFilterAction();
    this.actionSet.loadFilterAction();
    this.toolBarManager.add(new Separator());
    this.actionSet.resetEditorAction();
    this.toolBarManager.update(true);
    this.topSection.setTextClient(toolbar);
  }


  private void createFileToolbarActions() {
    this.fileToolbarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.fileToolbarManager.createControl(this.filesSection);
    this.actionSet.saveFilesAction();
    this.fileToolbarManager.update(true);
    this.filesSection.setTextClient(toolbar);

  }

  /**
   * Method to start caldata analyzer wizard
   *
   * @param isNewAnalysis - to check if it is a new analysis
   */
  public void startAnalysisWizard(final boolean isNewAnalysis) {

    Display.getDefault().syncExec(() -> {
      CaldataAnalyzerPage.this.newAnalysisBtn.setEnabled(false);
      CaldataAnalyzerPage.this.restartAnalysisBtn.setEnabled(false);
      CaldataAnalyzerWizard wizard = new CaldataAnalyzerWizard(CaldataAnalyzerPage.this, isNewAnalysis);

      final IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      final Shell parent = activeWindow.getShell();
      CaldataAnalyzerWizardDialog dialog = new CaldataAnalyzerWizardDialog(parent, wizard);
      dialog.open();
    });

  }


  /**
   * Add search button
   */
  private void addAnalyzeBtn() {

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.topForm.getBody().setLayout(gridLayout);
    this.topForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.newAnalysisBtn = new Button(this.topForm.getBody(), SWT.PUSH);
    this.newAnalysisBtn.setText("New Analysis");
    this.newAnalysisBtn.setToolTipText("New Analysis");

    this.newAnalysisBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        startAnalysisWizard(true);
      }
    });

    this.restartAnalysisBtn = new Button(this.topForm.getBody(), SWT.PUSH);
    this.restartAnalysisBtn.setText("Restart Analysis");
    this.restartAnalysisBtn.setToolTipText("Restart Analysis with inputs from previous analysis");

    this.restartAnalysisBtn.setEnabled(false);

    this.restartAnalysisBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        startAnalysisWizard(false);

      }
    });


  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final SashForm compositeTwo = new SashForm(this.mainComposite, SWT.VERTICAL);
    createTopSection(compositeTwo);
    createResultsSection(compositeTwo);

    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = 1;
    compositeTwo.setLayout(gridLayout1);
    compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());
    compositeTwo.setWeights(new int[] { 1, 4 });

    final SashForm compositeThree = new SashForm(this.resultForm.getBody(), SWT.VERTICAL);
    createDetailsSection(compositeThree);
    createFilesSection(compositeThree);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    compositeThree.setLayout(gridLayout1);
    compositeThree.setLayoutData(GridDataUtil.getInstance().getGridData());
    compositeThree.setWeights(new int[] { 2, 3 });
  }


  private void createFilesSection(final Composite composite) {

    this.filesSection = this.formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.filesSection.setText("Output Files");
    this.filesSection.setLayout(new GridLayout());
    this.filesSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.filesSection.getDescriptionControl().setEnabled(false);

    createFileToolbarActions();

    createFilesForm();

    this.filesSection.setClient(this.filesForm);


  }


  private void createResultsSection(final Composite composite) {
    this.resultSection = this.formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.resultSection.setText("Analysis Result");
    this.resultSection.setLayout(new GridLayout());
    this.resultSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.resultSection.getDescriptionControl().setEnabled(false);
    createResultForm();
    this.resultSection.setClient(this.resultForm);

  }

  private void createResultForm() {

    this.resultForm = this.formToolkit.createForm(this.resultSection);
    this.resultForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.resultForm.getBody().setLayout(new GridLayout());


  }

  /**
   * This method initializes section
   */
  private void createTopSection(final Composite composite) {
    this.topSection = this.formToolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.topSection.setText("Analysis");
    this.topSection.setDescription("Click \"New Analysis\" for new calibration data analysis.");
    this.topSection.setLayout(new GridLayout());
    this.topSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.topSection.getDescriptionControl().setEnabled(false);

    createToolbarActions();

    createTopForm();
    addAnalyzeBtn();

    this.topSection.setClient(this.topForm);
  }

  /**
   * Creates the form.
   */
  private void createTopForm() {
    this.topForm = this.formToolkit.createForm(this.topSection);
    this.topForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.topForm.getBody().setLayout(new GridLayout());

  }

  private void createFilesForm() {

    this.filesForm = this.formToolkit.createForm(this.filesSection);
    GridLayout overallLayout = new GridLayout();
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.filesForm.getBody().setLayoutData(gridData);
    this.filesForm.getBody().setLayout(overallLayout);


    this.filesSection.setLayoutData(gridData);


    this.fileTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.filesForm.getBody(),
        SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gridData);

    this.fileTabViewer.getGrid().setLayoutData(gridData);

    // add sorting
    this.sorter = new CaldataAnalyzerFilterSorter();
    this.fileTabViewer.setComparator(this.sorter);

    createFileColumn();
    createSizeColumn();


    this.filesForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.filesForm.getBody().setLayout(overallLayout);


    // add data to table
    this.fileTabViewer.setContentProvider(new CaldatAnalyzerFilesTabContentProvider());

    // enable tooltip
    ColumnViewerToolTipSupport.enableFor(this.fileTabViewer);


    // add toolbar actions

    createFileToolbarActions();

    // add double click action
    this.fileTabViewer.addDoubleClickListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      CaldataAnalyzerResultFileModel resultFile = (CaldataAnalyzerResultFileModel) selection.getFirstElement();
      CommonUiUtils.openFile(resultFile.getFilePath());
    });


    this.fileTabViewer.addSelectionChangedListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      List<CaldataAnalyzerResultFileModel> resultFiles = selection.toList();
      setSelectedResultFiles(resultFiles);
      CaldataAnalyzerPage.this.actionSet.getSaveFilesAction().setEnabled(true);
    });


  }


  private void createDetailsSection(final Composite composite) {
    this.detailsSection = this.formToolkit.createSection(composite,
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.CLIENT_INDENT);
    this.detailsSection.setText("Summary");
    GridLayout overallLayout = new GridLayout();
    this.detailsSection.setLayout(overallLayout);
    this.detailsSection.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.detailsSection.getDescriptionControl().setEnabled(false);
    createDetailsForm();
    this.detailsSection.setClient(this.detailsForm);
  }

  /**
   * Method to refresh summary labels
   */
  public void refreshSummary() {
    this.lblSummaryLeft.setText(getSummaryColumn1());
    this.lblSummaryLeft.redraw();
    this.lblSummaryRight.setText(getSummaryColumn2());
    this.lblSummaryRight.redraw();
  }


  /**
   * Method to set all ui elements to default state
   */
  public void setToDefaultState() {
    this.caldataAnalyzerFilterModel = new CaldataAnalyzerFilterModel();
    this.resultModel = new CaldataAnalyzerResultModel();
    this.fileTabViewer.setInput(new ArrayList<CaldataAnalyzerResultFileModel>());
    this.fileTabViewer.refresh();
    refreshSummary();
    this.newAnalysisBtn.setEnabled(true);
    this.restartAnalysisBtn.setEnabled(false);
    this.actionSet.getSaveFilesAction().setEnabled(false);
    this.actionSet.getSaveFilterAction().setEnabled(false);
    this.toolBarManager.update(true);
    this.fileToolbarManager.update(true);
  }

  private void createSummaryLabel() {

    GridData gridData = GridDataUtil.getInstance().getTextGridData();
    this.lblSummaryLeft = new StyledText(this.detailsForm.getBody(), SWT.NONE);
    this.lblSummaryLeft.setLayoutData(gridData);
    this.lblSummaryLeft.setText(getSummaryColumn1());
    this.lblSummaryLeft.setEditable(false);

    this.lblSummaryRight = new StyledText(this.detailsForm.getBody(), SWT.NONE);
    this.lblSummaryRight.setLayoutData(gridData);
    this.lblSummaryRight.setText(getSummaryColumn2());
    this.lblSummaryRight.setEditable(false);

  }


  /**
   * @return summary column 1 contents
   */
  private String getSummaryColumn1() {
    StringBuilder summaryStringBuilder = new StringBuilder();
    summaryStringBuilder.append("Parameters analyzed\t\t\t\t\t  : ")
        .append(this.resultModel.getSummaryModel().getLabelsAnalyzedCount()).append("\n");
    summaryStringBuilder.append("Number of Parameter filters\t\t  : ")
        .append(this.resultModel.getSummaryModel().getParamFilterItemsCount()).append("\n");
    summaryStringBuilder.append("Number of Function filters\t\t\t  : ")
        .append(this.resultModel.getSummaryModel().getFunctionFilterItemsCount());
    return summaryStringBuilder.toString();
  }


  private String getSummaryColumn2() {
    StringBuilder summaryStringBuilder = new StringBuilder();
    summaryStringBuilder.append("Number of System Constant filters \t : ")
        .append(this.resultModel.getSummaryModel().getSysconFilterItemsCount()).append("\n");
    summaryStringBuilder.append("Number of Platform filters \t\t\t\t\t : ")
        .append(this.resultModel.getSummaryModel().getPlatformFilterItemsCount()).append("\n");
    summaryStringBuilder.append("Number of Customer filters \t\t\t\t : ")
        .append(this.resultModel.getSummaryModel().getCustomerFilterItemsCount());
    return summaryStringBuilder.toString();
  }


  /**
   * This method initializes scrolledForm
   */
  private void createDetailsForm() {
    this.detailsForm = this.formToolkit.createForm(this.detailsSection);
    GridLayout overallLayout = new GridLayout();
    overallLayout.numColumns = 2;
    this.detailsForm.getBody().setLayout(overallLayout);

    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    this.detailsSection.setLayoutData(gridData);

    createSummaryLabel();

  }

  /**
   * Size column
   */
  private void createSizeColumn() {
    final GridViewerColumn sizeColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fileTabViewer, "Size (in KB)", 445);
    sizeColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        CaldataAnalyzerResultFileModel file = (CaldataAnalyzerResultFileModel) element;
        return String.valueOf(file.getFileSize());
      }

    });
    // Add column selection listener
    sizeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(sizeColumn.getColumn(), FILE_SIZE_COLUMN_IDX, this.sorter, this.fileTabViewer));
  }


  /**
   * create file column
   */
  private void createFileColumn() {
    final GridViewerColumn fileColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fileTabViewer, "Output File", 445);

    fileColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        CaldataAnalyzerResultFileModel file = (CaldataAnalyzerResultFileModel) element;
        return file.getFileName();
      }

      @Override
      public String getToolTipText(final Object element) {
        return "Double-click to open file";
      }

    });

    // Add column selection listener
    fileColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(fileColumn.getColumn(), FILE_NAME_COLUMN_IDX, this.sorter, this.fileTabViewer));

  }

  /**
   * Method to reset summary model
   */
  public void resetSummaryModel() {
    this.resultModel.setSummaryModel(new CaldataAnalyzerResultSummary());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setActive(final boolean active) {
    super.setActive(active);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ToolBarManager getToolBarManager() {
    return this.toolBarManager;
  }


  /**
   * @return the resultModel
   */
  public CaldataAnalyzerResultModel getResultModel() {
    return this.resultModel;
  }


  /**
   * @param resultModel the resultModel to set
   */
  public void setResultModel(final CaldataAnalyzerResultModel resultModel) {
    this.resultModel = resultModel;
  }


  /**
   * @return the fileTabViewer
   */
  public GridTableViewer getFileTabViewer() {
    return this.fileTabViewer;
  }


  /**
   * @return the selectedResultFiles
   */
  public List<CaldataAnalyzerResultFileModel> getSelectedResultFiles() {
    return new ArrayList<>(this.selectedResultFiles);
  }


  /**
   * @param selectedResultFiles the selectedResultFiles to set
   */
  public void setSelectedResultFiles(final List<CaldataAnalyzerResultFileModel> selectedResultFiles) {
    this.selectedResultFiles = selectedResultFiles;
  }

  /**
   * @return the caldataAnalyzerFilterModel
   */
  public CaldataAnalyzerFilterModel getCaldataAnalyzerFilterModel() {
    return this.caldataAnalyzerFilterModel;
  }


  /**
   * @param caldataAnalyzerFilterModel the caldataAnalyzerFilterModel to set
   */
  public void setCaldataAnalyzerFilterModel(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel) {
    this.caldataAnalyzerFilterModel = caldataAnalyzerFilterModel;
  }


  /**
   * @return the button2
   */
  public Button getButton2() {
    return this.restartAnalysisBtn;
  }


  /**
   * @param button2 the button2 to set
   */
  public void setButton2(final Button button2) {
    this.restartAnalysisBtn = button2;
  }


  /**
   * @param toolBarManager the toolBarManager to set
   */
  public void setToolBarManager(final ToolBarManager toolBarManager) {
    this.toolBarManager = toolBarManager;
  }


  /**
   * @return the actionSet
   */
  public CaldataAnalyzerEditorActionSet getActionSet() {
    return this.actionSet;
  }

  /**
   * @return the button1
   */
  public Button getButton1() {
    return this.newAnalysisBtn;
  }


  /**
   * @param button1 the button1 to set
   */
  public void setButton1(final Button button1) {
    this.newAnalysisBtn = button1;
  }


  /**
   * @return the fileToolbarManager
   */
  public ToolBarManager getFileToolbarManager() {
    return this.fileToolbarManager;
  }


  /**
   * @param fileToolbarManager the fileToolbarManager to set
   */
  public void setFileToolbarManager(final ToolBarManager fileToolbarManager) {
    this.fileToolbarManager = fileToolbarManager;
  }


  /**
   * @return the summaryLabel1
   */
  public StyledText getSummaryLabel1() {
    return this.lblSummaryLeft;
  }


  /**
   * @param summaryLabel1 the summaryLabel1 to set
   */
  public void setSummaryLabel1(final StyledText summaryLabel1) {
    this.lblSummaryLeft = summaryLabel1;
  }


  /**
   * @return the summaryLabel2
   */
  public StyledText getSummaryLabel2() {
    return this.lblSummaryRight;
  }


  /**
   * @param summaryLabel2 the summaryLabel2 to set
   */
  public void setSummaryLabel2(final StyledText summaryLabel2) {
    this.lblSummaryRight = summaryLabel2;
  }


  /**
   * @return the outputDirectoryList
   */
  public List<String> getOutputDirectoryList() {
    return this.outputDirectoryList;
  }


  /**
   * @param outputDirectoryList the outputDirectoryList to set
   */
  public void setOutputDirectoryList(final List<String> outputDirectoryList) {
    this.outputDirectoryList = outputDirectoryList;
  }


}
