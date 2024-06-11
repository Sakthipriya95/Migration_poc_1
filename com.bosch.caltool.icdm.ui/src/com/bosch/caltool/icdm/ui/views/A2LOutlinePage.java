/**
 *
 */
package com.bosch.caltool.icdm.ui.views;

import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.comppkg.ui.actions.ComponentPackageActionSet;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileInfoBO;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.comppkg.CompPkgBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.providers.OutlineA2LTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.OutlineA2LTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.action.A2LActionSet;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author adn1cob
 */
public class A2LOutlinePage extends AbstractPage {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  private Composite composite;
  private TreeViewer viewer;

  private final A2LContentsEditorInput editorInput;

  /**
   * Constructor
   *
   * @param a2lEditor
   */
  public A2LOutlinePage(final A2LContentsEditorInput a2lEditor) {
    this.editorInput = a2lEditor;
  }

  @Override
  public void createControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // Build the UI
    createComposite();
    // add selection provider
    getSite().setSelectionProvider(this.viewer);
  }

  @Override
  public Control getControl() {
    return this.top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editorInput.getDataHandler();
  }

  @Override
  public void setFocus() {
    // To prevent
    // "java_lang_RuntimeException: WARNING: Prevented recursive attempt to activate part
    // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
    // PlatformUI_getWorkbench()_getActiveWorkbenchWindow()_getShell()_setFocus()
    this.viewer.getControl().setFocus();
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    // Create filters
    PatternFilter filter = new PatternFilter();
    FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // ICDM 542
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

    // set auto expand level //ICDM-265
    this.viewer.setAutoExpandLevel(1);

    // Set Content provider for the tree

    this.viewer.setContentProvider(new OutlineA2LTreeViewContentProvider(this.editorInput.getA2lWPInfoBO(),
        this.editorInput.getPidcA2lBO(), true, true, null));
    // Set Label provider for the tree

    this.viewer.setLabelProvider(
        new OutlineA2LTreeViewLabelProvider(this.editorInput.getA2lWPInfoBO(), this.editorInput.getPidcA2lBO(), null));
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT");
    // expand the first level for A2L file node alone
    // ICDM-2272
    this.viewer.expandToLevel(this.editorInput.getA2lFile(), 1);

    addRightClickMenu();
    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);
  }

  /**
   * ICDM 440 This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {


    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      CommonActionSet cmnActionSet = new CommonActionSet();

      final IStructuredSelection selection = (IStructuredSelection) A2LOutlinePage.this.viewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      // ICDM-2272
      if (isValidRightClick(firstElement, selection)) {
        if ((firstElement instanceof Function) || (firstElement instanceof A2LBaseComponentFunctions)) {

          // Icdm 440 Passing the function list from the editor
          A2LFileInfoBO editorModelBo = A2LOutlinePage.this.editorInput.getA2lFileInfoBO();
          SortedSet<Function> functionList = editorModelBo.getAllSortedFunctions();
          ReviewActionSet actionSet = new ReviewActionSet();
        }
        // ICDM-939
        // Component package right click menu
        else if (firstElement instanceof CompPackage) {

          CompPackage cmpPkg = (CompPackage) firstElement;
          final A2LActionSet actionset = new A2LActionSet();
          actionset.getCompPkgDataAction(menuMgr, cmpPkg, A2LOutlinePage.this.editorInput);
          cmnActionSet.addLinkAction(menuMgr, new CompPkgBO(cmpPkg).getLinks());
          // ICDM-985
          ComponentPackageActionSet.getInstance().openCompPkgEditorFromA2lEditor(menuMgr, cmpPkg);
        }
        // Icdm-1368- new Item for rule Set
        else if (firstElement instanceof RuleSet) {
          final RuleSet ruleSet = (RuleSet) firstElement;
          A2LActionSet a2lActionSet = new A2LActionSet();
          a2lActionSet.copyRuleSetLinktoClipBoard(menuMgr, ruleSet);

          Action openEditorAction = new Action("Open Rule Set Editor", SWT.NONE) {

            // ICDM-1368
            @Override
            public void run() {
              ReviewActionSet actionSet = new ReviewActionSet();
              try {
                actionSet.openRulesEditor(new ReviewParamEditorInput(ruleSet), null);
              }
              catch (PartInitException excep) {
                CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
              }
            }
          };
          final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.RULE_SET_EDIT_16X16);
          openEditorAction.setImageDescriptor(imageDesc);
          menuMgr.add(openEditorAction);

          // menu seperator
          // ICDM-2272
          menuMgr.add(new Separator());

          RuleSetServiceClient comppkgServiceClient = new RuleSetServiceClient();
          RuleSetRulesResponse paramRuleResponse = null;
          try {
            paramRuleResponse = comppkgServiceClient.getRuleSetParamRules(ruleSet.getId());
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          // ICDM-1372
          cmnActionSet.addImportCalDataAction(menuMgr, ruleSet, new ParamCollectionDataProvider(), paramRuleResponse);

        }
      }
    });

    Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);

    // Register menu for extension.
    getSite().registerContextMenu(null, menuMgr, this.viewer);
  }

  /**
   * @param firstElement
   * @param selection
   * @return
   */
  // ICDM-2272
  private boolean isValidRightClick(final Object firstElement, final IStructuredSelection selection) {
    return CommonUtils.isNotNull(selection) && !selection.isEmpty() && (!(firstElement instanceof A2LFile));
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
  public void refreshTreeViewer(final boolean deselectAll) {
    this.viewer.refresh();
    if (deselectAll) {
      this.viewer.getTree().deselectAll();
    }

  }

  /**
   * CNS UI Refresh
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }

}
