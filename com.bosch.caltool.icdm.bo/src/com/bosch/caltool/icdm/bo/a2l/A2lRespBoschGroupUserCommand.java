package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsiblityBshgrpUsr;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;


/**
 * Command class for A2L Responsibility Bosch Group User
 *
 * @author PDH2COB
 */
public class A2lRespBoschGroupUserCommand extends AbstractCommand<A2lRespBoschGroupUser, A2lRespBoschGroupUserLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public A2lRespBoschGroupUserCommand(final ServiceData serviceData, final A2lRespBoschGroupUser input,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new A2lRespBoschGroupUserLoader(serviceData), getCommandMode(isDelete));
  }

  private static COMMAND_MODE getCommandMode(final boolean isDelete) {
    if (isDelete) {
      return COMMAND_MODE.DELETE;
    }
    return COMMAND_MODE.CREATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TA2lResponsiblityBshgrpUsr entity = new TA2lResponsiblityBshgrpUsr();

    TA2lResponsibility tA2lResp =
        new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getA2lRespId());
    entity.setTabvApicUser(new UserLoader(getServiceData()).getEntityObject(getInputData().getUserId()));

    tA2lResp.addBoschGrpUser(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
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
    A2lRespBoschGroupUserLoader loader = new A2lRespBoschGroupUserLoader(getServiceData());
    TA2lResponsiblityBshgrpUsr entity = loader.getEntityObject(getInputData().getId());

    // remove from referencing entities
    entity.getTabvApicUser().removeBoschGrpUser(entity);
    entity.getTA2lResponsibility().removeBoschGrpUser(entity);

    getEm().remove(entity);
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
