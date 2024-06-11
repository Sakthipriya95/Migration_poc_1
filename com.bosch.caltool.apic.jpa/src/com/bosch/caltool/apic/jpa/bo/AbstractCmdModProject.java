/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Abstract class as base for all commands regarding project ID card,variant,sub-variant
 *
 * @author bne4cob
 */
public abstract class AbstractCmdModProject extends AbstractCommand {

  /**
   * New English name
   */
  protected String newNameEng;

  /**
   * New German name
   */
  protected String newNameGer;

  /**
   * Old English name
   */
  protected String oldNameEng;

  /**
   * Old German name
   */
  protected String oldNameGer;

  /**
   * New English description
   */
  protected String newDescEng;

  /**
   * New German description
   */
  protected String newDescGer;

  /**
   * Old English description
   */
  protected String oldDescEng;

  /**
   * Old German description
   */
  protected String oldDescGer;

  /**
   * Name attribute value for newly created PIDC.
   */
  protected AttributeValue nameValue;

  /**
   * Whether the delete mode should be used for delete/undelete
   */
  protected boolean unDelete;

  /**
   * Stack for storing child commands executed after creating the PIDC entity
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Command for creating/modifying the attribute value
   */
  protected CmdModAttributeValue cmdAttrValue;

  /**
   * Only for logging, could be PIDCName or VariantName or SubVariant name
   */
  protected String attrValName;

  /**
   * Command For Top Level Entity Icdm-470
   */
  protected CmdModTopLevelEntity cmdTopLevel;

  /**
   * @param dataProvider the Apic Data Provider
   */
  public AbstractCmdModProject(final ApicDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * @return whether PIDC name is updated
   */
  protected final boolean isNameUpdated() {
    return isNameEngUpdated() || isNameGerUpdated() || isDescEngUpdated() || isDescGerUpdated();
  }

  /**
   * @return whether English name is updated
   */
  private boolean isNameEngUpdated() {
    return isStringChanged(this.oldNameEng, this.newNameEng);
  }

  /**
   * @return whether German name is updated
   */
  private boolean isNameGerUpdated() {
    return isStringChanged(this.oldNameGer, this.newNameGer);
  }

  /**
   * @return whether English description is updated
   */
  private boolean isDescEngUpdated() {
    return isStringChanged(this.oldDescEng, this.newDescEng);
  }

  /**
   * @return whether German description is updated
   */
  private boolean isDescGerUpdated() {
    return isStringChanged(this.oldDescGer, this.newDescGer);
  }

  /**
   * Sets the name AttributeValue object of PIDC
   *
   * @param value the name AttributeValue object
   */
  public final void setNameValue(final AttributeValue value) {
    this.nameValue = value;
  }

  /**
   * Sets the English name
   *
   * @param name the English name
   */
  public final void setNameEng(final String name) {
    this.newNameEng = name;
  }

  /**
   * Sets the German name
   *
   * @param name the German name
   */
  public final void setNameGer(final String name) {
    this.newNameGer = name;
  }

  /**
   * Set the Englist description
   *
   * @param des the Englist description
   */
  public final void setDescEng(final String des) {
    this.newDescEng = des;
  }

  /**
   * Sets the German description
   *
   * @param des the German description
   */
  public final void setDescGer(final String des) {
    this.newDescGer = des;
  }

  @Override
  protected boolean dataChanged() {
    return isNameUpdated();
  }

  /**
   * Set the mode of delete to Un-delete. This method will undone the deletion of a PIDC or sub type. Note : this is
   * different from undo of a command.
   */
  public final void setUndelete() {
    this.unDelete = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {

    String commandModeText = getPrimaryObjectType();
    if (this.unDelete) {
      commandModeText = "UN-DELETE " + commandModeText;
    }
    return super.getString(commandModeText, getPrimaryObjectIdentifier());

  }

  /**
   * Update the name of this PIDC.
   *
   * @param valueID value ID
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  protected final void updateName(final Long valueID) throws CommandException {

    final AttributeValue modifyAttrValue = getDataProvider().getAttrValue(valueID);

    this.cmdAttrValue = new CmdModAttributeValue(getDataProvider(), modifyAttrValue, false);

    this.cmdAttrValue.setNewAttrValEng(this.newNameEng);
    this.cmdAttrValue.setNewAttrValGer(this.newNameGer);
    this.cmdAttrValue.setNewAttrValDescEng(this.newDescEng);
    this.cmdAttrValue.setNewAttrValDescGer(this.newDescGer);

    this.childCmdStack.addCommand(this.cmdAttrValue);

  }

  /**
   * Create an attribute value entity to set the PIDC's name.
   *
   * @param nameAttr attribute used for keeping the name values
   * @return the TabvAttrValue
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  protected final TabvAttrValue createNameValue(final Attribute nameAttr) throws CommandException {

    this.cmdAttrValue = new CmdModAttributeValue(getDataProvider(), nameAttr);

    this.cmdAttrValue.setNewAttrValEng(this.newNameEng);
    this.cmdAttrValue.setNewAttrValGer(this.newNameGer);
    this.cmdAttrValue.setNewAttrValDescEng(this.newDescEng);
    this.cmdAttrValue.setNewAttrValDescGer(this.newDescGer);

    this.childCmdStack.addCommand(this.cmdAttrValue);

    getEntityProvider().getDbValue(this.cmdAttrValue.getAttrValue().getValueID());

    return getEntityProvider().getDbValue(this.cmdAttrValue.getAttrValue().getValueID());
  }

  /**
   * iCDM-834
   *
   * @return CmdModAttributeValue
   */
  public CmdModAttributeValue getCmdAttrVal() {
    return this.cmdAttrValue;
  }

  /**
   * Update the Top Level entity Table for PIDC icdm-470
   *
   * @throws CommandException In case of parallel changes detected icdm-943
   */
  protected void updateTopLevelEntity() throws CommandException {
    this.cmdTopLevel = new CmdModTopLevelEntity(getDataProvider(), ApicConstants.TOP_LVL_ENT_ID_PIDC);
    this.childCmdStack.addCommand(this.cmdTopLevel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier;

    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
      case DELETE:
        objectIdentifier = this.attrValName;
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }
    return objectIdentifier;
  }
}
