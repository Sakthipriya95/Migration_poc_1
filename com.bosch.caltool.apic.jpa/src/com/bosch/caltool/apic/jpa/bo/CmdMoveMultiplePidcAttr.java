/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * ICDM-1402 Parent command to modify multiple pidcattr
 *
 * @author bru2cob
 */
public class CmdMoveMultiplePidcAttr extends AbstractCmdMoveAttribute {

  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStk = new ChildCommandStack(this);
  /**
   * the PID Card version
   */
  private final PIDCVersion pidcVers;
  /**
   * Store the transactionSummary
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);
  /**
   * List of selected pidc attrs
   */
  private final List<IPIDCAttribute> pidcAttrs;
  /**
   * Move downwards the PIDC tree. If true move from PIDC to PIDC Variant/Variant to Subvariant to variant.
   */
  private final boolean moveDown;
  /**
   * Names of attributes to be moved
   */
  private final StringBuilder attrNames = new StringBuilder();
  /**
   * Store error message
   */
  private final StringBuilder errorMsg = new StringBuilder();


  /**
   * Constructor when moving multiple attributes from Common to Variant/variant to common
   *
   * @param dataProvider the apic data provider instance
   * @param pidcAttrs list of attrs to be moved
   * @param moveDown move direction
   */
  public CmdMoveMultiplePidcAttr(final ApicDataProvider dataProvider, final List<IPIDCAttribute> pidcAttrs,
      final boolean moveDown) {

    super(dataProvider, null, moveDown);
    setAttributeName("");
    setParentName(pidcAttrs.get(0).getPidcVersion().getName());
    this.moveDown = moveDown;
    this.pidcAttrs = pidcAttrs;
    this.pidcVers = pidcAttrs.get(0).getPidcVersion();
  }


  @Override
  protected final Set<IPIDCAttribute> getAllVarAttrsForAttr() {
    final Set<IPIDCAttribute> retSet = new HashSet<IPIDCAttribute>();
    for (PIDCVariant var : this.pidcVers.getVariantsMap().values()) {
      retSet.add(var.getAttributes(true, false).get(getPidcAttrToMove().getAttribute().getAttributeID()));
    }
    return retSet;
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
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {

    for (IPIDCAttribute selAttr : this.pidcAttrs) {
      CmdMovePidcAttr cmdMoveAttr;
      if (selAttr instanceof PIDCAttribute) {
        cmdMoveAttr = updatePidcAttr(selAttr);
      }
      else {
        cmdMoveAttr = new CmdMovePidcAttr(getDataProvider(), (PIDCAttributeVar) selAttr);
      }
      String modifiedAttr = selAttr.getAttribute().getName();
      this.childCmdStk.addCommand(cmdMoveAttr);

      if (cmdMoveAttr.getErrorCause() == ERROR_CAUSE.NONE) {
        this.attrNames.append(modifiedAttr).append(",");
      }
      else {
        this.errorMsg.append(" (MOVED " + selAttr.getAttribute().getName() + ") " + cmdMoveAttr.getErrorMessage())
            .append(" , ");
      }
    }
  }


  /**
   * @param selAttr
   * @return
   */
  private CmdMovePidcAttr updatePidcAttr(final IPIDCAttribute selAttr) {
    CmdMovePidcAttr cmdMoveAttr = null;
    if (((PIDCAttribute) selAttr).isVariant()) {
      SortedSet<PIDCVariant> varSet = this.pidcVers.getVariantsSet();
      if (!varSet.isEmpty()) {
        PIDCVariant pidcVarObj = varSet.first();
        PIDCAttributeVar editableAttrVar =
            pidcVarObj.getAllVarAttribute(((PIDCAttribute) selAttr).getAttribute().getAttributeID());
        cmdMoveAttr = new CmdMovePidcAttr(getDataProvider(), editableAttrVar);
      }
    }
    else {
      cmdMoveAttr = new CmdMovePidcAttr(getDataProvider(), (PIDCAttribute) selAttr);
    }
    return cmdMoveAttr;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * @return the errorMsg
   */
  public String getErrorMsg() {
    return this.errorMsg.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    this.childCmdStack.undoAll();
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
  protected boolean dataChanged() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    return super.getTransactionSummary(this.summaryData);
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
    return this.pidcVers == null ? null : this.pidcVers.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    if (!CommonUtils.isEmptyString(this.errorMsg.toString()) && CommonUtils.isEmptyString(this.attrNames.toString())) {
      return "Move Attribute failed";
    }
    String objIdentifier = "";
    if (!CommonUtils.isEmptyString(this.attrNames.toString())) {
      if (this.moveDown) {
        objIdentifier = "MOVED DOWN " + this.attrNames.toString().substring(0, this.attrNames.length() - 1) + " from " +
            this.pidcVers.getName();
      }
      else {
        objIdentifier = "MOVED UP " + this.attrNames.toString().substring(0, this.attrNames.length() - 1) + " to " +
            this.pidcVers.getName();
      }
    }
    return super.getString("", objIdentifier);
  }
}
