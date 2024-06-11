package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaParameter;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.DaParameter;


/**
 * Loader class for DaParameter
 *
 * @author say8cob
 */
public class DaParameterLoader extends AbstractBusinessObject<DaParameter, TDaParameter> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public DaParameterLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.DA_PARAMETER, TDaParameter.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DaParameter createDataObject(final TDaParameter entity) throws DataException {
    DaParameter object = new DaParameter();

    setCommonFields(object, entity);

    object.setDaWpRespId(entity.getTDaWpResp().getDaWpRespId());
    object.setParameterId(entity.getParameterId());
    object.setParameterName(entity.getParameterName());
    object.setParameterType(entity.getParameterType());
    object.setFunctionName(entity.getFunctionName());
    object.setFunctionVersion(entity.getFunctionVersion());
    object.setRvwA2lVersion(entity.getRvwA2lVersion());
    object.setRvwFuncVersion(entity.getRvwFuncVersion());
    object.setQuestionnaireStatus(entity.getQuestionnaireStatus());
    object.setReviewedFlag(entity.getReviewedFlag());
    object.setEqualsFlag(entity.getEqualsFlag());
    object.setCompliResult(entity.getCompliResult());
    object.setReviewScore(entity.getReviewScore());
    object.setReviewRemark(entity.getReviewRemark());
    object.setResultId(entity.getResultId());
    object.setRvwParamId(entity.getRvwParamId());
    object.setRvwResultName(entity.getRvwResultName());
    object.setHexValue(entity.getHexValue());
    object.setReviewedValue(entity.getReviewedValue());
    object.setCompliFlag(entity.getCompliFlag());
    object.setQssdFlag(entity.getQssdFlag());
    object.setReadOnlyFlag(entity.getReadOnlyFlag());
    object.setDependentCharacteristicFlag(entity.getDependentCharacteristicFlag());
    object.setBlackListFlag(entity.getBlackListFlag());
    object.setNeverReviewedFlag(entity.getNeverReviewedFlag());
    object.setQssdResult(entity.getQssdResult());
    object.setCompliTooltip(entity.getCompliTooltip());
    object.setQssdTooltip(entity.getQssdTooltip());

    return object;
  }

}
