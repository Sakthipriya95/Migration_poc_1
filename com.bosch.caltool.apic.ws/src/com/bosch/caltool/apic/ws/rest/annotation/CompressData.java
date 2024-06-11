/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * Compress the web service response, uses GZIP based compression
 * 
 * @author BNE4COB
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface CompressData {
  // Annotation definition, no content required
}
