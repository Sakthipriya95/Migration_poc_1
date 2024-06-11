/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.fileeditor.client;

import java.util.HashMap;
import java.util.HashSet;
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


/**
 * @author SSN9COB
 */
public class CustomEditorServiceClient {

  private final String targetUrl = "http://si-cdm01.de.bosch.com:8780/ssdapi/rest/customeditorservice";
//  private final String targetUrl = "http://localhost:8090/ssdapi/rest/customeditorservice";

  /**
   * @param uniqueLabelNameSet inputSet
   * @return map
   */
  public Map<String, String> getLabelCategory(final Set<String> uniqueLabelNameSet) {


    Client client = ClientBuilder.newClient();

    WebTarget target = client.target(this.targetUrl).path("getLabelCategory");
    MultivaluedMap<String, String> multiValuedMap = new MultivaluedHashMap<>();
    int id = 1;
    for (String name : uniqueLabelNameSet) {
      multiValuedMap.add(String.valueOf(id++), name);
    }
    Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
        .post(Entity.entity(multiValuedMap, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
    if ((response != null) && (response.getStatus() == 200)) { // if status ok
      String jsonStr = response.readEntity(String.class);
      jsonStr = jsonStr.substring(1, jsonStr.length() - 1); // remove curly brackets
      String[] keyValuePairs = jsonStr.split(","); // split the string to creat key-value pairs
      Map<String, String> map = new HashMap<>();

      for (String pair : keyValuePairs) // iterate over the pairs
      {
        String[] entry = pair.split("="); // split the pairs to get key and value
        map.put(entry[0].trim(), entry[1].trim()); // add them to the hashmap and trim whitespaces
      }
      return map;
    }
    return new HashMap<>();

  }

  /**
   * @param args test
   */
  public static void main(final String[] args) {

    CustomEditorServiceClient client = new CustomEditorServiceClient();
    Set<String> testSet = new HashSet<>();
    testSet.add("InjVlv_tiET_MAP");
    testSet.add("HPUn_stRailPMonRls_C");
    Map<String, String> mapObj = client.getLabelCategory(testSet);
    System.out.println(mapObj);

  }
}
