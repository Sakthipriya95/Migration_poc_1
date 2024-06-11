package com.bosch.caltool.icdm.ruleseditor.views.providers;

import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;


/**
 * @author rgo7cob
 */
public class ParamRulesLabelProvider<D extends IParameterAttribute, P extends IParameter> extends ColumnLabelProvider {


  /**
   * current Column index.
   */
  private int columnIndex;
  private final ReviewParamEditorInput editorInput;
  private final ParameterDataProvider<D, P> parameterDataProvider;

  /**
   * @param editorInput
   * @param parameterDataProvider
   */
  public ParamRulesLabelProvider(final ReviewParamEditorInput editorInput,
      final ParameterDataProvider<D, P> parameterDataProvider) {
    this.editorInput = editorInput;
    this.parameterDataProvider = parameterDataProvider;

  }

  /**
   * @return the columnIndex
   */
  public int getColumnIndex() {
    return this.columnIndex;
  }


  /**
   * @param columnIndex the columnIndex to set
   */
  public void setColumnIndex(final int columnIndex) {
    this.columnIndex = columnIndex;
  }


  /**
   * Constant for Dummy Rule
   */
  private static final String DUMMY_RULE = "Dummy Rule Created";


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    // Column text
    String result = "";
    if (element instanceof ReviewRule) {
      final ReviewRule rule = (ReviewRule) element;
      switch (this.columnIndex) {
        // Param name Column.
        case CommonUIConstants.COLUMN_INDEX_0:
          result = rule.getParameterName();
          break;
        // Min column.
        case CommonUIConstants.COLUMN_INDEX_1:
          result = CommonUtils.isNotNull(rule.getLowerLimit()) ? rule.getLowerLimit().toString() : "";
          break;
        // Max column.
        case CommonUIConstants.COLUMN_INDEX_2:
          result = CommonUtils.isNotNull(rule.getUpperLimit()) ? rule.getUpperLimit().toString() : "";
          break;
        case CommonUIConstants.COLUMN_INDEX_3:
          result = rule.getBitWiseRule();
          break;
        // Ref Value Column.
        case CommonUIConstants.COLUMN_INDEX_4:
          result = CommonUtils.isNotNull(rule.getRefValueDispString()) ? rule.getRefValueDispString() : "";
          break;
        // Exact match Column
        case CommonUIConstants.COLUMN_INDEX_5:
          result = setExactMatch(rule);
          break;
        // Unit Column.
        case CommonUIConstants.COLUMN_INDEX_6:
          result = rule.getUnit();
          break;
        // Hint column.
        case CommonUIConstants.COLUMN_INDEX_7:
          result = caseHint(rule);
          break;

        default:
          result = "";
      }

    }
    return result;
  }


  /**
   * @param rule
   * @return
   */
  private String caseHint(final ReviewRule rule) {
    String result;
    if (ApicUtil.compare(rule.getHint(), DUMMY_RULE) == 0) {
      result = "";
    }
    else {
      result = READY_FOR_SERIES.getType(rule.getReviewMethod()).getUiType();
    }
    return result;
  }


  /**
   * @param rule
   * @return the String to be displayed in Exact match column.
   */
  private String setExactMatch(final ReviewRule rule) {
    String result;
    // If Dummy rule then Exact match is empty.
    if (ApicUtil.compare(rule.getHint(), DUMMY_RULE) == 0) {
      result = "";
    }
    else {
      // If dcm to ssd.
      if (rule.isDcm2ssd()) {
        result = ApicConstants.USED_YES_DISPLAY;
      }
      else {
        // else No as display
        result = ApicConstants.USED_NO_DISPLAY;
      }
    }
    return result;
  }


  /**
   * {@inheritDoc} get the foreground color
   */
  @Override
  public Color getForeground(final Object element) {
    ReviewRule rule = (ReviewRule) element;
    // check whether the selected param has dependencies.


    Map<String, P> paramMap = this.editorInput.getParamRulesOutput().getParamMap();
    P parameter = paramMap.get(rule.getParameterName());
    boolean hasDep = this.parameterDataProvider.hasDependency(parameter);
    // Default rule grey.
    if (rule.getDependencyList().isEmpty() && hasDep && (ApicUtil.compare(rule.getHint(), DUMMY_RULE) != 0)) {
      return GUIHelper.COLOR_DARK_GRAY;
    }
    // Dummy rule
    else if (ApicUtil.compare(rule.getHint(), DUMMY_RULE) == 0) {
      rule.getReviewMethod();
      rule.isDcm2ssd();
      return GUIHelper.COLOR_RED;
    }
    return super.getForeground(element);
  }


  /**
   * {@inheritDoc} tool tip for Maturity Level
   */
  @Override
  public String getToolTipText(final Object element) {
    if (this.columnIndex == CommonUIConstants.COLUMN_INDEX_4) {
      ReviewRule rule = (ReviewRule) element;
      if (rule != null) {
        return RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(rule.getMaturityLevel()).getICDMMaturityLevel();
      }
    }
    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener iListner) {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener iListner) {
    // Not Applicable

  }


  /**
   * {@inheritDoc} get the background for Ref val column.
   */
  @Override
  public Color getBackground(final Object obj) {
    if (this.columnIndex == CommonUIConstants.COLUMN_INDEX_4) {
      ReviewRule rule = (ReviewRule) obj;
      if (rule.getMaturityLevel() != null) {
        return com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils
            .getMaturityLevelColor(RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(rule.getMaturityLevel()));
      }

    }
    return super.getBackground(obj);
  }

}
