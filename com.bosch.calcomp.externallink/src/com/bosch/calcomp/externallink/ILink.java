/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import java.util.Map;
import java.util.Set;

/**
 * Link
 *
 * @author bne4cob
 * @param <O> linkable object
 */
// ICDM-1649
public interface ILink<O extends ILinkableObject> {

  /**
   * @return link type
   */
  ILinkType getLinkType();

  /**
   * @return get the ID value for the link type's key
   */
  String getKeyID();

  /**
   * @return the linkable object, for which the link is created
   */
  ILinkableObject getLinkObject();

  /**
   * @return the link URL
   */
  String getUrl();

  /**
   * @return the display text of link url
   */
  String getDisplayText();

  /**
   * @return property keys
   */
  Set<String> getPropertyKeys();

  /**
   * @return properties of the link, and their values, used in the URL
   */
  Map<String, String> getProperties();


}
