/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcScout;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.precal.PreCalibrationDataLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchInput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSearchResult;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataInput;
import com.bosch.caltool.icdm.model.cdr.PreCalibrationDataResponse;

/**
 * Rest WebService for fetching pre caldata from review results
 *
 * @author svj7cob
 */
// Task 243510
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CDR_PRECALDATA)
public class PreCalibrationDataService extends AbstractRestService {


  /**
   * Fetch the pre cal data
   *
   * @param input preCalDataReviewResultInput
   * @return the response response
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response fetchPreCalData(final PreCalibrationDataInput input) throws IcdmException {

    getLogger().info("fetchPreCalData started. Paramater Ids = {};", input.getParameterIdSet());

    // initialising the pidc search for filtering attr & values
    PidcSearchInput searchIp = new PidcSearchInput();
    searchIp.getSearchConditions().addAll(input.getPidcSearchConditionSet());

    // Task 251282 Exclude focus matrix status in pidc search with a flag
    PidcSearchResponse scResp = new PidcScout(getServiceData(), searchIp).findProjects();

    // attr-id & value-id matches with variants
    Set<Long> variantIdSet = new HashSet<>();

    // attr-id & value-id matches with pidc version having no variants
    Set<Long> pidcVersIdWithNoVarSet = new HashSet<>();

    // attr-id & value-id matches with pidc version having variants
    Set<Long> pidcVersIdWithVarsSet = new HashSet<>();

    for (PidcSearchResult pidcSearchResult : scResp.getResults()) {
      Long pidcVersId = pidcSearchResult.getPidcVersion().getId();
      Set<Long> variantIds = pidcSearchResult.getVariantMap().keySet();
      if (CommonUtils.isNotEmpty(variantIds)) {
        // if variant id available
        variantIdSet.addAll(variantIds);
      }
      else {
        if (new PidcVariantLoader(getServiceData()).hasVariants(pidcVersId, false)) {
          pidcVersIdWithVarsSet.add(pidcVersId);
        }
        else {
          pidcVersIdWithNoVarSet.add(pidcVersId);
        }
      }
    }

    // Fetch the pre-caldata information from database
    PreCalibrationDataLoader preCalDataProvider = new PreCalibrationDataLoader(getServiceData());

    PreCalibrationDataResponse result = preCalDataProvider.loadReviewResultPreCalData(pidcVersIdWithNoVarSet,
        pidcVersIdWithVarsSet, variantIdSet, input);

    getLogger().info("PreCalibrationDataService.getParamPreCalDataDetails() completed. Number of definitions = {}",
        null == result.getParamPreCalDataDetails() ? 0 : result.getParamPreCalDataDetails().size());

    // return the response
    return Response.ok(result).build();
  }

}
