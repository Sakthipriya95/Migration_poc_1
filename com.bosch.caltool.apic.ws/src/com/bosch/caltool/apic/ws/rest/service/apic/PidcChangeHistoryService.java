/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcChangeHistoryLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsForVersType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsResponseType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcDiffsType;


/**
 * Service class for PidcChangeHistory
 *
 * @author dmr1cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_PIDCCHANGEHISTORY))
public class PidcChangeHistoryService extends AbstractRestService {

  /**
   * Get all PidcChangeHistory records
   *
   * @param pidcDiff {@link PidcDiffsType}
   * @return Rest response, with Map of PidcChangeHistory objects
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_PIDC_DIFFS)
  @CompressData
  public Response getPidcDiffs(final PidcDiffsType pidcDiff) throws IcdmException {
    PidcChangeHistoryLoader loader = new PidcChangeHistoryLoader(getServiceData());
    PidcDiffsResponseType pidcDiffsResponseType = loader.getPidcDiffs(pidcDiff);
    return Response.ok(pidcDiffsResponseType).build();
  }

  /**
   * @param allPidcDiffsForVersType PidcDiffsForVersType
   * @return List<AttrDiffType> object
   * @throws IcdmException exception from the service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_ATTR_DIFF_FOR_VERSION)
  @CompressData
  public Response getPidcAttrDiffForVersion(final PidcDiffsForVersType allPidcDiffsForVersType) throws IcdmException {
    PidcChangeHistoryLoader loader = new PidcChangeHistoryLoader(getServiceData());
    return Response.ok(loader.getPidcAttrDiffReportForVersion(allPidcDiffsForVersType)).build();
  }

  /**
   * @param allPidcDiffsForVersType PidcDiffsForVersType
   * @return PidcDiffsResponseType object
   * @throws IcdmException exception from the service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_PIDC_DIFF_FOR_VERSION)
  @CompressData
  public Response getAllPidcDiffForVersion(final PidcDiffsForVersType allPidcDiffsForVersType) throws IcdmException {
    PidcChangeHistoryLoader loader = new PidcChangeHistoryLoader(getServiceData());
    return Response.ok(loader.getAllPidcDiffForVersion(allPidcDiffsForVersType)).build();
  }
}
