/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.editors.pages.PredefinedAttributesPage;
import com.bosch.caltool.apic.ui.sorter.PredefinedValListTabSorter;
import com.bosch.caltool.apic.ui.table.filters.PredfndAttrValTabFilter;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.AddPredfndValLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * ICDM-2593 dalog to add predefined values for a grouped attribute
 *
 * @author dja7cob
 */
public class AddNewPredefinedValDialog extends AbstractDialog {

  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Add Predefined Value";
  /**
   * Dialog width
   */
  private static final int DIALOG_WIDTH = 750;
  /**
   * Dialog height
   */
  private static final int DIALOG_NORM_HEIGHT = 700;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
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
   * Table viewer to display all the values from which predefined value can be selected
   */
  private GridTableViewer valListTableViewer;
  /**
   * attrListTabFilter GridTableViewer filter instance
   */
  private PredfndAttrValTabFilter attrListTabFilter;
  /**
   * attrListTabFilter GridTableViewer sorter instance
   */
  private AbstractViewerSorter valListTabSorter;
  /**
   * Text instance
   */
  private Text filterTxt;
  /**
   * Button instance
   */
  private Button saveBtn;

  /**
   * List of predefined values selected
   */
  private List<AttributeValue> selPredefinedValList = new ArrayList<>();
  /**
   * Predefined attribute for which predefined value should be added
   */
  private final Attribute selPredefinedAttr;
  /**
   * Map of selected predefined attribute and value
   */
  private final Map<Attribute, AttributeValue> selPredefinedValMap;
  /**
   * PredefinedAttributesPage instance
   */
  private final PredefinedAttributesPage predefinedAttributesPage;
  private final EditValueDialog editValDialog;

  /**
   * @param parentShell instance
   * @param attr predefined attribute selected
   * @param predefinedAttributesPage instance
   * @param selPredefinedValMap Map of selected predefined attribute and value
   * @param editValDialog instance
   */
  public AddNewPredefinedValDialog(final Shell parentShell, final Attribute attr,
      final PredefinedAttributesPage predefinedAttributesPage, final Map<Attribute, AttributeValue> selPredefinedValMap,
      final EditValueDialog editValDialog) {
    super(parentShell);
    this.selPredefinedAttr = attr;
    this.selPredefinedValMap = selPredefinedValMap;
    this.predefinedAttributesPage = predefinedAttributesPage;
    this.editValDialog = editValDialog;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);

    // Set the title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Select Predefined Value for attribute : " + this.selPredefinedAttr.getName(),
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    newShell.layout(true, true);

    // Constants for the Width and height
    final Point newSize = newShell.computeSize(DIALOG_WIDTH, DIALOG_NORM_HEIGHT, true);
    newShell.setSize(newSize);
    super.configureShell(newShell);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // Create composite
    createComposite();
    parent.layout(true, true);
    return this.top;
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    this.composite.setLayoutData(gridData);
    // create table section
    createTableSection();

  }

  /**
   * on ok pressed
   */
  @Override
  protected void okPressed() {
    // On ok pressed,add the selected attribute and value to the map
    this.selPredefinedValMap.put(this.selPredefinedAttr, this.selPredefinedValList.get(0));
    this.editValDialog.getSaveBtn().setEnabled(true);
    // Refresh the attributes table in the predefined attributes page
    this.predefinedAttributesPage.getPredefinedAttrValTableViewer().refresh();
    super.okPressed();
  }

  /**
   *
   */
  private void createTableSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "List of values");
    this.section.setLayoutData(gridData);
    // Create table form
    createTableForm();
    this.section.setClient(this.form);
  }

  /**
   * Create table form
   */
  private void createTableForm() {
    this.form = this.formToolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    // Create filter text for table
    createFilterTxt();
    // create table to display the values
    createValListTable();
    // Add double click listener for the table
    addDoubleClickListener();
    // Add selection listener for the table
    addTableSelectionListener();
    // add filters for the table
    addFilters();
  }

  /**
   * This method defines the activities to be performed when double clicked on the table
   *
   * @param valListTableViewer
   */
  private void addDoubleClickListener() {
    this.valListTableViewer
        .addDoubleClickListener(event -> Display.getDefault().asyncExec(AddNewPredefinedValDialog.this::okPressed));
  }

  /**
   * This method add selection listener to valTableViewer
   */
  private void addTableSelectionListener() {
    this.valListTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * Table seection
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Add selected values to the selPredefinedValList list
        final List<AttributeValue> selValList = getSelValFromTabViewer();
        AddNewPredefinedValDialog.this.selPredefinedValList = selValList;
        // validate ok button
        validateOkBtn(selValList);
      }
    });
  }

  private void validateOkBtn(final List<AttributeValue> selValList) {
    if ((null != selValList) && !selValList.isEmpty()) {
      this.saveBtn.setEnabled(true);
    }
  }

  /**
   * @return Attribute
   */
  protected List<AttributeValue> getSelValFromTabViewer() {
    List<AttributeValue> selValList = new ArrayList<>();
    final IStructuredSelection selection = (IStructuredSelection) this.valListTableViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final List<IStructuredSelection> elementList = selection.toList();
      if (elementList.get(0) instanceof AttributeValue) {
        selValList.addAll((Collection<? extends AttributeValue>) elementList);
      }
    }
    return selValList;
  }

  /**
  *
  */
  private void addFilters() {
    this.attrListTabFilter = new PredfndAttrValTabFilter();
    this.valListTableViewer.addFilter(this.attrListTabFilter);
  }

  /**
  *
  */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.filterTxt.addModifyListener(event -> {
      final String text = AddNewPredefinedValDialog.this.filterTxt.getText().trim();
      AddNewPredefinedValDialog.this.attrListTabFilter.setFilterText(text);
      AddNewPredefinedValDialog.this.valListTableViewer.refresh();
    });

    this.filterTxt.setFocus();
  }

  /**
  *
  */
  private void createValListTable() {
    this.valListTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL, GridDataUtil.getInstance().getHeightHintGridData(350));
    // content provider for the table
    this.valListTableViewer.setContentProvider(new ArrayContentProvider());
    this.valListTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.valListTableViewer.getGrid().setLinesVisible(true);
    this.valListTableViewer.getGrid().setHeaderVisible(true);
    // Create sorter for the table
    this.valListTabSorter = new PredefinedValListTabSorter();
    this.valListTableViewer.setComparator(this.valListTabSorter);
    // Create GridViewerColumns
    createValListColumn();
    createValDescColumn();
    // Set input for the value list table
    this.valListTableViewer.setInput(new AttributeClientBO(this.selPredefinedAttr).getAttrValues());
  }

  /**
   * Create column to display attribute value description
   */
  private void createValDescColumn() {
    final GridViewerColumn attrDescColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valListTableViewer, "Description", 400);
    // Add column selection listener
    attrDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrDescColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_1, this.valListTabSorter, this.valListTableViewer));
    // Label provider for attribute value description column
    attrDescColumn.setLabelProvider(new AddPredfndValLabelProvider(ApicUiConstants.COLUMN_INDEX_1));
  }

  /**
   * Create column to display attribute value name
   */
  private void createValListColumn() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valListTableViewer, "Value", 300);
    // Add column selection listener
    attrNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        attrNameColumn.getColumn(), ApicUiConstants.COLUMN_INDEX_0, this.valListTabSorter, this.valListTableViewer));
    // Label provider for attribute value name column
    attrNameColumn.setLabelProvider(new AddPredfndValLabelProvider(ApicUiConstants.COLUMN_INDEX_0));
  }
}
