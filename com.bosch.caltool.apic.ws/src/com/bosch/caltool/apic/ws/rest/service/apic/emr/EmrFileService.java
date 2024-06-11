/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.emr;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.emr.EmrDataUploadCommand;
import com.bosch.caltool.icdm.bo.emr.EmrFileCommand;
import com.bosch.caltool.icdm.bo.emr.EmrFileLoader;
import com.bosch.caltool.icdm.bo.emr.EmrFileUploadCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrDataUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileInputData;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.emr.EmrInputData;
import com.bosch.caltool.icdm.model.general.IcdmFileData;

/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_EMISSION_ROBUSTNESS + "/" +
    WsCommonConstants.RWS_HANDLE_FILE)
public class EmrFileService extends AbstractRestService {

  /**
   * @param pidcVersId version id
   * @return Response with list of EMR file-variant details
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_EMR_FILE_VARIANTS)
  @CompressData
  public Response getPidcEmrFileMapping(@QueryParam(value = WsCommonConstants.RWS_QP_VERS_ID) final Long pidcVersId)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    EmrFileLoader loader = new EmrFileLoader(serviceData);
    Map<Long, EmrFileMapping> retMap = loader.getEmrFileVariantMapping(pidcVersId);
    getLogger().info("Number of Pidc EMR files for pidc version = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Returns byte array of EMR file data.
   *
   * @param fileId Emr file Id
   * @return byte array
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response downloadEmrFile(@QueryParam(value = WsCommonConstants.RWS_EMR_FILE_ID) final Long fileId)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    EmrFileLoader loader = new EmrFileLoader(serviceData);
    // download EMR file
    byte[] fileData = loader.getEmrFile(fileId);
    getLogger().info("EMR file to be downloaded : {}", fileId);
    ResponseBuilder response = Response.ok(fileData);
    return response.build();
  }


  /**
   * Upload a new Codex file
   *
   * @param multiPart multipart data
   * @return result
   * @throws IcdmException any error at the server side
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @CompressData
  public Response uploadEMRFile(final FormDataMultiPart multiPart) throws IcdmException {

    FormDataBodyPart pidcVersionIdPart = multiPart.getField(WsCommonConstants.EMR_PIDC_VERSION_ID);
    String pidcVersIdStr = pidcVersionIdPart.getValue();
    if (null == pidcVersIdStr) {
      // throw invalid input exception in case PIDC Version id is null
      throw new InvalidInputException("Invalid PIDC Version ID - " + pidcVersIdStr);
    }
    FormDataBodyPart pidcVersionLinkPart = multiPart.getField(WsCommonConstants.RWS_PIDC_VERSION_LINK);
    String pidcVersLinkStr = pidcVersionLinkPart.getValue();

    // data for command
    EmrFileInputData cmdIpData = new EmrFileInputData();

    Long pidcVersId = Long.parseLong(pidcVersIdStr);
    cmdIpData.setPidcVersId(pidcVersId);
    cmdIpData.setPidcVersionLink(pidcVersLinkStr);

    // get the EMR Excel file
    List<FormDataBodyPart> emrFileList = multiPart.getFields(WsCommonConstants.EMR_UPLOAD_FILES);
    if (emrFileList == null) {
      throw new InvalidInputException("EMR Excel File not provided");
    }
    // created two separate maps for emr file and icdm file data so as to maintain uniqueness of the key in the map
    Map<String, EmrFile> emrFileMap = cmdIpData.getEmrFileMap();
    Map<String, IcdmFileData> icdmFileDataMap = cmdIpData.getIcdmFileDataMap();
    InputStream emrFileInputStream = null;

    for (FormDataBodyPart field : emrFileList) {

      emrFileInputStream = field.getValueAs(InputStream.class);

      byte[] byteArray;
      try {
        byteArray = IOUtils.toByteArray(emrFileInputStream);
      }
      catch (IOException exp) {
        // throw invalid input exception in case the input stream cannot be
        throw new InvalidInputException("Error while reading excel file. " + exp.getMessage(), exp);
      }
      // IcdmFileData
      IcdmFileData fileData = new IcdmFileData();
      fileData.setFileData(byteArray);

      // EmrFile
      EmrFile emrFile = new EmrFile();
      emrFile.setDeletedFlag(false);
      emrFile.setPidcVersId(pidcVersId);

      // get the file name
      List<String> fileName = field.getHeaders().get(WsCommonConstants.EMR_FILE_NAME);
      if (CommonUtils.isNotEmpty(fileName)) {
        emrFile.setName(fileName.get(0));
        getLogger().debug("File Name - {}", fileName);
      }

      // get the file description
      List<String> fileDesc = field.getHeaders().get(WsCommonConstants.EMR_FILE_DESC);
      if (CommonUtils.isNotEmpty(fileDesc)) {
        emrFile.setDescription(fileDesc.get(0));
        getLogger().debug("Description - {}", fileDesc);
      }

      // get the file scope
      List<String> scope = field.getHeaders().get(WsCommonConstants.EMR_FILE_SCOPE);
      if (CommonUtils.isNotEmpty(scope)) {
        emrFile.setIsVariant(Boolean.parseBoolean(scope.get(0)));
        getLogger().debug("Scope - {}", scope);
      }
      List<String> emrFilePath = field.getHeaders().get(WsCommonConstants.EMR_FILE_PATH);
      if (CommonUtils.isNotEmpty(emrFilePath)) {
        emrFileMap.put(emrFilePath.get(0), emrFile);
        icdmFileDataMap.put(emrFilePath.get(0), fileData);
        getLogger().debug("Emr File Path Location - {}", emrFilePath);
      }
    }

    // execute command
    EmrFileUploadCommand emrUploadCommand = new EmrFileUploadCommand(getServiceData(), cmdIpData);
    executeCommand(emrUploadCommand);

    EMRFileUploadResponse upldRslt = emrUploadCommand.getUploadResponse();

    getLogger().info("EMR File(s) created = {}. Errors found = {}", upldRslt.getEmrFileMap().keySet(),
        upldRslt.getEmrFileErrorMap().size());

    return Response.ok(upldRslt).build();

  }

  /**
   * Update Codex file details
   *
   * @param file Codex file details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response updateEmrFileDetails(final EmrFile file) throws IcdmException {
    ServiceData servData = getServiceData();

    // Invoke command
    EmrFileCommand cmd = new EmrFileCommand(servData, file, true);
    executeCommand(cmd);

    EmrFile ret = cmd.getNewData();
    getLogger().info("Updated EMR file Id : {}", ret.getId());
    return Response.ok(ret).build();
  }


  /**
   * Update Codex file details
   *
   * @param fileId Long
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_EMR_FILE_RELOAD)
  @CompressData
  public Response reloadEmrFile(@QueryParam(value = WsCommonConstants.RWS_EMR_FILE_ID) final Long fileId)
      throws IcdmException {
    ServiceData servData = getServiceData();


    // execute command
    EmrFileUploadCommand emrUploadCommand = new EmrFileUploadCommand(servData, fileId);
    executeCommand(emrUploadCommand);


    EMRFileUploadResponse upldRslt = emrUploadCommand.getUploadResponse();

    getLogger().info("Reloaded EMR file Id : {}", upldRslt.getEmrFileMap().values().iterator().next().getId());
    return Response.ok(upldRslt).build();
  }

  /**
   * @param emrData To be updated in EMR related tables
   * @return Response message
   * @throws IcdmException in case of any exceptions
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_EMR_DATA)
  public Response createEmrData(final EmrInputData emrData) throws IcdmException {

    if (null == emrData.getPidcVersId()) {
      throw new InvalidInputException("PIDC Version ID is mandatory");
    }

    if (CommonUtils.isNullOrEmpty(emrData.getCodexResultsList())) {
      throw new InvalidInputException("EMR Data not provided");
    }

    if (CommonUtils.isEmptyString(emrData.getFileName())) {
      throw new InvalidInputException("EMR file name is mandatory");
    }

    // To update the input data into iCDM tables
    EmrDataUploadCommand emrUploadCommand = new EmrDataUploadCommand(getServiceData(), emrData);
    executeCommand(emrUploadCommand);

    EmrDataUploadResponse uploadResponse = emrUploadCommand.getUploadResponse();

    getLogger().info(
        "EMR File(s) created = {}. Errors found = {}. Variant assignment created = {}. No. of assignments created = {}",
        uploadResponse.getEmrFileMap().keySet(), uploadResponse.getEmrFileErrorMap().size(),
        uploadResponse.getEmrPidcVariantMap().keySet(), uploadResponse.getEmrPidcVariantMap().size());

    return Response.ok(uploadResponse).build();

  }
}
