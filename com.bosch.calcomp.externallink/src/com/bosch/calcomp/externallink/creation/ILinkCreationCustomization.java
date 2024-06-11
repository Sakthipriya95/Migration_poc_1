/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.creation;


/**
 * Any customization in link creator
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILinkCreationCustomization {

  /**
   * @return display text of link
   */
  String getDisplayText();

}
