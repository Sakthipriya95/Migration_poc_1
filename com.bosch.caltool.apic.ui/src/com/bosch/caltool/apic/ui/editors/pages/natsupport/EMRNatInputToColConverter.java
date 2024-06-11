/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * The Class EMRNatInputToColConverter.
 *
 * @author gge6cob
 */
public class EMRNatInputToColConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    // EmrFile represents a row in the table viewer
    if (evaluateObj instanceof EmrFileMapping) {
      result = getMappingData((EmrFileMapping) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * Gets the mapping data.
   *
   * @param evaluateObj the evaluate obj
   * @param colIndex the col index
   * @return the mapping data
   */
  private Object getMappingData(final EmrFileMapping evaluateObj, final int colIndex) {
    Object result = null;
    switch (colIndex) {
      case 0:// EMR - File Name
        result = evaluateObj.getEmrFile().getName();
        break;
      case 1:// File Description
        result = evaluateObj.getEmrFile().getDescription();
        break;
      case 2:// PIDC Variant Set
        result = getPIDCVariantUIString(evaluateObj.getEmrFile().getIsVariant(), evaluateObj.getVariantSet());
        break;
      case 3:// Uploaded Date
        result = setDateFormat(evaluateObj.getEmrFile().getCreatedDate());
        break;
      case 4:// is Delted flag
        result = getIsDeletedUIString(evaluateObj.getEmrFile().getDeletedFlag());
        break;
      case 5:// has errors flag
        result = getHasErrorsUIString(evaluateObj.getEmrFile().getLoadedWithoutErrorsFlag());
        break;
      default:
        break;
    }
    return result;
  }


  /**
   * @param isVariant is applicable for variant/pidc
   * @param variantSet variants
   * @return ui string
   */
  public String getPIDCVariantUIString(final boolean isVariant, final Set<PidcVariant> variantSet) {
    if (!isVariant) {
      return "<valid for all variants>";
    }
    if (CommonUtils.isNotEmpty(variantSet)) {
      return variantSet.stream().map(PidcVariant::getName).sorted().collect(Collectors.joining(","));
    }
    return null;
  }


  /**
   * @param deleted deletedFlag
   * @return hasError UI string
   */
  public String getIsDeletedUIString(final boolean deleted) {
    if (deleted) {
      return CommonUtilConstants.DISPLAY_YES;
    }
    return CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * Returns opposite of 'LoadedWithoutError' flag
   *
   * @param loadedWithOutErrors flag
   * @return hasError UI string
   */
  public String getHasErrorsUIString(final boolean loadedWithOutErrors) {
    if (!loadedWithOutErrors) {
      return CommonUtilConstants.DISPLAY_YES;
    }
    return CommonUtilConstants.DISPLAY_NO;
  }

  /**
   * @param agreeWithCocDate
   * @return
   */
  private String setDateFormat(final Date date) {
    String formattedDate = "";
    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
    }
    return formattedDate;
  }
}