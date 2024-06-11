package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import java.util.Map;
import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * Column Filter Class
 *
 * @author pdh2cob
 * @param <E>
 */
public class A2lWPDefinitionColumnFilterMatcher<E extends A2lWpResponsibility> implements Matcher<E> {

  /**
   * First pattern
   */
  private Pattern pattern1;
  /**
   * Second pattern
   */
  private Pattern pattern2;
  /**
   * Pattern to search
   */
  private boolean pattern2Search;
  /**
   *
   */
  private final Map<Long, A2lVariantGroup> a2lVarGrpMap;

  private final A2lResponsibilityModel pidcRespModel;

  /**
   * @param a2lWpInfoBo A2lWpInfoBo
   */
  public A2lWPDefinitionColumnFilterMatcher(final A2LWPInfoBO a2lWpInfoBo) {
    super();
    this.a2lVarGrpMap = a2lWpInfoBo.getDetailsStrucModel().getA2lVariantGrpMap();
    this.pidcRespModel = a2lWpInfoBo.getA2lResponsibilityModel();
  }

  /**
   * Method which converts the user entered filter text to regex where applicable
   *
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {
    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter.replace("\n", "").replace("\r", "");

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition
    if (subTexts.length == 0) {
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!"".equals(curString)) {
        sbRegExp.append(".*");
        sbRegExp.append(Pattern.quote(curString));
      }
      sbRegExp.append(".*");
    }
    regExp = sbRegExp.toString();

    this.pattern1 = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);

    // Word level matching is not required if filter text starts with '*'.
    if (regExp.startsWith(".*")) {
      this.pattern2Search = false;
    }
    else {
      this.pattern2Search = true;
      this.pattern2 = Pattern.compile(".* " + regExp, Pattern.CASE_INSENSITIVE);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E element) {

    if (matchText(element.getName()) || matchText(element.getWpNameCust())) {
      return true;
    }

    if ((element.getVariantGrpId() != null) && (this.a2lVarGrpMap.get(element.getVariantGrpId()) != null) &&
        matchText(this.a2lVarGrpMap.get(element.getVariantGrpId()).getName())) {
      return true;
    }

    if ((element.getA2lRespId() != null) &&
        matchText(this.pidcRespModel.getA2lResponsibilityMap().get(element.getA2lRespId()).getAliasName())) {

      return true;
    }

    return false;
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
