/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.editors.pages;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;

/**
 * @author pdh2cob
 */
public class UseCaseNatAttrPageLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * Instance variable which holds a reference to bodyDataLayer
   */
  private final ILayer layer;
  /**
   * Instance variable which holds a reference to bodyDataProvider
   */
  private final IRowDataProvider<UseCaseEditorRowAttr> bodyDataProvider;

  private final UseCaseNatAttributesPage natPage;

  /**
   * @param bodyDataLayer
   * @param bodyDataProvider
   * @param natPage
   */
  public UseCaseNatAttrPageLabelAccumulator(final ILayer bodyDataLayer,
      final IRowDataProvider<UseCaseEditorRowAttr> bodyDataProvider, final UseCaseNatAttributesPage natPage) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;
    this.natPage = natPage;
  }


  /**
   * This method is used to add labels to a cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);

    if (columnPosition == UseCaseNatAttributesPage.COLUMN_NUM_BALL) {
      configLabels.addLabel("BALL");
    }

    if (isRelevantColIndexForQuotation(columnIndex)) {
      UseCaseEditorRowAttr useCaseEditorRowAttr = this.bodyDataProvider.getRowObject(rowPosition);

      UsecaseEditorModel useCaseEditorModel =
          ((UseCaseEditorInput) this.natPage.getEditorInput()).getUseCaseEditorModel();
      Map<Long, Long> ucpAttrMap =
          useCaseEditorModel.getAttrToUcpAttrMap().get(useCaseEditorRowAttr.getAttributeBO().getAttribute().getId());

      IUseCaseItemClientBO clientBo = this.natPage.getColumnUseCaseItemMapping().get(columnIndex);

      if (clientBo != null) {
        Long ucItemId = useCaseEditorRowAttr.getUcItemId(clientBo);
        if (CommonUtils.isNotEmpty(ucpAttrMap) && ucpAttrMap.keySet().contains(ucItemId)) {
          boolean quotationFlag = useCaseEditorRowAttr.isUCItemQuotRelevant(ucItemId);

          if (quotationFlag) {
            configLabels.addLabel(UseCaseNatAttributesPage.QUOTATION_LABEL);
          }
        }
      }
    }
    if ((columnPosition == UseCaseNatAttributesPage.CREATION_DATE_COLUMN_IDX) ||
        (columnPosition == UseCaseNatAttributesPage.ATTR_CLASS_COLUMN_IDX)) {
      configLabels.addLabel("DATE_ATTRCLASS");
    }
  }


  private boolean isRelevantColIndexForQuotation(final int colIndex) {
    return !((colIndex == UseCaseNatAttributesPage.COLUMN_NUM_BALL) ||
        (colIndex == UseCaseNatAttributesPage.ATTR_COLUMN_IDX) ||
        (colIndex == UseCaseNatAttributesPage.DESC_COLUMN_IDX) ||
        (colIndex == UseCaseNatAttributesPage.CREATION_DATE_COLUMN_IDX) ||
        (colIndex == UseCaseNatAttributesPage.ANY_COLUMN_IDX) ||
        (colIndex == UseCaseNatAttributesPage.NONE_CHECKBOX_COLUMN_IDX) ||
        (colIndex == UseCaseNatAttributesPage.ALL_CHECKBOX_COLUMN_IDX));
  }

}
