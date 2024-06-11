package com.bosch.caltool.apic.ws.rest.service.cdr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.WpFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * Service class for WpArchival
 *
 * @author msp5cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_WP_FILES_DOWNLOAD))
public class WpFilesDownloadService extends AbstractRestService {


  /**
   * @param wpArchivalId wpArchivalId
   * @return File data
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response getWpArchivalFiles(@QueryParam(WsCommonConstants.RWS_QP_WP_ARCHIVAL_ID) final Long wpArchivalId)
      throws IcdmException {
    return Response.ok(new WpFilesLoader(getServiceData()).getWpArchivalFiles(wpArchivalId)).build();
  }


}
