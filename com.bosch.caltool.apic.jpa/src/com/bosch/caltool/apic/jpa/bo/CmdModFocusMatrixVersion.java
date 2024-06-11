/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.apic.jpa.bo.FocusMatrixVersion.FM_REVIEW_STATUS;
import com.bosch.caltool.apic.jpa.bo.FocusMatrixVersion.FM_VERS_STATUS;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;


/**
 * CmdModFocusMatrixReview - Command handles db operations like INSERT, UPDATE on T_FOCUS_MATRIX_REVIEW
 *
 * @author dmo5cob
 */
// ICDM-2569
public class CmdModFocusMatrixVersion extends AbstractCommand {


  /**
   * Entity id
   */
  private static final String FOCUS_MATRIX_VERS_ENTITY_ID = "FOCUS_MATRIX_VERS_ENTITY_ID";

  /**
   * Old Reviewed By
   */
  private ApicUser oldRvwedBy;
  /**
   * New Reviewed By
   */
  private ApicUser newRvwedBy;
  /**
   * new Reviewed on
   */
  private Calendar newRvwedOn;
  /**
   * old Reviewed on
   */
  private Calendar oldRvwedOn;
  /**
   * old link string
   */
  private String oldLink;

  /**
   * new link string
   */
  private String newLink;
  /**
   * old remark
   */
  private String oldRemark;

  /**
   * new remark
   */
  private String newRemark;
  /**
   * old status
   */
  private FocusMatrixVersion.FM_REVIEW_STATUS oldRvwStatus;

  /**
   * new status
   */
  private FocusMatrixVersion.FM_REVIEW_STATUS newRvwStatus;
  /**
   * old status
   */
  private FocusMatrixVersion.FM_VERS_STATUS oldVersStatus;

  /**
   * new status
   */
  private FocusMatrixVersion.FM_VERS_STATUS newVersStatus;
  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * FM version being maintained
   */
  private FocusMatrixVersion fmVersion;
  /**
   * PIDC Version
   */
  private final PIDCVersion pidcVersion;
  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStk = new ChildCommandStack(this);

  /**
   * Reference FM version, if this FM version is created from a copy of another FM version
   * <p>
   * Create FM version while create a new PIDC version for an existing PIDC
   */
  private final FocusMatrixVersion refFmVersion;

  /**
   * Name of FM version
   */
  private String name;


  /**
   * Create a new FM version.
   * <p>
   * a) For a new revision to be created for a PIDC Version, created for a new PIDC, provide <code>null</code> to
   * <code>refFmVersion</code> <br>
   * b) For new PIDC Version from another PIDC Version, for same PIDC, provide <code>refFmVersion</code> as the active
   * FM version of the 'reference' PIDC Version<br>
   * c) For a new FM revision in an existing PIDC, , provide <code>refFmVersion</code> as the active FM version of the
   * 'same' PIDC Version<br>
   *
   * @param pidcVersion pidc version, below which FM version to be created
   * @param refFmVersion reference FM version
   */
  public CmdModFocusMatrixVersion(final PIDCVersion pidcVersion, final FocusMatrixVersion refFmVersion) {
    super(pidcVersion.getDataCache().getDataProvider());

    this.pidcVersion = pidcVersion;
    this.refFmVersion = refFmVersion;
    this.newVersStatus = FM_VERS_STATUS.OLD;

    this.commandMode = COMMAND_MODE.INSERT;

  }

  /**
   * Update FM version
   *
   * @param fmVersion FM version to update
   */
  public CmdModFocusMatrixVersion(final FocusMatrixVersion fmVersion) {
    super(fmVersion.getDataCache().getDataProvider());

    this.fmVersion = fmVersion;
    this.pidcVersion = fmVersion.getPidcVersion();
    this.refFmVersion = null;

    this.commandMode = COMMAND_MODE.UPDATE;
    initialize();


  }

  /**
   * initialise existing values, for udpate mode
   */
  private void initialize() {
    // rvwed by
    this.oldRvwedBy = this.fmVersion.getReviewedBy();
    this.newRvwedBy = this.oldRvwedBy;

    // rvwed on
    this.oldRvwedOn = this.fmVersion.getReviewedOn();
    this.newRvwedOn = this.oldRvwedOn;

    // link
    this.oldLink = this.fmVersion.getLink();
    this.newLink = this.oldLink;

    // remark
    this.oldRemark = this.fmVersion.getRemarks();
    this.newRemark = this.oldRemark;

    // reivew status
    this.oldRvwStatus = this.fmVersion.getReviewStatus();
    this.newRvwStatus = this.oldRvwStatus;

    // Version status
    this.oldVersStatus = this.fmVersion.getVersionStatus();
    this.newVersStatus = this.oldVersStatus;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      getDataCache().addRemoveFocusMatrixVersion(this.fmVersion, true);
      this.pidcVersion.resetFocusMatrixVersionsLoaded();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    TFocusMatrixVersion dbFmVers = new TFocusMatrixVersion();

    long revNumber =
        (this.newVersStatus == FocusMatrixVersion.FM_VERS_STATUS.WORKING_SET) ? 0 : findRevisionNumber() + 1;
    String versName = (this.newVersStatus == FocusMatrixVersion.FM_VERS_STATUS.WORKING_SET) ? "Working Set" : this.name;

    dbFmVers.setName(versName);
    dbFmVers.setRevNumber(revNumber);
    dbFmVers.setStatus(this.newVersStatus.getDbStatus());

    dbFmVers.setTPidcVersion(getEntityProvider().getDbPIDCVersion(this.pidcVersion.getID()));

    if (this.refFmVersion != null) {
      dbFmVers.setLink(this.refFmVersion.getLink());
      dbFmVers.setRemark(this.refFmVersion.getRemarks());
      ApicUser reviewedBy = this.refFmVersion.getReviewedBy();
      TabvApicUser dbApicUser = null;
      if (null != reviewedBy) {
        dbApicUser = getEntityProvider().getDbApicUser(reviewedBy.getID());
      }
      dbFmVers.setReviewedUser(dbApicUser);
      dbFmVers.setReviewedDate((null == this.refFmVersion.getReviewedOn()) ? null
          : ApicUtil.calendarToTimestamp(this.refFmVersion.getReviewedOn()));
      dbFmVers.setRvwStatus(this.refFmVersion.getReviewStatus().getStatusStr());
    }

    // set created date and user
    setUserDetails(COMMAND_MODE.INSERT, dbFmVers, FOCUS_MATRIX_VERS_ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbFmVers);

    getEntityProvider().getDbPIDCVersion(this.pidcVersion.getID()).addTFocusMatrixVersion(dbFmVers);

    this.fmVersion = new FocusMatrixVersion(getDataProvider(), dbFmVers.getFmVersId());

    getChangedData().put(dbFmVers.getFmVersId(), new ChangedData(ChangeType.INSERT, dbFmVers.getFmVersId(),
        TFocusMatrixVersion.class, DisplayEventSource.COMMAND));

    if (this.refFmVersion != null) {
      addFocusMatrixDef();
    }

    if (this.newVersStatus == FM_VERS_STATUS.OLD) {
      addFmVersAttrs();
    }

    this.pidcVersion.resetFocusMatrixVersionsLoaded();
  }

  /**
   * @throws CommandException
   */
  private void addFmVersAttrs() throws CommandException {
    CmdModFocusMatrixVersionAttr cmdFmVersAttr;
    for (PIDCAttribute pidcAttr : this.pidcVersion.getAttributes(false).values()) {
      if (pidcAttr.isFocusMatrixApplicable()) {
        cmdFmVersAttr = new CmdModFocusMatrixVersionAttr(this.fmVersion, pidcAttr);
        this.childCmdStk.addCommand(cmdFmVersAttr);
      }
    }

  }

  /**
   * @return revision number
   */
  private long findRevisionNumber() {
    long maxRev = 0;
    for (Long fmVers : this.pidcVersion.getFocusMatrixVersionMap().keySet()) {
      if (fmVers.longValue() > maxRev) {
        maxRev = fmVers.longValue();
      }
    }
    return maxRev;
  }

  /**
   * @throws CommandException
   */
  private void addFocusMatrixDef() throws CommandException {
    Collection<ConcurrentMap<Long, FocusMatrixDetails>> fmDetailsMap =
        this.refFmVersion.getFocusMatrixItemMap().values();
    if (CommonUtils.isNotEmpty(fmDetailsMap)) {
      for (ConcurrentMap<Long, FocusMatrixDetails> concurrentMap : fmDetailsMap) {
        createFocusMatrixDef(concurrentMap);
      }
    }

  }

  /**
   * @param concurrentMap
   * @throws CommandException
   */
  private void createFocusMatrixDef(final ConcurrentMap<Long, FocusMatrixDetails> concurrentMap)
      throws CommandException {
    for (FocusMatrixDetails fmDetails : concurrentMap.values()) {
      if (!fmDetails.isDeleted()) {
        createFmDef(fmDetails);
      }
    }
  }

  /**
   * @param fmDetails
   * @return command executed
   * @throws CommandException
   */
  private CmdModFocusMatrix createFmDef(final FocusMatrixDetails fmDetails) throws CommandException {
    CmdModFocusMatrix command = new CmdModFocusMatrix(getDataProvider(), this.fmVersion, fmDetails.getUcpaId(),
        fmDetails.getUseCaseId(), fmDetails.getUseCaseSectionId(), fmDetails.getAttributeId(), false);

    if (CommonUtils.isEqual(this.refFmVersion.getPidcVersion(), this.pidcVersion)) {
      command.setColor(fmDetails.getColorCode().getColor());
      command.setComment(fmDetails.getComments());
    }
    else {
      // if new FM version is created for a new PIDC Version, reset color and comment
      command.setColor(FocusMatrixColorCode.NOT_DEFINED.getColor());
      command.setComment("");
    }
    command.setLink(fmDetails.getLink());
    this.childCmdStk.addCommand(command);
    return command;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    TFocusMatrixVersion dbFmVers = getEntityProvider().getDbFocuMatrixVersion(this.fmVersion.getID());

    final ChangedData chdata = new ChangedData(ChangeType.UPDATE, this.fmVersion.getID(), TFocusMatrixVersion.class,
        DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.fmVersion.getObjectDetails());

    if (isObjectChanged(this.oldRvwedBy, this.newRvwedBy)) {
      dbFmVers
          .setReviewedUser(null == this.newRvwedBy ? null : getEntityProvider().getDbApicUser(this.newRvwedBy.getID()));
    }
    if (isObjectChanged(this.oldRvwedOn, this.newRvwedOn)) {
      dbFmVers.setReviewedDate(null == this.newRvwedOn ? null : ApicUtil.calendarToTimestamp(this.newRvwedOn));
    }
    if (isStringChanged(this.oldLink, this.newLink)) {
      dbFmVers.setLink(this.newLink);
    }
    if (isStringChanged(this.oldRemark, this.newRemark)) {
      dbFmVers.setRemark(this.newRemark);
    }
    if (this.oldRvwStatus != this.newRvwStatus) {
      dbFmVers.setRvwStatus(this.newRvwStatus.getStatusStr());
    }

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, dbFmVers, FOCUS_MATRIX_VERS_ENTITY_ID);


    if ((this.oldRvwStatus != this.newRvwStatus) && (this.newRvwStatus == FM_REVIEW_STATUS.YES)) {
      // Create a version of focus matrix review
      CmdModFocusMatrixVersion cmdFmVers = new CmdModFocusMatrixVersion(this.pidcVersion, this.fmVersion);
      this.childCmdStk.addCommand(cmdFmVers);
    }


    getChangedData().put(dbFmVers.getFmVersId(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not Applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isObjectChanged(this.oldRvwedBy, this.newRvwedBy) || isObjectChanged(this.oldRvwedOn, this.newRvwedOn) ||
        isObjectChanged(this.oldLink, this.newLink) || isObjectChanged(this.oldRemark, this.newRemark) ||
        isObjectChanged(this.oldRvwStatus, this.newRvwStatus);

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
      default:
        // Do nothing
        break;
    }

    this.summaryData.setObjectName(getPrimaryObjectIdentifier());
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);

    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdUpd(final SortedSet<TransactionSummaryDetails> detailsList) {
    addTransactionSummaryDetails(detailsList, this.name, this.name, "Name");
    addTransactionSummaryDetails(detailsList, this.fmVersion.getRevisionNumber().toString(),
        this.fmVersion.getRevisionNumber().toString(), "Revision Number");
    addTransactionSummaryDetails(detailsList, null == this.oldRvwedOn ? "" : this.oldRvwedOn.getTime().toString(),
        null == this.newRvwedOn ? "" : this.newRvwedOn.getTime().toString(), "Reviewed On");

    addTransactionSummaryDetails(detailsList, null == this.oldRvwedBy ? "" : this.oldRvwedBy.getUserName(),
        null == this.newRvwedBy ? "" : this.newRvwedBy.getUserName(), "Reviewed By");
    addTransactionSummaryDetails(detailsList, this.oldLink, this.newLink, "Link");
    addTransactionSummaryDetails(detailsList, this.oldRemark, this.newRemark, "Remarks");
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // No action
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return CommonUtils.isNull(this.fmVersion) ? null : this.fmVersion.getID();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Focus Matrix Version";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return (this.fmVersion == null) ? " INVALID!" : this.fmVersion.getName();
  }


  /**
   * @param rvwBy the rvwBy to set
   */
  public void setReviewedBy(final ApicUser rvwBy) {
    this.newRvwedBy = rvwBy;
  }


  /**
   * @param rvwDate the date to set
   */
  public void setReviewedOn(final Calendar rvwDate) {
    this.newRvwedOn = rvwDate;
  }

  /**
   * @param remarks the remark to set
   */
  public void setRemarks(final String remarks) {
    this.newRemark = remarks;
  }

  /**
   * @param newlinkStr String
   */
  public void setLink(final String newlinkStr) {
    this.newLink = newlinkStr;

  }

  /**
   * @param newStatus new version status
   */
  public void setVersStatus(final FocusMatrixVersion.FM_VERS_STATUS newStatus) {
    this.newVersStatus = newStatus;

  }

  /**
   * @param newStatus String
   */
  public void setReviewStatus(final FocusMatrixVersion.FM_REVIEW_STATUS newStatus) {
    this.newRvwStatus = newStatus;

  }

  /**
   * @return fmVersion created or being used
   */
  public FocusMatrixVersion getFocusMatrixVersion() {
    return this.fmVersion;
  }

  /**
   * Applicable only for insert mode
   *
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }
}
