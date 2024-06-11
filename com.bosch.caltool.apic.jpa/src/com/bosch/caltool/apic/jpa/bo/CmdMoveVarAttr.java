/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdMoveVarAttr.java - Command to move the attributes between VARINATS and its Sub-Variants.
 *
 * @author adn1cob
 */
public class CmdMoveVarAttr extends AbstractCmdMoveAttribute {

  /**
   * the PID Variant
   */
  private final PIDCVariant pidcVar;
  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);


  /**
   * Constructor to move VARIANT to SUB-VARIANT
   *
   * @param dataProvider the apic data provider instance
   * @param varAttr PIDC attribute to move down to sub variants
   */
  public CmdMoveVarAttr(final ApicDataProvider dataProvider, final PIDCAttributeVar varAttr) {
    super(dataProvider, varAttr, true);
    this.pidcVar = varAttr.getPidcVariant();
  }

  /**
   * Constructor to move SUB-VARIANT to VARIANT
   *
   * @param dataProvider the apic data provider instance
   * @param subVarAttr SubVariant attribute to move up to VARIANT
   */
  public CmdMoveVarAttr(final ApicDataProvider dataProvider, final PIDCAttributeSubVar subVarAttr) {
    super(dataProvider, subVarAttr, false);
    this.pidcVar = subVarAttr.getPidcVariant();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {
    this.childCmdStack.clear();

    if (isMoveDown()) {
      if (this.pidcVar.getSubVariantsMap().size() == 0) {
        throw new CommandException("No sub variants defined for this Variant");
      }
      // Attribute should not be moved to subvariant if there are no undeleted subvariants for the variant
      else if (this.pidcVar.getSubVariantsMap(false).values().isEmpty()) {
        throw new CommandException("No undeleted sub variants are available for this Variant.");
      }
      for (PIDCSubVariant subVar : this.pidcVar.getSubVariantsMap().values()) {
        createSubVarAttr(subVar);
      }
      // mark the variant attribute as sub-variant
      moveProjVarAttr(true);

    }
    else {
      // mark the variant attribute as not sub-variant
      moveProjVarAttr(false);

      // For moving attribute from sub-variant to variant, only the
      // sub-variant attribute delete is done. Variant
      // attribute is not created as the not defined flag is to be set to
      // ???
      final Attribute attribute = getPidcAttrToMove().getAttribute();
      for (PIDCSubVariant subVar : this.pidcVar.getSubVariantsMap().values()) {
        // ICDM-1360
        for (PIDCAttributeSubVar subVarAttr : subVar.getAttributesAll().values()) {
          if (attribute.equals(subVarAttr.getAttribute())) {
            removeSubVarAttr(subVarAttr);
          }
        }
      }
    }
  }


  /**
   * Create SubVariant attribute
   *
   * @param subVar sub variant
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createSubVarAttr(final PIDCSubVariant subVar) throws CommandException {
    final PIDCAttributeSubVar subVarAttr = new PIDCAttributeSubVar(getDataProvider(), getPidcAttrToMove().attrID,
        subVar.getPidcVariant().getVariantID(), subVar.getSubVariantID());
    final CmdModProjSubVariantAttr command = new CmdModProjSubVariantAttr(getDataProvider(), subVarAttr);

    command.setUsed(getPidcAttrToMove().getIsUsed());
    command.setValue(getPidcAttrToMove().getAttributeValue());
    command.setNewDesc(getPidcAttrToMove().getAdditionalInfoDesc());
    command.setNewPartNumber(getPidcAttrToMove().getPartNumber());
    command.setNewSpecLink(getPidcAttrToMove().getSpecLink());
    command.setNewHiddenFlag(getPidcAttrToMove().isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);

    this.childCmdStack.addCommand(command);
  }

  /**
   * Delete the SubVariant attribute
   *
   * @param subVarAttr sub-variant attribute to remove
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void removeSubVarAttr(final IPIDCAttribute subVarAttr) throws CommandException {
    final CmdModProjSubVariantAttr command =
        new CmdModProjSubVariantAttr(getDataProvider(), (PIDCAttributeSubVar) subVarAttr, true);
    this.childCmdStack.addCommand(command);
  }

  /**
   * Define the variant attribute as sub-variant as defined in the parameter Set the used flag to undefined when moving
   * the attribute up to variant
   *
   * @param toSubVariant
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void moveProjVarAttr(final boolean toSubVariant) throws CommandException {
    if (toSubVariant) {

      this.parentCommand = new CmdModProjVariantAttr(getDataProvider(), (PIDCAttributeVar) getPidcAttrToMove());

      this.parentCommand.setUsed(ApicConstants.USED_YES_DISPLAY);
      this.parentCommand.setNewPartNumber("");
      this.parentCommand.setNewSpecLink("");
      this.parentCommand.setNewDesc("");

    }
    else {
      final PIDCAttributeVar pidcVarAttr = ((PIDCAttributeSubVar) getPidcAttrToMove()).getPidcVariant()
          .getAttributesAll().get(getPidcAttrToMove().getAttribute().getAttributeID());

      this.parentCommand = new CmdModProjVariantAttr(getDataProvider(), pidcVarAttr);
      setParentCmdPropsMvUp();
    }
    this.parentCommand.setIsVariant(toSubVariant);

    this.childCmdStack.addCommand(this.parentCommand);
    getChangedData().putAll(this.parentCommand.getChangedData());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    for (PIDCSubVariant subVar : this.pidcVar.getSubVariantsMap().values()) {
      subVar.resetChildrenLoaded();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean dataChanged() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeInsertCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeDeleteCommand() throws CommandException {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoInsertCommand() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoDeleteCommand() {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {

    // Not Implemented
  }

  /**
   * {@inheritDoc} return the id of project variant
   */
  @Override
  public Long getPrimaryObjectID() {
    // Since this command involves operation on multiple objects, the parent object's(PIDC Variant) id is returned as
    // the primary object id
    return this.pidcVar == null ? null : this.pidcVar.getID();
  }

  @Override
  protected final Set<IPIDCAttribute> getAllVarAttrsForAttr() {
    final Set<IPIDCAttribute> retSet = new HashSet<IPIDCAttribute>();

    for (PIDCSubVariant svar : this.pidcVar.getSubVariantsMap().values()) {
      // iCDM-1098
      retSet.add(svar.getAttributes(true, false).get(getPidcAttrToMove().getAttribute().getAttributeID()));
    }

    return retSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    return super.getTransactionSummary(this.summaryData);
  }


}
