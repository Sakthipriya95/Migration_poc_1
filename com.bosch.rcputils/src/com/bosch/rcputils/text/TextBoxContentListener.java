/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class performs the no of characters left out of total characters and displays it in a label
 * 
 * @author svj7cob
 */
// ICDM-2006 (Parent task : ICDM-1774)
public class TextBoxContentListener implements ModifyListener {

  /**
   * the message label to show the no of characters left
   */
  private Label messageLabel;
  /**
   * maximum length, a user can enter into the text
   */
  private int maxLength;

  /**
   * color of default text for character left
   */
  private Color colorGrey;

  /**
   * display the label
   */
  private final boolean canDisplayTheLabel;

  private TextBoxContentDisplay textBoxContentDisplay;

  /**
   * default constructor to keep control only inside the package
   * 
   * @param messageLabel
   * @param maxLength
   * @param textBoxContentDisplay
   */

  TextBoxContentListener(final boolean canDisplayTheLabel, final Label messageLabel, final int maxLength,
      final Color colorGrey, TextBoxContentDisplay textBoxContentDisplay) {
    this.canDisplayTheLabel = canDisplayTheLabel;
    if (canDisplayTheLabel) {
      this.messageLabel = messageLabel;
      this.maxLength = maxLength;
      this.colorGrey = colorGrey;
      this.textBoxContentDisplay = textBoxContentDisplay;
    }
  }

  /**
   * This method perform the listener activity to count the content of text and display it in a peculiar label below the
   * text
   */
  @Override
  public void modifyText(ModifyEvent event) {
    if (messageLabel != null) {
      messageLabel.setText("");
      if (this.canDisplayTheLabel) {
        // fetch the current comments provided and the actual length in bytes
        String cmt = ((Text) event.widget).getText();
        int count = ((Text) event.widget).getText().getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
        // if the length exceeds the max length then we truncate the length to the max allowed length and add it back to
        // the editor
        if (count > this.maxLength) {
          Text text = textBoxContentDisplay.getText();
          text.setText(truncateTextToMaxLen(cmt));
          count = ((Text) event.widget).getText().getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
          // add the cursor to the end of the text field
          text.setSelection(count);
        }
        int leftCount = this.maxLength - count;
        messageLabel.setText(leftCount + TextBoxContentDisplay.CHAR_LEFT + this.maxLength);
        messageLabel.setForeground(this.colorGrey);
        if (leftCount == 0) {
          // if zero count came, the text to be shown as red color
          messageLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
        }
      }
    }
  }

  /**
   * @param cmt
   * @return
   */
  private String truncateTextToMaxLen(String cmt) {
    while (cmt.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > this.maxLength) {
      cmt = cmt.substring(0, cmt.length() - 1);
    }
    return cmt;
  }

  /**
   * @return if current display is availble, get current display, else default display
   */
  private Display getDisplay() {
    return Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent();
  }

}
