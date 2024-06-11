/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByTreeFormat;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.AggregateConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.tree.SortableTreeComparator;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.RiskEvaluationPopupDialog;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalCellEditorConfig;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalCellStyleConfig;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalInputToColumnConverter;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalLabelAccumulator;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalToolBarActionSet;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalUpdateCmdHandler;
import com.bosch.caltool.apic.ui.table.filters.RiskEvalColumnFilterMatcher;
import com.bosch.caltool.apic.ui.table.filters.RiskEvalToolBarFilter;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping.SortColumn;
import com.bosch.caltool.icdm.client.bo.apic.PidcRiskResultHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.rm.ConsolidatedRisks;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel.RISK_LEVEL_CONFIG;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.ConsolidatedRiskCatClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmProjCharClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomColumnPropertyAccessor;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.caltool.nattable.configurations.FilterRowCustomConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;

import ca.odell.glazedlists.TreeList;

/**
 * The Class RiskMgmtNattableSection.
 *
 * @author gge6cob
 */
public class RiskEvalNatTableSection {

  /**
   *
   */
  private static final String OVERALL_STATUS_DESC =
      "Displays Risk Evaluation based on entries in the table. For each column in the table viewer, the highest risk determines the overall risk.";

  /** The Constant TYPE_FILTER_TEXT. */
  private static final String TYPE_FILTER_TEXT = "type filter text";

  /** The Constant STATIC_COL_INDEX. */
  private static final int STATIC_COL_INDEX = 0;

  /** The Constant PROJ_CHAR_COL_WIDTH. */
  private static final Integer PROJ_CHAR_COL_WIDTH = 225;

  /** The Constant RELEVANT_COL_WIDTH. */
  private static final Integer NORMAL_COL_WIDTH = 60;

  /** The Constant RB_SHARE_COL_WIDTH. */
  private static final Integer RELEVANT_COL_WIDTH = 30;

  /** constant for left mouse click. */
  protected static final int LEFT_MOUSE_CLICK = 1;

  /** Column RB Initial data. */
  public static final int RB_INIT_DATA_COLNUM = 5;

  /** The start of impact columns. */
  public static final int START_OF_IMPACT_COLUMNS = RB_INIT_DATA_COLNUM + 1;

  /** The Constant PROJ_CHAR_COLNUM. */
  private static final int PROJ_CHAR_COLNUM = 0;

  /** Column isRelevant - Yes. */
  public static final int RELEVANT_YES_COLNUM = 1;

  /** Column isRelevant - No. */
  public static final int RELEVANT_NO_COLNUM = 2;

  /** Column isRelevant - NA. */
  public static final int RELEVANT_NA_COLNUM = 3;

  /** Column RB Software Share. */
  public static final int RB_SW_SHARE_COLNUM = 4;

  /** Column RB Monetary Risk Pre-SOP. */
  public static final int RISK_PRE_SOP_COLNUM = 6;

  /** Column RB Monetary Risk Post-SOP. */
  public static final int RISK_POST_SOP_COLNUM = 7;

  /** Column RB Risk Law. */
  public static final int RISK_LAW_COLNUM = 8;

  /** Column RB Risk Reputation. */
  public static final int RISK_REPUTATION_COLNUM = 9;

  /** Column RB Risk Safety. */
  public static final int RISK_SAFETY_COLNUM = 10;

  /** The risk def page. */
  private final RiskEvaluationPage riskEvalPage;

  /** The section right. */
  private Section sectionRight;

  /** The form. */
  private Form form;

  /** The filter txt. */
  private Text filterTxt;

  /** The tot table row count. */
  private int totTableRowCount;

  /** The rm filter grid layer. */
  private CustomFilterGridLayer<PidcRMCharacterMapping> riskFilterGridLayer;

  /** The nat table. */
  private CustomNATTable natTable;

  /** The selection provider. */
  private RowSelectionProvider<PidcRMCharacterMapping> selectionProvider;

  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  /** The all column filter matcher. */
  private RiskEvalColumnFilterMatcher<PidcRMCharacterMapping> allColumnFilterMatcher;

  /** The sorted data set. */
  private Set<PidcRMCharacterMapping> sortedDataSet = new TreeSet<>();

  /** The result handler. */
  private final PidcRiskResultHandler resultHandler;


  /** The result parent map. */
  private Map<Long, PidcRMCharacterMapping> resultParentMap;

  /** comparator label. */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /** The pidc rm Webservice sclient. */
  PidcRmProjCharClient pidcRmWSclient;

  /** Section to display overall project risk evaluation */
  private Section sectionOverall;

  /** Form to display overall project risk evaluation */
  private Form overallForm;

  private final PidcVersion pidcVersion;

  private final Map<String, RmCategoryMeasures> catOverallMeasureMap = new HashMap<>();

  private final Map<String, RiskCategoryGroup> categoryGroupMap = new HashMap<>();

  final Map<String, Color> riskBackgroundMap = new HashMap<>();

  private final List<String> riskCatList = new ArrayList<>();

  private final CurrentUserBO currentUser = new CurrentUserBO();


  private boolean resetState;

  private RiskEvalCellStyleConfig riskEvalCellStyleConfig;

  /**
   * Instantiates a new risk mgmt nattable section.
   *
   * @param pidcVersion the pidc version
   * @param resultHandler the result handler
   * @param riskEvalPage RiskDefinitionPage
   */
  public RiskEvalNatTableSection(final PidcVersion pidcVersion, final PidcRiskResultHandler resultHandler,
      final RiskEvaluationPage riskEvalPage) {
    this.pidcVersion = pidcVersion;
    this.riskEvalPage = riskEvalPage;
    this.resultHandler = resultHandler;
    this.pidcRmWSclient = new PidcRmProjCharClient();
  }

  /**
   * This method initializes CompositeTwo.
   */
  public void createRightComposite() {
    final SashForm compositeTwo = new SashForm(this.riskEvalPage.getMainComposite(), SWT.VERTICAL);

    createRightSection(compositeTwo);
    createOverallSection(compositeTwo);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    compositeTwo.setLayout(gridLayout);
    compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());
    compositeTwo.setWeights(new int[] { 5, 2 });
    assignRiskBackground();
  }

  /**
   * create the right section.
   *
   * @param compositeTwo the composite two
   */
  private void createRightSection(final Composite compositeTwo) {
    this.sectionRight = this.riskEvalPage.getFormToolkit().createSection(compositeTwo,
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionRight.setText("Risk Evaluation");
    this.sectionRight.setLayout(new GridLayout());
    this.sectionRight.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionRight.getDescriptionControl().setEnabled(false);
    createRightForm();
    this.sectionRight.setClient(this.form);
  }

  /**
   * Creates the form.
   */
  private void createRightForm() {
    this.form = this.riskEvalPage.getFormToolkit().createForm(this.sectionRight);
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

    this.form.getBody().setLayout(new GridLayout());

    createFilterTxt();
    createTable();
  }

  /**
   * @param compositeOverall
   */
  private void createOverallSection(final Composite compositeOverall) {
    this.sectionOverall = this.riskEvalPage.getFormToolkit().createSection(compositeOverall,
        Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.CLIENT_INDENT);
    this.sectionOverall.setText("Overall Project Risk Evaluation");
    this.sectionOverall.setDescription(OVERALL_STATUS_DESC);
    GridLayout overallLayout = new GridLayout();
    this.sectionOverall.setLayout(overallLayout);
    this.sectionOverall.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.sectionOverall.getDescriptionControl().setEnabled(false);
    createOverallForm();
    this.sectionOverall.setClient(this.overallForm);
  }

  /**
   *
   */
  private void createOverallForm() {
    this.overallForm = this.riskEvalPage.getFormToolkit().createForm(this.sectionOverall);
    GridLayout overallLayout = new GridLayout();
    this.sectionOverall.setLayout(overallLayout);
    overallLayout.numColumns = 5;
    overallLayout.makeColumnsEqualWidth = true;
    this.overallForm.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.overallForm.getBody().setLayout(overallLayout);

    getRiskCategories();
    RiskMeasureGroupSection rmGrpcomponent =
        new RiskMeasureGroupSection(this.overallForm, this.catOverallMeasureMap, this);
    rmGrpcomponent.createRiskGroups(this.categoryGroupMap);
  }

  /**
   *
   */
  public void getRiskCategories() {
    this.riskCatList.clear();
    if ((null != this.resultHandler) &&
        (null != this.resultHandler.getImpactColumnHeaders(RiskEvalNatTableSection.START_OF_IMPACT_COLUMNS))) {
      this.riskCatList
          .addAll(this.resultHandler.getImpactColumnHeaders(RiskEvalNatTableSection.START_OF_IMPACT_COLUMNS).values());
    }
  }


  /**
   */
  public void updateOverallProjStatus() {

    getConsolidatedDataFromWs();
    updateOverAllSection();
  }

  /**
   *
   */
  private void updateOverAllSection() {
    for (Entry<String, RiskCategoryGroup> grp : this.categoryGroupMap.entrySet()) {
      grp.getValue().updateGroupData(getResultHandler().getAllRiskLevel(), getResultHandler().getAllRiskCodeMap(),
          grp.getKey(), this.catOverallMeasureMap);
    }
  }

  /**
   *
   */
  private void getConsolidatedDataFromWs() {
    this.catOverallMeasureMap.clear();
    ConsolidatedRiskCatClient consolRiskClient = new ConsolidatedRiskCatClient();
    try {
      ConsolidatedRisks consolPidcRisks = consolRiskClient.getConsolidatedRisks(this.currentRiskDefId);
      if (null != consolPidcRisks) {
        Map<Long, RmCategory> categoryMap = consolPidcRisks.getCategoryMap();
        if ((null != consolPidcRisks.getCategoryMeasureMap()) &&
            (null != consolPidcRisks.getCategoryMeasureMap().values()) &&
            !consolPidcRisks.getCategoryMeasureMap().isEmpty()) {
          for (RmCategoryMeasures rmCatMeas : consolPidcRisks.getCategoryMeasureMap().values()) {
            Long catId = rmCatMeas.getCategoryId();
            String categoryName = categoryMap.get(catId).getName();
            this.catOverallMeasureMap.put(categoryName, rmCatMeas);
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Creates the filter txt.
   */
  private void createFilterTxt() {

    this.filterTxt = TextUtil.getInstance().createFilterText(this.riskEvalPage.getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), TYPE_FILTER_TEXT);
    this.filterTxt.addModifyListener(event -> {
      String text = RiskEvalNatTableSection.this.filterTxt.getText().trim();
      RiskEvalNatTableSection.this.allColumnFilterMatcher.setFilterText(text, true);
      RiskEvalNatTableSection.this.riskFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);

      RiskEvalNatTableSection.this.riskFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(RiskEvalNatTableSection.this.riskFilterGridLayer.getSortableColumnHeaderLayer()));
      RiskEvalNatTableSection.this.riskEvalPage.setStatusBarMsg(false);
    });

  }

  /**
   * Creates the layer and table.
   *
   * @param columnWidthMap Map<Integer, Integer>
   * @return IConfigRegistry
   */
  private IConfigRegistry createLayerAndTable(final Map<Integer, Integer> columnWidthMap) {
    // instantiate the input to column converter
    RiskEvalInputToColumnConverter natInputToColumnConverter = new RiskEvalInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    // Group by model
    GroupByModel groupByModel = new GroupByModel();

    ProjectRMCharacterTreeModel treeFormat = new ProjectRMCharacterTreeModel(groupByModel,
        new CustomColumnPropertyAccessor<PidcRMCharacterMapping>(this.propertyToLabelMap.size()));
    this.totTableRowCount = this.sortedDataSet.size();
    this.riskFilterGridLayer = new CustomFilterGridLayer<>(configRegistry, this.sortedDataSet, this.propertyToLabelMap,
        columnWidthMap, getRiskComparator(PROJ_CHAR_COLNUM), natInputToColumnConverter, this.riskEvalPage, null,
        groupByModel, null, false, true, treeFormat, TreeList.NODES_START_EXPANDED, false);


    // all column filter matcher
    this.allColumnFilterMatcher = new RiskEvalColumnFilterMatcher<>();
    this.riskFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // creating the NAT table
    this.natTable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.riskFilterGridLayer, false, getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    // Header and row font style
    this.riskFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(this.columnHeaderStyleConfiguration));

    return configRegistry;
  }

  /** The column header style configuration. */
  CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // configure the painter
      configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.COLUMN_HEADER);
      configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.CORNER);

      // configure whether to render grid lines or not
      // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
      configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
          GridRegion.COLUMN_HEADER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
          GridRegion.CORNER);

      // configure the normal style
      Style cellStyle = new Style();
      cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.bacgrndColor);
      cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.foregrndColor);
      cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, this.gradintBgColor);
      cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, this.gradintFgColor);
      cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, this.horizontalAlign);
      cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, this.verticalAlign);
      cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.bordrStyle);
      cellStyle.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.NONE)));

      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL,
          GridRegion.COLUMN_HEADER);
      configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.CORNER);
    }
  };

  /** The tool bar manager. */
  private ToolBarManager toolBarManager;

  /** The tool bar action set. */
  private RiskEvalToolBarActionSet toolBarActionSet;

  /** The tool bar filters. */
  private RiskEvalToolBarFilter toolBarFilters;

  /** The current risk def id. */
  private Long currentRiskDefId = 0L;

  /**
   * Creates the table viewer.
   */
  private void createTable() {

    Map<Integer, Integer> columnWidthMap = createModelHeaderForTable();
    IConfigRegistry configRegistry = createLayerAndTable(columnWidthMap);

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Add Configurations
    CustomNatTableStyleConfiguration natTabConfig = new CustomNatTableStyleConfiguration();
    natTabConfig.setEnableAutoResize(true);
    this.natTable.addConfiguration(natTabConfig);
    this.natTable.addConfiguration(new FilterRowCustomConfiguration(this.totTableRowCount));

    // creating header menu configuration
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(RiskEvalNatTableSection.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((final NatTable table, final Menu popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      RiskEvalNatTableSection.this.resetState = true;
                      RiskEvalNatTableSection.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // Add Column Comparator
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.riskFilterGridLayer.getColumnHeaderDataLayer()));

    // Shade the row to be slightly darker than the blue background.
    final Style rowStyle = new Style();
    rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);

    // Group columns
    groupColumns();

    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.riskFilterGridLayer.getBodyLayer().getSelectionLayer();
    selectionLayer.setSelectedCell(STATIC_COL_INDEX, 0);

    // freeze the first two columns
    this.natTable.doCommand(new FreezeSelectionCommand());
    this.riskFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));
    this.selectionProvider = new RowSelectionProvider<>(this.riskFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.riskFilterGridLayer.getBodyDataProvider(), false);

    // Enable tootltip - only for cells which contain not fully visible content
    attachToolTip(this.natTable);

    // Enable Mouse listener
    addMouseListener();

    // Enable Tool bar filters
    this.toolBarFilters = new RiskEvalToolBarFilter(this);
    this.riskFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());
    createToolbarAction();

    this.riskEvalPage.getSite().setSelectionProvider(this.selectionProvider);

    this.riskEvalCellStyleConfig = new RiskEvalCellStyleConfig();
    // Checkbox and ComboBox - style & editor configuration
    this.natTable.addConfiguration(this.riskEvalCellStyleConfig);
    this.natTable.addConfiguration(new RiskEvalCellEditorConfig(this));

    // Enable coloring for params using label accumalator
    DataLayer bodyDataLayer = this.riskFilterGridLayer.getBodyDataLayer();
    bodyDataLayer.setConfigLabelAccumulator(new RiskEvalLabelAccumulator(bodyDataLayer, this, this.resultParentMap));

    // Merger cell painter and check box painter to same accumulator
    AggregateConfigLabelAccumulator aggregrateConfigLabelAccumulator = new AggregateConfigLabelAccumulator();
    aggregrateConfigLabelAccumulator.add(this.riskFilterGridLayer.getBodyLabelAccumulator());
    aggregrateConfigLabelAccumulator.add(bodyDataLayer.getConfigLabelAccumulator());
    bodyDataLayer.setConfigLabelAccumulator(aggregrateConfigLabelAccumulator);

    // Update command Handler - invokes Webservice
    bodyDataLayer.registerCommandHandler(new RiskEvalUpdateCmdHandler(this));

    this.natTable.configure();

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
    this.riskEvalPage.setStatusBarMsg(false);
  }

  /**
   * Creates the toolbar action.
   */
  private void createToolbarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);

    // NAT table action set
    this.toolBarActionSet = new RiskEvalToolBarActionSet(this.riskFilterGridLayer, this.riskEvalPage);

    ToolBarManager toolBarformManager = (ToolBarManager) this.form.getToolBarManager();
    this.toolBarActionSet.showAllChildren(toolBarformManager, this.toolBarFilters);
    toolBarformManager.add(new Separator());
    this.toolBarActionSet.isRelevant(toolBarformManager, this.toolBarFilters);
    this.toolBarActionSet.isNotRelevant(toolBarformManager, this.toolBarFilters);
    this.toolBarActionSet.isRelevantNotDefined(toolBarformManager, this.toolBarFilters);

    /*
     * By default - show All Entries is not checked
     */
    this.toolBarActionSet.getShowAllEntriesAction().setChecked(false);

    this.form.getToolBarManager().update(true);
    this.riskEvalPage.updateStatusBar(true);
  }

  /**
   * Reconstruct nat table.
   */
  public void reconstructNatTable() {
    this.natTable.dispose();
    this.riskFilterGridLayer = null;
    this.propertyToLabelMap.clear();
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
    if (this.form.getToolBarManager() != null) {
      this.form.getToolBarManager().removeAll();
    }

    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.form.getBody().pack();
    this.sectionRight.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }
    if (this.natTable != null) {
      this.natTable.refresh();
    }
  }

  /**
   * handleGroupColumnsCommand method in ColumnGroupsCommandHandler class.
   */
  private void groupColumns() {
    List<Integer> selectedPositions = new ArrayList<>();
    selectedPositions.add(RELEVANT_YES_COLNUM);
    selectedPositions.add(RELEVANT_NO_COLNUM);
    selectedPositions.add(RELEVANT_NA_COLNUM);
    int[] fullySelectedColumns = new int[] { RELEVANT_YES_COLNUM, RELEVANT_NO_COLNUM, RELEVANT_NA_COLNUM };
    ColumnGroupModel columnGroupModel = this.riskFilterGridLayer.getColumnGroupModel();
    String grpName;
    try {
      grpName = new CommonDataBO().getMessage(ApicConstants.RISK_EVALUATION, ApicConstants.COLUMN_IS_RELEVANT);
      columnGroupModel.addColumnsIndexesToGroup(grpName, fullySelectedColumns);
      columnGroupModel.setColumnGroupCollapseable(RELEVANT_NA_COLNUM, false);
      SelectionLayer selectionLayer = this.riskFilterGridLayer.getBodyLayer().getSelectionLayer();
      selectionLayer.doCommand(
          new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).intValue()));
      selectionLayer.clear();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
  }

  /**
   * Adds the mouse listener.
   */
  private void addMouseListener() {

    this.natTable.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // No action
      }

      @Override
      public void mouseDown(final MouseEvent mouseEvent) {
        // No action
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseEvent) {
        if (mouseEvent.button == LEFT_MOUSE_CLICK) {
          leftClickAction(mouseEvent);
        }
      }

      /**
       * @param mouseEvent
       */
      private void leftClickAction(final MouseEvent mouseEvent) {
        int rowPosition = RiskEvalNatTableSection.this.natTable.getRowPositionByY(mouseEvent.y);
        int colPosition = RiskEvalNatTableSection.this.natTable.getColumnPositionByX(mouseEvent.x);
        int rowIndex = RiskEvalNatTableSection.this.natTable.getRowIndexByPosition(rowPosition);
        int colIndex = RiskEvalNatTableSection.this.natTable.getColumnIndexByPosition(colPosition);

        if ((colIndex > RB_INIT_DATA_COLNUM) && (rowIndex >= 0)) {
          riskEvaluationPopupAction(rowIndex);
        }
      }

      /**
       * @param rowIndex
       */
      private void riskEvaluationPopupAction(final int rowIndex) {
        Object rowObject =
            RiskEvalNatTableSection.this.riskFilterGridLayer.getBodyDataProvider().getRowObject(rowIndex);
        if (rowObject instanceof PidcRMCharacterMapping) {
          PidcRMCharacterMapping selPidcChar = (PidcRMCharacterMapping) rowObject;

          // Map to hold the col Index and risk measure
          Map<String, RmCategoryMeasures> colIndexMeasureMap = new ConcurrentHashMap<>();

          // Impact columns
          Map<Integer, String> colIndexNameMap = RiskEvalNatTableSection.this.resultHandler
              .getImpactColumnHeaders(RiskEvalNatTableSection.START_OF_IMPACT_COLUMNS);

          Map<Integer, Long> colIndexCatIdMap = RiskEvalNatTableSection.this.resultHandler.getColIndexCatIdMap();

          // Get risk measures from metadata for the impact columns
          fillColIndexMeasureMap(selPidcChar, colIndexMeasureMap, colIndexNameMap, colIndexCatIdMap);

          openPopupDialog(selPidcChar, colIndexMeasureMap);
        }
      }

      /**
       * @param selPidcChar
       * @param colIndexMeasureMap
       * @param colIndexNameMap
       * @param colIndexCatIdMap
       */
      private void fillColIndexMeasureMap(final PidcRMCharacterMapping selPidcChar,
          final Map<String, RmCategoryMeasures> colIndexMeasureMap, final Map<Integer, String> colIndexNameMap,
          final Map<Integer, Long> colIndexCatIdMap) {
        for (Entry<Integer, String> entry : colIndexNameMap.entrySet()) {
          Collection<RmCategoryMeasures> rmMeasuresList =
              RiskEvalNatTableSection.this.resultHandler.getMetaData().getRmMeasuresMap().values();

          setColIndexMeasureMapFromList(selPidcChar, colIndexMeasureMap, colIndexNameMap, colIndexCatIdMap, entry,
              rmMeasuresList);
        }
      }

      /**
       * @param selPidcChar
       * @param colIndexMeasureMap
       * @param colIndexNameMap
       * @param colIndexCatIdMap
       * @param entry
       * @param rmMeasuresList
       */
      private void setColIndexMeasureMapFromList(final PidcRMCharacterMapping selPidcChar,
          final Map<String, RmCategoryMeasures> colIndexMeasureMap, final Map<Integer, String> colIndexNameMap,
          final Map<Integer, Long> colIndexCatIdMap, final Entry<Integer, String> entry,
          final Collection<RmCategoryMeasures> rmMeasuresList) {
        // Using lambda expression for iteration
        rmMeasuresList.forEach(rmMeasure -> {
          if (rmMeasure.getCategoryId().equals(colIndexCatIdMap.get(entry.getKey())) &&
              rmMeasure.getRiskLevel().equals(selPidcChar.getColIndexRiskIdMap().get(entry.getKey()))) {
            colIndexMeasureMap.put(colIndexNameMap.get(entry.getKey()), rmMeasure);
          }
        });
      }

      /**
       * @param selPidcChar
       * @param colIndexMeasureMap
       */
      private void openPopupDialog(final PidcRMCharacterMapping selPidcChar,
          final Map<String, RmCategoryMeasures> colIndexMeasureMap) {
        if (null != selPidcChar.getRiskImpactMap()) {
          RiskEvaluationPopupDialog popup =
              new RiskEvaluationPopupDialog(Display.getCurrent().getActiveShell(), RiskEvalNatTableSection.this,
                  selPidcChar, colIndexMeasureMap, RiskEvalNatTableSection.this.riskEvalPage.getSelPidcRmDef());
          popup.open();
        }
      }
    });
  }


  /**
   * get the comparator for the table.
   *
   * @param columnNum the column num
   * @return Comparator<FocusMatrixAttribute>
   */
  public Comparator<Object> getRiskComparator(final int columnNum) {
    return (input1, input2) -> {
      if (input1 instanceof PidcRMCharacterMapping) {
        PidcRMCharacterMapping data1 = (PidcRMCharacterMapping) input1;
        PidcRMCharacterMapping data2 = (PidcRMCharacterMapping) input2;
        SortColumn sortCol = PidcRMCharacterMapping.SortColumn.getType(columnNum);
        return PidcRMCharacterMapping.compareTo(data1, data2, sortCol);
      }
      return 0;

    };
  }

  /**
   * @param columnHeaderDataLayer AbstractLayer
   * @return IConfiguration
   */
  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        for (int col_index = 0; col_index < RiskEvalNatTableSection.this.propertyToLabelMap.size(); col_index++) {
          labelAccumulator.registerColumnOverrides(col_index, CUSTOM_COMPARATOR_LABEL + col_index);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getRiskComparator(col_index),
              NORMAL, CUSTOM_COMPARATOR_LABEL + col_index);
        }
      }
    };
  }

  /**
   * Creates the model header for table.
   *
   * @return the map
   */
  private Map<Integer, Integer> createModelHeaderForTable() {
    this.propertyToLabelMap = new HashMap<>();
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    // Get risk impact columns - dynamic
    if ((this.resultHandler != null) && (this.resultHandler.getAllProjectCharacter() != null)) {
      Map<Integer, String> allRiskImpactColumns =
          this.resultHandler.getImpactColumnHeaders(RiskEvalNatTableSection.START_OF_IMPACT_COLUMNS);
      for (Entry<Integer, String> entry : allRiskImpactColumns.entrySet()) {
        Integer columnRank = entry.getKey();
        String input = entry.getValue();
        input = input.replaceAll("(.*)\\s(.*)", "$1\n$2");
        this.propertyToLabelMap.put(columnRank, input);
        columnWidthMap.put(columnRank, NORMAL_COL_WIDTH + input.length());
      }
    }


    try {
      this.propertyToLabelMap.put(RB_SW_SHARE_COLNUM,
          new CommonDataBO().getMessage(ApicConstants.RISK_EVALUATION, ApicConstants.COLUMN_RB_SW_SHARE));
      this.propertyToLabelMap.put(PROJ_CHAR_COLNUM,
          new CommonDataBO().getMessage(ApicConstants.RISK_EVALUATION, ApicConstants.COLUMN_PROJECT_CHAR));
      this.propertyToLabelMap.put(RELEVANT_YES_COLNUM, "Yes");
      this.propertyToLabelMap.put(RELEVANT_NO_COLNUM, "No");
      this.propertyToLabelMap.put(RELEVANT_NA_COLNUM, ApicConstants.USED_NOTDEF_DISPLAY);
      this.propertyToLabelMap.put(RB_INIT_DATA_COLNUM,
          new CommonDataBO().getMessage(ApicConstants.RISK_EVALUATION, ApicConstants.COLUMN_RB_INPUT_DATA));

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    columnWidthMap.put(0, PROJ_CHAR_COL_WIDTH);
    columnWidthMap.put(1, RELEVANT_COL_WIDTH);
    columnWidthMap.put(2, RELEVANT_COL_WIDTH);
    columnWidthMap.put(3, RELEVANT_COL_WIDTH);
    columnWidthMap.put(4, NORMAL_COL_WIDTH - 10);
    columnWidthMap.put(5, NORMAL_COL_WIDTH - 10);
    return columnWidthMap;
  }

  /**
   * Load proj char mapping WS.
   *
   * @param selectedRiskDefId the selected risk def id
   * @param forceRefresh force refresh
   */
  public void getProjCharMappingWS(final Long selectedRiskDefId, final boolean forceRefresh) {

    // Refresh - only in case of new selection
    if ((this.currentRiskDefId.longValue() == selectedRiskDefId.longValue()) && !forceRefresh) {
      return;
    }
    this.currentRiskDefId = selectedRiskDefId;
    Set<PidcRmProjCharacterExt> webOutputSet = null;
    try {
      webOutputSet = this.pidcRmWSclient.getPidcRmProjcharExt(selectedRiskDefId);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    catch (Exception e) {
      CDMLogger.getInstance().error("Error occurred in loading : Risk MetaData" + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
    // Populate row objects & parent map
    this.resultParentMap = new HashMap<>();
    this.sortedDataSet = this.resultHandler.getPidcProjCharSet(selectedRiskDefId, webOutputSet, this.resultParentMap);

    // Refresh Nattable
    reconstructNatTable();
  }

  /**
   * Attach tool tip.
   *
   * @param natTableObj the nat table obj
   */
  private void attachToolTip(final CustomNATTable natTableObj) {
    DefaultToolTip toolTip = new SimpleNatTableToolTip(natTableObj);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }


  /**
   * The Class SimpleNatTableToolTip.
   */
  private class SimpleNatTableToolTip extends DefaultToolTip {

    /** The nat table obj. */
    private final NatTable natTableObj;

    /**
     * Instantiates a new simple nat table tool tip.
     *
     * @param natTable the nat table
     */
    public SimpleNatTableToolTip(final NatTable natTable) {
      super(natTable, ToolTip.NO_RECREATE, false);
      this.natTableObj = natTable;
    }

    /**
     * {@inheritDoc}
     */
    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets.Event) Implementation here means the
     * tooltip is not redrawn unless mouse hover moves outside of the current cell (the combination of
     * ToolTip.NO_RECREATE style and override of this method).
     */
    @Override
    protected Object getToolTipArea(final Event event) {
      int col = this.natTableObj.getColumnPositionByX(event.x);
      int row = this.natTableObj.getRowPositionByY(event.y);

      return new Point(col, row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getText(final Event event) {
      int col = this.natTableObj.getColumnPositionByX(event.x);
      int row = this.natTableObj.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
      return (String) cellByPosition.getDataValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldCreateToolTip(final Event event) {
      int col = this.natTableObj.getColumnPositionByX(event.x);
      int row = this.natTableObj.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
      if ((cellByPosition == null) || (cellByPosition.getDataValue() == null) ||
          !(cellByPosition.getDataValue() instanceof String)) {
        return false;
      }
      String cellValue = (String) cellByPosition.getDataValue();
      if ((cellValue == null) || cellValue.isEmpty()) {
        return false;
      }
      Rectangle currentBounds = cellByPosition.getBounds();
      cellByPosition.getLayer().getPreferredWidth();

      GC gcObj = new GC(this.natTableObj);
      Point size = gcObj.stringExtent(cellValue);

      return currentBounds.width < size.x;
    }
  }

  /**
   * Gets the selection provider.
   *
   * @return the selection provider
   */
  public RowSelectionProvider<PidcRMCharacterMapping> getSelectionProvider() {
    return this.selectionProvider;
  }

  /**
   * Gets the custom filter grid layer.
   *
   * @return riskFilterGridLayer
   */
  public CustomFilterGridLayer<PidcRMCharacterMapping> getCustomFilterGridLayer() {
    return this.riskFilterGridLayer;
  }

  /**
   * The Class ProjectRMCharacterTreeModel.
   */
  class ProjectRMCharacterTreeModel extends GroupByTreeFormat<PidcRMCharacterMapping> {


    /**
     * Instantiates a new project RM character tree model.
     *
     * @param model the model
     * @param columnAccessor the column accessor
     */
    public ProjectRMCharacterTreeModel(final GroupByModel model,
        final IColumnAccessor<PidcRMCharacterMapping> columnAccessor) {
      super(model, columnAccessor);
    }


    /** The sort model. */
    private ISortModel sortModel;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowsChildren(final Object arg0) {
      return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSortModel(final ISortModel model) {
      this.sortModel = model;
    }


    @Override
    public Comparator<Object> getComparator(final int depth) {
      return new SortableTreeComparator<>(getRiskComparator(PROJ_CHAR_COLNUM), this.sortModel);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void getPath(final List<Object> path, final Object current) {
      if (current instanceof PidcRMCharacterMapping) {
        PidcRMCharacterMapping mappingObj = (PidcRMCharacterMapping) current;
        // If Child -> Add the parent node first followed by current node
        Long parentId = mappingObj.getParentProjCharId();
        ArrayList<PidcRMCharacterMapping> parentList = new ArrayList<>();
        while (parentId.longValue() != 0l) {
          if (RiskEvalNatTableSection.this.resultParentMap.containsKey(parentId)) {
            PidcRMCharacterMapping parent = RiskEvalNatTableSection.this.resultParentMap.get(parentId);
            parentList.add(RiskEvalNatTableSection.this.resultParentMap.get(parentId));
            parentId = parent.getParentProjCharId();
          }
        }
        // Add parent in hierarchial order
        if (CommonUtils.isNotEmpty(parentList)) {
          for (int i = parentList.size() - 1; i >= 0; i--) {
            path.add(parentList.get(i));
          }
        }
        path.add(mappingObj);
      }
    }
  }

  /**
   * Gets the total row count.
   *
   * @return the total row count
   */
  public int getTotalRowCount() {
    return this.totTableRowCount;
  }

  /**
   * Gets the parent data map.
   *
   * @return the parent data map
   */
  public Map<Long, PidcRMCharacterMapping> getParentDataMap() {
    return this.resultParentMap;
  }

  /**
   * Gets the tool bar action.
   *
   * @return the tool bar action
   */
  public RiskEvalToolBarActionSet getToolBarAction() {
    return this.toolBarActionSet;
  }

  /**
   * Gets the result handler.
   *
   * @return the result handler
   */
  public PidcRiskResultHandler getResultHandler() {
    return this.resultHandler;
  }

  /**
   * @return access boolean
   */
  public boolean isModifiable() {

    NodeAccess nodeAccess;
    try {
      nodeAccess = this.currentUser.getNodeAccessRight(this.pidcVersion.getPidcId());
      if ((nodeAccess != null) && nodeAccess.isOwner()) {
        return !this.pidcVersion.isDeleted();
      }
      return false;
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }


  }

  /**
   * @return the riskBackgroundMap
   */
  public Map<String, Color> getRiskBackgroundMap() {
    return this.riskBackgroundMap;
  }

  /**
  *
  */
  private void assignRiskBackground() {
    this.riskBackgroundMap.put(RISK_LEVEL_CONFIG.RISK_LVL_HIGH.getCode(), this.riskEvalCellStyleConfig.getColorHigh());
    this.riskBackgroundMap.put(RISK_LEVEL_CONFIG.RISK_LVL_MEDIUM.getCode(),
        this.riskEvalCellStyleConfig.getColorMedium());
    this.riskBackgroundMap.put(RISK_LEVEL_CONFIG.RISK_LVL_LOW.getCode(), this.riskEvalCellStyleConfig.getColorLow());
  }

  /**
   * @return the riskCatList
   */
  public List<String> getRiskCatList() {
    return this.riskCatList;
  }

  /**
   * @return the catOverallMeasureMap
   */
  public Map<String, RmCategoryMeasures> getCatOverallMeasureMap() {
    return this.catOverallMeasureMap;
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
      CDMLogger.getInstance().warn("Failed to load Risk Evaluation nat table state", ioe, Activator.PLUGIN_ID);
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
      CDMLogger.getInstance().warn("Failed to save Risk Evaluation nat table state", ioe, Activator.PLUGIN_ID);
    }
  }


  /**
   * @return the riskEvalPage
   */
  public RiskEvaluationPage getRiskEvalPage() {
    return this.riskEvalPage;
  }
}