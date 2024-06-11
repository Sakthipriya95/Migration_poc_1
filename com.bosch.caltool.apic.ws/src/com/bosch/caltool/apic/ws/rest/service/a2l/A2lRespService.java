package com.bosch.caltool.apic.ws.rest.service.a2l;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2lRespLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lResp;


/**
 * Service class for A2L Responsibility.
 *
 * @author apj4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_RESP)
public class A2lRespService extends AbstractRestService {


  /**
   * Get A2L Responsibility record based on PidcA2l & WP root and type.
   *
   * @param pidcA2lId the pidc A2l id
   * @param wpTypeId the wp type id
   * @param wpRootId the wp root id
   * @param httpRequest the http request
   * @return Rest response, with A2lResp object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_A2LRESP_BY_PIDCA2L_WP)
  @CompressData
  public Response getA2lResponsibility(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @QueryParam(value = WsCommonConstants.RWS_QP_WP_TYPE_ID) final Long wpTypeId,
      @QueryParam(value = WsCommonConstants.RWS_QP_WP_ROOT_ID) final Long wpRootId,
      @Context final HttpServletRequest httpRequest)
      throws IcdmException {
    A2lRespLoader loader = new A2lRespLoader(getServiceData());
    A2lResp a2lResp = loader.getA2lResp(pidcA2lId, wpTypeId, wpRootId);
    getLogger().info(" A2L Responsibility.getA2lResponsibility() completed. Id = {}", a2lResp.getId());
    return Response.ok(a2lResp).build();
  }
}
