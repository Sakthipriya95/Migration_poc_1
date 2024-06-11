/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.wp.RegionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.wp.Region;

/**
 * Services for Region
 *
 * @author apj4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_REGION)
public class RegionService extends AbstractRestService {

  /**
   * @param regId to get the data object using ID
   * @return Rest response,with CompPackage object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response getRegionById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long regId)
      throws IcdmException {

    RegionLoader regLoader = new RegionLoader(getServiceData());
    Region reg = regLoader.getDataObjectByID(regId);
    getLogger().info(" Region getRegion= {}", reg.getRegionNameEng());
    // get the region by region id
    return Response.ok(reg).build();
  }

  /**
   * load al the regions
   *
   * @return Rest response retuns the map of all regions
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllRegion() throws IcdmException {

    RegionLoader loader = new RegionLoader(getServiceData());
    // load all the regions
    Map<String, Region> retMap = loader.getAll();
    WSObjectStore.getLogger().info("RegionService.getAllRegion() completed.Number of regions={} ", retMap.size());
    // return the map of all regions with key as region name and value region object
    return Response.ok(retMap).build();
  }


}
