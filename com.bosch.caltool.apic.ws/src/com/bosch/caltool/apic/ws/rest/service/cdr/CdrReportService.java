/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.calcomp.commonutil.CommonUtils;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReportBO;
import com.bosch.caltool.icdm.bo.cdr.CDRReportLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRReportData;
import com.bosch.caltool.icdm.model.cdr.CDRReportModel;
import com.bosch.caltool.icdm.model.cdr.CdrReport;


/**
 * Rest service to fetch review details of all parameters for the given A2L file
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDR_REPORT)
public class CdrReportService extends AbstractRestService {

  /**
   * Get the review details.
   *
   * @param pidcA2lId PIDC Version - A2L File mapping ID
   * @param fetchCheckVal true if checkvalue has to be fetched
   * @param variantID PIDC Variant ID
   * @param maxReviews maximum reviews to be considered
   * @param a2lRespId A2l Resp Id
   * @param a2lWpId A2l Wp Id
   * @return Rest response
   * @throws IcdmException error while fetching data
   */
  @GET
  @Path(WsCommonConstants.RWS_CDR_REPORT_MODEL)
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @CompressData
  public Response createCDRReport(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @QueryParam(value = WsCommonConstants.RWS_QP_FETCH_CHECKVAL) final boolean fetchCheckVal,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantID,
      @QueryParam(value = WsCommonConstants.RWS_CDRRPT_QP_MAX_REVIEWS) final int maxReviews,
      @QueryParam(value = WsCommonConstants.RWS_A2L_RESP_ID) final Long a2lRespId,
      @QueryParam(value = WsCommonConstants.A2L_WP_ID) final Long a2lWpId)
      throws IcdmException {

    WSObjectStore.getLogger().info(
        "CdrReportService started. User Inputs : {} = {}; {} = {}; {} = {}; {} = {}; {} = {}; {} = {}",
        WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId, WsCommonConstants.RWS_QP_VARIANT_ID, variantID,
        WsCommonConstants.RWS_CDRRPT_QP_MAX_REVIEWS, maxReviews, WsCommonConstants.RWS_QP_FETCH_CHECKVAL, fetchCheckVal,
        WsCommonConstants.RWS_A2L_RESP_ID, a2lRespId, WsCommonConstants.A2L_WP_ID, a2lWpId);

    // Fetch the review information from database
    CDRReportData cdrRprtData = new CDRReportData();
    cdrRprtData.setPidcA2lId(pidcA2lId);
    cdrRprtData.setA2lRespId(a2lRespId);
    cdrRprtData.setA2lWpId(a2lWpId);
    cdrRprtData.setMaxResults(maxReviews);
    cdrRprtData.setFetchCheckVal(fetchCheckVal);
    cdrRprtData.setVarId(variantID);

    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2lFileExt pidcA2lFileExt = pidcA2lLoader.getPidcA2LDetails(pidcA2lId);
    Long pidcVersionId = pidcA2lFileExt.getPidcA2l().getPidcVersId();

    PidcVariantLoader pidcVariantLoader = new PidcVariantLoader(getServiceData());
    Map<Long, PidcVariant> pidcVariantdetails = pidcVariantLoader.getVariants(pidcVersionId, false);

    if (!pidcVariantdetails.isEmpty()) {
      if (CommonUtils.isNull(variantID)) {
        throw new InvalidInputException("Variant should not be empty for this pidcA2lId");
      }
      else if (!pidcVariantdetails.containsKey(variantID)) {
        throw new InvalidInputException("Invalid VariantID, please pass a valid VariantID for this pidcA2lId");
      }
    }
    else if (CommonUtils.isNotNull(variantID)) {
      throw new InvalidInputException("VariantID is not applicable for this pidcA2lId");
    }


    if (CommonUtils.isNull(a2lRespId) && CommonUtils.isNotNull(a2lWpId)) {
      throw new InvalidInputException("Invalid input, a2lRespId is mandatory while passing a2lWpId");
    }


    CDRReportModel report = new CDRReportBO(getServiceData(), cdrRprtData).getCDRReport();

    WSObjectStore.getLogger().info("CdrReportService completed. Number of CDR Report parameters = {}",
        report.getCdrReportParams().size());

    return Response.ok(report).build();
  }

  /**
   * Get the review details.
   *
   * @param pidcA2lId PIDC Version - A2L File mapping ID
   * @param fetchCheckVal true if checkvalue has to be fetched
   * @param variantID PIDC Variant ID
   * @param maxReviews maximum reviews to be considered
   * @param a2lRespId A2l Resp Id
   * @param a2lWpId A2l Wp Id
   * @return Rest response
   * @throws IcdmException error while fetching data
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @CompressData
  public Response createReport(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @QueryParam(value = WsCommonConstants.RWS_QP_FETCH_CHECKVAL) final boolean fetchCheckVal,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantID,
      @QueryParam(value = WsCommonConstants.RWS_CDRRPT_QP_MAX_REVIEWS) final int maxReviews,
      @QueryParam(value = WsCommonConstants.RWS_A2L_RESP_ID) final Long a2lRespId,
      @QueryParam(value = WsCommonConstants.A2L_WP_ID) final Long a2lWpId)
      throws IcdmException {

    WSObjectStore.getLogger().info(
        "CdrReportService started. User Inputs : {} = {}; {} = {}; {} = {}; {} = {}; {} = {}; {} = {}",
        WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId, WsCommonConstants.RWS_QP_VARIANT_ID, variantID,
        WsCommonConstants.RWS_CDRRPT_QP_MAX_REVIEWS, maxReviews, WsCommonConstants.RWS_QP_FETCH_CHECKVAL, fetchCheckVal,
        WsCommonConstants.RWS_A2L_RESP_ID, a2lRespId, WsCommonConstants.A2L_WP_ID, a2lWpId);

    // Fetch the review information from database
    CDRReportData cdrRprtData = new CDRReportData();
    cdrRprtData.setPidcA2lId(pidcA2lId);
    cdrRprtData.setA2lRespId(a2lRespId);
    cdrRprtData.setA2lWpId(a2lWpId);
    cdrRprtData.setMaxResults(maxReviews);
    cdrRprtData.setFetchCheckVal(fetchCheckVal);
    cdrRprtData.setVarId(variantID);

    CdrReport report = new CDRReportLoader(getServiceData()).fetchCDRReportData(cdrRprtData);

    WSObjectStore.getLogger().info(
        "CdrReportService completed. Number of parameters with properties = {}; Number of parameters with reviews = {}; max reviews/parameter = {}; total reviews fetched = {}",
        report.getParamPropsMap().size(), report.getParamRvwDetMap().size(), report.getMaxParamReviewCount(),
        report.getReviewDetMap().size());

    return Response.ok(report).build();
  }
}
