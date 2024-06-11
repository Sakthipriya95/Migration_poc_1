/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author dmo5cob
 */
public class AttrValueEditDialog extends AbstractDialog {

  /**
   * Value col width
   */
  private static final int VALUE_COL_WIDTH = 200;
  /**
   * Value table height hint
   */
  private static final int VALUE_TAB_HEIGHT_HINT = 200;
  /**
   * Selected attribute
   */
  private final Attribute editableAttr;

  /**
   * Ok button label as add
   */
  private static final String okButtonLabel = "Add";
  /**
   * Add new user button instance
   */
  private Button okBtn;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * GridTableViewer instance for add values
   */
  private GridTableViewer valuesTabViewer;
  private final AddAttributeValueCombDialog addAttributeValueCombDialog;
  private final boolean editFlag;

  /**
   * @param shell
   * @param selectAttrValWizardPage
   * @param editableAttr
   * @param addAttributeValueCombDialog
   * @param editFlag
   */
  public AttrValueEditDialog(final Shell shell, final Attribute editableAttr,
      final AddAttributeValueCombDialog addAttributeValueCombDialog, final boolean editFlag) {
    super(shell);
    this.editableAttr = editableAttr;
    this.addAttributeValueCombDialog = addAttributeValueCombDialog;
    this.editFlag = editFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Select value/value's");
    // Set the message
    setMessage(ApicUiConstants.ATTR_NAME + this.editableAttr.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Select value/value's");
    super.configureShell(newShell);

  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite on parent comp
    createComposite();
    return this.top;
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of values");
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // Create values grid tableviewer
    createValuesGridTabViewer();

    this.form.getBody().setLayout(new GridLayout());

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
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createValuesGridTabViewer() {
    int style;
    if (this.editFlag) {
      style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;
    }
    else {
      style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI;
    }
    this.valuesTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(VALUE_TAB_HEIGHT_HINT));

    this.valuesTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // Create GridViewerColumns
    final GridViewerColumn valueColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.valuesTabViewer,
        "Attribute Values", VALUE_COL_WIDTH);
    valueColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        final AttributeValue item = (AttributeValue) element;
        return item.getName();
      }
    });
    // set table input
    List<AttributeValue> inputList = new ArrayList<>();
    // fetch the attribute values for the selected attribute
    SortedSet<AttributeValue> attrValues = new AttributeClientBO(this.editableAttr).getAttrValues();
    for (AttributeValue attrVal : attrValues) {
      if (!attrVal.isDeleted()) {
        inputList.add(attrVal);
      }
    }
    this.valuesTabViewer.setInput(inputList);

    // Add selection listener
    addTableSelectionListener();
    // add double click listener
    addDoubleClickListener();
  }

  private void addDoubleClickListener() {
    this.valuesTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            okPressed();
          }
        });
      }

    });
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.valuesTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) AttrValueEditDialog.this.valuesTabViewer.getSelection();
        if (selection != null) {
          AttrValueEditDialog.this.okBtn.setEnabled(true);
        }
      }
    });
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, AttrValueEditDialog.okButtonLabel, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final IStructuredSelection selection = (IStructuredSelection) this.valuesTabViewer.getSelection();
    SortedSet<AttributeValue> selValSet = new TreeSet<>();
    if (selection.getFirstElement() instanceof AttributeValue) {
      final Iterator<AttributeValue> values = selection.iterator();
      while (values.hasNext()) {
        final AttributeValue value = values.next();
        selValSet.add(value);
      }
      // add the selected attribute value to attrVal combination
      this.addAttributeValueCombDialog.getAttrVals().put(this.editableAttr, selValSet);
      this.addAttributeValueCombDialog.getAttrTab().refresh();

    }
    super.okPressed();
  }
}
