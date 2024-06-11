package com.bosch.rcputils.ui.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import com.bosch.rcputils.IUtilityConstants;


/**
 * @author mga1cob
 */
public final class NumValVerifyKeyListener implements VerifyListener, VerifyKeyListener {


  /**
   * CONSTANT for "]?[\\d]{0,"
   */
  private static final String REGEX_1 = "]?[\\d]{0,";

  /**
   * Defines
   */
  private String regex;

  /**
   * 
   */
  private Pattern pattern;

  /**
   * Defines attribute format style
   */
  private final String format;

  /**
   * @param format defines attribute format style
   */
  public NumValVerifyKeyListener(String format) {
    this.format = format;
  }


  public void verifyKey(VerifyEvent event) {
    verify(event);
  }


  public void verifyText(VerifyEvent event) {
    verify(event);
  }

  /**
   * @param event
   */
  private void verify(VerifyEvent event) {
    String string = event.text;
    char[] chars = new char[string.length()];
    string.getChars(0, chars.length, chars, 0);
    Text text = (Text) event.getSource();
    String delimeter = IUtilityConstants.EMPTY_STRING;
    String temp = null;

    if (format.contains(".")) {
      delimeter = ".";
      temp = format.substring(format.indexOf(delimeter) + 1);
    }
    else if (format.contains(",")) {
      delimeter = ",";
      temp = format.substring(format.indexOf(delimeter) + 1);
    }
    int decimalLength = 0;
    if (temp != null) {
      decimalLength = temp.length();
    }

    if (delimeter.equals(IUtilityConstants.EMPTY_STRING) && decimalLength == 0) {
      if (format.startsWith("-")) {
        regex = "-[\\d]*";
      }
      else if (format.startsWith("+")) {
        regex = "[+\\d]\\d*";
      }
      else {
        regex = "[-+]?[\\d]*";
      }
    }
    else {
      if (format.startsWith("-")) {
        regex = "-[\\d]*[\\" + delimeter + REGEX_1 + decimalLength + "}+$";
      }
      else if (format.startsWith("+")) {
        regex = "[+\\d][\\d]*[\\" + delimeter + REGEX_1 + decimalLength + "}+$";
      }
      else {
        regex = "[-+]?[\\d]*[\\" + delimeter + REGEX_1 + decimalLength + "}+$";
      }
    }
    pattern = Pattern.compile(regex);
    if (!delimeter.equals(IUtilityConstants.EMPTY_STRING)) {
      if ((delimeter.equals(string)) && text.getText().indexOf(delimeter) >= 0) {
        event.doit = false;
        return;
      }
    }
    for (int i = 0; i < chars.length; i++) {
      if (!(('0' <= chars[i] && chars[i] <= '9') || chars[i] == '.' || chars[i] == ',' || chars[i] == SWT.DEL ||
          chars[i] == SWT.BS || (chars[i] == '-') || (chars[i] == '+'))) {
        event.doit = false;
        return;
      }
    }
    event.text = new String(chars);
    final String oldS = text.getText();
    String newS = oldS.substring(0, event.start) + event.text + oldS.substring(event.end);
    if (!newS.equals(IUtilityConstants.EMPTY_STRING)) {
      Matcher matcher = pattern.matcher(newS);
      if (!matcher.matches()) {
        event.doit = false;
        return;
      }
    }

  }

}
