/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.List;

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

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.common.ui.editors.AbstractNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author apj4cob
 */
public class ComparePidcCopyPasteConfig implements IConfiguration {

  private class ComparePidcCopyPasteAction extends PasteDataAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTable, final KeyEvent event) {
      final IStructuredSelection selection =
          (IStructuredSelection) ComparePidcCopyPasteConfig.this.comparePidcPage.getSelectionProvider().getSelection();
      if (selection.getFirstElement() instanceof CompareRowObject) {
        CompareRowObject compareRowObject = ((CompareRowObject) selection.getFirstElement());
        if (ComparePidcCopyPasteConfig.this.comparePidcPage.isValColItemSelected(compareRowObject)) {
          // check for access rights in has privelege
          if (ComparePidcCopyPasteConfig.this.comparePidcPage.hasPrivilegeToModifySelPidc(compareRowObject)) {
            // invoke command to save Attribute Value for destination Pidc in Db
            ComparePidcCopyPasteConfig.this.comparePidcPage.pasteAttrValInTargetPidc(selection);
            super.run(natTable, event);
          }
          else {// if not modifiable ...show a message
            CDMLogger.getInstance().error("Insufficient privilege to perform Paste operation !", Activator.PLUGIN_ID);
          }

        }
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
  private final ComparePIDCPage comparePidcPage;

  /**
   * @param selectionLayer SelectionLayer instance
   * @param clipboard InternalCellClipboard
   * @param formPage AbstractGroupByNatFormPage
   */
  public ComparePidcCopyPasteConfig(final SelectionLayer selectionLayer, final InternalCellClipboard clipboard,
      final AbstractNatFormPage formPage) {

    this.selectionLayer = selectionLayer;
    this.clipboard = clipboard;
    this.comparePidcPage = (ComparePIDCPage) formPage;
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
            IStructuredSelection selection = (IStructuredSelection) ComparePidcCopyPasteConfig.this.comparePidcPage
                .getSelectionProvider().getSelection();
            List<?> copiedObjList = selection.toList();
            ICDMClipboard.getInstance()
                .setCopiedObject(ComparePidcCopyPasteConfig.this.comparePidcPage.getCopiedProjAttrMap(copiedObjList));
          }
        });
    // ui binding for a paste action on pressing CTRL+V
    uiBindingRegistry.registerFirstKeyBinding(
        new KeyEventMatcher(CommonUIConstants.KEY_CTRL, CommonUIConstants.KEY_PASTE), new ComparePidcCopyPasteAction());
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

