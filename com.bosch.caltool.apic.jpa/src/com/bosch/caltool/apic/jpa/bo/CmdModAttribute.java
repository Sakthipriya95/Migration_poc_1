/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TCharacteristic;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValueType;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;

/**
 * CmdModAttribute.java - Command handles all db operations on INSERT, UPDATE, DELETE on Attributes
 *
 * @author dmo5cob
 */
public class CmdModAttribute extends AbstractCommand {

  /**
   * the DB Attribute to be modified
   */

  // Changes for ICDM-229
  private Long modifiedDbAttrID;

  /**
   * the DB Attribute to be inserted
   */
  private Long newDbAttrID;

  /**
   * the DB Attribute to be marked as deleted
   */

  // Changes for ICDM-229
  private Long deletedDbAttrID;

  /**
   * Other old and new parameters
   */
  private String oldAttrNameEng;
  private String newAttrNameEng;

  private String oldAttrDescEng;
  private String newAttrDescEng;

  // ICDM-1397
  private String oldChangeComment;
  private String newChangeComment;

  // ICDM-1560
  private String oldEadmName;
  private String newEadmName;

  private String oldAttrNameGer;
  private String newAttrNameGer;

  private String oldAttrDescGer;
  private String newAttrDescGer;

  private long oldGroupID;
  private long newGroupID;

  /**
   * Old value type
   */
  private AttributeValueType oldValType;

  /**
   * New value type
   */
  private AttributeValueType newValType;

  private String oldUnit;
  private String newUnit;

  private String oldFormat;
  private String newFormat;

  private boolean oldNormFlag;
  private boolean newNormFlag;

  private boolean oldMandatory;
  private boolean newMandatory;

  private boolean oldPartNumFlag;
  private boolean newPartNumFlag;

  private boolean oldSpecLinkFlag;
  private boolean newSpecLinkFlag;

  private boolean oldGrpdFlag;
  private boolean newGrpdFlag;


  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  private static final String ATTR_ENTITY_ID = "ATTR_ENTITY_ID";

  private final TransactionSummary summaryData = new TransactionSummary(this);

  // ICdm-480 Attribute Internal and Attr value Internal flags
  private boolean oldAttrExtFlag;

  private boolean newAttrExtFlag;
  private boolean oldAttrValExtFlag;

  private boolean newAttrValExtFlag;


  private AttributeCharacteristic oldAttrChar;

  private AttributeCharacteristic newAttrChar;

  private boolean delCharFlag;

  /**
   * Stack for storing child commands executed after updating the Attr
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * ICDM-1502 command for multiple links
   */
  private CmdModMultipleLinks cmdMultipleLinks;

  /**
   * Constructor to add a new Attribute - use this constructor for INSERT
   *
   * @param apicDataProvider data provider
   */
  public CmdModAttribute(final ApicDataProvider apicDataProvider) {
    super(apicDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor to delete or update existing Attribute
   *
   * @param apicDataProvider data provider
   * @param modifyAttribute attribute
   * @param isDelete whether to delete or not
   */
  public CmdModAttribute(final ApicDataProvider apicDataProvider, final Attribute modifyAttribute,
      final boolean isDelete) {
    super(apicDataProvider);

    // Set the appropriate command mode
    if (isDelete) {
      // set the command mode - DELETE
      this.commandMode = COMMAND_MODE.DELETE;
      // the Attribute to be deleted, rember for undo
      // get the DB entity for the Attribute to be modified
      // ICDM-229 changes
      this.deletedDbAttrID = modifyAttribute.getAttributeID();
    }
    else {
      // set the command mode - UPDATE
      this.commandMode = COMMAND_MODE.UPDATE;
      // the Attribute to be modified
      // get the DB entity for the Attribute to be modified
      // ICDM-229 changes
      this.modifiedDbAttrID = modifyAttribute.getAttributeID();
    }
    // initialize command with current values from UI
    setAttributeFieldsToCommand(modifyAttribute);
  }

  /**
   * Set required fileds to the Command from UI, also store old fields to support undo
   *
   * @param apicDataProvider
   * @param modifyAttribute
   */
  private void setAttributeFieldsToCommand(final Attribute modifyAttribute) {
    // ENGLISH Namw
    this.oldAttrNameEng = modifyAttribute.getAttributeNameEng();
    this.newAttrNameEng = this.oldAttrNameEng;
    // ENGLISH Desc
    this.oldAttrDescEng = modifyAttribute.getAttributeDescEng();
    this.newAttrDescEng = this.oldAttrDescEng;

    // GERMAN Name
    this.oldAttrNameGer = modifyAttribute.getAttributeNameGer();
    this.newAttrNameGer = this.oldAttrNameGer;
    // GERMAN Desc
    this.oldAttrDescGer = modifyAttribute.getAttributeDescGer();
    this.newAttrDescGer = this.oldAttrDescGer;

    this.oldChangeComment = modifyAttribute.getChangeComment();
    this.newChangeComment = this.oldChangeComment;
    // ICDM-1560
    this.oldEadmName = modifyAttribute.getEadmName();
    this.newEadmName = this.oldEadmName;
    // Group id
    this.oldGroupID = modifyAttribute.getAttributeGroupID();
    this.newGroupID = this.oldGroupID;

    // Val type ID
    this.oldValType = modifyAttribute.getValueType();
    this.newValType = this.oldValType;

    // Unit
    this.oldUnit = modifyAttribute.getUnit();
    this.newUnit = this.oldUnit;
    // Number format
    this.oldFormat = modifyAttribute.getFormat();
    this.newFormat = this.oldFormat;
    // Normalized flag
    this.oldNormFlag = modifyAttribute.isNormalized();
    this.newNormFlag = this.oldNormFlag;
    // Mandatory flag
    this.oldMandatory = modifyAttribute.isMandatory();
    this.newMandatory = this.oldMandatory;

    // Part Number flag
    this.newPartNumFlag = this.oldPartNumFlag;
    this.oldPartNumFlag = modifyAttribute.hasPartNumber();

    // Spec Link flag
    this.oldSpecLinkFlag = modifyAttribute.hasSpecLink();
    this.newSpecLinkFlag = this.oldSpecLinkFlag;

    // Attr Internal Icdm-480
    this.oldAttrExtFlag = modifyAttribute.isAttrExternal();
    this.newAttrExtFlag = this.oldAttrExtFlag;

    // Value Security ICdm-480
    this.oldAttrValExtFlag = modifyAttribute.isValueExternal();
    this.newAttrValExtFlag = this.oldAttrValExtFlag;

    this.oldAttrChar = modifyAttribute.getCharacteristic();
    this.newAttrChar = this.oldAttrChar;

    // Grouped flag
    this.oldGrpdFlag = modifyAttribute.isGrouped();
    this.newGrpdFlag = this.oldGrpdFlag;


  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    // create a new database entity
    // ICDM-229
    final TabvAttribute newDbAttr = new TabvAttribute();
    validateEADMName(newDbAttr.getAttrId());
    setNewAttributeFields(newDbAttr);

    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, newDbAttr, ATTR_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(newDbAttr);
    this.newDbAttrID = newDbAttr.getAttrId();

    // add the new attribute to the list of all attributes
    // (the constructor of Attribute will add the new object)
    final Attribute dbAttr = new Attribute(getDataProvider(), newDbAttr.getAttrId());
    getDataProvider().getAllAttributes().put(dbAttr.getAttributeID(), dbAttr);
    // Icdm-461 Changes
    getChangedData().put(this.newDbAttrID,
        new ChangedData(ChangeType.INSERT, this.newDbAttrID, TabvAttribute.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    if (!CommonUtils.isEqual(this.oldEadmName, this.newEadmName)) {
      validateEADMName(this.modifiedDbAttrID);
    }
    // Check for any parallel changes
    // ICDM-229 Changes for DB notifiacation

    // Icdm-461 Changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.modifiedDbAttrID, TabvAttribute.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttribute(this.modifiedDbAttrID).getObjectDetails());
    final TabvAttribute modifiedAttr = getEntityProvider().getDbAttribute(this.modifiedDbAttrID);
    validateStaleData(modifiedAttr);
    // Update modified data
    setNewAttributeFields(modifiedAttr);

    if (null != this.cmdMultipleLinks) {
      // update the links
      this.childCmdStack.addCommand(this.cmdMultipleLinks);
    }

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifiedAttr, ATTR_ENTITY_ID);

    // Icdm-461 Changes
    getChangedData().put(this.modifiedDbAttrID, chdata);


  }

  /**
   * @throws CommandException
   */
  private void validateEADMName(final Long attrID) throws CommandException {
    if (getDataLoader().checkEADMNameExists(this.newEadmName, attrID)) {
      throw new CommandException("EADM name already exists");
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {

    // Check for any parallel changes
    // ICDM-229 Changes for DB notifiacation
    // Icdm-461 Changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.deletedDbAttrID, TabvAttribute.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttribute(this.deletedDbAttrID).getObjectDetails());
    final TabvAttribute deletedAttr = getEntityProvider().getDbAttribute(this.deletedDbAttrID);
    validateStaleData(deletedAttr);
    // remember the old attribute database entity for UNDO

    this.modifiedDbAttrID = this.deletedDbAttrID;
    // set the deleted flag, do not delete the entity
    deletedAttr.setDeletedFlag(ApicConstants.YES);
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, deletedAttr, ATTR_ENTITY_ID);

    // Icdm-461 Changes
    getChangedData().put(this.deletedDbAttrID, chdata);
    /* DO NOT Delete - Attribute Values and its Dependencies */

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TabvAttribute dbAttr = getEntityProvider().getDbAttribute(this.newDbAttrID);
    validateStaleData(dbAttr);
    // unregister the attr
    getEntityProvider().deleteEntity(dbAttr);
    // remove the NodeAccessRight from the list of all NodeAccessRights
    getDataProvider().getAllAttributes().remove(dbAttr.getAttrId());
    // Icdm-461 Changes
    getChangedData().put(this.newDbAttrID,
        new ChangedData(ChangeType.INSERT, this.newDbAttrID, TabvAttribute.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    // Check for any parallel changes
    // ICDM-229 Changes for DB notifiacation
    // Icdm-461 Changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.modifiedDbAttrID, TabvAttribute.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttribute(this.modifiedDbAttrID).getObjectDetails());
    final TabvAttribute modifiedAttr = getEntityProvider().getDbAttribute(this.modifiedDbAttrID);
    validateStaleData(modifiedAttr);
    // Update modified data,
    setOldAttributeFields(modifiedAttr);
    // sets to old modified user and date
    setUserDetails(COMMAND_MODE.UPDATE, modifiedAttr, ATTR_ENTITY_ID);

    // Icdm-461 Changes
    getChangedData().put(this.modifiedDbAttrID, chdata);

  }

  /**
   * {@inheritDoc}
   *
   * @throws CommandException from stale data validation
   */
  @Override
  protected final void undoDeleteCommand() throws CommandException {
    // Check for any parallel changes
    // ICDM-229 Changes for DB notifiacation
    // Icdm-461 Changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.deletedDbAttrID, TabvAttribute.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getAttribute(this.deletedDbAttrID).getObjectDetails());
    final TabvAttribute deletedAttr = getEntityProvider().getDbAttribute(this.deletedDbAttrID);
    validateStaleData(deletedAttr);
    /* UNDO DELETE for this main attribute */
    // set old attribute fields,also sets to old modified user and date
    setOldAttributeFields(deletedAttr);
    // sets to old modified user and date
    setUserDetails(COMMAND_MODE.UPDATE, deletedAttr, ATTR_ENTITY_ID);

    // Icdm-461 Changes
    getChangedData().put(this.deletedDbAttrID, chdata);

  }

  /**
   * Method sets the required fields of the TabvAttribute entity
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void setNewAttributeFields(final TabvAttribute dbAttr) throws CommandException {
    // set name and desc fields
    dbAttr.setAttrNameEng(this.newAttrNameEng);
    dbAttr.setAttrDescEng(this.newAttrDescEng);
    dbAttr.setAttrNameGer(this.newAttrNameGer);
    dbAttr.setAttrDescGer(this.newAttrDescGer);
    dbAttr.setChangeComment(this.newChangeComment);
    dbAttr.setEadmName(this.newEadmName);
    // set group info
    dbAttr.setTabvAttrGroup(getEntityProvider().getDbGroup(this.newGroupID));
    // set value type info
    final TabvAttrValueType valType = getEntityProvider().getDbValueType(this.newValType.getValueTypeID());
    dbAttr.setTabvAttrValueType(valType);
    setNewUnit(dbAttr, this.newValType);
    // set format
    dbAttr.setFormat(this.newFormat);
    setNewNormFlag(dbAttr);
    setNewMandatory(dbAttr);
    setNewPartNum(dbAttr);
    setNewSpecLink(dbAttr);

    setAttrSecurity(dbAttr);

    setNewValueSecurity(dbAttr);

    // ICdm-955 set the new Char to the Attr
    dbAttr.settCharacteristic(null);
    setNewCharacteristic(dbAttr);

    // new attribute, deleted flag is NO
    dbAttr.setDeletedFlag(ApicConstants.CODE_NO);

    if (this.delCharFlag) {
      createCharValDelCommand(dbAttr);
    }
    // ICDM-2590
    setNewGroupedAttr(dbAttr);
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewCharacteristic(final TabvAttribute dbAttr) {
    if (this.newAttrChar != null) {
      TCharacteristic dbCharacteristic = getEntityProvider().getDbCharacteristic(this.newAttrChar.getID());
      dbAttr.settCharacteristic(dbCharacteristic);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewValueSecurity(final TabvAttribute dbAttr) {
    if (this.newAttrValExtFlag) {
      dbAttr.setValueSecurity(ApicConstants.EXTERNAL);
    }
    else {
      dbAttr.setValueSecurity(ApicConstants.INTERNAL);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setAttrSecurity(final TabvAttribute dbAttr) {
    // ICdm-480
    if (this.newAttrExtFlag) {
      dbAttr.setAttrSecurity(ApicConstants.EXTERNAL);
    }
    else {
      dbAttr.setAttrSecurity(ApicConstants.INTERNAL);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewSpecLink(final TabvAttribute dbAttr) {
    // set spec link flag
    if (this.newSpecLinkFlag) {
      dbAttr.setSpecLinkFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setSpecLinkFlag(ApicConstants.CODE_NO);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewPartNum(final TabvAttribute dbAttr) {
    // set part number flag
    if (this.newPartNumFlag) {
      dbAttr.setPartNumberFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setPartNumberFlag(ApicConstants.CODE_NO);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewMandatory(final TabvAttribute dbAttr) {
    // set Mandatory flag
    if (this.newMandatory) {
      dbAttr.setMandatory(ApicConstants.YES);
    }
    else {
      dbAttr.setMandatory(ApicConstants.CODE_NO);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewGroupedAttr(final TabvAttribute dbAttr) {
    // set Grouped flag
    if (this.newGrpdFlag) {
      dbAttr.setGroupFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setGroupFlag(ApicConstants.CODE_NO);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setNewNormFlag(final TabvAttribute dbAttr) {
    // set normalized flag
    if (this.newNormFlag) {
      dbAttr.setNormalizedFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setNormalizedFlag(ApicConstants.CODE_NO);
    }
  }

  /**
   * @param dbAttr TabvAttribute
   * @param valType TabvAttrValueType
   */
  private void setNewUnit(final TabvAttribute dbAttr, final AttributeValueType valType) {
    // set Unit
    if (valType == AttributeValueType.NUMBER) {
      dbAttr.setUnits(this.newUnit);
    }
    else {
      dbAttr.setUnits("-");
    }
  }

  /**
   * ICdm-955 Delete the mapping of Char Value with the Char of attr is changed
   *
   * @param dbAttr
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createCharValDelCommand(final TabvAttribute dbAttr) throws CommandException {
    Attribute attribute = getDataProvider().getAllAttributes().get(dbAttr.getAttrId());
    if (attribute.hasCharValue()) {
      for (AttributeValue attrVal : attribute.getValuesWithCharVal()) {
        CmdModAttributeValue command = new CmdModAttributeValue(getDataProvider(), attrVal, false);
        command.setNewAttrValEng(attrVal.getValueEng());
        command.setNewAttrValGer(attrVal.getValueGer());
        command.setNewAttrValDescEng(attrVal.getValueDescEng());
        command.setNewAttrValDescGer(attrVal.getValueDescGer());
        command.setNewattrCharVal(null);
        this.childCmdStack.addCommand(command);
      }
    }
  }

  /**
   * @param dbAttr TabvAttribute
   */
  private void setOldAttributeFields(final TabvAttribute dbAttr) {
    // set name and desc fields
    dbAttr.setAttrNameEng(this.oldAttrNameEng);
    dbAttr.setAttrDescEng(this.oldAttrDescEng);
    dbAttr.setAttrNameGer(this.oldAttrNameGer);
    dbAttr.setAttrDescGer(this.oldAttrDescGer);
    dbAttr.setChangeComment(this.oldChangeComment);
    dbAttr.setEadmName(this.oldEadmName);
    // set group info
    dbAttr.setTabvAttrGroup(getEntityProvider().getDbGroup(this.oldGroupID));
    // set value type info
    final TabvAttrValueType valType = getEntityProvider().getDbValueType(this.oldValType.getValueTypeID());
    dbAttr.setTabvAttrValueType(valType);
    // set Unit
    if (this.oldValType == AttributeValueType.NUMBER) {
      dbAttr.setUnits(this.oldUnit);
    }
    else {
      dbAttr.setUnits("-");
    }
    // set format
    dbAttr.setFormat(this.oldFormat);
    // set normalized flag
    if (this.oldNormFlag) {
      dbAttr.setNormalizedFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setNormalizedFlag(ApicConstants.CODE_NO);
    }
    // set mandatory flag
    if (this.oldMandatory) {
      dbAttr.setMandatory(ApicConstants.YES);
    }
    else {
      dbAttr.setMandatory(ApicConstants.CODE_NO);
    }
    // set part number flag
    if (this.oldPartNumFlag) {
      dbAttr.setPartNumberFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setPartNumberFlag(ApicConstants.CODE_NO);
    }
    // set spec link flag
    if (this.oldSpecLinkFlag) {
      dbAttr.setSpecLinkFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setSpecLinkFlag(ApicConstants.CODE_NO);
    }
    // Icdm-480
    if (this.oldAttrExtFlag) {
      dbAttr.setAttrSecurity(ApicConstants.EXTERNAL);
    }
    else {
      dbAttr.setAttrSecurity(ApicConstants.INTERNAL);
    }

    if (this.oldAttrValExtFlag) {
      dbAttr.setValueSecurity(ApicConstants.EXTERNAL);
    }
    else {
      dbAttr.setValueSecurity(ApicConstants.INTERNAL);
    }
    // Set the Old value
    if (this.oldAttrChar != null) {
      TCharacteristic dbCharacteristic = getEntityProvider().getDbCharacteristic(this.oldAttrChar.getID());
      dbAttr.settCharacteristic(dbCharacteristic);
    }
    // set deleted flag
    dbAttr.setDeletedFlag(ApicConstants.CODE_NO);

    // set grouped flag
    if (this.oldGrpdFlag) {
      dbAttr.setGroupFlag(ApicConstants.YES);
    }
    else {
      dbAttr.setGroupFlag(ApicConstants.CODE_NO);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldAttrNameEng, this.newAttrNameEng) ||
        isStringChanged(this.oldAttrDescEng, this.newAttrDescEng) ||
        isStringChanged(this.oldChangeComment, this.newChangeComment) ||
        isStringChanged(this.oldEadmName, this.newEadmName) ||
        isStringChanged(this.oldAttrNameGer, this.newAttrNameGer) ||
        isStringChanged(this.oldAttrDescGer, this.newAttrDescGer) || (this.oldGroupID == this.newGroupID) ||
        isStringChanged(this.oldUnit, this.newUnit) || isStringChanged(this.oldFormat, this.newFormat) ||
        (this.oldValType == this.newValType) || (this.oldNormFlag != this.newNormFlag) ||
        (this.oldMandatory != this.newMandatory) || (this.oldPartNumFlag != this.newPartNumFlag) ||
        (this.oldSpecLinkFlag != this.newSpecLinkFlag) || (this.oldAttrExtFlag != this.newAttrExtFlag) ||
        (this.oldAttrValExtFlag != this.newAttrValExtFlag) ||
        (!CommonUtils.isEqual(this.oldAttrChar, this.newAttrChar) || (this.oldGrpdFlag != this.newGrpdFlag));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());

  }

  /**
   * Set the new English Name
   *
   * @param newAttrName The new attrName in english
   */
  public void setAttrName(final String newAttrName) {

    this.newAttrNameEng = newAttrName;
  }

  /**
   * Set the new attrName desc
   *
   * @param newAttrNameDesc new english description
   */
  public void setAttrNameDesc(final String newAttrNameDesc) {

    this.newAttrDescEng = newAttrNameDesc;
  }

  /**
   * Set the new group id
   *
   * @param newAttrGroupId group ID
   */
  public void setAttrGroupId(final long newAttrGroupId) {

    this.newGroupID = newAttrGroupId;
  }

  /**
   * Set the new value type
   *
   * @param newValueType value type
   */
  public void setValueType(final AttributeValueType newValueType) {
    this.newValType = newValueType;
  }

  /**
   * Set the new unit
   *
   * @param newUnit unit
   */
  public void setUnit(final String newUnit) {
    this.newUnit = newUnit;
  }

  /**
   * set the new format
   *
   * @param newFormat format
   */
  public void setFormat(final String newFormat) {
    this.newFormat = newFormat;

  }

  /**
   * Set the normalized flag
   *
   * @param normFlagitem normalised flag
   */
  public void setNormFlag(final boolean normFlagitem) {
    this.newNormFlag = normFlagitem;

  }

  /**
   * Set the part number flag
   *
   * @param partNumFlagitem part number flag
   */
  public void setPartNumFlag(final boolean partNumFlagitem) {
    this.newPartNumFlag = partNumFlagitem;

  }

  /**
   * Set the spec link flag
   *
   * @param specLinkFlagitem specification link flag
   */
  public void setSpecLinkFlag(final boolean specLinkFlagitem) {
    this.newSpecLinkFlag = specLinkFlagitem;

  }

  /**
   * Set the mandatory flag
   *
   * @param newMandatory mandatory flag
   */
  public void setMandatory(final boolean newMandatory) {
    this.newMandatory = newMandatory;

  }

  /**
   * Set the grouped attr flag
   *
   * @param newGrpd Grouped Attr flag
   */
  public void setGroupedAttr(final boolean newGrpd) {
    this.newGrpdFlag = newGrpd;

  }

  /**
   * Set the German name
   *
   * @param nameGer German name
   */
  public void setAttrNameGer(final String nameGer) {
    // do not allow empty spaces, save as null
    if ((nameGer == null) || nameGer.trim().isEmpty()) {
      this.newAttrNameGer = "";
    }
    else {
      this.newAttrNameGer = nameGer;
    }
  }

  /**
   * Set the german desc
   *
   * @param descGer German description
   */
  public void setAttrDescGer(final String descGer) {
    // do not allow empty spaces, save as null
    if ((descGer == null) || descGer.trim().isEmpty()) {
      this.newAttrDescGer = "";
    }
    else {
      this.newAttrDescGer = descGer;
    }
  }

  /**
   * Set the change comment
   *
   * @param changeCmt comment
   */
  public void setChangeComment(final String changeCmt) {
    this.newChangeComment = changeCmt;
  }

  // ICDM-1560
  /**
   * Set the eadm name
   *
   * @param eadmName eadmName
   */
  public void setEadmName(final String eadmName) {
    this.newEadmName = eadmName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable

  }

  /**
   * Get newly added attribute to enable selection after adding
   *
   * @return TabvAttribute newDbAttr
   */
  public TabvAttribute getNewDbAttr() {
    return getEntityProvider().getDbAttribute(this.newDbAttrID);
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {

    switch (this.commandMode) {
      case INSERT:
        if (null != this.newDbAttrID) {
          getDataProvider().getAllAttributes().remove(this.newDbAttrID);
        }
        break;
      case UPDATE:
      case DELETE:
      default:
        // Do nothing
        break;
    }

  }

  /**
   * {@inheritDoc} return the id of the new attr in case of insert & update mode, return the id of the old attr in case
   * of delete mode
   */
  @Override
  public Long getPrimaryObjectID() {

    if (this.commandMode == COMMAND_MODE.INSERT) {
      return this.newDbAttrID;
    }

    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return this.modifiedDbAttrID;
    }

    return this.deletedDbAttrID;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Attribute";
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
        caseCmdUpd(detailsList);
        break;
      case DELETE: // no details section necessary in case of delete (parent row is sufficient in transansions view)
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
  private void caseCmdUpd(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.oldAttrNameEng, this.newAttrNameEng, "Name (English)");
    addTransactionSummaryDetails(detailsList, this.oldAttrDescEng, this.newAttrDescEng, "Description (English)");
    addTransactionSummaryDetails(detailsList, this.oldAttrNameGer, this.newAttrNameGer, "Name (German)");
    addTransactionSummaryDetails(detailsList, this.oldAttrDescGer, this.newAttrDescGer, "Description (German)");
    addTransactionSummaryDetails(detailsList, this.oldChangeComment, this.newChangeComment, "Change Comment");
    addTransactionSummaryDetails(detailsList, this.oldEadmName, this.newEadmName, "EADM Name");
    addTransactionSummaryDetails(detailsList, getDataProvider().getGroup(this.oldGroupID).getName(),
        getDataProvider().getGroup(this.newGroupID).getName(), "Group");
    addTransactionSummaryDetails(detailsList, this.oldUnit, this.newUnit, "Unit");
    addTransactionSummaryDetails(detailsList, this.oldFormat, this.newFormat, "Format");
    addTransactionSummaryDetails(detailsList, this.oldValType.getDisplayText(), this.newValType.getDisplayText(),
        "Value Type");
    addTransactionSummaryDetails(detailsList, ApicBOUtil.getDisplayString(this.oldNormFlag),
        ApicBOUtil.getDisplayString(this.newNormFlag), "Normalised Flag");
    addTransactionSummaryDetails(detailsList, ApicBOUtil.getDisplayString(this.oldMandatory),
        ApicBOUtil.getDisplayString(this.newMandatory), "Mandatory");
    addTransactionSummaryDetails(detailsList, String.valueOf(this.oldPartNumFlag),
        String.valueOf(this.newPartNumFlag), "Part Number");
    addTransactionSummaryDetails(detailsList, String.valueOf(this.oldSpecLinkFlag),
        String.valueOf(this.newSpecLinkFlag), "Specification");
    addTransactionSummaryDetails(detailsList, ApicBOUtil.getDisplayString(this.oldAttrExtFlag),
        ApicBOUtil.getDisplayString(this.newAttrExtFlag), "Attribute Internally Secured");
    addTransactionSummaryDetails(detailsList, ApicBOUtil.getDisplayString(this.oldAttrValExtFlag),
        ApicBOUtil.getDisplayString(this.newAttrValExtFlag), "Attribute Value Internally Secured");
    addTransactionSummaryDetails(detailsList, String.valueOf(this.oldAttrChar), String.valueOf(this.newAttrChar),
        ApicConstants.CHARACTERISTIC);
    addTransactionSummaryDetails(detailsList, ApicBOUtil.getDisplayString(this.oldGrpdFlag),
        ApicBOUtil.getDisplayString(this.newGrpdFlag), "Grouped Attribute");
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
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {

    Long dbAttrID;
    if (this.commandMode == COMMAND_MODE.INSERT) {
      dbAttrID = this.newDbAttrID;
    }
    else if (this.commandMode == COMMAND_MODE.UPDATE) {
      dbAttrID = this.modifiedDbAttrID;
    }
    else {
      dbAttrID = this.deletedDbAttrID;
    }

    final TabvAttribute dbAttr = getEntityProvider().getDbAttribute(dbAttrID);
    if (dbAttr == null) {
      return " INVALID!";
    }

    if (getDataProvider().getLanguage() == Language.ENGLISH) {
      return dbAttr.getAttrNameEng();
    }

    return dbAttr.getAttrNameGer() == null ? dbAttr.getAttrNameEng() : dbAttr.getAttrNameGer();

  }

  /**
   * Icdm-480
   *
   * @param attrIntFlag attrIntFlag
   */
  public void setAttrExtFlag(final boolean attrIntFlag) {
    this.newAttrExtFlag = attrIntFlag;

  }

  /**
   * @param attrValIntFlag attrValIntFlag
   */
  public void setAttrValExtFlag(final boolean attrValIntFlag) {
    this.newAttrValExtFlag = attrValIntFlag;

  }

  /**
   * @param attrCharUpd attrCharUpdated
   */
  public void setAttrChar(final AttributeCharacteristic attrCharUpd) {
    this.newAttrChar = attrCharUpd;

  }

  /**
   * @param canDelCharVal flag indicates if the Char Values should be deleted
   */
  public void setDelCharValFlag(final boolean canDelCharVal) {
    this.delCharFlag = canDelCharVal;
  }

  /**
   * @param cmdMultipleLinks CmdModMultipleLinks
   */
  public void setCmdMultipleLinks(final CmdModMultipleLinks cmdMultipleLinks) {
    this.cmdMultipleLinks = cmdMultipleLinks;
  }
}
