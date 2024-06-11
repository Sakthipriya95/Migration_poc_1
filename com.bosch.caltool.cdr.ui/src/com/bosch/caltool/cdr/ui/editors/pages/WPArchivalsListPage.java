/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.WPArchivalsDownloadAction;
import com.bosch.caltool.cdr.ui.actions.WPArchivalsPageToolBarActionSet;
import com.bosch.caltool.cdr.ui.editors.WPArchivalListEditor;
import com.bosch.caltool.cdr.ui.editors.WPArchivalListEditorInput;
import com.bosch.caltool.cdr.ui.editors.WPArchivalsEditorConstants;
import com.bosch.caltool.cdr.ui.editors.natcolumnfilter.WPArchivalsAllColFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.WPArchivalsInputToColumnConverter;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.WPArchivalsListNatColHeaderLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.WpArchivalEditConfiguration;
import com.bosch.caltool.cdr.ui.table.filters.WPArchivalToolBarFilters;
import com.bosch.caltool.cdr.ui.views.providers.WPArchivalsListNatToolTip;
import com.bosch.caltool.icdm.client.bo.cdr.WPArchivalsListEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomGlazedListsDataProvider;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * ICDM-784616 Editor to display WP Archivals
 *
 * @author ukt1cob
 */
public class WPArchivalsListPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  private static final int STATIC_COL_INDEX = 7;

  /**
   * non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * main composite
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
   * filter text
   */
  private Text filterTxt;
  /**
   * all column matcher
   */
  private WPArchivalsAllColFilterMatcher<WpArchival> allColumnFilterMatcher;
  /**
   * intger for total table row count
   */
  private int totTableRowCount;
  /**
   * custom comparator label
   */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  /**
   * column index to label map
   */
  private HashMap<Integer, String> propertyToLabelMap;
  /**
   * wp archival filter grid layer
   */
  private CustomFilterGridLayer<WpArchival> wpArchivalFilterGridLayer;
  /**
   * NAT table instance
   */
  private CustomNATTable natTable;

  /**
   * selection provider
   */
  private RowSelectionProvider<WpArchival> selectionProvider;
  /**
   * input set
   */
  private final TreeSet<WpArchival> wpArchivalsNatInputs = new TreeSet<>();
  /**
   * COL HEADER label accumulator
   */
  private WPArchivalsListNatColHeaderLabelAccumulator columnLabelAccumulator;
  private GroupByHeaderLayer groupByHeaderLayer;
  private boolean resetState;
  private ToolBarManager toolBarManager;
  /**
   * toolbar filters
   */
  private WPArchivalToolBarFilters toolBarFilters;


  /**
   * Constructor
   *
   * @param editor FormEditor
   * @param pageId String
   * @param title String
   */
  public WPArchivalsListPage(final FormEditor editor, final String pageId, final String title) {
    super(editor, pageId, title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WPArchivalListEditorInput getEditorInput() {
    return (WPArchivalListEditorInput) super.getEditorInput();
  }


  /**
   * @return the editor
   */
  @Override
  public WPArchivalListEditor getEditor() {
    return (WPArchivalListEditor) super.getEditor();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WPArchivalsListEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    TreeSet<WpArchival> wpArchivalNatInp = (TreeSet<WpArchival>) getDataHandler().getWPArchivalsNatInputs();
    this.wpArchivalFilterGridLayer.getEventList().clear();
    this.wpArchivalFilterGridLayer.getEventList().addAll(wpArchivalNatInp);
    this.wpArchivalsNatInputs.clear();
    this.wpArchivalsNatInputs.addAll(wpArchivalNatInp);
    this.totTableRowCount = this.wpArchivalsNatInputs.size();
    refreshNatTable();
    // set status bar message
    setStatusBarMessage(WPArchivalsListPage.this.groupByHeaderLayer, false);
  }

  /**
   * refresh the table
   */
  private void refreshNatTable() {
    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    // setting the layout of non scrolable form
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(getEditorInput().getName().replace("&", "&&"));

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
   * creating the content of the form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // initiate the form tool kit
    this.formToolkit = managedForm.getToolkit();
    createComposite();
  }

  /**
   * create composite
   */
  private void createComposite() {

    // create scroll composite in the right side
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    final Composite compositeTwo = new Composite(this.scrollComp, SWT.NONE);

    createRightSection(compositeTwo);
    // setting the layout of the composite
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
        Rectangle rect = WPArchivalsListPage.this.scrollComp.getClientArea();
        WPArchivalsListPage.this.scrollComp.setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * @param compositeTwo
   */
  private void createRightSection(final Composite compositeTwo) {
    this.section = this.formToolkit.createSection(compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Workpackage Archivals");
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    createForm(this.section);
    this.section.setClient(this.form);

  }

  /**
   * @param sectionRight
   */
  private void createForm(final Section sectionRight) {
    this.form = this.formToolkit.createForm(sectionRight);
    // setting layout for form
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.form.getBody().setLayout(new GridLayout());

    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    // create filter text
    addModifyListenerForFilterTxt();

    // create table
    createWPArchivalTabViewer();
  }


  /**
   * create the NAT table viewer
   */
  private void createWPArchivalTabViewer() {
    // set column names and width for static columns
    ConcurrentMap<Integer, Integer> columnWidthMap = setHeaderNameColWidthForStaticCols();
    this.wpArchivalsNatInputs.addAll(getDataHandler().getWPArchivalsNatInputs());
    // initialise the row count
    this.totTableRowCount = this.wpArchivalsNatInputs.size();
    // create nat table
    IConfigRegistry configRegistry = createNatTable(columnWidthMap);

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.toolBarFilters = new WPArchivalToolBarFilters();
    // toolbar filter matcher
    this.wpArchivalFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    // initailise all column filter
    this.allColumnFilterMatcher = new WPArchivalsAllColFilterMatcher<>();
    getWpArchivalFilterGridLayer().getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration());

    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());

    this.natTable.addConfiguration(new WpArchivalEditConfiguration(this.wpArchivalFilterGridLayer));

    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(WPArchivalsListPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTab, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      WPArchivalsListPage.this.resetState = true;
                      WPArchivalsListPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });


    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configReg) {
        super.configureRegistry(configReg);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configReg.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    this.natTable.configure();
    // Load the saved state of NAT table
    loadSaveStateOfNatTable();


    SelectionLayer selectionLayer = this.wpArchivalFilterGridLayer.getBodyLayer().getSelectionLayer();
    CustomGlazedListsDataProvider<WpArchival> bodyDataProvider = this.wpArchivalFilterGridLayer.getBodyDataProvider();

    RowSelectionModel<WpArchival> rowSelectionModel =
        new RowSelectionModel<>(selectionLayer, bodyDataProvider, WpArchival::getId);
    selectionLayer.setSelectionModel(rowSelectionModel);
    rowSelectionModel.setMultipleSelectionAllowed(false);

    selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

    this.selectionProvider = new RowSelectionProvider<>(selectionLayer, bodyDataProvider, false);
    // using lambda expresion instead of annonymous inner class
    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) WPArchivalsListPage.this.selectionProvider.getSelection();
      if (!selection.isEmpty()) {
        WPArchivalListEditorInput editorInput = getEditorInput();
        editorInput.setSelectedWPArchivals(selection);
        CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
        expReportService.setExportService(true);
      }
    });

    // attcah tool tip for nat table
    attachToolTip();
    createToolbarAction();

    // set the status bar message
    setStatusBarMessage(this.groupByHeaderLayer, false);
    getSite().setSelectionProvider(this.selectionProvider);
  }

  /**
   * Method to add download action
   */
  private void createToolbarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar secToolbar = this.toolBarManager.createControl(this.section);
    WPArchivalsPageToolBarActionSet toolBarActionSet =
        new WPArchivalsPageToolBarActionSet(this.wpArchivalFilterGridLayer, this);

    toolBarActionSet.showNotAvailableAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showInProgressAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showCompletedAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showFailedAction(this.toolBarManager, this.toolBarFilters);
    final Separator separator = new Separator();
    this.toolBarManager.add(separator);
    addResetAllFiltersAction();
    this.toolBarManager.add(separator);
    this.form.getToolBarManager().add(new WPArchivalsDownloadAction(getEditorInput()));

    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);

    this.toolBarManager.update(true);
    this.section.setTextClient(secToolbar);
  }

  /**
   * ICDM-2141 Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.wpArchivalFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * Load the saved state of NAT table
   */
  private void loadSaveStateOfNatTable() {
    loadState();
    CustomDefaultBodyLayerStack bodyLayer = this.wpArchivalFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.wpArchivalFilterGridLayer.getColumnHeaderLayer(),
            this.wpArchivalFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
    this.wpArchivalFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

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
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content
   */
  private void attachToolTip() {
    // new class creatd to show the tooltip
    WPArchivalsListNatToolTip toolTip = new WPArchivalsListNatToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * @param columnWidthMap
   * @param cdrReportData
   * @return
   */
  private IConfigRegistry createNatTable(final ConcurrentMap<Integer, Integer> columnWidthMap) {
    // instantiate the input to column converter
    WPArchivalsInputToColumnConverter natInputToColumnConverter =
        new WPArchivalsInputToColumnConverter(getDataHandler());
    IConfigRegistry configRegistry = new ConfigRegistry();
    GroupByModel groupByModel = new GroupByModel();
    this.wpArchivalFilterGridLayer = new CustomFilterGridLayer<>(configRegistry, this.wpArchivalsNatInputs,
        this.propertyToLabelMap, columnWidthMap, getWPArchivalComparator(1), natInputToColumnConverter, this, null,
        groupByModel, null, false, true, null, null, false);

    // Col header configuration
    this.columnLabelAccumulator = new WPArchivalsListNatColHeaderLabelAccumulator(
        this.wpArchivalFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.wpArchivalFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(this.columnLabelAccumulator);

    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.wpArchivalFilterGridLayer,
        this.wpArchivalFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(null, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.wpArchivalFilterGridLayer, 0, 1);

    this.wpArchivalFilterGridLayer.getBodyDataLayer().getTreeRowModel().registerRowGroupModelListener(
        () -> WPArchivalsListPage.this.setStatusBarMessage(WPArchivalsListPage.this.groupByHeaderLayer, false));
    // creating the NAT table
    this.natTable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        compositeGridLayer, false, getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    return configRegistry;
  }

  /**
   * @return ConcurrentMap<Integer, Integer>
   */
  private ConcurrentMap<Integer, Integer> setHeaderNameColWidthForStaticCols() {
    // storing the header names
    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_WP_ARCHIVAL_DATE, "Archival Date");
    // ICDM-2177
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_BASELINE_NAME, "Baseline Name");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_PIDC_VERSION_NAME, "PIDC Version");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_A2L_FILE_NAME, "A2L File");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_WP_DEF_VERS_NAME, "WP Definition Version");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_VARIANT_NAME, "Variant");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_WP_NAME, "Workpackage");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_RESP_NAME, "Responsible");
    this.propertyToLabelMap.put(WPArchivalsEditorConstants.COLNDX_STATUS, "Status");


    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_WP_ARCHIVAL_DATE,
        WPArchivalsEditorConstants.COLWDH_WP_ARCHIVAL_DATE);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_BASELINE_NAME,
        WPArchivalsEditorConstants.COLWDH_BASELINE_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_PIDC_VERSION_NAME,
        WPArchivalsEditorConstants.COLWDH_PIDC_VERSION_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_A2L_FILE_NAME,
        WPArchivalsEditorConstants.COLWDH_A2L_FILE_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_WP_DEF_VERS_NAME,
        WPArchivalsEditorConstants.COLWDH_WP_DEF_VERS_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_VARIANT_NAME, WPArchivalsEditorConstants.COLWDH_VARIANT_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_WP_NAME, WPArchivalsEditorConstants.COLWDH_WP_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_RESP_NAME, WPArchivalsEditorConstants.COLWDH_RESP_NAME);
    columnWidthMap.put(WPArchivalsEditorConstants.COLNDX_STATUS, WPArchivalsEditorConstants.COLWDH_STATUS);

    return columnWidthMap;
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(event -> {
      String text = WPArchivalsListPage.this.filterTxt.getText().trim();
      WPArchivalsListPage.this.allColumnFilterMatcher.setFilterText(text, true);
      WPArchivalsListPage.this.getWpArchivalFilterGridLayer().getFilterStrategy().applyFilterInAllColumns(text);
      WPArchivalsListPage.this.getWpArchivalFilterGridLayer().getSortableColumnHeaderLayer()
          .fireLayerEvent(new FilterAppliedEvent(
              WPArchivalsListPage.this.getWpArchivalFilterGridLayer().getSortableColumnHeaderLayer()));
      // set status bar message
      setStatusBarMessage(WPArchivalsListPage.this.groupByHeaderLayer, false);

    });
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
   * @return IConfiguration
   */
  private IConfiguration getCustomComparatorConfiguration() {


    return new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        for (int col_index = 0; col_index < (STATIC_COL_INDEX); col_index++) {
          WPArchivalsListPage.this.columnLabelAccumulator.registerColumnOverrides(col_index,
              CUSTOM_COMPARATOR_LABEL + col_index);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
              getWPArchivalComparator(col_index), NORMAL, CUSTOM_COMPARATOR_LABEL + col_index);
        }


      }
    };

  }

  /**
   * get the comparator for the table
   *
   * @return Comparator<WpArchival>
   */
  private static Comparator<WpArchival> getWPArchivalComparator(final int columnNum) {

    return new Comparator<WpArchival>() {

      /**
       * @param wpArchival1 wpArchival
       * @param wpArchival2 wpArchival
       * @return int compare result
       */
      @Override
      public int compare(final WpArchival wpArchival1, final WpArchival wpArchival2) {
        int ret = 0;
        switch (columnNum) {
          case WPArchivalsEditorConstants.COLNDX_WP_ARCHIVAL_DATE:
            // compare created dates
            ret = ApicUtil.compare(wpArchival1.getCreatedDate(), wpArchival2.getCreatedDate());
            break;
          case WPArchivalsEditorConstants.COLNDX_BASELINE_NAME:
            // compare baseline name
            ret = ApicUtil.compare(wpArchival1.getBaselineName(), wpArchival2.getBaselineName());
            break;
          case WPArchivalsEditorConstants.COLNDX_PIDC_VERSION_NAME:
            // compare pidc version name
            ret = ApicUtil.compare(wpArchival1.getPidcVersFullname(), wpArchival2.getPidcVersFullname());
            break;
          case WPArchivalsEditorConstants.COLNDX_A2L_FILE_NAME:
            // compare a2l File name
            ret = ApicUtil.compare(wpArchival1.getA2lFilename(), wpArchival2.getA2lFilename());
            break;
          case WPArchivalsEditorConstants.COLNDX_WP_DEF_VERS_NAME:
            // compare wp defn version names
            ret = ApicUtil.compare(wpArchival1.getWpDefnVersName(), wpArchival2.getWpDefnVersName());
            break;
          case WPArchivalsEditorConstants.COLNDX_VARIANT_NAME:
            // compare variant names
            ret = comparePidcVariants(wpArchival1, wpArchival2);
            break;
          case WPArchivalsEditorConstants.COLNDX_WP_NAME:
            // compare WP names
            ret = ApicUtil.compare(wpArchival1.getWpName(), wpArchival2.getWpName());
            break;
          case WPArchivalsEditorConstants.COLNDX_RESP_NAME:
            // compare Resp names
            ret = ApicUtil.compare(wpArchival1.getRespName(), wpArchival2.getRespName());
            break;
          default:
            ret = 0;
            break;
        }
        return ret;
      }


      /**
       * @param wpArchival1
       * @param cdrRevVar2
       * @return
       */
      private int comparePidcVariants(final WpArchival wpArchival1, final WpArchival wpArchival2) {
        String val1 = null == wpArchival1.getVariantName() ? "" : wpArchival1.getVariantName();
        String val2 = null == wpArchival2.getVariantName() ? "" : wpArchival2.getVariantName();
        return ApicUtil.compare(val1, val2);
      }

    };
  }

  /**
   * column property accessor
   *
   * @author ukt1cob
   * @param <T>
   */
  class CustomWpArchivalColumnPropertyAccessor<T> implements IColumnAccessor<T> {


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
      // Not applicable
    }

    @Override
    public int getColumnCount() {
      return WPArchivalsListPage.this.propertyToLabelMap.size();
    }


  }

  /**
   * Column header data provider class
   *
   * @author ukt1cob
   */
  class CustomWpArchivalColumnHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String column header label
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = WPArchivalsListPage.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    @Override
    public int getColumnCount() {
      return WPArchivalsListPage.this.propertyToLabelMap.size();
    }

    @Override
    public int getRowCount() {
      // first row is the header
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
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * @return the wpArchivalFilterGridLayer
   */
  public CustomFilterGridLayer<WpArchival> getWpArchivalFilterGridLayer() {
    return this.wpArchivalFilterGridLayer;
  }

  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);
      for (int index = 0; index < STATIC_COL_INDEX; index++) {

        // code to use the regular expression matching
        configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
            TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + index);

      }
    }
  }

  /**
   * input for status line
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    getEditor().updateStatusBar(outlineSelection, this.totTableRowCount,
        this.wpArchivalFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateStatusBar(final boolean outlineSelection) {
    super.updateStatusBar(outlineSelection);
    setStatusBarMessage(this.groupByHeaderLayer, outlineSelection);
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
      CDMLogger.getInstance().warn("Failed to load Workpackage Archivals nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
  *
  */
  public void reconstructNatTable() {


    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.wpArchivalFilterGridLayer = null;

    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }

    if (this.nonScrollableForm.getToolBarManager() != null) {
      this.nonScrollableForm.getToolBarManager().removeAll();
    }
    createWPArchivalTabViewer();
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
      CDMLogger.getInstance().warn("Failed to save Workpackage Archivals nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (getEditor().getActivePage() == 0)) {
      setStatusBarMessage(this.groupByHeaderLayer, false);
    }
  }


  /**
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }
}
