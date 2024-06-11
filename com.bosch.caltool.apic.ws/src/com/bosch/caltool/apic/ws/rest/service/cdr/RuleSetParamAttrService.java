/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParamAttrCommand;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParameterAttrLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;


/**
 * @author rgo7cob
 */

@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_RULEST_PARAM_ATTR)
public class RuleSetParamAttrService extends AbstractRestService {

  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final RuleSetParameterAttr obj) throws IcdmException {
    RuleSetParamAttrCommand cmd = new RuleSetParamAttrCommand(getServiceData(), obj, COMMAND_MODE.CREATE);
    executeCommand(cmd);
    RuleSetParameterAttr ret = cmd.getNewData();
    getLogger().info("Created Attribute Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Create a Attribute record
   *
   * @param obj object to create
   * @return Rest response, with created Attribute object
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long ruleSetParamAttrId)
      throws IcdmException {
    RuleSetParameterAttrLoader paramAttrLoader = new RuleSetParameterAttrLoader(getServiceData());
    RuleSetParamAttrCommand cmd = new RuleSetParamAttrCommand(getServiceData(),
        paramAttrLoader.getDataObjectByID(ruleSetParamAttrId), COMMAND_MODE.DELETE);
    executeCommand(cmd);

    return Response.ok().build();
  }

}
