/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

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
import com.bosch.caltool.icdm.bo.cdr.FeatureValueLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_FEATURE_VALUE)
public class FeatureValueService extends AbstractRestService {


  /**
   * fetch all the feature values for the given feature id
   *
   * @param featureId featureID
   * @return response of all feature values
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_BY_FEA_ID)
  @CompressData
  public Response getFeaValForFeatureId(@QueryParam(value = WsCommonConstants.RWS_QP_FEATURE_ID) final Long featureId)
      throws IcdmException {

    FeatureValueLoader feaValLoader = new FeatureValueLoader(getServiceData());
    // get all feature values of the given feature id
    List<FeatureValue> fValForFeatureId = feaValLoader.getFeatureValues(featureId);
    // return the feature values
    return Response.ok(fValForFeatureId).build();
  }


}
