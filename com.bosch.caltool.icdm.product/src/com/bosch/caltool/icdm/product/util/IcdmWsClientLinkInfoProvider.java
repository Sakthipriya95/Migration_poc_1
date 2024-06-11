/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.util;

import java.util.Map;

import com.bosch.calcomp.externallink.ILinkInfoProvider;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.LinkInfo;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.ExternalLinkInfo;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.ExternalLinkServiceClient;


/**
 * @author bne4cob
 */
public class IcdmWsClientLinkInfoProvider implements ILinkInfoProvider {


  /**
   * {@inheritDoc}
   */
  @Override
  public LinkInfo getLinkInfo(final Object obj) {
    if (!(obj instanceof IModel)) {
      CDMLogger.getInstance().error("External link is not supported for this object");
      return null;
    }
    try {
      ExternalLinkInfo info = new ExternalLinkServiceClient().getLinkInfo((IModel) obj);

      LinkInfo ret = new LinkInfo();
      ret.setDisplayText(info.getDisplayText());
      ret.setUrl(info.getUrl());
      return ret;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType(final Object obj) {
    return EXTERNAL_LINK_TYPE.getLinkType((IModel) obj);
  }


  /**
   * @param obj IModel object
   * @param additionalDetails extra details to create the link
   */
  @Override
  public LinkInfo getLinkInfo(final Object obj, final Map<String, String> additionalDetails) {
    if (!(obj instanceof IModel)) {
      CDMLogger.getInstance().error("External link is not supported for this object");
      return null;
    }
    try {
      ExternalLinkInfo info = new ExternalLinkServiceClient().getLinkInfo((IModel) obj, additionalDetails);
      LinkInfo ret = new LinkInfo();
      ret.setDisplayText(info.getDisplayText());
      ret.setUrl(info.getUrl());
      return ret;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

}
