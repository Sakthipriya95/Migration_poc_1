package com.bosch.caltool.apic.ui.views.providers;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * This class provides labels for body datalayer of the GridLayer of the NatTable
 *
 * @author dmo5cob
 */
public class FocusmatrixLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * ILayer instance
   */
  private final ILayer layer;
  /**
   * static cols index
   */
  private static final int STATIC_COLS_INDEX = 3;
  /**
   * IRowDataProvider<FocusMatrixAttribute> instance
   */
  private final IRowDataProvider<FocusMatrixAttributeClientBO> bodyDataProvider;

  /**
   * @param bodyDataLayer ILayer instance
   * @param bodyDataProvider IRowDataProvider instance
   */
  public FocusmatrixLabelAccumulator(final ILayer bodyDataLayer,
      final IRowDataProvider<FocusMatrixAttributeClientBO> bodyDataProvider) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;

  }

  /**
   * Labels which are to be configured in each cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }

    // get the row object out of the dataprovider
    FocusMatrixAttributeClientBO rowObject = this.bodyDataProvider.getRowObject(rowPosition);

    if (columnPosition == CommonUIConstants.COLUMN_INDEX_3) {
      if (!CommonUtils.isEmptyString(rowObject.getRemarksDisplay())) {
        configLabels.addLabel("ATTR_REMARKS");
      }
    }
    if (columnPosition > STATIC_COLS_INDEX) {
      com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem fmUseCaseItem =
          rowObject.getFocusmatrixUseCaseItem(columnPosition - 4);
      // if there is no mapping
      if ((!fmUseCaseItem.isUcpaMapped(rowObject.getAttribute())) &&
          !fmUseCaseItem.isMapped(rowObject.getAttribute())) {
        configLabels.addLabel("NONE");
      } // if attrs are mapped to the use case item
      else {
        // If the attribute is mapped from the focus matrix level
        configLabels.addLabel(fmUseCaseItem.isMapped(rowObject.getAttribute()) ? "MAPPED_FROM_FOCUS_MATRIX" : "");

        // If the attribute is mapped from the use case level
        configLabels.addLabel(fmUseCaseItem.isUcpaMapped(rowObject.getAttribute()) ? "USECASE_PROPOSAL" : "");


        configLabels
            .addLabel(((null == fmUseCaseItem.getColorCode(rowObject.getAttribute())) || (rowObject.isHiddenToUser()))
                ? "NONE" : fmUseCaseItem.getColorCode(rowObject.getAttribute()).getColor());


        String comments = null == fmUseCaseItem.getComments(rowObject.getAttribute()) ? ""
            : fmUseCaseItem.getComments(rowObject.getAttribute());
        if (!CommonUtils.isEmptyString(comments)) {
          configLabels.addLabel("COMMENT");
        }

        // ICDM-1740
        String link = null == fmUseCaseItem.getLink(rowObject.getAttribute()) ? ""
            : fmUseCaseItem.getLink(rowObject.getAttribute());
        if (!CommonUtils.isEmptyString(link)) {
          configLabels.addLabel("LINK");// adding label if link is available
        }
        // ICDM-2328


        String hidden = rowObject.isHiddenToUser() ? "HIDDEN" : "";
        if (!CommonUtils.isEmptyString(hidden)) {
          configLabels.addLabel("HIDDEN");// adding label if attr hidden
        }
      }

    }

    // ICDM-1629
    if (!rowObject.isVisible()) {
      // add the label if the attribute is not visible in the project..This would be shown in dark grey color
      configLabels.addLabel("INVISIBLE");
    }
    else if (!rowObject.isFocusMatrixApplicable()) {
      // add the label if the attribute is not marked as FM relevant ...This would be shown in grey color
      configLabels.addLabel("NOT_FM_RELEVANT");
    }

  }
}
