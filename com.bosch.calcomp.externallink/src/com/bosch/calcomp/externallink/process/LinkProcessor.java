/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.process;

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.externallink.ILinkType;
import com.bosch.calcomp.externallink.LinkRegistry;
import com.bosch.calcomp.externallink.exception.ExternalLinkException;
import com.bosch.calcomp.externallink.utils.LinkLoggerUtil;
import com.bosch.calcomp.externallink.utils.LinkUtils;

/**
 * Process the external link. Validates the link and opens it
 *
 * @author bne4cob
 */
// ICDM-1649
public class LinkProcessor {

  /**
   * Error - Invalid hyperlink format
   */
  private static final String ERR_INVALID_HYPERLINK = "Invalid format of iCDM hyperlink";

  /**
   * Defines constant for arg size
   */
  private static final int CMD_ARGS_SIZE = 2;


  /**
   * Defines constant for arg size when the -WMODE arg is passed
   */
  private static final int CMD_ARGS_SIZE_WITH_WMODE = 4;


  /**
   * Defines constant for arg size
   */
  private static final int CMD_ARGS_WITH_VAR = 3;
  /**
   * Defines constant for arg size when the -WMODE arg is passed
   */
  private static final int CMD_ARGS_WITH_VAR_WITH_WMODE = 5;

  /**
   * link text
   */
  private final String linkText;

  /**
   * type of link identified from the link text
   */
  private ILinkType linkType;

  /**
   * properties and values identified from link text
   */
  private final ConcurrentMap<String, String> linkProperties = new ConcurrentHashMap<>();


  /**
   * Constructor
   *
   * @param linkText link Text - URL
   */
  public LinkProcessor(final String linkText) {
    super();
    this.linkText = linkText;
  }


  /**
   * Validates the external link text
   *
   * @throws ExternalLinkException if link is invalid
   */
  public void validateLink() throws ExternalLinkException {
    decodeUrl();
    ILinkValidator validator = this.linkType.getValidator();
    if (validator != null) {
      validator.validate(this.linkText);
    }
  }


  /**
   * Opens the external link. Also validates the link. Uses the ILinkActionProvider.openLink() to open the link
   *
   * @return true, if link is opened successfully
   * @throws ExternalLinkException if link is not valid or, unable to open the link
   */
  public boolean openLink() throws ExternalLinkException {
    validateLink();
    ILinkProcessAction linkAction = LinkRegistry.INSTANCE.getProcessAction(this.linkType);
    return linkAction.openLink(this.linkProperties);
  }


  /**
   * Decode the URL text
   *
   * @throws ExternalLinkException if link is not valid
   */
  private void decodeUrl() throws ExternalLinkException {
    String inputLine = this.linkText;
    getLogger().info("Input arguments from External Link {}", inputLine);

    if (LinkUtils.isEmptyString(inputLine)) {
      throw new ExternalLinkException(ERR_INVALID_HYPERLINK);
    }

    String protocolWithSep = LinkRegistry.INSTANCE.getProtocolWithSep();
    if (inputLine.startsWith(protocolWithSep)) {
      inputLine = inputLine.substring(protocolWithSep.length());
    }
    if (LinkUtils.isEmptyString(inputLine)) {
      throw new ExternalLinkException(ERR_INVALID_HYPERLINK);
    }

    decodeProperties(inputLine);

    decodeLinkType();

  }


  /**
   * Decodes properties in url by parsing the text
   *
   * @param linkTxt input line after basic validation
   * @throws ExternalLinkException if link text is invalid
   */
  private void decodeProperties(final String linkTxt) throws ExternalLinkException {
    StringTokenizer strTknzer = new StringTokenizer(linkTxt, "&");
    String nvp;
    String[] nvpSplit;

    while (strTknzer.hasMoreTokens()) {
      nvp = strTknzer.nextToken();
      nvpSplit = nvp.split(",");
      if ((nvpSplit == null) || checkLength(nvpSplit)) {
        throw new ExternalLinkException(ERR_INVALID_HYPERLINK);
      }
      this.linkProperties.put(nvpSplit[0].toLowerCase(Locale.getDefault()), nvpSplit[1]);

    }
  }


  /**
   * @param nvpSplit
   * @return
   */
  private boolean checkLength(final String[] nvpSplit) {
    return (nvpSplit.length != CMD_ARGS_SIZE) && (nvpSplit.length != CMD_ARGS_SIZE_WITH_WMODE) &&
        isVarLengthValid(nvpSplit);
  }

  /**
   * @param nvpSplit
   * @return
   */
  private boolean isVarLengthValid(final String[] nvpSplit) {
    return (nvpSplit.length != CMD_ARGS_WITH_VAR) && (nvpSplit.length != CMD_ARGS_WITH_VAR_WITH_WMODE);
  }

  /**
   * Finds and sets the link type with the url inputs
   *
   * @throws ExternalLinkException if link type was not found
   */
  private void decodeLinkType() throws ExternalLinkException {

    for (String key : this.linkProperties.keySet()) {
      // supported formats = <key> OR -<key>
      // e.g. 'pidvid' or '-pidvid'
      ILinkType type = LinkRegistry.INSTANCE.getLinkTypeForKey(key);
      if (type != null) {
        this.linkType = type;
        break;
      }
    }

    // Link type not found
    if (this.linkType == null) {
      throw new ExternalLinkException(ERR_INVALID_HYPERLINK);
    }
  }

  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return LinkLoggerUtil.getLogger();
  }


  /**
   * @return the linkProperties
   */
  public ConcurrentMap<String, String> getLinkProperties() {
    return this.linkProperties;
  }


}
