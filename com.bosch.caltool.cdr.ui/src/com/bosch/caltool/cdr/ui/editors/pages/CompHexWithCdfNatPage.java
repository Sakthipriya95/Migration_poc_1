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
import java.util.List;
import java.util.Set;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
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
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.CalibrationStatusListExportAction;
import com.bosch.caltool.cdr.ui.actions.CompHexCheckSSDReportAction;
import com.bosch.caltool.cdr.ui.actions.CompHexRevExportPdfAction;
import com.bosch.caltool.cdr.ui.actions.CompHexWithCDFToolbarActionSet;
import com.bosch.caltool.cdr.ui.actions.CompHexWithCdfNatActionSet;
import com.bosch.caltool.cdr.ui.actions.CompHexZipDownloadAction;
import com.bosch.caltool.cdr.ui.actions.ShowRelatedQnaireAction;
import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditor;
import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditorInput;
import com.bosch.caltool.cdr.ui.editors.natcolumnfilter.CompHexWithCdfAllColMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.CompHEXCDFEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.CompHEXCDFLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.CompHexNatToolTip;
import com.bosch.caltool.cdr.ui.table.filters.CompHEXCDFOutlineFilter;
import com.bosch.caltool.cdr.ui.table.filters.CompHexWithCdfToolBarFilter;
import com.bosch.caltool.cdr.ui.views.providers.CompHexWithCdfNatInputToColConvertr;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler.SortColumns;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
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
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author mkl2cob
 */
public class CompHexWithCdfNatPage extends AbstractNatFormPage implements ISelectionListener {


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
   * CDRDataReportEditor
   */
  private final CompHexWithCDFxEditor editor;
  /**
   * nonScrollableForm
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
   * A2LOutlineNatFilter
   */
  private CompHEXCDFOutlineFilter outlineNatFilter;
  /**
   * CustomNATTable
   */
  private CustomNATTable natTable;
  /**
   * CustomFilterGridLayer
   */
  private CustomFilterGridLayer<CompHexWithCDFParam> compRprtFilterGridLayer;

  /**
   * all column filter matcher
   */
  private CompHexWithCdfAllColMatcher<CompHexWithCDFParam> allColumnFilterMatcher;

  /**
   * Row selection provider of CompHEXWtihCDFParam
   */
  private RowSelectionProvider<CompHexWithCDFParam> selectionProvider;
  /**
   * CompHexWithCdfToolBarFilter
   */
  private CompHexWithCdfToolBarFilter toolBarFilters;
  private Label filterParamStatLbl;
  private Label filterRvwdNotEqlStatLbl;
  private Label filterParamRvwdStatLbl;
  private final CompHexWithCDFxDataHandler compHexDataHdlr;
  /** The Constant COL_NM_COMPLI. */
  private static final String COL_NM_COMPLI_TYPE = "Compliance";


  /** The Constant COL_NM_RESULT. */
  private static final String COL_NM_RESULT = "Equal";

  /** The Constant COL_REVIEW_STATUS. */
  private static final String COL_NM_REVW_STATUS = "Review";

  private static final String COL_RVW_DESC = "Review Description";

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

  /**
   * @param editor form editor
   * @param pageId page id
   * @param title title
   */
  public CompHexWithCdfNatPage(final FormEditor editor, final String pageId, final String title) {
    super(editor, pageId, title);
    this.editor = (CompHexWithCDFxEditor) editor;
    this.compHexDataHdlr = ((CompHexWithCDFxEditorInput) editor.getEditorInput()).getCompHexDataHdlr();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(this.editor.getEditorInput().getName());

    if (getCompHexDataHdlr().getCdrReportData().getCdrReport().getConsiderReviewsOfPrevPidcVers()) {
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

  /**
   * {@inheritDoc}
   */
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
        Rectangle rect = CompHexWithCdfNatPage.this.scrollComp.getClientArea();
        CompHexWithCdfNatPage.this.scrollComp.setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
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
    Set<CompHexWithCDFParam> inputSet = this.compHexDataHdlr.getCompHexResultParamSet();

    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);

    // initialising the filter
    this.toolBarFilters = new CompHexWithCdfToolBarFilter(this.compHexDataHdlr);
    createComparisonTabViewer(inputSet);
    // type filter listener
    addModifyListenerForFilterTxt();


    // create tool bar filters
    createToolbarFilters();


    // create stat labels
    createStatCountLabels(this.statComp);
    // create qnaire status count
    createQnaireStatusCountLabels(this.qnaireStatComp);

    // mouse listener
    addMouseListener();
  }

  /**
   * @param qnaireStatComp as composite
   */
  private void createQnaireStatusCountLabels(final Composite qnaireStatComp) {
    LabelUtil.getInstance().createLabel(qnaireStatComp, "Parameters in Bosch Responsibility Reviewed :");
    LabelUtil.getInstance().createLabel(qnaireStatComp, TXT_FILLER +
        CommonUiUtils.displayLangBasedPercentage(this.compHexDataHdlr.getReviewedParameterWithBoschResp()) + "%");
    LabelUtil.getInstance().createLabel(qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(qnaireStatComp, "Number of Parameters in Bosch Responsibility :");
    LabelUtil.getInstance().createLabel(qnaireStatComp, TXT_FILLER + this.compHexDataHdlr.getParameterInBoschResp());
    LabelUtil.getInstance().createLabel(qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(qnaireStatComp,
        "Parameters in Bosch Responsibility with Completed Questionnaire :");
    LabelUtil.getInstance().createLabel(qnaireStatComp, TXT_FILLER +
        CommonUiUtils.displayLangBasedPercentage(this.compHexDataHdlr.getRvwParamWithBoschRespForCompletedQnaire()) +
        "%");
    LabelUtil.getInstance().createLabel(qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(qnaireStatComp, "Number of Parameters in Bosch Responsibility Reviewed :");
    LabelUtil.getInstance().createLabel(qnaireStatComp,
        TXT_FILLER + this.compHexDataHdlr.getParameterInBoschRespRvwed());
    LabelUtil.getInstance().createLabel(qnaireStatComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(qnaireStatComp, "Number of questionnaires with negative answer included :");
    LabelUtil.getInstance().createLabel(qnaireStatComp,
        TXT_FILLER + this.compHexDataHdlr.getQnaireWithNegativeAnswersCount());
    LabelUtil.getInstance().createLabel(qnaireStatComp, LABEL_FILLER);
  }


  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(modifyEvent -> {

      String text = CompHexWithCdfNatPage.this.filterTxt.getText().trim();
      CompHexWithCdfNatPage.this.allColumnFilterMatcher.setFilterText(text, true);
      CompHexWithCdfNatPage.this.compRprtFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      CompHexWithCdfNatPage.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(CompHexWithCdfNatPage.this.compRprtFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMsgAndStatHdr(false);
    });
  }

  /**
   * create toolbar and filters
   */
  private void createToolbarFilters() {

    this.toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = this.toolBarManager.createControl(this.section);
    final Separator separator = new Separator();

    CompHexWithCDFToolbarActionSet toolBarActionSet =
        new CompHexWithCDFToolbarActionSet(this.compRprtFilterGridLayer, this);

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


    CalibrationStatusListExportAction calibrationStatusListExportAction =
        new CalibrationStatusListExportAction(null, this.compHexDataHdlr, false);
    this.form.getToolBarManager().add(calibrationStatusListExportAction);

    CompHexCheckSSDReportAction exportSSDReportAction = new CompHexCheckSSDReportAction(this.compHexDataHdlr);
    this.form.getToolBarManager().add(exportSSDReportAction);

    CompHexRevExportPdfAction exportPdfAction =
        new CompHexRevExportPdfAction(CompHexWithCdfNatPage.this.compHexDataHdlr);

    this.form.getToolBarManager().add(exportPdfAction);

    CompHexZipDownloadAction exportZipAction = new CompHexZipDownloadAction(this.compHexDataHdlr);
    this.form.getToolBarManager().add(exportZipAction);

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
  private void createStatCountLabels(final Composite statComp) {
    // total count and statistics
    this.compHexDataHdlr.getCompHexStatitics()
        .setStatFilteredParam(this.compRprtFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());

    LabelUtil.getInstance().createLabel(statComp, "Total Parameters in A2L :");
    LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatTotalParamInA2L());
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    // Dynamic Filter labels for statistics
    LabelUtil.getInstance().createLabel(statComp, "Filtered Parameters :");
    this.filterParamStatLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParam());
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(statComp,
        COMPLI_FAILED + "(" + CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType() + ") :");
    Label compliCSSDFailedLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatCompliCssdFailed() + TXT_FILLER);
    if (this.compHexDataHdlr.getCompHexStatitics().getStatCompliCssdFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(compliCSSDFailedLbl);
    }
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(statComp, "Compli parameters in A2L :");
    LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatCompliParamInA2L());
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(statComp, "Parameters Reviewed :");
    LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatParamReviewed());
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(statComp, "Filtered Parameters reviewed :");
    this.filterParamRvwdStatLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParamRvwd());
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);


    LabelUtil.getInstance().createLabel(statComp,
        COMPLI_FAILED + "(" + CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType() + ") :");
    Label compliSSD2RvFailedLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatCompliSSDRvFailed() + TXT_FILLER);
    if (this.compHexDataHdlr.getCompHexStatitics().getStatCompliSSDRvFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(compliSSD2RvFailedLbl);
    }
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);


    LabelUtil.getInstance().createLabel(statComp, "Compli parameters passed :");
    LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatCompliParamPassed());
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    // Param Reviewed not equal
    LabelUtil.getInstance().createLabel(statComp, "Parameter reviewed NOT equal :");
    Label rvwdNotEqLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatParamRvwdNotEqual() + TXT_FILLER);
    if (this.compHexDataHdlr.getCompHexStatitics().getStatParamRvwdNotEqual() > 0) {
      LabelUtil.getInstance().setErrorStyle(rvwdNotEqLbl);
    }
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    // Filtered Param Reviewed not equal
    LabelUtil.getInstance().createLabel(statComp, "Filtered Parameter reviewed NOT equal :");
    this.filterRvwdNotEqlStatLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParamRvwdNotEqual() + TXT_FILLER);
    if (this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParamRvwdNotEqual() > 0) {
      LabelUtil.getInstance().setErrorStyle(this.filterRvwdNotEqlStatLbl);
    }
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(statComp,
        COMPLI_FAILED + "(" + CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType() + ") :");
    Label compliNoRuleFailedLbl = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatCompliNoRuleFailed() + TXT_FILLER);
    if (this.compHexDataHdlr.getCompHexStatitics().getStatCompliNoRuleFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(compliNoRuleFailedLbl);
    }
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);

    LabelUtil.getInstance().createLabel(statComp, "Q-SSD parameters failed :");
    Label qssdParamFailed = LabelUtil.getInstance().createLabel(statComp,
        TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatQSSDParamFailed());
    if (this.compHexDataHdlr.getCompHexStatitics().getStatQSSDParamFailed() > 0) {
      LabelUtil.getInstance().setErrorStyle(qssdParamFailed);
    }
    LabelUtil.getInstance().createLabel(statComp, LABEL_FILLER);


  }


  /**
   * create the NAT table viewer
   *
   * @param inputSet
   * @param compHexDataHdlr2
   */
  private void createComparisonTabViewer(final Set<CompHexWithCDFParam> inputSet) {
    // set column names and width for static columns
    ConcurrentMap<Integer, Integer> columnWidthMap = setHeaderNameColWidthForStaticCols();

    this.totTableRowCount = inputSet.size();
    // create nat table
    IConfigRegistry configRegistry = createNatTable(columnWidthMap, inputSet);

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());


    // initailise all column filter
    this.allColumnFilterMatcher = new CompHexWithCdfAllColMatcher(this.compHexDataHdlr);
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
    this.natTable.addConfiguration(new CompHEXCDFEditConfiguration());

    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(CompHexWithCdfNatPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTable1, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      CompHexWithCdfNatPage.this.resetState = true;
                      CompHexWithCdfNatPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // add the label accumulator
    DataLayer bodyDataLayer = this.compRprtFilterGridLayer.getDummyDataLayer();
    IRowDataProvider<CompHexWithCDFParam> bodyDataProvider =
        (IRowDataProvider<CompHexWithCDFParam>) bodyDataLayer.getDataProvider();
    final CompHEXCDFLabelAccumulator rvwReportLabelAccumulator =
        new CompHEXCDFLabelAccumulator(bodyDataLayer, bodyDataProvider, this.compHexDataHdlr.getCdrReportData());
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
    setStatusBarMsgAndStatHdr(false);
    getSite().setSelectionProvider(this.selectionProvider);
  }

  /**
   *
   */
  private void attachTooltip() {
    // Icdm-1208- Custom tool tip for Nat table.
    DefaultToolTip toolTip = new CompHexNatToolTip(this.natTable, new String[0], this);
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
        if (selection.getFirstElement() instanceof CompHexWithCDFParam) {
          final CompHexWithCDFParam compHEXWtihCDFParam = (CompHexWithCDFParam) selection.getFirstElement();
          // case-1 : no dialog had opened, if reviewResultEditor.getCalDataViewerDialog() is null, this condition
          // satifies
          // case-2 : Dialog already opened and still in that position, if
          // reviewResultEditor.getCalDataViewerDialog().getShell() is not null, this condition satifies
          CompHexWithCdfNatActionSet compHexWithCdfNatActionSet = new CompHexWithCdfNatActionSet(this.compHexDataHdlr);
          CompHexWithCDFxEditor compHexWithCDFxEditor = (CompHexWithCDFxEditor) getEditor();
          // ICDM-2304
          if ((null != compHexWithCDFxEditor.getSynchCalDataViewerDialog()) &&
              (null != compHexWithCDFxEditor.getSynchCalDataViewerDialog().getShell())) {
            compHexWithCdfNatActionSet.showTableGraphAction(compHEXWtihCDFParam, CompHexWithCdfNatPage.this.editor,
                true);
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

  private void leftMouseDoubleClickAction(final MouseEvent mouseEvent) {
    ILayerCell cell = this.natTable.getCellByPosition(this.natTable.getColumnPositionByX(mouseEvent.x),
        this.natTable.getRowPositionByY(mouseEvent.y));
    if ((CommonUtils.isNotNull(cell)) &&
        (CommonUtils.isEqual(cell.getColumnIndex(), CommonUIConstants.REVIEW_DESCRIPTION_INDEX))) {
      int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(mouseEvent.y),
          ((CustomFilterGridLayer<CompHexWithCDFParam>) this.natTable.getLayer()).getDummyDataLayer());
      Object rowObject = this.compRprtFilterGridLayer.getBodyDataProvider().getRowObject(row);

      new CommonActionSet().openEditorForReviewDescCol(this.compHexDataHdlr.getCdrReportData(), rowObject,
          this.compHexDataHdlr.getSelctedVar());

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
      this.filterParamRvwdStatLbl
          .setText(TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParamRvwd());
      this.filterRvwdNotEqlStatLbl
          .setText(TXT_FILLER + this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParamRvwdNotEqual());
      if (this.compHexDataHdlr.getCompHexStatitics().getStatFilteredParamRvwdNotEqual() > 0) {
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
   * @param filteredList the list
   */
  private void updateFilteredDataCount(final List<CompHexWithCDFParam> filteredList) {
    int statFilteredParamRvwd = 0;
    int statFilteredRvwdNtEqual = 0;
    for (CompHexWithCDFParam item : filteredList) {
      if (item.isReviewed()) {
        statFilteredParamRvwd++;
      }
      if (item.isReviewed() && !item.isEqual()) {
        statFilteredRvwdNtEqual++;
      }
    }
    this.compHexDataHdlr.getCompHexStatitics().setStatFilteredParamRvwd(statFilteredParamRvwd);
    this.compHexDataHdlr.getCompHexStatitics().setStatFilteredParamRvwdNotEqual(statFilteredRvwdNtEqual);
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
          (IStructuredSelection) CompHexWithCdfNatPage.this.selectionProvider.getSelection();
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

  // Retrieves a PidcVarRvwDetails object for an external link based on the provided selection, parameter name,and
  // CdrReportDataHandler.

  private PidcVarRvwDetails getResultObjectForExtLink(final Object selection, final String paramName,
      final CdrReportDataHandler cdrReportData) {
    // Create a new PidcVarRvwDetails object to store the result
    PidcVarRvwDetails pidcVarRvwDetails = new PidcVarRvwDetails();

    // Check if the selection is an instance of CompHexWithCDFParam
    if (selection instanceof CompHexWithCDFParam) {
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

    if (firstElement instanceof CompHexWithCDFParam) {

      // Show table graph
      final CompHexWithCdfNatActionSet compHexWithCdfNatActionSet =
          new CompHexWithCdfNatActionSet(this.compHexDataHdlr);
      compHexWithCdfNatActionSet.showUnSynchronizedTableGraph(menuManagr, firstElement,
          CompHexWithCdfNatPage.this.editor);
      compHexWithCdfNatActionSet.showSynchronizedTableGraph(menuManagr, firstElement,
          CompHexWithCdfNatPage.this.editor);

      // Show compli rule history
      CompHexWithCDFParam compHexCdfxData = (CompHexWithCDFParam) firstElement;
      String paramName = compHexCdfxData.getParamName();
      if (compHexCdfxData.isCompli()) {
        menuManagr.add(new Separator());
        ReviewRule cdrRule = getCDRRule(paramName);
        (new ReviewRuleActionSet()).showRuleHistory(menuManagr, IMessageConstants.OPEN_COMPLI_RULE_HISTORY, getTitle(),
            cdrRule, true, false, this.compHexDataHdlr.getCdrReportData(), compHexCdfxData.getFuncName());
      }

      // show qssd rule history
      if (compHexCdfxData.isQssdParameter()) {
        menuManagr.add(new Separator());
        ReviewRule cdrRule = getCDRRuleForQssd(paramName);
        (new ReviewRuleActionSet()).showRuleHistory(menuManagr, IMessageConstants.OPEN_QSSD_RULE_HISTORY, getTitle(),
            cdrRule, false, true, this.compHexDataHdlr.getCdrReportData(), compHexCdfxData.getFuncName());

      }

      // Show rule history
      SelectionLayer selectionLayer = this.compRprtFilterGridLayer.getBodyLayer().getSelectionLayer();
      PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();


      if (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEWED_INDEX) {
        // Open Review Result menu
        contextMenuForReviewValCol(menuManagr, this.compHexDataHdlr.getCdrReportData(), firstElement);
        // Opem Ruleset editor
        ReviewActionSet reviewEditorActionSet = new ReviewActionSet();
        reviewEditorActionSet.addReviewParamEditor(menuManagr, this.compHexDataHdlr.getA2lParam(paramName), null, null,
            true);
      }
      // For Adding Context Menu
      CdrReportDataHandler cdrReportData = this.compHexDataHdlr.getCdrReportData();
      addContextMenuForReviewResultCols(menuManagr, seperator, paramName, selectedCellPositions, cdrReportData);


      // Check if the selected cell position corresponds to the review description column
      if (selectedCellPositions[CommonUIConstants.COLUMN_INDEX_0].columnPosition == CommonUIConstants.REVIEW_DESCRIPTION_INDEX) {

        // Obtain the result object for the external link based on certain parameters
        final PidcVarRvwDetails cdrResult = getResultObjectForExtLink(firstElement, paramName, cdrReportData);

        // Determine the link object based on whether review variant is null or not
        final Object linkObj =
            cdrResult.getReviewVariant() == null ? cdrResult.getReviewResult() : cdrResult.getReviewVariant();

        // Create a new Action for copying the link to the clipboard
        final Action copyLink = new Action() {

          @Override
          public void run() {
            // ICDM-1649
            // Attempt to copy the link to the clipboard using a LinkCreator
            try {
              new LinkCreator(linkObj).copyToClipBoard();
            }
            catch (ExternalLinkException exp) {
              CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }
          }

        };
        // Set the text, image descriptor, and add the copyLink action to the context menu
        copyLink.setText("Copy Review Result Link");
        copyLink.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.CDR_RES_LINK_16X16));

        menuManagr.add(copyLink);
      }


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
            this.compHexDataHdlr, false);
      }
    }

  }


  /**
   * @param menuManagr
   * @param seperator
   * @param paramName
   * @param selectedCellPositions
   * @param cdrReportData
   */
  private void addContextMenuForReviewResultCols(final MenuManager menuManagr, final Separator seperator,
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
          this.compHexDataHdlr.getSelctedVar() != null ? this.compHexDataHdlr.getSelctedVar().getId() : null);
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
    if ((this.compHexDataHdlr == null) ||
        (this.compHexDataHdlr.getCompHexResponse().getSsdRulesForCompliance() == null) ||
        !CommonUtils.isNotEmpty(this.compHexDataHdlr.getCompHexResponse().getSsdRulesForCompliance())) {
      return null;
    }
    if (CommonUtils.isNotEmpty(this.compHexDataHdlr.getCompHexResponse().getSsdRulesForCompliance().get(paramName))) {
      return this.compHexDataHdlr.getCompHexResponse().getSsdRulesForCompliance().get(paramName).get(0);
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
    if ((this.compHexDataHdlr == null) ||
        CommonUtils.isNullOrEmpty(this.compHexDataHdlr.getCompHexResponse().getSsdRulesForQssd())) {
      return null;
    }
    if (CommonUtils.isNotEmpty(this.compHexDataHdlr.getCompHexResponse().getSsdRulesForQssd().get(paramName))) {
      return this.compHexDataHdlr.getCompHexResponse().getSsdRulesForQssd().get(paramName).get(0);
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
    String paramName = ((CompHexWithCDFParam) firstElement).getParamName();
    CDRReviewResult reviewResult = cdrReportData.getReviewResult(paramName, 0);
    if (reviewResult != null) {
      new CommonActionSet().openReviewResultAction(menuMgr, reviewResult, paramName,
          this.compHexDataHdlr.getSelctedVar() != null ? this.compHexDataHdlr.getSelctedVar().getId() : null);
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
          Arrays.asList(CommonUtils.concatenate(ApicConstants.REVIEWED, CompHexWithCdfNatPage.TXT_FILLER),
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

      // Configure for review description
      List<String> rvwDescList = Arrays.asList(CDRConstants.NO_REVIEW_RESULT_FOUND);

      ICellEditor rvwBoxCellEditor = new ComboBoxCellEditor(rvwDescList);

      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, rvwBoxCellEditor, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.REVIEW_DESCRIPTION_INDEX);

      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_RVW_DESC), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.REVIEW_DESCRIPTION_INDEX);


      // For Questionniare Status filter
      List<String> qniareStatusList = Arrays.asList(CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getUiType(),
          CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getUiType(),
          CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getUiType());


      ComboBoxCellEditor comboBoxCellEditor1 = new ComboBoxCellEditor(qniareStatusList);

      configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor1, NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.QNAIRE_STATUS);
      // Configure Filter Display Converter
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
          new FilterDisplayConverter(COL_QUESTIONNIARE_STATUS), NORMAL,
          FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + CommonUIConstants.QNAIRE_STATUS);


      // combo for Equal column
      comboList =
          Arrays.asList(CommonUtils.concatenate(RESULT_STATUS.EQUAL.getText(), CompHexWithCdfNatPage.TXT_FILLER),
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
   * @param inputSet
   * @param compHexDataHdlr2
   * @return
   */
  private IConfigRegistry createNatTable(final ConcurrentMap<Integer, Integer> columnWidthMap,
      final Set<CompHexWithCDFParam> inputSet) {
    // instantiate the input to column converter
    CompHexWithCdfNatInputToColConvertr natInputToColumnConverter =
        new CompHexWithCdfNatInputToColConvertr(this.compHexDataHdlr.getCdrReportData());
    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.compRprtFilterGridLayer = new CustomFilterGridLayer<>(configRegistry, inputSet, columnWidthMap,
        new CustomCompHexColPropertyAccessor<>(), new CompHexColumnHdrDataProvider(), getParamComparator(2),
        natInputToColumnConverter, this, null, true, true, false);
    this.outlineNatFilter = new CompHEXCDFOutlineFilter(this.compRprtFilterGridLayer, this.compHexDataHdlr.getA2lFile(),
        this.compHexDataHdlr.getCdrReportData().getA2lEditorDataProvider(), this.compHexDataHdlr.getCdrReportData());
    this.outlineNatFilter.setWpType(
        this.compHexDataHdlr.getCdrReportData().getA2lEditorDataProvider().getA2lFileInfoBO().getMappingSourceID());
    this.compRprtFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outlineNatFilter.getOutlineMatcher());

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
   * @author mkl2cob
   * @param <T>
   */
  class CustomCompHexColPropertyAccessor<T> implements IColumnAccessor<T> {

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
      return CompHexWithCdfNatPage.this.propertyToLabelMap.size();
    }
  }

  /**
   * Column header data provider class
   *
   * @author mkl2cob
   */
  class CompHexColumnHdrDataProvider implements IDataProvider {

    /**
     * @param columnIndex int
     * @return String column header label
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = CompHexWithCdfNatPage.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    @Override
    public int getColumnCount() {
      return CompHexWithCdfNatPage.this.propertyToLabelMap.size();
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
  private Comparator<CompHexWithCDFParam> getParamComparator(final int columnNum) {

    return (compParam1, compParam2) -> {
      int ret = 0;
      CdrReportDataHandler cdrReportData = CompHexWithCdfNatPage.this.compHexDataHdlr.getCdrReportData();
      switch (columnNum) {
        case 0:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_PARAM_COMPLI);
          break;
        case 1:
          ret =
              CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2, SortColumns.SORT_PARAM_TYPE);
          break;
        case 2:
          ret =
              CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2, SortColumns.SORT_PARAM_NAME);
          break;
        case 3:
          ret =
              CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2, SortColumns.SORT_FUNC_NAME);
          break;
        case 4:
          ret =
              CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2, SortColumns.SORT_FUNC_VERS);
          break;
        case 5:
          ret = ApicUtil.compare(cdrReportData.getWpName(compParam1.getParamName()),
              cdrReportData.getWpName(compParam2.getParamName()));
          break;
        case 6:
          // Compare Responsibility Types
          ret = ApicUtil.compare(cdrReportData.getRespType(compParam1.getParamName()),
              cdrReportData.getRespType(compParam2.getParamName()));
          break;
        case 7:
          // comparing responsibilities
          ret = ApicUtil.compare(cdrReportData.getRespName(compParam1.getParamName()),
              cdrReportData.getRespName(compParam2.getParamName()));
          break;
        case 8:
          ret = ApicUtil.compare(cdrReportData.getLatestA2LVersion(compParam1.getParamName()),
              cdrReportData.getLatestA2LVersion(compParam2.getParamName()));
          break;
        case 9:
          ret = ApicUtil.compare(cdrReportData.getWpFinishedRespStatuswithName(compParam1.getParamName()),
              cdrReportData.getWpFinishedRespStatuswithName(compParam2.getParamName()));
          break;
        case 10:
          ret = ApicUtil.compare(cdrReportData.getLatestFunctionVersion(compParam1.getParamName()),
              cdrReportData.getLatestFunctionVersion(compParam2.getParamName()));
          break;
        case 11:
          // comparing whether the paramter is reviewed or not
          ret = ApicUtil.compare(cdrReportData.getQnaireRespVersStatus(compParam1.getParamName(), false),
              cdrReportData.getQnaireRespVersStatus(compParam2.getParamName(), false));
          break;
        case 12:
          // comparing whether the paramter is reviewed or not
          ret = ApicUtil.compare(cdrReportData.getReviewScore(compParam1.getParamName()),
              cdrReportData.getReviewScore(compParam2.getParamName()));
          break;
        case 13:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_PARAM_EQUAL);
          break;
        case 14:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_HEX_CALDATA);
          break;
        case 15:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_CDF_CALDATA);
          break;
        case 16:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_COMPLI_SHAPE_RESULT);
          break;
        case CommonUIConstants.REVIEW_SCORE_INDEX:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_REVIEW_SCORE);
          break;
        case 18:
          ret = CompHexWithCdfNatPage.this.compHexDataHdlr.compareTo(compParam1, compParam2,
              SortColumns.SORT_REVIEW_COMMENTS);
          break;
        case 19:
          ret = ApicUtil.compare(cdrReportData.getReviewResultName(compParam1.getParamName()),
              cdrReportData.getReviewResultName(compParam2.getParamName()));
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
    Set<CompHexWithCDFParam> inputSet = this.compHexDataHdlr.getCompHexResultParamSet();
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

  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart)) {
      selectionListener(selection);
    }
  }

  /**
   * Selection listener implementation for selections on outlineFilter
   *
   * @param selection
   */
  private void selectionListener(final ISelection selection) {

    this.outlineNatFilter.a2lOutlineSelectionListener(selection);
    if (this.editor.getActivePage() == 0) {
      setStatusBarMsgAndStatHdr(true);
    }
  }

  /**
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  public void setStatusBarMsgAndStatHdr(final boolean outlineSelection) {

    int totalItemCount = this.totTableRowCount;
    int filteredItemCount = this.compRprtFilterGridLayer.getRowHeaderLayer().getPreferredRowCount();
    final StringBuilder buf = new StringBuilder(40);
    buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount).append(" records ");
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      // in case of outline selection
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(ApicUiConstants.OUTLINE_TREE_VIEW).getSite();
      // get the status line manager from the outline
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    else {
      // get the status line manager from the editor
      statusLine = this.editor.getEditorSite().getActionBars().getStatusLineManager();
    }
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
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMsgAndStatHdr(outlineSelection);
  }

  /**
   * @return the compRprtFilterGridLayer
   */
  public CustomFilterGridLayer<CompHexWithCDFParam> getCompRprtFilterGridLayer() {
    return this.compRprtFilterGridLayer;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * @return the compHexDataHdlr
   */
  public CompHexWithCDFxDataHandler getCompHexDataHdlr() {
    return this.compHexDataHdlr;
  }
}
