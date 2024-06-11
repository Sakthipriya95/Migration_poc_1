/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringJoiner;

import javax.ws.rs.client.WebTarget;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportInput;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.util.ZipUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author pdh2cob
 */
public class CdfxExportServiceClient extends AbstractRestServiceClient {

  /**
   *
   */
  public CdfxExportServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDFX_EXPORT);
  }

  /**
   * @param zipFilePath zipFilePath
   * @param inputModel input model
   * @param fileName - file name
   * @return CdfxExportOutputModel
   * @throws ApicWebServiceException exception
   */
  public CdfxExportOutput exportCdfx(final CdfxExportInput inputModel, final String zipFilePath, final String fileName)
      throws ApicWebServiceException {
    String filePath = downloadFilePost(getWsBase(), inputModel, fileName, zipFilePath);
    return createOutputModel(filePath, inputModel);
  }

  private CdfxExportOutput createOutputModel(final String zipFilePath, final CdfxExportInput inputModel)
      throws ApicWebServiceException {

    CdfxExportOutput outputModel = null;

    String unzippedFolder = zipFilePath.substring(0, zipFilePath.lastIndexOf('.'));

    String outputFolder = zipFilePath.substring(0, zipFilePath.lastIndexOf(File.separator));

    try {
      ZipUtils.unzip(zipFilePath, outputFolder);
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }


    File[] variantsFolder = new File(unzippedFolder).listFiles();
    int variantsCount = inputModel.getVariantsList().size();
    for (File folder : variantsFolder) {
      for (File file : new File(folder.getAbsolutePath()).listFiles()) {
        if (file.getName().endsWith(".json")) {
          ObjectMapper mapper = new ObjectMapper();
          try {
            outputModel = mapper.readValue(file, CdfxExportOutput.class);
            FileUtils.deleteDirectory(new File(unzippedFolder));
            break;
          }
          catch (IOException exp) {
            throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
          }
        }
      }
      // If the input variant is single just break from the ouer loop
      if (outputModel != null) {
        break;
      }
    }
    // If the input variants size is more than one then fetch cdfx and xlsx file names
    if ((variantsCount > 1) && (outputModel != null)) {
      createMultiVarsOutputModel(inputModel, outputModel);
    }
    return outputModel;
  }

  /**
   * @param inputModel
   * @param outputModel
   */
  private void createMultiVarsOutputModel(final CdfxExportInput inputModel, final CdfxExportOutput outputModel) {
    StringJoiner cdfxFileNamesJoiner = new StringJoiner(",");
    StringJoiner xlsxFileNamesJoiner = new StringJoiner(",");
    for (PidcVariant variant : inputModel.getVariantsList()) {
      cdfxFileNamesJoiner.add(variant.getName() + ".cdfx");
      xlsxFileNamesJoiner.add(variant.getName() + ".xlsx");
    }
    outputModel.setCdfxFileName(cdfxFileNamesJoiner.toString());
    outputModel.setExcelFileName(xlsxFileNamesJoiner.toString());
  }

  /**
   * Retrieves the cdfx readiness condition file
   *
   * @param dirPath directory path
   * @return byte[]
   * @throws ApicWebServiceException error during webservice call
   */
  public byte[] getCdfxReadinessConditionFile(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CDFX_READINESS_FILE);
    downloadFile(wsTarget, ApicConstants.CDFX_READINESS_FILE, dirPath);
    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.CDFX_READINESS_FILE));
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }

  }
}
