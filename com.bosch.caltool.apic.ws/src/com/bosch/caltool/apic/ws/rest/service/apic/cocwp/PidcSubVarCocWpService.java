package com.bosch.caltool.apic.ws.rest.service.apic.cocwp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcSubVarCocWpLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;


/**
 * Service class for PidcVariantCocWp
 *
 * @author UKT1COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCSUBVARCOCWP))
public class PidcSubVarCocWpService extends AbstractRestService {

  /**
   * Get PidcSubVarCocWp using its id
   *
   * @param pidcSubVarCocWpId object's id
   * @return Rest response, with PidcSubVarCocWp object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcSubVarCocWpId) throws IcdmException {
    PidcSubVarCocWpLoader pidcSubVarCocWploader = new PidcSubVarCocWpLoader(getServiceData());
    PidcSubVarCocWp pidcSubVarCocWp = pidcSubVarCocWploader.getDataObjectByID(pidcSubVarCocWpId);
    return Response.ok(pidcSubVarCocWp).build();
  }

}
