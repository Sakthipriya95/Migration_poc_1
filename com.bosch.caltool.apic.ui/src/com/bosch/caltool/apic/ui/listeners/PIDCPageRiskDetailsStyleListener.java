/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.listeners;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.model.rm.RmRiskLevel.RISK_LEVEL_CONFIG;

/**
 * @author dmo5cob
 */
public class PIDCPageRiskDetailsStyleListener implements LineStyleListener {

  /**
   * Risk details header in Pidcstatistics section
   */
  private static final String RISK_DETAILS_LABEL = "Risk Details :  ";
  private final Map<String, String> riskCodeMap;
  private final StringBuilder riskDetailsString;

  /**
   * @param riskWithWeight key: risk code , value : risk name
   * @param riskDetailsString risk details
   */
  public PIDCPageRiskDetailsStyleListener(final Map<String, String> riskCodeMap,
      final StringBuilder riskDetailsString) {
    this.riskCodeMap = riskCodeMap;
    this.riskDetailsString = riskDetailsString;
  }

  /**
   * @param event LineStyleEvent
   */
  @Override
  public void lineGetStyle(final LineStyleEvent event) {

    java.util.List<StyleRange> styles = new java.util.ArrayList<StyleRange>();

    for (int i = 0, n = event.lineText.length(); i < n; i++) {
      if (event.lineText.equals(RISK_DETAILS_LABEL)) {
        int start = i;
        while (i < n) {
          i++;
        }
        StyleRange styleRange = new StyleRange();
        styleRange.start = event.lineOffset + start;
        styleRange.length = i - start;
        styleRange.fontStyle = SWT.BOLD;
        styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        styles.add(styleRange);
      }

    }


    for (int i = 0, n = event.lineText.length(); i < n; i++) {

      if (event.lineText.contains(this.riskDetailsString.toString()) && (event.lineText.charAt(i) == ':')) {
        int start = i;
        while ((i < n) && (event.lineText.charAt(i) != '\t')) {
          i++;
        }
        for (Entry<String, String> grp : this.riskCodeMap.entrySet()) {
          if (event.lineText.substring(start + 2, i).equals(grp.getValue())) {
            String code = grp.getKey();
            RISK_LEVEL_CONFIG type = RISK_LEVEL_CONFIG.getType(code);
            Color colorRisk = new Color(Display.getCurrent(), type.getRed(), type.getGreen(), type.getBlue());
            StyleRange styleRangeHigh = new StyleRange(event.lineOffset + start + 1, i - start - 1,
                Display.getCurrent().getSystemColor(SWT.COLOR_BLACK), colorRisk);
            styleRangeHigh.fontStyle = SWT.BOLD;
            styles.add(styleRangeHigh);
          }
        }
      }
    }
    event.styles = styles.toArray(new StyleRange[0]);
  }
}
