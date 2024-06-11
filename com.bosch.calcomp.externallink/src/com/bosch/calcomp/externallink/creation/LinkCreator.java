/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.creation;

import java.util.Map;

import com.bosch.calcomp.externallink.ILinkInfoProvider;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.LinkInfo;
import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;

/**
 * Link creator. Creates the link. Can provide URL, display text etc.
 *
 * @author bne4cob
 */
// ICDM-1649
public class LinkCreator {

  /**
   * Type of link identified from linkable object
   */
  private final ILinkType linkType;

  /**
   * Link info
   */
  private final LinkInfo linkInfo;

  /**
   * Customization, if any, during link creation.
   */
  private ILinkCreationCustomization customization;

  /**
   * Constructor
   *
   * @param obj linkable object
   */
  public LinkCreator(final Object obj) {
    ILinkInfoProvider linkInfoProvider = LinkRegistry.INSTANCE.getLinkInfoProvider();
    this.linkInfo = linkInfoProvider.getLinkInfo(obj);
    this.linkType = linkInfoProvider.getLinkType(obj);
  }

  /**
   * Constructor
   *
   * @param obj linkable object
   * @param additionalDetails containing extra details to create the link
   */
  public LinkCreator(final Object obj, final Map<String, String> additionalDetails) {
    ILinkInfoProvider linkInfoProvider = LinkRegistry.INSTANCE.getLinkInfoProvider();
    this.linkInfo = linkInfoProvider.getLinkInfo(obj, additionalDetails);
    this.linkType = linkInfoProvider.getLinkType(obj);
  }

  /**
   * Link's URL
   *
   * @return URL as string
   */
  public String getUrl() {
    return this.linkInfo.getUrl();
  }

  /**
   * @return display text for URL
   */
  public String getDisplayText() {
    return (this.customization == null) || (this.customization.getDisplayText() == null)
        ? this.linkInfo.getDisplayText() : this.customization.getDisplayText();
  }

  /**
   * @return URL and display text in html format
   */
  public String getHtmlText() {
    return "<a href=\"" + getUrl() + "\">" + getDisplayText() + "</a>";
  }

  /**
   * Copy the link to clipboard
   *
   * @throws ExternalLinkException if copying fails
   */
  public void copyToClipBoard() throws ExternalLinkException {
    ILinkCreationAction clipboardCopier = LinkRegistry.INSTANCE.getCreationAction(getLinkType());
    if (clipboardCopier != null) {
      clipboardCopier.copyToClipboard(this);
    }
  }

  /**
   * Sets the customization
   *
   * @param customization the customization to set
   */
  public void setCustomization(final ILinkCreationCustomization customization) {
    this.customization = customization;
  }

  /**
   * @return the linkType
   */
  public ILinkType getLinkType() {
    return this.linkType;
  }
}
