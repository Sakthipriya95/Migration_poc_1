/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

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
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * ICDM-1374 This is the command to create or delete parameters that belong to a rule set
 *
 * @author mkl2cob
 */
public class CmdModRuleSetParam extends AbstractCDRCommand {

  /**
   * RuleSetParameter instance
   */
  private RuleSetParameter ruleSetParam;
  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Transaction summary instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";

  /**
   * Unique entity id for setting user details
   */
  private static final String ENTITY_ID = "RULE_SET_PARAM";

  /**
   * CDRFuncParameter instance
   */
  private CDRFuncParameter param;

  /**
   * CDRFunction instance
   */
  private CDRFunction function;

  /**
   * RuleSet instance
   */
  private final RuleSet ruleSet;

  /**
   * string that represents parameter name
   */
  private String paramName;

  /**
   * to include transaction summary for rules
   */
  private boolean rulesDeleted;

  /**
   * Ssd success message Code
   */
  private static final int SSD_SUCCESS_CODE = 0;

  /**
   * Constructor for INSERT
   *
   * @param dataProvider CDRDataProvider
   * @param ruleSet RuleSet
   * @param param CDRFuncParameter
   * @param function CDRFunction
   */
  public CmdModRuleSetParam(final CDRDataProvider dataProvider, final RuleSet ruleSet, final CDRFuncParameter param,
      final CDRFunction function) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.ruleSet = ruleSet;
    this.param = param;
    this.function = function;
  }

  /**
   * Constructor for DELETE
   *
   * @param dataProvider CDRDataProvider
   * @param ruleSetParam RuleSetParameter
   */
  public CmdModRuleSetParam(final CDRDataProvider dataProvider, final RuleSetParameter ruleSetParam) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.DELETE;
    this.ruleSetParam = ruleSetParam;
    this.paramName = ruleSetParam.getName();
    this.ruleSet = ruleSetParam.getRuleSet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    switch (this.commandMode) {
      case INSERT:
        // remove the parameter from cache
        try {
          this.ruleSet.getAllParameters(false).remove(this.ruleSetParam.getID());
        }
        catch (SsdInterfaceException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        getEntityProvider().getDbRuleSet(this.ruleSet.getID()).getTRuleSetParams().remove(this.ruleSetParam.getID());
        break;
      case UPDATE:
      case DELETE:
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
    TRuleSetParam dbRuleSetParam = new TRuleSetParam();

    // set the rule set
    dbRuleSetParam.setTRuleSet(getEntityProvider().getDbRuleSet(this.ruleSet.getID()));
    // set the parameter and function
    dbRuleSetParam.setTParameter(getEntityProvider().getDbParameter(this.param.getID()));
    dbRuleSetParam.setTFunction(getEntityProvider().getDbFunction(this.function.getID()));


    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbRuleSetParam);
    // add the parameter to the rule set
    getEntityProvider().getDbRuleSet(this.ruleSet.getID()).getTRuleSetParams().add(dbRuleSetParam);

    this.ruleSetParam = new RuleSetParameter(getDataProvider(), dbRuleSetParam.getRsetParamId());
    // add to cache
    getDataCache().addRuleSetParameter(this.ruleSetParam);
    try {
      this.ruleSet.getAllParameters(false).put(this.ruleSetParam.getName(), this.ruleSetParam);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.ruleSetParam.getID(),
        new ChangedData(ChangeType.INSERT, this.ruleSetParam.getID(), TRuleSetParam.class, DisplayEventSource.COMMAND));

    setUserDetails(this.commandMode, dbRuleSetParam, ENTITY_ID);
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
    // Check for any parallel changes
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.ruleSetParam.getID(), TRuleSetParam.class, DisplayEventSource.COMMAND);
    try {
      chdata
          .setOldDataDetails(this.ruleSet.getAllParameters(false).get(this.ruleSetParam.getName()).getObjectDetails());
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final TRuleSetParam deletedRuleSetParam = getEntityProvider().getDbRuleSetParam(this.ruleSetParam.getID());
    validateStaleData(deletedRuleSetParam);

    // remove from rule set
    getEntityProvider().getDbRuleSet(this.ruleSet.getID()).getTRuleSetParams().remove(this.ruleSetParam.getID());


    try {
      if (CommonUtils.isNotNull(this.ruleSetParam.getReviewRuleList())) {
        try {
          final SSDMessage ssdMsg =
              this.ruleSet.getRuleManager().deleteMultipleRules(this.ruleSetParam.getReviewRuleList());
          if (ssdMsg.getCode() == SSD_SUCCESS_CODE) {
            setTransSummaryForRules(true);
          }
        }
        catch (IcdmException exception) {
          CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception);
        }
      }
    }
    catch (SsdInterfaceException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // If the rule set params has param attributes mapped to it then delete them first
    try {
      if ((null != this.ruleSetParam.getParamAttrs()) && !this.ruleSetParam.getParamAttrs().isEmpty()) {
        Set<RuleSetParameterAttribute> dependenciesToBeRemoved = new TreeSet<>(this.ruleSetParam.getParamAttrs());
        for (RuleSetParameterAttribute paramAttr : dependenciesToBeRemoved) {
          CmdModRuleSetParamAttr command;
          try {
            command = new CmdModRuleSetParamAttr(getDataProvider(), paramAttr);
            this.childCmdStack.addCommand(command);
          }
          catch (SsdInterfaceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }
    catch (SsdInterfaceException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // delete the entity
    getEntityProvider().deleteEntity(deletedRuleSetParam);

    // delete the entity mapping with its parent rule set
    getEntityProvider().getDbRuleSet(this.ruleSet.getID()).getTRuleSetParams().remove(deletedRuleSetParam);

    // remove the parameter from cache
    getDataCache().removeRuleSetParameter(this.ruleSetParam);
    try {
      this.ruleSet.getAllParameters(false).remove(this.paramName);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    getChangedData().put(this.ruleSetParam.getID(), chdata);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // Check for any parallel changes
    final TRuleSetParam deletedRuleSetParam = getEntityProvider().getDbRuleSetParam(this.ruleSetParam.getID());
    validateStaleData(deletedRuleSetParam);
    // remove from rule set
    getEntityProvider().getDbRuleSet(this.ruleSet.getID()).getTRuleSetParams().remove(this.ruleSetParam.getID());
    // unregister the Rule set parameter
    getEntityProvider().deleteEntity(deletedRuleSetParam);

    // remove the parameter from cache
    getDataCache().removeRuleSetParameter(this.ruleSetParam);
    try {
      this.ruleSet.getAllParameters(false).remove(this.ruleSetParam.getID());
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.ruleSetParam.getID(),
        new ChangedData(ChangeType.INSERT, this.ruleSetParam.getID(), TRuleSetParam.class, DisplayEventSource.COMMAND));

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

    TRuleSetParam dbRuleSetParam = new TRuleSetParam();

    dbRuleSetParam.setTRuleSet(getEntityProvider().getDbRuleSet(this.ruleSet.getID()));

    // register the new Entity to get the ID
    getEntityProvider().registerNewEntity(dbRuleSetParam);

    getEntityProvider().getDbRuleSet(this.ruleSet.getID()).getTRuleSetParams().add(dbRuleSetParam);

    this.ruleSetParam = new RuleSetParameter(getDataProvider(), dbRuleSetParam.getRsetParamId());
    // add to cache
    getDataCache().addRuleSetParameter(this.ruleSetParam);
    // add the new rule set parameter to rule set
    try {
      this.ruleSet.getAllParameters(false).put(this.ruleSetParam.getName(), this.ruleSetParam);
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    getChangedData().put(this.ruleSetParam.getID(),
        new ChangedData(ChangeType.INSERT, this.ruleSetParam.getID(), TRuleSetParam.class, DisplayEventSource.COMMAND));

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
  public String getString() {
    if (!(getErrorCause() == ERROR_CAUSE.NONE) && this.rulesDeleted && (this.commandMode == COMMAND_MODE.DELETE)) {
      return super.getString("", "Rules were deleted for " + STR_WITH_NAME + getPrimaryObjectIdentifier());
    }
    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());

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
      case DELETE:
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
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return null == this.ruleSetParam ? null : this.ruleSetParam.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Rule Set Parameter";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier = "";
    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = this.param.getName();
        break;
      case DELETE:
        objectIdentifier = this.paramName;
        break;
      case UPDATE:
      default:
        // Do nothing
        break;
    }
    return objectIdentifier;
  }

  /**
   * @return the transSummaryForRules
   */
  public boolean isTransSummaryForRules() {
    return this.rulesDeleted;
  }

  /**
   * @param transSummaryForRules the transSummaryForRules to set
   */
  public void setTransSummaryForRules(final boolean transSummaryForRules) {
    this.rulesDeleted = transSummaryForRules;
  }

}
