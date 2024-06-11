package com.bosch.caltool.apic.ws.rest.service.bc;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.bc.SdomBcLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.bc.SdomBc;


/**
 * Service class for SdomBc
 *
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_BC + "/" + WsCommonConstants.RWS_SDOMBC)
public class SdomBcService extends AbstractRestService {

  /**
   * Rest web service path for SdomBc
   */
  public final static String RWS_SDOMBC = "sdombc";


  /**
   * Get all distinct SdomBc records
   *
   * @return Rest response, with Map of SdomBc objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL_DISTINCT)
  @CompressData
  public Response getAllDistinctBcName() throws IcdmException {
    SdomBcLoader loader = new SdomBcLoader(getServiceData());
    Set<SdomBc> retMap = loader.getAllDistinctBcName();
    getLogger().info(" SdomBc getAllDistinctBcName completed. Total records = {}", retMap.size());
    // return the bc names
    return Response.ok(retMap).build();
  }


}
