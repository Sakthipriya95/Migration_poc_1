/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.servlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.lyo.oslc4j.client.ServiceProviderRegistryURIs;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.PrefixDefinition;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderFactory;

import com.bosch.caltool.apic.ws.oslc.resources.ConstantsOSLC;
import com.bosch.caltool.apic.ws.oslc.services.OSLCPIDVersionService;


/**
 * @author mkl2cob
 */
public class PIDCServiceProviderFactory {

  private static Class<?>[] RESOURCE_SERVICE_CLASSES = { OSLCPIDVersionService.class };

  /**
   * Constructor
   */
  public PIDCServiceProviderFactory() {
    super();
  }


  /**
   * @param baseURI Base URI
   * @param title String
   * @param parameterValueMap Map<String, Object>
   * @return ServiceProvider
   * @throws OslcCoreApplicationException oslc core application exception
   * @throws URISyntaxException URI Syntax exception
   */
  public static ServiceProvider createPIDCServiceProvider(final String baseURI, final String title,
      final Map<String, Object> parameterValueMap) throws OslcCoreApplicationException, URISyntaxException {
    final ServiceProvider serviceProvider = ServiceProviderFactory.createServiceProvider(baseURI,
        ServiceProviderRegistryURIs.getUIURI(), title, "Service provider for iCDM PIDCard: " + title,
        new Publisher("iCDM", "urn:oslc:ServiceProvider"), RESOURCE_SERVICE_CLASSES, parameterValueMap);

    final PrefixDefinition[] prefixDefinitions = {
        new PrefixDefinition(OslcConstants.DCTERMS_NAMESPACE_PREFIX, new URI(OslcConstants.DCTERMS_NAMESPACE)),
        new PrefixDefinition(OslcConstants.OSLC_CORE_NAMESPACE_PREFIX, new URI(OslcConstants.OSLC_CORE_NAMESPACE)),
        new PrefixDefinition(OslcConstants.OSLC_DATA_NAMESPACE_PREFIX, new URI(OslcConstants.OSLC_DATA_NAMESPACE)),
        new PrefixDefinition(OslcConstants.RDF_NAMESPACE_PREFIX, new URI(OslcConstants.RDF_NAMESPACE)),
        new PrefixDefinition(ConstantsOSLC.ARCHITECTURE_MANAGEMENT_PREFIX,
            new URI(ConstantsOSLC.ARCHITECTURE_MANAGEMENT_NAMESPACE)),
        new PrefixDefinition(ConstantsOSLC.QUALITY_MANAGEMENT_PREFIX,
            new URI(ConstantsOSLC.QUALITY_MANAGEMENT_NAMESPACE)),
        new PrefixDefinition(ConstantsOSLC.CHANGE_MANAGEMENT_PREFIX,
            new URI(ConstantsOSLC.CHANGE_MANAGEMENT_NAMESPACE)),
        new PrefixDefinition(ConstantsOSLC.ASSET_MANAGEMENT_PREFIX, new URI(ConstantsOSLC.ASSET_MANAGEMENT_NAMESPACE)),
        new PrefixDefinition(OslcConstants.RDFS_NAMESPACE_PREFIX, new URI(OslcConstants.RDFS_NAMESPACE)) };

    serviceProvider.setPrefixDefinitions(prefixDefinitions);

    return serviceProvider;
  }
}
