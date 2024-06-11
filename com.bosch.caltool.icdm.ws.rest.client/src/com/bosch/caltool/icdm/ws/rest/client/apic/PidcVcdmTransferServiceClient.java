/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Arrays;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVcdmTransferInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */
public class PidcVcdmTransferServiceClient extends AbstractRestServiceClient {

  private static final IMapper PIDC_VCDM_INPUT_MAPPER = obj -> Arrays.asList(((PidcVcdmTransferInput) obj).getPidc());

  private static final IMapper PIDC_VCDM_OUTPUT_MAPPER = obj -> Arrays.asList((Pidc) obj);

  /**
   * Constructor
   */
  public PidcVcdmTransferServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_TRANSFER_VCDM_DATA);
  }


  /**
   * @param input transfer input
   * @return whether transfer successful or not
   * @throws ApicWebServiceException exception while invoking service
   */
  public Pidc transferPidc(final PidcVcdmTransferInput input) throws ApicWebServiceException {

    LOGGER.info("PIDC transfer to vCDM started. PIDC ID = {}", input.getPidc().getId());
    Pidc ret = null;

    ret = update(getWsBase(), input, Pidc.class, PidcVcdmTransferServiceClient.PIDC_VCDM_INPUT_MAPPER,
        PidcVcdmTransferServiceClient.PIDC_VCDM_OUTPUT_MAPPER);

    LOGGER.info("PIDC transfer to vCDM completed. Return status = {}", (ret != null));

    return ret;
  }

  /**
   * @param aprjValue String
   * @return APRJ
   * @throws ApicWebServiceException exception
   */
  public String findAPRJId(final String aprjValue) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_ATTRIBUTE_VALUE, aprjValue);
    return get(wsTarget, String.class);
  }

}
