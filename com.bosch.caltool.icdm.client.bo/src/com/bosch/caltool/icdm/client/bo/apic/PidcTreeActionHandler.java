/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.FavouritesServiceClient;

/**
 * @author dja7cob
 */
public class PidcTreeActionHandler {

  /**
   * @param nameValId
   * @param nameEng
   * @param nameGer
   * @param descEng
   * @param descGer
   * @return
   */
  public AttributeValue updatePidcName(final Long nameValId, final String nameEng, final String nameGer,
      final String descEng, final String descGer) {
    AttributeValueServiceClient attrValSerClient = new AttributeValueServiceClient();
    AttributeValue updatedPidcNameVal = null;
    try {
      AttributeValue pidcNameVal = new AttributeValue();
      pidcNameVal.setId(nameValId);
      pidcNameVal.setTextValueEng(nameEng);
      pidcNameVal.setTextValueGer(nameGer);
      pidcNameVal.setDescriptionEng(descEng);
      pidcNameVal.setDescriptionGer(descGer);
      updatedPidcNameVal = attrValSerClient.updatePidcName(pidcNameVal);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    return updatedPidcNameVal;
  }

  /**
   * @param pidcNameVal
   */
  public void deleteUnDelPidc(final AttributeValue pidcNameVal) {
    AttributeValueServiceClient attrValService = new AttributeValueServiceClient();
    try {
      attrValService.deleteUnDelPidc(pidcNameVal);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param selPidc
   */
  public void setasActiveVer(final Pidc selPidc) {
    PidcServiceClient pidcService = new PidcServiceClient();
    try {
      pidcService.update(selPidc);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcId
   */
  public void createPidcFavourite(final Long pidcId) {
    FavouritesServiceClient favSerClient = new FavouritesServiceClient();
    PidcFavourite pidcFav = new PidcFavourite();
    pidcFav.setPidcId(pidcId);

    CurrentUserBO currUser = new CurrentUserBO();
    try {
      pidcFav.setUserId(currUser.getUserID());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    try {
      favSerClient.createPidcFavourite(pidcFav);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param id
   */
  public void deletePidcFavourite(final PidcFavourite pidcFav) {
    FavouritesServiceClient favSerClient = new FavouritesServiceClient();
    try {
      favSerClient.deletePidcFavourite(pidcFav);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }
}
