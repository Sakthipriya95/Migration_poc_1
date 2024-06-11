/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;


/**
 * Data object for which external links are being created
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILinkableObject {

  /**
   * @return ID of the object, to be placed in the Link URL
   */
  Object getID();

  /**
   * Default display text of the URL
   *
   * @return display text
   */
  String getName();
}
