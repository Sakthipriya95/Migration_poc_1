/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByTreeFormat;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tree.SortableTreeComparator;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleToolBarActionSet;
import com.bosch.caltool.icdm.ruleseditor.listeners.TableDoubleClickListeners;
import com.bosch.caltool.icdm.ruleseditor.sorters.ParameterSorter;
import com.bosch.caltool.icdm.ruleseditor.sorters.ParameterSorter.SortColumns;
import com.bosch.caltool.icdm.ruleseditor.table.filters.ReviewParamToolBarFilters;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomColumnPropertyAccessor;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGroupByDataLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;

import ca.odell.glazedlists.TreeList;

/**
 * @author bru2cob
 */
public class ParamNatTable<D extends IParameterAttribute, P extends IParameter> extends AbstractGroupByNatFormPage
    implements ISelectionListener {

  /**
   * Param table section instance
   */
  private final ParamTableSection paramTableSection;
  /**
   * Nat table instance
   */
  private CustomNATTable natTable;
  /**
   * NAT input converter
   */
  private AbstractNatInputToColumnConverter natInputToColumnConverter;
  private Map<Integer, String> propertyToLabelMap;
  /**
   * NAT table grid layer
   */
  private CustomFilterGridLayer paramFilterGridLayer;

  /**
   * Column header configuration
   */
  private ParamNatColHeaderLabelAccumulator columnLabelAccumulator;
  /**
   * Row selection provide
   */
  private RowSelectionProvider<Object> selectionProvider;
  private GroupByHeaderLayer groupByHeaderLayer;
  private int totalRowCount;
  private ToolBarManager toolBarManager;

  private ParamRulesColumnFilterMatcher<IParameter> allColumnFilterMatcher;
  /**
   * CUSTOM_COMPARATOR Label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  private boolean resetState;
  private ParameterDataProvider<D, P> parameterDataProvider;

  private boolean isRefreshNeeded;
// boolean to check function Or ruleset
  private boolean isFunction;

  private List<Object> inputList;

  /**
   * @param paramTableSection
   * @param parameterDataProvider
   */
  public ParamNatTable(final ParamTableSection paramTableSection) {
    super(paramTableSection.getListPage().getEditor(), "Review rules", "Review rules");
    this.paramTableSection = paramTableSection;

  }

  /**
   * Creates the summary tree tableviewer
   */
  public void createTableViewer() {
    this.propertyToLabelMap = new HashMap<>();
    Map<Integer, Integer> columnWidthMap = new HashMap<>();
    this.isFunction = this.paramTableSection.getListPage().getEditor().getEditorInput().getDataProvider()
        .hasVersions(this.paramTableSection.getListPage().getCdrFunction());
    // Configure columns
    configureColumnsNATTable(this.propertyToLabelMap, columnWidthMap);
    this.inputList = new ArrayList<>();
    this.inputList = this.paramTableSection.getListPage().setTabViewerInput();
    this.totalRowCount = this.inputList.size();
    this.parameterDataProvider =
        this.paramTableSection.getListPage().getEditor().getEditorInput().getParamDataProvider();
    this.natInputToColumnConverter = new ParamNatInputToColConverter(this.parameterDataProvider);
    IConfigRegistry configRegistry = new ConfigRegistry();
    // Group by model
    GroupByModel groupByModel = new GroupByModel();
    List<Integer> colsToHide = new ArrayList<>();
    if (!this.isFunction) {
      colsToHide.add(15);// Param Type
      colsToHide.add(16);// Param Responsibility
      colsToHide.add(17);// System Element
      colsToHide.add(18);// HW Component
    }
    // A Custom Filter Grid Layer is constructe
    Collections.sort(this.inputList, getParamComparator(SortColumns.SORT_PARAM_NAME));

    ParamTreeFormat treeFormat =
        new ParamTreeFormat(groupByModel, new CustomColumnPropertyAccessor<>(this.propertyToLabelMap.size()));
    this.paramFilterGridLayer = new CustomFilterGridLayer(configRegistry, this.inputList, this.propertyToLabelMap,
        columnWidthMap, null, this.natInputToColumnConverter, this,
        new TableDoubleClickListeners(this.paramTableSection.getListPage(), this.parameterDataProvider), groupByModel,
        colsToHide, false, true, treeFormat, null, true);
    DataLayer bodyDataLayer = this.paramFilterGridLayer.getBodyDataLayer();
    bodyDataLayer.setConfigLabelAccumulator(new ParamRulesLabelAccumulator(bodyDataLayer, this.parameterDataProvider));

    // Col header configuration
    this.columnLabelAccumulator =
        new ParamNatColHeaderLabelAccumulator(this.paramFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.paramFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(this.columnLabelAccumulator);
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.paramFilterGridLayer,
        this.paramFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.paramFilterGridLayer, 0, 1);

    // Enable column filters
    this.allColumnFilterMatcher = new ParamRulesColumnFilterMatcher<>(this.parameterDataProvider);
    this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
        .setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // Create NAT table
    this.natTable = new CustomNATTable(this.paramTableSection.getFormTwo().getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.paramFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    addNattableConfiguration(configRegistry);
// To maintain column persistant
    saveState();
    // Save and Load the state of NAT table
    loadState();


    // Column chooser configuration
    CustomDefaultBodyLayerStack bodyLayer = this.paramFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.paramFilterGridLayer.getColumnHeaderLayer(), this.paramFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);

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

    this.natTable.addDisposeListener(event ->
    // Stop
    saveState());

    addListeners();
    this.paramFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));
    // add filters
    this.paramTableSection.getListPage().addFilters();
    this.paramTableSection.getListPage().setRowSelection();
    ParamTabContextMenu contextMenu = new ParamTabContextMenu(this.paramTableSection, this.parameterDataProvider,
        this.paramTableSection.getListPage().getEditor().getEditorInput().getDataProvider());
    contextMenu.addRightClickMenu();

    addKeyListenerOnTableViewer();
    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
    }
    RuleNatToolTip toolTip = new RuleNatToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   *
   */
  private void addListeners() {
    // Added a workaround to maintain selection in nattable
    // Reason :After setting row selection in nattable via selection listenr
    // due to some reason nattable automatically triggred a event
    // which removes the selection from nattable
    this.natTable.addPaintListener(paintevent -> {
      IParameter selection = null;
      Object reviewRuleObject = null;
      if ((ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().getCdrFuncParam() != null) &&
          (this.paramTableSection.getListPage().getEditor().getEditorInput().getRuleId() == null)) {
        selection = ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().getCdrFuncParam();
      }
      else if (this.paramTableSection.getListPage().getEditor().getEditorInput().getRuleId() != null) {
        Long ruleId = this.paramTableSection.getListPage().getEditor().getEditorInput().getRuleId();
        ReviewRule rvwRule = new ReviewRule();
        rvwRule.setRuleId(new BigDecimal(ruleId));
        reviewRuleObject = this.paramTableSection.getListPage().getReviewRuleObject(this, rvwRule);
        // added logic for spacial case where a parameter contains a simple rule
        if (reviewRuleObject == null) {
          selection = ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().getCdrFuncParam();
          ParamNatTable.this.isRefreshNeeded = true;
        }
      }
      if (ParamNatTable.this.isRefreshNeeded && (selection != null)) {
        ParamNatTable.this.selectionProvider.setSelection(new StructuredSelection(selection));
        this.paramTableSection.getNatTable().setFocus();
        ParamNatTable.this.isRefreshNeeded = false;
      }
      if (ParamNatTable.this.isRefreshNeeded && (reviewRuleObject != null)) {
        this.getSelectionProvider().setSelection(new StructuredSelection(reviewRuleObject));
        this.paramTableSection.getNatTable().setFocus();
        // do we need to added refresh needed false
      }
    });
    this.selectionProvider = new RowSelectionProvider<>(this.paramFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.paramFilterGridLayer.getBodyDataProvider(), false);


    this.selectionProvider.addSelectionChangedListener(event -> {

      ParamNatTable.this.paramTableSection.getListPage().setSelectedParameter();
      ParamNatTable.this.paramTableSection.getListPage().setSelectedRule();
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      setSelectedObjectToPropertiesView(selection);

      selectionListenerForRuleSetNattable(selection);

    });
  }

  /**
   * @param selection
   */
  private void selectionListenerForRuleSetNattable(final IStructuredSelection selection) {
    // maintain selection while traversing between pages
    if (CommonUtils.isNotNull(selection) && selection.isEmpty()) {
      if ((ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().getCdrFuncParam() != null) &&
          (this.paramTableSection.getListPage().getEditor().getEditorInput().getRuleId() == null)) {
        ParamNatTable.this.selectionProvider.setSelection(new StructuredSelection(
            ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().getCdrFuncParam()));
        this.paramTableSection.getNatTable().setFocus();
        this.paramTableSection.getListPage().getEditor().getEditorInput().setRuleId(null);
      }
      if (ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().getRuleId() != null) {
        Long ruleId = this.paramTableSection.getListPage().getEditor().getEditorInput().getRuleId();
        ReviewRule rvwRule = new ReviewRule();
        rvwRule.setRuleId(new BigDecimal(ruleId));
        Object reviewRuleObject = this.paramTableSection.getListPage().getReviewRuleObject(this, rvwRule);
        setRuleSetParamAndRvwRuleSelection(reviewRuleObject);
        ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().setCdrFuncParam(null);
      }
      ParamNatTable.this.isRefreshNeeded = true;
    }
    if (CommonUtils.isNotNull(selection)) {
      Object firstElement = selection.getFirstElement();
      if (firstElement instanceof ReviewRule) {
        ReviewRule reviewRule = (ReviewRule) firstElement;
        this.paramTableSection.getListPage().getEditor().getEditorInput().setRuleId(reviewRule.getRuleId().longValue());
        ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().setCdrFuncParam(null);
      }
      else if (firstElement instanceof DefaultRuleDefinition) {
        DefaultRuleDefinition defaultRuleDefinition = (DefaultRuleDefinition) firstElement;
        this.paramTableSection.getListPage().getEditor().getEditorInput()
            .setRuleId(defaultRuleDefinition.getReviewRule().getRuleId().longValue());
        ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput().setCdrFuncParam(null);
      }
      else if (firstElement instanceof RuleSetParameter) {
        RuleSetParameter ruleSetParameter = (RuleSetParameter) firstElement;
        ParamNatTable.this.paramTableSection.getListPage().getEditor().getEditorInput()
            .setCdrFuncParam(ruleSetParameter);
        this.paramTableSection.getListPage().getEditor().getEditorInput().setRuleId(null);
      }
    }
  }

  /**
   * @param reviewRuleObject
   */
  private void setRuleSetParamAndRvwRuleSelection(Object reviewRuleObject) {
    if (CommonUtils.isNotNull(reviewRuleObject)) {
      this.getSelectionProvider().setSelection(new StructuredSelection(reviewRuleObject));
      this.paramTableSection.getNatTable().setFocus();
    }
    else {
      // added logic for spacial case where a parameter contains a simple rule
      IParameter cdrFuncParam = this.paramTableSection.getListPage().getEditor().getEditorInput().getCdrFuncParam();
      this.getSelectionProvider().setSelection(new StructuredSelection(cdrFuncParam));
      this.paramTableSection.getNatTable().setFocus();
    }
  }


  /**
   * @param configRegistry
   */
  private void addNattableConfiguration(final IConfigRegistry configRegistry) {
    // Add configurations
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(getGridData());

    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable.addConfiguration(new ParamRulesEditConfiguration());
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(new ParamClassConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration());
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(ParamNatTable.this.natTable).withColumnChooserMenuItem()
                .withMenuItemProvider((final NatTable natTableLocal, final Menu popupMenu) -> {

                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      ParamNatTable.this.resetState = true;
                      ParamNatTable.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });


    // Configure NAT table
    this.natTable.configure();
  }


  /**
   * Method to set selection to properties view
   *
   * @param selection from nattable
   */
  public void setSelectedObjectToPropertiesView(final IStructuredSelection selection) {
    IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
    if (viewPart != null) {
      PropertySheet propertySheet = (PropertySheet) viewPart;
      IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
      if (page != null) {
        page.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(),
            selection);
      }
    }
  }

  public void addKeyListenerOnTableViewer() {
    this.natTable.addKeyListener(new KeyListener() {

      @Override
      public void keyReleased(final KeyEvent keyevent) {
        // Not applicable
      }

      @Override
      public void keyPressed(final KeyEvent keyevent) {
        final IStructuredSelection selection =
            (IStructuredSelection) ParamNatTable.this.selectionProvider.getSelection();
        if (selection.getFirstElement() instanceof IParameter) {
          IParameter param = (IParameter) selection.getFirstElement();
          if (CommonUtils.isNotNull(param) && (keyevent.stateMask == CommonUIConstants.KEY_CTRL) &&
              (keyevent.keyCode == CommonUIConstants.KEY_COPY)) {
            CommonUiUtils.setTextContentsToClipboard(param.getName());
          }
        }
      }
    });
  }

  public Comparator<Object> getParamComparator(final SortColumns sortColumns) {
    return (final Object o1, final Object o2) -> {
      final IParameter param1 = getParameter(o1);
      final IParameter param2 = getParameter(o2);
      ParameterSorter sorter = new ParameterSorter(ParamNatTable.this.parameterDataProvider);
      return sorter.compare(param1, param2, sortColumns);
    };
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
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }

  /**
   * @param propertyToLabelMap
   * @param columnWidthMap
   */
  private void configureColumnsNATTable(final Map<Integer, String> propertyToLabelMap,
      final Map<Integer, Integer> columnWidthMap) {

    if (this.isFunction) {
      configurecloumns(propertyToLabelMap, columnWidthMap);
    }
    else {
      configurecloumns(propertyToLabelMap, columnWidthMap);
      propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_15, "Parameter Type");
      propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_16, "Parameter Responsible");
      propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_17, "System Element");
      propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_18, "HW Component");
      columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_15, 15 * 15);
      columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_16, 15 * 15);
      columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_17, 15 * 15);
      columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_18, 15 * 15);
    }
  }

  /**
   * @param propertyToLabelMap
   * @param columnWidthMap
   */
  private void configurecloumns(final Map<Integer, String> propertyToLabelMap,
      final Map<Integer, Integer> columnWidthMap) {
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_0, "");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_1, "");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_2, "Parameter");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_3, "Long Name");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_4, "Class");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_5, "Code Word");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_6, "Lower Limit");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_7, "Upper Limit");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_8, "Bitwise Limit");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_9, "Reference Value");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_10, "Exact Match");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_11, "Unit");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_12, "Ready for series");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_13, "Remarks");
    propertyToLabelMap.put(CommonUIConstants.COLUMN_INDEX_14, CommonUIConstants.REMARK_UNICODE);

    // The below map is used by NatTable to Map Columns with their respective widths
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_0, 6 * 6);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_1, 6 * 6);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_2, 10 * 15);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_3, 15 * 15);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_4, 10 * 15);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_5, 10 * 15);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_6, 10 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_7, 10 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_8, 10 * 15);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_9, 15 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_10, 15 * 6);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_11, 15 * 6);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_12, 15 * 10);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_13, 15 * 15);
    columnWidthMap.put(CommonUIConstants.COLUMN_INDEX_14, 15 * 15);

  }

  /**
   * Add reset filter button ICDM-1207
   */
  public void addResetAllFiltersAction() {
    // ICDM-1185
    getFilterTxtSet().add(this.paramTableSection.getFilterTxt());
    getRefreshComponentSet().add(this.paramFilterGridLayer);
    addResetFiltersAction();
  }

  private class ParamTreeFormat extends GroupByTreeFormat<Object> {

    /**
     * @param model
     * @param columnAccessor
     */
    public ParamTreeFormat(final GroupByModel model, final IColumnAccessor columnAccessor) {
      super(model, columnAccessor);
    }

    private final Map<String, Object> parentMapping = new HashMap<>();

    private ISortModel sortModel;

    /**
     * @return the parentMapping
     */
    public Object getParentMapping(final String paramName) {

      return this.parentMapping.get(paramName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSortModel(final ISortModel model) {
      this.sortModel = model;
    }

    /**
     * Populate path with a list describing the path from a root node to this element. Upon returning, the list must
     * have size >= 1, where the provided element identical to the list's last element. This implementation will use the
     * first object found for a last name as root node by storing it within a map. If there is already an object stored
     * for the lastname of the given element, it will be used as root for the path.
     */
    @Override
    public void getPath(final List<Object> path, final Object element) {
      if (element instanceof IParameter) {
        IParameter param = (IParameter) element;
        this.parentMapping.put(param.getName(), element);
      }
      else if (element instanceof ReviewRule) {
        ReviewRule rule = (ReviewRule) element;
        path.add(this.parentMapping.get(rule.getParameterName()));
      }
      else if (element instanceof DefaultRuleDefinition) {
        DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) element;
        path.add(this.parentMapping.get(defaultRule.getReviewRule().getParameterName()));
      }
      path.add(element);

    }

    /**
     * Simply always return <code>true</code>.
     *
     * @return <code>true</code> if this element can have child elements, or <code>false</code> if it is always a leaf
     *         node.
     */
    @Override
    public boolean allowsChildren(final Object element) {
      return true;
    }

    /**
     * Returns the comparator used to order path elements of the specified depth. If enforcing order at this level is
     * not intended, this method should return <code>null</code>. We do a simple sorting of the last names of the
     * persons to show so the tree nodes are sorted in alphabetical order.
     */
    @Override
    public Comparator<Object> getComparator(final int depth) {
      return new SortableTreeComparator<>(getParamComparator(SortColumns.SORT_PARAM_NAME), this.sortModel);

    }
  }

  /**
   * @return the ruleFilterGridLayer
   */
  @SuppressWarnings("rawtypes")
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.paramFilterGridLayer;
  }

  /**
   * Refreshes the NatTable.</br>
   * </br>
   * <b><i>StructuralRefreshCommand</b></i> refreshes all the layers because of which the sorting order might
   * change</br>
   * <b><i>VisualRefreshCommand</b></i> is used instead but this results in incorrect values when predefined filters are
   * applied. TODO: Need to find how to refresh specific layers
   */
  public void refreshNatTable() {
    this.paramFilterGridLayer.getComboGlazedListsFilterStrategy().applyToolBarFilterInAllColumns(false);
    final CustomGroupByDataLayer bodyDataLayer = this.paramFilterGridLayer.getBodyDataLayer();
    if (bodyDataLayer.getTreeRowModel().hasChildren(0)) {
      Display.getDefault().syncExec(() -> {
        bodyDataLayer.killCache();
        bodyDataLayer.updateTree();
        updateStatusBar(true);
      });
    }
    // Refresh the natTable
    this.natTable.doCommand(new VisualRefreshCommand());
  }

  public void reconstructNatTable() {
    this.natTable.dispose();
    this.propertyToLabelMap.clear();
    this.toolBarManager.removeAll();
    this.paramFilterGridLayer = null;

    createTableViewer();
    createToolBarAction();

    if (!this.paramTableSection.getFilterTxt().getText().isEmpty()) {
      this.paramTableSection.getFilterTxt().setText(this.paramTableSection.getFilterTxt().getText());
    }
    if (this.natTable != null) {
      this.natTable.refresh();
    }
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable

    this.paramTableSection.getFormTwo().getBody().pack();
    this.paramTableSection.getSectionTwo().layout();

    if (null != this.paramTableSection.getListPage().getFuncSelcSec()) {
      this.paramTableSection.getListPage().getFuncSelcSec().getFormOne().getBody().pack();
      this.paramTableSection.getListPage().getFuncSelcSec().getSectionOne().layout();
    }

    setStatusBarMessage(false);
  }


  /**
   * @return This method defines GridData
   */
  private GridData getGridData() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart iworkbenchpart, final ISelection iselection) {
    updateStatusBar(false);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelction) {
    ParameterDataProvider paramDataProvider =
        this.paramTableSection.getListPage().getEditor().getEditorInput().getParamDataProvider();
    int toltalCount = 0;
    if (null != paramDataProvider) {
      toltalCount = null != paramDataProvider.getParamRulesOutput()
          ? paramDataProvider.getParamRulesOutput().getParamMap().size() : 0;
    }
    this.paramTableSection.getListPage().getEditor().updateStatusBar(false, toltalCount,
        getCustomFilterGridLayer().getRowHeaderLayer().getPreferredRowCount());
  }

  public class Test implements TreeList.Format {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowsChildren(final Object arg0) {
      return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comparator getComparator(final int arg0) {
      return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getPath(final List arg0, final Object arg1) {
      // empty overridden method
      // implementation not needed
    }


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(outlineSelection);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolBarManager;
  }


  /**
   * @return the natTable
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
  }


  /**
   * @return the selectionProvider
   */
  public RowSelectionProvider<Object> getSelectionProvider() {
    return this.selectionProvider;
  }


  /**
   * @return the totalRowCount
   */
  public int getTotalRowCount() {
    return this.totalRowCount;
  }

  // ICDM-500
  /**
   * Create the Pre-Defined Filter for the List page
   */
  public void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    addHelpAction(this.toolBarManager);
    ReviewParamToolBarFilters toolBarFilters = new ReviewParamToolBarFilters(this.parameterDataProvider);

    this.paramFilterGridLayer.getComboGlazedListsFilterStrategy()
        .setToolBarFilterMatcher(toolBarFilters.getToolBarMatcher());
    ListPage listPage = this.paramTableSection.getListPage();
    ParamCollectionDataProvider dataProvider = listPage.getEditor().getEditorInput().getDataProvider();
    ToolBar toolbar;
    if (dataProvider.hasVersions(listPage.getCdrFunction())) {
      toolbar = this.toolBarManager.createControl(listPage.getFuncSelcSec().getSectionOne());
    }
    else {
      toolbar = this.toolBarManager.createControl(this.paramTableSection.getSectionTwo());
    }

    final ReviewRuleToolBarActionSet toolBarActionSet = new ReviewRuleToolBarActionSet(null, this);

    // Filters for value types

    // ICDM-2439
    // Filter for compliance parameters
    toolBarActionSet.complianceFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter for compliance parameters
    toolBarActionSet.nonComplianceFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    this.toolBarManager.add(new Separator());

    // Filter for black list parameters
    toolBarActionSet.blackListFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter for non black list parameters
    toolBarActionSet.nonBlackListFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    this.toolBarManager.add(new Separator());

    // Filter for QSSD parameters
    toolBarActionSet.qSSDFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter for non QSSD parameters
    toolBarActionSet.nonQSSDFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    this.toolBarManager.add(new Separator());

    // Filter For the Axis point - Value Type
    toolBarActionSet.axisPointFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter for the Ascii - Value Type
    toolBarActionSet.asciiFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Value Block - Value Type
    toolBarActionSet.valueBlockFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter for the Value- Value Type
    toolBarActionSet.valueFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Curve - Value Type
    toolBarActionSet.curveFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter for the Map Value Type
    toolBarActionSet.mapFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);

    this.toolBarManager.add(new Separator());

    // Filters for parameter class

    // Filter For the Rivet - Class Type
    toolBarActionSet.rivetFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Nail - Class Type
    toolBarActionSet.nailFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Screw - Class Type
    toolBarActionSet.screwFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Undefined - Class Type
    toolBarActionSet.undefinedFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);

    this.toolBarManager.add(new Separator());

    // Filters for Codeword

    // Filter For the Yes - Code Type
    toolBarActionSet.codeYesFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the No - Code Type
    toolBarActionSet.codeNoFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);

    this.toolBarManager.add(new Separator());

    // Rule specific filters

    // Filters for review type

    // Filter For the Manual - Review Type
    toolBarActionSet.manualFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Automatic - Review Type
    toolBarActionSet.automaticFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Undefined - Review Type Icdm-654
    toolBarActionSet.reviewUnDefFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);

    // iCDM-650
    this.toolBarManager.add(new Separator());

    // Filters for rule dependency

    // Parameters with dependency
    toolBarActionSet.paramWithDepnAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Parameters without dependency
    toolBarActionSet.paramWithNoDepnAction(this.toolBarManager, toolBarFilters, this.natTable, null);

    this.toolBarManager.add(new Separator());

    // Filters for rule status

    // No Rule exists
    toolBarActionSet.ruleNotExistsFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Review Rule - Complete
    toolBarActionSet.ruleCompleteFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);
    // Filter For the Review Rule - InComplete // NOT Exists
    toolBarActionSet.ruleInCompleteFilterAction(this.toolBarManager, toolBarFilters, this.natTable, null);

    addResetAllFiltersAction();
    this.toolBarManager.update(true);


    if (dataProvider.hasVersions(listPage.getCdrFunction())) {
      listPage.getFuncSelcSec().getSectionOne().setTextClient(toolbar);
    }
    else {
      this.paramTableSection.getSectionTwo().setTextClient(toolbar);
    }
  }


  /**
   * If element is CDR Rule or Default Rule, get the CDRFuncParameter from the rule
   *
   * @param element row item
   * @return CDR Func param
   */
  public IParameter getParameter(final Object element) {


    IParameter param = null;
    // Get parameter from rule
    if (element instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) element;
      ParameterDataProvider<D, P> paramDataProvider = ParamNatTable.this.parameterDataProvider;
      String parameterName = rule.getParameterName();
      return paramDataProvider.getParamRulesOutput().getParamMap().get(parameterName) == null
          ? paramDataProvider.getParamRulesOutput().getParamMap().get(parameterName.toUpperCase(Locale.getDefault()))
          : paramDataProvider.getParamRulesOutput().getParamMap().get(parameterName);

    }
    // Get parameter from default rule definition
    else if (element instanceof DefaultRuleDefinition) {
      ReviewRule rule = ((DefaultRuleDefinition) element).getReviewRule();
      ParameterDataProvider<D, P> paramDataProvider = ParamNatTable.this.parameterDataProvider;

      String parameterName = rule.getParameterName();
      return paramDataProvider.getParamRulesOutput().getParamMap().get(parameterName) == null
          ? paramDataProvider.getParamRulesOutput().getParamMap().get(parameterName.toUpperCase(Locale.getDefault()))
          : paramDataProvider.getParamRulesOutput().getParamMap().get(parameterName);

    }
    // Parameter is directly available
    else if (element instanceof IParameter) {
      param = (IParameter) element;
    }

    return param;
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
        for (int i = 2; i <= 13; i++) {
          ParamNatTable.this.columnLabelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
        }
        // Register column attributes
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_NAME), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 2);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_LONGNAME), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 3);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_CLASS), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 4);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_CODEWORD), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 5);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_LOWERLIMIT), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 6);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_UPPERLIMIT), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 7);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_BITWISE), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 8);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_REFVALUE), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 9);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_EXACT_MATCH), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 10);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_UNIT), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 11);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_PARAM_READY_SERIES), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 12);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getParamComparator(SortColumns.SORT_REMARKS), DisplayMode.NORMAL, CUSTOM_COMPARATOR_LABEL + 13);

      }
    };
  }


  /**
   * @return the allColumnFilterMatcher
   */
  public ParamRulesColumnFilterMatcher<IParameter> getAllColumnFilterMatcher() {
    return this.allColumnFilterMatcher;
  }


  /**
   * @param parameterDataProvider the parameterDataProvider to set
   */
  public void setParameterDataProvider(final ParameterDataProvider<D, P> parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }


  /**
   * @return the parameterDataProvider
   */
  public ParameterDataProvider<D, P> getParameterDataProvider() {
    return this.parameterDataProvider;
  }


  /**
   * @return the natInputToColumnConverter
   */
  public AbstractNatInputToColumnConverter getNatInputToColumnConverter() {
    return this.natInputToColumnConverter;
  }


  /**
   * @return the inputList
   */
  public List<Object> getInputList() {
    return this.inputList;
  }


  /**
   * @param inputList the inputList to set
   */
  public void setInputList(final List<Object> inputList) {
    this.inputList = inputList;
  }


}
