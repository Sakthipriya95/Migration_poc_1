/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Set;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author bne4cob
 */
public abstract class AbstractCmdMoveAttribute extends AbstractCommand { // NOPMD

  /**
   * Command to insert/delete parent attribute.
   */
  protected AbstractCmdModProjAttr parentCommand;

  /**
   * Stack for storing insert/delete commands for child attributes
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Move downwards the PIDC tree. If true move from PIDC to PIDC Variant/Variant to Subvariant to variant.
   */
  private final boolean moveDown;

  /**
   * Only for logging
   */
  private String attributeName;
  /**
   * Only for logging
   */
  private String parentName;

  /**
   * The PIDC/Variant/Subvariant attribute being moved
   */
  private final IPIDCAttribute pidcAttrToMove;

  /**
   * @param dataProvider the apic data provider instance
   * @param attrToMove IPIDC attribute to move
   * @param moveDown Move downwards the PIDC tree.<br>
   *          true - PIDC -> Variant or Variant -> Subvariant<br>
   *          false - Variant -> PIDC or Subvariant -> Variant
   */
  protected AbstractCmdMoveAttribute(final ApicDataProvider dataProvider, final IPIDCAttribute attrToMove,
      final boolean moveDown) {
    super(dataProvider);
    this.moveDown = moveDown;
    this.commandMode = COMMAND_MODE.UPDATE;
    this.pidcAttrToMove = attrToMove;
    // ICDM-1402
    if (null != attrToMove) {
      this.attributeName = attrToMove.getName();
      this.parentName = attrToMove.getPidcVersion().getName();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {
    String objIdentifier;
    // Move the attribute from higher to lower level
    if (this.moveDown) {
      objIdentifier = "MOVED DOWN " + this.attributeName + " from " + this.parentName;
    }
    else {
      objIdentifier = "MOVED UP " + this.attributeName + " to " + this.parentName;
    }
    return super.getString("", objIdentifier);
  }

  /**
   * @return the moveDown
   */
  protected final boolean isMoveDown() {
    return this.moveDown;
  }

  /**
   * @return the pidcAttrToMove
   */
  protected IPIDCAttribute getPidcAttrToMove() {
    return this.pidcAttrToMove;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "Move Attribute";
  }

  /**
   * Set the parent command properties while moving the command upwards
   */
  protected final void setParentCmdPropsMvUp() { // NOPMD
    boolean setUsedFlag = true, setPartNo = true, setDesc = true, setSpecLink = true, setValue = true;

    final String used = getPidcAttrToMove().getIsUsed();
    final String partNo = getPidcAttrToMove().getPartNumber();
    final String desc = getPidcAttrToMove().getAdditionalInfoDesc();
    final String specLink = getPidcAttrToMove().getSpecLink();
    final AttributeValue value = getPidcAttrToMove().getAttributeValue();

    for (IPIDCAttribute varAttr : getAllVarAttrsForAttr()) {
      // iCDM-1098
      if (varAttr != null) {
        if (setUsedFlag && (ApicUtil.compare(used, varAttr.getIsUsed()) != 0)) {
          setUsedFlag = false;
        }

        if (setValue && (ApicUtil.compare(value, varAttr.getAttributeValue()) != 0)) {
          setValue = false;
        }

        if (setPartNo && (ApicUtil.compare(partNo, varAttr.getPartNumber()) != 0)) {
          setPartNo = false;
        }

        if (setSpecLink && (ApicUtil.compare(specLink, varAttr.getSpecLink()) != 0)) {
          setSpecLink = false;
        }

        if (setDesc && (ApicUtil.compare(desc, varAttr.getAdditionalInfoDesc()) != 0)) {
          setDesc = false;
        }
      }
    }

    if (setUsedFlag) {
      this.parentCommand.setUsed(used);
    }
    else {
      this.parentCommand.setUsed(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
    }

    if (setValue) {
      this.parentCommand.setValue(value);
    }
    else {
      this.parentCommand.setValue(null);
    }

    if (setPartNo) {
      this.parentCommand.setNewPartNumber(partNo);
    }
    else {
      this.parentCommand.setNewPartNumber("");
    }

    if (setSpecLink) {
      this.parentCommand.setNewSpecLink(specLink);
    }
    else {
      this.parentCommand.setNewSpecLink("");
    }

    if (setDesc) {
      this.parentCommand.setNewDesc(desc);
    }
    else {
      this.parentCommand.setNewDesc("");
    }
    // while moving an attribute to variant , the transfer to vcdm flag should be set
    this.parentCommand.setNewTrnfrVcdmFlag(ApicConstants.YES);
  }

  /**
   * @return all the attributes in the children(variants/sub variants) for the attribute in the selected project
   *         attribute
   */
  protected abstract Set<IPIDCAttribute> getAllVarAttrsForAttr();

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    return this.attributeName;
  }

  // ICDM-1402
  /**
   * @param parentName the parentName to set
   */
  public void setParentName(final String parentName) {
    this.parentName = parentName;
  }

  /**
   * @param attributeName the attributeName to set
   */
  public void setAttributeName(final String attributeName) {
    this.attributeName = attributeName;
  }
}
