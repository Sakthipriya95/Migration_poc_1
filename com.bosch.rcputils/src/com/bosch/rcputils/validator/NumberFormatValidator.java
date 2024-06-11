package com.bosch.rcputils.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.ControlDecoration;

import com.bosch.rcputils.decorators.Decorators;

public final class NumberFormatValidator {

  /**
   * APICUtil instance
   */
  private static NumberFormatValidator numVal;

  /**
   * Private Constructor
   */
  private NumberFormatValidator() {

  }

  /**
   * This method returns APICUtil instance
   * 
   * @return APICUtil
   */
  public static NumberFormatValidator getInstance() {
    if (numVal == null) {
      numVal = new NumberFormatValidator();
    }
    return numVal;
  }

  /**
   * @param unit
   * @return boolean
   */
  public boolean validateUnit(final String unit) {
    boolean isValid;
    // [/,:<>!~@#$%^&()+=?()\"|!\\[#$-]
    Pattern pattern = Pattern.compile("[,:<>!~@*#$%^&()+=?()\"|!\\[#$]");
    Matcher patternMatcher = pattern.matcher(unit.subSequence(0, unit.length()));
    if (patternMatcher.find()) {
      isValid = false;
    }
    else {
      isValid = true;
    }
    return isValid;
  }

  /**
   * @param format
   * @return boolean
   */
  public boolean validateNumberFormat(final String format) {
    boolean isValid;
    Pattern pattern;
    Matcher patternMatcher;
    String regex;
    if ("".equals(format)) {
      isValid = true;
    }
    else {
      if ((format.length() > 2) && (format.contains(".") || format.contains(","))) {
        regex = "[/:<>!~@*#$%^&()+=?()\"|![a-zA-Z]\\[#$-]";
        pattern = Pattern.compile(regex);
        patternMatcher = pattern.matcher(format.subSequence(0, format.length()));
        if (patternMatcher.find()) {
          isValid = false;
        }
        else {
          isValid = true;
        }
      }
      else {
        regex = "[/.,:<>!~@*#$%^&()+=?()\"|![a-zA-Z]\\[#$-]";
        pattern = Pattern.compile(regex);
        patternMatcher = pattern.matcher(format.subSequence(0, format.length()));
        if (patternMatcher.find()) {
          isValid = false;
        }
        else {
          isValid = true;
        }
      }
    }
    return isValid;
  }

  /**
   * @param numVal
   * @return boolean
   */
  public boolean validateNumberVal(final String numVal) {
    boolean isValid = false;
    if ("".equals(numVal)) {
      isValid = true;
    }
    else {
      if (numVal.length() > 0) {
        Pattern pattern = Pattern.compile("[/,:<>!~@*#$%^&()+=?()\"|![a-zA-Z]\\[#$-]");
        Matcher patternMatcher = pattern.matcher(numVal.subSequence(0, numVal.length()));
        if (patternMatcher.find()) {
          isValid = false;
        }
        else {
          isValid = true;
        }
      }
    }
    return isValid;
  }


  public void isInputFieldIsNumber(final String numVal, final Decorators decorators,
      final ControlDecoration controlDec) {
    if (!"".equals(numVal)) {
      boolean isValidNumValue = NumberFormatValidator.getInstance().validateNumberVal(numVal);
      if (isValidNumValue) {
        decorators.showErrDecoration(controlDec, "", false);
      }
      else {
        decorators.showErrDecoration(controlDec, "This field contains invalid input", true);
      }
    }
    else {
      decorators.showReqdDecoration(controlDec, "This field is mandatory");
    }
  }


  /* *//**
        * @param input
        * @param decorators
        * @param conDec
        */

  public void validateNumFormatStyle(final String input, final Decorators decorators, final ControlDecoration conDec) {
    boolean isValid = validateFormat(input);
    if (isValid) {
      if (input.length() > 0) {
        decorators.showErrDecoration(conDec, "", false);
      }
      else {
        decorators.showReqdDecoration(conDec, "This field is mandatory");
      }
    }
    else {
      decorators.showErrDecoration(conDec, "Multiple decimal seperaters not allowed!", true);
    }
  }


  /**
   * @param format
   * @return boolean
   */
  public boolean validateFormat(final String format) {
    boolean isValid;
    int commaCount = 0;
    int dotCount = 0;
    if (!"".equals(format.trim())) {
      for (int i = 0; i < format.length(); i++) {
        char chVal = format.charAt(i);
        boolean isDigit = Character.isDigit(chVal);
        if (!isDigit) {
          if (chVal == ',') {
            commaCount++;
          }
          else if (chVal == '.') {
            dotCount++;
          }
        }
      }
    }
    if ((commaCount > 1) || (dotCount > 1) || ((commaCount == 1) && (dotCount == 1))) {
      isValid = false;
    }
    else {
      isValid = true;
    }
    return isValid;
  }
}
