/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Command to copy a sub variant to new/existing sub-variant of the same variant
 *
 * @author bne4cob
 */
public class CmdCopySubVariant extends AbstractCmdModProject { // NOPMD by BNE4COB on 6/21/13 6:06 PM

  /**
   * Sub Variant to copy
   */
  private final PIDCSubVariant subVarToCopy;

  /**
   * Destination sub-variant, if the sub-variant is to be copied to another sub-variant/sub-variant is the new variant
   */
  private PIDCSubVariant destSubVariant;

  /**
   * Whether this command is to be used for creating a new sub-variant or not
   */
  private boolean createNewSubVar = true;

  /**
   * PID Card of the sub-variant(s) under operation
   */
  private final PIDCVersion pidVersion;

  /**
   * PID Variant of the sub-variant(s) under operation
   */
  private final PIDCVariant variant;

  /**
   * List of var attr value already exists while copying
   */
  private List<PIDCAttributeSubVar> valAlreadyExists;

  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Constructor, to be used if the attributes are to be copied from one sub-variant to another.
   *
   * @param dataProvider the data provider
   * @param source the Sub-Variant to copy
   * @param destination the Sub-variant to which the attributes are copied
   */
  public CmdCopySubVariant(final ApicDataProvider dataProvider, final PIDCSubVariant source,
      final PIDCSubVariant destination) {
    this(dataProvider, source);
    this.destSubVariant = destination;

    this.createNewSubVar = false;
  }

  /**
   * Constructor, to be used if a new sub-variant is to be created.
   *
   * @param dataProvider the data provider
   * @param source the Sub-Variant to copy
   */
  public CmdCopySubVariant(final ApicDataProvider dataProvider, final PIDCSubVariant source) {
    super(dataProvider);
    this.subVarToCopy = source;

    this.commandMode = COMMAND_MODE.INSERT;
    this.pidVersion = source.getPidcVersion();
    this.variant = source.getPidcVariant();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    this.childCmdStack.clear();

    if (this.createNewSubVar) {
      createSubVariant();
    }
    this.attrValName = this.destSubVariant.getSubVariantName();

    this.valAlreadyExists = new ArrayList<PIDCAttributeSubVar>();

    for (IPIDCAttribute pidcAttr : this.subVarToCopy.getAttributes().values()) {
      final PIDCAttributeSubVar subVarAttrToCopy = (PIDCAttributeSubVar) pidcAttr;
      if (!isValidSubVarAttr(subVarAttrToCopy)) {
        continue;
      }
      // ICDM-1780
      final PIDCAttributeSubVar svAttrToUpdate = this.destSubVariant.getAllSubVarAttribute(subVarAttrToCopy.attrID);

      /*
       * Make the changes if 1. New sub-variant 2. If the attribute is not defined in the new sub-variant (ie, dependent
       * attributes) 3. If the value in new sub-variant attribute is null 4. value not null
       */
      createOrUpdateSubVarAttr(subVarAttrToCopy, svAttrToUpdate);

    }

  }

  /**
   * @param subVarAttrToCopy
   * @param svAttrToUpdate
   * @throws CommandException
   */
  private void createOrUpdateSubVarAttr(final PIDCAttributeSubVar subVarAttrToCopy,
      final PIDCAttributeSubVar svAttrToUpdate) throws CommandException {
    if (this.createNewSubVar || (svAttrToUpdate == null) || (svAttrToUpdate.getAttributeValue() == null) ||
        (svAttrToUpdate.getAttributeValue() != null)) { // iCDM-535, include value exists case too
      if (svAttrToUpdate == null) {
        createSubVariantAttr(subVarAttrToCopy); // Required to dependent attributes if createNewSubVar is false.
      }
      else {
        if (svAttrToUpdate.getAttributeValue() == null) {
          updateSubVariantAttr(subVarAttrToCopy, svAttrToUpdate);
        }
        else { // iCDM-535
          // attrValue already exists
          // add to a already exists list
          this.valAlreadyExists.add(svAttrToUpdate);
        }
      }
    }
  }

  /**
   * Update the sub-variant attribute
   *
   * @param subVarAttrToCopy reference attribute
   * @param svAttrToUpdate attribute to update
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void updateSubVariantAttr(final PIDCAttributeSubVar subVarAttrToCopy,
      final PIDCAttributeSubVar svAttrToUpdate) throws CommandException {
    final CmdModProjSubVariantAttr command = new CmdModProjSubVariantAttr(getDataProvider(), svAttrToUpdate);
    command.setUsed(subVarAttrToCopy.getIsUsed());
    command.setValue(subVarAttrToCopy.getAttributeValue());
    // ICDM-260
    command.setNewPartNumber(subVarAttrToCopy.getPartNumber());
    command.setNewSpecLink(subVarAttrToCopy.getSpecLink());
    command.setNewDesc(subVarAttrToCopy.getAdditionalInfoDesc());
    command.setNewHiddenFlag(subVarAttrToCopy.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);

    // Disable stale data validation as the variant attribute is created in this session
    command.setStaleDataValidate(false);
    this.childCmdStack.addCommand(command);

  }

  /**
   * Checks whether this Sub-Variant attribute can be copied to the new Sub-Variant
   *
   * @param pidcSubVarAttr the SubVariant attribute to copy
   * @return true/false
   */
  private boolean isValidSubVarAttr(final PIDCAttributeSubVar pidcSubVarAttr) {

    if (pidcSubVarAttr.getAttribute().getAttrLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR) {
      return false;
    }

    return true;
  }

  /**
   * Create the Sub-Variant
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createSubVariant() throws CommandException {
    final CmdModPidcSubVariant command = new CmdModPidcSubVariant(getDataProvider(), this.variant, false, null);
    if (this.nameValue == null) {
      command.setNameEng(this.newNameEng);
      command.setNameGer(this.newNameGer);
      command.setDescEng(this.newDescEng);
      command.setDescGer(this.newDescGer);
    }
    else {
      command.setNameValue(this.nameValue);
    }
    this.childCmdStack.addCommand(command);

    this.destSubVariant = getDataProvider().getPidcSubVaraint(command.getNewSubVariantID());
    this.attrValName = this.destSubVariant.getSubVariantName();


  }

  /**
   * Create the PIDC SubVariant attribute
   *
   * @param subVarAttrToCopy Sub-Variant attribute to copy
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createSubVariantAttr(final IPIDCAttribute subVarAttrToCopy) throws CommandException {
    final PIDCAttributeSubVar subVarAttr = new PIDCAttributeSubVar(getDataProvider(), subVarAttrToCopy.attrID,
        this.variant.getVariantID(), this.destSubVariant.getSubVariantID());

    final CmdModProjSubVariantAttr command = new CmdModProjSubVariantAttr(getDataProvider(), subVarAttr, false);
    command.setUsed(subVarAttrToCopy.getIsUsed());
    command.setValue(subVarAttrToCopy.getAttributeValue());
    command.setProRevId(this.destSubVariant.getRevision());
    // ICDM-260
    command.setNewPartNumber(subVarAttrToCopy.getPartNumber());
    command.setNewSpecLink(subVarAttrToCopy.getSpecLink());
    command.setNewDesc(subVarAttrToCopy.getAdditionalInfoDesc());
    command.setNewHiddenFlag(subVarAttrToCopy.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);

    this.childCmdStack.addCommand(command);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() throws CommandException {
    // Undo the child commands
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoDeleteCommand() {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected final void doPostCommit() {
    // iCDM-834
    // Notify icdm hotline ,if value clearance required
    CmdModAttributeValue cmdAttrVal = getCmdAttrVal();
    if ((cmdAttrVal != null) && cmdAttrVal.isClearanceRequired()) {
      cmdAttrVal.sendMailNotification();
    }

    // Refresh the PIDC for changes to reflect in UI
    final TPidcVersion dbPidcVrsn = getEntityProvider().getDbPIDCVersion(this.pidVersion.getID());
    getEntityProvider().refreshCacheObject(dbPidcVrsn);

    final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.variant.getVariantID());
    getEntityProvider().refreshCacheObject(dbVariant);

    if ((getExecutionMode() == EXECUTION_MODE.EXECUTION) || (getExecutionMode() == EXECUTION_MODE.REDO)) {
      final TabvProjectSubVariant dbSubVariant =
          getEntityProvider().getDbPidcSubVariant(this.destSubVariant.getSubVariantID());
      getEntityProvider().refreshCacheObject(dbSubVariant);

    }

  }

  /**
   * @return the new/updated Sub-Variant's ID
   */
  public final Long getSubVariantId() {
    return this.destSubVariant.getSubVariantID();
  }

  // iCDM-535
  /**
   * Get the list of sub variant attributes, whose value already exists when copying
   *
   * @return PIDCAttributeVar list, empty list if all copied
   */
  public final List<PIDCAttributeSubVar> getAttrValueAlreadyExists() {
    return this.valAlreadyExists;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    this.childCmdStack.rollbackAll(getExecutionMode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.destSubVariant == null ? null : this.destSubVariant.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Copy Sub-Variant";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    if (this.commandMode == COMMAND_MODE.INSERT) {
      this.summaryData.setOperation("COPY");
      final TransactionSummaryDetails details = new TransactionSummaryDetails();
      details.setOldValue("");
      details.setNewValue(getPrimaryObjectIdentifier());
      details.setModifiedItem(getPrimaryObjectType());
      detailsList.add(details);
    }


    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objIdentifier;

    if (this.createNewSubVar) {
      objIdentifier =
          "Create sub-variant " + this.attrValName + " from sub-variant " + this.subVarToCopy.getSubVariantName();
    }
    else {
      objIdentifier =
          "Copy attributes to " + this.attrValName + " from sub-variant " + this.subVarToCopy.getSubVariantName();
    }
    return objIdentifier;
  }

}
