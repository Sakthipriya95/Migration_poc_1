/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.rules.bo.IParameter;
import com.bosch.caltool.apic.jpa.rules.bo.IParameterAttribute;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;


/**
 * Abstract class for Parameter Attribute for CDRFunction and RuleSet
 *
 * @author adn1cob
 */
public abstract class AbstractCmdModParamAttr extends AbstractCDRCommand {

  /**
   * the DB Parameter to be modified
   */
  protected final Long paramID;

  /**
   * Attribute
   */
  protected final Attribute attr;

  /**
   * Param attr obj
   */
  protected IParameterAttribute paramAttr;

  /**
   * Param attr id
   */
  protected long newParamAttrID;

  /**
   * Param
   */
  protected final IParameter param;

  /**
   * Param name
   */
  protected final String paramName;

  /**
   * Attr name
   */
  protected final String attrName;

  /**
   * Transaction Summary data instance
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * String constant for building info/error message
   */
  private static final String STR_WITH_NAME = " with name: ";


  /**
   * Constructor - INSERT
   *
   * @param dataProvider dataProvider
   * @param param param
   * @param attr attr
   */
  protected AbstractCmdModParamAttr(final CDRDataProvider dataProvider, final IParameter<?> param,
      final Attribute attr) {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.INSERT;
    this.paramID = param.getID();
    this.attr = attr;
    this.param = param;
    this.paramName = param.getName();
    this.paramAttr = null;
    this.attrName = attr.getAttributeName();
  }

  /**
   * Constructor - DELETE
   *
   * @param dataProvider dataProvider
   * @param parameterAttribute parameterAttribute
   * @throws SsdInterfaceException
   */
  public AbstractCmdModParamAttr(final CDRDataProvider dataProvider, final IParameterAttribute parameterAttribute)
      throws SsdInterfaceException {
    super(dataProvider);
    this.commandMode = COMMAND_MODE.DELETE;
    this.paramAttr = parameterAttribute;
    this.paramID = parameterAttribute.getParameter().getID();
    this.attr = null;
    this.param = parameterAttribute.getParameter();
    this.paramName = parameterAttribute.getParameter().getName();
    this.attrName = parameterAttribute.getAttribute().getAttributeName();
  }

  /**
   * Creates INSERT command based on the param object
   *
   * @param dataProvider dataprovider
   * @param param Parameter (CDRFuncParameter or RuleSetParameter)
   * @param attr attribute
   * @return command
   */
  public static AbstractCmdModParamAttr createInsertCommand(final CDRDataProvider dataProvider, final IParameter param,
      final Attribute attr) {
    AbstractCmdModParamAttr cmd = null;
    if (param instanceof CDRFuncParameter) {
      cmd = new CmdModParamAttr(dataProvider, (CDRFuncParameter) param, attr);
    }
    else if (param instanceof RuleSetParameter) {
      cmd = new CmdModRuleSetParamAttr(dataProvider, (RuleSetParameter) param, attr);
    }
    return cmd;
  }

  /**
   * Creates DELETE command based on the paramAttr object
   *
   * @param dataProvider dataprovider
   * @param paramAttr Parameter (ParameterAttribute or RuleSetParameterAttribute)
   * @return command
   */
  public static AbstractCmdModParamAttr createDeleteCommand(final CDRDataProvider dataProvider,
      final IParameterAttribute paramAttr) {
    AbstractCmdModParamAttr cmd = null;
    if (paramAttr instanceof ParameterAttribute) {
      try {
        cmd = new CmdModParamAttr(dataProvider, (ParameterAttribute) paramAttr);
      }
      catch (SsdInterfaceException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else if (paramAttr instanceof RuleSetParameterAttribute) {
      try {
        cmd = new CmdModRuleSetParamAttr(dataProvider, (RuleSetParameterAttribute) paramAttr);
      }
      catch (SsdInterfaceException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return cmd;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    return super.getString("", STR_WITH_NAME + getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Long getPrimaryObjectID() {
    return this.paramID;
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
      case DELETE:
        caseCmdDel(detailsList);
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
  private void caseCmdDel(final SortedSet<TransactionSummaryDetails> detailsList) {
    TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue(getPrimaryObjectIdentifier());
    details.setNewValue("");
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
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


}
