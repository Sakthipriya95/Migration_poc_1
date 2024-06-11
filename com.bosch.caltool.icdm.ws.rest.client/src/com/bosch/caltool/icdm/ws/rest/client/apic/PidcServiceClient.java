/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.ExternalPidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationRespData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTypeV2;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributes;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithAttributesV2;
import com.bosch.caltool.icdm.model.uc.ProjectUsecaseModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PidcServiceClient extends AbstractRestServiceClient {

  private static final IMapper PIDC_CREATION_MAPPER =
      obj -> Arrays.asList(((PidcCreationRespData) obj).getPidc(), ((PidcCreationRespData) obj).getNodeAccess());

  /**
   * Constructor
   */
  public PidcServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC);
  }

  /**
   * @param pidcID pidcID
   * @param ucIdSet use case ID set
   * @return ProjectIdCardWithAttributes
   * @throws ApicWebServiceException error during service call
   */
  public PidcVersionWithAttributes getPidcWithAttributes(final Long pidcID, final Set<Long> ucIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Loading PidcWithAttributes ");

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PIDC_WITH_ATTRS).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcID);
    for (Long ucid : ucIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_UC_ID, ucid);
    }

    GenericType<PidcVersionWithAttributes> type = new GenericType<PidcVersionWithAttributes>() {};
    PidcVersionWithAttributes ret = get(wsTarget, type);

    LOGGER.debug("PidcWithAttributes loaded. PIDC Name : {}, Version ID : {}",
        ret.getPidcVersionInfo().getPidc().getName(), ret.getPidcVersionInfo().getPidcVersion().getId());

    return ret;
  }


  /**
   * @param pidcID pidcID
   * @param ucIdSet use case ID set
   * @return ProjectIdCardWithAttributes
   * @throws ApicWebServiceException error during service call
   */
  public ExternalPidcVersionWithAttributes getExternalPidcWithAttributes(final Long pidcID, final Set<Long> ucIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Loading PidcWithAttributes ");

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_EXT_PIDC_WITH_ATTRS).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcID);
    for (Long ucid : ucIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_UC_ID, ucid);
    }

    GenericType<ExternalPidcVersionWithAttributes> type = new GenericType<ExternalPidcVersionWithAttributes>() {};
    ExternalPidcVersionWithAttributes ret = get(wsTarget, type);

    LOGGER.debug("PidcWithAttributes loaded. PIDC Name : {}, Version ID : {}",
        ret.getPidcVersionInfo().getPidc().getName(), ret.getPidcVersionInfo().getPidcVersion().getId());

    return ret;
  }

  /**
   * Get a Pidc by its ID
   *
   * @param objId Pidc ID
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public Pidc getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);

    return get(wsTarget, Pidc.class);
  }


  /**
   * @param attrValId as input
   * @return as map
   * @throws ApicWebServiceException as exception
   */
  public Map<String, Map<String, Map<String, Long>>> getPidcUsersUsingAttrValue(final Long attrValId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDCUSERS_BY_ATTR_VAL_ID)
        .queryParam(WsCommonConstants.RWS_QP_ATTR_VALUE_ID, attrValId);
    GenericType<Map<String, Map<String, Map<String, Long>>>> type =
        new GenericType<Map<String, Map<String, Map<String, Long>>>>() {};
    return get(wsTarget, type);
  }

  /**
   * Get a Pidc by its name value ID
   *
   * @param nameValId name value id of pidc
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public Pidc getByNameValueId(final Long nameValId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_BY_NAMEVAL_ID)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, nameValId);

    return get(wsTarget, Pidc.class);
  }

  /**
   * @param pidcBO PidcCreationData
   * @return Pidc
   * @throws ApicWebServiceException any exception
   */
  public PidcCreationRespData createPidc(final PidcCreationData pidcBO) throws ApicWebServiceException {
    return create(getWsBase(), pidcBO, PidcCreationRespData.class, PidcServiceClient.PIDC_CREATION_MAPPER);
  }

  /**
   * Update a Pidc record
   *
   * @param obj object to update
   * @return updated Pidc object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Pidc update(final Pidc obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }


  /**
   * @param pidcID Project Id card Id
   * @param pidcVersId pidc Version Id
   * @param ucIdSet Usecase Id
   * @return {@link PidcVersionWithAttributesV2} object
   * @throws ApicWebServiceException Exception
   */
  public PidcVersionWithAttributesV2 getPidcWithAttributesV2(final Long pidcID, final Long pidcVersId,
      final Set<Long> ucIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Loading PidcWithAttributesV2 ");

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PIDC_WITH_ATTRS_V2).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcID)
            .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId);
    for (Long ucid : ucIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_UC_ID, ucid);
    }

    GenericType<PidcVersionWithAttributesV2> type = new GenericType<PidcVersionWithAttributesV2>() {};
    PidcVersionWithAttributesV2 ret = get(wsTarget, type);

    LOGGER.debug("PidcWithAttributesV2 loaded. PIDC Name : {}, Version ID : {}",
        ret.getPidcVersionInfo().getPidc().getName(), ret.getPidcVersionInfo().getPidcVersion().getId());

    return ret;
  }


  /**
   * @param pidcID Project Id card Id
   * @param pidcVersId pidc Version Id
   * @param ucIdSet Usecase Id
   * @return {@link PidcVersionWithAttributesV2} object
   * @throws ApicWebServiceException Exception
   */
  public PidcTypeV2 getPidcTypeV2(final Long pidcID, final Long pidcVersId, final Set<Long> ucIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Loading PidcWithAttributesV2 ");

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PIDC_TYPE_V2).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcID)
            .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId);
    for (Long ucid : ucIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_UC_ID, ucid);
    }

    GenericType<PidcTypeV2> type = new GenericType<PidcTypeV2>() {};
    return get(wsTarget, type);

  }

  /**
   * Get set of attrIds mapped to project usecase favorites
   *
   * @param pidcId id of pidc
   * @return version
   * @throws ApicWebServiceException any exception
   */
  public ProjectUsecaseModel getProjectUsecaseModel(final Long pidcId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_PIDC_UC_ATTR).queryParam(WsCommonConstants.RWS_QP_PIDC_ID, pidcId);
    GenericType<ProjectUsecaseModel> type = new GenericType<ProjectUsecaseModel>() {};
    return get(wsTarget, type);
  }

}
