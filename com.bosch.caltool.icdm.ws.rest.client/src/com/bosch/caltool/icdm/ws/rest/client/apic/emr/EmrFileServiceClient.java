/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic.emr;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EMRWizardData;
import com.bosch.caltool.icdm.model.emr.EmrDataUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.emr.EmrInputData;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class EmrFileServiceClient extends AbstractRestServiceClient {

  private static final IMapper EMR_UPLOAD_RESP_MAPPER = obj -> {
    Collection<IModel> changeDataList = new HashSet<>();
    changeDataList.addAll(((EMRFileUploadResponse) obj).getEmrFileMap().values());
    return changeDataList;
  };


  /**
   * Service client for EmrFileService
   */
  public EmrFileServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_EMISSION_ROBUSTNESS +
        WsCommonConstants.RWS_URL_DELIMITER + WsCommonConstants.RWS_HANDLE_FILE);
  }

  /**
   * Get all the Pidc EMR file variants For the version id
   *
   * @param pidVersId pidc version id
   * @return Map of EMR file-variant mapping,key=Emr File Id ,value=EmrFileMapping
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, EmrFileMapping> getPidcEmrFileMapping(final Long pidVersId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_EMR_FILE_VARIANTS)
        .queryParam(WsCommonConstants.RWS_QP_VERS_ID, pidVersId);

    GenericType<Map<Long, EmrFileMapping>> type = new GenericType<Map<Long, EmrFileMapping>>() {};
    return get(wsTarget, type);
  }


  /**
   * Download EMR file for given EMR file Id
   *
   * @param fileId Emr FileId
   * @param fileName fileName
   * @param directory file path
   * @return inputStream input Stream
   * @throws ApicWebServiceException data exception
   */
  public String downloadEmrFile(final Long fileId, final String fileName, final String directory)
      throws ApicWebServiceException {
    return downloadFile(getWsBase().queryParam(WsCommonConstants.RWS_EMR_FILE_ID, fileId), fileName, directory);
  }


  /**
   * Updates EMR file details
   *
   * @param pidcVersId Long
   * @param pidcVersionLink hyperlink
   * @param tableInput List of EMRWizardData
   * @param notFoundFiles
   * @return EMRFileUploadResponse service response
   * @throws IOException i/o Exception
   * @throws ApicWebServiceException WS exception
   */
  public EMRFileUploadResponse uploadEMRFilesForPidcVersion(final Long pidcVersId, final String pidcVersionLink,
      final List<EMRWizardData> tableInput, final List<String> notFoundFiles)
      throws IOException, ApicWebServiceException {

    FormDataMultiPart multipart = new FormDataMultiPart();

    // add version id to body part
    FormDataBodyPart versionIdBodyPart =
        new FormDataBodyPart(WsCommonConstants.EMR_PIDC_VERSION_ID, pidcVersId.toString());
    multipart.bodyPart(versionIdBodyPart);

    // add version link to body part
    FormDataBodyPart versionLinkBodyPart =
        new FormDataBodyPart(WsCommonConstants.RWS_PIDC_VERSION_LINK, pidcVersionLink);
    multipart.bodyPart(versionLinkBodyPart);

    for (EMRWizardData emrWizardData : tableInput) {
      // for each file data
      String filePath = emrWizardData.getFilePath();
      if (!notFoundFiles.contains(filePath)) {
        // create filedatabodypart with the file
        final FileDataBodyPart emrFile = new FileDataBodyPart(WsCommonConstants.EMR_UPLOAD_FILES, new File(filePath));
        // add file path to header
        emrFile.getHeaders().add(WsCommonConstants.EMR_FILE_PATH, filePath);
        // add file name to header
        emrFile.getHeaders().add(WsCommonConstants.EMR_FILE_NAME, emrWizardData.getFileName());
        // add desription to header
        emrFile.getHeaders().add(WsCommonConstants.EMR_FILE_DESC, emrWizardData.getDescripton());
        // add isVariantScoped to header
        Boolean partialPIDCScope = emrWizardData.isPartialPIDCScope();
        emrFile.getHeaders().add(WsCommonConstants.EMR_FILE_SCOPE, partialPIDCScope.toString());
        multipart.bodyPart(emrFile);
      }

    }

    EMRFileUploadResponse fileUploadResponse =
        create(getWsBase(), multipart, EMRFileUploadResponse.class, EMR_UPLOAD_RESP_MAPPER);
    multipart.close();
    return fileUploadResponse;

  }

  /**
   * Updates EMR file details
   *
   * @param emrFile emrFile to be updated
   * @return updated instance
   * @throws ApicWebServiceException exception
   */
  public EmrFile updateEmrFileDetails(final EmrFile emrFile) throws ApicWebServiceException {
    return update(getWsBase(), emrFile);
  }

  /**
   * get the error messages during upload
   *
   * @param emrFileId Long
   * @return List<EmrUploadError>
   * @throws ApicWebServiceException exception
   */
  public List<EmrUploadError> getUploadProtocol(final Long emrFileId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_EMR_FILE_UPLOAD_RESULT)
        .queryParam(WsCommonConstants.RWS_EMR_FILE_ID, emrFileId);

    GenericType<List<EmrUploadError>> type = new GenericType<List<EmrUploadError>>() {};
    return get(wsTarget, type);

  }

  /**
   * @param emrFileId Long
   * @return EMRFileUploadResponse
   * @throws ApicWebServiceException exception
   */
  public EMRFileUploadResponse reloadEmrFile(final Long emrFileId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_EMR_FILE_RELOAD)
        .queryParam(WsCommonConstants.RWS_EMR_FILE_ID, emrFileId);
    return create(wsTarget, emrFileId, EMRFileUploadResponse.class, EMR_UPLOAD_RESP_MAPPER);
  }

  /**
   * @param emrInputData EmrInputData
   * @return EmrDataUploadResponse
   * @throws ApicWebServiceException exception
   */
  public EmrDataUploadResponse createEmrData(final EmrInputData emrInputData) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_EMR_DATA);
    return post(wsTarget, emrInputData, EmrDataUploadResponse.class);
  }
}
