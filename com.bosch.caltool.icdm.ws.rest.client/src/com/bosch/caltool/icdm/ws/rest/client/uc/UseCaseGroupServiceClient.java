package com.bosch.caltool.icdm.ws.rest.client.uc;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseDetailsModel;
import com.bosch.caltool.icdm.model.uc.UsecaseTreeGroupModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Usecase Group
 *
 * @author MKL2COB
 */
public class UseCaseGroupServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public UseCaseGroupServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_UC, WsCommonConstants.RWS_USECASEGROUP);
  }


  /**
   * @return UsecaseTreeGroupModel
   * @throws ApicWebServiceException exception from servers
   */
  public UsecaseTreeGroupModel getUseCaseTreeDataModel() throws ApicWebServiceException {
    LOGGER.debug("Get Usecase Tree View Data ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_UC_TREE_DATA);
    return get(wsTarget, UsecaseTreeGroupModel.class);

  }

  /**
   * @return UsecaseDetailsModel
   * @throws ApicWebServiceException exception from servers
   */
  public UsecaseDetailsModel getUseCaseDetailsModel() throws ApicWebServiceException {
    LOGGER.debug("Get Usecase details  Data ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_UC_DETAILS_DATA);
    return get(wsTarget, UsecaseDetailsModel.class);

  }

  /**
   * Get Usecase Group using its id
   *
   * @param objId object's id
   * @return UseCaseGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseGroup getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, UseCaseGroup.class);
  }

  /**
   * Create a Usecase Group record
   *
   * @param obj object to create
   * @return created UseCaseGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseGroup create(final UseCaseGroup obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a Usecase Group record
   *
   * @param obj object to update
   * @return updated UseCaseGroup object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UseCaseGroup update(final UseCaseGroup obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

}
