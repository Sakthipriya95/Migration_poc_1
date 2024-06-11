package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Pidc Sub Variant
 *
 * @author mkl2cob
 */
public class PidcSubVariantServiceClient extends AbstractRestServiceClient {


  private static final IMapperChangeData VAR_UPDATE_RESPONSE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcSubVariantData varChangeModel = (PidcSubVariantData) data;
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVarBeforeUpdate(),
        varChangeModel.getPidcVarAfterUpdate()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVersBeforeUpdate(),
        varChangeModel.getPidcVersAfterUpdate()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getSrcPidcSubVar(),
        varChangeModel.getDestPidcSubVar()));

    return changeDataList;
  };
  private static final IMapperChangeData VAR_CREATE_RESPONSE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcSubVariantData varChangeModel = (PidcSubVariantData) data;
    changeDataList.add(changeDataCreator.createDataForCreate(0L, varChangeModel.getDestPidcSubVar()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVarBeforeUpdate(),
        varChangeModel.getPidcVarAfterUpdate()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varChangeModel.getPidcVersBeforeUpdate(),
        varChangeModel.getPidcVersAfterUpdate()));
    
    varChangeModel.getCreatedPidcSubVarCocWpSet()
        .forEach(pidcSubVarCocWp -> changeDataList.add(changeDataCreator.createDataForCreate(0L, pidcSubVarCocWp)));
    // update the PidcVarCocWp
    varChangeModel.getPidcVarCocWpSet().forEach(
        pidcVarCocWp -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, pidcVarCocWp, pidcVarCocWp)));
    return changeDataList;
  };

  /**
   * Constructor
   */
  public PidcSubVariantServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDCSUBVARIANT);
  }


  /**
   * Get Pidc Sub Variant using its id
   *
   * @param objId object's id
   * @return PidcSubVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVariant get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, PidcSubVariant.class);
  }

  /**
   * Create a Pidc Sub Variant record
   *
   * @param obj object to create
   * @return created PidcSubVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVariantData create(final PidcSubVariantData obj) throws ApicWebServiceException {
    PidcSubVariantData svData = post(getWsBase(), obj, PidcSubVariantData.class);
    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(svData, VAR_CREATE_RESPONSE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));
    return svData;
  }

  /**
   * Update a Pidc Sub Variant record
   *
   * @param obj object to update
   * @return updated PidcSubVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVariantData update(final PidcSubVariantData obj) throws ApicWebServiceException {
    PidcSubVariantData svData = put(getWsBase(), obj, PidcSubVariantData.class);
    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(svData, VAR_UPDATE_RESPONSE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));
    return svData;
  }


  /**
   * @param pidcVersionId  Long
   * @param includeDeleted boolean
   * @return Map<Long, PidcSubVariant>
   * @throws ApicWebServiceException
   */
  public Map<Long, PidcSubVariant> getSubVariantsForVersion(final Long pidcVersionId, final boolean includeDeleted)
      throws ApicWebServiceException {
    LOGGER.debug("Get Pidc sub variants for pidc version");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_VAR_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_PIDC_VERSION_ID, pidcVersionId)
        .queryParam(WsCommonConstants.RWS_QP_INCLUDE_DELETED, includeDeleted);
    GenericType<Map<Long, PidcSubVariant>> type = new GenericType<Map<Long, PidcSubVariant>>() {};
    Map<Long, PidcSubVariant> retMap;

    retMap = get(wsTarget, type);

    LOGGER.debug("Pidc Sub Variant records loaded count = {}", retMap.size());
    return retMap;
  }

}
