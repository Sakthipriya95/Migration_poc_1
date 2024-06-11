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
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Command to create a new PIDC by copying the details of another PIDC
 *
 * @author bne4cob
 */
public class CmdCopyPidc extends AbstractCmdModProject {

  /**
   * PIDC version to copy
   */
  private final PIDCVersion pidcVersToCopy;

  /**
   * Leaf node of the new PIDC
   */
  private final PIDCNode pidcNode;

  /**
   * New PIDC version
   */
  private PIDCVersion newPidcVersion;

  /**
   * DB Entity of new PIDC
   */
  // icdm-229
  private Long newDbPidcId;
  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * icdm-1449 version details
   */
  /**
   * CmdModPidCard instance
   */
  private final CmdModPidCard pidcCommand;

  /**
   * ICDM-2437 selected uc items from the wizard
   */
  private List<AbstractUseCaseItem> selectedUCItems;

  /**
   * Constructor
   *
   * @param dataProvider the data provider
   * @param pidcVers the PIDC version to copy
   * @param pidcNode the node to which the new PIDC is to be placed
   */
  public CmdCopyPidc(final ApicDataProvider dataProvider, final PIDCVersion pidcVers, final PIDCNode pidcNode) {
    super(dataProvider);
    this.pidcVersToCopy = pidcVers;
    this.pidcNode = pidcNode;
    this.commandMode = COMMAND_MODE.INSERT;
    this.pidcCommand = new CmdModPidCard(getDataProvider(), this.pidcNode);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    this.childCmdStack.clear();

    createPidcAndVersion();

    for (IPIDCAttribute pidcAttr : this.pidcVersToCopy.getAttributes().values()) {
      if (isValidProjAttr(pidcAttr)) {
        createProjectAttr(pidcAttr);
      }
    }
  }

  /**
   * Checks whether thie project attribute can be copied to the new PIDC
   *
   * @param pidcAttr the PIDC attribute to copy
   * @return true/false
   */
  private boolean isValidProjAttr(final IPIDCAttribute pidcAttr) {

    // Exclude undefined attributes and attributes defined at variant level
    if ((pidcAttr.getID() == null) || ApicConstants.USED_NOTDEF_DISPLAY.equals(pidcAttr.getIsUsed()) ||
        pidcAttr.isVariant()) {
      return false;
    }

    final int attrLevel = pidcAttr.getAttribute().getAttrLevel();

    // Ignore structure attributes, they are already created
    if (attrLevel > 0) {
      return false;
    }

    // Ignore name, variant attributes
    if ((attrLevel == ApicConstants.PROJECT_NAME_ATTR) || (attrLevel == ApicConstants.VARIANT_CODE_ATTR) ||
        (attrLevel == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
      return false;
    }
    // If the source pidc attr is not readable and hidden then do not copy the attr
    if (!pidcAttr.isReadable() && pidcAttr.isHidden()) {
      return false;
    }
    return true;
  }

  /**
   * Create the PIDC
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createPidcAndVersion() throws CommandException {

    if (this.nameValue == null) {
      this.pidcCommand.setNameEng(this.newNameEng);
      this.pidcCommand.setNameGer(this.newNameGer);
      this.pidcCommand.setDescEng(this.newDescEng);
      this.pidcCommand.setDescGer(this.newDescGer);
      // ICDM-2437
      this.pidcCommand.setProjUcItems(this.selectedUCItems);
    }
    else {
      this.pidcCommand.setNameValue(this.nameValue);
    }
    // Create a PIDC command
    this.childCmdStack.addCommand(this.pidcCommand);

    this.newPidcVersion = getDataCache().getPidcVersion(this.pidcCommand.getPidcVrsnCmd().getPrimaryObjectID());
    this.attrValName = this.newPidcVersion.getPidc().getName();
    // icdm-229
    this.newDbPidcId = this.pidcCommand.getDbPidc().getProjectId();
  }

  /**
   * Create the PIDC attributes
   *
   * @param pidcAttrToCopy pidc attribute to copy
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createProjectAttr(final IPIDCAttribute pidcAttrToCopy) throws CommandException {
    final PIDCAttribute pidcAttr =
        new PIDCAttribute(getDataProvider(), null, pidcAttrToCopy.attrID, this.newPidcVersion.getID());

    final CmdModProjectAttr cmdProjAttr = new CmdModProjectAttr(getDataProvider(), pidcAttr);


    cmdProjAttr.setUsed(pidcAttrToCopy.getIsUsed());
    cmdProjAttr.setValue(pidcAttrToCopy.getAttributeValue());
    cmdProjAttr.setProRevId(this.newPidcVersion.getPidcRevision());
    // ICDM-260
    cmdProjAttr.setNewPartNumber(pidcAttrToCopy.getPartNumber());
    cmdProjAttr.setNewSpecLink(pidcAttrToCopy.getSpecLink());
    cmdProjAttr.setNewDesc(pidcAttrToCopy.getAdditionalInfoDesc());
    cmdProjAttr.setNewHiddenFlag(pidcAttrToCopy.isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);
    cmdProjAttr.setNewTrnfrVcdmFlag(pidcAttrToCopy.canTransferToVcdm() ? ApicConstants.YES : ApicConstants.CODE_NO);
    cmdProjAttr
        .setNewFMRelevantFlag(pidcAttrToCopy.isFocusMatrixApplicable() ? ApicConstants.YES : ApicConstants.CODE_NO);

    this.childCmdStack.addCommand(cmdProjAttr);
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
    // Delete the project attributes and project
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
    if ((getExecutionMode() == EXECUTION_MODE.EXECUTION) || (getExecutionMode() == EXECUTION_MODE.REDO)) {
      // icdm-229
      final TabvProjectidcard dbPidc = getEntityProvider().getDbPIDC(this.newDbPidcId);
      getEntityProvider().refreshCacheObject(dbPidc);
    }
  }

  /**
   * @return the new PIDC Version's ID
   */
  public final Long getPidcVersID() {
    return this.newPidcVersion.getID();
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
    // Only Insert mode is present, and the PIDC is being inserted
    return this.newPidcVersion == null ? null : this.newPidcVersion.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Copy Project ID Card";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    // Pmd changes made for Violation 1 and Violation 2
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
    return "as " + this.attrValName + " from " + this.pidcVersToCopy.getName();
  }


  /**
   * @return the pidcCommand
   */
  public CmdModPidCard getPidcCommand() {
    return this.pidcCommand;
  }

  /**
   * @param selectedUCItems
   */
  public void setProjUcItems(final List<AbstractUseCaseItem> selectedUCItems) {
    // Task 290992 : Mutable members should not be stored or returned directly
    if (null != selectedUCItems) {
      this.selectedUCItems = new ArrayList<AbstractUseCaseItem>(selectedUCItems);
    }
  }

}
