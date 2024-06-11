/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editors.pages;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.admin.ui.actions.MultiNodeAccessPageActionSet;
import com.bosch.caltool.admin.ui.editor.filter.MultiNodeFilter;
import com.bosch.caltool.admin.ui.editor.sorter.MultiNodeSorter;
import com.bosch.caltool.icdm.common.bo.apic.NodeType;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.sorter.NewUserGridTableViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.NewUsersFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.providers.NewUserLabelProvider;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author say8cob
 */
public class MultiNodeAccessPage extends AbstractFormPage {


  /**
   *
   */
  private static final boolean DISABLE = false;

  /**
   *
   */
  private static final String ALL_READ = "All Read";

  /**
   *
   */
  private static final String ALL_WRITE = "All Write";

  /**
   *
   */
  private static final String ALL_GRANT = "All Grant";

  /**
   *
   */
  private static final String ALL_OWNER = "All Owner";

  /**
   * btn name
   */
  private static final String IMPORT_USER_NODES = "Import Node Access";

  /**
   * Tool tip for node access
   */
  private static final String IMPORT_USER_NODES_TOOLTIP = "Import node access definitions from a User";

  /**
   * RELATED TO USER TABLE
   */
  private static final int COL_DEPT_SIZE = 300;

  /**
   * RELATED TO USER TABLE
   */
  private static final String COL_DEPARTMENT = "Department";

  /**
   * RELATED TO USER TABLE
   */
  private static final int COL_NT_ID_SIZE = 100;

  /**
   * RELATED TO USER TABLE
   */
  private static final String COL_NT_ID = "NT-ID";

  /**
   * RELATED TO USER TABLE
   */
  private static final int COL_USER_NAME_SIZE = 250;

  /**
   * RELATED TO USER TABLE
   */
  private static final String COL_USER_NAME = "User Name";

  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_OWNER = "Owner";

  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_GRANT = "Grant";

  /**
   * RELATED TO NODES TABLE
   */
  private static final int COL_CHECKBOX_SIZE = 100;

  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_WRITE = "Write";


  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_READ = "Read";

  /**
   * RELATED TO NODES TABLE
   */
  private static final int COL_DESC_SIZE = 200;

  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_DESCRIPTION = "Description";

  /**
   * RELATED TO NODES TABLE
   */
  private static final int COL_NODE_TYPE_SIZE = 150;

  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_NODE_TYPE = "Node Type";

  /**
   * RELATED TO NODES TABLE
   */
  private static final int COL_NODE_NAME_SIZE = 300;

  /**
   * RELATED TO NODES TABLE
   */
  private static final String COL_NODE_NAME = "Node Name";

  /**
   * Toolbar manager
   */
  private ToolBarManager toolBarManager;

  private Form nonScrollableForm;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /**
   * Composite instance for base layout
   */
  private SashForm mainComposite;


  /**
   * Table section instance
   */
  private Section nodesTableSection;

  /**
   * Table form instance
   */
  private Form nodesTableForm;

  /**
   * Table section instance
   */
  private Section usersTableSection;

  /**
   * Table form instance
   */
  private Form usersTableForm;

  /**
   * Text form instance
   */
  private Text nodeFilterTxt;

  private Text userNameTextField;

  /**
   * GridTableViewer for node table
   */
  private GridTableViewer nodeTableViewer;

  /**
   * GridTableViewer instance for add new user
   */
  private GridTableViewer newUserTabViewer;
  /**
   * Text form instance
   */
  private Text userFilterText;

  /**
   * NewUserGridTableViewerSorter instance for Columns sortting
   */
  private NewUserGridTableViewerSorter userTabSorter;
  /**
   * NewUsersFilter instance
   */
  private NewUsersFilter newUserFilters;


  private MultiNodeSorter nodeSorter;

  private MultiNodeFilter adminMultiNodeFilter;

  private Set<NodeAccessInfo> nodeTableInputSet = new HashSet<>();

  private Set<NodeAccessInfo> selectedNodeTableInputSet = new HashSet<>();


  private Set<User> userTableInputSet = new HashSet<>();

  private Set<User> selectedUserTableInputSet = new HashSet<>();

  private User selectedUser = null;

  private MultiNodeAccessPageActionSet nodeAccessPageActionSet;


  // Access Buttons
  private Button allOwnerBtn;

  private Button allReadBtn;

  private Button allWriteBtn;

  private Button allGrantBtn;

  private Button userNameBwsBtn;

  /**
   * @param editor as input
   * @param title as input
   */
  public MultiNodeAccessPage(final FormEditor editor, final String title) {
    super(editor, title, title);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {

    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(getGridData());
    this.nonScrollableForm.setText("Manage Multiple Nodes");


    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;

    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.VERTICAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(getGridData());
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    // create composite
    createComposite();
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {

    this.nodeAccessPageActionSet = new MultiNodeAccessPageActionSet(this);

    // To Create Main tool bar
    ToolBarManager topLvlToolBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    addHelpAction(topLvlToolBarManager);

    topLvlToolBarManager.add(this.nodeAccessPageActionSet.saveEditorAction());
    topLvlToolBarManager.add(new Separator());
    topLvlToolBarManager.add(this.nodeAccessPageActionSet.resetEditorAction());

    topLvlToolBarManager.update(true);

    createNodesTableViewerSection();

    createUsersTableViewerSection();
  }

  private void createActionSections() {
    Composite parentComp = new Composite(this.nodesTableForm.getBody(), SWT.NONE);

    GridLayout parentGridLayoutForComp = new GridLayout();
    parentGridLayoutForComp.numColumns = 2;
    parentComp.setLayout(parentGridLayoutForComp);
    parentComp.setLayoutData(getFirstGridData());

    addViewUserSection(parentComp);
    addAccessBtnSection(parentComp);
  }

  /**
   * @param parentComp
   */
  private void addViewUserSection(final Composite parentComp) {
    Composite viewUserComp = new Composite(parentComp, SWT.NONE);

    GridLayout gridLayoutForComp = new GridLayout();
    gridLayoutForComp.numColumns = 3;

    viewUserComp.setLayout(gridLayoutForComp);
    viewUserComp.setLayoutData(getFirstGridData());

    this.userNameBwsBtn = this.formToolkit.createButton(viewUserComp, "", SWT.PUSH);
    this.userNameBwsBtn.setToolTipText(IMPORT_USER_NODES_TOOLTIP);
    this.userNameBwsBtn.setText(IMPORT_USER_NODES);

    new Label(viewUserComp, SWT.NONE).setText("User : ");

    this.userNameTextField = this.formToolkit.createText(viewUserComp, null, SWT.SINGLE | SWT.BORDER);
    GridData txtGridData = new GridData();
    txtGridData.widthHint = 300;
    txtGridData.grabExcessHorizontalSpace = false;
    txtGridData.horizontalAlignment = GridData.FILL;
    this.userNameTextField.setLayoutData(txtGridData);
    this.userNameTextField.setEditable(false);

    this.nodeAccessPageActionSet.addViewUserSelectionBtnListener();
  }

  /**
   * @param parentComp
   */
  private void addAccessBtnSection(final Composite parentComp) {
    Composite accessComp = new Composite(parentComp, SWT.RIGHT_TO_LEFT);

    GridLayout accessGridLayoutForComp = new GridLayout();
    accessGridLayoutForComp.numColumns = 4;

    accessComp.setLayout(accessGridLayoutForComp);
    accessComp.setLayoutData(getFirstGridData());

    this.allOwnerBtn = this.formToolkit.createButton(accessComp, "", SWT.PUSH);
    this.allOwnerBtn.setToolTipText(ALL_OWNER);
    this.allOwnerBtn.setText(ALL_OWNER);
    this.allOwnerBtn.setEnabled(false);

    this.allGrantBtn = this.formToolkit.createButton(accessComp, "", SWT.PUSH);
    this.allGrantBtn.setToolTipText(ALL_GRANT);
    this.allGrantBtn.setText(ALL_GRANT);
    this.allGrantBtn.setEnabled(false);

    this.allWriteBtn = this.formToolkit.createButton(accessComp, "", SWT.PUSH);
    this.allWriteBtn.setToolTipText(ALL_WRITE);
    this.allWriteBtn.setText(ALL_WRITE);
    this.allWriteBtn.setEnabled(false);

    this.allReadBtn = this.formToolkit.createButton(accessComp, "", SWT.PUSH);
    this.allReadBtn.setToolTipText(ALL_READ);
    this.allReadBtn.setText(ALL_READ);
    this.allReadBtn.setEnabled(false);


  }

  /**
   * @return
   */
  private GridData getFirstGridData() {
    GridData accessGridData = new GridData();
    accessGridData.grabExcessHorizontalSpace = true;
    accessGridData.horizontalAlignment = GridData.FILL;
    return accessGridData;
  }

  /**
   * Create table viewer section
   */
  private void createNodesTableViewerSection() {
    this.nodesTableSection =
        SectionUtil.getInstance().createSection(this.mainComposite, this.formToolkit, "Node(s) to manage");
    this.nodesTableSection.setLayoutData(getGridData());
    createNodesTableForm(this.formToolkit);
    this.nodesTableSection.setClient(this.nodesTableForm);
    this.nodesTableSection.setDescription("Add nodes for access or import from other user's access rights.");
    this.nodesTableSection.getDescriptionControl().setEnabled(false);
  }

  /**
   * Create NAT Table form
   *
   * @param toolkit toolkit instance
   */
  private void createNodesTableForm(final FormToolkit toolkit) {
    // create table form
    this.nodesTableForm = toolkit.createForm(this.nodesTableSection);
    // get parameters
    this.nodesTableForm.getBody().setLayout(new GridLayout());
    this.nodesTableForm.getBody().setLayoutData(getGridData());

    createActionSections();
    createNodesTableViewer();
    createNodeTableToolbarActions();
  }

  /**
   * Create grid table viewer
   */
  public void createNodesTableViewer() {
    // Create Filter text
    createNodesFilterTxt();
    this.nodeTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.nodesTableForm.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        GridDataUtil.getInstance().getHeightHintGridData(200));


    this.nodeTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.nodeTableViewer.setInput(this.nodeTableInputSet);
    // Invokde GridColumnViewer sorter
    invokeNodesColumnSorter();
    // Create GridViewerColumns
    createGridViewerColumns();
    // Add selection listener
    addTableSelectionListener();
    // Add filters to the TableViewer
    addNodesFilters();

    this.nodeAccessPageActionSet.addAllNodeAccessBtnListener();

  }

  private void createNodesFilterTxt() {
    this.nodeFilterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.nodesTableForm.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.nodeFilterTxt.addModifyListener(modify -> {

      final String text = MultiNodeAccessPage.this.nodeFilterTxt.getText().trim();
      MultiNodeAccessPage.this.adminMultiNodeFilter.setFilterText(text);
      MultiNodeAccessPage.this.nodeTableViewer.refresh();
    });
    this.nodeFilterTxt.setFocus();
  }

  /**
   * This method adds selection listener
   */
  public void addTableSelectionListener() {
    this.nodeTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Node selection list is cleared before adding new selection list
        getSelectedNodeTableInputSet().clear();
        IStructuredSelection selection = (IStructuredSelection) MultiNodeAccessPage.this.nodeTableViewer.getSelection();

        final Iterator<NodeAccessInfo> nodeAccessInfoNode = selection.iterator();
        while (nodeAccessInfoNode.hasNext()) {
          final NodeAccessInfo nodeAccessInfo = nodeAccessInfoNode.next();
          getSelectedNodeTableInputSet().add(nodeAccessInfo);
        }
      }
    });

  }

  /**
   * @param flag as input
   */
  public void enableDisableAccessBtns(final boolean flag) {
    getAllReadBtn().setEnabled(flag);
    getAllWriteBtn().setEnabled(flag);
    getAllOwnerBtn().setEnabled(flag);
    getAllGrantBtn().setEnabled(flag);
  }

  /**
   * This method adds filter to table
   */
  public void addNodesFilters() {
    this.adminMultiNodeFilter = new MultiNodeFilter();
    this.nodeTableViewer.addFilter(this.adminMultiNodeFilter);
  }

  /**
   * This method adds sorter to table
   */
  public void invokeNodesColumnSorter() {
    this.nodeSorter = new MultiNodeSorter();
    this.nodeTableViewer.setComparator(this.nodeSorter);
  }


  /**
   * @param composite
   */
  private void createNodeTableToolbarActions() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = this.toolBarManager.createControl(this.nodesTableSection);
    toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));

    this.nodeAccessPageActionSet.addNodeAction();
    this.nodeAccessPageActionSet.deleteNodeAction();
    this.toolBarManager.add(new Separator());
    this.nodeAccessPageActionSet.importNodeAction();

    this.toolBarManager.update(true);
    this.nodesTableSection.setTextClient(toolbar);
  }

  /**
   * This method creates gridtable columns
   */
  public void createGridViewerColumns() {
    createNodeTypeColumn();
    createNodeNameColumn();
    createNodeDescriptionColumn();
    createReadAccessColumn();
    createWriteAccessColumn();
    createGrantAccessColumn();
    createOwnerAccessColumn();
  }

  /**
   * create user name columnset
   */
  private void createNodeNameColumn() {
    final GridViewerColumn nodeNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.nodeTableViewer, COL_NODE_NAME, COL_NODE_NAME_SIZE);
    nodeNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof NodeAccessInfo) {
          NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          return null != nodeAccessInfo.getNodeName() ? nodeAccessInfo.getNodeName() : "";
        }
        return "";
      }
    });
    nodeNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        nodeNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.nodeSorter, this.nodeTableViewer));
  }

  /**
   * create user name columnset
   */
  private void createNodeTypeColumn() {
    final GridViewerColumn nodeTypeColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.nodeTableViewer, COL_NODE_TYPE, COL_NODE_TYPE_SIZE);
    nodeTypeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof NodeAccessInfo) {
          NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          if (Arrays.asList(NodeType.getTypeNames()).contains(nodeAccessInfo.getAccess().getNodeType())) {
            NodeType nodeType = NodeType.getNodeType(nodeAccessInfo.getAccess().getNodeType());
            return null != nodeType.getModelType().getTypeName() ? nodeType.getModelType().getTypeName() : "";
          }
          else {
            return nodeAccessInfo.getAccess().getNodeType();
          }
        }
        return "";
      }
    });
    nodeTypeColumn.getColumn().setSort(1);
    nodeTypeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        nodeTypeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0, this.nodeSorter, this.nodeTableViewer));
  }

  /**
   * create user name columnset
   */
  private void createNodeDescriptionColumn() {
    final GridViewerColumn nodeDescColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.nodeTableViewer, COL_DESCRIPTION, COL_DESC_SIZE);
    nodeDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        if (element instanceof NodeAccessInfo) {
          NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          return null != nodeAccessInfo.getNodeDesc() ? nodeAccessInfo.getNodeDesc() : "";
        }
        return "";
      }
    });
    nodeDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        nodeDescColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.nodeSorter, this.nodeTableViewer));
  }

  private void createReadAccessColumn() {
    final GridColumn readGridCol = new GridColumn(this.nodeTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    readGridCol.setWidth(100);
    readGridCol.setText("No");
    readGridCol.setSummary(false);
    final GridViewerColumn readColumn = new GridViewerColumn(this.nodeTableViewer, readGridCol);
    readColumn.getColumn().setText(COL_READ);
    readColumn.getColumn().setWidth(COL_CHECKBOX_SIZE);
    readColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        final Object element = cell.getElement();
        if (element instanceof NodeAccessInfo) {
          final NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), nodeAccessInfo.getAccess().isRead());
          gridItem.setCheckable(cell.getVisualIndex(), true);
          // To Mark the Read Column in GRAY color
          cell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
        }
      }

      @Override
      public String getToolTipText(final Object element) {
        return "";
      }
    });

    readColumn.getColumn().setCheckable(false);

    readColumn.setEditingSupport(new CheckEditingSupport(readColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        NodeAccessInfo node = (NodeAccessInfo) arg0;
        boolean checkStatus = (boolean) arg1;
        node.getAccess().setRead(checkStatus);
        node.getAccess().setWrite(DISABLE);
        node.getAccess().setGrant(DISABLE);
        node.getAccess().setOwner(DISABLE);
        MultiNodeAccessPage.this.nodeTableViewer.refresh();
      }
    });

    readColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        readColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.nodeSorter, this.nodeTableViewer));
  }

  /**
   * creates write access rights column
   */
  private void createWriteAccessColumn() {
    GridColumn writeGridCol = new GridColumn(this.nodeTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    writeGridCol.setWidth(100);
    writeGridCol.setText(ApicConstants.USED_NO_DISPLAY);
    writeGridCol.setSummary(false);
    GridViewerColumn writeColumn = new GridViewerColumn(this.nodeTableViewer, writeGridCol);
    writeColumn.getColumn().setText(COL_WRITE);
    writeColumn.getColumn().setWidth(COL_CHECKBOX_SIZE);
    writeColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        final Object element = cell.getElement();
        if (element instanceof NodeAccessInfo) {
          final NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();

          boolean isChecked = (null != nodeAccessInfo.getAccess()) && nodeAccessInfo.getAccess().isWrite();
          gridItem.setChecked(cell.getVisualIndex(), isChecked);

          gridItem.setCheckable(cell.getVisualIndex(), true);
        }
      }
    });
    writeColumn.setEditingSupport(new CheckEditingSupport(writeColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        NodeAccessInfo node = (NodeAccessInfo) arg0;
        boolean checkStatus = (boolean) arg1;
        if (checkStatus) {
          node.getAccess().setRead(checkStatus);
          node.getAccess().setWrite(checkStatus);
        }
        else {
          node.getAccess().setWrite(DISABLE);
        }
        node.getAccess().setGrant(DISABLE);
        node.getAccess().setOwner(DISABLE);
        MultiNodeAccessPage.this.nodeTableViewer.refresh();
      }
    });

    writeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        writeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.nodeSorter, this.nodeTableViewer));
  }

  /**
   * creates grant access rights column
   */
  private void createGrantAccessColumn() {
    final GridColumn grantGridCol = new GridColumn(this.nodeTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    grantGridCol.setWidth(100);
    grantGridCol.setText("No");
    grantGridCol.setSummary(false);
    final GridViewerColumn grantColumn = new GridViewerColumn(this.nodeTableViewer, grantGridCol);
    grantColumn.getColumn().setText(COL_GRANT);
    grantColumn.getColumn().setWidth(COL_CHECKBOX_SIZE);
    grantColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {


        final Object element = cell.getElement();

        if (element instanceof NodeAccessInfo) {
          final NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();

          boolean isChecked = (null != nodeAccessInfo.getAccess()) && nodeAccessInfo.getAccess().isGrant();
          gridItem.setChecked(cell.getVisualIndex(), isChecked);

          gridItem.setCheckable(cell.getVisualIndex(), true);
        }

      }
    });
    grantColumn.setEditingSupport(new CheckEditingSupport(grantColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        NodeAccessInfo node = (NodeAccessInfo) arg0;
        boolean checkStatus = (boolean) arg1;
        if (checkStatus) {
          node.getAccess().setRead(checkStatus);
          node.getAccess().setWrite(checkStatus);
          node.getAccess().setGrant(checkStatus);
        }
        else {
          node.getAccess().setGrant(DISABLE);
        }
        node.getAccess().setOwner(DISABLE);
        MultiNodeAccessPage.this.nodeTableViewer.refresh();
      }
    });

    grantColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        grantColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_5, this.nodeSorter, this.nodeTableViewer));
  }

  /**
   * creates owner access rights column
   */
  private void createOwnerAccessColumn() {
    final GridColumn ownerGridCol = new GridColumn(this.nodeTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    ownerGridCol.setWidth(100);
    ownerGridCol.setText("No");
    ownerGridCol.setSummary(false);
    final GridViewerColumn ownerColumn = new GridViewerColumn(this.nodeTableViewer, ownerGridCol);
    ownerColumn.getColumn().setText(COL_OWNER);
    ownerColumn.getColumn().setWidth(COL_CHECKBOX_SIZE);
    ownerColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {


        final Object element = cell.getElement();
        if (element instanceof NodeAccessInfo) {
          final NodeAccessInfo nodeAccessInfo = (NodeAccessInfo) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();

          boolean isChecked = (null != nodeAccessInfo.getAccess()) && nodeAccessInfo.getAccess().isOwner();
          gridItem.setChecked(cell.getVisualIndex(), isChecked);

          gridItem.setCheckable(cell.getVisualIndex(), true);
        }
      }
    });
    ownerColumn.setEditingSupport(new CheckEditingSupport(ownerColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        NodeAccessInfo node = (NodeAccessInfo) arg0;
        boolean checkStatus = (boolean) arg1;
        if (checkStatus) {
          node.getAccess().setRead(checkStatus);
          node.getAccess().setWrite(checkStatus);
          node.getAccess().setGrant(checkStatus);
          node.getAccess().setOwner(checkStatus);
        }
        else {
          node.getAccess().setOwner(DISABLE);
        }

        MultiNodeAccessPage.this.nodeTableViewer.refresh();
      }
    });

    ownerColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        ownerColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_6, this.nodeSorter, this.nodeTableViewer));
  }

  // Start of Users Table Viewer

  /**
   * Create table viewer section
   */
  private void createUsersTableViewerSection() {
    this.usersTableSection = SectionUtil.getInstance().createSection(this.mainComposite, this.formToolkit, "Users");
    this.usersTableSection.setLayoutData(getGridData());
    createUsersTableForm(this.formToolkit);
    this.usersTableSection.setClient(this.usersTableForm);
    this.usersTableSection.setDescription("Select users to whom the above access rights would be applied.");
    this.usersTableSection.getDescriptionControl().setEnabled(false);
  }

  /**
   * Create NAT Table form
   *
   * @param toolkit toolkit instance
   */
  private void createUsersTableForm(final FormToolkit toolkit) {
    // create table form
    this.usersTableForm = toolkit.createForm(this.usersTableSection);
    // Create Filter text
    createUserFilterTxt();
    // get parameters
    this.usersTableForm.getBody().setLayout(new GridLayout());
    this.usersTableForm.getBody().setLayoutData(getGridData());

    createNewUsersGridTabViewer();

    setUserTabViewerProviders();

    addUserTableSelectionListener();

    addUserFilters();

    invokeUserColumnSorter();

    createUserTableToolbarActions();
  }

  /**
   * This method creates filter text
   */
  private void createUserFilterTxt() {
    this.userFilterText = TextUtil.getInstance().createFilterText(this.formToolkit, this.usersTableForm.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.userFilterText.addModifyListener(modify -> {
      final String text = MultiNodeAccessPage.this.userFilterText.getText().trim();
      MultiNodeAccessPage.this.newUserFilters.setFilterText(text);
      MultiNodeAccessPage.this.newUserTabViewer.refresh();
    });
    // ICDM-183
    this.userFilterText.setFocus();

  }

  /**
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createNewUsersGridTabViewer() {
    this.newUserTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.usersTableForm.getBody(),
        SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createUserGridViewerColumns();
  }

  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createUserGridViewerColumns() {
    createUserNameColumn();
    createUserIdColumn();
    createDeptColumn();
  }

  /**
   * This method adds user name column to the addNewUserTableViewer
   */
  private void createUserNameColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.newUserTabViewer, COL_USER_NAME, COL_USER_NAME_SIZE);
    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(getUserSelectionAdapter(userNameColumn.getColumn(), 0));
  }

  /**
   * This method adds user id column to the addNewUserTableViewer
   */
  private void createUserIdColumn() {
    final GridViewerColumn userIdColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.newUserTabViewer, COL_NT_ID, COL_NT_ID_SIZE);
    // Add column selection listener
    userIdColumn.getColumn().addSelectionListener(getUserSelectionAdapter(userIdColumn.getColumn(), 1));
  }

  /**
   * This method adds department column to the addNewUserTableViewer
   */
  private void createDeptColumn() {
    final GridViewerColumn deptColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.newUserTabViewer, COL_DEPARTMENT, COL_DEPT_SIZE);
    // Add column selection listener
    deptColumn.getColumn().addSelectionListener(getUserSelectionAdapter(deptColumn.getColumn(), 2));
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setUserTabViewerProviders() {
    this.newUserTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.newUserTabViewer.setLabelProvider(new NewUserLabelProvider());
    this.newUserTabViewer.setInput(this.userTableInputSet);
  }

  /**
   * @param composite
   */
  private void createUserTableToolbarActions() {
    this.toolBarManager = new ToolBarManager(SWT.FLAT);
    ToolBar toolbar = this.toolBarManager.createControl(this.usersTableSection);
    toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
    this.nodeAccessPageActionSet.addUserAction();
    this.nodeAccessPageActionSet.deleteUserAction();
    this.toolBarManager.update(true);
    this.usersTableSection.setTextClient(toolbar);
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addUserTableSelectionListener() {
    this.newUserTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // User selection list is cleared before adding new selection list
        getSelectedUserTableInputSet().clear();
        IStructuredSelection selection =
            (IStructuredSelection) MultiNodeAccessPage.this.newUserTabViewer.getSelection();

        final Iterator<User> userNode = selection.iterator();
        while (userNode.hasNext()) {
          final User user = userNode.next();
          getSelectedUserTableInputSet().add(user);
        }
      }
    });
  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addUserFilters() {
    this.newUserFilters = new NewUsersFilter();
    // Add PIDC Attribute TableViewer filter
    this.newUserTabViewer.addFilter(this.newUserFilters);

  }

  /**
   * Add sorter for the table columns
   */
  private void invokeUserColumnSorter() {
    this.userTabSorter = new NewUserGridTableViewerSorter();
    this.newUserTabViewer.setComparator(this.userTabSorter);
  }

  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getUserSelectionAdapter(final GridColumn column, final int index) {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        sortColumn(column, index);
      }
    };
  }

  /**
   * @param column
   * @param index
   */
  private void sortColumn(final GridColumn column, final int index) {
    MultiNodeAccessPage.this.userTabSorter.setColumn(index);
    int direction = MultiNodeAccessPage.this.userTabSorter.getDirection();
    for (int i = 0; i < MultiNodeAccessPage.this.newUserTabViewer.getGrid().getColumnCount(); i++) {
      if (i == index) {
        if (direction == ApicConstants.ASCENDING) {
          column.setSort(SWT.DOWN);
        }
        else if (direction == ApicConstants.DESCENDING) {
          column.setSort(SWT.UP);
        }
      }
      if (i != index) {
        MultiNodeAccessPage.this.newUserTabViewer.getGrid().getColumn(i).setSort(SWT.NONE);
      }
    }
    MultiNodeAccessPage.this.newUserTabViewer.refresh();
  }

  /**
   * @return This method defines GridData
   */
  private GridData getGridData() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ToolBarManager getToolBarManager() {
    return this.toolBarManager;
  }

  /**
   * @return the selectedUser
   */
  public User getSelectedUser() {
    return this.selectedUser;
  }

  /**
   * @param selectedUser the selectedUser to set
   */
  public void setSelectedUser(final User selectedUser) {
    this.selectedUser = selectedUser;
  }

  /**
   * @return the userTableInputSet
   */
  public Set<User> getUserTableInputSet() {
    return this.userTableInputSet;
  }

  /**
   * @return the nodeTableViewer
   */
  public GridTableViewer getNodeTableViewer() {
    return this.nodeTableViewer;
  }

  /**
   * @param nodeTableViewer the nodeTableViewer to set
   */
  public void setNodeTableViewer(final GridTableViewer nodeTableViewer) {
    this.nodeTableViewer = nodeTableViewer;
  }


  /**
   * @return the newUserTabViewer
   */
  public GridTableViewer getNewUserTabViewer() {
    return this.newUserTabViewer;
  }

  /**
   * @param newUserTabViewer the newUserTabViewer to set
   */
  public void setNewUserTabViewer(final GridTableViewer newUserTabViewer) {
    this.newUserTabViewer = newUserTabViewer;
  }

  /**
   * @return the nodeTableInputSet
   */
  public Set<NodeAccessInfo> getNodeTableInputSet() {
    return this.nodeTableInputSet;
  }

  /**
   * @return the allOwnerBtn
   */
  public Button getAllOwnerBtn() {
    return this.allOwnerBtn;
  }

  /**
   * @param allOwnerBtn the allOwnerBtn to set
   */
  public void setAllOwnerBtn(final Button allOwnerBtn) {
    this.allOwnerBtn = allOwnerBtn;
  }

  /**
   * @return the allReadBtn
   */
  public Button getAllReadBtn() {
    return this.allReadBtn;
  }

  /**
   * @param allReadBtn the allReadBtn to set
   */
  public void setAllReadBtn(final Button allReadBtn) {
    this.allReadBtn = allReadBtn;
  }

  /**
   * @return the allWriteBtn
   */
  public Button getAllWriteBtn() {
    return this.allWriteBtn;
  }

  /**
   * @param allWriteBtn the allWriteBtn to set
   */
  public void setAllWriteBtn(final Button allWriteBtn) {
    this.allWriteBtn = allWriteBtn;
  }

  /**
   * @return the allGrantBtn
   */
  public Button getAllGrantBtn() {
    return this.allGrantBtn;
  }

  /**
   * @param allGrantBtn the allGrantBtn to set
   */
  public void setAllGrantBtn(final Button allGrantBtn) {
    this.allGrantBtn = allGrantBtn;
  }

  /**
   * @return the selectedNodeTableInputSet
   */
  public Set<NodeAccessInfo> getSelectedNodeTableInputSet() {
    return this.selectedNodeTableInputSet;
  }

  /**
   * @return the selectedUserTableInputSet
   */
  public Set<User> getSelectedUserTableInputSet() {
    return this.selectedUserTableInputSet;
  }

  /**
   * @return the userNameBwsBtn
   */
  public Button getUserNameBwsBtn() {
    return this.userNameBwsBtn;
  }

  /**
   * @param userNameBwsBtn the userNameBwsBtn to set
   */
  public void setUserNameBwsBtn(final Button userNameBwsBtn) {
    this.userNameBwsBtn = userNameBwsBtn;
  }

  /**
   * @return the userNameTextField
   */
  public Text getUserNameTextField() {
    return this.userNameTextField;
  }

  /**
   * @param userNameTextField the userNameTextField to set
   */
  public void setUserNameTextField(final Text userNameTextField) {
    this.userNameTextField = userNameTextField;
  }
}
