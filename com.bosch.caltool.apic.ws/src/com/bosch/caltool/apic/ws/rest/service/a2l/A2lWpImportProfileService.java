package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2lWpImportProfileLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lWpImportProfile;


/**
 * Service class for A2lWpImportProfile
 *
 * @author and4cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_WP_IMPORT_PROFILE))
public class A2lWpImportProfileService extends AbstractRestService {


  /**
   * Get all A2lWpImportProfile records
   *
   * @return Rest response, with Map of A2lWpImportProfile objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {
    // Use the A2lWpImportProfileLoader to load all profile
    A2lWpImportProfileLoader loader = new A2lWpImportProfileLoader(getServiceData());
    Map<Long, A2lWpImportProfile> retMap = loader.getAll();
    getLogger().info("A2lWpImportProfile getAll completed. Total records = {}", retMap.size());
    // Return the map of all profiles with key as Profile id
    return Response.ok(retMap).build();
  }

}
