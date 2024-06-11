package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for WpArchival
 *
 * @author msp5cob
 */
public class WpArchivalServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public WpArchivalServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_WP_ARCHIVAL);
  }

  /**
   * Get WpArchival using its id
   *
   * @param objId object's id
   * @return WpArchival object
   * @throws ApicWebServiceException exception while invoking service
   */
  public WpArchival get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, WpArchival.class);
  }

  /**
   * Get WpArchival using its id Set
   *
   * @param objIdSet object's id Set
   * @return Rest response, with Map<Long, WpArchival>
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, WpArchival> getWpArchivalsMap(final Set<Long> objIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_ARCHIVALS_BY_IDS);
    GenericType<Map<Long, WpArchival>> type = new GenericType<Map<Long, WpArchival>>() {};

    for (Long objId : objIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    }

    Map<Long, WpArchival> ret = get(wsTarget, type);

    LOGGER.debug("WP Archival Results fetched, No. of records : {}", ret.size());

    return ret;
  }


  /**
   * @param pidcA2lId Pidc A2l Id
   * @return Set<WpArchival>
   * @throws ApicWebServiceException excpetion while invoking service
   */
  public Set<WpArchival> getBaselinesForPidcA2l(final Long pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_ARCHIVAL_BASELINE)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);

    GenericType<Set<WpArchival>> type = new GenericType<Set<WpArchival>>() {};

    return get(wsTarget, type);
  }


  /**
   * @param pidcVersionId Pidc Version Id (Optional)
   * @param pidcA2lId Pidc A2l Id (Optional)
   * @param variantId variantId, if -1 then considered as No-Variant Case (Optional)
   * @param respId respId (Optional)
   * @param wpName wpName (Optional)
   * @param nodeName nodeName (Optional)
   * @return Set<WpArchival>
   * @throws ApicWebServiceException excpetion while invoking service
   */
  public Set<WpArchival> getFilteredBaselinesForPidc(final Long pidcVersionId, final Long pidcA2lId,
      final Long variantId, final Long respId, final String wpName, final String nodeName)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WP_ARCHIVAL_BASELINE_FILTERED_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantId).queryParam(WsCommonConstants.RWS_QP_RESP_ID, respId)
        .queryParam(WsCommonConstants.RWS_QP_WP_ID, wpName).queryParam(WsCommonConstants.RWS_QP_NODE_NAME, nodeName);

    GenericType<Set<WpArchival>> type = new GenericType<Set<WpArchival>>() {};

    return get(wsTarget, type);
  }


}
