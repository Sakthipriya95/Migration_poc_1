package com.bosch.caltool.icdm.bo.a2l;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;

/**
 * Loader class for A2LWPResponsibilityStatus
 *
 * @author UKT1COB
 */
public class A2lWpResponsibilityStatusLoader
    extends AbstractBusinessObject<A2lWpResponsibilityStatus, TA2lWpResponsibilityStatus> {


  /**
   *
   */
  private static final int PARAM_VAR_ID = 2;
  /**
   *
   */
  private static final int PARAM_WP_RESP_ID = 1;

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWpResponsibilityStatusLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WP_RESPONSIBILITY_STATUS, TA2lWpResponsibilityStatus.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lWpResponsibilityStatus createDataObject(final TA2lWpResponsibilityStatus entity) throws DataException {

    A2lWpResponsibilityStatus a2lWPRespStatus = new A2lWpResponsibilityStatus();

    setCommonFields(a2lWPRespStatus, entity);

    a2lWPRespStatus
        .setVariantId(CommonUtils.isNotNull(entity.getTabvProjVar()) ? entity.getTabvProjVar().getVariantId() : null);
    TA2lWpResponsibility ta2lWpResp = entity.getTA2lWpResp();
    a2lWPRespStatus.setWpRespId(ta2lWpResp.getWpRespId());
    a2lWPRespStatus.setWpRespFinStatus(entity.getWpRespFinStatus());
    a2lWPRespStatus.setMappedWpRespName(getRespName(a2lWPRespStatus.getWpRespId()));

    TA2lWorkPackage a2lWp = ta2lWpResp.getA2lWp();
    a2lWPRespStatus.setName(a2lWp.getWpName());
    a2lWPRespStatus.setDescription(a2lWp.getWpDesc());
    a2lWPRespStatus.setA2lWpId(a2lWp.getA2lWpId());

    TA2lResponsibility ta2lResp = entity.gettA2lResp();
    a2lWPRespStatus.setA2lRespId(
        CommonUtils.isNull(ta2lResp) ? ta2lWpResp.getA2lResponsibility().getA2lRespId() : ta2lResp.getA2lRespId());
    a2lWPRespStatus.setInheritedFlag(CommonUtils.isNull(ta2lResp));
    return a2lWPRespStatus;
  }


  /**
   * @param listOfNewlyCreatedA2lWPRespStatus list of newly created a2lWPResp Status
   * @param a2lWpRespStatusBeforeUpdateMap a2lWpRespStatusBeforeUpdateMap
   * @param a2lWpRespStatusAfterUpdateMap a2lWpRespStatusAfterUpdateMap
   * @return A2lWpRespStatusUpdationModel
   * @throws DataException exception
   */
  public A2lWpRespStatusUpdationModel getOutputUpdationModel(
      final List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWPRespStatus,
      final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpdateMap,
      final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpdateMap)
      throws DataException {


    A2lWpRespStatusUpdationModel a2lWpRespStatusUpdModelAfterUpd = new A2lWpRespStatusUpdationModel();

    List<A2lWpResponsibilityStatus> newlyCreatedA2lWpRespStatusList = new ArrayList<>();
    for (A2lWpResponsibilityStatus newA2lWPRespStatus : listOfNewlyCreatedA2lWPRespStatus) {
      newlyCreatedA2lWpRespStatusList.add(getDataObjectByID(newA2lWPRespStatus.getId()));
    }
    a2lWpRespStatusUpdModelAfterUpd.setListOfNewlyCreatedA2lWpRespStatus(newlyCreatedA2lWpRespStatusList);

    a2lWpRespStatusUpdModelAfterUpd.setA2lWpRespStatusToBeUpdatedMap(a2lWpRespStatusBeforeUpdateMap);

    Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpdMap = new HashMap<>();
    for (A2lWpResponsibilityStatus updatedA2lWPRespStatus : a2lWpRespStatusAfterUpdateMap.values()) {
      Long updA2lWpRespStatusId = updatedA2lWPRespStatus.getId();
      a2lWpRespStatusAfterUpdMap.put(updA2lWpRespStatusId, getDataObjectByID(updA2lWpRespStatusId));
    }
    a2lWpRespStatusUpdModelAfterUpd.setA2lWpRespStatusMapAfterUpdate(a2lWpRespStatusAfterUpdMap);

    return a2lWpRespStatusUpdModelAfterUpd;
  }


  /**
   * @param wpRespId
   * @return
   * @throws DataException
   */
  private String getRespName(final Long wpRespId) throws DataException {
    return (new A2lResponsibilityLoader(getServiceData())
        .getDataObjectByID(new A2lWpResponsibilityLoader(getServiceData()).getDataObjectByID(wpRespId).getA2lRespId()))
            .getName();
  }


  /**
   * @param variantId variant Id
   * @param wpRespId WP resp Id
   * @param a2lRespId
   * @return List Of TA2lWpResponsibilityStatus From table
   */
  public A2lWpResponsibilityStatus getA2lWpStatusByVarAndWpRespId(final Long variantId, final Long wpRespId,
      final Long a2lRespId)
      throws DataException {
    List<BigDecimal> existingA2lWPRespStatusList = getTA2lWpStatusByVarAndWpRespId(variantId, wpRespId, a2lRespId);

    return CommonUtils.isNullOrEmpty(existingA2lWPRespStatusList) ? null
        : getDataObjectByID((existingA2lWPRespStatusList.get(0)).longValue());
  }

  /**
   * @param variantId variant Id
   * @param wpRespId WP resp Id
   * @return List Of TA2lWpResponsibilityStatus From table
   */
  public List<BigDecimal> getTA2lWpStatusByVarAndWpRespId(final Long variantId, final Long wpRespId,
      final Long a2lRespId) {
    TypedQuery<BigDecimal> typedQuery;
    if (CommonUtils.isNotNull(variantId) && CommonUtils.isNotNull(a2lRespId)) {
      typedQuery = getEntMgr().createNamedQuery(
          TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS_WITH_VARID_RESPID_WPRESPID, BigDecimal.class);
      typedQuery.setParameter(PARAM_WP_RESP_ID, wpRespId).setParameter(PARAM_VAR_ID, variantId).setParameter(3,
          a2lRespId);
    }
    else if (CommonUtils.isNotNull(a2lRespId)) {
      typedQuery = getEntMgr().createNamedQuery(
          TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS_WITH_WPRESPID_FOR_NO_VARIANT, BigDecimal.class);
      typedQuery.setParameter(PARAM_WP_RESP_ID, wpRespId).setParameter(2, a2lRespId);
    }
    else if (CommonUtils.isNotNull(variantId)) {
      typedQuery = getEntMgr().createNamedQuery(TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS_WITH_VARID_WPRESPID,
          BigDecimal.class);
      typedQuery.setParameter(PARAM_WP_RESP_ID, wpRespId).setParameter(PARAM_VAR_ID, variantId);
    }
    else {
      typedQuery = getEntMgr().createNamedQuery(TA2lWpResponsibilityStatus.GET_A2LWPRESPSTATUS, BigDecimal.class);
      typedQuery.setParameter(PARAM_WP_RESP_ID, wpRespId);
    }

    return typedQuery.getResultList();
  }

  /**
   * @param variantId variant Id
   * @param wpDefnVersId WP Definition version Id
   * @return Map of Key - Wp Id, Value - Map Of Resp Id and A2lWpResponsibilityStatus
   * @throws DataException exception
   */
  public Map<Long, Map<Long, A2lWpResponsibilityStatus>> getA2lWpStatusByVarAndWpDefnVersId(final Long variantId,
      final Long wpDefnVersId)
      throws DataException {
   
    Map<Long, Map<Long, A2lWpResponsibilityStatus>> wpIdRespIdAndStatusMap = new HashMap<>();
    A2lWpResponsibilityStatusLoader statusLoader = new A2lWpResponsibilityStatusLoader(getServiceData());
    TA2lWpDefnVersion ta2lWpDefnVersion = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(wpDefnVersId);

    for (TA2lWpResponsibility tWpResp : ta2lWpDefnVersion.getTA2lWpResponsibility()) {

      for (TA2lWpResponsibilityStatus tWpRespStatus : tWpResp.gettA2lWPRespStatus()) {
        A2lWpResponsibilityStatus wpRespStatus = statusLoader.createDataObject(tWpRespStatus);
        if ((variantId == null) && (tWpRespStatus.getTabvProjVar() == null)) {
          wpIdRespIdAndStatusMap.computeIfAbsent(wpRespStatus.getA2lWpId(), value -> new HashMap<>())
              .put(wpRespStatus.getA2lRespId(), wpRespStatus);

        }
        if ((tWpRespStatus.getTabvProjVar() != null) && (variantId != null) &&
            CommonUtils.isEqual(tWpRespStatus.getTabvProjVar().getVariantId(), variantId)) {
          wpIdRespIdAndStatusMap.computeIfAbsent(wpRespStatus.getA2lWpId(), value -> new HashMap<>())
              .put(wpRespStatus.getA2lRespId(), wpRespStatus);
        }
      }
    }

    return wpIdRespIdAndStatusMap;
  }

  /**
   * @param wpRespId as A2lWpRespId
   * @return Set<A2lWpResponsibilityStatus>
   * @throws DataException as Exception
   */
  public Set<A2lWpResponsibilityStatus> getA2lWpRespStatusBasedOnWPRespId(final Long wpRespId) throws DataException {
    TA2lWpResponsibility ta2lWpResponsibility =
        new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(wpRespId);
    Set<A2lWpResponsibilityStatus> a2lWpRespStatusSet = new HashSet<>();
    for (TA2lWpResponsibilityStatus ta2lWpResponsibilityStatus : ta2lWpResponsibility.gettA2lWPRespStatus()) {
      a2lWpRespStatusSet.add(createDataObject(ta2lWpResponsibilityStatus));
    }
    return a2lWpRespStatusSet;
  }

  /**
   * @param wpRespId as A2lWpRespId
   * @return boolean true if status is available
   */
  public boolean isA2lWPRespStatusAvailable(final Long wpRespId) {
    TA2lWpResponsibility ta2lWpResponsibility =
        new A2lWpResponsibilityLoader(getServiceData()).getEntityObject(wpRespId);
    return !ta2lWpResponsibility.gettA2lWPRespStatus().isEmpty();
  }


}
