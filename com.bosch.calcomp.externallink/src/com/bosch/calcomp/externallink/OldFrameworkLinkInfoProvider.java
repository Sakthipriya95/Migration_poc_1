/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import java.util.Map;

import com.bosch.calcomp.externallink.exception.ExternalLinkRuntimeException;

/**
 * This class is deprecated and currently not in use.
 *
 * @deprecated
 */
/**
 * @author bne4cob
 */
@Deprecated
public class OldFrameworkLinkInfoProvider implements ILinkInfoProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  public LinkInfo getLinkInfo(final Object obj) {
//  deprecated method
    ILinkType linkType = findType(obj);
    ILink<?> link = linkType.createNewLink((ILinkableObject) obj);

    LinkInfo linkInfo = new LinkInfo();
    linkInfo.setDisplayText(link.getDisplayText());
    linkInfo.setUrl(link.getUrl());

    return linkInfo;


  }

  /**
   * Identifiy the link type
   *
   * @param obj
   * @return
   */
  private ILinkType findType(final Object obj) {
//  deprecated method
    ILinkType linkType = null;
    for (ILinkType type : LinkRegistry.INSTANCE.getAllLinkTypes()) {
      if (obj.getClass() == type.getObjectType()) {
        linkType = type;
        break;
      }
    }

    if (linkType == null) {
      throw new ExternalLinkRuntimeException("Failed to resolve link type");
    }

    return linkType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ILinkType getLinkType(final Object obj) {
//    deprecated method
    return findType(obj);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public LinkInfo getLinkInfo(final Object linkable, final Map<String, String> additionaDetails) {
    // Do nothing
    return null;
  }

}
