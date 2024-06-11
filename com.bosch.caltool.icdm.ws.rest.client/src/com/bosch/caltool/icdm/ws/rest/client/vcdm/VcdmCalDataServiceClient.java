/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import java.io.File;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.vcdm.VcdmCalDataInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmCalDataServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public VcdmCalDataServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_VCDM, WsCommonConstants.RWS_VCDM_CALDATA);
  }

  /**
   * Fetches vCDM cal data object in cdfx file
   *
   * @param vcdmCalDataInput input for the service
   * @param filePath File path to download the CDFx file
   * @throws ApicWebServiceException Exception in retrieving cal data from vCDM
   */
  public void fetchVcdmCalDataCdfx(final VcdmCalDataInput vcdmCalDataInput, final String filePath)
      throws ApicWebServiceException {

    LOGGER.debug("Downloading cal data as CDFx file. path - {}", filePath);
    File cdfxFile = new File(filePath);
    downloadFilePost(getWsBase(), vcdmCalDataInput, cdfxFile.getName(), cdfxFile.getParent());
  }

  /**
   * Fetches vCDM cal data object in serialized file
   *
   * @param vcdmCalDataInput input for the service
   * @param filePath File path to download the serialized cal data file
   * @throws ApicWebServiceException Exception in retrieving cal data from vCDM
   */
  public void fetchVcdmCalDataSerialized(final VcdmCalDataInput vcdmCalDataInput, final String filePath)
      throws ApicWebServiceException {

    LOGGER.debug("Downloading cal data as serialized file. path - {}", filePath);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SERIALIZED);
    File cdfxFile = new File(filePath);
    downloadFilePost(wsTarget, vcdmCalDataInput, cdfxFile.getName(), cdfxFile.getParent());
  }
}
