/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.pidc;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_PIDC_REQUESTOR)

public class PidcRequestorService extends AbstractRestService {


  /**
   * retrieve the pidcrequestor file
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_ICDM_PIDC_REQUESTOR_FILE)
  @CompressData
  public Response getPidcRequestorFile() throws IcdmException {
    String fileIdStr = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_PIDC_REQUESTOR_TEMPL_FILE);

    Map<String, byte[]> ret = new IcdmFilesLoader(getServiceData()).getFiles(Long.valueOf(fileIdStr));
    return Response.ok(ret.get(ApicConstants.ICDM_PIDC_REQUESTOR_FILE_NAME)).build();
  }
}
