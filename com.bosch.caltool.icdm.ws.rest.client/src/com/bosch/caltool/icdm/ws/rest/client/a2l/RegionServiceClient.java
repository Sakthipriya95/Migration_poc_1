/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.wp.Region;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author apj4cob
 *
 */
public class RegionServiceClient extends AbstractRestServiceClient{
  
  /**
   * Service client for WorkPackageDivServiceClient
   */
  public RegionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_REGION);
  }
  /**
   * Get all Region
   * 
   * @return region
   * @throws ApicWebServiceException any exception
   */
  public Map<String,Region> getAllRegion()throws ApicWebServiceException{
    WebTarget wsTarget =getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<String,Region>> type = new GenericType<Map<String,Region>>() {};
    return get(wsTarget, type);
 
  }
  /**
   * Get all Region
   * 
   * @return region
   * @throws ApicWebServiceException any exception
   */
  public Region getRegionById(final Long id)throws ApicWebServiceException{
    WebTarget wsTarget =getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, id);
    GenericType<Region> type = new GenericType<Region>() {};
    return get(wsTarget, type);
 
  }
}
