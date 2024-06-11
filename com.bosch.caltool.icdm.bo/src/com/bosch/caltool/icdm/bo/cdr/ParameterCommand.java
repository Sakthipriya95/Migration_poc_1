/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;

/**
 * @author rgo7cob
 */
public class ParameterCommand extends AbstractCommand<Parameter, ParameterLoader> {


  /**
   * @param serviceData
   * @param inputData
   * @param busObj
   * @param mode
   * @throws IcdmException IcdmException
   */
  public ParameterCommand(final ServiceData serviceData, final Parameter inputData, final ParameterLoader busObj,
      final COMMAND_MODE mode) throws IcdmException {
    super(serviceData, inputData, busObj, mode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    // No need
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    ParameterLoader loader = new ParameterLoader(getServiceData());
    TParameter entityObject = loader.getEntityObject(getInputData().getId());
    entityObject.setIscodeword(getInputData().getCodeWord());
    if (!CommonUtils.isEmptyString(getInputData().getpClassText())) {
      String dbText = ParameterClass.getParamClassT(getInputData().getpClassText()).getShortText();
      entityObject.setPclass(dbText);
    }

    entityObject.setIsbitwise(getInputData().getIsBitWise());
    entityObject.setIsBlackListLabel(booleanToYorN(getInputData().isBlackList()));
    entityObject.setHint(getInputData().getParamHint());
    entityObject.setLongname(getInputData().getLongName());

    setUserDetails(COMMAND_MODE.UPDATE, entityObject);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // no need for Implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
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
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
