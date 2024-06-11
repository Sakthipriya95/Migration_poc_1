/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * msp5cob
 */
public class DataAssessmentSharePointUploadBO {

  private final Long dataAssessmentId;
  private final Long pidcVersionId;
  private final boolean isBaselinesPage;

  private String sharePointUrl;
  private String sharePointAttrName;

  /**
   * @param dataAssessmentId dataAssessmentId
   * @param pidcVersionId pidcVersionId
   * @param isOverviewPage isOverviewPage
   */
  public DataAssessmentSharePointUploadBO(final Long dataAssessmentId, final Long pidcVersionId,
      final boolean isOverviewPage) {
    this.dataAssessmentId = dataAssessmentId;
    this.pidcVersionId = pidcVersionId;
    this.isBaselinesPage = isOverviewPage;
  }


  /**
   * Checks if PIDC has SharePointUrl configured and also loads the Attribute details
   *
   * @return SharePoint Url is configured in PIDC
   */
  public String checkAndLoadPidcSharePointUrl() {
    try {
      PidcVersionAttribute versAttr = new PidcVersionAttributeServiceClient()
          .getPidcVersAttributeById(this.pidcVersionId, CommonParamKey.SHAREPOINT_ARCHIVAL_URL_ATTR.getParamName());
      this.sharePointUrl = versAttr.getValue();
      this.sharePointAttrName = versAttr.getName();
      return this.sharePointAttrName + " : " + this.sharePointUrl;
    }
    catch (ApicWebServiceException e) {
      if (this.isBaselinesPage) {
        CDMLogger.getInstance().warnDialog(e.getMessage(), Activator.PLUGIN_ID);
      }
      return null;
    }
  }

  /**
   * @return DataAssessSharePointUploadInputModel
   */
  public DataAssessSharePointUploadInputModel getDataAssessmentToSharePointInput() {
    if (this.sharePointUrl != null) {
      return createInputModel();
    }
    return null;
  }

  private DataAssessSharePointUploadInputModel createInputModel() {
    CurrentUserBO currentUserBO = new CurrentUserBO();
    DataAssessSharePointUploadInputModel inputModel = null;
    try {
      if (currentUserBO.hasPassword()) {
        inputModel = new DataAssessSharePointUploadInputModel();
        inputModel.setBaselineId(this.dataAssessmentId);
        inputModel.setUserName(currentUserBO.getUserName());
        inputModel.setEncryptedPassword(currentUserBO.getEncPassword());
        inputModel.setSharePointUrl(this.sharePointUrl);
        inputModel.setSharePointAttrName(this.sharePointAttrName);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), Activator.PLUGIN_ID);
    }
    return inputModel;
  }


}
