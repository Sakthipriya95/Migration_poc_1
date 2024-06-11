package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetParamType;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RulesetParamType;

/**
 * Loader class for RulesetParamType
 *
 * @author NDV4KOR
 */
public class RulesetParamTypeLoader extends AbstractBusinessObject<RulesetParamType, TRulesetParamType> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RulesetParamTypeLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RULESET_PARAM_TYPE, TRulesetParamType.class);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected RulesetParamType createDataObject(final TRulesetParamType entity) throws DataException {
    RulesetParamType rulesetParamType = new RulesetParamType();
    rulesetParamType.setId(entity.getParamTypeId());
    rulesetParamType.setParamType(entity.getParamType());
    setCommonFields(rulesetParamType, entity);


    return rulesetParamType;
  }


}
