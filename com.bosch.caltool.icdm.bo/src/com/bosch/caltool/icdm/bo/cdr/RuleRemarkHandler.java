/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author dja7cob
 */
public class RuleRemarkHandler extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData service data
   */
  public RuleRemarkHandler(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * Method to create
   *
   * @param unicodeRmkMap rules to be created
   * @throws IcdmException exception from cmd
   */
  public void createUpdDelRmrks(final Map<CDRRule, ReviewRule> unicodeRmkMap) throws IcdmException {
    if (CommonUtils.isNullOrEmpty(unicodeRmkMap)) {
      getLogger().info("No unicode rule remarks to create/update");
    }
    else {
      RuleRemarkMasterCommand ruleRmkMasterCmd = new RuleRemarkMasterCommand(getServiceData(), unicodeRmkMap);
      getServiceData().getCommandExecutor().execute(ruleRmkMasterCmd);

      getLogger().info("Rule create/Update completed with remarks.");
    }
  }

}
