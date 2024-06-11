package com.bosch.caltool.nattable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.columnRename.ColumnRenameDialog;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupUtils;
import org.eclipse.nebula.widgets.nattable.group.command.CreateColumnGroupCommand;
import org.eclipse.nebula.widgets.nattable.group.command.DisplayColumnGroupRenameDialogCommand;
import org.eclipse.nebula.widgets.nattable.group.command.IColumnGroupCommand;
import org.eclipse.nebula.widgets.nattable.group.command.OpenCreateColumnGroupDialog;
import org.eclipse.nebula.widgets.nattable.group.command.RemoveColumnGroupCommand;
import org.eclipse.nebula.widgets.nattable.group.command.ReorderColumnGroupCommand;
import org.eclipse.nebula.widgets.nattable.group.command.UngroupColumnCommand;
import org.eclipse.nebula.widgets.nattable.group.event.GroupColumnsEvent;
import org.eclipse.nebula.widgets.nattable.group.event.UngroupColumnsEvent;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;


/**
 * This class is to support CustomColumnGroupHeaderLayer class.</br>This class can be removed
 * CustomColumnGroupHeaderLayer extends ColumnGroupHeaderLayer
 * 
 * @author jvi6cob
 */
public class CustomColumnGroupsCommandHandler extends AbstractLayerCommandHandler<IColumnGroupCommand> {

  private final SelectionLayer selLayer;
  private final ColumnGroupModel colGrpModel;
  private Map<Integer, Integer> colIndxsToPostnsMap;
  private final CustomColumnGroupHeaderLayer contxtLayer;

  /**
   * @param colGrpModel ColumnGroupModel
   * @param selLayer SelectionLayer
   * @param contxtLayer CustomColumnGroupHeaderLayer
   */
  public CustomColumnGroupsCommandHandler(final ColumnGroupModel colGrpModel, final SelectionLayer selLayer,
      final CustomColumnGroupHeaderLayer contxtLayer) {
    this.selLayer = selLayer;
    this.contxtLayer = contxtLayer;
    this.colGrpModel = colGrpModel;
  }

  @Override
  public boolean doCommand(final IColumnGroupCommand cmd) {
    if (cmd instanceof OpenCreateColumnGroupDialog) {
      return onCreateColGrpDialog(cmd);
    }
    else if (cmd instanceof UngroupColumnCommand) {
      handleUngroupCmd();
      return true;
    }
    else if (cmd instanceof RemoveColumnGroupCommand) {
      return onRemoveColGrpCommand(cmd);
    }

    else if (cmd instanceof DisplayColumnGroupRenameDialogCommand) {
      return displayColumnGroupRenameDialog((DisplayColumnGroupRenameDialogCommand) cmd);
    }
    else if (cmd instanceof CreateColumnGroupCommand) {
      return onCreateColGrpCommand(cmd);
    }


    return false;
  }

  /**
   * @param cmd
   */
  private boolean onCreateColGrpCommand(final IColumnGroupCommand cmd) {
    if (!this.colIndxsToPostnsMap.isEmpty()) {
      handleGroupColumnsCommand(((CreateColumnGroupCommand) cmd).getColumnGroupName());
      this.colIndxsToPostnsMap.clear();
      return true;
    }
    return false;
  }

  /**
   * @param cmd
   * @return
   */
  private boolean onRemoveColGrpCommand(final IColumnGroupCommand cmd) {
    RemoveColumnGroupCommand removColumnGroupCommand = (RemoveColumnGroupCommand) cmd;
    int colIndex = removColumnGroupCommand.getColumnIndex();
    handleRemoveColumnGroupCommand(colIndex);
    return true;
  }

  /**
   * @param cmd
   * @return
   */
  private boolean onCreateColGrpDialog(final IColumnGroupCommand cmd) {
    OpenCreateColumnGroupDialog openDlgCommand = (OpenCreateColumnGroupDialog) cmd;
    loadSelectedColsIndexesWithPositions();
    if ((this.colIndxsToPostnsMap.size() > 0) && (getTheFullySelColPositions().length > 0)) {
      openDlgCommand.openDialog(this.contxtLayer);
    }
    else {
      openDlgCommand.openErrorBox(Messages.getString("ColumnGroups.selectNonGroupedColumns"));
    }
    return true;
  }

  private boolean displayColumnGroupRenameDialog(final DisplayColumnGroupRenameDialogCommand cmd) {
    int colPosition = cmd.getColumnPosition();

    ColumnRenameDialog dlg = openRenameDialog(colPosition, cmd);

    if (!dlg.isCancelPressed()) {
      int colIndex = this.contxtLayer.getColumnIndexByPosition(colPosition);
      ColumnGroup colGroup = this.colGrpModel.getColumnGroupByIndex(colIndex);
      colGroup.setName(dlg.getNewColumnLabel());
    }

    return true;
  }

  /**
   * @param cmd
   * @param colPosition
   * @return
   */
  private ColumnRenameDialog openRenameDialog(final int colPosition, final DisplayColumnGroupRenameDialogCommand cmd) {
    ColumnRenameDialog dlg = new ColumnRenameDialog(Display.getDefault().getActiveShell(), null, null);
    Rectangle colHeadrBounds = this.contxtLayer.getBoundsByPosition(colPosition, 0);
    Point pnt = new Point(colHeadrBounds.x, colHeadrBounds.y + colHeadrBounds.height);
    dlg.setLocation(cmd.toDisplayCoordinates(pnt));
    dlg.open();
    return dlg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<IColumnGroupCommand> getCommandClass() {
    Class<IColumnGroupCommand> classObj = IColumnGroupCommand.class;
    return classObj;
  }

  /**
   * 
   */
  protected void loadSelectedColsIndexesWithPositions() {
    this.colIndxsToPostnsMap = new LinkedHashMap<Integer, Integer>();
    int[] fullySelectedCols = getTheFullySelColPositions();

    if (fullySelectedCols.length > 0) {
      for (final int colPosition : fullySelectedCols) {
        int colIndex = this.selLayer.getColumnIndexByPosition(colPosition);
        if (this.colGrpModel.isPartOfAGroup(colIndex)) {
          this.colIndxsToPostnsMap.clear();
          break;
        }
        this.colIndxsToPostnsMap.put(Integer.valueOf(colIndex), Integer.valueOf(colPosition));
      }

    }
  }

  /**
   * @param colGrpName column group name
   */
  public void handleGroupColumnsCommand(final String colGrpName) {

    try {
      List<Integer> selPositions = new ArrayList<Integer>();
      int[] fullySelectedCols = new int[this.colIndxsToPostnsMap.size()];
      int counter = 0;
      for (Integer colIndex : this.colIndxsToPostnsMap.keySet()) {
        fullySelectedCols[counter++] = colIndex.intValue();
        selPositions.add(this.colIndxsToPostnsMap.get(colIndex));
      }
      this.colGrpModel.addColumnsIndexesToGroup(colGrpName, fullySelectedCols);
      this.selLayer
          .doCommand(new MultiColumnReorderCommand(this.selLayer, selPositions, selPositions.get(0).intValue()));
      this.selLayer.clear();
    }
    // TODO:Previously catch was Throwable need to analyse this since this is code from NattAble Devs also catch is not
    // required
    catch (Exception exp) {
      // TODO:
    }
    this.contxtLayer.fireLayerEvent(new GroupColumnsEvent(this.contxtLayer));
  }


  /**
   * 
   */
  public void handleUngroupCommand() {
    // Grab fully selected column positions
    int[] fullySelectedCols = getTheFullySelColPositions();
    Map<String, Integer> toColPositions = new HashMap<String, Integer>();
    if (fullySelectedCols.length > 0) {
      removeFromGroup(fullySelectedCols, toColPositions);
      reOrderToStart(toColPositions);
      this.selLayer.clear();
    }

    this.contxtLayer.fireLayerEvent(new UngroupColumnsEvent(this.contxtLayer));
  }


  /**
   * @return
   */
  private int[] getTheFullySelColPositions() {
    return this.selLayer.getFullySelectedColumnPositions();
  }

  private void handleRemovalFromGroup(final Map<String, Integer> toColumnPositions, final int colIndex) {
    ColumnGroup columnGroup = this.colGrpModel.getColumnGroupByIndex(colIndex);

    final String colGroupName = columnGroup.getName();
    final List<Integer> colIndexesInGroup = columnGroup.getMembers();
    final int colGroupSize = colIndexesInGroup.size();
    if (!toColumnPositions.containsKey(colGroupName)) {
      for (int colGroupIndex : colIndexesInGroup) {
        if (ColumnGroupUtils.isFirstVisibleColumnIndexInGroup(colGroupIndex, this.contxtLayer, this.selLayer,
            this.colGrpModel)) {
          int toPositn = this.selLayer.getColumnPositionByIndex(colGroupIndex);
          if (colGroupIndex == colIndex) {
            if (colGroupSize == 1) {
              break;
            }
            toPositn++;
          }
          toColumnPositions.put(colGroupName, Integer.valueOf(toPositn));
          break;
        }
      }
    }
    else {
      if ((colGroupSize - 1) <= 0) {
        toColumnPositions.remove(colGroupName);
      }
    }
    columnGroup.removeColumn(colIndex);
  }

  private void handleRemoveColumnGroupCommand(final int colIndex) {
    ColumnGroup colGroup = this.colGrpModel.getColumnGroupByIndex(colIndex);
    this.colGrpModel.removeColumnGroup(colGroup);
  }

  private void handleUngroupCmd() {
    handleUngroupCommand();
  }

  /**
   * @param toColPositions
   */
  private void reOrderToStart(final Map<String, Integer> toColPositions) {
    // The groups which were affected should be reordered to the start position, this should group all columns
    // together
    Collection<Integer> values = toColPositions.values();
    final Iterator<Integer> toColumnPositionsIterator = values.iterator();
    while (toColumnPositionsIterator.hasNext()) {
      Integer toColumnPosition = toColumnPositionsIterator.next();
      this.selLayer.doCommand(new ReorderColumnGroupCommand(this.selLayer, toColumnPosition.intValue(),
          toColumnPosition.intValue()));
    }
  }

  /**
   * @param fullySelectedCols
   * @param toColPositions
   */
  private void removeFromGroup(final int[] fullySelectedCols, final Map<String, Integer> toColPositions) {
    // Pick the ones which belong to a group and remove them from the group
    for (final int colPosition : fullySelectedCols) {
      int colIndex = this.selLayer.getColumnIndexByPosition(colPosition);
      if (this.colGrpModel.isPartOfAGroup(colIndex) && !this.colGrpModel.isPartOfAnUnbreakableGroup(colIndex)) {
        handleRemovalFromGroup(toColPositions, colIndex);
      }
    }
  }

}
