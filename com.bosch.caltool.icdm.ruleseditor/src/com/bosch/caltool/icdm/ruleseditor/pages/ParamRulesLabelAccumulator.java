/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;


/**
 * @author bru2cob
 */
public class ParamRulesLabelAccumulator<D extends IParameterAttribute, P extends Parameter>
    extends ColumnOverrideLabelAccumulator {

  /**
   * NAT table Data Layer
   */
  private final ILayer layer;
  private final ParameterDataProvider<D, P> paramDataProvider;

  /**
   * @param layer
   * @param parameterDataProvider
   */
  public ParamRulesLabelAccumulator(final ILayer layer, final ParameterDataProvider<D, P> parameterDataProvider) {
    super(layer);
    this.layer = layer;
    this.paramDataProvider = parameterDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the row object out of the dataprovider
    @SuppressWarnings("unchecked")
    IRowDataProvider<IParameter> bodyDataProvider =
        (IRowDataProvider<IParameter>) ((DataLayer) this.layer).getDataProvider();
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    else if (rowObject instanceof IParameter) {

      final IParameter funcParam = (IParameter) rowObject;
      if (columnPosition == CommonUIConstants.COLUMN_INDEX_1) {

        if (funcParam.getType().equalsIgnoreCase(ParameterType.MAP.getText())) {
          configLabels.addLabel(RuleEditorConstants.MAP_TYPE_LABEL);
        }
        else if (funcParam.getType().equalsIgnoreCase(ParameterType.CURVE.getText())) {
          configLabels.addLabel(RuleEditorConstants.CURVE_TYPE_LABEL);
        }
        else if (funcParam.getType().equalsIgnoreCase(ParameterType.VALUE.getText())) {
          configLabels.addLabel(RuleEditorConstants.VALUE_TYPE_LABEL);
        }
        else if (funcParam.getType().equalsIgnoreCase(ParameterType.ASCII.getText())) {
          configLabels.addLabel(RuleEditorConstants.ASCII_TYPE_LABEL);
        }
        else if (funcParam.getType().equalsIgnoreCase(ParameterType.VAL_BLK.getText())) {
          configLabels.addLabel(RuleEditorConstants.VAL_BLK_TYPE_LABEL);
        }
        else if (funcParam.getType().equalsIgnoreCase(ParameterType.AXIS_PTS.getText())) {
          configLabels.addLabel(RuleEditorConstants.AXIS_PTS_TYPE_LABEL);
        }
      }
      if (columnPosition == CommonUIConstants.COLUMN_INDEX_0) {
        configLabels.addLabel(RuleEditorConstants.MULTI_IMAGE_PAINTER);
        if (this.paramDataProvider.isComplianceParam(funcParam)) {
          configLabels.addLabel(RuleEditorConstants.COMPLIANCE_TYPE_LABEL);
        }
        if (funcParam.isBlackList()) {
          configLabels.addLabel(RuleEditorConstants.BLACK_LIST_LABEL);
        }
        if (funcParam.isQssdFlag()) {
          configLabels.addLabel(RuleEditorConstants.QSSD_LABEL);
        }
      }

    }
    if (columnPosition == CommonUIConstants.COLUMN_INDEX_9) {
      ReviewRule rule = getCDRRule(rowObject);
      if (rule != null) {
        RuleMaturityLevel icdmMaturityLevel = RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(rule.getMaturityLevel());
        switch (icdmMaturityLevel) {
          case START:
            configLabels.addLabel(RuleEditorConstants.MATURITY_START_LABEL);
            break;

          case STANDARD:
            configLabels.addLabel(RuleEditorConstants.MATURITY_STANDARD_LABEL);
            break;

          case FIXED:
            configLabels.addLabel(RuleEditorConstants.MATURITY_FIXED_LABEL);
            break;
          default:
            configLabels.addLabel(RuleEditorConstants.MATURITY_NONE_LABEL);
        }
      }
    }
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
  }

  /**
   * This method gives the rule for the selected element
   *
   * @param element Object
   * @return CDRRule
   */
  private ReviewRule getCDRRule(final Object element) {
    ReviewRule returnRule = null;
    // return the rule based on type of element
    if (element instanceof IParameter) {
      IParameter param = (IParameter) element;
      if (!this.paramDataProvider.hasDependency(param) && (this.paramDataProvider.getRuleList(param) != null) &&
          !this.paramDataProvider.getRuleList(param).isEmpty()) {
        returnRule = this.paramDataProvider.getRuleList(param).get(0);
      }
    }
    if (element instanceof ReviewRule) {
      returnRule = (ReviewRule) element;
    }
    if (element instanceof DefaultRuleDefinition) {
      DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) element;
      returnRule = defaultRule.getReviewRule();
    }
    return returnRule;
  }
}
