package com.bosch.caltool.cdr.ui.editors.pages;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.copy.InternalCellClipboard;
import org.eclipse.nebula.widgets.nattable.copy.InternalClipboardStructuralChangeListener;
import org.eclipse.nebula.widgets.nattable.copy.action.ClearClipboardAction;
import org.eclipse.nebula.widgets.nattable.copy.action.CopyDataAction;
import org.eclipse.nebula.widgets.nattable.copy.action.PasteDataAction;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.copy.command.InternalCopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.copy.command.InternalPasteDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.fillhandle.FillHandleLayerPainter;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.actions.ReviewResultNATActionSet;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author DMO5COB This class supports the copy paste functionality in ReviewResultEditor. Paste operation is supported
 *         only in the Comments column.
 */
public class ReviewResultCopyPasteConfiguration implements IConfiguration {


  /**
   * @author dmo5cob
   */
  private final class CommentsPasteAction extends PasteDataAction {

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final NatTable natTable, final KeyEvent event) {
      // check if the result is modifiable
      if (((ReviewResultEditorInput) (ReviewResultCopyPasteConfiguration.this.resultPage.getEditorInput()))
          .getResultData().getResultBo().isModifiable()) {
        // check if it is comments column
        if ((null != ReviewResultCopyPasteConfiguration.this.resultPage.getCustomFilterGridLayer()
            .getColumnHeaderDataLayer().getDataProvider()
            .getDataValue(ReviewResultCopyPasteConfiguration.this.resultPage.selectedColPostn, 0)) &&
            (ReviewResultCopyPasteConfiguration.this.resultPage.getCustomFilterGridLayer().getColumnHeaderDataLayer()
                .getDataProvider().getDataValue(ReviewResultCopyPasteConfiguration.this.resultPage.selectedColPostn, 0)
                .equals(ReviewResultParamListPage.getCommentHeaderLabel()))) {
          final IStructuredSelection selection =
              (IStructuredSelection) ReviewResultCopyPasteConfiguration.this.resultPage.getSelectionProvider()
                  .getSelection();
          final ReviewResultNATActionSet reviewActionSet = new ReviewResultNATActionSet();
          // invoke command to save comments to db
          reviewActionSet.setCommentsToResult(selection, ReviewResultCopyPasteConfiguration.this.resultPage);
          ReviewResultCopyPasteConfiguration.this.resultPage.getNatTable().redraw();
        }
        else {
          CDMLogger.getInstance().info("Paste operation not supported in this cell(s)", Activator.PLUGIN_ID);
          return;

        }
        super.run(natTable, event);
      }
      else {// if not modifiable ...show a message
        CDMLogger.getInstance().error("Insufficient priviledge to edit !", Activator.PLUGIN_ID);
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
  private final ReviewResultParamListPage resultPage;

  /**
   * @param selectionLayer SelectionLayer instance
   * @param clipboard InternalCellClipboard
   * @param formPage AbstractGroupByNatFormPage
   */
  public ReviewResultCopyPasteConfiguration(final SelectionLayer selectionLayer, final InternalCellClipboard clipboard,
      final AbstractGroupByNatFormPage formPage) {

    this.selectionLayer = selectionLayer;
    this.clipboard = clipboard;
    this.resultPage = (ReviewResultParamListPage) formPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
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
          }
        });
    // ui binding for a paste action on pressing CTRL+V
    uiBindingRegistry.registerFirstKeyBinding(
        new KeyEventMatcher(CommonUIConstants.KEY_CTRL, CommonUIConstants.KEY_PASTE), new CommentsPasteAction());
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