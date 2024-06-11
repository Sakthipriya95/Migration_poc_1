/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.applications;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.lyo.oslc4j.application.OslcWinkApplication;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

import com.bosch.caltool.apic.ws.oslc.resources.ConstantsOSLC;
import com.bosch.caltool.apic.ws.oslc.resources.OSLCPIDCVersion;
import com.bosch.caltool.apic.ws.oslc.services.OSLCPIDVersionService;


/**
 * @author MKL2COB
 */
public class ICDMOSLCWinkApplication extends OslcWinkApplication {

  private static final Set<Class<?>> RESOURCE_CLASSES = new HashSet<Class<?>>();
  private static final Map<String, Class<?>> RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP =
      new HashMap<String, Class<?>>();

  static {
    RESOURCE_CLASSES.addAll(JenaProvidersRegistry.getProviders());
    RESOURCE_CLASSES.addAll(Json4JProvidersRegistry.getProviders());
    RESOURCE_CLASSES.add(OSLCPIDVersionService.class);
    RESOURCE_CLASSES.add(OSLCPIDCVersion.class);
    RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(ConstantsOSLC.PATH_PIDC_VERSION_RESOURCE, OSLCPIDCVersion.class);

  }

  /**
   * @throws OslcCoreApplicationException OSLC core App exp
   * @throws URISyntaxException URI syntax exception
   */
  public ICDMOSLCWinkApplication() throws OslcCoreApplicationException, URISyntaxException {
    super(RESOURCE_CLASSES, OslcConstants.PATH_RESOURCE_SHAPES, RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP);
  }

}
