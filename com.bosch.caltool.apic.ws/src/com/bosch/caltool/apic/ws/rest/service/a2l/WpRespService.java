package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.WpRespLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.WpResp;


/**
 * Service class for WP Responsibility
 *
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_WP_RESPONSIBILITY)
public class WpRespService extends AbstractRestService {


  /**
   * Get all WP Responsibility records
   *
   * @return Rest response, with Map of WpResp objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {
    WpRespLoader loader = new WpRespLoader(getServiceData());
    Map<Long, WpResp> retMap = loader.getAll();
    getLogger().info(" WP Responsibility getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }


  /**
   * Gets the wp responsibility.
   *
   * @param wpRespId the wp resp id
   * @return the wp responsibility
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getWpResponsibility(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long wpRespId)
      throws IcdmException {
    WpRespLoader loader = new WpRespLoader(getServiceData());
    WpResp wpResp = loader.getDataObjectByID(wpRespId);
    getLogger().info(" WP Responsibility - getWpResponsibility completed. WP Responsibility = {}",
        wpResp.getRespName());
    // return the wp resp object of the given wp resp id
    return Response.ok(wpResp).build();
  }
}
