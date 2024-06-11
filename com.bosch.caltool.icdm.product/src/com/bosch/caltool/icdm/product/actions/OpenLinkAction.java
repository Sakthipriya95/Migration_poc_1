/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * This class handles - Opening browser links from help menu <br>
 * iCDM-1306
 *
 * @author adn1cob
 */
public class OpenLinkAction extends Action implements IWorkbenchAction {


  /**
   * Constructor
   *
   * @param actionIdCPKey Common param key representing the action ID
   * @param actionTxt action text
   * @param imageDesc image
   */
  public OpenLinkAction(final CommonParamKey actionIdCPKey, final String actionTxt, final ImageDescriptor imageDesc) {
    super();
    super.setId(actionIdCPKey.getParamName());
    super.setText(actionTxt);
    super.setImageDescriptor(imageDesc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }

  @Override
  public void run() {
    // Get link configured in common params table
    String hyperLink;
    try {
      hyperLink = new CommonDataBO().getParameterValue(CommonParamKey.getType(getId()));
      CommonActionSet action = new CommonActionSet();
      action.openExternalBrowser(hyperLink);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    // Open link in browser

  }


}
