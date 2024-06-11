/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.resources;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcOccurs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.annotation.OslcValueType;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import org.eclipse.lyo.oslc4j.core.model.Occurs;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.ValueType;

/**
 * OSLC Resource class that defines the project ID card
 *
 * @author mkl2cob
 */
@OslcNamespace(ConstantsOSLC.ARCHITECTURE_MANAGEMENT_DOMAIN)
@OslcResourceShape(title = "Project ID-Resource Shape", describes = ConstantsOSLC.TYPE_PID_CARD)
public class OSLCPIDCard extends AbstractResource {

  private String title;

  private String identifier;


  /**
   * @return the name
   */
  @OslcDescription("Name of the PIDC")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("PIDC Name")
  @OslcValueType(ValueType.XMLLiteral)
  @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "title")
  public String getTitle() {
    return this.title;
  }

  /**
   * @param name the name to set
   */
  public void setTitle(final String name) {
    this.title = name;
  }

  /**
   * @return the id
   */
  @OslcDescription("ID of the PIDC")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("PIDC ID")
  @OslcValueType(ValueType.String)
  @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "identifier")
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * @param id the id to set
   */
  public void setIdentifier(final String id) {
    this.identifier = id;
  }

}
