package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.WebflowElement;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Webflow Element
 *
 * @author dja7cob
 */
public class WebflowElementServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public WebflowElementServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_WEBFLOWELEMENT);
  }

  /**
   * Get Webflow Element using its id
   *
   * @param objId object's id
   * @return WebflowElement object
   * @throws ApicWebServiceException exception while invoking service
   */
  public WebflowElement get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, WebflowElement.class);
  }

  /**
   * Create a Webflow Element record
   *
   * @param webFlowEleList object to create
   * @return created WebflowElement object
   * @throws ApicWebServiceException exception while invoking service
   */
  public List<WebflowElement> create(final List<WebflowElement> webFlowEleList) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    GenericType<List<WebflowElement>> type = new GenericType<List<WebflowElement>>() {};
    List<WebflowElement> retSet = post(wsTarget, webFlowEleList, type);
    LOGGER.debug("New WebflowElements have been created. No of WebflowElements created : {}", retSet.size());
    return retSet;
  }

  /**
   * Update a Webflow Element record
   *
   * @param obj object to update
   * @return updated WebflowElement object
   * @throws ApicWebServiceException exception while invoking service
   */
  public WebflowElement update(final WebflowElement obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }
}
