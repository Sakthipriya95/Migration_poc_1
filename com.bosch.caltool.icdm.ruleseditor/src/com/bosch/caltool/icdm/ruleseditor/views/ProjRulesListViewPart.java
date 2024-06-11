package com.bosch.caltool.icdm.ruleseditor.views;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * Project Rules view part
 *
 * @author bru2cob
 */
public class ProjRulesListViewPart extends AbstractViewPart {

  /**
   * Composite instance for base layout
   */
  private Composite top;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * TableViewer instance
   */
  private TableViewer tableViewer;

  /**
   * Instance of text filter
   */
  private Text filterTxt;
  /**
   * Instance of table filte
   */
  private ViewerFilter tableFilter;
  /**
   * Selected rule set
   */
  private RuleSet selectedRuleSet;

  /**
   * The width of each column for properties table.
   */
  private static final int COLWIDTH = 200;


  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    setTitleToolTip("List of Project Specific Rules set");
  }


  @Override
  public void setFocus() {
    this.tableViewer.getControl().setFocus();
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    // create a scrolled composite
    final ScrolledComposite scrollComp = new ScrolledComposite(this.top, SWT.V_SCROLL);
    scrollComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = new Composite(scrollComp, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);
    // set the composite properties
    scrollComp.setContent(this.composite);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    // create group
    createGroup();
    // create toolbar actions
    createToolBar();

    getSite().setSelectionProvider(ProjRulesListViewPart.this.tableViewer);
  }

  /**
   * Creates an actions on view tool bar
   */
  private void createToolBar() {
    // get the toolbar manager instance
    final IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    // create the seperator object
    // cdr action set instance
    ReviewRuleActionSet actionSet = new ReviewRuleActionSet();

    // create the adding rule set action
    final IAction toolBarAddAction = actionSet.projRulesAddAction(this);
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    // add the action to menu manager
    mgr.add(toolBarAddAction);

    // create the editing rule set action
    final IAction toolBarEditAction = actionSet.projRulesEditAction(this);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    // add the action to menu manager
    mgr.add(toolBarEditAction);

    // create the deleting rule set action
    final IAction toolBarDelAction = actionSet.projRulesDelAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    // add the action to menu manager
    mgr.add(toolBarDelAction);
    // set the delete action to false initially
    toolBarDelAction.setEnabled(false);

  }

  /**
   * This method initializes group
   */
  private void createGroup() {

    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;

    // create the filter text for the view
    this.filterTxt = TextUtil.getInstance().createText(this.composite, true, "");
    // set the message
    this.filterTxt.setMessage(CommonUIConstants.TEXT_FILTER);
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    // add modify listener to the filter text
    addModifyListenerForFilterTxt();

    final Group group = new Group(this.composite, SWT.NONE);
    group.setLayoutData(gridData);
    group.setLayout(new GridLayout());

    // create the table viewer to display the ruleSet
    this.tableViewer = new TableViewer(group, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
    // set table viewer properties
    this.tableViewer.setUseHashlookup(true);
    this.tableViewer.getTable().setLayoutData(gridData);
    this.tableViewer.getTable().setLinesVisible(false);
    this.tableViewer.getTable().setHeaderVisible(false);

    // create the columns
    createCol();

    // set the input for the table
    try {
      this.tableViewer.setInput(new RuleSetServiceClient().getAllRuleSets());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }


  /**
   * Create the column
   */
  private void createCol() {
    final TableViewerColumn prjRuleSetNames = new TableViewerColumn(this.tableViewer, SWT.LEFT);

    // create the table column
    final TableColumn[] columns = this.tableViewer.getTable().getColumns();
    columns[0].setWidth(COLWIDTH);
    // set the content provider
    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    // enable tooltip for the column
    ColumnViewerToolTipSupport.enableFor(this.tableViewer);
    prjRuleSetNames.setLabelProvider(new StyledCellLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        String result = "";
        if (element instanceof RuleSet) {
          final RuleSet data = (RuleSet) element;
          // display the name of the ruleset
          result = data.getName();
          cell.setText(result);
          // if the ruleset is delted display in red colour else black
          if (data.isDeleted()) {
            cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
                .getSystemColor(SWT.COLOR_RED));
          }
          else {
            cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
                .getSystemColor(SWT.COLOR_BLACK));
          }
          cell.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CMP_PKG_16X16));
        }
        else {
          cell.setText(result);
        }

      }

    });
  }


  /**
   * Modify listener for the filter text
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(event -> {
      final String text = ProjRulesListViewPart.this.filterTxt.getText().trim();
      ((AbstractViewerFilter) ProjRulesListViewPart.this.tableFilter).setFilterText(text);
      ProjRulesListViewPart.this.tableViewer.refresh();
    });
  }


  /**
   * @return the selectedCmpPkg
   */
  public RuleSet getSelecProjRuleSet() {
    return this.selectedRuleSet;
  }


  /**
   * @return the tableViewer
   */
  public TableViewer getTableViewer() {
    return this.tableViewer;
  }

}
