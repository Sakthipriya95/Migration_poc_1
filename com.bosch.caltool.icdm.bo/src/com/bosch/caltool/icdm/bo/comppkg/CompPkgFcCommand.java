package com.bosch.caltool.icdm.bo.comppkg;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBc;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkgBcFc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;


/**
 * Command class for TCompPkgBcFc
 *
 * @author say8cob
 */
public class CompPkgFcCommand extends AbstractCommand<CompPkgFc, CompPkgFcLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CompPkgFcCommand(final ServiceData serviceData, final CompPkgFc input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CompPkgFcLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCompPkgBcFc entity = new TCompPkgBcFc();
    TCompPkgBc dbCompPkgBc = new CompPkgBcLoader(getServiceData()).getEntityObject(getInputData().getCompBcId());

    entity.setTCompPkgBc(dbCompPkgBc);
    entity.setFcName(getInputData().getFcName());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    dbCompPkgBc.addTCompPkgBcFc(entity);

    persistEntity(entity);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TCompPkgBcFc entity = new CompPkgFcLoader(getServiceData()).getEntityObject(getInputData().getId());
    TCompPkgBc dbCompPkgBc = new CompPkgBcLoader(getServiceData()).getEntityObject(getInputData().getCompBcId());

    entity.setTCompPkgBc(dbCompPkgBc);
    entity.setFcName(getInputData().getFcName());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TCompPkgBcFc fcEntity = new CompPkgFcLoader(getServiceData()).getEntityObject(getInputData().getId());
    TCompPkgBc bcEntity = new CompPkgBcLoader(getServiceData()).getEntityObject(fcEntity.getTCompPkgBc().getCompBcId());

    bcEntity.removeTCompPkgBcFc(fcEntity);

    getEm().remove(fcEntity);
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
