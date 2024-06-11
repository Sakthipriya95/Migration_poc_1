package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Focus Matrix Version Attribute
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionAttrServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public FocusMatrixVersionAttrServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_FOCUSMATRIXVERSIONATTR);
  }


  /**
   * Get all Focus Matrix version attributesrecords in system
   *
   * @param fmVersId focus matrix version id
   * @return Map. Key - id, Value - FocusMatrix object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, FocusMatrixVersionAttr> getFocusMatrixAttrForVersion(final Long fmVersId)
      throws ApicWebServiceException {
    LOGGER.debug("Get all Focus Matrix records for a focus matrix version ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FOCUS_MATRIX_ATTR_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, fmVersId);
    GenericType<Map<Long, FocusMatrixVersionAttr>> type = new GenericType<Map<Long, FocusMatrixVersionAttr>>() {};
    Map<Long, FocusMatrixVersionAttr> retMap = get(wsTarget, type);
    LOGGER.debug("Focus Matrix records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get Focus Matrix Version Attribute using its id
   *
   * @param objId object's id
   * @return FocusMatrixVersionAttr object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixVersionAttr getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, FocusMatrixVersionAttr.class);
  }

  /**
   * Create a Focus Matrix Version Attribute record
   *
   * @param obj object to create
   * @return created FocusMatrixVersionAttr object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixVersionAttr create(final FocusMatrixVersionAttr obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a Focus Matrix Version Attribute record
   *
   * @param obj object to update
   * @return updated FocusMatrixVersionAttr object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixVersionAttr update(final FocusMatrixVersionAttr obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a Focus Matrix Version Attribute record
   *
   * @param objId id of object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    delete(wsTarget);
  }

}
