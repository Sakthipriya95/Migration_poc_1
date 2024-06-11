package com.bosch.caltool.icdm.ws.rest.client.comppkg;

import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for TCompPkgBc
 *
 * @author say8cob
 */
public class CompPkgBcServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public CompPkgBcServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_COMP, WsCommonConstants.RWS_COMPPKGBC);
  }


  /**
   * Get TCompPkgBc using its compPkgid
   *
   * @param compPkgid object's ID
   * @return CompPkgBc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public SortedSet<CompPkgBc> getBCByCompId(final Long compPkgid) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_COMP_BC_ID)
        .queryParam(WsCommonConstants.RWS_QP_COMP_ID, compPkgid);
    GenericType<SortedSet<CompPkgBc>> type = new GenericType<SortedSet<CompPkgBc>>() {};
    SortedSet<CompPkgBc> retMap = get(wsTarget, type);
    LOGGER.debug("TCompPkgBc records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get TCompPkgBc using its compPkgid
   *
   * @param compPkgid object's ID
   * @return CompPkgBcFc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgData getCompBcFcByCompId(final Long compPkgid) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_COMP_BC_FC).queryParam(WsCommonConstants.RWS_QP_COMP_ID, compPkgid);
    CompPkgData bcFc = get(wsTarget, CompPkgData.class);
    LOGGER.debug("TCompPkgBc & TCompPkgBcFc Loaded ");
    return bcFc;
  }

  /**
   * Gets the CompPackageBc.
   *
   * @param compPkgBcId the comp package id Bc
   * @return the comp package Bc
   * @throws ApicWebServiceException the apic web service exception
   */

  public CompPkgBc getCompPkgBcbyId(final Long compPkgBcId) throws ApicWebServiceException {
    LOGGER.debug("Get CompPkgBc record ");
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, compPkgBcId);
    return get(wsTarget, CompPkgBc.class);
  }


  /**
   * Create a TCompPkgBc record
   *
   * @param obj object to create
   * @return created CompPkgBc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgBc create(final CompPkgBc obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a TCompPkgBc record
   *
   * @param obj object to update
   * @param isUp
   * @return updated CompPkgBc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public CompPkgBc update(final CompPkgBc obj, final boolean isUp) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_IS_UP, isUp);
    return update(wsTarget, obj);
  }

  /**
   * Delete a TCompPkgBc record
   *
   * @param obj CompPkgBc object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final CompPkgBc obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

}
