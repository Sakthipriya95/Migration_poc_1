/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.vcdm;

import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.vcdm.VcdmInterfaceProvider;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_VCDM + "/" + WsCommonConstants.RWS_VCDM_AVAILABILITY)
public class VcdmAvailabilityCheckService extends AbstractRestService {

  /**
   * @return VCDMInterface
   * @throws IcdmException Exception in creating vcdm interface for logged in user
   */
  @GET
  @CompressData
  public Response isVcdmCurrSuperLogin() throws IcdmException {
    VCDMInterface vcdmInf = new VcdmInterfaceProvider(getServiceData()).createInterfaceSuperUser();
    boolean ret = Objects.nonNull(vcdmInf);
    getLogger().debug("vCDM service availability : {}", ret);
    return Response.ok(ret).build();
  }
}
