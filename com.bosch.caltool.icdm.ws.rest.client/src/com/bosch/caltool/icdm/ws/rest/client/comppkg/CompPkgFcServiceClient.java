package com.bosch.caltool.icdm.ws.rest.client.comppkg;

import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for TCompPkgBcFc
 *
 * @author say8cob
 */
public class CompPkgFcServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public CompPkgFcServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_COMP, WsCommonConstants.RWS_COMP_BC_FC);
  }


  /**
   * Get TCompPkgBcFc using its id
   *
   * @param objId object's id
   * @return CompPkgFc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgFc getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, CompPkgFc.class);
  }

  /**
   * Get TCompPkgBcFc records using CompBcId
   *
   * @param compBcId Comp Bc Id id
   * @return Map. Key - id, Value - CompPkgFc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public SortedSet<CompPkgFc> getByCompBcId(final Long compBcId) throws ApicWebServiceException {
    LOGGER.debug("Get CompPkgBcFc records using CompBcId ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_COMP_BC_ID)
        .queryParam(WsCommonConstants.RWS_QP_COMP_BC_ID, compBcId);
    GenericType<SortedSet<CompPkgFc>> type = new GenericType<SortedSet<CompPkgFc>>() {};
    SortedSet<CompPkgFc> retMap = get(wsTarget, type);
    LOGGER.debug("CompPkgBcFc records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * @param compBcFcId object's ID
   * @return String
   * @throws ApicWebServiceException exception while invoking service
   */
  public String getPropertyTitle(final Long compBcFcId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_TITLE_BY_ID).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, compBcFcId);
    GenericType<String> type = new GenericType<String>() {};
    String retTitle = get(wsTarget, type);
    LOGGER.debug("CompBC record found ={}", retTitle);
    return retTitle;
  }

  /**
   * Create a CompPkgBcFc record
   *
   * @param obj object to create
   * @return created CompPkgFc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgFc create(final CompPkgFc obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a CompPkgBcFc record
   *
   * @param obj object to update
   * @return updated CompPkgFc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgFc update(final CompPkgFc obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a CompPkgBcFc record
   *
   * @param compPkgFc object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final CompPkgFc compPkgFc) throws ApicWebServiceException {
    delete(getWsBase(), compPkgFc);
  }

}
