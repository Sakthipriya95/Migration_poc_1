package com.bosch.caltool.apic.ws.rest.service.apic.cocwp;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcCocWpUpdationCommand;
import com.bosch.caltool.icdm.bo.apic.cocwp.PidcVersCocWpLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationInputModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PIDCCocWpUpdationModel;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcCocWpExternalAPIData;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVersCocWpData;


/**
 * Service class for PidcVersCocWp
 *
 * @author UKT1COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCVERSCOCWP))
public class PidcVersCocWpService extends AbstractRestService {

  /**
   * Get PidcVersCocWp using its id
   *
   * @param pidcVersCocWpId object's id
   * @return Rest response, with PidcVersCocWp object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long pidcVersCocWpId) throws IcdmException {
    PidcVersCocWpLoader pidcVersCocWpLoader = new PidcVersCocWpLoader(getServiceData());
    PidcVersCocWp pidcVersCocWp = pidcVersCocWpLoader.getDataObjectByID(pidcVersCocWpId);
    return Response.ok(pidcVersCocWp).build();
  }

  /**
   * Get all PidcVersCocWp records
   *
   * @param pidcVersionId pidc version Id
   * @return Rest response, with Map of PidcVersCocWp objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL_COC_WP_BY_PIDC_VERS)
  @CompressData
  public Response getAllCocWpByPidcVersId(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersionId)
      throws IcdmException {

    PidcVersCocWpData pidcVersCocWpData =
        new PidcVersCocWpLoader(getServiceData()).getAllCocWpByPidcVersId(pidcVersionId);
    getLogger().info("PidcVersCocWpService.getAllCocWpByPidcVersId completed. Total WpDivs = {}",
        pidcVersCocWpData.getPidcVersCocWpMap().size());

    return Response.ok(pidcVersCocWpData).build();
  }


  /**
   * Get all PidcVersCocWp records
   *
   * @param pidcVersionId pidc version Id
   * @return Rest response, with Map of PidcVersCocWp objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL_COC_WP_BY_PIDC_VERS + "/" + WsCommonConstants.RWS_EXTERNAL)
  @CompressData
  public Response getAllCocWpExternal(@QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersionId)
      throws IcdmException {

    PidcCocWpExternalAPIData pidcVersCocWpExternalAPIData =
        new PidcVersCocWpLoader(getServiceData()).getCocWpForExtAPI(pidcVersionId);
    getLogger().info("PidcVersCocWpService.getAllCocWpByPidcVersId completed. Total WpDivs = {}",
        pidcVersCocWpExternalAPIData.getPidcVersCocWpMap().size());

    return Response.ok(pidcVersCocWpExternalAPIData).build();
  }


  /**
   * @param pidcCocWPUpdInputModel PIDCCocWpUpdationInputModel
   * @return setOfUpdatedCocWP objects with details
   * @throws IcdmException any error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_UPDATE_COC_WP)
  @CompressData
  public Response updatePidcCocWPs(final PIDCCocWpUpdationInputModel pidcCocWPUpdInputModel) throws IcdmException {

    if (CommonUtils.isNull(pidcCocWPUpdInputModel)) {
      throw new InvalidInputException("Invalid input for Coc WP update.");
    }

    PidcCocWpUpdationCommand mainCmd = new PidcCocWpUpdationCommand(getServiceData(), pidcCocWPUpdInputModel, false);
    executeCommand(mainCmd);

    PIDCCocWpUpdationModel updatedModel =
        new PidcVersCocWpLoader(getServiceData()).createUpdationModel(mainCmd.getOutputModel(), pidcCocWPUpdInputModel);

    return Response.ok(updatedModel).build();
  }

}
