/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.fc2wp;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.PTTypeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapPtType;
import com.bosch.caltool.icdm.database.entity.a2l.TFc2wpMapping;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapPtType;


/**
 * @author bne4cob
 */
public class FC2WPMapPtTypeCommand extends AbstractCommand<FC2WPMapPtType, FC2WPMapPtTypeLoader> {

  /**
   * @param serviceData service Data
   * @param inputData FC2WPMapPtType to create
   * @throws IcdmException any exception
   */
  public FC2WPMapPtTypeCommand(final ServiceData serviceData, final FC2WPMapPtType inputData) throws IcdmException {
    super(serviceData, inputData, new FC2WPMapPtTypeLoader(serviceData), COMMAND_MODE.CREATE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TFc2wpMapPtType dbMapType = new TFc2wpMapPtType();

    TFc2wpMapping dbMapping = new FC2WPMappingLoader(getServiceData()).getEntityObject(getInputData().getFcwpMapId());
    dbMapping.addTFc2wpMapPtType(dbMapType);
    dbMapType.setTPowerTrainType(new PTTypeLoader(getServiceData()).getEntityObject(getInputData().getPtTypeId()));
    setUserDetails(COMMAND_MODE.CREATE, dbMapType);

    persistEntity(dbMapType);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
