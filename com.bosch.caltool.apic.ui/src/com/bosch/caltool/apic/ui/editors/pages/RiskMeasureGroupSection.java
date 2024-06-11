/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.Map;

import org.eclipse.ui.forms.widgets.Form;

import com.bosch.caltool.icdm.model.rm.RmCategoryMeasures;

/**
 * Class to create groups dynamically based on the risk categories
 *
 * @author dja7cob
 */
public class RiskMeasureGroupSection {

  private final Form form;

  private final Map<String, RmCategoryMeasures> rmCatMeasureMap;

  private final RiskEvalNatTableSection riskEvalNattableSection;

  private Map<Long, String> riskLvlMap;

  private Map<String, String> riskCodeMap;


  /**
   * @param form
   * @param rmCatMeasureMap
   * @param riskEvalNattableSection
   */
  public RiskMeasureGroupSection(final Form form, final Map<String, RmCategoryMeasures> rmCatMeasureMap,
      final RiskEvalNatTableSection riskEvalNattableSection) {
    this.form = form;
    this.rmCatMeasureMap = rmCatMeasureMap;
    this.riskEvalNattableSection = riskEvalNattableSection;
    if (null != riskEvalNattableSection.getResultHandler()) {
      this.riskLvlMap = riskEvalNattableSection.getResultHandler().getAllRiskLevel();
      this.riskCodeMap = riskEvalNattableSection.getResultHandler().getAllRiskCodeMap();
    }
  }


  /**
   * @param categoryGroupMap
   */
  public void createRiskGroups(final Map<String, RiskCategoryGroup> categoryGroupMap) {
    // create category
    if ((null != this.riskEvalNattableSection.getRiskCatList()) &&
        !this.riskEvalNattableSection.getRiskCatList().isEmpty()) {
      for (String category : this.riskEvalNattableSection.getRiskCatList()) {
        createCategryGrp(category, categoryGroupMap);
      }
    }
  }


  /**
   * @param category
   * @param categoryGroupMap
   */
  private void createCategryGrp(final String category, final Map<String, RiskCategoryGroup> categoryGroupMap) {
    RiskCategoryGroup catGroup = new RiskCategoryGroup(this.form, this.riskEvalNattableSection);
    catGroup.createGrpElements(category);
    if (null != this.riskLvlMap) {
      catGroup.updateGroupData(this.riskLvlMap, this.riskCodeMap, category, this.rmCatMeasureMap);
    }
    categoryGroupMap.put(category, catGroup);
  }


  /**
   * @return the form
   */
  public Form getForm() {
    return this.form;
  }

  /**
   * @return the rmCatMeasureMap
   */
  public Map<String, RmCategoryMeasures> getRmCatMeasureMap() {
    return this.rmCatMeasureMap;
  }

  /**
   * @return the riskEvalNattableSection
   */
  public RiskEvalNatTableSection getRiskEvalNattableSection() {
    return this.riskEvalNattableSection;
  }

}
