package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetStructure;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for PidcDetailsStructure
 *
 * @author pdh2cob
 */
public class PidcDetStructureServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public PidcDetStructureServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_DET_STRUCTURE);
  }


  /**
   * Get PidcDetailsStructure using its id
   *
   * @param objId object's id
   * @return PidcDetStructure object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcDetStructure getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcDetStructure.class);
  }

  /**
   * Create a PIDC Details Structure record
   *
   * @param obj object to create
   * @return created PidcDetStructure object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcDetStructure create(final PidcDetStructure obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a PIDC Details Structure record
   *
   * @param obj object to update
   * @return updated PidcDetStructure object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcDetStructure update(final PidcDetStructure obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Delete a PIDC Details Structure record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final PidcDetStructure obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

  /**
   * @param pidcVersionId
   * @return Map<Long, PidcDetStructure>
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcDetStructure> getPidcDetStructForVersion(final Long pidcVersionId)
      throws ApicWebServiceException {
    LOGGER.debug("Get all PidcDetailsStructure records for PIDC Version");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_DET_STRUCT_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcVersionId);
    GenericType<Map<Long, PidcDetStructure>> type = new GenericType<Map<Long, PidcDetStructure>>() {};
    Map<Long, PidcDetStructure> retMap = get(wsTarget, type);
    LOGGER.debug("PidcDetailsStructure records loaded count = {}", retMap.size());
    return retMap;
  }

}
