/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.extlink;

import com.bosch.calcomp.externallink.AbstractLink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.caltool.apic.jpa.bo.PIDCard;

/**
 * External link of PID Card. This opens active version of the projectid card. This supports backward compactibility of
 * opening links from other tools(PSR-C..) who still use "pidid" as format to open pidc links.
 *
 * @author bne4cob
 */
//ICDM-1649
public class PidcExternalLink extends AbstractLink<PIDCard> {

  /**
   * Creates new external link with the given PID Card
   *
   * @param pidc PID Card
   */
  public PidcExternalLink(final PIDCard pidc) {
    super(pidc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return APIC_LINK_TYPE.PIDC;
  }

}
