/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_COMMON_PARAM)
public class CommonParamService extends AbstractRestService {

  /**
   * Get all Common Parameters.
   *
   * @return map all common parameters
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {

    // Convert to response model
    Map<String, String> retMap = new CommonParamLoader(getServiceData()).getAll().entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getParamName(), Entry::getValue));

    getLogger().info("CommonParamService.getAll() completed. Number of Common Parameters = {}", retMap.size());

    return Response.ok(retMap).build();
  }


  /**
   * @param paramId Param Key
   * @return Param Value
   * @throws IcdmException service error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_PARAM)
  @CompressData
  public Response getParameterValue(@QueryParam(WsCommonConstants.RWS_QP_PARAM_ID) final String paramId)
      throws IcdmException {

    CommonParamKey key;
    // convert to param key
    try {
      key = CommonParamKey.getType(paramId);
    }
    catch (IllegalArgumentException e) {
      throw new InvalidInputException(e.getMessage(), e);
    }

    String ret = new CommonParamLoader(getServiceData()).getValue(key);

    getLogger().info("CommonParamService.getParameterValue() completed. Param Key : {} - value = {}", paramId, ret);

    return Response.ok(ret).build();
  }

}