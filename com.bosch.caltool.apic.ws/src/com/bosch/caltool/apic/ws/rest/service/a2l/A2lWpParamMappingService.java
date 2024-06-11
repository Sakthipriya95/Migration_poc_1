package com.bosch.caltool.apic.ws.rest.service.a2l;

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
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpParamMappingUpdateCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMapping;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpParamMappingUpdateModel;


/**
 * Service class for A2lWpParamMapping
 *
 * @author pdh2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_WP_PARAM_MAPPING)
public class A2lWpParamMappingService extends AbstractRestService {

  /**
   * Get A2lWpParamMapping using its id
   *
   * @param objId object's id
   * @return Rest response, with A2lWpParamMapping object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    A2lWpParamMappingLoader loader = new A2lWpParamMappingLoader(getServiceData());
    A2lWpParamMapping ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all A2lWpParamMapping based on A2lWpDefVersion id
   *
   * @param wpDefVersId A2lWpDefVersion id
   * @return Rest response, with Map of A2lWpParamMapping objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_WP_PARAM_MAPPING_BY_WP_DEFN_VERS_ID)
  @CompressData
  public Response getAllByWpDefVersId(@QueryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID) final Long wpDefVersId)
      throws IcdmException {
    A2lWpParamMappingLoader loader = new A2lWpParamMappingLoader(getServiceData());
    A2lWpParamMappingModel model = loader.getAllByWpDefVersId(wpDefVersId);
    getLogger().info("A2lWpParamMappingService.getAllByWpDefVersIdAll completed. Total records = {}",
        model.getA2lWpParamMapping().size());
    return Response.ok(model).build();
  }

  /**
   * @param a2lWpParamMappingUpdateModel - model to create/update/delete mappings
   * @return after update model
   * @throws IcdmException - icdm exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_UPDATE_A2L_WP_PARAM_MAPPING)
  @CompressData
  public Response updateA2lWpParamMapping(final A2lWpParamMappingUpdateModel a2lWpParamMappingUpdateModel)
      throws IcdmException {

    A2lWpParamMappingUpdateCommand command =
        new A2lWpParamMappingUpdateCommand(getServiceData(), a2lWpParamMappingUpdateModel);
    executeCommand(command);

    A2lWpParamMappingUpdateModel responseModel = new A2lWpParamMappingUpdateModel();
    A2lWpParamMappingLoader loader = new A2lWpParamMappingLoader(getServiceData());

    // construct response model
    // fill the mappings before update in response model
    responseModel.setA2lWpParamMappingToBeCreated(a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeCreated());
    responseModel.setA2lWpParamMappingToBeUpdated(a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeUpdated());
    responseModel.setA2lWpParamMappingToBeDeleted(a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeDeleted());

    // fill the newly created mappings in reponse model
    for (Long a2lWpParamMappingId : command.getCreatedA2lWpParamMappingIds()) {
      responseModel.getCreatedA2lWpParamMapping().put(a2lWpParamMappingId,
          loader.getDataObjectByID(a2lWpParamMappingId));
    }
    // fill the updated mappings in reponse model
    for (A2lWpParamMapping a2lWpParamMapping : a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeUpdated()
        .values()) {
      responseModel.getUpdatedA2lWpParamMapping().put(a2lWpParamMapping.getId(),
          loader.getDataObjectByID(a2lWpParamMapping.getId()));
    }
    // fill the deleted mappings in reponse model
    for (A2lWpParamMapping a2lWpParamMapping : a2lWpParamMappingUpdateModel.getA2lWpParamMappingToBeDeleted()
        .values()) {
      responseModel.getDeletedA2lWpParamMapping().put(a2lWpParamMapping.getId(), a2lWpParamMapping);
    }


    return Response.ok(responseModel).build();

  }


}
