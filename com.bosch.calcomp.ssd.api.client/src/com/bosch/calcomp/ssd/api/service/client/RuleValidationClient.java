/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.service.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.calcomp.ssd.api.client.model.RuleValidationOutputModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author TAB1JA
 */
public class RuleValidationClient {

  private static final String CONTENT_TYPE_JSON = "application/json";

  private static final String CONTENT_TYPE_STR = "Content-Type";

  public static final String SUCCESS = "success";

  public static final String ERROR = "error";

  public static final String RESULT = "result";


  public List<RuleValidationOutputModel> getRuleValidation(final String ruleText, final String targetUrl) {
    List<RuleValidationOutputModel> outputModelList = new ArrayList<>();

    try {
      Client client = ClientBuilder.newBuilder().build();
      WebTarget target = client.target(targetUrl).path("validaterule");
      Builder builder = target.request(MediaType.APPLICATION_JSON);
      builder.header(CONTENT_TYPE_STR, CONTENT_TYPE_JSON);
      builder.header("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:68.0) Gecko/20100101 Firefox/68.0");
      Response response = builder.post(Entity.entity(ruleText, javax.ws.rs.core.MediaType.APPLICATION_JSON));

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String result = response.readEntity(String.class);
        outputModelList = updateValidationResult(result);
        return outputModelList;
      }
      return outputModelList;
    }
    catch (Exception e) {
      e.printStackTrace();
      return outputModelList;
    }
  }


  /**
   * @param jsonObject
   * @return
   */
  public List<RuleValidationOutputModel> updateValidationResult(final String resultString) {

    List<RuleValidationOutputModel> outputModelList = new ArrayList<>();

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.readTree(resultString);


      if (jsonNode.get(SUCCESS).asBoolean()) {
        JsonNode jsonArray = jsonNode.get(RESULT);
        for (int i = 0; i < jsonArray.size(); i++) {

          // store each object in JSONObject
          JsonNode explrObject = jsonArray.get(i);
          RuleValidationOutputModel outputModel = new RuleValidationOutputModel();
          // get field value from JSONObject using get() method
          if (!explrObject.get("lineNo").isNull()) {
            outputModel.setLineNo(explrObject.get("lineNo").asInt());
          }

          if (!explrObject.get("message").isNull()) {
            outputModel.setMessage(explrObject.get("message").asText(""));
          }

          if (!explrObject.get("type").isNull()) {
            outputModel.setType(explrObject.get("type").asText(""));
          }
          outputModelList.add(outputModel);
        }
        return outputModelList;
      }

      RuleValidationOutputModel outputModel = new RuleValidationOutputModel();
      outputModel.setMessage(jsonNode.get(ERROR).asText());
      outputModelList.add(outputModel);
      return outputModelList;

    }
    catch (IOException e) {
      e.printStackTrace();
      return outputModelList;
    }
  }

}
