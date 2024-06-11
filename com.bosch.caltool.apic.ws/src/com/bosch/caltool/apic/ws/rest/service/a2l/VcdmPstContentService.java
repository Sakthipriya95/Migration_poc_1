/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

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
import com.bosch.caltool.icdm.bo.cdr.VcdmPstContentLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;

/**
 * Rest service for Vcdm Pst Content
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_VCDM_PST_CONTENT)
public class VcdmPstContentService extends AbstractRestService {

  /**
   * Get VcdmPstContent records for the given PIDC A2L object ID
   *
   * @param a2lId A2l File Id
   * @return Response with Set of VcdmPstContent
   * @throws IcdmException the icdm exception
   */
  @GET
  @Path(WsCommonConstants.RWS_BY_A2L)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getVcdmPstContentsForA2l(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long a2lId)
      throws IcdmException {
    // Use VcdmPstContentLoader to load all the VcdmPstContent
    Set<VcdmPstContent> retSet = new VcdmPstContentLoader(getServiceData()).getVcdmPstContentsForA2l(a2lId);
    getLogger().info("VcdmPstContentService.getVcdmPstContentsForA2l() completed. Number of VcdmPstContent = {}",
        retSet.size());

    return Response.ok(retSet).build();
  }

}
