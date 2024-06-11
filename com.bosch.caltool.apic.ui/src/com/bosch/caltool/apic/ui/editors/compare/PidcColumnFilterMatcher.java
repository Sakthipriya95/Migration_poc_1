/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.text.ParseException;
import java.util.regex.Pattern;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * All column filter matcher used for compare editor This class implements Matcher which is used in the Determination of
 * values to be filtered.
 *
 * @author jvi6cob
 * @param <E> generic type
 */
public class PidcColumnFilterMatcher<E extends PidcNattableRowObject> implements Matcher<E> {

  // Pattern for search
  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;


  /**
   * Method which converts the user entered filter text to regex where applicable
   *
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || CommonUIConstants.EMPTY_STRING.equals(textToFilter) || !setPattern) {
      return;
    }
    // Matcher text
    String regExp = textToFilter;
    // split text with *
    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
    if (subTexts.length == 0) {
      return;
    }
    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();

    for (String curString : subTexts) {
      if (!CommonUIConstants.EMPTY_STRING.equals(curString)) {
        sbRegExp.append(".*");
        sbRegExp.append(Pattern.quote(curString));
      }
      sbRegExp.append(".*");
    }
    regExp = sbRegExp.toString();
    // Pattern.DOTALL or (?s) tells Java to allow the dot to match newline characters, too.
    this.pattern1 = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // Word level matching is not required if filter text starts with '*'.
    if (regExp.startsWith(".*")) {
      this.pattern2Search = false;
    }
    else {
      this.pattern2Search = true;
      this.pattern2 = Pattern.compile(".* " + regExp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E compareRowObject) {
    IProjectAttribute iProjectAttribute = compareRowObject.getProjectAttributeHandler().getProjectAttr();
    // match type filter text for comment column
    if (matchText(iProjectAttribute.getAdditionalInfoDesc())) {
      return true;
    }

    PidcDataHandler pidcDataHandler = compareRowObject.getColumnDataMapper().getPidcDataHandler();
    String group = pidcDataHandler.getAttributeGroupMap()
        .get(pidcDataHandler.getAttributeMap().get(iProjectAttribute.getAttrId()).getAttrGrpId()).getName();
    // match type filter text for group column
    if (matchText(group)) {
      return true;
    }

    // get the super group
    String superGroup = pidcDataHandler.getAttributeSuperGroupMap()
        .get(pidcDataHandler.getAttributeGroupMap()
            .get(pidcDataHandler.getAttributeMap().get(iProjectAttribute.getAttrId()).getAttrGrpId()).getSuperGrpId())
        .getName();
    // match type filter text for super group column
    if (matchText(superGroup)) {
      return true;
    }
    // match part
    if (matchText(iProjectAttribute.getPartNumber())) {
      return true;
    }
    // match specification
    if (matchText(iProjectAttribute.getSpecLink())) {
      return true;
    }

    final Attribute attribute = compareRowObject.getAttribute();
    // match type filter text for attribute
    if (matchText(attribute.getNameEng()) || matchText(attribute.getNameGer())) {
      return true;
    }
    // match type filter text for attribute description
    if (matchText(attribute.getDescriptionEng()) || matchText(attribute.getDescriptionGer())) {
      return true;
    }
    String valueClass = getValueClass(pidcDataHandler, iProjectAttribute);
    // match value class
    if (matchText(valueClass)) {
      return true;
    }
    // match attributeClass
    String attributeClass = getAttributeValClass(pidcDataHandler, iProjectAttribute, compareRowObject);
    if (matchText(attributeClass)) {
      return true;
    }
    // match modified date
    if (matchText(getModifiedDate(iProjectAttribute))) {
      return true;
    }
    // match type filter text with value column
    return (matchText(compareRowObject.getProjectAttributeHandler().getDefaultValueDisplayName(true)));
  }

  /**
   *
   */
  private String getModifiedDate(final IProjectAttribute iProjectAttribute) {
    try {
      return (iProjectAttribute.getModifiedDate() == null) ? ""
          : ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, iProjectAttribute.getModifiedDate());
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @param iProjectAttribute
   * @param pidcDataHandler
   */
  private String getValueClass(final PidcDataHandler pidcDataHandler, final IProjectAttribute iProjectAttribute) {
    String returnValue = "";
    if ((pidcDataHandler.getAttributeValueMap().get(iProjectAttribute.getValueId()) != null) && (pidcDataHandler
        .getAttributeValueMap().get(iProjectAttribute.getValueId()).getCharacteristicValueId() != null)) {
      returnValue = pidcDataHandler.getCharacteristicValueMap()
          .get(pidcDataHandler.getAttributeValueMap().get(iProjectAttribute.getValueId()).getCharacteristicValueId())
          .getValNameEng();
    }
    return returnValue;

  }

  /**
   * @param iProjectAttribute
   * @param pidcDataHandler
   */
  private String getAttributeValClass(final PidcDataHandler pidcDataHandler, final IProjectAttribute iProjectAttribute,
      final PidcNattableRowObject compareRowObject) {
    String returnValue = "";
    if (compareRowObject.getProjectAttributeHandler().getProjectAttr() != null) {
      if (pidcDataHandler.getAttributeMap().get(iProjectAttribute.getAttrId()).getCharacteristicId() != null) {
        returnValue = pidcDataHandler.getCharacteristicMap()
            .get(pidcDataHandler.getAttributeMap().get(iProjectAttribute.getAttrId()).getCharacteristicId()).getName();
      }
    }
    else {
      returnValue = "";
    }
    return returnValue;
  }

  /**
   * Method which checks whether the passed in text matches against the patterns created in this class
   *
   * @param text
   * @return boolean
   */
  private final boolean matchText(final String text) {
    if (text == null) {
      return false;
    }
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }
    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }
    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);
    return matcher2.matches();
  }


}