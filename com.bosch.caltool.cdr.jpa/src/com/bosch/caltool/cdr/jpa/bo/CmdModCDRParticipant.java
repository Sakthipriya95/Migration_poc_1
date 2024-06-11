/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParticipant;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;


/**
 * Command to create Review participants (calibration engineer, auditor, other participants)
 *
 * @author bne4cob
 */
public class CmdModCDRParticipant extends AbstractCDRCommand {


  /**
   * Parent review result
   */
  private final CDRResult reviewResult;

  /**
   * Participant object
   */
  private CDRParticipant participant;

  /**
   * participant user
   */
  private final ApicUser user;

  /**
   * type of participation
   */
  final private CDRConstants.REVIEW_USER_TYPE participationType;

  private CDRParticipant cdrParticipant;

  // old apic user
  private ApicUser oldUser;

  /**
   * Review function entity ID
   */
  private static final String ENTITY_ID = "RVW_USR";

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * @param dataProvider data provider
   * @param reviewResult review result parent
   * @param user participant user
   * @param partType type of participation
   */
  public CmdModCDRParticipant(final CDRDataProvider dataProvider, final CDRResult reviewResult, final ApicUser user,
      final CDRConstants.REVIEW_USER_TYPE partType) {

    super(dataProvider);

    this.commandMode = COMMAND_MODE.INSERT;

    this.reviewResult = reviewResult;
    this.user = user;
    this.participationType = partType;
  }

  /**
   * @param dataProvider data provider
   * @param reviewResult review result parent
   * @param cdrParticipant CDRParticipant
   * @param user participant user
   * @param isDelete boolean that tells whether its delete or update
   */
  public CmdModCDRParticipant(final CDRDataProvider dataProvider, final CDRResult reviewResult,
      final CDRParticipant cdrParticipant, final ApicUser user, final boolean isDelete) {

    super(dataProvider);

    if (!isDelete) {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    else {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    this.reviewResult = reviewResult;
    this.cdrParticipant = cdrParticipant;
    this.oldUser = cdrParticipant.getUser();
    this.user = user;
    this.participationType = cdrParticipant.getParticipationType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // Remove the object from the cache
    if ((this.commandMode == COMMAND_MODE.INSERT) && (this.participant != null)) {
      getDataCache().getAllCDRParticipants().remove(this.participant);
      this.reviewResult.getParticipantMap().remove(this.participant.getID());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    final TRvwResult dbResult = getEntityProvider().getDbCDRResult(this.reviewResult.getID());

    // Create the review participant entity
    final TRvwParticipant dbRvwPart = new TRvwParticipant();

    dbRvwPart.setTRvwResult(dbResult);
    dbRvwPart.setTabvApicUser(getEntityProvider().getDbApicUser(this.user.getID()));
    dbRvwPart.setActivityType(this.participationType.getDbType());
    setUserDetails(COMMAND_MODE.INSERT, dbRvwPart, ENTITY_ID);

    getEntityProvider().registerNewEntity(dbRvwPart);

    // Add this participant to the review result
    Set<TRvwParticipant> rvwUsrSet =
        getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParticipants();
    if (rvwUsrSet == null) {
      rvwUsrSet = new HashSet<TRvwParticipant>();
    }
    rvwUsrSet.add(dbRvwPart);

    this.participant = new CDRParticipant(getDataProvider(), dbRvwPart.getParticipantId());
    this.reviewResult.getParticipantMap().put(this.participant.getID(), this.participant);

    Set<TRvwParticipant> tRvwParticipants =
        getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParticipants();
    if (tRvwParticipants == null) {
      tRvwParticipants = new HashSet<TRvwParticipant>();
    }
    tRvwParticipants.add(dbRvwPart);

    getEntityProvider().getDbCDRResult(this.reviewResult.getID()).setTRvwParticipants(tRvwParticipants);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    TRvwParticipant dbCDRParticipant = getEntityProvider().getDbCDRParticipant(this.cdrParticipant.getID());
    validateStaleData(dbCDRParticipant);
    dbCDRParticipant.setTabvApicUser(getEntityProvider().getDbApicUser(this.user.getID()));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    TRvwParticipant dbCDRParticipant = getEntityProvider().getDbCDRParticipant(this.cdrParticipant.getID());
    final Set<TRvwParticipant> rvwUsrSet =
        getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParticipants();
    if (rvwUsrSet != null) {
      rvwUsrSet.remove(dbCDRParticipant);
    }

    getEntityProvider().deleteEntity(dbCDRParticipant);

    // Remove the object from the cache
    getDataCache().getAllCDRParticipants().remove(this.cdrParticipant);
    this.reviewResult.getParticipantMap().remove(this.cdrParticipant.getID());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    final TRvwParticipant dbPart = getEntityProvider().getDbCDRParticipant(this.participant.getID());
    // Remove the participant from the review
    final Set<TRvwParticipant> rvwUsrSet =
        getEntityProvider().getDbCDRResult(this.reviewResult.getID()).getTRvwParticipants();
    if (rvwUsrSet != null) {
      rvwUsrSet.remove(dbPart);
    }

    getEntityProvider().deleteEntity(dbPart);

    // Remove the object from the cache
    getDataCache().getAllCDRParticipants().remove(this.participant);
    this.reviewResult.getParticipantMap().remove(this.participant.getID());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // No implementation required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // No implementation required

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    if (this.commandMode == COMMAND_MODE.UPDATE) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("",
        ": type '" + this.participationType.getDbType() + "', User '" + getPrimaryObjectIdentifier() + "'");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // No implementation required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.participant == null ? null : this.participant.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Review participant";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // ICDM-723
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdIns(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldUser.getFullName(), this.user.getFullName(),
            this.participationType.toString());
        break;
      case DELETE:
        // no details section necessary in case of delete (parent row is sufficient in transansions view)
        addTransactionSummaryDetails(detailsList, getPrimaryObjectIdentifier(), "", this.participationType.toString());
        break;
      default:
        // Do nothing
        break;
    }
    this.summaryData.setObjectName("Participant");
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(this.participationType.toString());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String returnStr;
    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
        returnStr = this.user.getFullName();
        break;
      case DELETE:
        returnStr = this.oldUser.getFullName();
        break;
      default:
        returnStr = "";
    }
    return returnStr;

  }

}
