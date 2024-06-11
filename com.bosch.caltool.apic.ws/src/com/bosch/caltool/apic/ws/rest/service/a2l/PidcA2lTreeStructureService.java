/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.PidcA2lTreeStructureHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_PIDC_A2L_TREE_STRUCT)
public class PidcA2lTreeStructureService extends AbstractRestService {

  /**
   * Service to fetch the data needed for the new tree structure model for a specific a2l file
   *
   * @param pidcA2lId as input
   * @return PidcA2lTreeStructureModel
   * @throws IcdmException asexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getPidcA2lTreeStructuresModel(
      @QueryParam(value = WsCommonConstants.RWS_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    return Response.ok(new PidcA2lTreeStructureHandler(getServiceData()).getPidcA2lTreeStruct(pidcA2lId)).build();
  }


  /**
   * @param a2lVarGrpId
   * @param activeWpDefnVersID
   * @return
   * @throws IcdmException
   */
  @GET
  @Path(WsCommonConstants.RWS_WPRESP_MODEL_FOR_VARGRP_DEFNVERSID)
  @Produces({ MediaType.APPLICATION_JSON })
  public Response getA2lWpRespModelsForVarGrpWpDefnVersId(
      @QueryParam(value = WsCommonConstants.RWS_QP_A2L_WP_VARIANT_GROUP_ID) final Long a2lVarGrpId,
      @QueryParam(value = WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID) final Long activeWpDefnVersID)
      throws IcdmException {
    return Response
        .ok(new PidcA2lTreeStructureHandler(getServiceData()).getWpRespLabelResponse(a2lVarGrpId, activeWpDefnVersID))
        .build();
  }
}
