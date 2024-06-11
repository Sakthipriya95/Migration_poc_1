/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.UserPreferenceCommand;
import com.bosch.caltool.icdm.bo.apic.UserPreferenceLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.UserPreference;


/**
 * Service class for tUserPreference
 *
 * @author EKIR1KOR
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_USERPREFERENCE))
public class UserPreferenceService extends AbstractRestService {

  /**
   * Get tUserPreference using its id
   *
   * @param prefId preference's id
   * @return Rest response, with UserPreference object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long prefId) throws IcdmException {
    UserPreferenceLoader loader = new UserPreferenceLoader(getServiceData());
    UserPreference userPreference = loader.getDataObjectByID(prefId);
    return Response.ok(userPreference).build();
  }

  /**
   * Get all tUserPreference records
   *
   * @param userId the user Id
   * @return Rest response, with Map of UserPreference objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_USER_PREF_BY_USER_ID)
  @CompressData
  public Response getByUserId(@QueryParam(WsCommonConstants.RWS_USER_ID) final long userId) throws IcdmException {
    UserPreferenceLoader loader = new UserPreferenceLoader(getServiceData());
    Map<Long, UserPreference> userPreferenceMap = loader.getByUserId(userId);
    getLogger().info("tUserPreference getAll completed. Total records = {}", userPreferenceMap.size());
    return Response.ok(userPreferenceMap).build();
  }

  /**
   * Create a tUserPreference record
   *
   * @param userPreference object to create
   * @return Rest response, with created UserPreference object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final UserPreference userPreference) throws IcdmException {
    UserPreferenceCommand cmd = new UserPreferenceCommand(getServiceData(), userPreference, false);
    executeCommand(cmd);
    UserPreference newUserPreference = cmd.getNewData();
    getLogger().info("Created tUserPreference Id : {}", newUserPreference.getId());
    return Response.ok(newUserPreference).build();
  }

  /**
   * Update a tUserPreference record
   *
   * @param userPreference object to update
   * @return Rest response, with updated UserPreference object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final UserPreference userPreference) throws IcdmException {
    UserPreferenceCommand cmd = new UserPreferenceCommand(getServiceData(), userPreference, true);
    executeCommand(cmd);
    UserPreference newUserPreference = cmd.getNewData();
    getLogger().info("Updated tUserPreference Id : {}", newUserPreference.getId());
    return Response.ok(newUserPreference).build();
  }

}
