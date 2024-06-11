/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.client.file.upload;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/**
 * @author VAU3COB
 */
public class FileUpload {

  /**
   * @param args
   */
  public static void main(
      final String[] args) {/*
                             * Client client = ClientBuilder.newBuilder() .register(MultiPartFeature.class).build();
                             * WebTarget webTarget = client.target(
                             * "http://localhost:8081/com.bosch.ssd.api/rest/fileuploadservice/uploadfile2"); MultiPart
                             * multiPart = new MultiPart(); multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
                             * FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", new
                             * File("C:/temp/purpose.PNG"), MediaType.APPLICATION_OCTET_STREAM_TYPE); FormDataMultiPart
                             * formDataMultiPart = new FormDataMultiPart(); formDataMultiPart.field("id",
                             * "ssdid").bodyPart(fileDataBodyPart); fileDataBodyPart.set
                             * multiPart.bodyPart(fileDataBodyPart); multiPart.set Response response =
                             * webTarget.request(MediaType.APPLICATION_JSON_TYPE) .post(Entity.entity(multiPart,
                             * multiPart.getMediaType())); System.out.println(response.getStatus() + " " +
                             * response.getStatusInfo() + " " + response);
                             */

    testUpload1();
  }

  /**
   *
   */
  private static void testUpload1() {
    // TODO Auto-generated method stub
    final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

    final FileDataBodyPart filePart =
        new FileDataBodyPart("file", new File("C:\\Users\\HSU4COB\\Desktop\\SSD\\newfolder\\Compliance_Labels.pdf"));
    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
    final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("foo", "bar").bodyPart(filePart);

//    final WebTarget target = client.target("http://localhost:8080/com.bosch.ssd.api/rest/fileuploadservice/uploadfile");
    final WebTarget target =
        client.target("http://si-cdm01.de.bosch.com:8780/ssdapi/rest/fileuploadservice/uploadfile");
    final Response response =
        target.queryParam("ssdrvid", "1234567").request().post(Entity.entity(multipart, multipart.getMediaType()));
    // Use response object to verify upload success
    System.out.println(response.getStatus());
    System.out.println(response.getEntity());
    try {
      formDataMultiPart.close();
      multipart.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
