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


/**
 * ICDM-1402 Parent command to modify multiple pidc variant attr
 *
 * @author bru2cob
 */
public class CmdMoveMultipleVarAttr extends AbstractCmdMoveAttribute {

  /**
   * Child command stack instance
   */
  protected final ChildCommandStack childCmdStk = new ChildCommandStack(this);
  /**
   * the PID Variant
   */
  private PIDCVariant pidcVar;
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
  private final boolean moveUp;
  /**
   * Names of attributes to be moved
   */
  StringBuilder attrNames = new StringBuilder();

  /**
   * Constructor when moving multiple attributes from Variant to sub-variant /sub-variant to variant
   *
   * @param dataProvider the apic data provider instance
   * @param pidcAttrs list of attrs to be moved
   * @param moveUp move direction
   */
  public CmdMoveMultipleVarAttr(final ApicDataProvider dataProvider, final List<IPIDCAttribute> pidcAttrs,
      final boolean moveUp) {

    super(dataProvider, null, moveUp);
    setAttributeName("");
    setParentName(pidcAttrs.get(0).getPidcVersion().getName());
    this.moveUp = moveUp;
    this.pidcAttrs = pidcAttrs;
    IPIDCAttribute ipidcAttribute = pidcAttrs.get(0);
    if (ipidcAttribute instanceof PIDCAttributeVar) {
      this.pidcVar = ((PIDCAttributeVar) ipidcAttribute).getPidcVariant();
    }
    else if (ipidcAttribute instanceof PIDCAttributeSubVar) {
      this.pidcVar = ((PIDCAttributeSubVar) ipidcAttribute).getPidcVariant();
    }
    for (IPIDCAttribute pidcAttr : this.pidcAttrs) {
      this.attrNames.append(pidcAttr.getAttribute().getName()).append(",");
    }
  }

  @Override
  protected final Set<IPIDCAttribute> getAllVarAttrsForAttr() {
    final Set<IPIDCAttribute> retSet = new HashSet<IPIDCAttribute>();
    for (PIDCSubVariant svar : this.pidcVar.getSubVariantsMap().values()) {
      retSet.add(svar.getAttributes(true, false).get(getPidcAttrToMove().getAttribute().getAttributeID()));
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
      CmdMoveVarAttr moveAttr = null;
      if (selAttr instanceof PIDCAttributeVar) {
        moveAttr = updateVarAttr(selAttr);
      }
      else if (selAttr instanceof PIDCAttributeSubVar) {
        moveAttr = new CmdMoveVarAttr(getDataProvider(), (PIDCAttributeSubVar) selAttr);
      }
      this.childCmdStk.addCommand(moveAttr);
    }
  }

  /**
   * @param selAttr
   * @return
   */
  private CmdMoveVarAttr updateVarAttr(final IPIDCAttribute selAttr) {
    CmdMoveVarAttr moveAttr = null;
    PIDCAttributeVar varAttr = (PIDCAttributeVar) selAttr;
    if (varAttr.isVariant()) {
      SortedSet<PIDCSubVariant> subVariantsSet = this.pidcVar.getSubVariantsSet();
      if (!subVariantsSet.isEmpty()) {
        PIDCSubVariant pidcSubVar = subVariantsSet.first();
        PIDCAttributeSubVar editableAttrSubVar =
            pidcSubVar.getAllSubVarAttribute(varAttr.getAttribute().getAttributeID());
        moveAttr = new CmdMoveVarAttr(getDataProvider(), editableAttrSubVar);
      }
    }
    else {
      moveAttr = new CmdMoveVarAttr(getDataProvider(), (PIDCAttributeVar) selAttr);
    }
    return moveAttr;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // TODO Auto-generated method stub
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
    this.childCmdStk.doPostCommit();

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    return this.pidcVar == null ? null : this.pidcVar.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    String objIdentifier;
    if (this.moveUp) {
      objIdentifier = "MOVED UP " + this.attrNames.toString().substring(0, this.attrNames.length() - 1) + " to " +
          this.pidcVar.getName();
    }
    else {
      objIdentifier = "MOVED DOWN " + this.attrNames.toString().substring(0, this.attrNames.length() - 1) + " from " +
          this.pidcVar.getName();
    }
    return super.getString("", objIdentifier);
  }
}
