/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.FunctionVersionUniqueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;


/**
 * Get Icdm functions which contains the search string
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FUNCTION_VERSIONS)
public class FunctionVersionService extends AbstractRestService {

  /**
   * @param funcName string tp search functions which contains the search string
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_BY_PARENT)
  @CompressData
  public Response getSearchFunctions(@QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String funcName)
      throws IcdmException {
    // Use the funcation version unique loader
    FunctionVersionUniqueLoader loader = new FunctionVersionUniqueLoader(getServiceData());
    // Fetch all functions
    List<FunctionVersionUnique> retSet = loader.getAllVersions(funcName);

    WSObjectStore.getLogger().info("Function.getSearchFunctions() completed. Functions found = {}", retSet.size());
    // Return the unqiue function versions
    return Response.ok(retSet).build();

  }


}
