/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.io.File;
import java.io.IOException;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class A2LFileUploadServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor.
   */
  public A2LFileUploadServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_FILE_UPLOAD);
  }


  /**
   * Upload A 2 L file - Returns A2lFileId.
   *
   * @param filePath the file path
   * @param pidcVersId the pidc vers id
   * @param sdomPverName the sdom pver name
   * @param pverVariant the pver variant
   * @param pverVariantRevId the pver variant rev id
   * @return the long
   * @throws ApicWebServiceException the apic web service exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public PidcA2l uploadA2LFile(final String filePath, final Long pidcVersId, final String sdomPverName,
      final String pverVariant, final Long pverVariantRevId)
      throws ApicWebServiceException, IOException {

    // validate the input
    validate(filePath, pidcVersId, sdomPverName, pverVariant, pverVariantRevId);

    FormDataMultiPart multipart = new FormDataMultiPart();

    // add version id to body part
    FormDataBodyPart pverVariantRevIdBodyPart =
        new FormDataBodyPart(WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT_REV, pverVariantRevId.toString());
    multipart.bodyPart(pverVariantRevIdBodyPart);

    // add version id to body part
    FormDataBodyPart versionIdBodyPart =
        new FormDataBodyPart(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId.toString());
    multipart.bodyPart(versionIdBodyPart);

    // add pver to body part
    FormDataBodyPart pverVariantBodyPart =
        new FormDataBodyPart(WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT, pverVariant);
    multipart.bodyPart(pverVariantBodyPart);

    // add pver to body part
    FormDataBodyPart pverBodyPart = new FormDataBodyPart(WsCommonConstants.RWS_QP_SDOM_PVER_NAME, sdomPverName);
    multipart.bodyPart(pverBodyPart);

    // create filedatabodypart with the file
    final FileDataBodyPart emrFile = new FileDataBodyPart(WsCommonConstants.A2L_FILE_MULTIPART, new File(filePath));
    multipart.bodyPart(emrFile);

    PidcA2l output = create(getWsBase(), multipart, PidcA2l.class);
    multipart.close();
    return output;
  }


  /**
   * Method to validate input
   *
   * @param filePath
   * @param pidcVersId
   * @param sdomPverName
   * @param pverVariant
   * @param pverVariantRevId
   * @throws ApicWebServiceException
   */
  private void validate(final String filePath, final Long pidcVersId, final String sdomPverName,
      final String pverVariant, final Long pverVariantRevId)
      throws ApicWebServiceException {
    String errorMsg = null;
    if (filePath == null) {
      errorMsg = "A2L File path cannot be null.";
    }
    else if (pidcVersId == null) {
      errorMsg = "Pidc version id cannot be null.";
    }
    else if (sdomPverName == null) {
      errorMsg = "SDOM PVER Name cannot be null.";
    }
    else if (pverVariant == null) {
      errorMsg = "SDOM PVER Variant cannot be null.";
    }
    else if (pverVariantRevId == null) {
      errorMsg = "SDOM PVER Revision cannot be null.";
    }

    if (errorMsg != null) {
      throw new ApicWebServiceException(WSErrorCodes.ICDM_ERROR, errorMsg);
    }
  }

}
