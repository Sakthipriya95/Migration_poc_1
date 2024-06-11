package com.bosch.caltool.icdm.bo.cdr.cdfx;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDeliveryParam;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;


/**
 * Command class for CDFx Delivery Parameter
 *
 * @author pdh2cob
 */
public class CdfxDelvryParamCommand extends AbstractCommand<CdfxDelvryParam, CdfxDelvryParamLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CdfxDelvryParamCommand(final ServiceData serviceData, final CdfxDelvryParam input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CdfxDelvryParamLoader(serviceData), resolveCommandModeA(isDelete, isUpdate));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TCDFxDeliveryParam entity = new TCDFxDeliveryParam();

    entity.settCDFxDelvryWpResp(
        new CDFxDelWpRespLoader(getServiceData()).getEntityObject(getInputData().getCdfxDelvryWpRespId()));
    entity.setParam(new ParameterLoader(getServiceData()).getEntityObject(getInputData().getParamId()));
    TRvwResult entityObject =
        new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getRvwResultId());
    entityObject.addTCDFXDeliveryParam(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TCDFxDeliveryParam entity = (new CdfxDelvryParamLoader(getServiceData())).getEntityObject(getInputData().getId());
    entity.settCDFxDelvryWpResp(
        new CDFxDelWpRespLoader(getServiceData()).getEntityObject(getInputData().getCdfxDelvryWpRespId()));

    setUserDetails(COMMAND_MODE.UPDATE, entity);

    persistEntity(entity);
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

}
