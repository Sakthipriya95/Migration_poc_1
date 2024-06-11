/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.service.client;


import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.calcomp.ssd.api.client.model.InputReviewDetailsModel;
import com.bosch.calcomp.ssd.api.client.model.ReviewResultModel;

/**
 * @author TAB1JA
 */
public class ReviewServiceClient {

  private static final String CONTENT_TYPE_JSON = "application/json";

  private static final String CONTENT_TYPE_STR = "Content-Type";


  /**
   * @param reviewModel input model
   * @param targetUrl service url
   * @return service response
   */
  public ReviewResultModel createAndUpdateReview(final InputReviewDetailsModel reviewModel, final String targetUrl) {
    try {
      final Client client = ClientBuilder.newBuilder().build();
      WebTarget target = client.target(targetUrl).path("createReviewSSD");
      Builder builder = target.request(MediaType.APPLICATION_JSON);
      builder.header(HttpHeaders.ACCEPT_ENCODING, CONTENT_TYPE_JSON);
      builder.header(CONTENT_TYPE_STR, CONTENT_TYPE_JSON);
      builder.header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:68.0) Gecko/20100101 Firefox/68.0");

      String revJson = reviewModel.getComplianceReviewJson().toString();
      Response response = builder.post(Entity.entity(revJson, javax.ws.rs.core.MediaType.APPLICATION_JSON));

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String result = response.readEntity(String.class);
        return new ReviewResultModel(result);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * @param targetUrl url
   * @param ssdReviewID review id
   * @param pdfFileLoc pdf file location
   * @return service response
   */
  public Response uploadReviewFile(final String targetUrl, final String ssdReviewID, final String pdfFileLoc) {

    final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();

    WebTarget target = client.target(targetUrl).path("uploadfile");

    final FileDataBodyPart filePart = new FileDataBodyPart("file", new File(pdfFileLoc));

    try (FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("foo", "bar").bodyPart(filePart)) {

      return target.queryParam("ssdrvid", ssdReviewID).request()
          .post(Entity.entity(multipart, multipart.getMediaType()));

    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }


}
