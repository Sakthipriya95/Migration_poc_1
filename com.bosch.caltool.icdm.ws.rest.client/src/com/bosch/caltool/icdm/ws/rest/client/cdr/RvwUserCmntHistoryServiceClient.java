package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Review Comment History
 *
 * @author PDH2COB
 */
public class RvwUserCmntHistoryServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public RvwUserCmntHistoryServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVWCMNTHISTORY);
  }

  /**
   * Create a Review Comment History record
   *
   * @param obj object to create
   * @return created RvwCmntHistory object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwUserCmntHistory create(final RvwUserCmntHistory obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Get RvwCmntHistory using its id
   *
   * @param objId object's id
   * @return RvwCmntHistory object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwUserCmntHistory get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, RvwUserCmntHistory.class);
  }


  /**
   * Delete a Review Comment History record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final RvwUserCmntHistory obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }


  /**
   * Gets the all Review History by user id.
   *
   * @param user nt id
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, RvwUserCmntHistory> getRvwCmntHistoryByUser(final Long user) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_RVW_CMNT_HISTORY_BY_USER)
        .queryParam(WsCommonConstants.RWS_QP_USER, user);

    GenericType<Map<Long, RvwUserCmntHistory>> type = new GenericType<Map<Long, RvwUserCmntHistory>>() {};
    return get(wsTarget, type);
  }

}
