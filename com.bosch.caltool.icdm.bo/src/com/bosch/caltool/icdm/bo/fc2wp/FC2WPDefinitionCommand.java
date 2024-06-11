/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpDefinition;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author bne4cob
 */
public class FC2WPDefinitionCommand extends AbstractCommand<FC2WPDef, FC2WPDefLoader> {

  /**
   * @param serviceData service Data
   * @param inputDef input Definition
   * @throws IcdmException error when initializing
   */
  public FC2WPDefinitionCommand(final ServiceData serviceData, final FC2WPDef inputDef) throws IcdmException {
    super(serviceData, inputDef, new FC2WPDefLoader(serviceData), COMMAND_MODE.CREATE);
  }

  @Override
  public void create() throws IcdmException {

    TFc2wpDefinition dbDef = new TFc2wpDefinition();

    dbDef.setFc2wpName(getInputData().getName());
    dbDef.setFc2wpDescEng(getInputData().getDescriptionEng());
    dbDef.setFc2wpDescGer(getInputData().getDescriptionGer());

    TabvAttrValue divValue = getEm().find(TabvAttrValue.class, getInputData().getDivisionValId());
    dbDef.setTabvAttrValueDiv(divValue);

    setUserDetails(COMMAND_MODE.CREATE, dbDef);

    persistEntity(dbDef);

    // Create a working set version
    FC2WPVersion wsVersion = new FC2WPVersion();
    wsVersion.setFcwpDefId(dbDef.getFcwpDefId());
    wsVersion.setMajorVersNo(0L);
    wsVersion.setMinorVersNo(0L);
    wsVersion.setWorkingSet(true);

    FC2WPVersionCommand versCmd = new FC2WPVersionCommand(getServiceData(), wsVersion);
    versCmd.setRefFcwpDefId(getInputData().getRefFcwpDefId());
    executeChildCommand(versCmd);

    NodeAccess access = new NodeAccess();
    access.setNodeId(dbDef.getFcwpDefId());
    access.setNodeType("FC2WP_DEF");
    access.setUserId(getServiceData().getUserId());
    access.setOwner(true);

    NodeAccessCommand accessCommand = new NodeAccessCommand(getServiceData(), access);
    executeChildCommand(accessCommand);

  }


  @Override
  protected void delete() {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws CommandException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException when object is not found
   */
  @Override
  protected void doPostCommit() throws DataException {
    // No actions
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
