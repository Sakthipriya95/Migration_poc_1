package com.bosch.caltool.nattable;

import java.util.List;
import java.util.Properties;

import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupUtils;
import org.eclipse.nebula.widgets.nattable.group.IColumnGroupModelListener;
import org.eclipse.nebula.widgets.nattable.group.config.DefaultColumnGroupHeaderLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.SizeConfig;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.TransformedLayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;


/**
 * This class overrides <b>getDataValueByPosition and getCellByPosition</b> methods to allow multiple rows in column
 * header. </br> But overriding <b>getCellByPosition</b> method prevents resize of ungrouped columns when cursor is
 * placed at second of ungrouped columns near grouped columns
 * 
 * @author jvi6cob
 */
public class CustomColumnGroupHeaderLayer extends AbstractLayerTransform {

  /**
   * 
   */
  private static final int ROW_NOS_2 = 2;
  private final SizeConfig rowHightConfig = new SizeConfig(DataLayer.DEFAULT_ROW_HEIGHT);
  private final ILayer colHeadrLyr;
  private final ColumnGroupModel colGrpModel;

  /**
   * Flag which is used to tell the ColumnGroupHeaderLayer whether to calculate the height of the layer dependent on
   * column group configuration or not. If it is set to <code>true</code> the column header will check if column groups
   * are configured and if not, the height of the column header will not show the double height for showing column
   * groups.
   */
  private boolean calcHeight;

  /**
   * Listener that will fire a RowStructuralRefreshEvent when the ColumnGroupModel changes. Its only needed when the
   * dynamic height calculation is enabled.
   */
  private final IColumnGroupModelListener modelChangeListnr;

  /**
   * @param colHeaderLayer ILayer instance
   * @param selLayer SelectionLayer instance
   * @param colGrpModel ColumnGroupModel instance
   */
  public CustomColumnGroupHeaderLayer(final ILayer colHeaderLayer, final SelectionLayer selLayer,
      final ColumnGroupModel colGrpModel) {
    this(colHeaderLayer, selLayer, colGrpModel, true);
  }

  /**
   * @param colHeaderLayer ILayer
   * @param selLayer SelectionLayer
   * @param colGrpModel ColumnGroupModel
   * @param useDefaultConfign boolean
   */
  public CustomColumnGroupHeaderLayer(final ILayer colHeaderLayer, final SelectionLayer selLayer,
      final ColumnGroupModel colGrpModel, final boolean useDefaultConfign) {
    super(colHeaderLayer);

    this.colHeadrLyr = colHeaderLayer;
    this.colGrpModel = colGrpModel;

    addConfig(selLayer, colGrpModel, useDefaultConfign);

    this.modelChangeListnr = new IColumnGroupModelListener() {

      @Override
      public void columnGroupModelChanged() {
        fireLayerEvent(new RowStructuralRefreshEvent(colHeaderLayer));
      }
    };

    this.colGrpModel.registerColumnGroupModelListener(this.modelChangeListnr);
  }

  /**
   * @param selLayer
   * @param colGrpModelObj
   * @param useDefaultConfign
   */
  private void addConfig(final SelectionLayer selLayer, final ColumnGroupModel colGrpModelObj,
      final boolean useDefaultConfign) {
    registerCommandHandler(new CustomColumnGroupsCommandHandler(this.colGrpModel, selLayer, this));

    if (useDefaultConfign) {
      addConfiguration(new DefaultColumnGroupHeaderLayerConfiguration(colGrpModelObj));
    }
  }

  // Save the state
  /**
   * {@inheritDoc}
   */
  @Override
  public void saveState(final String preFix, final Properties prop) {
    super.saveState(preFix, prop);
    this.colGrpModel.saveState(preFix, prop);
  }

  @Override
  public void loadState(final String preFix, final Properties prop) {
    super.loadState(preFix, prop);
    this.colGrpModel.loadState(preFix, prop);
    fireLayerEvent(new ColumnStructuralRefreshEvent(this));
  }

  // Vertical features
  // Rows
  @Override
  public int getRowCount() {
    if (!this.calcHeight ||
        ((this.colGrpModel.getAllIndexesInGroups() != null) && (!this.colGrpModel.getAllIndexesInGroups().isEmpty()))) {
      return this.colHeadrLyr.getRowCount() + 1;
    }
    return this.colHeadrLyr.getRowCount();
  }

  @Override
  public int getPreferredRowCount() {
    return this.colHeadrLyr.getPreferredRowCount() + 1;
  }

  @Override
  public int getRowIndexByPosition(final int rowPstn) {
    if (rowPstn == 0) {
      return rowPstn;
    }
    return this.colHeadrLyr.getRowIndexByPosition(rowPstn - 1);
  }

  @Override
  public int localToUnderlyingRowPosition(final int localRowPostn) {
    if (localRowPostn == 0) {
      return localRowPostn;
    }
    return localRowPostn - 1;
  }

  // Height

  @Override
  public int getHeight() {
    if (!this.calcHeight ||
        ((this.colGrpModel.getAllIndexesInGroups() != null) && (!this.colGrpModel.getAllIndexesInGroups().isEmpty()))) {
      return this.rowHightConfig.getAggregateSize(1) + this.colHeadrLyr.getHeight();
    }
    return this.colHeadrLyr.getHeight();
  }


  @Override
  public int getPreferredHeight() {
    return this.rowHightConfig.getAggregateSize(1) + this.colHeadrLyr.getPreferredHeight();
  }

  @Override
  public int getRowHeightByPosition(final int rowPostn) {
    if (rowPostn == 0) {
      return this.rowHightConfig.getSize(rowPostn);
    }
    return this.colHeadrLyr.getRowHeightByPosition(rowPostn - 1);
  }

  /**
   * @param rowHght int
   */
  public void setRowHeight(final int rowHght) {
    this.rowHightConfig.setSize(0, rowHght);
  }

  // Row resize
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRowPositionResizable(final int rowPostn) {
    if (rowPostn == 0) {
      return this.rowHightConfig.isPositionResizable(rowPostn);
    }
    return this.colHeadrLyr.isRowPositionResizable(rowPostn - 1);
  }

  // Y
  @Override
  public int getRowPositionByY(final int y_Index) {
    int row_0_Height = getRowHeightByPosition(0);
    if (y_Index < row_0_Height) {
      return 0;
    }
    return 1 + this.colHeadrLyr.getRowPositionByY(y_Index - row_0_Height);
  }

  @Override
  public int getStartYOfRowPosition(final int rowPostn) {
    if (rowPostn == 0) {
      return this.rowHightConfig.getAggregateSize(rowPostn);
    }
    return getRowHeightByPosition(0) + this.colHeadrLyr.getStartYOfRowPosition(rowPostn - 1);
  }

  // Cell features

  /**
   * If a cell belongs to a column group: column position - set to the start position of the group span - set to the
   * width/size of the column group NOTE: gc.setClip() is used in the CompositeLayerPainter to ensure that partially
   * visible Column group header cells are rendered properly.
   */
  @Override
  public ILayerCell getCellByPosition(final int colPostn, final int rowPostn) {
    int bodyColIndex = getColumnIndexByPosition(colPostn);

    // Column group header cell
    if (this.colGrpModel.isPartOfAGroup(bodyColIndex)) {
      if (rowPostn == 0) {
        return new LayerCell(this, getStartPositionOfGroup(colPostn), rowPostn, colPostn, rowPostn,
            getColumnSpan(colPostn), 1);
      }
      return new LayerCell(this, colPostn, rowPostn);
    }
    // render col header wrt rowspan = 2
    // for this case we ask the column header layer for the cell position
    // and the column header layer asks the data provider for the row count
    // which should always return 1 , we ask for row position 0 instead of using
    // getGroupHeaderRowPosition(), if we use getGroupHeaderRowPosition()
    // the ColumnGroupGroupHeaderLayer would not work .
    // Joel
    // the below if loop takes care that when rowcount is 1 the rowposition passed is always is 0 because of the
    // following case
    // When there are ungrouped columns(Attribute column in Usecase page) existing with grouped columns(All,none,Any
    // -> Select) the numbers of column header rows is visually 2 even thought according to
    // code(columnHeaderDataProvider getRowCount) it is 1
    // In the above case, when we hover the mouse over the right edge of the Attribute column header the icon for
    // resize doesnt appear because of a condition in
    // getCellByPosition method(rowPosition >= getRowCount() return null;) in ColumnHeaderLayer(AbstractLayer)
    // class.So this fails when columnHeaderDataProvider.getRowCount() is 1 since rowposition is 1(2nd row- 0 based)
    int tempRowPostn = 0;
    if (this.colHeadrLyr.getRowCount() > 1) {
      tempRowPostn = rowPostn;
    }
    ILayerCell layerCell = this.colHeadrLyr.getCellByPosition(colPostn, tempRowPostn);
    if (layerCell != null) {
      final int rowSpanObj;

      if (this.calcHeight && (this.colGrpModel.isEmpty())) {
        rowSpanObj = 1;
      }
      else {
        rowSpanObj = ROW_NOS_2;
      }

      layerCell = new TransformedLayerCell(layerCell) {

        @Override
        public ILayer getLayer() {
          CustomColumnGroupHeaderLayer classInstance = CustomColumnGroupHeaderLayer.this;
          return classInstance;
        }

        @Override
        public int getRowSpan() {
          return rowSpanObj;
        }
      };
    }
    return layerCell;
  }

  /**
   * Calculates the span of a cell in a Column Group. Takes into account collapsing and hidden columns in the group.
   * 
   * @param colPosition position of any column belonging to the group
   * @return int value
   */
  protected int getColumnSpan(final int colPosition) {
    int colIndex = getColumnIndexByPosition(colPosition);
    ColumnGroup columnGrp = this.colGrpModel.getColumnGroupByIndex(colIndex);

    int sizeOfGrp = columnGrp.getSize();

    if (columnGrp.isCollapsed()) {
      int sizeOfStaticCols = columnGrp.getStaticColumnIndexes().size();
      if (sizeOfStaticCols == 0) {
        return 1;
      }
      sizeOfGrp = sizeOfStaticCols;
    }

    int startPostnOfGroup = getStartPositionOfGroup(colPosition);
    int endPostnOfGroup = startPostnOfGroup + sizeOfGrp;
    List<Integer> colIndexesInGroup = columnGrp.getMembers();

    for (int i = startPostnOfGroup; i < endPostnOfGroup; i++) {
      int indexObj = getColumnIndexByPosition(i);
      if (!colIndexesInGroup.contains(Integer.valueOf(indexObj))) {
        sizeOfGrp--;
      }
    }
    return sizeOfGrp;
  }

  /**
   * Figures out the start position of the group.
   * 
   * @param selectionLayerColumnPosition of any column belonging to the group
   * @return first position of the column group
   */
  private int getStartPositionOfGroup(final int colPosition) {
    int bodyColIndex = getColumnIndexByPosition(colPosition);
    ColumnGroup colGroup = this.colGrpModel.getColumnGroupByIndex(bodyColIndex);

    int minStrtPosOfGroup = colPosition - (colGroup.getSize() - 1);
    int iIndexObj = 0;
    for (iIndexObj = minStrtPosOfGroup; iIndexObj < colPosition; iIndexObj++) {
      if (ColumnGroupUtils.isInTheSameGroup(getColumnIndexByPosition(iIndexObj), bodyColIndex, this.colGrpModel)) {
        break;
      }
    }
    return iIndexObj;
  }

  @Override
  public String getDisplayModeByPosition(final int colPosition, final int rowPstn) {
    int colIndex = getColumnIndexByPosition(colPosition);
    if ((rowPstn == 0) && this.colGrpModel.isPartOfAGroup(colIndex)) {
      return DisplayMode.NORMAL;
    }
    return this.colHeadrLyr.getDisplayModeByPosition(colPosition, rowPstn);
  }

  @Override
  public LabelStack getConfigLabelsByPosition(final int colPostn, final int rowPstn) {
    int colIndex = getColumnIndexByPosition(colPostn);
    if ((rowPstn == 0) && this.colGrpModel.isPartOfAGroup(colIndex)) {
      LabelStack stack = new LabelStack(GridRegion.COLUMN_GROUP_HEADER);

      if (this.colGrpModel.isPartOfACollapseableGroup(colIndex)) {
        ColumnGroup group = this.colGrpModel.getColumnGroupByIndex(colIndex);
        if (group.isCollapsed()) {
          stack.addLabelOnTop(DefaultColumnGroupHeaderLayerConfiguration.GROUP_COLLAPSED_CONFIG_TYPE);
        }
        else {
          stack.addLabelOnTop(DefaultColumnGroupHeaderLayerConfiguration.GROUP_EXPANDED_CONFIG_TYPE);
        }
      }

      return stack;
    }
    return this.colHeadrLyr.getConfigLabelsByPosition(colPostn, rowPstn);
  }

  @Override
  public Object getDataValueByPosition(final int columnPosition, final int rowPosition) {
    int columnIndex = getColumnIndexByPosition(columnPosition);
    if ((rowPosition == 0) && this.colGrpModel.isPartOfAGroup(columnIndex)) {
      return this.colGrpModel.getColumnGroupByIndex(columnIndex).getName();
    }
    return this.colHeadrLyr.getDataValueByPosition(columnPosition, rowPosition);
  }

  @Override
  public LabelStack getRegionLabelsByXY(final int xValue, final int yValue) {
    int columnIndex = getColumnIndexByPosition(getColumnPositionByX(xValue));
    if (this.colGrpModel.isPartOfAGroup(columnIndex) && (yValue < getRowHeightByPosition(0))) {
      return new LabelStack(GridRegion.COLUMN_GROUP_HEADER);
    }
    return this.colHeadrLyr.getRegionLabelsByXY(xValue, yValue - getRowHeightByPosition(0));
  }

  // ColumnGroupModel delegates

  /**
   * @param colGroupName String
   * @param colIndexes int..
   */
  public void addColumnsIndexesToGroup(final String colGroupName, final int... colIndexes) {
    this.colGrpModel.addColumnsIndexesToGroup(colGroupName, colIndexes);
  }

  /**
   * Clear all groups
   */
  public void clearAllGroups() {
    this.colGrpModel.clear();
  }

  /**
   * @param colGroupName String
   * @param staticColumnIndexes int...
   */
  public void setStaticColumnIndexesByGroup(final String colGroupName, final int... staticColumnIndexes) {
    this.colGrpModel.setStaticColumnIndexesByGroup(colGroupName, staticColumnIndexes);
  }

  /**
   * @param bodyColumnIndex int
   * @return boolean
   */
  public boolean isColumnInGroup(final int bodyColumnIndex) {
    return this.colGrpModel.isPartOfAGroup(bodyColumnIndex);
  }


  /**
   * @param columnIndex int
   */
  public void setGroupUnbreakable(final int columnIndex) {
    ColumnGroup columnGroup = this.colGrpModel.getColumnGroupByIndex(columnIndex);
    columnGroup.setUnbreakable(true);
  }

  /**
   * @param columnIndex int
   */
  public void setGroupAsCollapsed(final int columnIndex) {
    ColumnGroup columnGroup = this.colGrpModel.getColumnGroupByIndex(columnIndex);
    columnGroup.setCollapsed(true);
  }

  /**
   * @return boolean
   */
  public boolean isCalculateHeight() {
    return this.calcHeight;
  }

  /**
   * @param calculateHeight boolean
   */
  public void setCalculateHeight(final boolean calculateHeight) {
    this.calcHeight = calculateHeight;

    if (calculateHeight) {
      this.colGrpModel.registerColumnGroupModelListener(this.modelChangeListnr);
    }
    else {
      this.colGrpModel.unregisterColumnGroupModelListener(this.modelChangeListnr);
    }
  }

}
