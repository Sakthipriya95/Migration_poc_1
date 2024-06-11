/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.apic.jpa.bo.AttributeValue.CLEARING_STATUS;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristicValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * CmdModAttributeValue.java
 *
 * @author adn1cob
 */
public class CmdModAttributeValue extends AbstractCommand {

  /**
   * the Attribute Value obj to modify
   */
  private AttributeValue modifyAttrValue;

  /**
   * the DB AttrValue to be modified
   */
  // ICDM 229
  private Long modfyDbAttrValID;

  /**
   * new object for DB AttrValue
   */
  // ICDM 229
  private Long newDbAttrValueID;

  /**
   * obj for deleted AttrValue
   */
  // ICDM 229
  private Long delDbAttrValueID;

  /**
   * Attribute obj of this value belongs to
   */
  private final Attribute attribute;
  /**
   * Old English value for text attributes
   */
  private String oldAttrValEng;
  /**
   * New English value for text attributes
   */
  private String newAttrValEng;

  /**
   * Old German value for text attributes
   */
  private String oldAttrValGer;
  /**
   * New German value for text attributes
   */
  private String newAttrValGer;

  /**
   * Old English description for text attributes
   */
  private String oldAttrValDescEng;
  /**
   * New English description for text attributes
   */
  private String newAttrValDescEng;

  /**
   * Old German description for text attributes
   */
  private String oldAttrValDescGer;
  /**
   * New German description for text attributes
   */
  private String newAttrValDescGer;


  /**
   * old values in case of boolean,number & date value type
   */
  private String oldAttrVal;
  /**
   * new values in case of boolean,number & date value type
   */
  private String newAttrVal;

  // ICDM-2300
  /**
   *
   */
  private String delAttrValMailTemplate;

  /**
   * True if command is invoked for undelete of value
   */
  private boolean unDelete;


  // Icdm-830 Data Model changes for New Column
  /**
   * Old clearing status
   */
  private CLEARING_STATUS oldClearingStatus;

  /**
   * New clearing status
   */
  private CLEARING_STATUS newClearingStatus;


  /**
   * String constant used for displaying error or info
   */
  private static final String STR_WITH_VALUE = "with value: ";

  /**
   * key for entity id
   */
  private static final String VAL_ENTITY_ID = "VAL_ENTITY_ID";
  /**
   * Summary of transactions of this command
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Old Characteristic Value
   */
  private AttributeCharacteristicValue oldAttrCharVal;
  /**
   * New Characteristic Value
   */
  private AttributeCharacteristicValue newAttrCharVal;
  // ICDM-1397
  /**
   * Old change comment
   */
  private String oldChangeComment;
  /**
   * New change comment
   */
  private String newChangeComment;

  /**
   * Constructor to add a new AttributeValue - use this constructor for INSERT
   *
   * @param apicDataProvider - data provider
   * @param attr - the attribute obj
   */
  public CmdModAttributeValue(final ApicDataProvider apicDataProvider, final Attribute attr) {
    super(apicDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;

    // set the attribute, needed to know for which attribute the value belongs to
    this.attribute = attr;
  }

  /**
   * Constructor to delete or update existing AttributeValue
   *
   * @param apicDataProvider - data provider
   * @param modifyAttrValue - Attr obj to be modified
   * @param isDelete - TRUE for DELETE operation, FALSE for UPDATE
   */
  public CmdModAttributeValue(final ApicDataProvider apicDataProvider, final AttributeValue modifyAttrValue,
      final boolean isDelete) {
    super(apicDataProvider);
    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
      // the AttributeValue to be deleted
      this.modifyAttrValue = modifyAttrValue;

      // get the DB entity for the AttributeValue to be modified
      // ICDM-229
      this.delDbAttrValueID = modifyAttrValue.getValueID();
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      // the AttributeValue to be modified
      this.modifyAttrValue = modifyAttrValue;
      // get the DB entity for the AttributeValue to be modified
      // ICDM-229
      this.modfyDbAttrValID = modifyAttrValue.getValueID();
    }
    this.attribute = modifyAttrValue.getAttribute();
    // initialize command with current values from UI
    setAttributeValueFieldsToCommand(modifyAttrValue);
  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException if date is invalid
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    // create a new database entity
    final TabvAttrValue newDbAttrValue = new TabvAttrValue();

    // set the values to this entity
    setNewAttrValueFields(newDbAttrValue);

    // set created user and date fields
    setUserDetails(COMMAND_MODE.INSERT, newDbAttrValue, VAL_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbAttrValue);
    this.newDbAttrValueID = newDbAttrValue.getValueId();
    // Also,add the new attributevalue to the list of all attributes values
    final long dbAttrID = this.attribute.getAttributeID();
    getEntityProvider().getDbAttribute(dbAttrID).getTabvAttrValues().add(newDbAttrValue);
    // ICDM-108
    this.modifyAttrValue = new AttributeValue(getDataProvider(), newDbAttrValue.getValueId());

    // If this a structure attribute, add the new value to the pidc tree structure
    if (this.attribute.getAttrLevel() > 0) {
      getDataLoader().fetchPidcStructure(this.attribute.getAttributeID());
    }

    getChangedData().put(this.newDbAttrValueID,
        new ChangedData(ChangeType.INSERT, this.newDbAttrValueID, TabvAttrValue.class, DisplayEventSource.COMMAND));
  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.modfyDbAttrValID, TabvAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttrValue(this.modfyDbAttrValID).getObjectDetails());

    // Check for any parallel changes
    // ICDM-229
    final TabvAttrValue modifiedAttrVal = getEntityProvider().getDbValue(this.modfyDbAttrValID);
    validateStaleData(modifiedAttrVal);

    // Update modified data
    setNewAttrValueFields(modifiedAttrVal);

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedAttrVal, VAL_ENTITY_ID);

    getChangedData().put(this.modfyDbAttrValID, chdata);
  }


  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.delDbAttrValueID, TabvAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttrValue(this.delDbAttrValueID).getObjectDetails());

    // Check for any parallel changes
    // ICDM-229
    final TabvAttrValue deletedAttrVal = getEntityProvider().getDbValue(this.delDbAttrValueID);
    validateStaleData(deletedAttrVal);
    // set the deleted flag, do not delete the entity
    if (this.unDelete) {
      deletedAttrVal.setDeletedFlag(ApicConstants.CODE_NO);
    }
    else {
      deletedAttrVal.setDeletedFlag(ApicConstants.YES);
    }

    updateClearingStatus(deletedAttrVal);

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, deletedAttrVal, VAL_ENTITY_ID);

    getChangedData().put(this.delDbAttrValueID, chdata);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // ICDM-229
    final TabvAttrValue attrVal = getEntityProvider().getDbValue(this.newDbAttrValueID);
    // Check for any parallel changes
    validateStaleData(attrVal);
    // unregister the attr
    getEntityProvider().deleteEntity(attrVal);
    // remove the value from the list of all values for this attribute
    final long dbAttrID = this.attribute.getAttributeID();
    getEntityProvider().getDbAttribute(dbAttrID).getTabvAttrValues().remove(attrVal);

    // If this a structure attribute, remove the new value to the pidc tree structure
    if (this.attribute.getAttrLevel() > 0) {
      getDataLoader().fetchPidcStructure(this.attribute.getAttributeID());
    }

    getChangedData().put(this.newDbAttrValueID,
        new ChangedData(ChangeType.DELETE, this.newDbAttrValueID, TabvAttrValue.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException if date is invalid
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.modfyDbAttrValID, TabvAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttrValue(this.modfyDbAttrValID).getObjectDetails());

    // Check for any parallel changes
    // ICDM-229
    final TabvAttrValue modifiedAttrVal = getEntityProvider().getDbValue(this.modfyDbAttrValID);
    validateStaleData(modifiedAttrVal);
    // Update modified data
    try {
      setOldAttrValueFields(modifiedAttrVal);
    }
    catch (ParseException e) {
      throw new CommandException(CommandErrorCodes.PRSING_INVLD_DATE, e);
    }
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedAttrVal, VAL_ENTITY_ID);

    getChangedData().put(this.modfyDbAttrValID, chdata);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {

    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.delDbAttrValueID, TabvAttrValue.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttrValue(this.delDbAttrValueID).getObjectDetails());

    // Check for any parallel changes
    // ICDM-229
    final TabvAttrValue deletedAttrVal = getEntityProvider().getDbValue(this.delDbAttrValueID);
    validateStaleData(deletedAttrVal);
    /* UNDO delete for this AttrValue */
    // set the deleted flag to NO, if this is delete, else set as YES
    if (this.unDelete) {
      deletedAttrVal.setDeletedFlag(ApicConstants.YES);
    }
    else {
      deletedAttrVal.setDeletedFlag(ApicConstants.CODE_NO);
    }
    updateClearingStatus(deletedAttrVal);
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, deletedAttrVal, VAL_ENTITY_ID);

    getChangedData().put(this.delDbAttrValueID, chdata);
  }

  /**
   * Icdm-1180 - update the Clearing Status during the Deletion
   *
   * @param deletedAttrVal
   * @param unDelFlag
   */
  private void updateClearingStatus(final TabvAttrValue deletedAttrVal) {
    switch (CLEARING_STATUS.getClearingStatus(deletedAttrVal.getClearingStatus())) {
      case CLEARED:
        deletedAttrVal.setClearingStatus(CLEARING_STATUS.DELETED.getDBText());

        break;
      case IN_CLEARING:
      case NOT_CLEARED:
        deletedAttrVal.setClearingStatus(CLEARING_STATUS.REJECTED.getDBText());

        break;
      case REJECTED:
        deletedAttrVal.setClearingStatus(CLEARING_STATUS.NOT_CLEARED.getDBText());

        break;
      case DELETED:
        deletedAttrVal.setClearingStatus(CLEARING_STATUS.CLEARED.getDBText());
        break;
      default:
        break;
    }

  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param modifyAttrValue
   */
  private void setAttributeValueFieldsToCommand(final AttributeValue modifyAttrValue) {

    if ((modifyAttrValue.getAttribute().getValueType() == AttributeValueType.TEXT) ||
        (modifyAttrValue.getAttribute().getValueType() == AttributeValueType.HYPERLINK)) {
      this.oldAttrValEng = modifyAttrValue.getTextValueEng();
      this.newAttrValEng = this.oldAttrValEng;

      this.oldAttrValGer = modifyAttrValue.getTextValueGer();
      this.newAttrValGer = this.oldAttrValGer;
    }
    else {
      // setting the old new values in case of boolean,date,number value types
      this.oldAttrVal = modifyAttrValue.getValue();
      this.newAttrVal = this.oldAttrVal;
    }
    // ENGLISH Desc
    this.oldAttrValDescEng = modifyAttrValue.getValueDescEng();
    this.newAttrValDescEng = this.oldAttrValDescEng;

    this.oldChangeComment = modifyAttrValue.getChangeComment();
    this.newChangeComment = this.oldChangeComment;
    // GERMAN Desc
    this.oldAttrValDescGer = modifyAttrValue.getValueDescGer();
    this.newAttrValDescGer = this.oldAttrValDescGer;

    // New Change for the Clering status
    this.oldClearingStatus = modifyAttrValue.getClearingStatus();
    this.newClearingStatus = this.oldClearingStatus;

    this.oldAttrCharVal = modifyAttrValue.getCharacteristicValue();
    this.newAttrCharVal = this.oldAttrCharVal;

  }

  /**
   * Method sets the required fields of the TabvAttrValue entity. Used for INSERT and UPDATE operations
   *
   * @param dbAttrValue
   * @throws CommandException
   */
  private void setNewAttrValueFields(final TabvAttrValue dbAttrValue) throws CommandException {
    TabvAttribute tabvAttr;
    AttributeValueType attrValType;
    // Set Attribute fields
    tabvAttr = getEntityProvider().getDbAttribute(this.attribute.getAttributeID());
    // get value type from attribute for setting values appropriatly based on type
    attrValType = this.attribute.getValueType();
    // set the TabvAttribute
    dbAttrValue.setTabvAttribute(tabvAttr);

    // Set VALUE and DESC - both languages

    setNewValue(dbAttrValue, attrValType);

    // set ENGLISH description
    dbAttrValue.setValueDescEng(this.newAttrValDescEng);
    // set GERMAN description
    dbAttrValue.setValueDescGer(this.newAttrValDescGer);
    dbAttrValue.setChangeComment(this.newChangeComment);
    // new attribute, deleted flag is NO
    dbAttrValue.setDeletedFlag(ApicConstants.CODE_NO);
    // Icdm-830 Data Model changes for New Column Clearing status


    // Creation of a value from the Apic Write User or Write access
    if (this.newClearingStatus == null) {
      if (this.attribute.canModifyValues() || !this.attribute.isNormalized()) {
        this.newClearingStatus = CLEARING_STATUS.CLEARED;

      }
      // Creation of a value from Ordinary User
      else if (this.attribute.isNormalized()) {
        this.newClearingStatus = CLEARING_STATUS.NOT_CLEARED;

      }
    }
    dbAttrValue.settCharacteristicValue(null);
    if (this.newAttrCharVal != null) {
      TCharacteristicValue dbCharacteristicValue =
          getEntityProvider().getDbCharacteristicValue(this.newAttrCharVal.getID());
      dbAttrValue.settCharacteristicValue(dbCharacteristicValue);
    }
    dbAttrValue.setClearingStatus(this.newClearingStatus.getDBText());
  }

  /**
   * @param dbAttrValue
   * @param attrValType
   * @throws CommandException
   */
  private void setNewValue(final TabvAttrValue dbAttrValue, final AttributeValueType attrValType)
      throws CommandException {
    // set both ENGLISH & GERMAN values for TEXT & HYPERLINK
    if ((attrValType == AttributeValueType.TEXT) || (attrValType == AttributeValueType.HYPERLINK)) {
      dbAttrValue.setTextvalueEng(this.newAttrValEng);
      dbAttrValue.setTextvalueGer(this.newAttrValGer);
    }
    else if (attrValType == AttributeValueType.NUMBER) {
      dbAttrValue.setNumvalue(new BigDecimal(this.newAttrVal));
    }
    else if (attrValType == AttributeValueType.DATE) {
      try {
        dbAttrValue.setDatevalue(
            DateFormat.convertStringToTimestamp(dbAttrValue.getTabvAttribute().getFormat(), this.newAttrVal));
      }
      catch (ParseException e) {
        throw new CommandException(CommandErrorCodes.PRSING_INVLD_DATE, e);
      }
    }
    else if (attrValType == AttributeValueType.BOOLEAN) {
      dbAttrValue.setBoolvalue(getDbBoolValue(this.newAttrVal));
    }

  }

  /**
   * Method sets the required fields of the TabvAttrValue entity. Used for UNDO operation
   *
   * @param dbAttrValue
   * @throws ParseException
   */
  private void setOldAttrValueFields(final TabvAttrValue dbAttrValue) throws ParseException {
    // get value type from attribute for setting values appropriatly based on type
    final AttributeValueType attrValType = this.attribute.getValueType();
    // Set VALUE and DESC - both languages
    // set both ENGLISH & GERMAN values for TEXT
    if (this.attribute.getValueType() == AttributeValueType.TEXT) {
      setOldEnglishValue(dbAttrValue, attrValType);
      setOldGermanValue(dbAttrValue, attrValType);
    }
    else { // other types not specific to LANG, set to ENG default.
      setOldEnglishValue(dbAttrValue, attrValType);
    }
    // set ENGLISH description
    dbAttrValue.setValueDescEng(this.oldAttrValDescEng);
    // set GERMAN description
    dbAttrValue.setValueDescGer(this.oldAttrValDescGer);
    dbAttrValue.setChangeComment(this.oldChangeComment);
    // deleted flag is NO, for update
    dbAttrValue.setDeletedFlag(ApicConstants.CODE_NO);
    if (this.oldAttrCharVal == null) {
      dbAttrValue.settCharacteristicValue(null);
    }
    else {
      TCharacteristicValue dbCharacteristicValue =
          getEntityProvider().getDbCharacteristicValue(this.oldAttrCharVal.getID());
      dbAttrValue.settCharacteristicValue(dbCharacteristicValue);
    }

    dbAttrValue.setClearingStatus(this.oldClearingStatus.getDBText());
  }

  /**
   * Sets GERMAN values
   *
   * @param dbAttrValue
   * @param attrValType
   * @throws ParseException
   */
  private void setOldGermanValue(final TabvAttrValue dbAttrValue, final AttributeValueType attrValType)
      throws ParseException {
    if (attrValType == AttributeValueType.TEXT) {
      dbAttrValue.setTextvalueGer(this.oldAttrValGer);
    }
    else if (attrValType == AttributeValueType.NUMBER) {
      dbAttrValue.setNumvalue(new BigDecimal(this.oldAttrValGer));
    }
    else if (attrValType == AttributeValueType.DATE) {
      dbAttrValue.setDatevalue(
          DateFormat.convertStringToTimestamp(dbAttrValue.getTabvAttribute().getFormat(), this.oldAttrValGer));
    }
    else if (attrValType == AttributeValueType.BOOLEAN) {
      dbAttrValue.setBoolvalue(getDbBoolValue(this.oldAttrValGer));
    }
  }

  /**
   * Set Old ENGLISH values
   *
   * @param dbAttrValue
   * @param attrValType
   * @throws ParseException
   */
  private void setOldEnglishValue(final TabvAttrValue dbAttrValue, final AttributeValueType attrValType)
      throws ParseException {
    if ((attrValType == AttributeValueType.TEXT) || (attrValType == AttributeValueType.HYPERLINK)) {
      dbAttrValue.setTextvalueEng(this.oldAttrValEng);
    }
    else if (attrValType == AttributeValueType.NUMBER) {
      dbAttrValue.setNumvalue(new BigDecimal(this.oldAttrValEng));
    }
    else if (attrValType == AttributeValueType.DATE) {
      dbAttrValue.setDatevalue(
          DateFormat.convertStringToTimestamp(dbAttrValue.getTabvAttribute().getFormat(), this.oldAttrValEng));
    }
    else if (attrValType == AttributeValueType.BOOLEAN) {
      dbAttrValue.setBoolvalue(getDbBoolValue(this.oldAttrValEng));
    }
  }

  /**
   * Set the DB Boolean value
   *
   * @param uiBoolVal uiBoolVal
   */
  private String getDbBoolValue(final String uiBoolVal) {
    if (ApicConstants.BOOLEAN_TRUE_DB_STRING.equalsIgnoreCase(uiBoolVal) ||
        ApicConstants.BOOLEAN_TRUE_STRING.equalsIgnoreCase(uiBoolVal)) {
      return ApicConstants.BOOLEAN_TRUE_DB_STRING;
    }
    return ApicConstants.BOOLEAN_FALSE_DB_STRING;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldAttrValEng, this.newAttrValEng) ||
        isStringChanged(this.oldAttrValGer, this.newAttrValGer) ||
        isStringChanged(this.oldChangeComment, this.newChangeComment) ||
        isStringChanged(this.oldAttrValDescEng, this.newAttrValDescEng) ||
        isStringChanged(this.oldAttrValDescGer, this.newAttrValDescGer) ||
        (this.oldClearingStatus != this.newClearingStatus) ||
        !CommonUtils.isEqual(this.oldAttrCharVal, this.newAttrCharVal);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    // The following call will build the objectIdentifier based on command mode
    final String objectIdentifier = getPrimaryObjectIdentifier();
    // UnDo delete is handled
    if (this.commandMode == COMMAND_MODE.DELETE) {
      final StringBuilder commandModeText = new StringBuilder(this.commandMode.toString());
      if (this.unDelete) {
        commandModeText.insert(0, "UN-");
      }
      final StringBuilder message = new StringBuilder();
      if (getErrorCause() == ERROR_CAUSE.NONE) {
        message.append(commandModeText).append(' ').append(this.getClass().getSimpleName()).append(' ')
            .append(objectIdentifier);
      }
      else {
        // the statement failed
        message.append(this.getClass().getSimpleName()).append(' ').append(commandModeText).append(" failed (")
            .append(objectIdentifier).append(").");
        if (getErrorCause() == ERROR_CAUSE.COMMAND_EXCEPTION) {
          message.append(" Cause: ").append(getErrorMessage());
        }
      }
      return message.toString();

    }
    return super.getString("", STR_WITH_VALUE + getPrimaryObjectIdentifier());
  }

  /**
   * @param newAttrValEng the newAttrValEng to set
   */
  public void setNewAttrValEng(final String newAttrValEng) {
    if ((this.attribute.getValueType() == AttributeValueType.TEXT) ||
        (this.attribute.getValueType() == AttributeValueType.HYPERLINK)) {
      this.newAttrValEng = newAttrValEng;
    }
    else {
      this.newAttrVal = newAttrValEng;
    }
  }

  /**
   * @param newAttrValGer the newAttrValGer to set
   */
  public void setNewAttrValGer(final String newAttrValGer) {
    // do not allow empty spaces, save as null

    if ((newAttrValGer == null) || newAttrValGer.trim().isEmpty()) {
      this.newAttrValGer = "";
    }
    else {
      this.newAttrValGer = newAttrValGer;
    }

  }

  /**
   * @param newAttrValDescEng the newAttrValDescEng to set
   */
  public void setNewAttrValDescEng(final String newAttrValDescEng) {
    this.newAttrValDescEng = newAttrValDescEng;
  }

  /**
   * Set the change comment
   *
   * @param changeCmt comment
   */
  public void setChangeComment(final String changeCmt) {
    this.newChangeComment = changeCmt;
  }

  /**
   * @param newAttrValDescGer the newAttrValDescGer to set
   */
  public void setNewAttrValDescGer(final String newAttrValDescGer) {
    // do not allow empty spaces, save as null
    if ((newAttrValDescGer == null) || newAttrValDescGer.trim().isEmpty()) {
      this.newAttrValDescGer = "";
    }
    else {
      this.newAttrValDescGer = newAttrValDescGer;
    }

  }

  /**
   * @return AttributeValue instance
   */
  // ICDM-108
  public AttributeValue getAttrValue() {
    return this.modifyAttrValue;
  }

  /**
   * For delete mode, set whether the command is to be used for deleting or undeleting. Undeleting is applicable for
   * project name values
   *
   * @param unDelete the unDelete to set
   */
  public void setUnDelete(final boolean unDelete) {
    this.unDelete = unDelete;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // icdm-834
    // clearance check is made inside this method
    sendMailNotification();
    // icdm-890
    // Notify the owners of PIDC if attribute value is deleted and used in any pidcs
    notifyPidcOwners();
  }

  /**
   * iCDM-889 <br>
   * Outlook mail notification, when Attr Value in NOT_CLEARED or IN_CLEARING status is DELETED
   */
  public void notifyUnclearedAttrValDel() {
    if ((getErrorCause() == ERROR_CAUSE.NONE) && (this.commandMode == COMMAND_MODE.DELETE) &&
        ((this.oldClearingStatus == CLEARING_STATUS.NOT_CLEARED) ||
            (this.oldClearingStatus == CLEARING_STATUS.IN_CLEARING))) {

      final Set<String> toAddr = new TreeSet<String>();
      final MailHotline mailHotline = getUserNotifier(toAddr);
      mailHotline.notifyRejection(this.modifyAttrValue.getAttribute().getAttributeName(),
          this.modifyAttrValue.getValue());
    }
  }

  /**
   * iCDM-890 <br>
   * On AttrVal DELETE Operation, if this attribute value is used in PIDC, notify all the owners of PIDC
   */
  private void notifyPidcOwners() {
    if ((getErrorCause() == ERROR_CAUSE.NONE) && (this.commandMode == COMMAND_MODE.DELETE) && !this.unDelete) {
      notifyOwners();
    }
  }

  /**
   * iCDM-890 <br>
   * Trigger mail in Background (without user inervention) thread for the pidc owners *
   */
  private void notifyOwners() {
    // Run the operation in background
    final Thread job = new Thread("NotifyPIDCOwners") { // NOPMD by adn1cob on 7/28/14 10:46 AM

      /**
       * Notify owners of each PIDC
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        // ICDM-1505
        Map<String, Map<String, Map<String, Long>>> userPidcMap = CmdModAttributeValue.this.getDataLoader()
            .getPidcUsersUsingAttrValue(CmdModAttributeValue.this.modifyAttrValue.getValueID());
        if (!userPidcMap.isEmpty()) {
          Map<String, Map<String, Map<String, Long>>> toAddrPidc =
              new ConcurrentHashMap<String, Map<String, Map<String, Long>>>();
          // Iterate users
          // Trigger mail to the receipients
          if (!toAddrPidc.isEmpty()) {
            CmdModAttributeValue.this.triggerMailNotification(toAddrPidc);
          }
        }
      }

    };
    job.start();
  }

  /**
   * iCDM-890 <br>
   * Triggers mail notification , grouped by receipient address with their PIDC's
   *
   * @param toAddrPidc receipients with pidcs
   */
  private void triggerMailNotification(final Map<String, Map<String, Map<String, Long>>> toAddrPidc) {
    SortedSet<String> addr;
    for (String toAddr : toAddrPidc.keySet()) {
      addr = new TreeSet<String>(); // NOPMD by adn1cob on 8/1/14 10:47 AM
      addr.add(toAddr);
      final MailHotline mailHotline = getUserNotifier(addr);
      mailHotline.notifyDeletionToOwner(CmdModAttributeValue.this.modifyAttrValue.getAttribute().getAttributeName(),
          CmdModAttributeValue.this.modifyAttrValue.getValue(), toAddrPidc.get(toAddr),
          getDataProvider().getCurrentUser().getFullName(), getDataProvider().getCurrentUser().getDepartment(),
          getDelAttrValMailTemp());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      getDataCache().getAllAttrValuesMap().remove(this.newDbAttrValueID);
    }
  }

  /**
   * {@inheritDoc} return the id of the new attr value in case of insert & update mode, return the id of the old attr
   * value in case of delete mode
   */
  @Override
  public Long getPrimaryObjectID() {

    if (this.commandMode == COMMAND_MODE.INSERT) {
      return this.newDbAttrValueID;
    }

    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return this.modfyDbAttrValID;
    }

    return this.delDbAttrValueID;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Attribute Value";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();

    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        updateTransSummary(detailsList);
        break;
      case DELETE:
        if (this.unDelete) {
          this.summaryData.setOperation("UN-" + getCommandMode().toString());
        }
        break;
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * @param detailsList
   */
  private void updateTransSummary(final SortedSet<TransactionSummaryDetails> detailsList) {
    if (this.modifyAttrValue.getAttribute().getValueType() == AttributeValueType.BOOLEAN) {
      if (this.newAttrVal.equals(ApicConstants.BOOLEAN_TRUE_DB_STRING)) {
        this.newAttrVal = ApicConstants.BOOLEAN_TRUE_STRING;
      }
      else {
        this.newAttrVal = ApicConstants.BOOLEAN_FALSE_STRING;
      }
    }
    String newValue = CommonUtils.checkNull(this.newAttrValEng, this.newAttrVal);
    String oldValue = CommonUtils.checkNull(this.oldAttrValEng, this.oldAttrVal);
    addTransactionSummaryDetails(detailsList, oldValue, newValue, "Value (English)");
    addTransactionSummaryDetails(detailsList, this.oldAttrValGer, this.newAttrValGer, "Value (German)");
    // Add transaction data for the clearing status
    addTransactionSummaryDetails(detailsList, this.oldClearingStatus.getUiText(), this.newClearingStatus.getUiText(),
        "ClearingStatus");
    addTransactionSummaryDetails(detailsList, this.oldAttrValDescEng, this.newAttrValDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldAttrValDescGer, this.newAttrValDescGer, "Description (German)");
    addTransactionSummaryDetails(detailsList, this.oldChangeComment, this.newChangeComment, "Change Comment");
    addTransactionSummaryDetails(detailsList, this.oldAttrCharVal == null ? "" : this.oldAttrCharVal.getName(),
        this.newAttrCharVal == null ? "" : this.newAttrCharVal.getName(), ApicConstants.CHARVAL);
  }

  /**
   * iCDM-834 <br>
   * Checks if the attribute value if created in un-cleared status. <br>
   * Call this method before sending mail notification
   *
   * @return true if MAIL notication is required for iCDM Hotline
   */
  protected boolean isClearanceRequired() {
    if ((getErrorCause() == ERROR_CAUSE.NONE) && (this.commandMode == COMMAND_MODE.INSERT) &&
        (this.newClearingStatus == CLEARING_STATUS.NOT_CLEARED)) {
      return true;
    }
    return false;
  }

  /**
   * iCDM-834 <br>
   * Send mail notification
   */
  protected void sendMailNotification() {
    // Additional clearance check, if caller does not check for clearance,avaoid sending mail
    if (isClearanceRequired()) {
      final MailHotline mailHotline = getHotlineNotifier();
      // get appropriate SUBJECT from table
      final String subject = getDataProvider().getParameterValue(ApicConstants.ICDM_HOTLINE_SUBJECT);
      mailHotline.setSubject(subject);
      // Icdm-1154 get the Value of the Attribute for all types
      // ICDM-2624 Mail notification for grouped attribute
      mailHotline.send4Clearance(this.attribute.getAttributeName(), this.modifyAttrValue.getValue(),
          this.attribute.isGrouped());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier;

    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = insertPrimaryIdn();
        break;
      case UPDATE:
        objectIdentifier = updatePrimaryIdn();
        break;
      case DELETE:
        objectIdentifier = this.modifyAttrValue.getValue();
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }

    return objectIdentifier;

  }

  /**
   * @return
   */
  private String updatePrimaryIdn() {
    String objectIdentifier;
    if (this.modifyAttrValue.getAttribute().getValueType() == AttributeValueType.BOOLEAN) {
      if (this.modifyAttrValue.getValue().equals(ApicConstants.BOOLEAN_TRUE_DB_STRING)) {
        objectIdentifier = ApicConstants.BOOLEAN_TRUE_STRING;
      }
      else {
        objectIdentifier = ApicConstants.BOOLEAN_FALSE_STRING;
      }
    }
    else {
      objectIdentifier = this.modifyAttrValue.getValue();
    }
    return objectIdentifier;
  }

  /**
   * @return
   */
  private String insertPrimaryIdn() {
    String objectIdentifier;
    if (getDataProvider().getLanguage().equals(Language.ENGLISH)) {
      objectIdentifier = this.newAttrValEng;
    }
    else {
      objectIdentifier = this.newAttrValGer;
    }
    if (objectIdentifier == null) {
      objectIdentifier = this.newAttrVal;
    }
    return objectIdentifier;
  }

  /**
   * Icdm-830 New Clearing status will be set only for Not and In clearing values set the new Clearing status
   *
   * @param status Clearing status enum
   */
  public void setNewClearingStatus(final CLEARING_STATUS status) {
    this.newClearingStatus = status;
  }

  /**
   * @param attrCharValUpd updated Char val
   */
  public void setNewattrCharVal(final AttributeCharacteristicValue attrCharValUpd) {
    this.newAttrCharVal = attrCharValUpd;

  }

  /**
   * @return the delAttrValMailTemp
   */
  public String getDelAttrValMailTemp() {
    return this.delAttrValMailTemplate;
  }

  /**
   * @param delAttrValMail the delAttrValMailTemp to set
   */
  public void setDelAttrValMailTemp(final String delAttrValMail) {
    this.delAttrValMailTemplate = delAttrValMail;
  }

}
