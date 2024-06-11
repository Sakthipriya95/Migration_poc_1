/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.Set;

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
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcFavouriteLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcFavouritesCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_USER_FAV)
public class FavouritesService extends AbstractRestService {

  /**
   * Get all ser favourites
   *
   * @return Rest response
   * @throws IcdmException IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getFavourites() throws IcdmException {
    PidcFavouriteLoader loader = new PidcFavouriteLoader(getServiceData());
    return Response.ok(loader.getAllFavourites()).build();
  }

  /**
   * Get favourites for the given user(s)
   *
   * @param userSet user's NT ID set
   * @return Rest response
   * @throws IcdmException IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_BY_USER_NT_ID)
  @CompressData
  public Response getFavouritesByUser(@QueryParam(value = WsCommonConstants.RWS_QP_USER) final Set<String> userSet)
      throws IcdmException {
    PidcFavouriteLoader loader = new PidcFavouriteLoader(getServiceData());
    return Response.ok(loader.getAllFavouritesByUser(userSet)).build();
  }

  /**
   * Get favourites for the given user
   *
   * @param userId user ID
   * @return Rest response
   * @throws IcdmException IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_BY_USER_ID)
  @CompressData
  public Response getFavouritePidcForUser(@QueryParam(value = WsCommonConstants.RWS_QP_USER_ID) final Long userId)
      throws IcdmException {

    PidcFavouriteLoader loader = new PidcFavouriteLoader(getServiceData());
    return Response.ok(loader.getFavouritePidcForUser(userId)).build();
  }

  /**
   * /**
   *
   * @param objId PIDC favourite ID
   * @return response PidcFavourite obj response
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    PidcFavouriteLoader loader = new PidcFavouriteLoader(getServiceData());
    PidcFavourite ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Create a Pidc favourite record
   *
   * @param pidcFav
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final PidcFavourite pidcFav) throws IcdmException {
    PidcFavouritesCommand pidcFavCmd = new PidcFavouritesCommand(getServiceData(), pidcFav, false, false);
    executeCommand(pidcFavCmd);
    PidcFavourite ret = pidcFavCmd.getNewData();
    getLogger().info("Created Pidc Favourite Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a Pidc favourite record
   *
   * @param objId pidc fav id
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    PidcFavouriteLoader favLoader = new PidcFavouriteLoader(getServiceData());
    PidcFavourite pidcFav = favLoader.getDataObjectByID(objId);
    PidcFavouritesCommand pidcFavCmd = new PidcFavouritesCommand(getServiceData(), pidcFav, false, true);
    executeCommand(pidcFavCmd);
    getLogger().info("Deleted Pidc Favourite Id : {}", objId);
    return Response.ok().build();
  }
}
