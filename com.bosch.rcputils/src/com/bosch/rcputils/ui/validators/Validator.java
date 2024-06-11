package com.bosch.rcputils.ui.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import com.bosch.rcputils.IUtilityConstants;
import com.bosch.rcputils.decorators.Decorators;

/**
 * @author mga1cob
 */
public final class Validator {

  /**
   * Validator instance
   */
  private static Validator validator;
  /**
   * Defines the input is valid or not
   */
  private boolean isValid;

  /**
   * Holds the case sensitive selection
   */
  private boolean caseSensitive;

  /**
   * Holds the auto save flag
   */
  private boolean autoSave;

  /**
   * The private constructor
   */
  private Validator() {
    // The private constructor
  }

  /**
   * This method returns the Validator instance
   * 
   * @return Validator
   */
  public static Validator getInstance() {
    if (validator == null) {
      validator = new Validator();
    }
    return validator;
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec1 instance
   * @param dec2 instance
   * @param text1 instance
   * @param text2 instance
   * @param isMandatory defines mandatory or not
   */
  // ICDM-112
  public void validateNDecorate(ControlDecoration dec1, ControlDecoration dec2, Text text1, Text text2,
      boolean isMandatory) {
    String textVal1 = text1.getText().trim();
    String textVal2 = text2.getText().trim();
    Decorators decorators = new Decorators();
    if (textVal1.length() == 0 && isMandatory) {
      decorators.showReqdDecoration(dec1, IUtilityConstants.MANDATORY_MSG);
    }
    else {
      decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
    }
    if (textVal2.length() == 0 && isMandatory) {
      decorators.showReqdDecoration(dec2, IUtilityConstants.MANDATORY_MSG);
    }
    else {
      decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
    }
    validateInputs(text1, text2);
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec instance
   * @param combo instance
   * @param isMandatory defines mandatory or not
   */
  public void validateNDecorate(ControlDecoration dec, Combo combo, boolean isMandatory) {
    String item = null;
    if (combo != null) {
      int index = combo.getSelectionIndex();
      item = combo.getItem(index);
    }
    Decorators decorators = new Decorators();
    if (item == null || item.equals(IUtilityConstants.DEFAULT_COMBO_SELECT) ||
        item.equals(IUtilityConstants.NO_GROUP_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
    }
    if (item != null) {
      validateInput(item);
    }
  }


  /**
   * @param dec instance
   * @param text1 instance
   * @param text2 instance
   * @param text3 instance
   * @param isMandatory defines mandatory or not
   */
  public void validateNDecorate(ControlDecoration dec, Text text1, Text text2, Text text3, boolean isMandatory) {
    String textVal = text1.getText().trim();
    Decorators decorators = new Decorators();
    if (textVal.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
    }
    validateInputs(text1, text2, text3);
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec instance
   * @param text1 instance
   * @param text2 instance
   * @param isMandatory defines mandatory or not
   * @param isNumber defines number datatype or not
   * @param format defines the format style
   * @return valid
   */
  public boolean validateNDecorate(ControlDecoration dec, Text text1, Text text2, boolean isMandatory, boolean isNumber,
      String format) {
    String textVal = text1.getText().trim();
    boolean valid = false;
    Decorators decorators = new Decorators();
    if (textVal.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      if (isNumber) {
        try {
          if (textVal.length() > 0) {
            if (format != null && !format.equals(IUtilityConstants.EMPTY_STRING)) {
              if (textVal.contains(",")) {
                textVal = textVal.replace(",", ".").trim();
              }
              // Allowing to enter -
              if (textVal.startsWith("-")) {
                textVal = textVal.replace("-", "").trim();
                valid = true;
              }
              // Allowing to enter -
              if (textVal.startsWith("+")) {
                textVal = textVal.replace("+", "").trim();
                valid = true;
              }
            }
            if (!textVal.isEmpty()) {
              Double.parseDouble(textVal);
              if (!checkformat(textVal, format)) {
                decorators.showErrDecoration(dec, IUtilityConstants.NUMBER_FORMAT_WRONG, true);
                valid = false;
                return valid;
              }
              decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
              valid = true;
            }
          }
        }
        catch (NumberFormatException e) {
          decorators.showErrDecoration(dec, IUtilityConstants.IP_TYPE_SHOULD_BE_NUMERIC, true);
          valid = false;
        }
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    if (valid) {
      validateInputs(text1, text2);
    }
    return valid;

  }

  /**
   * @param textVal
   * @param format
   */
  private boolean checkformat(String textVal, String format) {
    if (format != null) {
      if (format.contains(",")) {
        format = format.replace(",", ".").trim();
      }

      if (!format.contains(".")) {
        if (textVal.contains(".")) {
          return false;
        }
      }
      else if (textVal.contains(".")) {
        String[] split1 = textVal.split("\\.");
        String[] split2 = format.split("\\.");
        if (split1.length > 1 && split2.length > 1 && split1[1].length() > split2[1].length()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param dec
   * @param text1
   * @param text2
   * @param isMandatory
   * @param isNumber
   * @param format
   * @return
   */
  public boolean validateNDecorate(ControlDecoration dec, Object text1, Object text2, boolean isMandatory,
      boolean isNumber, String format) {
    String textVal = getTextValue(text1);
    boolean valid = false;
    Decorators decorators = new Decorators();
    if (textVal.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      if (isNumber) {
        try {
          if (textVal.length() > 0) {
            if (format != null && !format.equals(IUtilityConstants.EMPTY_STRING)) {
              if (textVal.contains(",")) {
                textVal = textVal.replace(",", ".").trim();
              }
            }
            Double.parseDouble(textVal);
            decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
            valid = true;
          }
        }
        catch (NumberFormatException e) {
          decorators.showErrDecoration(dec, IUtilityConstants.IP_TYPE_SHOULD_BE_NUMERIC, true);
          valid = false;
        }
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    if (valid) {

      validateInputs(text1, text2);
    }
    return valid;

  }

  /**
   * @param textObj
   * @return
   */
  private String getTextValue(Object textObj) {
    String textVal = "";
    if (textObj instanceof StyledText) {
      StyledText styledText = (StyledText) textObj;
      textVal = styledText.getText().trim();
    }
    else if (textObj instanceof Text) {
      Text text = (Text) textObj;
      textVal = text.getText().trim();
    }
    return textVal;
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec instance
   * @param text1 instance
   * @param isNumber defines number datatype or not
   * @param format defines the format style
   * @return valid
   */
  public boolean validateNDecorate(ControlDecoration dec, Text text1, boolean isNumber, String format) {

    boolean valid = false;
    Decorators decorators = new Decorators();
    String textVal = text1.getText().trim();
    if (isNumber) {
      try {
        if (textVal.length() > 0) {
          if (format != null && !format.equals(IUtilityConstants.EMPTY_STRING)) {
            if (textVal.contains(",")) {
              textVal = textVal.replace(",", ".").trim();
            }
          }
          Double.parseDouble(textVal);
          decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
          valid = true;
        }
      }
      catch (NumberFormatException e) {
        decorators.showErrDecoration(dec, IUtilityConstants.IP_TYPE_SHOULD_BE_NUMERIC, true);
        valid = false;
      }
    }
    else {
      decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
    }
    return valid;
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec instance
   * @param text1 instance
   * @param isMandatory defines mandatory or not
   * @param isNumber defines number datatype or not
   * @return boolean
   */
  public boolean validateNDecorate(ControlDecoration dec, Text text1, boolean isMandatory, boolean isNumber) {
    String textVal = text1.getText().trim();
    boolean valid = false;
    Decorators decorators = new Decorators();
    if (textVal.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      if (isNumber) {
        try {
          if (textVal.length() > 0) {
            if (textVal.contains(",")) {
              textVal = textVal.replaceAll(",", ".").trim();
            }
            Double.parseDouble(textVal);
            decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
            valid = true;
          }
        }
        catch (NumberFormatException e) {
          decorators.showErrDecoration(dec, IUtilityConstants.IP_TYPE_SHOULD_BE_NUMERIC, true);
          valid = false;
        }
      }
      else {
        valid = true;
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    if (valid) {
      validateInputs(text1);
    }
    return valid;
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec instance
   * @param text1 instance
   * @param text2 instance
   * @param isMandatory defines number datatype or not
   * @param isNumber defines number datatype or not
   * @param isMatched defines whether data is matched or not
   */
  public void validateNDecorate(ControlDecoration dec, Text text1, Text text2, boolean isMandatory, boolean isNumber,
      boolean isMatched) {
    String textVal = text1.getText().trim();
    boolean valid = false;
    Decorators decorators = new Decorators();
    if (textVal.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      if (isNumber) {
        try {
          if (textVal.length() > 0) {
            if (textVal.contains(",")) {
              textVal = textVal.replaceAll(",", ".").trim();
            }
            Double.parseDouble(textVal);
            decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
            valid = true;
          }
        }
        catch (NumberFormatException e) {
          decorators.showErrDecoration(dec, IUtilityConstants.IP_TYPE_SHOULD_BE_NUMERIC, true);
          valid = false;
        }
      }
      else {
        if (isMatched) {
          decorators.showErrDecoration(dec, IUtilityConstants.PID_ALREADY_EXISTED, true);
          valid = false;
        }
        else {
          decorators.showErrDecoration(dec, IUtilityConstants.EMPTY_STRING, false);
        }

      }
    }
    if (valid) {
      validateInputs(text1, text2);
    }
  }

  /**
   * This method validates text field
   * 
   * @param text1
   */
  private void validateInputs(Text text1) {

    setValid(text1.getText().length() > 0);
  }

  /**
   * This method validates text fields
   * 
   * @param text1
   * @param text2
   */
  private void validateInputs(Object text1, Object text2) {
    boolean valid = getTextLength(text1) > 0;
    if (text2 != null) {
      valid = valid && getTextLength(text2) > 0;
    }
    else {
      valid = false;
    }
    setValid(valid);
  }

  /**
   * @param text1
   */
  private int getTextLength(Object textObj) {
    int textlength = 0;
    if (textObj instanceof StyledText) {
      StyledText styledText = (StyledText) textObj;
      textlength = styledText.getText().length();
    }
    else if (textObj instanceof Text) {
      Text text = (Text) textObj;
      textlength = text.getText().length();
    }
    return textlength;

  }

  /**
   * This method validates text fields
   * 
   * @param text1
   */
  private void validateInput(String text1) {

    setValid(text1.length() > 0);
  }

  /**
   * This method validates text fields
   * 
   * @param text1 instnace
   * @param text2 instnace
   * @param text3 instnace
   */
  private void validateInputs(Text text1, Text text2, Text text3) {
    boolean valid = text1.getText().length() > 0;
    if (text2 != null) {
      valid = valid && text2.getText().length() > 0;
    }
    else if (text3 != null) {
      valid = valid && text3.getText().length() > 0;
    }
    else {
      valid = false;
    }
    setValid(valid);
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec1 instnace
   * @param dec2 instnace
   * @param combo instnace
   * @param text instnace
   * @param isMandatory defines whether data is matched or not
   */
  public void validateNDecorate(ControlDecoration dec1, ControlDecoration dec2, Combo combo, Text text,
      boolean isMandatory) {
    String item = null;
    if (combo != null) {
      int index = combo.getSelectionIndex();
      item = combo.getItem(index);
    }
    String textVal = text.getText().trim();
    Decorators decorators = new Decorators();
    if (item != null && item.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec1, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else if (textVal.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec2, IUtilityConstants.MANDATORY_MSG);
        if (dec1 != null) {
          decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
        }
      }
      else {
        decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
      decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
    }
    validateInputs(combo, text);
  }

  /**
   * This method validates input of Combo and text fields
   * 
   * @param combo
   * @param text
   */
  private void validateInputs(Combo combo, Text text) {
    boolean valid;
    String item = null;
    if (combo != null) {
      int index = combo.getSelectionIndex();
      item = combo.getItem(index);
    }
    if (item != null) {
      valid = !(item.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    }
    else {
      valid = false;
    }
    valid = valid && text.getText().length() > 0;
    setValid(valid);
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec1 instance
   * @param dec2 instance
   * @param combo1 instance
   * @param combo2 instance
   * @param isMandatory defines whether data is matched or not
   */
  public void validateNDecorate(ControlDecoration dec1, ControlDecoration dec2, Combo combo1, Combo combo2,
      boolean isMandatory) {
    int index = combo1.getSelectionIndex();
    String textVal1 = combo1.getItem(index);
    Decorators decorators = new Decorators();
    index = combo2.getSelectionIndex();
    String textVal2 = combo2.getItem(index);
    if (textVal1.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        textVal1.equalsIgnoreCase(IUtilityConstants.SELECT_DEPN_ATTR)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec1, IUtilityConstants.MANDATORY_MSG);
        decorators.showReqdDecoration(dec2, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else if (textVal2.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        textVal2.equalsIgnoreCase(IUtilityConstants.SELECT_ATTR_VAL) ||
        textVal2.equalsIgnoreCase(IUtilityConstants.NO_ACTIVE_VALS_ARE_AVAILABLE)) {
      decorators.showReqdDecoration(dec2, IUtilityConstants.MANDATORY_MSG);
      decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
    }
    else {
      decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
      decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
    }
    validateInputs(combo1, combo2);

  }

  /**
   * This method validates the input fields of Combo
   * 
   * @param combo1
   * @param combo2
   */
  private void validateInputs(Combo combo1, Combo combo2) {

    int index = combo1.getSelectionIndex();
    String item1 = combo1.getItem(index);
    boolean valid = !(item1.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    index = combo2.getSelectionIndex();
    String item2 = combo2.getItem(index);
    valid = valid && !(item2.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    setValid(valid);
  }


  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec1 instance
   * @param dec2 instance
   * @param dec3 instance
   * @param dec4 instance
   * @param dec5 instance
   * @param text1 instance
   * @param text2 instance
   * @param combo1 instance
   * @param combo2 instance
   * @param combo3 instance
   * @param isMandatory defines whether data is matched or not
   */
  public void validateNDecorate(ControlDecoration dec1, ControlDecoration dec2, ControlDecoration dec3,
      ControlDecoration dec4, ControlDecoration dec5, Text text1, Text text2, Combo combo1, Combo combo2, Combo combo3,
      boolean isMandatory) {
    Decorators decorators = new Decorators();
    String textVal1 = text1.getText().trim();
    String textVal2 = text2.getText().trim();
    int index = combo1.getSelectionIndex();
    String item1 = combo1.getItem(index);

    index = combo2.getSelectionIndex();
    String item2 = combo2.getItem(index);

    index = combo3.getSelectionIndex();
    String item4 = combo3.getItem(index);
    if (textVal1.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec1, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
    }
    if (textVal2.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec2, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
    }
    if (item1.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        item1.equals(IUtilityConstants.DEFAULT_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec3, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec3, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec3, IUtilityConstants.EMPTY_STRING, false);
    }
    if (item2.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        item2.equals(IUtilityConstants.DEFAULT_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec4, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec4, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec4, IUtilityConstants.EMPTY_STRING, false);
    }
    if (item4.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        item4.equals(IUtilityConstants.DEFAULT_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec5, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec5, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec5, IUtilityConstants.EMPTY_STRING, false);
    }
    validateInputs(text1, text2, combo1, combo2, combo3);
  }

  /**
   * This method sets empty decorator if the input is valid
   * 
   * @param dec1
   * @param dec2
   * @param dec3
   * @param dec4
   * @param dec5
   * @param decorators
   */


  /**
   * This method validates input of Combo and text fields
   * 
   * @param text1 instnace
   * @param text2 instnace
   * @param combo1 instnace
   * @param combo2 instnace
   * @param combo3 instnace
   * @param combo3 instnace
   */
  private void validateInputs(Text text1, Text text2, Combo combo1, Combo combo2, Combo combo3) {
    // default made to true. If the value is true all the fields are enabled then enable it.
    boolean valid = text1.getText().length() > 0;
    valid = valid && text2.getText().length() > 0;
    int index = combo1.getSelectionIndex();
    String item1 = combo1.getItem(index);
    valid = valid && !(item1.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    index = combo2.getSelectionIndex();
    String item2 = combo1.getItem(index);
    valid = valid && !(item2.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    index = combo3.getSelectionIndex();
    String item4 = combo3.getItem(index);
    valid = valid && !(item4.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    setValid(valid);
  }

  /**
   * This method validates input of Combo and text fields
   * 
   * @param text1 instnace
   * @param text2 instnace
   * @param text3 instance
   * @param combo1 instnace
   * @param combo2 instnace
   * @param combo3 instnace
   * @param combo3 instnace
   */
  private void validateInputs(Text text1, Text text2, Text text3, Combo combo1, Combo combo2, Combo combo3) {
    // default made to true. If the value is true all the fields are enabled then enable it.
    boolean valid = text1.getText().length() > 0;
    valid = valid && text2.getText().length() > 0;
    valid = valid && text3.getText().length() > 0;
    int index = combo1.getSelectionIndex();
    String item1 = combo1.getItem(index);
    valid = valid && !(item1.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    index = combo2.getSelectionIndex();
    String item2 = combo1.getItem(index);
    valid = valid && !(item2.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    index = combo3.getSelectionIndex();
    String item4 = combo3.getItem(index);
    valid = valid && !(item4.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING));
    setValid(valid);
  }

  /**
   * @param isValid the isValid to set
   */
  public void setValid(boolean isValid) {
    this.isValid = isValid;
  }

  /**
   * @return the isValid
   */
  public boolean isValid() {
    return isValid;
  }


  /**
   * @param caseSensitive the caseSensitive to set
   */
  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  /**
   * @return the caseSensitive
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  /**
   * @param autoSave the autoSave to set
   */
  public void setAutoSave(boolean autoSave) {
    this.autoSave = autoSave;
  }

  /**
   * @return the autoSave
   */
  public boolean isAutoSave() {
    return autoSave;
  }

  /**
   * @param unit defines unit value
   * @return boolean
   */
  public boolean validateUnit(String unit) {
    boolean valid;
    // [/,:<>!~@#$%^&()+=?()\"|!\\[#$-]
    Pattern pattern = Pattern.compile("[,:<>!~@*#$%^&()+=?()\"|!\\[#$]");
    Matcher patternMatcher = pattern.matcher(unit.subSequence(0, unit.length()));
    if (patternMatcher.find()) {
      valid = false;
    }
    else {
      valid = true;
    }
    return valid;
  }

  /**
   * Validates the field value passed and sets decoration if required.
   * 
   * @param dec1 instance
   * @param dec2 instance
   * @param dec3 instance
   * @param dec4 instance
   * @param dec5 instnace
   * @param dec6 instance
   * @param text1 instnace
   * @param text2 instnace
   * @param text3 instnace
   * @param combo1 instnace
   * @param combo2 instnace
   * @param combo3 instnace
   * @param isMandatory defines whether data is matched or not
   */
  public void validateNDecorate(ControlDecoration dec1, ControlDecoration dec2, ControlDecoration dec3,
      ControlDecoration dec4, ControlDecoration dec5, ControlDecoration dec6, Text text1, Text text2, Text text3,
      Combo combo1, Combo combo2, Combo combo3, boolean isMandatory) {
    Decorators decorators = new Decorators();
    String textVal1 = text1.getText().trim();
    String textVal2 = text2.getText().trim();
    String textVal3 = text3.getText().trim();

    int index = combo1.getSelectionIndex();
    String item1 = combo1.getItem(index);

    index = combo2.getSelectionIndex();
    String item2 = combo2.getItem(index);

    index = combo3.getSelectionIndex();
    String item3 = combo3.getItem(index);

    if (textVal1.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec1, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec1, IUtilityConstants.EMPTY_STRING, false);
    }

    if (textVal2.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec2, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec2, IUtilityConstants.EMPTY_STRING, false);
    }

    if (textVal3.length() == 0) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec3, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec3, IUtilityConstants.EMPTY_STRING, false);
      }
    }

    else {
      decorators.showErrDecoration(dec3, IUtilityConstants.EMPTY_STRING, false);
    }

    if (item1.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        item1.equalsIgnoreCase(IUtilityConstants.DEFAULT_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec4, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec4, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec4, IUtilityConstants.EMPTY_STRING, false);
    }
    if (item2.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        item2.equalsIgnoreCase(IUtilityConstants.DEFAULT_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec5, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec5, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec5, IUtilityConstants.EMPTY_STRING, false);
    }
    if (item3.equalsIgnoreCase(IUtilityConstants.EMPTY_STRING) ||
        item3.equalsIgnoreCase(IUtilityConstants.DEFAULT_COMBO_SELECT)) {
      if (isMandatory) {
        decorators.showReqdDecoration(dec6, IUtilityConstants.MANDATORY_MSG);
      }
      else {
        decorators.showErrDecoration(dec6, IUtilityConstants.EMPTY_STRING, false);
      }
    }
    else {
      decorators.showErrDecoration(dec6, IUtilityConstants.EMPTY_STRING, false);
    }
    validateInputs(text1, text2, text3, combo1, combo2, combo3);
  }

}
