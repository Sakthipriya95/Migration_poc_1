/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.dialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.admin.ui.Activator;
import com.bosch.caltool.admin.ui.editor.filter.NodeSelectionTableViewerFilter;
import com.bosch.caltool.admin.ui.editor.provider.NodeSelectionTableViewerLabelProvider;
import com.bosch.caltool.admin.ui.editor.sorter.NodeSelectionTableViewerSorter;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.apic.NodeType;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author say8cob
 */
public class NodeSelectionDialog extends AbstractDialog {


  /**
   * Dialog title
   */
  private static final String SEARCH = "Search";

  /**
   * GridTableViewer instance for add new user
   */
  private GridTableViewer nodeTabViewer;

  /**
   * nodesTableViewerSorter instance
   */
  private NodeSelectionTableViewerSorter nodesTableViewerSorter;

  /**
   * Composite instance
   */
  private Composite nodeComposite;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;

  /**
   * Default shell Title
   */
  private static final String SHELL_TITLE = "Select Node(s)";

  /**
   * Default dialog Title
   */
  private static final String DIALOG_TITLE = "Select Node(s)";

  /**
   * Composite instance
   */
  private Composite composite;

  private Text keyWordTextField;

  private Combo nodeTypeCombo;

  private Button searchBtn;


  Set<NodeAccessInfo> selectedNodeAccess = new HashSet<>();

  private Button okBtn;

  private NodeSelectionTableViewerFilter nodeSelectionTableViewerFilter;

  private Text filterTxt;

  /**
   * Constant for mandatory msg
   */
  private static final String MSG_MANDATORY = "This field is mandatory.";
  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();

  private Map<Long, NodeAccessInfo> allSpecialNodeAccess = new HashMap<>();

  private Combo specialNodeTypeCombo;

  /**
   * @param parentShell as input
   */
  public NodeSelectionDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set the title
    setTitle(NodeSelectionDialog.DIALOG_TITLE);

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(NodeSelectionDialog.SHELL_TITLE);
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
//    dialog grid
    this.nodeComposite = (Composite) super.createDialogArea(parent);
    this.nodeComposite.setLayout(new GridLayout());
    createComposite();

    return this.nodeComposite;
  }

  /**
   * This method initializes composite
   *
   * @throws ApicWebServiceException
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.nodeComposite);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   *
   * @throws ApicWebServiceException
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of Node(s)");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   *
   * @throws ApicWebServiceException
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    createSearchItems();
    // Create Filter text
    createFilterTxt();

    // Create new users grid tableviewer
    createNewUsersGridTabViewer();

    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();

    // Set input to the addNewUserTableViewer
    setTabViewerInput();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();

    // Adds double click selection listener to the addNewUserTableViewer
    addDoubleClickListener();

    // Add filters to the TableViewer
    addFilters();

    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * This method creates the search items
   */
  private void createSearchItems() {
    Composite searchComp = new Composite(this.form.getBody(), SWT.NONE);

    GridLayout gridLayoutForComp = new GridLayout();
    gridLayoutForComp.numColumns = 5;
    GridData variantGridData = new GridData();
    variantGridData.grabExcessHorizontalSpace = true;
    variantGridData.horizontalAlignment = GridData.FILL;

    searchComp.setLayout(gridLayoutForComp);
    searchComp.setLayoutData(variantGridData);

    new Label(searchComp, SWT.NONE).setText("Type : ");

    this.nodeTypeCombo = new Combo(searchComp, SWT.READ_ONLY);
    GridData comboGridData = new GridData();
    this.nodeTypeCombo.setLayoutData(comboGridData);

    //get list of node access type
    getNodeAccessTypeList();

    new Label(searchComp, SWT.NONE).setText("Keyword : ");

    this.keyWordTextField = this.formToolkit.createText(searchComp, null, SWT.SINGLE | SWT.BORDER);
    GridData txtGridData = new GridData();
    txtGridData.widthHint = 250;
    txtGridData.grabExcessHorizontalSpace = false;
    txtGridData.horizontalAlignment = GridData.FILL;
    this.keyWordTextField.setLayoutData(txtGridData);
    ControlDecoration keyWordTextDescription = new ControlDecoration(this.keyWordTextField, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(keyWordTextDescription, MSG_MANDATORY);

    this.keyWordTextField.addModifyListener(evnt -> checkSearchBtn());

    this.searchBtn = this.formToolkit.createButton(searchComp, "", SWT.PUSH);
    this.searchBtn.setToolTipText(SEARCH);
    this.searchBtn.setText(SEARCH);
    this.searchBtn.setEnabled(false);
    this.searchBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        try {
          String typeCode = NodeType.getNodeTypeByTypeName(NodeSelectionDialog.this.nodeTypeCombo.getText())
              .getModelType().getTypeCode();
          Map<Long, NodeAccessInfo> nodeAccessMap = new NodeAccessServiceClient().findNodeAccessForUserAndNodeNames(
              typeCode, new CurrentUserBO().getUserName(), NodeSelectionDialog.this.keyWordTextField.getText());
          NodeSelectionDialog.this.nodeTabViewer.setInput(nodeAccessMap.values());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented

      }
    });

    createSpecialNodeAccessCombo(searchComp);

    this.nodeTypeCombo.addSelectionListener(new SelectionListener() {

//    enable search button
      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        NodeSelectionDialog.this.specialNodeTypeCombo.deselectAll();
        NodeSelectionDialog.this.nodeTabViewer.setInput("");
        checkSearchBtn();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented
      }
    });
  }

  /**
   * @param searchComp
   */
  private void createSpecialNodeAccessCombo(final Composite searchComp) {
    
    new Label(searchComp, SWT.NONE).setText("");
    new Label(searchComp, SWT.NONE).setText("");
    new Label(searchComp, SWT.NONE).setText("(or)");
    new Label(searchComp, SWT.NONE).setText("");
    new Label(searchComp, SWT.NONE).setText("");
    
    new Label(searchComp, SWT.NONE).setText("Special Type :");

    this.specialNodeTypeCombo = new Combo(searchComp, SWT.READ_ONLY);
    this.specialNodeTypeCombo.setLayoutData(new GridData());

    getSpecialNodeAccessTypeList();

    this.specialNodeTypeCombo.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        NodeSelectionDialog.this.nodeTypeCombo.deselectAll();
        if (NodeSelectionDialog.this.specialNodeTypeCombo.getText() != null) {
          for (Entry<Long, NodeAccessInfo> nodeAccessInfoEntry : getAllSpecialNodeAccess().entrySet()) {
            if (CommonUtils.isEqual(NodeSelectionDialog.this.specialNodeTypeCombo.getText(),
                nodeAccessInfoEntry.getValue().getNodeName())) {
              NodeSelectionDialog.this.nodeTabViewer
                  .setInput(new HashSet<>(Arrays.asList(nodeAccessInfoEntry.getValue())));
              NodeSelectionDialog.this.keyWordTextField.setText("");
            }
          }
        }
        checkSearchBtn();
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not Implemented
      }
    });
  }

  /**
   *
   */
  private void getSpecialNodeAccessTypeList() {
    try {
      setAllSpecialNodeAccess(new NodeAccessServiceClient().getAllSpecialNodeAccess());
      Set<String> specialNodeAccess = new HashSet<>();
      for (NodeAccessInfo nodeAccessInfo : getAllSpecialNodeAccess().values()) {
        specialNodeAccess.add(nodeAccessInfo.getNodeName());
      }
      this.specialNodeTypeCombo.setItems(specialNodeAccess.toArray(new String[0]));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Adding Node types
   *
   * @throws ApicWebServiceException
   */
  private void getNodeAccessTypeList() {
    this.nodeTypeCombo.setItems(NodeType.getTypeNames());
  }

  /**
   * This method enables the search button
   */
  private void checkSearchBtn() {
//    enable search button if keywork text field is not empty
    this.searchBtn.setEnabled(!this.keyWordTextField.getText().isEmpty() && (this.nodeTypeCombo.getText() != null));
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
//    to get and trim the entered text in the filter field
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.filterTxt.addModifyListener(modify -> {
      final String text = NodeSelectionDialog.this.filterTxt.getText().trim();
      NodeSelectionDialog.this.nodeSelectionTableViewerFilter.setFilterText(text);
      NodeSelectionDialog.this.nodeTabViewer.refresh();
    });
    // ICDM-183
    this.filterTxt.setFocus();

  }

  /**
   * This method creates grid table viewer
   */
  private void createNewUsersGridTabViewer() {
    this.nodeTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createGridViewerColumns() {
    createUserNameColumn();
    createUserIdColumn();
  }

  /**
   * This method adds user name column to the addNewUserTableViewer
   */
  private void createUserNameColumn() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.nodeTabViewer, "Name", 200);
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(getSelectionAdapter(nameColumn.getColumn(), 0));
  }

  /**
   * This method adds user id column to the addNewUserTableViewer
   */
  private void createUserIdColumn() {
    final GridViewerColumn descriptionColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.nodeTabViewer, "Description", 300);
    // Add column selection listener
    descriptionColumn.getColumn().addSelectionListener(getSelectionAdapter(descriptionColumn.getColumn(), 1));
  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.nodeTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.nodeTabViewer.setLabelProvider(new NodeSelectionTableViewerLabelProvider());
  }

  /**
   * This method sets the input to the newNodeTabViewer
   */
  protected void setTabViewerInput() {
    this.nodeTabViewer.setInput("");
  }

  /**
   * This method adds selection listener to the newNodeTabViewer
   */
  private void addTableSelectionListener() {
    this.nodeTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) NodeSelectionDialog.this.nodeTabViewer.getSelection();
        if ((selection == null) || (selection.isEmpty())) {
          NodeSelectionDialog.this.okBtn.setEnabled(false);
        }
        else {
          final Iterator<NodeAccessInfo> nodeAccessInfo = selection.iterator();
          while (nodeAccessInfo.hasNext()) {
            final NodeAccessInfo node = nodeAccessInfo.next();
            NodeSelectionDialog.this.selectedNodeAccess.add(node);
          }
          NodeSelectionDialog.this.okBtn.setEnabled(true);
        }
      }
    });
  }

  /**
   * This method adds double click listener to the newNodeTabViewer
   */
  private void addDoubleClickListener() {
//    double click also invokes ok button event
    this.nodeTabViewer.addDoubleClickListener(value -> {
      final IStructuredSelection selection =
          (IStructuredSelection) NodeSelectionDialog.this.nodeTabViewer.getSelection();

      final Iterator<NodeAccessInfo> nodeAccessInfo = selection.iterator();
      while (nodeAccessInfo.hasNext()) {
        final NodeAccessInfo node = nodeAccessInfo.next();
        NodeSelectionDialog.this.selectedNodeAccess.add(node);
      }
      super.okPressed();
    });
  }


  /**
   * This method adds the filter instance to newNodeTabViewer
   */
  private void addFilters() {
    // Add PIDC Attribute TableViewer filter
    this.nodeSelectionTableViewerFilter = new NodeSelectionTableViewerFilter();
    this.nodeTabViewer.addFilter(this.nodeSelectionTableViewerFilter);

  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
//    to sort table columns on a click
    this.nodesTableViewerSorter = new NodeSelectionTableViewerSorter();
    this.nodeTabViewer.setComparator(this.nodesTableViewerSorter);
  }


  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
//    create add and cancel button
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Add", false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    // clear the selected nodes
    getSelectedNodeAccess().clear();
    super.cancelPressed();
  }

  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index) {
    return new SelectionAdapter() {

//      to sort the column
      @Override
      public void widgetSelected(final SelectionEvent event) {
        sortColumn(column, index);
      }
    };
  }


  /**
   * @param column column
   * @param index index
   */
  private void sortColumn(final GridColumn column, final int index) {
//    sort column logic
    NodeSelectionDialog.this.nodesTableViewerSorter.setColumn(index);
    int direction = NodeSelectionDialog.this.nodesTableViewerSorter.getDirection();
    for (int i = 0; i < NodeSelectionDialog.this.nodeTabViewer.getGrid().getColumnCount(); i++) {
      if (i == index) {
        if (direction == ApicConstants.ASCENDING) {
          column.setSort(SWT.DOWN);
        }
        else if (direction == ApicConstants.DESCENDING) {
          column.setSort(SWT.UP);
        }
      }
      if (i != index) {
        NodeSelectionDialog.this.nodeTabViewer.getGrid().getColumn(i).setSort(SWT.NONE);
      }
    }
    NodeSelectionDialog.this.nodeTabViewer.refresh();
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * @return the selectedNodeAccess
   */
  public Set<NodeAccessInfo> getSelectedNodeAccess() {
    return this.selectedNodeAccess;
  }


  /**
   * @param selectedNodeAccess the selectedNodeAccess to set
   */
  public void setSelectedNodeAccess(final Set<NodeAccessInfo> selectedNodeAccess) {
    this.selectedNodeAccess = selectedNodeAccess;
  }


  /**
   * @return the allSpecialNodeAccess
   */
  public Map<Long, NodeAccessInfo> getAllSpecialNodeAccess() {
    return this.allSpecialNodeAccess;
  }


  /**
   * @param allSpecialNodeAccess the allSpecialNodeAccess to set
   */
  public void setAllSpecialNodeAccess(final Map<Long, NodeAccessInfo> allSpecialNodeAccess) {
    this.allSpecialNodeAccess = allSpecialNodeAccess;
  }

}
