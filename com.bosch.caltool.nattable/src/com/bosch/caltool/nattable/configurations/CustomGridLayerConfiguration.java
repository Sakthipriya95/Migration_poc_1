package com.bosch.caltool.nattable.configurations;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AggregateConfiguration;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.cell.AlternatingRowConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultRowStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.print.config.DefaultPrintBindings;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.action.SelectCellAction;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.action.ViewportSelectRowAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

import com.bosch.caltool.nattable.CustomDefaultBodyLayerStack;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNatExportBindings;


/**
 * @author jvi6cob
 */
public class CustomGridLayerConfiguration extends AggregateConfiguration {

  /**
   * Constructor
   * 
   * @param gridLayer CompositeLayer
   * @param mouseDoubleClickAction mouseclick action to be configured
   * @param enableEditing true to enable editing
   * @param selectRow true enables row selection on right click. false enables cell selection on right click
   */
  public CustomGridLayerConfiguration(final CompositeLayer gridLayer, final IMouseClickAction mouseDoubleClickAction,
      final boolean enableEditing, final boolean selectRow) {
    super();
    if (enableEditing) {
      addEditingHandlerConfig();
      addEditingUIConfig();
    }
    addPrintUIBindings();
    addExcelExportUIBindings();

    // Add right click
    if (selectRow) {
      addSelectRowOnRightClickConfiguration();
    }
    else {
      addSelectCellOnRightClickConfiguration();
    }
    if (mouseDoubleClickAction != null) {
      addDoubleClickBindings(mouseDoubleClickAction);
    }
  }

  /**
   * 
   */
  private void addSelectRowOnRightClickConfiguration() {
    addConfiguration(new AbstractUiBindingConfiguration() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegion.BODY,
            MouseEventMatcher.RIGHT_BUTTON),

        new IMouseAction() {

          ViewportSelectRowAction selectRowAction = new ViewportSelectRowAction(false, false);

          @Override
          public void run(final NatTable natTable, final MouseEvent event) {
            CompositeLayer compositeLayer = (CompositeLayer) natTable.getLayer();
            CustomFilterGridLayer customFilterGridLayer =
                (CustomFilterGridLayer) compositeLayer.getChildLayerByRegionName("Grid");
            SelectionLayer selectionLayer = null;
            if (customFilterGridLayer == null) {
              selectionLayer =
                  ((CustomDefaultBodyLayerStack) ((GridLayer) natTable.getLayer()).getBodyLayer()).getSelectionLayer();
            }
            else {
              selectionLayer = customFilterGridLayer.getBodyLayer().getSelectionLayer();
            }
            final int rowPosition =
                LayerUtil.convertRowPosition(natTable, natTable.getRowPositionByY(event.y), selectionLayer);

            if (!selectionLayer.isRowPositionSelected(rowPosition)) {
              this.selectRowAction.run(natTable, event);
            }
          }
        });

      }
    });
  }

  /**
   * 
   */
  private void addSelectCellOnRightClickConfiguration() {
    addConfiguration(new AbstractUiBindingConfiguration() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {

        uiBindingRegistry.registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegion.BODY,
            MouseEventMatcher.RIGHT_BUTTON),

        new IMouseAction() {

          SelectCellAction selectCellAction = new SelectCellAction();

          @Override
          public void run(final NatTable natTable, final MouseEvent event) {
            this.selectCellAction.run(natTable, event);
          }
        });

      }
    });
  }

  /**
   * @param mouseDoubleClickAction
   */
  private void addDoubleClickBindings(final IMouseClickAction mouseDoubleClickAction) {
    addConfiguration(new AbstractUiBindingConfiguration() {

      @Override
      public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerDoubleClickBinding(new MouseEventMatcher(SWT.NONE, GridRegion.BODY,
            MouseEventMatcher.LEFT_BUTTON), mouseDoubleClickAction);

      }
    });

  }

  /**
   * Method used to add CustomNatExportUIBindings This is required to change the default key binding for excel to
   * Ctrl+Alt+E
   */
  private void addExcelExportUIBindings() {
    addConfiguration(new CustomNatExportBindings());
  }

  /**
   * Method used to add DefaultPrintBindings
   */
  private void addPrintUIBindings() {
    addConfiguration(new DefaultPrintBindings());
  }

  /**
   * Method used to add DefaultEditBindings
   */
  private void addEditingUIConfig() {
    addConfiguration(new DefaultEditBindings());
  }

  /**
   * Method used to add DefaultEditConfiguration
   */
  private void addEditingHandlerConfig() {
    addConfiguration(new DefaultEditConfiguration());
  }

  /**
   * Method used to add DefaultRowStyleConfiguration
   * 
   * @param gridLayer CompositeLayer
   */
  protected void addAlternateRowColoringConfig(final CompositeLayer gridLayer) {
    addConfiguration(new DefaultRowStyleConfiguration());
    gridLayer.setConfigLabelAccumulatorForRegion(GridRegion.BODY, new AlternatingRowConfigLabelAccumulator());
  }

}
