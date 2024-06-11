/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarChangeModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantsInputData;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectObjectWithAttributes;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author DMR1COB
 */
public class PidcVariantServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData VAR_UPDATE_RESPONSE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcVarChangeModel varChangeModel = (PidcVarChangeModel) data;
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVarBeforeUpdate(),
        varChangeModel.getPidcVarAfterUpdate()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVersBeforeUpdate(),
        varChangeModel.getPidcVersAfterUpdate()));

    return changeDataList;
  };
  private static final IMapperChangeData VAR_CREATE_RESPONSE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcVarChangeModel varChangeModel = (PidcVarChangeModel) data;
    changeDataList.add(changeDataCreator.createDataForCreate(0L, varChangeModel.getPidcVarAfterUpdate()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVersBeforeUpdate(),
        varChangeModel.getPidcVersAfterUpdate()));

    varChangeModel.getCreatedPidcVarCocWpSet()
        .forEach(pidcVarCocWp -> changeDataList.add(changeDataCreator.createDataForCreate(0L, pidcVarCocWp)));
    return changeDataList;
  };

  /**
   * Constructor
   */
  public PidcVariantServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCVARIANT);
  }


  /**
   * Get PIDC Variant using its id
   *
   * @param objId object's id
   * @return PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVariant get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcVariant.class);
  }

  /**
   * Create a PIDC Variant record
   *
   * @param obj object to create
   * @return created PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVariantData create(final PidcVariantData obj) throws ApicWebServiceException {
    PidcVarChangeModel varChangeModel = new PidcVarChangeModel();
    varChangeModel.setPidcVersBeforeUpdate(obj.getPidcVersion());
    PidcVariantData ret = post(getWsBase(), obj, PidcVariantData.class);

    varChangeModel.setPidcVarAfterUpdate(ret.getDestPidcVar());
    varChangeModel.setPidcVersAfterUpdate(ret.getPidcVersion());
    varChangeModel.setCreatedPidcVarCocWpSet(ret.getCreatedPidcVarCocWpSet());
    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(varChangeModel, VAR_CREATE_RESPONSE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));
    return ret;
  }

  /**
   * Update a PIDC Variant record
   *
   * @param pidcVarData object to update
   * @return updated PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVariantData update(final PidcVariantData pidcVarData) throws ApicWebServiceException {
    PidcVarChangeModel varChangeModel = new PidcVarChangeModel();
    varChangeModel.setPidcVarBeforeUpdate(pidcVarData.getSrcPidcVar());
    varChangeModel.setPidcVersBeforeUpdate(pidcVarData.getPidcVersion());

    PidcVariantData ret = put(getWsBase(), pidcVarData, PidcVariantData.class);
    varChangeModel.setPidcVarAfterUpdate(ret.getDestPidcVar());
    varChangeModel.setPidcVersAfterUpdate(ret.getPidcVersion());
    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(varChangeModel, VAR_UPDATE_RESPONSE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Project variant updated !");

    return ret;
  }

  /**
   * Get PidcVersion records using pidcVersionId and a2lFileId
   *
   * @param pidcVersionId pidcVersionId
   * @param a2lFileId     a2lFileId
   * @return Map. Key - id, Value - PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public SortedSet<PidcVariant> getPidcVarForPidcVersAndA2l(final Long pidcVersionId, final Long a2lFileId)
      throws ApicWebServiceException {
    LOGGER.debug("Get Pidc Variant records using pidcVersionId and a2lFileId");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_VARIANT_FOR_PIDC_VERS_N_A2L_ID)
        .queryParam(WsCommonConstants.RWS_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_A2L_FILE_ID, a2lFileId);
    GenericType<SortedSet<PidcVariant>> type = new GenericType<SortedSet<PidcVariant>>() {};
    SortedSet<PidcVariant> retMap = get(wsTarget, type);
    LOGGER.debug("Pidc Variant records loaded count = {}", retMap.size());
    return retMap;
  }


  /**
   * To check whether the pidc version has variants
   *
   * @param pidcVersionId
   * @param includeDeleted
   * @return
   * @throws ApicWebServiceException
   */
  public boolean hasVariant(final Long pidcVersionId, final boolean includeDeleted) throws ApicWebServiceException {

    LOGGER.debug("Get Pidc has Variant records using pidcVersionId and includeDeleted");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_HAS_PIDC_VARIANT)
        .queryParam(WsCommonConstants.RWS_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted);
    return get(wsTarget, boolean.class);

  }

  /**
   * @param pidcVersionId  Long
   * @param includeDeleted boolean
   * @return SortedSet<PidcVariant>
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcVariant> getVariantsForVersion(final Long pidcVersionId, final boolean includeDeleted)
      throws ApicWebServiceException {
    LOGGER.debug("Get Pidc variants for pidc version");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_VAR_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted);
    GenericType<Map<Long, PidcVariant>> type = new GenericType<Map<Long, PidcVariant>>() {};
    Map<Long, PidcVariant> retMap = get(wsTarget, type);
    LOGGER.debug("Pidc Variant records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get all variants of a pidc version, mapped to an a2l file
   *
   * @param pidcA2lId PIDC A2L mapping ID
   * @return map of pidc variants. key - variant ID, value - variant
   * @throws ApicWebServiceException error in service call
   */
  public Map<Long, PidcVariant> getA2lMappedVariants(final Long pidcA2lId) throws ApicWebServiceException {

    LOGGER.debug("Getting pidc variants mapped to pidc a2l {}", pidcA2lId);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_A2L_MAPPED_VAR_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);

    GenericType<Map<Long, PidcVariant>> type = new GenericType<Map<Long, PidcVariant>>() {};
    Map<Long, PidcVariant> retMap = get(wsTarget, type);

    LOGGER.debug("Pidc Variant records loaded. Count = {}", retMap.size());

    return retMap;
  }

  /**
   * Get all variants of a pidc version, mapped to an sdom pver
   *
   * @param pidcA2lId PIDC A2L mapping ID
   * @return map of pidc variants. key - variant ID, value - variant
   * @throws ApicWebServiceException error in service call
   */
  public Map<Long, PidcVariant> getSdomPverMappedVariants(final Long pidcVersId, final String sdomPverName)
      throws ApicWebServiceException {

    LOGGER.debug("Getting pidc variants mapped to sdom pver {}", sdomPverName);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SDOM_MAPPED_VAR_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId)
        .queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_NAME, sdomPverName);

    GenericType<Map<Long, PidcVariant>> type = new GenericType<Map<Long, PidcVariant>>() {};
    Map<Long, PidcVariant> retMap = get(wsTarget, type);

    LOGGER.debug("Pidc Variant records loaded. Count = {}", retMap.size());

    return retMap;
  }

  /**
   * @param pidVar PidcVariant
   * @return
   * @throws ApicWebServiceException
   */
  public Boolean hasReviews(final PidcVariant pidVar) throws ApicWebServiceException {
    LOGGER.debug("Getting pidc variant has reviews or not {}", pidVar.getId());

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_VAR_HAS_REVIEWS)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidVar.getId());


    Boolean hasReviews = get(wsTarget, Boolean.class);

    LOGGER.debug("Pidc Variant has reviews {}", hasReviews);

    return hasReviews;

  }


  /**
   * Get all pidc variants of pidc version, including the variant attributes, for given attribute list
   *
   * @param pidcVersId PIDC version ID
   * @param attrIdSet  set of attrribute IDs
   * @return Map - key variant Id, value variant with variant attributes
   * @throws ApicWebServiceException error in service call
   */
  public Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> getVariantsWithAttrs(
      final Long pidcVersId, final Set<Long> attrIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Getting pidc variants for pidc version {} and specifc attribute definitions - {}", pidcVersId,
        attrIdSet);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VARIANTS_WITH_ATTRS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId);

    for (Long attrId : attrIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_ATTRIBUTE_ID, attrId);
    }

    GenericType<Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>>> type =
        new GenericType<Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>>>() {};
    Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> retMap = get(wsTarget, type);

    LOGGER.debug("Pidc Variants records loaded. Count = {}", retMap.size());

    return retMap;
  }

  /**
   * @param objId
   * @return
   * @throws ApicWebServiceException
   */
  public PidcVariant getById(final long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcVariant.class);
  }


  /**
   * @return {@link PidcVariantsInputData}
   * @throws ApicWebServiceException Exception
   */
  public PidcVariantsInputData getPidcVariantsInputData(final List<Long> elementIdList) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_PIDC_VARIANTS_INPUT_DATA);
    GenericType<PidcVariantsInputData> type = new GenericType<PidcVariantsInputData>() {};
    return post(wsTarget, elementIdList, type);
  }

}
