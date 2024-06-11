/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_ASSESMENT_TYPE;

/**
 * @author hnu1cob
 */
class QuestionResultOptionComparator implements Comparator<String> {


  private static final Map<String, Integer> ORDER_MAP = new HashMap<>();

  static {
    ORDER_MAP.put(QS_ASSESMENT_TYPE.POSITIVE.getUiType(), 0);
    ORDER_MAP.put(QS_ASSESMENT_TYPE.NEGATIVE.getUiType(), 1);
    ORDER_MAP.put(QS_ASSESMENT_TYPE.NEUTRAL.getUiType(), 2);
  }

  /**
   * {@inheritDoc} Primary sorting based on result type. To sort the result strings based on the order: Positive,
   * Negative and Neutral
   */
  @Override
  public int compare(final String o1, final String o2) {
    String[] result1 = o1.split(QuestionDialog.RESULT_SEPARATOR);
    String[] result2 = o2.split(QuestionDialog.RESULT_SEPARATOR);

    int result = ORDER_MAP.get(result1[0]) - ORDER_MAP.get(result2[0]);
    if (result == 0) {
      result = ApicUtil.compare(result1[1], result2[1]);
    }
    return result;

  }


}
