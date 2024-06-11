package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.ws.rs.Consumes;
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
import com.bosch.caltool.icdm.bo.a2l.A2lWpRespStatusUpdationCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;


/**
 * Service class for A2LWPResponsibilityStatus
 *
 * @author UKT1COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2LWPRESPONSIBILITYSTATUS))
public class A2lWpResponsibilityStatusService extends AbstractRestService {


  /**
   * Get A2lWpResponsibilityStatus using its id
   *
   * @param a2lWPRespStatusId object's id
   * @return Rest response, with A2lWpResponsibilityStatus object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long a2lWPRespStatusId) throws IcdmException {

    A2lWpResponsibilityStatusLoader a2lWPRespStatusLoader = new A2lWpResponsibilityStatusLoader(getServiceData());
    A2lWpResponsibilityStatus a2lWPRespStatus = a2lWPRespStatusLoader.getDataObjectByID(a2lWPRespStatusId);
    return Response.ok(a2lWPRespStatus).build();
  }

  /**
   * Get A2lWpResponsibilityStatus for Wp Definition Version ID and Variant ID
   *
   * @param variantId , Pidc Variant ID
   * @param activeWpDefnVersId , Active Wp Definition Version ID
   * @return Rest response, with Map<Long, Map<Long, A2lWpResponsibilityStatus>> - Key : WPID, Value:[ Key : RESPID,
   *         Value: A2lWpResponsibilityStatus]
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Path(WsCommonConstants.RWS_WORKPACKAGE_STATUS)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getWpStatusByVarAndWpDefnVersId(@QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantId,
      @QueryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID) final Long activeWpDefnVersId)
      throws IcdmException {

    A2lWpResponsibilityStatusLoader a2lWPRespStatusLoader = new A2lWpResponsibilityStatusLoader(getServiceData());
    Map<Long, Map<Long, A2lWpResponsibilityStatus>> a2lWPRespStatusMap =
        a2lWPRespStatusLoader.getA2lWpStatusByVarAndWpDefnVersId(variantId, activeWpDefnVersId);
    return Response.ok(a2lWPRespStatusMap).build();
  }

  /**
   * Update a A2lWpResponsibilityStatus record
   *
   * @param a2lWpRespStatusUpdModel - Model with data to be created or update
   * @return Rest response, with updated Model
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_UPDATE_A2L_WP_RESPONSIBILITY_STATUS)
  @CompressData
  public Response updateWpFinStatus(final A2lWpRespStatusUpdationModel a2lWpRespStatusUpdModel) throws IcdmException {

    A2lWpRespStatusUpdationCommand a2lWPRespUpdCmd =
        new A2lWpRespStatusUpdationCommand(getServiceData(), a2lWpRespStatusUpdModel);

    if (CommonUtils.isNotEmpty(a2lWpRespStatusUpdModel.getA2lWpRespStatusListToBeCreated()) ||
        CommonUtils.isNotEmpty(a2lWpRespStatusUpdModel.getA2lWpRespStatusToBeUpdatedMap())) {
      executeCommand(a2lWPRespUpdCmd);
    }

    A2lWpRespStatusUpdationModel a2lWpRespStatusUpdModelAfterUpd = new A2lWpResponsibilityStatusLoader(getServiceData())
        .getOutputUpdationModel(a2lWPRespUpdCmd.getListOfNewlyCreatedA2lWPRespStatus(),
            a2lWPRespUpdCmd.getA2lWpRespStatusBeforeUpdateMap(), a2lWPRespUpdCmd.getA2lWpRespStatusAfterUpdateMap());

    WSObjectStore.getLogger().info("Updating A2lWpResponsibilityStatus is completed");
    return Response.ok(a2lWpRespStatusUpdModelAfterUpd).build();
  }

}
