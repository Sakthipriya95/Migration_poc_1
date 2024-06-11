/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import com.bosch.caltool.apic.ui.sorter.PredefinedAttrListTabSorter;
import com.bosch.caltool.apic.ui.table.filters.PredfndAttrValTabFilter;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.AddPredfndAttrLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * ICDM-2593 Dialog to add predefined attribute for a grouped attribute
 *
 * @author dja7cob
 */
public class AddNewPredefinedAttrDialog extends AbstractDialog {

  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Add Predefined Attributes";
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
   * Table viewer to display all the attributes from which predefined attribute can be selected
   */
  private GridTableViewer allAttrListTableViewer;
  /**
   * PredfndAttrListTabFilter instance
   */
  private PredfndAttrValTabFilter attrListTabFilter;
  /**
   * Table Sorter for allAttrListTableViewer
   */
  private AbstractViewerSorter attrListTabSorter;
  /**
   * Text instance
   */
  private Text filterTxt;
  /**
   * Button instance
   */
  private Button saveBtn;

  /**
   * Set of predefined attributes selected
   */
  private final SortedSet<Attribute> predfndAttrSet = new TreeSet<>();
  /**
   * PredefinedAttributesPage instance
   */
  final PredefinedAttributesPage predefinedAttributesPage;
  /**
   * List of predefined attributes selected in the table
   */
  private List<Attribute> selAttrListfromTable;
  /**
   * EditValueDialog instance
   */
  private final EditValueDialog editValDialog;
  /**
   * List of attributes to be dispalyed in AddNewPredefinedAttrDialog table
   */
  private final List<Attribute> allAttrList;

  /**
   * @param parentShell instance
   * @param predefinedAttributesPage PredefinedAttributesPage
   * @param editValDialog instance
   * @param allAttrList instance
   */
  public AddNewPredefinedAttrDialog(final Shell parentShell, final PredefinedAttributesPage predefinedAttributesPage,
      final EditValueDialog editValDialog, final List<Attribute> allAttrList) {
    super(parentShell);
    this.predefinedAttributesPage = predefinedAttributesPage;
    this.editValDialog = editValDialog;
    this.allAttrList = new ArrayList<>(allAttrList);
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
    setMessage("Select one or more Attributes", IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set the dialog title
    newShell.setText(DIALOG_TITLE);
    newShell.layout(true, true);

    // Constants for the Width and height
    final Point newSize = newShell.computeSize(DIALOG_WIDTH, DIALOG_NORM_HEIGHT, true);
    newShell.setSize(newSize);
    super.configureShell(newShell);
  }


  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    parent.layout(true, true);
    return this.top;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Select", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
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
    // Create section for attributes list table
    createTableSection();

  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    // On ok pressed
    // Add selected predefined attributes to the predfndAttrSet
    AddNewPredefinedAttrDialog.this.predfndAttrSet.addAll(this.selAttrListfromTable);
    this.predefinedAttributesPage.getPredfndAttrList().addAll(this.predfndAttrSet);
    this.editValDialog.getSaveBtn().setEnabled(true);
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
    createTableForm();
    this.section.setClient(this.form);
  }

  /**
   * @param formToolkit2
   */
  private void createTableForm() {
    this.form = this.formToolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    // create filter text for the table
    createFilterTxt();
    // Create table to display the list of attributes
    createAttrListTable();
    // Add double click listener for the table
    addDoubleClickListener();
    // Add table selection listener
    addTableSelectionListener();
    // Add filter for the table
    addFilters();
  }

  /**
   * This method defines the activities to be performed when double clicked on the table
   *
   * @param allAttrListTableViewer
   */
  private void addDoubleClickListener() {
    this.allAttrListTableViewer
        .addDoubleClickListener(event -> Display.getDefault().asyncExec(AddNewPredefinedAttrDialog.this::okPressed));

  }

  /**
   * This method add selection listener to valTableViewer
   */
  private void addTableSelectionListener() {
    this.allAttrListTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * Table selection
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final List<Attribute> selAttrList = getSelAttrFromTabViewer();
        AddNewPredefinedAttrDialog.this.selAttrListfromTable = selAttrList;
        validateOkBtn(selAttrList);
      }
    });
  }

  /**
   * Validate Save button
   */
  private void validateOkBtn(final List<Attribute> selAttrList) {
    if ((null != selAttrList) && !selAttrList.isEmpty()) {
      this.saveBtn.setEnabled(true);
    }
  }

  /**
   * @return Attribute
   */
  protected List<Attribute> getSelAttrFromTabViewer() {
    List<Attribute> selAttrList = new ArrayList<>();
    final IStructuredSelection selection = (IStructuredSelection) this.allAttrListTableViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final List<IStructuredSelection> elementList = selection.toList();
      if (elementList.get(0) instanceof Attribute) {
        selAttrList.addAll((Collection<? extends Attribute>) elementList);
      }
    }
    return selAttrList;
  }

  /**
   * Add filter for filter text
   */
  private void addFilters() {
    this.attrListTabFilter = new PredfndAttrValTabFilter();
    this.allAttrListTableViewer.addFilter(this.attrListTabFilter);
  }

  /**
   * Create filter text for table
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.filterTxt.addModifyListener(event -> {
      final String text = AddNewPredefinedAttrDialog.this.filterTxt.getText().trim();
      AddNewPredefinedAttrDialog.this.attrListTabFilter.setFilterText(text);
      AddNewPredefinedAttrDialog.this.allAttrListTableViewer.refresh();
    });

    this.filterTxt.setFocus();
  }

  /**
   * Create table to display the attribute value list
   */
  private void createAttrListTable() {
    this.allAttrListTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(350));
    this.allAttrListTableViewer.setContentProvider(new ArrayContentProvider());
    this.allAttrListTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.allAttrListTableViewer.getGrid().setLinesVisible(true);
    this.allAttrListTableViewer.getGrid().setHeaderVisible(true);
    // Create table sorter
    this.attrListTabSorter = new PredefinedAttrListTabSorter();
    this.allAttrListTableViewer.setComparator(this.attrListTabSorter);
    // Create GridViewerColumns
    createAttrListColumn();
    createAttrDescColumn();
    // Level attributes should not be displayed


    Set<Attribute> attrsList = new HashSet<>();
    attrsList.addAll(this.allAttrList);
    this.allAttrList.clear();
    for (Attribute attribute : attrsList) {
      if (!attribute.isGroupedAttr()) {
        this.allAttrList.add(attribute);
      }
    }

    // Remove all level attributes(attribute level is not null)
    this.allAttrList.removeAll(new ApicDataBO().getAllLvlAttrByLevel().values());
    this.allAttrList.removeAll(this.predefinedAttributesPage.getPredfndAttrList());
    this.allAttrListTableViewer.setInput(this.allAttrList);
  }

  /**
   * Create column for attribute description
   */
  private void createAttrDescColumn() {
    final GridViewerColumn attrDescColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.allAttrListTableViewer, "Description", 400);
    // Add column selection listener
    attrDescColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrDescColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_1, this.attrListTabSorter, this.allAttrListTableViewer));
    attrDescColumn.setLabelProvider(new AddPredfndAttrLabelProvider(ApicUiConstants.COLUMN_INDEX_1));
  }

  /**
   * Create column for attribute name
   */
  private void createAttrListColumn() {
    final GridViewerColumn attrNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.allAttrListTableViewer, "Attribute", 300);
    // Add column selection listener
    attrNameColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(attrNameColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.attrListTabSorter, this.allAttrListTableViewer));
    attrNameColumn.setLabelProvider(new AddPredfndAttrLabelProvider(ApicUiConstants.COLUMN_INDEX_0));
  }
}
