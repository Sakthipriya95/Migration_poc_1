/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.oslc.resources;

import java.util.Date;

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
 * @author mkl2cob
 */
@OslcNamespace(ConstantsOSLC.ARCHITECTURE_MANAGEMENT_DOMAIN)
@OslcResourceShape(title = "Project Vesion - Resource Shape", describes = ConstantsOSLC.TYPE_PIDC_VERSION)
public class OSLCPIDCVersion extends AbstractResource {


  private String identifier;

  private String name;

  private Date lastConfirmationDate;

  private String version;

  private Date lastValidDate;


  /**
   * @return the name
   */
  @OslcDescription("Last Date where this version was marked up to date")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("Last Confirmation Date")
  @OslcValueType(ValueType.DateTime)
  @OslcPropertyDefinition(ConstantsOSLC.LOCAL_SERVER_NAMESPACE + "lastConfirmationDate")
  public Date getLastConfirmationDate() {
    return this.lastConfirmationDate;
  }

  /**
   * @return the name
   */
  @OslcDescription("Name of the PIDC Version")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("PIDC Version Name")
  @OslcValueType(ValueType.XMLLiteral)
  @OslcPropertyDefinition(OslcConstants.OSLC_CORE_NAMESPACE + "name")
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the pidcVerId
   */
  @OslcDescription("ID of the PIDC Version")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("PIDC Version ID")
  @OslcValueType(ValueType.String)
  @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "identifier")
  public String getIdentifier() {
    return this.identifier;
  }

  /**
   * @param pidcVerId the pidcVerId to set
   */
  public void setIdentifier(final String pidcVerId) {
    this.identifier = pidcVerId;
  }

  /**
   * @param modified the modified to set
   */
  public void setLastConfirmationDate(final Date modified) {
    this.lastConfirmationDate = modified;
  }

  /**
   * @return the version
   */
  @OslcDescription("Version Number of the PIDC Version as String ")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("Version Number")
  @OslcValueType(ValueType.String)
  @OslcPropertyDefinition(ConstantsOSLC.ASSET_MANAGEMENT_NAMESPACE + "version")
  public String getVersion() {
    return this.version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(final String version) {
    this.version = version;
  }

  /**
   * @return the lastValidDate
   */
  @OslcDescription("Last Date where this version will be valid")
  @OslcOccurs(Occurs.ExactlyOne)
  @OslcTitle("Last Valid Date")
  @OslcValueType(ValueType.DateTime)
  @OslcPropertyDefinition(ConstantsOSLC.LOCAL_SERVER_NAMESPACE + "lastValidDate")
  public Date getLastValidDate() {
    return this.lastValidDate;
  }

  /**
   * @param lastValidDate the lastValidDate to set
   */
  public void setLastValidDate(final Date lastValidDate) {
    this.lastValidDate = lastValidDate;
  }

}
