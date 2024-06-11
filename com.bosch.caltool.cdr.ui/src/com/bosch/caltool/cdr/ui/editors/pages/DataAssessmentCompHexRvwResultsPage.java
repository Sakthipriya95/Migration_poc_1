/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.externallink.creation.LinkCreator;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CalibrationStatusListExportAction;
import com.bosch.caltool.cdr.ui.actions.CompHexCheckSSDReportAction;
import com.bosch.caltool.cdr.ui.actions.CompHexRevExportPdfAction;
import com.bosch.caltool.cdr.ui.actions.CompHexZipDownloadAction;
import com.bosch.caltool.cdr.ui.actions.DaCompHexNatActionSet;
import com.bosch.caltool.cdr.ui.actions.DaCompHexToolbarActionSet;
import com.bosch.caltool.cdr.ui.actions.ShowRelatedQnaireAction;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditorInput;
import com.bosch.caltool.cdr.ui.editors.natcolumnfilter.DaCompHexAllColMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DaCompHEXEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DaCompHEXLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.DaCompHexNatToolTip;
import com.bosch.caltool.cdr.ui.table.filters.DaCompHexToolBarFilter;
import com.bosch.caltool.cdr.ui.views.providers.DaCompHexNatInputToColConverter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarRvwDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.COMPLI_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.RESULT_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.QSSDResValues;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comphex.CompHexStatistics;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentCompareHexData;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author ajk2cob
 */
public class DataAssessmentCompHexRvwResultsPage extends AbstractNatFormPage implements ISelectionListener {

  /**
   * Questionnaire status column name
   */
  private static final String COL_QUESTIONNIARE_STATUS = "Questionniare\nStatus";

  private static final Integer PTYPE_COL_WIDTH = 80;

  private static final Integer PARAMETER_COLUMN_WIDTH = 200;

  private static final Integer FUNC_COLUMN_WIDTH = 100;

  private static final Integer TICK_COLUMN_WIDTH = 100;

  private static final Integer CAL_DATA_COL_WIDTH = 100;

  /**
   * TEXT & space fillers
   */
  private static final String TXT_FILLER = "  ";
  private static final String LABEL_FILLER = "        ";
  private static final String COMPLI_FAILED = "Compli parameter failed";

  /**
   * column count
   */
  private static final int COLUMN_COUNT = 18;

  /**
   * CONSTANT
   */
  private static final Integer RESP_VERSIONS_COLUMN = 100;
  /**
   * custom comparator label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  /**
   * Editor Instance
   */
  private final DataAssessmentReportEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * SashForm
   */
  private SashForm mainComposite;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * ScrolledComposite
   */
  private ScrolledComposite scrollComp;
  /**
   * Section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * Statistic Form
   */
  private Form formStatistics;
  /**
   * statComp
   **/
  private Composite statComp;
  /**
   * qnaireStatComp
   */
  private Composite qnaireStatComp;
  /**
   * type filter
   */
  private Text filterTxt;
  /**
   * column name map
   */
  private HashMap<Integer, String> propertyToLabelMap;
  /**
   * row count
   */
  private int totTableRowCount;
  /**
   * CustomNATTable
   */
  private CustomNATTable natTable;
  /**
   * CustomFilterGridLayer
   */
  private CustomFilterGridLayer<DaCompareHexParam> compRprtFilterGridLayer;

  /**
   * all column filter matcher
   */
  private DaCompHexAllColMatcher<DaCompareHexParam> allColumnFilterMatcher;

  /**
   * Row selection provider of DaCompareHexParam
   */
  private RowSelectionProvider<DaCompareHexParam> selectionProvider;
  private Label filterParamStatLbl;
  private Label filterRvwdNotEqlStatLbl;
  private Label filterParamRvwdStatLbl;
  private final DataAssmntReportDataHandler dataAssmntReportDataHandler;

  /** The Constant COL_NM_COMPLI. */
  private static final String COL_NM_COMPLI_TYPE = "Compliance";


  /** The Constant COL_NM_RESULT. */
  private static final String COL_NM_RESULT = "Equal";

  /** The Constant COL_REVIEW_STATUS. */
  private static final String COL_NM_REVW_STATUS = "Review";

  /** The Constant COL_NM_TYPE. */
  private static final String COL_NM_TYPE = "Type";

  /** The Constant COL_NM_RESP. */
  private static final String COL_NAME_RESP = "Responsible";
  /** The Constant for the column Responsibility Type. */
  private static final String COL_NAME_RESP_TYPE = "Responsible Type";
  /**
   * column width
   */
  private static final int LATEST_QNAIRE_COL_WIDTH = 100;


  private boolean resetState;
  private ToolBarManager toolBarManager;

  private final CommonActionSet actionSet = new CommonActionSet();
  private final PIDCActionSet pidcActionSet = new PIDCActionSet();

  private DaCompHexToolBarFilter toolBarFilters;

  /**
   * Data assessment compare HEX and review results page, Constructor.
   *
   * @param editor editor
   */
  public DataAssessmentCompHexRvwResultsPage(final FormEditor editor) {
    super(editor, "dataAssessmentCompHexRvwResults", "Compare HEX and Review Results");
    this.editor = (DataAssessmentReportEditor) editor;
    this.dataAssmntReportDataHandler =
        ((DataAssessmentReportEditorInput) editor.getEditorInput()).getDataAssmntReportDataHandler();
  }

  @Override
  public void createPartControl(final Composite parent) {

    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(this.editor.getEditorInput().getName());

    if (this.dataAssmntReportDataHandler.getDataAssessmentReport().getConsiderRvwsOfPrevPidcVers()) {
      this.nonScrollableForm
          .setText(this.editor.getEditorInput().getName() + " \nConsidered Reviews of previous PIDC Versions");
    }
    else {
      this.nonScrollableForm
          .setText(this.editor.getEditorInput().getName() + " \nNot Considered Reviews of previous PIDC Versions");
    }

    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());

    final GridLayout gridLayout = new GridLayout();
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    // create the main composite
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * creating the content of the form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // initiate the form tool kit
    this.formToolkit = managedForm.getToolkit();

    createComposite();
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   *
   */
  private void createComposite() {
    // create scroll composite in the right side
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    final Composite compositeTwo = new Composite(this.scrollComp, SWT.NONE);

    createSection(compositeTwo);

    compositeTwo.setLayout(new GridLayout());
    compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());


    this.scrollComp.setContent(compositeTwo);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setDragDetect(true);
    // create the control listener for scrolling
    this.scrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        Rectangle rect = DataAssessmentCompHexRvwResultsPage.this.scrollComp.getClientArea();
        DataAssessmentCompHexRvwResultsPage.this.scrollComp
            .setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * @param compositeTwo
   */
  private void createSection(final Composite compositeTwo) {
    this.section = this.formToolkit.createSection(compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Comparsion Results HEX vs Latest Review");
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    createForm();
    this.section.setClient(this.form);

  }

  /**
   * @param toolkit
   */
  private void createStatisticsInfoControl() {
    Section sectionStatistics = this.formToolkit.createSection(this.form.getBody(),
        ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
    sectionStatistics.setLayout(new GridLayout());
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    sectionStatistics.setLayoutData(gridData);
    sectionStatistics.setText("Compare Hex Statistics");
    sectionStatistics.setExpanded(true);
    sectionStatistics.setVisible(true);

    this.formStatistics = this.formToolkit.createForm(sectionStatistics);
    this.formStatistics.getBody().setLayoutData(gridData);
    this.formStatistics.getBody().setLayout(new GridLayout());

    this.statComp = createStatCountComp(gridData);

    this.qnaireStatComp = createQnaireStatCountComp();

    sectionStatistics.setClient(this.formStatistics);
  }

  /**
   * @param sectionRight
   */
  private void createForm() {
    this.form = this.formToolkit.createForm(this.section);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.form.getBody().setLayoutData(gridData);
    this.form.getBody().setLayout(new GridLayout());

    createStatisticsInfoControl();
    // get the input for the nat table
    DataAssessmentCompareHexData compareHexData =
        getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData();

    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);

    // initialising the filter
    this.toolBarFilters = new DaCompHexToolBarFilter();
    createComparisonTabViewer(compareHexData.getDaCompareHexParam());
    // type filter listener
    addModifyListenerForFilterTxt();


    // create tool bar filters
    createToolbarFilters();


    // create stat labels
    createStatCountLabels();
    // create qnaire status count
    createQnaireStatusCountLabels();

    // mouse listener
    addMouseListener();
  }

  /**
   * @param qnaireStatComp as composite
   */
  private void createQnaireStatusCountLabels() {
    LabelUtil.getInstance().createLabel(this.qnaireStatComp, "Parameters in Bosch Responsibility Reviewed :");
    CompHexStatistics compareHexStatics =
        getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData().getCompareHexStatics();
    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        TXT_FILLER + compareHexStatics.getStatParamWithBoschRespRvw() + "%");
    LabelUtil.getInstance().createLabel(this.qnaireStatComp, LABEL_FILLER);


    LabelUtil.getInstance().createLabel(this.qnaireStatComp, "Number of Parameters in Bosch Responsibility :");
    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        TXT_FILLER + compareHexStatics.getstatNumParamInBoschResp());
    LabelUtil.getInstance().createLabel(this.qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        "Parameters in Bosch Responsibility with Completed Questionnaire :");
    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        TXT_FILLER + compareHexStatics.getStatParamWithBoschRespQnaireRvw() + "%");
    LabelUtil.getInstance().createLabel(this.qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.qnaireStatComp, "Number of Parameters in Bosch Responsibility Reviewed :");
    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        TXT_FILLER + compareHexStatics.getstatNumParamInBoschRespRvwed());
    LabelUtil.getInstance().createLabel(this.qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        "Number of questionnaires with negative answer included :");
    LabelUtil.getInstance().createLabel(this.qnaireStatComp,
        TXT_FILLER + compareHexStatics.getStatQnaireNagativeAnswer());
    LabelUtil.getInstance().createLabel(this.qnaireStatComp, LABEL_FILLER);
  }


  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(modifyEvent -> {

      String text = DataAssessmentCompHexRvwResultsPage.this.filterTxt.getText().trim();
      DataAssessmentCompHexRvwResultsPage.this.allColumnFilterMatcher.setFilterText(text, true);
      DataAssessmentCompHexRvwResultsPage.this.compRprtFilterGridLayer.getFilterStrategy()
          .applyFilterInAllColumns(text);
      DataAssessmentCompHexRvwResultsPage.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(
              DataAssessmentCompHexRvwResultsPage.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMsgAndStatHdr();
    });
  }

  /**
   * create toolbar and filters
   */
  private void createToolbarFilters() {

    this.toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = this.toolBarManager.createControl(this.section);
    final Separator separator = new Separator();

    DaCompHexToolbarActionSet toolBarActionSet = new DaCompHexToolbarActionSet(this.compRprtFilterGridLayer, this);

    toolBarActionSet.showMatchLatestFuncVer(this.toolBarManager, this.toolBarFilters);

    toolBarActionSet.showNotMatchLatestFuncVer(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    toolBarActionSet.showReadOnlyAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showNotReadOnlyAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    toolBarActionSet.dependantCharAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.noDependantCharAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    // Filter For the compliance parameters
    toolBarActionSet.blackListFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    // Filter For the non compliance parameters
    toolBarActionSet.nonBlackListFilterAction(this.toolBarManager, this.toolBarFilters, this.natTable);
    this.toolBarManager.add(separator);

    // Filter For the compliance parameters
    toolBarActionSet.complianceFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the non compliance parameters
    toolBarActionSet.nonComplianceFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    // Filter For the QSSD parameters
    toolBarActionSet.qSSDFilterAction(this.toolBarManager, this.toolBarFilters);
    // Filter For the non QSSD parameters
    toolBarActionSet.nonQSSDFilterAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);


    toolBarActionSet.showCompli(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showOk(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showNotApplicable(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    toolBarActionSet.showWpFinished(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showWpNotFinished(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    if (this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler() != null) {
      CalibrationStatusListExportAction calibrationStatusListExportAction = new CalibrationStatusListExportAction(null,
          this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler(), false);
      this.form.getToolBarManager().add(calibrationStatusListExportAction);

      CompHexCheckSSDReportAction exportSSDReportAction =
          new CompHexCheckSSDReportAction(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler());
      this.form.getToolBarManager().add(exportSSDReportAction);

      CompHexRevExportPdfAction exportPdfAction =
          new CompHexRevExportPdfAction(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler());

      this.form.getToolBarManager().add(exportPdfAction);

      CompHexZipDownloadAction exportZipAction =
          new CompHexZipDownloadAction(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler());
      this.form.getToolBarManager().add(exportZipAction);
    }

    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);

    this.toolBarManager.update(true);

    addResetAllFiltersAction();
    this.section.setTextClient(toolbar);

  }

  /**
   * ICDM-2141 Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.compRprtFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * Creates the stat count comp.
   *
   * @param gridData the grid data
   * @param compHexDataHdlr the comp hex with cdfx data
   * @return the composite
   */
  private Composite createStatCountComp(final GridData gridData) {
    // total count and statistics
    Composite statisitcComp = new Composite(this.formStatistics.getBody(), SWT.BORDER);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 12;
    statisitcComp.setLayout(gridLayout);
    gridData.grabExcessVerticalSpace = false;
    statisitcComp.setLayoutData(gridData);
    return statisitcComp;
  }

  /**
   * Creates the stat count comp.
   *
   * @param gridData the grid data
   * @param compHexDataHdlr the comp hex with cdfx data
   * @return the composite
   */
  private Composite createQnaireStatCountComp() {
    // total count and statistics
    Composite qnaireStatisticComp = new Composite(this.formStatistics.getBody(), SWT.BORDER);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 6;
    qnaireStatisticComp.setLayout(gridLayout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    qnaireStatisticComp.setLayoutData(gridData);
    return qnaireStatisticComp;
  }

  /**
   * Creates the stat count labels.
   *
   * @param statComp the stat comp
   * @param inputSet the input set
   */
  private void createStatCountLabels() {
    // total count and statistics
    CompHexStatistics compareHexStatics =
        getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData().getCompareHexStatics();
    compareHexStatics.setStatFilteredParam(this.compRprtFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());

    LabelUtil.getInstance().createLabel(this.statComp, "Total Parameters in A2L :");
    LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatTotalParamInA2L());
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    // Dynamic Filter labels for statistics
    LabelUtil.getInstance().createLabel(this.statComp, "Filtered Parameters :");
    this.filterParamStatLbl =
        LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatFilteredParam());
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.statComp,
        COMPLI_FAILED + "(" + CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType() + ") :");
    Label compliCSSDFailedLbl = LabelUtil.getInstance().createLabel(this.statComp,
        TXT_FILLER + compareHexStatics.getStatCompliCssdFailed() + TXT_FILLER);
    if (compareHexStatics.getStatCompliCssdFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(compliCSSDFailedLbl);
    }
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.statComp, "Compli parameters in A2L :");
    LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatCompliParamInA2L());
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.statComp, "Parameters Reviewed :");
    LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatParamReviewed());
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.statComp, "Filtered Parameters reviewed :");
    this.filterParamRvwdStatLbl =
        LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatFilteredParamRvwd());
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);


    LabelUtil.getInstance().createLabel(this.statComp,
        COMPLI_FAILED + "(" + CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType() + ") :");
    Label compliSSD2RvFailedLbl = LabelUtil.getInstance().createLabel(this.statComp,
        TXT_FILLER + compareHexStatics.getStatCompliSSDRvFailed() + TXT_FILLER);
    if (compareHexStatics.getStatCompliSSDRvFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(compliSSD2RvFailedLbl);
    }
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);


    LabelUtil.getInstance().createLabel(this.statComp, "Compli parameters passed :");
    LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatCompliParamPassed());
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    // Param Reviewed not equal
    LabelUtil.getInstance().createLabel(this.statComp, "Parameter reviewed NOT equal :");
    Label rvwdNotEqLbl = LabelUtil.getInstance().createLabel(this.statComp,
        TXT_FILLER + compareHexStatics.getStatParamRvwdNotEqual() + TXT_FILLER);
    if (compareHexStatics.getStatParamRvwdNotEqual() > 0) {
      LabelUtil.getInstance().setErrorStyle(rvwdNotEqLbl);
    }
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    // Filtered Param Reviewed not equal
    LabelUtil.getInstance().createLabel(this.statComp, "Filtered Parameter reviewed NOT equal :");
    this.filterRvwdNotEqlStatLbl = LabelUtil.getInstance().createLabel(this.statComp,
        TXT_FILLER + compareHexStatics.getStatFilteredParamRvwdNotEqual() + TXT_FILLER);
    if (compareHexStatics.getStatFilteredParamRvwdNotEqual() > 0) {
      LabelUtil.getInstance().setErrorStyle(this.filterRvwdNotEqlStatLbl);
    }
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.statComp,
        COMPLI_FAILED + "(" + CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType() + ") :");
    Label compliNoRuleFailedLbl = LabelUtil.getInstance().createLabel(this.statComp,
        TXT_FILLER + compareHexStatics.getStatCompliNoRuleFailed() + TXT_FILLER);
    if (compareHexStatics.getStatCompliNoRuleFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(compliNoRuleFailedLbl);
    }
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(this.statComp, "Q-SSD parameters failed :");
    Label qssdParamFailed =
        LabelUtil.getInstance().createLabel(this.statComp, TXT_FILLER + compareHexStatics.getStatQSSDParamFailed());
    if (compareHexStatics.getStatQSSDParamFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(qssdParamFailed);
    }
    LabelUtil.getInstance().createLabel(this.statComp, LABEL_FILLER);


  }


  /**
   * create the NAT table viewer
   *
   * @param daCompHexParam
   * @param compHexDataHdlr2
   */
  private void createComparisonTabViewer(final List<DaCompareHexParam> daCompHexParam) {
    // set column names and width for static columns
    ConcurrentMap<Integer, Integer> columnWidthMap = setHeaderNameColWidthForStaticCols();

    this.totTableRowCount = daCompHexParam.size();
    // create nat table
    IConfigRegistry configRegistry = createNatTable(columnWidthMap, daCompHexParam);

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());


    // initailise all column filter
    this.allColumnFilterMatcher = new DaCompHexAllColMatcher(getDataAssmntReportDataHandler());
    this.compRprtFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);


    this.compRprtFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());

    this.natTable.addConfiguration(new FilterRowCustomConfiguration(COLUMN_COUNT) {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry1) {
        super.configureRegistry(configRegistry1);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry1.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.compRprtFilterGridLayer.getColumnHeaderDataLayer()));

    // add the edit configuration which will give images for type column
    this.natTable.addConfiguration(new DaCompHEXEditConfiguration());

    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(DataAssessmentCompHexRvwResultsPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTable1, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      DataAssessmentCompHexRvwResultsPage.this.resetState = true;
                      reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // add the label accumulator
    DataLayer bodyDataLayer = this.compRprtFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<DaCompareHexParam> bodyDataProvider =
        (IRowDataProvider<DaCompareHexParam>) bodyDataLayer.getDataProvider();
    final DaCompHEXLabelAccumulator rvwReportLabelAccumulator =
        new DaCompHEXLabelAccumulator(bodyDataLayer, bodyDataProvider, getDataAssmntReportDataHandler());
    bodyDataLayer.setConfigLabelAccumulator(rvwReportLabelAccumulator);


    // Right click of the context menu in the table graph viewer
    addRightClickMenu();

    // configures the nattable
    this.natTable.configure();
    // Attach tooltip
    attachTooltip();
    // Load the saved state of NAT table
    loadState();

    // add listeners to save state
    this.natTable.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(final FocusEvent event) {
        // save state on focus lost to maintain state for other result editors
        saveState();
      }

      @Override
      public void focusGained(final FocusEvent event) {
        // no implementation at the moment

      }
    });

    // Save the current state of the nat table before disposing
    this.natTable.addDisposeListener(event -> saveState());

    // initialise the selection provider
    this.selectionProvider = new RowSelectionProvider<>(this.compRprtFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.compRprtFilterGridLayer.getBodyDataProvider(), false);
    addSelectionListener();


    // set the status bar message
    setStatusBarMsgAndStatHdr();
    getSite().setSelectionProvider(this.selectionProvider);
  }

  /**
  *
  */
  private void attachTooltip() {
    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip = new DaCompHexNatToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * Adds the selection listener to the selection provider
   */
  // ICDM-2498
  private void addSelectionListener() {
    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if ((selection != null) && (selection.getFirstElement() != null)) {
        if (selection.getFirstElement() instanceof DaCompareHexParam) {
          final DaCompareHexParam compHEXWtihCDFParam = (DaCompareHexParam) selection.getFirstElement();
          // case-1 : no dialog had opened, if reviewResultEditor.getCalDataViewerDialog() is null, this condition
          // satifies
          // case-2 : Dialog already opened and still in that position, if
          // reviewResultEditor.getCalDataViewerDialog().getShell() is not null, this condition satifies
          DaCompHexNatActionSet compHexWithCdfNatActionSet =
              new DaCompHexNatActionSet(getDataAssmntReportDataHandler());
          DataAssessmentReportEditor compHexWithCDFxEditor = (DataAssessmentReportEditor) getEditor();
          // ICDM-2304
          if ((null != compHexWithCDFxEditor.getSynchCalDataViewerDialog()) &&
              (null != compHexWithCDFxEditor.getSynchCalDataViewerDialog().getShell())) {
            compHexWithCdfNatActionSet.showTableGraphAction(compHEXWtihCDFParam,
                DataAssessmentCompHexRvwResultsPage.this.editor, true);
          }
        }
        updateStatusBar(false);
      }
    });
  }

  private void addMouseListener() {
    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        // NA
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        // Left double click
        if (CommonUtils.isEqual(mouseEvent.button, 1)) {
          leftMouseDoubleClickAction(mouseEvent);
        }
      }
    });
  }

  /**
   * Handles the action triggered by a left mouse double click event.
   *
   * @param mouseEvent The MouseEvent representing the left mouse double click event.
   */
  private void leftMouseDoubleClickAction(final MouseEvent mouseEvent) {
    // Get the cell at the mouse click position
    ILayerCell cell = this.natTable.getCellByPosition(
        this.natTable.getColumnPositionByX(mouseEvent.x),
        this.natTable.getRowPositionByY(mouseEvent.y));

    // Check if the cell is not null and if it corresponds to the review description column
    if ((CommonUtils.isNotNull(cell)) &&
        (CommonUtils.isEqual(cell.getColumnIndex(), CommonUIConstants.REVIEW_DESCRIPTION_INDEX))) {
      // Convert the row position to the underlying data layer's row position
      int row = LayerUtil.convertRowPosition(this.natTable,
          this.natTable.getRowPositionByY(mouseEvent.y),
          ((CustomFilterGridLayer<DaCompareHexParam>) this.natTable.getLayer()).getDummyDataLayer());
      
      // Retrieve the object corresponding to the clicked row
      Object rowObject = this.compRprtFilterGridLayer.getBodyDataProvider().getRowObject(row);
      
      // Check if CompHexWithCdfxDataHandler is not null
      if (CommonUtils.isNotNull(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler())) {
        // Open the editor for reviewing the description column with the CDR report data, clicked row object,
        // and the selected PidcVariant
        new CommonActionSet().openEditorForReviewDescCol(
            this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCdrReportData(),
            rowObject,
            this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getSelctedVar());
      } else {
        // Open the editor for reviewing the description column with null CDR report data, clicked row object, and null PidcVariant
        new CommonActionSet().openEditorForReviewDescCol(null, rowObject, null);
      }
    }
  }


  /**
   * Update statistics header labels.
   */
  protected void updateStatisticsHdrLabels() {
    if (this.compRprtFilterGridLayer != null) {
      updateFilteredDataCount(this.compRprtFilterGridLayer.getBodyDataProvider().getList());
    }
    // Check done to ensure - if the refresh is done from NatTable before initialising header labels
    if (this.filterParamRvwdStatLbl != null) {
      if (this.compRprtFilterGridLayer != null) {
        this.filterParamStatLbl
            .setText(TXT_FILLER + this.compRprtFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
      }
      final CompHexStatistics compareHexStatics =
          getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData().getCompareHexStatics();
      this.filterParamRvwdStatLbl.setText(TXT_FILLER + compareHexStatics.getStatFilteredParamRvwd());
      this.filterRvwdNotEqlStatLbl.setText(TXT_FILLER + compareHexStatics.getStatFilteredParamRvwdNotEqual());
      if (compareHexStatics.getStatFilteredParamRvwdNotEqual() > 0) {
        LabelUtil.getInstance().setErrorStyle(this.filterRvwdNotEqlStatLbl);
      }
      else {
        LabelUtil.getInstance().resetLabelStyle(this.filterRvwdNotEqlStatLbl);
      }
    }
  }

  /**
   * Update filtered data count.
   *
   * @param daCompList the list
   */
  private void updateFilteredDataCount(final List<DaCompareHexParam> daCompList) {
    int statFilteredParamRvwd = 0;
    int statFilteredRvwdNtEqual = 0;
    for (DaCompareHexParam item : daCompList) {
      if (item.isReviewed()) {
        statFilteredParamRvwd++;
      }
      if (item.isReviewed() && !item.isEqual()) {
        statFilteredRvwdNtEqual++;
      }
    }
    getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData().getCompareHexStatics()
        .setStatFilteredParamRvwd(statFilteredParamRvwd);
    getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData().getCompareHexStatics()
        .setStatFilteredParamRvwdNotEqual(statFilteredRvwdNtEqual);
  }

  /**
   * Add right click context menu items
   *
   * @param cdrReportData
   */
  // ICDM-2498
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) DataAssessmentCompHexRvwResultsPage.this.selectionProvider.getSelection();
      // only single selection allowed
      if (selection.size() == 1) {
        menuForSingleSelect(menuMgr, selection);
      }
    });
    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }

  private PidcVarRvwDetails getResultObjectForExtLink(final Object selection, final String paramName,
      final CdrReportDataHandler cdrReportData) {
    PidcVarRvwDetails pidcVarRvwDetails = new PidcVarRvwDetails();

    if (selection instanceof DaCompareHexParam) {
      pidcVarRvwDetails.setReviewResult(cdrReportData.getReviewResult(paramName, 0));
    }

    return pidcVarRvwDetails;
  }

  /**
   * Menu actions for single selection
   *
   * @param menuManagr menu
   * @param mgr Imgr
   * @param selection selection
   * @param cdrReportData
   */
  // ICDM-2498
  private void menuForSingleSelect(final MenuManager menuManagr, final IStructuredSelection selection) {

    final Object firstElement = selection.getFirstElement();
    Separator seperator = new Separator();

    if (firstElement instanceof DaCompareHexParam) {

      // Show table graph
      final DaCompHexNatActionSet compHexWithCdfNatActionSet =
          new DaCompHexNatActionSet(this.dataAssmntReportDataHandler);
      compHexWithCdfNatActionSet.showUnSynchronizedTableGraph(menuManagr, firstElement,
          DataAssessmentCompHexRvwResultsPage.this.editor);
      compHexWithCdfNatActionSet.showSynchronizedTableGraph(menuManagr, firstElement,
          DataAssessmentCompHexRvwResultsPage.this.editor);

      if (CommonUtils.isNotNull(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler())) {
        menuForDataAssmntReport(menuManagr, firstElement, seperator);
      }
      else {
        menuForDataAssmntBaseline(menuManagr, firstElement, seperator);
      }
    }

  }

  /**
   * @param menuManagr
   * @param firstElement
   * @param seperator
   */
  private void menuForDataAssmntBaseline(final MenuManager menuManagr, final Object firstElement,
      final Separator seperator) {

    SelectionLayer selectionLayer = this.compRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
    PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();


    DaCompareHexParam daCompareHexParam = (DaCompareHexParam) firstElement;
    String paramName = daCompareHexParam.getParamName();
    if (CommonUtils.isNotNull(daCompareHexParam.getCdrResultId()) &&
        (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEWED_INDEX)) {

      // Open Review Result menu
      menuManagr.add(seperator);
      openReviewResultMenuForBaseline(menuManagr, daCompareHexParam, paramName);
      menuManagr.add(seperator);

      // Open Ruleset editor
      ReviewActionSet reviewEditorActionSet = new ReviewActionSet();
      reviewEditorActionSet.addReviewParamEditor(menuManagr,
          this.dataAssmntReportDataHandler.getA2lParamMap().get(paramName), null, null, true);
    }

    if ((selectedCellPositions.length == 1) &&
        (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEW_SCORE_INDEX) &&
        CommonUtils.isNotNull(daCompareHexParam.getCdrResultId())) {
      menuManagr.add(seperator);
      // Add Open review result menu item
      openReviewResultMenuForBaseline(menuManagr, daCompareHexParam, paramName);
      menuManagr.add(seperator);

      // Add Open PIDC Version menu item
      try {
        PidcVersion pidcVers = new PidcVersionServiceClient()
            .getById(this.dataAssmntReportDataHandler.getDataAssessmentReport().getPidcVersId());
        this.pidcActionSet.openPidcFromCDRReport(menuManagr, pidcVers);
        menuManagr.add(seperator);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error occurred while getting Pidc Version : " + e.getLocalizedMessage(), e,
            Activator.PLUGIN_ID);
      }


      // Add Open A2L File menu item
      try {
        PidcA2l pidcA2l = new PidcA2lServiceClient()
            .getById(this.dataAssmntReportDataHandler.getDataAssessmentReport().getPidcA2lId());
        this.actionSet.openA2LEditor(menuManagr, pidcA2l);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error occurred while getting Pidc A2L : " + e.getLocalizedMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
  }

  private void openReviewResultMenuForBaseline(final MenuManager menuManagr, final DaCompareHexParam daCompareHexParam,
      final String paramName) {
    // open review result option
    CDRReviewResult reviewResult = null;
    try {
      reviewResult = new CDRReviewResultServiceClient().getById(daCompareHexParam.getCdrResultId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error occurred while getting Review result : " + e.getLocalizedMessage(), e,
          Activator.PLUGIN_ID);
    }
    if (reviewResult != null) {
      new CommonActionSet().openReviewResultAction(menuManagr, reviewResult, paramName,
          this.dataAssmntReportDataHandler.getDataAssessmentReport().getPidcVariantId());
    }
  }

  private void menuForDataAssmntReport(final MenuManager menuManagr, final Object firstElement,
      final Separator seperator) {
    // Show compli rule history
    DaCompareHexParam compHexCdfxData = (DaCompareHexParam) firstElement;
    String paramName = compHexCdfxData.getParamName();

    CdrReportDataHandler cdrReportData =
        this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCdrReportData();
    if (compHexCdfxData.isCompli()) {
      menuManagr.add(seperator);
      ReviewRule cdrRule = getCDRRule(paramName);
      (new ReviewRuleActionSet()).showRuleHistory(menuManagr, IMessageConstants.OPEN_COMPLI_RULE_HISTORY, getTitle(),
          cdrRule, true, false, cdrReportData, compHexCdfxData.getFuncName());
    }

    // show qssd rule history
    if (compHexCdfxData.isQssdParameter()) {
      menuManagr.add(seperator);
      ReviewRule cdrRule = getCDRRuleForQssd(paramName);
      (new ReviewRuleActionSet()).showRuleHistory(menuManagr, IMessageConstants.OPEN_QSSD_RULE_HISTORY, getTitle(),
          cdrRule, false, true, cdrReportData, compHexCdfxData.getFuncName());

    }

    // Show rule history
    SelectionLayer selectionLayer = this.compRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
    PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();


    if (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEWED_INDEX) {

      // Open Review Result menu
      contextMenuForReviewValCol(menuManagr, cdrReportData, firstElement);

      // Opem Ruleset editor
      ReviewActionSet reviewEditorActionSet = new ReviewActionSet();
      reviewEditorActionSet.addReviewParamEditor(menuManagr,
          cdrReportData.getA2lEditorDataProvider().getA2lFileInfoBO().getA2lParamMap(null).get(paramName), null, null,
          true);
    }

    // For Adding Context Menu for review score
    addContextMenuForRvwResultScoreCol(menuManagr, seperator, paramName, selectedCellPositions, cdrReportData);


    addContextMenuForReviewDescCol(menuManagr, firstElement, paramName, cdrReportData, selectedCellPositions);


    Long paramId =
        cdrReportData.getA2lEditorDataProvider().getA2lFileInfoBO().getA2lParamMap(null).get(paramName).getParamId();
    // Context menu to show related questionnaires
    String qnaireRespVersStatus = cdrReportData.getQnaireRespVersStatus(paramId, true);
    if ((selectedCellPositions.length == 1) &&
        (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.QNAIRE_STATUS) &&
        CommonUtils.isNotEmptyString(qnaireRespVersStatus) &&
        CommonUtils.isNotEqual(CDRConstants.RVW_QNAIRE_STATUS_N_A, qnaireRespVersStatus) &&
        CommonUtils.isNotEqual(CDRConstants.NO_QNAIRE_STATUS, qnaireRespVersStatus)) {
      menuManagr.add(seperator);
      new ShowRelatedQnaireAction(this.mainComposite.getShell(), menuManagr, paramId, cdrReportData,
          this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler(), false);
    }
  }

  /**
   * @param menuManagr
   * @param firstElement
   * @param paramName
   * @param cdrReportData
   * @param selectedCellPositions
   */
  private void addContextMenuForReviewDescCol(final MenuManager menuManagr, final Object firstElement,
      final String paramName, final CdrReportDataHandler cdrReportData,
      final PositionCoordinate[] selectedCellPositions) {
    if (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEW_DESCRIPTION_INDEX) {
      final PidcVarRvwDetails cdrResult = getResultObjectForExtLink(firstElement, paramName, cdrReportData);

      // Determine the link object based on the review variant and review result in the result object
      final Object linkObj =
          CommonUtils.isNull(cdrResult.getReviewVariant()) ? cdrResult.getReviewResult() : cdrResult.getReviewVariant();
      final Action copyLink = new Action() {

        @Override
        public void run() {

          try {
            // Use LinkCreator to copy the link to the clipboard
            new LinkCreator(linkObj).copyToClipBoard();
          }
          catch (ExternalLinkException exp) {
            CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }

      };
      // Set the text, image, and action for the "Copy Review Result Link" menu item
      copyLink.setText("Copy Review Result Link");
      copyLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_RES_LINK_16X16));

      // Add the "Copy Review Result Link" menu item to the menu manager
      menuManagr.add(copyLink);
    }
  }

  /**
   * @param menuManagr
   * @param seperator
   * @param paramName
   * @param selectedCellPositions
   * @param cdrReportData
   */
  private void addContextMenuForRvwResultScoreCol(final MenuManager menuManagr, final Separator seperator,
      final String paramName, final PositionCoordinate[] selectedCellPositions,
      final CdrReportDataHandler cdrReportData) {
    if ((selectedCellPositions.length == 1) &&
        (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEW_SCORE_INDEX) &&
        cdrReportData.hasReviewResult(paramName, 0)) {
      menuManagr.add(seperator);
      // Add Open review result menu item
      CDRReviewResult reviewResult = cdrReportData.getReviewResult(paramName, 0);
      // Defect Fix 260937
      this.actionSet.openReviewResultAction(menuManagr, reviewResult, paramName,
          this.dataAssmntReportDataHandler.getDataAssessmentReport().getPidcVariantId());
      menuManagr.add(seperator);

      // Add Open PIDC Version menu item
      PidcVersion pidcVers = cdrReportData.getPidcVersion(paramName, 0);
      reviewResult.getPidcVersionId();
      this.pidcActionSet.openPidcFromCDRReport(menuManagr, pidcVers);
      menuManagr.add(seperator);

      // Add Open A2L File menu item
      PidcA2l pidcA2l = cdrReportData.getPidcA2l(paramName, 0);
      this.actionSet.openA2LEditor(menuManagr, pidcA2l);

    }
  }


  /**
   * Returns CDRRule from CompliRvw input data
   *
   * @param paramName label
   * @return cdr rule
   */
  private ReviewRule getCDRRule(final String paramName) {
    if ((this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler() == null) ||
        (this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse() == null) ||
        !CommonUtils.isNotEmpty(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse()
            .getSsdRulesForCompliance())) {
      return null;
    }
    if (CommonUtils.isNotEmpty(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse()
        .getSsdRulesForCompliance().get(paramName))) {
      return this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse()
          .getSsdRulesForCompliance().get(paramName).get(0);
    }
    return null;
  }


  /**
   * Returns CDRRule for qssd param from CompliRvw input data
   *
   * @param paramName label
   * @return cdr rule
   */
  private ReviewRule getCDRRuleForQssd(final String paramName) {
    if ((this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler() == null) || CommonUtils.isNullOrEmpty(
        this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse().getSsdRulesForQssd())) {
      return null;
    }
    if (CommonUtils.isNotEmpty(this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse()
        .getSsdRulesForQssd().get(paramName))) {
      return this.dataAssmntReportDataHandler.getCompHexWithCdfxDataHandler().getCompHexResponse().getSsdRulesForQssd()
          .get(paramName).get(0);
    }
    return null;
  }

  /**
   * @param menuManagr
   * @param cdrReportData
   * @param firstElement
   */
  private void contextMenuForReviewValCol(final MenuManager menuMgr, final CdrReportDataHandler cdrReportData,
      final Object firstElement) {

    menuMgr.add(new Separator());
    // open review result option
    String paramName = ((DaCompareHexParam) firstElement).getParamName();
    CDRReviewResult reviewResult = cdrReportData.getReviewResult(paramName, 0);
    if (reviewResult != null) {
      new CommonActionSet().openReviewResultAction(menuMgr, reviewResult, paramName,
          this.dataAssmntReportDataHandler.getDataAssessmentReport().getPidcVariantId());
    }
    menuMgr.add(new Separator());
  }

  /**
   * @author mkl2cob
   */
  private class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    /**
     * @author jvi6cob
     */
    private final class FilterDisplayConverter extends DisplayConverter {

      /** The col name. */
      private final String colName;

      /**
       * Instantiates a new filter display converter.
       *
       * @param colName the col name
       */
      FilterDisplayConverter(final String colName) {
        this.colName = colName;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Object displayToCanonicalValue(final Object displayValue) {
        return null;
      }

      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (this.colName.equalsIgnoreCase(COL_NM_COMPLI_TYPE) && (canonicalValue instanceof String) &&
            ((String) canonicalValue).isEmpty()) {
          return COMPLI_TYPE.NON_COMPLIANCE.getText();
        }
        if (this.colName.equalsIgnoreCase(COL_NM_RESULT) && (canonicalValue instanceof String) &&
            ((String) canonicalValue).trim().isEmpty()) {
          return RESULT_STATUS.NA.getText();
        }
        if (this.colName.equalsIgnoreCase(COL_NM_TYPE) && (canonicalValue instanceof String) &&
            ((String) canonicalValue).trim().isEmpty()) {
          return "NA";
        }
        return canonicalValue;
      }
    }

    private final int numOfColumns;

    /**
     * @param noOfColumns
     */
    public FilterRowCustomConfiguration(final int noOfColumns) {
      this.numOfColumns = noOfColumns;
    }

    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

      // enable filter comparator for cols
      for (int index = 0; index <= this.numOfColumns; index++) {
        // register config attr for each col
        configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
            TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + index);
      }

      configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {},
          NORMAL);

      // combo for Reviewed column - Space is appended to differentiate the filter text 'Reviewed' from other options
      List<String> comboList =
          Arrays.asList(CommonUtils.concatenate(ApicConstants.REVIEWED, DataAssessmentCompHexRvwResultsPage.TXT_FILLER),
              ApicConstants.NEVER_REVIEWED, ApicConstants.NOT_FINALLY_REVIEWED);

      // register a combo box cell editor for the gender column in the filter row
      // the label is set automatically to the value of
      // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
      ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.REVIEWED_INDEX);
      // Configure Filter Display Converter
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_REVW_STATUS), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.REVIEWED_INDEX);
      // For Questionniare Status filter
      List<String> qniareStatusList = Arrays.asList(CDRConstants.DA_QS_STATUS_TYPE.ALL_POSITIVE.getUiType(),
          CDRConstants.DA_QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType(),
          CDRConstants.DA_QS_STATUS_TYPE.NOT_ANSWERED.getUiType(),
          CDRConstants.DA_QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getUiType(),
          CDRConstants.DA_QS_STATUS_TYPE.NO_BASELINE.getUiType(), CDRConstants.RVW_QNAIRE_STATUS_N_A,
          CDRConstants.DA_QS_STATUS_TYPE.NO_QNAIRE.getUiType(),
          CDRConstants.DA_QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getUiType());


      ComboBoxCellEditor comboBoxCellEditor1 = new ComboBoxCellEditor(qniareStatusList);

      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor1, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.QNAIRE_STATUS);
      // Configure Filter Display Converter
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_QUESTIONNIARE_STATUS), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.QNAIRE_STATUS);


      // combo for Equal column
      comboList = Arrays.asList(
          CommonUtils.concatenate(RESULT_STATUS.EQUAL.getText(), DataAssessmentCompHexRvwResultsPage.TXT_FILLER),
          RESULT_STATUS.NOT_EQUAL.getText());
      comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.EQUAL_INDEX);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_RESULT), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.EQUAL_INDEX);


      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NAME_RESP), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RESP_INDEX);

      // combo for Responsibility Type column
      comboList = Arrays.asList("Customer", "Robert Bosch", "Others");
      comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RESP_TYPE_INDEX);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NAME_RESP_TYPE), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.RESP_TYPE_INDEX);


      // combo for Type column
      comboList = Arrays.asList(CommonUtils.concatenate(COMPLI_TYPE.COMPLIANCE.getText(), " "),
          COMPLI_TYPE.NON_COMPLIANCE.getText());

      comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.COMPLI_RESULT_INDEX);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_COMPLI_TYPE), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.COMPLI_RESULT_INDEX);

      // combo for Type column
      comboList =
          Arrays.asList(ParameterType.VALUE.getText(), ParameterType.VAL_BLK.getText(), ParameterType.MAP.getText(),
              ParameterType.CURVE.getText(), ParameterType.ASCII.getText(), ParameterType.AXIS_PTS.getText());

      comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.TYPE_INDEX);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_TYPE), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.TYPE_INDEX);

      // combo for COMPLI/Shape check column
      comboList = Arrays.asList(CompliResValues.OK.getUiValue(), CompliResValues.CSSD.getUiValue(),
          QSSDResValues.QSSD.getUiValue(), CompliResValues.SSD2RV.getUiValue(), CompliResValues.NO_RULE.getUiValue(),
          CompliResValues.SHAPE.getUiValue(), "NA");
      comboBoxCellEditor = new ComboBoxCellEditor(comboList);
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.COMPLI_RESULT_INDEX);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_NM_TYPE), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.COMPLI_RESULT_INDEX);


      // Adding Score to the Filter
      List<String> scoreComboList = new ArrayList<>();
      List<DATA_REVIEW_SCORE> scoreEnums = DATA_REVIEW_SCORE.getApplicableScore(null);
      for (DATA_REVIEW_SCORE score : scoreEnums) {
        scoreComboList.add(score.getScoreDisplay());
      }
      // register a combo box cell editor for the gender column in the filter row
      // the label is set automatically to the value of
      // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
      ICellEditor scoreComboBoxCellEditor = new ComboBoxCellEditor(scoreComboList, scoreComboList.size());
      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, scoreComboBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.REVIEW_SCORE_INDEX);
      // Configure Filter Display Converter
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(""), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.REVIEW_SCORE_INDEX);
    }
  }

  /**
   * @param columnHeaderDataLayer
   * @param cdrReportData
   * @return
   */
  private IConfiguration getCustomComparatorConfiguration(final DefaultColumnHeaderDataLayer columnHeaderDataLayer) {
    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        for (int col_index = 0; col_index < (COLUMN_COUNT); col_index++) {
          labelAccumulator.registerColumnOverrides(col_index, CUSTOM_COMPARATOR_LABEL + col_index);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getParamComparator(col_index),
              NORMAL, CUSTOM_COMPARATOR_LABEL + col_index);
        }


      }
    };
  }

  /**
   * @param columnWidthMap
   * @param daCompHexParam
   * @param compHexDataHdlr2
   * @return
   */
  private IConfigRegistry createNatTable(final ConcurrentMap<Integer, Integer> columnWidthMap,
      final List<DaCompareHexParam> daCompHexParam) {
    // instantiate the input to column converter
    DaCompHexNatInputToColConverter natInputToColumnConverter =
        new DaCompHexNatInputToColConverter(getDataAssmntReportDataHandler());
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.compRprtFilterGridLayer = new CustomFilterGridLayer<>(configRegistry, new HashSet<>(daCompHexParam),
        columnWidthMap, new CustomDaCompHexColPropertyAccessor<>(), new DaCompHexColumnHdrDataProvider(),
        getParamComparator(2), natInputToColumnConverter, this, null, true, true, false);

    // creating the NAT table
    this.natTable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.compRprtFilterGridLayer, false, getClass().getSimpleName(), this.propertyToLabelMap);
    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return configRegistry;
  }

  /**
   * column property accessor
   *
   * @param <T>
   */
  class CustomDaCompHexColPropertyAccessor<T> implements IColumnAccessor<T> {

    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T type, final int columnIndex) {
      return type;
    }


    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // implementation not required currently
    }

    @Override
    public int getColumnCount() {
      return DataAssessmentCompHexRvwResultsPage.this.propertyToLabelMap.size();
    }
  }

  /**
   * Column header data provider class
   */
  class DaCompHexColumnHdrDataProvider implements IDataProvider {

    /**
     * @param columnIndex int
     * @return String column header label
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = DataAssessmentCompHexRvwResultsPage.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    @Override
    public int getColumnCount() {
      return DataAssessmentCompHexRvwResultsPage.this.propertyToLabelMap.size();
    }

    @Override
    public int getRowCount() {
      return 1;
    }

    /**
     * This class does not support multiple rows in the column header layer.
     */
    @Override
    public Object getDataValue(final int columnIndex, final int rowIndex) {
      return getColumnHeaderLabel(columnIndex);
    }

    @Override
    public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * get the comparator for the table
   *
   * @param cdrReportData
   * @param cdrReportData CdrReportData
   * @return Comparator<FocusMatrixAttribute>
   */
  private Comparator<DaCompareHexParam> getParamComparator(final int columnNum) {
    return (compParam1, compParam2) -> {
      int ret = 0;

      switch (columnNum) {
        case CommonUIConstants.SSD_CLASS_INDEX:
          ret = ApicUtil.compare(compParam1.isCompli(), compParam2.isCompli());
          break;
        case CommonUIConstants.TYPE_INDEX:
          ret = ApicUtil.compare(compParam1.getParamType().getText(), compParam2.getParamType().getText());
          break;
        case CommonUIConstants.PARAMTETER_INDEX:
          ret = ApicUtil.compare(compParam1.getParamName(), compParam2.getParamName());
          break;
        case CommonUIConstants.FUNC_INDEX:
          ret = ApicUtil.compare(compParam1.getFuncName(), compParam2.getFuncName());
          break;
        case CommonUIConstants.FUNC_VERS_INDEX:
          ret = ApicUtil.compare(compParam1.getFuncVers(), compParam2.getFuncVers());
          break;
        case CommonUIConstants.WP_INDEX:
          ret = ApicUtil.compare(compParam1.getWpName(), compParam2.getWpName());
          break;
        case CommonUIConstants.RESP_TYPE_INDEX:
          // Compare Responsibility Types
          ret = ApicUtil.compare(compParam1.getRespType(), compParam2.getRespType());
          break;
        case CommonUIConstants.RESP_INDEX:
          // comparing responsibilities
          ret = ApicUtil.compare(compParam1.getRespName(), compParam2.getRespName());
          break;
        case CommonUIConstants.WP_FINISHED_INDEX:
          ret = ApicUtil.compare(compParam1.getWpFinishedStatus(), compParam2.getWpFinishedStatus());
          break;
        case CommonUIConstants.LATEST_A2L_VERS_INDEX:
          ret = ApicUtil.compare(compParam1.getLatestA2lVersion(), compParam2.getLatestA2lVersion());
          break;
        case CommonUIConstants.LATEST_FUNC_VERS_INDEX:
          // comparing whether the paramter is reviewed or not
          ret = ApicUtil.compare(compParam1.getLatestFunctionVersion(), compParam2.getLatestFunctionVersion());
          break;
        case CommonUIConstants.QNAIRE_STATUS:
          // comparing whether the paramter is reviewed or not
          ret = ApicUtil.compare(getDataAssmntReportDataHandler().getQnaireStatus(compParam1),
              getDataAssmntReportDataHandler().getQnaireStatus(compParam2));
          break;
        case CommonUIConstants.REVIEWED_INDEX:
          ret = ApicUtil.compare(getDataAssmntReportDataHandler().getReviewedStatus(compParam1),
              getDataAssmntReportDataHandler().getReviewedStatus(compParam2));
          break;
        case CommonUIConstants.EQUAL_INDEX:
          ret = ApicUtil.compare(getDataAssmntReportDataHandler().getEqualStatus(compParam1),
              getDataAssmntReportDataHandler().getEqualStatus(compParam2));
          break;
        case CommonUIConstants.HEX_VALUE_INDEX:
          ret = ApicUtil.compare(getDataAssmntReportDataHandler().getCalDataStringFromBytes(compParam1.getHexValue()),
              getDataAssmntReportDataHandler().getCalDataStringFromBytes(compParam2.getHexValue()));
          break;
        case CommonUIConstants.REVIEWED_VALUE_INDEX:
          ret = ApicUtil.compare(
              getDataAssmntReportDataHandler().getCalDataStringFromBytes(compParam1.getReviewedValue()),
              getDataAssmntReportDataHandler().getCalDataStringFromBytes(compParam2.getReviewedValue()));
          break;
        case CommonUIConstants.COMPLI_RESULT_INDEX:
          ret = ApicUtil.compare(getDataAssmntReportDataHandler().getResult(compParam1),
              getDataAssmntReportDataHandler().getResult(compParam2));
          break;
        case CommonUIConstants.REVIEW_SCORE_INDEX:
          ret = ApicUtil.compare(compParam1.getReviewScore(), compParam2.getReviewScore());
          break;
        case CommonUIConstants.LATEST_RVW_CMNT_INDEX:
          ret = ApicUtil.compare(compParam1.getLatestReviewComments(), compParam2.getLatestReviewComments());
          break;
        case 19:
          ret = ApicUtil.compare(
              getDataAssmntReportDataHandler().getCompHexWithCdfxDataHandler().getCdrReportData()
                  .getReviewResultName(compParam1.getParamName()),
              getDataAssmntReportDataHandler().getCompHexWithCdfxDataHandler().getCdrReportData()
                  .getReviewResultName(compParam2.getParamName()));
          break;
        default:
          break;
      }
      return ret;
    };
  }

  /**
   * @return ConcurrentMap<Integer, Integer>
   */
  private ConcurrentMap<Integer, Integer> setHeaderNameColWidthForStaticCols() {
    // storing the header names
    this.propertyToLabelMap = new HashMap<>();

    this.propertyToLabelMap.put(CommonUIConstants.SSD_CLASS_INDEX, " ");
    this.propertyToLabelMap.put(CommonUIConstants.TYPE_INDEX, "Type");
    this.propertyToLabelMap.put(CommonUIConstants.PARAMTETER_INDEX, "Parameter");
    this.propertyToLabelMap.put(CommonUIConstants.FUNC_INDEX, "Function");
    this.propertyToLabelMap.put(CommonUIConstants.FUNC_VERS_INDEX, "Function Version");
    this.propertyToLabelMap.put(CommonUIConstants.WP_INDEX, "WP");
    this.propertyToLabelMap.put(CommonUIConstants.RESP_TYPE_INDEX, COL_NAME_RESP_TYPE);
    this.propertyToLabelMap.put(CommonUIConstants.RESP_INDEX, "RESP");
    this.propertyToLabelMap.put(CommonUIConstants.WP_FINISHED_INDEX, "WP Finished");
    this.propertyToLabelMap.put(CommonUIConstants.LATEST_A2L_VERS_INDEX, "Latest A2L Version");
    this.propertyToLabelMap.put(CommonUIConstants.LATEST_FUNC_VERS_INDEX, "Latest Func Version");
    this.propertyToLabelMap.put(CommonUIConstants.QNAIRE_STATUS, COL_QUESTIONNIARE_STATUS);
    this.propertyToLabelMap.put(CommonUIConstants.REVIEWED_INDEX, "Reviewed");
    this.propertyToLabelMap.put(CommonUIConstants.EQUAL_INDEX, COL_NM_RESULT);
    this.propertyToLabelMap.put(CommonUIConstants.HEX_VALUE_INDEX, "HEX Value");
    this.propertyToLabelMap.put(CommonUIConstants.REVIEWED_VALUE_INDEX, "Reviewed Value");
    this.propertyToLabelMap.put(CommonUIConstants.COMPLI_RESULT_INDEX, "Compli Result");
    this.propertyToLabelMap.put(CommonUIConstants.REVIEW_SCORE_INDEX, "Review Score");
    this.propertyToLabelMap.put(CommonUIConstants.LATEST_RVW_CMNT_INDEX, "Latest Review Comments");
    this.propertyToLabelMap.put(CommonUIConstants.REVIEW_DESCRIPTION_INDEX, "Review Description");


    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    columnWidthMap.put(CommonUIConstants.SSD_CLASS_INDEX, PTYPE_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.TYPE_INDEX, PTYPE_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.PARAMTETER_INDEX, PARAMETER_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.FUNC_INDEX, FUNC_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.FUNC_VERS_INDEX, FUNC_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.WP_INDEX, RESP_VERSIONS_COLUMN);
    columnWidthMap.put(CommonUIConstants.RESP_TYPE_INDEX, RESP_VERSIONS_COLUMN);
    columnWidthMap.put(CommonUIConstants.RESP_INDEX, RESP_VERSIONS_COLUMN);
    columnWidthMap.put(CommonUIConstants.WP_FINISHED_INDEX, RESP_VERSIONS_COLUMN);
    columnWidthMap.put(CommonUIConstants.LATEST_A2L_VERS_INDEX, RESP_VERSIONS_COLUMN);
    columnWidthMap.put(CommonUIConstants.LATEST_FUNC_VERS_INDEX, RESP_VERSIONS_COLUMN);
    columnWidthMap.put(CommonUIConstants.QNAIRE_STATUS, LATEST_QNAIRE_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.REVIEWED_INDEX, TICK_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.EQUAL_INDEX, TICK_COLUMN_WIDTH);
    columnWidthMap.put(CommonUIConstants.HEX_VALUE_INDEX, CAL_DATA_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.REVIEWED_VALUE_INDEX, CAL_DATA_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_14, CAL_DATA_COL_WIDTH);
    columnWidthMap.put(CommonUIConstants.REVIEW_DESCRIPTION_INDEX, TICK_COLUMN_WIDTH);
    return columnWidthMap;
  }

  /**
   * Load saved state of NAT table
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.natTable.resetState();
      }
      this.natTable.loadState();

    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load CDR Result nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Reconstruct nat table.
   */
  public void reconstructNatTable() {
    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.compRprtFilterGridLayer = null;
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
    if (this.form.getToolBarManager() != null) {
      this.form.getToolBarManager().removeAll();
    }
    List<DaCompareHexParam> inputSet =
        getDataAssmntReportDataHandler().getDataAssessmentReport().getDataAssmntCompHexData().getDaCompareHexParam();
    createComparisonTabViewer(inputSet);
    createToolbarFilters();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.form.getBody().pack();
    this.section.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
  }

  /**
   * Save current state for the NAT table
   */
  private void saveState() {
    try {
      this.natTable.saveState();


    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save CDR Result nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    if (null == this.toolBarManager) {
      this.toolBarManager = new ToolBarManager(SWT.FLAT);
    }
    return this.toolBarManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMsgAndStatHdr();
  }

  /**
   * input for status line
   */
  public void setStatusBarMsgAndStatHdr() {

    int totalItemCount = this.totTableRowCount;
    int filteredItemCount = this.compRprtFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
    final StringBuilder buf = new StringBuilder(40);
    buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount).append(" records ");
    IStatusLineManager statusLine;

    // get the status line manager from the editor
    statusLine = this.editor.getEditorSite().getActionBars().getStatusLineManager();
    if (totalItemCount == filteredItemCount) {
      statusLine.setErrorMessage(null);
      statusLine.setMessage(buf.toString());
    }
    else {
      // show the message in red if the count is not equal
      statusLine.setErrorMessage(buf.toString());
    }
    statusLine.update(true);

    // Update Statistics Labels
    updateStatisticsHdrLabels();
  }


  /**
   * @return the compRprtFilterGridLayer
   */
  public CustomFilterGridLayer<DaCompareHexParam> getCompRprtFilterGridLayer() {
    return this.compRprtFilterGridLayer;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (this.editor.getActivePage() == 2)) {
      setStatusBarMsgAndStatHdr();
    }

  }

  /**
   * @return the dataAssmntReportDataHandler
   */
  public DataAssmntReportDataHandler getDataAssmntReportDataHandler() {
    return this.dataAssmntReportDataHandler;
  }


}
