package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResp;
import com.bosch.caltool.icdm.model.a2l.A2lResp;


/**
 * Command class for Attribute
 *
 * @author dmo5cob
 */
public class A2lRespCommand extends AbstractCommand<A2lResp, A2lRespLoader> {


  /**
   * @param serviceData ServiceData
   * @param inputData A2lResp
   * @param cmdMode COMMAND_MODE
   * @throws IcdmException Exception
   */
  public A2lRespCommand(final ServiceData serviceData, final A2lResp inputData, final COMMAND_MODE cmdMode)
      throws IcdmException {
    super(serviceData, inputData, new A2lRespLoader(serviceData), cmdMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lResp dbA2lResp = new TA2lResp();
    dbA2lResp.setTPidcA2l(new PidcA2lLoader(getServiceData()).getEntityObject(getInputData().getPidcA2lId()));
    dbA2lResp.setWpRootId(getInputData().getWpRootId());
    dbA2lResp.setWpTypeId(getInputData().getWpTypeId());
    dbA2lResp.setCreatedUser(Long.toString(getServiceData().getUserId()));

    setUserDetails(COMMAND_MODE.CREATE, dbA2lResp);
    persistEntity(dbA2lResp);
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
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub

  }

}
