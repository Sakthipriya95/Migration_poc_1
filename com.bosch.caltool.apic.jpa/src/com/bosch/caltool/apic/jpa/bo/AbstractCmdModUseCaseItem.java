/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.sql.Timestamp;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Abstract class for modification of a use case item (command)
 *
 * @author bne4cob
 */
public abstract class AbstractCmdModUseCaseItem extends AbstractCommand {

  /**
   * The UC item
   */
  private AbstractUseCaseItem ucItem;

  /**
   * Old English name
   */
  private String oldNameEng;

  /**
   * New English name
   */
  private String newNameEng;

  /**
   * Old German name
   */
  private String oldNameGer;
  /**
   * New German name
   */
  private String newNameGer;

  /**
   * Old English Desc
   */
  private String oldDescEng;
  /**
   * New English Desc
   */
  private String newDescEng;

  /**
   * Old German Desc
   */
  private String oldDescGer;
  /**
   * New German Desc
   */
  private String newDescGer;

  /**
   * Name of uc item, only for logging
   */
  private String ucName;

  /**
   * The old delete flag
   */
  private String oldDeleteFlag;

  /**
   * The old last confirmation date
   */
  private Timestamp oldLastConfirmationDate;


  /**
   * The new last confirmation date
   */
  private Timestamp newLastConfirmationDate;


  /**
   * New delete flag
   */
  private String newDeleteFlag;

  /**
   * Parent of the item to be created
   */
  private AbstractUseCaseItem parent;

  /**
   * ICDM-1502 CmdModMultipleLinks instance
   */
  protected CmdModMultipleLinks cmdMultipleLinks;

  /**
   * ICDM-1558 old value for Relevancy for Focus matrix
   */
  protected boolean oldFocusMatrxMapping;


  /**
   * ICDM-1558 new value for Relevancy for Focus matrix
   */
  protected boolean newFocusMatrxMapping;

  /**
   * Stack for storing child commands executed after creating the Use Case Entry icdm-358
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);


  /**
   * Constructor to be called when a uc item is to created
   *
   * @param dataProvider data Provider
   * @param parent parent of this use case item.
   */
  protected AbstractCmdModUseCaseItem(final ApicDataProvider dataProvider, final AbstractUseCaseItem parent) {
    super(dataProvider);

    this.parent = parent;
    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor to be called when a uc item is to be modified or deleted
   *
   * @param dataProvider data Provider
   * @param ucItem Use Case Item
   * @param deleteFlag if delete/un-delete is not to be done, set this as null. else set either ApicConstants.YES or
   *          ApicConstants.NO
   */
  protected AbstractCmdModUseCaseItem(final ApicDataProvider dataProvider, final AbstractUseCaseItem ucItem,
      final String deleteFlag) {
    super(dataProvider);

    setUcItem(ucItem);

    this.ucName = ucItem.getName();
    this.newDeleteFlag = deleteFlag;

    this.oldNameEng = ucItem.getNameEng();
    this.newNameEng = this.oldNameEng;

    this.oldNameGer = ucItem.getNameGer();
    this.newNameGer = this.oldNameGer;

    this.oldDescEng = ucItem.getDescEng();
    this.newDescEng = this.oldDescEng;

    this.oldDescGer = ucItem.getDescGer();
    this.newDescGer = this.oldDescGer;

    this.oldDeleteFlag = ucItem.isDeleted() ? ApicConstants.YES : ApicConstants.CODE_NO;

    this.oldLastConfirmationDate = ApicUtil.calendarToTimestamp(ucItem.getLastConfirmationDate());
    this.newLastConfirmationDate = this.oldLastConfirmationDate;

    if (deleteFlag == null) {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    else {
      this.commandMode = COMMAND_MODE.DELETE;
    }

  }

  /**
   * @return the ucItem
   */
  protected final AbstractUseCaseItem getUcItem() {
    return this.ucItem;
  }

  /**
   * @return the oldNameEng
   */
  protected final String getOldNameEng() {
    return this.oldNameEng;
  }

  /**
   * @return the newNameEng
   */
  protected final String getNewNameEng() {
    return this.newNameEng;
  }

  /**
   * @return the oldNameGer
   */
  protected final String getOldNameGer() {
    return this.oldNameGer;
  }

  /**
   * @return the newNameGer
   */
  protected final String getNewNameGer() {
    return this.newNameGer;
  }

  /**
   * @return the oldDescEng
   */
  protected final String getOldDescEng() {
    return this.oldDescEng;
  }

  /**
   * @return the newDescEng
   */
  protected final String getNewDescEng() {
    return this.newDescEng;
  }

  /**
   * @return the oldDescGer
   */
  protected final String getOldDescGer() {
    return this.oldDescGer;
  }

  /**
   * @return the newDescGer
   */
  protected final String getNewDescGer() {
    return this.newDescGer;
  }

  /**
   * @param nameEng the name Eng to set
   */
  public final void setNameEng(final String nameEng) {
    this.newNameEng = nameEng;
  }

  /**
   * @param nameGer the name Ger to set
   */
  public final void setNameGer(final String nameGer) {
    this.newNameGer = nameGer;
  }

  /**
   * @param descEng the desc Eng to set
   */
  public final void setDescEng(final String descEng) {
    this.newDescEng = descEng;
  }

  /**
   * @param descGer the desc Ger to set
   */
  public final void setDescGer(final String descGer) {
    this.newDescGer = descGer;
  }

  /**
   * @return true if Eng name is changed
   */
  protected final boolean isNameChangedEng() {
    return (this.newNameEng != null) && !CommonUtils.isEqual(this.oldNameEng, this.newNameEng);
  }

  /**
   * @return true if Ger name is changed
   */
  protected final boolean isNameChangedGer() {
    // German Name can be null in case of edit Use Case Values
    return !CommonUtils.isEqual(this.oldNameGer, this.newNameGer);
  }

  /**
   * @return true if Eng Desc is changed
   */
  protected final boolean isDescChangedEng() {
    return (this.newDescEng != null) && !CommonUtils.isEqual(this.oldDescEng, this.newDescEng);
  }

  /**
   * @return true if Ger Desc is changed
   */
  protected final boolean isDescChangedGer() {
    // German Name can be null in case of edit Use Case Values
    return !CommonUtils.isEqual(this.oldDescGer, this.newDescGer);
  }

  /**
   * @return if the Focus matrix mapping is updated
   */
  protected final boolean isFocusMatrixMappingChanged() {
    return this.newFocusMatrxMapping != this.oldFocusMatrxMapping;
  }

  /**
   * @return true if deleted flag is changed
   */
  protected final boolean isDeletedFlagChanged() {
    // If delete flag is set and is different from old delete flag
    return (this.newDeleteFlag != null) && !CommonUtils.isEqual(this.oldDeleteFlag, this.newDeleteFlag);
  }

  /**
   * @return
   */
  protected final boolean isLastconfirmationdateChanged() {
    return (!CommonUtils.isEqual(this.oldLastConfirmationDate, this.newLastConfirmationDate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isNameChangedEng() || isNameChangedGer() || isDescChangedEng() || isDescChangedGer() ||
        isDeletedFlagChanged() || (this.cmdMultipleLinks != null) || isFocusMatrixMappingChanged() ||
        isLastconfirmationdateChanged();
  }


  /**
   * ICDM-722 {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
  }

  /**
   * @return New Uc Item's ID
   */
  public final Long getNewUcItemID() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      return getUcItem().getID();
    }
    return null;
  }

  /**
   * @return the parent
   */
  protected final AbstractUseCaseItem getParent() {
    return this.parent;
  }

  /**
   * @param ucItem the ucItem to set
   */
  protected final void setUcItem(final AbstractUseCaseItem ucItem) {
    this.ucItem = ucItem;
  }

  /**
   * @return the oldDeleteFlag
   */
  protected final String getOldDeleteFlag() {
    return this.oldDeleteFlag;
  }

  /**
   * @return the newDeleteFlag
   */
  protected final String getNewDeleteFlag() {
    return this.newDeleteFlag;
  }

  /**
   * ICDM-722 returns modified value
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier;
    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = objForCmdIns();
        break;

      case UPDATE:
      case DELETE:
        objectIdentifier = this.ucName;
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
  private String objForCmdIns() {
    String objectIdentifier;
    if (getDataProvider().getLanguage() == Language.ENGLISH) {
      objectIdentifier = this.newNameEng;
    }
    else {
      objectIdentifier = this.newNameGer;
    }
    return objectIdentifier;
  }

  /**
   * @param newFocMatrxMapping to set
   */
  public void setNewFocusMatrxMapping(final boolean newFocMatrxMapping) {
    this.newFocusMatrxMapping = newFocMatrxMapping;
  }

  /**
   * @return the oldLastConfirmationDate
   */
  public Timestamp getOldLastConfirmationDate() {
    return this.oldLastConfirmationDate;
  }

  /**
   * @param oldLastConfirmationDate the oldLastConfirmationDate to set
   */
  public void setOldLastConfirmationDate(final Timestamp oldLastConfirmationDate) {
    this.oldLastConfirmationDate = oldLastConfirmationDate;
  }

  /**
   * @return the newLastConfirmationDate
   */
  public Timestamp getLastConfirmationDate() {
    return this.newLastConfirmationDate;
  }

  /**
   * @param newLastConfirmationDate the newLastConfirmationDate to set
   */
  public void setLastConfirmationDate(final Timestamp newLastConfirmationDate) {
    this.newLastConfirmationDate = newLastConfirmationDate;
  }
}
