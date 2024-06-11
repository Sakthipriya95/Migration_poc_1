/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import java.util.HashSet;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;

/**
 * @author bne4cob
 */
// ICDM-1649
public final class LinkInitProperties {

  /**
   * Protocol of external links
   */
  private String protocol;
  /**
   * Logger to be used
   */
  private ILoggerAdapter logger;
  /**
   * Custom action provider (Optional)
   */
  private ILinkActionProvider customActionProvider;
  /**
   * Logging customization to display messages in UI(Optional)
   */
  private ILoggingCustomization logCustomization;
  /**
   * Link info provider
   */
  private ILinkInfoProvider linkInfoProvider = new OldFrameworkLinkInfoProvider();

  /**
   * Link types to register
   */
  private final Set<ILinkType> linkTypeSet = new HashSet<>();

  /**
   * @return the protocol
   */
  public String getProtocol() {
    return this.protocol;
  }

  /**
   * @param protocol the protocol to set
   */
  public void setProtocol(final String protocol) {
    this.protocol = protocol;
  }

  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }

  /**
   * @param logger the logger to set
   */
  public void setLogger(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * @return the customActionProvider
   */
  public ILinkActionProvider getCustomActionProvider() {
    return this.customActionProvider;
  }

  /**
   * @param customActionProvider the customActionProvider to set
   */
  public void setCustomActionProvider(final ILinkActionProvider customActionProvider) {
    this.customActionProvider = customActionProvider;
  }

  /**
   * @return the logCustomization
   */
  public ILoggingCustomization getLogCustomization() {
    return this.logCustomization;
  }

  /**
   * @param logCustomization the logCustomization to set
   */
  public void setLogCustomization(final ILoggingCustomization logCustomization) {
    this.logCustomization = logCustomization;
  }

  /**
   * @return the linkInfoProvider
   */
  public ILinkInfoProvider getLinkInfoProvider() {
    return this.linkInfoProvider;
  }

  /**
   * @param linkInfoProvider the linkInfoProvider to set
   */
  public void setLinkInfoProvider(final ILinkInfoProvider linkInfoProvider) {
    this.linkInfoProvider = linkInfoProvider;
  }

  /**
   * Add link types to register
   *
   * @param types link types
   */
  public void addLinkTypes(final ILinkType... types) {
    for (ILinkType linkType : types) {
      this.linkTypeSet.add(linkType);
    }
  }

  /**
   * @return the linkTypeSet
   */
  Set<ILinkType> getLinkTypeSet() {
    return this.linkTypeSet;
  }

}
