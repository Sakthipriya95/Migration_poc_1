package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetSysElement;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RulesetSysElement;


/**
 * Loader class for RulesetSysElement
 *
 * @author NDV4KOR
 */
public class RulesetSysElementLoader extends AbstractBusinessObject<RulesetSysElement, TRulesetSysElement> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RulesetSysElementLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RULESET_SYS_ELEMENT, TRulesetSysElement.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RulesetSysElement createDataObject(final TRulesetSysElement entity) throws DataException {
    RulesetSysElement rulesetSysElement = new RulesetSysElement();
    rulesetSysElement.setId(entity.getSysElementId());
    rulesetSysElement.setSysElement(entity.getSysElement());
    setCommonFields(rulesetSysElement, entity);

    return rulesetSysElement;
  }


}
