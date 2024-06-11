package com.bosch.caltool.comppkg.ui.views;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.comppkg.ui.actions.ComponentPackageActionSet;
import com.bosch.caltool.comppkg.ui.utils.IUIConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.comppkg.CompPkgBO;
import com.bosch.caltool.icdm.client.bo.comppkg.ComponentPackagesListViewClientDataHandler;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgRuleResponse;
import com.bosch.caltool.icdm.model.comppkg.CompPkgType;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * @author dmo5cob
 */
public class ComponentPackagesListViewPart extends AbstractViewPart implements IUIConstants {

  /**
   * The width of each column for properties table.
   */
  private static final int COLWIDTH = 200;

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
   * filter text
   */
  private Text filterTxt;
  /**
   * table filter
   */
  private ViewerFilter tableFilter;

  /**
   * ICDM-796 CommandState instance
   */
  private CommandState expReportService = new CommandState();

  private final ComponentPackagesListViewClientDataHandler dataHandler =
      new ComponentPackagesListViewClientDataHandler();


  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();

    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    createComposite();
    // ICDM-1293
    // iCDM-530
    setTitleToolTip("List of all Component Packages");
  }


  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void addDataImportAction(final IMenuManager manager, final CompPackage selectedCp) {
    if (!selectedCp.isDeleted() && CompPkgType.NE.getLiteral().equals(selectedCp.getCompPkgType())) {
      // if comp pkg is not deleted and of NE type
      // import cal data as right click action
      CompPkgServiceClient comppkgServiceClient = new CompPkgServiceClient();
      CompPkgRuleResponse paramRuleResponse = null;
      try {
        paramRuleResponse = comppkgServiceClient.getCompPkgRule(selectedCp.getId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      new CommonActionSet().addImportCalDataAction(manager, selectedCp, new ParamCollectionDataProvider(),
          paramRuleResponse);
    }
  }

  @Override
  public void setFocus() {
    // setting focus to the table
    this.tableViewer.getControl().setFocus();
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    // creating scrolled composite
    final ScrolledComposite scrollComp = new ScrolledComposite(this.top, SWT.V_SCROLL);
    scrollComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = new Composite(scrollComp, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);
    scrollComp.setContent(this.composite);
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    // set minimum size for the composite
    scrollComp.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    // create group below composite
    createGroup();
    // create tool bar action
    createToolBar();
    // set selection provider
    getSite().setSelectionProvider(ComponentPackagesListViewPart.this.tableViewer);
  }

  /**
   * Creates an actions on view tool bar
   */
  private void createToolBar() {

    final IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    // add component package action
    final IAction toolBarAddAction = ComponentPackageActionSet.getInstance().createCmpPkgAddAction(this);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(toolBarAddAction);
    // edit component package action
    final IAction toolBarEditAction = ComponentPackageActionSet.getInstance().createCmpPkgEditAction(this);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(toolBarEditAction);
    // delete component package action
    final IAction toolBarDelAction = ComponentPackageActionSet.getInstance().createCmpPkgDelAction(this);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(toolBarDelAction);
    toolBarDelAction.setEnabled(false);

  }

  /**
   * This method initializes group
   */
  private void createGroup() {

    final GridData tvGridData = new GridData();
    tvGridData.horizontalAlignment = GridData.FILL;
    tvGridData.grabExcessHorizontalSpace = true;
    tvGridData.grabExcessVerticalSpace = true;
    tvGridData.verticalAlignment = GridData.FILL;

    final GridData txtGridData = new GridData();
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.verticalAlignment = GridData.CENTER;
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;

    this.filterTxt = TextUtil.getInstance().createText(this.composite, true, "");
    this.filterTxt.setMessage(IUIConstants.TYPE_FILTER_TEXT_LABEL);
    this.filterTxt.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    addModifyListenerForFilterTxt();

    final Group group = new Group(this.composite, SWT.NONE);
    group.setLayoutData(gridData);
    group.setLayout(gridLayout);
    this.tableViewer = new TableViewer(group, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
    this.tableViewer.setUseHashlookup(true);


    this.tableViewer.getTable().setLayoutData(tvGridData);
    this.tableViewer.getTable().setLinesVisible(false);
    this.tableViewer.getTable().setHeaderVisible(false);

    final TableViewerColumn compPkgNames = new TableViewerColumn(this.tableViewer, SWT.LEFT);


    final TableColumn[] columns = this.tableViewer.getTable().getColumns();
    columns[0].setWidth(COLWIDTH);

    // set content provider
    this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    ColumnViewerToolTipSupport.enableFor(this.tableViewer);
    // set label provider
    compPkgNames.setLabelProvider(labelProvider());
    // add filter for the table viewer
    addFilter();
    this.tableViewer.getTable().addSelectionListener(selectionListener());

    setTableInput();

    hookContextMenu(); // ICDM-977
    addDoubleClickListener();
  }


  /**
   * @return
   */
  private SelectionAdapter selectionListener() {
    return new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // ICDM-796
        // ICDM-865
        ComponentPackagesListViewPart.this.expReportService =
            (CommandState) CommonUiUtils.getInstance().getSourceProvider();
        ComponentPackagesListViewPart.this.expReportService.setExportService(false);

        // enable the delete and edit actions if component package is modifialble
        boolean cpModifiable = new CompPkgBO(getSelectedCmpPkg()).isModifiable();
        ComponentPackageActionSet.getInstance().getDeleteAction().setEnabled(cpModifiable);
        ComponentPackageActionSet.getInstance().getEditAction().setEnabled(cpModifiable);
      }

    };
  }


  /**
   * @return
   */
  private StyledCellLabelProvider labelProvider() {
    return new StyledCellLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        String result = "";
        if (element instanceof CompPackage) {
          final CompPackage data = (CompPackage) element;
          result = data.getName();
          cell.setText(result);
          if (data.isDeleted()) {
            // set the foreground for deleted items
            cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
                .getSystemColor(SWT.COLOR_RED));
          }
          else {
            // set the foreground for non deleted items
            cell.setForeground(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay()
                .getSystemColor(SWT.COLOR_BLACK));
          }
          // ICDM-977
          // set image for comp pkg with links
          Image mandatoryImage = CommonUiUtils.getInstance().getLinkOverLayedImageCompPkg(ImageKeys.CMP_PKG_16X16,
              ImageKeys.LINK_DECORATOR_12X12, data, ComponentPackagesListViewPart.this.dataHandler.getNodeWithLinks());
          cell.setImage(mandatoryImage);
        }
        else {
          // set the text for component package
          cell.setText(result);
        }

      }

    };
  }

  private void setTableInput() {
    this.tableViewer.setInput("");
    this.tableViewer.setInput(this.dataHandler.getAllCompPackages());
  }

  /**
   * add filter for the table view
   */
  private void addFilter() {
    this.tableViewer.addFilter(new AbstractViewerFilter() {

      @Override
      protected boolean selectElement(final Object element) {
        if (element instanceof CompPackage) {

          final CompPackage cmpPkg = (CompPackage) element;
          // match the entered text with component package name
          if (matchText(cmpPkg.getName())) {
            return true;
          }
        }
        return false;
      }
    });

    final ViewerFilter[] filters = this.tableViewer.getFilters();
    this.tableFilter = filters[0];
  }

  /**
   *
   */
  private void addModifyListenerForFilterTxt() {
    this.filterTxt.addModifyListener(event -> {
      final String text = ComponentPackagesListViewPart.this.filterTxt.getText().trim();
      // set the entered text as filter text and invoke the table viewer refresh
      ((AbstractViewerFilter) ComponentPackagesListViewPart.this.tableFilter).setFilterText(text);
      ComponentPackagesListViewPart.this.tableViewer.refresh();
    });
  }

  /**
   * adding double click listener to the table viewer . On double click the corresponding editor needs to get opened
   */
  private void addDoubleClickListener() {
    this.tableViewer.addDoubleClickListener(event -> Display.getDefault().asyncExec(() -> {
      CompPackage selectedCp = getSelectedCmpPkg();
      if (selectedCp != null) {
        // open the comp pkg editor on double click
        ComponentPackageActionSet.getInstance().openEditor(selectedCp);
      }
    }));
  }


  /**
   * This method adds right click menu for tableviewer
   */
  private void hookContextMenu() {
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);

    menuMgr.addMenuListener(mgr -> {
      CompPackage selCp = getSelectedCmpPkg();
      if (selCp != null) {
        addDataImportAction(mgr, selCp);
        new CommonActionSet().addLinkAction(mgr, new CompPkgBO(selCp).getLinks());
      }
    });

    Menu menu = menuMgr.createContextMenu(this.tableViewer.getControl());
    this.tableViewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.tableViewer);
  }

  /**
   * @return the selectedCmpPkg
   */
  public CompPackage getSelectedCmpPkg() {
    CompPackage selectedCp = null;
    final IStructuredSelection selection =
        (IStructuredSelection) ComponentPackagesListViewPart.this.tableViewer.getSelection();
    if ((selection != null) && (!selection.isEmpty())) {
      // if there is a valid selection
      final Object element = selection.getFirstElement();
      if (element instanceof CompPackage) {
        // get the selected component package
        selectedCp = (CompPackage) element;
      }
    }
    return selectedCp;
  }


  /**
   * @return the tableViewer
   */
  public TableViewer getTableViewer() {
    return this.tableViewer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ComponentPackagesListViewClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    CompPackage selectedCp = null;
    ISelection selection = this.tableViewer.getSelection();
    if (selection != null) {
      selectedCp = (CompPackage) ((StructuredSelection) selection).getFirstElement();
    }

    setTableInput();

    if (selectedCp != null) {
      this.tableViewer.setSelection(new StructuredSelection(selectedCp), true);
    }
  }
}
