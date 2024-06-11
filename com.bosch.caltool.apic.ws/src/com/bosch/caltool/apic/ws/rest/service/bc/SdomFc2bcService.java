package com.bosch.caltool.apic.ws.rest.service.bc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.bc.SdomFc2bcLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.bc.SdomFc2bc;


/**
 * Service class for SdomFc2bc
 *
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_BC + "/" + WsCommonConstants.RWS_SDOMFC2BC)
public class SdomFc2bcService extends AbstractRestService {

  /**
   * Rest web service path for SdomFc2bc
   */
  public final static String RWS_SDOMFC2BC = "sdomfc2bc";

  /**
   * Get SdomFc2bc using its id
   *
   * @param objId object's id
   * @return Rest response, with SdomFc2bc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    SdomFc2bcLoader loader = new SdomFc2bcLoader(getServiceData());
    SdomFc2bc ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


}
