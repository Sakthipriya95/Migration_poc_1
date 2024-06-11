package com.bosch.caltool.nattable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.group.command.ColumnGroupExpandCollapseCommand;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.event.HideColumnPositionsEvent;
import org.eclipse.nebula.widgets.nattable.hideshow.event.ShowColumnPositionsEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;


/**
 * @author jvi6cob
 */
public class CustomColumnGroupExpandCollapseCommandHandler extends
    AbstractLayerCommandHandler<ColumnGroupExpandCollapseCommand> {

  private final ColumnGroupExpandCollapseLayer colGrpExpandCollapseLayer;
  private final ColumnHideShowLayer colHideShowLayer;


  /**
   * @param colGrupExpandCollapseLayer ColumnGroupExpandCollapseLayer instance
   * @param colHideShowLayer ColumnHideShowLayer instance
   */
  public CustomColumnGroupExpandCollapseCommandHandler(final ColumnGroupExpandCollapseLayer colGrupExpandCollapseLayer,
      final ColumnHideShowLayer colHideShowLayer) {
    this.colGrpExpandCollapseLayer = colGrupExpandCollapseLayer;
    this.colHideShowLayer = colHideShowLayer;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<ColumnGroupExpandCollapseCommand> getCommandClass() {
    Class<ColumnGroupExpandCollapseCommand> classObj = ColumnGroupExpandCollapseCommand.class;
    return classObj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doCommand(final ColumnGroupExpandCollapseCommand commandObj) {

    ColumnGroup columnGrp = getColConfig(commandObj);

    // if group of columnIndex is not collapseable then just return
    if ((columnGrp == null) || !columnGrp.isCollapseable()) {
      return true;
    }

    List<Integer> colIndexes = new ArrayList<Integer>(columnGrp.getMembers());
    colIndexes.removeAll(columnGrp.getStaticColumnIndexes());


    boolean wasCollapsd = doIfCollapsed(columnGrp, colIndexes);

    columnGrp.toggleCollapsed();

    doIfNotCollapsed(colIndexes, wasCollapsd);

    ILayerEvent layerEvent = manipulateColsConfig(columnGrp, colIndexes, wasCollapsd);

    this.colGrpExpandCollapseLayer.fireLayerEvent(layerEvent);

    return true;
  }

  /**
   * @param columnGrp
   * @param colIndexes
   * @param wasCollapsd
   * @return
   */
  private ILayerEvent manipulateColsConfig(ColumnGroup columnGrp, List<Integer> colIndexes, boolean wasCollapsd) {
    List<Integer> colsToManipulateForSummary = new ArrayList<>();
    colsToManipulateForSummary.add(columnGrp.getMembers().get(columnGrp.getMembers().size() - 1));
    ILayerEvent layerEvent;
    if (wasCollapsd) {
      this.colHideShowLayer.hideColumnPositions(this.colHideShowLayer
          .getColumnPositionsByIndexes(colsToManipulateForSummary));
      layerEvent = new ShowColumnPositionsEvent(this.colGrpExpandCollapseLayer, colIndexes);

    }
    else {
      this.colHideShowLayer.showColumnIndexes(colsToManipulateForSummary);
      layerEvent = new HideColumnPositionsEvent(this.colGrpExpandCollapseLayer, colIndexes);
    }
    return layerEvent;
  }

  /**
   * @param commandObj
   * @return
   */
  private ColumnGroup getColConfig(final ColumnGroupExpandCollapseCommand commandObj) {
    int colIndex = this.colGrpExpandCollapseLayer.getColumnIndexByPosition(commandObj.getColumnPosition());
    ColumnGroupModel modelObj = this.colGrpExpandCollapseLayer.getModel(commandObj.getRowPosition());
    ColumnGroup columnGrp = modelObj.getColumnGroupByIndex(colIndex);
    return columnGrp;
  }

  /**
   * @param colIndexes
   * @param wasCollapsd
   */
  private void doIfNotCollapsed(final List<Integer> colIndexes, final boolean wasCollapsd) {
    if (!wasCollapsd) {
      // cleanup the column position list after we toggle
      // the columns are hidden now
      cleanupColumnIndexes(colIndexes);
    }
  }

  /**
   * @param columnGrp
   * @param colIndexes
   * @return
   */
  private boolean doIfCollapsed(final ColumnGroup columnGrp, final List<Integer> colIndexes) {
    boolean wasCollapsd = columnGrp.isCollapsed();

    if (wasCollapsd) {
      // cleanup the column position list before the toggle
      // the columns are hidden before the toggle and will be visible

      cleanupColumnIndexes(colIndexes);
    }
    return wasCollapsd;
  }

  /**
   * Cleans up the list of column indexes for a column group, so that those column indexes will stay in the list that
   * are relevant for the hide/show column events.
   * 
   * @param colIndexes The column indexes to cleanup.
   */
  private void cleanupColumnIndexes(final List<Integer> colIndexes) {
    for (Iterator<Integer> iterator = colIndexes.iterator(); iterator.hasNext();) {
      Integer colIndex = iterator.next();
      if (!this.colGrpExpandCollapseLayer.isColumnIndexHidden(colIndex)) {
        iterator.remove();
      }
    }
  }
}
