package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2lSysconstLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lSysconst;


/**
 * Service class for A2lSyscon
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_SYSTEM_CONSTANTS)
public class A2lSysconstService extends AbstractRestService {

  /**
   * Rest web service path for A2lSyscon
   */
  public final static String RWS_A2LSYSCONST = "a2lsysconst";

  /**
   * Get A2lSyscon using its id
   * 
   * @param objId object's id
   * @return Rest response, with A2lSysconst object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lSysconstLoader loader = new A2lSysconstLoader(getServiceData());
    A2lSysconst ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Check if list of system constants exists, return invalid system constants
   *
   * @param list of system constants
   * @param httpRequest the http request
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_INVALID_SYSTEM_CONSTANTS)
  @CompressData
  public Response getInvalidSystemConstants(final List<String> sysconList,
      @Context final HttpServletRequest httpRequest)
      throws IcdmException {

    WSObjectStore.getLogger().info("A2lSysconstService started. User Inputs : sysconList = " + sysconList);


    A2lSysconstLoader loader = new A2lSysconstLoader(getServiceData());

    List<String> invalidSysconlist = loader.getInvalidSystemConstants(sysconList);

    WSObjectStore.getLogger()
        .info("A2lSysconstService completed. Number of invalid system constants = " + invalidSysconlist.size());

    return Response.ok(invalidSysconlist).build();

  }

}
