package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * icdm-1088 Sorter used in the Param Details Page
 *
 * @author rgo7cob
 */
public class ParamAttrTabViewerSorter extends AbstractViewerSorter {

  /**
   * index for Sorting
   */
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  // Default ascending
  private int direction = ASCENDING;
  private final ParameterDataProvider parameterDataProvider;

  /**
   * Constant for Attr name Col
   */
  private static final int ATTR_NAME_COL_IDX = 0;

  /**
   * Constant for Attr desc Col
   */
  private static final int ATTR_DESC_COL_IDX = 1;

  /**
   * Constant for Attr Unit Col
   */
  private static final int ATTR_UNIT_COL_IDX = 2;

  /**
   * @param parameterDataProvider parameterDataProvider
   */
  public ParamAttrTabViewerSorter(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }

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

  /**
   * {@inheritDoc} Compare for Attribute.
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    IParameterAttribute paramAttr1 = (IParameterAttribute) obj1;
    IParameterAttribute paramAttr2 = (IParameterAttribute) obj2;
    int compRes;
    switch (this.index) {
      // Attr name
      case ATTR_NAME_COL_IDX:
        compRes = ApicUtil.compare(paramAttr1.getName(), paramAttr2.getName());
        break;
      // Attr Desc
      case ATTR_DESC_COL_IDX:
        compRes = ApicUtil.compare(this.parameterDataProvider.getAttribute(paramAttr1).getDescription(),
            this.parameterDataProvider.getAttribute(paramAttr2).getDescription());
        break;
      // Attr Unit
      case ATTR_UNIT_COL_IDX:
        compRes = ApicUtil.compare(this.parameterDataProvider.getAttribute(paramAttr1).getUnit(),
            this.parameterDataProvider.getAttribute(paramAttr2).getUnit());
        break;
      default:
        compRes = 0;

    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compRes = -compRes;
    }
    return compRes;
  }

  @Override
  public int getDirection() {
    return this.direction;
  }

}