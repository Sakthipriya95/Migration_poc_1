package com.bosch.rcputils.ui.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;


/**
 * @author mga1cob
 */
public final class NumFormatVerifyKeyListener implements VerifyListener, VerifyKeyListener {

  private static final String REGEX = "^[-+]?[0-9]*[.,]?[0-9]{0,15}+$";

  private static final Pattern PATTERN = Pattern.compile(REGEX);

  public void verifyText(VerifyEvent verifyevent) {
    verify(verifyevent);
  }

  public void verifyKey(VerifyEvent verifyevent) {
    verify(verifyevent);
  }

  /**
   * @param event
   */
  private void verify(VerifyEvent event) {
    String string = event.text;
    char[] chars = new char[string.length()];
    string.getChars(0, chars.length, chars, 0);
    Text text = (Text) event.getSource();
    if ((",".equals(string) || ".".equals(string)) && text.getText().indexOf(',') >= 0) {
      event.doit = false;
      return;
    }
    for (int i = 0; i < chars.length; i++) {
      if (!(('0' <= chars[i] && chars[i] <= '9') || chars[i] == '.' || chars[i] == ',' || chars[i] == '-')) {
        event.doit = false;
        return;
      }
    }
    event.text = new String(chars);
    final String oldS = text.getText();
    String newS = oldS.substring(0, event.start) + event.text + oldS.substring(event.end);
    Matcher matcher = PATTERN.matcher(newS);
    if (!matcher.matches()) {
      event.doit = false;
      return;
    }
  }
}
