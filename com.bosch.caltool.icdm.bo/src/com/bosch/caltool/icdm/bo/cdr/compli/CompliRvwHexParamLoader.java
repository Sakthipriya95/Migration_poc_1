package com.bosch.caltool.icdm.bo.cdr.compli;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TCompliRvwHexParam;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.compli.CompliRvwHexParam;


/**
 * Loader class for CompliReviewParamResultHex
 *
 * @author dmr1cob
 */
public class CompliRvwHexParamLoader extends AbstractBusinessObject<CompliRvwHexParam, TCompliRvwHexParam> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CompliRvwHexParamLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.COMPLI_RVW_HEX_PARAM, TCompliRvwHexParam.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompliRvwHexParam createDataObject(final TCompliRvwHexParam entity) throws DataException {
    CompliRvwHexParam object = new CompliRvwHexParam();

    setCommonFields(object, entity);

    object.setCheckValue(entity.getCheckValue());
    object.setCompliResult(entity.getCompliResult());
    object.setQssdResult(entity.getQssdResult());
    object.setLabObjId(entity.getLabObjId());
    object.setRevId(entity.getRevId());

    return object;
  }

  /**
   * Get all CompliReviewParamResultHex records in system
   *
   * @return Map. Key - id, Value - CompliRvwHexParam object
   * @throws DataException error while retrieving data
   */
  public Map<Long, CompliRvwHexParam> getAll() throws DataException {
    Map<Long, CompliRvwHexParam> objMap = new ConcurrentHashMap<>();
    TypedQuery<TCompliRvwHexParam> tQuery =
        getEntMgr().createNamedQuery(TCompliRvwHexParam.GET_ALL, TCompliRvwHexParam.class);
    List<TCompliRvwHexParam> dbObj = tQuery.getResultList();
    for (TCompliRvwHexParam entity : dbObj) {
      objMap.put(entity.getHexParamsId(), createDataObject(entity));
    }
    return objMap;
  }

}
