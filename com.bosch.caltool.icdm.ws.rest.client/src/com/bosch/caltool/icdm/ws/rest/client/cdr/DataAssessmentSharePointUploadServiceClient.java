package com.bosch.caltool.icdm.ws.rest.client.cdr;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for DaDataAssessment
 *
 * @author msp5cob
 */
public class DataAssessmentSharePointUploadServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public DataAssessmentSharePointUploadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_DATA_ASSESSMENT_SHARE_POINT_UPLOAD);
  }


  /**
   * @param inputModel DataAssessSharePointUploadInputModel
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public void uploadFileToSharePoint(final DataAssessSharePointUploadInputModel inputModel)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    new Thread(() -> {
      try {
        String response = (post(wsTarget, inputModel, String.class));
        CDMLogger.getInstance().infoDialog(response, Activator.PLUGIN_ID);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }).start();

  }


}
