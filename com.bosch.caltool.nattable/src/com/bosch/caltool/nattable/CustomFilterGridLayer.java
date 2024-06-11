package com.bosch.caltool.nattable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.ComboBoxFilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByTreeFormat;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.ComboBoxFilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.ComboBoxFilterRowConfiguration;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.FilterRowComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.FilterRowComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.command.ColumnGroupExpandCollapseCommand;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.resize.action.AutoResizeColumnAction;
import org.eclipse.nebula.widgets.nattable.resize.action.ColumnResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.event.ColumnResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.resize.mode.ColumnResizeDragMode;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.action.ClearCursorAction;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.nebula.widgets.nattable.ui.action.NoOpMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import com.bosch.caltool.nattable.configurations.CustomGridLayerConfiguration;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.TreeList;


/**
 * The <code>CustomFilterGridLayer</code> is a Custom Top level layer. It is composed of the smaller child layers:
 * RowHeader, ColumnHeader, Corner and Body It does not have its own coordinate system unlike the other layers. It
 * simply delegates most functions to its child layers. </br>
 * </br>
 * Contains implementations of sorting and filtering
 *
 * @author jvi6cob
 * @param <T> NatTable Input data type
 */
public class CustomFilterGridLayer<T> extends GridLayer {


  /**
   * Font size of header
   */
  private static final int HEADER_FONT_SIZE = 9;
  /**
   * default column width multiplier
   */
  private static final int DEFAULT_COL_WIDTH_CONST = 10;
  private final CustomGlazedListsDataProvider<T> bodyDataProvidr;
  private final CustomGlazedListsFilterStrategy<T> filterStratgy;
  private final SortHeaderLayer<T> sortableColHeaderLayr;
  private final DefaultColumnHeaderDataLayer colHeaderDataLayr;
  private final FilterRowHeaderComposite<T> filtrRowHeaderLayr;
  private final ColumnGroupHeaderLayer colGrpHeaderLayer;
  private ColumnOverrideLabelAccumulator bodyLabelAccumultr;
  private CustomColumnHeaderDataProvider colHeaderDataProvidr;
  private CustomGroupByDataLayer<T> bodyDataLayr;
  private final CustomDefaultBodyLayerStack bodyLayr;
  private final ColumnHeaderLayer colHeaderLayr;

  // To de removed added for usecase editor
  // CDMBC-61
  // This is same as bodyDataLayer(CustomGroupByDataLayer).
  // Both these variables can be combined together of dataType DataLayer.
  // This above change requires changes in the Nattable users' code also
  private DataLayer dummyDataLayr;
  private final ColumnGroupModel colGrpModel;
  private final EventList<T> evntList;
  private FilterList<T> filtrList;
  private ColumnGroupModel colGroupGroupModel;
  private ColumnGroupGroupHeaderLayer colGroupGroupHeaderLayer;

  private TreeList.Format<T> treeFormat;
  private CustomComboGlazedListsFilterStrategy<T> comboFilterStratergy;
  private ComboBoxFilterRowHeaderComposite<T> filterRowHeaderLayer;
  private GlazedListsEventLayer<T> glazedListsEventLayer;

  /**
   * Constructor without grouping
   *
   * @param configRegstry IConfigRegistry
   * @param a2lSysConstVals Set<T>
   * @param propertyToLabelMap Map<Integer, String
   * @param colWidthMap Map<Integer, Integer>
   * @param sortedListComparator Comparator
   * @param natInputToColumnConverter AbstractNatInputToColumnConverter
   * @param natColumnFilterObserver INatColumnFilterObserver
   * @param mouseDoubleClickActn IMouseClickAction
   * @param selRowOnRightClick True for selecting row and false for selecting cell
   * @param isEnableReorder isEnableReorder
   */
  public CustomFilterGridLayer(final IConfigRegistry configRegstry, final Set<T> a2lSysConstVals,
      final Map<Integer, String> propertyToLabelMap, final Map<Integer, Integer> colWidthMap,
      final Comparator sortedListComparator, final AbstractNatInputToColumnConverter natInputToColumnConverter,
      final INatColumnFilterObserver natColumnFilterObserver, final IMouseClickAction mouseDoubleClickActn,
      final boolean selRowOnRightClick, final boolean isEnableReorder) {
    super(false);

    // TODO: Remove below line after merging constructors
    this.bodyLabelAccumultr = null;
    // Underlying data source
    this.evntList = GlazedLists.eventList(a2lSysConstVals);
    TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(this.evntList);
    SortedList<T> sortdList = new SortedList<T>(rowObjectsGlazedList, sortedListComparator);
    FilterList<T> filtrList = new FilterList<T>(sortdList);

    // Body layer
    IColumnAccessor<T> columnAccessor = new CustomColumnPropertyAccessor<T>(propertyToLabelMap.size());
    this.bodyDataProvidr = new CustomGlazedListsDataProvider<T>(filtrList, columnAccessor, natInputToColumnConverter);
    // CDMBC-61
    this.dummyDataLayr = new CustomDataLayer(this.bodyDataProvidr);
    for (Entry<Integer, Integer> colWidthEntry : colWidthMap.entrySet()) {
      this.dummyDataLayr.setColumnWidthPercentageByPosition(colWidthEntry.getKey(), colWidthEntry.getValue());
    }

    this.colGrpModel = new ColumnGroupModel();
    // ICDM-1595
    this.bodyLayr = new CustomDefaultBodyLayerStack(this.dummyDataLayr, this.colGrpModel, isEnableReorder, true);

    this.bodyLabelAccumultr = new ColumnOverrideLabelAccumulator(this.dummyDataLayr);
    this.dummyDataLayr.setConfigLabelAccumulator(this.bodyLabelAccumultr);

    // Column header layer
    this.colHeaderDataProvidr = new CustomColumnHeaderDataProvider(propertyToLabelMap);
    this.colHeaderDataLayr = new DefaultColumnHeaderDataLayer(this.colHeaderDataProvidr);
    this.colHeaderLayr =
        new ColumnHeaderLayer(this.colHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    this.sortableColHeaderLayr = new SortHeaderLayer<T>(this.colHeaderLayr,
        new GlazedListsSortModel<T>(sortdList, columnAccessor, null, configRegstry, this.colHeaderDataLayr), false);

    this.colGrpHeaderLayer =
        new ColumnGroupHeaderLayer(this.sortableColHeaderLayr, this.bodyLayr.getSelectionLayer(), this.colGrpModel);
    this.colGrpHeaderLayer.setCalculateHeight(true);

    this.filterStratgy = new CustomGlazedListsFilterStrategy<T>(filtrList, columnAccessor, configRegstry,
        natInputToColumnConverter, natColumnFilterObserver);
    this.filtrRowHeaderLayr = new FilterRowHeaderComposite<T>(this.filterStratgy, this.colGrpHeaderLayer,
        this.colHeaderDataProvidr, configRegstry);

    ColumnOverrideLabelAccumulator labelAccumulatr = new ColumnOverrideLabelAccumulator(this.colHeaderDataLayr);
    this.colHeaderDataLayr.setConfigLabelAccumulator(labelAccumulatr);

    // Row header layer
    DefaultRowHeaderDataProvider rowHeaderDataProvidr = new DefaultRowHeaderDataProvider(this.bodyDataProvidr);
    DefaultRowHeaderDataLayer rowHeaderDataLayr = new DefaultRowHeaderDataLayer(rowHeaderDataProvidr);
    // rowHeaderDataLayer.setDefaultColumnWidth(0) hides the row number column
    rowHeaderDataLayr
        .setDefaultColumnWidth(Integer.toString(a2lSysConstVals.size()).length() * DEFAULT_COL_WIDTH_CONST);
    RowHeaderLayer rowHeaderLayr =
        new RowHeaderLayer(rowHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    // The below configuration is added to rowheader to facilitate resizing of row number column
    // The resize icon appears near the cell and not on the column header.This is to be analysed
    rowHeaderLayr.addConfiguration(getRowHeaderResizeConfiguration());

    // Corner layer
    DefaultCornerDataProvider cornerDataProvidr =
        new DefaultCornerDataProvider(this.colHeaderDataProvidr, rowHeaderDataProvidr);
    DataLayer cornerDataLayr = new DataLayer(cornerDataProvidr);
    CornerLayer cornerLayr = new CornerLayer(cornerDataLayr, rowHeaderLayr, this.filtrRowHeaderLayr);

    // Grid
    setBodyLayer(this.bodyLayr);
    // Note: Set the filter row as the column header
    setColumnHeaderLayer(this.filtrRowHeaderLayr);
    setRowHeaderLayer(rowHeaderLayr);
    setCornerLayer(cornerLayr);

    addConfiguration(new CustomGridLayerConfiguration(this, mouseDoubleClickActn, true, selRowOnRightClick));


  }

  /**
   * Constructor with Grouping Enabled TODO:Code Refactoring and merge constructors(With and Without Group)
   *
   * @param configRegstry IConfigRegistry
   * @param tableInput Set<T>
   * @param propertyToLabelMap Map<Integer, String
   * @param columnWidthMap Map<Integer, Integer>
   * @param sortedListComparator Comparator
   * @param natInputToColConverter AbstractNatInputToColumnConverter
   * @param natColumnFilterObserver INatColumnFilterObserver
   * @param mouseDoubleClkAction IMouseClickAction
   * @param groupByModel Grouping
   * @param colsToHide columns to hide by default
   * @param colWidthByPercentage boolean
   * @param selRowOnRightClick True for selecting row and false for selecting cell
   * @param treeFormat treeformat instance to define parent child relationship
   * @param paramExpansionModel to define the expansion model for tree
   * @param addComboFilter add excel like filter in nattable header
   */
  public CustomFilterGridLayer(final IConfigRegistry configRegstry, final Collection<T> tableInput,
      final Map<Integer, String> propertyToLabelMap, final Map<Integer, Integer> columnWidthMap,
      final Comparator sortedListComparator, final AbstractNatInputToColumnConverter natInputToColConverter,
      final INatColumnFilterObserver natColumnFilterObserver, final IMouseClickAction mouseDoubleClkAction,
      final GroupByModel groupByModel, final List<Integer> colsToHide, final boolean colWidthByPercentage,
      final boolean selRowOnRightClick, final GroupByTreeFormat<T> treeFormat,
      final TreeList.ExpansionModel<T> paramExpansionModel, final boolean addComboFilter) {
    super(false);

    // Underlying data source
    this.evntList = GlazedLists.eventList(tableInput);
    TransformedList<T, T> rowObjsGlazedList = GlazedLists.threadSafeList(this.evntList);
    SortedList<T> sortedList = new SortedList<T>(rowObjsGlazedList, sortedListComparator);

    // instead of filter list we have to create tree list using new tree format class
    FilterList<T> filtrList = new FilterList<T>(sortedList);

    // Body layer
    IColumnAccessor<T> colAccessor = new CustomColumnPropertyAccessor<T>(propertyToLabelMap.size());

    CustomColumnPropertyAccessor modColAccessor = new CustomColumnPropertyAccessor<T>(propertyToLabelMap.size()) {

      @Override
      public Object getDataValue(final T type, final int colIndex) {
        return natInputToColConverter.getColumnValue(type, colIndex);
      }
    };

    configRegstry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_CHILD_COUNT_PATTERN, "[{0}] - ({1})");
    configRegstry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_HINT, "Drag columns here");

    this.colHeaderDataProvidr = new CustomColumnHeaderDataProvider(propertyToLabelMap);
    this.colHeaderDataLayr = new DefaultColumnHeaderDataLayer(this.colHeaderDataProvidr);


    // Column header layer

    GlazedListsSortModel<T> glazdListsSortModel =
        new GlazedListsSortModel<T>(sortedList, colAccessor, null, configRegstry, this.colHeaderDataLayr);


    this.bodyDataLayr = new CustomGroupByDataLayer<T>(groupByModel, filtrList, colAccessor, configRegstry, true, true,
        natInputToColConverter, treeFormat, paramExpansionModel, glazdListsSortModel);
    this.bodyDataProvidr = (CustomGlazedListsDataProvider<T>) this.bodyDataLayr.getDataProvider();
    this.colGrpModel = new ColumnGroupModel();
    this.bodyLayr = new CustomDefaultBodyLayerStack(this.bodyDataLayr, this.colGrpModel, filtrList, colsToHide);


    this.colHeaderLayr =
        new ColumnHeaderLayer(this.colHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());

    this.colGrpHeaderLayer =
        new ColumnGroupHeaderLayer(this.colHeaderLayr, this.bodyLayr.getSelectionLayer(), this.colGrpModel);
    this.colGrpHeaderLayer.setCalculateHeight(true);
    this.sortableColHeaderLayr = new SortHeaderLayer<T>(this.colGrpHeaderLayer, glazdListsSortModel, false);
    for (Entry<Integer, Integer> colWidthEntry : columnWidthMap.entrySet()) {
      if (colWidthByPercentage) {
        this.bodyDataLayr.setColumnWidthPercentageByPosition(colWidthEntry.getKey(), colWidthEntry.getValue());
      }
      else {
        this.bodyDataLayr.setColumnWidthByPosition(colWidthEntry.getKey(), colWidthEntry.getValue());
      }
    }


    this.bodyLabelAccumultr = new ColumnOverrideLabelAccumulator(this.bodyDataLayr);
    this.bodyDataLayr.setConfigLabelAccumulator(this.bodyLabelAccumultr);

    this.filterStratgy = new CustomGlazedListsFilterStrategy<T>(filtrList, colAccessor, configRegstry,
        natInputToColConverter, natColumnFilterObserver);
    this.filtrRowHeaderLayr = new FilterRowHeaderComposite<T>(this.filterStratgy, this.sortableColHeaderLayr,
        this.colHeaderDataProvidr, configRegstry);

    ColumnOverrideLabelAccumulator labelAccumulatr = new ColumnOverrideLabelAccumulator(this.colHeaderDataLayr);
    this.colHeaderDataLayr.setConfigLabelAccumulator(labelAccumulatr);
    this.filterRowHeaderLayer = null;

    // Row header layer
    if (addComboFilter) {

      FilterRowComboBoxDataProvider<T> comboBoxDataProvider =
          new FilterRowComboBoxDataProvider<>(this.bodyDataLayr, sortedList, modColAccessor);

      this.glazedListsEventLayer = new GlazedListsEventLayer<>(this.bodyDataLayr, filtrList);
      this.filterRowHeaderLayer =
          new ComboBoxFilterRowHeaderComposite<>(filtrList, this.glazedListsEventLayer, new ArrayList<>(),
              modColAccessor, false, this.sortableColHeaderLayr, this.colHeaderDataProvidr, configRegstry, false);

      this.comboFilterStratergy = new CustomComboGlazedListsFilterStrategy<T>(comboBoxDataProvider, filtrList,
          modColAccessor, configRegstry, natColumnFilterObserver, this.filterRowHeaderLayer.getMatcherEditor());


      this.filterRowHeaderLayer.addConfiguration(new ComboBoxFilterRowConfiguration() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void configureRegistry(final IConfigRegistry configRegistry) {
          super.configureRegistry(configRegistry);

          // Shade the row to be slightly darker than the blue background.
          final Style rowStyle = new Style();
          rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
          configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, DisplayMode.NORMAL,
              GridRegion.FILTER_ROW);
        }

        {
          this.cellEditor = new FilterRowComboBoxCellEditor(comboBoxDataProvider, 5);
          ((FilterRowComboBoxCellEditor) this.cellEditor).setShowDropdownFilter(true);
          this.filterIconPainter =
              new ComboBoxFilterIconPainter(comboBoxDataProvider, GUIHelper.getImage("filtered"), null);
        }
      });

    }

    DefaultRowHeaderDataProvider rowHeaderDataProvidr = new DefaultRowHeaderDataProvider(this.bodyDataProvidr);
    DefaultRowHeaderDataLayer rowHeaderDataLayr = new DefaultRowHeaderDataLayer(rowHeaderDataProvidr);
    // rowHeaderDataLayer.setDefaultColumnWidth(0) hides the row number column
    rowHeaderDataLayr.setDefaultColumnWidth(Integer.toString(tableInput.size()).length() * DEFAULT_COL_WIDTH_CONST);
    RowHeaderLayer rowHeaderLayr =
        new RowHeaderLayer(rowHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    // The below configuration is added to rowheader to facilitate resizing of row number column
    // The resize icon appears near the cell and not on the column header.This is to be analysed
    rowHeaderLayr.addConfiguration(getRowHeaderResizeConfiguration());
    // Corner layer
    DefaultCornerDataProvider cornerDataProvidr =
        new DefaultCornerDataProvider(this.colHeaderDataProvidr, rowHeaderDataProvidr);
    DataLayer cornerDataLayr = new DataLayer(cornerDataProvidr);
    CornerLayer cornerLayr;
    if (addComboFilter) {
      cornerLayr = new CornerLayer(cornerDataLayr, rowHeaderLayr, this.filterRowHeaderLayer);
      setColumnHeaderLayer(this.filterRowHeaderLayer);
    }
    else {
      cornerLayr = new CornerLayer(cornerDataLayr, rowHeaderLayr, this.filtrRowHeaderLayr);
      setColumnHeaderLayer(this.filtrRowHeaderLayr);
    }

    // Grid
    setBodyLayer(this.bodyLayr);
    // Note: Set the filter row as the column header

    setRowHeaderLayer(rowHeaderLayr);
    setCornerLayer(cornerLayr);

    addConfiguration(new CustomGridLayerConfiguration(this, mouseDoubleClkAction, true, selRowOnRightClick));


  }


  /**
   * Constructor for Label Based View
   *
   * @param configRegistry IConfigRegistry
   * @param a2lSysConstVals Set<T>
   * @param columnAccessor IColumnAccessor
   * @param modColumnAccessor IColumnAccessor
   * @param sortedListComparator Comparator
   * @param natInputToColumnConverter AbstractNatInputToColumnConverter
   * @param natColumnFilterObserver INatColumnFilterObserver
   * @param mouseDoubleClckAction IMouseClickAction
   * @param groupByModel GroupByModel
   * @param customColumnHeaderDataProvider CustomColumnHeaderDataProvider
   * @param seleRowOnRightClick True for selecting row and false for selecting cell
   */
  public CustomFilterGridLayer(final IConfigRegistry configRegistry, final Set<T> a2lSysConstVals,
      final IColumnAccessor<T> columnAccessor, final IColumnAccessor modColumnAccessor,
      final Comparator sortedListComparator, final AbstractNatInputToColumnConverter natInputToColumnConverter,
      final INatColumnFilterObserver natColumnFilterObserver, final IMouseClickAction mouseDoubleClckAction,
      final GroupByModel groupByModel, final CustomColumnHeaderDataProvider customColumnHeaderDataProvider,
      final boolean seleRowOnRightClick) {
    super(false);

    // Underlying data source
    this.evntList = GlazedLists.eventList(a2lSysConstVals);
    TransformedList<T, T> rowObjsGlazedList = GlazedLists.threadSafeList(this.evntList);
    SortedList<T> sortdList = new SortedList<T>(rowObjsGlazedList, sortedListComparator);
    FilterList<T> filtrList = new FilterList<T>(sortdList);
    this.colHeaderDataProvidr = customColumnHeaderDataProvider;
    this.colHeaderDataLayr = new DefaultColumnHeaderDataLayer(this.colHeaderDataProvidr);

    configRegistry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_CHILD_COUNT_PATTERN, "[{0}] - ({1})");
    configRegistry.registerConfigAttribute(GroupByConfigAttributes.GROUP_BY_HINT, "Drag columns here");


    // Column header layer

    GlazedListsSortModel<T> glazedListsSortModel =
        new GlazedListsSortModel<T>(sortdList, columnAccessor, null, configRegistry, this.colHeaderDataLayr);

    this.bodyDataLayr = new CustomGroupByDataLayer<T>(groupByModel, filtrList, modColumnAccessor, configRegistry, true,
        true, natInputToColumnConverter, null, null, glazedListsSortModel);
    this.bodyDataProvidr = (CustomGlazedListsDataProvider<T>) this.bodyDataLayr.getDataProvider();
    this.colGrpModel = new ColumnGroupModel();
    this.bodyLayr =
        new CustomDefaultBodyLayerStack(this.bodyDataLayr, this.colGrpModel, filtrList, new ArrayList<Integer>());
    this.colHeaderLayr =
        new ColumnHeaderLayer(this.colHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    this.bodyLabelAccumultr = new ColumnOverrideLabelAccumulator(this.bodyDataLayr);
    this.bodyDataLayr.setConfigLabelAccumulator(this.bodyLabelAccumultr);

    // Column header layer

    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration();
    this.colHeaderLayr.addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));


    this.colGrpHeaderLayer = null;
    CustomColumnGroupHeaderLayer customColumnGroupHeaderLayer =
        new CustomColumnGroupHeaderLayer(this.colHeaderLayr, this.bodyLayr.getSelectionLayer(), this.colGrpModel);
    customColumnGroupHeaderLayer.setCalculateHeight(true);

    this.sortableColHeaderLayr = new SortHeaderLayer<T>(customColumnGroupHeaderLayer, glazedListsSortModel, false);

    this.filterStratgy = new CustomGlazedListsFilterStrategy<T>(filtrList, columnAccessor, configRegistry,
        natInputToColumnConverter, natColumnFilterObserver);
    this.filtrRowHeaderLayr = new FilterRowHeaderComposite<T>(this.filterStratgy, this.sortableColHeaderLayr,
        this.colHeaderDataProvidr, configRegistry);

    ColumnOverrideLabelAccumulator labelAccumulatr = new ColumnOverrideLabelAccumulator(this.colHeaderDataLayr);
    this.colHeaderDataLayr.setConfigLabelAccumulator(labelAccumulatr);

    // Row header layer
    DefaultRowHeaderDataProvider rowHeaderDataProvidr = new DefaultRowHeaderDataProvider(this.bodyDataProvidr);
    DefaultRowHeaderDataLayer rowHeaderDataLayr = new DefaultRowHeaderDataLayer(rowHeaderDataProvidr);
    rowHeaderDataLayr
        .setDefaultColumnWidth(Integer.toString(a2lSysConstVals.size()).length() * DEFAULT_COL_WIDTH_CONST);
    RowHeaderLayer rowHeaderLayr =
        new RowHeaderLayer(rowHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    // The below configuration is added to rowheader to facilitate resizing of row number column
    // The resize icon appears near the cell and not on the column header.This is to be analysed
    rowHeaderLayr.addConfiguration(getRowHeaderResizeConfiguration());

    // Corner layer
    DefaultCornerDataProvider cornrDataProvider =
        new DefaultCornerDataProvider(this.colHeaderDataProvidr, rowHeaderDataProvidr);
    DataLayer cornrDataLayer = new DataLayer(cornrDataProvider);
    CornerLayer cornerLayr = new CornerLayer(cornrDataLayer, rowHeaderLayr, this.filtrRowHeaderLayr);

    // Grid
    setBodyLayer(this.bodyLayr);
    // Note: Set the filter row as the column header
    setColumnHeaderLayer(this.filtrRowHeaderLayr);
    setRowHeaderLayer(rowHeaderLayr);
    setCornerLayer(cornerLayr);

    addConfiguration(new CustomGridLayerConfiguration(this, mouseDoubleClckAction, true, seleRowOnRightClick));

  }


  /**
   * Constructor used specifically for UseCaseAttributes Editor TODO: Code refactoring
   *
   * @param configRegistry ConfigRegistry
   * @param inputData data for NatTable
   * @param columnWidthMap Map with Fixed width for columns
   * @param columnAccessor Column Accessor
   * @param columnHeaderDataProvider Column header data provider
   * @param sortedListComparator Comparator for sorting
   * @param natInputToColumnConverter Converter for column data
   * @param natColumnFilterObserver NatColumnFilterObserver usually the pages in which the nattable is implemented for
   *          statusbar update
   * @param mouseDoubleClckAction Mouseaction
   * @param enableColumnReorder flag to enable column reordering ICDM-1595
   * @param enableColumnGroupReorder flag to enable columnGroup reordering ICDM-1595
   * @param selRowOnRightClick True for selecting row and false for selecting cell
   */
  public CustomFilterGridLayer(final IConfigRegistry configRegistry, final Set<T> inputData,
      final Map<Integer, Integer> columnWidthMap, final IColumnAccessor columnAccessor,
      final IDataProvider columnHeaderDataProvider, final Comparator sortedListComparator,
      final AbstractNatInputToColumnConverter natInputToColumnConverter,
      final INatColumnFilterObserver natColumnFilterObserver, final IMouseClickAction mouseDoubleClckAction,
      final boolean enableColumnReorder, final boolean enableColumnGroupReorder, final boolean selRowOnRightClick) {

    super(false);

    // Underlying data source
    this.evntList = GlazedLists.eventList(inputData);
    TransformedList<T, T> rowObjsGlazedList = GlazedLists.threadSafeList(this.evntList);
    SortedList<T> sortedList = new SortedList<T>(rowObjsGlazedList, sortedListComparator);
    this.filtrList = new FilterList<T>(sortedList);

    // Body layer
    this.bodyDataProvidr =
        new CustomGlazedListsDataProvider<T>(this.filtrList, columnAccessor, natInputToColumnConverter);
    this.dummyDataLayr = new DataLayer(this.bodyDataProvidr);
    for (Entry<Integer, Integer> columnWidthEntry : columnWidthMap.entrySet()) {
      this.dummyDataLayr.setColumnWidthByPosition(columnWidthEntry.getKey(), columnWidthEntry.getValue());
    }
    this.colGrpModel = new ColumnGroupModel();
    this.bodyLayr = new CustomDefaultBodyLayerStack(this.dummyDataLayr, this.colGrpModel, enableColumnReorder,
        enableColumnGroupReorder);
    // unregister and register
    ColumnGroupExpandCollapseLayer colGroupExpandCollapseLayer = this.bodyLayr.getColumnGroupExpandCollapseLayer();
    ColumnHideShowLayer columnHideShowLayr = this.bodyLayr.getColumnHideShowLayer();
    colGroupExpandCollapseLayer.unregisterCommandHandler(ColumnGroupExpandCollapseCommand.class);
    colGroupExpandCollapseLayer.registerCommandHandler(
        new CustomColumnGroupExpandCollapseCommandHandler(colGroupExpandCollapseLayer, columnHideShowLayr));

    this.bodyLabelAccumultr = new ColumnOverrideLabelAccumulator(this.dummyDataLayr);
    this.dummyDataLayr.setConfigLabelAccumulator(this.bodyLabelAccumultr);

    // Column header layer
    this.colHeaderDataLayr = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
    this.colHeaderLayr =
        new ColumnHeaderLayer(this.colHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    CustomColumnHeaderStyleConfiguration columnHeaderStyleConfiguration = new CustomColumnHeaderStyleConfiguration();
    columnHeaderStyleConfiguration.font = GUIHelper.getFont(new FontData("Segoe UI", HEADER_FONT_SIZE, SWT.NONE));
    this.colHeaderLayr.addConfiguration(new CustomColumnHeaderLayerConfiguration(columnHeaderStyleConfiguration));
    GlazedListsSortModel<T> glazedListsSortModel =
        new GlazedListsSortModel<T>(sortedList, columnAccessor, null, configRegistry, this.colHeaderDataLayr);
    this.colGrpHeaderLayer =
        new ColumnGroupHeaderLayer(this.colHeaderLayr, this.bodyLayr.getSelectionLayer(), this.colGrpModel);
    this.colGrpHeaderLayer.setCalculateHeight(true);
    this.colGroupGroupModel = new ColumnGroupModel();
    this.colGroupGroupHeaderLayer = new ColumnGroupGroupHeaderLayer(this.colGrpHeaderLayer,
        this.bodyLayr.getSelectionLayer(), this.colGroupGroupModel, false) {

      @Override
      // ICDM-1579
      public LabelStack getRegionLabelsByXY(final int xPixel, final int yPixel) {
        int columnIndex = getColumnIndexByPosition(getColumnPositionByX(xPixel));
        if (CustomFilterGridLayer.this.colGroupGroupModel.isPartOfAGroup(columnIndex) &&
            (yPixel < getRowHeightByPosition(0))) {
          return new LabelStack("COLUMN_GROUP_GROUP_HEADER");
        }
        return CustomFilterGridLayer.this.colGrpHeaderLayer.getRegionLabelsByXY(xPixel,
            yPixel - getRowHeightByPosition(0));
      }
    };
    this.sortableColHeaderLayr = new SortHeaderLayer<T>(this.colGroupGroupHeaderLayer, glazedListsSortModel, false);

    this.filterStratgy = new CustomGlazedListsFilterStrategy<T>(this.filtrList, columnAccessor, configRegistry,
        natInputToColumnConverter, natColumnFilterObserver);
    this.filtrRowHeaderLayr = new FilterRowHeaderComposite<T>(this.filterStratgy, this.sortableColHeaderLayr,
        columnHeaderDataProvider, configRegistry);

    ColumnOverrideLabelAccumulator labelAccumulatr = new ColumnOverrideLabelAccumulator(this.colHeaderDataLayr);
    this.colHeaderDataLayr.setConfigLabelAccumulator(labelAccumulatr);

    // Row header layer
    DefaultRowHeaderDataProvider rowHeaderDataProvidr = new DefaultRowHeaderDataProvider(this.bodyDataProvidr);
    DefaultRowHeaderDataLayer rowHeaderDataLayr = new DefaultRowHeaderDataLayer(rowHeaderDataProvidr);
    // rowHeaderDataLayer.setDefaultColumnWidth(0) hides the row number column
    rowHeaderDataLayr.setDefaultColumnWidth(Integer.toString(inputData.size()).length() * DEFAULT_COL_WIDTH_CONST);
    RowHeaderLayer rowHeaderLayr =
        new RowHeaderLayer(rowHeaderDataLayr, this.bodyLayr, this.bodyLayr.getSelectionLayer());
    // The below configuration is added to rowheader to facilitate resizing of row number column
    // The resize icon appears near the cell and not on the column header.This is to be analysed
    rowHeaderLayr.addConfiguration(getRowHeaderResizeConfiguration());

    // Corner layer
    DefaultCornerDataProvider cornerDataProvidr =
        new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvidr);
    DataLayer cornerDataLayr = new DataLayer(cornerDataProvidr);
    CornerLayer cornerLayr = new CornerLayer(cornerDataLayr, rowHeaderLayr, this.filtrRowHeaderLayr);

    // Grid
    setBodyLayer(this.bodyLayr);
    // Note: Set the filter row as the column header
    setColumnHeaderLayer(this.filtrRowHeaderLayr);
    setRowHeaderLayer(rowHeaderLayr);
    setCornerLayer(cornerLayr);

    addConfiguration(new CustomGridLayerConfiguration(this, mouseDoubleClckAction, true, selRowOnRightClick));

  }

  @Override
  public void setClientAreaProvider(final IClientAreaProvider clientAreaProvidr) {
    super.setClientAreaProvider(clientAreaProvidr);
  }


  /**
   * @return the bodyDataProvider
   */
  public CustomGlazedListsDataProvider<T> getBodyDataProvider() {
    return this.bodyDataProvidr;
  }


  /**
   * @return the filterStrategy
   */
  public CustomGlazedListsFilterStrategy<T> getFilterStrategy() {
    return this.filterStratgy;
  }


  /**
   * @return the sortableColumnHeaderLayer
   */
  public SortHeaderLayer<T> getSortableColumnHeaderLayer() {
    return this.sortableColHeaderLayr;
  }


  /**
   * @return the columnHeaderDataLayer
   */
  public DefaultColumnHeaderDataLayer getColumnHeaderDataLayer() {
    return this.colHeaderDataLayr;
  }


  /**
   * @return the filterRowHeaderLayer
   */
  public FilterRowHeaderComposite<T> getFilterRowHeaderLayer() {
    return this.filtrRowHeaderLayr;
  }


  /**
   * @return the columnGroupHeaderLayer
   */
  public ColumnGroupHeaderLayer getColumnGroupHeaderLayer() {
    return this.colGrpHeaderLayer;
  }


  /**
   * @return the bodyLabelAccumulator
   */
  public ColumnOverrideLabelAccumulator getBodyLabelAccumulator() {
    return this.bodyLabelAccumultr;
  }


  /**
   * @return the columnHeaderDataProvider
   */
  public CustomColumnHeaderDataProvider getColumnHeaderDataProvider() {
    return this.colHeaderDataProvidr;
  }


  /**
   * @return the bodyDataLayer
   */
  public CustomGroupByDataLayer<T> getBodyDataLayer() {
    return this.bodyDataLayr;
  }


  /**
   * @return the bodyLayer
   */
  @Override
  public CustomDefaultBodyLayerStack getBodyLayer() {
    return this.bodyLayr;
  }


  /**
   * @return the columnHeaderLayer
   */
  @Override
  public ColumnHeaderLayer getColumnHeaderLayer() {
    return this.colHeaderLayr;
  }


  /**
   * @return the dummyDataLayer
   */
  public DataLayer getDummyDataLayer() {
    return this.dummyDataLayr;
  }


  /**
   * @return the columnGroupModel
   */
  public ColumnGroupModel getColumnGroupModel() {
    return this.colGrpModel;
  }


  /**
   * @return the eventList
   */
  public EventList<T> getEventList() {
    return this.evntList;
  }


  /**
   * @return the columnGroupGroupModel
   */
  public ColumnGroupModel getColumnGroupGroupModel() {
    return this.colGroupGroupModel;
  }

  /**
   * The below configuration is added to rowheader to facilitate resizing of row number column The resize icon appears
   * near the cell and not on the column header.This is to be analysed
   *
   * @return
   */
  private AbstractUiBindingConfiguration getRowHeaderResizeConfiguration() {
    return new AbstractUiBindingConfiguration() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        // Mouse move - Show resize cursor
        uiBindingRegistry.registerFirstMouseMoveBinding(
            new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 0), new ColumnResizeCursorAction());
        uiBindingRegistry.registerMouseMoveBinding(new MouseEventMatcher(), new ClearCursorAction());

        // Column resize
        uiBindingRegistry.registerFirstMouseDragMode(new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 1),
            new ColumnResizeDragMode());

        uiBindingRegistry.registerDoubleClickBinding(new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 1),
            new AutoResizeColumnAction());
        uiBindingRegistry.registerSingleClickBinding(new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 1),
            new NoOpMouseAction());

      }
    };
  }

  /**
   * @param filterRowHeaderLayer the filterRowHeaderLayer to set
   */
  public void setFilterRowHeaderLayer(final ComboBoxFilterRowHeaderComposite<T> filterRowHeaderLayer) {
    this.filterRowHeaderLayer = filterRowHeaderLayer;
  }

  /**
   * @return the filterStrat
   */
  public CustomComboGlazedListsFilterStrategy<T> getComboGlazedListsFilterStrategy() {
    return this.comboFilterStratergy;
  }

  /**
   * @return the filterRowHeaderLayer
   */
  public ComboBoxFilterRowHeaderComposite<T> getComboBoxFilterRowHeaderLayer() {
    return this.filterRowHeaderLayer;
  }

}
