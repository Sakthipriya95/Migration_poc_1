package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireRespVariant;
import com.bosch.caltool.icdm.database.entity.apic.TRvwQnaireResponse;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.DefineQnaireRespInputData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;


/**
 * Loader class for RvwQnaireRespVariant
 *
 * @author say8cob
 */
public class RvwQnaireRespVariantLoader extends AbstractBusinessObject<RvwQnaireRespVariant, TRvwQnaireRespVariant> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwQnaireRespVariantLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT, TRvwQnaireRespVariant.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwQnaireRespVariant createDataObject(final TRvwQnaireRespVariant entity) throws DataException {
    RvwQnaireRespVariant object = new RvwQnaireRespVariant();

    setCommonFields(object, entity);

    object.setPidcVersId(entity.getTPidcVersion().getPidcVersId());
    if (null != entity.getTabvProjectVariant()) {
      object.setVariantId(entity.getTabvProjectVariant().getVariantId());
    }
    // tRvwQnaireResp is Null for Simplified Gnrl Qnaire resp - Empty WP/Resp Structure
    TRvwQnaireResponse tRvwQnaireResponse = entity.getTRvwQnaireResponse();
    if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
      object.setQnaireRespId(tRvwQnaireResponse.getQnaireRespId());
    }
    object.setA2lRespId(entity.gettA2lResponsibility().getA2lRespId());
    object.setA2lWpId(entity.gettA2lWorkPackage().getA2lWpId());
    return object;
  }

  /**
   * @param pidcVerId
   * @return
   */
  public Set<TRvwQnaireRespVariant> getQnaireRespVariantList(final Long pidcVerId) {
    TPidcVersion tPidcVersion = new PidcVersionLoader(getServiceData()).getEntityObject(pidcVerId);

    return tPidcVersion.getTRvwQnaireRespVariants();
  }

  /**
   * This service returns List of review questionnaire response variants from the given review questionnaire response id
   *
   * @param rvwQnaireRespId {@link RvwQnaireResponse} Id
   * @return {@link RvwQnaireRespVariant} id
   * @throws DataException exception in service
   */
  public List<RvwQnaireRespVariant> getRvwQnaireRespVariant(final Long rvwQnaireRespId) throws DataException {
    RvwQnaireResponseLoader qnaireRespLoader = new RvwQnaireResponseLoader(getServiceData());
    // Validate Rvw Qnaire Resp Id
    qnaireRespLoader.validateId(rvwQnaireRespId);
    List<RvwQnaireRespVariant> rvwQnaireRespVariantList = new ArrayList<>();
    Set<TRvwQnaireRespVariant> tRvwQnaireRespVariantsList =
        qnaireRespLoader.getEntityObject(rvwQnaireRespId).getTRvwQnaireRespVariants();
    for (TRvwQnaireRespVariant tRvwQnaireRespVariant2 : tRvwQnaireRespVariantsList) {
      rvwQnaireRespVariantList.add(createDataObject(tRvwQnaireRespVariant2));
    }
    return rvwQnaireRespVariantList;
  }


  /**
   * This service returns qnaire response variant map
   *
   * @param pidcVersId pidc vers id
   * @return Map key - qnaire resp id value - qnaire resp variant set
   * @throws DataException Exception in service
   */
  public Map<Long, List<RvwQnaireRespVariant>> getRvwQnaireRespVariantMap(final Long pidcVersId) throws DataException {
    Map<Long, List<RvwQnaireRespVariant>> qnaireRespVarMap = new HashMap<>();
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    Set<TRvwQnaireRespVariant> tRvwQnaireRespVariantSet =
        pidcVersionLoader.getEntityObject(pidcVersId).getTRvwQnaireRespVariants();

    for (TRvwQnaireRespVariant tRvwQnaireRespVariant : tRvwQnaireRespVariantSet) {
      // RvwQnaireResp will be null for Simplified Qnaire
      TRvwQnaireResponse tRvwQnaireResponse = tRvwQnaireRespVariant.getTRvwQnaireResponse();
      if (CommonUtils.isNotNull(tRvwQnaireResponse)) {
        long qnaireRespId = tRvwQnaireResponse.getQnaireRespId();
        if (qnaireRespVarMap.containsKey(qnaireRespId)) {
          qnaireRespVarMap.get(qnaireRespId).add(createDataObject(tRvwQnaireRespVariant));
        }
        else {
          List<RvwQnaireRespVariant> rvwQnaireRespVariantList = new ArrayList<>();
          rvwQnaireRespVariantList.add(createDataObject(tRvwQnaireRespVariant));
          qnaireRespVarMap.put(qnaireRespId, rvwQnaireRespVariantList);
        }
      }
    }
    return qnaireRespVarMap;
  }

  /**
   * @param pidcVersId selected pidc version
   * @return {@link DefineQnaireRespInputData} object
   * @throws DataException exception in ws service
   */
  public DefineQnaireRespInputData getDefineQnaireRespInputData(final Long pidcVersId) throws DataException {
    DefineQnaireRespInputData defineQnaireRespInputData = new DefineQnaireRespInputData();

    // Get all undeleted variants using PIDC version id
    Map<Long, PidcVariant> variantsMap = new PidcVariantLoader(getServiceData()).getVariants(pidcVersId, false);

    getLogger().info("Get variants for version finished. Variants = {}", variantsMap.size());

    Map<Long, List<RvwQnaireRespVariant>> qnaireRespVarMap = getRvwQnaireRespVariantMap(pidcVersId);

    getLogger().info("Get qnaire resp variants finished. Qnaire Resp = {}", qnaireRespVarMap.size());

    PidcQnaireInfo pidcQnaireResponse = new RvwQnaireResponseLoader(getServiceData()).getPidcQnaireResponse(pidcVersId);

    getLogger().info("Get pidc qnaire info for version finished. Qnaire Resp = {}");

    defineQnaireRespInputData.setAllVariantMap(variantsMap);
    defineQnaireRespInputData.setQnaireRespVarMap(qnaireRespVarMap);
    defineQnaireRespInputData.setQnaireInfo(pidcQnaireResponse);

    return defineQnaireRespInputData;
  }

  /**
   * @param a2lRespIds a2lRespIds
   * @return list of TRvwQnaireRespVariant entities
   */
  public List<TRvwQnaireRespVariant> getQnaireRespVariantListForA2lRespIds(final List<Long> a2lRespIds) {

    TypedQuery<TRvwQnaireRespVariant> tQuery =
        getEntMgr().createNamedQuery(TRvwQnaireRespVariant.GET_BY_A2L_RESP_IDS, TRvwQnaireRespVariant.class);
    tQuery.setParameter("a2lRespIds", a2lRespIds);

    return tQuery.getResultList();
  }
}
