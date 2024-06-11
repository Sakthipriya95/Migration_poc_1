/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.DataRefreshHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.DataRefreshInput;
import com.bosch.caltool.icdm.model.general.DataRefreshResult;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_DATA_REFRESH)
public class DataRefreshService extends AbstractRestService {

  /**
   * Refresh the data in the input model. Can add IDs of different types
   *
   * @param input refresh input
   * @return DataRefreshResult
   * @throws IcdmException error during processing
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response refreshData(final DataRefreshInput input) throws IcdmException {
    DataRefreshResult ret = new DataRefreshHandler(getServiceData()).refreshData(input);
    return Response.ok(ret).build();
  }

}
