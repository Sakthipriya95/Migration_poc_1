package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;
import java.util.Set;

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
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireResponseLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVersionData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;


/**
 * Service class for RvwQnaireRespVersion
 *
 * @author say8cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVWQNAIRERESPVERSION))
public class RvwQnaireRespVersionService extends AbstractRestService {


  /**
   * Get RvwQnaireRespVersion using its id
   *
   * @param objId object's id
   * @return Rest response, with RvwQnaireRespVersion object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    RvwQnaireRespVersionLoader loader = new RvwQnaireRespVersionLoader(getServiceData());
    RvwQnaireRespVersion ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all RvwQnaireRespVersion records
   *
   * @param qnaireRespId as input
   * @return Rest response, with Map of RvwQnaireRespVersion objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_QNAIRE_RESP_VERS_BY_RESPID)
  @CompressData
  public Response getQnaireRespVersionsByRespId(
      @QueryParam(WsCommonConstants.RVW_QNAIRE_RESP_ID) final Long qnaireRespId)
      throws IcdmException {
    RvwQnaireRespVersionLoader loader = new RvwQnaireRespVersionLoader(getServiceData());
    Map<Long, RvwQnaireRespVersion> retMap = loader.getQnaireRespVersionsByRespId(qnaireRespId);
    getLogger().info(" RvwQnaireRespVersion getAllQnaireRespVersion completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * @param qnarieRespModel QnaireRespVersionModel
   * @return Response
   * @throws IcdmException exception during creation of response version
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final RvwQnaireRespVersion qnarieRespModel) throws IcdmException {
    RvwQnaireRespVersionCommand qnaireRespVersionCommand =
        new RvwQnaireRespVersionCommand(getServiceData(), qnarieRespModel, false, false, false, false);
    executeCommand(qnaireRespVersionCommand);
    RvwQnaireRespVersion ret = qnaireRespVersionCommand.getNewData();
    getLogger().info("Created Review Questionaire response Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * @param qnarieRespModel as RvwQnaireRespVersion
   * @return as RvwQnaireResponse
   * @throws IcdmException as IcdmException
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final RvwQnaireRespVersion qnarieRespModel) throws IcdmException {
    RvwQnaireRespVersionCommand cmd =
        new RvwQnaireRespVersionCommand(getServiceData(), qnarieRespModel, true, false, false, false);
    executeCommand(cmd);
    RvwQnaireRespVersion ret = cmd.getNewData();
    getLogger().info("Updated Review Questionaire Response Version Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Get all RvwQnaireRespVersion records
   *
   * @param qnaireRespVersDataSet qnaireDataSet
   * @return Rest response, Map with qnaire resp id and QnaireRespVersionData
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_LATEST_RVW_QNAIRE_RESP_VERS)
  @CompressData
  public Map<Long, QnaireRespVersionData> getLatestRvwQnaireRespVersion(
      final Set<QnaireRespStatusData> qnaireRespVersDataSet)
      throws IcdmException {

    Map<Long, QnaireRespVersionData> reponseMap =
        new RvwQnaireResponseLoader(getServiceData()).getQnaireRespVersData(qnaireRespVersDataSet);
    getLogger().info(" RvwQnaireRespVersion getAllQnaireRespVersion completed for data set. Total records = {}",
        reponseMap.size());
    return reponseMap;
  }
}
