/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommand;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.config.RenderErrorHandling;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
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
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionProvider;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.ui.IEditorInput;
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
import com.bosch.caltool.apic.ui.actions.ResponsibilityActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.editors.compare.RespNattableLabelAccumulator;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RespComboEditConfiguration;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RespNatInputToColConverter;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RespNatTableMouseListener;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RespNatValidator;
import com.bosch.caltool.apic.ui.table.filters.RespColFilterMatcher;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditA2lRespDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.natsupport.CustomSortFilterGroupEnabler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
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
 * @author apj4cob
 */
public class ResponsibilityPage extends AbstractGroupByNatFormPage implements ISelectionListener {

  private final PIDCEditor editor;
  private final PIDCEditorInput editorInput;
  /** Non scrollable form. */
  private Form nonScrollableForm;
  /** The composite. */
  private Composite composite;
  /** The section. */
  private Section section;

  /** The base form. */
  private Form form;
  /** Filter text instance. */
  private Text filterTxt;
  private ConfigRegistry configRegistry;
  /** The nat table. */
  private CustomNATTable natTable;
  /** The custom filter grid layer. */
  private CustomFilterGridLayer customFilterGridLayer;
  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  /** The column width map. */
  private Map<Integer, Integer> columnWidthMap;
  private RespColFilterMatcher respColFilterMatcher;
  /** Selected cell's column position. */
  protected int selectedColPostn;
  /** Selected cell's row position. */
  protected int selectedRowPostn;
  /**
   * Section Title
   */
  private static final String TITLE = "A2L Responsibilities";
  /**
   * Reset State
   */
  protected boolean resetState;
  /** The tool bar manager. */
  private ToolBarManager toolBarManager;
  private int totTableRowCount;
  /** The selected obj. */
  protected Object selectedObj;


  /** The Constant CUSTOM_COMPARATOR_LABEL. */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";
  /** The Constant For Column Number. */
  public static final int COLUMN_RESP_TYPE = 0;
  public static final int COLUMN_RESP_USER_NAME = 1;
  public static final int COLUMN_FIRST_NAME = 2;
  public static final int COLUMN_LAST_NAME = 3;
  public static final int COLUMN_DEPT_NAME = 4;
  public static final int COLUMN_ALIAS_NAME = 5;
  public static final int COLUMN_DELETED_FLAG = 6;
  public static final int COLUMN_CREATED_DATE = 7;
  public static final int COLUMN_MODIFIED_DATE = 8;
  public static final int COLUMN_CREATED_USER = 9;


  private WorkPkgResponsibilityBO workPkgResponsibilityBO;
  private Action addAction;
  private Action editAction;
  private RowSelectionProvider<A2lResponsibility> selectionProvider;
  private static final String CONFIG_LABEL_FIRST_NAME = "FIRST_NAME";

  private static final String CONFIG_LABEL_LAST_NAME = "LAST_NAME";

  private static final String CONFIG_LABEL_DEPT = "DEPARTMENT";

  private final TextCellEditor textCellEditor = new TextCellEditor();
  /**
   * the cell editor mouse event matcher for the ui binding on double clicking a cell
   */
  private final CellEditorMouseEventMatcher cellEditorMouseEventMatcher = new CellEditorMouseEventMatcher();

  private RespNatValidator validator;

  private GroupByHeaderLayer groupByHeaderLayer;

  private boolean isRefreshNeeded;

  private Object selectedResposibility;


  /**
   * @param editor FormEditor
   * @param editorInput IEditorInput
   */
  public ResponsibilityPage(final FormEditor editor, final IEditorInput editorInput) {
    super(editor, "Responsibility at PIDC Level", TITLE);
    this.editor = (PIDCEditor) editor;
    this.editorInput = (PIDCEditorInput) editorInput;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-249
  @Override
  public void createPartControl(final Composite parent) {
    // ICDM-249
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
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
    FormToolkit formToolkit; // @jve:decl-index=0:visual-constraint=""
    formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);
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
   * Creates the composite.
   *
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());

    createSection(toolkit);
    this.composite.setLayoutData(gridData);
  }


  /**
   * Creates the section.
   *
   * @param toolkit FormToolkit
   */
  public void createSection(final FormToolkit toolkit) {
    GridData gridData = getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText(TITLE);
    this.section.setExpanded(true);
    // ICDM-183
    this.section.setDescription("List of all Work Package Responsibilities defined for the PIDC");
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
    this.filterTxt = toolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    this.form.getBody().setLayout(new GridLayout());
    createNatTable();
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
   * This method creates filter text.
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = ResponsibilityPage.this.filterTxt.getText().trim();
      ResponsibilityPage.this.respColFilterMatcher.setFilterText(text, true);
      ResponsibilityPage.this.customFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      ResponsibilityPage.this.customFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(ResponsibilityPage.this.customFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(this.groupByHeaderLayer, false);
    });
  }

  /**
   * Configure columns.
   */
  private void configureColumns() {
    this.propertyToLabelMap = new HashMap<>();
    this.columnWidthMap = new HashMap<>();
    this.propertyToLabelMap.put(COLUMN_RESP_TYPE, "Responsibility Type");
    this.columnWidthMap.put(COLUMN_RESP_TYPE, 120);
    this.propertyToLabelMap.put(COLUMN_RESP_USER_NAME, "Name");
    this.columnWidthMap.put(COLUMN_RESP_USER_NAME, 225);
    this.propertyToLabelMap.put(COLUMN_FIRST_NAME, "First Name");
    this.columnWidthMap.put(COLUMN_FIRST_NAME, 225);
    this.propertyToLabelMap.put(COLUMN_LAST_NAME, "Last Name");
    this.columnWidthMap.put(COLUMN_LAST_NAME, 225);
    this.propertyToLabelMap.put(COLUMN_DEPT_NAME, "Department");
    this.columnWidthMap.put(COLUMN_DEPT_NAME, 100);
    this.propertyToLabelMap.put(COLUMN_ALIAS_NAME, "Alias Name");
    this.columnWidthMap.put(COLUMN_ALIAS_NAME, 225);
    this.propertyToLabelMap.put(COLUMN_DELETED_FLAG, "Deleted");
    this.columnWidthMap.put(COLUMN_DELETED_FLAG, 40);

    this.propertyToLabelMap.put(COLUMN_CREATED_DATE, "Created Date");
    this.columnWidthMap.put(COLUMN_CREATED_DATE, 80);
    this.propertyToLabelMap.put(COLUMN_MODIFIED_DATE, "Modified Date");
    this.columnWidthMap.put(COLUMN_MODIFIED_DATE, 80);
    this.propertyToLabelMap.put(COLUMN_CREATED_USER, "Created User");
    this.columnWidthMap.put(COLUMN_CREATED_USER, 40);
  }

  /**
   * Creates the table.
   */
  private void createNatTable() {
    this.configRegistry = new ConfigRegistry();
    configureColumns();

    GroupByModel groupByModel = createCustomFilterGridLayer();
    // Composite grid layer
    CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
    this.groupByHeaderLayer = new GroupByHeaderLayer(groupByModel, this.customFilterGridLayer,
        this.customFilterGridLayer.getColumnHeaderDataProvider());
    compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, this.groupByHeaderLayer, 0, 0);
    compositeGridLayer.setChildLayer("Grid", this.customFilterGridLayer, 0, 1);
    ResponsibilityPage.this.customFilterGridLayer.getBodyDataLayer().getTreeRowModel().registerRowGroupModelListener(
        () -> ResponsibilityPage.this.setStatusBarMessage(ResponsibilityPage.this.groupByHeaderLayer, false));

    this.natTable = new CustomNATTable(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        compositeGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);
    // Filter configuration
    this.respColFilterMatcher = new RespColFilterMatcher(getWorkPkgResponsibilityBO());
    this.customFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.respColFilterMatcher);
    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    this.natTable.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.natTable.addConfiguration(new GroupByHeaderMenuConfiguration(this.natTable, this.groupByHeaderLayer));
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.customFilterGridLayer.getColumnHeaderDataLayer()));
    this.natTable.setConfigRegistry(this.configRegistry);
    // Configuration for Column header Context Menu
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(ResponsibilityPage.this.natTable)
                .withColumnChooserMenuItem().withMenuItemProvider((natTable1, popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      ResponsibilityPage.this.resetState = true;
                      ResponsibilityPage.this.reconstructNatTable();
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
        ResponsibilityPage.this.configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL,
            GridRegion.COLUMN_HEADER);
        ResponsibilityPage.this.configRegistry.registerConfigAttribute(CELL_PAINTER, this.cellPaintr, NORMAL,
            GridRegion.CORNER);

        // configure whether to render grid lines or not
        // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
        ResponsibilityPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES,
            this.rendrGridLines, NORMAL, GridRegion.COLUMN_HEADER);
        ResponsibilityPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES,
            this.rendrGridLines, NORMAL, GridRegion.CORNER);

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

        ResponsibilityPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
            NORMAL, GridRegion.COLUMN_HEADER);
        ResponsibilityPage.this.configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle,
            NORMAL, GridRegion.CORNER);
      }
    };
    this.customFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));
    // Style Configuration for Column filter
    this.natTable.addConfiguration(new FilterRowCustomConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry2) {
        super.configureRegistry(configRegistry2);
        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry2.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    // Sorting configuration
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable.addMouseListener(new RespNatTableMouseListener(this));
    DataLayer bodyDataLayer = this.customFilterGridLayer.getBodyDataLayer();
    createSelProvider(bodyDataLayer);
    this.natTable.addConfiguration(new AbstractRegistryConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        registerEditableColumns();
      }
    });
    // double click on nattable to be editable
    this.natTable.addConfiguration(new DefaultEditBindings() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        CustomSortFilterGroupEnabler customSortFilterGroupEnabler = new CustomSortFilterGroupEnabler(uiBindingRegistry,
            true, ResponsibilityPage.this.natTable, ResponsibilityPage.this.customFilterGridLayer);
        uiBindingRegistry.registerFirstSingleClickBinding(ResponsibilityPage.this.cellEditorMouseEventMatcher,
            customSortFilterGroupEnabler);
        uiBindingRegistry.registerDoubleClickBinding(ResponsibilityPage.this.cellEditorMouseEventMatcher,
            new MouseEditAction());
      }
    });

    // register custom command handler
    this.customFilterGridLayer.getBodyDataLayer()
        .registerCommandHandler(new RespUpdateCommandHandler(this.customFilterGridLayer, this));
    // set label accumulator in body layer
    bodyDataLayer.setConfigLabelAccumulator(new RespNattableLabelAccumulator(bodyDataLayer, this));

    this.natTable.addConfiguration(new RespComboEditConfiguration(ResponsibilityPage.this));

    this.natTable.setConfigRegistry(this.configRegistry);
    this.natTable.configure();

    // Load the saved state of NAT table
    loadState();
    this.customFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));
    CustomDefaultBodyLayerStack bodyLayer = this.customFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.customFilterGridLayer.getColumnHeaderLayer(), this.customFilterGridLayer.getColumnHeaderDataLayer(),
            null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);

    // Using lambda expression instead of anonymous class
    this.natTable.addPaintListener(paintevent -> {
      if (ResponsibilityPage.this.isRefreshNeeded && (ResponsibilityPage.this.selectedResposibility != null)) {
        getSelectionProvider().setSelection(new StructuredSelection(ResponsibilityPage.this.selectedResposibility));
        ResponsibilityPage.this.isRefreshNeeded = false;
      }
    });

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
    createToolBarAction();
    addRightClickOption();
    attachToolTip(this.natTable);
  }

  /**
   * Context menu actions
   */
  private void addRightClickOption() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(menuManager -> {

      SelectionLayer selectionLayer = this.customFilterGridLayer.getBodyLayer().getSelectionLayer();
      PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();
      final IStructuredSelection selection = (IStructuredSelection) this.selectionProvider.getSelection();
      // add Context menu options to edit resp type
      ResponsibilityActionSet respActionSet = new ResponsibilityActionSet();
      if ((null != selectedCellPositions) && allCellsBelongToFirstColumn(selectedCellPositions)) {
        List<A2lResponsibility> a2lWpRespList = selection.toList();
        boolean enable = enableRespTypeAction(a2lWpRespList);
        respActionSet.setBoschResponsibleAction(menuMgr, selection, enable);
        respActionSet.setCustomerResponsibleAction(menuMgr, selection, enable);
        respActionSet.setOthersResponsibleAction(menuMgr, selection, enable);
      }
      mergeA2lResponsibility(menuMgr, selection, respActionSet);
    });

    final Menu menu = menuMgr.createContextMenu(this.natTable.getShell());
    this.natTable.setMenu(menu);

    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.selectionProvider);
  }


  /**
   * @param selection
   * @param menuMgr
   * @param respActionSet
   */
  private void mergeA2lResponsibility(final MenuManager menuMgr, final IStructuredSelection selection,
      final ResponsibilityActionSet respActionSet) {
    List<A2lResponsibility> a2lWpRespList = selection.toList();
    if (isDefaultRespSel(a2lWpRespList)) {
      CDMLogger.getInstance().info("Default Responsibility cannot be merged", Activator.PLUGIN_ID);
      return;
    }
    if (a2lWpRespList.size() > 1) {
      IMenuManager subMenu = new MenuManager("Merge records to");
      subMenu.setRemoveAllWhenShown(true);
      subMenu.addMenuListener(imenumanager -> a2lWpRespList.forEach(a2lResponsibility -> respActionSet
          .setMergeA2lResponsibleAction(imenumanager, a2lResponsibility, a2lWpRespList)));
      menuMgr.add(subMenu);
    }
  }

  /**
   * @param a2lWpRespList
   * @return
   */
  private boolean isDefaultRespSel(final List<A2lResponsibility> a2lWpRespList) {
    return !a2lWpRespList.stream().filter(resp -> A2lResponsibilityCommon.isDefaultResponsibility(resp))
        .collect(Collectors.toCollection(ArrayList::new)).isEmpty();
  }

  /**
   * @param selectedCellPositions
   * @return true when all cells belong to first column
   */
  private boolean allCellsBelongToFirstColumn(final PositionCoordinate[] selectedCellPositions) {
    for (PositionCoordinate cellPosition : selectedCellPositions) {
      if (cellPosition.columnPosition != COLUMN_RESP_TYPE) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param a2lWpRespList
   * @return List<A2lResponsibility>
   */
  private boolean enableRespTypeAction(final List<A2lResponsibility> a2lWpRespList) {
    try {
      if (!getWorkPkgResponsibilityBO().canAddResp()) {
        return false;
      }
      int validRespCount = 0;
      for (A2lResponsibility a2lResponsibility : a2lWpRespList) {
        if (getWorkPkgResponsibilityBO().canEditResp(a2lResponsibility)) {
          validRespCount++;
        }
      }
      if (validRespCount == 0) {
        return false;
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   * @param bodyDataLayer DataLayer
   * @return IRowDataProvider<A2lResponsibility>
   */
  public IRowDataProvider<A2lResponsibility> createSelProvider(final DataLayer bodyDataLayer) {
    // Configuration related to selection in nat page
    this.selectionProvider = new RowSelectionProvider<>(this.customFilterGridLayer.getBodyLayer().getSelectionLayer(),
        this.customFilterGridLayer.getBodyDataProvider(), false);

    IRowDataProvider<A2lResponsibility> bodyDataProvider =
        (IRowDataProvider<A2lResponsibility>) bodyDataLayer.getDataProvider();
    this.selectionProvider.addSelectionChangedListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if ((selection == null) || (selection.getFirstElement() == null)) {
        ResponsibilityPage.this.selectedObj = null;
        ResponsibilityPage.this.editAction.setEnabled(false);
        this.isRefreshNeeded = true;
      }
      else {
        enableEditButton(selection);
        this.selectedResposibility = selection.getFirstElement();
      }
    });
    return bodyDataProvider;
  }


  /**
   * @param selection IStructuredSelection
   */
  public void enableEditButton(final IStructuredSelection selection) {
    if (selection.getFirstElement() instanceof A2lResponsibility) {
      ResponsibilityPage.this.selectedObj = selection.getFirstElement();
      A2lResponsibility selA2lResp = (A2lResponsibility) ResponsibilityPage.this.selectedObj;

      try {
        // Enable edit option only if user has access rights and edit is applicable in the selected row
        this.editAction.setEnabled(getWorkPkgResponsibilityBO().canEditResp(selA2lResp));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      }
    }
  }

  /**
   * @return GroupByModel
   */
  public GroupByModel createCustomFilterGridLayer() {
    // Group by model
    GroupByModel groupByModel = new GroupByModel();
    // iCDM-848, Select cols to be hidden by default
    List<Integer> colsToHide = new ArrayList<>();
    colsToHide.add(7);
    colsToHide.add(8);
    colsToHide.add(9);
    setWorkPkgResponsibilityBO(this.editorInput.getWorkPkgResponsibilityBO());
    A2lResponsibilityModel a2lRespModel = getWorkPkgResponsibilityBO().loadA2lRespForPidc(false);
    Set<A2lResponsibility> a2lRespSet = new HashSet<>(a2lRespModel.getA2lResponsibilityMap().values());
    this.totTableRowCount = a2lRespSet.size();
    RespNatInputToColConverter respColConverter =
        new RespNatInputToColConverter(this.editorInput.getWorkPkgResponsibilityBO());
    // Custom Grid Filter Layer
    this.customFilterGridLayer = new CustomFilterGridLayer<A2lResponsibility>(this.configRegistry, a2lRespSet,
        this.propertyToLabelMap, this.columnWidthMap, null, respColConverter, this, null, groupByModel, colsToHide,
        false, true, null, null, false);
    return groupByModel;
  }

  private class NattableEditableRule implements IEditableRule {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(final ILayerCell cell, final IConfigRegistry configRegistry) {
      A2lResponsibility selA2lResp = (A2lResponsibility) ResponsibilityPage.this.selectedObj;
      ResponsibilityPage.this.validator.setSelA2lResp(selA2lResp);
      try {
        return getWorkPkgResponsibilityBO().canEditResp(selA2lResp);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
      }
      return false;
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
   * register config labels for editable columns
   */
  private void registerEditableColumns() {

    NattableEditableRule rule = new NattableEditableRule();

    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.textCellEditor, DisplayMode.EDIT,
        CONFIG_LABEL_FIRST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, rule, DisplayMode.EDIT,
        CONFIG_LABEL_FIRST_NAME);

    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.textCellEditor, DisplayMode.EDIT,
        CONFIG_LABEL_LAST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, rule, DisplayMode.EDIT,
        CONFIG_LABEL_LAST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, this.textCellEditor, DisplayMode.EDIT,
        CONFIG_LABEL_DEPT);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, rule, DisplayMode.EDIT,
        CONFIG_LABEL_DEPT);
    this.validator = new RespNatValidator(ResponsibilityPage.this.getWorkPkgResponsibilityBO());
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, this.validator, DisplayMode.EDIT,
        CONFIG_LABEL_FIRST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, this.validator, DisplayMode.EDIT,
        CONFIG_LABEL_LAST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, this.validator, DisplayMode.EDIT,
        CONFIG_LABEL_DEPT);
    // register error handler to display error messages
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, CONFIG_LABEL_FIRST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, CONFIG_LABEL_LAST_NAME);
    this.configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER,
        new RenderErrorHandling(), DisplayMode.EDIT, CONFIG_LABEL_DEPT);
    ResponsibilityPage.this.textCellEditor.setErrorDecorationEnabled(true);
    ResponsibilityPage.this.textCellEditor.setDecorationPositionOverride(SWT.RIGHT | SWT.TOP);
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
        labelAccumulator.registerColumnOverrides(COLUMN_RESP_TYPE, CUSTOM_COMPARATOR_LABEL + COLUMN_RESP_TYPE);

        labelAccumulator.registerColumnOverrides(COLUMN_RESP_USER_NAME,
            CUSTOM_COMPARATOR_LABEL + COLUMN_RESP_USER_NAME);

        labelAccumulator.registerColumnOverrides(COLUMN_FIRST_NAME, CUSTOM_COMPARATOR_LABEL + COLUMN_FIRST_NAME);

        labelAccumulator.registerColumnOverrides(COLUMN_LAST_NAME, CUSTOM_COMPARATOR_LABEL + COLUMN_LAST_NAME);


        labelAccumulator.registerColumnOverrides(COLUMN_DEPT_NAME, CUSTOM_COMPARATOR_LABEL + COLUMN_DEPT_NAME);

        labelAccumulator.registerColumnOverrides(COLUMN_ALIAS_NAME, CUSTOM_COMPARATOR_LABEL + COLUMN_ALIAS_NAME);

        labelAccumulator.registerColumnOverrides(COLUMN_DELETED_FLAG, CUSTOM_COMPARATOR_LABEL + COLUMN_DELETED_FLAG);

        labelAccumulator.registerColumnOverrides(COLUMN_CREATED_DATE, CUSTOM_COMPARATOR_LABEL + COLUMN_CREATED_DATE);

        labelAccumulator.registerColumnOverrides(COLUMN_MODIFIED_DATE, CUSTOM_COMPARATOR_LABEL + COLUMN_MODIFIED_DATE);

        labelAccumulator.registerColumnOverrides(COLUMN_CREATED_USER, CUSTOM_COMPARATOR_LABEL + COLUMN_CREATED_USER);


        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_RESP_TYPE), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_RESP_TYPE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_RESP_USER_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_RESP_USER_NAME);
        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_FIRST_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_FIRST_NAME);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_LAST_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_LAST_NAME);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_DEPT_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_DEPT_NAME);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_ALIAS_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_ALIAS_NAME);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_DELETED_FLAG), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_DELETED_FLAG);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_CREATED_DATE), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_CREATED_DATE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_MODIFIED_DATE), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_MODIFIED_DATE);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getA2lRespComparator(COLUMN_CREATED_USER), NORMAL, CUSTOM_COMPARATOR_LABEL + COLUMN_CREATED_USER);
      }
    };
  }

  /**
   * Creates the tool bar action.
   */
  private void createToolBarAction() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = this.toolBarManager.createControl(this.section);
    addHelpAction(this.toolBarManager);
    // Get the user info NodeAccessRight currentUserRight =
    this.addAction = new Action("Add Responsibility") {

      @Override
      public void run() {
        CreateEditA2lRespDialog createDialog = new CreateEditA2lRespDialog(Display.getCurrent().getActiveShell(),
            ResponsibilityPage.this.getWorkPkgResponsibilityBO(), new A2lResponsibility(), false);
        createDialog.open();
        if (CommonUtils.isNotNull(createDialog.getSelA2lResp())) {
          setSelectedObj(createDialog.getSelA2lResp());
          ResponsibilityPage.this.selectedResposibility = createDialog.getSelA2lResp();
        }
      }
    };

    this.addAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    this.form.getToolBarManager().add(this.addAction);
    try {
      this.addAction.setEnabled(getWorkPkgResponsibilityBO().canAddResp());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }

    this.editAction = new Action("Edit Responsibility") {

      @Override
      public void run() {
        CreateEditA2lRespDialog editDialog = new CreateEditA2lRespDialog(Display.getCurrent().getActiveShell(),
            ResponsibilityPage.this.getWorkPkgResponsibilityBO(),
            (A2lResponsibility) ResponsibilityPage.this.selectedObj, true);
        editDialog.open();
      }
    };
    // set icon for button
    this.editAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.form.getToolBarManager().add(this.editAction);
    this.editAction.setEnabled(false);
    this.form.getToolBarManager().update(true);
    this.form.setToolBarVerticalAlignment(SWT.TOP);
    this.section.setTextClient(toolbar);
  }


  /**
   *
   */
  protected void reconstructNatTable() {
    this.natTable.dispose();
    this.propertyToLabelMap.clear();
    this.customFilterGridLayer = null;
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
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

    if (this.natTable != null) {
      this.natTable.doCommand(new StructuralRefreshCommand());
      this.natTable.doCommand(new VisualRefreshCommand());
      this.natTable.refresh(false);
      this.customFilterGridLayer.getEventList().sort(getA2lRespComparator(0));
    }
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
   * Gets the a 2 l wp param comparator.
   *
   * @param columnNumber int
   * @return Comparator
   */
  public Comparator<A2lResponsibility> getA2lRespComparator(final int columnNumber) {
    return ((final A2lResponsibility obj1, final A2lResponsibility obj2) -> {
      int ret = 0;
      switch (columnNumber) {
        case COLUMN_RESP_TYPE:
          ret = ModelUtil.compare(WpRespType.getType(obj1.getRespType()).getDispName(),
              WpRespType.getType(obj2.getRespType()).getDispName());
          break;
        case COLUMN_RESP_USER_NAME:
          ret = ModelUtil.compare(obj1.getName(), obj2.getName());
          break;
        case COLUMN_FIRST_NAME:
          ret = ModelUtil.compare(getWorkPkgResponsibilityBO().getRespFirstName(obj1),
              getWorkPkgResponsibilityBO().getRespFirstName(obj2));
          break;
        case COLUMN_LAST_NAME:
          ret = ModelUtil.compare(getWorkPkgResponsibilityBO().getRespLastName(obj1),
              getWorkPkgResponsibilityBO().getRespLastName(obj2));
          break;
        case COLUMN_DEPT_NAME:
          ret = ModelUtil.compare(getWorkPkgResponsibilityBO().getRespDeptName(obj1),
              getWorkPkgResponsibilityBO().getRespDeptName(obj2));
          break;
        case COLUMN_ALIAS_NAME:
          ret = ModelUtil.compare(obj1.getAliasName(), obj2.getAliasName());
          break;
        case COLUMN_DELETED_FLAG:
          ret = ModelUtil.compare(obj1.isDeleted(), obj2.isDeleted());
          break;
        case COLUMN_CREATED_DATE:
          ret = ModelUtil.compare(obj1.getCreatedDate(), obj2.getCreatedDate());
          break;
        case COLUMN_MODIFIED_DATE:
          ret = ModelUtil.compare(obj1.getModifiedDate(), obj2.getModifiedDate());
          break;
        case COLUMN_CREATED_USER:
          ret = ModelUtil.compare(obj1.getCreatedUser(), obj2.getCreatedUser());
          break;

        default:
          ret = ModelUtil.compare(WpRespType.getType(obj1.getRespType()).getDispName(),
              WpRespType.getType(obj2.getRespType()).getDispName());
          break;
      }
      // Second level sorting based on Responsible User Name
      if (ret == 0) {
        ret = ModelUtil.compare(obj1.getName(), obj2.getName());
      }
      return ret;
    });
  }


  /**
   * The Class FilterRowCustomConfiguration.
   */
  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    /** The double display converter. */
    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    /**
     * Gets the ignorecase comparator.
     *
     * @return the ignorecase comparator
     */
    private static Comparator<?> getIgnorecaseComparator() {
      return (final String obj1, final String obj2) -> obj1.compareToIgnoreCase(obj2);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);

      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 3);

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart arg0, final ISelection arg1) {
    // No implementation
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
  public void setStatusBarMessage(final boolean outlineSelection) {
    this.totTableRowCount = getWorkPkgResponsibilityBO().getA2lRespModel().getA2lResponsibilityMap().values().size();
    this.editor.updateStatusBar(this.totTableRowCount,
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
   * Load saved state of NAT table.
   */
  private void loadState() {
    try {
      if (this.resetState) {
        this.natTable.resetState();
      }
      this.natTable.loadState();
      clearSorting();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to load Responsibility nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  // Added the method to clear the sorting to prevent loading the previous sorting status
  private void clearSorting() {
    this.customFilterGridLayer.getSortableColumnHeaderLayer().getSortModel().clear();
  }

  /**
   * Save current state for the NAT table.
   */
  private void saveState() {
    try {
      this.natTable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save Responsibility nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput().getWpRespDataHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (null != this.natTable) {
      refreshNattable();
      refreshUIElements();
    }
  }

  /**
   *
   */
  public void refreshUIElements() {
    try {
      this.addAction.setEnabled(getWorkPkgResponsibilityBO().canAddResp());
      if (CommonUtils.isNotNull(this.selectedObj)) {
        this.editAction.setEnabled(
            getWorkPkgResponsibilityBO().canEditResp((A2lResponsibility) ResponsibilityPage.this.selectedObj));
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp);
    }
  }

  /**
   * Refresh nattable
   */
  private void refreshNattable() {
    // Get selected object's primary key ;to be used to update selected object reference with edited value after
    // refresh
    Long selectedObjId = null;
    if (null != ResponsibilityPage.this.selectedObj) {
      selectedObjId = ((A2lResponsibility) ResponsibilityPage.this.selectedObj).getId();
    }
    this.customFilterGridLayer.getEventList().clear();
    this.customFilterGridLayer.getEventList()
        .addAll(getWorkPkgResponsibilityBO().getA2lRespModel().getA2lResponsibilityMap().values());

    this.natTable.refresh(false);
    // update selected object
    if ((null != ResponsibilityPage.this.selectedObj) || (null != selectedObjId)) {
      setSelectedObj(ResponsibilityPage.this.getWorkPkgResponsibilityBO().getA2lRespModel().getA2lResponsibilityMap()
          .get(selectedObjId));
      this.selectedResposibility = ResponsibilityPage.this.getWorkPkgResponsibilityBO().getA2lRespModel()
          .getA2lResponsibilityMap().get(selectedObjId);
      this.isRefreshNeeded = true;
    }

    setStatusBarMessage(false);

  }

  /**
   * @param selectedObj the selectedObj to set
   */
  public void setSelectedObj(final Object selectedObj) {
    this.selectedObj = selectedObj;
  }

  /**
   * Gets the selected col postn.
   *
   * @return the selectedColPostn
   */
  public int getSelectedColPostn() {
    return this.selectedColPostn;
  }

  /**
   * Gets the selected row postn.
   *
   * @return the selectedRowPostn
   */
  public int getSelectedRowPostn() {
    return this.selectedRowPostn;
  }

  /**
   * Gets the selection provider.
   *
   * @return the selectionProvider
   */
  public RowSelectionProvider<A2lResponsibility> getSelectionProvider() {
    return this.selectionProvider;
  }


  /**
   * @return the workPkgResponsibilityBO
   */
  public WorkPkgResponsibilityBO getWorkPkgResponsibilityBO() {
    return this.workPkgResponsibilityBO;
  }

  /**
   * @return CustomNATTable
   */
  public CustomNATTable getNATTable() {
    return this.natTable;
  }

  /**
   * @return CustomFilterGridLayer
   */
  public CustomFilterGridLayer getCustomFilterGridLayer() {
    return this.customFilterGridLayer;
  }

  /**
   * @param convertColumnPosition int
   */
  public void setSelectedColPostn(final int convertColumnPosition) {
    this.selectedColPostn = convertColumnPosition;
  }

  /**
   * @param convertRowPosition int
   */
  public void setSelectedRowPostn(final int convertRowPosition) {
    this.selectedRowPostn = convertRowPosition;
  }

  /**
   * @param workPkgResponsibilityBO the workPkgResponsibilityBO to set
   */
  public void setWorkPkgResponsibilityBO(final WorkPkgResponsibilityBO workPkgResponsibilityBO) {
    this.workPkgResponsibilityBO = workPkgResponsibilityBO;
  }
}
