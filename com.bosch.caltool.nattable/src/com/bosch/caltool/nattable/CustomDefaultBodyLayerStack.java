package com.bosch.caltool.nattable;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupReorderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractIndexLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWTException;

import ca.odell.glazedlists.FilterList;


/**
 * This class configures the layers required for Column Grouping and Freeze Features
 * 
 * @author jvi6cob
 */
public class CustomDefaultBodyLayerStack extends AbstractIndexLayerTransform {

  /**
   * ColumnReorderLayer
   */
  private final ColumnReorderLayer columnReorderLayer;
  /**
   * ColumnHideShowLayer
   */
  private final ColumnHideShowLayer columnHideShowLayer;
  /**
   * SelectionLayer
   */
  private final SelectionLayer selectionLayer;
  /**
   * ViewportLayer
   */
  private final ViewportLayer viewportLayer;
  /**
   * ColumnGroupReorderLayer
   */
  private final ColumnGroupReorderLayer columnGroupReorderLayer;
  /**
   * ColumnGroupExpandCollapseLayer
   */
  private final ColumnGroupExpandCollapseLayer columnGroupExpandCollapseLayer;
  /**
   * FreezeLayer
   */
  private final FreezeLayer freezeLayer;
  /**
   * CompositeFreezeLayer
   */
  private final CompositeFreezeLayer compositeFreezeLayer;
  /**
   * TreeLayer
   */
  private final TreeLayer treeLayer;

  /**
   * Constructor for grouping
   * 
   * @param bodyDataLayer IUniqueIndexLayer
   * @param columnGroupModel ColumnGroupModel
   * @param filterList FilterList<?>
   * @param colsToHide columns to be hidden by default
   */
  public CustomDefaultBodyLayerStack(final CustomGroupByDataLayer<?> bodyDataLayer,
      final ColumnGroupModel columnGroupModel, final FilterList<?> filterList, final List<Integer> colsToHide) {

    GlazedListsEventLayer<?> glazedListsEventLayer = new GlazedListsEventLayer(bodyDataLayer, filterList);
    this.columnReorderLayer = new ColumnReorderLayer(glazedListsEventLayer) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void handleLayerEvent(final ILayerEvent event) {
        try {
          super.handleLayerEvent(event);
        }
        catch (SWTException e) {
          // ICDM-1157
        }
      }
    };
    this.columnGroupReorderLayer = new ColumnGroupReorderLayer(this.columnReorderLayer, columnGroupModel);
    this.columnHideShowLayer = new ColumnHideShowLayer(this.columnGroupReorderLayer);
    if ((colsToHide != null) && !colsToHide.isEmpty()) {
      this.columnHideShowLayer.hideColumnPositions(colsToHide);
    }
    this.columnGroupExpandCollapseLayer =
        new ColumnGroupExpandCollapseLayer(this.columnHideShowLayer, columnGroupModel);
    this.selectionLayer = new SelectionLayer(this.columnGroupExpandCollapseLayer);
    this.treeLayer = new TreeLayer(this.selectionLayer, bodyDataLayer.getTreeRowModel());
    this.viewportLayer = new ViewportLayer(this.treeLayer);
    this.freezeLayer = new FreezeLayer(this.selectionLayer);
    this.compositeFreezeLayer = new CompositeFreezeLayer(this.freezeLayer, this.viewportLayer, this.selectionLayer);

    setUnderlyingLayer(this.compositeFreezeLayer);
    registerCommandHandler(new CopyDataCommandHandler(this.selectionLayer));
  }

  /**
   * Constructor without grouping. Currently used for SysConstNatFormPage and UseCaseNatAttributes Editor
   * 
   * @param underlyingLayer IUniqueIndexLayer
   * @param columnGroupModel ColumnGroupModel
   * @param useColumnReorderLayer flag to permit usage of columnReorder layer ICDM-1595
   * @param useColumnGroupReorderLayer flag to permit usage of columnGroupReorderlayer ICDM-1595
   */
  public CustomDefaultBodyLayerStack(final IUniqueIndexLayer underlyingLayer, final ColumnGroupModel columnGroupModel,
      final boolean useColumnReorderLayer, final boolean useColumnGroupReorderLayer) {
    this.treeLayer = null;

    this.columnReorderLayer = useColumnReorderLayer ? new ColumnReorderLayer(underlyingLayer) : null;
    this.columnGroupReorderLayer =
        useColumnGroupReorderLayer ? new ColumnGroupReorderLayer(useColumnReorderLayer ? this.columnReorderLayer
            : underlyingLayer, columnGroupModel) : null;
    this.columnHideShowLayer =
        new ColumnHideShowLayer(useColumnGroupReorderLayer ? this.columnGroupReorderLayer : useColumnReorderLayer
            ? this.columnReorderLayer : underlyingLayer);
    this.columnGroupExpandCollapseLayer =
        new ColumnGroupExpandCollapseLayer(this.columnHideShowLayer, columnGroupModel);
    this.selectionLayer = new SelectionLayer(this.columnGroupExpandCollapseLayer);
    this.viewportLayer = new ViewportLayer(this.selectionLayer);
    this.freezeLayer = new FreezeLayer(this.selectionLayer);
    this.compositeFreezeLayer = new CompositeFreezeLayer(this.freezeLayer, this.viewportLayer, this.selectionLayer);

    setUnderlyingLayer(this.compositeFreezeLayer);
    registerCommandHandler(new CopyDataCommandHandler(this.selectionLayer));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setClientAreaProvider(final IClientAreaProvider clientAreaProvider) {
    super.setClientAreaProvider(clientAreaProvider);
  }

  /**
   * @return ColumnReorderLayer
   */
  public ColumnReorderLayer getColumnReorderLayer() {
    return this.columnReorderLayer;
  }


  /**
   * @return the columnGroupReorderLayer
   */
  public ColumnGroupReorderLayer getColumnGroupReorderLayer() {
    return this.columnGroupReorderLayer;
  }

  /**
   * @return ColumnHideShowLayer
   */
  public ColumnHideShowLayer getColumnHideShowLayer() {
    return this.columnHideShowLayer;
  }

  /**
   * @return the columnGroupExpandCollapseLayer
   */
  public ColumnGroupExpandCollapseLayer getColumnGroupExpandCollapseLayer() {
    return this.columnGroupExpandCollapseLayer;
  }

  /**
   * @return SelectionLayer
   */
  public SelectionLayer getSelectionLayer() {
    return this.selectionLayer;
  }

  /**
   * @return ViewportLayer
   */
  public ViewportLayer getViewportLayer() {
    return this.viewportLayer;
  }


  /**
   * @return the freezeLayer
   */
  public FreezeLayer getFreezeLayer() {
    return this.freezeLayer;
  }


  /**
   * @return the compositeFreezeLayer
   */
  public CompositeFreezeLayer getCompositeFreezeLayer() {
    return this.compositeFreezeLayer;
  }


  /**
   * @return the treeLayer
   */
  public TreeLayer getTreeLayer() {
    return this.treeLayer;
  }

}
