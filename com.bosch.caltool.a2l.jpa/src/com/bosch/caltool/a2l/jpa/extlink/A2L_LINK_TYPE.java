/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.extlink;

import com.bosch.calcomp.externallink.ILink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.calcomp.externallink.process.ILinkValidator;
import com.bosch.caltool.apic.jpa.bo.A2LFile;

/**
 * External Link types in A2L module
 *
 * @author bne4cob
 */
// ICDM-1649
@Deprecated
public enum A2L_LINK_TYPE implements ILinkType {
                                                /**
                                                 * A2L File
                                                 */
                                                A2L_FILE(A2L_LINK_TYPE.KEY_A2L_FILE, A2LFile.class, "A2L File") {

                                                  /**
                                                   * {@inheritDoc}
                                                   */
                                                  @Override
                                                  public ILink createNewLink(final ILinkableObject obj) {
                                                    A2LFile a2lfile = (A2LFile) obj;
                                                    return new A2LFileExternalLink(a2lfile);
                                                  }


                                                };


  /**
   * A2L file type Key
   */
  private static final String KEY_A2L_FILE = "a2lid";

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
  private A2L_LINK_TYPE(final String key, final Class<?> linkObjectType, final String typeDisplayTxt) {
    this.key = key;
    this.linkObjectType = linkObjectType;
    this.typeDisplayTxt = typeDisplayTxt;
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
  public String getName() {
    return name();
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
