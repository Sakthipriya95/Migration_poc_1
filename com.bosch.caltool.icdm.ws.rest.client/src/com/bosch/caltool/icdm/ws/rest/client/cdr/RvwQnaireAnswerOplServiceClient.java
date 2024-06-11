/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 */
public class RvwQnaireAnswerOplServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public RvwQnaireAnswerOplServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_QNAIRE_ANSWER_OPL);
  }

  /**
   * Get RvwQnaireAnswerOpl using its id
   *
   * @param objId object's id
   * @return RvwQnaireAnswer object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwQnaireAnswerOpl getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, RvwQnaireAnswerOpl.class);
  }

  /**
   * Creates the.
   *
   * @param rvwQnaireAnswerOpl List<RvwQnaireAnswerOpl> the rvw qnaire answer
   * @return the rvw qnaire answer
   * @throws ApicWebServiceException the apic web service exception
   */
  public List<RvwQnaireAnswerOpl> create(final List<RvwQnaireAnswerOpl> rvwQnaireAnswerOpl)
      throws ApicWebServiceException {
    GenericType<List<RvwQnaireAnswerOpl>> type = new GenericType<List<RvwQnaireAnswerOpl>>() {};
    return post(getWsBase(), rvwQnaireAnswerOpl, type);
  }

  /**
   * Update.
   *
   * @param rvwQnaireAnswerOpl List<RvwQnaireAnswerOpl> the rvw qnaire answer opl list
   * @return the rvw qnaire answer opl
   * @throws ApicWebServiceException the apic web service exception
   */
  public List<RvwQnaireAnswerOpl> update(final List<RvwQnaireAnswerOpl> rvwQnaireAnswerOpl)
      throws ApicWebServiceException {
    GenericType<List<RvwQnaireAnswerOpl>> type = new GenericType<List<RvwQnaireAnswerOpl>>() {};
    return put(getWsBase(), rvwQnaireAnswerOpl, type);

  }

  /**
   * Delete.
   *
   * @param rvwQnaireAnswerOplId List<Long> the rvw qnaire answer opl id
   * @throws ApicWebServiceException the apic web service exception
   */
  public void delete(final List<Long> rvwQnaireAnswerOplId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    for (Long oplId : rvwQnaireAnswerOplId) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, oplId);
    }
    delete(wsTarget);
    LOGGER.debug("Review Questionaire Answer  Open Points List have been Deleted. No of records deleted : {}",
        rvwQnaireAnswerOplId.size());
  }
}
