/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;

import java.util.BitSet;

/**
 * @author say8cob
 */
public class A2lObjectIdentifierValidator {


  private static final int A2L_MAX_IDENTIFIER = 1024;

  /**
   * Input name length exceed 1028 character
   */
  public static final byte INVALID_NAME_LENGTH = 0;

  /**
   * First letter should be alphanumaric or underscore
   */
  public static final byte INVALID_FIRST_LETTER = 1;

  /**
   * Only alphanumaric, underscore and square brackets are allowed
   */
  public static final byte INVALID_INPUT_CHARACTERS = 2;

  /**
   * Method to validate the
   *
   * @param name as input
   * @return true/false based on conditions
   */
  public BitSet isValidName(final String name) {
    BitSet result = new BitSet();
    // To validate the alias name is under the max allowed limit
    if (name.length() > A2L_MAX_IDENTIFIER) {
      result.set(INVALID_NAME_LENGTH);
    }
    // 505005 - Defect fix - Validate it shoud not be possible to add wp name starting with . (dot) (Eg .Wp) using UI.
    if (name.matches("^[0-9\\.].*$")) {
      result.set(INVALID_FIRST_LETTER);
    }
    String[] partialIdentifierList = name.split("\\.");
    // To validate the partial identifiers seperately
    if (partialIdentifierList.length != 0) {
      for (String partialIden : partialIdentifierList) {
        validatePartialIdentifiers(partialIden, result);
      }
    }
    return result;
  }

  /**
   * 494776 - Handling special characters in Alias names of responsibilities--------------------------- Method to
   * validate the input by excluding given special character
   *
   * @param name as input
   * @param charToExclude input character in name which needs to be excluded from the validation
   * @return true/false based on conditions
   */
  public BitSet isValidName(final String name, final char... charToExclude) {
    String nameWithoutChar = name;

    // to remove special characters from name which need to be excluded during validation
    if (null != charToExclude) {
      for (char exclude : charToExclude) {
        nameWithoutChar = nameWithoutChar.replace(exclude, ' ');
      }
    }
    return isValidName(nameWithoutChar.replace(" ", "").replace("-", "_"));
  }

  private void validatePartialIdentifiers(final String partialIdent, final BitSet result) {
    if (!partialIdent.matches("[a-zA-Z0-9_\\[\\]]*")) {
      result.set(INVALID_INPUT_CHARACTERS);
    }
  }

  /**
   * 497765 - Error Message 'WP does not comply with A2L specification' should be clearer
   *
   * @param result bitset which contains error details
   * @param charToExclude Special characters to exclude from validation
   * @return error string
   */
  public String createErrorMsg(final BitSet result, final char... charToExclude) {
    StringBuilder errorMsg = new StringBuilder();
    int count = 0;
    if (result.get(INVALID_NAME_LENGTH)) {
      count++;
      errorMsg.append("\n  ").append(count).append(". Input name should not exceed 1024 characters");
    }
    if (result.get(INVALID_FIRST_LETTER)) {
      count++;
      errorMsg.append("\n  ").append(count).append(". First letter should be an alphabet or underscore");
    }
    if (result.get(INVALID_INPUT_CHARACTERS)) {
      count++;
      errorMsg.append("\n  ").append(count).append(". Allowed characters are alphanumeric, underscore");
      for (char spclChar : charToExclude) {
        errorMsg.append(", ").append(spclChar);
      }
      errorMsg.append(" and square brackets");
    }
    return errorMsg.toString();
  }

  /**
   * @param input input string
   * @param charToExclude special character to exclude
   * @return modified String
   */
  public String replaceInvalidChars(final String input, final char... charToExclude) {
    StringBuilder allowedChar = new StringBuilder();
    allowedChar.append("[^a-zA-Z0-9.\\[\\]_");
    for (char spclChar : charToExclude) {
      allowedChar.append(spclChar);
    }
    allowedChar.append("]+");

    String ret = input;
    ret = ret.replace("ß", "ss");
    ret = ret.replace("ü", "ue");
    ret = ret.replace("Ü", "Ue");
    ret = ret.replace("ö", "oe");
    ret = ret.replace("Ö", "Oe");
    ret = ret.replace("ä", "ae");
    ret = ret.replace("Ä", "Ae");
    ret = ret.replace('(', '[');
    ret = ret.replace(')', ']');
    ret = ret.replaceAll("[\\W]", "_");
    return ret;
  }

  public static String clearGroupNamesFromSpecialChars(final String input) {
    String output = null;

    String temp = input.replace("ä", "ae");
    temp = temp.replace("ö", "oe");
    temp = temp.replace("ü", "ue");
    temp = temp.replace("ß", "ss");
    temp = temp.replace("Ä", "Ae");
    temp = temp.replace("Ö", "Oe");
    temp = temp.replace("Ü", "Ue");
    temp = temp.trim(); // cut all spaces before and after --> ALM-332772
    output = temp.replaceAll("[\\W]", "_");

    return output;
  }
}
