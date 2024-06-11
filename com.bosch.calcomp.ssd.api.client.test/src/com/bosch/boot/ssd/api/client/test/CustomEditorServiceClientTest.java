/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.client.test;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.calcomp.ssd.api.service.client.CustomEditorServiceClient;


/**
 * @author TAB1JA
 *
 */
public class CustomEditorServiceClientTest {

  // Server
  private final String targetUrl = "https://si-cdm01.de.bosch.com:8643/ssdapiservice/customeditorservice";
   
  
  /**
   * 
   */
  @Test
  public void testGetLabelCategory() {
  
  CustomEditorServiceClient client = new CustomEditorServiceClient();
  Set<String> testSet = new HashSet<>();
  testSet.add("InjVlv_tiET_MAP");
  testSet.add("HPUn_stRailPMonRls_C");
  Map<String, String> mapObj = client.getLabelCategory(testSet,targetUrl);
  assertEquals("\"VALUE\"", mapObj.get("\"HPUn_stRailPMonRls_C\""));
  }
}
