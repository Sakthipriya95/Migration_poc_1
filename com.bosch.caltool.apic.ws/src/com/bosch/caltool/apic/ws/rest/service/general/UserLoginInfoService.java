package com.bosch.caltool.apic.ws.rest.service.general;

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
import com.bosch.caltool.icdm.bo.general.UserLoginInfoCommand;
import com.bosch.caltool.icdm.bo.general.UserLoginInfoLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.UserLoginInfo;


/**
 * Service class for UserLoginInfo
 *
 * @author msp5cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_USERLOGININFO))
public class UserLoginInfoService extends AbstractRestService {


  /**
   * Create a UserLoginInfo record
   *
   * @param obj object to create
   * @return Rest response, with created UserLoginInfo object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final UserLoginInfo obj) throws IcdmException {
    UserLoginInfoCommand cmd = new UserLoginInfoCommand(getServiceData(), obj, true);
    executeCommand(cmd);
    UserLoginInfo ret = cmd.getNewData();
    getLogger().info("Created UserLoginInfo Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a UserLoginInfo record
   *
   * @param obj object to update
   * @return Rest response, with updated UserLoginInfo object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final UserLoginInfo obj) throws IcdmException {

    UserLoginInfoLoader loader = new UserLoginInfoLoader(getServiceData());
    UserLoginInfo ret = loader.getByUserNtId(obj.getUserNtId());
    UserLoginInfoCommand cmd = null;

    if (ret == null) {
      cmd = new UserLoginInfoCommand(getServiceData(), obj, true);
      executeCommand(cmd);
      UserLoginInfo retCreate = cmd.getNewData();
      getLogger().info("Created UserLoginInfo Id : {}", retCreate.getId());

      return Response.ok(retCreate).build();
    }

    long azureLoginCount = ret.getAzureLoginCount() + obj.getAzureLoginCount();
    long ldapLoginCount = ret.getLdapLoginCount() + obj.getLdapLoginCount();
    ret.setAzureLoginCount(azureLoginCount);
    ret.setLdapLoginCount(ldapLoginCount);

    cmd = new UserLoginInfoCommand(getServiceData(), ret, false);
    executeCommand(cmd);
    UserLoginInfo retUpadte = cmd.getNewData();
    getLogger().info("Updated UserLoginInfo Id : {}", retUpadte.getId());

    return Response.ok(retUpadte).build();

  }


  /**
   * @param userNtId userNtId
   * @return model UserLoginInfo
   * @throws IcdmException exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_NAME) final String userNtId) throws IcdmException {
    UserLoginInfoLoader loader = new UserLoginInfoLoader(getServiceData());
    UserLoginInfo ret = loader.getByUserNtId(userNtId);
    return Response.ok(ret).build();
  }


}
