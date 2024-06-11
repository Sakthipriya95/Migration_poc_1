package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.RvwUserCmntHistoryCommand;
import com.bosch.caltool.icdm.bo.cdr.RvwUserCmntHistoryLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;


/**
 * Service class for Review Comment History
 *
 * @author PDH2COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVWCMNTHISTORY))
public class RvwUserCmntHistoryService extends AbstractRestService {

  /**
   * Get RvwCmntHistory using its id
   *
   * @param objId object's id
   * @return Rest response, with RvwCmntHistory object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwUserCmntHistoryLoader loader = new RvwUserCmntHistoryLoader(getServiceData());
    RvwUserCmntHistory ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }


  /**
   * Create a Review Comment History record
   *
   * @param obj object to create
   * @return Rest response, with created RvwCmntHistory object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final RvwUserCmntHistory obj) throws IcdmException {
    RvwUserCmntHistoryCommand cmd = new RvwUserCmntHistoryCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    RvwUserCmntHistory ret = cmd.getNewData();
    getLogger().info("Created Review Comment History Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param userId user id
   * @return Map of review comments entered by user
   * @throws IcdmException exception frm serice
   */
  @GET
  @Path(WsCommonConstants.RWS_GET_RVW_CMNT_HISTORY_BY_USER)
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getLatestReviewComments(@QueryParam(value = WsCommonConstants.RWS_QP_USER) final Long userId)
      throws IcdmException {
    WSObjectStore.getLogger().info("RvwCmntHistoryService.getLatestReviewComments() started. User Inputs : {} = {}; ",
        WsCommonConstants.RWS_QP_USER, userId);

    RvwUserCmntHistoryLoader loader = new RvwUserCmntHistoryLoader(getServiceData());
    Map<Long, RvwUserCmntHistory> historyMap = loader.getRvwCmntHistoryForUser(userId);

    WSObjectStore.getLogger().info("RvwCmntHistoryService.getLatestReviewComments() completed. Size - {}",
        historyMap.size());

    return Response.ok(historyMap).build();
  }


  /**
   * Delete a Review Comment History record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwUserCmntHistoryLoader loader = new RvwUserCmntHistoryLoader(getServiceData());
    RvwUserCmntHistory obj = loader.getDataObjectByID(objId);
    RvwUserCmntHistoryCommand cmd = new RvwUserCmntHistoryCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }


}
