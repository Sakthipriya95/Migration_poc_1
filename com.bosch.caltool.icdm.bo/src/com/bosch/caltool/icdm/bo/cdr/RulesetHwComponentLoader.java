package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TRulesetHwComponent;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.RulesetHwComponent;


/**
 * Loader class for RulesetHwComponent
 *
 * @author NDV4KOR
 */
public class RulesetHwComponentLoader extends AbstractBusinessObject<RulesetHwComponent, TRulesetHwComponent> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RulesetHwComponentLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RULESET_HW_COMPONENT, TRulesetHwComponent.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RulesetHwComponent createDataObject(final TRulesetHwComponent entity) throws DataException {
    RulesetHwComponent rulesetHwComponent = new RulesetHwComponent();
    rulesetHwComponent.setId(entity.getHwComponentId());
    rulesetHwComponent.setHwComponent(entity.getHwComponent());
    setCommonFields(rulesetHwComponent, entity);

    return rulesetHwComponent;
  }

}
