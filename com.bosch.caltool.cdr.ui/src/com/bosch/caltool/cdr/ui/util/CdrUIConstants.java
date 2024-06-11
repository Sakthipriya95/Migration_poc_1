/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.bosch.rcputils.decorators.Decorators;


/**
 * UI constants for CDR UI
 *
 * @author bne4cob
 */
public final class CdrUIConstants {

  /**
   * Constant for the display Object
   */
  private static final Display DISPLAY = Display.getCurrent();

  // Icdm-619
  /**
   * Constant for the Red color
   */
  public static final Color RED = DISPLAY.getSystemColor(SWT.COLOR_DARK_RED);
  // Icdm-619
  /**
   * Constant for the Green color
   */
  public static final Color GREEN = DISPLAY.getSystemColor(SWT.COLOR_DARK_GREEN);

  // Icdm-619
  /**
   * Constant for the Black color
   */
  public static final Color BLACK = DISPLAY.getSystemColor(SWT.COLOR_BLACK);

  /**
   * NAT col label for upper limit diff
   */
  public static final String UPPER_LIMIT_DIFF_LABEL = "UPPER_LIMIT_DIFF";
  /**
   * NAT col label for lower limit diff
   */
  public static final String LOW_LIMIT_DIFF_LABEL = "LOW_LIMIT_DIFF";
  /**
   * NAT col label for ready for series diff
   */
  public static final String READY_FOR_SERIES_DIFF_LABEL = "READY_FOR_SERIES_DIFF";
  /**
   * NAT col label for exact match diff
   */
  public static final String EXACT_MATCH_DIFF_LABEL = "READY_FOR_SERIES_DIFF";
  /**
   * NAT col label for result diff
   */
  public static final String RESULT_DIFF_LABEL = "RESULT_DIFF";
  /**
   * NAT col label for sec result diff
   */
  // Task 236308
  public static final String SEC_RESULT_DIFF_LABEL = "SEC_RESULT_DIFF";
  /**
   * NAT col label for check val diff
   */
  public static final String CHK_VALUE_DIFF_LABEL = "CHK_VALUE_DIFF";
  /**
   * NAT col label for check val diff
   */
  public static final String SCORE_DIFF_LABEL = "SCORE_DIFF";

  /**
   * NAT col label for check val unit diff
   */
  // iCDM-2151
  public static final String CHK_VALUE_UNIT_DIFF_LABEL = "CHK_VALUE_UNIT_DIFF";
  /**
   * NAT col label for ref val diff
   */
  public static final String REF_VALUE_DIFF_LABEL = "REF_VALUE_DIFF";
  /**
   * NAT col label for reviewed
   */
  public static final String REVIEWED_LABEL = "REVIEWED";
  /**
   * NAT col label for not ok
   */
  public static final String NOT_OK_LABEL = "NOT_OK";
  /**
   * NAT col label fornot reviewed
   */
  public static final String NOT_REVIEWED_LABEL = "NOT_REVIEWED";

  /**
   * History available for check value
   */
  public static final String HISTORY_YES_LABEL = "HISTORY_YES";

  /**
   * Column header label
   */
  public static final String COL_HEADER_LABEL = "COL_HEADER";

  /**
   * Lable finished with Positive answer
   */
  public static final String FINISHED_POSITIVE_ANS_LABEL = "Finished with Positive answer";

  /**
   * Lable finished with Negative answer
   */
  public static final String FINISHED_NEGATIVE_ANS_LABEL = "Finished with Negative answer";

  /**
   * Lable finished with Neutral answer
   */
  public static final String FINISHED_NEUTRAL_ANS_LABEL = "Finished with Neutral answer";


  /**
   * Lable finished with Negative answer, where no Negative Answer Allowed to finish WP
   */
  public static final String FINISHED_NO_NEG_ANS_ALLOWED = "No Negative Answer Allowed to finish WP ";

  /**
   * NAT col label for bitwise limit diff
   */
  public static final String BITWISE_LIMIT_DIFF_LABEL = "BITWISE_LIMIT_DIFF";

  /**
   * NAT col label for bitwise flag diff
   */
  public static final String BITWISE_FLAG_DIFF_LABEL = "BITWISE_FLAG_DIFF";

  /**
   * Decorators instance
   */
  public static final Decorators DECORATOR = new Decorators();

  /**
   * Add a question
   */
  public static final String ADD_QUESTION = "Add Question";
  /**
   * Edit a question
   */
  public static final String EDIT_QUESTION = "Edit Question";
  /**
   * Edit a questionaire
   */
  // iCDM-1968
  public static final String EDIT_QUESTIONAIRE = "Edit Questionnaire";
  /**
   * export a questionaire
   */
  public static final String EXPORT_QUESTIONAIRE = "Export Questionaire";
  /**
   * Validation msg for a working set of questionnaire
   */
  public static final String WORKING_SET_MSG = "Only the working set of the Questionnaire can be modified!";

  /**
   * Compliance label
   */
  public static final String COMPLIANCE_LABEL = "COMPLIANCE";

  /**
   * Result cannot be linked since the dependant attributes are not set
   */
  public static final String DEPENDANT_NOT_SET = "Dependent attribute(s) value are not same";
  /**
   * Result cannot be linked since it is already linked to that variant
   */
  public static final String LINKED_RESULT = "Result is already linked to this variant";
  /**
   * Pver names are different in varaints
   */
  public static final String DIFFERENT_SDOM_PVER = "PVER Names are not same";

  public static final String READ_ONLY_LABEL = "READ_ONLY";

  public static final String COMPLI_READ_ONLY_LABEL = "Compli_read_only";

  public static final String BLACK_LIST_LABEL = "BLACK_LIST_LABEL";

  /**
   *
   */
  public static final String QSSD_LABEL = "QSSD_LABEL";
  /**
   *
   */
  public static final String MULTI_IMAGE_PAINTER = "MULTI_IMAGE_PAINTER";


  public static final String COMPLI_BLACK_LIST_LABEL = "COMPLI_BLACK_LIST_LABEL";

  public static final String READ_ONLY_BLACK_LIST_LABEL = "READ_ONLY_BLACK_LIST_LABEL";

  public static final String COMPLI_READ_ONLY_BLACK_LIST_LABEL = "COMPLI_READ_ONLY_BLACK_LIST_LABEL";

  public static final String DIFFERENT_VAR_GROUP = "The variant group of the target variant is different";

  /**
   * warning message to user about some of the qnaire response are not answered
   */
  public static final String WARN_MSG_ABOUT_UNFILLED_QNAIRE_RESP =
      "Some of the questionnaires response for this review are not completely answered.";
  /**
   * warning message to user about some of the qnaire response are not answered
   */
  public static final String SHOW_UNFILLED_QNAIRES_HYPERLINK = "<a>(Click here to show questionnaires)</a>";

  /**
   * No instance should be created
   */
  private CdrUIConstants() {
    // No instance should be created
  }

}
