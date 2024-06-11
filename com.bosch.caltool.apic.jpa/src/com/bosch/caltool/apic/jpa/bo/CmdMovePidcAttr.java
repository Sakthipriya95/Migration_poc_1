/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * Command to move the attributes between PIDC and its variants.
 *
 * @author bne4cob
 */
public class CmdMovePidcAttr extends AbstractCmdMoveAttribute {

  /**
   * the PID Card version
   */
  private final PIDCVersion pidcVers;

  /**
   * Store the transactionSummary - ICDM-721
   */
  private final TransactionSummary summaryData = new TransactionSummary(this);

  /**
   * Constructor when moving an attribute from Common to Variant
   *
   * @param dataProvider the apic data provider instance
   * @param pidcAttr PIDC attribute to move down to variants
   */
  public CmdMovePidcAttr(final ApicDataProvider dataProvider, final PIDCAttribute pidcAttr) {
    super(dataProvider, pidcAttr, true);
    this.pidcVers = pidcAttr.getPidcVersion();
  }

  /**
   * Constructor when moving an attribute from Variant to Common
   *
   * @param dataProvider the apic data provider instance
   * @param varAttr Variant attribute to move up to PIDC
   */
  public CmdMovePidcAttr(final ApicDataProvider dataProvider, final PIDCAttributeVar varAttr) {
    super(dataProvider, varAttr, false);
    this.pidcVers = varAttr.getPidcVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {
    this.childCmdStack.clear();
    if (isMoveDown()) {
      if (this.pidcVers.getVariantsMap().isEmpty()) {
        throw new CommandException("No variants defined for this project ID card");
      }
      // Attribute should not be moved to variant if there are no undeleted variants for the PIDC
      else if (!this.pidcVers.hasVariants()) {
        throw new CommandException("No undeleted variants are available for this project ID card");
      }

      for (PIDCVariant variant : this.pidcVers.getVariantsMap().values()) {
        createVarAttr(variant);
      }

      // mark the PIDC attribute as Variant or create a PIDC attribute as
      // a Variant attribute if not existing
      moveProjAttr(true);

    }
    else {
      // Check if this attribute is a sub-variant in any of the variants of this pidc and also if the subvariant is not
      // deleted,
      // before moving to common
      checkMoveUpAllowed(getPidcAttrToMove());
      // mark the attribute in the PIDC as non variant
      moveProjAttr(false);
      // delete all attribute values in the variants
      final Attribute attribute = getPidcAttrToMove().getAttribute();
      for (PIDCVariant variant : this.pidcVers.getVariantsMap().values()) {
        // ICDM-1360
        for (PIDCAttributeVar varAttr : variant.getAttributesAll().values()) {

          removeAttributeFromSubvariantIfDeleted(variant, attribute);

          if (attribute.equals(varAttr.getAttribute())) {
            removeVarAttr(varAttr);
          }


        }
      }

    }
  }


  /**
   * Method to remove the attribute from deleted subvariant if moved to common from variant level
   *
   * @param variant - PIDC Variant
   * @param attribute - Attribute
   * @throws CommandException
   */
  private void removeAttributeFromSubvariantIfDeleted(final PIDCVariant variant, final Attribute attribute)
      throws CommandException {
    if (isSubVariantDeleted(variant, attribute)) {
      for (PIDCSubVariant subVar : variant.getSubVariantsMap().values()) {
        for (PIDCAttributeSubVar subVarAttr : subVar.getAttributesAll().values()) {
          removeAttrFromSubvariant(attribute, subVarAttr);
        }
      }
    }

  }

  /**
   * Method to invoke removal of attribute from subvariant level
   *
   * @param attribute
   * @param subVarAttr
   * @throws CommandException
   */
  private void removeAttrFromSubvariant(final Attribute attribute, final PIDCAttributeSubVar subVarAttr)
      throws CommandException {
    if (attribute.equals(subVarAttr.getAttribute())) {
      CmdMoveVarAttr cmd = new CmdMoveVarAttr(getDataProvider(), subVarAttr);
      this.childCmdStack.addCommand(cmd);
    }
  }

  /**
   * Method to check if a PIDC attribute can be moved to common, (if an attribute is used in sub-variant and is not
   * deleted, then it cannot be moved to common)
   *
   * @param attr - Attribute for which the check is done
   */
  private void checkMoveUpAllowed(final IPIDCAttribute attr) throws CommandException {

    // Get all variants of this pidc
    final Map<Long, PIDCVariant> pidcVars = attr.getPidcVersion().getVariantsMap();
    for (PIDCVariant pidcVariant : pidcVars.values()) {
      // get all attributes of this variant
      final Map<Long, PIDCAttributeVar> varAttrs = pidcVariant.getAttributesAll();
      for (PIDCAttributeVar varAttr : varAttrs.values()) {
        // If any attr is defined as sub-variant, attribute cannot be moved up
        if ((attr.getAttribute().getAttributeID().longValue() == varAttr.getAttribute().getAttributeID().longValue()) &&
            varAttr.isVariant()) {

          checkSubvariantDeletion(pidcVariant, attr);


        }

      }
    }
  }


  /**
   * If subvariant is deleted, then allow move to common, else display error
   *
   * @param pidcVariant
   * @param attr
   */
  private void checkSubvariantDeletion(final PIDCVariant pidcVariant, final IPIDCAttribute attr)
      throws CommandException {
    if (!isSubVariantDeleted(pidcVariant, attr.getAttribute())) {
      throw new CommandException("Attribute is defined as Sub-Variant for Variant : " + pidcVariant.getVariantName());
    }

  }


  /**
   * Method that checks if variant attribute is part of any undeleted subvariant If it is part of undeleted subvariant,
   * throw error If it is part of deleted subvariant, do nothing
   *
   * @param pidcVariant - pidcVariant to check
   * @param attribute - pidc attribute to check
   * @return true if subvariant is deleted, false if not deleted
   */
  private boolean isSubVariantDeleted(final PIDCVariant pidcVariant, final Attribute attribute) {
    for (PIDCSubVariant subVar : pidcVariant.getSubVariantsMap().values()) {
      if (subVar.isDeleted()) {
        continue;
      }
      if (!subVar.getAttributes().isEmpty() &&
          (subVar.getAttributes().get(attribute.getAttributeID()) != null)) {
        return false;
      }

    }
    return true;
  }


  /**
   * Create variant attribute
   *
   * @param variant variant
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void createVarAttr(final PIDCVariant variant) throws CommandException {
    final PIDCAttributeVar varAttr =
        new PIDCAttributeVar(getDataProvider(), getPidcAttrToMove().attrID, variant.getVariantID());
    final CmdModProjVariantAttr command = new CmdModProjVariantAttr(getDataProvider(), varAttr);
    // ICDM-2227
    if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType(), getPidcAttrToMove().getIsUsed())) {
      command.setUsed(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
    }
    else {
      command.setUsed(getPidcAttrToMove().getIsUsed());
    }
    command.setValue(getPidcAttrToMove().getAttributeValue());
    command.setNewDesc(getPidcAttrToMove().getAdditionalInfoDesc());
    command.setNewPartNumber(getPidcAttrToMove().getPartNumber());
    command.setNewSpecLink(getPidcAttrToMove().getSpecLink());
    command.setNewHiddenFlag(getPidcAttrToMove().isHidden() ? ApicConstants.YES : ApicConstants.CODE_NO);

    this.childCmdStack.addCommand(command);
  }

  /**
   * Delete the variant attribute
   *
   * @param varAttr variant attribute to remove
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void removeVarAttr(final IPIDCAttribute varAttr) throws CommandException {
    AbstractCmdModProjAttr command = null;
    if (varAttr instanceof PIDCAttributeVar) {
      command = new CmdModProjVariantAttr(getDataProvider(), (PIDCAttributeVar) varAttr, true);
    }
    else if (varAttr instanceof PIDCAttributeSubVar) {
      command = new CmdModProjSubVariantAttr(getDataProvider(), (PIDCAttributeSubVar) varAttr, true);
    }
    if (command != null) {
      this.childCmdStack.addCommand(command);
    }
  }

  /**
   * Define the project attribute as variant
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  private void moveProjAttr(final boolean toVariant) throws CommandException {

    PIDCAttribute pidcAttribute;
    if (toVariant) {
      // the pidcAttribute is the attribute to move
      pidcAttribute = (PIDCAttribute) getPidcAttrToMove();
    }
    else {
      // the attribueToMove is a variant attribute

      final Attribute attributeToMove = getPidcAttrToMove().getAttribute();

      pidcAttribute = getPidcAttrToMove().getPidcVersion().getAttributesAll().get(attributeToMove.getAttributeID());
    }

    this.parentCommand = new CmdModProjectAttr(getDataProvider(), pidcAttribute);
    this.parentCommand.setIsVariant(toVariant);


    if (toVariant) {
      this.parentCommand.setNewPartNumber("");
      this.parentCommand.setNewSpecLink("");
      this.parentCommand.setNewDesc("");
      this.parentCommand.setUsed(ApicConstants.USED_YES_DISPLAY);
      if (!((pidcAttribute.getAttribute().getValueType() == AttributeValueType.DATE) ||
          (pidcAttribute.getAttribute().getValueType() == AttributeValueType.HYPERLINK))) {
        this.parentCommand.setNewTrnfrVcdmFlag(ApicConstants.YES);
      }

    }
    else {
      setParentCmdPropsMvUp();
      // ICDM-1583 deleting unmapped pidc_a2l file (Variant level - Move to Common)
      changeInPidcA2L(pidcAttribute);
    }

    this.childCmdStack.addCommand(this.parentCommand);
    getChangedData().putAll(this.parentCommand.getChangedData());
  }


  /**
   * Deleting the unmapped A2L File (Variant level - Move to Common)<br>
   * 1. checking attribute level to be SDOM PVER<br>
   * 2. checking parent command new value against null for unequal sdom value in different variants<br>
   * 3. Iterating Sdom in current version<br>
   * 4. Getting pidc_a2l entry for both mapped and unmapped a2l files in current version<br>
   * 5. Iterating the pidc_a2l entry from point-4 and checking null for pidc_version and comparing equality for sdom
   * from point-3 i.e unmapped a2l files<br>
   * 6. Deleting unmapped a2l files in point-5<br>
   */
  private void changeInPidcA2L(final PIDCAttribute pidcAttribute) throws CommandException {
    if (pidcAttribute.getAttribute().getAttrLevel() == ApicConstants.SDOM_PROJECT_NAME_ATTR) {
      if (null == this.parentCommand.getNewValue()) { // checking null for unequal sdom value in variants
        for (SdomPver pver : this.pidcVers.getPVerSet()) {
          String pverName = pver.getPVERName();
          Map<Long, PIDCA2l> definedPidcA2lMap = this.pidcVers.getPidc().getDefinedPidcA2lMap();// fetch pidc_a2l entry
                                                                                                // for given pver
          List<PIDCA2l> pidcA2lList = new ArrayList<PIDCA2l>();
          for (PIDCA2l pidcA2l : definedPidcA2lMap.values()) {
            if ((pidcA2l.getPidcVersion() == null) && pidcA2l.getSdomPverName().equals(pverName)) { // extracting only
                                                                                                    // unmapped pidc_a2l
              pidcA2lList.add(pidcA2l);
            }
          }
          for (PIDCA2l pidcA2l : pidcA2lList) {
            CmdModPidcA2l cmdModPidcA2l = new CmdModPidcA2l(getDataProvider(), pidcA2l, true, true);
            this.childCmdStack.addCommand(cmdModPidcA2l);
          }
        }
      }
    }
  }

  @Override
  protected final Set<IPIDCAttribute> getAllVarAttrsForAttr() {
    final Set<IPIDCAttribute> retSet = new HashSet<IPIDCAttribute>();

    for (PIDCVariant var : this.pidcVers.getVariantsMap().values()) {
      // iCDM-1098
      retSet.add(var.getAttributes(true, false).get(getPidcAttrToMove().getAttribute().getAttributeID()));
    }
    return retSet;
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
    for (PIDCVariant variant : this.pidcVers.getVariantsMap().values()) {
      variant.resetChildrenLoaded();
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
   * {@inheritDoc} return the id of PIDC
   */
  @Override
  public Long getPrimaryObjectID() {
    // Since this command involves operation on multiple objects, the parent object's(PIDC) id is returned as the
    // primary object id
    return this.pidcVers == null ? null : this.pidcVers.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {
    return super.getTransactionSummary(this.summaryData);
  }

}
