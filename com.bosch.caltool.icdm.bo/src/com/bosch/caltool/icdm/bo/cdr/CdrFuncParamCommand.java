/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.model.a2l.Parameter;

/**
 * @author rgo7cob
 */
public class CdrFuncParamCommand extends AbstractCommand<Parameter, ParameterLoader> {

  /**
   * @param serviceData
   * @param inputData
   * @param busObj
   * @param mode
   * @throws IcdmException
   */
  protected CdrFuncParamCommand(final ServiceData serviceData, final Parameter inputData, final ParameterLoader busObj,
      final COMMAND_MODE mode) throws IcdmException {
    super(serviceData, inputData, busObj, mode);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    // Not needed

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {

    ParameterLoader loader = new ParameterLoader(getServiceData());
    TParameter tParam = loader.getEntityObject(getInputData().getId());
    setNewParameterFields(tParam);
    // Update modified data
    // set ModifiedDate and User
    setUserDetails(COMMAND_MODE.UPDATE, tParam);

  }

  /**
   * This method updates the database with the new parameter values
   *
   * @param modifiedparam TParameter
   */
  private void setNewParameterFields(final TParameter modifiedparam) {

    if (getInputData().getpClassText() == null) {
      modifiedparam.setPclass(null);
    }
    else {
      modifiedparam.setPclass(getInputData().getpClassText());
    }

    modifiedparam.setIscodeword(getInputData().getCodeWord());

    modifiedparam.setIsbitwise(getInputData().getIsBitWise());

    modifiedparam.setIsBlackListLabel(booleanToYorN(getInputData().isBlackList()));

    // Set new Hint Value
    modifiedparam.setHint(getInputData().getParamHint());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // Not needed

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {

    return false;
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
    // No need

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {

    return true;
  }

}
