package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetParamResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RulesetParamResp;

/**
 * Loader class for RulesetParamRep
 *
 * @author NDV4KOR
 */
public class RulesetParamRespLoader extends AbstractBusinessObject<RulesetParamResp, TRulesetParamResp> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RulesetParamRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RULESET_PARAM_RESP, TRulesetParamResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RulesetParamResp createDataObject(final TRulesetParamResp entity) throws DataException {
    RulesetParamResp rulesetParamResp = new RulesetParamResp();
    rulesetParamResp.setId(entity.getParamRespId());
    rulesetParamResp.setParamResp(entity.getParamResp());
    setCommonFields(rulesetParamResp, entity);

    return rulesetParamResp;
  }


}
