/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPDefBO;
import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.action.FC2WPNatToolBarActionSet;
import com.bosch.caltool.icdm.ui.editors.CompareFC2WPRowObject;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditor;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditorInput;
import com.bosch.caltool.icdm.ui.editors.pages.natsupport.FC2WPValidationEditConfiguration;
import com.bosch.caltool.icdm.ui.table.filters.FC2WPColumnFilterMatcher;
import com.bosch.caltool.icdm.ui.table.filters.FC2WPNatToolBarFilters;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.caltool.nattable.configurations.FilterRowCustomConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The Class FC2WPNatFormPage.
 *
 * @author gge6cob
 */
public class FC2WPNatFormPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /** Form tool kit instance. */
  private FormToolkit formToolkit;

  /** Non scrollable form instance. */
  private Form nonScrollableForm;

  /** Composite one instance. */
  private Composite compositeOne;

  /** Table section instance. */
  private Section tableSection;

  /** Table form instance. */
  private Form tableForm;

  /*
   * Tool bar filters instance
   */
  private FC2WPNatToolBarFilters toolBarFilters;

  /** The filter txt. */
  private Text filterTxt;

  /** The editor. */
  private final FC2WPEditor editor;

  /*
   * Nat table filter matcher
   */
  private FC2WPColumnFilterMatcher<FC2WPMapping> allColumnFilterMatcher;


  /** The nat input to colum converter. */
  private AbstractNatInputToColumnConverter natInputToColumConverter;

  /*
   * NAT table grid layer
   */
  private CustomFilterGridLayer fc2wpFilterGridLayer;

  /*
   * NAT table instance
   */
  private CustomNATTable natTable;

  /*
   * Total table row count
   */
  private int totTableRowCount;

  /*
   * Tool bar manager instance
   */
  private ToolBarManager toolBarManager;

  /*
   * Column header configuration
   */
  private FC2WPNatToolBarActionSet toolBarActionSet;


  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  /** The group by header layer. */
  private GroupByHeaderLayer groupByHeaderLayer;

  Set<FC2WPMapping> fc2wpMappingData;


  private SortedSet<FC2WPRelvPTType> relevantPTtypeSet = new TreeSet<>();

  private FC2WPMapping currentSelection;

  private final List<FC2WPMapping> selMappingList = new ArrayList<>();

  /**
   * Map to hold the filter action text and its current state (checked - true or false)
   */
  private final Map<String, Boolean> toolBarFilterCurrStateMap = new ConcurrentHashMap<>();

  /**
   * @return the selMappingList
   */
  public List<FC2WPMapping> getSelMappingList() {
    return this.selMappingList;
  }

  private RowSelectionProvider<FC2WPMapping> selectionProvider;

  private final FC2WPEditorInput editorInput;


  /** Column to display . */
  static final int FC_NAME_COL_NUMBER = 0;
  static final int FC_LONG_NAME_COL_NUMBER = 1;
  static final int WP_COL_NUMBER = 2;
  static final int RESOURCE_COL_NUMBER = 3;

  static final int WPID_COL_NUMBER = 4;
  static final int BC_COL_NUMBER = 5;
  static final int PTTYPE_COL_NUMBER = 6;
  static final int CONTACT1_COL_NUMBER = 7;
  static final int CONTACT2_COL_NUMBER = 8;

  static final int IS_COCAGREED_COL_NUMBER = 9;
  static final int COC_AGREEDON_COL_NUMBER = 10;
  static final int COC_RESP_AGREED_COL_NUMBER = 11;

  static final int COMMENTS_COL_NUMBER = 12;
  static final int FC2WP_INFO_COL_NUMBER = 13;
  static final int IS_INICDMA2L_COL_NUMBER = 14;
  static final int IS_FC_IN_SDOM_COL_NUMBER = 15;
  static final int IS_FC_WITH_PARAMS_COL_NUMBER = 16;
  static final int ISDELETED_COL_NUMBER = 17;
  static final int CREATED_DATE_COL_NUMBER = 18;
  static final int MODIFIED_DATE_COL_NUMBER = 19;

  static final int TOT_COLUMNS_TO_COMPARE = 18;
  private final Map<FC2WPMappingResult, Map<String, FC2WPMapping>> mappingDetailsMap = new ConcurrentHashMap<>();
  /**
  *
  */
  private static final String GROUP_GROUP_REGION = "COLUMN_GROUP_GROUP_HEADER";


  /**
   * @return the mappingDetailsMap
   */
  public Map<FC2WPMappingResult, Map<String, FC2WPMapping>> getMappingDetailsMap() {
    return this.mappingDetailsMap;
  }

  Map<String, String> funcNames = new HashMap<>();
  private final SortedSet<CompareFC2WPRowObject> compareRowObjects = new TreeSet<>();
  private int colCounter = 0;
  private final List<FC2WPMappingWithDetails> fc2wpMappingDetailsList = new ArrayList<>();
  private final List<FC2WPMappingResult> fc2wpMappinResultList = new ArrayList<>();
  private final Map<FC2WPMappingWithDetails, FC2WPVersion> fc2wpVerMap = new ConcurrentHashMap<>();
  private boolean isCompareEditor;

  /**
   *
   */
  private FC2WPDefBO selFc2wpDefBo;

  /**
   *
   */
  private FC2WPMappingWithDetails selFc2wpMapDetail;

  /**
   *
   */
  private FC2WPDef selFc2wpDef;
  private boolean resetState;

  /**
   * @return the isCompareEditor
   */
  public boolean isCompareEditor() {
    return this.isCompareEditor;
  }

  /**
   * Gets the table section.
   *
   * @return the tableSection
   */
  public Section getTableSection() {
    return this.tableSection;
  }

  /**
   * Instantiates a new FC2WP nat form page.
   *
   * @param fc2wpEditor the fc2wp editor
   * @param editorInput the editor input
   */
  public FC2WPNatFormPage(final FC2WPEditor fc2wpEditor, final FC2WPEditorInput editorInput) {
    super(fc2wpEditor, "FC-WP", Messages.getString("FC2WPFormPage.label"));
    this.editor = fc2wpEditor;
    this.editorInput = editorInput;
    this.fc2wpMappingDetailsList.addAll(editorInput.getFC2WPDefBO().getFC2WPMappingWithDetailsList());
    if (this.fc2wpMappingDetailsList.size() > 1) {
      this.isCompareEditor = true;
    }
    this.editorInput.getFc2wpDef();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    final GridData gridData = GridDataUtil.getInstance().createGridData();
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    if (this.isCompareEditor) {
      this.nonScrollableForm.setText("Versions Compare");
    }
    else {
      this.nonScrollableForm.setText(this.editorInput.getFc2wpVersion().getName());
    }
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

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
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createComposite();
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * This method initializes composite.
   */
  private void createComposite() {
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessVerticalSpace = true;
    gridData1.verticalAlignment = GridData.FILL;
    this.compositeOne = this.nonScrollableForm.getBody();
    final GridLayout gridLayout = new GridLayout();
    createTableViewerSection();
    this.compositeOne.setLayout(gridLayout);
    this.compositeOne.setLayoutData(gridData1);
  }

  /**
   * Creates the table viewer section.
   */
  private void createTableViewerSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.tableSection =
        SectionUtil.getInstance().createSection(this.compositeOne, this.formToolkit, "List of FC-WP Mappings");
    this.tableSection.setLayoutData(gridData);
    createTableForm(this.formToolkit);
    this.tableSection.setClient(this.tableForm);
    this.tableSection.getDescriptionControl().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    this.tableSection.getDescriptionControl()
        .setFont(new Font(this.tableForm.getBody().getDisplay(), "Segoe UI", 10, SWT.BOLD));
    this.tableSection.getDescriptionControl().setEnabled(false);
  }

  /**
   * Creates the table form.
   *
   * @param toolkit the toolkit
   */
  private void createTableForm(final FormToolkit toolkit) {
    // create table form
    this.tableForm = toolkit.createForm(this.tableSection);
    // create filter text
    this.filterTxt = toolkit.createText(this.tableForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    // get parameters
    this.tableForm.getBody().setLayout(new GridLayout());
    createTable();
  }

  /**
   * Creates the table.
   */
  private void createTable() {

    // Retrieve data from WebService
    getFC2WPMappingData(this.editorInput.getFc2wpVersion().getId());


    Map<Integer, Integer> columnWidthMap = createNatTableColumns();

    this.natInputToColumConverter = new FC2WPNatInputToColumnConverter();
    IConfigRegistry configRegistry = new ConfigRegistry();

    // Group by Model
    GroupByModel groupByModel = new GroupByModel();

    List<Integer> colsToHide = new ArrayList<>();

    this.totTableRowCount = this.funcNames.keySet().size();
    this.colCounter = this.propertyToLabelMap.size();
    // A Custom Filter Grid Layer is constructed
    createCustomGridLayer(columnWidthMap, configRegistry, groupByModel, colsToHide);
    // Enable Tool bar filters
    this.toolBarFilters = new FC2WPNatToolBarFilters(this);
    this.fc2wpFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    // Enable column filters
    this.allColumnFilterMatcher = new FC2WPColumnFilterMatcher<>();
    this.fc2wpFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);

    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.fc2wpFilterGridLayer, 0, 1);


    // Create NAT table
    this.natTable = new CustomNATTable(this.tableForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        this.fc2wpFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }


    // Add Configurations
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(getGridData());
    CustomNatTableStyleConfiguration natTabConfig = new CustomNatTableStyleConfiguration();
    natTabConfig.setEnableAutoResize(true);
    this.natTable.addConfiguration(natTabConfig);
    this.natTable.addConfiguration(new FilterRowCustomConfiguration(this.propertyToLabelMap.size()) {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistryLocal) {
        super.configureRegistry(configRegistryLocal);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    // add listeners to save state
    addTableListeners();
    // Column Comparator
    this.natTable
        .addConfiguration(new FC2WPNatComparatorConfig((this.fc2wpFilterGridLayer.getColumnHeaderDataLayer())));

    // Group columns
    groupColumns();

    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    addHeaderConfig();

    // Configure toolbar actions
    configureToolbarAct();
    // Configure NAT table
    this.natTable.configure();

    // Load the saved state of NAT table
    loadState();

    // Column chooser configuration
    CustomDefaultBodyLayerStack bodyLayer = this.fc2wpFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.fc2wpFilterGridLayer.getColumnHeaderLayer(), this.fc2wpFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);

    attachToolTip(this.natTable);
    // get the reference to the SelectionLayer
    SelectionLayer selectionLayer = this.fc2wpFilterGridLayer.getBodyLayer().getSelectionLayer();
    selLayerForCompEditor(selectionLayer);

    this.fc2wpFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));
    addSelectionProvider(selectionLayer);


    createMouseListenerForTable(selectionLayer);
  }

  /**
   * @param selectionLayer
   */
  private void createMouseListenerForTable(final SelectionLayer selectionLayer) {
    this.natTable.addMouseListener(new MouseListener() {


      @Override
      public void mouseUp(final MouseEvent mouseevent) {
        // To be implemented
      }

      @Override
      public void mouseDown(final MouseEvent mouseevent) {

        if (FC2WPNatFormPage.this.isCompareEditor) {
          int rowPosition = LayerUtil.convertRowPosition(FC2WPNatFormPage.this.natTable,
              FC2WPNatFormPage.this.natTable.getRowPositionByY(mouseevent.y), selectionLayer);
          int columnPosition = LayerUtil.convertColumnPosition(FC2WPNatFormPage.this.natTable,
              FC2WPNatFormPage.this.natTable.getColumnPositionByX(mouseevent.x),
              FC2WPNatFormPage.this.fc2wpFilterGridLayer.getDummyDataLayer());
          Object rowObject = FC2WPNatFormPage.this.fc2wpFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
          if ((rowObject instanceof CompareFC2WPRowObject) && FC2WPNatFormPage.this.isCompareEditor &&
              (columnPosition > 1)) {
            // get the selected row obj
            CompareFC2WPRowObject selObj = (CompareFC2WPRowObject) rowObject;

            // get selected mapping result and values
            FC2WPMappingResult fc2wpMappingResult =
                selObj.getColumnDataMapper().getColumnIndexFC2WPMapResult().get(columnPosition);
            Map<String, FC2WPMapping> fc2wpVal = FC2WPNatFormPage.this.mappingDetailsMap.get(fc2wpMappingResult);
            FC2WPNatFormPage.this.currentSelection = fc2wpVal.get(selObj.getFuncName());
            FC2WPNatFormPage.this.selMappingList.clear();
            FC2WPNatFormPage.this.selMappingList.add(getCurrentSelection());
            if ((null != FC2WPNatFormPage.this.currentSelection) && (null != fc2wpMappingResult)) {
              editInCompareEditor(fc2wpMappingResult);
            }
            else {
              FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().setEnabled(false);
              FC2WPNatFormPage.this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(false);
            }
          }
          else {
            FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().setEnabled(false);
            FC2WPNatFormPage.this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(false);
          }
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseevent) {
        if (FC2WPNatFormPage.this.editorInput.getFC2WPDefBO().isModifiable()) {
          FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().run();
        }
      }
    });
  }

  /**
   * @param selectionLayer SelectionLayer
   */
  public void selLayerForCompEditor(final SelectionLayer selectionLayer) {
    if (this.isCompareEditor) {
      // select cell with column position 3 and row position 0
      selectionLayer.setSelectedCell(2, 0);
      // freeze the first two columns
      this.natTable.doCommand(new FreezeSelectionCommand());
      // reset the selection to first row first column
      selectionLayer.setSelectedCell(0, 0);
    }
  }

  /**
   *
   */
  public void configureToolbarAct() {
    if (this.isCompareEditor) {
      createCompareToolBarAction();

      DataLayer bodyDataLayer = this.fc2wpFilterGridLayer.getDummyDataLayer();
      IRowDataProvider<CompareFC2WPRowObject> bodyDataProvider = this.fc2wpFilterGridLayer.getBodyDataProvider();
      final CompareFC2WPLabelAccumulator compFC2WPLabelAccumulator =
          new CompareFC2WPLabelAccumulator(bodyDataLayer, bodyDataProvider);
      bodyDataLayer.setConfigLabelAccumulator(compFC2WPLabelAccumulator);

      this.natTable.addConfiguration(new FC2WPValidationEditConfiguration());
    }
    else {
      createToolBarAction();
    }
  }

  /**
   * @param columnWidthMap Map<Integer, Integer>
   * @param configRegistry IConfigRegistry
   * @param groupByModel GroupByModel
   * @param colsToHide List<Integer>
   */
  public void createCustomGridLayer(final Map<Integer, Integer> columnWidthMap, final IConfigRegistry configRegistry,
      final GroupByModel groupByModel, final List<Integer> colsToHide) {
    if (this.isCompareEditor) {
      CustomFC2WPompareHeaderDataProvider columnHeaderDataProvider = new CustomFC2WPompareHeaderDataProvider();
      this.fc2wpFilterGridLayer = new CustomFilterGridLayer<CompareFC2WPRowObject>(configRegistry,
          this.compareRowObjects, columnWidthMap, new CustomFC2WPCompareColumnPropertyAccessor<>(),
          columnHeaderDataProvider, FC2WPNatComparatorConfig.getColumnComparator(0), this.natInputToColumConverter,
          this, null, false, false, true);
      this.groupByHeaderLayer =
          new GroupByHeaderLayer(groupByModel, this.fc2wpFilterGridLayer, columnHeaderDataProvider);
    }
    else {
      this.fc2wpFilterGridLayer = new CustomFilterGridLayer(configRegistry, this.compareRowObjects,
          this.propertyToLabelMap, columnWidthMap, FC2WPNatComparatorConfig.getColumnComparator(0),
          this.natInputToColumConverter, this, null, groupByModel, colsToHide, false, true, null, null, false);
      this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.fc2wpFilterGridLayer,
          this.fc2wpFilterGridLayer.getColumnHeaderDataProvider());
    }
  }


  /**
   * @param selectionLayer
   */
  private void addSelectionProvider(final SelectionLayer selectionLayer) {
    this.selectionProvider =
        new RowSelectionProvider<>(selectionLayer, this.fc2wpFilterGridLayer.getBodyDataProvider(), false);

    this.selectionProvider.addSelectionChangedListener(event -> {
      FC2WPNatFormPage.this.selMappingList.clear();
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();

      if ((selection != null) && (selection.getFirstElement() != null) && !FC2WPNatFormPage.this.isCompareEditor) {

        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
          CompareFC2WPRowObject selObj = (CompareFC2WPRowObject) iterator.next();
          Map<String, FC2WPMapping> fc2wpVal = FC2WPNatFormPage.this.mappingDetailsMap
              .get(selObj.getColumnDataMapper().getColumnIndexFC2WPMapResult().get(3));
          FC2WPNatFormPage.this.selMappingList.add(fc2wpVal.get(selObj.getFuncName()));
        }
        FC2WPNatFormPage.this.currentSelection = FC2WPNatFormPage.this.selMappingList.get(0);
        if (FC2WPNatFormPage.this.editorInput.getFC2WPDefBO().isModifiable()) {
          FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().setEnabled(true);
          FC2WPNatFormPage.this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(true);
        }
      }
      if ((selection != null) && selection.isEmpty() &&
          event.getSource().equals(FC2WPNatFormPage.this.selectionProvider) &&
          (FC2WPNatFormPage.this.currentSelection != null)) {
        FC2WPNatFormPage.this.selectionProvider
            .setSelection(new StructuredSelection(FC2WPNatFormPage.this.currentSelection));
      }
      FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction()
          .setEnabled(FC2WPNatFormPage.this.editorInput.getFC2WPDefBO().isModifiable() &&
              !FC2WPNatFormPage.this.getSelMappingList().isEmpty());
    });

    getSite().setSelectionProvider(this.selectionProvider);
  }

  /**
   *
   */
  private void addHeaderConfig() {
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GROUP_GROUP_REGION, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(FC2WPNatFormPage.this.natTable)
                .withMenuItemProvider(removeColumnMenuItemProvider("Remove")).build()));
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(FC2WPNatFormPage.this.natTable).withColumnChooserMenuItem()
                .withMenuItemProvider((final NatTable natTab, final Menu popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      FC2WPNatFormPage.this.resetState = true;
                      FC2WPNatFormPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    addHeaderStyle();
  }

  /**
   *
   */
  private void addHeaderStyle() {
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
    this.fc2wpFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));
  }

  /**
   *
   */
  private void addTableListeners() {
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
    this.natTable.addDisposeListener(event -> {
      try {
        this.natTable.saveState();
      }
      catch (IOException ioe) {
        CDMLogger.getInstance().warn("Failed to save FC2WP  nat table state", ioe, Activator.PLUGIN_ID);
      }
    });
  }

  /**
   * @return
   */
  private Map<Integer, Integer> createNatTableColumns() {
    this.propertyToLabelMap = new HashMap<>();

    Map<Integer, Integer> columnWidthMap = new HashMap<>();
    // Configure columns
    configureColumnsNATTable(columnWidthMap);
    boolean updatePropertyToLabelMap = true;

    for (Map.Entry<String, String> entry : this.funcNames.entrySet()) {
      CompareFC2WPRowObject compareRow = new CompareFC2WPRowObject();
      compareRow.setFuncName(entry.getKey());
      compareRow.setFuncDesc(entry.getValue());
      int columnCounter = 1;

      for (FC2WPMappingResult mappingRes : this.fc2wpMappinResultList) {
        Map<String, FC2WPMapping> mappingValues = this.mappingDetailsMap.get(mappingRes);
        FC2WPMapping fc2wpMapping = mappingValues.get(entry.getKey());
        compareRow.addFC2WPMapping(fc2wpMapping, mappingRes);
        if (updatePropertyToLabelMap) {
          columnCounter = updatePropToLabelMap(columnWidthMap, columnCounter);
        }
      }
      updatePropertyToLabelMap = false;
      this.compareRowObjects.add(compareRow);
    }
    if (this.funcNames.isEmpty()) {
      updatePropToLabelMap(columnWidthMap, 1);
    }
    return columnWidthMap;
  }

  /**
   *
   */
  private void createCompareToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.tableSection);
    final Separator separator = new Separator();
    // Filter for PT-types
    if ((null != this.relevantPTtypeSet) && !this.relevantPTtypeSet.isEmpty()) {
      this.relevantPTtypeSet.clear();
      for (FC2WPDef selDef : this.editorInput.getFC2WPDefBO().getFc2wpDefMap().values()) {
        this.relevantPTtypeSet
            .addAll(this.editorInput.getFC2WPDefBO().getRelevantPTTypeDataForCompEditor(selDef.getId()));
      }
      this.toolBarFilters.setRelevantPTtypeSet(this.relevantPTtypeSet);
    }

    this.toolBarActionSet = new FC2WPNatToolBarActionSet(this, this.fc2wpFilterGridLayer, this.editorInput);

    this.toolBarActionSet.isDifferentRowAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isNotDifferentRowAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarActionSet.isWpDiffAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isWpNotDiffAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarActionSet.isContactDiffAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isContactNotDiffAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarActionSet.isAgreedDiffAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isNotAgreedAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarActionSet.isFcSdomFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isFcNotInSdomFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isInIcdmA2lAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isNotInIcdmA2lAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarActionSet.hasReleventPTTypeAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.hasNotRevelantPTtypeaction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);
    this.toolBarManager.update(true);
    this.tableSection.setTextClient(toolbar);
    ToolBarManager toolBarformManager = (ToolBarManager) this.tableForm.getToolBarManager();
    this.toolBarActionSet.editFC2WPMapping(toolBarformManager);
    this.toolBarActionSet.deleteFC2WPMapping(toolBarformManager);
    FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().setEnabled(false);
    FC2WPNatFormPage.this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(false);
    this.tableForm.getToolBarManager().update(true);
  }


  private IMenuItemProvider removeColumnMenuItemProvider(final String menuLabel) {
    return (final NatTable natTab, final Menu popupMenu) -> {
      MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
      menuItem.setText(menuLabel);
      menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
      menuItem.setEnabled(true);
      menuItem.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          addRemoveColumnAction(natTab, event);
        }

      });
    };
  }

  private void removeFromCompareEditor(final FC2WPMappingResult mappingResultToRemove) {
    int resIndex = this.fc2wpMappinResultList.indexOf(mappingResultToRemove);
    if (resIndex == 0) {
      CDMLogger.getInstance().infoDialog("Cannot remove the primary version from the editor", Activator.PLUGIN_ID);
    }
    else {
      this.editorInput.getFC2WPDefBO().getFc2wpVersionList().remove(resIndex);
      this.editorInput.getFC2WPDefBO().getFC2WPMappingWithDetailsList().remove(resIndex);
      reconstructNatTable();
    }
  }

  class CustomFC2WPompareHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String columnHeaderLabel = FC2WPNatFormPage.this.propertyToLabelMap.get(columnIndex);

      return columnHeaderLabel == null ? "" : columnHeaderLabel;
    }

    @Override
    public int getColumnCount() {
      return FC2WPNatFormPage.this.colCounter;
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


  class CustomFC2WPCompareColumnPropertyAccessor<T> implements IColumnAccessor<T> {

    /**
     * This method has been overridden so that it returns the passed row object. The above behavior is required for use
     * of custom comparators for sorting which requires the Row object to be passed without converting to a particular
     * column String value {@inheritDoc}
     */
    @Override
    public Object getDataValue(final T compareFC2WPRowObject, final int columnIndex) {
      return compareFC2WPRowObject;
    }


    @Override
    public void setDataValue(final T sysConstNatModel, final int columnIndex, final Object newValue) {
      // To be implemented
    }

    @Override
    public int getColumnCount() {
      return FC2WPNatFormPage.this.colCounter;
    }


  }

  /**
   * @param natTable2
   */
  private void attachToolTip(final CustomNATTable natTableObj) {
    DefaultToolTip toolTip = new FC2WPNatTableToolTip(natTableObj, this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }


  private void getFC2WPMappingData(final Long fc2wpVersID) {
    try {

      ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Retrieving FC2WP mapping data...", 100);
        monitor.worked(20);
        loadData(fc2wpVersID);
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      // interrupting the thread
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Calls FC2WP webservice method and loads the FC2WP mapping
   *
   * @param fc2wpVersID
   * @param a2lVersion
   * @throws Exception
   */
  private void loadData(final Long fc2wpVersID) {
    FC2WPNatFormPage.this.fc2wpMappingData = new TreeSet<>();
    int versCounter = 0;
    try {
      // Load the contents using webservice calls

      for (FC2WPMappingWithDetails fc2wpMappingDetail : FC2WPNatFormPage.this.fc2wpMappingDetailsList) {
        FC2WPMappingResult mappingResult = new FC2WPMappingResult(fc2wpMappingDetail);
        Set<FC2WPMapping> fc2wpMappingDataSet = mappingResult.getFc2wpDataSet();
        HashMap<String, FC2WPMapping> mappingData = new HashMap<>();
        // add all the func names
        for (FC2WPMapping mapping : fc2wpMappingDataSet) {
          FC2WPNatFormPage.this.funcNames.put(mapping.getFunctionName(), mapping.getFunctionDesc());
          mappingData.put(mapping.getFunctionName(), mapping);
        }
        FC2WPNatFormPage.this.mappingDetailsMap.put(mappingResult, mappingData);
        FC2WPNatFormPage.this.fc2wpMappinResultList.add(mappingResult);
        Object[] verObjArray = this.editorInput.getFC2WPDefBO().getFc2wpVersionList().toArray();
        FC2WPVersion fc2wpVer = (FC2WPVersion) (verObjArray[versCounter]);
        FC2WPNatFormPage.this.fc2wpVerMap.put(fc2wpMappingDetail, fc2wpVer);
        versCounter++;
      }
      FC2WPNatFormPage.this.editorInput.setFC2WPMappingResult(FC2WPNatFormPage.this.fc2wpMappinResultList.get(0));
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error("Error loading FC2WP Mapping Data for version number :" + fc2wpVersID, exp,
          Activator.PLUGIN_ID);
    }

  }


  /**
   * Sets the status bar message.
   */
  protected void setStatusBarMessage() {
    // Auto-generated method stub

  }

  /**
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.natTable.resetState();
      }
      this.natTable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load FC2WP nat table state", ioe,
          com.bosch.calcomp.adapter.logger.Activator.PLUGIN_ID);
    }
  }

  /**
   * Creates the tool bar action.
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.tableSection);

    // Filter for PT-types
    if ((null != this.relevantPTtypeSet) && !this.relevantPTtypeSet.isEmpty()) {
      this.relevantPTtypeSet.clear();
    }
    this.relevantPTtypeSet = this.editorInput.getFC2WPDefBO().getRelPTTypeData();
    this.toolBarFilters.setRelevantPTtypeSet(this.relevantPTtypeSet);

    // NAT table action set
    this.toolBarActionSet = new FC2WPNatToolBarActionSet(this, this.fc2wpFilterGridLayer, this.editorInput);

    final Separator separator = new Separator();

    this.toolBarActionSet.isFcWithParamsFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isFcWithoutParamsFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isFcSdomFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isFcNotInSdomFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isInICDMFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isNotInICDMFilterAction(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isDeleted(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isNotDeleted(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isContactAssigned(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isContactNotAssigned(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isWPAssigned(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isWPNotAssigned(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarActionSet.isRevelantPTtype(this.toolBarManager, this.toolBarFilters);
    this.toolBarActionSet.isNotRevelantPTtype(this.toolBarManager, this.toolBarFilters);
    this.toolBarManager.add(separator);

    this.toolBarManager.update(true);
    this.tableSection.setTextClient(toolbar);
    ToolBarManager toolBarformManager = (ToolBarManager) this.tableForm.getToolBarManager();
    this.selFc2wpDef = this.editorInput.getFc2wpDef();
    this.selFc2wpMapDetail = this.editorInput.getFc2wpMapping();
    this.selFc2wpDefBo = this.editorInput.getFC2WPDefBO();
    addHelpAction(toolBarformManager);
    this.toolBarActionSet.addFC2WPMapping(toolBarformManager);
    this.toolBarActionSet.editFC2WPMapping(toolBarformManager);
    this.toolBarActionSet.deleteFC2WPMapping(toolBarformManager);
    toolBarformManager.add(separator);
    this.toolBarActionSet.setPowerTrainPreference(toolBarformManager, this, this.toolBarFilters);

    toolBarformManager.add(separator);

    this.tableForm.getToolBarManager().update(true);

    FC2WPNatFormPage.this.updateStatusBar(true);

    /*
     * By default - Is Not in ICDM is unchecked - Trigger filter event
     */
    this.toolBarActionSet.getCodeIsNotInICDMA2L().runWithEvent(new Event());
    addResetAllFiltersAction();
  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.fc2wpFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * Gets the grid data.
   *
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
   * Configure columns NAT table.
   *
   * @param propertyToLabelMap2 the property to label map 2
   * @param columnWidthMap the column width map
   */
  private void configureColumnsNATTable(final Map<Integer, Integer> columnWidthMap) {

    this.propertyToLabelMap.put(FC_NAME_COL_NUMBER, IUIConstants.FUNCTION);
    this.propertyToLabelMap.put(FC_LONG_NAME_COL_NUMBER, IUIConstants.FUNCTION_LONG_NAME);

    columnWidthMap.put(FC_NAME_COL_NUMBER, 120);
    columnWidthMap.put(FC_LONG_NAME_COL_NUMBER, 120);


    this.editorInput.setNatTableHeaderMap(this.propertyToLabelMap);
  }

  /**
   * @param columnWidthMap
   */
  private int updatePropToLabelMap(final Map<Integer, Integer> columnWidthMap, final int columnCounter) {
    int counter = columnCounter;
    counter = counter + 1;
    this.propertyToLabelMap.put(counter, IUIConstants.WORK_PACKAGE);
    columnWidthMap.put(counter, 120);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.RESOURCE);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.WP_ID_MCR);
    columnWidthMap.put(counter, 90);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.BC);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.PT_TYPE);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.FIRST_CONTACT);
    columnWidthMap.put(counter, 80);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.SECOND_CONTACT);
    columnWidthMap.put(counter, 80);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.IS_AGREED);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.AGREED_ON);
    columnWidthMap.put(counter, 90);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.RESP_FOR_AGREEMENT);
    columnWidthMap.put(counter, 100);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.COMMENT);
    columnWidthMap.put(counter, 120);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.FC2WP_INFO);
    columnWidthMap.put(counter, 120);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.IS_IN_ICDM);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.IS_FC_IN_SDOM);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.FC_WITH_PARAMS);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.DELETED);
    columnWidthMap.put(counter, 60);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.CREATED_DATE);
    columnWidthMap.put(counter, 100);
    counter = counter + 1;

    this.propertyToLabelMap.put(counter, IUIConstants.MODIFIED_DATE);
    columnWidthMap.put(counter, 100);
    return counter;
  }

  /**
   * Creates the filter txt.
   */
  private void createFilterTxt() {
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.filterTxt.setMessage("type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = FC2WPNatFormPage.this.filterTxt.getText().trim();
      FC2WPNatFormPage.this.allColumnFilterMatcher.setFilterText(text, true);
      FC2WPNatFormPage.this.fc2wpFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      FC2WPNatFormPage.this.fc2wpFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(FC2WPNatFormPage.this.fc2wpFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(FC2WPNatFormPage.this.groupByHeaderLayer, false);
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // Auto-generated method stub
  }

  /**
   * Gets the custom filter grid layer.
   *
   * @return the ruleFilterGridLayer
   */
  @SuppressWarnings("rawtypes")
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.fc2wpFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelction) {
    this.totTableRowCount = this.funcNames.keySet().size();
    this.editor.updateStatusBar(this.totTableRowCount,
        this.fc2wpFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }

  /**
   * handleGroupColumnsCommand method in ColumnGroupsCommandHandler class.
   */
  private void groupColumns() {
    List<Integer> selectedPositions = new ArrayList<>();
    selectedPositions.add(IS_COCAGREED_COL_NUMBER);
    selectedPositions.add(COC_AGREEDON_COL_NUMBER);
    selectedPositions.add(COC_RESP_AGREED_COL_NUMBER);
    int[] fullySelectedColumns =
        new int[] { IS_COCAGREED_COL_NUMBER, COC_AGREEDON_COL_NUMBER, COC_RESP_AGREED_COL_NUMBER };
    ColumnGroupModel columnGroupModel = this.fc2wpFilterGridLayer.getColumnGroupModel();
    columnGroupModel.addColumnsIndexesToGroup(IUIConstants.AGREEMENT_WITH_COC, fullySelectedColumns);
    columnGroupModel.setColumnGroupCollapseable(selectedPositions.size(), false);
    SelectionLayer selectionLayer = this.fc2wpFilterGridLayer.getBodyLayer().getSelectionLayer();
    selectionLayer.doCommand(
        new MultiColumnReorderCommand(selectionLayer, selectedPositions, selectedPositions.get(0).intValue()));
    selectionLayer.clear();

    if (FC2WPNatFormPage.this.isCompareEditor) {
      // second level grouping
      int columnGroupCounter = 0;
      int[] groupGroupSelectedColumns = new int[TOT_COLUMNS_TO_COMPARE];
      List<Integer> groupGroupSelectedColumnsList = new ArrayList<>(TOT_COLUMNS_TO_COMPARE);
      int colGrpNameCounter = 0;
      ColumnGroupModel columnGroupGroupModel = this.fc2wpFilterGridLayer.getColumnGroupGroupModel();
      List<Integer> selectedPosition = new ArrayList<>(TOT_COLUMNS_TO_COMPARE + 1);
      for (int colIndex = 2; colIndex < (this.propertyToLabelMap.size()); colIndex++) {
        selectedPosition.add(columnGroupCounter, colIndex);
        groupGroupSelectedColumns[columnGroupCounter] = colIndex;
        groupGroupSelectedColumnsList.add(colIndex);
        ++columnGroupCounter;
        if (columnGroupCounter == TOT_COLUMNS_TO_COMPARE) {

          // Add group
          columnGroupGroupModel.addColumnsIndexesToGroup(
              FC2WPNatFormPage.this.editorInput.getFC2WPDefBO().getFc2wpVersionList().get(colGrpNameCounter).getName(),
              groupGroupSelectedColumns);
          columnGroupGroupModel.setColumnGroupCollapseable(groupGroupSelectedColumns[0], true);
          columnGroupCounter = 0;
          groupGroupSelectedColumnsList.clear();
          colGrpNameCounter++;
        }

      }
    }

  }

  /**
   * Gets the nat table.
   *
   * @return the nat table
   */
  public CustomNATTable getNatTable() {
    return this.natTable;
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
   * Gets the relevant Pttypes.
   *
   * @return the relevant Pttypes
   */
  public SortedSet<FC2WPRelvPTType> getRelevantPTtypeSet() {
    return this.relevantPTtypeSet;
  }

  /**
   * @return the currentSelection
   */
  public FC2WPMapping getCurrentSelection() {
    return this.currentSelection;
  }

  /**
   * @param currentSelection the currentSelection to set
   */
  public void setCurrentSelection(final FC2WPMapping currentSelection) {
    this.currentSelection = currentSelection;
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
  public void reconstructNatTable() {
    this.natTable.dispose();
    this.propertyToLabelMap.clear();
    this.compareRowObjects.clear();
    this.funcNames.clear();
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
    if (this.tableForm.getToolBarManager() != null) {
      this.tableForm.getToolBarManager().removeAll();
    }
    FC2WPNatFormPage.this.mappingDetailsMap.clear();
    FC2WPNatFormPage.this.fc2wpVerMap.clear();
    this.fc2wpMappinResultList.clear();
    this.fc2wpFilterGridLayer = null;
    this.fc2wpMappingDetailsList.clear();
    this.fc2wpMappingDetailsList.addAll(this.editorInput.getFC2WPDefBO().getFC2WPMappingWithDetailsList());
    if (this.fc2wpMappingDetailsList.size() > 1) {
      this.isCompareEditor = true;
    }
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.tableForm.getBody().pack();
    this.tableSection.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.natTable != null) {
      this.natTable.refresh();
    }
    setStatusBarMessage(false);
  }

  /**
   * Save current state for the NAT table
   */
  private void saveState() {
    try {
      this.natTable.saveState();


    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save FC2WP  nat table state", ioe, Activator.PLUGIN_ID);
    }

  }


  /**
   * @return the selFc2wpDefBo
   */
  public FC2WPDefBO getSelFc2wpDefBo() {
    return this.selFc2wpDefBo;
  }


  /**
   * @return the selFc2wpMapDetail
   */
  public FC2WPMappingWithDetails getSelFc2wpMapDetail() {
    return this.selFc2wpMapDetail;
  }


  /**
   * @return the selFc2wpDef
   */
  public FC2WPDef getSelFc2wpDef() {
    return this.selFc2wpDef;
  }

  /**
   * @param natTab
   * @param event
   */
  private void addRemoveColumnAction(final NatTable natTab, final SelectionEvent event) {
    NatEventData natEventData = MenuItemProviders.getNatEventData(event);
    int columnPosition = natEventData.getColumnPosition();
    int convertColumnPosition = LayerUtil.convertColumnPosition(natTab, columnPosition,
        FC2WPNatFormPage.this.fc2wpFilterGridLayer.getColumnHeaderDataLayer());
    LabelStack regionLabels = natEventData.getRegionLabels();
    List<String> labels = regionLabels.getLabels();
    if (labels.contains(GROUP_GROUP_REGION)) {
      FC2WPMappingResult mappingResultToRemove = FC2WPNatFormPage.this.compareRowObjects.first().getColumnDataMapper()
          .getColumnIndexFC2WPMapResult().get(convertColumnPosition);

      removeFromCompareEditor(mappingResultToRemove);
    }
  }

  /**
   * @param fc2wpMappingResult
   */
  private void editInCompareEditor(final FC2WPMappingResult fc2wpMappingResult) {
    int resIndex = FC2WPNatFormPage.this.fc2wpMappinResultList.indexOf(fc2wpMappingResult);
    FC2WPVersion fc2wpVer = FC2WPNatFormPage.this.editorInput.getFC2WPDefBO().getFc2wpVersionList().get(resIndex);
    FC2WPDefBO fc2wpDefBO = new FC2WPDefBO(fc2wpVer);
    FC2WPNatFormPage.this.selFc2wpDefBo = fc2wpDefBO;
    FC2WPNatFormPage.this.selFc2wpMapDetail = FC2WPNatFormPage.this.fc2wpMappingDetailsList.get(resIndex);
    FC2WPNatFormPage.this.selFc2wpDef =
        FC2WPNatFormPage.this.editorInput.getFC2WPDefBO().getFc2wpDefMap().get(fc2wpVer);

    if (fc2wpDefBO.isModifiable()) {

      FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().setEnabled(true);
      FC2WPNatFormPage.this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(true);
    }
    else {
      FC2WPNatFormPage.this.toolBarActionSet.getEditFC2WPAction().setEnabled(false);
      FC2WPNatFormPage.this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(false);
    }
  }

  /**
   * @return the toolBarFilterCurrStateMap
   */
  public Map<String, Boolean> getToolBarFilterCurrStateMap() {
    return this.toolBarFilterCurrStateMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput().getFc2wpEditorDataHandler();
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (this.editor.getSelectedPage().getClass().equals(this.getClass())) {
      if (this.natTable != null) {
        this.compareRowObjects.clear();
        this.funcNames.clear();
        this.fc2wpMappinResultList.clear();
        this.fc2wpMappingDetailsList.clear();
        this.fc2wpMappingDetailsList.addAll(this.editorInput.getFC2WPDefBO().getFC2WPMappingWithDetailsList());
        if (this.fc2wpMappingDetailsList.size() > 1) {
          this.isCompareEditor = true;
        }
        getFC2WPMappingData(this.editorInput.getFc2wpVersion().getId());
        createNatTableColumns();
        this.fc2wpFilterGridLayer.getEventList().clear();
        this.fc2wpFilterGridLayer.getEventList().addAll(this.compareRowObjects);
        this.natTable.refresh();
        if (this.editor.getActivePage() == 0) {
          setStatusBarMessage(false);
        }
      }
    }
    // if node access is granted then add button should be enabled
    else {
      this.toolBarActionSet.getAddFC2WPAction().setEnabled(this.editorInput.getFC2WPDefBO().isModifiable());
      if (null != getCurrentSelection()) {
        this.toolBarActionSet.getEditFC2WPAction().setEnabled(this.editorInput.getFC2WPDefBO().isModifiable());
        this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(this.editorInput.getFC2WPDefBO().isModifiable());
      }
      else {
        this.toolBarActionSet.getEditFC2WPAction().setEnabled(false);
        this.toolBarActionSet.getDeleteFC2WPAction().setEnabled(false);
      }

    }
  }

  /**
   * @return the editorInput
   */
  @Override
  public FC2WPEditorInput getEditorInput() {
    return this.editorInput;
  }

}
