/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo;

import com.bosch.calcomp.tulservice.internal.model.ToolCategory;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.bo.tul.AbstractIcdmTULInvoker;
import com.bosch.caltool.icdm.common.bo.tul.TULConstants.ICDM_TUL_COMPONENT;
import com.bosch.caltool.icdm.common.bo.tul.TULConstants.TUL_FEATURE;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * Class that needs to be invoked from service layer of iCDM for TUL Logging
 *
 * @author TRL1COB
 */
public class IcdmServerTULInvoker extends AbstractIcdmTULInvoker {


  /**
   * @param serviceData Service Data
   * @param toolCategory Category of the tool that invoked TUL
   * @param toolFeature Feature of the tool that invoked TUL
   */
  public IcdmServerTULInvoker(final ServiceData serviceData, final ToolCategory toolCategory,
      final TUL_FEATURE toolFeature) {
    super(toolCategory, toolFeature);
    this.userName = serviceData.getUsername();
    this.toolVersion = new CommonParamLoader(serviceData).getValue(CommonParamKey.ICDM_CLIENT_VERSION);
    this.toolComponent = ICDM_TUL_COMPONENT.ICDM_WEBSERVICE.getName();
  }


}
