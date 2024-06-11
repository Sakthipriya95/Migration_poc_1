/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic.emr;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.emr.EmrFileEmsVariantMapping;
import com.bosch.caltool.icdm.model.emr.EmrPidcVariant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class EmrFileVariantServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public EmrFileVariantServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_EMISSION_ROBUSTNESS +
        WsCommonConstants.RWS_URL_DELIMITER + WsCommonConstants.RWS_EMR_FILE_VARIANT);
  }

  /**
   * Get all the mapping of Pidc EMR file-EMS-dvariants For fileId
   *
   * @param fileIds emr file ids
   * @return List of EMR file-variant-EMS mapping
   * @throws ApicWebServiceException error during service call
   */
  public EmrFileEmsVariantMapping getPidcEmrFileEmsVariantMapping(final Set<Long> emrFileIds)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_EMS_VARIANT_MAP)
        .queryParam(WsCommonConstants.RWS_EMR_FILE_ID, emrFileIds.toArray());
    GenericType<EmrFileEmsVariantMapping> type = new GenericType<EmrFileEmsVariantMapping>() {};
    return get(wsTarget, type);
  }

  /**
   * Save the mapping of Pidc EMR file-EMS-dvariants For fileId
   *
   * @param emrPidcVariants emr file variants
   * @return Map of EMR file-variant-EMS mapping,key= EmrPidcVariant id,value=EmrPidcVariant
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, EmrPidcVariant> saveEmrFileEmsVariantMapping(final Set<EmrPidcVariant> emrPidcVariants)
      throws ApicWebServiceException {
    GenericType<Map<Long, EmrPidcVariant>> type = new GenericType<Map<Long, EmrPidcVariant>>() {};
    return create(getWsBase(), emrPidcVariants, type);
  }

  /**
   * Deletes the mapping of Pidc EMR file-EMS-dvariants For given Ids
   *
   * @param emrPidcVariants EmrPidcVariant/(s) to be deleted
   * @throws ApicWebServiceException error during service call
   */
  public void deleteEmrFileEmsVariantMapping(final Set<EmrPidcVariant> emrPidcVariants) throws ApicWebServiceException {
    delete(getWsBase(), emrPidcVariants);
  }
}
