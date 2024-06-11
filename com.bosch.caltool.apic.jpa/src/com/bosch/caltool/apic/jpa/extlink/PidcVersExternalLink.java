/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.extlink;

import com.bosch.calcomp.externallink.AbstractLink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.caltool.apic.jpa.bo.PIDCVersion;

/**
 * External link of PIDC Version
 *
 * @author bne4cob
 */
// ICDM-1649
public class PidcVersExternalLink extends AbstractLink<PIDCVersion> {

  /**
   * Creates new external link with the given PIDC Version
   *
   * @param pidcVers PIDC Version
   */
  public PidcVersExternalLink(final PIDCVersion pidcVers) {
    super(pidcVers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDisplayText() {
    return getLinkType().getTypeDisplayText() + ": " + getExtendedName() + getLinkObject().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return APIC_LINK_TYPE.PIDC_VERSION;
  }

  /**
   * @return the extend path, that includes the tree structure of the PIDC
   */
  private String getExtendedName() {

    final PIDCVersion pidcVer = getLinkObject();

    String extendedPidcName;
    if (pidcVer.isActiveVersion()) {
      extendedPidcName = pidcVer.getPidcVersionPath();
    }
    else {
      extendedPidcName = pidcVer.getPidc().getActiveVersion().getPidcVersionPath();
    }
    return extendedPidcName;
  }

}
