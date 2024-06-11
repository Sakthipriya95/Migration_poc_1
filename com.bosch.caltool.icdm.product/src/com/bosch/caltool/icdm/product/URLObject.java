/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product;

import com.bosch.calcomp.externallink.ILinkType;

/**
 * URLObject handles objects where external hyperlinks from iCDM needs to be provided
 *
 * @author adn1cob
 */
// iCDM-1241
public class URLObject {

  /**
   * URL ID
   */
  private final String urlID;

  /**
   * object type of this object
   */
  private final ILinkType type;

  /**
   * Constructor
   *
   * @param type object type
   * @param uriID uriID
   */
  public URLObject(final ILinkType type, final String uriID) {
    super();
    this.type = type;
    this.urlID = uriID;

  }

  /**
   * @return the urlID
   */
  public String getUrlID() {
    return this.urlID;
  }

  /**
   * @return the type
   */
  public ILinkType getType() {
    return this.type;
  }

  /**
   * @return the URL as a string
   */
  // ICDM-1649
  public String getURLAsString() {
    return this.type.getKey() + "," + this.urlID;
  }

}
