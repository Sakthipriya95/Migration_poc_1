/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.ssd;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.SSDReleaseFetcher;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;

/**
 * @author dmr1cob Rest web service to get SSD Release set by Software version id
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_CONTEXT_SSD_RELEASE)
public class SSDReleaseService extends AbstractRestService {

  /**
   * @param swVersionId software version id
   * @return ssdrelease list
   * @throws IcdmException Exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_SSD_RELEASE)
  @CompressData
  public List<SSDReleaseIcdmModel> getSSDReleaseList(
      @QueryParam(value = WsCommonConstants.RWS_QP_SW_VERSION_ID) final long swVersionId) throws IcdmException {
    WSObjectStore.getLogger().info("SSDReleaseService.getSSDReleaseList() started. SSD sofware version id = {}",
        swVersionId);
    SSDReleaseFetcher fetcher = new SSDReleaseFetcher(getServiceData());
    List<SSDReleaseIcdmModel> ssdReleaseList = fetcher.getSSDReleaseListbyswVersionId(swVersionId);
    WSObjectStore.getLogger().info("SSDReleaseService.getSSDReleaseList() completed. SSD releases found = {}",
        ssdReleaseList.size());
    return ssdReleaseList;


  }

  /**
   * @param dependencyList FeatureValueICDMModel list
   * @return SSDFeatureICDMAttrModel list
   * @throws IcdmException Exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_SSD_FEATURE_ATTR_MODEL)
  @CompressData
  public List<SSDFeatureICDMAttrModel> getSSDFeatureICDMAttrModelList(final List<FeatureValueICDMModel> dependencyList)
      throws IcdmException {
    WSObjectStore.getLogger().info(
        "SSDReleaseService.getSSDFeatureICDMAttrModelList() started. FeatureValueICDMModel list count",
        dependencyList.size());
    SSDReleaseFetcher fetcher = new SSDReleaseFetcher(getServiceData());
    List<SSDFeatureICDMAttrModel> ssdFeatureICDMAttrModel = fetcher.getSSDFeatureICDMAttrModel(dependencyList);
    WSObjectStore.getLogger().info(
        "SSDReleaseService.getSSDFeatureICDMAttrModelList() completed. SSDFeatureICDMAttrModel found = {}",
        ssdFeatureICDMAttrModel.size());
    return ssdFeatureICDMAttrModel;
  }
}
