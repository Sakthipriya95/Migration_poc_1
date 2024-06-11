/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstant;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.caltool.icdm.model.a2l.VCDMA2LFileDetail;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class A2LFileInfoServiceClient.
 *
 * @author gge6cob
 */
public class A2LFileInfoServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public A2LFileInfoServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FILE_INFO);
  }


  /**
   * Download serialized a2L file based on a2l file id
   *
   * @param a2lFileId the a 2 l file id
   * @param serializedFileName serialized a2l file name
   * @param destDirectory destination directory to download a2l
   * @throws ApicWebServiceException the apic web service exception
   */
  public void getA2LFileInfoSerialized(final Long a2lFileId, final String serializedFileName, final String destDirectory)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SERIALIZED_A2L_FILE_INFO)
        .queryParam(WsCommonConstants.RWS_QP_A2L_FILE_ID, a2lFileId);
    downloadFile(wsTarget, serializedFileName, destDirectory);
  }

  /**
   * Gets the all A 2 L sys constants.
   *
   * @return the all A 2 L sys constants
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<String, A2LSystemConstant> getAllA2LSysConstants() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_A2L_SYS_CONSTANTS);
    GenericType<Map<String, A2LSystemConstant>> type = new GenericType<Map<String, A2LSystemConstant>>() {};
    Map<String, A2LSystemConstant> retMap = get(wsTarget, type);
    LOGGER.debug("A2L System Constants loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Gets the a 2 L base components.
   *
   * @param a2lFileId the a 2 l file id
   * @return the a 2 L base components
   * @throws ApicWebServiceException the apic web service exception
   */
  public Map<String, A2LBaseComponents> getA2LBaseComponents(final Long a2lFileId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_A2L_BASE_COMP)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, a2lFileId);
    GenericType<Map<String, A2LBaseComponents>> type = new GenericType<Map<String, A2LBaseComponents>>() {};
    Map<String, A2LBaseComponents> retMap = get(wsTarget, type);
    LOGGER.debug("A2L System Constants loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get fcbcusage for bc.
   *
   * @param bcName name bc
   * @return list of fcbcUsage
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public List<FCBCUsage> getBCUsage(final String bcName) throws ApicWebServiceException {

    LOGGER.debug("Loading BCUsage for bc name = {}", bcName);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FCBC_USAGE_BY_BC)
        .queryParam(WsCommonConstants.RWS_QP_BC_NAME, bcName);

    GenericType<List<FCBCUsage>> type = new GenericType<List<FCBCUsage>>() {};

    List<FCBCUsage> response = get(wsTarget, type);

    LOGGER.debug("BCUsage loaded. No. of records : {}", response.size());

    return response;
  }


  /**
   * Get fcbcusage for fc.
   *
   * @param fcName name fc
   * @return list of fcbcUsage
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public List<FCBCUsage> getFCUsage(final String fcName) throws ApicWebServiceException {

    LOGGER.debug("Loading FCUsage for bc name = {}", fcName);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FCBC_USAGE_BY_FC)
        .queryParam(WsCommonConstants.RWS_QP_FC_NAME, fcName);

    GenericType<List<FCBCUsage>> type = new GenericType<List<FCBCUsage>>() {};

    List<FCBCUsage> response = get(wsTarget, type);

    LOGGER.debug("FCUsage loaded. No. of records : {}", response.size());

    return response;
  }

  /**
   * Gets the vcdm data sets.
   *
   * @param vcdmA2lFileID long
   * @return List<VCDMApplicationProject>
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public List<VCDMApplicationProject> getVcdmDataSets(final Long vcdmA2lFileID) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VCDM_DATASETS)
        .queryParam(WsCommonConstants.RWS_QP_VCDM_A2LFILE_ID, vcdmA2lFileID);

    GenericType<List<VCDMApplicationProject>> type = new GenericType<List<VCDMApplicationProject>>() {};

    List<VCDMApplicationProject> response = get(wsTarget, type);
    LOGGER.debug("vCDM DataSets loaded. No. of records : {}", response.size());

    return response;
  }

  /**
   * Gets the VCDMA 2 L file details.
   *
   * @param a2lFileCheckSum the a 2 l file check sum
   * @return the VCDMA 2 L file details
   * @throws ApicWebServiceException the apic web service exception
   */
  public Set<VCDMA2LFileDetail> getVCDMA2LFileDetails(final String a2lFileCheckSum) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VCDM_A2L_FILE_DETAIL)
        .queryParam(WsCommonConstants.RWS_QP_A2L_FILE_CHECKSUM, a2lFileCheckSum);
    GenericType<Set<VCDMA2LFileDetail>> type = new GenericType<Set<VCDMA2LFileDetail>>() {};
    return get(wsTarget, type);
  }

  /**
   * Get all PVER Variants, for the given input
   *
   * @param pver PVER name
   * @return sorted set of variants
   * @throws ApicWebServiceException error during service call
   */
  public SortedSet<String> getA2LFilePVERVars(final String pver) throws ApicWebServiceException {

    LOGGER.debug("Get all PVER variants for given pver - {}.. ", pver);

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PVER_VARS).queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_NAME, pver);

    GenericType<SortedSet<String>> type = new GenericType<SortedSet<String>>() {};
    SortedSet<String> retSet = get(wsTarget, type);

    LOGGER.debug("Number of items returned = {}", retSet.size());

    return retSet;
  }

}
