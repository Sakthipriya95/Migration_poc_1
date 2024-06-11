/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.extlink;

import com.bosch.calcomp.externallink.AbstractLink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.caltool.cdr.jpa.bo.RuleSet;


/**
 * External Link of Rule Set
 * 
 * @author bne4cob
 */
//ICDM-1649
public class RuleSetExternalLink extends AbstractLink<RuleSet> {

  /**
   * Creates a new external link with the given Rule Set
   * 
   * @param ruleset Rule Set
   */
  public RuleSetExternalLink(final RuleSet ruleset) {
    super(ruleset);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return CDR_LINK_TYPE.RULE_SET;
  }


}
