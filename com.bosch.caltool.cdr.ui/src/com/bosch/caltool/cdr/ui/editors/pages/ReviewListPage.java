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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
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

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.PIDCTreeViewActionSet;
import com.bosch.caltool.cdr.ui.actions.ReviewListPageNatMouseClickAction;
import com.bosch.caltool.cdr.ui.actions.ReviewListPageToolBarActionSet;
import com.bosch.caltool.cdr.ui.editors.PIDCRvwResultsEditorConstants;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditor;
import com.bosch.caltool.cdr.ui.editors.ReviewListEditorInput;
import com.bosch.caltool.cdr.ui.editors.natcolumnfilter.PIDCRvwResAllColFilterMatcher;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.PIDCRvwResEditConfiguration;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.PIDCRvwResInputToColumnConverter;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.PIDCRvwResLabelAccumulator;
import com.bosch.caltool.cdr.ui.editors.pages.natsupport.ReviewListNatColHeaderLabelAccumulator;
import com.bosch.caltool.cdr.ui.table.filters.PIDCRwResToolBarFilters;
import com.bosch.caltool.cdr.ui.views.providers.ReviewListNatToolTip;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewListEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * ICDM-1764 Editor page to display review results
 *
 * @author mkl2cob
 */
public class ReviewListPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  // ICDM-2177
  private static final int STATIC_COL_INDEX = 15;

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
  private PIDCRvwResAllColFilterMatcher allColumnFilterMatcher;
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
   * data report filter grid layer
   */
  private CustomFilterGridLayer pidcRvwFilterGridLayer;
  /**
   * NAT table instance
   */
  private CustomNATTable natTable;

  /**
   * selection provider
   */
  private RowSelectionProvider<ReviewVariantModel> selectionProvider;
  /**
   * toolbar filters
   */
  private PIDCRwResToolBarFilters toolBarFilters;
  /**
   * input set
   */
  private final TreeSet<ReviewVariantModel> pidcRvwsNatInputs = new TreeSet<>();
  /**
   * COL HEADER label accumulator
   */
  private ReviewListNatColHeaderLabelAccumulator columnLabelAccumulator;
  private GroupByHeaderLayer groupByHeaderLayer;
  private boolean resetState;
  private ToolBarManager toolBarManager;


  /**
   * Constructor
   *
   * @param editor FormEditor
   * @param pageId String
   * @param title String
   */
  public ReviewListPage(final FormEditor editor, final String pageId, final String title) {
    super(editor, pageId, title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewListEditorInput getEditorInput() {
    return (ReviewListEditorInput) super.getEditorInput();
  }


  /**
   * @return the editor
   */
  @Override
  public ReviewListEditor getEditor() {
    return (ReviewListEditor) super.getEditor();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewListEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    TreeSet<ReviewVariantModel> pidcRvwsNatInp = (TreeSet<ReviewVariantModel>) getDataHandler().getPidcRvwsNatInputs();
    this.pidcRvwFilterGridLayer.getEventList().clear();
    this.pidcRvwFilterGridLayer.getEventList().addAll(pidcRvwsNatInp);
    this.pidcRvwsNatInputs.clear();
    this.pidcRvwsNatInputs.addAll(pidcRvwsNatInp);
    this.totTableRowCount = this.pidcRvwsNatInputs.size();
    refreshNatTable();
    // set status bar message
    setStatusBarMessage(ReviewListPage.this.groupByHeaderLayer, false);
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
        Rectangle rect = ReviewListPage.this.scrollComp.getClientArea();
        ReviewListPage.this.scrollComp.setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * Build contect menu on tree
   */
  // ICDM-2081
  private void addRightClickMenu() {
    // Create the menu manager and fill context menu
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(manager -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ReviewListPage.this.selectionProvider.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((CommonUtils.isNotNull(firstElement)) && (!selection.isEmpty())) {
        fillContextMenu(manager, selection);
      }
    });

    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager, final IStructuredSelection selection) {
    // Get the current selection and add actions to show the context menu
    if ((selection != null) && (selection.getFirstElement() != null)) {
      PIDCTreeViewActionSet actionSet = new PIDCTreeViewActionSet();
      actionSet.setConMenuForResult(manager, null, selection);
    }
  }

  /**
   * @param compositeTwo
   */
  private void createRightSection(final Composite compositeTwo) {
    this.section = this.formToolkit.createSection(compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Review Results");
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
    createPIDCRvwTabViewer();
  }

  /**
   * create the NAT table viewer
   */
  private void createPIDCRvwTabViewer() {
    // set column names and width for static columns
    ConcurrentMap<Integer, Integer> columnWidthMap = setHeaderNameColWidthForStaticCols();
    this.pidcRvwsNatInputs.addAll(getDataHandler().getPidcRvwsNatInputs());
    // initialise the row count
    this.totTableRowCount = this.pidcRvwsNatInputs.size();
    // initialising the filter
    this.toolBarFilters = new PIDCRwResToolBarFilters(this.pidcRvwsNatInputs);
    // create nat table
    IConfigRegistry configRegistry = createNatTable(columnWidthMap, this.pidcRvwsNatInputs);

    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());

    // initailise all column filter
    this.allColumnFilterMatcher = new PIDCRvwResAllColFilterMatcher<>();
    getDataRprtFilterGridLayer().getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    // toolbar filter matcher
    this.pidcRvwFilterGridLayer.getFilterStrategy().setToolBarFilterMatcher(this.toolBarFilters.getToolBarMatcher());

    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addConfiguration(getCustomComparatorConfiguration());

    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    // add the edit configuration which will give images for type,status
    this.natTable.addConfiguration(new PIDCRvwResEditConfiguration(this.pidcRvwFilterGridLayer));

    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(ReviewListPage.this.natTable).withColumnChooserMenuItem()
                .withMenuItemProvider((natTab, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      ReviewListPage.this.resetState = true;
                      ReviewListPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });


    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        super.configureRegistry(configRegistry);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });
    this.natTable.configure();
    // Load the saved state of NAT table
    loadSaveStateOfNatTable();


    this.selectionProvider = new RowSelectionProvider<>(this.pidcRvwFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.pidcRvwFilterGridLayer.getBodyDataProvider(), false);

    // using lambda expresion instead of annonymous inner class
    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ReviewListPage.this.selectionProvider.getSelection();
      if (!selection.isEmpty()) {
        ReviewListEditorInput editorInput = getEditorInput();
        editorInput.setSelectedResults(selection);
        CommandState expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
        expReportService.setExportService(true);
      }
    });

    // attcah tool tip for nat table
    attachToolTip();
    // create tool bar filters
    createToolbarFilters();

    // ICDM-2081
    addRightClickMenu();

    // set the status bar message
    setStatusBarMessage(this.groupByHeaderLayer, false);
    getSite().setSelectionProvider(this.selectionProvider);


  }

  /**
   * Load the saved state of NAT table
   */
  private void loadSaveStateOfNatTable() {
    loadState();
    CustomDefaultBodyLayerStack bodyLayer = this.pidcRvwFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.pidcRvwFilterGridLayer.getColumnHeaderLayer(), this.pidcRvwFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
    this.pidcRvwFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

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
   * create toolbar and filters
   */
  private void createToolbarFilters() {

    this.toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = this.toolBarManager.createControl(this.section);
    ReviewListPageToolBarActionSet toolBarActionSet =
        new ReviewListPageToolBarActionSet(this.pidcRvwFilterGridLayer, this);

    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    // review type filters
    toolBarActionSet.showNormalTypeAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showLinkedTypeAction(this.toolBarManager, this.toolBarFilters);
    final Separator separator = new Separator();
    this.toolBarManager.add(separator);
    toolBarActionSet.showOfficialTypeAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showStartTypeAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showTestTypeAction(this.toolBarManager, this.toolBarFilters);


    this.toolBarManager.add(separator);
    // review status filter
    toolBarActionSet.showOpenAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showInProgressAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showClosedAction(this.toolBarManager, this.toolBarFilters);

    // ICDM-2078
    this.toolBarManager.add(separator);
    // review lock status filter
    toolBarActionSet.showLockedAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showUnlockedAction(this.toolBarManager, this.toolBarFilters);

    this.toolBarManager.add(separator);
    // review scope filter
    toolBarActionSet.showWPAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showGrpAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showFunAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showLabAction(this.toolBarManager, this.toolBarFilters);
    // ICDM-2138
    toolBarActionSet.showMonicaAction(this.toolBarManager, this.toolBarFilters);

    toolBarActionSet.showRFAction(this.toolBarManager, this.toolBarFilters);
    toolBarActionSet.showNotDefAction(this.toolBarManager, this.toolBarFilters);

    // Task 237156
    toolBarActionSet.showCompliAction(this.toolBarManager, this.toolBarFilters);


    this.toolBarManager.update(true);
    // ICDM-2141
    addResetAllFiltersAction();
    this.section.setTextClient(toolbar);

  }


  /**
   * Enables tootltip only for cells which contain not fully visible content
   */
  private void attachToolTip() {
    // new class creatd to show the tooltip
    ReviewListNatToolTip toolTip = new ReviewListNatToolTip(this.natTable, new String[0], this);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));

  }

  /**
   * @param columnWidthMap
   * @param cdrReportData
   * @param dataRvwRprtNatInputs
   * @return
   */
  private IConfigRegistry createNatTable(final ConcurrentMap<Integer, Integer> columnWidthMap,
      final TreeSet<ReviewVariantModel> dataRvwRprtNatInputs) {
    // instantiate the input to column converter
    PIDCRvwResInputToColumnConverter natInputToColumnConverter = new PIDCRvwResInputToColumnConverter(getDataHandler());
    IConfigRegistry configRegistry = new ConfigRegistry();
    GroupByModel groupByModel = new GroupByModel();
    this.pidcRvwFilterGridLayer = new CustomFilterGridLayer(configRegistry, dataRvwRprtNatInputs,
        this.propertyToLabelMap, columnWidthMap, getCDRResultComparator(1), natInputToColumnConverter, this,
        new ReviewListPageNatMouseClickAction(this), groupByModel, null, false, true, null, null, false);

    // add the label accumulator
    DataLayer bodyDataLayer = this.pidcRvwFilterGridLayer.getBodyDataLayer();
    final PIDCRvwResLabelAccumulator rvwResLabelAccumulator = new PIDCRvwResLabelAccumulator(bodyDataLayer);
    bodyDataLayer.setConfigLabelAccumulator(rvwResLabelAccumulator);
    // Col header configuration
    this.columnLabelAccumulator =
        new ReviewListNatColHeaderLabelAccumulator(this.pidcRvwFilterGridLayer.getColumnHeaderDataLayer(), this);
    this.pidcRvwFilterGridLayer.getColumnHeaderDataLayer().setConfigLabelAccumulator(this.columnLabelAccumulator);

    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.pidcRvwFilterGridLayer,
        this.pidcRvwFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.pidcRvwFilterGridLayer, 0, 1);

    this.pidcRvwFilterGridLayer.getBodyDataLayer().getTreeRowModel().registerRowGroupModelListener(
        () -> ReviewListPage.this.setStatusBarMessage(ReviewListPage.this.groupByHeaderLayer, false));
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

    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));
    return configRegistry;
  }

  /**
   * @return ConcurrentMap<Integer, Integer>
   */
  private ConcurrentMap<Integer, Integer> setHeaderNameColWidthForStaticCols() {
    // storing the header names
    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_ICON, " ");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DATE, "Review Date");
    // ICDM-2177
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_PVER_NAME, "PVER Name");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_A2L_VERSION, "A2L Version");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_PIDC_VARIANT, "PIDC Variant");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DESC, "Description");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_TYPE, "Review Type");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_STATUS, "Review Status");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_LOCK_STATUS, "Is Locked?");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_SCOPE, "Review Scope");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_SCOPE_NAME, "Scope Name");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_CAL_ENGINEER, "Calibration Engineer");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_AUDITOR, "Auditor");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_PARENT_REVIEW, "Parent Review");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_RULESET_NAME, "Rule Set Name");
    this.propertyToLabelMap.put(PIDCRvwResultsEditorConstants.COLNDX_BASE_REVIEW, "Base Review");


    // The below map is used by NatTable to Map Columns with their respective widths
    // Width is based on pixels
    ConcurrentMap<Integer, Integer> columnWidthMap = new ConcurrentHashMap<>();
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_ICON,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_ICON);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DATE,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_DATE);
    // ICDM-2177
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_PVER_NAME, PIDCRvwResultsEditorConstants.COLWDH_PVER_NAME);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_A2L_VERSION,
        PIDCRvwResultsEditorConstants.COLWDH_A2L_VERSION);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_PIDC_VARIANT,
        PIDCRvwResultsEditorConstants.COLWDH_PIDC_VARIANT);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DESC,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_DESC);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_TYPE,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_TYPE);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_STATUS,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_STATUS);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_LOCK_STATUS,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_LOCK_STATUS);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_REVIEW_SCOPE,
        PIDCRvwResultsEditorConstants.COLWDH_REVIEW_SCOPE);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_SCOPE_NAME,
        PIDCRvwResultsEditorConstants.COLWDH_SCOPE_NAME);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_CAL_ENGINEER,
        PIDCRvwResultsEditorConstants.COLWDH_CAL_ENGINEER);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_AUDITOR, PIDCRvwResultsEditorConstants.COLWDH_AUDITOR);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_PARENT_REVIEW,
        PIDCRvwResultsEditorConstants.COLWDH_PARENT_REVIEW);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_RULESET_NAME,
        PIDCRvwResultsEditorConstants.COLWDH_RULESET_NAME);
    columnWidthMap.put(PIDCRvwResultsEditorConstants.COLNDX_BASE_REVIEW,
        PIDCRvwResultsEditorConstants.COLWDH_BASE_REVIEW);
    return columnWidthMap;
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(event -> {
      String text = ReviewListPage.this.filterTxt.getText().trim();
      ReviewListPage.this.allColumnFilterMatcher.setFilterText(text, true);
      ReviewListPage.this.getDataRprtFilterGridLayer().getFilterStrategy().applyFilterInAllColumns(text);
      ReviewListPage.this.getDataRprtFilterGridLayer().getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(ReviewListPage.this.getDataRprtFilterGridLayer().getSortableColumnHeaderLayer()));
      // set status bar message
      setStatusBarMessage(ReviewListPage.this.groupByHeaderLayer, false);

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
          ReviewListPage.this.columnLabelAccumulator.registerColumnOverrides(col_index,
              CUSTOM_COMPARATOR_LABEL + col_index);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
              getCDRResultComparator(col_index), NORMAL, CUSTOM_COMPARATOR_LABEL + col_index);
        }


      }
    };

  }

  /**
   * get the comparator for the table
   *
   * @return Comparator<FocusMatrixAttribute>
   */
  private static Comparator<ReviewVariantModel> getCDRResultComparator(final int columnNum) {

    return new Comparator<ReviewVariantModel>() {

      /**
       * @param cdrRevVar1 A2LParameter
       * @param cdrRevVar2 A2LParameter
       * @return int compare result
       */
      @Override
      public int compare(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        int ret = 0;
        switch (columnNum) {
          case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DATE:
            // compare created dates
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getCreatedDate(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getCreatedDate());
            break;
          // ICDM-2177
          case PIDCRvwResultsEditorConstants.COLNDX_PVER_NAME:
            // compare pver name
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getPverName(),
                cdrRevVar2.getReviewResultData().getPverName());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_A2L_VERSION:
            // compare a2l versions
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getSdomPverVarName(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getSdomPverVarName());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_PIDC_VARIANT:
            // compare variants
            ret = comparePidcVariants(cdrRevVar1, cdrRevVar2);
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DESC:
            // compare descriptions
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getDescription(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getDescription());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_TYPE:
            // compare review type string
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getReviewType(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getReviewType());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_STATUS:
            // compare review status
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getRvwStatus(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getRvwStatus());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_LOCK_STATUS:
            // compare is locked
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getLockStatus(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getLockStatus());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_SCOPE:
            // compare source types
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getCdrReviewResult().getSourceType(),
                cdrRevVar2.getReviewResultData().getCdrReviewResult().getSourceType());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_SCOPE_NAME:
            // compare source names
            ret = caseScopeName(cdrRevVar1, cdrRevVar2);
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_CAL_ENGINEER:
            // compare calibration engineers
            ret = caseCalEngg(cdrRevVar1, cdrRevVar2);
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_AUDITOR:
            ret = ApicUtil.compare(cdrRevVar1.getReviewResultData().getAuditor(),
                cdrRevVar2.getReviewResultData().getAuditor());
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_PARENT_REVIEW:
            // compate parent reviews
            ret = caseParentRev(cdrRevVar1, cdrRevVar2);
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_RULESET_NAME:
            // compare rule set names
            ret = compareRuleSetNames(cdrRevVar1, cdrRevVar2);
            break;
          case PIDCRvwResultsEditorConstants.COLNDX_BASE_REVIEW:
            // compare base reviews
            ret = caseBaseRev(cdrRevVar1, cdrRevVar2);
            break;
          default:
            ret = 0;
        }
        return ret;
      }

      /**
       * @param cdrRevVar1
       * @param cdrRevVar2
       * @return
       */
      private int compareRuleSetNames(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        String val1 = null == cdrRevVar1.getReviewResultData().getRuleSetName() ? ""
            : cdrRevVar1.getReviewResultData().getRuleSetName();
        String val2 = null == cdrRevVar2.getReviewResultData().getRuleSetName() ? ""
            : cdrRevVar2.getReviewResultData().getRuleSetName();
        return ApicUtil.compare(val1, val2);
      }

      /**
       * @param cdrRevVar1
       * @param cdrRevVar2
       * @return
       */
      private int comparePidcVariants(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        String val1 =
            null == cdrRevVar1.getRvwVariant().getVariantName() ? "" : cdrRevVar1.getRvwVariant().getVariantName();
        String val2 =
            null == cdrRevVar2.getRvwVariant().getVariantName() ? "" : cdrRevVar2.getRvwVariant().getVariantName();
        return ApicUtil.compare(val1, val2);
      }

      /**
       * @param cdrRevVar1
       * @param cdrRevVar2
       * @param val1
       * @param val2
       * @return
       */
      private int caseBaseRev(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        return ApicUtil.compare(cdrRevVar1.getReviewResultData().getBaseReview(),
            cdrRevVar2.getReviewResultData().getBaseReview());
      }

      /**
       * @param cdrRevVar1
       * @param cdrRevVar2
       * @return
       */
      private int caseParentRev(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        int ret;
        String val1;
        String val2;
        val1 = null == cdrRevVar1.getReviewResultData().getParentReview() ? ""
            : cdrRevVar1.getReviewResultData().getParentReview();
        val2 = null == cdrRevVar2.getReviewResultData().getParentReview() ? ""
            : cdrRevVar2.getReviewResultData().getParentReview();
        ret = ApicUtil.compare(val1, val2);
        return ret;
      }

      /**
       * @param cdrRevVar1
       * @param cdrRevVar2
       * @return
       * @throws ApicWebServiceException
       */
      private int caseCalEngg(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        return ApicUtil.compare(cdrRevVar1.getReviewResultData().getCalEngineer(),
            cdrRevVar2.getReviewResultData().getCalEngineer());
      }

      /**
       * @param cdrRevVar1
       * @param cdrRevVar2
       * @return
       */
      private int caseScopeName(final ReviewVariantModel cdrRevVar1, final ReviewVariantModel cdrRevVar2) {
        return ApicUtil.compare(cdrRevVar1.getReviewResultData().getScopeName(),
            cdrRevVar2.getReviewResultData().getScopeName());
      }

    };
  }

  /**
   * column property accessor
   *
   * @author mkl2cob
   * @param <T>
   */
  class CustomFocusMatrixColumnPropertyAccessor<T> implements IColumnAccessor<T> {


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
      return ReviewListPage.this.propertyToLabelMap.size();
    }


  }

  /**
   * Column header data provider class
   *
   * @author mkl2cob
   */
  class CustomFocusMatrixColumnHeaderDataProvider implements IDataProvider {


    /**
     * @param columnIndex int
     * @return String column header label
     */
    public String getColumnHeaderLabel(final int columnIndex) {
      String string = ReviewListPage.this.propertyToLabelMap.get(columnIndex);

      return string == null ? "" : string;
    }

    @Override
    public int getColumnCount() {
      return ReviewListPage.this.propertyToLabelMap.size();
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
   * @return the dataRprtFilterGridLayer
   */
  public CustomFilterGridLayer getDataRprtFilterGridLayer() {
    return this.pidcRvwFilterGridLayer;
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
        this.pidcRvwFilterGridLayer.getRowHeaderLayer().getPreferredRowCount());
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
      CDMLogger.getInstance().warn("Failed to load A2L Parameters nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
  *
  */
  public void reconstructNatTable() {


    this.natTable.dispose();
    this.propertyToLabelMap.clear();

    this.pidcRvwFilterGridLayer = null;

    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }

    if (this.nonScrollableForm.getToolBarManager() != null) {
      this.nonScrollableForm.getToolBarManager().removeAll();
    }
    createPIDCRvwTabViewer();
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
      CDMLogger.getInstance().warn("Failed to save A2L Parameters nat table state", ioe, Activator.PLUGIN_ID);
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
   * ICDM-2141 Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.pidcRvwFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * @return CustomGroupByHeaderLayer
   */
  public GroupByHeaderLayer getGroupByHeaderLayer() {
    return this.groupByHeaderLayer;
  }
}
