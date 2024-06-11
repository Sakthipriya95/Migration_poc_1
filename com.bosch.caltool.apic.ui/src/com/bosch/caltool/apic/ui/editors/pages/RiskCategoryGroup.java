/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;

/**
 * @author dja7cob Group With Risk Level and Risk measure
 */
public class RiskCategoryGroup {

  private final Form form;
  private Group group;
  private Text riskLvlText;
  private Text measuresText;
  private final RiskEvalNatTableSection riskEvalNattableSection;

  /**
   * @param form
   * @param riskEvalNattableSection
   */
  public RiskCategoryGroup(final Form form, final RiskEvalNatTableSection riskEvalNattableSection) {
    this.form = form;
    this.riskEvalNattableSection = riskEvalNattableSection;
  }

  /**
   * @param category
   */
  public void createGrpElements(final String category) {
    this.group = createGroup(category);
    createRiskLvlText();
    createRiskMeasuresText();
  }

  /**
   * @param riskLvlMap
   * @param riskWeightMap
   * @param category
   * @param rmCatMeasureMap
   */
  public void updateGroupData(final Map<Long, String> riskLvlMap, final Map<String, String> riskCodeMap,
      final String category, final Map<String, RmCategoryMeasures> rmCatMeasureMap) {
    RmCategoryMeasures rmCatMeasure = rmCatMeasureMap.get(category);
    if (null != rmCatMeasure) {
      if (null != rmCatMeasure.getRiskLevel()) {
        String riskTxt = riskLvlMap.get(rmCatMeasure.getRiskLevel());
        this.riskLvlText.setText(riskTxt);
        String code = riskCodeMap.get(riskTxt);
        if (null != this.riskEvalNattableSection.getRiskBackgroundMap().get(code)) {
          this.riskLvlText.setBackground(this.riskEvalNattableSection.getRiskBackgroundMap().get(code));
        }
      }
      else {
        resetGroup();
      }
      if (null != rmCatMeasure.getName()) {
        this.measuresText.setText(rmCatMeasure.getName());
      }
    }
    else {
      resetGroup();
    }
  }

  /**
   *
   */
  private void resetGroup() {
    this.riskLvlText.setText("N.A.");
    this.measuresText.setText("");
    this.riskLvlText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
  }

  /**
   *
   */
  private void createRiskMeasuresText() {
    GridData gridDataText = getTextAreaGridData();
    this.measuresText = getFormToolkit().createText(this.group, null, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
    this.measuresText.setLayoutData(gridDataText);
    this.measuresText.setEditable(false);
  }

  /**
   *
   */
  private void createRiskLvlText() {
    GridData gridDataText = getTextBoxGridData();
    this.riskLvlText = getFormToolkit().createText(this.group, null, SWT.BORDER | SWT.CENTER);
    this.riskLvlText.setLayoutData(gridDataText);
    this.riskLvlText.setEditable(false);
  }

  /**
   * @param category
   * @param currForm
   * @return
   */
  private Group createGroup(final String category) {
    Group currGrp = new Group(this.form.getBody(), SWT.SHADOW_NONE);
    GridData layoutData = getGroupGridData();
    currGrp.setLayout(new GridLayout());
    currGrp.setLayoutData(layoutData);
    currGrp.setText(category);
    Font boldFont = new Font(currGrp.getDisplay(), new FontData("Arial", 8, SWT.BOLD));
    currGrp.setFont(boldFont);
    return currGrp;
  }

  /**
   * @return
   */
  public GridData getGroupGridData() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    gridData.heightHint = 100;
    gridData.minimumWidth = 50;
    gridData.grabExcessVerticalSpace = true;
    return gridData;
  }

  /**
   * @return
   */
  public GridData getTextBoxGridData() {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    gridData.heightHint = 15;
    gridData.minimumWidth = 50;
    gridData.grabExcessVerticalSpace = false;
    return gridData;
  }

  /**
   * @return
   */
  public GridData getTextAreaGridData() {

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.verticalAlignment = GridData.FILL;
    gridData.heightHint = 100;
    gridData.minimumWidth = 50;
    gridData.grabExcessVerticalSpace = true;
    return gridData;

  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {

    return new FormToolkit(Display.getCurrent());
  }


}
