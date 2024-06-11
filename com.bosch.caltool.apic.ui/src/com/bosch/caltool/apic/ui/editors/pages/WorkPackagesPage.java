/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.config.RenderErrorHandling;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellEditorMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCWorkPkgActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.WorkPackageNatInputToColConverter;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.WorkPackagesNatValidator;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.WorkPkgEditConfiguration;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.WorkPkgsLabelAccumulator;
import com.bosch.caltool.apic.ui.table.filters.WorkPackgColmnFilterMatcher;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.natsupport.CustomSortFilterGroupEnabler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author elm1cob
 */
public class WorkPackagesPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  /**
   *
   */
  private static final String CUSTOMER_COL_NAME = "Name at Customer";
  /**
   *
   */
  private static final String DESCRIPTION_COL_NAME = "Description";
  /**
   *
   */
  private static final String WORK_PACKAGE_COL_NAME = "Work Package";
  /**
   * Section Title
   */
  private static final String TITLE = "A2L Work Packages";
  /**
   * The Composite
   */
  private Composite composite;
  /**
   * The Section
   */
  private Section section;
  /**
   * The Base form
   */
  private Form form;

  private Text filterTxt;

  private final PIDCEditor pidcEditor;

  private final PIDCEditorInput pidcEditorInput;

  private CustomNATTable workPkgsNattable;

  /** The custom filter grid layer. */
  private CustomFilterGridLayer customFilterGridLayer;

  private WorkPackgColmnFilterMatcher workPkgColmnFilterMatcher;

  /** Non scrollable form. */
  private Form nonScrollableForm;

  private RowSelectionProvider<A2lWorkPackage> selectionProvider;

  private ToolBarManager toolbarManager;

  private Object selectedRowObj;

  private PIDCWorkPkgActionSet wrkPkgActionSet;

  private Map<Integer, String> propertyToLabelMap;

  private Map<Integer, Integer> columnWidthMap;

  private IConfigRegistry configRegistry;

  private final TextCellEditor cellEditor = new TextCellEditor();

  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  private final CurrentUserBO currUser = new CurrentUserBO();


  /**
   * the cell editor mouse event matcher for the ui binding when double clicking on a cell
   */
  private final CellEditorMouseEventMatcher cellEditorMouseEventMatcher = new CellEditorMouseEventMatcher();

  /**
   * totTableRowCount contains the Total number of rows set to NatTable Viewer.Used to update the StatusBar Message
   */
  private int totTableRowCount;

  private boolean resetState;

  private GroupByHeaderLayer groupByHeaderLayer;

  private boolean isRefreshNeeded;

  private Object selectedWorkpackage;

  /**
   * @param editor FormEditor
   * @param editorInput PIDCEditorInput
   */
  public WorkPackagesPage(final FormEditor editor, final PIDCEditorInput editorInput) {
    super(editor, "Work Packages", TITLE);
    this.pidcEditor = (PIDCEditor) editor;
    this.pidcEditorInput = editorInput;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.pidcEditor.getToolkit().createForm(parent);
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
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
    /** The form toolkit. */
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addPostSelectionListener(this);
  }


  /**
   * Creates the composite.
   *
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());

    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }

  /**
   * Creates the section.
   *
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText(TITLE);
    this.section.setExpanded(true);

    this.section.setDescription("List of all Work packages defined for the PIDC Version");
    createForm(toolkit);
    this.section.setLayoutData(gridData);

    this.section.setClient(this.form);
  }

  /**
   * Creates the form.
   *
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    createToolBarAction();
    this.filterTxt = toolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    this.form.getBody().setLayout(new GridLayout());
    createNatTable();
  }

  /**
   *
   */
  private void createToolBarAction() {
    this.toolbarManager = (ToolBarManager) this.form.getToolBarManager();
    addHelpAction(this.toolbarManager);
    this.wrkPkgActionSet = new PIDCWorkPkgActionSet(this.pidcEditorInput.getWorkPkgResponsibilityBO(), this);
    this.wrkPkgActionSet.addWorkPkgAction(this.toolbarManager);
    // TODO remove edit action
    this.wrkPkgActionSet.editWrkPkgAction(this.toolbarManager);
    this.toolbarManager.update(true);

  }


  /**
   * Creates the table.
   */
  private void createNatTable() {
    this.configRegistry = new ConfigRegistry();
    configureColumns();
    GroupByModel groupByModel = new GroupByModel();

    SortedSet<A2lWorkPackage> workPackagesSet =
        new TreeSet<>(this.pidcEditorInput.getWorkPkgResponsibilityBO().getWorkPackages().values());
    this.totTableRowCount = workPackagesSet.size();
    WorkPackageNatInputToColConverter workPackageConvertor = new WorkPackageNatInputToColConverter();

    // Custom Grid Filter Layer
    this.customFilterGridLayer = new CustomFilterGridLayer<A2lWorkPackage>(this.configRegistry, workPackagesSet,
        this.propertyToLabelMap, this.columnWidthMap, getWpComparator(0), workPackageConvertor, this, null,
        groupByModel, null, false, false, null, null, false);
    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.customFilterGridLayer,
        this.customFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.customFilterGridLayer, 0, 1);

    this.workPkgsNattable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        compositeGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    this.workPkgColmnFilterMatcher = new WorkPackgColmnFilterMatcher();
    this.customFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.workPkgColmnFilterMatcher);
    try {
      this.workPkgsNattable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.workPkgsNattable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.workPkgsNattable
        .addConfiguration(new GroupByHeaderMenuConfiguration(this.workPkgsNattable, this.groupByHeaderLayer));
    this.workPkgsNattable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.workPkgsNattable
        .addConfiguration(getCustomComparatorConfiguration(this.customFilterGridLayer.getColumnHeaderDataLayer()));
    this.workPkgsNattable.setConfigRegistry(this.configRegistry);
    // Configuration for Column header Context Menu
    this.workPkgsNattable.addConfiguration(new HeaderMenuConfiguration(this.workPkgsNattable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(WorkPackagesPage.this.workPkgsNattable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTable1, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      WorkPackagesPage.this.resetState = true;
                      WorkPackagesPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // Column Header Style Configuration
    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry2) {
        // configure the painter
        configRegistry2.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.COLUMN_HEADER);
        configRegistry2.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL, GridRegion.CORNER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
        configRegistry2.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
            GridRegion.COLUMN_HEADER);
        configRegistry2.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines, NORMAL,
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

        configRegistry2.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL,
            GridRegion.COLUMN_HEADER);
        configRegistry2.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.CORNER);
      }
    };
    this.customFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    this.workPkgsNattable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry2) {
        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry2.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);

        // override the default filter row configuration for painter
        WorkPackagesPage.this.configRegistry.registerConfigAttribute(CELL_PAINTER,
            new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

        registerEditableColumns();

      }
    });

    this.workPkgsNattable.addConfiguration(new WorkPkgEditConfiguration());


    // Sorting configuration
    this.workPkgsNattable.addConfiguration(new SingleClickSortConfiguration());
    this.workPkgsNattable.setConfigRegistry(this.configRegistry);
    this.workPkgsNattable.configure();

    // Using lambda expression instead of annonymous inner class
    this.workPkgsNattable.addPaintListener(paintevent -> {
      if (WorkPackagesPage.this.isRefreshNeeded && (WorkPackagesPage.this.selectedWorkpackage != null)) {
        WorkPackagesPage.this.selectionProvider
            .setSelection(new StructuredSelection(WorkPackagesPage.this.selectedWorkpackage));
        WorkPackagesPage.this.isRefreshNeeded = false;
      }
    });

    // double click on nattable to be editable
    this.workPkgsNattable.addConfiguration(new DefaultEditBindings() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        CustomSortFilterGroupEnabler customSortFilterGroupEnabler = new CustomSortFilterGroupEnabler(uiBindingRegistry,
            true, WorkPackagesPage.this.workPkgsNattable, WorkPackagesPage.this.customFilterGridLayer);
        uiBindingRegistry.registerFirstSingleClickBinding(WorkPackagesPage.this.cellEditorMouseEventMatcher,
            customSortFilterGroupEnabler);
        uiBindingRegistry.registerDoubleClickBinding(WorkPackagesPage.this.cellEditorMouseEventMatcher,
            new MouseEditAction());
      }
    });

    DataLayer bodyDataLayer = this.customFilterGridLayer.getBodyDataLayer();

    // unregister existing default update command handler
    this.customFilterGridLayer.getBodyDataLayer().unregisterCommandHandler(UpdateDataCommand.class);

    // register custom command handler
    this.customFilterGridLayer.getBodyDataLayer()
        .registerCommandHandler(new WorkPkgsUpdateCmndHandler(this.customFilterGridLayer));

    IRowDataProvider<A2lWorkPackage> bodyDataProvider =
        (IRowDataProvider<A2lWorkPackage>) bodyDataLayer.getDataProvider();

    WorkPkgsLabelAccumulator wrkPkgLblAccumulator = new WorkPkgsLabelAccumulator(bodyDataLayer, bodyDataProvider);

    bodyDataLayer.setConfigLabelAccumulator(wrkPkgLblAccumulator);

    this.selectionProvider = new RowSelectionProvider<>(this.customFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.customFilterGridLayer.getBodyDataProvider(), false);

    getSite().setSelectionProvider(this.selectionProvider);


    // adding selection provider
    addSelectionToNattable();
    this.workPkgsNattable.configure();

    loadState();

    // add listeners to save state
    this.workPkgsNattable.addFocusListener(new FocusListener() {

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
    this.workPkgsNattable.addDisposeListener(event ->
    // save state
    saveState());
    CustomDefaultBodyLayerStack bodyLayer = this.customFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.customFilterGridLayer.getColumnHeaderLayer(), this.customFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.workPkgsNattable.registerCommandHandler(columnChooserCommandHandler);
  }

  /**
   * Save current state for the NAT table.
   */
  private void saveState() {
    try {
      this.workPkgsNattable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save WP Definition nat table state", ioe, Activator.PLUGIN_ID);
    }

  }


  /**
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.workPkgsNattable.resetState();
      }
      this.workPkgsNattable.loadState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load WP Definition nat table state", ioe, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void addSelectionToNattable() {

    this.selectionProvider.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();

      if ((selection == null) || (selection.getFirstElement() == null)) {
        WorkPackagesPage.this.selectedRowObj = null;
        this.wrkPkgActionSet.getEditAction().setEnabled(false);
        this.isRefreshNeeded = true;
      }
      else if (selection.getFirstElement() instanceof A2lWorkPackage) {
        this.selectedRowObj = selection.getFirstElement();
        this.selectedWorkpackage = this.selectedRowObj;
        isRowEditable();
      }

    });
  }

  /**
   * @return
   */
  public boolean hasAccessRights() {
    try {
      return (null != this.currUser.getApicAccessRight()) &&
          this.currUser.hasNodeOwnerAccess(this.pidcEditorInput.getWorkPkgResponsibilityBO().getPidcId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return false;
  }

  /**
   * @param currUser CurrentUserBO
   * @return boolean
   */
  public boolean isRowEditable() {

    A2lWorkPackage selA2lWorkpackage = (A2lWorkPackage) WorkPackagesPage.this.selectedRowObj;
    if (!selA2lWorkpackage.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME) && hasAccessRights()) {
      this.wrkPkgActionSet.getEditAction().setEnabled(true);
      return true;
    }
    this.wrkPkgActionSet.getEditAction().setEnabled(false);
    return false;
  }


  /**
   * Gets the a 2 l wp param comparator.
   *
   * @param columnNumber int
   * @return Comparator
   */
  public Comparator<A2lWorkPackage> getWpComparator(final int columnNumber) {
    return ((final A2lWorkPackage obj1, final A2lWorkPackage obj2) -> {
      int ret = 0;
      switch (columnNumber) {
        case ApicConstants.COLUMN_INDEX_0:
          ret = ModelUtil.compare(obj1.getName(), obj2.getName());
          break;
        case ApicConstants.COLUMN_INDEX_1:
          ret = ModelUtil.compare(obj1.getDescription(), obj2.getDescription());
          break;
        case ApicConstants.COLUMN_INDEX_2:
          ret = ModelUtil.compare(obj1.getNameCustomer(), obj2.getNameCustomer());
          break;
        default:
          ret = obj1.compareTo(obj2);
          break;
      }
      return ret;

    });
  }


  /**
   * Gets the custom comparator configuration.
   *
   * @param columnHeaderDataLayer the column header data layer
   * @return the custom comparator configuration
   */
  private IConfiguration getCustomComparatorConfiguration(final AbstractLayer columnHeaderDataLayer) {

    return new AbstractRegistryConfiguration() {

      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // Register labels
        labelAccumulator.registerColumnOverrides(ApicConstants.COLUMN_INDEX_0,
            CUSTOM_COMPARATOR_LABEL + ApicConstants.COLUMN_INDEX_0);

        labelAccumulator.registerColumnOverrides(ApicConstants.COLUMN_INDEX_1,
            CUSTOM_COMPARATOR_LABEL + ApicConstants.COLUMN_INDEX_1);

        labelAccumulator.registerColumnOverrides(ApicConstants.COLUMN_INDEX_2,
            CUSTOM_COMPARATOR_LABEL + ApicConstants.COLUMN_INDEX_2);


        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getWpComparator(ApicConstants.COLUMN_INDEX_0), NORMAL,
            CUSTOM_COMPARATOR_LABEL + ApicConstants.COLUMN_INDEX_0);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getWpComparator(ApicConstants.COLUMN_INDEX_1), NORMAL,
            CUSTOM_COMPARATOR_LABEL + ApicConstants.COLUMN_INDEX_1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getWpComparator(ApicConstants.COLUMN_INDEX_2), NORMAL,
            CUSTOM_COMPARATOR_LABEL + ApicConstants.COLUMN_INDEX_2);
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.pidcEditor.getEditorInput().getWpRespDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshNattable();
    refreshUIElements();
  }

  /**
  *
  */
  public void refreshUIElements() {
    if (this.wrkPkgActionSet != null) {
      this.wrkPkgActionSet.getEditAction().setEnabled(hasAccessRights());
      this.wrkPkgActionSet.getAddAction().setEnabled(hasAccessRights());
    }

  }

  /**
   * Refresh nattable
   */
  private void refreshNattable() {
    if (this.workPkgsNattable != null) {
      // Get selected object's primary key ;to be used to update selected object reference with edited value after
      // refresh
      Long selectedObjId = null;
      if (null != WorkPackagesPage.this.selectedRowObj) {
        selectedObjId = ((A2lWorkPackage) WorkPackagesPage.this.selectedRowObj).getId();
      }
      this.customFilterGridLayer.getEventList().clear();
      this.customFilterGridLayer.getEventList()
          .addAll(this.pidcEditorInput.getWorkPkgResponsibilityBO().getWorkPackgsMap().values());
      this.customFilterGridLayer.getEventList().sort(getWpComparator(0));
      this.workPkgsNattable.doCommand(new StructuralRefreshCommand());
      this.workPkgsNattable.doCommand(new VisualRefreshCommand());
      this.workPkgsNattable.refresh(false);
      // update selected object refresh with updated value
      if ((null != this.selectedRowObj) || (null != selectedObjId)) {
        setSelectedRow(this.pidcEditorInput.getWorkPkgResponsibilityBO().getWorkPackgsMap().get(selectedObjId));
        this.selectedWorkpackage = this.selectedRowObj;
        this.isRefreshNeeded = true;
      }
      setStatusBarMessage(false);
    }
  }


  /**
   * @param selectedObj the selectedObj to set
   */
  public void setSelectedRow(final A2lWorkPackage selectedObj) {
    this.selectedRowObj = selectedObj;
  }

  private class NattableEditableRule implements IEditableRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(final ILayerCell cell, final IConfigRegistry configRegistry) {
      return isRowEditable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(final int columnIndex, final int rowIndex) {
      return false;
    }
  }


  /**
   * @param editable - true for editable, false for not editable
   */
  private void registerEditableColumns() {

    NattableEditableRule rule = new NattableEditableRule();

    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.cellEditor, DisplayMode.NORMAL,
        ApicUiConstants.CONFIG_LABEL_WP_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, rule, DisplayMode.EDIT,
        ApicUiConstants.CONFIG_LABEL_WP_NAME);

    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.cellEditor, DisplayMode.NORMAL,
        ApicUiConstants.CONFIG_LABEL_WP_NAME_CUST);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, rule, DisplayMode.EDIT,
        ApicUiConstants.CONFIG_LABEL_WP_NAME_CUST);

    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.cellEditor, DisplayMode.NORMAL,
        ApicUiConstants.CONFIG_LABEL_DESC);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, rule, DisplayMode.EDIT,
        ApicUiConstants.CONFIG_LABEL_DESC);

    // register data validator for nattable edit validation
    WorkPackagesNatValidator validator =
        new WorkPackagesNatValidator(this.pidcEditorInput.getWorkPkgResponsibilityBO());
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, validator, DisplayMode.EDIT,
        ApicUiConstants.CONFIG_LABEL_WP_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, validator, DisplayMode.EDIT,
        ApicUiConstants.CONFIG_LABEL_WP_NAME_CUST);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, validator, DisplayMode.EDIT,
        ApicUiConstants.CONFIG_LABEL_DESC);

    // register error handler to display error messages
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, ApicUiConstants.CONFIG_LABEL_WP_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, ApicUiConstants.CONFIG_LABEL_WP_NAME_CUST);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, ApicUiConstants.CONFIG_LABEL_DESC);

    WorkPackagesPage.this.cellEditor.setErrorDecorationEnabled(true);
    WorkPackagesPage.this.cellEditor.setDecorationPositionOverride(SWT.RIGHT | SWT.TOP);


  }

  /**
  *
  */
  protected void reconstructNatTable() {
    this.workPkgsNattable.dispose();
    this.propertyToLabelMap.clear();
    this.customFilterGridLayer = null;


    if (this.form.getToolBarManager() != null) {
      this.form.getToolBarManager().removeAll();
    }
    createNatTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.form.getBody().pack();
    this.section.layout();

    if (!this.filterTxt.getText().isEmpty()) {
      this.filterTxt.setText(this.filterTxt.getText());
    }

    if (this.workPkgsNattable != null) {
      this.workPkgsNattable.doCommand(new StructuralRefreshCommand());
      this.workPkgsNattable.doCommand(new VisualRefreshCommand());
      this.workPkgsNattable.refresh();
      this.customFilterGridLayer.getEventList().sort(getWpComparator(0));
    }
  }

  /**
   * Method to create columns for nattable
   */
  private void configureColumns() {
    this.propertyToLabelMap = new HashMap<>();

    this.columnWidthMap = new HashMap<>();

    this.propertyToLabelMap.put(ApicConstants.COLUMN_INDEX_0, WORK_PACKAGE_COL_NAME);

    this.columnWidthMap.put(ApicConstants.COLUMN_INDEX_0, 300);

    this.propertyToLabelMap.put(ApicConstants.COLUMN_INDEX_1, DESCRIPTION_COL_NAME);

    this.columnWidthMap.put(ApicConstants.COLUMN_INDEX_1, 300);

    this.propertyToLabelMap.put(ApicConstants.COLUMN_INDEX_2, CUSTOMER_COL_NAME);

    this.columnWidthMap.put(ApicConstants.COLUMN_INDEX_2, 300);

  }

  /**
   * This method creates filter text.
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();

    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage("Type filter text");
    this.filterTxt.addModifyListener(event -> {
      String text = WorkPackagesPage.this.filterTxt.getText().trim();
      WorkPackagesPage.this.workPkgColmnFilterMatcher.setFilterText(text, true);
      WorkPackagesPage.this.customFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      WorkPackagesPage.this.customFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(WorkPackagesPage.this.customFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(this.groupByHeaderLayer, false);
    });
  }

  /**
   * {@inheritDoc}
   *
   * @param outlineSelection boolean
   */
  @Override
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.totTableRowCount = this.pidcEditorInput.getWorkPkgResponsibilityBO().getWorkPackages().values().size();
    this.pidcEditor.updateStatusBar(outlineSelection, false, this.totTableRowCount,
        this.customFilterGridLayer != null ? this.customFilterGridLayer.getRowHeaderLayer().getPreferredRowCount() : 0);
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
   * This method returns filter text GridData object.
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    if (null == this.toolbarManager) {
      this.toolbarManager = new ToolBarManager(SWT.FLAT);
    }
    return this.toolbarManager;
  }


  /**
   * @return the selectedRow
   */
  public A2lWorkPackage getSelectedRow() {
    return (A2lWorkPackage) this.selectedRowObj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // TODO implementation
  }


  /**
   * @return the selectedWorkpackage
   */
  public Object getSelectedWorkpackage() {
    return this.selectedWorkpackage;
  }


  /**
   * @param selectedWorkpackage the selectedWorkpackage to set
   */
  public void setSelectedWorkpackage(final Object selectedWorkpackage) {
    this.selectedWorkpackage = selectedWorkpackage;
  }

}
