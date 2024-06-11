package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidityCreationModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PredefinedValidity
 *
 * @author pdh2cob
 */
public class PredefinedValidityServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PredefinedValidityServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PREDEFINEDVALIDITY);
  }


  /**
   * Get PredefinedValidity using its id
   *
   * @param objId object's id
   * @return PredefinedValidity object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PredefinedValidity getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PredefinedValidity.class);
  }

  /**
   * Get PredefinedValidity using value id
   *
   * @param objId object's id
   * @return PredefinedValidity object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<PredefinedValidity> getByValueId(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PREDEFINEDATTRVALUE).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    GenericType<Set<PredefinedValidity>> type = new GenericType<Set<PredefinedValidity>>() {};
    return get(wsTarget, type);
  }

  /**
   * Create a PredefinedValidity record
   *
   * @param obj object to create
   * @return created PredefinedValidity object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PredefinedValidity create(final PredefinedValidity obj) throws ApicWebServiceException {
    return post(getWsBase(), obj, PredefinedValidity.class);
  }

  /**
   * Update a PredefinedValidity record
   *
   * @param obj object to update
   * @return updated PredefinedValidity object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PredefinedValidity update(final PredefinedValidity obj) throws ApicWebServiceException {
    return put(getWsBase(), obj, PredefinedValidity.class);
  }

  /**
   * Delete a PredefinedValidity record
   *
   * @param objId id of object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    delete(wsTarget);
  }


  /**
   * @param model PredefinedValidityCreationModel
   * @return PredefinedValidity
   * @throws ApicWebServiceException exception while invoking service
   */
  public Set<PredefinedValidity> createValidity(final PredefinedValidityCreationModel model)
      throws ApicWebServiceException {
    GenericType<Set<PredefinedValidity>> type = new GenericType<Set<PredefinedValidity>>() {};
    return create(getWsBase(), model, type);

  }

}
