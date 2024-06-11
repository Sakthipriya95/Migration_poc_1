package com.bosch.caltool.apic.ws.rest.service.apic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
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
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.apic.WebflowElementCommand;
import com.bosch.caltool.icdm.bo.apic.WebflowElementLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.WebflowElement;


/**
 * Service class for Webflow Element
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_WEBFLOWELEMENT)
public class WebflowElementService extends AbstractRestService {

  /**
   * Rest web service path for Webflow Element
   */
  public final String RWS_WEBFLOWELEMENT = "webflowelement";

  /**
   * Get Webflow Element using its id
   *
   * @param objId object's id
   * @return Rest response, with WebflowElement object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    WebflowElementLoader loader = new WebflowElementLoader(getServiceData());
    WebflowElement ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Create a Webflow Element record
   *
   * @param webFlowEleList
   * @return Rest response, with created WebflowElement object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final List<WebflowElement> webFlowEleList) throws IcdmException {
    List<AbstractSimpleCommand> cmdList = new ArrayList<>();


    Long eleId = null;

    for (WebflowElement webFlowObj : webFlowEleList) {
      WebflowElementCommand cmd = new WebflowElementCommand(getServiceData(), webFlowObj, false, false);
      if (null == eleId) {
        final Query seqQuery =
            cmd.getServiceData().getEntMgr().createNativeQuery("SELECT SEQV_ATTRIBUTES.nextval from DUAL");
        eleId = ((BigDecimal) seqQuery.getSingleResult()).longValue();
      }
      cmd.setEleId(eleId);
      cmdList.add(cmd);
    }
    executeCommand(cmdList);
    List<WebflowElement> webfloweleList = new ArrayList<>();
    for (AbstractSimpleCommand cmd : cmdList) {
      webfloweleList.add(((WebflowElementCommand) cmd).getNewData());
    }
    getLogger().info("Created Webflow Elements");
    return Response.ok(webfloweleList).build();
  }

  /**
   * Update a Webflow Element record
   *
   * @param obj object to update
   * @return Rest response, with updated WebflowElement object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final WebflowElement obj) throws IcdmException {
    WebflowElementCommand cmd = new WebflowElementCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    WebflowElement ret = cmd.getNewData();
    getLogger().info("Updated Webflow Element Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

}
