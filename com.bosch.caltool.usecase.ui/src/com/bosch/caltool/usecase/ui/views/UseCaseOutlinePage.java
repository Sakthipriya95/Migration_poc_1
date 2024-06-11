/**
 *
 */
package com.bosch.caltool.usecase.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AttrGroupClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrSuperGroupClientBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;

/**
 * This class is for the outline page of the usecase editor
 *
 * @author adn1cob
 */
public class UseCaseOutlinePage extends AbstractPage {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Composite
   */
  private Composite composite;
  /**
   * Tree viewer
   */
  private TreeViewer viewer;
  private final OutLineViewDataHandler outLineViewDataHandler;

  /**
   * Constructor
   *
   * @param outLineViewDataHandler Outline View Data Handler
   */
  public UseCaseOutlinePage(final OutLineViewDataHandler outLineViewDataHandler) {
    this.outLineViewDataHandler = outLineViewDataHandler;
  }

  @Override
  public void createControl(final Composite parent) {
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    addHelpAction();
    // Build the UI
    createComposite();
    // hook context menu
    hookContextMenu();
    // adding drag and drop listeners
    addDragDropListeners();// ICDM-1035
    // add selection provider
    getSite().setSelectionProvider(this.viewer);
  }

  // ICDM-766
  /**
   * Build the context menu for tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(UseCaseOutlinePage.this::fillContextMenu);
    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);


  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {
    final CommonActionSet actionSet = new CommonActionSet();
    // Get the current selection and add actions to it
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      if (selection.getFirstElement() instanceof IUseCaseItemClientBO) {
        // add link action in case the object is usecase or usecase section
        if (selection.getFirstElement() instanceof UseCaseSectionClientBO) {
          actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) selection.getFirstElement()).getLinks());
        }
        else if (selection.getFirstElement() instanceof UsecaseClientBO) {
          // add 'Open' use case action for common use cases
          actionSet.openUseCaseAction(manager, (UsecaseClientBO) selection.getFirstElement());
          manager.add(new Separator());
          actionSet.addLinkAction(manager, ((UsecaseClientBO) selection.getFirstElement()).getLinks());
        }
        // add move to favorites action
        actionSet.addMovToUserFav(manager, (IUseCaseItemClientBO) selection.getFirstElement(), this.viewer,
            this.outLineViewDataHandler.getUcDataHandler(), true);
      }
      // ICDM-930
      else if (selection.getFirstElement() instanceof AttrGroup) {
        actionSet.addLinkAction(manager, (new AttrGroupClientBO((AttrGroup) selection.getFirstElement())).getLinks());
      } // ICDM-929
      else if (selection.getFirstElement() instanceof AttrSuperGroup) {
        actionSet.addLinkAction(manager,
            (new AttrSuperGroupClientBO((AttrSuperGroup) selection.getFirstElement())).getLinks());
      }
      // ICDM-1035
      else if (selection.getFirstElement() instanceof FavUseCaseItemNode) {
        // add link action in case of fav nodes
        FavUseCaseItemNode favUcNode = (FavUseCaseItemNode) selection.getFirstElement();
        // add link context menu for usecase, usecase section
        if (favUcNode.getUseCaseItem() instanceof UsecaseClientBO) {
          // add 'Open' use case action for private use cases
          actionSet.openUseCaseAction(manager, (UsecaseClientBO) favUcNode.getUseCaseItem());
          manager.add(new Separator());
          actionSet.addLinkAction(manager, ((UsecaseClientBO) (favUcNode.getUseCaseItem())).getLinks());
        }
        else if (favUcNode.getUseCaseItem() instanceof UseCaseSectionClientBO) {
          actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) (favUcNode.getUseCaseItem())).getLinks());
        }
        if (favUcNode.getFavUcItem() != null) { // add delete fav uc node action
          actionSet.addDelFavUcAction(manager, favUcNode, this.viewer, null, null);
        }
      }
    }
  }

  @Override
  public Control getControl() {
    return this.top;
  }

  @Override
  public void setFocus() {
    // To prevent
    // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
    // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
    this.viewer.getControl().setFocus();
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    // Icdm-294
    GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    // Create filters
    PatternFilter filter = new PatternFilter();
    FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(2);

    // Set Content provider for the tree

    this.viewer.setContentProvider(new OutlinePIDCTreeViewContentProvider(null, this.outLineViewDataHandler));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlinePIDCTreeViewLabelProvider());
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT");

    // iCDM-350
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

    new CommonActionSet().addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);

  }

  /**
   * Applies styles to GridData
   *
   * @param gridData
   */
  private void applyGridDataStyles(final GridData gridData) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    GridLayout gridLayout = new GridLayout();
    this.composite = new Composite(this.top, SWT.NONE);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deleselectAll) {
    this.viewer.refresh();
    if (deleselectAll) {
      this.viewer.getTree().deselectAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {

    return this.outLineViewDataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }

  /**
   * ICDM-1035 Adds drag & drop listener to the tree view control
   */
  private void addDragDropListeners() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    // add drag listener
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

    // add drop listener
    final DropTarget target =
        new DropTarget(this.viewer.getControl(), DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      @Override
      public void dragOver(final DropTargetEvent event) {
        if ((event.item.getData() instanceof ProjFavUcRootNode) ||
            (event.item.getData() instanceof UserFavUcRootNode)) {
          event.detail = DND.DROP_DEFAULT;
        }
        else {
          event.detail = DND.DROP_NONE;
        }
        // this line is to enable automatic scroll in the tree viewer during drag
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }


      @Override
      public void dragEnter(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }

      }

      @Override
      public void drop(final DropTargetEvent event) {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        final Object dragData = event.data;
        final IStructuredSelection strucSelec = (StructuredSelection) dragData;
        if ((strucSelec.getFirstElement() instanceof IUseCaseItemClientBO) &&
            (event.item.getData() instanceof UserFavUcRootNode)) {
          CommonActionSet actionSet = new CommonActionSet();
          // // for private usecases
          try {
            actionSet.moveToUserFav((IUseCaseItemClientBO) strucSelec.getFirstElement(), UseCaseOutlinePage.this.viewer,
                UseCaseOutlinePage.this.outLineViewDataHandler.getUcDataHandler());
          }
          catch (IcdmException exp) {
            CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }
        }

      }


    });
  }


}
