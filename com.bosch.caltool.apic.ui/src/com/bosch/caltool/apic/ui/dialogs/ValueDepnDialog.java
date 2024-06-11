/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * This class provides UI to add value dependency information
 */
public class ValueDepnDialog extends AbstractDialog {

  /**
   * No. of columns in the form
   */
  private static final int FORM_COL_COUNT = 2;
  /**
   * Button instance for save
   */
  private Button saveBtn;
  /**
   * Button instance for cancel
   */
  Button cancelBtn;
  /**
   * Composite instance for the dialog
   */
  protected Composite composite;
  /**
   * Dependent attribute combo
   */
  protected Combo comboDepnAttrName;
  /**
   * Dependent value combo
   */
  protected Combo comboDepnAttrVal;
  /**
   * FormToolkit instance
   */
  protected FormToolkit formToolkit;
  /**
   * Section instance
   */
  protected Section section;
  /**
   * Form instance
   */
  protected Form form;
  /**
   * Top composite
   */
  protected Composite top;

  // List of selected attributes
  private final List<Attribute> selectedAttr;
  // List of selected attr values
  private final List<AttributeValue> selectedValues;
  /**
   * selected DepnAttrName as text
   */
  protected String selDepnAttrName;
  /**
   * Text for adding comment when a value is edited
   */
  protected Text comment;

  /**
   *
   */
  protected final SortedSet<Attribute> attributes = new TreeSet<>();

  /**
   * Constructor
   *
   * @param parentShell reference of parent Shell
   * @param attributesPage reference of attributesPage
   */
  public ValueDepnDialog(final Shell parentShell, final AttributesPage attributesPage) {
    super(parentShell);

    this.selectedAttr = attributesPage.getAttrHandler().getSelectedAttributes();
    this.selectedValues = attributesPage.getAttrHandler().getSelectedValues();

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
    setTitle("");

    // Set the message
    setMessage("");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
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
  protected void createComposite() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    // ICDM-183
    this.comboDepnAttrName.setFocus();
  }

  /**
   * This method initializes section
   */
  private void createSection() {
    // create section
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter the details");

    createForm();

    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = FORM_COL_COUNT;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    // create text fields
    createControls();
  }

  /**
   *
   */
  protected void createControls() {

    // Text for attr name
    GridData gridData = getTextFieldGridData();
    getFormToolkit().createLabel(this.form.getBody(), "Attribute Name:");
    Text attributeNameText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    attributeNameText.setLayoutData(gridData);
    attributeNameText.setEnabled(false);
    attributeNameText.setText(this.selectedAttr.get(0).getName());

    // Text for attr value
    getFormToolkit().createLabel(this.form.getBody(), "Attribute Value:");
    Text attributeValueText = getFormToolkit().createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    attributeValueText.setLayoutData(gridData);
    attributeValueText.setEnabled(false);
    attributeValueText.setText(this.selectedValues.get(0).getName());

    getFormToolkit().createLabel(this.form.getBody(), "Dependent Attribute Name:");
    // Create combo for dependency attribute value
    createComboDepntAttr();

    getFormToolkit().createLabel(this.form.getBody(), "Dependent Attribute Value");
    // Create combo for dependency attribute value
    createComboDepntAttrVal();
    // ICDM-1397
    getFormToolkit().createLabel(this.form.getBody(), "Comment");
    // Text for comments
    this.comment =
        getFormToolkit().createText(this.form.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    GridData commentGridData = getTextFieldGridData();
    commentGridData.heightHint = 40;
    this.comment.setLayoutData(commentGridData);
    this.comment.addModifyListener(event -> checkSaveBtnEnable());
  }

  /**
   *
   */
  private void createComboDepntAttr() {
    // Combo for depn attr
    GridData gridData = getTextFieldGridData();
    this.comboDepnAttrName = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.comboDepnAttrName.setLayoutData(gridData);
    this.comboDepnAttrName.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    try {
      Map<Long, Attribute> attrMap = new AttributeServiceClient().getAll();
      this.attributes.addAll(attrMap.values());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    for (Attribute attribute : this.attributes) {
      if (!attribute.isDeleted() && attribute.isNormalized() &&
          (attribute.getLevel() != ApicConstants.VARIANT_CODE_ATTR) &&
          (attribute.getLevel() != ApicConstants.SUB_VARIANT_CODE_ATTR)) {
        this.comboDepnAttrName.add(attribute.getName());
      }
    }
    this.comboDepnAttrName.select(0);
    // Selection listener
    this.comboDepnAttrName.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        int index = ValueDepnDialog.this.comboDepnAttrName.getSelectionIndex();
        ValueDepnDialog.this.selDepnAttrName = ValueDepnDialog.this.comboDepnAttrName.getItem(index);
        boolean attrSet = false;
        for (Attribute attribute : ValueDepnDialog.this.attributes) {
          if (attribute.getName().equalsIgnoreCase(ValueDepnDialog.this.selDepnAttrName)) {
            attrSet = true;
            setValues(attribute);
            break;
          }
        }
        if (!attrSet) {
          ValueDepnDialog.this.comboDepnAttrVal.removeAll();
          ValueDepnDialog.this.comboDepnAttrVal.add("No active values are avaliable!");
          ValueDepnDialog.this.comboDepnAttrVal.select(0);
        }

      }
    });


  }

  /**
   * This method initializes comboDepnAttrVal
   */
  private void createComboDepntAttrVal() {
    // Combo for depn attr value
    GridData gridData = getTextFieldGridData();
    this.comboDepnAttrVal = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.comboDepnAttrVal.setLayoutData(gridData);
    this.comboDepnAttrVal.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    this.comboDepnAttrVal.select(0);
    this.comboDepnAttrVal.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        int index = ValueDepnDialog.this.comboDepnAttrName.getSelectionIndex();
        ValueDepnDialog.this.selDepnAttrName = ValueDepnDialog.this.comboDepnAttrName.getItem(index);
        // Checks if save button should be enabled
        checkSaveBtnEnable();

      }
    });
  }

  /**
   * @return GridData of text field
   */
  protected GridData getTextFieldGridData() {
    // Grid data for text fields
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.CENTER;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
  }

  /**
   * Sets the values for selected dependent attribute
   *
   * @param attribute Attribute
   */

  protected void setValues(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    AttributeClientBO bo = new AttributeClientBO(attribute);
    // List of attr values
    Set<AttributeValue> listAttrVals = bo.getAttrValues();

    // ICDM-156
    SortedSet<AttributeValue> sortedAttrValues = new TreeSet<>();
    sortedAttrValues.addAll(listAttrVals);

    String[] valueItems = this.comboDepnAttrVal.getItems();

    if (valueItems.length > 0) {
      this.comboDepnAttrVal.removeAll();
      this.comboDepnAttrVal.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    }
    for (AttributeValue attrVal : sortedAttrValues) {
      this.comboDepnAttrVal.add(attrVal.getName());
    }
    // Select first element in combo list
    this.comboDepnAttrVal.select(0);

    if (sortedAttrValues.isEmpty()) {
      this.comboDepnAttrVal.removeAll();
      this.comboDepnAttrVal.add("No active values are avaliable!");
      this.comboDepnAttrVal.select(0);
    }
  }

  /**
   * Checks if the save button should be enabled
   */
  private void checkSaveBtnEnable() {
    // Validation for save button
    this.saveBtn.setEnabled(validateTextFields());
  }

  /**
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {
    // Validation for text fields
    int attrValIndex = this.comboDepnAttrVal.getSelectionIndex();
    if (attrValIndex == -1) {
      return false;
    }

    return (this.selDepnAttrName != null) &&
        !CommonUtils.isEqualIgnoreCase(this.selDepnAttrName, ApicConstants.DEFAULT_COMBO_SELECT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


}
