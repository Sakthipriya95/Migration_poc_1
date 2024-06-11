/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.extlink;

import com.bosch.calcomp.externallink.AbstractLink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.caltool.cdr.jpa.bo.CDRResult;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * External Link of CDR Result
 *
 * @author bne4cob
 */
// ICDM-1649
public class CdrResultExternalLink extends AbstractLink<CDRResult> {

  /**
   * Creates a new external link with the given CDR Result
   *
   * @param result CDRResult
   */
  public CdrResultExternalLink(final CDRResult result) {
    super(result);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return CDR_LINK_TYPE.CDR_RESULT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKeyID() {

    CDRResult result = getLinkObject();
    Long cdrID = result.getID();
    Long pidcId = result.getPidcVersion().getID();

    StringBuilder keyID = new StringBuilder();
    keyID.append(cdrID).append('-').append(pidcId);
    if (CommonUtils.isNotNull(result.getLinkedVar())) {
      keyID.append('-').append(result.getLinkedVar().getVariantID());
    }

    return keyID.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDisplayText() {
    return getLinkType().getTypeDisplayText() + ": " + getLinkObject().getExtendedPath() + getLinkObject().getName();
  }

}
