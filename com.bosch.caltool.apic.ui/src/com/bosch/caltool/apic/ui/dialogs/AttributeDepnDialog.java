/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.List;
import java.util.Map;
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
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.combo.ComboUtil;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * This class provides UI to add attributes dependency information
 */
public class AttributeDepnDialog extends AbstractDialog {


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
  private Composite composite;
  /**
   * Combo box instance for dependent attribute name
   */
  protected Combo comboDepnAttrName;
  /**
   * Combo box instance for dependent attribute value
   */
  protected Combo comboDepnAttrVal;

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
   * Composite instance
   */
  private Composite top;
  /**
   * Selected Attribute isnatnce
   */
  protected final List<Attribute> selectedAttr;
  /**
   * Defines attributes sortedset
   */
  protected final SortedSet<Attribute> attributes = new TreeSet<>();
  /**
   *
   */
  protected static final String NO_ACTIVE_VALS_ARE_AVAILABLE = "No active values are avaliable!";
  /**
   * Defines selected dependent attribute name
   */
  protected String selDepnAttrName;
  // ICDM-1397
  /**
   * Text for adding comment when a value is edited
   */
  protected Text comment;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param selectedAttr instance
   */
  public AttributeDepnDialog(final Shell parentShell, final List<Attribute> selectedAttr) {
    super(parentShell);
    this.selectedAttr = selectedAttr;
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


  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Create save & cancel buttons
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-153
  @Override
  protected void configureShell(final Shell newShell) {
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
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
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method creates the dialog ui
   *
   * @param top
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // ICDM-183
    this.comboDepnAttrName.setFocus();

  }

  /**
   * This method initializes section
   */
  private void createSection() {
    // Craete section for Dependency details
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "Enter Details");
    createForm();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.form = getFormToolkit().createForm(this.section);
    this.form.getBody().setLayout(gridLayout);
    // Create labels and text fields
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Attribute Name:");
    TextUtil.getInstance().createText(this.form.getBody(), GridDataUtil.getInstance().createGridData(), false,
        this.selectedAttr.get(0).getName());
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Dependent Attribute Name:");
    createComboDepntAttrName();
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Dependent Attribute Value:");
    createComboDepntAttrVal();
    // ICDM-1397
    LabelUtil.getInstance().createLabel(this.form.getBody(), "Comment:");
    this.comment =
        getFormToolkit().createText(this.form.getBody(), null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    GridData commentGridData = GridDataUtil.getInstance().createGridData();
    commentGridData.heightHint = 40;
    this.comment.setLayoutData(commentGridData);
    this.comment.addModifyListener(event -> checkSaveBtnEnable());
  }

  /**
   * This method initializes comboDepnAttrName
   */

  private void createComboDepntAttrName() {
    // Combo selection
    this.comboDepnAttrName = new Combo(this.form.getBody(), SWT.READ_ONLY);
    this.comboDepnAttrName.setLayoutData(GridDataUtil.getInstance().createGridData());
    this.comboDepnAttrName.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);

    try {
      Map<Long, Attribute> attrMap = new AttributeServiceClient().getAll();
      this.attributes.addAll(attrMap.values());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

    for (Attribute attr : this.selectedAttr) {
      this.attributes.remove(attr);
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


      @Override
      public void widgetSelected(final SelectionEvent event) {
        int index = AttributeDepnDialog.this.comboDepnAttrName.getSelectionIndex();
        AttributeDepnDialog.this.selDepnAttrName = AttributeDepnDialog.this.comboDepnAttrName.getItem(index);
        boolean attrSet;
        attrSet = identifyAndSetValuesForSelDepnAttr();
        if (!attrSet) {
          AttributeDepnDialog.this.comboDepnAttrVal.removeAll();
          AttributeDepnDialog.this.comboDepnAttrVal.add(NO_ACTIVE_VALS_ARE_AVAILABLE);
          AttributeDepnDialog.this.comboDepnAttrVal.select(0);
        }
        // Checks if save button should be enabled
        checkSaveBtnEnable();

      }

    });
  }

  /**
   * @return
   */
  private boolean identifyAndSetValuesForSelDepnAttr() {
    boolean attrSet = false;
    for (Attribute attribute : AttributeDepnDialog.this.attributes) {
      if (attribute.getName().equalsIgnoreCase(AttributeDepnDialog.this.selDepnAttrName)) {
        attrSet = true;
        setValues(attribute);
        break;
      }
    }
    return attrSet;
  }

  /**
   * This method initializes comboDepnAttrVal
   */
  private void createComboDepntAttrVal() {
    this.comboDepnAttrVal = ComboUtil.getInstance().createComboBox(this.form.getBody(),
        GridDataUtil.getInstance().createGridData(), ApicConstants.DEFAULT_COMBO_SELECT, 0);
    this.comboDepnAttrVal.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        int index = AttributeDepnDialog.this.comboDepnAttrName.getSelectionIndex();
        AttributeDepnDialog.this.selDepnAttrName = AttributeDepnDialog.this.comboDepnAttrName.getItem(index);
        // Checks if save button should be enabled
        checkSaveBtnEnable();

      }
    });

  }

  /**
   * Sets the values for selected dependent attribute
   *
   * @param attribute instance
   */
  protected void setValues(final Attribute attribute) {

    SortedSet<AttributeValue> sortedAttrValues = new AttributeClientBO(attribute).getAttrValues();

    // ICDM-156
    String[] valueItems = this.comboDepnAttrVal.getItems();

    if (valueItems.length > 0) {
      this.comboDepnAttrVal.removeAll();
      this.comboDepnAttrVal.add(ApicConstants.DEFAULT_COMBO_SELECT, 0);
    }
    for (AttributeValue attrVal : sortedAttrValues) {
      // check whether attr val is not deleted
      if (!attrVal.isDeleted()) {
        this.comboDepnAttrVal.add(attrVal.getName());
      }
    }
    this.comboDepnAttrVal.select(0);

    if (sortedAttrValues.isEmpty()) {
      this.comboDepnAttrVal.removeAll();
      this.comboDepnAttrVal.add(NO_ACTIVE_VALS_ARE_AVAILABLE);
      this.comboDepnAttrVal.select(0);
    }
  }

  /**
   * Checks if the save button should be enabled
   */
  private void checkSaveBtnEnable() {
    // Perform validations
    this.saveBtn.setEnabled(validateTextFields());
  }

  /**
   * Validates the text fields before enabling the save button
   *
   * @return boolean
   */
  private boolean validateTextFields() {
    int attrValIndex = this.comboDepnAttrVal.getSelectionIndex();
    if (attrValIndex != -1) {
      return (this.selDepnAttrName != null) &&
          !this.selDepnAttrName.equalsIgnoreCase(ApicConstants.DEFAULT_COMBO_SELECT);
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM);
  }


}
