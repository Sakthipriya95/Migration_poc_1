/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
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
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;


/**
 * CmdModPidcA2l- Command handles all db operations on INSERT, UPDATE, DELETE on usecase
 *
 * @author bru2cob
 */
@Deprecated
public class CmdModPidcA2l extends AbstractCmdModProject {

  /**
   * PidcA2l instance
   */
  private PIDCA2l pidcA2l;

  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * old pidc version
   */
  private final PIDCVersion oldPidcVersion;


  /**
   * new pidc version
   */
  private PIDCVersion newPidcVersion;

  /*
   * old and new values
   */
  private String oldPidcVersionName;
  private String newPidcVersionName;


  /**
   * old vers name
   */
  private String oldSoftwareVersion;
  /**
   * new version name
   */
  private String newSoftwareVersion;
  /**
   * new vers id
   */
  private Long newSoftwareVersId;
  /**
   * new Proj id
   */
  private Long newSoftwareProjId;

  /**
   * Unique entity id for setting user details
   */
  private static final String PIDC_A2L_ENTITY_ID = "PIDC_A2L_ENTITY_ID";
  /**
   * A2l file instance
   */
  private A2LFile a2lFile;

  /**
   * re-map flag
   */
  private Boolean unMapped;
  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStk = new ChildCommandStack(this);

  private String oldOriginalName;
  private String newOriginalName;

  private Timestamp oldOriginalDate;
  private Timestamp newOriginalDate;


  /**
   * Constructor for INSERT command mode
   *
   * @param dataProvider AbstractDataProvider
   * @param pidcVersion version
   * @param a2lFile
   */
  public CmdModPidcA2l(final ApicDataProvider dataProvider, final PIDCVersion pidcVersion, final A2LFile a2lFile) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.oldPidcVersion = pidcVersion;
    this.a2lFile = a2lFile;
    this.attrValName = a2lFile.getA2LFileName();
  }


  /**
   * Create a new command for update/delete records
   *
   * @param dataProvider apic data provider
   * @param pidcA2l
   * @param unMapped
   * @param isDelete update/Delete
   */
  public CmdModPidcA2l(final ApicDataProvider dataProvider, final PIDCA2l pidcA2l, final Boolean unMapped,
      final boolean isDelete) {
    super(dataProvider);
    this.attrValName = pidcA2l.getA2LFileName();
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;
    }
    this.unMapped = unMapped;
    this.pidcA2l = pidcA2l;
    this.oldPidcVersion = this.pidcA2l.getPidcVersion();
    this.oldOriginalName = this.pidcA2l.getVcdmA2lName();
    this.oldOriginalDate =
        this.pidcA2l.getVcdmA2lDate() == null ? null : new Timestamp(this.pidcA2l.getVcdmA2lDate().getTimeInMillis());
    setFieldsToCommand();
  }

  /**
   * @param newPidcVersion the newPidcVersion to set
   */
  public void setNewPidcVersion(final PIDCVersion newPidcVersion) {
    this.newPidcVersion = newPidcVersion;
    this.newPidcVersionName = newPidcVersion.getPidcVersionName();

  }

  /**
   * @param originalFileName String
   */
  public void setNewOriginalName(final String originalFileName) {
    this.newOriginalName = originalFileName;
  }

  /**
   * @param originalDate Timestamp
   */
  public void setNewOriginalDate(final Calendar originalDate) {
    // calendar to timestamp
    if (originalDate != null) {
      this.newOriginalDate = new Timestamp(originalDate.getTimeInMillis());
    }
  }


  /**
   * set old and new values of rule set
   */
  private void setFieldsToCommand() {
    if (null == this.pidcA2l.getPidcVersion()) {
      this.oldPidcVersionName = "";
    }
    else {
      this.oldPidcVersionName = this.pidcA2l.getPidcVersion().getPidcVersionName();
    }
    this.newPidcVersionName = this.oldPidcVersionName;
    this.oldSoftwareVersion = this.pidcA2l.getSsdSoftwareVersion();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return isStringChanged(this.oldPidcVersionName, this.newPidcVersionName) ||
        isStringChanged(this.oldOriginalName, this.newOriginalName) ||
        !CommonUtils.isEqual(this.oldOriginalDate, this.newOriginalDate) ||
        !CommonUtils.isEqual(this.oldSoftwareVersion, this.newSoftwareVersion);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", "for " + getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    TPidcA2l dbPidcA2l = new TPidcA2l();
    setNewPIDCA2lDtls(dbPidcA2l);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbPidcA2l);

    Set<TPidcA2l> tabvPidcA2l =
        getEntityProvider().getDbPIDC(dbPidcA2l.getTabvProjectidcard().getProjectId()).getTabvPidcA2ls();
    if (tabvPidcA2l == null) {
      tabvPidcA2l = new HashSet<>();
    }

    tabvPidcA2l.add(dbPidcA2l);

    TPidcVersion newDbPidcVers = getEntityProvider().getDbPIDCVersion(this.oldPidcVersion.getID());
    if (newDbPidcVers.getTabvPidcA2ls() == null) {
      newDbPidcVers.setTabvPidcA2ls(new ArrayList<TPidcA2l>());
    }
    newDbPidcVers.getTabvPidcA2ls().add(dbPidcA2l);

    this.pidcA2l = new PIDCA2l(getDataProvider(), dbPidcA2l.getPidcA2lId());

    this.a2lFile.setPidcA2l(this.pidcA2l);

    getDataCache().getMappedA2LFilesMap().put(this.pidcA2l.getID(), this.a2lFile);

    getChangedData().put(this.pidcA2l.getID(),
        new ChangedData(ChangeType.INSERT, this.pidcA2l.getID(), TPidcA2l.class, DisplayEventSource.COMMAND));

    // user access rights
    setUserDetails(this.commandMode, dbPidcA2l, PIDC_A2L_ENTITY_ID);

    // ICDM-1585 incrementing PIDCVersion.version column to support webservice call
    CmdModPidcVersion pidcVersCmd = new CmdModPidcVersion(getDataProvider(), this.oldPidcVersion, false);
    pidcVersCmd.setUpdateTimestamp(true);
    this.childCmdStk.addCommand(pidcVersCmd);

  }


  /**
   * {@inheritDoc}
   */
  /**
   * {@inheritDoc}
   */
  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.pidcA2l.getID(), TPidcA2l.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcA2l(this.pidcA2l.getID()).getObjectDetails());
    final TPidcA2l modifiedPidcA2l = getEntityProvider().getDbPIDCA2l(this.pidcA2l.getID());
    validateStaleData(modifiedPidcA2l);
    // Update modified data

    TPidcVersion newPIDCVersion = null;
    // ICDM-1585
    /**
     * Two cases occur for the below if condition 1. When user unmapps the a2l file 2. When user remaps an a2l file from
     * one version to another version
     */
    if (null != this.oldPidcVersion) {
      CmdModPidcVersion oldPidcVersCmd = new CmdModPidcVersion(getDataProvider(), this.oldPidcVersion, false);
      oldPidcVersCmd.setUpdateTimestamp(true);
      this.childCmdStk.addCommand(oldPidcVersCmd);
    }
    if (this.unMapped != null) {// ICDM-1842
      if (this.unMapped) {
        // maps from one version to dummy version
        TPidcVersion existingDbPidcVers =
            getEntityProvider().getDbPIDCVersion(modifiedPidcA2l.getTPidcVersion().getPidcVersId());
        if (existingDbPidcVers.getTabvPidcA2ls() != null) {
          existingDbPidcVers.getTabvPidcA2ls().remove(modifiedPidcA2l);
        }
        modifiedPidcA2l.setTPidcVersion(newPIDCVersion);
        getDataCache().getMappedA2LFilesMap().remove(this.pidcA2l.getID());
      }
      else {
        // remaps from dummy or one version to another version
        if (modifiedPidcA2l.getTPidcVersion() != null) {
          TPidcVersion existingDbPidcVers =
              getEntityProvider().getDbPIDCVersion(modifiedPidcA2l.getTPidcVersion().getPidcVersId());
          if (existingDbPidcVers.getTabvPidcA2ls() != null) {
            existingDbPidcVers.getTabvPidcA2ls().remove(modifiedPidcA2l);
          }
        }
        if (null != this.newPidcVersion) {
          newPIDCVersion = getEntityProvider().getDbPIDCVersion(this.newPidcVersion.getID());

          modifiedPidcA2l.setTPidcVersion(newPIDCVersion);

          if (newPIDCVersion.getTabvPidcA2ls() == null) {
            newPIDCVersion.setTabvPidcA2ls(new ArrayList<TPidcA2l>());
          }
          newPIDCVersion.getTabvPidcA2ls().add(modifiedPidcA2l);

          CmdModPidcVersion newPidcVersCmd = new CmdModPidcVersion(getDataProvider(), this.newPidcVersion, false);
          newPidcVersCmd.setUpdateTimestamp(true);
          this.childCmdStk.addCommand(newPidcVersCmd);
        }
      }
    }
    // ICDM-1842
    modifiedPidcA2l.setVcdmA2lName(this.newOriginalName);
    modifiedPidcA2l.setVcdmA2lDate(this.newOriginalDate);


    modifiedPidcA2l.setSsdSoftwareProjID(this.newSoftwareProjId);
    modifiedPidcA2l.setSsdSoftwareVersion(this.newSoftwareVersion);
    modifiedPidcA2l.setSsdSoftwareVersionID(this.newSoftwareVersId);


    setUserDetails(this.commandMode, modifiedPidcA2l, PIDC_A2L_ENTITY_ID);
    getChangedData().put(this.pidcA2l.getID(), chdata);


  }

  /**
   * @param modifiedPidcA2l
   */
  private void setNewPIDCA2lDtls(final TPidcA2l dbPidcA2l) {
    dbPidcA2l.setMvTa2lFileinfo(getEntityProvider().getDbA2LFileInfo(this.a2lFile.getID()));
    dbPidcA2l.setTabvProjectidcard(getEntityProvider().getDbPIDC(this.oldPidcVersion.getPidc().getID()));
    dbPidcA2l.setTPidcVersion(getEntityProvider().getDbPIDCVersion(this.oldPidcVersion.getID()));
    dbPidcA2l.setSdomPverName(this.a2lFile.getSdomPverName());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.pidcA2l.getID(), TPidcA2l.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(getDataCache().getPidcA2l(this.pidcA2l.getID()).getObjectDetails());
    final TPidcA2l deletedPidcA2l = getEntityProvider().getDbPIDCA2l(this.pidcA2l.getID());

    setUserDetails(COMMAND_MODE.DELETE, deletedPidcA2l, PIDC_A2L_ENTITY_ID);

    validateStaleData(deletedPidcA2l);

    Long pidcId = this.pidcA2l.getPIDCard().getID();
    getEntityProvider().getDbPIDC(pidcId).getTabvPidcA2ls().remove(deletedPidcA2l);

    if (deletedPidcA2l.getTPidcVersion() != null) {
      getEntityProvider().getDbPIDCVersion(deletedPidcA2l.getTPidcVersion().getPidcVersId()).getTabvPidcA2ls()
          .remove(deletedPidcA2l);
    }

    getEntityProvider().deleteEntity(deletedPidcA2l);

    getDataCache().getMappedA2LFilesMap().remove(this.pidcA2l.getID());
    getDataCache().getAllPidcA2lMap().remove(this.pidcA2l.getID());

    getChangedData().put(this.pidcA2l.getID(), chdata);

    CmdModPidcVersion oldPidcVersCmd = new CmdModPidcVersion(getDataProvider(), this.oldPidcVersion, false);
    oldPidcVersCmd.setUpdateTimestamp(true);
    this.childCmdStk.addCommand(oldPidcVersCmd);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        cmdModIns(detailsList);

        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldPidcVersionName, this.newPidcVersionName,
            getPrimaryObjectType());
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
  private void cmdModIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(this.pidcA2l.getPidcVersion().getPidcVersionName());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
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
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return null == this.pidcA2l ? null : this.pidcA2l.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "A2L Mapping";
  }


  /**
   * @return the newSoftwareVersion
   */
  public String getNewSoftwareVersion() {
    return this.newSoftwareVersion;
  }


  /**
   * @param newSoftwareVersion the newSoftwareVersion to set
   */
  public void setNewSoftwareVersion(final String newSoftwareVersion) {
    this.newSoftwareVersion = newSoftwareVersion;
  }


  /**
   * @param newSoftwareVersId the newSoftwareVersId to set
   */
  public void setNewSoftwareVersId(final Long newSoftwareVersId) {
    this.newSoftwareVersId = newSoftwareVersId;
  }


  /**
   * @param newSoftwareProjId the newSoftwareProjId to set
   */
  public void setNewSoftwareProjId(final Long newSoftwareProjId) {
    this.newSoftwareProjId = newSoftwareProjId;
  }


}
