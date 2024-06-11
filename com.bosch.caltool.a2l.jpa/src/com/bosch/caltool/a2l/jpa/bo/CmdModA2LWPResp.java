/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;

/**
 * @author mkl2cob
 */
public class CmdModA2LWPResp extends AbstractA2LCommand {

  private static final String ENTITY_ID = "A2L_WP_RESP_ENT_ID";

  /**
   * A2LResponsibility
   */
  private A2LResponsibility a2lResp;


  /**
   * workpackage id
   */
  private Long wrkPkgId;

  /**
   * a2l group id
   */
  private Long a2lGroupId;

  /**
   * TA2lWpResp
   */
  private TA2lWpResp dbA2LWpResp;

  /**
   * A2LWpResponsibility
   */
  private A2LWpResponsibility a2lWpResp;

  /**
   * new WPResponsibility
   */
  private final WPResponsibility newWpResp;

  private WPResponsibility oldWpResp;
  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor for INSERT
   *
   * @param dataProvider AbstractDataProvider
   * @param a2lResp A2LResponsibility
   * @param wrkPkgId Long
   * @param a2lGroupId Long
   * @param wpResp WPResponsibility
   */
  public CmdModA2LWPResp(final AbstractDataProvider dataProvider, final A2LResponsibility a2lResp, final Long wrkPkgId,
      final Long a2lGroupId, final WPResponsibility wpResp) {
    super(dataProvider);
    this.a2lResp = a2lResp;
    this.wrkPkgId = wrkPkgId;
    this.a2lGroupId = a2lGroupId;
    this.newWpResp = wpResp;


    this.commandMode = COMMAND_MODE.INSERT;
  }

  /**
   * Constructor for UPDATE
   *
   * @param dataProvider AbstractDataProvider
   * @param wpResp WPResponsibility
   * @param a2lWpResp A2LWpResponsibility
   */
  public CmdModA2LWPResp(final AbstractDataProvider dataProvider, final WPResponsibility wpResp,
      final A2LWpResponsibility a2lWpResp) {
    super(dataProvider);
    this.a2lWpResp = a2lWpResp;
    this.oldWpResp = a2lWpResp.getResponbility();
    this.newWpResp = wpResp;
    this.commandMode = COMMAND_MODE.UPDATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    if (this.commandMode == COMMAND_MODE.INSERT) {
      // remove from Data cache
      Set<A2LWpResponsibility> a2lWpRespSet = getDataCache().getA2lWpRespMap().get(this.a2lResp.getID());
      if (null != a2lWpRespSet) {
        a2lWpRespSet.remove(this.a2lWpResp);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    this.dbA2LWpResp = new TA2lWpResp();
    this.dbA2LWpResp.setTA2lResp(getEntityProvider().getDbA2lResp(this.a2lResp.getID()));
    this.dbA2LWpResp.setTWpResp(getEntityProvider().getDbWpResp(this.newWpResp.getID()));
    if (this.a2lGroupId == null) {
      // workpackage
      this.dbA2LWpResp.setTWorkpackage(getEntityProvider().getDbIcdmWorkPackage(this.wrkPkgId));
    }
    else {
      // group
      this.dbA2LWpResp.setTA2lGroup(getEntityProvider().getDbA2lGrp(this.a2lGroupId));
    }

    // set the CreatedUser / date information
    setUserDetails(COMMAND_MODE.INSERT, this.dbA2LWpResp, ENTITY_ID);
    // register the new Entity in the EntityManager to get the ID
    getEntityProvider().registerNewEntity(this.dbA2LWpResp);

    this.a2lWpResp = new A2LWpResponsibility(getDataProvider(), this.dbA2LWpResp.getA2lWpRespId());
    // add the new object to cache
    SortedSet<A2LWpResponsibility> a2lWpRespSet = getDataCache().getA2lWpRespMap().get(this.a2lResp.getID());
    if (null == a2lWpRespSet) {
      a2lWpRespSet = new TreeSet<>();
      getDataCache().getA2lWpRespMap().put(this.a2lResp.getID(), a2lWpRespSet);
    }
    a2lWpRespSet.add(this.a2lWpResp);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    final ChangedData chdata =
        new ChangedData(ChangeType.UPDATE, this.a2lWpResp.getID(), TA2lWpResp.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.a2lWpResp.getObjectDetails());

    // Check for any parallel changes
    final TA2lWpResp modifyA2lWpResp = getEntityProvider().getDbA2lWpResp(this.a2lWpResp.getID());
    validateStaleData(modifyA2lWpResp);

    // Update modified data
    modifyA2lWpResp.setTWpResp(getEntityProvider().getDbWpResp(this.newWpResp.getID()));

    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, modifyA2lWpResp, ENTITY_ID);

    getChangedData().put(this.a2lWpResp.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // remove from Data cache
    Set<A2LWpResponsibility> a2lWpRespSet = getDataCache().getA2lWpRespMap().get(this.a2lResp.getID());
    if (null != a2lWpRespSet) {
      a2lWpRespSet.remove(this.a2lWpResp);
    }

    TA2lWpResp dbA2lWpResp = getEntityProvider().getDbA2lWpResp(this.a2lWpResp.getID());
    getEntityProvider().deleteEntity(dbA2lWpResp);
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
    return this.oldWpResp.getID() != this.newWpResp.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        detailsForCmdInsert(detailsList);
        break;
      case UPDATE:
        addTransactionSummaryDetails(detailsList, this.oldWpResp.getWpRespEnum().getDispName(),
            this.newWpResp.getWpRespEnum().getDispName(), "Responsibility");
        break;
      case DELETE:// Not applicable
      default:
        // Do nothing
        break;
    }
    this.summaryData.setObjectType("A2L WP Responsibility");
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);

  }

  /**
   * @param detailsList
   */
  private void detailsForCmdInsert(final SortedSet<TransactionSummaryDetails> detailsList) {
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
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.dbA2LWpResp == null ? null : this.dbA2LWpResp.getA2lWpRespId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "A2L WP Responsibility";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.dbA2LWpResp == null ? null : getWorkPkgOrGrp();
  }

  /**
   * @return
   */
  private String getWorkPkgOrGrp() {
    String name = "";
    if (this.dbA2LWpResp.getTA2lGroup() != null) {
      name = this.dbA2LWpResp.getTA2lGroup().getGrpName();
    }
    if (this.dbA2LWpResp.getTWorkpackage() != null) {
      name = this.dbA2LWpResp.getTWorkpackage().getWpNameE();
    }
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", getPrimaryObjectIdentifier());
  }
}
