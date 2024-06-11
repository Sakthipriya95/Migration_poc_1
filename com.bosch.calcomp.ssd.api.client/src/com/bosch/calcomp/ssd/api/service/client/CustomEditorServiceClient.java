/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.service.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


/**
 * @author SSN9COB
 */
public class CustomEditorServiceClient {

  /**
   * @param uniqueLabelNameSet inputSet
   * @param targetUrl2
   * @return map
   */
  public Map<String, String> getLabelCategory(final Set<String> uniqueLabelNameSet, final String targetUrl) {

    try {
      Client client = ClientBuilder.newBuilder().build();

      WebTarget target = client.target(targetUrl).path("getLabelCategory");
      MultivaluedMap<String, String> multiValuedMap = new MultivaluedHashMap<>();
      ObjectMapper mapper = new ObjectMapper();
      ArrayNode jsonArray = mapper.createArrayNode();
      int id = 1;
      for (String name : uniqueLabelNameSet) {
        multiValuedMap.add(String.valueOf(id++), name);
        jsonArray.add(name);
      }
      Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
          .post(Entity.entity(jsonArray.toString(), MediaType.valueOf("application/json")));
      if ((response != null) && (response.getStatus() == 200)) { // if status ok
        String resultStr = response.readEntity(String.class);
        JsonNode jsonNode = mapper.readTree(resultStr);
        String jsonStr = jsonNode.get("result").asText();
        jsonStr = jsonStr.substring(1, jsonStr.length() - 1); // remove curly brackets
        String[] keyValuePairs = jsonStr.split(","); // split the string to creat key-value pairs
        Map<String, String> map = new HashMap<>();

        for (String pair : keyValuePairs) // iterate over the pairs
        {
          String[] entry = pair.split(":"); // split the pairs to get key and value
          map.put(entry[0].trim(), entry[1].trim()); // add them to the hashmap and trim whitespaces
        }
        return map;
      }
      return new HashMap<>();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }


}
