package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Focus Matrix Version
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public FocusMatrixVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_FOCUSMATRIXVERSION);
  }


  /**
   * Get all Focus Matrix records in system
   *
   * @param pidcVersId focus matrix version id
   * @return Map. Key - id, Value - FocusMatrix object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, FocusMatrixVersion> getFocusMatrixVersionForPidcVers(final Long pidcVersId)
      throws ApicWebServiceException {
    LOGGER.debug("Get all Focus Matrix version records for a pidc version ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FOCUS_MATRIX_VERSION_FOR_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcVersId);
    GenericType<Map<Long, FocusMatrixVersion>> type = new GenericType<Map<Long, FocusMatrixVersion>>() {};
    Map<Long, FocusMatrixVersion> retMap = get(wsTarget, type);
    LOGGER.debug("Focus Matrix records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get Focus Matrix Version using its id
   *
   * @param objId object's id
   * @return FocusMatrixVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixVersion getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, FocusMatrixVersion.class);
  }

  /**
   * Create a Focus Matrix Version record
   *
   * @param obj object to create
   * @return created FocusMatrixVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixVersion create(final FocusMatrixVersion obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a Focus Matrix Version record
   *
   * @param obj object to update
   * @return updated FocusMatrixVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixVersion update(final FocusMatrixVersion obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a Focus Matrix Version record
   *
   * @param objId id of object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    delete(wsTarget);
  }

}
