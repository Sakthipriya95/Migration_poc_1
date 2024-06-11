package com.bosch.caltool.apic.ws.rest.service.bc;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.bc.SdomFcLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.bc.SdomFc;


/**
 * Service class for SdomFc
 *
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_BC + "/" + WsCommonConstants.RWS_SDOMFC)
public class SdomFcService extends AbstractRestService {

  /**
   * Rest web service path for SdomFc
   */
  public final static String RWS_SDOMFC = "sdomfc";

  /**
   * Get SdomFc using its id
   *
   * @param objId object's id
   * @return Rest response, with SdomFc object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    SdomFcLoader loader = new SdomFcLoader(getServiceData());
    SdomFc ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * @param bcName
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_SDOMFCBYBCNAME)
  @CompressData
  public Response getSDOMFcByBCName(@QueryParam(WsCommonConstants.RWS_QP_BC_NAME) final String bcName)
      throws IcdmException {
    SdomFcLoader loader = new SdomFcLoader(getServiceData());
    List<String> sdomFcs = loader.getSDOMFcByBCName(bcName);
    getLogger().info(" SdomFc getSDOMFcByBCName completed. Total records = {}", sdomFcs.size());
    return Response.ok(sdomFcs).build();

  }

}
