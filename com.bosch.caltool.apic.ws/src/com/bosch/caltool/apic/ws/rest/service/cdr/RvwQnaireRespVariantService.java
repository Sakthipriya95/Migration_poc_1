/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.qnaire.RvwQnaireRespVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.qnaire.DefineQnaireRespInputData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author dmr1cob
 */

/**
 * Service class for {@link RvwQnaireRespVariant}
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVW_QNAIRE_RESPONSE_VARIANT)
public class RvwQnaireRespVariantService extends AbstractRestService {


  /**
   * @param objId Qnaire response variant id
   * @return Qnaire response variant
   * @throws IcdmException exception in web service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getById(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    return Response.ok(new RvwQnaireRespVariantLoader(getServiceData()).getDataObjectByID(objId)).build();
  }


  /**
   * This service returns List of review questionnaire response variants from the given review questionnaire response id
   *
   * @param rvwQnaireRespId {@link RvwQnaireResponse} Id
   * @return {@link RvwQnaireRespVariant} id
   * @throws IcdmException Exception in webservice
   */
  @GET
  @Path(WsCommonConstants.RWS_RVW_QNAIRE_RESPONSE_VARIANT_LIST)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public List<RvwQnaireRespVariant> getRvwQnaireRespVariant(
      @QueryParam(WsCommonConstants.RVW_QP_QNAIRE_RESP_ID) final Long rvwQnaireRespId)
      throws IcdmException {
    return new RvwQnaireRespVariantLoader(getServiceData()).getRvwQnaireRespVariant(rvwQnaireRespId);
  }

  /**
   * @param pidcVersId selected pidc version id
   * @return {@link DefineQnaireRespInputData} object
   * @throws IcdmException exception in ws call
   */
  @GET
  @Path(WsCommonConstants.RWS_DEFINE_QNAIRERESP_INPUT_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public DefineQnaireRespInputData getDefineQnaireRespInputData(
      @QueryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID) final Long pidcVersId)
      throws IcdmException {
    return new RvwQnaireRespVariantLoader(getServiceData()).getDefineQnaireRespInputData(pidcVersId);
  }
}
