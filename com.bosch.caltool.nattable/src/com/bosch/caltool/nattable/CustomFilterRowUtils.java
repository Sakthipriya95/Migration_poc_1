package com.bosch.caltool.nattable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.FilterRowUtils;
import org.eclipse.nebula.widgets.nattable.filterrow.ParseResult;
import org.eclipse.nebula.widgets.nattable.filterrow.ParseResult.MatchType;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;

import ca.odell.glazedlists.matchers.ThresholdMatcherEditor;


/**
 * The <code>CustomFilterRowUtils</code> contains a code for searching filter content in a customised manner </br>
 * </br>
 * 1. When the filter text entered begins without a <b>*</b> , a <b>*</b> is prefixed and suffixed to the filter
 * text</br>
 * 2. When the filter text entered begins with a <b>*</b> , a <b>*</b> suffixed to the filter text</br>
 * 3. <i>Pattern.quote</i> method is used to get a literal pattern string</br>
 * </br>
 * By passing the regular expression(by OR'ing steps 1 and 2) obtained in the above three steps to the
 * {@link FilterRowUtils} class an equivalent behavior is achieved.<b>But this approach demands REGEX knowledge for the
 * user</b> </br>
 * <b>This class is to be used while implementing greater than ,less than ,etc features</b>
 *
 * @author jvi6cob
 */
public final class CustomFilterRowUtils {

  private CustomFilterRowUtils() {
    // Utility classes must declare private constructor
  }

  /**
   * @param text String
   * @param textDelimter String
   * @param textMatchngMode TextMatchingMode
   * @return List<ParseResult>
   */
  public static List<ParseResult> parse(final String text, final String textDelimter,
      final TextMatchingMode textMatchngMode) {
    final List<ParseResult> parseResultsList = new ArrayList<ParseResult>();

    if (textDelimter != null) {
      final StringTokenizer token = new StringTokenizer(text, textDelimter);
      while (token.hasMoreTokens()) {
        parse(token.nextToken(), textMatchngMode, parseResultsList);
      }
    }
    else {
      parse(text, textMatchngMode, parseResultsList);
    }

    return parseResultsList;
    // TODO: The below line will use the default parsing used by Nattable
    // Used when regex needs to be enabled. eg: When user can search using regex for a negative search(i.e NOT
    // something)
  }

  private static void parse(final String stringObj, final TextMatchingMode textMatchMode,
      final List<ParseResult> parseResultsList) {
    ParseResult parseResultObj;

    if (textMatchMode == TextMatchingMode.REGULAR_EXPRESSION) {
      parseResultObj = parseExpression(stringObj);
    }
    else {
      parseResultObj = parseLiteral(stringObj);
    }

    if (parseResultObj != null) {
      parseResultsList.add(parseResultObj);
    }
  }

  /**
   * Parses the text entered in the filter row. The text is parsed to figure out the type of match operation (&lt;, &gt;
   * etc.) and the value next to it.
   *
   * @param string entered by the user in the filter row text box
   * @return ParseResult
   */
  public static ParseResult parseExpression(final String string) {
    final Scanner scannerObj = new Scanner(string.trim());
    final ParseResult parseResultObj = new ParseResult();

    String regExprssn = string.toLowerCase(Locale.getDefault());

    final String[] subTextsObj = regExprssn.split("\\*");
    // If filter text contains only *, it is equivalent to not providing filter condition.
    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExpObj = new StringBuilder();
    for (String curStringObj : subTextsObj) {
      sbRegExpObj.append("(?s).*");
      if (!"".equals(curStringObj)) {
        sbRegExpObj.append(Pattern.quote(curStringObj));
      }
      // ICDM-2126
      sbRegExpObj.append("(?s).*");
    }
    regExprssn = sbRegExpObj.toString();
    // The below code generated a pattern similar to the one used in AbstractViewerFilter

    if (!regExprssn.startsWith("(?s).*")) {
      regExprssn = "(" + regExprssn + ")" + "|((?s).*(\\s)+" + regExprssn + ")";
    }

    // TODO: The below code is to be analysed to enable filtering with >,< symbols

    parseResultObj.setValueToMatch(regExprssn);
    scannerObj.close();
    return parseResultObj;
  }

  /**
   * @param stringObj String
   * @return ParseResult
   */
  public static ParseResult parseLiteral(final String stringObj) {
    final ParseResult parseReslt = new ParseResult();
    parseReslt.setMatchType(MatchType.NONE);
    parseReslt.setValueToMatch(stringObj);
    return parseReslt;
  }

  /**
   * Set the Match operation on the {@link ThresholdMatcherEditor} corresponding to the {@link MatchType}. This must be
   * done this way since ThresholdMatcherEditor.MatcherEditor is private.
   *
   * @param thrsholdMatchrEditor ThresholdMatcherEditor<T, Object>
   * @param matchTypeObj MatchType
   * @param <T> type of the row object
   */
  public static <T> void setMatchOperation(final ThresholdMatcherEditor<T, Object> thrsholdMatchrEditor,
      final MatchType matchTypeObj) {
    switch (matchTypeObj) {
      case GREATER_THAN:
        thrsholdMatchrEditor.setMatchOperation(ThresholdMatcherEditor.GREATER_THAN);
        break;
      case GREATER_THAN_OR_EQUAL:
        thrsholdMatchrEditor.setMatchOperation(ThresholdMatcherEditor.GREATER_THAN_OR_EQUAL);
        break;
      case LESS_THAN:
        thrsholdMatchrEditor.setMatchOperation(ThresholdMatcherEditor.LESS_THAN);
        break;
      case LESS_THAN_OR_EQUAL:
        thrsholdMatchrEditor.setMatchOperation(ThresholdMatcherEditor.LESS_THAN_OR_EQUAL);
        break;
      case NOT_EQUAL:
        thrsholdMatchrEditor.setMatchOperation(ThresholdMatcherEditor.NOT_EQUAL);
        break;
      default:
        thrsholdMatchrEditor.setMatchOperation(ThresholdMatcherEditor.EQUAL);
    }
  }

}
