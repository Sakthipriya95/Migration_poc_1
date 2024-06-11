/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.notification.ChangeType;
import com.bosch.caltool.dmframework.notification.ChangedData;
import com.bosch.caltool.dmframework.notification.DisplayEventSource;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TParamAttr;


/**
 * ICDM-1033 This class is to add or delete Param Attribute
 *
 * @author rgo7cob
 */
public class CmdModParamAttr extends AbstractCmdModParamAttr {

  private static final String PARAM_ATTR_ENTITY_ID = "PARAM_ATTR_ENTITY_ID";


  /**
   * Constructor
   *
   * @param dataProvider ApicDataProvider
   * @param cdrParam CDRFuncParameter
   * @param attr attribute List
   */
  protected CmdModParamAttr(final CDRDataProvider dataProvider, final CDRFuncParameter cdrParam, final Attribute attr) {
    super(dataProvider, cdrParam, attr);
  }

  /**
   * Constructor
   *
   * @param dataProvider CDRDataProvider
   * @param parameterAttribute paramAttrList
   * @throws SsdInterfaceException
   */
  protected CmdModParamAttr(final CDRDataProvider dataProvider, final ParameterAttribute parameterAttribute)
      throws SsdInterfaceException {
    super(dataProvider, parameterAttribute);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void rollBackDataModel() {
    // If the deletion fails then remove the Data to the map.
    if (this.commandMode == COMMAND_MODE.INSERT) {
      Set<ParameterAttribute> paramAttrSet = getDataCache().getParamDependencyMap().get(this.paramID);
      if (CommonUtils.isNotNull(paramAttrSet)) {
        paramAttrSet.remove(this.paramAttr);
      }
      getDataCache().getParamAttrMap().remove(this.paramAttr.getID());
    }
    // If the deletion fails then add the Data to the map.
    if (this.commandMode == COMMAND_MODE.DELETE) {
      Set<ParameterAttribute> paramAttrSet = getDataCache().getParamDependencyMap().get(this.paramID);
      if (CommonUtils.isNotNull(paramAttrSet)) {
        paramAttrSet.add((ParameterAttribute) this.paramAttr);
      }
      getDataCache().getParamAttrMap().put(this.paramAttr.getID(), (ParameterAttribute) this.paramAttr);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {
    // Changes made with Single Attribute Param Insert
    final TParamAttr dbParamAttr = new TParamAttr();
    dbParamAttr.setTabvAttribute(getEntityProvider().getDbAttribute(this.attr.getAttributeID()));
    dbParamAttr.setTParameter(getEntityProvider().getDbFunctionParameter(this.paramID));
    getEntityProvider().registerNewEntity(dbParamAttr);
    setUserDetails(COMMAND_MODE.INSERT, dbParamAttr, PARAM_ATTR_ENTITY_ID + ":" + dbParamAttr.getParamAttrId());
    this.newParamAttrID = dbParamAttr.getParamAttrId();
    this.paramAttr = new ParameterAttribute(getDataProvider(), this.newParamAttrID);
    getDataCache().getParamAttrMap().put(this.newParamAttrID, (ParameterAttribute) this.paramAttr);
    // Delete the Param attr from dep Map icdm-1088
    Set<ParameterAttribute> paramAttrSet = getDataCache().getParamDependencyMap().get(this.paramID);
    if (CommonUtils.isNull(paramAttrSet)) {
      paramAttrSet = new TreeSet<ParameterAttribute>();
      paramAttrSet.add((ParameterAttribute) this.paramAttr);
      getDataCache().getParamDependencyMap().put(this.paramID, paramAttrSet);
    }
    paramAttrSet.add((ParameterAttribute) this.paramAttr);

    getChangedData().put(this.newParamAttrID,
        new ChangedData(ChangeType.INSERT, this.newParamAttrID, TParamAttr.class, DisplayEventSource.COMMAND));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void executeUpdateCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Changes made with Single Attribute Param Delete
    long paramId = 0;
    try {
      paramId = this.paramAttr.getParameter().getID();
    }
    catch (SsdInterfaceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final ChangedData chdata =
        new ChangedData(ChangeType.DELETE, this.paramAttr.getID(), TParamAttr.class, DisplayEventSource.COMMAND);
    final TParamAttr dbParamAttr = getEntityProvider().getDbParamAttr(this.paramAttr.getID());
    setUserDetails(COMMAND_MODE.DELETE, dbParamAttr, PARAM_ATTR_ENTITY_ID + ":" + dbParamAttr.getParamAttrId());
    // Delete the Param attr from dep Map icdm-1088
    Set<ParameterAttribute> paramAttrSet = getDataCache().getParamDependencyMap().get(paramId);
    if (CommonUtils.isNotNull(paramAttrSet)) {
      paramAttrSet.remove(this.paramAttr);
    }
    getDataCache().getParamAttrMap().remove(this.paramAttr);
    getChangedData().put(this.paramAttr.getID(), chdata);
    getEntityProvider().deleteEntity(dbParamAttr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void undoUpdateCommand() throws CommandException {
    // Not Applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() throws CommandException {
    // not applicable

  }

  /**
   * {@inheritDoc} returns true if the class or isCodeWord of the parameter is changed
   */
  @Override
  protected final boolean dataChanged() {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getPrimaryObjectType() {
    return "Parameter Attribute";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier;
    // Pmd changes made
    if ((this.commandMode == COMMAND_MODE.INSERT) || (this.commandMode == COMMAND_MODE.DELETE)) {
      objectIdentifier = this.paramName + " and Attribute " + this.attrName;
    }
    else {
      objectIdentifier = " INVALID!";
    }

    return objectIdentifier;
  }


}
