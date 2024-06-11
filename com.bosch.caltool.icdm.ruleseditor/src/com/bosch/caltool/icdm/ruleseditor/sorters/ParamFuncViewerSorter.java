/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-1380 Parameter and function viewer sorter
 *
 * @author mkl2cob
 */
public class ParamFuncViewerSorter extends AbstractViewerSorter {

  /**
   * index
   */
  private int index;
  /**
   * constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default direction
   */
  private int direction = ASCENDING;

  /**
   * {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    // convert the object to list of objects
    FunctionParamProperties list1 = (FunctionParamProperties) obj1;

    FunctionParamProperties list2 = (FunctionParamProperties) obj2;


    // Compare Result
    int result;
    switch (this.index) {

      case CommonUIConstants.COLUMN_INDEX_1:
        // Sort for param name
        result = com.bosch.caltool.icdm.common.util.ApicUtil.compare(list1.getParamName(), list2.getParamName());
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        result = compareParamDescription(list1, list2);
        break;

      case CommonUIConstants.COLUMN_INDEX_3:
        // Sort for func name
        result = com.bosch.caltool.icdm.common.util.ApicUtil.compare(list1.getFunctionName(), list2.getFunctionName());
        break;

      default:
        result = 0;
    }
    if (result == 0) {
      // Sort for param name
      result = com.bosch.caltool.icdm.common.util.ApicUtil.compare(list1.getParamName(), list2.getParamName());
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      result = -result;
    }
    return result;
  }

  /**
   * @param list1 List<Object>
   * @param list2 List<Object>
   * @return int compare result of param desc
   */
  private int compareParamDescription(final FunctionParamProperties list1, final FunctionParamProperties list2) {
    int result;
    // Sort for param description
    if (new CommonDataBO().getLanguage() == Language.ENGLISH) {
      // compare english description if the language is ENGLISH
      result = com.bosch.caltool.icdm.common.util.ApicUtil.compare(list1.getParamLongName(), list2.getParamLongName());
    }
    else {
      // compare german description if the language is GERMAN
      result =
          com.bosch.caltool.icdm.common.util.ApicUtil.compare(list1.getParamLongNameGer(), list2.getParamLongNameGer());
    }
    return result;
  }

  /**
   * @return int defines direction
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


}
