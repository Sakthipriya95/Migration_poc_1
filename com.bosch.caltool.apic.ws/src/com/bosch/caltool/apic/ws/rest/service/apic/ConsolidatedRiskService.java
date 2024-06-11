/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.rm.ConsolidatedRiskLoader;
import com.bosch.caltool.icdm.bo.rm.PidcRmDefinitionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.rm.ConsolidatedRisks;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;


/**
 * Get Icdm functions which contains the search string
 *
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_RISK_DEFINTION)
public class ConsolidatedRiskService extends AbstractRestService {


  /**
   * @param pidcRmId pidcRmId
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_RISK_CONS_CAT_RISK)
  @CompressData
  
  // method fetches the consolidated risks
  public Response getConsolidatedRisks(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_RM_ID) final Long pidcRmId)
      throws IcdmException {

    //fetch the service data
    ServiceData serviceData = getServiceData();

    //load the service data 
    ConsolidatedRiskLoader loader = new ConsolidatedRiskLoader(serviceData);

    // Fetch all pidc rm defintions
    ConsolidatedRisks consRiskMap = loader.getConsolidatedRisk(pidcRmId);

    return Response.ok(consRiskMap).build();

  }

  /**
   * @param pidcVersId pidc versId
   * @return Response with list of functions
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @Path(WsCommonConstants.RWS_RISK_CONS_VERSION)
  @CompressData
  public Response getConsolidatedRiskPidc(@QueryParam(value = WsCommonConstants.RWS_QP_VERS_ID) final Long pidcVersId)
      throws IcdmException {

    // fetch the service data
    ServiceData serviceData = getServiceData();
    // load the service data
    PidcRmDefinitionLoader rmDefLoader = new PidcRmDefinitionLoader(serviceData);

    // Fetch all pidc rm defintions
    SortedSet<PidcRmDefinition> pidRmDefintions = rmDefLoader.getPidRmDefintions(pidcVersId);
    ConsolidatedRisks consRisks = null;
    if (CommonUtils.isNotEmpty(pidRmDefintions)) {
      ConsolidatedRiskLoader loader = new ConsolidatedRiskLoader(serviceData);
      // fetch the consolidated risks
      consRisks = loader.getConsolidatedRisk(pidRmDefintions.first().getId());
    }
    else {
      consRisks = new ConsolidatedRisks();
    }
    return Response.ok(consRisks).build();

  }


}
