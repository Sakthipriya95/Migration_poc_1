/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

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
import com.bosch.caltool.icdm.bo.a2l.ParameterAttributeLoader;
import com.bosch.caltool.icdm.bo.cdr.ParameterAttrCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_PARAM_ATTR)
public class ParamAttrService extends AbstractRestService {


  /**
   * Create a Attribute record
   *
   * @param paramAttr paramAttribute to create
   * @return Rest response, with created ParameterAttribute object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final ParameterAttribute paramAttr) throws IcdmException {
    ParameterAttrCommand cmd = new ParameterAttrCommand(getServiceData(), paramAttr,
        new ParameterAttributeLoader(getServiceData()), COMMAND_MODE.CREATE);
    executeCommand(cmd);
    ParameterAttribute ret = cmd.getNewData();
    getLogger().info("Created Attribute Id : {}", ret.getId());
    // the responseis the newly created data model object.
    return Response.ok(ret).build();
  }

  /**
   * Create a Attribute record
   *
   * @param paramAttrId param Attr id to delete
   * @return Rest response is empty with 200 code
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long paramAttrId)
      throws IcdmException {
    ParameterAttributeLoader paramAttrLoader = new ParameterAttributeLoader(getServiceData());
    ParameterAttrCommand cmd = new ParameterAttrCommand(getServiceData(),
        paramAttrLoader.getDataObjectByID(paramAttrId), paramAttrLoader, COMMAND_MODE.DELETE);
    executeCommand(cmd);
    // the reposne is empty
    return Response.ok().build();
  }

}
