/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.apic.ws.rest.service.JsonWriter;
import com.bosch.caltool.icdm.bo.a2l.A2lCompliParameterLoader;
import com.bosch.caltool.icdm.bo.report.compli.A2lCompliCheckPdfReport;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;

/**
 * Get the compliance parameter of an A2L file with configured SSD Class
 *
 * @author svj7cob
 */
// Task 263282
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L)
public class A2lCompliParameterService extends AbstractRestService {


  /**
   * server location
   */
  public static final String SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//A2L_COMPLI_REPORT//";
  private String folderPath;

  /**
   * Gets the compliance parameter in an a2l file
   *
   * @param fileInputStream fileInputStream
   * @param fileMetaData fileMetaData
   * @param webFlowId webFlowId
   * @return the response
   * @throws IcdmException IcdmException
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_COMPLI_PARAM)
  @CompressData
  public Response fetchCompliParameter(
      @FormDataParam(WsCommonConstants.RWS_QP_A2L_FILE) final InputStream fileInputStream,
      @FormDataParam(WsCommonConstants.RWS_QP_A2L_FILE) final FormDataContentDisposition fileMetaData,
      @FormDataParam(WsCommonConstants.RWS_QP_WEB_FLOW_ID) final String webFlowId)
      throws IcdmException {

    WSObjectStore.getLogger().info("fetchCompliParameter service started. WebFlow ID = {}", webFlowId);

    A2lCompliParameterLoader loader = new A2lCompliParameterLoader(getServiceData());
    String fileName = getUTFFilePath(fileMetaData.getFileName());
    // fetch compliance parameters to java model
    A2lCompliParameterServiceResponse response = loader.getCompliParameters(fileInputStream, webFlowId, fileName);

    // if last modified date is not set, try to find it from meta data
    if (CommonUtils.isEmptyString(response.getLastModifiedDate()) && (fileMetaData.getModificationDate() != null)) {
      String lastModifiedDate =
          new SimpleDateFormat(DateFormat.DATE_FORMAT_13, Locale.ENGLISH).format(fileMetaData.getModificationDate());
      response.setLastModifiedDate(lastModifiedDate);
    }

    ResponseBuilder respBuilder;

    if (MediaType.APPLICATION_OCTET_STREAM.equalsIgnoreCase(getRequest().getHeader(HttpHeaders.ACCEPT))) {
      // create Pdf file
      createOutputFolder();
      createPdfReport(response);
      includeQSSDParamINCompliance(response);
      createJsonfile(response);

      // create Zip file and response builder
      try {
        respBuilder = createZipfile();
        File file = new File(this.folderPath);
        delete(file);
      }
      catch (IOException exp) {
        throw new IcdmException("Error when creating Zip file ", exp);
      }
    }
    else {
      // Create Json ouput
      respBuilder = Response.ok(response);
    }


    return respBuilder.build();
  }


  /**
   * @param response
   */
  private void includeQSSDParamINCompliance(final A2lCompliParameterServiceResponse response) {
    for (Entry<String, String> qSSDParams : response.getA2lQSSDParamMap().entrySet()) {
      if (response.getA2lCompliParamMap().get(qSSDParams.getKey()) == null) {
        int compliParamSize = response.getCompliParamSize();
        response.setCompliParamSize(compliParamSize + 1);
      }
    }
  }


  /**
   * @throws IOException
   */
  private ResponseBuilder createZipfile() throws IOException {
    String zipfileName = FilenameUtils.getBaseName(this.folderPath) + ".zip";
    String zipFilePath = SERVER_PATH + zipfileName;

    // Compress output files to a single zip file
    ZipUtils.zip(Paths.get(this.folderPath), Paths.get(SERVER_PATH + zipfileName));

    File zipFile = new File(zipFilePath);
    ResponseBuilder response = Response.ok(zipFile);
    // set the Response file name
    response.header("Content-Disposition", "attachment; filename=" + zipfileName);
    return response;
  }


  /**
   * @param folderPath
   */
  private void delete(final File file) {
    if (file.isDirectory()) {

      // directory is empty, then delete it
      if (file.list().length == 0) {
        file.delete();

      }
      // Directory is not empty
      else {

        // list all the directory contents
        String files[] = file.list();
        for (String temp : files) {
          // construct the file structure
          File fileDelete = new File(file, temp);

          // recursive delete
          delete(fileDelete);
        }

        // check the directory again, if empty then delete it
        if (file.list().length == 0) {
          file.delete();

        }
      }

    }
    else {
      // if file, then delete it
      file.delete();

    }


  }


  /**
   * @param response
   * @throws IcdmException
   */
  private void createJsonfile(final A2lCompliParameterServiceResponse response) throws IcdmException {
    getLogger().debug("Creating JSON report...");
    JsonWriter.createJsonFile(response, this.folderPath, "A2lComplianceReport.json");
    getLogger().debug("JSON report created successfully");
  }


  /**
   * @param fileInputStream
   * @param fileMetaData
   * @param webFlowId
   * @param response
   * @throws UnAuthorizedAccessException
   * @throws IcdmException
   * @throws DataException
   */
  private void createPdfReport(final A2lCompliParameterServiceResponse response) throws IcdmException {
    getLogger().debug("Creating PDF report...");

    // create pdf report
    File pdfFile = new File(this.folderPath + File.separator + "A2lComplianceReport_" +
        ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_17) + ".pdf");
    final A2lCompliCheckPdfReport report = new A2lCompliCheckPdfReport(getServiceData(), response, pdfFile);
    report.constructPdf();
    response.setReportFileName(pdfFile.getName());
    getLogger().debug("PDF report created successfully");

  }


  /**
   * create Ouput folder
   */
  private void createOutputFolder() {
    // create the file only for the first time
    if (this.folderPath == null) {
      String currentDate = new SimpleDateFormat(DateFormat.DATE_FORMAT_18, Locale.getDefault()).format(new Date());
      File file = new File(SERVER_PATH);
      if (!file.exists()) {
        file.mkdir();
      }
      file = new File(file.getAbsoluteFile() + File.separator + "A2lCompliReport" + currentDate);
      file.mkdir();
      this.folderPath = file.getAbsolutePath();
    }

  }

}
