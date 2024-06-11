package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwWpResp;
import com.bosch.caltool.icdm.model.cdr.RvwWpResp;


/**
 * Command class for Review Result WorkPackage Resp
 *
 * @author say8cob
 */
public class RvwWpRespCommand extends AbstractCommand<RvwWpResp, RvwWpRespLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwWpRespCommand(final ServiceData serviceData, final RvwWpResp input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwWpRespLoader(serviceData),
        ((isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE))));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwWpResp entity = new TRvwWpResp();
    TRvwResult tRvwResult = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    TA2lWorkPackage dbA2lWp = new A2lWorkPackageLoader(getServiceData()).getEntityObject(getInputData().getA2lWpId());
    TA2lResponsibility dbResp =
        new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getA2lRespId());
    entity.setTRvwResult(tRvwResult);
    entity.setTA2lWorkPackage(dbA2lWp);
    entity.settA2lResponsibility(dbResp);
    setUserDetails(COMMAND_MODE.CREATE, entity);

    Set<TRvwWpResp> tRvwWpResps = tRvwResult.getTRvwWpResps();
    if (null == tRvwWpResps) {
      tRvwWpResps = new HashSet<>();
    }
    tRvwWpResps.add(entity);
    tRvwResult.setTRvwWpResps(tRvwWpResps);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    RvwWpRespLoader loader = new RvwWpRespLoader(getServiceData());
    TRvwWpResp entity = loader.getEntityObject(getInputData().getId());

    TRvwResult tRvwResult = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    TA2lResponsibility dbResp =
        new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getA2lRespId());

    TA2lWorkPackage dbA2lWp = new A2lWorkPackageLoader(getServiceData()).getEntityObject(getInputData().getA2lWpId());
    entity.setTRvwResult(tRvwResult);
    entity.setTA2lWorkPackage(dbA2lWp);
    entity.settA2lResponsibility(dbResp);

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwWpRespLoader loader = new RvwWpRespLoader(getServiceData());
    TRvwWpResp entity = loader.getEntityObject(getInputData().getId());
    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    if (resultEntity != null) {
      resultEntity.getTRvwWpResps().remove(entity);
    }
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
