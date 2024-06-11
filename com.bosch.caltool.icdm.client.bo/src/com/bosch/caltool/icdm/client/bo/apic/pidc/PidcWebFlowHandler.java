/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.WebflowElement;
import com.bosch.caltool.icdm.ws.rest.client.apic.WebflowElementServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcWebFlowHandler {

  /**
   * @param pidcVarIdList
   * @return
   */
  public Long getElementIdForVars(final Set<Long> pidcVarIdList) {
    WebflowElementServiceClient serClient = new WebflowElementServiceClient();
    List<WebflowElement> newWebFlowEles = new ArrayList<>();
    try {
      List<WebflowElement> webFlowEleList = new ArrayList<>();
      for (Long varId : pidcVarIdList) {
        webFlowEleList.add(createWebFlowEle(varId));
      }
      if (webFlowEleList.size() > 1) {
        newWebFlowEles = serClient.create(webFlowEleList);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    if (!newWebFlowEles.isEmpty()) {
      return newWebFlowEles.get(0).getElementId();
    }
    return null;
  }

  /**
   * @param varId
   * @return
   */
  private WebflowElement createWebFlowEle(final Long varId) {
    WebflowElement webflowEle = new WebflowElement();
    webflowEle.setVariantId(varId);
    return webflowEle;
  }
}
