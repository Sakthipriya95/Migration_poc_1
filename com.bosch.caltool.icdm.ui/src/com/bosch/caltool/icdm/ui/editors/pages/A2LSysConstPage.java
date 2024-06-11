/**
 *
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.calmodel.a2ldata.module.system.constant.SystemConstant;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant.SortColumns;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ui.table.filters.AllColumnFilterMatcher;
import com.bosch.caltool.icdm.ui.util.IMessageConstants;
import com.bosch.caltool.icdm.ui.util.Messages;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;

/**
 * The Class SysConstNatFormPage.
 */
public class A2LSysConstPage extends AbstractNatFormPage implements ISelectionListener {

  /** The composite. */
  private Composite composite;

  /** The section. */
  private Section section;

  /** The base form. */
  private Form baseForm;


  /** The Constant CUSTOM_COMPARATOR_LABEL. */
  private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

  /** Filter text instance. */
  private Text filterTxt;
  /**
   * totTableRowCount contains the Total number of rows set to NatTable Viewer.Used to update the StatusBar Message
   */
  private int totTableRowCount;

  /** Editor instance. */
  private final A2LContentsEditor editor;

  /** Non scrollable form. */
  private Form nonScrollableForm;

  /** The sys constant filter grid layer. */
  private CustomFilterGridLayer sysConstantFilterGridLayer;

  /** The nat table. */
  private CustomNATTable natTable;

  /** The all column filter matcher. */
  private AllColumnFilterMatcher<A2LSystemConstantValues> allColumnFilterMatcher;

  /** The reset state. */
  private boolean resetState;

  /** The property to label map. */
  private Map<Integer, String> propertyToLabelMap;

  /** The a 2 l contents editor input. */
  private final A2LContentsEditorInput a2lContentsEditorInput;

  /**
   * Instantiates a new sys const nat form page.
   *
   * @param editor the editor
   */
  public A2LSysConstPage(final FormEditor editor) {
    super(editor, "Parameters", Messages.getString("SysConstFormPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = (A2LContentsEditor) editor;
    this.a2lContentsEditorInput = (A2LContentsEditorInput) (editor.getEditorInput());
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
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // add listeners
    getSite().getPage().addSelectionListener(this);
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
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {

    GridData gridData = getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("System Constants");
    this.section.setExpanded(true);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkit);
    this.section.setLayoutData(gridData);
    this.section.setClient(this.baseForm);
  }

  /**
   * Creates the form.
   *
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    this.baseForm = toolkit.createForm(this.section);
    this.filterTxt = toolkit.createText(this.baseForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    this.baseForm.getBody().setLayout(new GridLayout());

    createTable();


  }

  /**
   * Creates the table.
   */
  private void createTable() {
    CDMLogger.getInstance().debug("Finding system constants in A2L file");
    Set<SystemConstant> sysConstSet = this.a2lContentsEditorInput.getA2lFileInfoBO().getSystemConstants();
    CDMLogger.getInstance().debug("No. of system constants in A2L file = {}", sysConstSet.size());
    // ICDM-205
    SortedSet<A2LSystemConstantValues> newListSysConst =
        this.a2lContentsEditorInput.getA2lFileInfoBO().getSystemConstantDetails(sysConstSet);

    this.totTableRowCount = newListSysConst.size();
    this.propertyToLabelMap = new HashMap<>();
    this.propertyToLabelMap.put(0, "System Constant Name");
    this.propertyToLabelMap.put(1, "Long Name");
    this.propertyToLabelMap.put(2, "Value");
    this.propertyToLabelMap.put(3, "Value Description");

    // The below map is used by NatTable to Map Columns with their respective widths
    Map<Integer, Integer> columnWidthMap = new HashMap<>();
    columnWidthMap.put(0, 25);
    columnWidthMap.put(1, 30);
    columnWidthMap.put(2, 15);
    columnWidthMap.put(3, 30);

    // NatInputToColumnConverter is used to convert A2LSystemConstantValues (which is given as input to nattable viewer)
    // to the respective column values
    AbstractNatInputToColumnConverter natInputToColumnConverter = new SysConstNatInputToColumnConverter();

    IConfigRegistry configRegistry = new ConfigRegistry();

    // A Custom Filter Grid Layer is constructed
    this.sysConstantFilterGridLayer =
        new CustomFilterGridLayer(configRegistry, newListSysConst, this.propertyToLabelMap, columnWidthMap,
            getNameComparator(SortColumns.SORT_NAME), natInputToColumnConverter, this, null, true, true);


    this.allColumnFilterMatcher = new AllColumnFilterMatcher<>(this.a2lContentsEditorInput.getA2lFileInfoBO());
    this.sysConstantFilterGridLayer.getFilterStrategy().setAllColumnFilterMatcher(this.allColumnFilterMatcher);

    this.natTable = new CustomNATTable(this.baseForm.getBody(),
        SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL |
            SWT.V_SCROLL | SWT.H_SCROLL,
        this.sysConstantFilterGridLayer, false, this.getClass().getSimpleName(), this.propertyToLabelMap);

    try {
      this.natTable.setProductVersion(new CommonDataBO().getIcdmVersion());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    this.natTable.setLayoutData(getGridData());
    this.natTable.addConfiguration(new CustomNatTableStyleConfiguration());
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
    this.natTable.addConfiguration(new SingleClickSortConfiguration());
    this.natTable
        .addConfiguration(getCustomComparatorConfiguration(this.sysConstantFilterGridLayer.getColumnHeaderDataLayer()));
    this.natTable.setConfigRegistry(configRegistry);
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {


      /**
       * {@inheritDoc}
       */
      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(A2LSysConstPage.this.natTable).withColumnChooserMenuItem()
                .withMenuItemProvider((final NatTable natTableLocal, final Menu popupMenu) -> {
                  MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
                  menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
                  menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
                  menuItem.setEnabled(true);
                  menuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(final SelectionEvent event) {
                      A2LSysConstPage.this.resetState = true;
                      A2LSysConstPage.this.reconstructNatTable();
                    }
                  });
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });

    // Custom table header style
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

    this.sysConstantFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    this.sysConstantFilterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));

    CustomDefaultBodyLayerStack bodyLayer = this.sysConstantFilterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            this.sysConstantFilterGridLayer.getColumnHeaderLayer(),
            this.sysConstantFilterGridLayer.getColumnHeaderDataLayer(), null, null);
    this.natTable.registerCommandHandler(columnChooserCommandHandler);
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
    this.natTable.addDisposeListener(event ->
    // save state
    saveState());

    this.sysConstantFilterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(this.natTable));

    // TODO: Need to implement property selection listener to SelectionProviderMediator

    RowSelectionProvider<A2LSystemConstantValues> selectionProvider =
        new RowSelectionProvider<>(this.sysConstantFilterGridLayer.getBodyLayer().getSelectionLayer(),
            this.sysConstantFilterGridLayer.getBodyDataProvider(), false);

    selectionProvider.addSelectionChangedListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      if (selection.getFirstElement() instanceof A2LSystemConstantValues) {
        IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
        if (viewPart != null) {
          PropertySheet propertySheet = (PropertySheet) viewPart;
          IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
          if (page != null) {
            page.selectionChanged(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(), selection);
          }
        }
      }
    });

    // The below method is required to enable tootltip only for cells which contain not fully visible content
    attachToolTip(this.natTable);
    addHelpAction((ToolBarManager) getToolBarManager());
    addResetAllFiltersAction();
    // ICDM-886
    getSite().setSelectionProvider(selectionProvider);

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.natTable.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(selectionProvider,
        this.sysConstantFilterGridLayer.getBodyLayer().getSelectionLayer(), this.natTable));
  }

  /**
   * Reconstruct nat table.
   */
  public void reconstructNatTable() {


    this.natTable.dispose();
    this.propertyToLabelMap.clear();
    this.sysConstantFilterGridLayer = null;
    if (this.nonScrollableForm.getToolBarManager() != null) {
      this.nonScrollableForm.getToolBarManager().removeAll();
    }
    createTable();
    // First the form's body is repacked and then the section is repacked
    // Packing in the below manner prevents the disappearance of Filter Field and refreshes the natTable
    this.baseForm.getBody().pack();
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
   * input for status line.
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   */
  // ICDM-343
  public void setStatusBarMessage(final boolean outlineSelection) {
    if (this.natTable != null) {
      this.editor.updateStatusBar(outlineSelection, this.totTableRowCount, this.natTable.getPreferredRowCount() - 3);
    }
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
   * This method creates filter text.
   */
  private void createFilterTxt() {
    GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      String text = A2LSysConstPage.this.filterTxt.getText().trim();
      A2LSysConstPage.this.allColumnFilterMatcher.setFilterText(text, true);
      A2LSysConstPage.this.sysConstantFilterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
      A2LSysConstPage.this.sysConstantFilterGridLayer.getSortableColumnHeaderLayer().fireLayerEvent(
          new FilterAppliedEvent(A2LSysConstPage.this.sysConstantFilterGridLayer.getSortableColumnHeaderLayer()));
      setStatusBarMessage(false);
    });
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
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (part instanceof OutlineViewPart) &&
        (getEditor().getActivePage() == 6)) {
      setStatusBarMessage(true);
    }


  }


  /**
   * The Class FilterRowCustomConfiguration.
   */
  private static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

    /** The double display converter. */
    final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

    /**
     * {@inheritDoc}
     */
    public void configureRegistry(final IConfigRegistry configRegistry) {
      // override the default filter row configuration for painter
      configRegistry.registerConfigAttribute(CELL_PAINTER,
          new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);


      // TODO: Below four lines can be removed. To be checked
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

    /**
     * Gets the ignorecase comparator.
     *
     * @return the ignorecase comparator
     */
    private static Comparator<?> getIgnorecaseComparator() {
      return (final String obj1, final String obj2) -> obj1.compareToIgnoreCase(obj2);
    }
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
        labelAccumulator.registerColumnOverrides(0, CUSTOM_COMPARATOR_LABEL + 0);

        labelAccumulator.registerColumnOverrides(1, CUSTOM_COMPARATOR_LABEL + 1);

        labelAccumulator.registerColumnOverrides(2, CUSTOM_COMPARATOR_LABEL + 2);

        labelAccumulator.registerColumnOverrides(3, CUSTOM_COMPARATOR_LABEL + 3);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNameComparator(SortColumns.SORT_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 0);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getNameComparator(SortColumns.SORT_LONG_NAME), NORMAL, CUSTOM_COMPARATOR_LABEL + 1);

        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getValueOrValueDescComparator(A2LSystemConstantValues.SortColumns.SORT_VALUE), NORMAL,
            CUSTOM_COMPARATOR_LABEL + 2);


        configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR,
            getValueOrValueDescComparator(A2LSystemConstantValues.SortColumns.SORT_VALUE_DESC), NORMAL,
            CUSTOM_COMPARATOR_LABEL + 3);

        // Register null comparator to disable sort
      }
    };
  }


  /**
   * Gets the value or value desc comparator.
   *
   * @param sortColumns the sort columns
   * @return the value or value desc comparator
   */
  private static Comparator<A2LSystemConstantValues> getValueOrValueDescComparator(
      final A2LSystemConstantValues.SortColumns sortColumns) {
    return (final A2LSystemConstantValues val1, final A2LSystemConstantValues val2) -> val1.compareTo(val2,
        sortColumns);
  }

  /**
   * Gets the name comparator.
   *
   * @param sortColumns the sort columns
   * @return the name comparator
   */
  public static Comparator<A2LSystemConstantValues> getNameComparator(final A2LSystemConstant.SortColumns sortColumns) {
    return (final A2LSystemConstantValues val1, final A2LSystemConstantValues val2) -> {
      if (sortColumns.equals(A2LSystemConstant.SortColumns.SORT_LONG_NAME)) {
        return ModelUtil.compare(val1.getSysconLongName(), val2.getSysconLongName());
      }
      return ModelUtil.compare(val1.getSysconName(), val2.getSysconName());
    };
  }

  /**
   * Enables tootltip only for cells which contain not fully visible content.
   *
   * @param natTable the nat table
   */
  private void attachToolTip(final NatTable natTable) {
    DefaultToolTip toolTip = new ExampleNatTableToolTip(natTable);
    toolTip.setPopupDelay(0);
    toolTip.activate();
    toolTip.setShift(new Point(10, 10));
  }

  /**
   * The Class ExampleNatTableToolTip.
   */
  private class ExampleNatTableToolTip extends DefaultToolTip {

    /** The nat table. */
    private final NatTable natTable;

    /**
     * Instantiates a new example nat table tool tip.
     *
     * @param natTable the nat table
     */
    public ExampleNatTableToolTip(final NatTable natTable) {
      super(natTable, ToolTip.NO_RECREATE, false);
      this.natTable = natTable;
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
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);

      return new Point(col, row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getText(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
      return (String) cellByPosition.getDataValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldCreateToolTip(final Event event) {
      int col = this.natTable.getColumnPositionByX(event.x);
      int row = this.natTable.getRowPositionByY(event.y);
      ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
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

      GC gcObj = new GC(this.natTable);
      Point size = gcObj.stringExtent(cellValue);
      return (currentBounds.width < size.x);
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
   * Add reset filter button ICDM-1207.
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.sysConstantFilterGridLayer);
    addResetFiltersAction();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();

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
      CDMLogger.getInstance().warn("Failed to load A2L System constants nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

  /**
   * Save current state for the NAT table.
   */
  private void saveState() {
    try {
      this.natTable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save A2L System constants nat table state", ioe, Activator.PLUGIN_ID);
    }

  }

}
