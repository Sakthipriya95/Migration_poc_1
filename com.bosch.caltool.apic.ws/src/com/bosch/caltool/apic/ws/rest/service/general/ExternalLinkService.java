/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.HashMap;
import java.util.Map;
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
import com.bosch.caltool.icdm.bo.general.ExternalLinkInfoLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;

/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_EXT_LINK)
public class ExternalLinkService extends AbstractRestService {

  /**
   * Get all links by node
   *
   * @param objId object ID
   * @param typeCode Link NODE_TYPE
   * @param details additional details
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getLinkInfo(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId,
      @QueryParam(value = WsCommonConstants.RWS_QP_TYPE) final String typeCode,
      @QueryParam(value = WsCommonConstants.RWS_QP_DETAIL) final Set<String> details)
      throws IcdmException {

    Map<String, String> additionalDetails = new HashMap<>();
    for (String det : details) {
      String[] keyValue = det.split(WsCommonConstants.QP_SEPARATOR);
      additionalDetails.put(keyValue[0], keyValue[1]);
    }

    ExternalLinkInfo ret = new ExternalLinkInfoLoader(getServiceData()).getLinkInfo(objId, typeCode, additionalDetails);

    getLogger().info("ExternalLinkService.getLinkInfo() completed. Link URL = {}", ret.getUrl());

    return Response.ok(ret).build();
  }


}

