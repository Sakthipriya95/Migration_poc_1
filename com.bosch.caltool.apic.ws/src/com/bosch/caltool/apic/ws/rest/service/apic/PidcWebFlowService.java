/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcWebFlowLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDetailsType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowReponseType;


/**
 * @author dmr1cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDC_WEBFLOW)
public class PidcWebFlowService extends AbstractRestService {


  /**
   * Get the pidc web flow data
   *
   * @param elementID pidcVersionID or PidcVariantId
   * @return Rest response {@link PidcWebFlowReponseType}
   * @throws IcdmException Exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WEB_FLOW_DATA)
  @CompressData
  public Response loadPidcWebFlowDetails(@QueryParam(value = WsCommonConstants.RWS_QP_ELEMENT_ID) final Long elementID)
      throws IcdmException {

    PidcWebFlowData pidcWebFlowData = null;
    // get the data for pidc web flow.
    PidcWebFlowLoader loader = new PidcWebFlowLoader(getServiceData());
    pidcWebFlowData = loader.fetchDataForWebFlow(elementID);

    PidcDetailsType pidcDetailsType = new PidcDetailsType();
    pidcDetailsType.setId(pidcWebFlowData.getElemementID());
    pidcDetailsType.setName(pidcWebFlowData.getName());
    pidcDetailsType.setChangeNumber(pidcWebFlowData.getChangeNum());
    pidcDetailsType.setVcdmElementName(pidcWebFlowData.getVcdmElementName());

    PidcWebFlowReponseType pidcWebFlowReponseType = new PidcWebFlowReponseType();
    pidcWebFlowReponseType.setPidcDetailsType(pidcDetailsType);
    pidcWebFlowReponseType.setWebFlowAttr(pidcWebFlowData.getWebFlowAttr());
    // return the pidc webb flow response type
    return Response.ok(pidcWebFlowReponseType).build();
  }

  /**
   * Get the pidc web flow data
   *
   * @param elementID pidcVersionID or PidcVariantId
   * @return Rest response {@link PidcWebFlowElementRespType}
   * @throws IcdmException Exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WEB_FLOW_ELEMENT)
  @CompressData
  public Response loadPidcWebFlowDetailsElement(
      @QueryParam(value = WsCommonConstants.RWS_QP_ELEMENT_ID) final Long elementID)
      throws IcdmException {

    // get the data for pidc web flow.
    PidcWebFlowLoader loader = new PidcWebFlowLoader(getServiceData());
    Set<PidcWebFlowData> pidcWebFlowDataSet = loader.fetchDataForWebFlowElement(elementID);
    // get the web flow response type
    loader.getPidcWebFlowElementRespType(elementID, pidcWebFlowDataSet);

    return Response.ok(loader.getPidcWebFlowElementRespType(elementID, pidcWebFlowDataSet)).build();
  }

}
