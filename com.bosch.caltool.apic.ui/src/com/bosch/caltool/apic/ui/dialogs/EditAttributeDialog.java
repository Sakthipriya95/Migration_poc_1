/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.editors.pages.AttributesRightsPage;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * This class provides a dialog to edit an attribute
 */
public class EditAttributeDialog extends AttributeDialog {

  /**
   * Dialog title
   */
  private static final String DIALOG_TITLE = "Edit Attribute";
  // Constants for the Width and height
  /**
   * Dialog Width
   */
  private static final int DIALOG_WIDTH = 1000;
  /**
   * Default height
   */
  private static final int DIALOG_NORM_HEIGHT = 800;

  /**
   * message
   */
  private static final String DIALOG_WARN_MESSAGE =
      "The value(s) for this attribute have Characteristic Value mappings. Continuing will clear these associations. Click OK to proceed.";

  /**
   * Attribute page
   */
  private final AttributesPage attributesPage;
  /**
   * Selected list of attributes
   */
  private final List<Attribute> selectedAttributes;

  /**
   * Attribute rights page
   */
  private AttributesRightsPage rightsPage;

  private final AttributeClientBO attrBO;


  /**
   * new instance of the dialog
   *
   * @param parentShell parent shell
   * @param attributesPage parent attribute page
   */
  public EditAttributeDialog(final Shell parentShell, final AttributesPage attributesPage) {
    super(parentShell, attributesPage, true);
    this.attributesPage = attributesPage;

    this.selectedAttributes = attributesPage.getAttrHandler().getSelectedAttributes();
    this.attrBO = new AttributeClientBO(this.selectedAttributes.get(0));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // icdm-253
    newShell.setText(DIALOG_TITLE);
    newShell.layout(true, true);// ICDM-1781
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
    // Icdm-327
    // icdm-253 && icdm-306
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
    TabItem editAttrTabItem = new TabItem(tabFolder, SWT.NONE);
    editAttrTabItem.setText(DIALOG_TITLE);
    final Control contents = super.createContents(editAttrTabItem.getParent());

    editAttrTabItem.setControl(contents);
    TabItem accessRightTabItem = new TabItem(tabFolder, SWT.NONE);
    accessRightTabItem.setText("Access Rights");
    NodeAccessPageDataHandler nodeAccessBO = new NodeAccessPageDataHandler(this.selectedAttributes.get(0));
    nodeAccessBO.setNormalized(this.selectedAttributes.get(0).isNormalized());
    this.rightsPage = new AttributesRightsPage(this.selectedAttributes.get(0).getName(), nodeAccessBO);
    this.rightsPage.createPartControl(tabFolder);
    accessRightTabItem.setControl(this.rightsPage.getPartControl());

    gridData1.horizontalAlignment = GridData.FILL;
    // Set the title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Edit Attribute Details", IMessageProvider.INFORMATION);
    setExistingValues();
    if (!this.attrBO.canModifyValues()) {
      disableAllfields();
    }

    return comp;
  }


  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * Disable the fields, if attribute cannot be edited
   */
  private void disableAllfields() {
    this.comboDateFormat.setEnabled(false);
    this.comboGrp.setEnabled(false);
    this.comboMandatory.setEnabled(false);
    this.comboNormFlag.setEnabled(false);
    this.comboValType.setEnabled(false);
    this.descEngText.setEnabled(false);
    this.descGermText.setEnabled(false);
    this.nameEngText.setEnabled(false);
    this.nameGermText.setEnabled(false);
    this.comboSuperGrp.setEnabled(false);
    this.chkBoxPartNumFlag.setEnabled(false);
    this.chkBoxSpecLinkFlag.setEnabled(false);
    // ICDM-452
    this.newLinkAction.setEnabled(false);
    this.editLinkAction.setEnabled(false);
    this.deleteLinkAction.setEnabled(false);
    // Icdm-480
    this.comboAttrSecure.setEnabled(false);
    this.comboAttrValSecurity.setEnabled(false);
    this.comboChar.setEnabled(false);
    // iCDM-2035
    this.eadmNameText.setEnabled(false);
    this.textBoxContentDisplay.setEditable(false);
  }


  /**
   * Populate the existing data, in edit mode/read mode
   */
  private void setExistingValues() {
    Attribute selAttribute = this.selectedAttributes.get(0);
    this.nameEngText.setText(selAttribute.getNameEng());
    this.nameGermText.setText(selAttribute.getNameGer() == null ? "" : selAttribute.getNameGer());
    this.descEngText.setText(selAttribute.getDescriptionEng());
    this.descGermText.setText(selAttribute.getDescriptionGer() == null ? "" : selAttribute.getDescriptionGer());
    this.unitText.setText(selAttribute.getUnit());
    this.comment.setText(selAttribute.getChangeComment() == null ? "" : selAttribute.getChangeComment());
    this.eadmNameText.setText(selAttribute.getEadmName());
    String[] itemsValType = this.comboValType.getItems();
    selectValType(selAttribute, itemsValType);


    SortedSet<AttributeValue> attrValues = this.attrBO.getAttrValues();

    enableDisableWidgets(attrValues);

    AttrGroup attributeGroup =
        this.attributesPage.getAttrGroupModel().getAllGroupMap().get(selAttribute.getAttrGrpId());
    AttrSuperGroup selectedAttrSuperGrp =
        this.attributesPage.getAttrGroupModel().getAllSuperGroupMap().get(attributeGroup.getSuperGrpId());
    String[] items = this.comboSuperGrp.getItems();

    selectSuperGroup(selectedAttrSuperGrp, items);


    Set<Long> grpIds =
        this.attributesPage.getAttrGroupModel().getGroupBySuperGroupMap().get(selectedAttrSuperGrp.getId());
    for (Long grpId : grpIds) {
      this.groups.add(this.attributesPage.getAttrGroupModel().getAllGroupMap().get(grpId));
    }
    setGroups(this.groups, null);
    String[] itemsGrp = this.comboGrp.getItems();


    selectGrp(attributeGroup, itemsGrp);
    if (selAttribute.getValueType().equals(AttributeValueType.DATE.toString())) {
      SortedSet<String> dateFormats = CommonUiUtils.getDateFormats();
      populateDataformatInCombo(dateFormats);
      String[] itemsDateFrmt = this.comboDateFormat.getItems();
      selectDateFormat(selAttribute, itemsDateFrmt);
    }
    else {
      this.comboDateFormat.setEnabled(false);
    }
    if (selAttribute.getValueType().equals(AttributeValueType.NUMBER.toString())) {
      this.numberFrmtText.setText((selAttribute.getFormat() == null) ? "" : selAttribute.getFormat());
    }
    else {
      this.numberFrmtText.setEnabled(false);
    }
    if (!attrValues.isEmpty()) {
      selectNormFlag(selAttribute);
      this.comboNormFlag.setEnabled(false);
    }
    else {
      this.comboNormFlag.setEnabled(true);
      selectNormFlag(selAttribute);

    }
    selectAddValByUserFlag(selAttribute);
    selectAttrMandatoryFlag(selAttribute);
    this.chkBoxPartNumFlag.setSelection(selAttribute.isWithPartNumber());
    this.chkBoxSpecLinkFlag.setSelection(selAttribute.isWithSpecLink());
    Validator.getInstance().validateNDecorate(this.txtNameEngDec, this.txtDescEngDec, this.comboValTypeDec,
        this.comboNormFlagDec, this.comboMandatoryDec, this.nameEngText, this.descEngText, this.comboValType,
        this.comboNormFlag, this.comboMandatory, true);
    Validator.getInstance().validateNDecorate(this.comboSuperGrpDec, this.comboSuperGrp, true);
    Validator.getInstance().validateNDecorate(this.comboGrpDec, this.comboGrp, true);
    // ICDM-452,1502

    SortedSet<LinkData> linkDataCollection = getLinks();

    this.linksTabViewer.setInput(linkDataCollection);
    this.linksTabViewer.refresh();
    // Icdm-480
    selectAttrSecureFlag(selAttribute);

    selectAttrValSecurityFlag(selAttribute);

    Characteristic attrChar = getAttrChar(selAttribute);

    selectAttrChar(attrChar);
    selectGrpdAttr(selAttribute);
  }

  /**
   * @param selAttribute
   * @param attrChar
   * @return
   */
  private Characteristic getAttrChar(final Attribute selAttribute) {
    Characteristic attrChar = null;
    if (selAttribute.getCharacteristicId() != null) {
      for (Characteristic characteristic : this.characteristics) {
        if (selAttribute.getCharacteristicId().equals(characteristic.getId())) {
          attrChar = characteristic;
          break;
        }
      }
    }
    return attrChar;
  }

  /**
   * @param selAttribute
   */
  private void selectGrpdAttr(final Attribute selAttribute) {
    if (selAttribute.isGroupedAttr()) {
      this.comboGrpdAttr.select(this.comboGrpdAttr.indexOf(ApicConstants.USED_YES_DISPLAY));
    }
    else {
      this.comboGrpdAttr.select(this.comboGrpdAttr.indexOf(ApicConstants.USED_NO_DISPLAY));
    }
  }

  /**
   * @param attrChar
   */
  private void selectAttrChar(final Characteristic attrChar) {
    if (attrChar == null) {
      this.comboChar.select(this.comboChar.indexOf("<SELECT>"));

    }
    else {
      this.comboChar.select(this.comboChar.indexOf(attrChar.getName()));
    }
  }

  /**
   * @param selAttribute
   */
  private void selectAttrValSecurityFlag(final Attribute selAttribute) {
    if (selAttribute.isExternalValue()) {
      this.comboAttrValSecurity.select(this.comboAttrValSecurity.indexOf(ApicConstants.USED_YES_DISPLAY));
    }
    else {
      this.comboAttrValSecurity.select(this.comboAttrValSecurity.indexOf(ApicConstants.USED_NO_DISPLAY));
    }
  }

  /**
   * @param selAttribute
   */
  private void selectAttrSecureFlag(final Attribute selAttribute) {
    if (selAttribute.isExternal()) {
      this.comboAttrSecure.select(this.comboAttrSecure.indexOf(ApicConstants.USED_YES_DISPLAY));
    }
    else {
      this.comboAttrSecure.select(this.comboAttrSecure.indexOf(ApicConstants.USED_NO_DISPLAY));
    }
  }

  /**
   * @param selAttribute
   */
  private void selectAttrMandatoryFlag(final Attribute selAttribute) {
    if (selAttribute.isMandatory()) {
      this.comboMandatory.select(this.comboMandatory.indexOf(ApicConstants.USED_YES_DISPLAY));
    }
    else {
      this.comboMandatory.select(this.comboMandatory.indexOf(ApicConstants.USED_NO_DISPLAY));
    }
  }

  /**
   * @param selAttribute
   */
  private void selectAddValByUserFlag(final Attribute selAttribute) {
    if (selAttribute.isAddValByUserFlag()) {
      this.comboAddValFlag.select(this.comboAddValFlag.indexOf(ApicConstants.USED_YES_DISPLAY));
    }
    else {
      this.comboAddValFlag.select(this.comboAddValFlag.indexOf(ApicConstants.USED_NO_DISPLAY));
    }
  }

  /**
   * @param selAttribute
   */
  private void selectNormFlag(final Attribute selAttribute) {
    if (selAttribute.isNormalized()) {
      this.comboNormFlag.select(this.comboNormFlag.indexOf(ApicConstants.USED_YES_DISPLAY));
    }
    else {
      this.comboNormFlag.select(this.comboNormFlag.indexOf(ApicConstants.USED_NO_DISPLAY));
    }
  }

  /**
   * @param dateFormats
   */
  private void populateDataformatInCombo(final SortedSet<String> dateFormats) {
    for (String string : dateFormats) {
      this.comboDateFormat.add(string);
    }
  }

  /**
   * @param selAttribute
   * @param itemsDateFrmt
   */
  private void selectDateFormat(final Attribute selAttribute, final String[] itemsDateFrmt) {
    for (String string : itemsDateFrmt) {
      if (string.equalsIgnoreCase(selAttribute.getFormat())) {
        int index = this.comboDateFormat.indexOf(string);
        this.comboDateFormat.select(index);
        break;
      }
    }
  }

  /**
   * @param attributeGroup
   * @param itemsGrp
   */
  private void selectGrp(final AttrGroup attributeGroup, final String[] itemsGrp) {
    for (String grpName : itemsGrp) {
      if (grpName.equalsIgnoreCase(attributeGroup.getName())) {
        int index = this.comboGrp.indexOf(grpName);
        this.comboGrp.select(index);
        break;
      }
    }
  }

  /**
   * @param selectedAttrSuperGrp
   * @param items
   */
  private void selectSuperGroup(final AttrSuperGroup selectedAttrSuperGrp, final String[] items) {
    for (String superGrpName : items) {
      if (superGrpName.equalsIgnoreCase(selectedAttrSuperGrp.getName())) {
        int index = this.comboSuperGrp.indexOf(superGrpName);
        this.comboSuperGrp.select(index);
        break;
      }
    }
  }

  /**
   * @param selAttribute
   * @param itemsValType
   */
  private void selectValType(final Attribute selAttribute, final String[] itemsValType) {
    for (String string : itemsValType) {
      if (selAttribute.getValueType().equalsIgnoreCase(string)) {
        int index = this.comboValType.indexOf(string);
        this.comboValType.select(index);
        break;
      }
    }
  }

  /**
   * @param attrValues
   */
  private void enableDisableWidgets(final SortedSet<AttributeValue> attrValues) {
    if (attrValues.isEmpty()) {
      this.comboValType.setEnabled(true);
      int index = this.comboValType.getSelectionIndex();
      if (this.comboValType.getItem(index).equalsIgnoreCase(AttributeValueType.DATE.getDisplayText())) {
        this.comboDateFormat.setEnabled(true);
      }
      else if (this.comboValType.getItem(index).equalsIgnoreCase(AttributeValueType.NUMBER.getDisplayText())) {
        this.numberFrmtText.setEnabled(true);
        this.unitText.setEnabled(true);
      }
    }
    else {
      this.comboValType.setEnabled(false);
      this.comboDateFormat.setEnabled(false);
      this.numberFrmtText.setEnabled(false);
      this.unitText.setEnabled(false);
    }
  }

  /**
   * @return
   */
  private SortedSet<LinkData> getLinks() {
    SortedSet<LinkData> linkDataCollection = new TreeSet<>();
    LinkServiceClient linkServiceClient = new LinkServiceClient();
    Set<Long> nodesWithLink = null;
    try {
      nodesWithLink = linkServiceClient.getNodesWithLink(MODEL_TYPE.ATTRIBUTE);
      boolean hasLinks = nodesWithLink.contains(this.selectedAttributes.get(0).getId());
      if (hasLinks) {
        Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
            linkServiceClient.getAllLinksByNode(this.selectedAttributes.get(0).getId(), MODEL_TYPE.ATTRIBUTE);
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // Icdm-955 check if the Char Name is Changed
    boolean valueChanged = isCharNameChanged(this.selectedAttributes.get(0));
    if (valueChanged &&
        !MessageDialogUtils.getConfirmMessageDialog("Press ok to continue with the update", DIALOG_WARN_MESSAGE)) {
      return;
    }

    if (!validateGroupedFlagChange(this.selectedAttributes.get(0))) {
      return;
    }

    String nameEng = this.nameEngText.getText().trim();
    String nameGer = this.nameGermText.getText().trim();
    String descEng = this.descEngText.getText().trim();
    String descGer = this.descGermText.getText().trim();
    String changeComment = this.comment.getText().trim();
    String eadmName = this.eadmNameText.getText().trim();
    int grpIndex = this.comboGrp.getSelectionIndex();
    String grpItem = this.comboGrp.getItem(grpIndex);
    for (AttrGroup attrGroup : this.groups) {
      if (attrGroup.getName().equalsIgnoreCase(grpItem)) {
        this.selectedAttrGroup = attrGroup;
        break;
      }
    }
    String unit = this.unitText.getText().trim();

    int valTypeindex = this.comboValType.getSelectionIndex();
    String valTypeItem = this.comboValType.getItem(valTypeindex);
    AttributeValueType valType = AttributeValueType.getType(valTypeItem);
    String format = getValFormat(valTypeItem, valType);
    int normFlagindex = this.comboNormFlag.getSelectionIndex();
    String normFlagitem = this.comboNormFlag.getItem(normFlagindex);

    int addValFlagIndex = this.comboAddValFlag.getSelectionIndex();
    String addValFlagItem = this.comboAddValFlag.getItem(addValFlagIndex);

    int mandatoryIndex = this.comboMandatory.getSelectionIndex();
    String valMandatory = this.comboMandatory.getItem(mandatoryIndex);

    boolean partNumFlag = this.chkBoxPartNumFlag.getSelection();
    boolean specLinkFlag = this.chkBoxSpecLinkFlag.getSelection();
    boolean attrExtFlag = false;
    boolean attrValExtFlag = false;
    /**
     * Icdm-480 Combo for Attr and Value Security
     */
    if (this.comboAttrSecure.getItem(this.comboAttrSecure.getSelectionIndex()).equals(ApicConstants.USED_YES_DISPLAY)) {
      attrExtFlag = true;
    }

    if (this.comboAttrValSecurity.getItem(this.comboAttrValSecurity.getSelectionIndex())
        .equals(ApicConstants.USED_YES_DISPLAY)) {
      attrValExtFlag = true;
    }
    // ICdm-955 add the new Field to the command
    Characteristic attrCharUpd = null;
    String charName = this.comboChar.getItem(this.comboChar.getSelectionIndex());
    for (Characteristic attrChar : this.characteristics) {
      if (attrChar.getName().equals(charName)) {
        attrCharUpd = attrChar;
      }
    }
    String valGrpdAttr = this.comboGrpdAttr.getItem(this.comboGrpdAttr.getSelectionIndex());
    setModifiedDetails(nameEng, descEng, nameGer, descGer, unit, valType, format, normFlagitem, partNumFlag,
        specLinkFlag, valMandatory, attrExtFlag, attrValExtFlag, attrCharUpd, valueChanged, changeComment, eadmName,
        valGrpdAttr, addValFlagItem);
  }

  /**
   * @param valTypeItem
   * @param valType
   * @param format
   * @return
   */
  private String getValFormat(final String valTypeItem, final AttributeValueType valType) {
    String format = null;
    if (valTypeItem != null) {
      if (valType == AttributeValueType.DATE) {
        int index = this.comboDateFormat.getSelectionIndex();
        String selectItem = this.comboDateFormat.getItem(index);
        if ("Date formats are not defined".equalsIgnoreCase(selectItem.trim())) {
          format = "";
        }
        else {
          format = selectItem;
        }
      }
      else if (valType == AttributeValueType.NUMBER) {
        format = this.numberFrmtText.getText().trim();
      }
    }
    return format;
  }

  /**
   * check if the Attr Char name is Changed
   *
   * @param attr attribute
   * @return true if the char name is changed
   */
  // Icdm-955
  private boolean isCharNameChanged(final Attribute attr) {
    AttributeClientBO attrBo = new AttributeClientBO(attr);
    Characteristic characteristic = attrBo.getCharacteristic();
    if (characteristic != null) {
      String currSel = this.comboChar.getItem(this.comboChar.getSelectionIndex());
      if (!currSel.equals(characteristic.getName()) && attrBo.isWithCharValue()) {
        return true;
      }
    }
    return false;
  }

  /**
   * check if the Grouped Attr flag is Changed
   *
   * @param attr attribute
   * @return true if the Grouped Attr flag is changed
   */
  private boolean validateGroupedFlagChange(final Attribute attr) {

    String currSel = this.comboGrpdAttr.getItem(this.comboGrpdAttr.getSelectionIndex());
    if (currSel.equals(ApicConstants.USED_NO_DISPLAY) && attr.isGroupedAttr()) {
      this.comboGrpdAttr.select(1);
      if (this.preDefndValuesPresent) {
        MessageDialogUtils.getErrorMessageDialog("Update not possible",
            "Cannot update the grouped attribute flag , since there are predefined attributes added to this grouped attribute.");
        return false;

      }
    }

    return true;
  }

  /**
   * Add details to the attribute command
   *
   * @param nameEng
   * @param descEng
   * @param nameGer
   * @param descGer
   * @param unit
   * @param valType
   * @param format
   * @param normFlagitem
   * @param specLinkFlag
   * @param partNumFlag
   * @param valMandatory
   * @param attrValExtFlag
   * @param attrExtFlag
   * @param attrCharUpd
   * @param canDelCharVal
   * @param changeComment
   * @param eadmName
   * @param valGrpdAttr
   */
  private void setModifiedDetails(final String nameEng, final String descEng, final String nameGer,
      final String descGer, final String unit, final AttributeValueType valType, final String format,
      final String normFlagitem, final boolean partNumFlag, final boolean specLinkFlag, final String valMandatory,
      final boolean attrExtFlag, final boolean attrValExtFlag, final Characteristic attrCharUpd,
      final boolean canDelCharVal, final String changeComment, final String eadmName, final String valGrpdAttr,
      final String addValByUserFlag) {

    Attribute attrToBeUpdated = this.selectedAttributes.get(0);
    attrToBeUpdated.setNameEng(nameEng);
    attrToBeUpdated.setDescriptionEng(descEng);
    attrToBeUpdated.setNameGer(nameGer);
    attrToBeUpdated.setDescriptionGer(descGer);
    attrToBeUpdated.setChangeComment(changeComment);
    // ICDM-1560
    attrToBeUpdated.setEadmName(eadmName);
    SortedSet<AttrGroup> groupSet = getAllGroups();
    Long groupId = null;

    for (AttrGroup entry : groupSet) {
      if ((this.selectedAttrGroup).equals(entry)) {
        groupId = entry.getId();
        break;
      }
    }

    if (groupId != null) {
      attrToBeUpdated.setAttrGrpId(groupId);
    }
    attrToBeUpdated.setValueType(valType.toString());
    attrToBeUpdated.setUnit(unit);
    attrToBeUpdated.setFormat(format);
    attrToBeUpdated.setNormalized(ApicConstants.USED_YES_DISPLAY.equals(normFlagitem));
    attrToBeUpdated.setMandatory(ApicConstants.USED_YES_DISPLAY.equals(valMandatory));
    attrToBeUpdated.setWithPartNumber(partNumFlag);
    attrToBeUpdated.setWithSpecLink(specLinkFlag);
    // ICdm-480
    attrToBeUpdated.setExternal(attrExtFlag);
    attrToBeUpdated.setExternalValue(attrValExtFlag);
    if (null != attrCharUpd) {
      attrToBeUpdated.setCharacteristicId(attrCharUpd.getId());
    }
    attrToBeUpdated.setDelCharFlag(canDelCharVal);
    attrToBeUpdated.setValueTypeId(valType.getValueTypeID());
    attrToBeUpdated.setGroupedAttr(ApicConstants.USED_YES_DISPLAY.equals(valGrpdAttr));
    attrToBeUpdated.setAddValByUserFlag(ApicConstants.USED_YES_DISPLAY.equals(addValByUserFlag));
    // For an attribute which is not normalized,value can be created by users flag cannot be set to NO
    validateNUpdateAttr(attrToBeUpdated);
  }

  /**
   * @param attrToBeUpdated
   */
  private void validateNUpdateAttr(final Attribute attrToBeUpdated) {
    if (!attrToBeUpdated.isNormalized() && !attrToBeUpdated.isAddValByUserFlag()) {
      attrToBeUpdated.setAddValByUserFlag(true);
      CDMLogger.getInstance().errorDialog(
          "\'Values can be created by users\' option cannot be set to NO as this not a normalized attribute",
          Activator.PLUGIN_ID);
      this.comboAddValFlag.setText(ApicConstants.USED_YES_DISPLAY);
    }
    // For normalized attribute ,value can be created by users flag-NO,check whether special access rights are defined
    else if (attrToBeUpdated.isNormalized() && !attrToBeUpdated.isAddValByUserFlag() && !isSpclWriteAccessDefined()) {
      CDMLogger.getInstance().errorDialog(
          "For normalized attributes, when \'Values can be created by users\' option is set to NO,  it is mandatory to define write access",
          Activator.PLUGIN_ID);
    }
    // when all validations are met then only proceeed with saving the attribute and access rights in db and close the
    // dialog
    else {
      if (this.rightsPage != null) {
        this.rightsPage.saveUpdate();
      }
      updateAttribute(attrToBeUpdated);
      this.attributesPage.getAttrTableViewer().refresh();
      super.okPressed();
    }
  }

  /**
   * @return boolean
   */
  private boolean isSpclWriteAccessDefined() {
    if (this.rightsPage != null) {
      Set<NodeAccess> nodeAccessSet = new HashSet<>();
      nodeAccessSet.addAll(this.rightsPage.getNodeAccessToCreate());
      nodeAccessSet.addAll(this.rightsPage.getNodeAccessToUpdate());
      for (NodeAccess existingNodeAccess : this.rightsPage.getDataHandler().getNodeAccess()) {
        nodeAccessSet.add(existingNodeAccess);
      }

      for (NodeAccess nodeAccess : nodeAccessSet) {
        if (nodeAccess.isWrite()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param attrToBeUpdated
   */
  private void updateAttribute(final Attribute attrToBeUpdated) {
    try {
      AttributeServiceClient attrClient = new AttributeServiceClient();
      CommonUiUtils.getInstance().createMultipleLinkService((SortedSet<LinkData>) this.linksTabViewer.getInput(),
          attrToBeUpdated.getId(), MODEL_TYPE.ATTRIBUTE);
      attrClient.update(attrToBeUpdated);

    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Validates save button enable or disable
   */
  @Override
  protected void checkSaveBtnEnable() {
    super.checkSaveBtnEnable();

    if (this.saveBtn != null) {
      this.saveBtn.setEnabled(this.saveBtn.getEnabled() && this.linksChanged);
    }
  }

  /**
   * @return SortedSet<AttrGroup>
   */
  public SortedSet<AttrGroup> getAllGroups() {
    SortedSet<AttrGroup> groupsSet = new TreeSet<>();
    groupsSet.addAll(this.attributesPage.getAttrGroupModel().getAllGroupMap().values());
    return groupsSet;
  }
}