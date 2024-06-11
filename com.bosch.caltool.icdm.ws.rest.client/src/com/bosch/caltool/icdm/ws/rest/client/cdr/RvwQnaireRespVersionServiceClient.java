package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVersionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for RvwQnaireRespVersion
 *
 * @author say8cob
 */
public class RvwQnaireRespVersionServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public RvwQnaireRespVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVWQNAIRERESPVERSION);
  }


  /**
   * Get RvwQnaireRespVersion using its id
   *
   * @param objId object's id
   * @return RvwQnaireRespVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwQnaireRespVersion getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, RvwQnaireRespVersion.class);
  }


  /**
   * Get all RvwQnaireRespVersion records in system
   *
   * @param qnaireRespId as input
   * @return Set. RvwQnaireRespVersion object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, RvwQnaireRespVersion> getQnaireRespVersionsByRespId(final Long qnaireRespId)
      throws ApicWebServiceException {
    LOGGER.debug("Get all RvwQnaireRespVersion records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_QNAIRE_RESP_VERS_BY_RESPID)
        .queryParam(WsCommonConstants.RVW_QNAIRE_RESP_ID, qnaireRespId);

    GenericType<Map<Long, RvwQnaireRespVersion>> type = new GenericType<Map<Long, RvwQnaireRespVersion>>() {};
    Map<Long, RvwQnaireRespVersion> retMap = get(wsTarget, type);
    LOGGER.debug("RvwQnaireRespVersion records loaded count = {}", retMap.size());
    return retMap;
  }


  /**
   * @param qnaireRespVersion QnaireRespVersionModel
   * @return RvwQnaireRespVersion
   * @throws ApicWebServiceException exception from web service
   */
  public RvwQnaireRespVersion create(final RvwQnaireRespVersion qnaireRespVersion) throws ApicWebServiceException {
    return create(getWsBase(), qnaireRespVersion, RvwQnaireRespVersion.class);
  }

  /**
   * @param qnaireRespVersion QnaireRespVersionModel
   * @return RvwQnaireRespVersion
   * @throws ApicWebServiceException exception from web service
   */
  public RvwQnaireRespVersion update(final RvwQnaireRespVersion qnaireRespVersion) throws ApicWebServiceException {
    return update(getWsBase(), qnaireRespVersion);
  }


  /**
   * @param qnaireRespVersDataSet qnaireDataSet
   * @return Qnaire resp id , QnaireRespVersionData
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<Long, QnaireRespVersionData> getLatestRvwQnaireRespVersion(
      final Set<QnaireRespStatusData> qnaireRespVersDataSet)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_LATEST_RVW_QNAIRE_RESP_VERS);
    GenericType<Map<Long, QnaireRespVersionData>> type = new GenericType<Map<Long, QnaireRespVersionData>>() {};
    return post(wsTarget, qnaireRespVersDataSet, type);
  }
}
