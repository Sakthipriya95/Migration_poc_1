/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportInput;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class CalDataImportServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public CalDataImportServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CAL_DATA_IMPORT);
  }


  public CalDataImportData parsefile(final Set<String> filePathSet, final String funcId, final String paramColType,
      final String funcVersion)
      throws ApicWebServiceException {

    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
    FormDataMultiPart multipart = null;
    CalDataImportData retTypeResponse = null;
    FileDataBodyPart filePart = null;
    try {

      LOGGER.debug("Parse Cal data import file started...");

      Set<String> zippedFilePathSet = new TreeSet<>();
      for (String hexFilePath : filePathSet) {
        String zippedFilePath = compressFile(hexFilePath);
        zippedFilePathSet.add(zippedFilePath);
      }
      for (String filePath : zippedFilePathSet) {
        // calls the WS
        filePart = new FileDataBodyPart(WsCommonConstants.RWS_QP_IMPORT_FILE, new File(filePath));
        if (funcVersion == null) {
          multipart = (FormDataMultiPart) formDataMultiPart.field(WsCommonConstants.RWS_FP_PARAM_COL_ID, funcId)
              .field(WsCommonConstants.RWS_FP_PARAM_COL_TYPE, paramColType).bodyPart(filePart);
        }
        else {
          multipart = (FormDataMultiPart) formDataMultiPart.field(WsCommonConstants.RWS_FP_PARAM_COL_ID, funcId)
              .field(WsCommonConstants.RWS_FP_PARAM_COL_TYPE, paramColType)
              .field(WsCommonConstants.RWS_FP_PARAM_FUNC_VER, funcVersion).bodyPart(filePart);
        }
      }
      retTypeResponse = post(getWsBase().path(WsCommonConstants.RWS_PARSE_FILE), multipart, CalDataImportData.class);

      LOGGER.debug("Fetch the Cal import data completed");
    }
    finally {

      closeResource(multipart);
      closeResource(formDataMultiPart);
    }
    return retTypeResponse;

  }


  /**
   * @param importInput calDataImportData
   * @return
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public CalDataImportData getCalDataCompareList(final CalDataImportInput importInput) throws ApicWebServiceException {
    return post(getWsBase().path(WsCommonConstants.RWS_GET_CAL_COMPARE_OBJ), importInput, CalDataImportData.class);

  }

  /**
   * @param importInput calDataImportData
   * @return
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public CalDataImportSummary createParmandRules(final CalDataImportInput importInput) throws ApicWebServiceException {
    return post(getWsBase().path(WsCommonConstants.RWS_SAVE_PARAMS_RULES), importInput, CalDataImportSummary.class);

  }


}
