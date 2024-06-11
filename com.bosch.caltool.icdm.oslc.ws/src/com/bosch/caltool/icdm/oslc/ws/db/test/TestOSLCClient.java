/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.oslc.ws.db.test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.wink.client.ClientResponse;
import org.eclipse.lyo.oslc4j.client.OslcRestClient;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

import com.bosch.caltool.apic.ws.oslc.resources.OSLCPIDCVersion;

/**
 * @author mkl2cob
 */
public class TestOSLCClient {

  private static final String MEDIA_TYPE = OslcMediaType.APPLICATION_RDF_XML;
  private static final int timeout = 15000;


  private static final String queryBase =
      "http://si-cdm02.de.bosch.com:8980/com.bosch.caltool.icdm.oslc.ws/pidcVersion/";

  public static void main(final String[] args) {

    // Init
    Set<Class<?>> providers = new HashSet<Class<?>>();
    providers.addAll(JenaProvidersRegistry.getProviders());
    providers.addAll(Json4JProvidersRegistry.getProviders());


    // Get one element
    final String id = "1447027081";
    final String resourceBase = queryBase + id;
    OslcRestClient oslcRestClient = new OslcRestClient(providers, resourceBase, MEDIA_TYPE, timeout);


    // First retrieve
    final OSLCPIDCVersion updateResource = oslcRestClient.getOslcResource(OSLCPIDCVersion.class);
    // Do something with resource
    updateResource.setLastConfirmationDate(Calendar.getInstance().getTime());

    // uncomment before executing main method
    // oslcRestClient.getClientResource().header("userName", "mkl2cob")
    // oslcRestClient.getClientResource().header("passWord", "ICDM_PWD")

    // Update resource
    ClientResponse clientResponse = oslcRestClient.updateOslcResourceReturnClientResponse(updateResource);
    OSLCPIDCVersion entity = clientResponse.getEntity(OSLCPIDCVersion.class);
    System.out.println("Last Confirmation Date : " + entity.getLastConfirmationDate());


  }
}
