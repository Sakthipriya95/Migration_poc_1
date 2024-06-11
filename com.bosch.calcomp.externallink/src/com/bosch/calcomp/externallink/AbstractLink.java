/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Abstract implementation if ILink
 *
 * @author bne4cob
 * @param <O> ILinkableObject
 */
// ICDM-1649
public abstract class AbstractLink<O extends ILinkableObject> implements ILink<O> {

  /**
   * Linkable object
   */
  private final O linkObj;

  /**
   * @param linkObj data object of the link
   */
  public AbstractLink(final O linkObj) {
    this.linkObj = linkObj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUrl() {
    return LinkRegistry.INSTANCE.getProtocolWithSep() + getLinkType().getKey() + ',' + getKeyID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKeyID() {
    return this.linkObj.getID().toString();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDisplayText() {
    return getLinkType().getTypeDisplayText() + ": " + getLinkObject().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> getPropertyKeys() {
    Set<String> propKeys = new HashSet<>();
    propKeys.add(getLinkType().getKey());

    return propKeys;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> getProperties() {
    ConcurrentMap<String, String> propMap = new ConcurrentHashMap<>();
    propMap.put(getLinkType().getKey(), getKeyID());
    return propMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public O getLinkObject() {
    return this.linkObj;
  }

}
