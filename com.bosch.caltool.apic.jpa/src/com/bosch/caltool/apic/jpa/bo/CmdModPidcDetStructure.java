/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;


/**
 * @author bru2cob
 */
public class CmdModPidcDetStructure extends AbstractCommand {

  // unique id for pds
  private Long pdsID;

  // unique id of pds to be deleted
  private Long deletedDbpdsID;

  // pds object
  private PIDCDetStructure pds;

  // Level for the node
  private final Long level;

  /**
   * the ID of the new attribute
   */
  protected Long attrID;

  /**
   * the ID of the PIDC where attribute is to be added
   */
  protected Long pidcVrsnID;

  /**
   * Creation of new pidcversion or not
   */
  private final boolean isNewVersion;
  /**
   * PDS Entity ID
   */
  private static final String PDS_ENTITY_ID = "PDS_ENTITY_ID";

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  /**
   * Constant for - Level sort ascending
   */
  private static final int SORT_ORDR_LVL_ASC = 0;
  /**
   * Constant for - Level sort descending
   */
  private static final int SORT_ORDR_LVL_DSC = 1;


  /**
   * @param dataProvider dataprovider
   * @param pidcVer pidCard version
   * @param attribute attribute
   * @param level level
   * @param isDelete flag to indicate to delete
   */
  public CmdModPidcDetStructure(final ApicDataProvider dataProvider, final PIDCVersion pidcVer,
      final Attribute attribute, final long level, final boolean isDelete, final boolean isNewVersion) {
    super(dataProvider);

    this.pidcVrsnID = pidcVer.getID();
    this.attrID = attribute.getAttributeID();
    this.level = level;
    this.isNewVersion = isNewVersion;
    if (isDelete) {
      this.commandMode = COMMAND_MODE.DELETE;
    }
    else {
      this.commandMode = COMMAND_MODE.INSERT;
    }
    getDataCache().getPidcVersion(this.pidcVrsnID).resetNodes();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    final TabvPidcDetStructure dbPds = new TabvPidcDetStructure();

    List<TabvPidcDetStructure> pdsSet =
        getEntityProvider().getDbPIDCVersion(this.pidcVrsnID).getTabvPidcDetStructures();
    // check if any structure attrs are available
    long levelToBeSet;
    if (this.isNewVersion) {
      // if new version is created then just set the level as the level of structure attr being copied from older
      // version
      levelToBeSet = this.level;
    }
    else {
      levelToBeSet = this.level + 1;
    }
    if ((pdsSet == null) || pdsSet.isEmpty()) {
      // Set the values, sets the NEW node
      setValuesToDbEntity(dbPds, levelToBeSet);
    }
    else {
      if (!this.isNewVersion) {
        // Sort the set DESC, to perform the increment in order
        SortedSet<TabvPidcDetStructure> pdsSortedSet = getSortedSet(pdsSet, SORT_ORDR_LVL_DSC);
        // first, check and increment level for all nodes available under this node
        if ((pdsSortedSet != null) && !pdsSortedSet.isEmpty()) {
          for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
            if (tabvPidcDetStructure.getPidAttrLevel() > this.level) {
              // set level for other nodes under this node
              tabvPidcDetStructure.setPidAttrLevel(tabvPidcDetStructure.getPidAttrLevel() + 1);
              // send it to db, such that further updates needs to be done
              getEntityProvider().getEm().flush();
            }
          }
        }
      }
      // Set the values, sets the NEW node
      setValuesToDbEntity(dbPds, levelToBeSet);
    }
  }


  /**
   * @param dbPds
   * @param pidAttrLevel
   */
  private void setValuesToDbEntity(final TabvPidcDetStructure dbPds, final Long pidAttrLevel) {
    dbPds.setTabvAttribute(getEntityProvider().getDbAttribute(this.attrID));
    dbPds.setTPidcVersion(getEntityProvider().getDbPIDCVersion(this.pidcVrsnID));
    dbPds.setPidAttrLevel(pidAttrLevel);

    setUserDetails(COMMAND_MODE.INSERT, dbPds, PDS_ENTITY_ID);

    getEntityProvider().registerNewEntity(dbPds);

    if (this.pds == null) {
      this.pds = new PIDCDetStructure(getDataProvider(), dbPds.getPdsId());
      this.pdsID = this.pds.getID();
    }
    else {
      this.pdsID = dbPds.getPdsId();
    }


    if (getEntityProvider().getDbPIDCVersion(this.pidcVrsnID).getTabvPidcDetStructures() == null) {
      getEntityProvider().getDbPIDCVersion(this.pidcVrsnID)
          .setTabvPidcDetStructures(new ArrayList<TabvPidcDetStructure>());
    }
    getEntityProvider().getDbPIDCVersion(this.pidcVrsnID).getTabvPidcDetStructures().add(dbPds);
    getDataCache().getAllPidcDetStructure().put(this.pdsID, this.pds);

    getEntityProvider().getDbPIDCVersion(this.pidcVrsnID)
        .setTabvPidcDetStructures(getEntityProvider().getDbPIDCVersion(this.pidcVrsnID).getTabvPidcDetStructures());

    // Enable Change notification
    getChangedData().put(dbPds.getPdsId(),
        new ChangedData(ChangeType.INSERT, dbPds.getPdsId(), TabvPidcDetStructure.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {

    final List<TabvPidcDetStructure> pdsSet =
        getEntityProvider().getDbPIDCVersion(this.pidcVrsnID).getTabvPidcDetStructures();
    // additional check if any structure attrs are available, else nothing can be done
    if ((pdsSet == null) || pdsSet.isEmpty()) {
      return;
    }
    // Sort the set ASC, to perform the decrement in order
    SortedSet<TabvPidcDetStructure> pdsSortedSet = getSortedSet(pdsSet, SORT_ORDR_LVL_ASC);
    // Delete from intermediate nodes
    if (this.level > 0) {
      // delete the node
      if (!pdsSortedSet.isEmpty()) {
        deleteNodeFromDB(pdsSortedSet);

        // now, decrement the levels for the nodes under this node

        decrementChildNodeLvls(pdsSortedSet);
      }
    }
    else {
      deleteTopLevelNodes(pdsSortedSet);
    }
    // Enable Change notification
    ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.deletedDbpdsID, TabvPidcDetStructure.class, DisplayEventSource.COMMAND);
    getChangedData().put(this.deletedDbpdsID, chdata);
  }


  /**
   * @param pdsSortedSet
   */
  private void decrementChildNodeLvls(final SortedSet<TabvPidcDetStructure> pdsSortedSet) {
    for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
      if (tabvPidcDetStructure.getPidAttrLevel() > this.level) {
        // decrement the level
        tabvPidcDetStructure.setPidAttrLevel(tabvPidcDetStructure.getPidAttrLevel() - 1);
        // send it to db, such that further updates needs to be done
        getEntityProvider().getEm().flush();
      }
    }
  }


  /**
   * @param pdsSortedSet SortedSet<TabvPidcDetStructure>
   * @throws CommandException
   */
  private void deleteNodeFromDB(final SortedSet<TabvPidcDetStructure> pdsSortedSet) throws CommandException {
    for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
      if (tabvPidcDetStructure.getPidAttrLevel().longValue() == this.level.longValue()) {
        // remove entity, removes the node to be deleted
        removeDbEntity(tabvPidcDetStructure);
        // send it to db, such that further updates needs to be done
        getEntityProvider().getEm().flush();
        break;
      }
    }
  }


  /**
   * @param pdsSortedSet SortedSet<TabvPidcDetStructure>
   * @throws CommandException
   */
  private void deleteTopLevelNodes(final SortedSet<TabvPidcDetStructure> pdsSortedSet) throws CommandException {
    for (TabvPidcDetStructure tabvPidcDetStructure : pdsSortedSet) {
      if (tabvPidcDetStructure.getTabvAttribute().getAttrId() == this.attrID.longValue()) {
        // remove entity, removes the node to be deleted
        removeDbEntity(tabvPidcDetStructure);
        // send it to db, such that further updates needs to be done, if there are any
        getEntityProvider().getEm().flush();
        break;
      }
    }
  }


  /**
   * @param pdsSet
   * @return
   */
  private SortedSet<TabvPidcDetStructure> getSortedSet(final List<TabvPidcDetStructure> pdsSet, final int sortOrder) {
    // Sort the set, to maintain the order when batch update with flushh
    // First, implement a comparator
    Comparator<TabvPidcDetStructure> pdsComparator = new Comparator<TabvPidcDetStructure>() {

      @Override
      public int compare(final TabvPidcDetStructure pds1, final TabvPidcDetStructure pds2) {
        // if order is desceding
        if (sortOrder == SORT_ORDR_LVL_DSC) {
          return pds2.getPidAttrLevel().compareTo(pds1.getPidAttrLevel());
        }
        // default by asc
        return pds1.getPidAttrLevel().compareTo(pds2.getPidAttrLevel());
      }
    };
    // Create sorted set with the comparator
    SortedSet<TabvPidcDetStructure> pdsSortedSet = new TreeSet<TabvPidcDetStructure>(pdsComparator);
    pdsSortedSet.addAll(pdsSet);

    return pdsSortedSet;
  }


  /**
   * @param dbPds
   * @throws CommandException
   */
  private void removeDbEntity(final TabvPidcDetStructure dbPds) throws CommandException {
    validateStaleData(dbPds);
    getEntityProvider().deleteEntity(dbPds);
    // set the deleted id
    this.deletedDbpdsID = dbPds.getPdsId();
    // add it to collections
    getEntityProvider().getDbPIDCVersion(this.pidcVrsnID).getTabvPidcDetStructures().remove(dbPds);
    setUserDetails(COMMAND_MODE.DELETE, dbPds, PDS_ENTITY_ID);
    getDataCache().getAllPidcDetStructure().remove(dbPds.getPdsId());
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
  protected void undoDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Not applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // TODO Auto-generated method stub
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    String objectIdentifier = "";

    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = caseCmdIns(objectIdentifier);
        break;
      case DELETE:
        objectIdentifier = caseCmdDel(objectIdentifier);
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }


    return super.getString("", objectIdentifier);

  }


  /**
   * @param objectIdentifier
   * @return
   */
  private String caseCmdDel(String objectIdentifier) {
    TabvPidcDetStructure dbDeletedPds = getEntityProvider().getDbPidcDetStructure(this.deletedDbpdsID);
    if (dbDeletedPds != null) {
      if (getDataProvider().getLanguage().equals(Language.ENGLISH)) {
        objectIdentifier = STR_WITH_NAME + dbDeletedPds.getTabvAttribute().getAttrNameEng();
      }
      else {
        objectIdentifier = STR_WITH_NAME + dbDeletedPds.getTabvAttribute().getAttrNameGer();
      }
    }
    return objectIdentifier;
  }


  /**
   * @param objectIdentifier
   * @return
   */
  private String caseCmdIns(String objectIdentifier) {
    TabvPidcDetStructure dbPds = getEntityProvider().getDbPidcDetStructure(this.pdsID);
    if (dbPds != null) {
      if (getDataProvider().getLanguage().equals(Language.ENGLISH)) {
        objectIdentifier = STR_WITH_NAME + dbPds.getTabvAttribute().getAttrNameEng();
      }
      else {
        objectIdentifier = STR_WITH_NAME + dbPds.getTabvAttribute().getAttrNameGer();
      }
    }
    return objectIdentifier;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // Not applicable
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
  public Long getPrimaryObjectID() {
    return this.pdsID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "PIDC Virtual Structure";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    // TODO Auto-generated method stub
    return null;
  }

}
