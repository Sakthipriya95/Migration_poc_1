/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.creation;


/**
 * Special actions during link creation
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILinkCreationAction {

  /**
   * Copy the link to system clipboard
   *
   * @param creator link creator
   */
  void copyToClipboard(LinkCreator creator);

}
