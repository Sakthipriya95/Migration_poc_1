/**
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.IOException;
import java.util.Comparator;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.EMRNatPage;
import com.bosch.caltool.apic.ui.table.filters.EMRColFilterMatcher;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.nattable.CustomColumnHeaderLayerConfiguration;
import com.bosch.caltool.nattable.CustomColumnHeaderStyleConfiguration;
import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;
import com.bosch.caltool.nattable.configurations.CustomNatTableStyleConfiguration;
import com.bosch.caltool.nattable.configurations.FilterRowCustomConfiguration;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;

/**
 * The Class EMRNatPageConfig.
 *
 * @author gge6cob
 */
public class EMRNatPageConfig {


  /** The nat table. */
  private CustomNATTable natTable;

  /** The filter grid layer. */
  private CustomFilterGridLayer<EmrFileMapping> filterGridLayer;

  /** The reset state. */
  private boolean resetState;

  /** The page. */
  private EMRNatPage emrNatPage;

  private Label pageDescLbl;

  /**
   * Adds the nattable config.
   *
   * @param emrNatPage1 the page
   * @param natTable the nat table
   * @param filterGridLayer the filter grid layer
   * @param configRegistry the config registry
   * @param numCols the num cols
   */
  public void addNattableConfig(final EMRNatPage emrNatPage1, final CustomNATTable natTable,
      final CustomFilterGridLayer<EmrFileMapping> filterGridLayer, final ConfigRegistry configRegistry,
      final long numCols) {

    this.emrNatPage = emrNatPage1;
    this.natTable = natTable;
    this.filterGridLayer = filterGridLayer;
    // Add style Configurations
    natTable.setConfigRegistry(configRegistry);
    natTable.setLayoutData(getGridData());
    CustomNatTableStyleConfiguration natTabConfig = new CustomNatTableStyleConfiguration();
    natTabConfig.setEnableAutoResize(true);
    natTable.addConfiguration(natTabConfig);
    natTable.addConfiguration(new FilterRowCustomConfiguration(numCols) {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry1) {
        super.configureRegistry(configRegistry1);

        // Shade the row to be slightly darker than the blue background.
        final Style rowStyle = new Style();
        rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
        configRegistry1.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, NORMAL, FILTER_ROW);
      }
    });

    // Header config
    natTable.addConfiguration(new SingleClickSortConfiguration());
    addHeaderConfig();
    addHeaderStyle();

    // Column Comparator
    natTable.addConfiguration(getCustomComparatorConfiguration(filterGridLayer.getColumnHeaderDataLayer()));

    // Configure NAT table
    natTable.configure();

    // Load the saved state of NAT table
    loadState();

    // Column chooser configuration
    CustomDefaultBodyLayerStack bodyLayer = filterGridLayer.getBodyLayer();
    DisplayColumnChooserCommandHandler columnChooserCommandHandler =
        new DisplayColumnChooserCommandHandler(bodyLayer.getSelectionLayer(), bodyLayer.getColumnHideShowLayer(),
            filterGridLayer.getColumnHeaderLayer(), filterGridLayer.getColumnHeaderDataLayer(), null, null);
    natTable.registerCommandHandler(columnChooserCommandHandler);

    attachToolTip(natTable);

    filterGridLayer.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(natTable));

    // Save state of NAT table
    addTableListeners();

    // Mouse listener to tract row&column position
    this.natTable.addMouseListener(new MouseEventListener());
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
   * @return the pageDescLbl
   */
  public final Label getPageDescLbl() {
    return this.pageDescLbl;
  }

  /**
   * Adds the table listeners.
   */
  private void addTableListeners() {
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
    this.natTable.addDisposeListener(new DisposeListener() {

      @Override
      public void widgetDisposed(final DisposeEvent event) {
        // save state
        saveState();
      }
    });
  }

  /**
   * Save current state for the NAT table.
   */
  private void saveState() {
    try {
      this.natTable.saveState();
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().warn("Failed to save Codex nat table state", ioe, Activator.PLUGIN_ID);
    }
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
      CDMLogger.getInstance().warn("Failed to load CODEX nat table state", ioe,
          com.bosch.calcomp.adapter.logger.Activator.PLUGIN_ID);
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

      private static final String CUSTOM_COMPARATOR_LABEL = "customComparatorLabel";

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // Add label accumulator
        ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
        columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);
        // Register labels
        for (int i = 0; i <= EMRNatPage.IS_LOADED_WTO_ERRORS_COLNUM; i++) {
          labelAccumulator.registerColumnOverrides(i, CUSTOM_COMPARATOR_LABEL + i);
          configRegistry.registerConfigAttribute(SortConfigAttributes.SORT_COMPARATOR, getColumnComparator(i), NORMAL,
              CUSTOM_COMPARATOR_LABEL + i);
        }
      }
    };
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
   * Adds the header config.
   */
  private void addHeaderConfig() {
    this.natTable.addConfiguration(new HeaderMenuConfiguration(this.natTable) {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
            new MouseEventMatcher(SWT.NONE, GridRegion.COLUMN_HEADER, MouseEventMatcher.RIGHT_BUTTON),
            new PopupMenuAction(super.createColumnHeaderMenu(EMRNatPageConfig.this.natTable).withColumnChooserMenuItem()
                .withMenuItemProvider(new IMenuItemProvider() {

                  @Override
                  public void addMenuItem(final NatTable natTable1, final Menu popupMenu) {
                    resetMenuItem(popupMenu);
                  }
                }).build()));
        super.configureUiBindings(uiBindingRegistry);
      }
    });
  }

  /**
   * Resets menu reset state.
   *
   * @param popupMenu menu
   */
  public void resetMenuItem(final Menu popupMenu) {
    MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
    menuItem.setText(CommonUIConstants.NATTABLE_RESET_STATE);
    menuItem.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.REFRESH_16X16));
    menuItem.setEnabled(true);
    menuItem.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        EMRNatPageConfig.this.resetState = true;
      }
    });
  }

  /**
   * Adds the header style.
   */
  private void addHeaderStyle() {
    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration() {

      @Override
      public void configureRegistry(final IConfigRegistry configRegistry) {
        // configure the painter
        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.bacgrndColor);
        cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.foregrndColor);
        cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, this.gradintBgColor);
        cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, this.gradintFgColor);
        cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, this.horizontalAlign);
        cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, this.verticalAlign);
        cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.bordrStyle);
        cellStyle.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.NONE)));
        setHeaderCellStyleAttributes(configRegistry, this.cellPaintr, this.rendrGridLines, cellStyle);
      }
    };
    this.filterGridLayer.getColumnHeaderLayer()
        .addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));
  }

  /**
   * Sets the header cell style attributes.
   *
   * @param configRegistry the config registry
   * @param cellPaintr the cell paintr
   * @param rendrGridLines the rendr grid lines
   * @param cellStyle the cell style
   */
  private void setHeaderCellStyleAttributes(final IConfigRegistry configRegistry, final ICellPainter cellPaintr,
      final Boolean rendrGridLines, final Style cellStyle) {
    // configure the painter
    configRegistry.registerConfigAttribute(CELL_PAINTER, cellPaintr, NORMAL, GridRegion.COLUMN_HEADER);
    configRegistry.registerConfigAttribute(CELL_PAINTER, cellPaintr, NORMAL, GridRegion.CORNER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, rendrGridLines, NORMAL,
        GridRegion.COLUMN_HEADER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, rendrGridLines, NORMAL,
        GridRegion.CORNER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL,
        GridRegion.COLUMN_HEADER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, NORMAL, GridRegion.CORNER);
  }

  /**
   * Gets the name comparator.
   *
   * @param columnNum the sort columns
   * @return the name comparator
   */
  public Comparator<EmrFileMapping> getColumnComparator(final int columnNum) {
    return new Comparator<EmrFileMapping>() {

      @Override
      public int compare(final EmrFileMapping cmpRowObj1, final EmrFileMapping cmpRowObj2) {
        return compareObjects(columnNum, cmpRowObj1, cmpRowObj2);
      }
    };
  }


  /**
   * Compare objects.
   *
   * @param columnNum the column num
   * @param cmpRowObj1 the cmp row obj 1
   * @param cmpRowObj2 the cmp row obj 2
   * @return the int
   */
  private int compareObjects(final int columnNum, final EmrFileMapping cmpRowObj1, final EmrFileMapping cmpRowObj2) {
    switch (columnNum) {
      case EMRNatPage.FILE_NAME_COLNUM:
        return ApicUtil.compare(cmpRowObj1.getEmrFile().getName(), cmpRowObj2.getEmrFile().getName());
      case EMRNatPage.FILE_DESC_COLNUM:
        return ApicUtil.compare(cmpRowObj1.getEmrFile().getDescription(), cmpRowObj2.getEmrFile().getDescription());
      case EMRNatPage.VARIANTS_COLNUM:
        return ApicUtil.compare(
            cmpRowObj1.getVariantSet().stream().map(PidcVariant::getName).reduce("", String::concat),
            cmpRowObj2.getVariantSet().stream().map(PidcVariant::getName).reduce("", String::concat));
      case EMRNatPage.UPLOAD_DATE_COLNUM:
        return ApicUtil.compareCalendar(DateUtil.dateToCalendar(cmpRowObj1.getEmrFile().getCreatedDate()),
            DateUtil.dateToCalendar(cmpRowObj2.getEmrFile().getCreatedDate()));
      case EMRNatPage.IS_DELETED_COLNUM:
        return ApicUtil.compare(cmpRowObj1.getEmrFile().getDeletedFlag(), cmpRowObj2.getEmrFile().getDeletedFlag());
      case EMRNatPage.IS_LOADED_WTO_ERRORS_COLNUM:
        return ApicUtil.compare(cmpRowObj1.getEmrFile().getLoadedWithoutErrorsFlag(),
            cmpRowObj2.getEmrFile().getLoadedWithoutErrorsFlag());
      default:
        return cmpRowObj1.compareTo(cmpRowObj2);
    }
  }

  /**
   * Sets the status bar msg.
   *
   * @param outlineSelection outline tree view selection
   */
  public void setStatusBarMsg(final boolean outlineSelection) {
    if ((this.natTable != null) && (this.filterGridLayer != null)) {
      int totalItemCount = this.emrNatPage.getTotalRowCount();
      int filteredItemCount = this.filterGridLayer.getRowHeaderLayer().getPreferredRowCount();
      final StringBuilder buf = new StringBuilder(40);
      buf.append("Displaying : ").append(filteredItemCount).append(" out of ").append(totalItemCount)
          .append(" records ");
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
        statusLine = this.emrNatPage.getEditor().getEditorSite().getActionBars().getStatusLineManager();
      }
      if (totalItemCount == filteredItemCount) {
        statusLine.setErrorMessage(null);
        statusLine.setMessage(buf.toString());
      }
      else {
        statusLine.setErrorMessage(buf.toString());
      }
      statusLine.update(true);
    }
  }


  /**
   * The listener interface for receiving mouseEvent events. The class that is interested in processing a mouseEvent
   * event implements this interface, and the object created with that class is registered with a component using the
   * component's <code>addMouseEventListener<code> method. When the mouseEvent event occurs, that object's appropriate
   * method is invoked.
   *
   * @see MouseEventEvent
   */
  private final class MouseEventListener implements MouseListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseUp(final MouseEvent mouseevent) {
      // NA
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDown(final MouseEvent event) {
      EMRNatPageConfig.this.emrNatPage.setCurrentRowIndex(LayerUtil.convertRowPosition(EMRNatPageConfig.this.natTable,
          EMRNatPageConfig.this.natTable.getRowPositionByY(event.y),
          EMRNatPageConfig.this.filterGridLayer.getBodyLayer()));
      EMRNatPageConfig.this.emrNatPage.setCurrentColIndex(LayerUtil.convertColumnPosition(
          EMRNatPageConfig.this.natTable, EMRNatPageConfig.this.natTable.getColumnPositionByX(event.x),
          EMRNatPageConfig.this.filterGridLayer.getBodyLayer()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDoubleClick(final MouseEvent mouseevent) {
      // NA
    }
  }

  /**
   * Creates Title description.
   *
   * @param comp the comp
   * @param isPidcVersionReadable flag to indicate whether user has read access or not
   */
  public void createTitleDescription(final Composite comp, final boolean isPidcVersionReadable) {
    this.pageDescLbl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    Font boldFont =
        new Font(this.pageDescLbl.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID));
    this.pageDescLbl.setFont(boldFont);
    // set visibility of the label based on read access
    this.pageDescLbl.setVisible(!isPidcVersionReadable);
  }

  /**
   * @param comp Composite
   */
  public void initializeTitleLabel(final Composite comp) {
    this.pageDescLbl = LabelUtil.getInstance().createLabel(comp,
        "Insufficent access rights. At least Read access to the PIDC is required.");
  }


  /**
   * Creates the filter txt.
   *
   * @param filterTxt the filter txt
   * @param filterGridLayer the filter grid layer
   * @param groupByHeaderLayer the group by header layer
   * @param allColumnFilterMatcher the all column filter matcher
   * @param emrNatPage the emr nat page
   */
  public void createFilterTxt(final Text filterTxt, final CustomFilterGridLayer<EmrFileMapping> filterGridLayer,
      final GroupByHeaderLayer groupByHeaderLayer, final EMRColFilterMatcher<EmrFileMapping> allColumnFilterMatcher,
      final EMRNatPage emrNatPage) {
    filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    filterTxt.setMessage("type filter text");
    filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = filterTxt.getText().trim();
        allColumnFilterMatcher.setFilterText(text, true);
        filterGridLayer.getFilterStrategy().applyFilterInAllColumns(text);
        filterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(filterGridLayer.getSortableColumnHeaderLayer()));
        emrNatPage.setStatusBarMessage(groupByHeaderLayer, false);
      }
    });
  }

}
