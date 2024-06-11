/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.cns.CnsChangeEventSummary;
import com.bosch.caltool.icdm.client.bo.cns.TransactionSummaryViewDataHandler;
import com.bosch.caltool.icdm.common.ui.sorters.TransactionSummaryTabSorter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.providers.TransactionSummaryContentProvider;
import com.bosch.caltool.icdm.common.ui.views.providers.TransactionSummaryLabelProvider;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This viewpart displays the list of changes (commands) in one session. ICDM-484
 *
 * @author dmo5cob
 */
public class TransactionSummaryViewPart extends AbstractViewPart {

  /**
   * Column Index for EventID
   */
  public static final int COLUMN_EVENT_ID = 0;
  /**
   * Column Index for EventID
   */
  public static final int COLUMN_SERVICE_ID = 1;
  /**
   * Column Index for Created Date
   */
  public static final int COLUMN_CREATED_DATE = 2;
  /**
   * Column Index for Data Size
   */
  public static final int COLUMN_DATA_SIZE = 3;
  /**
   * Column Index for Change Count
   */
  public static final int COLUMN_CHANGE_COUNT = 4;
  /**
   * Column Index for Event Summary
   */
  public static final int COLUMN_EVENT_SUMMARY = 5;

  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * Main Composite
   */
  private Composite composite;
  /**
   * Section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * Summary tree
   */
  private TreeViewer summaryTreeViewer;
  /**
   * Tree viewer as table
   */
  private FilteredTree tableTree;
  /**
   * CnsChangeEventHandler
   */
  private final TransactionSummaryViewDataHandler cnsChangeHandler = new TransactionSummaryViewDataHandler();
  /**
   * TransactionSummaryTabSorter
   */
  private final TransactionSummaryTabSorter tableViewSorter = new TransactionSummaryTabSorter();

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // Create tool kit
    FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    this.nonScrollableForm = toolkit.createForm(parent);
    // create composite
    createComposite(toolkit);
    hookContextMenu();
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkitObj) {
    // Create main composite
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createSection(toolkitObj);
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // icdm-530
    setTitleToolTip("Protocol of changes in a session");

  }

  /**
   * Build contect menu on tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(TransactionSummaryViewPart.this::fillContextMenu);
    Menu menu = menuMgr.createContextMenu(this.summaryTreeViewer.getControl());
    this.summaryTreeViewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, this.summaryTreeViewer);
  }

  private void fillContextMenu(final IMenuManager manager) {

    // Get the current selection and add actions to it
    IStructuredSelection selection = (IStructuredSelection) this.summaryTreeViewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      Set<String> sidSet = new HashSet<>();
      for (Object sel : selection.toList()) {
        if (sel instanceof CnsChangeEventSummary) {
          sidSet.add(((CnsChangeEventSummary) sel).getServiceID());
        }
      }
      if (!sidSet.isEmpty()) {
        manager.add(new ServiceIdCopyToClipboardAction(sidSet));
      }
    }
  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkitObj) {
    // Create main section
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkitObj, "");
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setText("Transaction Summary");
    this.section.getDescriptionControl().setEnabled(false);
    createForm(toolkitObj);
    this.section.setClient(this.form);

  }

  /**
   * This method initializes scrolledForm
   */
  private void createForm(final FormToolkit toolkitObj) {
    this.form = toolkitObj.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    // Create table viewer
    createTableViewer();
    setTableViewerSorter();
    // Create Refresh Action
    createRefreshAction();
  }


  /**
   * Creates the summary tree tableviewer
   */
  private void createTableViewer() {
    // Enable pattern filter
    final PatternFilter filter = new PatternFilter() {

      @Override
      protected boolean isLeafMatch(final Viewer viewer, final Object element) {
        TreeViewer treeViewer = (TreeViewer) viewer;
        int numberOfColumns = treeViewer.getTree().getColumnCount();
        ITableLabelProvider labelProvider = (ITableLabelProvider) treeViewer.getLabelProvider();
        // check if any column text matches the filter text
        boolean isMatch = false;
        for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
          String labelText = labelProvider.getColumnText(element, columnIndex);
          isMatch |= wordMatches(labelText);
        }
        isMatch |= wordMatches(((TransactionSummaryLabelProvider) labelProvider).getSummaryToolTipText(element));
        return isMatch;
      }
    };
    // Filtered tree
    this.tableTree = new FilteredTree(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI, filter, true);

    this.summaryTreeViewer = this.tableTree.getViewer();
    this.summaryTreeViewer.getTree().setHeaderVisible(true);
    this.summaryTreeViewer.getTree().setLayoutData(GridDataUtil.getInstance().getGridData());
    // expand to first level
    this.summaryTreeViewer.setAutoExpandLevel(1);
    // set content and label providers
    this.summaryTreeViewer.setContentProvider(new TransactionSummaryContentProvider());
    this.summaryTreeViewer.setLabelProvider(new TransactionSummaryLabelProvider());
    // Create columns
    createTabViewerColumns();
    this.summaryTreeViewer.setInput(this.cnsChangeHandler.getEventSummary());

  }

  /**
   *
   */
  private void setTableViewerSorter() {
    this.summaryTreeViewer.setComparator(this.tableViewSorter);
  }

  /**
   * create refresh action
   */
  private void createRefreshAction() {
    ToolBarManager toolbarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolbarManager.createControl(this.section);

    Action refreshAction = new Action("Refresh", SWT.BUTTON1) {

      @Override
      public void run() {
        TransactionSummaryViewPart.this.summaryTreeViewer
            .setInput(TransactionSummaryViewPart.this.cnsChangeHandler.getLatestChangeEvents());
        TransactionSummaryViewPart.this.summaryTreeViewer.refresh();
      }
    };
    // Set the image for Transaction Summary Refresh Action
    refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REFRESH_16X16));
    refreshAction.setEnabled(true);
    toolbarManager.add(refreshAction);

    toolbarManager.update(true);
    this.section.setTextClient(toolbar);

  }

  /**
   * Create Transaction summary columns
   */
  private void createTabViewerColumns() {
    createEventIdColViewer();
    createSeriveIdColViewer();
    createDateColViewer();
    createDataSizeColViewer();
    createChangeCountColViewer();
    createSummaryColViewer();
  }

  /**
   * This method creates event ID column
   */
  private void createEventIdColViewer() {
    TreeColumn eventIdCol = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    eventIdCol.setAlignment(SWT.LEFT);
    eventIdCol.setText("Event ID");
    eventIdCol.setWidth(75);

    eventIdCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(eventIdCol, COLUMN_EVENT_ID,
        this.tableViewSorter, this.summaryTreeViewer));

  }

  /**
   * This method creates event ID column
   */
  private void createSeriveIdColViewer() {
    TreeColumn eventIdCol = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    eventIdCol.setAlignment(SWT.LEFT);
    eventIdCol.setText("Service ID");
    eventIdCol.setWidth(230);


    eventIdCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(eventIdCol, COLUMN_SERVICE_ID,
        this.tableViewSorter, this.summaryTreeViewer));

  }

  /**
   * This method creates name column
   */
  private void createDateColViewer() {
    final TreeColumn dateColumn = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    dateColumn.setAlignment(SWT.LEFT);
    dateColumn.setText("Created Date");
    dateColumn.setWidth(120);

    dateColumn.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dateColumn,
        COLUMN_CREATED_DATE, this.tableViewSorter, this.summaryTreeViewer));
  }

  /**
   * This method creates Modified Item column
   */
  private void createDataSizeColViewer() {

    final TreeColumn dataSizeColumn = new TreeColumn(this.summaryTreeViewer.getTree(), SWT.LEFT);
    this.summaryTreeViewer.getTree().setLinesVisible(true);
    dataSizeColumn.setAlignment(SWT.LEFT);
    dataSizeColumn.setText("Data Size (Bytes)");
    dataSizeColumn.setWidth(100);

    dataSizeColumn.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(dataSizeColumn,
        COLUMN_DATA_SIZE, this.tableViewSorter, this.summaryTreeViewer));
  }


  /**
   * This method creates old value column
   */
  private void createChangeCountColViewer() {

    final TreeColumn chngeCntCol = new TreeColumn(this.tableTree.getViewer().getTree(), SWT.LEFT);
    this.tableTree.getViewer().getTree().setLinesVisible(true);
    chngeCntCol.setAlignment(SWT.LEFT);
    chngeCntCol.setText("Change Count");
    chngeCntCol.setWidth(90);

    chngeCntCol.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(chngeCntCol,
        COLUMN_CHANGE_COUNT, this.tableViewSorter, this.summaryTreeViewer));
  }

  /**
   * This method creates new value column
   */
  private void createSummaryColViewer() {
    final TreeColumn summaryColumn = new TreeColumn(this.tableTree.getViewer().getTree(), SWT.LEFT);
    this.tableTree.getViewer().getTree().setLinesVisible(true);
    summaryColumn.setAlignment(SWT.LEFT);
    summaryColumn.setText("Event Summary");
    summaryColumn.setWidth(415);

    summaryColumn.addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(summaryColumn,
        COLUMN_EVENT_SUMMARY, this.tableViewSorter, this.summaryTreeViewer));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    // Not Applicable
  }

}
