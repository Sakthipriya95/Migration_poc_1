/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.common.utility;


/**
 * @author apl1cob
 */
public final class VarCodeLabelUtil {

  private VarCodeLabelUtil() {
    // dummy constructor
  }

  /**
   * If the Variant coded param name is given then the last characters with [int] is removed and the base parameter name
   * is returned
   *
   * @param varcodedParamName paramName
   * @return the Base parameter name from the Varaint coded param name.
   */
  public static String getParamName(final String varcodedParamName) {
    return concatenate(getBaseParamFirstName(varcodedParamName), getBaseParamLastName(varcodedParamName));
  }

  /**
   * Concatenates the string representation of the input objects. Uses <code>StringBuilder</code> class to concatenate
   * the strings.
   * <p>
   * Sample usage : <br>
   * <code>
   * int weight = 10;<br>
   * String text = CommonUtils.concatenate(true, "The weight is = ", weight, "kg");
   * </code> <br>
   * gives the result 'The weight is = 10kg'
   *
   * @param objects objects to be concatenated
   * @return concatenated string
   */
  public static String concatenate(final Object... objects) {
    // Object array should not be used
    final StringBuilder string = new StringBuilder(objects.length);

    for (Object obj : objects) {
      if (obj != null) {
        string.append(obj.toString());
      }
    }

    return string.toString();
  }


  /**
   * Occurs in situation where parameter name is PT_drRmpTransP[0].Neg_C
   *
   * @param varcodedParamName varcodedParamName
   * @return the base param first name for example PT_drRmpTransP[0].Neg_C the first name is PT_drRmpTransP.
   */
  public static String getBaseParamFirstName(final String varcodedParamName) {
    return varcodedParamName.substring(0, varcodedParamName.lastIndexOf('['));
  }

  /**
   * Occurs in situation where parameter name is PT_drRmpTransP[0].Neg_C
   *
   * @param varcodedParamName varcodedParamName
   * @return the base parameter last name for example PT_drRmpTransP[0].Neg_C the first name is .Neg_C.
   */
  public static String getBaseParamLastName(final String varcodedParamName) {
    String lastName = "";
    if ((varcodedParamName.length() - 1) > varcodedParamName.lastIndexOf(']')) {
      lastName = varcodedParamName.substring(varcodedParamName.lastIndexOf(']') + 1, varcodedParamName.length());
    }
    return lastName;
  }
}
