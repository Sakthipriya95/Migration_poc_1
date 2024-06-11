/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;


import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.PIDCPageEditUtil;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.apic.AttributeCommon;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.CharacteristicValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantCopyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.IUtilityConstants;

/**
 * This class provides UI to add value
 */
public class AddValueDialog extends ValueDialog {

  /**
   * Defines ApicObject
   */
  // ICDM-108
  private final IModel apicObject;

  /**
   * ColumnViewer instance
   */
  // ICDM-94
  private final ColumnViewer viewer;

  /**
   * PIDCPage instance
   */
  private final PIDCAttrPage pidcPage;

  /**
   * Defines to enable paste dialog
   */
  // ICDM-150
  private final boolean isPasteAction;
  // ICDM-260
  private AttributeValue newAttrValue;

  private boolean isMulVar;

  private boolean isMulSubVar;

  private final PIDCAttrValueEditDialog pidcAttrValDia;

  private PidcVersionBO pidcVersionBO;

  private long newVariantID;

  private boolean fromVarNameAssgnmtDialog;

  private Set<String> existingValues = new HashSet<>();


  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param apicObject instance
   * @param viewer instance
   * @param pidcPage instance
   * @param isPasteAction defines whether it from paste action or not
   * @param pidcAttrValueEditDialog PIDCAttrValueEditDialog
   */
  // ICDM-108
  // ICDM-94 - GridTableViewer is changed to ColumnViewer
  // ICDM-150 - Added a new isPasteAction flag
  public AddValueDialog(final Shell parentShell, final IModel apicObject, final ColumnViewer viewer,
      final PIDCAttrPage pidcPage, final boolean isPasteAction, final PIDCAttrValueEditDialog pidcAttrValueEditDialog,
      final Set<String> set) {
    // ICDM-2580
    super(parentShell, apicObject, pidcAttrValueEditDialog, false);
    this.apicObject = apicObject;
    this.viewer = viewer;
    this.pidcPage = pidcPage;
    this.isPasteAction = isPasteAction;
    this.newAttrValue = null;
    this.pidcAttrValDia = pidcAttrValueEditDialog;
    if (null != pidcPage) {
      this.pidcVersionBO = pidcPage.getPidcVersionBO();
    }
    else if (null != pidcAttrValueEditDialog) {
      this.pidcVersionBO = pidcAttrValueEditDialog.getPidcVersionBO();
    }
    this.existingValues = new HashSet<>(set);
  }

  /**
   * The Parameterized Constructor
   *
   * @param parentShell instance
   * @param apicObject instance
   * @param viewer instance
   * @param pidcPage instance
   * @param isPasteAction defines whether it from paste action or not
   * @param pidcAttrValueEditDialog PIDCAttrValueEditDialog
   */
  // ICDM-108
  // ICDM-94 - GridTableViewer is changed to ColumnViewer
  // ICDM-150 - Added a new isPasteAction flag
  public AddValueDialog(final Shell parentShell, final IModel apicObject, final ColumnViewer viewer,
      final PidcVersionBO pidcVersionBo, final Set<String> set) {
    // ICDM-2580
    super(parentShell, apicObject, null, false);
    this.apicObject = apicObject;
    this.viewer = viewer;
    this.pidcVersionBO = pidcVersionBo;
    this.pidcPage = null;
    this.isPasteAction = false;
    this.newAttrValue = null;
    this.pidcAttrValDia = null;
    this.existingValues = new HashSet<>(set);
  }


  /**
   * @param parentShell instance
   * @param apicObject instance
   * @param viewer instance
   * @param isMulVariant instance
   * @param isMulSubVar instance
   * @param pidcAttrValueEditDialog pidcAttrValueEditDialog
   * @param isPasteAction defines whether it from paste action or not
   * @param set
   */
  public AddValueDialog(final Shell parentShell, final IModel apicObject, final ColumnViewer viewer,
      final boolean isMulVariant, final boolean isMulSubVar, final PIDCAttrValueEditDialog pidcAttrValueEditDialog,
      final boolean isPasteAction, final Set<String> set) {
    // ICDM-2580
    super(parentShell, apicObject, pidcAttrValueEditDialog, false);
    this.apicObject = apicObject;
    this.viewer = viewer;
    this.pidcPage = pidcAttrValueEditDialog.getPidcPage();
    this.pidcAttrValDia = pidcAttrValueEditDialog;
    this.isPasteAction = isPasteAction;
    this.newAttrValue = null;
    this.isMulSubVar = isMulSubVar;
    this.isMulVar = isMulVariant;
    this.existingValues = new HashSet<>(set);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // ICDM-94
    final String shellTitle = getShellTitle();
    newShell.setText(shellTitle);
    super.configureShell(newShell);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveBtn.setEnabled(false);
    this.cancelBtn = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {

    if ((this.pidcPage == null) && (getAttribute().getLevel() != ApicConstants.SUB_VARIANT_CODE_ATTR) &&
        (getAttribute().getLevel() != ApicConstants.VARIANT_CODE_ATTR) && (this.viewer instanceof TreeViewer)) {
      setIncludeVersionSection(true);
    }

    final Control contents = super.createContents(parent);
    // ICDM-94
    final String title = getTitle();
    // Set the title
    setTitle(title);

    final String dialogMessage = getDialogMessage();
    // Set the message
    setMessage(dialogMessage, IMessageProvider.INFORMATION);


    prePopulateValueNames(getValueEngText(), getValueGerText(), getDescripEng());
    return contents;
  }

  /**
   * @param valEngText eng text
   * @param valGerText german text
   * @param descEngl eng description
   */
  public void prePopulateValueNames(final String valEngText, final String valGerText, final String descEngl) {
    if (this.fromVarNameAssgnmtDialog) {
      this.valueEngText.setText(null == valEngText ? "" : valEngText);
      this.valueGerText.setText(null == valGerText ? "" : valGerText);
      this.valueDescEngText.setText(null == descEngl ? "" : descEngl);
    }
  }


  /**
   * This method returns dialog title
   *
   * @return String
   */
  // ICDM-94
  private String getTitle() {
    String title;
    if ((this.pidcPage == null) && (this.viewer instanceof TreeViewer)) {
      title = getTitleForPIDC();
    }
    else {
      title = ApicUiConstants.ADD_VAL;
    }
    return title;
  }

  /**
   * This method returns title for pidc/variant/sub-variant
   *
   * @return String
   */
  private String getTitleForPIDC() {
    String title;
    if (this.isPasteAction) {
      title = ApicUiConstants.PASTE_AS_NEW;
    }
    else {
      // ICDM-121
      if (getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        title = ApicUiConstants.CREATE_SVAR_NEW_NAME;
      }
      else if (getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
        title = ApicUiConstants.CREATE_VAR_NEW_NAME;
      }
      else {
        title = ApicUiConstants.DESC_CREAT_PIDC_NEW_NAME;

      }
    }
    return title;
  }

  /**
   * This method returns shell title
   *
   * @return String
   */
  // ICDM-94
  private String getShellTitle() {
    String title;
    if ((this.pidcPage == null) && (this.viewer instanceof TreeViewer)) {
      title = getShellTitleForPIDC();
    }
    else {
      title = ApicUiConstants.ADD_VAL;
    }
    return title;
  }

  /**
   * This method returns shell title for pidc/variant/sub-variant
   *
   * @return String
   */
  private String getShellTitleForPIDC() {
    String title;
    if (this.isPasteAction) {
      title = ApicUiConstants.PASTE_AS_NEW;
    }
    else {
      // ICDM-121
      if (getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        title = ApicUiConstants.ADD_A_SUB_VARIANT;
      }
      else if (getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
        title = ApicUiConstants.ADD_A_VARIANT;
      }
      else {
        title = ApicUiConstants.ADD_A_PIDC;
      }
    }
    return title;
  }

  /**
   * This method returns dialog message
   *
   * @return String
   */
  // ICDM-94
  private String getDialogMessage() {
    String message;
    if ((this.pidcPage == null) && (this.viewer instanceof TreeViewer)) {
      message = getDialogMessageForPIDC();
    }
    else {
      message = ApicUiConstants.DESC_ADD_NEW_ATTR_VAL;
    }
    return message;
  }

  /**
   * @return
   */
  private String getDialogMessageForPIDC() {
    String message;
    if (this.isPasteAction) {
      message = ApicUiConstants.DESC_PASTE_NEW_VALUE;
    }
    else {
      // ICDM-121
      if (getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
        message = ApicUiConstants.DESC_CRETE_SVAR_NEW_NAME;
      }
      else if (getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
        message = ApicUiConstants.DESC_CREATE_VAR_NEW_NAME;
      }
      else {
        message = ApicUiConstants.DESC_CREATE_PID_NEW_NAME;
      }

    }
    return message;
  }

  /**
   * This method creates a new record in TABV_ATTRIBUTE_VALUES table
   *
   * @param attrVal defines attribute value
   * @param attrValGer defines attribute value in German
   * @param descGerm attribute value description in German
   * @param descEnglish defines attribute value description in English
   * @param attrCharValUpd
   * @param chtCmt
   * @param attr
   * @param apicDataProvider instance
   */
  private void addAttrValToCmdStack(final String attrVal, final String attrValGer, final String descEnglish,
      final String chtCmt, final Attribute attr) {

    try {
      AttributeValue valueObj = new AttributeValue();
      Attribute attribute = attr;
      AttributeClientBO attrBO = new AttributeClientBO(attribute);
      AttributeValueServiceClient client = new AttributeValueServiceClient();
      String attrValType;

      // get value type from attribute for setting values appropriatly based on type
      attrValType = attribute.getValueType();
      // set the TabvAttribute
      valueObj.setAttributeId(attribute.getId());


      // set both ENGLISH & GERMAN values for TEXT & HYPERLINK
      if ((attrValType.equals(AttributeValueType.TEXT.toString())) ||
          (attrValType.equals(AttributeValueType.HYPERLINK.toString()))) {
        valueObj.setTextValueEng(attrVal);

        valueObj.setTextValueGer(attrValGer);
      }
      else if (attrValType.equals(AttributeValueType.NUMBER.toString())) {
        valueObj.setNumValue(new BigDecimal(attrVal));
      }
      else if (attrValType.equals(AttributeValueType.DATE.toString())) {
        valueObj.setDateValue(AttributeCommon.convertAttrDateStringToDefaultDateFormat(attribute.getFormat(), attrVal));
      }
      else if (attrValType.equals(AttributeValueType.BOOLEAN.toString())) {
        valueObj.setBoolvalue(attrVal);
      }


      // set ENGLISH description
      valueObj.setDescriptionEng(descEnglish);
      // set GERMAN description
      valueObj.setDescriptionGer(this.descGer);
      valueObj.setChangeComment(chtCmt);
      // new attribute, deleted flag is NO
      valueObj.setDeleted(false);
      // Icdm-830 Data Model changes for New Column Clearing status


      // Creation of a value from the Apic Write User or Write access

      com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS newClearingStatus =
          com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS.CLEARED;
      if (attrBO.canModifyValues() || !attrBO.isNormalized()) {
        newClearingStatus = com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS.CLEARED;
      }
      // Creation of a value from Ordinary User
      else if (attrBO.isNormalized()) {
        newClearingStatus = com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO.CLEARING_STATUS.NOT_CLEARED;
      }

      valueObj.setCharacteristicValueId(null);
      valueObj.setClearingStatus(newClearingStatus.getDBText());

      this.newAttrValue = client.create(valueObj);

      if (null != this.viewer) {
        this.viewer.refresh();
      }

    }
    catch (ParseException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * This method set new PIDC info to command. This method creates a new record in TABV_ATTRIBUTE_VALUES table
   *
   * @param apicDataProvider instance
   */
  private void addPIDCToCmdStack() {}


  private boolean checkForDuplicates() {
    for (String stringValue : this.existingValues) {
      if (this.attrValue.equals(stringValue)) {
        return true;
      }
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    onOkPressed();
    boolean hasDuplicates = checkForDuplicates();

    if (hasDuplicates) {
      CDMLogger.getInstance().warnDialog(ApicConstants.DUPLICATE_ATTR_VALUE, Activator.PLUGIN_ID);
    }

    if (this.attrValue.equals(IUtilityConstants.EMPTY_STRING) && this.descEng.equals(IUtilityConstants.EMPTY_STRING)) {
      CDMLogger.getInstance().warnDialog("Value/Description should not be empty!", Activator.PLUGIN_ID);
    }


    if (CommonUiUtils.getInstance().validateVariantName(getAttribute().getLevel().intValue(), this.attrValue,
        this.attrValueGer) && !hasDuplicates) {

      // ICDM-1500
      if ((getAttribute().getValueType().equals(AttributeValueType.HYPERLINK.toString())) &&
          CommonUtils.isValidURLFormat(this.attrValue.trim())) {
        this.attrValue = CommonUtils.formatUrl(this.attrValue.trim());
      }
      // iCDM-400,Validate hyperlink
      if (getAttribute().getValueType().equals(AttributeValueType.HYPERLINK.toString()) &&
          !CommonUtils.isValidHyperlinkFormat(this.attrValue)) {

        CDMLogger.getInstance().warnDialog("Valid hyperlink value to be provided!", Activator.PLUGIN_ID);
      }
      else {

        // Define new attribute value
        if (checkVarSubvarValidity() ||
            ((null != this.pidcAttrValDia) && (this.pidcAttrValDia.getColumnDataMapper() != null))) {
          // ICdm-955 new field char Value
          setAttrCharVal();
          addAttrValToCmdStack(this.attrValue, this.attrValueGer, this.descEng, this.changeComment, getAttribute());
        }
        // Define new value for PIDC normalized attribute
        // ICDM-108
        else if ((this.pidcPage != null) || this.isMulSubVar || this.isMulVar) {
          // ICDM-260
          // case 1: If for an attribute the part number or speclink is not allowed ,then also the value should be set
          // directly to pidc page
          if ((this.apicObject instanceof IProjectAttribute) && (this.pidcPage != null)) {
            Attribute attr = this.pidcPage.getPidcDataHandler().getAttributeMap()
                .get(((IProjectAttribute) this.apicObject).getAttrId());
            if (!(this.isMulSubVar || this.isMulVar) && (!attr.isWithPartNumber() && !(attr.isWithSpecLink()))) {
              CharacteristicValue attrCharValUpd = setAttrCharVal();
              addPIDCAttrValToCmdStack(this.attrValue, this.attrValueGer, this.descGer, this.descEng, attrCharValUpd,
                  this.changeComment);
            }

            else {
              // case 2: Either select from existing values in PIDCAttrValueEditDialog or add new value from
              // AddValueDialog.
              // Here if new value is added the value should be saved , but the control should return back to
              // PIDCAttrValueEditDialog
              setAttrCharVal();
              addAttrValToCmdStack(this.attrValue, this.attrValueGer, this.descEng, this.changeComment, attr);
            }
          }
        }
        // ICDM-94 Define new PIDC/Variant/Sub-Variant
        else if (this.viewer instanceof TreeViewer) {
          // ICDM-150 To paste as new PIDC/Variant/Sub-Variant
          if (this.isPasteAction) {
            setPasteActionCommand();
          }
          else {
            addNewPIDCActionCommand();
          }
        }
        else if ((null == this.pidcAttrValDia)) {
          // ICDM-150 To paste as new PIDC/Variant/Sub-Variant

          addNewPIDCActionCommand();

        }
        super.okPressed();
      }
    }
  }

  /**
   * @return
   */
  private boolean checkVarSubvarValidity() {
    return (this.pidcPage == null) && (this.viewer instanceof GridTableViewer) && (!this.isMulSubVar) &&
        (!this.isMulVar);
  }


  /**
   * @param attrCharVal
   * @return
   */
  private CharacteristicValue setAttrCharVal() {
    CharacteristicValue attrCharVal = null;
    if (CommonUtils.isNotNull(this.comboCharVal)) {
      String charValStr = this.comboCharVal.getItem(this.comboCharVal.getSelectionIndex());
      if (this.attrCharValues != null) {
        attrCharVal = setUpdatedCharVal(charValStr);
      }
    }
    return attrCharVal;
  }


  /**
   * This method is responsible to add the pidc/variant/sub-variant command
   *
   * @param apicDataProvider
   */
  private void addNewPIDCActionCommand() {
    // ICDM-121
    if (getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
      addSubVariantToCommandStack();
    }
    else if (getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
      addVariantToCommandStack();
    }
    else if (getAttribute().getLevel() == ApicConstants.PROJECT_NAME_ATTR) {
      addPIDCToCmdStack();
    }
  }

  /**
   * This method creates a new record in TABV_ATTRIBUTE_VALUES table. It will adds a new value to PIDC attrute or
   * variant
   *
   * @param attrVal defines attribute value
   * @param attrValGer defines attribute value in German
   * @param descGerm attribute value description in German
   * @param descEnglish defines attribute value description in English
   * @param attrCharValUpd
   * @param chtCmt
   * @param apicDataProvider instance
   */
  private void addPIDCAttrValToCmdStack(final String attrVal, final String attrValGer, final String descGerm,
      final String descEnglish, final CharacteristicValue attrCharValUpd, final String chtCmt) {

    AttributeValue val = setNewAttrValInfoToCmd(attrVal, attrValGer, descGerm, descEnglish, attrCharValUpd, chtCmt);
    val.setAttributeId(getAttribute().getId());
    AttributeValueServiceClient valClient = new AttributeValueServiceClient();
    AttributeValue create = null;
    try {
      create = valClient.create(val);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    PIDCPageEditUtil pageEditUtil;
    if (this.pidcPage == null) {
      pageEditUtil = new PIDCPageEditUtil(this.pidcAttrValDia.getPidcVariantValueDialog(), this.pidcVersionBO);
    }
    else {
      pageEditUtil = new PIDCPageEditUtil(this.pidcPage.getProjectObjectBO());
    }
    // Add a new value to selected variant attribute
    if (((this.pidcPage != null) && this.pidcPage.isVaraintNodeSelected()) || this.isMulVar) {
      pageEditUtil.editValue(null, this.apicObject, "", "", "");
    } // ICDM-122
    else if (((this.pidcPage != null) && this.pidcPage.isSubVaraintNodeSelected()) || this.isMulSubVar) {
      pageEditUtil.editValue(null, this.apicObject, "", "", "");
    }
    else {
      // Add a new value to selected pidc attribute
      pageEditUtil.editPIDCAttrValue(create, (IProjectAttribute) this.apicObject, "", "", "");
    }
    this.newAttrValue = create;
  }


  /**
   * This method sets all the paste action commands
   *
   * @param apicDataProvider
   */
  // ICDM-150
  private void setPasteActionCommand() {
    // Get source of copied PIDC
    final Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();


    if (getAttribute().getLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
      addPasteSubVarToCommandStack();
    }
    else if (getAttribute().getLevel() == ApicConstants.VARIANT_CODE_ATTR) {
      addPasteVariantToCommandStack(copiedObject);
    }
    else if (getAttribute().getLevel() == ApicConstants.PROJECT_NAME_ATTR) {
      addPastePIDCToCommandStack();
    }

  }

  /**
   * This method set new Sub-Variant info to command. This method creates a new record in TABV_ATTRIBUTE_VALUES table
   *
   * @param apicDataProvider
   */
  // ICDM-121
  private void addSubVariantToCommandStack() {
    // Get target PIDC variant tree node to create a Sub-Variant final
    IStructuredSelection selection = null;
    if (null != this.viewer) {
      selection = (IStructuredSelection) this.viewer.getSelection();
    }
    if ((selection != null) && ((selection.getFirstElement() instanceof PidcVariant) ||
        ((selection.getFirstElement() instanceof PIDCDetailsNode) &&
            ((PIDCDetailsNode) selection.getFirstElement()).isVariantNode()))) {
      PidcVariant selPidcVar = null;
      if (selection.getFirstElement() instanceof PidcVariant) {
        selPidcVar = (PidcVariant) selection.getFirstElement();
      }
      else if (selection.getFirstElement() instanceof PIDCDetailsNode) {
        PIDCDetailsNode pidcDetailsNode = (PIDCDetailsNode) selection.getFirstElement();
        selPidcVar = pidcDetailsNode.getPidcVariant();
      }
      if (selPidcVar != null) {

        // Get selected pidc name from table viewer
        boolean usedSubVariant;

        // iCDM-1155
        // get all variants of selected pid card
        PidcVariantBO pidcVarHandler =
            new PidcVariantBO(this.pidcVersionBO.getPidcVersion(), selPidcVar, this.pidcVersionBO.getPidcDataHandler());
        SortedSet<IProjectObject> subVariantsSet = new TreeSet<>(pidcVarHandler.getSubVariantsSet(true));

        // check if varaint is used
        usedSubVariant = checkifVarNameIsAvailable(subVariantsSet);
        if (usedSubVariant) {
          MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Invalid Value Info:",
              "This variant name already exists !");
        }
        else {
          callSubVariantService(selPidcVar);
        }
      }


    }

  }

  /**
   * This method is responsible to add the information and to execute service call for sub variant creation
   *
   * @param selPidcVar
   * @param valuesMap
   */
  private void callSubVariantService(final PidcVariant selPidcVar) {
    PidcSubVariantServiceClient pidcVarServiceClient = new PidcSubVariantServiceClient();
    PidcSubVariantData pidcVar = new PidcSubVariantData();
    pidcVar.setPidcVariantId(selPidcVar.getId());
    // create Attribute value and set it to pidc variant creation data
    AttributeValue varNameAttrValue = new AttributeValue();
    varNameAttrValue.setTextValueEng(this.attrValue);
    varNameAttrValue.setTextValueGer(this.attrValueGer);
    varNameAttrValue.setDescriptionEng(this.descEng);
    varNameAttrValue.setDescriptionGer(this.descGer);
    varNameAttrValue.setClearingStatus("Y");


    ApicDataBO apicDataBO = new ApicDataBO();
    com.bosch.caltool.icdm.model.apic.attr.Attribute varNameAttr =
        apicDataBO.getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.SUB_VARIANT_CODE_ATTR));
    varNameAttrValue.setAttributeId(varNameAttr.getId());
    pidcVar.setSubvarNameAttrValue(varNameAttrValue);

    try {
      pidcVarServiceClient.create(pidcVar);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }


  /**
   * This method is responsible to add the information and to execute service call for variant creation
   *
   * @param valuesMap
   */
  private void callVariantService(final Map<Attribute, AttributeValue> valuesMap) {
    PidcVariantServiceClient pidcVarServiceClient = new PidcVariantServiceClient();
    PidcVariantData pidcVar = new PidcVariantData();
    pidcVar.setPidcVersion(this.pidcVersionBO.getPidcVersion());
    // create Attribute value and set it to pidc variant creation data
    AttributeValue varNameAttrValue = new AttributeValue();
    varNameAttrValue.setTextValueEng(this.attrValue);
    varNameAttrValue.setTextValueGer(this.attrValueGer);
    varNameAttrValue.setDescriptionEng(this.descEng);
    varNameAttrValue.setDescriptionGer(this.descGer);
    varNameAttrValue.setClearingStatus("Y");


    ApicDataBO apicDataBO = new ApicDataBO();
    com.bosch.caltool.icdm.model.apic.attr.Attribute varNameAttr =
        apicDataBO.getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.VARIANT_CODE_ATTR));
    varNameAttrValue.setAttributeId(varNameAttr.getId());
    pidcVar.setVarNameAttrValue(varNameAttrValue);
    if (valuesMap != null) {
      Map<Long, Long> structAttrValueMap = getAttrValueMapWithId(valuesMap);
      pidcVar.setStructAttrValueMap(structAttrValueMap);
    }


    try {
      PidcVariantData newVar = pidcVarServiceClient.create(pidcVar);
      this.newVariantID = newVar.getDestPidcVar().getId();
    }
    catch (ApicWebServiceException exp) {
      if (exp.getMessage().contains("CONSTRAINT_VIOLATION")) {
        CDMLogger.getInstance().errorDialog("PIDC Variant insert failed. Record already exists.", Activator.PLUGIN_ID);
      }
      else {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param attrValMap2
   * @return
   */
  private Map<Long, Long> getAttrValueMapWithId(final Map<Attribute, AttributeValue> attrValMap2) {
    Map<Long, Long> structAttrValueMap = new HashMap<>();
    for (Entry<Attribute, AttributeValue> attrValueEntry : attrValMap2.entrySet()) {
      if (null != attrValueEntry.getValue()) {
        structAttrValueMap.put(attrValueEntry.getKey().getId(), attrValueEntry.getValue().getId());
      }
    }
    return structAttrValueMap;
  }

  /**
   *
   */
  private void addVariantToCommandStack() {


    IStructuredSelection selection = null;
    if (null != this.viewer) {
      selection = (IStructuredSelection) this.viewer.getSelection();
    }
    PidcVersion selPidcVer = null;
    Map<Attribute, AttributeValue> valuesMap = null;
    if ((selection != null) && (selection.getFirstElement() instanceof PidcVersion)) {
      selPidcVer = (PidcVersion) selection.getFirstElement();
    }
    else if ((selection != null) && (selection.getFirstElement() instanceof PIDCDetailsNode)) {
      PIDCDetailsNode pidcDetailsNode = (PIDCDetailsNode) selection.getFirstElement();
      selPidcVer = pidcDetailsNode.getPidcVersion();
      valuesMap = pidcDetailsNode.getAttrValuesFromNodeTree(this.pidcVersionBO);
    }
    if (selPidcVer != null) {
      // Get selected pidc name from table viewer
      boolean usedVariant;

      // iCDM-1155
      // get all variants of selected pid card
      SortedSet<IProjectObject> variantsSet = new TreeSet<>(this.pidcVersionBO.getVariantsSet(true));

      // check if varaint is used
      usedVariant = checkifVarNameIsAvailable(variantsSet);
      if (usedVariant) {
        MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Invalid Value Info:",
            "This variant name already exists !");
      }
      else {
        callVariantService(valuesMap);
      }
    }

  }


  /**
   * @param variantsSet
   * @return boolean
   */
  private boolean checkifVarNameIsAvailable(final SortedSet<IProjectObject> variantsSet) {
    CommonDataBO commonBo = new CommonDataBO();
    boolean alreadyExists = false;
    // get all variants
    for (IProjectObject variant : variantsSet) {
      if (CommonUtils.isEqual(commonBo.getLanguage(), Language.ENGLISH)) {
        if (variant.getName().equalsIgnoreCase(this.attrValue)) {
          alreadyExists = true;
          break;
        }
      }
      else {
        if (CommonUtils.isNotEmptyString(this.attrValueGer) && variant.getName().equalsIgnoreCase(this.attrValueGer)) {
          alreadyExists = true;
          break;
        }
        if (variant.getName().equalsIgnoreCase(this.attrValue)) {
          alreadyExists = true;
          break;
        }
      }

    }
    return alreadyExists;
  }

  /**
   * This method set paste Sub-variant info to command. This method creates a new record in TABV_ATTRIBUTE_VALUES table
   *
   * @param apicDataProvider
   */
  // ICDM-150
  private void addPasteSubVarToCommandStack() {}


  /**
   * This method set paste variant info to command. This method creates a new record in TABV_ATTRIBUTE_VALUES table
   *
   * @param apicDataProvider
   */
  // ICDM-150
  private void addPasteVariantToCommandStack(final Object copiedObject) {
    // Get source of copied PIDCVariant
    final PidcVariant pidcVarToCopy = (PidcVariant) copiedObject;
    // Get target tree node to paste variant
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    // If the selected target is PIDCard
    if ((selection != null) && (selection.getFirstElement() instanceof PidcVersion)) { // ICDM-222 progress for pasting
                                                                                       // PIDC,variant,Sub Variant
      try {
        ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
        dialog.run(true, true, monitor -> {
          monitor.beginTask("Copying  Variant...", 100);
          monitor.worked(20);
          pasteVariantOnPIDCardTarget(pidcVarToCopy, selection);
          monitor.worked(100);
          monitor.done();
        });
      }
      catch (InvocationTargetException | InterruptedException exp) {
        CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * This method is resposible to paste as new variant on the target pidcard tree node
   *
   * @param apicDataProvider
   * @param pidcVarToCopy
   * @param selection
   */
  // ICDM-150
  private void pasteVariantOnPIDCardTarget(final PidcVariant pidcVarToCopy, final IStructuredSelection selection) {
    // Get selected PIDCard
    final PidcVersion selPidcVer = (PidcVersion) selection.getFirstElement();
    // Create a copy variant service call
    PidcVariantCopyServiceClient pidcVarClient = new PidcVariantCopyServiceClient();
    PidcVariantData pidcVarCreationData = new PidcVariantData();
    pidcVarCreationData.setSrcPidcVar(pidcVarToCopy);
    // create Attribute value and set it to pidc variant creation data
    AttributeValue varNameAttrValue = new AttributeValue();
    varNameAttrValue.setTextValueEng(this.attrValue);
    varNameAttrValue.setTextValueGer(this.attrValueGer);
    varNameAttrValue.setDescriptionEng(this.descEng);
    varNameAttrValue.setDescriptionGer(this.descGer);
    varNameAttrValue.setClearingStatus("Y");
    ApicDataBO apicDataBO = new ApicDataBO();
    com.bosch.caltool.icdm.model.apic.attr.Attribute varNameAttr =
        apicDataBO.getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.VARIANT_CODE_ATTR));
    varNameAttrValue.setAttributeId(varNameAttr.getId());
    pidcVarCreationData.setVarNameAttrValue(varNameAttrValue);
    pidcVarCreationData.setPidcVersion(selPidcVer);

    try {
      pidcVarClient.create(pidcVarCreationData);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * This method adds the copy pidc command to the command stack
   *
   * @param apicDataProvider
   */
  // ICDM-150
  private void addPastePIDCToCommandStack() {}

  /**
   * Sets the attribute value information to the CmdModAttributeValue commad
   *
   * @param attrValue
   * @param attrValueGer
   * @param descGer
   * @param descEng
   * @param attrCharValUpd
   * @param changeComment
   * @param command
   */
  private AttributeValue setNewAttrValInfoToCmd(final String attrValue, final String attrValueGer, final String descGer,
      final String descEng, final CharacteristicValue attrCharValUpd, final String changeComment) {
    AttributeValue val = new AttributeValue();

    // get value type from attribute for setting values appropriatly based on type
    String attrValType = this.attribute.getValueType();
    // set the TabvAttribute
    val.setAttributeId(this.attribute.getId());


    // set both ENGLISH & GERMAN values for TEXT & HYPERLINK
    if ((attrValType.equals(AttributeValueType.TEXT.toString())) ||
        (attrValType.equals(AttributeValueType.HYPERLINK.toString()))) {
      val.setTextValueEng(attrValue);

      val.setTextValueGer(attrValueGer);
    }
    else if (attrValType.equals(AttributeValueType.NUMBER.toString())) {

      val.setNumValue(new BigDecimal(attrValue));
    }
    else if (attrValType.equals(AttributeValueType.DATE.toString())) {
      try {
        val.setDateValue(
            AttributeCommon.convertAttrDateStringToDefaultDateFormat(this.attribute.getFormat(), attrValue));
      }
      catch (ParseException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    else if (attrValType.equals(AttributeValueType.BOOLEAN.toString())) {
      val.setBoolvalue(attrValue);
    }


    val.setDescriptionEng(descEng);
    val.setDescriptionGer(descGer);

    if (attrCharValUpd != null) {
      val.setCharacteristicValueId(attrCharValUpd.getId());
    }
    val.setChangeComment(changeComment);
    return val;
  }


  /**
   * @return the newAttrValue
   */
  public AttributeValue getNewAttrValue() {
    return this.newAttrValue;
  }


  /**
   * @param newAttrValue the newAttrValue to set
   */
  public void setNewAttrValue(final AttributeValue newAttrValue) {
    this.newAttrValue = newAttrValue;
  }


  /**
   * @return the newVariantID
   */
  public long getNewVariantID() {
    return this.newVariantID;
  }


  /**
   * @param b
   */
  public void setFromVarNameAssgnmtDialog(final boolean flag) {
    this.fromVarNameAssgnmtDialog = flag;

  }


}