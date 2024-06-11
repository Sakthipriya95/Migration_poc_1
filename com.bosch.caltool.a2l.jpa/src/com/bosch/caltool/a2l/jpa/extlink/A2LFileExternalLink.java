/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.extlink;

import com.bosch.calcomp.externallink.AbstractLink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.caltool.apic.jpa.bo.A2LFile;


/**
 * External link of A2L file
 *
 * @author bne4cob
 */
// ICDM-1649
public class A2LFileExternalLink extends AbstractLink<A2LFile> {

  /**
   * Creates new external link with the given A2L file
   *
   * @param a2lfile A2L File
   */
  public A2LFileExternalLink(final A2LFile a2lfile) {
    super(a2lfile);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return A2L_LINK_TYPE.A2L_FILE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKeyID() {
    String a2lFileID = getLinkObject().getID().toString();
    Long pidcA2lId = getLinkObject().getPidcA2l().getID();

    return pidcA2lId + "-" + a2lFileID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDisplayText() {
    return getLinkType().getTypeDisplayText() + ": " + getLinkObject().getExtendedPath() + getLinkObject().getName();
  }

}
