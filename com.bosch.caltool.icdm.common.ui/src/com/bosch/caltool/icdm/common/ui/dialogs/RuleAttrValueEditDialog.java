/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import com.bosch.caltool.icdm.client.bo.caldataimport.CalDataImporterHandler;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.wizards.pages.AttrValueImpWizardPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDontCare;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * A dialog which allows the user to select values for attributes </br>
 * TODO: Most part is similar to com.bosch.caltool.cdr.ui.dialogs.RuleAttrValueEditDialog.To be made as a single class
 *
 * @author jvi6cob
 */
public class RuleAttrValueEditDialog extends AbstractDialog {

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
   * Instance of select attr value wizard page
   */
  private final AttrValueImpWizardPage attrValWizardPage;
  /**
   * Ok button label as add
   */
  private static final String ADD_LABEL = "Add";
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

  /**
   * @param shell
   * @param attrValWizardPage
   * @param editableAttr
   */
  public RuleAttrValueEditDialog(final Shell shell, final AttrValueImpWizardPage attrValWizardPage,
      final Attribute editableAttr) {
    super(shell);
    this.editableAttr = editableAttr;
    this.attrValWizardPage = attrValWizardPage;
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
    setMessage("Attribute Name : " + this.editableAttr.getName(), IMessageProvider.INFORMATION);
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
    style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI;
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
    try {
      CalDataImporterHandler handler = new CalDataImporterHandler();
      SortedSet<AttributeValue> featureMappedAttributeValues =
          handler.getFeatureMappedAttributeValues(this.editableAttr.getId());


      Map<Long, AttributeValueDontCare> dontCareAttrValueMap =
          this.attrValWizardPage.getWizardData().getParamRuleResponse().getDontCareAttrValueMap();
      if (CommonUtils.isNotEmpty(dontCareAttrValueMap) && dontCareAttrValueMap.containsKey(this.editableAttr.getId())) {
        featureMappedAttributeValues.add(dontCareAttrValueMap.get(this.editableAttr.getId()));
      }
      this.valuesTabViewer.setInput(featureMappedAttributeValues);
    }

    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    // Add selection listener
    addTableSelectionListener();
    // add double click listener
    addDoubleClickListener();
  }

  private void addDoubleClickListener() {
    this.valuesTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(RuleAttrValueEditDialog.this::okPressed);
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
            (IStructuredSelection) RuleAttrValueEditDialog.this.valuesTabViewer.getSelection();
        if (selection != null) {
          RuleAttrValueEditDialog.this.okBtn.setEnabled(true);
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
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, RuleAttrValueEditDialog.ADD_LABEL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    final IStructuredSelection selection =
        (IStructuredSelection) RuleAttrValueEditDialog.this.valuesTabViewer.getSelection();
    SortedSet<AttributeValue> selValSet = new TreeSet<>();
    if (selection.getFirstElement() instanceof AttributeValue) {
      final Iterator<AttributeValue> values = selection.iterator();
      if (validateIfMultiSelectContainsDontCareValue(selection)) {
        MessageDialogUtils.getErrorMessageDialog("Invalid selection",
            "Cannot select " + CDRConstants.DONT_CARE_ATTR_VALUE_TEXT + " value in combination with other values.");
        return;
      }
      while (values.hasNext()) {
        final AttributeValue value = values.next();
        selValSet.add(value);
      }
      this.attrValWizardPage.getWizardData().getAttrVals().put(this.editableAttr, selValSet);
      this.attrValWizardPage.getAttrsTableViewer().refresh();
    }
    super.okPressed();
  }

  private boolean validateIfMultiSelectContainsDontCareValue(final IStructuredSelection selection) {
    List<AttributeValue> selectedValList = selection.toList();
    if (CommonUtils.isNotEmpty(selectedValList) && (selectedValList.size() > 1)) {
      for (AttributeValue attributeValue : selectedValList) {
        if (attributeValue instanceof AttributeValueDontCare) {
          return true;
        }
      }
    }
    return false;
  }
}
