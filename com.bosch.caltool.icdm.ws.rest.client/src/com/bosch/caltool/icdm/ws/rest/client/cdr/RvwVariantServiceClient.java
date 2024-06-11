/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultInput;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultResponse;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class RvwVariantServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData RVW_VAR_QNAIRE_CREATION = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    changeDataList
        .add(new ChangeDataCreator<>().createDataForCreate(0L, ((AttachRvwResultResponse) data).getRvwVariant()));

    return changeDataList;
  };

  /**
   * Constructor
   */
  public RvwVariantServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDR_RVW_VARIANT);
  }

  /**
   * Get Rvw Variant by ID
   *
   * @param rvwVariantId Rvw Variant Id
   * @return RvwVariant
   * @throws ApicWebServiceException any error while calling service
   */
  public RvwVariant getById(final Long rvwVariantId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, rvwVariantId);
    return get(wsTarget, RvwVariant.class);
  }

  /**
   * @param pidcVerId
   * @return key - workpackage name and value - sortedset of reviewvariant
   * @throws ApicWebServiceException
   * @deprecated not used
   */
  @Deprecated
  public Map<String, SortedSet<ReviewVariantModel>> getReviewVariantListByPidcVersion(final Long pidcVerId)
      throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_RVW_VARIANT_SET).queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID,
        pidcVerId), new GenericType<Map<String, SortedSet<ReviewVariantModel>>>() {});
  }


  /**
   * @param resultId
   * @return key - reviewVariantId and value - reviewvariant object
   * @throws ApicWebServiceException
   */
  public Map<Long, ReviewVariantModel> getReviewVarMap(final Long resultId) throws ApicWebServiceException {
    return get(
        getWsBase().path(WsCommonConstants.RWS_RVW_VAR_MAP).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, resultId),
        new GenericType<Map<Long, ReviewVariantModel>>() {});
  }


  /**
   * @param cdrResultIds as input
   * @return ReviewVariantModel object
   * @throws ApicWebServiceException as exception
   */
  public Set<ReviewVariantModel> getReviewVariantModelSet(final Set<Long> cdrResultIds) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RVW_VAR_MODEL_SET);
    GenericType<Set<ReviewVariantModel>> type = new GenericType<Set<ReviewVariantModel>>() {};
    Set<ReviewVariantModel> reviewVariantModelSet = post(wsTarget, cdrResultIds, type);

    LOGGER.debug("Review Variant Model fetched. No. of records : {}", reviewVariantModelSet.size());
    return reviewVariantModelSet;
  }

  /**
   * @param pidcVariantId
   * @param pidcVerId
   * @return key - workpackage name and value - sortedset of reviewvariant
   * @throws ApicWebServiceException
   * @deprecated not used
   */
  @Deprecated
  public Map<String, SortedSet<ReviewVariantModel>> getReviewVariantMapWithWPName(final Long pidcVariantId,
      final Long pidcVerId)
      throws ApicWebServiceException {
    return get(
        getWsBase().path(WsCommonConstants.RWS_CDR_WRKPKG)
            .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, pidcVariantId)
            .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId),
        new GenericType<Map<String, SortedSet<ReviewVariantModel>>>() {});
  }

  /**
   * @param pidcVariantId
   * @param pidcVerId
   * @return set of reviewvariant object
   * @throws ApicWebServiceException
   * @deprecated not used
   */
  @Deprecated
  public Set<ReviewVariantModel> getReviewVariantListByPidcVariant(final Long pidcVariantId, final Long pidcVerId)
      throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_RVW_VARIANT)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, pidcVariantId)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVerId), new GenericType<Set<ReviewVariantModel>>() {});
  }

  /**
   * Create a RvwVariant record
   *
   * @param obj object to create
   * @return created CDRReviewResult object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwVariant create(final RvwVariant obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Delete a RvwVariant record
   *
   * @param obj object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final RvwVariant obj) throws ApicWebServiceException {
    delete(getWsBase(), obj);
  }

  /**
   * Get review variant object for the given CDR result and Variant ID
   *
   * @param resultId CDR result ID
   * @param variantId Variant ID
   * @return review variant
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwVariant getRvwVariantByResultNVarId(final long resultId, final long variantId)
      throws ApicWebServiceException {
    return get(getWsBase().path(WsCommonConstants.RWS_RVW_VARIANT_BY_RESULT_N_VAR)
        .queryParam(WsCommonConstants.RWS_QP_RESULT_ID, resultId)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantId), RvwVariant.class);
  }

  /**
   * @param inp
   * @return
   * @throws ApicWebServiceException
   */
  public AttachRvwResultResponse attachRvwResWithQnaire(final AttachRvwResultInput inp) throws ApicWebServiceException {
    AttachRvwResultResponse ret =
        post(getWsBase().path(WsCommonConstants.RWS_ATTACH_RVW_RES_WITH_QNAIRE), inp, AttachRvwResultResponse.class);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, RVW_VAR_QNAIRE_CREATION);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    return ret;
  }
}
