/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.bosch.calcomp.externallink.creation.ILinkCreationAction;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.ILinkProcessAction;

/**
 * Action provider - eclipse's extension point based. Extension point name is LINK_ACTION_EXT_PT
 *
 * @author bne4cob
 */
// ICDM-1649
public class EclipseExtPtBasedLinkActionProvider implements ILinkActionProvider {

  /**
   * Extension point name
   */
  private static final String LINK_ACTION_EXT_PT = "com.bosch.calcomp.externallink.action";

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkProcessAction getProcessAction(final ILinkType linkType) throws ExternalLinkException {
    IConfigurationElement config = getExternalLinkExtPtConfig(linkType);
    try {
      return (ILinkProcessAction) config.createExecutableExtension("process");
    }
    catch (CoreException exp) {
      throw new ExternalLinkException("Failed to load extension point", exp);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkCreationAction getCreationAction(final ILinkType linkType) throws ExternalLinkException {
    IConfigurationElement config = getExternalLinkExtPtConfig(linkType);
    try {
      return (ILinkCreationAction) config.createExecutableExtension("creation");
    }
    catch (CoreException exp) {
      throw new ExternalLinkException("Failed to load extension point", exp);
    }
  }

  /**
   * Fetch the configuration element of the extension point's extension for the given link type
   *
   * @param linkType
   * @return configuration element
   * @throws ExternalLinkException if finding fails
   */
  private IConfigurationElement getExternalLinkExtPtConfig(final ILinkType linkType) throws ExternalLinkException {
    IConfigurationElement retConfig = null;
    ILinkType extPtLinkType;

    final IConfigurationElement[] configs =
        Platform.getExtensionRegistry().getConfigurationElementsFor(LINK_ACTION_EXT_PT);

    for (IConfigurationElement config : configs) {
      // find the type configured in the extension, and compare with the input link type
      extPtLinkType = LinkRegistry.INSTANCE.getLinkType(config.getAttribute("type"));
      if (linkType.equals(extPtLinkType)) {
        retConfig = config;
        break;
      }
    }
    // Action not configured for the external link type
    if (retConfig == null) {
      throw new ExternalLinkException("Action not found for this external link");
    }
    return retConfig;

  }

}
