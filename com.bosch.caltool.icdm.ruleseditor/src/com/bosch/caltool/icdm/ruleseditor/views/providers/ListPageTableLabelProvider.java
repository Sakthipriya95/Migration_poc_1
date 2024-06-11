package com.bosch.caltool.icdm.ruleseditor.views.providers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author dmo5cob
 */
public class ListPageTableLabelProvider<D extends IParameterAttribute, P extends IParameter> extends ColumnLabelProvider
    implements ITableLabelProvider {

  /**
   * Q-SSD constant
   */
  private static final String QSSD_STR = "Qssd";

  /**
   * blacklist constant
   */
  private static final String BLACKLIST_STR = "Blacklist";

  /**
   * compli constant
   */
  private static final String COMPLI_STR = "Compli";
  private boolean flag;
  private final ParameterDataProvider<D, P> parameterDataProvider;

  /**
   * key - string combination for the multiple image, value - Image
   */
  private final Map<String, Image> multiImageMap = new ConcurrentHashMap<>();

  // NOPMD by dmo5cob on

  // 4/2/14 2:31 PM


  /**
   * @param paramColDataProvider
   * @param parameterDataProvider
   */
  public ListPageTableLabelProvider(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }


  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }


  @Override
  public String getColumnText(final Object element, final int columnIndex) { // NOPMD by dmo5cob on 4/2/14 2:31 PM
    String result = ApicConstants.EMPTY_STRING;
    this.flag = true;
    if (element instanceof IParameter) {
      result = setParamDetails(element, columnIndex);
    }
    // ICDM-1070
    if (element instanceof ReviewRule) {
      result = setRuleDetails(element, columnIndex);
    }
    if (element instanceof DefaultRuleDefinition) {
      result = setDefaultRuleDetails(element, columnIndex);
    }
    return result;
  }


  /**
   * Label provider for default rule
   *
   * @param element selec element
   * @param columnIndex index
   * @return label
   */
  private String setDefaultRuleDetails(final Object element, final int columnIndex) {
    DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) element;
    String result = "";
    ReviewRule rule = defaultRule.getReviewRule();
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_3:
        result = defaultRule.getDescription();
        break;
      case CommonUIConstants.COLUMN_INDEX_6:
        if (rule.getLowerLimit() != null) {
          result = rule.getLowerLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        if (rule.getUpperLimit() != null) {
          result = rule.getUpperLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        result = rule.getBitWiseRule();
        break;
      case CommonUIConstants.COLUMN_INDEX_9:
        result = defaultRule.getReviewRule().getRefValueDispString();
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = ruleExactMatch(rule);
        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        if (rule.getUnit() != null) {
          result = rule.getUnit();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = ReviewRuleUtil.getReadyForSeriesUIVal(rule);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = rule.getHint() == null ? "" : rule.getHint().toString();
        break;
      default:
        result = "";
        break;

    }
    return result;
  }


  /**
   * Label provider for rule
   *
   * @param element selec element
   * @param columnIndex index
   * @return label
   */
  private String setRuleDetails(final Object element, final int columnIndex) {
    final ReviewRule rule = (ReviewRule) element;
    String result = "";
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_3:
        result = "";
        break;
      case CommonUIConstants.COLUMN_INDEX_6:
        if (rule.getLowerLimit() != null) {
          result = rule.getLowerLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        if (rule.getUpperLimit() != null) {
          result = rule.getUpperLimit().toString();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_8:

        result = rule.getBitWiseRule();
        break;
      case CommonUIConstants.COLUMN_INDEX_9:

        result = rule.getRefValueDispString();
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = ruleExactMatch(rule);
        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        if (rule.getUnit() != null) {
          result = rule.getUnit();
        }
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = ReviewRuleUtil.getReadyForSeriesUIVal(rule);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = CommonUtils.checkNull(rule.getHint());
        break;
      default:
        result = "";
        break;
    }
    return result;
  }

  /**
   * Returns the exact match of rule
   *
   * @param rule selceted rule
   * @return exact match
   */
  private String ruleExactMatch(final ReviewRule rule) {
    String result = "";
    if (!rule.getRefValueDispString().isEmpty()) {
      if (rule.isDcm2ssd()) {
        result = CommonUIConstants.EXACT_MATCH_YES;
      }
      else {
        result = CommonUIConstants.EXACT_MATCH_NO;
      }
    }
    return result;
  }


  /**
   * Label provider for parameter
   *
   * @param element selec element
   * @param columnIndex index
   * @return label
   */
  private String setParamDetails(final Object element, final int columnIndex) {
    String result;
    final IParameter funcParam = (IParameter) element;
    if (this.parameterDataProvider.hasDependency(funcParam)) {
      this.flag = false;
    }
    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_2:
        result = funcParam.getName();
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        result = funcParam.getLongName();
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        result = funcParam.getpClassText();
        break;
      case CommonUIConstants.COLUMN_INDEX_5:

        result = funcParam.getCodeWord();

        break;
      // Changes Implemented For the Cdr rule values
      case CommonUIConstants.COLUMN_INDEX_6:
        result = setParmLowerLmt(funcParam);
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        result = setParamUpperLmt(funcParam);

        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        result = setParamBitWiseRule(funcParam);

        break;
      case CommonUIConstants.COLUMN_INDEX_9:

        result = getParamRefValue(funcParam);
        break;
      // ICDM-1173
      case CommonUIConstants.COLUMN_INDEX_10:
        result = setParamExactMatch(funcParam);

        break;
      case CommonUIConstants.COLUMN_INDEX_11:
        result = setParamUnit(funcParam);
        break;
      case CommonUIConstants.COLUMN_INDEX_12:
        result = setParamReviewMethod(funcParam);
        break;
      // ICDM-2152
      case CommonUIConstants.COLUMN_INDEX_13:
        result = setParamRemarks(funcParam);
        break;
      default:
        result = "";
        break;
    }
    return result;
  }

  /**
   * @param funcParam
   * @return
   */
  private String getParamRefValue(final IParameter funcParam) {
    String result = "";
    if (!this.parameterDataProvider.hasDependency(funcParam) &&
        (this.parameterDataProvider.getRuleList(funcParam) != null) &&
        !this.parameterDataProvider.getRuleList(funcParam).isEmpty() &&
        (this.parameterDataProvider.getReviewRule(funcParam) != null)) {
      result = this.parameterDataProvider.getReviewRule(funcParam).getRefValueDispString();
    }
    return result;
  }


  // ICDM-2152
  /**
   * @param funcParam
   * @return
   */
  private String setParamRemarks(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null) &&
        (this.parameterDataProvider.getReviewRule(funcParam).getHint() != null)) {
      result = this.parameterDataProvider.getReviewRule(funcParam).getHint();
    }
    return result;
  }

  /**
   * @param funcParam
   * @return
   */
  private String setParamBitWiseRule(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null)) {
      result = this.parameterDataProvider.getReviewRule(funcParam).getBitWiseRule();
    }
    return result;
  }


  /**
   * Sets param unit
   *
   * @param funcParam sel param
   * @return unit
   */
  private String setParamUnit(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null) &&
        (this.parameterDataProvider.getReviewRule(funcParam).getUnit() != null)) {
      result = this.parameterDataProvider.getReviewRule(funcParam).getUnit();
    }
    return result;
  }


  /**
   * Sets param upper limit
   *
   * @param funcParam sel param
   * @return upper limit
   */
  private String setParamUpperLmt(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null) &&
        (this.parameterDataProvider.getReviewRule(funcParam).getUpperLimit() != null)) {
      result = this.parameterDataProvider.getReviewRule(funcParam).getUpperLimit().toString();
    }
    return result;
  }


  /**
   * Sets param lower limit
   *
   * @param funcParam sel param
   * @return lower limit
   */
  private String setParmLowerLmt(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null) &&
        (this.parameterDataProvider.getReviewRule(funcParam).getLowerLimit() != null)) {
      result = this.parameterDataProvider.getReviewRule(funcParam).getLowerLimit().toString();
    }
    return result;
  }


  /**
   * Sets param ready for series
   *
   * @param funcParam sel param
   * @return ready for series value
   */
  private String setParamReviewMethod(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null) &&
        (ReviewRuleUtil.getReadyForSeriesUIVal(this.parameterDataProvider.getReviewRule(funcParam)) != null)) {
      result = ReviewRuleUtil.getReadyForSeriesUIVal(this.parameterDataProvider.getReviewRule(funcParam));
    }
    return result;
  }


  /**
   * Returns the exact match of param rule
   *
   * @param rule selceted param
   * @return exact match
   */
  private String setParamExactMatch(final IParameter funcParam) {
    String result = "";
    if (this.flag && (this.parameterDataProvider.getReviewRule(funcParam) != null) &&
        (!this.parameterDataProvider.getReviewRule(funcParam).getRefValueDispString().isEmpty()) &&
        (ReviewRuleUtil.getRefValue(this.parameterDataProvider.getReviewRule(funcParam)) != null)) {
      if ((!this.parameterDataProvider.getReviewRule(funcParam).getRefValueDispString().isEmpty()) &&
          this.parameterDataProvider.getReviewRule(funcParam).isDcm2ssd()) {
        result = CommonUIConstants.EXACT_MATCH_YES;
      }
      else {
        result = CommonUIConstants.EXACT_MATCH_NO;
      }
    }
    return result;
  }


  /**
   * Get the column image
   */
  @Override
  public Image getColumnImage(final Object element, final int columnIndex) {

    Image img = null;
    // ICDM-2439
    if ((element instanceof IParameter) && (columnIndex == 1)) {
      final IParameter funcParam = (IParameter) element;
      if (funcParam.getType().equalsIgnoreCase(ParameterType.MAP.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.CURVE.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.VALUE.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.ASCII.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.VAL_BLK.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16);
      }
      else if (funcParam.getType().equalsIgnoreCase(ParameterType.AXIS_PTS.getText())) {
        img = ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16);
      }

    }

    // ICDM-2439
    if ((element instanceof IParameter) && (columnIndex == 0)) {
      final IParameter funcParam = (IParameter) element;
      String keyString = new String();
      if (this.parameterDataProvider.isComplianceParameter(funcParam)) {
        keyString = keyString.concat(COMPLI_STR);
      }
      if (funcParam.isBlackList()) {
        keyString = keyString.concat(BLACKLIST_STR);
      }
      if (funcParam.isQssdFlag()) {
        keyString = keyString.concat(QSSD_STR);
      }
      Image imageFromMap = this.multiImageMap.get(keyString);
      if (null == imageFromMap) {
        imageFromMap = createImage(funcParam);
      }
      return imageFromMap;
    }
    return img;

  }


  /**
   * @param funcParam
   * @return
   */
  private Image createImage(final IParameter funcParam) {
    Image compositeImg = new Image(Display.getCurrent(), 48, 16);
    String keyString = new String();
    int iconWidth = 0;
    GC gc = new GC(compositeImg);
    if (this.parameterDataProvider.isComplianceParameter(funcParam)) {
      keyString = keyString.concat(COMPLI_STR);
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16), iconWidth, 0);
    }
    if (funcParam.isBlackList()) {
      keyString = keyString.concat(BLACKLIST_STR);
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL), iconWidth, 0);
    }
    if (funcParam.isQssdFlag()) {
      keyString = keyString.concat(QSSD_STR);
      iconWidth = iconWidth + 16;
      gc.drawImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL), iconWidth, 0);
    }
    this.multiImageMap.put(keyString, compositeImg);
    return compositeImg;
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
    super.dispose();
    for (Image image : this.multiImageMap.values()) {
      if (null != image) {
        image.dispose();
      }
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeListener(final ILabelProviderListener iListner) {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Color getBackground(final Object obj) {
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object obj) {
    // TODO Auto-generated method stub
    return null;
  }

}
