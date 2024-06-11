package com.bosch.caltool.apic.ws.rest.service.uc;

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
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.uc.UseCaseFavData;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;


/**
 * Service class for UsecaseFavorite
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_UC + "/" + WsCommonConstants.RWS_USECASEFAVORITE)
public class UsecaseFavoriteService extends AbstractRestService {

  /**
   * Rest web service path for UsecaseFavorite
   */
  public static final String RWS_USECASEFAVORITE = "usecasefavorite";

  /**
   * Get UsecaseFavorite using its id
   *
   * @param objId object's id
   * @return Rest response, with UsecaseFavorite object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader loader =
        new com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader(getServiceData());
    UsecaseFavorite ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get UsecaseFavorite using user id
   *
   * @param userId object's id
   * @return Rest response, with UsecaseFavorite object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getFavoriteUseCases(@QueryParam(WsCommonConstants.RWS_USECASEFAVORITE) final Long userId)
      throws IcdmException {
    com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader loader =
        new com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader(getServiceData());
    Map<Long, UsecaseFavorite> ret = loader.getFavoriteUseCases(userId);
    return Response.ok(ret).build();
  }

  /**
   * Get UsecaseFavorite using user id
   *
   * @param projectId object's id
   * @return Rest response, with UsecaseFavorite object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_USECASE)
  @CompressData
  public Response getProjectFavoriteUseCases(
      @QueryParam(WsCommonConstants.RWS_PROJUSECASEFAVORITE) final Long projectId)
      throws IcdmException {
    com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader loader =
        new com.bosch.caltool.icdm.bo.uc.UsecaseFavoriteLoader(getServiceData());
    Map<Long, UsecaseFavorite> ret = loader.getProjFavoriteUseCases(projectId);
    return Response.ok(ret).build();
  }

  /**
   * Create a UsecaseFavorite record
   *
   * @param obj object to create
   * @return Rest response, with created UsecaseFavorite object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final UsecaseFavorite obj) throws IcdmException {

    UsecaseFavoriteCommand cmd = new UsecaseFavoriteCommand(getServiceData(), obj, false, false, false);
    executeCommand(cmd);

    UseCaseFavData ret = new UseCaseFavData();
    ret.setPidcCoCWPUpdModel(cmd.getPidcVersCoCWPUpdateModel());

    UsecaseFavorite newlyCreatedUcFav = cmd.getNewData();
    ret.setNewlyCreatedUcFav(newlyCreatedUcFav);

    getLogger().info("Created UsecaseFavorite Id : {}", newlyCreatedUcFav.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a UsecaseFavorite record
   *
   * @param obj object to create
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DEL_UCFAV_AND_UPD_COC_WP)
  @CompressData
  public Response delete(final UsecaseFavorite obj) throws IcdmException {

    UsecaseFavoriteCommand cmd = new UsecaseFavoriteCommand(getServiceData(), obj, false, true, false);
    executeCommand(cmd);

    UseCaseFavData ret = new UseCaseFavData();
    ret.setPidcCoCWPUpdModel(cmd.getPidcVersCoCWPUpdateModel());

    return Response.ok(ret).build();
  }
}
