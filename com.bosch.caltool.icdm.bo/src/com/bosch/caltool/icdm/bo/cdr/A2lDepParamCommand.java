package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TA2lDepParam;
import com.bosch.caltool.icdm.model.cdr.A2lDepParam;


/**
 * Command class for A2lDepParam
 *
 * @author UKT1COB
 */
public class A2lDepParamCommand extends AbstractCommand<A2lDepParam, A2lDepParamLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isDelete if true, delete mode, else Create mode
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lDepParamCommand(final ServiceData serviceData, final A2lDepParam input) throws IcdmException {
    super(serviceData, input, new A2lDepParamLoader(serviceData), COMMAND_MODE.CREATE);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    TA2lDepParam entity = new TA2lDepParam();

    setValuesToEntity(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValuesToEntity(final TA2lDepParam entity) {

    entity.setA2lFileId(getInputData().getA2lFileId());
    entity.setParamName(getInputData().getParamName());
    entity.setDependsOnParamName(getInputData().getDependsOnParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // NA
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
//NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // NA
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
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // NA
  }


}
