/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.utils;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;

/**
 * UI Level utility methods for CDR perspective
 *
 * @author bne4cob
 */
public final class CdrUIUtils {


  /**
   * Private constructor for the utility clas
   */
  private CdrUIUtils() {
    // Private Constructor
  }


  /**
   * @param configRegistry
   */
  public static IConfigRegistry getQuestionHeadingStyle(final IConfigRegistry configRegistry) {
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(251, 231, 200));
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.BOLD)));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, "NONE");
    return configRegistry;
  }

  /**
   * @param cdrResParam CDRResultParameter
   * @param resultData ReviewResultClientBO
   * @return String
   */
  public static String getSSDLabelString(final CDRResultParameter cdrResParam, final ReviewResultClientBO resultData) {
    StringBuilder ssdLblStrBuilder = new StringBuilder();
    if (resultData.isComplianceParameter(cdrResParam)) {
      ssdLblStrBuilder.append("Compliance,");
    }
    if (resultData.isReadOnly(cdrResParam)) {
      ssdLblStrBuilder.append("Read Only,");
    }
    if (resultData.isBlackList(cdrResParam)) {
      ssdLblStrBuilder.append("Black List,");
    }
    if (resultData.isQssdParameter(cdrResParam)) {
      ssdLblStrBuilder.append("Q-SSD,");
    }
    if (resultData.isDependentParam(cdrResParam)) {
      ssdLblStrBuilder.append("Dependent,");
    }
    // delete "," if it is the last character
    return ssdLblStrBuilder.toString().isEmpty() ? ""
        : ssdLblStrBuilder.toString().substring(0, ssdLblStrBuilder.length() - 1);
  }
}
