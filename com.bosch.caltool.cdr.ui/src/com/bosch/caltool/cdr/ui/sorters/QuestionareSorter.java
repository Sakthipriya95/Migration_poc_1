/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.sorters.BasicObjectViewerSorter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;


/**
 * Sorter class for questionnaire name selection table
 *
 * @author bru2cob
 */
public class QuestionareSorter extends BasicObjectViewerSorter<Questionnaire> {


  /**
   * Compares two questionare details {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // call super method to return compare result of other col data
    int result = super.compare(viewer, obj1, obj2);
    // check Questionnaire instance
    if ((obj1 instanceof Questionnaire) && (obj2 instanceof Questionnaire)) {
      Questionnaire ques1 = (Questionnaire) obj1;
      Questionnaire ques2 = (Questionnaire) obj2;

      switch (this.index) {
        // check qnaire name
        case CommonUIConstants.COLUMN_INDEX_0:
          result = ApicUtil.compare(ques1.getNameSimple(), ques2.getNameSimple());
          break;
        // check description
        case CommonUIConstants.COLUMN_INDEX_1:
          result = ApicUtil.compare(ques1.getDescription(), ques2.getDescription());
          break;
        // Check Div
        case CommonUIConstants.COLUMN_INDEX_2:
          result = ApicUtil.compare(ques1.getDivName(), ques2.getDivName());
          break;
        default:
          result = 0;
      }
      // If descending order, flip the direction
      if (this.direction == ApicConstants.DESCENDING) {
        result = -result;
      }
    }
    return result;
  }


}
