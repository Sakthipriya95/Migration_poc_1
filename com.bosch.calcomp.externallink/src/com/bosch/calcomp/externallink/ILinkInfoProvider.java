/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import java.util.Map;

/**
 * @author bne4cob
 */
public interface ILinkInfoProvider {

  /**
   * @param linkable linkable object
   * @return Link information
   */
  LinkInfo getLinkInfo(Object linkable);


  /**
   * @param linkable object to which the model object is resolved to
   * @param additionalDetails extra details to create the link
   * @return Link information
   */
  LinkInfo getLinkInfo(final Object linkable, final Map<String, String> additionalDetails);

  /**
   * @param linkable linkable object
   * @return link type
   */
  ILinkType getLinkType(Object linkable);
}
