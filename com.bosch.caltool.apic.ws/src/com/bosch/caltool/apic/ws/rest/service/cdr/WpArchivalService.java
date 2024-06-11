package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.WpArchivalLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.WpArchival;


/**
 * Service class for WpArchival
 *
 * @author msp5cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_WP_ARCHIVAL))
public class WpArchivalService extends AbstractRestService {


  /**
   * Get WpArchival using its id
   *
   * @param objId object's id
   * @return Rest response, with WpArchival object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    WpArchival ret = new WpArchivalLoader(getServiceData()).getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get WpArchival using its id Set
   *
   * @param objIdSet object's id Set
   * @return Rest response, with Map of WpArchival Id and object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WP_ARCHIVALS_BY_IDS)
  @CompressData
  public Response getWpArchivalsMap(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> objIdSet)
      throws IcdmException {
    Map<Long, WpArchival> ret = new WpArchivalLoader(getServiceData()).getDataObjectByID(objIdSet);
    return Response.ok(ret).build();
  }

  /**
   * @param pidcA2lId Pidc A2l Id
   * @return Set<WpArchival>
   * @throws IcdmException excpetion while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WP_ARCHIVAL_BASELINE)
  public Response getWpBaselinesForPidcA2l(@QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    Set<WpArchival> baselinesForPidcA2l = new WpArchivalLoader(getServiceData()).getBaselinesForPidcA2l(pidcA2lId);
    getLogger().info("Wp Archival Baselines for Pidc A2l Id {} is :{}", pidcA2lId, baselinesForPidcA2l.size());
    return Response.ok(baselinesForPidcA2l).build();
  }


  /**
   * @param pidcVersId Pidc Version Id (Optional)
   * @param pidcA2lId Pidc A2l Id (Optional)
   * @param variantId variantId, if -1 then considered as No-Variant Case (Optional)
   * @param respId respId (Optional)
   * @param wpName wpName (Optional)
   * @param nodeName nodeName (Optional)
   * @return Set<WpArchival>
   * @throws IcdmException excpetion while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_WP_ARCHIVAL_BASELINE_FILTERED_PIDC)
  public Response getFilteredWpBaselinesForPidc(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersId,
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @QueryParam(WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantId,
      @QueryParam(WsCommonConstants.RWS_QP_RESP_ID) final Long respId,
      @QueryParam(WsCommonConstants.RWS_QP_WP_ID) final String wpName,
      @QueryParam(WsCommonConstants.RWS_QP_NODE_NAME) final String nodeName)
      throws IcdmException {
    Set<WpArchival> baselinesForPidcA2l = new WpArchivalLoader(getServiceData()).getFilteredBaselinesForPidc(pidcVersId,
        pidcA2lId, variantId, respId, wpName, nodeName);
    getLogger().info("Wp Archival Baselines for Pidc A2l Id {} is :{}", pidcA2lId, baselinesForPidcA2l.size());
    return Response.ok(baselinesForPidcA2l).build();
  }


}
