/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import com.bosch.calcomp.externallink.creation.ILinkCreationAction;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.ILinkProcessAction;

/**
 * Action provider for link's external actions, including creation of links, opening link etc.
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILinkActionProvider {

  /**
   * Link processing action
   *
   * @param linkType type of link
   * @return processing action
   * @throws ExternalLinkException any exception, while finding process action
   */
  ILinkProcessAction getProcessAction(final ILinkType linkType) throws ExternalLinkException;

  /**
   * Link creation action
   *
   * @param linkType type of link
   * @return creation action
   * @throws ExternalLinkException any exception, while finding creation action
   */
  ILinkCreationAction getCreationAction(final ILinkType linkType) throws ExternalLinkException;

}
