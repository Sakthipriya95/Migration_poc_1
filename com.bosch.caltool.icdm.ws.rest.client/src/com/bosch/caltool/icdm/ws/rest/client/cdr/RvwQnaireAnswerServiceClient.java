package com.bosch.caltool.icdm.ws.rest.client.cdr;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class RvwQnaireAnswerServiceClient.
 */
public class RvwQnaireAnswerServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public RvwQnaireAnswerServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_QNAIRE_ANSWER);
  }

  /**
   * Get QuestionnaireResponse using its id
   *
   * @param objId object's id
   * @return RvwQnaireAnswer object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwQnaireAnswer getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, RvwQnaireAnswer.class);
  }


  /**
   * Creates the.
   *
   * @param rvwQnaireAnswer the rvw qnaire answer
   * @return the rvw qnaire answer
   * @throws ApicWebServiceException the apic web service exception
   */
  public RvwQnaireAnswer create(final RvwQnaireAnswer rvwQnaireAnswer) throws ApicWebServiceException {
    return create(getWsBase(), rvwQnaireAnswer);
  }

  /**
   * Update.
   *
   * @param rvwQnaireAnswer the rvw qnaire answer
   * @return the rvw qnaire answer
   * @throws ApicWebServiceException the apic web service exception
   */
  public RvwQnaireAnswer update(final RvwQnaireAnswer rvwQnaireAnswer) throws ApicWebServiceException {
    return update(getWsBase(), rvwQnaireAnswer);
  }

  /**
   * Delete.
   *
   * @param rvwQnaireAnswer the rvw qnaire answer
   * @throws ApicWebServiceException the apic web service exception
   */
  public void delete(final RvwQnaireAnswer rvwQnaireAnswer) throws ApicWebServiceException {
    delete(getWsBase(), rvwQnaireAnswer);
  }
}
