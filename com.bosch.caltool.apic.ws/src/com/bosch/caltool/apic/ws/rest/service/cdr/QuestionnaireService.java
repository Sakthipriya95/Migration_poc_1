/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;
import java.util.SortedSet;

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
import com.bosch.caltool.icdm.bo.cdr.qnaire.QnaireCreationCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireCommand;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireLoader;
import com.bosch.caltool.icdm.bo.cdr.qnaire.QuestionnaireVersionLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireCreationModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.general.DataCreationModel;


/**
 * Service class for Questionnaire
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_QUESTIONNAIRE)
public class QuestionnaireService extends AbstractRestService {

  /**
   * Get Questionnaire using its id
   *
   * @param objId object's id
   * @return Rest response, with Questionnaire object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    QuestionnaireLoader loader = new QuestionnaireLoader(getServiceData());
    Questionnaire ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * @param objId
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL_VERSIONS)
  @CompressData
  public Response getAllVersions(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    SortedSet<QuestionnaireVersion> ret = new QuestionnaireVersionLoader(getServiceData()).getAllVersions(objId);
    return Response.ok(ret).build();
  }

  /**
   * Get all Questionnaire records
   *
   * @param includeDeleted if true, deleted questionnaires are also returned
   * @param includeQnaireWithoutQues include qnaire without question flag
   * @return Rest response, with Map of Questionnaire objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll(@QueryParam(value = WsCommonConstants.RWS_QP_INCLUDE_DELETED) final boolean includeDeleted,
      @QueryParam(value = WsCommonConstants.RWS_QP_INCLUDE_QNAIRE_WITHOUT_QUEST) final boolean includeQnaireWithoutQues)
      throws IcdmException {
    QuestionnaireLoader loader = new QuestionnaireLoader(getServiceData());
    Map<Long, Questionnaire> retMap = loader.getAll(includeDeleted, includeQnaireWithoutQues);
    getLogger().info("Questionnaire getAll completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * @param questionnaireId questionnaireId
   * @return response
   * @throws IcdmException exception during web service call
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_WORKING_SET)
  @CompressData
  public Response getWorkingSet(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long questionnaireId)
      throws IcdmException {
    QuestionnaireVersion ret = new QuestionnaireVersionLoader(getServiceData()).getWorkingSet(questionnaireId);
    getLogger().info("working set  loaded for the questionnaireId = ", questionnaireId);
    return Response.ok(ret).build();
  }


  /**
   * Creates the qnaire & qnaire version.
   *
   * @param obj QnaireCreationModel
   * @return Response, with created Questionnaire object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CREATE_QNAIRE_MODEL)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response createQnaireAndVersion(final QnaireCreationModel obj) throws IcdmException {
    QnaireCreationCommand cmd = new QnaireCreationCommand(getServiceData(), obj);
    executeCommand(cmd);
    Questionnaire qnaire = new QuestionnaireLoader(getServiceData()).getDataObjectByID(obj.getQnaire().getId());
    // DataCreationModel is used as response object so that both Questionnaire and NodeAccess object can be available in
    // response object
    DataCreationModel<Questionnaire> ret = new DataCreationModel<>();
    ret.setDataCreated(qnaire);
    // Use NodeAccessLoader to get the created node access object for the user who has created questionnaire
    ret.setNodeAccess(new NodeAccessLoader(getServiceData()).getDataObjectByID(obj.getNodeAccess().getId()));
    getLogger().info("Created Questionnaire Id : {}", ret.getDataCreated().getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a Questionnaire record
   *
   * @param obj object to update
   * @return Rest response, with updated Questionnaire object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final Questionnaire obj) throws IcdmException {
    QuestionnaireCommand cmd = new QuestionnaireCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    Questionnaire ret = cmd.getNewData();
    getLogger().info("Updated Questionnaire Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

}
