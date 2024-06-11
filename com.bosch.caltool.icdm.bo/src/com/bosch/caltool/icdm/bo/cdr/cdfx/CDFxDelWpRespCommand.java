package com.bosch.caltool.icdm.bo.cdr.cdfx;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelvryWpResp;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;


/**
 * Command class for CDFxDelWpResp
 *
 * @author pdh2cob
 */
public class CDFxDelWpRespCommand extends AbstractCommand<CDFxDelWpResp, CDFxDelWpRespLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CDFxDelWpRespCommand(final ServiceData serviceData, final CDFxDelWpResp input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CDFxDelWpRespLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCDFxDelvryWpResp entity = new TCDFxDelvryWpResp();

    entity
        .setTCdfxDelivery(new CDFxDeliveryLoader(getServiceData()).getEntityObject(getInputData().getCdfxDeliveryId()));
    entity.setWp(new A2lWorkPackageLoader(getServiceData()).getEntityObject(getInputData().getWpId()));
    entity.setResp(new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getRespId()));

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {


    TCDFxDelvryWpResp entity = (new CDFxDelWpRespLoader(getServiceData())).getEntityObject(getInputData().getId());

    entity.setResp(new A2lResponsibilityLoader(getServiceData()).getEntityObject(getInputData().getRespId()));

    setUserDetails(COMMAND_MODE.UPDATE, entity);

    persistEntity(entity);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    TCDFxDelvryWpResp entity = (new CDFxDelWpRespLoader(getServiceData())).getEntityObject(getInputData().getId());
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // not required as of now
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
    // not required as of now
  }

}
