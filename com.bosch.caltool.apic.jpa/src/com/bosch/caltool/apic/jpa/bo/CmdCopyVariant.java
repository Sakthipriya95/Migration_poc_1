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
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Command to create a new PIDC by copying the details of another PIDC
 *
 * @author bne4cob
 */
public class CmdCopyVariant extends AbstractCmdModProject { // NOPMD by BNE4COB on 6/21/13 6:05 PM

  /**
   * PIDC Variant to copy
   */
  private final PIDCVariant varToCopy;

  /**
   * Destination variant, if the variant is to be copied to another variant/variant is the new variant
   */
  private PIDCVariant destVariant;

  /**
   * Whether this command is to be used for creating a new variant or not
   */
  private boolean createNewVar = true;

  /**
   * PID Card of the variant(s) under operation
   */
  private final PIDCVersion pidVersion;

  /**
   * List of var attr value already exists while copying
   */
  private List<PIDCAttributeVar> valAlreadyExists;

  /**
   * Transaction summary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Constructor, to be used, if only the attributes are to be copied from one variant to another.
   *
   * @param dataProvider the data provider
   * @param source the Variant to copy
   * @param destination the variant to which the attributes are copied
   */
  public CmdCopyVariant(final ApicDataProvider dataProvider, final PIDCVariant source, final PIDCVariant destination) {
    this(dataProvider, source);
    this.destVariant = destination;

    this.createNewVar = false;
  }

  /**
   * Constructor, to be used, if a new variant is to be created.
   *
   * @param dataProvider the data provider
   * @param source the Variant to copy
   */
  public CmdCopyVariant(final ApicDataProvider dataProvider, final PIDCVariant source) {
    super(dataProvider);
    this.varToCopy = source;
    this.commandMode = COMMAND_MODE.INSERT;
    this.pidVersion = source.getPidcVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    this.childCmdStack.clear();

    if (this.createNewVar) {
      createVariant();
    }
    this.attrValName = this.destVariant.getVariantName();
    this.valAlreadyExists = new ArrayList<PIDCAttributeVar>();
    for (IPIDCAttribute pidcAttr : this.varToCopy.getAttributes().values()) {
      final PIDCAttributeVar varAttrToCopy = (PIDCAttributeVar) pidcAttr;
      if (!isValidVarAttr(varAttrToCopy)) {
        continue;
      }
      // ICDM-1780
      final PIDCAttributeVar varAttrToUpdate = this.destVariant.getAllVarAttribute(varAttrToCopy.attrID);

      createOrUpdateVarAttr(varAttrToCopy, varAttrToUpdate);
    }
  }

  /**
   * @param varAttrToCopy
   * @param varAttrToUpdate
   * @throws CommandException
   */
  private void createOrUpdateVarAttr(final PIDCAttributeVar varAttrToCopy, final PIDCAttributeVar varAttrToUpdate)
      throws CommandException {
    if (this.createNewVar || (varAttrToUpdate == null) || (varAttrToUpdate.getAttributeValue() == null) ||
        (varAttrToUpdate.getAttributeValue() != null)) {
      if (varAttrToUpdate == null) {
        createVariantAttr(varAttrToCopy);
      }
      else {
        if (varAttrToUpdate.getAttributeValue() == null) {
          updateVariantAttr(varAttrToCopy, varAttrToUpdate);
        }
        else {
          this.valAlreadyExists.add(varAttrToUpdate);
        }
      }
    }
  }

  /**
   * Update the variant attribute
   *
   * @param varAttrToCopy reference attribute
   * @param varAttrToUpdate attribute to update
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void updateVariantAttr(final PIDCAttributeVar varAttrToCopy, final PIDCAttributeVar varAttrToUpdate)
      throws CommandException {
    final CmdModProjVariantAttr command = new CmdModProjVariantAttr(getDataProvider(), varAttrToUpdate);
    command.setUsed(varAttrToCopy.getIsUsed());
    command.setValue(varAttrToCopy.getAttributeValue());
    // ICDM-260
    command.setNewPartNumber(varAttrToCopy.getPartNumber());
    command.setNewSpecLink(varAttrToCopy.getSpecLink());
    command.setNewDesc(varAttrToCopy.getAdditionalInfoDesc());
    command.setNewHiddenFlag(varAttrToCopy.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);

    // Disable stale data validation as the variant attribute is created in this session
    command.setStaleDataValidate(false);

    this.childCmdStack.addCommand(command);

  }

  /**
   * Checks whether the Variant attribute can be copied to the new Variant
   *
   * @param varAttrToCopy the Variant attribute to copy
   * @return true/false
   */
  private boolean isValidVarAttr(final PIDCAttributeVar varAttrToCopy) {

    if (varAttrToCopy.isVariant()) {
      return false;
    }

    final int attrLevel = varAttrToCopy.getAttribute().getAttrLevel();
    // Ignore name, variant attributes
    if (attrLevel == ApicConstants.VARIANT_CODE_ATTR) {
      return false;
    }

    return true;
  }

  /**
   * Create the Variant
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createVariant() throws CommandException {
    final CmdModPidcVariant command = new CmdModPidcVariant(getDataProvider(), this.pidVersion, false, null);
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

    this.destVariant = getDataProvider().getPidcVaraint(command.getNewVariantID());
    this.attrValName = this.destVariant.getVariantName();

  }

  /**
   * Create the PIDC Variant attribute
   *
   * @param varAttrToCopy Variant attribute to copy
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createVariantAttr(final IPIDCAttribute varAttrToCopy) throws CommandException {
    final PIDCAttributeVar varAttr =
        new PIDCAttributeVar(getDataProvider(), varAttrToCopy.attrID, this.destVariant.getVariantID());

    final CmdModProjVariantAttr command = new CmdModProjVariantAttr(getDataProvider(), varAttr);
    command.setUsed(varAttrToCopy.getIsUsed());
    command.setValue(varAttrToCopy.getAttributeValue());
    command.setProRevId(this.destVariant.getRevision());
    // ICDM-260
    command.setNewPartNumber(varAttrToCopy.getPartNumber());
    command.setNewSpecLink(varAttrToCopy.getSpecLink());
    command.setNewDesc(varAttrToCopy.getAdditionalInfoDesc());
    command.setNewHiddenFlag(varAttrToCopy.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);

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


    if ((getExecutionMode() == EXECUTION_MODE.EXECUTION) || (getExecutionMode() == EXECUTION_MODE.REDO)) {
      final TabvProjectVariant dbVariant = getEntityProvider().getDbPidcVariant(this.destVariant.getVariantID());
      getEntityProvider().refreshCacheObject(dbVariant);
    }

  }

  /**
   * @return the new/updated Variant's ID
   */
  public final Long getVariantId() {
    return this.destVariant.getVariantID();
  }

  // iCDM-535
  /**
   * Get the list of variant attributes, whose value already exists when copying
   *
   * @return PIDCAttributeVar list, empty list if all copied
   */
  public final List<PIDCAttributeVar> getAttrValueAlreadyExists() {
    return this.valAlreadyExists;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    // Not Implemented
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.destVariant == null ? null : this.destVariant.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Copy Variant";
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

    if (this.createNewVar) {
      objIdentifier = "Create variant " + this.attrValName + " from variant " + this.varToCopy.getVariantName();
    }
    else {
      objIdentifier = "Copy attributes to " + this.attrValName + " from variant " + this.varToCopy.getVariantName();
    }
    return objIdentifier;
  }

}
