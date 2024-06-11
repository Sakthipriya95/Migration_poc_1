/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.FeatureLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_FEATURE)
public class FeatureService extends AbstractRestService {


  /**
   * Get all the features from t_SSD_features table
   *
   * @return all feature map
   * @throws IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_FEATURES)
  @CompressData
  public Response getAllFeatures() throws IcdmException {
    // Use the feature loader
    FeatureLoader feaLoader = new FeatureLoader(getServiceData());
    // fetch all feature map
    ConcurrentMap<Long, Feature> allFeatures = feaLoader.fetchAllFeatures();
    // Retrun the feature map
    return Response.ok(allFeatures).build();
  }


}
