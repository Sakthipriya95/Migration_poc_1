/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.SortedSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
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
import com.bosch.caltool.apic.ui.sorter.ValidityAttrValTableSorter;
import com.bosch.caltool.apic.ui.table.filters.ValidityAttrValTableFilter;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.ValidityValColLblProvider;
import com.bosch.caltool.apic.ui.views.providers.ValidityValDescColLblProvider;
import com.bosch.caltool.apic.ui.views.providers.ValidityValSelColLblProvider;
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
 * ICDM-2593 Dialog to choose attribute values for the selected level attribute (Validity Attribute in predefined page
 *
 * @author dja7cob
 */
public class AddValidityAttrValDialog extends AbstractDialog {

  /**
   * AddValidityAttrValDialog Title
   */
  private static final String DIALOG_TITLE = "Select values";
  /**
   * AddValidityAttrValDialog width
   */
  private static final int DIALOG_WIDTH = 600;
  /**
   * AddValidityAttrValDialog height
   */
  private static final int DIALOG_NORM_HEIGHT = 650;
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
   * Validity attribute selected
   */
  private final Attribute selValidityAttribute;
  /**
   * Validity attribute values list
   */
  private final SortedSet<AttributeValue> validityAttrValList;
  /**
   * Selected validity attribute values list
   */
  private final SortedSet<AttributeValue> selValidityAttrValues;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form form
   */
  private Form form;
  /**
   * Validity attribute values Tableviewer
   */
  private GridTableViewer validityvaluesTableViewer;
  /**
   * validityvaluesTableViewer filter instance
   */
  private ValidityAttrValTableFilter validityValuesTableFilter;
  /**
   * validityvaluesTableViewer sorter instance
   */
  private AbstractViewerSorter validityValuesTableSorter;
  /**
   * validityvaluesTableViewer filter text
   */
  private Text filterTxt;
  /**
   * Button instance
   */
  private Button saveBtn;

  /**
   * PredefinedAttributesPage instance
   */
  private final PredefinedAttributesPage dependentAttributesPage;
  /**
   * EditValueDialog instance
   */
  private final EditValueDialog editValDialog;

  /**
   * @param parentShell instance
   * @param dependentAttributesPage PredefinedAttributesPage instance instance
   * @param selValidityAttribute Level attribute selected in PredefinedAttributesPage
   * @param selValidityAttrValues Attribute values chosen for 'selectedLevelAttr' from AddLevelAttrValDialog
   * @param editValDialog instance
   */
  public AddValidityAttrValDialog(final Shell parentShell, final PredefinedAttributesPage dependentAttributesPage,
      final Attribute selValidityAttribute, final SortedSet<AttributeValue> selValidityAttrValues,
      final EditValueDialog editValDialog) {
    super(parentShell);
    this.selValidityAttribute = selValidityAttribute;
    this.validityAttrValList = new AttributeClientBO(selValidityAttribute).getAttrValues();
    this.selValidityAttrValues = selValidityAttrValues;
    this.dependentAttributesPage = dependentAttributesPage;
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
    setTitle("Select Values");
    // Set the message
    setMessage("Attribute Name : " + this.selValidityAttribute.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);

    // Constants for the Width and height
    final Point newSize = newShell.computeSize(DIALOG_WIDTH, DIALOG_NORM_HEIGHT, true);
    newShell.setSize(newSize);
    newShell.layout(true, true);
    super.configureShell(newShell);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Set", true);
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
    // create composite
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
    this.composite.setLayoutData(gridData);
    // create table section
    createTableSection();
  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    // Build a string with the list of values selected from the table
    // Set the string in the validity value text firld of predefined attributes page
    StringBuilder lvlValuesStr = new StringBuilder();
    if (!this.selValidityAttrValues.isEmpty()) {
      for (AttributeValue lvlVal : this.selValidityAttrValues) {
        lvlValuesStr.append(lvlVal.getName());
        lvlValuesStr.append(", ");
      }
      this.dependentAttributesPage.getAttrValuesText().setText(lvlValuesStr.toString());
      // Validate save button
      // If a validity attribute is selected, a value must be set to it.
      // Else disable the save button
      this.editValDialog.getSaveBtn().setEnabled(true);
    }
    else {
      this.dependentAttributesPage.getAttrValuesText().setText("");
      this.editValDialog.getSaveBtn().setEnabled(false);
    }
    this.dependentAttributesPage.getAttrValuesText().redraw();
    super.okPressed();
  }


  /**
   *
   */
  private void createTableSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "List of values");
    this.section.setLayoutData(gridData);
    // create table form
    createTableForm();
    this.section.setClient(this.form);
  }

  /**
   * Create table form
   */
  private void createTableForm() {
    this.form = this.formToolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    // create filter text for the table
    createFilterTxt();
    // craete table to display the list of validity values
    createValListTable();
    // add filter to the table
    addFilters();
  }

  /**
  *
  */
  private void addFilters() {
    this.validityValuesTableFilter = new ValidityAttrValTableFilter();
    this.validityvaluesTableViewer.addFilter(this.validityValuesTableFilter);
  }

  /**
  *
  */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));

    this.filterTxt.addModifyListener(event -> {
      final String text = AddValidityAttrValDialog.this.filterTxt.getText().trim();
      AddValidityAttrValDialog.this.validityValuesTableFilter.setFilterText(text);
      AddValidityAttrValDialog.this.validityvaluesTableViewer.refresh();
    });

    this.filterTxt.setFocus();
  }

  /**
   * Create validity value table
   */
  private void createValListTable() {
    this.validityvaluesTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL, GridDataUtil.getInstance().getHeightHintGridData(350));
    this.validityvaluesTableViewer.setContentProvider(new ArrayContentProvider());
    this.validityvaluesTableViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.validityvaluesTableViewer.getGrid().setLinesVisible(true);
    this.validityvaluesTableViewer.getGrid().setHeaderVisible(true);
    // Create sorter for the table
    this.validityValuesTableSorter = new ValidityAttrValTableSorter(this.selValidityAttrValues);

    // Create GridViewerColumns
    createValListColumn();
    createValListDescColumn();
    createValSelColumn();
    // Set input to the table
    this.validityvaluesTableViewer.setInput(this.validityAttrValList);
    this.validityvaluesTableViewer.setComparator(this.validityValuesTableSorter);
  }

  /**
   * Create column for validity value selection
   */
  private void createValSelColumn() {
    // Create colum with check box editing support
    final GridViewerColumn valSelectionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerCheckStyleColumn(this.validityvaluesTableViewer, "Selection", 100);
    // Add column selection listener
    valSelectionColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(valSelectionColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_2, this.validityValuesTableSorter, this.validityvaluesTableViewer));
    valSelectionColumn.setLabelProvider(new ValidityValSelColLblProvider(this.selValidityAttrValues));
    // set checkbox editing support for selcting values for the level attribute selected
    valSelectionColumn.setEditingSupport(new CheckEditingSupport(valSelectionColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        if (arg0 instanceof AttributeValue) {
          if ((boolean) arg1) {
            // If checked (selected)
            addAttrValToTheSet(arg0);
          }
          else {
            // If unchecked (unselected)
            removeAttrValFromSet(arg0);
          }
        }
        AddValidityAttrValDialog.this.saveBtn
            .setEnabled((null != AddValidityAttrValDialog.this.selValidityAttrValues) ||
                (!AddValidityAttrValDialog.this.selValidityAttrValues.isEmpty()));
      }

    });
  }

  /**
   * @param arg0
   */
  private void removeAttrValFromSet(final Object arg0) {
    if ((null != AddValidityAttrValDialog.this.selValidityAttrValues) &&
        AddValidityAttrValDialog.this.selValidityAttrValues.contains(arg0)) {
      AddValidityAttrValDialog.this.selValidityAttrValues.remove(arg0);
    }
  }

  /**
   * @param arg0
   */
  private void addAttrValToTheSet(final Object arg0) {
    if ((null != AddValidityAttrValDialog.this.selValidityAttrValues) &&
        !AddValidityAttrValDialog.this.selValidityAttrValues.contains(arg0)) {
      AddValidityAttrValDialog.this.selValidityAttrValues.add((AttributeValue) arg0);
    }
  }

  /**
   * Create column for validity value description
   */
  private void createValListDescColumn() {
    final GridViewerColumn validityValDescColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.validityvaluesTableViewer, "Value", 250);
    // Add column selection listener
    validityValDescColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(validityValDescColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_1, this.validityValuesTableSorter, this.validityvaluesTableViewer));
    // Label provider for validity value description column
    validityValDescColumn.setLabelProvider(new ValidityValDescColLblProvider());
  }

  /**
   * Create column for validity value name
   */
  private void createValListColumn() {
    final GridViewerColumn validityValColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.validityvaluesTableViewer, "Value", 200);
    // Add column selection listener
    validityValColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(validityValColumn.getColumn(),
            ApicUiConstants.COLUMN_INDEX_0, this.validityValuesTableSorter, this.validityvaluesTableViewer));
    // Label provider for validity value name column
    validityValColumn.setLabelProvider(new ValidityValColLblProvider());
  }
}
