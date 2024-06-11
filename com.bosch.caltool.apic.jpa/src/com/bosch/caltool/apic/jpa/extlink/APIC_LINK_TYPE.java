/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.extlink;

import com.bosch.calcomp.externallink.ILink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.calcomp.externallink.process.ILinkValidator;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;
import com.bosch.caltool.apic.jpa.bo.PIDCard;

/**
 * External Link types in APIC module
 *
 * @author bne4cob
 */
// ICDM-1649
@Deprecated
public enum APIC_LINK_TYPE implements ILinkType {
                                                 /**
                                                  * PIDC Version link type
                                                  */
                                                 PIDC_VERSION(
                                                              APIC_LINK_TYPE.KEY_PIDC_VERS,
                                                              PIDCVersion.class,
                                                              "PIDC Version") {

                                                   /**
                                                    * {@inheritDoc}
                                                    */
                                                   @Override
                                                   public ILink<?> createNewLink(final ILinkableObject obj) {
                                                     PIDCVersion pidcVers = (PIDCVersion) obj;
                                                     return new PidcVersExternalLink(pidcVers);
                                                   }
                                                 },
                                                 /**
                                                  * PIDC Link type
                                                  */
                                                 PIDC(APIC_LINK_TYPE.KEY_PIDC, PIDCard.class, "PIDC") {

                                                   /**
                                                    * {@inheritDoc}
                                                    */
                                                   @Override
                                                   public ILink createNewLink(final ILinkableObject obj) {
                                                     PIDCard pidc = (PIDCard) obj;
                                                     return new PidcExternalLink(pidc);
                                                   }
                                                 },

                                                 /**
                                                  * PIDC Version link type
                                                  */
                                                 PIDC_VARIANT(
                                                              APIC_LINK_TYPE.KEY_PIDC_VARIANT,
                                                              PIDCVariant.class,
                                                              "PIDC Variant") {

                                                   /**
                                                    * {@inheritDoc}
                                                    */
                                                   @Override
                                                   public ILink<?> createNewLink(final ILinkableObject obj) {
                                                     PIDCVariant pidcVar = (PIDCVariant) obj;
                                                     return new PidcVarExternalLink(pidcVar);
                                                   }
                                                 };

  /**
   * PIDC type Key
   */
  private static final String KEY_PIDC = "pidid";
  /**
   * PIDC Version type Key
   */
  private static final String KEY_PIDC_VERS = "pidvid";
  /**
   * PIDC Version type Key
   */
  private static final String KEY_PIDC_VARIANT = "pidvarid";

  /**
   * Key
   */
  private String key;

  /**
   * Display text of the type
   */
  private String typeDisplayTxt;

  /**
   * Linkable Object's type
   */
  private final Class<?> linkObjectType;

  /**
   * Constructor
   */
  APIC_LINK_TYPE(final String key, final Class<?> linkObjectType, final String typeDisplayTxt) {
    this.key = key;
    this.linkObjectType = linkObjectType;
    this.typeDisplayTxt = typeDisplayTxt;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKey() {
    return this.key;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkValidator getValidator() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getObjectType() {
    return this.linkObjectType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTypeDisplayText() {
    return this.typeDisplayTxt;
  }
}
