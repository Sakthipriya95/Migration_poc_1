/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.extlink;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.bosch.calcomp.externallink.ILink;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;


/**
 * @author BRU2COB
 */
public class CDRFuncParamExternalLink implements ILink {

  /**
   * @param cdrRes
   */
  public CDRFuncParamExternalLink(final CDRFuncParameter cdrRes) {
    // TODO Auto-generated constructor stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType() {
    return CDR_LINK_TYPE.CDR_FUNCTION_PARAM;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKeyID() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkableObject getLinkObject() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUrl() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDisplayText() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set getPropertyKeys() {
    return Collections.emptySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map getProperties() {
    // TODO Auto-generated method stub
    return null;
  }

}
