/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.QuestionEditMouseClickAction;
import com.bosch.caltool.cdr.ui.actions.QuestionaireToolBarActionSet;
import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditor;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.FilterRowCustomConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QuestionaireEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QuestionaireLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QuestionnaireColHeaderLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QuestionnaireNatToolTip;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.QuestionnaireUpdateDataCommandHandler;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ReviewQuestionaireNatInputToColConverter;
import com.bosch.caltool.cdr.ui.table.filters.QuestionColumnFilterMatcher;
import com.bosch.caltool.cdr.ui.table.filters.QuestionaireEditorToolBarFilters;
import com.bosch.caltool.cdr.ui.table.filters.QuestionnaireOutlineNatFilter;
import com.bosch.caltool.cdr.ui.views.ReviewQuestionnaireOutlinePage;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO.SortColumns;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * First page of Questionare editor
 *
 * @author dmo5cob
 */
public class QuestionDetailsPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   * Col width - Name
   */
  private static final int COL_WIDTH_Q_NAME = 15 * 15;
  /**
   * Col width - Number
   */
  private static final int COL_WIDTH_Q_NUM = 3 * 15;
  /**
   * Col width - Hint
   */
  private static final Integer COL_WIDTH_Q_HINT = 20 * 15;
  /**
   * Col width - Config columns
   */
  private static final Integer COL_WIDTH_Q_CONFIG = 8 * 15;
  /**
   *
   */
  private static final int YCOORDINATE_TEN = 10;
  /**
   *
   */
  private static final int XCOORDINATE_TEN = 10;
  /**
   * ReviewQuestionaireEditor instance
   */
  private final ReviewQuestionaireEditor editor;

  private QnaireDefBO qnaireDefBo;

  /**
   * Form instance
   */
  private Form nonScrollableForm;
  /**
   * SashForm instance
   */
  private SashForm mainComposite;
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
  private Section tableSection;
  /**
   * Form insatnce
   */
  private Form tableForm;
  /**
   * Text instance
   */
  private Text filterTxt;
  /**
   * CustomFilterGridLayer<?> instance
   */
  private CustomFilterGridLayer<Question> questionsFilterGridLayer;
  /**
   * CustomNATTable instance
   */
  private CustomNATTable natTable;
  /**
   * QuestionColumnFilterMatcher<Question> instance
   */
  private QuestionColumnFilterMatcher<Question> allColumnFilterMatcher;
  /**
   * ToolBarManager instance
   */
  private ToolBarManager toolBarManager;
  /**
   * RowSelectionProvider<?> instance
   */
  private RowSelectionProvider<Question> selectionProvider;
  /**
   * Total row count
   */
  private int totTableRowCount;


  private QuestionaireEditorToolBarFilters toolBarFilters;
  private QuestionnaireOutlineNatFilter outlineNatFilter;
  private QuestionaireToolBarActionSet toolBarActionSet;
  /**
   * The selected instance of Question in the tableviewer
   */
  protected Question selectedQues;
  private QuestionnaireColHeaderLabelAccumulator columnHeaderLabelAccumulator;
  private HashMap<Integer, String> propertyToLabelMap;
  /**
   * CUSTOM_COMPARATOR Label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  private GroupByHeaderLayer groupByHeaderLayer;

  /**
   * @param editor ReviewQuestionaireEditor instance
   * @param qnaireDefBo QnaireDefBo
   */
  public QuestionDetailsPage(final FormEditor editor, final QnaireDefBO qnaireDefBo) {
    super(editor, "Questionaire Details", "Question Definition");
    this.editor = (ReviewQuestionaireEditor) editor;
    this.qnaireDefBo = qnaireDefBo;
  }


  /**
   *
   */
  public void reconstructNatTable() {

    // normal refresh for update operation inside same qnaire resp versions
    this.questionsFilterGridLayer.getEventList().clear();
    this.questionsFilterGridLayer.getEventList().addAll(this.qnaireDefBo.getQnaireDefModel().getQuestionMap().values());
    this.totTableRowCount = this.qnaireDefBo.getQnaireDefModel().getQuestionMap().values().size();
    this.questionsFilterGridLayer.getEventList().sort(getQuestionsComparator(SortColumns.SORT_QUES_NUMBER));
    this.natTable.doCommand(new StructuralRefreshCommand());
    this.natTable.doCommand(new VisualRefreshCommand());
    this.natTable.refresh();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }
  }


  /**
   * @return the qnaireDefBo
   */
  public QnaireDefBO getQnaireDefBo() {
    return this.qnaireDefBo;
  }


  /**
   * @param qnaireDefBo the qnaireDefBo to set
   */
  public void setQnaireDefBo(final QnaireDefBO qnaireDefBo) {
    this.qnaireDefBo = qnaireDefBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(this.groupByHeaderLayer, false);
  }

  /**
   *
   */
  public void refreshOutlineViewer() {
    // Refresh outline navigator filter
    final IViewPart viewPartObj =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(OutlineViewPart.PART_ID);
    if (viewPartObj instanceof OutlineViewPart) {
      final OutlineViewPart ucViewPart = (OutlineViewPart) viewPartObj;
      if (ucViewPart.getCurrentPage() instanceof ReviewQuestionnaireOutlinePage) {
        ReviewQuestionnaireOutlinePage currentPage = (ReviewQuestionnaireOutlinePage) ucViewPart.getCurrentPage();
        currentPage.getViewer().refresh();

      }
    }
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

    this.nonScrollableForm.setText(this.qnaireDefBo.getNameForPart() + " - Definition of Questions");

    final GridLayout gridLayout = new GridLayout();

    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
  *
  */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createInnerComposite();
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   *
   */
  private void createInnerComposite() {
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.verticalAlignment = GridData.FILL;
    this.composite = new Composite(this.mainComposite, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    createTableViewerSection();
    this.composite.setLayoutData(gridData1);
  }

  /**
   *
   */
  private void createTableViewerSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.tableSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Define Questions");
    this.tableSection.setLayoutData(gridData);
    createTableForm(this.formToolkit);
    this.tableSection.setClient(this.tableForm);

    this.tableSection.getDescriptionControl().setEnabled(false);

  }

  /**
   * Create filter text
   */
  private void createFilterTxt() {
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.filterTxt.setMessage("type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = QuestionDetailsPage.this.filterTxt.getText().trim();
      QuestionDetailsPage.this.allColumnFilterMatcher.setFilterText(text, true);
      QuestionDetailsPage.this.questionsFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);

      QuestionDetailsPage.this.questionsFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(QuestionDetailsPage.this.questionsFilterGridLayer.getSortableColumnHeaderLayer()));

      setStatusBarMessage(QuestionDetailsPage.this.groupByHeaderLayer, false);

    });
  }


  /**
   * @param formToolkit2
   */
  private void createTableForm(final FormToolkit toolkit) {
    // create table form
    this.tableForm = toolkit.createForm(this.tableSection);

    createFormToolBarActions();

    // create filter text
    this.filterTxt = toolkit.createText(this.tableForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    // get parameters
    this.tableForm.getBody().setLayout(new GridLayout());

    createNatTable();
    createToolBarAction();

  }

  /**
   *
   */
  private void createFormToolBarActions() {
    this.toolBarActionSet = new QuestionaireToolBarActionSet(this, this.questionsFilterGridLayer);
    ToolBarManager secToolBarManager = (ToolBarManager) this.tableForm.getToolBarManager();
    this.toolBarActionSet.createAddQuestionAction(secToolBarManager);
    this.toolBarActionSet.createEditQuestionAction(secToolBarManager);
    final Separator separator = new Separator();
    secToolBarManager.add(separator);
    // iCDM-1968
    // passing the questionare version;from QnaireDefBO in order to get Questionnaire Version with latest changes
    this.toolBarActionSet.createEditQuestionaireAction(secToolBarManager,
        this.editor.getEditorInput().getQnaireDefBo());
    secToolBarManager.update(true);
  }

  /**
   *
   */
  private void createNatTable() {
    // The below map is used by NatTable to Map Columns with their respective names
    this.propertyToLabelMap = new HashMap<>();
    Map<Integer, Integer> columnWidthMap = new HashMap<>();
    // Configure columns
    configureColumnsNATTable(columnWidthMap);

    // NatInputToColumnConverter is used to convert Questionare (which is given as input to nattable viewer)
    // to the respective column values
    ReviewQuestionaireNatInputToColConverter natInputToColumnConverter =
        new ReviewQuestionaireNatInputToColConverter(this.qnaireDefBo);
    IConfigRegistry configRegistry = new ConfigRegistry();
    // Group by model
    GroupByModel groupByModel = new GroupByModel();
    List<Integer> colsToHide = new ArrayList<>();
    addColsToHideBasedOnRelHideFlags(colsToHide);

    // A Custom Filter Grid Layer is constructed

    this.totTableRowCount = this.qnaireDefBo.getQnaireDefModel().getQuestionMap().size();


    this.questionsFilterGridLayer = new CustomFilterGridLayer<>(configRegistry,
        this.qnaireDefBo.getQnaireDefModel().getQuestionMap().values(), this.propertyToLabelMap, columnWidthMap,
        getQuestionsComparator(SortColumns.SORT_QUES_NUMBER), natInputToColumnConverter, this,
        this.qnaireDefBo.isModifiable() ? new QuestionEditMouseClickAction(this) : null, groupByModel, colsToHide,
        false, true, null, null, false);

    // Enable Tool bar filters
    this.toolBarFilters = new QuestionaireEditorToolBarFilters();
    this.questionsFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    // Enable column filters
    this.allColumnFilterMatcher = new QuestionColumnFilterMatcher<>(this.qnaireDefBo, null);
    this.questionsFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);


    this.outlineNatFilter = new QuestionnaireOutlineNatFilter(this.questionsFilterGridLayer, this.qnaireDefBo);
    this.questionsFilterGridLayer.getFilterStrategy()
        .setOutlineNatFilterMatcher(this.outlineNatFilter.getOutlineMatcher());

    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.questionsFilterGridLayer,
        this.questionsFilterGridLayer.getColumnHeaderDataProvider());


    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.questionsFilterGridLayer, 0, 1);
    // // Status bar message listener
    this.questionsFilterGridLayer.getBodyDataLayer().getTreeRowModel()
        .registerRowGroupModelListener(() -> setStatusBarMessage(QuestionDetailsPage.this.groupByHeaderLayer, false));

    this.columnHeaderLabelAccumulator =
        new QuestionnaireColHeaderLabelAccumulator(this.questionsFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.questionsFilterGridLayer.getColumnHeaderDataLayer()
        .setConfigLabelAccumulator(this.columnHeaderLabelAccumulator);


    // Create NAT table
    this.natTable = new CustomNATTable(
        this.tableForm.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED |
            SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        compositeGridLayer, false, this.getClass().getSimpleName());

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    // Add configurations
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    CustomNatTableStyleConfiguration natTabConfig = new CustomNatTableStyleConfiguration();
    natTabConfig.setEnableAutoResize(true);
    this.natTable.addConfiguration(natTabConfig);
    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));
    addFilterConfiguration();
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      protected PopupMenuBuilder createColumnHeaderMenu(final NatTable localNatTable) {
        return super.createColumnHeaderMenu(localNatTable).withColumnChooserMenuItem();
      }
    });

    addComboBoxConfiguration();

    // Configure NAT table
    this.natTable.configure();


    // Column chooser configuration
    CustomDefaultBodyLayerStack bodyLayer = this.questionsFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.questionsFilterGridLayer.getColumnHeaderLayer(),
            this.questionsFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);


    this.selectionProvider =
        new RowSelectionProvider<>(this.questionsFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.questionsFilterGridLayer.getBodyDataProvider(), false);

    this.selectionProvider.addSelectionChangedListener(event -> {

      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if ((selection == null) || (selection.getFirstElement() == null)) {
        QuestionDetailsPage.this.selectedQues = null;
      }
      else {
        if (selection.getFirstElement() instanceof Question) {
          QuestionDetailsPage.this.selectedQues = (Question) selection.getFirstElement();
          QuestionDetailsPage.this.toolBarActionSet.enableOrDisableAddAction();
        }
      }

      // maintain selection while traversing between pages
      if ((selection != null) && selection.isEmpty()) {
        // TODO
      }
    }

    );

    attachToolTip(this.natTable);
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content
   *
   * @param localNatTable
   */
  private void attachToolTip(final NatTable localNatTable) {
    // Custom tool tip for Nat table.
    DefaultToolTip toolTip = new QuestionnaireNatToolTip(localNatTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(XCOORDINATE_TEN, YCOORDINATE_TEN));
  }

  /**
   * @param colsToHide
   */
  private void addColsToHideBasedOnRelHideFlags(final List<Integer> colsToHide) {
    if (CommonUtils
        .getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getMeasurementHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_3);
    }
    if (CommonUtils.getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getSeriesHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_4);
    }
    if (CommonUtils.getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getLinkHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_5);
    }
    if (CommonUtils.getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getRemarksHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_6);
    }
    if (CommonUtils.getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getOpenPointsHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_7);
    }
    if (CommonUtils.getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getMeasureHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_8);
    }
    if (CommonUtils
        .getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getResponsibleHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_9);
    }
    if (CommonUtils
        .getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getCompletionDateHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_10);
    }
    if (CommonUtils.getBooleanType(this.editor.getEditorInput().getSelQuestionareVersion().getResultHiddenFlag())) {
      colsToHide.add(CommonUIConstants.COLUMN_INDEX_11);
    }
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
    this.outlineNatFilter.questionOutlineSelectionListener(selection);
    if (this.editor.getActivePage() == 0) {
      setStatusBarMessage(this.groupByHeaderLayer, true);
    }
  }

  /**
   *
   */
  private void addFilterConfiguration() {
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, DisplayMode.NORMAL,
            GridRegion.FILTER_ROW);
      }
    });
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration());
  }

  /**
   * Get custom comparator configuration
   *
   * @param columnHeaderDataLayer
   * @return IConfiguration
   */
  private IConfiguration getCustomComparatorConfiguration() {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Register labels
        for (int i = 0; i < 12; i++) {
          QuestionDetailsPage.this.columnHeaderLabelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
        }

        // Register column attributes
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_QUES_NUMBER), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 0);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_QUES_NAME), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 1);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_QUES_HINT), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 2);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_QUES_MEASURABLE), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 3);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_SERIES), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 4);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_LINK), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 5);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_REMARK), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 6);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_OP), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 7);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_MEASURES), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 8);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_RESPONSIBLE), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 9);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_DATE), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 10);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getQuestionsComparator(SortColumns.SORT_RESULT), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 11);

      }
    };
  }


  /**
   *
   */
  private void addComboBoxConfiguration() {
    DataLayer bodyDataLayer = this.questionsFilterGridLayer.getBodyDataLayer();
    IRowDataProvider<Question> bodyDataProvider = (IRowDataProvider<Question>) bodyDataLayer.getDataProvider();
    QuestionaireLabelAccumulator columnLabelAccumulator =
        new QuestionaireLabelAccumulator(bodyDataLayer, bodyDataProvider, getEditorInput());
    bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);

    this.natTable.addConfiguration(new QuestionaireEditConfiguration());


    this.questionsFilterGridLayer.getBodyDataLayer().unregisterCommandHandler(UpdateDataCommand.class);
    this.questionsFilterGridLayer.getBodyDataLayer().registerCommandHandler(
        new QuestionnaireUpdateDataCommandHandler(this.questionsFilterGridLayer, this.qnaireDefBo));

  }

  /**
   * @param propertyToLabelMap
   * @param columnWidthMap
   */
  private void configureColumnsNATTable(final Map<Integer, Integer> columnWidthMap) {
    this.propertyToLabelMap.put(0, "No:");
    this.propertyToLabelMap.put(1, "Question");
    // ICDM-2188
    this.propertyToLabelMap.put(2,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_HINT));
    this.propertyToLabelMap.put(3,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    this.propertyToLabelMap.put(4,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    this.propertyToLabelMap.put(5, "Link");
    this.propertyToLabelMap.put(6,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    this.propertyToLabelMap.put(7,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_OPEN_POINTS));
    this.propertyToLabelMap.put(8,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_MEASURE));
    this.propertyToLabelMap.put(9,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_RESPONSIBLE));
    this.propertyToLabelMap.put(10,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_OPL_DATE));
    this.propertyToLabelMap.put(11,
        CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_RESULT));
    this.propertyToLabelMap.put(12, "Deleted");

    // The below map is used by NatTable to Map Columns with their respective widths
    columnWidthMap.put(0, COL_WIDTH_Q_NUM);
    columnWidthMap.put(1, COL_WIDTH_Q_NAME);
    columnWidthMap.put(2, COL_WIDTH_Q_HINT);
    columnWidthMap.put(3, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(4, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(5, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(6, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(7, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(8, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(9, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(10, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(11, COL_WIDTH_Q_CONFIG);
    columnWidthMap.put(12, COL_WIDTH_Q_CONFIG);

  }

  /**
   * Get param comparator
   *
   * @param sortColumns SortColumns
   * @return Comparator
   */
  public Comparator<Question> getQuestionsComparator(final SortColumns sortColumns) {

    return (param1, param2) -> QuestionDetailsPage.this.qnaireDefBo.compareTo(param1.getId(), param2.getId(),
        sortColumns);
  }

  /**
   *
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.tableSection);
    QuestionaireToolBarActionSet localToolBarActionSet =
        new QuestionaireToolBarActionSet(this, this.questionsFilterGridLayer);

    localToolBarActionSet.showAllHeadingsAction(this.toolBarManager, this.toolBarFilters);
    localToolBarActionSet.showAllQuestionsAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(new Separator());
    localToolBarActionSet.showAllDeletedAction(this.toolBarManager, this.toolBarFilters);
    localToolBarActionSet.showAllNotDeletedAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.update(true);
    // ICDM-2141
    addResetAllFiltersAction();
    this.tableSection.setTextClient(toolbar);

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
   * @return the ruleFilterGridLayer
   */
  @SuppressWarnings("rawtypes")
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.questionsFilterGridLayer;
  }


  /**
   * @return the selectedQues
   */
  public Question getSelectedQues() {
    return this.selectedQues;
  }


  /**
   * @return the selectionProvider
   */
  public RowSelectionProvider<Question> getSelectionProvider() {
    return this.selectionProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelction) {
    this.editor.updateStatusBar(outlineSelction, this.totTableRowCount,
        this.questionsFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }

  /**
   * ICDM-2141 Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.questionsFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput().getQuestionnaireEditorDataHandler();
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    reconstructNatTable();
  }

}
