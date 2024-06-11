/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParamAttr;


/**
 * this command is to insert or delete rule set param attr
 *
 * @author mkl2cob
 */
public class CmdModRuleSetParamAttr extends AbstractCmdModParamAttr {

  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID = "RULE_SET_PARAM_ATTR";

  /**
   * TransactionSummary instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * String to store attr name
   */
  private String attrName;


  /**
   * constructor for INSERT
   *
   * @param dataProvider CDRDataProvider
   * @param ruleSetParam RuleSetParameter
   * @param attr Attribute
   */
  protected CmdModRuleSetParamAttr(final CDRDataProvider dataProvider, final RuleSetParameter ruleSetParam,
      final Attribute attr) {
    super(dataProvider, ruleSetParam, attr);
  }

  /**
   * constructor for DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param ruleSetParamAttr RuleSetParameterAttribute
   * @throws SsdInterfaceException
   */
  protected CmdModRuleSetParamAttr(final CDRDataProvider dataProvider, final RuleSetParameterAttribute ruleSetParamAttr)
      throws SsdInterfaceException {
    super(dataProvider, ruleSetParamAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        // remove from cache
        try {
          getDataCache().removeRuleSetParamAttr((RuleSetParameterAttribute) this.paramAttr);
        }
        catch (SsdInterfaceException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
      case DELETE:
        // add to cache
        try {
          getDataCache().addRuleSetParamAttr((RuleSetParameterAttribute) this.paramAttr);
        }
        catch (SsdInterfaceException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
      case UPDATE:
      default:
        // Do nothing
        break;
    }


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // create a new database entity
    final TRuleSetParamAttr dbRuleSetParamAttr = new TRuleSetParamAttr();

    // set the rule set parameter & attribute
    dbRuleSetParamAttr.setTRuleSetParam(getEntityProvider().getDbRuleSetParam(this.param.getID()));
    dbRuleSetParamAttr.setTabvAttribute(getEntityProvider().getDbAttribute(this.attr.getAttributeID()));

    setUserDetails(this.commandMode, dbRuleSetParamAttr, ENTITY_ID);

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbRuleSetParamAttr);

    // add it to the parent rule set param
    getEntityProvider().getDbRuleSetParam(this.param.getID()).getTRuleSetParamAttrs().add(dbRuleSetParamAttr);

    // create bo for rule set param attr
    this.paramAttr = new RuleSetParameterAttribute(getDataProvider(), dbRuleSetParamAttr.getRsetParAttrId());
    // add to cache
    try {
      getDataCache().addRuleSetParamAttr((RuleSetParameterAttribute) this.paramAttr);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.param.getID(),
        new ChangedData(ChangeType.INSERT, this.param.getID(), TRuleSetParamAttr.class, DisplayEventSource.COMMAND));

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
  protected void executeDeleteCommand() throws CommandException {
    // store the name before its deleted
    this.attrName = this.paramAttr.getName();
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.paramAttr.getID(), TRuleSetParamAttr.class, DisplayEventSource.COMMAND);
    chdata.setOldDataDetails(this.paramAttr.getObjectDetails());
    final TRuleSetParamAttr deletedRuleSetParamAttr = getEntityProvider().getDbRuleSetParamAttr(this.paramAttr.getID());
    validateStaleData(deletedRuleSetParamAttr);

    // remove from parent parameter in entity
    getEntityProvider().getDbRuleSetParam(this.param.getID()).getTRuleSetParamAttrs().remove(deletedRuleSetParamAttr);
    // remove from cache
    try {
      getDataCache().removeRuleSetParamAttr((RuleSetParameterAttribute) this.paramAttr);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.paramAttr.getID(), chdata);
    // delete the entity
    getEntityProvider().deleteEntity(deletedRuleSetParamAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TRuleSetParamAttr deletedRuleSetParamAttr = getEntityProvider().getDbRuleSetParamAttr(this.paramAttr.getID());

    validateStaleData(deletedRuleSetParamAttr);
    // remove from entity param
    getEntityProvider().getDbRuleSetParam(this.param.getID()).getTRuleSetParamAttrs().remove(deletedRuleSetParamAttr);

    // unregister the cp
    getEntityProvider().deleteEntity(deletedRuleSetParamAttr);

    // remove from cache
    try {
      getDataCache().removeRuleSetParamAttr((RuleSetParameterAttribute) this.paramAttr);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.paramAttr.getID(), new ChangedData(ChangeType.INSERT, this.paramAttr.getID(),
        TRuleSetParamAttr.class, DisplayEventSource.COMMAND));

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
  protected void undoDeleteCommand() throws CommandException {
    // create a new database entity
    final TRuleSetParamAttr dbRuleSetParamAttr = new TRuleSetParamAttr();

    // set the rule set parameter & attribute
    dbRuleSetParamAttr.setTRuleSetParam(getEntityProvider().getDbRuleSetParam(this.param.getID()));
    dbRuleSetParamAttr.setTabvAttribute(getEntityProvider().getDbAttribute(this.attr.getAttributeID()));
    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbRuleSetParamAttr);

    // add it to the parent rule set param
    getEntityProvider().getDbRuleSetParam(this.param.getID()).getTRuleSetParamAttrs().add(dbRuleSetParamAttr);

    // add to cache
    try {
      getDataCache().addRuleSetParamAttr((RuleSetParameterAttribute) this.paramAttr);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.paramAttr.getID(), new ChangedData(ChangeType.DELETE, this.paramAttr.getID(),
        TRuleSetParamAttr.class, DisplayEventSource.COMMAND));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    return false;
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
  public String getPrimaryObjectType() {
    return "Rule Set Parameter Attribute";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier = "";
    if (null != this.paramAttr.getID()) {
      final TRuleSetParamAttr dbruleSetParamAttr = getEntityProvider().getDbRuleSetParamAttr(this.paramAttr.getID());
      if (null != dbruleSetParamAttr) {
        objectIdentifier = dbruleSetParamAttr.getTabvAttribute().getAttrNameEng();
      }
    }
    return objectIdentifier;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {


    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      case INSERT:
        caseCmdModIns(detailsList);
        break;
      case DELETE:
        addTransactionSummaryDetails(detailsList, this.attrName, "", "Dependency Attribute Name");
        break;
      case UPDATE:
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
  private void caseCmdModIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }
}
