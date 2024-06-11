/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.process;

import java.util.Map;

/**
 * Action handler to process an external link such as opening the link
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILinkProcessAction {

  /**
   * Opens the external link
   *
   * @param properties properties and values obtained from link URL
   * @return true if the link is opened
   */
  boolean openLink(Map<String, String> properties);

}
