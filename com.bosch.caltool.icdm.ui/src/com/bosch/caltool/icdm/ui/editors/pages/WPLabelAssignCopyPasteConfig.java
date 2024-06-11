/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.copy.InternalCellClipboard;
import org.eclipse.nebula.widgets.nattable.copy.InternalClipboardStructuralChangeListener;
import org.eclipse.nebula.widgets.nattable.copy.action.ClearClipboardAction;
import org.eclipse.nebula.widgets.nattable.copy.action.CopyDataAction;
import org.eclipse.nebula.widgets.nattable.copy.action.PasteDataAction;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.copy.command.InternalCopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.copy.command.InternalPasteDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.fillhandle.FillHandleLayerPainter;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ui.Activator;

/**
 * @author apj4cob
 */
public class WPLabelAssignCopyPasteConfig implements IConfiguration {


  /**
   * @author dmo5cob
   */
  private final class WPNRespPasteAction extends PasteDataAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTable, final KeyEvent event) {
      if (WPLabelAssignCopyPasteConfig.this.wpLabelAssignPage.getA2lWPInfoBO().isA2lWpParamMapModifiable()) {
        final IStructuredSelection selection =
            (IStructuredSelection) WPLabelAssignCopyPasteConfig.this.wpLabelAssignPage.getSelectionProvider()
                .getSelection();
        // invoke command to save A2lWpParamMapping field to db
        WPLabelAssignCopyPasteConfig.this.wpLabelAssignPage.setCopiedA2lWpParamMappingField(selection);
        super.run(natTable, event);
      }
      else {// if not modifiable ...show a message
        CDMLogger.getInstance().error("Insufficient privilege to perform Paste operation !", Activator.PLUGIN_ID);
        return;
      }
    }
  }

  /**
   * SelectionLayer in nattable
   */
  private final SelectionLayer selectionLayer;

  /**
   * used to temporarily store copied cells for later paste actions. There is one instance created and referenced per
   * NatTable instance.
   */
  private final InternalCellClipboard clipboard;
  /**
   * Results form page
   */
  private final WPLabelAssignNatPage wpLabelAssignPage;

  /**
   * @param selectionLayer SelectionLayer instance
   * @param clipboard InternalCellClipboard
   * @param formPage AbstractGroupByNatFormPage
   */
  public WPLabelAssignCopyPasteConfig(final SelectionLayer selectionLayer, final InternalCellClipboard clipboard,
      final AbstractGroupByNatFormPage formPage) {

    this.selectionLayer = selectionLayer;
    this.clipboard = clipboard;
    this.wpLabelAssignPage = (WPLabelAssignNatPage) formPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // Nothing to do
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
    // Key binding for copy operation on pressing Ctrl + C
    uiBindingRegistry.registerFirstKeyBinding(
        new KeyEventMatcher(CommonUIConstants.KEY_CTRL, CommonUIConstants.KEY_COPY), new CopyDataAction() {

          /**
           * {@inheritDoc}
           */
          @Override
          public void run(final NatTable natTable, final KeyEvent event) {
            natTable.doCommand(new CopyDataToClipboardCommand("\t", System.getProperty("line.separator"),
                natTable.getConfigRegistry()));
            IStructuredSelection selection = (IStructuredSelection) WPLabelAssignCopyPasteConfig.this.wpLabelAssignPage
                .getSelectionProvider().getSelection();
            final A2LWpParamInfo copiedObj = (A2LWpParamInfo) (selection.getFirstElement());
            ICDMClipboard.getInstance().setCopiedObject(copiedObj);

          }
        });
    // ui binding for a paste action on pressing CTRL+V
    uiBindingRegistry.registerFirstKeyBinding(
        new KeyEventMatcher(CommonUIConstants.KEY_CTRL, CommonUIConstants.KEY_PASTE), new WPNRespPasteAction());
    // ui binding to clear the InternalCellClipboard
    uiBindingRegistry.registerFirstKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.ESC),
        new ClearClipboardAction(this.clipboard));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureLayer(final ILayer layer) {

    this.selectionLayer.addLayerListener(new InternalClipboardStructuralChangeListener(this.clipboard));

    // gives a border around copied cells
    if (!(this.selectionLayer.getLayerPainter() instanceof FillHandleLayerPainter)) {
      this.selectionLayer.setLayerPainter(new FillHandleLayerPainter(this.clipboard));
    }
    else {
      // to set the clipboard
      ((FillHandleLayerPainter) this.selectionLayer.getLayerPainter()).setClipboard(this.clipboard);
    }

    // set copy handlers
    layer.registerCommandHandler(new InternalCopyDataCommandHandler(this.selectionLayer, this.clipboard));
    // set paste handlers
    layer.registerCommandHandler(new InternalPasteDataCommandHandler(this.selectionLayer, this.clipboard));

  }

}
