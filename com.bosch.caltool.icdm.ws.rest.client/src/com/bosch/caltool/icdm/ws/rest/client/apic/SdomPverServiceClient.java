/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class SdomPverServiceClient extends AbstractRestServiceClient {

  
  private static final String NUMBER_OF_ITEMS_MSG = "Number of items = {}";

  /**
   * Constructor
   */
  public SdomPverServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_SDOM_PVER);
  }

  /**
   * Get all PVER names in the system
   *
   * @return all PVER names
   * @throws ApicWebServiceException error during service call
   */
  public SortedSet<String> getAllPverNames() throws ApicWebServiceException {

    LOGGER.debug("Get all PVER names.. ");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PVER_NAMES);

    GenericType<SortedSet<String>> type = new GenericType<SortedSet<String>>() {};
    SortedSet<String> retSet = get(wsTarget, type);

    LOGGER.debug(NUMBER_OF_ITEMS_MSG, retSet.size());

    return retSet;
  }

  /**
   * Get all PVER Variants, for the given input
   *
   * @param pver PVER name
   * @return sorted set of variants
   * @throws ApicWebServiceException error during service call
   */
  public SortedSet<String> getPverVariantNames(final String pver) throws ApicWebServiceException {

    LOGGER.debug("Get all PVER variants for given pver - {}.. ", pver);

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PVER_VARS).queryParam(WsCommonConstants.RWS_QP_NAME, pver);

    GenericType<SortedSet<String>> type = new GenericType<SortedSet<String>>() {};
    SortedSet<String> retSet = get(wsTarget, type);

    LOGGER.debug("Number of Pver variant names returned = {}", retSet.size());

    return retSet;
  }

  /**
   * Get all PVER Variant versions, for the given inputs
   *
   * @param pver PVER name
   * @param pverVariant PVER variant
   * @return sorted set of versions
   * @throws ApicWebServiceException error during service call
   */
  public SortedSet<Long> getPverVariantVersions(final String pver, final String pverVariant)
      throws ApicWebServiceException {

    LOGGER.debug("Get all PVER variants for given pver - {}, pver variant - {} .. ", pver, pverVariant);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PVER_VAR_VERSIONS)
        .queryParam(WsCommonConstants.RWS_QP_NAME, pver).queryParam(WsCommonConstants.RWS_QP_VARIANT, pverVariant);

    GenericType<SortedSet<Long>> type = new GenericType<SortedSet<Long>>() {};
    SortedSet<Long> retSet = get(wsTarget, type);

    LOGGER.debug("Number of items returned = {}", retSet.size());

    return retSet;
  }

  /**
   * @param pidcVersionId PIDC Version ID
   * @return SET of SDOM PVER names
   * @throws ApicWebServiceException error during web service call
   */
  public Set<String> getAllPverNamesForPidcVersion(final long pidcVersionId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_SDOM_FOR_PIDC_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersionId);
    GenericType<Set<String>> type = new GenericType<Set<String>>() {};
    Set<String> retSet = get(wsTarget, type);

    LOGGER.debug("Number of items returned = {}", retSet.size());

    return retSet;
  }


  /**
   * @param pidcVersionId PIDC Version ID
   * @param pidcVariantId PIDC Variant ID (can be null)
   * @return SDOM PVER name applicable
   * @throws ApicWebServiceException error from service call
   */
  public String getSDOMPverName(final Long pidcVersionId, final Long pidcVariantId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SDOM_PVER)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VARIANT_ID, pidcVariantId);
    String ret = get(wsTarget, String.class);
    LOGGER.debug("SDOM PVER returned for PIDC Version {}, variant {} = {}", pidcVersionId, pidcVariantId, ret);
    return ret;
  }

  /**
   * Get SDOM PVERs by PIDC
   *
   * @param pidcId PIDC ID
   * @return the Map of pver names associated with a PIDC. Key - pidc version ID, value set of SDOM-PVER names
   * @throws ApicWebServiceException error from service call
   */
  public Map<Long, SortedSet<String>> getSdomPverByPidc(final Long pidcId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SDOMPVER_NAMES_BY_PIDC)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);
    GenericType<Map<Long, SortedSet<String>>> type = new GenericType<Map<Long, SortedSet<String>>>() {};
    Map<Long, SortedSet<String>> retSet = get(wsTarget, type);
    LOGGER.debug("Number of items returned = {}", retSet.size());

    return retSet;
  }

}
