/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.attr;

import java.text.ParseException;

import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;

/**
 * @author bne4cob
 */
public final class AttrValueTextResolver {

  /**
   * Get a String representation of the attribute value. All datatypes will be converted to a String.
   *
   * @param dbValue TabvAttrValue
   * @param language language
   * @param onlyEnglish return the English text value independently of the current languate setting in ApivDataProvider
   * @return String
   */
  public static String getStringValue(final TabvAttrValue dbValue, final Language language, final boolean onlyEnglish) {

    if (CommonUtils.isNull(dbValue)) {
      return "";
    }
    AttributeValueType valueType =
        AttributeValueType.getType(dbValue.getTabvAttribute().getTabvAttrValueType().getValueTypeId());
    String stringValue;

    // Get the string value to be displayed in the UI, based on the value type of the attribute
    // The value is taken from different fields, according to the type.
    switch (valueType) {
      case TEXT:
        stringValue = getValueFromText(dbValue, language, onlyEnglish);
        break;

      case NUMBER:
        stringValue = dbValue.getNumvalue().toString();
        break;

      case DATE:
        stringValue = getValueFromDate(dbValue);
        break;

      case BOOLEAN:
        stringValue = getValueFromBoolean(dbValue);
        break;

      // iCDM-321
      case HYPERLINK:
        stringValue = dbValue.getTextvalueEng();
        break;
      case ICDM_USER:
        stringValue = dbValue.getTextvalueEng();
        break;
      default:
        stringValue = "ERROR";
        break;
    }

    return stringValue;
  }

  /**
   * Get the string value from text, based on the language setting
   *
   * @param dbValue TabvAttrValue
   * @param dataCache DataCache
   * @param onlyEnglish enable language selection
   * @return String value
   */
  private static String getValueFromText(final TabvAttrValue dbValue, final Language language,
      final boolean onlyEnglish) {
    String stringValue = dbValue.getTextvalueEng();

    if (!onlyEnglish) {
      // check the current language setting and return the value according to this setting
      if (language == Language.ENGLISH) {
        stringValue = dbValue.getTextvalueEng();
      }
      else {// GERMAN:
        stringValue = dbValue.getTextvalueGer();
        if (CommonUtils.isEmptyString(stringValue)) {
          stringValue = dbValue.getTextvalueEng();
        }
      }

    }

    return stringValue;
  }

  /**
   * Get the string value from date
   *
   * @param dbValue TabvAttrValue
   * @param dataCache DataCache
   * @return date as string
   */
  private static String getValueFromDate(final TabvAttrValue dbValue) {
    String stringValue;
    try {
      stringValue = ApicUtil.formatDate(dbValue.getTabvAttribute().getFormat(), dbValue.getDatevalue().toString());
    }
    catch (ParseException exp) {
      ObjectStore.getInstance().getLogger().error("Error parsing date", exp);
      stringValue = "## DATE PARSE ERROR! ##";
    }

    return stringValue;
  }

  /**
   * Get the string value from boolean
   *
   * @param dbValue TabvAttrValue
   * @return stringValue
   */
  private static String getValueFromBoolean(final TabvAttrValue dbValue) {
    String stringValue;
    if (dbValue.getBoolvalue().equals(ApicConstants.BOOLEAN_TRUE_DB_STRING)) {
      stringValue = ApicConstants.BOOLEAN_TRUE_STRING;
    }
    else {
      stringValue = ApicConstants.BOOLEAN_FALSE_STRING;
    }
    return stringValue;
  }


  /**
   * Private constructor for utility class
   */
  private AttrValueTextResolver() {
    // Private constructor
  }

}
