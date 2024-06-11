/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.editors.pages.PredefinedAttributesPage;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PredfndAttrValsValidityClientBO;
import com.bosch.caltool.icdm.common.bo.apic.AttributeCommon;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValuesCreationModel;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidityCreationModel;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedAttrValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedValidityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * This class provides a dialog to edit an attribute
 */
public class EditValueDialog extends ValueDialog {

  /**
   * String constant for invalid value info
   */
  private static final String INVALID_VALUE_INFO = "Invalid Value Info:";
  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Edit Value";
  // Constants for the Width and height
  /**
   * Dialog Width
   */
  private static final int DIALOG_WIDTH = 750;
  /**
   * Default height
   */
  private static final int DIALOG_NORM_HEIGHT = 850;

  /**
   * Attribute page
   */
  private final AttributesPage attributesPage;
  /**
   * Attribute rights page
   */
  private PredefinedAttributesPage predefinedAttributesPage;
  /**
   * Selected list of attributes
   */
  private final List<Attribute> selectedAttr;

  /**
   * tab item
   */
  TabItem editValTabItem;
  /**
   * Access right tab item
   */
  TabItem dependentAttrTabItem;

  private final List<AttributeValue> selectedValue;


  private final AttributeValueClientBO valClientBO;
  private AttributeClientBO attrBO;
  // to check for change in predefined attributes tab
  private boolean predefAttrValChanged = false;

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param attributesPage instance
   */
  public EditValueDialog(final Shell parentShell, final AttributesPage attributesPage) {
    // ICDM-2580
    super(parentShell,
        new AttributeValueClientBO(attributesPage.getAttrHandler().getSelectedValues().get(0)).getAttribute(), null,
        true);
    this.selectedAttr = new ArrayList<>();
    this.valClientBO = new AttributeValueClientBO(attributesPage.getAttrHandler().getSelectedValues().get(0));
    this.selectedValue = attributesPage.getAttrHandler().getSelectedValues();
    this.selectedAttr.add(this.valClientBO.getAttribute());
    this.attributesPage = attributesPage;


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


  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Composite comp = new Composite(parent, SWT.NONE);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.verticalSpacing = 0;
    gridLayout.horizontalSpacing = 0;
    comp.setLayout(gridLayout);
    final GridData gridData1 = new GridData();
    gridData1.grabExcessHorizontalSpace = true;
    gridData1.grabExcessVerticalSpace = true;


    comp.setLayoutData(gridData1);

    TabFolder tabFolder = new TabFolder(comp, SWT.V_SCROLL | SWT.H_SCROLL);
    tabFolder.setLayout(new GridLayout());
    final GridData tabgridData = GridDataUtil.getInstance().getGridData();
    tabFolder.setLayoutData(tabgridData);
    this.editValTabItem = new TabItem(tabFolder, SWT.NONE);
    this.editValTabItem.setText(DIALOG_TITLE);
    final Control contents = super.createContents(this.editValTabItem.getParent());

    this.editValTabItem.setControl(contents);
    // ICDM-2593
    // If the selected attribute value's attribute is a Grouped attribute
    // Add new tab item to select the validity & predefined attributes & values
    this.attrBO = new AttributeClientBO(this.valClientBO.getAttribute());

    if (this.attrBO.isGrouped()) {
      this.dependentAttrTabItem = new TabItem(tabFolder, SWT.NONE);
      this.dependentAttrTabItem.setText("Predefined Attributes and Values");
      EditValueDialog.this.predefinedAttributesPage = new PredefinedAttributesPage(this, this.selectedValue.get(0),
          this.attributesPage.getAttrHandler().getAttrsMap());
      this.predefinedAttributesPage.createPartControl(tabFolder);
      this.dependentAttrTabItem.setControl(this.predefinedAttributesPage.getPartControl());
    }
    gridData1.horizontalAlignment = GridData.FILL;

    // Set the title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Edit Value Details", IMessageProvider.INFORMATION);
    // Set the existing values
    setExistingValues();
    return comp;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  private void fillUserDetails() {
    this.attrNameText.setEnabled(true);
    this.attrNameText.setEditable(false);

    this.valueEngText.setText(this.attributesPage.getAttrHandler().getSelectedValues().get(0).getTextValueEng());
    this.valueDescEngText.setText(this.attributesPage.getAttrHandler().getSelectedValues().get(0).getDescriptionEng());

    this.valueEngText.setEnabled(true);
    this.valueEngText.setEditable(false);

    this.valueTypeText.setEnabled(true);
    this.valueTypeText.setEditable(false);

    this.valueDescEngText.setEnabled(true);
    this.valueDescEngText.setEditable(false);

    this.valueDescGerText.setEnabled(true);
    this.valueDescGerText.setEditable(false);
    if (this.comboCharVal != null) {
      this.comboCharVal.setEnabled(false);
    }
    this.comment.setEnabled(true);
    this.comment.setEditable(false);
  }

  /**
   * Populate the existing data, in edit mode/read mode
   */
  private void setExistingValues() {

    if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.TEXT.getDisplayText())) {
      this.valueEngText.setText(this.selectedValue.get(0).getTextValueEng());
      this.valueDescEngText.setText(this.selectedValue.get(0).getDescriptionEng());
      if (this.valueGerText != null) {
        if (this.selectedValue.get(0).getTextValueGer() != null) {
          this.valueGerText.setText(this.selectedValue.get(0).getTextValueGer());
        }
        this.valueGerText.addModifyListener(event -> this.saveBtn.setEnabled(validateFields()));
      }
      if (this.selectedValue.get(0).getDescriptionGer() != null) {
        this.valueDescGerText.setText(this.selectedValue.get(0).getDescriptionGer());
      }

    }
    else if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.BOOLEAN.getDisplayText())) {
      String[] items = this.combo.getItems();
      for (String string : items) {
        if (this.selectedValue.get(0).getName().equalsIgnoreCase(string)) {
          int index = this.combo.indexOf(string);
          this.combo.select(index);
          break;
        }
      }
      this.valueDescEngText.setText(this.selectedValue.get(0).getDescriptionEng());
      if (this.selectedValue.get(0).getDescriptionGer() != null) {
        this.valueDescGerText.setText(this.selectedValue.get(0).getDescriptionGer());
      }
    }

    else if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
      String value = this.selectedValue.get(0).getNameRaw();
      if ((null != getAttribute().getFormat()) && getAttribute().getFormat().contains(",")) {
        value = value.replace(".", ",");
      }
      this.valueEngText.setText(value);
      this.valueDescEngText.setText(this.selectedValue.get(0).getDescriptionEng());
      if (this.selectedValue.get(0).getDescriptionGer() != null) {
        this.valueDescGerText.setText(this.selectedValue.get(0).getDescriptionGer());
      }
      if (null != this.selectedAttr.get(0).getFormat()) {
        this.formatText.setText(this.selectedAttr.get(0).getFormat());
      }
      this.unitText.setText(this.selectedValue.get(0).getUnit());
    }
    else if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
      this.valueEngText.setText(this.selectedValue.get(0).getName());
      this.valueDescEngText.setText(
          null == this.selectedValue.get(0).getDescriptionEng() ? "" : this.selectedValue.get(0).getDescriptionEng());
      this.valueDescGerText.setText(
          null == this.selectedValue.get(0).getDescriptionGer() ? "" : this.selectedValue.get(0).getDescriptionGer());
      this.formatText.setText(this.selectedAttr.get(0).getFormat());
    }
    else if (this.valueTypeText.getText().trim().equalsIgnoreCase(AttributeValueType.HYPERLINK.getDisplayText())) {
      this.valueEngText.setText(this.selectedValue.get(0).getName());
      this.valueDescEngText.setText(
          null == this.selectedValue.get(0).getDescriptionEng() ? "" : this.selectedValue.get(0).getDescriptionEng());
      this.valueDescGerText.setText(
          null == this.selectedValue.get(0).getDescriptionGer() ? "" : this.selectedValue.get(0).getDescriptionGer());
    }

    if (this.attrBO.hasCharacteristic()) {
      this.comboCharVal.select(this.comboCharVal.indexOf(ApicConstants.DEFAULT_COMBO_SELECT));


      if (this.selectedValue.get(0).getCharStr() != null) {
        this.comboCharVal.select(this.comboCharVal.indexOf(this.selectedValue.get(0).getCharStr()));
      }
    }
    this.comment.setText(
        this.selectedValue.get(0).getChangeComment() == null ? "" : this.selectedValue.get(0).getChangeComment());

    // 237910
    this.valueDescGerText.addModifyListener(event -> enableOrDisableSaveButton());

    if (this.valClientBO.getAttribute().getValueType().equals(AttributeValueType.ICDM_USER.toString())) {
      fillUserDetails();
    }
    SortedSet<LinkData> linkDataCollection = new TreeSet<>(getLinks());
    this.linksTabViewer.setInput(linkDataCollection);
  }

  /**
   * @return
   */
  private SortedSet<LinkData> getLinks() {
    SortedSet<LinkData> linkDataCollection = new TreeSet<>();
    LinkServiceClient linkServiceClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkServiceClient.getNodesWithLink(MODEL_TYPE.ATTRIB_VALUE);
      boolean hasLinks = nodesWithLink.contains(this.selectedValue.get(0).getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkServiceClient.getAllLinksByNode(this.selectedValue.get(0).getId(), MODEL_TYPE.ATTRIB_VALUE);
        for (Link link : allLinksByNode.values()) {
          linkDataCollection.add(new LinkData(link));
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return linkDataCollection;
  }

  // 237910
  /**
   * Checks if German description changes
   */
  private void enableOrDisableSaveButton() {
    if (this.saveBtn != null) {
      if (validateFields() && CommonUtils.isNotEqual(EditValueDialog.this.selectedValue.get(0).getDescriptionGer(),
          this.valueDescGerText.getText())) {
        this.saveBtn.setEnabled(true);
      }
      else if (this.saveBtn != null) {
        this.saveBtn.setEnabled(false);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    onOkPressed();
    Set<String> existingValues = this.attributesPage.getAttrHandler().getValueStringsForValidation(
        this.attributesPage.getValueTableViewer().getInput(), this.valClientBO.getAttrValue());
    boolean hasDuplicates = this.attributesPage.getAttrHandler().checkForDuplicates(this.attrValue, existingValues);


    boolean flag = false;
    if (CommonUiUtils.getInstance().validateVariantName(getAttribute().getLevel().intValue(), this.attrValue,
        this.attrValueGer)) {

      if (!this.attrValue.isEmpty() && !this.descEng.isEmpty()) {
        CharacteristicValue attrCharValUpd = null;
        AttributeValueType valType = AttributeValueType.getType(this.valueTypeText.getText().trim());
        if (valType == AttributeValueType.HYPERLINK) {
          if (CommonUtils.isValidURLFormat(this.attrValue.trim())) {
            this.attrValue = CommonUtils.formatUrl(this.attrValue.trim());
          }
          else if (!CommonUtils.isValidHyperlinkFormat(this.attrValue)) {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
                "Valid hyperlink value to be provided!");
          }
        }


        else {
          // ICdm-955 add the new field to the Command
          if (CommonUtils.isNotNull(this.comboCharVal)) {
            String charValStr = this.comboCharVal.getItem(this.comboCharVal.getSelectionIndex());
            if (!CommonUtils.isEmptyString(charValStr)) {
              attrCharValUpd = setUpdatedCharVal(charValStr);
            }
          }

          // ICDM-2593
          if (this.attrBO.isGrouped()) {
            // Task 229131
            for (Attribute attr : this.predefinedAttributesPage.getPredfndAttrList()) {
              if (!this.predefinedAttributesPage.getSelPredefinedAttrValMap().containsKey(attr)) {
                this.predefinedAttributesPage.getSelPredefinedAttrValMap().put(attr, null);
              }
            }
            // Add commmands
            flag = performPredefAttrCmdActions();
          }

        }
        boolean isAnyFieldChanged = isAnyFieldChanged(attrCharValUpd);

        if ((hasDuplicates || !isAnyFieldChanged) && !(this.attrBO.isGrouped() && this.predefAttrValChanged)) {
          CDMLogger.getInstance().warnDialog(ApicConstants.DUPLICATE_ATTR_VALUE, Activator.PLUGIN_ID);
          return;
        }
        addToCommandStack(this.attrValue, this.attrValueGer, this.descGer, this.descEng, this.changeComment,
            attrCharValUpd, valType);
        if (!flag) {
          super.okPressed();
        }
      }
      else {
        MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
            "Value/Description should not be empty!");
      }
    }
  }


  private boolean isAnyFieldChanged(final CharacteristicValue attrCharValUpd) {
    // return true if any of the fields in the dialog is modified
    boolean isOtherFieldsChanged = false;
    boolean isDescChanged = !this.descGer.equals(this.valClientBO.getAttrValue().getDescriptionGer()) ||
        !this.descEng.equals(this.valClientBO.getAttrValue().getDescriptionEng());
    if (isDescChanged || !this.changeComment.equals(this.valClientBO.getAttrValue().getChangeComment()) ||
        !isCharValChanged(attrCharValUpd)) {
      isOtherFieldsChanged = true;
    }
    return isOtherFieldsChanged;
  }

  /**
   * @param attrCharValUpd
   * @return
   */
  private boolean isCharValChanged(final CharacteristicValue attrCharValUpd) {
    Long existingCharValId = this.valClientBO.getAttrValue().getCharacteristicValueId();
    if (attrCharValUpd == null) {
      return existingCharValId != null;
    }
    return attrCharValUpd.getId().equals(existingCharValId);
  }


  // ICDM-2593
  /**
   * Commands for Predefined attributes page
   */
  private boolean performPredefAttrCmdActions() {
    // Commands for validity values
    cmdActionsforValidityAttrVal();
    // commands for predefined values
    return cmdActionsforPredefinedAttrVal();
  }

  // ICDM-2593
  /**
   * Commands for Validity Attribute & values
   */
  private void cmdActionsforValidityAttrVal() {


    PredefinedValidityCreationModel model = new PredefinedValidityCreationModel();

    if (isNoValidityAttrinDB()) {
      // Add all validity attr values
      addAllSelValidityAttrValues(model);
    }
    else if (isValidityAttrChanged()) {
      // If the validity attribute is changed,delete the old validity
      // attribute and its values
      deleteAllValidityAttrValues(model);
      addAllSelValidityAttrValues(model);
    }
    else {
      removedValueDelCmds(model);
      addedValInsCmds(model);
    }

    PredefinedValidityServiceClient preClient = new PredefinedValidityServiceClient();
    try {
      preClient.createValidity(model);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }

  // ICDM-2593
  /**
   * Commands for predefined Attribute & values
   */
  private boolean cmdActionsforPredefinedAttrVal() {


    PredefinedAttrValuesCreationModel model = new PredefinedAttrValuesCreationModel();
    boolean flag = false;
    for (Attribute newPredefAttr : this.predefinedAttributesPage.getSelPredefinedAttrValMap().keySet()) {
      if (this.predefinedAttributesPage.getSelPredefinedAttrValMap().get(newPredefAttr) == null) {
        MessageDialog.openWarning(Display.getCurrent().getActiveShell(), INVALID_VALUE_INFO,
            "Please add values to all attributes. It is not allowed to save when there are attributes without assigned values.");
        flag = true;
        break;
      }
    }
    if (!flag) {

      if (isNoPredefinedAttrinDB()) {
        addAllSelPredefinedAttrValues(model);
      }
      else {
        predefAttrModifyCmds(model);
        addedPredefAttrValInsCmds(model);
      }
      PredefinedAttrValueServiceClient client = new PredefinedAttrValueServiceClient();
      try {
        client.createPredefinedValues(model);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return flag;


  }

  // ICDM-2593
  /**
   * Addition of a new predefined attribute and value
   *
   * @param model
   */
  private void addedPredefAttrValInsCmds(final PredefinedAttrValuesCreationModel model) {

    Map<Attribute, Long> predefAttrValinDb = new HashMap<>();
    if (null != this.predefinedAttributesPage.getExistingPreDefinedAttrValueSet()) {
      for (com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue existingPredefAttrVal : this.predefinedAttributesPage
          .getExistingPreDefinedAttrValueSet()) {
        predefAttrValinDb.put(
            this.attributesPage.getAttrHandler().getAttrsMap().get(existingPredefAttrVal.getPredefinedAttrId()),
            existingPredefAttrVal.getPredefinedValueId());
      }
    }
    if (null != this.predefinedAttributesPage.getSelPredefinedAttrValMap()) {
      for (Attribute newPredefAttr : this.predefinedAttributesPage.getSelPredefinedAttrValMap().keySet()) {
        if (!predefAttrValinDb.keySet().contains(newPredefAttr)) {
          this.predefAttrValChanged = true;
          addPredefAttrInsCmdtoCmdStack(newPredefAttr.getId(),
              this.predefinedAttributesPage.getSelPredefinedAttrValMap().get(newPredefAttr).getId(), model);
        }
      }
    }
  }

  // ICDM-2593
  /**
   * Modifications in the predefined attribute value (update or delete)
   *
   * @param model
   */
  private void predefAttrModifyCmds(final PredefinedAttrValuesCreationModel model) {
    if (null != this.predefinedAttributesPage.getExistingPreDefinedAttrValueSet()) {
      for (com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue existingPredefAttrVal : this.predefinedAttributesPage
          .getExistingPreDefinedAttrValueSet()) {
        addOrModifyPredefVal(existingPredefAttrVal, model);
      }
    }
  }

  /**
   * @param existingPredefAttrVal
   * @param model
   */
  private void addOrModifyPredefVal(
      final com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue existingPredefAttrVal,
      final PredefinedAttrValuesCreationModel model) {
    if (!this.predefinedAttributesPage.getSelPredefinedAttrValMap().keySet().contains(
        this.attributesPage.getAttrHandler().getAttrsMap().get(existingPredefAttrVal.getPredefinedAttrId()))) { // For
                                                                                                                // delete
      // command
      this.predefAttrValChanged = true;
      addPredefAttrModifyCmdtoCmdStack(existingPredefAttrVal, model);
    }
    else { // Task 229131
      if ((null == existingPredefAttrVal.getPredefinedValueId()) || !existingPredefAttrVal.getPredefinedValueId()
          .equals(this.predefinedAttributesPage.getSelPredefinedAttrValMap()
              .get(this.attributesPage.getAttrHandler().getAttrsMap().get(existingPredefAttrVal.getPredefinedAttrId()))
              .getId())) {
        this.predefAttrValChanged = true;
        // For update command
        addPredefAttrModifyCmdtoCmdStack(existingPredefAttrVal, model);
        addPredefAttrInsCmdtoCmdStack(existingPredefAttrVal.getPredefinedAttrId(),
            this.predefinedAttributesPage.getSelPredefinedAttrValMap()
                .get(
                    this.attributesPage.getAttrHandler().getAttrsMap().get(existingPredefAttrVal.getPredefinedAttrId()))
                .getId(),
            model);
      }
    }
  }

  // ICDM-2593 Create command to delete / modify a predefined attribute value and add it to the command stack
  /**
   * @param existingPredefAttrVal
   * @param isDelete
   * @param model
   */
  private void addPredefAttrModifyCmdtoCmdStack(
      final com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue existingPredefAttrVal,
      final PredefinedAttrValuesCreationModel model) {
    model.getValuesToBeDeleted().add(existingPredefAttrVal);
  }

  // ICDM-2593
  /**
   * Add all the selected predefined attributes and values in the list
   *
   * @param model
   */
  private void addAllSelPredefinedAttrValues(final PredefinedAttrValuesCreationModel model) {
    if (null != this.predefinedAttributesPage.getSelPredefinedAttrValMap()) {
      for (Attribute newPredefAttr : this.predefinedAttributesPage.getSelPredefinedAttrValMap().keySet()) {
        this.predefAttrValChanged = true;
        addPredefAttrInsCmdtoCmdStack(newPredefAttr.getId(),
            this.predefinedAttributesPage.getSelPredefinedAttrValMap().get(newPredefAttr).getId(), model);
      }
    }
  }

  // ICDM-2593
  /**
   * @param newPredefAttr Predefined attribute selected
   * @param newPredefAttrVal Predefined value selected
   * @param model
   */
  private void addPredefAttrInsCmdtoCmdStack(final Long newPredefAttrId, final Long newPredefAttrValId,
      final PredefinedAttrValuesCreationModel model) {
    com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue val =
        new com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue();
    val.setPredefinedAttrId(newPredefAttrId);
    val.setPredefinedValueId(newPredefAttrValId);
    val.setGrpAttrValId(this.selectedValue.get(0).getId());
    model.getValuesToBeCreated().add(val);
  }

  // ICDM-2593
  /**
   * @return
   */
  private boolean isNoPredefinedAttrinDB() {
    return (null == this.predefinedAttributesPage.getExistingPreDefinedAttrValueSet()) ||
        this.predefinedAttributesPage.getExistingPreDefinedAttrValueSet().isEmpty();
  }


  // ICDM-2593
  /**
   * If the validity attribute in DB and the one selected are same, only manipulate the validity values, Add the
   * validity attribute value pair if it is selected now and is not already present in DB
   *
   * @param model
   */

  private void addedValInsCmds(final PredefinedValidityCreationModel model) {
    if (null != this.predefinedAttributesPage.getSelLevAttrValues()) {
      for (AttributeValue attrValNew : this.predefinedAttributesPage.getSelLevAttrValues()) {
        if ((null != this.predefinedAttributesPage.getExistingValidity()) &&
            (null != this.predefinedAttributesPage.getExistingValidity().getValidityAttribute()) &&
            (null != this.predefinedAttributesPage.getExistingValidity().getValidityAttributeValues()) &&
            !this.predefinedAttributesPage.getExistingValidity().getValidityAttributeValues().values()
                .contains(attrValNew)) {
          addValidityAttrInsCmdtoCmdStack(attrValNew, model);
        }
      }
    }
  }


  // ICDM-2593
  /**
   * If the validity attribute in DB and the one selected are same, only manipulate the validity values, Delete the
   * validity attribute value pair if it is deselected now but is already present in DB
   *
   * @param model
   */

  private void removedValueDelCmds(final PredefinedValidityCreationModel model) {
    // Check if validity attribute and values are already available in DB for the selected attribute value
    if ((null != this.predefinedAttributesPage.getExistingValidity()) &&
        (null != this.predefinedAttributesPage.getExistingValidity().getValidityAttribute()) &&
        (null != this.predefinedAttributesPage.getExistingValidity().getValidityAttributeValues())) {
      // If validity attribute values are already available, check whether it is changed now
      for (Entry<PredfndAttrValsValidityClientBO, AttributeValue> entry : this.predefinedAttributesPage
          .getExistingValidity().getValidityAttributeValues().entrySet()) {
        addOrDelValidityAttr(entry, model);
      }
    }
  }


  /**
   * @param entry
   * @param model
   */

  private void addOrDelValidityAttr(final Entry<PredfndAttrValsValidityClientBO, AttributeValue> entry,
      final PredefinedValidityCreationModel model) {
    if (CommonUtils.isNullOrEmpty(this.predefinedAttributesPage.getSelLevAttrValues())) {
      deleteAllValidityAttrValues(model);
    }
    else {
      if (!this.predefinedAttributesPage.getSelLevAttrValues().contains(entry.getValue())) {
        addValidityAttrDelCmdtoCmdStack(entry, model);
      }
    }
  }


  // ICDM-2593
  /**
   * Check if the selected grouped attribute has no validity attribute in DB
   *
   * @return
   */

  private boolean isNoValidityAttrinDB() {
    return (null == this.predefinedAttributesPage.getExistingValidity()) ||
        (null == this.predefinedAttributesPage.getExistingValidity().getValidityAttribute());
  }


  // ICDM-2593
  /**
   * If the group attribute does not have any validity attribute in DB, add the selected validity attribute & values
   *
   * @param model
   */

  private void addAllSelValidityAttrValues(final PredefinedValidityCreationModel model) {
    if (null != this.predefinedAttributesPage.getSelLevAttrValues()) {
      for (AttributeValue newValidityAttrVal : this.predefinedAttributesPage.getSelLevAttrValues()) {
        addValidityAttrInsCmdtoCmdStack(newValidityAttrVal, model);
        this.predefAttrValChanged = true;
      }
    }
  }


  // ICDM-2593
  /**
   * Create Insert command for validity attribute & value and add it to the child command stack
   *
   * @param newValidityAttrVal
   * @param model
   */

  private void addValidityAttrInsCmdtoCmdStack(final AttributeValue newValidityAttrVal,
      final PredefinedValidityCreationModel model) {

    PredefinedValidity val = new PredefinedValidity();
    val.setValidityAttrId(newValidityAttrVal.getAttributeId());
    val.setValidityValueId(newValidityAttrVal.getId());
    val.setGrpAttrValId(this.selectedValue.get(0).getId());
    model.getValidityToBeCreated().add(val);
  }


  // ICDM-2593
  /**
   * Delete all attribute values and the current attribute stored in DB if the validity attribute itself is changed
   *
   * @param model
   */

  private void deleteAllValidityAttrValues(final PredefinedValidityCreationModel model) { // Delete validity attr values
    for (Entry<PredfndAttrValsValidityClientBO, AttributeValue> entry : this.predefinedAttributesPage
        .getExistingValidity().getValidityAttributeValues().entrySet()) {
      addValidityAttrDelCmdtoCmdStack(entry, model);
      this.predefAttrValChanged = true;
    }
  }


  // ICDM-2593
  /**
   * Create Delete command for validity attribute & value and add it to the child command stack
   *
   * @param entry
   * @param model
   */

  private void addValidityAttrDelCmdtoCmdStack(final Entry<PredfndAttrValsValidityClientBO, AttributeValue> entry,
      final PredefinedValidityCreationModel model) {
    model.getValidityToBeDeleted().add(entry.getKey().getPreDfndValdty());
  }


  // ICDM-2593
  /**
   * Check wehether the validity attribute for the selected grouped attribute value is changed
   *
   * @return boolean
   */

  private boolean isValidityAttrChanged() {
    // Check if validity attribute is already available in DB for the selected attribute value
    if ((null != this.predefinedAttributesPage.getExistingValidity()) &&
        (null != this.predefinedAttributesPage.getExistingValidity().getValidityAttribute())) {
      // If validity attribute is already available, check whether it is changed now
      if (!this.predefinedAttributesPage.getSelectedLevelAttr()
          .equals(this.predefinedAttributesPage.getExistingValidity().getValidityAttribute())) {
        return true;
      }
    }
    return false;
  }


  /**
   * @param attrValue
   * @param attrValueGer
   * @param descGer
   * @param descEng This method creates a new record in TABV_ATTRIBUTE_VALUES table
   * @param changeComment
   * @param attrCharValUpd
   * @param attrValType
   */
  private void addToCommandStack(final String attrValue, final String attrValueGer, final String descGer,
      final String descEng, final String changeComment, final CharacteristicValue attrCharValUpd,
      final AttributeValueType attrValType) {


    AttributeValueServiceClient serClient = new AttributeValueServiceClient();

    AttributeValue attrValueToBeUpd = this.selectedValue.get(0);

    AttributeValueClientBO valBO = null;
    try {
      // get latest updated attribute value object from db after updation of links
      AttributeValue attrValueAfterLinksUpdation = CommonUiUtils.getInstance().createMultipleLinkService(
          (SortedSet<LinkData>) this.linksTabViewer.getInput(), attrValueToBeUpd.getId(), MODEL_TYPE.ATTRIB_VALUE);
      if (attrValueAfterLinksUpdation != null) {
        attrValueToBeUpd = attrValueAfterLinksUpdation;
      }

      valBO = new AttributeValueClientBO(attrValueToBeUpd);


      switch (attrValType) {
        case TEXT:
        case ICDM_USER:
        case HYPERLINK:

          attrValueToBeUpd.setTextValueEng(attrValue);
          attrValueToBeUpd.setTextValueGer(attrValueGer);
          break;

        case NUMBER:
          attrValueToBeUpd.setNumValue(new BigDecimal(attrValue));
          break;

        case DATE:
          attrValueToBeUpd.setDateValue(
              AttributeCommon.convertAttrDateStringToDefaultDateFormat(this.attribute.getFormat(), attrValue));
          break;

        case BOOLEAN:
          attrValueToBeUpd.setBoolvalue(attrValue);
          break;
        default:
          CDMLogger.getInstance().errorDialog("Invalid Data Type", Activator.PLUGIN_ID);
          break;
      }


      attrValueToBeUpd.setDescriptionEng(descEng);
      attrValueToBeUpd.setDescriptionGer(descGer);
      attrValueToBeUpd.setCharacteristicValueId(null == attrCharValUpd ? null : attrCharValUpd.getId());

      attrValueToBeUpd.setChangeComment(changeComment);

      attrValueToBeUpd = serClient.update(attrValueToBeUpd);

      SortedSet<AttributeValue> values = this.attributesPage.getSelectedValuesMap().get(valBO.getAttribute());
      if (null != values) {
        if (values.contains(attrValueToBeUpd)) {
          values.remove(attrValueToBeUpd);
        }
        values.add(attrValueToBeUpd);

      }
      this.attributesPage.getValueTableViewer().setInput(values);

      this.attributesPage.getValueTableViewer().refresh();
    }
    catch (ParseException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }
}