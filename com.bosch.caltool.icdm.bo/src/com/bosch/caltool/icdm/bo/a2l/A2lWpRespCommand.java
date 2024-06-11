package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResp;
import com.bosch.caltool.icdm.model.a2l.A2lWpResp;


/**
 * Command class for Attribute
 *
 * @author dmo5cob
 */
public class A2lWpRespCommand extends AbstractCommand<A2lWpResp, A2lWpRespLoader> {


  /**
   * @param serviceData ServiceData
   * @param inputData A2lWpResp
   * @param cmdMode COMMAND_MODE
   * @throws IcdmException Exception
   */
  public A2lWpRespCommand(final ServiceData serviceData, final A2lWpResp inputData, final COMMAND_MODE cmdMode)
      throws IcdmException {
    super(serviceData, inputData, new A2lWpRespLoader(serviceData), cmdMode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lWpResp dbA2LWpResp = new TA2lWpResp();
    dbA2LWpResp.setTA2lResp(new A2lRespLoader(getServiceData()).getEntityObject(getInputData().getA2lRespId()));
    dbA2LWpResp.setTWpResp(new WpRespLoader(getServiceData()).getEntityObject(getInputData().getWpRespId()));
    dbA2LWpResp.setTA2lGroup(new IcdmA2lGroupLoader(getServiceData()).getEntityObject(getInputData().getA2lGroupId()));
    setUserDetails(COMMAND_MODE.CREATE, dbA2LWpResp);
    persistEntity(dbA2LWpResp);
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
    return isObjectChanged(getInputData().getA2lGroupId(), getOldData().getA2lGroupId()) ||
        isObjectChanged(getInputData().getA2lRespId(), getOldData().getA2lRespId()) ||
        isObjectChanged(getInputData().getWpId(), getOldData().getWpId()) ||
        isObjectChanged(getInputData().getWpRespId(), getOldData().getWpRespId());
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
    A2lWpRespLoader loader = new A2lWpRespLoader(getServiceData());
    TA2lWpResp dbA2lWpResp = loader.getEntityObject(getObjId());
    if (isObjectChanged(getInputData().getWpRespId(), getOldData().getWpRespId())) {
      dbA2lWpResp.setTWpResp(new WpRespLoader(getServiceData()).getEntityObject(getInputData().getWpRespId()));
      // IMP: update the associated entity also
      new WpRespLoader(getServiceData()).getEntityObject(getInputData().getWpRespId()).addTA2lWpResp(dbA2lWpResp);
    }
    setUserDetails(COMMAND_MODE.UPDATE, dbA2lWpResp);
  }
}
