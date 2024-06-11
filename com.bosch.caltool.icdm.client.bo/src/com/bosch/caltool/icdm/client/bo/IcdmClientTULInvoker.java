/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo;

import com.bosch.calcomp.tulservice.internal.model.ToolCategory;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.bo.tul.AbstractIcdmTULInvoker;
import com.bosch.caltool.icdm.common.bo.tul.TULConstants.ICDM_TUL_COMPONENT;
import com.bosch.caltool.icdm.common.bo.tul.TULConstants.TUL_FEATURE;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Class that needs to be invoked from client layer of iCDM for TUL Logging
 *
 * @author TRL1COB
 */
public class IcdmClientTULInvoker extends AbstractIcdmTULInvoker {


  /**
   * @param toolCategory Category of the tool that invoked TUL
   * @param toolFeature Features of the tool that invoked TUL
   * @throws ApicWebServiceException Exception
   */
  public IcdmClientTULInvoker(final ToolCategory toolCategory, final TUL_FEATURE toolFeature)
      throws ApicWebServiceException {
    super(toolCategory, toolFeature);
    this.userName = new CurrentUserBO().getUser().getName();
    this.toolVersion = new CommonDataBO().getIcdmVersion();
    this.toolComponent = ICDM_TUL_COMPONENT.ICDM_CLIENT.getName();
  }

}
