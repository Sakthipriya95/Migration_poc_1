/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.RvwCommentTemplateLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_COMMENT_TEMPLATE)
public class RvwCommentTemplateService extends AbstractRestService {

  /**
   * @return Response with list of Rvw Comment Template
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {

    RvwCommentTemplateLoader loader = new RvwCommentTemplateLoader(getServiceData());

    // Fetch all rvw comment template
    Map<Long, RvwCommentTemplate> rvwCommentTemplateMap = loader.getAll();

    getLogger().info(
        "RvwCommentTemplateService.getAll() completed. Rvw Comment Template found = {}",
        rvwCommentTemplateMap.size());

    return Response.ok(rvwCommentTemplateMap).build();

  }
}
