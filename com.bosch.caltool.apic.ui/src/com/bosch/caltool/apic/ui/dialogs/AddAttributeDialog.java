/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * This class provides a dialog to add a new attribute
 */
public class AddAttributeDialog extends AttributeDialog {

  /**
   * AttributesPage instance
   */
  private final AttributesPage attributesPage;


  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param attributesPage instance
   */
  public AddAttributeDialog(final Shell parentShell, final AttributesPage attributesPage) {
    super(parentShell, attributesPage, false);
    this.attributesPage = attributesPage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Add Attribute");
    super.configureShell(newShell);
    // Icdm-326
    super.setHelpAvailable(true);
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
    setTitle("Add Attribute");
    // Set the message
    setMessage("Enter Attribute Details", IMessageProvider.INFORMATION);
    disableValAddFlag();
    return contents;
  }

  // Icdm-327
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
  protected void okPressed() {

    String nameEng = this.nameEngText.getText().trim();
    String nameGer = this.nameGermText.getText().trim();
    String descEng = this.descEngText.getText().trim();
    String descGer = this.descGermText.getText().trim();
    int grpIndex = this.comboGrp.getSelectionIndex();
    String grpItem = this.comboGrp.getItem(grpIndex);
    String changeComment = this.comment.getText().trim();
    String eadmName = this.eadmNameText.getText();
    // get selected attribute
    for (AttrGroup attrGroup : this.groups) {
      if (attrGroup.getName().equalsIgnoreCase(grpItem)) {
        this.selectedAttrGroup = attrGroup;
        break;
      }
    }
    // get all the values to be set for attr
    String unit = this.unitText.getText().trim();

    int valTypeindex = this.comboValType.getSelectionIndex();
    String valTypeItem = this.comboValType.getItem(valTypeindex);
    AttributeValueType valType = AttributeValueType.getType(valTypeItem);

    String format = null;
    if (valTypeItem != null) {
      format = getFormat(valType);
    }
    int normFlagindex = this.comboNormFlag.getSelectionIndex();
    String normFlagitem = this.comboNormFlag.getItem(normFlagindex);

    int addValFlagIndex = this.comboAddValFlag.getSelectionIndex();
    String addValFlagItem = this.comboAddValFlag.getItem(addValFlagIndex);

    int mandatoryIndex = this.comboMandatory.getSelectionIndex();
    String valMandatory = this.comboMandatory.getItem(mandatoryIndex);

    boolean partNumFlag = this.chkBoxPartNumFlag.getSelection();
    boolean specLinkFlag = this.chkBoxSpecLinkFlag.getSelection();
    // Iddm-480
    boolean attrExtFlag = false;
    boolean attrValExtFlag = false;

    if (this.comboAttrSecure.getItem(this.comboAttrSecure.getSelectionIndex()).equals(ApicConstants.USED_YES_DISPLAY)) {
      attrExtFlag = true;
    }

    if (this.comboAttrValSecurity.getItem(this.comboAttrValSecurity.getSelectionIndex())
        .equals(ApicConstants.USED_YES_DISPLAY)) {
      attrValExtFlag = true;
    }
    // Add the new Field Attr Char Icdm-955
    Characteristic attrCharUpd = null;
    String charName = this.comboChar.getItem(this.comboChar.getSelectionIndex());
    for (Characteristic attrChar : this.characteristics) {

      if (attrChar.getName().equals(charName)) {
        attrCharUpd = attrChar;
      }
    }

    String valGrpdAttr = this.comboGrpdAttr.getItem(this.comboGrpdAttr.getSelectionIndex());

    // create the new attribute
    addToCommandStack(nameEng, descEng, nameGer, descGer, unit, valTypeItem, valType.getValueTypeID(), format,
        normFlagitem, partNumFlag, specLinkFlag, valMandatory, attrExtFlag, attrValExtFlag, attrCharUpd, changeComment,
        eadmName, valGrpdAttr, addValFlagItem);
   super.okPressed();
 }


  /**
   * @param valType AttributeValueType
   * @return String
   */
  private String getFormat(final AttributeValueType valType) {
    String format = null;
    if (valType == AttributeValueType.DATE) {
      int index = this.comboDateFormat.getSelectionIndex();
      String selectItem = this.comboDateFormat.getItem(index);
      if (!"Date formats are not defined".equalsIgnoreCase(selectItem.trim())) {
        format = selectItem;
      }
      else {
        format = "";
      }
    }
    else if (valType == AttributeValueType.NUMBER) {
      format = this.numberFrmtText.getText().trim();
    }
    return format;
  }

  /**
   * This method creates a new record in TABV_ATTRIBUTES table
   *
   * @param nameEng
   * @param descEng
   * @param unit
   * @param valType
   * @param valTypeId
   * @param format
   * @param normFlagitem
   * @param partNumFlag
   * @param specLinkFlag
   * @param valMandatory
   * @param attrExtFlag
   * @param attrValExtFlag
   * @param attrCharUpd
   * @param changeComment
   * @param eadmName
   * @param valGrpdAttr
   */
  private void addToCommandStack(final String nameEng, final String descEng, final String nameGer, final String descGer,
      final String unit, final String valType, final long valTypeId, final String format, final String normFlagitem,
      final boolean partNumFlag, final boolean specLinkFlag, final String valMandatory, final boolean attrExtFlag,
      final boolean attrValExtFlag, final Characteristic attrCharUpd, final String changeComment, final String eadmName,
      final String valGrpdAttr, final String addValFlagItem) {

    AttributeServiceClient serviceClient = new AttributeServiceClient();
    Attribute attribute = new Attribute();
    attribute.setNameEng(nameEng);
    attribute.setDescriptionEng(descEng);
    attribute.setNameGer(nameGer);
    attribute.setDescriptionGer(descGer);
    attribute.setChangeComment(changeComment);
    attribute.setValueTypeId(valTypeId);
    // ICDM-1560
    attribute.setEadmName(eadmName);
    try {
      Map<Long, AttrGroup> groupMap = new AttrGroupServiceClient().getAll();

      for (Entry<Long, AttrGroup> entry : groupMap.entrySet()) {
        if ((this.selectedAttrGroup.getId()).equals(entry.getValue().getId())) {
          attribute.setAttrGrpId(entry.getKey());
          break;
        }
      }
    }
    catch (ApicWebServiceException e1) {
      CDMLogger.getInstance().error(e1.getLocalizedMessage(), e1, Activator.PLUGIN_ID);
    }

    attribute.setValueType(valType);
    attribute.setUnit(unit);
    attribute.setFormat(format);
    attribute.setNormalized(ApicConstants.USED_YES_DISPLAY.equals(normFlagitem));
    attribute.setWithPartNumber(partNumFlag);
    attribute.setWithSpecLink(specLinkFlag);
    attribute.setMandatory(ApicConstants.USED_YES_DISPLAY.equals(valMandatory));
    // Iddm-480
    attribute.setExternal(attrExtFlag);
    attribute.setExternalValue(attrValExtFlag);
    if (CommonUtils.isNotNull(attrCharUpd)) {
      attribute.setCharacteristicId(attrCharUpd.getId());
    }

    attribute.setGroupedAttr(ApicConstants.USED_YES_DISPLAY.equals(valGrpdAttr));
    attribute.setAddValByUserFlag(ApicConstants.USED_YES_DISPLAY.equals(addValFlagItem));
    try {
      Attribute newAttr = serviceClient.create(attribute);
      this.attributesPage.getAttrTableViewer().setInput(this.attributesPage.getAttrHandler().getAllAttrsFromWS());
      // To select the newly added attribute in Attributes editor
      GridTableViewerUtil.getInstance().setSelection(this.attributesPage.getAttrTableViewer(), newAttr);
      // To enable icons in "Attributes Dependency" and "Values" form pages in Attributes editor on addition of new
      // attributes
      this.attributesPage.getAttrTableViewer().getControl().notifyListeners(SWT.Selection, null);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }


  }


}