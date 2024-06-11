/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.client.file.upload;

import java.io.FileOutputStream;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 * @author VAU3COB
 */
public class ReviewClient {

  public static void main(final String[] args) {

    // testServer();
    // testLocal();
    try {
//      fileDownload();
      testLocal();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static void testServer() {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://si-cdm01.de.bosch.com:8680/ssdapi/rest/reviewservice/")
        .path("updateReviewStatuswithDecision");
    Form form = new Form();
    Response response = target.queryParam("rvwID", "RV1").queryParam("status", 4).queryParam("decision", 4).request()
        .post(Entity.form(form));
    System.out.println(response.getStatus() + ":" + response.readEntity(String.class));

  }


  /**
   *
   */
  private static void testLocal() {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://localhost:8090/ssdapi/rest/reviewservice/").path("updateReviewStatus");
    Form form = new Form();
    Response response = target.queryParam("rvwID", "RT95062").queryParam("status", 5).request().post(Entity.form(form));
    System.out.println(response.getStatus() + ":" + response.readEntity(String.class));
  }


  public static void fileDownload() throws Exception {
    // String url = "http://localhost:8080/com.bosch.ssd.api/rest/fileservice?";
    // Response response2 = client2.target(url).request().get();
    String location = "c://temp//777//testdownload.war";


    Client client = ClientBuilder.newClient();
    WebTarget target = client.target("http://localhost:8081/com.bosch.ssd.api/rest/fileservice/").path("downloadFile");
    Form form = new Form();
    Response response = target.queryParam("keyName", "test").queryParam("fileName", "test").request().get();

    FileOutputStream out = new FileOutputStream(location);
    InputStream is = (InputStream) response.getEntity();
    int len = 0;
    byte[] buffer = new byte[4096];
    while ((len = is.read(buffer)) != -1) {
      out.write(buffer, 0, len);
    }
    out.flush();
    out.close();
    is.close();
  }
}
