/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.creation;


import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.externallink.utils.LinkLoggerUtil;


/**
 * Default action handler. Copies the code to clipboard for eclipse RCP based applications. Sets HTML text and plain
 * text in the clipboard information.
 * <p>
 * HTML Text = display text + url <br>
 * Plain Text = url
 *
 * @author bne4cob
 */
// ICDM-1649
public class DefaultLinkCreationActionHandler implements ILinkCreationAction {

  /**
   * {@inheritDoc}
   */
  @Override
  public void copyToClipboard(final LinkCreator creator) {

    Clipboard clipboard = new Clipboard(Display.getCurrent());

    String url = creator.getUrl();
    String html = creator.getHtmlText();

    HTMLTransfer htmlTransfer = HTMLTransfer.getInstance();
    TextTransfer textTransfer = TextTransfer.getInstance();

    // Set both html and plain texts
    Transfer[] transfers = new Transfer[] { htmlTransfer, textTransfer };
    Object[] data = new Object[] { html, url };
    clipboard.setContents(data, transfers);

    clipboard.dispose();

    LinkLoggerUtil.showInfo("Link '" + url + "' copied to clipboard.");

  }

}
