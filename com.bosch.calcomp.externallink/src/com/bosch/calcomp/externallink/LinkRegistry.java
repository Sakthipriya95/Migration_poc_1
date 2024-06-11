/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.externallink.creation.ILinkCreationAction;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.process.ILinkProcessAction;
import com.bosch.calcomp.externallink.utils.LinkLoggerUtil;

/**
 * Link registry
 *
 * @author bne4cob
 */
public enum LinkRegistry {
                          /**
                           * Singleton instance
                           */
                          INSTANCE;


  /**
   * Link types registry. Key - Link type name, value - Link type
   */
  private final ConcurrentMap<String, ILinkType> linkTypeMap = new ConcurrentHashMap<>();

  /**
   * Protocol of external links
   */
  private String protocol;

  /**
   * Action provider
   */
  private ILinkActionProvider actionProvider;

  /**
   * Link info provider
   */
  private ILinkInfoProvider linkInfoProvider;


  /**
   * Initializes the registry with the given properties.
   * <p>
   * Points to consider : <br>
   * 1. protocol - mandatory<br>
   * 2. logger - mandatory<br>
   * 3. actionProvider - optional. If not available, eclipse extension point based action provider is used . <br>
   * 4. loggingCustomization - optional. If not available, logger is used for logging messages being displayed in UI.
   *
   * @param props properties
   */
  public void initialize(final LinkInitProperties props) {

    LinkLoggerUtil.setLogger(props.getLogger());
    LinkLoggerUtil.setLoggingCustomization(props.getLogCustomization());

    this.protocol = props.getProtocol().trim().toLowerCase(Locale.getDefault());

    if (props.getCustomActionProvider() == null) {
      this.actionProvider = new EclipseExtPtBasedLinkActionProvider();
    }
    else {
      this.actionProvider = props.getCustomActionProvider();
    }

    this.linkInfoProvider = props.getLinkInfoProvider();

    registerLinkTypes(props.getLinkTypeSet().toArray(new ILinkType[props.getLinkTypeSet().size()]));

  }

  /**
   * Resister link types.
   *
   * @param linkTypes link types
   */
  private void registerLinkTypes(final ILinkType... linkTypes) {
    for (ILinkType linkType : linkTypes) {
      this.linkTypeMap.put(linkType.getName(), linkType);
    }
  }

  /**
   * Retrieves the link type with the given name
   *
   * @param typeName type name identified by ILinkType.getName()
   * @return ILinkType
   */
  public ILinkType getLinkType(final String typeName) {
    return this.linkTypeMap.get(typeName);
  }

  /**
   * Retrieves the link type with the given link type key
   *
   * @param linkTypeKey type name identified by ILinkType.getName()
   * @return ILinkType
   */
  public ILinkType getLinkTypeForKey(final String linkTypeKey) {
    ILinkType ret = null;
    for (ILinkType type : this.linkTypeMap.values()) {
      if (type.getKey().equalsIgnoreCase(linkTypeKey) || ('-' + type.getKey()).equalsIgnoreCase(linkTypeKey)) {
        ret = type;
      }
    }
    return ret;
  }


  /**
   * @return all the registered link types
   */
  public Set<ILinkType> getAllLinkTypes() {
    return new HashSet<>(this.linkTypeMap.values());
  }


  /**
   * @return the protocol
   */
  public String getProtocol() {
    return this.protocol;
  }

  /**
   * @return the protocol
   */
  public String getProtocolWithSep() {
    return this.protocol + ":";
  }

  /**
   * Provides the process action handler for the given link type
   *
   * @param linkType link type
   * @return action handler
   * @throws ExternalLinkException when finding handler creates error
   */
  public ILinkProcessAction getProcessAction(final ILinkType linkType) throws ExternalLinkException {
    return this.actionProvider.getProcessAction(linkType);
  }

  /**
   * Provides the creation action handler for the given link type, to perform additional tasks
   *
   * @param linkType link type
   * @return action handler
   * @throws ExternalLinkException when finding handler creates error
   */
  public ILinkCreationAction getCreationAction(final ILinkType linkType) throws ExternalLinkException {
    return this.actionProvider.getCreationAction(linkType);
  }

  /**
   * @return the linkInfoProvider
   */
  public ILinkInfoProvider getLinkInfoProvider() {
    return this.linkInfoProvider;
  }


}
