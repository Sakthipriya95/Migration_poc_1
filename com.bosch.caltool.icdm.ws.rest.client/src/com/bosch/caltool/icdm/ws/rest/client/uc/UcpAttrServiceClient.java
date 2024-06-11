package com.bosch.caltool.icdm.ws.rest.client.uc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Ucp Attr
 *
 * @author MKL2COB
 */
public class UcpAttrServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public UcpAttrServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_UC, WsCommonConstants.RWS_UCPATTR);
  }

  /**
   * Get all Ucp Attr records in system
   *
   * @return Map. Key - id, Value - UcpAttr object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UcpAttr> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all Ucp Attr records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, UcpAttr>> type = new GenericType<Map<Long, UcpAttr>>() {};
    Map<Long, UcpAttr> retMap = get(wsTarget, type);
    LOGGER.debug("Ucp Attr records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get Ucp Attr using its id
   *
   * @param objId object's id
   * @return UcpAttr object
   * @throws ApicWebServiceException exception while invoking service
   */
  public UcpAttr getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, UcpAttr.class);
  }


  /**
   * Create a Ucp Attr record
   *
   * @param ucpAttrList object to create
   * @return created UcpAttr objects
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UcpAttr> create(final List<UcpAttr> ucpAttrList) throws ApicWebServiceException {
    GenericType<Map<Long, UcpAttr>> type = new GenericType<Map<Long, UcpAttr>>() {};
    return create(getWsBase(), ucpAttrList, type);
  }

  /**
   * Update multiple Ucp Attr records
   *
   * @param ucpAttrMap object to update
   * @return updated UcpAttr objects
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, UcpAttr> update(final Map<Long, UcpAttr> ucpAttrMap) throws ApicWebServiceException {
    GenericType<Map<Long, UcpAttr>> type = new GenericType<Map<Long, UcpAttr>>() {};
    return update(getWsBase(), ucpAttrMap, type);
  }


  /**
   * Delete a Ucp Attr record
   *
   * @param ucpAttrSet set of object id's to be deleted
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Set<UcpAttr> ucpAttrSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    delete(wsTarget, ucpAttrSet);
  }

}
