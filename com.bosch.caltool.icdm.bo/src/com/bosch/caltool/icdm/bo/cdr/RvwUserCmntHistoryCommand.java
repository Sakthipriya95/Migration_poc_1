package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwUserCmntHistory;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;


/**
 * Command class for Review Comment History
 *
 * @author PDH2COB
 */
public class RvwUserCmntHistoryCommand extends AbstractCommand<RvwUserCmntHistory, RvwUserCmntHistoryLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwUserCmntHistoryCommand(final ServiceData serviceData, final RvwUserCmntHistory input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwUserCmntHistoryLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwUserCmntHistory entity = new TRvwUserCmntHistory();

    entity.setRvwComment(getInputData().getRvwComment());
    TabvApicUser userEntity = new UserLoader(getServiceData()).getEntityObject(getInputData().getRvwCmntUserId());
    entity.setRvwCmntUser(userEntity);

    userEntity.gettRvwUserCmntHistoryList().add(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // not required
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwUserCmntHistoryLoader loader = new RvwUserCmntHistoryLoader(getServiceData());
    TRvwUserCmntHistory entity = loader.getEntityObject(getInputData().getId());

    // remove from user entity
    new UserLoader(getServiceData()).getEntityObject(getInputData().getRvwCmntUserId()).gettRvwUserCmntHistoryList()
        .remove(entity);

    getEm().remove(entity);
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

}
