/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Review Files
 *
 * @author bru2cob
 */
public class RvwFileServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public RvwFileServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVWFILE);
  }

  /**
   * Get Review Files records using ResultId
   *
   * @param resultId Result Id id
   * @return Map. Key - id, Value - RvwFile object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, RvwFile> getByResultId(final Long resultId) throws ApicWebServiceException {
    LOGGER.debug("Get Review Files records using ResultId ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_RESULT_ID)
        .queryParam(WsCommonConstants.RWS_QP_RESULT_ID, resultId);
    GenericType<Map<Long, RvwFile>> type = new GenericType<Map<Long, RvwFile>>() {};
    Map<Long, RvwFile> retMap = get(wsTarget, type);
    LOGGER.debug("Review Files records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Create a Review Files record
   *
   * @param obj object to create
   * @return created RvwFile object
   * @throws ApicWebServiceException exception while invoking service
   */
  public RvwFile create(final RvwFile obj) throws ApicWebServiceException {
    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
    FormDataMultiPart multipart = null;
    RvwFile rvwFile = null;
    String zippedFile = compressFile(obj.getFilePath());
    try {
      LOGGER.debug("Add files to be reviewed service started...");
      // calls the WS
      final FileDataBodyPart filePart = new FileDataBodyPart(WsCommonConstants.RWS_FILE_PATH, new File(zippedFile));
      multipart = (FormDataMultiPart) formDataMultiPart
          .field(WsCommonConstants.RWS_RVW_FILE_OBJ, obj, MediaType.APPLICATION_JSON_TYPE).bodyPart(filePart);
      rvwFile = create(getWsBase().path(WsCommonConstants.RWS_FILES_TO_BE_REVIEWED), multipart, RvwFile.class);

      LOGGER.debug("Add files to be reviewed service ended");
    }
    finally {
      closeResource(multipart);
      closeResource(formDataMultiPart);
    }
    return rvwFile;
  }

  /**
   * Delete a Review Files record
   *
   * @param objIds id of objects to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Set<RvwFile> objIds) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase();
    delete(wsTarget, objIds);
  }

  /**
   * Download Review file for given Review file Id
   *
   * @param fileId Review FileId
   * @param fileName fileName
   * @param directory file path
   * @return inputStream input Stream
   * @throws ApicWebServiceException data exception
   */
  public String downloadEmrFile(final Long fileId, final String fileName, final String directory)
      throws ApicWebServiceException {
    return downloadFile(getWsBase().queryParam(WsCommonConstants.RVW_FILE_ID, fileId), fileName, directory);
  }

  /**
   * Get CDR Result files using its ids. Note : this is a POST request
   *
   * @param objIdSet set of object ids
   * @return CDRResultParameter object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, RvwFile> getMultiple(final Set<Long> objIdSet) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE);

    GenericType<Map<Long, RvwFile>> type = new GenericType<Map<Long, RvwFile>>() {};
    return post(wsTarget, objIdSet, type);
  }
}
