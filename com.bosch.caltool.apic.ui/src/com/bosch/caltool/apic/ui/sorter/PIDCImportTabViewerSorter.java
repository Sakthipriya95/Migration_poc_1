/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;
import com.bosch.caltool.icdm.report.excel.ExcelClientConstants;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author jvi6cob
 */
public class PIDCImportTabViewerSorter extends AbstractViewerSorter {

  private int index;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;

  /**
   * @param index defines grid tableviewercolumn index
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
    ProjectImportAttr<?> pidcCompResult1 = (ProjectImportAttr<?>) obj1;
    ProjectImportAttr<?> pidcCompResult2 = (ProjectImportAttr<?>) obj2;
    int result = 0;
    switch (this.index) {
      case ExcelClientConstants.COLUMN_NUM_ZERO:
        result = getVariantName(pidcCompResult1).compareToIgnoreCase(getVariantName(pidcCompResult2));
        break;

      case ExcelClientConstants.COLUMN_NUM_ONE:
        result = getSubVariantName(pidcCompResult1).compareToIgnoreCase(getSubVariantName(pidcCompResult2));
        break;

      case ExcelClientConstants.COLUMN_NUM_TWO:
        result = (pidcCompResult1.getPidcAttr().getName()).compareToIgnoreCase(pidcCompResult2.getPidcAttr().getName());
        break;

      case ExcelClientConstants.COLUMN_NUM_THREE:
        result = (pidcCompResult1.getAttr().getDescription())
            .compareToIgnoreCase(pidcCompResult2.getAttr().getDescription());
        break;

      case ExcelClientConstants.COLUMN_NUM_FOUR:
        result = (pidcCompResult1.getPidcAttr().getUsedFlag())
            .compareToIgnoreCase(pidcCompResult2.getPidcAttr().getUsedFlag());
        break;

      case ExcelClientConstants.COLUMN_NUM_FIVE:
        result =
            (pidcCompResult1.getPidcAttr().getValue()).compareToIgnoreCase(pidcCompResult2.getPidcAttr().getValue());
        break;

      case ExcelClientConstants.COLUMN_NUM_SIX:
        result = (pidcCompResult1.getPidcAttr().getPartNumber())
            .compareToIgnoreCase(pidcCompResult2.getPidcAttr().getPartNumber());
        break;

      case ExcelClientConstants.COLUMN_NUM_SEVEN:
        result = (pidcCompResult1.getPidcAttr().getSpecLink())
            .compareToIgnoreCase(pidcCompResult2.getPidcAttr().getSpecLink());
        break;

      case ExcelClientConstants.COLUMN_NUM_EIGHT:
        result = (pidcCompResult1.getPidcAttr().getDescription())
            .compareToIgnoreCase(pidcCompResult2.getPidcAttr().getDescription());
        break;

      case ExcelClientConstants.COLUMN_NUM_NINE:
        result = (pidcCompResult1.getExcelAttr().getUsedFlag())
            .compareToIgnoreCase(pidcCompResult2.getExcelAttr().getUsedFlag());
        break;

      case ExcelClientConstants.COLUMN_NUM_TEN:
        result =
            (pidcCompResult1.getExcelAttr().getValue()).compareToIgnoreCase(pidcCompResult2.getExcelAttr().getValue());
        break;

      case ExcelClientConstants.COLUMN_NUM_ELEVEN:
        result = (pidcCompResult1.getExcelAttr().getPartNumber())
            .compareToIgnoreCase(pidcCompResult2.getExcelAttr().getPartNumber());
        break;

      case ExcelClientConstants.COLUMN_NUM_TWELEVE:
        result = (pidcCompResult1.getExcelAttr().getSpecLink())
            .compareToIgnoreCase(pidcCompResult2.getExcelAttr().getSpecLink());
        break;

      case ExcelClientConstants.COLUMN_NUM_THIRTEEN:
        result = (pidcCompResult1.getExcelAttr().getDescription() != null
            ? pidcCompResult1.getExcelAttr().getDescription() : ApicConstants.EMPTY_STRING)
                .compareToIgnoreCase(pidcCompResult2.getExcelAttr().getDescription() != null
                    ? pidcCompResult2.getExcelAttr().getDescription() : ApicConstants.EMPTY_STRING);
        break;

      // Data Status
      case ExcelClientConstants.COLUMN_NUM_FOURTEEN:
        result = (pidcCompResult1.isNewlyAddedVal() ? "NEW VALUE" : "MODIFIED")
            .compareToIgnoreCase(pidcCompResult2.isNewlyAddedVal() ? "NEW VALUE" : "MODIFIED");
        break;

      case ExcelClientConstants.COLUMN_NUM_FIFTEEN:
        result = (pidcCompResult1.getComment() != null ? pidcCompResult1.getComment() : ApicConstants.EMPTY_STRING)
            .compareToIgnoreCase(
                pidcCompResult2.getComment() != null ? pidcCompResult2.getComment() : ApicConstants.EMPTY_STRING);
        break;

      default:
        break;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      return -result;
    }
    return 0;
  }

  /**
   * Gets the variant name.
   *
   * @param item the item
   * @return the variant name
   */
  private String getVariantName(final ProjectImportAttr<?> item) {
    if (item.getPidcAttr() instanceof PidcVariantAttribute) {
      return ((PidcVariantAttribute) item.getPidcAttr()).getVariantName();
    }
    else if (item.getPidcAttr() instanceof PidcSubVariantAttribute) {
      return ((PidcSubVariantAttribute) item.getPidcAttr()).getVariantName();
    }
    return ApicConstants.EMPTY_STRING;
  }


  /**
   * Gets the sub-variant name.
   *
   * @param item the item
   * @return the variant name
   */
  private String getSubVariantName(final ProjectImportAttr<?> item) {
    if (item.getPidcAttr() instanceof PidcSubVariantAttribute) {
      return ((PidcSubVariantAttribute) item.getPidcAttr()).getVariantName();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}
