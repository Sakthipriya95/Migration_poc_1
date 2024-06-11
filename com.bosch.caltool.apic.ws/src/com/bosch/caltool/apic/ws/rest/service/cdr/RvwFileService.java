/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.RvwFileCommand;
import com.bosch.caltool.icdm.bo.cdr.RvwFileLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.RvwFile;


/**
 * Service class for Review Files
 *
 * @author bru2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RVWFILE)
public class RvwFileService extends AbstractRestService {


  /**
   * Rest web service path for result_id
   */
  public final static String RWS_GET_BY_RESULT_ID = "byresultid";
  /**
   * Rest web service path for result_id
   */
  public final static String RWS_QP_RESULT_ID = "resultid";

  /**
   * Get Review Files using ResultId id
   *
   * @param resultId Result Id id
   * @return Rest response, with RvwFile object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_BY_RESULT_ID)
  @CompressData
  public Response getByResultId(@QueryParam(WsCommonConstants.RWS_QP_RESULT_ID) final Long resultId)
      throws IcdmException {
    RvwFileLoader loader = new RvwFileLoader(getServiceData());
    CDRReviewResultLoader resultLoader = new CDRReviewResultLoader(getServiceData());
    TRvwResult entityObject = resultLoader.getEntityObject(resultId);
    Map<Long, RvwFile> retMap = loader.getByResultId(entityObject);
    getLogger().info(" Review Files getByResultId completed. Total records = {}", retMap.size());
    return Response.ok(retMap).build();
  }

  /**
   * Get CDR Result files using its id. Note : this is a POST request
   *
   * @param objIdSet set of object IDs
   * @return Rest response, with Rvw file object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  @Path(WsCommonConstants.RWS_MULTIPLE)
  public Response getMultiple(final Set<Long> objIdSet) throws IcdmException {
    RvwFileLoader loader = new RvwFileLoader(getServiceData());
    Map<Long, RvwFile> ret = loader.getDataObjectByID(objIdSet);
    return Response.ok(ret).build();
  }

  /**
   * @param multiPart multiPart
   * @param fileInputStream review file fileInputStream
   * @param rvwfile rvwfile object
   * @return rvwfile object
   * @throws IcdmException Exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_FILES_TO_BE_REVIEWED)
  @CompressData
  public Response create(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_RVW_FILE_OBJ) final RvwFile rvwfile)
      throws IcdmException {
    FormDataBodyPart field = multiPart.getField(WsCommonConstants.RWS_FILE_PATH);
    InputStream unzipIfZippedStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
    RvwFile obj = rvwfile.clone();
    obj.setFilePath(null);
    RvwFileCommand cmd = new RvwFileCommand(getServiceData(), obj, false, false);
    try {
      cmd.setFileData(IOUtils.toByteArray(unzipIfZippedStream));
    }
    catch (IOException e) {
      throw new IcdmException("Error while converting inputstream to Byte array ", e);
    }
    executeCommand(cmd);
    RvwFile ret = cmd.getNewData();
    getLogger().info("Created Review Files Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a Review Files record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> objIds) throws IcdmException {
    RvwFileLoader loader = new RvwFileLoader(getServiceData());

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();
    Set<Long> fileIdSet = new HashSet<>();
    for (Long fileId : objIds) {
      RvwFileCommand cmd = new RvwFileCommand(getServiceData(), loader.getDataObjectByID(fileId), false, true);
      cmdList.add(cmd);
      fileIdSet.add(fileId);
    }
    executeCommand(cmdList);

    StringBuilder fileIds = new StringBuilder();
    for (Long id : fileIdSet) {
      fileIds.append(id).append("-");
    }
    getLogger().info("Deleted Review file Id : {}", fileIds.toString());
    return Response.ok().build();
  }

  /**
   * Returns byte array of Rvw file data.
   *
   * @param fileId Emr file Id
   * @return byte array
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response downloadRvwFile(@QueryParam(value = WsCommonConstants.RVW_FILE_ID) final Long fileId)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    RvwFileLoader loader = new RvwFileLoader(serviceData);
    // download Rvw file
    byte[] fileData = loader.getRvwFile(fileId);
    getLogger().info("Rvw file to be downloaded : {}", fileId);
    ResponseBuilder response = Response.ok(fileData);
    return response.build();
  }
}
