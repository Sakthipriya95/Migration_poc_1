/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.extlink;

import com.bosch.calcomp.externallink.AbstractLink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.caltool.apic.jpa.bo.PIDCVariant;


/**
 * @author dja7cob
 */
public class PidcVarExternalLink extends AbstractLink<PIDCVariant> {

  /**
   * @param pidcVar
   */
  public PidcVarExternalLink(final PIDCVariant pidcVar) {
    super(pidcVar);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return APIC_LINK_TYPE.PIDC_VARIANT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDisplayText() {
    // Display text for the external link
    return getLinkType().getTypeDisplayText() + ": " + getExtendedName() + getLinkObject().getPidcVersion().getName() +
        "->" + getLinkObject().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKeyID() {

    // Key ID to construct the link
    final PIDCVariant pidcVar = getLinkObject();
    Long varID = pidcVar.getID();
    Long pidcId = pidcVar.getPidcVersion().getID();

    StringBuilder keyID = new StringBuilder();
    keyID.append(pidcId).append('-').append(varID);

    return keyID.toString();
  }

  /**
   * @return the extend path, that includes the tree structure of the PIDC
   */
  private String getExtendedName() {

    // Extended name of the variant
    // Constructed fromt he tree elments
    // Structure attrs -> PIDC ver --> PIDC avr
    final PIDCVariant pidcVar = getLinkObject();

    StringBuilder extendedPidcVarName = new StringBuilder();
    if (pidcVar.getPidcVersion().isActiveVersion()) {
      extendedPidcVarName.append(pidcVar.getPidcVersion().getPidcVersionPath());

    }
    else {
      extendedPidcVarName.append(pidcVar.getPidcVersion().getPidc().getActiveVersion().getPidcVersionPath());
    }
    return extendedPidcVarName.toString();
  }
}
