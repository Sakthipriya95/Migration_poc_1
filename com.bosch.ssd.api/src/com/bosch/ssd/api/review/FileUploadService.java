/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.review;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.ssd.api.logger.SSDApiLogger;
import com.bosch.ssd.api.util.SSDAPIUtil;

/**
 * @author VAU3COB
 */
@Path("/fileuploadservice")
public class FileUploadService {

  public static String STR_WORKPRODUCT = "WorkProduct";

  /**
   * @param ssdRvId - SSD Review ID
   * @param uploadedInputStream - Input stream as multipart
   * @param fileDetail FileDetails
   * @return Response with Status code
   */
  @POST
  @Path("/uploadfile")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadFile(@QueryParam("ssdrvid") final long ssdRvId,
      @FormDataParam("file") final InputStream uploadedInputStream,
      @FormDataParam("file") final FormDataContentDisposition fileDetail) {

    String fileLocation = SSDAPIUtil.getInstance().getFileLocation() + File.separator + ssdRvId + File.separator +
        STR_WORKPRODUCT + File.separator + fileDetail.getFileName();
    SSDApiLogger.getLoggerInstance().info(fileLocation + " -File");
    // save it
    writeToFile(uploadedInputStream, fileLocation);
    String output = "File uploaded to : " + fileLocation;
    SSDApiLogger.getLoggerInstance().info(output);

    return Response.status(200).entity(output).build();

  }

  /**
   * save uploaded file to new location
   *
   * @param uploadedInputStream
   * @param uploadFileLocation
   */
  private void writeToFile(final InputStream uploadedInputStream, final String uploadFileLocation) {
    // To Create sub directories if its not existing.
    File file = new File(uploadFileLocation);
    try {
      file.getParentFile().mkdirs();
      SSDApiLogger.getLoggerInstance().info(file.getParentFile() + " - Subdirectory created");
    }
    catch (SecurityException se) {
      SSDApiLogger.getLoggerInstance().error("Error while creating sub directory : " + se.getLocalizedMessage());
    }
    // End

    try (OutputStream out = new FileOutputStream(new File(uploadFileLocation))) {
      int read = 0;
      byte[] bytes = new byte[1024];

      while ((read = uploadedInputStream.read(bytes)) != -1) {
        out.write(bytes, 0, read);
      }
      out.flush();
    }
    catch (IOException e) {
      SSDApiLogger.getLoggerInstance().error(e.getLocalizedMessage());
    }

  }


}
