package com.bosch.caltool.icdm.ruleseditor.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;

/**
 * @author dmo5cob
 */
public class RuleViewerNatMouseClickAction implements IMouseClickAction {

  /**
   * ParametersRulePage instance
   */
  private final ParametersRulePage page;

  /**
   * @param page ParametersRulePage
   */
  public RuleViewerNatMouseClickAction(final ParametersRulePage page) {
    super();
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable natTable, final MouseEvent event) {
    // fetch the selected rules
    final Map<RuleDefinition, List<ReviewRule>> ruleDefnRulesMap = this.page.fetchSelectedRules();
    final List<ReviewRule> rules = new ArrayList<ReviewRule>();
    // collect the list of review rules
    for (RuleDefinition ruleDef : ruleDefnRulesMap.keySet()) {
      rules.addAll(ruleDefnRulesMap.get(ruleDef));
    }

    final ReviewRuleActionSet paramActionSet = new ReviewRuleActionSet();

    ParamCollectionDataProvider paramColDataProvider = this.page.getParamColDataProvider();
    ParameterDataProvider parameterDataProvider = this.page.getParameterDataProvider();
    // If rules exist
    if (!rules.isEmpty()) {
      // Pass the selectedRules to the appropriate actions
      // Edit Rule


      // open edit rule dialog
      paramActionSet.openEditRuleDialog(rules, this.page.getSelectedParam(), this.page.getCdrFunction(), this.page,
          !(paramColDataProvider.isModifiable(this.page.getCdrFunction()) &&
              paramColDataProvider.isRulesModifiable(this.page.getCdrFunction())),
          paramColDataProvider, parameterDataProvider);


    }
    else {
      // if selected rule is empty , check for default rules
      final Map<RuleDefinition, List<ReviewRule>> defaultRuleDefnRulesMap = this.page.fetchSelectedDefaultRule();
      // open edit rule dialog for default rule if available
      if ((defaultRuleDefnRulesMap != null) && !defaultRuleDefnRulesMap.isEmpty()) {
        RuleDefinition ruleDef = defaultRuleDefnRulesMap.keySet().iterator().next();

        paramActionSet.openEditRuleDialog(defaultRuleDefnRulesMap.get(ruleDef).get(0), this.page.getCdrFunction(),
            this.page.getEditor(), null,
            !(paramColDataProvider.isModifiable(this.page.getCdrFunction()) &&
                paramColDataProvider.isRulesModifiable(this.page.getCdrFunction())),
            null, null, null, paramColDataProvider, parameterDataProvider);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExclusive() {
    return true;
  }

}