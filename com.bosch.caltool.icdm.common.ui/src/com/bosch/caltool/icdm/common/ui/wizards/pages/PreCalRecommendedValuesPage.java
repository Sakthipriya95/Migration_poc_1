/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.progress.DetailedProgressViewer;
import org.eclipse.ui.internal.progress.JobInfo;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.eclipse.ui.internal.progress.ProgressView;
import org.eclipse.ui.internal.progress.ProgressViewerContentProvider;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.a2l.CdfxExportParameter;
import com.bosch.caltool.icdm.client.bo.a2l.precal.PreCalDataWizardDataHandler;
import com.bosch.caltool.icdm.common.ui.jobs.PreCalDataRetrieveJob;
import com.bosch.caltool.icdm.common.ui.sorters.CdfxFilterTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.CdfxParamExportFilter;
import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author rgo7cob Icdm-703 last page of the wizard
 */
public class PreCalRecommendedValuesPage extends WizardPage {

  /**
   * Name column width
   */
  private static final int NAME_COL_WIDTH = 300;
  /**
   * Value col width
   */
  private static final int VAL_COL_WIDTH = 150;
  /**
   *
   */
  private static final int HEIGHT_HINT_1 = 185;
  /**
   * width hint
   */
  private static final int WIDTH_HINT_1 = 400;
  /**
   * Height hint
   */
  private static final int HEIGHT_HINT = 285;
  /**
   * Number of columns
   */
  private static final int NUM_COLS_1 = 2;
  /**
   * Width hint
   */
  private static final int WIDTH_HINT = 25;
  /**
   * CustomGridTableViewer instance
   */
  private CustomGridTableViewer recomValTabViewer;
  /**
   * Label instance
   */
  private Label totalParamlabel;
  /**
   * File Path
   */
  private Text filePathText;

  /**
   * Start time of job
   */
  private Long jobStartTime;

  /**
   * flag to denote whether the job got added
   */
  private boolean addedJob;
  /**
   * file selection
   */
  protected String fileSelect;
  /**
   * Browse button
   */
  private Button browseButton;
  /**
   * Filtered params label
   */
  private Label paramFilteredLabel;
  /**
   * count of params label
   */
  private Label paramCountlabel;
  /**
   * count label
   */
  private Label filCountlabel;
  /**
   * GridTableViewer instance
   */
  private GridTableViewer paramTabViewer;
  /**
   * Text instance
   */
  private Text filterText;
  /**
   * CdfxParamExportFilter instance
   */
  private CdfxParamExportFilter paramFilter;
  /**
   * AbstractViewerSorter instance
   */
  private AbstractViewerSorter paramTabSorter;
  /**
   * Label instance
   */
  private Label sourceTextlabel;
  // ICDM-900
  /**
   * Label instance
   */
  private Label paramLabel;
  /**
   * Label instance
   */
  private Label sysConstLabel;
  /**
   * Label instance
   */
  private Label filterParamLabel;
  /**
   * Label instance
   */
  private Label filterSysConstLabel;
  /**
   * DetailedProgressViewer instance
   */
  private DetailedProgressViewer viewer;
  /**
   * JobInfo instance
   */
  protected JobInfo jobInfo;
  // Sonar qube violation correction
  private static final int SHELL_WIDTH = 650;
  private static final int SHELL_HEIGHT = 800;
  private Button cdfxFileButton;
  private Button a2lFileButton;

  /**
   * @param pageName pageName
   * @param a2lFileName A2LFileName
   */
  public PreCalRecommendedValuesPage(final String pageName, final String a2lFileName) {
    super(pageName);
    setTitle("Get Pre-Calibration Data for : " + a2lFileName);
    setDescription("Please review the summary and click Finish to Export as CDFX file");
    setPageComplete(false);

  }


  /**
   * @return the paramList
   */
  public List<A2LParameter> getParamList() {
    return getDataHandler().getParamList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    final Composite myComp = new Composite(scrollComp, SWT.NONE);
    myComp.setLayout(new GridLayout());
    myComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    final FormToolkit toolkit = new FormToolkit(parent.getDisplay());

    createStatsComp(myComp, toolkit);

    createExportFileSection(myComp, toolkit);
    createTabViewer(myComp, toolkit);

    scrollComp.setContent(myComp);
    scrollComp.setMinSize(myComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);

  }


  /**
   * @param myComp Composite// icDM-857 CDFX export listing parameters
   * @param toolkit FormToolkit
   */
  @SuppressWarnings("restriction")
  private void createTabViewer(final Composite myComp, final FormToolkit toolkit) {

    final GridLayout gridLayout = new GridLayout();
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    // ICDM-880
    final Section sectionParamTab = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionParamTab.setText("Parameters ");
    sectionParamTab.setDescription("List of parameters selected for CDFX export");
    sectionParamTab.getDescriptionControl().setEnabled(false);
    sectionParamTab.setLayout(gridLayout);
    sectionParamTab.setLayoutData(gridData);

    final Composite inputParamComp = toolkit.createComposite(sectionParamTab);

    inputParamComp.setLayoutData(gridData);
    inputParamComp.setLayout(gridLayout);
    this.paramTabSorter = new CdfxFilterTabViewerSorter();
    this.filterText = new Text(inputParamComp, SWT.SINGLE | SWT.BORDER);

    createFilterTxt();
    this.paramTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(inputParamComp,
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    gridData.heightHint = HEIGHT_HINT;
    gridData.widthHint = WIDTH_HINT_1;
    this.paramTabViewer.getGrid().setLayoutData(gridData);


    this.paramTabViewer.getGrid().setLinesVisible(true);
    this.paramTabViewer.getGrid().setHeaderVisible(true);
    createColumns();
    addFilters();
    invokeColumnSorter();
    this.paramTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.paramTabViewer.setInput(null);

    setProgressViewerComparartor(inputParamComp);
    ScrolledComposite scrolledComposite = (ScrolledComposite) this.viewer.getControl();
    scrolledComposite.setAlwaysShowScrollBars(false);
    scrolledComposite.getVerticalBar().setEnabled(false);
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.verticalAlignment = GridData.FILL;
    gridData1.heightHint = HEIGHT_HINT_1;
    gridData1.widthHint = WIDTH_HINT_1;
    this.viewer.getControl().setLayoutData(gridData1);

    ProgressViewerContentProvider provider = new ProgressViewerContentProvider(this.viewer, true, true);
    this.viewer.setContentProvider(provider);
    this.viewer.setInput(ProgressManager.getInstance());

    JobInfo[] jobInfos = ProgressManager.getInstance().getJobInfos(false);
    if (jobInfos.length > 0) {
      List<JobInfo> jobInfosToRemove = new ArrayList<>();
      for (JobInfo jobInf : jobInfos) {
        if (jobInf.getJob() instanceof PreCalDataRetrieveJob) {
          PreCalDataRetrieveJob retrieveJob = (PreCalDataRetrieveJob) jobInf.getJob();
          if (!retrieveJob.getTimeInMillisecs().equals(PreCalRecommendedValuesPage.this.getJobStartTime())) {
            jobInfosToRemove.add(jobInf);
          }
        }
      }
      this.viewer.remove(jobInfosToRemove.toArray());
    }
    sectionParamTab.setClient(inputParamComp);

  }

  /**
   * @param inputParamComp
   */
  private void setProgressViewerComparartor(final Composite inputParamComp) {
    this.viewer = new DetailedProgressViewer(inputParamComp, SWT.MULTI | SWT.H_SCROLL) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void add(final Object[] elements) {
        for (Object object : elements) {
          if (object instanceof JobInfo) {
            JobInfo jobInf = (JobInfo) object;
            if (jobInf.getJob() instanceof PreCalDataRetrieveJob) {
              PreCalDataRetrieveJob retrieveJob = (PreCalDataRetrieveJob) jobInf.getJob();
              if (retrieveJob.getTimeInMillisecs().equals(PreCalRecommendedValuesPage.this.getJobStartTime())) {
                PreCalRecommendedValuesPage.this.jobInfo = jobInf;
                Object[] modJobs = new Object[] { object };
                unmapElement(object);
                if (!PreCalRecommendedValuesPage.this.addedJob) {
                  super.add(modJobs);
                  PreCalRecommendedValuesPage.this.addedJob = true;
                  break;
                }
              }
            }
          }

        }
      }

    };
    this.viewer.setComparator(getProgressViewerComparator());
  }

  static ViewerComparator getProgressViewerComparator() {
    return new ViewerComparator() {

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings({ "unchecked", "rawtypes" })
      @Override
      public int compare(final Viewer testViewer, final Object obj1, final Object obj2) {
        return ((Comparable) obj1).compareTo(obj2);
      }

    };
  }

  /**
   * Create table viewer columns
   */
  private void createColumns() {
    final GridViewerColumn nameColumn = new GridViewerColumn(this.paramTabViewer, SWT.NONE);
    nameColumn.getColumn().setText("Parameter Name");
    nameColumn.getColumn().setWidth(NAME_COL_WIDTH);
    nameColumn.getColumn().setResizeable(true);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        final CdfxExportParameter param = (CdfxExportParameter) element;
        if (param.isFiltered()) {
          return super.getForeground(element);
        }
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        final CdfxExportParameter param = (CdfxExportParameter) element;
        return param.getA2lparam().getName();
      }
    });

    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 0, this.paramTabSorter, this.paramTabViewer));

    final GridViewerColumn filteredColumn = new GridViewerColumn(this.paramTabViewer, SWT.NONE);
    filteredColumn.getColumn().setText("Value Available?");
    filteredColumn.getColumn().setWidth(VAL_COL_WIDTH);
    filteredColumn.getColumn().setResizeable(true);
    filteredColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        final CdfxExportParameter param = (CdfxExportParameter) element;
        if (param.isFiltered()) {
          return super.getForeground(element);
        }
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        final CdfxExportParameter param = (CdfxExportParameter) element;
        return param.isFilteredAsStr();
      }
    });

    filteredColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(filteredColumn.getColumn(), 1, this.paramTabSorter, this.paramTabViewer));
  }


  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.paramTabViewer.setComparator(this.paramTabSorter);
  }

  /**
   * Add filters for the table
   */
  private void addFilters() {
    this.paramFilter = new CdfxParamExportFilter(); // Add TableViewer filter
    this.paramTabViewer.addFilter(this.paramFilter);
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    final GridData gridData = getFilterTxtGridData();
    this.filterText.setLayoutData(gridData);
    this.filterText.setMessage("type filter text");
    this.filterText.addModifyListener(event -> {
      final String text = PreCalRecommendedValuesPage.this.filterText.getText().trim();
      PreCalRecommendedValuesPage.this.paramFilter.setFilterText(text);
      PreCalRecommendedValuesPage.this.paramTabViewer.refresh();
    });
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * @param myComp Composite
   * @param toolkit FormToolkit
   */
  private void createStatsComp(final Composite myComp, final FormToolkit toolkit) {
    final GridLayout gridLayout = new GridLayout();
    final Section sectionStatInfo = toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionStatInfo.setText("Statistical Information");
    sectionStatInfo.getDescriptionControl().setEnabled(false);
    sectionStatInfo.setLayout(gridLayout);
    sectionStatInfo.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite compStat = toolkit.createComposite(sectionStatInfo);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    gridData.grabExcessHorizontalSpace = true;
    compStat.setLayoutData(gridData);

    final GridLayout layout = new GridLayout();
    layout.numColumns = NUM_COLS_1;
    layout.makeColumnsEqualWidth = false;
    compStat.setLayout(layout);

    final Label sourceParamlabel = new Label(compStat, SWT.NONE);
    sourceParamlabel.setText("Source ");
    sourceParamlabel.setLayoutData(gridData);
    this.sourceTextlabel = new Label(compStat, SWT.NONE);
    this.sourceTextlabel.setLayoutData(gridData);
    sourceParamlabel.setLayoutData(gridData);
    this.totalParamlabel = new Label(compStat, SWT.NONE);
    this.totalParamlabel.setLayoutData(gridData);
    this.totalParamlabel.setText("Total number of parameters ");
    this.paramCountlabel = new Label(compStat, SWT.NONE);
    this.paramCountlabel.setLayoutData(gridData);
    this.paramCountlabel.setText(Integer.toString(getParamList().size()));

    this.filterParamLabel = new Label(compStat, SWT.NONE);
    this.filterParamLabel.setLayoutData(gridData);

    this.paramLabel = new Label(compStat, SWT.NONE);
    this.paramLabel.setLayoutData(gridData);

    this.filterSysConstLabel = new Label(compStat, SWT.NONE);
    this.filterSysConstLabel.setLayoutData(gridData);


    this.sysConstLabel = new Label(compStat, SWT.NONE);
    this.sysConstLabel.setLayoutData(gridData);
    this.paramFilteredLabel = new Label(compStat, SWT.NONE);
    this.paramFilteredLabel.setText("Number of parameter Filtered and available for export ");
    this.filCountlabel = new Label(compStat, SWT.NONE);
    this.filCountlabel.setLayoutData(gridData);
    int filParamSize = getDataHandler().getCalDataMap().size();
    this.filCountlabel.setText(String.valueOf(filParamSize));

    sectionStatInfo.setClient(compStat);
  }

  /**
   * @param myComp Composite
   * @param toolkit FormToolkit
   */
  private void createExportFileSection(final Composite myComp, final FormToolkit toolkit) {

    final GridLayout gridLayout = new GridLayout();
    final Section sectionFileExport =
        toolkit.createSection(myComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionFileExport.setText("CDF file Export");
    sectionFileExport.getDescriptionControl().setEnabled(false);
    sectionFileExport.setLayout(gridLayout);
    sectionFileExport.setLayoutData(GridDataUtil.getInstance().getGridData());

    final Composite compExport = toolkit.createComposite(sectionFileExport);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    gridData.grabExcessHorizontalSpace = false;
    compExport.setLayoutData(gridData);
    compExport.setLayout(new GridLayout());
    initializeDialogUnits(compExport);
    final Composite projectGroup = new Composite(compExport, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.makeColumnsEqualWidth = true;
    layout.marginWidth = 0;

    projectGroup.setLayout(layout);
    final GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
    gridData2.grabExcessVerticalSpace = false;
    gridData.grabExcessHorizontalSpace = false;
    projectGroup.setLayoutData(GridDataUtil.getInstance().getGridData());
    setControl(projectGroup);

    final Composite firstRowComposite = new Composite(projectGroup, SWT.NONE);

    firstRowComposite.setLayout(new GridLayout(3, true));
    firstRowComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


    new Label(firstRowComposite, SWT.NONE).setText("Export To : ");
    this.a2lFileButton = new Button(firstRowComposite, SWT.RADIO);
    this.a2lFileButton.setText("A2L File");
    this.a2lFileButton.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        PreCalRecommendedValuesPage.this.filePathText.setEnabled(false);
        PreCalRecommendedValuesPage.this.browseButton.setEnabled(false);
        getDataHandler().setLoadToA2lSelected(true);
      }
    });

    this.cdfxFileButton = new Button(firstRowComposite, SWT.RADIO);
    this.cdfxFileButton.setText("CDFX File");
    this.cdfxFileButton.setSelection(true);
    this.cdfxFileButton.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        PreCalRecommendedValuesPage.this.filePathText.setEnabled(true);
        PreCalRecommendedValuesPage.this.browseButton.setEnabled(true);
        getDataHandler().setLoadToA2lSelected(false);
      }
    });


    final Composite secondRowComposite = new Composite(projectGroup, SWT.NONE);

    secondRowComposite.setLayout(new GridLayout(3, false));
    secondRowComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    new Label(secondRowComposite, SWT.NONE).setText("File Name : ");
    this.filePathText = new Text(secondRowComposite, SWT.BORDER);
    final GridData directoryPathData = new GridData(SWT.FILL, SWT.NONE, true, false);
    directoryPathData.widthHint = new PixelConverter(this.filePathText).convertWidthInCharsToPixels(WIDTH_HINT);
    this.filePathText.setLayoutData(directoryPathData);
    this.filePathText.addModifyListener(event -> {
      if (PreCalRecommendedValuesPage.this.filePathText.getText().isEmpty()) {
        PreCalRecommendedValuesPage.this.setPageComplete(false);
      }
      else {
        PreCalRecommendedValuesPage.this.setErrorMessage(null);
        PreCalRecommendedValuesPage.this.setPageComplete(true);
      }
    });

    this.browseButton = new Button(secondRowComposite, SWT.PUSH);
    this.browseButton.setText("Browse");
    this.browseButton.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        String fileName = "CDF_Export_Report";
        fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "_");
        final FileDialog saveFileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        saveFileDialog.setText("Save");
        final String[] filterExt = { "*.cdfx" };
        saveFileDialog.setFilterExtensions(filterExt);
        saveFileDialog.setFilterIndex(0);
        saveFileDialog.setFileName(fileName);
        saveFileDialog.setOverwrite(true);
        PreCalRecommendedValuesPage.this.fileSelect = saveFileDialog.open();

        if (PreCalRecommendedValuesPage.this.fileSelect != null) {
          PreCalRecommendedValuesPage.this.filePathText.setText(PreCalRecommendedValuesPage.this.fileSelect);
          if (FileIOUtil.checkIfFileIsLocked(PreCalRecommendedValuesPage.this.fileSelect)) {
            setFileUsedErrorMessage();
          }
          else if (!getDataHandler().getCalDataMap().isEmpty()) {
            setErrorMessage(null);
            setPageComplete(true);
          }
        }

      }
    });
    setButtonLayoutData(this.browseButton);

    sectionFileExport.setClient(compExport);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {

    return false;
  }


  /**
   * @param seriesStatSrc seriesStatSrc
   * @param shell shell instance
   */
  public void setInput(final boolean seriesStatSrc, final Shell shell) {
    Display.getDefault().asyncExec(() -> {
      fillStatData(seriesStatSrc);
      fillNotFilteredList(seriesStatSrc);
      setFinishStatus();

      refreshJobUI();
      getContainer().updateButtons();
      // ICdm-976 make the Shell Visible
      shell.setVisible(true);
      shell.setMaximized(true);
      shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
    });


  }

  /**
   * @param seriesStatSrc seriesStatSrc // icDM-857 CDFX export listing parameters
   */
  protected void fillNotFilteredList(final boolean seriesStatSrc) {
    final Set<CdfxExportParameter> expParamSet = new TreeSet<>();

    Map<String, CalData> calDataMap = getDataHandler().getCalDataMap();

    for (A2LParameter param : getParamList()) {
      final CdfxExportParameter exportParam = new CdfxExportParameter();
      exportParam.setA2lparam(param);

      exportParam.setFiltered(calDataMap.get(param.getName()) != null);

      expParamSet.add(exportParam);
    }
    if (seriesStatSrc) {
      this.sourceTextlabel.setText(": Series Statistics");
    }
    else {
      this.sourceTextlabel.setText(": Calibration Data Review");
    }
    this.paramTabViewer.setInput(expParamSet);
  }

  /**
   * Add the next pressed
   */
  public void nextPressed() {
    // Do Nothing
  }


  /**
   * @return the file name selected
   */
  public String getFileSelect() {
    return this.filePathText.getText();
  }


  /**
   * Based on the conditions set the status of the finish button
   */
  private void setFinishStatus() {

    Map<String, CalData> calDataMap = getDataHandler().getCalDataMap();

    if (calDataMap.isEmpty()) {
      PreCalRecommendedValuesPage.this.browseButton.setEnabled(false);
      PreCalRecommendedValuesPage.this.filePathText.setEditable(false);
      this.a2lFileButton.setEnabled(false);
      this.a2lFileButton.setSelection(false);
      this.cdfxFileButton.setEnabled(false);
      this.cdfxFileButton.setSelection(false);
    }
    else {
      this.a2lFileButton.setEnabled(true);
      this.a2lFileButton.setSelection(false);
      this.cdfxFileButton.setEnabled(true);
      this.cdfxFileButton.setSelection(true);
      PreCalRecommendedValuesPage.this.browseButton.setEnabled(true);
      PreCalRecommendedValuesPage.this.filePathText.setEditable(true);
      if ((PreCalRecommendedValuesPage.this.filePathText.getText() == null) ||
          PreCalRecommendedValuesPage.this.filePathText.getText().isEmpty()) {
        PreCalRecommendedValuesPage.this.filePathText.setText(System.getProperty("user.home") + "\\CDF_Export.cdfx");
      }
      if (FileIOUtil.checkIfFileIsLocked(PreCalRecommendedValuesPage.this.filePathText.getText())) {
        setFileUsedErrorMessage();
      }
      else {
        setErrorMessage(null);
        setPageComplete(true);
      }

    }
  }

  /**
   * Fill the statistical Data
   *
   * @param seriesStatSrc seriesStatSrc
   */
  private void fillStatData(final boolean seriesStatSrc) {
    this.totalParamlabel.setText("Total number of parameters ");
    this.paramCountlabel.setText(": " + Integer.toString(getParamList().size()));
    this.filterParamLabel.setText("Number of parameters added in filter criteria ");
    this.filterSysConstLabel.setText("Number of system constants added in filter criteria ");
    // ICDM-900
    if (seriesStatSrc) {
      IWizardPage page = getWizard().getPreviousPage(this);
      if (page instanceof PreCalSeriesFltrSltnPage) {
        PreCalSeriesFltrSltnPage seriesPg = (PreCalSeriesFltrSltnPage) page;

        this.paramLabel.setText(": " + seriesPg.getParams().size());
        this.sysConstLabel.setText(": " + seriesPg.getSysConts().size());
      }
    }
    else {
      this.paramLabel.setText(": 0");
      this.sysConstLabel.setText(": 0");
    }
    this.paramFilteredLabel.setText("Number of parameter filtered and available for export ");
    this.filCountlabel.setText(": " + Integer.toString(getDataHandler().getCalDataMap().size()));
  }

  /**
   * @return the jobStartTime
   */
  public Long getJobStartTime() {
    return this.jobStartTime;
  }


  /**
   * @param jobStartTime the jobStartTime to set
   */
  public void setJobStartTime(final Long jobStartTime) {
    this.jobStartTime = jobStartTime;
  }


  /**
   * @return the addedJob
   */
  public boolean isAddedJob() {
    return this.addedJob;
  }


  /**
   * @param addedJob the addedJob to set
   */
  public void setAddedJob(final boolean addedJob) {
    this.addedJob = addedJob;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IWizardPage getPreviousPage() {
    return this.addedJob ? super.getPreviousPage() : null;
  }


  /**
   * Refresh UI
   */
  public void refreshJobUI() {
    if (PreCalRecommendedValuesPage.this.jobInfo != null) {
      PreCalRecommendedValuesPage.this.viewer.update(PreCalRecommendedValuesPage.this.jobInfo, null);
      ProgressView progressView = (ProgressView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("org.eclipse.ui.views.ProgressView");
      if (progressView != null) {
        progressView.getViewer().update(PreCalRecommendedValuesPage.this.jobInfo, null);
      }
    }
  }


  /**
   * Method which enables BrowseButton and FilePathText This method is called on back pressed of the this page
   */
  public void enableExportUI() {
    this.browseButton.setEnabled(true);
    this.filePathText.setEditable(true);
    this.filePathText.setText("");
  }

  /**
   * @return the recomValTableViewer
   */
  public CustomGridTableViewer getRecomValTableViewer() {
    return this.recomValTabViewer;
  }

  /**
   * iCDM-1455 <br>
   * Set appropriate error message
   */
  private void setFileUsedErrorMessage() {
    setErrorMessage("File is used by another process. Please provide a different file name to export.");
    setPageComplete(false);
  }

  @Override
  public PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

  private PreCalDataWizardDataHandler getDataHandler() {
    return getWizard().getDataHandler();
  }

}
