/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedRuleInput;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRuleParamCol;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;

/**
 * @author rgo7cob
 */
public class ConfigInputCreator<C extends ParamCollection> {


  /**
   * @param labelNames
   * @param attrValueModSet
   * @return
   */
  public ConfigBasedRuleInput createconfigInput(final List<String> labelNames,
      final Set<AttributeValueModel> attrValueModSet, final ParamCollection cdrFunction) {
    if (cdrFunction instanceof Function) {
      ConfigBasedRuleInput<Function> configInput = new ConfigBasedRuleInput<>();
      configInput.setLabelNames(labelNames);
      configInput.setAttrValueModSet(new TreeSet<>(attrValueModSet));
      ReviewRuleParamCol<Function> paramCol = new ReviewRuleParamCol();
      paramCol.setParamCollection((Function) cdrFunction);
      configInput.setParamCol(paramCol);
      return configInput;
    }
    else if (cdrFunction instanceof RuleSet) {
      ConfigBasedRuleInput<RuleSet> configInput = new ConfigBasedRuleInput<>();
      configInput.setLabelNames(labelNames);
      configInput.setAttrValueModSet(new TreeSet<>(attrValueModSet));
      ReviewRuleParamCol<RuleSet> paramCol = new ReviewRuleParamCol();
      paramCol.setParamCollection((RuleSet) cdrFunction);
      configInput.setParamCol(paramCol);
      return configInput;
    }
    else {
      ConfigBasedRuleInput<CompPackage> configInput = new ConfigBasedRuleInput<>();
      configInput.setLabelNames(labelNames);
      configInput.setAttrValueModSet(new TreeSet<>(attrValueModSet));
      ReviewRuleParamCol<CompPackage> paramCol = new ReviewRuleParamCol();
      paramCol.setParamCollection((CompPackage) cdrFunction);
      configInput.setParamCol(paramCol);
      return configInput;

    }

  }
}
