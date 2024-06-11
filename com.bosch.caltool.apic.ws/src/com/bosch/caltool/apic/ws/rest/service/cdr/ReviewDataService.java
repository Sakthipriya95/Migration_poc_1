/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRResultParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.ParameterRuleFetcher;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewDataInputModel;
import com.bosch.caltool.icdm.model.cdr.ReviewDataParamResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewParamResponse;


/**
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CONTEXT_REVIEW_DATA)
public class ReviewDataService extends AbstractRestService {


  /**
   * @param reviewDataInputModel input model
   * @return Review Data Param Response
   * @throws IcdmException data retrieval error
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getReviewData(final ReviewDataInputModel reviewDataInputModel) throws IcdmException {
    ParameterLoader loader = new ParameterLoader(getServiceData());
    List<Long> varCodedParamIdList = new ArrayList<>();

    if (!CommonUtils.isEmptyString(reviewDataInputModel.getVarCodedParam())) {
      /* Fetch all the parameters for the base parameter */
      varCodedParamIdList =
          loader.fetchAllVarCodedParam(ApicUtil.getBaseParamFirstName(reviewDataInputModel.getVarCodedParam()),
              ApicUtil.getBaseParamLastName(reviewDataInputModel.getVarCodedParam()));
    }

    // Create parameter properties loader object
    CDRResultParameterLoader cdrResultLoader = new CDRResultParameterLoader(getServiceData());
    List<String> paramNames = reviewDataInputModel.getParamName();

    // Build the response object and set parameter review response to it
    Map<String, ReviewParamResponse> response = cdrResultLoader.getParamReviewRsponse(paramNames, varCodedParamIdList);

    Map<String, Parameter> paramMap = loader.getParamMapByParamNames(paramNames);

    // Fetch all the rules for the parameter
    ParameterRuleFetcher ruleFetcher = new ParameterRuleFetcher(getServiceData());
    ParameterRulesResponse rulesOutput = ruleFetcher.getParamRuleOutput(null, null, paramMap);
    rulesOutput.setParamMap(paramMap);

    ReviewDataParamResponse reviewDataParamResponse = new ReviewDataParamResponse();
    reviewDataParamResponse.setParameterRulesResponse(rulesOutput);
    reviewDataParamResponse.setReviewParamResponse(response);

    getLogger().info("getReviewData() completed for paramater(s) : {}", reviewDataInputModel.getParamName());
    return Response.ok(reviewDataParamResponse).build();
  }
}
