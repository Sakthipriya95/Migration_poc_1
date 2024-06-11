package com.bosch.caltool.icdm.ruleseditor.views.providers;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.client.bo.cdr.ParamRulesModel;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.RuleDependency;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;
import com.bosch.caltool.nattable.CustomColumnHeaderDataProvider;

/**
 * @author dmo5cob
 */
public class AttrValColumnHeaderDataProvider<D extends IParameterAttribute, P extends IParameter>
    extends CustomColumnHeaderDataProvider {

  /**
   * Counter index
   */
  private static final int INDEX = 1;

  /**
   * dynamic cols start index
   */
  private static final int COL_INDEX_START = 7;

  /**
   * col index
   */
  private static final int COL_INDEX = 6;

  /**
   * ParametersRulePage instance
   */
  private final ParametersRulePage<D, P> page;

  /**
   * constant for row count one
   */
  private static final int ROW_COUNT_1 = 1;

  /**
   * Map<Integer, Attribute> instance
   */
  ConcurrentMap<Integer, Attribute> rowAttributeMap = new ConcurrentHashMap<>();

  /**
   * @param page ParametersRulePage
   */
  public AttrValColumnHeaderDataProvider(final ParametersRulePage page) {
    super();
    this.page = page;
  }

  /**
   * @param columnIndex int
   * @return String
   */
  private String getColumnHeaderLabel(final int columnIndex, final int rowIndex) {

    // filter label
    setChooseColsFilterLabel();
    ParamRulesModel<D, P> model = this.page.getModel();
    // Get rule dependency
    RuleDependency ruleDependency = model.getRuleDependencyMap().get(columnIndex);
    if (null != ruleDependency) {
      // Get attrvalue model
      SortedSet<AttributeValueModel> attrValueSet = ruleDependency.getAttrValues();
      AttributeValueModel attributeValueModel =
          ruleDependency.getAttrAttrValModelMap().get(this.rowAttributeMap.get(rowIndex));
      if (!attrValueSet.isEmpty() && (attrValueSet.size() >= rowIndex) && (attributeValueModel != null)) {
        // add to map
        return attributeValueModel.getValue().getName();

      }
    }
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getColumnCount() {
    return this.page.getModel().getRuleDependencyMap().size() + COL_INDEX_START;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getRowCount() {
    IParameter selectedParam = this.page.getSelectedParam();
    ParameterDataProvider<D, P> parameterDataProvider = this.page.getParameterDataProvider();

    if ((selectedParam == null) || (parameterDataProvider.getParamAttrs(selectedParam) == null) ||
        parameterDataProvider.getParamAttrs(selectedParam).isEmpty()) {
      return INDEX;
    }
    return parameterDataProvider.getParamAttrs(selectedParam).size() + INDEX;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getDataValue(final int columnIndex, final int rowIndex) {

    if (getRowCount() == ROW_COUNT_1) {
      return getColumnHeaderLabelForStaticColumns(columnIndex);
    }
    IParameter selectedParam = this.page.getSelectedParam();
    ParameterDataProvider<D, P> parameterDataProvider = this.page.getParameterDataProvider();
    if ((this.page.getSelectedParam() == null) || (parameterDataProvider.getParamAttrs(selectedParam) == null) ||
        parameterDataProvider.getParamAttrs(selectedParam).isEmpty()) {
      return null;
    }
    if (((columnIndex == CommonUIConstants.COLUMN_INDEX_0) || (columnIndex == CommonUIConstants.COLUMN_INDEX_1) ||
        (columnIndex == CommonUIConstants.COLUMN_INDEX_2) || (columnIndex == CommonUIConstants.COLUMN_INDEX_3) ||
        (columnIndex == CommonUIConstants.COLUMN_INDEX_4) || (columnIndex == CommonUIConstants.COLUMN_INDEX_5)) &&
        (rowIndex != parameterDataProvider.getParamAttrs(selectedParam).size())) {
      return "";
    }

    if ((columnIndex == COL_INDEX) && (rowIndex < parameterDataProvider.getParamAttrs(selectedParam).size())) {
      D object = this.page.getAttrlist().get(rowIndex);
      Attribute attribute = this.page.getParameterDataProvider().getAttribute(object);

      this.rowAttributeMap.put(rowIndex, attribute);
      return object.getName();
    }
    if ((columnIndex > COL_INDEX) && (rowIndex != parameterDataProvider.getParamAttrs(selectedParam).size())) {

      return getColumnHeaderLabel(columnIndex, rowIndex);
    }
    return getColumnHeaderLabelForStaticColumns(columnIndex);
  }

  /**
   * @param columnIndex
   * @return
   */
  private Object getColumnHeaderLabelForStaticColumns(final int columnIndex) {
    return this.page.getPropertyToLabelMap().get(columnIndex);
  }

  /**
   * @return ParamRulesModel
   */
  public ParamRulesModel getParamModel() {
    return this.page.getModel();
  }

  /**
   * Set the text based on the current columm filter in the NAT table
   */
  private void setChooseColsFilterLabel() {
    StringBuilder filterText = new StringBuilder();
    for (Attribute attr : this.page.getCombiMap().keySet()) {
      if (!CommonUtils.isEqual(this.page.getCombiMap().get(attr), ApicConstants.ANY)) {
        if (filterText.length() > ApicConstants.OBJ_EQUAL_CHK_VAL) {
          filterText.append(" ; ");
        }
        filterText.append(attr.getName()).append("-->").append(this.page.getCombiMap().get(attr));
      }
    }
    if (filterText.length() == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      filterText.append("<NONE>");
    }
    filterText.insert(0, "Column Filter : ");
    this.page.getChooseColsLabel().setText(filterText.toString());
  }
}
