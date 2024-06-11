/**
 *
 */
package com.bosch.caltool.cdr.ui.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.providers.OutlineA2LTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.OutlineA2LTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.TreeViewSelectnRespWP;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;

/**
 * @author adn1cob
 */
public class CdrReportOutlinePage extends AbstractPage {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  private Composite composite;
  private TreeViewer viewer;

  private final CdrReportEditorInput editorInput;

  /**
   * Constructor
   *
   * @param input Cdr Report Editor Input
   */
  public CdrReportOutlinePage(final CdrReportEditorInput input) {
    this.editorInput = input;
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
    this.viewer.setContentProvider(new OutlineA2LTreeViewContentProvider(
        this.editorInput.getReportData().getA2lEditorDataProvider().getA2lWpInfoBO(),
        this.editorInput.getReportData().getA2lEditorDataProvider().getPidcA2LBO(), false, false,
        this.editorInput.getReportData()));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlineA2LTreeViewLabelProvider(
        this.editorInput.getReportData().getA2lEditorDataProvider().getA2lWpInfoBO(),
        this.editorInput.getReportData().getA2lEditorDataProvider().getPidcA2LBO(), this.editorInput.getReportData()));
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT");
    // ICDM-2272
    this.viewer.expandToLevel(this.editorInput.getReportData().getA2lFile(), 1);

    addRightClickMenu();
    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);


  }

  /**
   * ICDM 440 This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {

    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(CdrReportOutlinePage.this::fillContextMenu);
    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {

    // Get the current selection and add actions to it
    IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();

    if ((selection != null) && (selection.getFirstElement() != null) &&
        ((selection.getFirstElement() instanceof A2lResponsibility) ||
            (selection.getFirstElement() instanceof A2lWpResponsibility))) {

      Long a2lRespId = resolveRespIdFromSeln(selection);

      final Action lockWPAction = new Action() {

        @Override
        public void run() {

          TreeViewSelectnRespWP selWpResp = new TreeViewSelectnRespWP();

          CdrReportDataHandler cdrReportData = CdrReportOutlinePage.this.editorInput.getReportData();

          PidcVariant pidcVariant = cdrReportData.getPidcVariant();
          Long pidcVariantID = CommonUtils.isNull(pidcVariant) ? -1L : pidcVariant.getId();
          selWpResp.setVariantID(pidcVariantID);

          selWpResp.setPidcA2lID(CdrReportOutlinePage.this.editorInput.getPidcA2l().getId());

          // Map of responsibility id,workpackage id and A2lWPRespModel
          Map<Long, Map<Long, A2lWPRespModel>> respWpA2lWpRespModelMap = new HashMap<>();
          selWpResp.setRespWpA2lWpRespModelMap(respWpA2lWpRespModelMap);

          Map<Long, A2lWPRespModel> wpA2lWpRespModelMap = new HashMap<>();
          respWpA2lWpRespModelMap.put(a2lRespId, wpA2lWpRespModelMap);

          if (selection.getFirstElement() instanceof A2lResponsibility) {

            A2lResponsibility a2lResp = (A2lResponsibility) selection.getFirstElement();

            Set<A2lWpResponsibility> a2lWpRespSet = cdrReportData.getA2lWPResponsibleMap().get(a2lResp.getName());

            cdrReportData.populateWpRespModelForRespNode(a2lRespId, a2lWpRespSet,
                cdrReportData.getWPRespModelAtVariant(), wpA2lWpRespModelMap);

          }
          else if (selection.getFirstElement() instanceof A2lWpResponsibility) {

            A2lWpResponsibility a2lWpResp = (A2lWpResponsibility) selection.getFirstElement();

            Set<A2lWpResponsibility> a2lWpRespSet = new HashSet<>();
            a2lWpRespSet.add(a2lWpResp);

            cdrReportData.populateWpRespModelForRespNode(a2lRespId, a2lWpRespSet,
                cdrReportData.getWPRespModelAtVariant(), wpA2lWpRespModelMap);

          }

          new CDRReviewResultServiceClient().updateSelWorkpackageStatus(selWpResp);
        }

      };

      lockWPAction.setText("Mark Workpackage Responsibility as Finished");
      ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.ALL_16X16);
      lockWPAction.setImageDescriptor(imageDesc);
      lockWPAction.setEnabled(CdrReportOutlinePage.this.editorInput.getReportData().wpRespFinishAccessValidation(
          a2lRespId, CdrReportOutlinePage.this.editorInput.getReportData().getPidcA2l().getProjectId()));
      manager.add(lockWPAction);
    }
  }

  /**
   * @param selection
   * @return
   */
  private Long resolveRespIdFromSeln(final IStructuredSelection selection) {
    Long a2lRespId = 0L;
    if (selection.getFirstElement() instanceof A2lWpResponsibility) {

      A2lWpResponsibility a2lWpResp = (A2lWpResponsibility) selection.getFirstElement();

      a2lRespId =
          this.editorInput.getReportData().getResponsibilityForRespName(a2lWpResp.getMappedWpRespName()).getId();
    }
    if (selection.getFirstElement() instanceof A2lResponsibility) {
      A2lResponsibility a2lResp = (A2lResponsibility) selection.getFirstElement();
      a2lRespId = a2lResp.getId();
    }
    return a2lRespId;
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

}
